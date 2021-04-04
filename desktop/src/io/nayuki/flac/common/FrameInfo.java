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

package io.nayuki.flac.common;

import java.io.IOException;
import java.util.Objects;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.decode.FlacLowLevelInput;
import io.nayuki.flac.decode.FrameDecoder;
import io.nayuki.flac.encode.BitOutputStream;


/**
 * Represents most fields in a frame header, in decoded (not raw) form. Mutable structure,
 * not thread safe. Also has methods for parsing and serializing this structure to/from bytes.
 * All fields can be modified freely when no method call is active.
 * @see FrameDecoder
 * @see StreamInfo#checkFrame(FrameInfo)
 */
public final class FrameInfo {
	
	/*---- Fields ----*/
	
	// Exactly one of these following two fields equals -1.
	
	/**
	 * The index of this frame, where the foremost frame has index 0 and each subsequent frame
	 * increments it. This is either a uint31 value or &minus;1 if unused. Exactly one of the fields
	 * frameIndex and sampleOffse is equal to &minus;1 (not both nor neither). This value can only
	 * be used if the stream info's minBlockSize = maxBlockSize (constant block size encoding style).
	 */
	public int frameIndex;
	
	/**
	 * The offset of the first sample in this frame with respect to the beginning of the
	 * audio stream. This is either a uint36 value or &minus;1 if unused. Exactly one of
	 * the fields frameIndex and sampleOffse is equal to &minus;1 (not both nor neither).
	 */
	public long sampleOffset;
	
	
	/**
	 * The number of audio channels in this frame, in the range 1 to 8 inclusive.
	 * This value is fully determined by the channelAssignment field.
	 */
	public int numChannels;
	
	/**
	 * The raw channel assignment value of this frame, which is a uint4 value.
	 * This indicates the number of channels, but also tells the stereo coding mode.
	 */
	public int channelAssignment;
	
	/**
	 * The number of samples per channel in this frame, in the range 1 to 65536 inclusive.
	 */
	public int blockSize;
	
	/**
	 * The sample rate of this frame in hertz (Hz), in the range 1 to 655360 inclusive,
	 * or &minus;1 if unavailable (i.e. the stream info should be consulted).
	 */
	public int sampleRate;
	
	/**
	 * The sample depth of this frame in bits, in the range 8 to 24 inclusive,
	 * or &minus;1 if unavailable (i.e. the stream info should be consulted).
	 */
	public int sampleDepth;
	
	/**
	 * The size of this frame in bytes, from the start of the sync sequence to the end
	 * of the trailing CRC-16 checksum. A valid value is at least 10, or &minus;1
	 * if unavailable (e.g. the frame header was parsed but not the entire frame).
	 */
	public int frameSize;
	
	
	
	/*---- Constructors ----*/
	
	/**
	 * Constructs a blank frame metadata structure, setting all fields to unknown or invalid values.
	 */
	public FrameInfo() {
		frameIndex        = -1;
		sampleOffset      = -1;
		numChannels       = -1;
		channelAssignment = -1;
		blockSize         = -1;
		sampleRate        = -1;
		sampleDepth       = -1;
		frameSize         = -1;
	}
	
	
	
	/*---- Functions to read FrameInfo from stream ----*/
	
