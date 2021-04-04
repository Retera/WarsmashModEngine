/* 
 * FLAC library (Java)
 * 
 * Copyright (c) Project Nayuki
 * https://www.nayuki.io/page/flac-library-java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */

package io.nayuki.flac.decode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import io.nayuki.flac.common.FrameInfo;


/**
 * Decodes a FLAC frame from an input stream into raw audio samples. Note that these objects are
 * stateful and not thread-safe, due to the bit input stream field, private temporary arrays, etc.
 * <p>This class only uses memory and has no native resources; however, the
 * code that uses this class is responsible for cleaning up the input stream.</p>
 * @see FlacDecoder
 * @see FlacLowLevelInput
 */
public final class FrameDecoder {
	
	/*---- Fields ----*/
	
	// Can be changed when there is no active call of readFrame().
	// Must be not null when readFrame() is called.
	public FlacLowLevelInput in;
	
	// Can be changed when there is no active call of readFrame().
	// Must be in the range [4, 32].
	public int expectedSampleDepth;
	
	// Temporary arrays to hold two decoded audio channels (a.k.a. subframes). They have int64 range
	// because the worst case of 32-bit audio encoded in stereo side mode uses signed 33 bits.
	// The maximum possible block size is either 65536 samples per channel from the
	// frame header logic, or 65535 from a strict reading of the FLAC specification.
	// Two buffers are needed for stereo coding modes, but not more than two because
	// all other multi-channel audio is processed independently per channel.
	private long[] temp0;
	private long[] temp1;
	
	// The number of samples (per channel) in the current block/frame being processed.
	// This value is only valid while the method readFrame() is on the call stack.
	// When readFrame() is active, this value is in the range [1, 65536].
	private int currentBlockSize;
	
	
	
	/*---- Constructors ----*/
	
	// Constructs a frame decoder that initially uses the given stream.
	// The caller is responsible for cleaning up the input stream.
	public FrameDecoder(FlacLowLevelInput in, int expectDepth) {
		this.in = in;
		expectedSampleDepth = expectDepth;
		temp0 = new long[65536];
		temp1 = new long[65536];
		currentBlockSize = -1;
	}
	
	
	
	/*---- Methods ----*/
	
