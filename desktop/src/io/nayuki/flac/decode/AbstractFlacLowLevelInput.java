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

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


/**
 * A basic implementation of most functionality required by FlacLowLevelInpuut.
 */
public abstract class AbstractFlacLowLevelInput implements FlacLowLevelInput {
	
	/*---- Fields ----*/
	
	// Data from the underlying stream is first stored into this byte buffer before further processing.
	private long byteBufferStartPos;
	private byte[] byteBuffer;
	private int byteBufferLen;
	private int byteBufferIndex;
	
	// The buffer of next bits to return to a reader. Note that byteBufferIndex is incremented when byte
	// values are put into the bit buffer, but they might not have been consumed by the ultimate reader yet.
	private long bitBuffer;  // Only the bottom bitBufferLen bits are valid; the top bits are garbage.
	private int bitBufferLen;  // Always in the range [0, 64].
	
	// Current state of the CRC calculations.
	private int crc8;  // Always a uint8 value.
	private int crc16;  // Always a uint16 value.
	private int crcStartIndex;  // In the range [0, byteBufferLen], unless byteBufferLen = -1.
	
	
	
	/*---- Constructors ----*/
	
	public AbstractFlacLowLevelInput() {
		byteBuffer = new byte[4096];
		positionChanged(0);
	}
	
	
	
	/*---- Methods ----*/
	
	/*-- Stream position --*/
	
	public long getPosition() {
		return byteBufferStartPos + byteBufferIndex - (bitBufferLen + 7) / 8;
	}
	
	
	public int getBitPosition() {
		return (-bitBufferLen) & 7;
	}
	
	
	// When a subclass handles seekTo() and didn't throw UnsupportedOperationException,
	// it must call this method to flush the buffers of upcoming data.
	protected void positionChanged(long pos) {
		byteBufferStartPos = pos;
		Arrays.fill(byteBuffer, (byte)0);  // Defensive clearing, should have no visible effect outside of debugging
		byteBufferLen = 0;
		byteBufferIndex = 0;
		bitBuffer = 0;  // Defensive clearing, should have no visible effect outside of debugging
		bitBufferLen = 0;
		resetCrcs();
	}
	
	
	// Either returns silently or throws an exception.
	private void checkByteAligned() {
		if (bitBufferLen % 8 != 0)
			throw new IllegalStateException("Not at a byte boundary");
	}
	
	
	/*-- Reading bitwise integers --*/
	