	/**
	 * Reads the next FLAC frame header from the specified input stream, either returning
	 * a new frame info object or {@code null}. The stream must be aligned to a byte
	 * boundary and start at a sync sequence. If EOF is immediately encountered before
	 * any bytes were read, then this returns {@code null}.
	 * <p>Otherwise this reads between 6 to 16 bytes from the stream &ndash; starting
	 * from the sync code, and ending after the CRC-8 value is read (but before reading
	 * any subframes). It tries to parse the frame header data. After the values are
	 * successfully decoded, a new frame info object is created, almost all fields are
	 * set to the parsed values, and it is returned. (This doesn't read to the end
	 * of the frame, so the frameSize field is set to -1.)</p>
	 * @param in the input stream to read from (not {@code null})
	 * @return a new frame info object or {@code null}
	 * @throws NullPointerException if the input stream is {@code null}
	 * @throws DataFormatException if the input data contains invalid values
	 * @throws IOException if an I/O exception occurred
	 */
	public static FrameInfo readFrame(FlacLowLevelInput in) throws IOException {
		// Preliminaries
		in.resetCrcs();
		int temp = in.readByte();
		if (temp == -1)
			return null;
		FrameInfo result = new FrameInfo();
		result.frameSize = -1;
		
		// Read sync bits
		int sync = temp << 6 | in.readUint(6);  // Uint14
		if (sync != 0x3FFE)
			throw new DataFormatException("Sync code expected");
		
		// Read various simple fields
		if (in.readUint(1) != 0)
			throw new DataFormatException("Reserved bit");
		int blockStrategy  = in.readUint(1);
		int blockSizeCode  = in.readUint(4);
		int sampleRateCode = in.readUint(4);
		int chanAsgn       = in.readUint(4);
		result.channelAssignment = chanAsgn;
		if (chanAsgn < 8)
			result.numChannels = chanAsgn + 1;
		else if (8 <= chanAsgn && chanAsgn <= 10)
			result.numChannels = 2;
		else
			throw new DataFormatException("Reserved channel assignment");
		result.sampleDepth = decodeSampleDepth(in.readUint(3));
		if (in.readUint(1) != 0)
			throw new DataFormatException("Reserved bit");
		
		// Read and check the frame/sample position field
		long position = readUtf8Integer(in);  // Reads 1 to 7 bytes
		if (blockStrategy == 0) {
			if ((position >>> 31) != 0)
				throw new DataFormatException("Frame index too large");
			result.frameIndex = (int)position;
			result.sampleOffset = -1;
		} else if (blockStrategy == 1) {
			result.sampleOffset = position;
			result.frameIndex = -1;
		} else
			throw new AssertionError();
		
		// Read variable-length data for some fields
		result.blockSize = decodeBlockSize(blockSizeCode, in);  // Reads 0 to 2 bytes
		result.sampleRate = decodeSampleRate(sampleRateCode, in);  // Reads 0 to 2 bytes
		int computedCrc8 = in.getCrc8();
		if (in.readUint(8) != computedCrc8)
			throw new DataFormatException("CRC-8 mismatch");
		return result;
	}
	
	
	// Reads 1 to 7 whole bytes from the input stream. Return value is a uint36.
	// See: https://hydrogenaud.io/index.php/topic,112831.msg929128.html#msg929128
	private static long readUtf8Integer(FlacLowLevelInput in) throws IOException {
		int head = in.readUint(8);
		int n = Integer.numberOfLeadingZeros(~(head << 24));  // Number of leading 1s in the byte
		assert 0 <= n && n <= 8;
		if (n == 0)
			return head;
		else if (n == 1 || n == 8)
			throw new DataFormatException("Invalid UTF-8 coded number");
		else {
			long result = head & (0x7F >>> n);
			for (int i = 0; i < n - 1; i++) {
				int temp = in.readUint(8);
				if ((temp & 0xC0) != 0x80)
					throw new DataFormatException("Invalid UTF-8 coded number");
				result = (result << 6) | (temp & 0x3F);
			}
			if ((result >>> 36) != 0)
				throw new AssertionError();
			return result;
		}
	}
	
	
	// Argument is a uint4 value. Reads 0 to 2 bytes from the input stream.
	// Return value is in the range [1, 65536].
	private static int decodeBlockSize(int code, FlacLowLevelInput in) throws IOException {
		if ((code >>> 4) != 0)
			throw new IllegalArgumentException();
		switch (code) {
			case 0:  throw new DataFormatException("Reserved block size");
			case 6:  return in.readUint(8) + 1;
			case 7:  return in.readUint(16) + 1;
			default:
				int result = searchSecond(BLOCK_SIZE_CODES, code);
				if (result < 1 || result > 65536)
					throw new AssertionError();
				return result;
		}
	}
	
	
	// Argument is a uint4 value. Reads 0 to 2 bytes from the input stream.
	// Return value is in the range [-1, 655350].
	private static int decodeSampleRate(int code, FlacLowLevelInput in) throws IOException {
		if ((code >>> 4) != 0)
			throw new IllegalArgumentException();
		switch (code) {
			case  0:  return -1;  // Caller should obtain value from stream info metadata block
			case 12:  return in.readUint(8);
			case 13:  return in.readUint(16);
			case 14:  return in.readUint(16) * 10;
			case 15:  throw new DataFormatException("Invalid sample rate");
			default:
				int result = searchSecond(SAMPLE_RATE_CODES, code);
				if (result < 1 || result > 655350)
					throw new AssertionError();
				return result;
		}
	}
	
	
	// Argument is a uint3 value. Pure function and performs no I/O. Return value is in the range [-1, 24].
	private static int decodeSampleDepth(int code) {
		if ((code >>> 3) != 0)
			throw new IllegalArgumentException();
		else if (code == 0)
			return -1;  // Caller should obtain value from stream info metadata block
		else {
			int result = searchSecond(SAMPLE_DEPTH_CODES, code);
			if (result == -1)
				throw new DataFormatException("Reserved bit depth");
			if (result < 1 || result > 32)
				throw new AssertionError();
			return result;
		}
	}
	
	
	