	// Reads the next frame of FLAC data from the current bit input stream, decodes it,
	// and stores output samples into the given array, and returns a new metadata object.
	// The bit input stream must be initially aligned at a byte boundary. If EOF is encountered before
	// any actual bytes were read, then this returns null. Otherwise this function either successfully
	// decodes a frame and returns a new metadata object, or throws an appropriate exception. A frame
	// may have up to 8 channels and 65536 samples, so the output arrays need to be sized appropriately.
	public FrameInfo readFrame(int[][] outSamples, int outOffset) throws IOException {
		// Check field states
		Objects.requireNonNull(in);
		if (currentBlockSize != -1)
			throw new IllegalStateException("Concurrent call");
		
		// Parse the frame header to see if one is available
		long startByte = in.getPosition();
		FrameInfo meta = FrameInfo.readFrame(in);
		if (meta == null)  // EOF occurred cleanly
			return null;
		if (meta.sampleDepth != -1 && meta.sampleDepth != expectedSampleDepth)
			throw new DataFormatException("Sample depth mismatch");
		
		// Check arguments and read frame header
		currentBlockSize = meta.blockSize;
		Objects.requireNonNull(outSamples);
		if (outOffset < 0 || outOffset > outSamples[0].length)
			throw new IndexOutOfBoundsException();
		if (outSamples.length < meta.numChannels)
			throw new IllegalArgumentException("Output array too small for number of channels");
		if (outOffset > outSamples[0].length - currentBlockSize)
			throw new IndexOutOfBoundsException();
		
		// Do the hard work
		decodeSubframes(expectedSampleDepth, meta.channelAssignment, outSamples, outOffset);
		
		// Read padding and footer
		if (in.readUint((8 - in.getBitPosition()) % 8) != 0)
			throw new DataFormatException("Invalid padding bits");
		int computedCrc16 = in.getCrc16();
		if (in.readUint(16) != computedCrc16)
			throw new DataFormatException("CRC-16 mismatch");
		
		// Handle frame size and miscellaneous
		long frameSize = in.getPosition() - startByte;
		if (frameSize < 10)
			throw new AssertionError();
		if ((int)frameSize != frameSize)
			throw new DataFormatException("Frame size too large");
		meta.frameSize = (int)frameSize;
		currentBlockSize = -1;
		return meta;
	}
	
	
	// Based on the current bit input stream and the two given arguments, this method reads and decodes
	// each subframe, performs stereo decoding if applicable, and writes the final uncompressed audio data
	// to the array range outSamples[0 : numChannels][outOffset : outOffset + currentBlockSize].
	// Note that this method uses the private temporary arrays and passes them into sub-method calls.
	private void decodeSubframes(int sampleDepth, int chanAsgn, int[][] outSamples, int outOffset) throws IOException {
		// Check arguments
		if (sampleDepth < 1 || sampleDepth > 32)
			throw new IllegalArgumentException();
		if ((chanAsgn >>> 4) != 0)
			throw new IllegalArgumentException();
		
		if (0 <= chanAsgn && chanAsgn <= 7) {
			// Handle 1 to 8 independently coded channels
			int numChannels = chanAsgn + 1;
			for (int ch = 0; ch < numChannels; ch++) {
				decodeSubframe(sampleDepth, temp0);
				int[] outChan = outSamples[ch];
				for (int i = 0; i < currentBlockSize; i++)
					outChan[outOffset + i] = checkBitDepth(temp0[i], sampleDepth);
			}
			
		} else if (8 <= chanAsgn && chanAsgn <= 10) {
			// Handle one of the side-coded stereo methods
			decodeSubframe(sampleDepth + (chanAsgn == 9 ? 1 : 0), temp0);
			decodeSubframe(sampleDepth + (chanAsgn == 9 ? 0 : 1), temp1);
			
			if (chanAsgn == 8) {  // Left-side stereo
				for (int i = 0; i < currentBlockSize; i++)
					temp1[i] = temp0[i] - temp1[i];
			} else if (chanAsgn == 9) {  // Side-right stereo
				for (int i = 0; i < currentBlockSize; i++)
					temp0[i] += temp1[i];
			} else if (chanAsgn == 10) {  // Mid-side stereo
				for (int i = 0; i < currentBlockSize; i++) {
					long side = temp1[i];
					long right = temp0[i] - (side >> 1);
					temp1[i] = right;
					temp0[i] = right + side;
				}
			} else
				throw new AssertionError();
			
			// Copy data from temporary to output arrays, and convert from long to int
			int[] outLeft  = outSamples[0];
			int[] outRight = outSamples[1];
			for (int i = 0; i < currentBlockSize; i++) {
				outLeft [outOffset + i] = checkBitDepth(temp0[i], sampleDepth);
				outRight[outOffset + i] = checkBitDepth(temp1[i], sampleDepth);
			}
		} else  // 11 <= channelAssignment <= 15
			throw new DataFormatException("Reserved channel assignment");
	}
	
	
	// Checks that 'val' is a signed 'depth'-bit integer, and either returns the
	// value downcasted to an int or throws an exception if it's out of range.
	// Note that depth must be in the range [1, 32] because the return value is an int.
	// For example when depth = 16, the range of valid values is [-32768, 32767].
	private static int checkBitDepth(long val, int depth) {
		assert 1 <= depth && depth <= 32;
		// Equivalent check: (val >> (depth - 1)) == 0 || (val >> (depth - 1)) == -1
		if (val >> (depth - 1) == val >> depth)
			return (int)val;
		else
			throw new IllegalArgumentException(val + " is not a signed " + depth + "-bit value");
	}
	
	
	// Reads one subframe from the bit input stream, decodes it, and writes to result[0 : currentBlockSize].
	private void decodeSubframe(int sampleDepth, long[] result) throws IOException {
		// Check arguments
		Objects.requireNonNull(result);
		if (sampleDepth < 1 || sampleDepth > 33)
			throw new IllegalArgumentException();
		if (result.length < currentBlockSize)
			throw new IllegalArgumentException();
		
		// Read header fields
		if (in.readUint(1) != 0)
			throw new DataFormatException("Invalid padding bit");
		int type = in.readUint(6);
		int shift = in.readUint(1);  // Also known as "wasted bits-per-sample"
		if (shift == 1) {
			while (in.readUint(1) == 0) {  // Unary coding
				if (shift >= sampleDepth)
					throw new DataFormatException("Waste-bits-per-sample exceeds sample depth");
				shift++;
			}
		}
		if (!(0 <= shift && shift <= sampleDepth))
			throw new AssertionError();
		sampleDepth -= shift;
		
		// Read sample data based on type
		if (type == 0)  // Constant coding
			Arrays.fill(result, 0, currentBlockSize, in.readSignedInt(sampleDepth));
		else if (type == 1) {  // Verbatim coding
			for (int i = 0; i < currentBlockSize; i++)
				result[i] = in.readSignedInt(sampleDepth);
		} else if (8 <= type && type <= 12)
			decodeFixedPredictionSubframe(type - 8, sampleDepth, result);
		else if (32 <= type && type <= 63)
			decodeLinearPredictiveCodingSubframe(type - 31, sampleDepth, result);
		else
			throw new DataFormatException("Reserved subframe type");
		
		// Add trailing zeros to each sample
		if (shift > 0) {
			for (int i = 0; i < currentBlockSize; i++)
				result[i] <<= shift;
		}
	}
	
	
	// Reads from the input stream, performs computation, and writes to result[0 : currentBlockSize].
	private void decodeFixedPredictionSubframe(int predOrder, int sampleDepth, long[] result) throws IOException {
		// Check arguments
		Objects.requireNonNull(result);
		if (sampleDepth < 1 || sampleDepth > 33)
			throw new IllegalArgumentException();
		if (predOrder < 0 || predOrder >= FIXED_PREDICTION_COEFFICIENTS.length)
			throw new IllegalArgumentException();
		if (predOrder > currentBlockSize)
			throw new DataFormatException("Fixed prediction order exceeds block size");
		if (result.length < currentBlockSize)
			throw new IllegalArgumentException();
		
		// Read and compute various values
		for (int i = 0; i < predOrder; i++)  // Non-Rice-coded warm-up samples
			result[i] = in.readSignedInt(sampleDepth);
		readResiduals(predOrder, result);
		restoreLpc(result, FIXED_PREDICTION_COEFFICIENTS[predOrder], sampleDepth, 0);
	}
	