	public int readUint(int n) throws IOException {
		if (n < 0 || n > 32)
			throw new IllegalArgumentException();
		while (bitBufferLen < n) {
			int b = readUnderlying();
			if (b == -1)
				throw new EOFException();
			bitBuffer = (bitBuffer << 8) | b;
			bitBufferLen += 8;
			assert 0 <= bitBufferLen && bitBufferLen <= 64;
		}
		int result = (int)(bitBuffer >>> (bitBufferLen - n));
		if (n != 32) {
			result &= (1 << n) - 1;
			assert (result >>> n) == 0;
		}
		bitBufferLen -= n;
		assert 0 <= bitBufferLen && bitBufferLen <= 64;
		return result;
	}
	
	
	public int readSignedInt(int n) throws IOException {
		int shift = 32 - n;
		return (readUint(n) << shift) >> shift;
	}
	
	
	public void readRiceSignedInts(int param, long[] result, int start, int end) throws IOException {
		if (param < 0 || param > 31)
			throw new IllegalArgumentException();
		long unaryLimit = 1L << (53 - param);
		
		byte[] consumeTable = RICE_DECODING_CONSUMED_TABLES[param];
		int[] valueTable = RICE_DECODING_VALUE_TABLES[param];
		while (true) {
			middle:
			while (start <= end - RICE_DECODING_CHUNK) {
				if (bitBufferLen < RICE_DECODING_CHUNK * RICE_DECODING_TABLE_BITS) {
					if (byteBufferIndex <= byteBufferLen - 8) {
						fillBitBuffer();
					} else
						break;
				}
				for (int i = 0; i < RICE_DECODING_CHUNK; i++, start++) {
					// Fast decoder
					int extractedBits = (int)(bitBuffer >>> (bitBufferLen - RICE_DECODING_TABLE_BITS)) & RICE_DECODING_TABLE_MASK;
					int consumed = consumeTable[extractedBits];
					if (consumed == 0)
						break middle;
					bitBufferLen -= consumed;
					result[start] = valueTable[extractedBits];
				}
			}
			
			// Slow decoder
			if (start >= end)
				break;
			long val = 0;
			while (readUint(1) == 0) {
				if (val >= unaryLimit) {
					// At this point, the final decoded value would be so large that the result of the
					// downstream restoreLpc() calculation would not fit in the output sample's bit depth -
					// hence why we stop early and throw an exception. However, this check is conservative
					// and doesn't catch all the cases where the post-LPC result wouldn't fit.
					throw new DataFormatException("Residual value too large");
				}
				val++;
			}
			val = (val << param) | readUint(param);  // Note: Long masking unnecessary because param <= 31
			assert (val >>> 53) == 0;  // Must fit a uint53 by design due to unaryLimit
			val = (val >>> 1) ^ -(val & 1);  // Transform uint53 to int53 according to Rice coding of signed numbers
			assert (val >> 52) == 0 || (val >> 52) == -1;  // Must fit a signed int53 by design
			result[start] = val;
			start++;
		}
	}
	
	
	// Appends at least 8 bits to the bit buffer, or throws EOFException.
	private void fillBitBuffer() throws IOException {
		int i = byteBufferIndex;
		int n = Math.min((64 - bitBufferLen) >>> 3, byteBufferLen - i);
		byte[] b = byteBuffer;
		if (n > 0) {
			for (int j = 0; j < n; j++, i++)
				bitBuffer = (bitBuffer << 8) | (b[i] & 0xFF);
			bitBufferLen += n << 3;
		} else if (bitBufferLen <= 56) {
			int temp = readUnderlying();
			if (temp == -1)
				throw new EOFException();
			bitBuffer = (bitBuffer << 8) | temp;
			bitBufferLen += 8;
		}
		assert 8 <= bitBufferLen && bitBufferLen <= 64;
		byteBufferIndex += n;
	}
	
	
	/*-- Reading bytes --*/
	
	public int readByte() throws IOException {
		checkByteAligned();
		if (bitBufferLen >= 8)
			return readUint(8);
		else {
			assert bitBufferLen == 0;
			return readUnderlying();
		}
	}
	
	
	public void readFully(byte[] b) throws IOException {
		Objects.requireNonNull(b);
		checkByteAligned();
		for (int i = 0; i < b.length; i++)
			b[i] = (byte)readUint(8);
	}
	
	
	// Reads a byte from the byte buffer (if available) or from the underlying stream, returning either a uint8 or -1.
	private int readUnderlying() throws IOException {
		if (byteBufferIndex >= byteBufferLen) {
			if (byteBufferLen == -1)
				return -1;
			byteBufferStartPos += byteBufferLen;
			updateCrcs(0);
			byteBufferLen = readUnderlying(byteBuffer, 0, byteBuffer.length);
			crcStartIndex = 0;
			if (byteBufferLen <= 0)
				return -1;
			byteBufferIndex = 0;
		}
		assert byteBufferIndex < byteBufferLen;
		int temp = byteBuffer[byteBufferIndex] & 0xFF;
		byteBufferIndex++;
		return temp;
	}
	
	
	// Reads up to 'len' bytes from the underlying byte-based input stream into the given array subrange.
	// Returns a value in the range [0, len] for a successful read, or -1 if the end of stream was reached.
	protected abstract int readUnderlying(byte[] buf, int off, int len) throws IOException;
	
	
	/*-- CRC calculations --*/
	