	/*---- Functions to write FrameInfo to stream ----*/
	
	/**
	 * Writes the current state of this object as a frame header to the specified
	 * output stream, from the sync field through to the CRC-8 field (inclusive).
	 * This does not write the data of subframes, the bit padding, nor the CRC-16 field.</p>
	 * <p>The stream must be byte-aligned before this method is called, and will be aligned
	 * upon returning (i.e. it writes a whole number of bytes). This method initially resets
	 * the stream's CRC computations, which is useful behavior for the caller because
	 * it will need to write the CRC-16 at the end of the frame.</p>
	 * @param out the output stream to write to (not {@code null})
	 * @throws NullPointerException if the output stream is {@code null}
	 * @throws IOException if an I/O exception occurred
	 */
	public void writeHeader(BitOutputStream out) throws IOException {
		Objects.requireNonNull(out);
		out.resetCrcs();
		out.writeInt(14, 0x3FFE);  // Sync
		out.writeInt(1, 0);  // Reserved
		out.writeInt(1, 1);  // Blocking strategy
		
		int blockSizeCode = getBlockSizeCode(blockSize);
		out.writeInt(4, blockSizeCode);
		int sampleRateCode = getSampleRateCode(sampleRate);
		out.writeInt(4, sampleRateCode);
		
		out.writeInt(4, channelAssignment);
		out.writeInt(3, getSampleDepthCode(sampleDepth));
		out.writeInt(1, 0);  // Reserved
		
		// Variable-length: 1 to 7 bytes
		if (frameIndex != -1 && sampleOffset == -1)
			writeUtf8Integer(sampleOffset, out);
		else if (sampleOffset != -1 && frameIndex == -1)
			writeUtf8Integer(sampleOffset, out);
		else
			throw new IllegalStateException();
		
		// Variable-length: 0 to 2 bytes
		if (blockSizeCode == 6)
			out.writeInt(8, blockSize - 1);
		else if (blockSizeCode == 7)
			out.writeInt(16, blockSize - 1);
		
		// Variable-length: 0 to 2 bytes
		if (sampleRateCode == 12)
			out.writeInt(8, sampleRate);
		else if (sampleRateCode == 13)
			out.writeInt(16, sampleRate);
		else if (sampleRateCode == 14)
			out.writeInt(16, sampleRate / 10);
		
		out.writeInt(8, out.getCrc8());
	}
	
	
	// Given a uint36 value, this writes 1 to 7 whole bytes to the given output stream.
	private static void writeUtf8Integer(long val, BitOutputStream out) throws IOException {
		if ((val >>> 36) != 0)
			throw new IllegalArgumentException();
		int bitLen = 64 - Long.numberOfLeadingZeros(val);
		if (bitLen <= 7)
			out.writeInt(8, (int)val);
		else {
			int n = (bitLen - 2) / 5;
			out.writeInt(8, (0xFF80 >>> n) | (int)(val >>> (n * 6)));
			for (int i = n - 1; i >= 0; i--)
				out.writeInt(8, 0x80 | ((int)(val >>> (i * 6)) & 0x3F));
		}
	}
	
	
	// Returns a uint4 value representing the given block size. Pure function.
	private static int getBlockSizeCode(int blockSize) {
		int result = searchFirst(BLOCK_SIZE_CODES, blockSize);
		if (result != -1);  // Already done
		else if (1 <= blockSize && blockSize <= 256)
			result = 6;
		else if (1 <= blockSize && blockSize <= 65536)
			result = 7;
		else  // blockSize < 1 || blockSize > 65536
			throw new IllegalArgumentException();
		
		if ((result >>> 4) != 0)
			throw new AssertionError();
		return result;
	}
	
	
	// Returns a uint4 value representing the given sample rate. Pure function.
	private static int getSampleRateCode(int sampleRate) {
		if (sampleRate == 0 || sampleRate < -1)
			throw new IllegalArgumentException();
		int result = searchFirst(SAMPLE_RATE_CODES, sampleRate);
		if (result != -1);  // Already done
		else if (0 <= sampleRate && sampleRate < 256)
			result = 12;
		else if (0 <= sampleRate && sampleRate < 65536)
			result = 13;
		else if (0 <= sampleRate && sampleRate < 655360 && sampleRate % 10 == 0)
			result = 14;
		else
			result = 0;
		
		if ((result >>> 4) != 0)
			throw new AssertionError();
		return result;
	}
	
	
	// Returns a uint3 value representing the given sample depth. Pure function.
	private static int getSampleDepthCode(int sampleDepth) {
		if (sampleDepth != -1 && (sampleDepth < 1 || sampleDepth > 32))
			throw new IllegalArgumentException();
		int result = searchFirst(SAMPLE_DEPTH_CODES, sampleDepth);
		if (result == -1)
			result = 0;
		if ((result >>> 3) != 0)
			throw new AssertionError();
		return result;
	}
	
	
	