	private static final int[][] FIXED_PREDICTION_COEFFICIENTS = {
		{},
		{1},
		{2, -1},
		{3, -3, 1},
		{4, -6, 4, -1},
	};
	
	
	// Reads from the input stream, performs computation, and writes to result[0 : currentBlockSize].
	private void decodeLinearPredictiveCodingSubframe(int lpcOrder, int sampleDepth, long[] result) throws IOException {
		// Check arguments
		Objects.requireNonNull(result);
		if (sampleDepth < 1 || sampleDepth > 33)
			throw new IllegalArgumentException();
		if (lpcOrder < 1 || lpcOrder > 32)
			throw new IllegalArgumentException();
		if (lpcOrder > currentBlockSize)
			throw new DataFormatException("LPC order exceeds block size");
		if (result.length < currentBlockSize)
			throw new IllegalArgumentException();
		
		// Read non-Rice-coded warm-up samples
		for (int i = 0; i < lpcOrder; i++)
			result[i] = in.readSignedInt(sampleDepth);
		
		// Read parameters for the LPC coefficients
		int precision = in.readUint(4) + 1;
		if (precision == 16)
			throw new DataFormatException("Invalid LPC precision");
		int shift = in.readSignedInt(5);
		if (shift < 0)
			throw new DataFormatException("Invalid LPC shift");
		
		// Read the coefficients themselves
		int[] coefs = new int[lpcOrder];
		for (int i = 0; i < coefs.length; i++)
			coefs[i] = in.readSignedInt(precision);
		
		// Perform the main LPC decoding
		readResiduals(lpcOrder, result);
		restoreLpc(result, coefs, sampleDepth, shift);
	}
	
	
	// Updates the values of result[coefs.length : currentBlockSize] according to linear predictive coding.
	// This method reads all the arguments and the field currentBlockSize, only writes to result, and has no other side effects.
	// After this method returns, every value in result must fit in a signed sampleDepth-bit integer.
	// The largest allowed sample depth is 33, hence the largest absolute value allowed in the result is 2^32.
	// During the LPC restoration process, the prefix of result before index i consists of entirely int33 values.
	// Because coefs.length <= 32 and each coefficient fits in a signed int15 (both according to the FLAC specification),
	// the maximum (worst-case) absolute value of 'sum' is 2^32 * 2^14 * 32 = 2^51, which fits in a signed int53.
	// And because of this, the maximum possible absolute value of a residual before LPC restoration is applied,
	// such that the post-LPC result fits in a signed int33, is 2^51 + 2^32 which also fits in a signed int53.
	// Therefore a residue that is larger than a signed int53 will necessarily not fit in the int33 result and is wrong.
	private void restoreLpc(long[] result, int[] coefs, int sampleDepth, int shift) {
		// Check and handle arguments
		Objects.requireNonNull(result);
		Objects.requireNonNull(coefs);
		if (result.length < currentBlockSize)
			throw new IllegalArgumentException();
		if (sampleDepth < 1 || sampleDepth > 33)
			throw new IllegalArgumentException();
		if (shift < 0 || shift > 63)
			throw new IllegalArgumentException();
		long lowerBound = (-1) << (sampleDepth - 1);
		long upperBound = -(lowerBound + 1);
		
		for (int i = coefs.length; i < currentBlockSize; i++) {
			long sum = 0;
			for (int j = 0; j < coefs.length; j++)
				sum += result[i - 1 - j] * coefs[j];
			assert (sum >> 53) == 0 || (sum >> 53) == -1;  // Fits in signed int54
			sum = result[i] + (sum >> shift);
			// Check that sum fits in a sampleDepth-bit signed integer,
			// i.e. -(2^(sampleDepth-1)) <= sum < 2^(sampleDepth-1)
			if (sum < lowerBound || sum > upperBound)
				throw new DataFormatException("Post-LPC result exceeds bit depth");
			result[i] = sum;
		}
	}
	
	
	// Reads metadata and Rice-coded numbers from the input stream, storing them in result[warmup : currentBlockSize].
	// The stored numbers are guaranteed to fit in a signed int53 - see the explanation in restoreLpc().
	private void readResiduals(int warmup, long[] result) throws IOException {
		// Check and handle arguments
		Objects.requireNonNull(result);
		if (warmup < 0 || warmup > currentBlockSize)
			throw new IllegalArgumentException();
		if (result.length < currentBlockSize)
			throw new IllegalArgumentException();
		
		int method = in.readUint(2);
		if (method >= 2)
			throw new DataFormatException("Reserved residual coding method");
		assert method == 0 || method == 1;
		int paramBits = method == 0 ? 4 : 5;
		int escapeParam = method == 0 ? 0xF : 0x1F;
		
		int partitionOrder = in.readUint(4);
		int numPartitions = 1 << partitionOrder;
		if (currentBlockSize % numPartitions != 0)
			throw new DataFormatException("Block size not divisible by number of Rice partitions");
		for (int inc = currentBlockSize >>> partitionOrder, partEnd = inc, resultIndex = warmup;
				partEnd <= currentBlockSize; partEnd += inc) {
			
			int param = in.readUint(paramBits);
			if (param == escapeParam) {
				int numBits = in.readUint(5);
				for (; resultIndex < partEnd; resultIndex++)
					result[resultIndex] = in.readSignedInt(numBits);
			} else {
				in.readRiceSignedInts(param, result, resultIndex, partEnd);
				resultIndex = partEnd;
			}
		}
	}
	
}