	public void resetCrcs() {
		checkByteAligned();
		crcStartIndex = byteBufferIndex - bitBufferLen / 8;
		crc8 = 0;
		crc16 = 0;
	}
	
	
	public int getCrc8() {
		checkByteAligned();
		updateCrcs(bitBufferLen / 8);
		if ((crc8 >>> 8) != 0)
			throw new AssertionError();
		return crc8;
	}
	
	
	public int getCrc16() {
		checkByteAligned();
		updateCrcs(bitBufferLen / 8);
		if ((crc16 >>> 16) != 0)
			throw new AssertionError();
		return crc16;
	}
	
	
	// Updates the two CRC values with data in byteBuffer[crcStartIndex : byteBufferIndex - unusedTrailingBytes].
	private void updateCrcs(int unusedTrailingBytes) {
		int end = byteBufferIndex - unusedTrailingBytes;
		for (int i = crcStartIndex; i < end; i++) {
			int b = byteBuffer[i] & 0xFF;
			crc8 = CRC8_TABLE[crc8 ^ b] & 0xFF;
			crc16 = CRC16_TABLE[(crc16 >>> 8) ^ b] ^ ((crc16 & 0xFF) << 8);
			assert (crc8 >>> 8) == 0;
			assert (crc16 >>> 16) == 0;
		}
		crcStartIndex = end;
	}
	
	
	/*-- Miscellaneous --*/
	
	// Note: This class only uses memory and has no native resources. It's not strictly necessary to
	// call the implementation of AbstractFlacLowLevelInput.close() here, but it's a good habit anyway.
	public void close() throws IOException {
		byteBuffer = null;
		byteBufferLen = -1;
		byteBufferIndex = -1;
		bitBuffer = 0;
		bitBufferLen = -1;
		crc8 = -1;
		crc16 = -1;
		crcStartIndex = -1;
	}
	
	
	
	/*---- Tables of constants ----*/
	
	// For Rice decoding
	
	private static final int RICE_DECODING_TABLE_BITS = 13;  // Configurable, must be positive
	private static final int RICE_DECODING_TABLE_MASK = (1 << RICE_DECODING_TABLE_BITS) - 1;
	private static final byte[][] RICE_DECODING_CONSUMED_TABLES = new byte[31][1 << RICE_DECODING_TABLE_BITS];
	private static final int[][] RICE_DECODING_VALUE_TABLES = new int[31][1 << RICE_DECODING_TABLE_BITS];
	private static final int RICE_DECODING_CHUNK = 4;  // Configurable, must be positive, and RICE_DECODING_CHUNK * RICE_DECODING_TABLE_BITS <= 64
	
	static {
		for (int param = 0; param < RICE_DECODING_CONSUMED_TABLES.length; param++) {
			byte[] consumed = RICE_DECODING_CONSUMED_TABLES[param];
			int[] values = RICE_DECODING_VALUE_TABLES[param];
			for (int i = 0; ; i++) {
				int numBits = (i >>> param) + 1 + param;
				if (numBits > RICE_DECODING_TABLE_BITS)
					break;
				int bits = ((1 << param) | (i & ((1 << param) - 1)));
				int shift = RICE_DECODING_TABLE_BITS - numBits;
				for (int j = 0; j < (1 << shift); j++) {
					consumed[(bits << shift) | j] = (byte)numBits;
					values[(bits << shift) | j] = (i >>> 1) ^ -(i & 1);
				}
			}
			if (consumed[0] != 0)
				throw new AssertionError();
		}
	}
	
	
	// For CRC calculations
	
	private static byte[] CRC8_TABLE  = new byte[256];
	private static char[] CRC16_TABLE = new char[256];
	
	static {
		for (int i = 0; i < CRC8_TABLE.length; i++) {
			int temp8 = i;
			int temp16 = i << 8;
			for (int j = 0; j < 8; j++) {
				temp8 = (temp8 << 1) ^ ((temp8 >>> 7) * 0x107);
				temp16 = (temp16 << 1) ^ ((temp16 >>> 15) * 0x18005);
			}
			CRC8_TABLE[i] = (byte)temp8;
			CRC16_TABLE[i] = (char)temp16;
		}
	}
	
}