	/*---- Tables of constants and search functions ----*/
	
	private static final int searchFirst(int[][] table, int key) {
		for (int[] pair : table) {
			if (pair[0] == key)
				return pair[1];
		}
		return -1;
	}
	
	
	private static final int searchSecond(int[][] table, int key) {
		for (int[] pair : table) {
			if (pair[1] == key)
				return pair[0];
		}
		return -1;
	}
	
	
	private static final int[][] BLOCK_SIZE_CODES = {
		{  192,  1},
		{  576,  2},
		{ 1152,  3},
		{ 2304,  4},
		{ 4608,  5},
		{  256,  8},
		{  512,  9},
		{ 1024, 10},
		{ 2048, 11},
		{ 4096, 12},
		{ 8192, 13},
		{16384, 14},
		{32768, 15},
	};
	
	
	private static final int[][] SAMPLE_DEPTH_CODES = {
		{ 8, 1},
		{12, 2},
		{16, 4},
		{20, 5},
		{24, 6},
	};
	
	
	private static final int[][] SAMPLE_RATE_CODES = {
		{ 88200,  1},
		{176400,  2},
		{192000,  3},
		{  8000,  4},
		{ 16000,  5},
		{ 22050,  6},
		{ 24000,  7},
		{ 32000,  8},
		{ 44100,  9},
		{ 48000, 10},
		{ 96000, 11},
	};
	
}
