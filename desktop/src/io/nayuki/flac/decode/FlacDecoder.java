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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import io.nayuki.flac.common.FrameInfo;
import io.nayuki.flac.common.SeekTable;
import io.nayuki.flac.common.StreamInfo;

/**
 * Handles high-level decoding and seeking in FLAC files. Also returns metadata
 * blocks. Every object is stateful, not thread-safe, and needs to be closed.
 * Sample usage:
 *
 * <pre>
 * // Create a decoder
 *FlacDecoder dec = new FlacDecoder(...);
 *
 *&#x2F;/ Make the decoder process all metadata blocks internally.
 *&#x2F;/ We could capture the returned data for extra processing.
 *&#x2F;/ We must read all metadata before reading audio data.
 *while (dec.readAndHandleMetadataBlock() != null);
 *
 *&#x2F;/ Read audio samples starting from beginning
 *int[][] samples = (...);
 *dec.readAudioBlock(samples, ...);
 *dec.readAudioBlock(samples, ...);
 *dec.readAudioBlock(samples, ...);
 *
 *&#x2F;/ Seek to some position and continue reading
 *dec.seekAndReadAudioBlock(..., samples, ...);
 *dec.readAudioBlock(samples, ...);
 *dec.readAudioBlock(samples, ...);
 *
 *&#x2F;/ Close underlying file stream
 *dec.close();
 * </pre>
 *
 * @see FrameDecoder
 * @see FlacLowLevelInput
 */
public final class FlacDecoder implements AutoCloseable {

	/*---- Fields ----*/

	public StreamInfo streamInfo;
	public SeekTable seekTable;

	private FlacLowLevelInput input;

	private long metadataEndPos;

	private FrameDecoder frameDec;

	/*---- Constructors ----*/

	// Constructs a new FLAC decoder to read the given file.
	// This immediately reads the basic header but not metadata blocks.
	public FlacDecoder(final File file) throws IOException {
		// Initialize streams
		Objects.requireNonNull(file);
		this.input = new SeekableFileFlacInput(file);

		// Read basic header
		if (this.input.readUint(32) != 0x664C6143) {
			throw new DataFormatException("Invalid magic string");
		}
		this.metadataEndPos = -1;
	}

	// Constructs a new FLAC decoder to read the given file.
	// This immediately reads the basic header but not metadata blocks.
	public FlacDecoder(final byte[] file) throws IOException {
		// Initialize streams
		Objects.requireNonNull(file);
		this.input = new ByteArrayFlacInput(file);

		// Read basic header
		if (this.input.readUint(32) != 0x664C6143) {
			throw new DataFormatException("Invalid magic string");
		}
		this.metadataEndPos = -1;
	}

	/*---- Methods ----*/

	// Reads, handles, and returns the next metadata block. Returns a pair (Integer
	// type, byte[] data) if the
	// next metadata block exists, otherwise returns null if the final metadata
	// block was previously read.
	// In addition to reading and returning data, this method also updates the
	// internal state
	// of this object to reflect the new data seen, and throws exceptions for
	// situations such as
	// not starting with a stream info metadata block or encountering duplicates of
	// certain blocks.
	public Object[] readAndHandleMetadataBlock() throws IOException {
		if (this.metadataEndPos != -1) {
			return null; // All metadata already consumed
		}

		// Read entire block
		final boolean last = this.input.readUint(1) != 0;
		final int type = this.input.readUint(7);
		final int length = this.input.readUint(24);
		final byte[] data = new byte[length];
		this.input.readFully(data);

		// Handle recognized block
		if (type == 0) {
			if (this.streamInfo != null) {
				throw new DataFormatException("Duplicate stream info metadata block");
			}
			this.streamInfo = new StreamInfo(data);
		}
		else {
			if (this.streamInfo == null) {
				throw new DataFormatException("Expected stream info metadata block");
			}
			if (type == 3) {
				if (this.seekTable != null) {
					throw new DataFormatException("Duplicate seek table metadata block");
				}
				this.seekTable = new SeekTable(data);
			}
		}

		if (last) {
			this.metadataEndPos = this.input.getPosition();
			this.frameDec = new FrameDecoder(this.input, this.streamInfo.sampleDepth);
		}
		return new Object[] { type, data };
	}

	// Reads and decodes the next block of audio samples into the given buffer,
	// returning the number of samples in the block. The return value is 0 if the
	// read
	// started at the end of stream, or a number in the range [1, 65536] for a valid
	// block.
	// All metadata blocks must be read before starting to read audio blocks.
	public int readAudioBlock(final int[][] samples, final int off) throws IOException {
		if (this.frameDec == null) {
			throw new IllegalStateException("Metadata blocks not fully consumed yet");
		}
		final FrameInfo frame = this.frameDec.readFrame(samples, off);
		if (frame == null) {
			return 0;
		}
		else {
			return frame.blockSize; // In the range [1, 65536]
		}
	}

	// Seeks to the given sample position and reads audio samples into the given
	// buffer,
	// returning the number of samples filled. If audio data is available then the
	// return value
	// is at least 1; otherwise 0 is returned to indicate the end of stream. Note
	// that the
	// sample position can land in the middle of a FLAC block and will still behave
	// correctly.
	// In theory this method subsumes the functionality of readAudioBlock(), but
	// seeking can be
	// an expensive operation so readAudioBlock() should be used for ordinary
	// contiguous streaming.
	public int seekAndReadAudioBlock(final long pos, final int[][] samples, final int off) throws IOException {
		if (this.frameDec == null) {
			throw new IllegalStateException("Metadata blocks not fully consumed yet");
		}

		long[] sampleAndFilePos = getBestSeekPoint(pos);
		if ((pos - sampleAndFilePos[0]) > 300000) {
			sampleAndFilePos = seekBySyncAndDecode(pos);
			sampleAndFilePos[1] -= this.metadataEndPos;
		}
		this.input.seekTo(sampleAndFilePos[1] + this.metadataEndPos);

		long curPos = sampleAndFilePos[0];
		final int[][] smpl = new int[this.streamInfo.numChannels][65536];
		while (true) {
			final FrameInfo frame = this.frameDec.readFrame(smpl, 0);
			if (frame == null) {
				return 0;
			}
			final long nextPos = curPos + frame.blockSize;
			if (nextPos > pos) {
				for (int ch = 0; ch < smpl.length; ch++) {
					System.arraycopy(smpl[ch], (int) (pos - curPos), samples[ch], off, (int) (nextPos - pos));
				}
				return (int) (nextPos - pos);
			}
			curPos = nextPos;
		}
	}

	private long[] getBestSeekPoint(final long pos) {
		long samplePos = 0;
		long filePos = 0;
		if (this.seekTable != null) {
			for (final SeekTable.SeekPoint p : this.seekTable.points) {
				if (p.sampleOffset <= pos) {
					samplePos = p.sampleOffset;
					filePos = p.fileOffset;
				}
				else {
					break;
				}
			}
		}
		return new long[] { samplePos, filePos };
	}

	// Returns a pair (sample offset, file position) such sampleOffset <= pos and
	// abs(sampleOffset - pos)
	// is a relatively small number compared to the total number of samples in the
	// audio file.
	// This method works by skipping to arbitrary places in the file, finding a sync
	// sequence,
	// decoding the frame header, examining the audio position stored in the frame,
	// and possibly deciding
	// to skip to other places and retrying. This changes the state of the input
	// streams as a side effect.
	// There is a small chance of finding a valid-looking frame header but causing
	// erroneous decoding later.
	private long[] seekBySyncAndDecode(final long pos) throws IOException {
		long start = this.metadataEndPos;
		long end = this.input.getLength();
		while ((end - start) > 100000) { // Binary search
			final long mid = (start + end) >>> 1;
			final long[] offsets = getNextFrameOffsets(mid);
			if ((offsets == null) || (offsets[0] > pos)) {
				end = mid;
			}
			else {
				start = offsets[1];
			}
		}
		return getNextFrameOffsets(start);
	}

	// Returns a pair (sample offset, file position) describing the next frame found
	// starting
	// at the given file offset, or null if no frame is found before the end of
	// stream.
	// This changes the state of the input streams as a side effect.
	private long[] getNextFrameOffsets(long filePos) throws IOException {
		if ((filePos < this.metadataEndPos) || (filePos > this.input.getLength())) {
			throw new IllegalArgumentException("File position out of bounds");
		}

		// Repeatedly search for a sync
		while (true) {
			this.input.seekTo(filePos);

			// Finite state machine to match the 2-byte sync sequence
			int state = 0;
			while (true) {
				final int b = this.input.readByte();
				if (b == -1) {
					return null;
				}
				else if (b == 0xFF) {
					state = 1;
				}
				else if ((state == 1) && ((b & 0xFE) == 0xF8)) {
					break;
				}
				else {
					state = 0;
				}
			}

			// Sync found, rewind 2 bytes, try to decode frame header
			filePos = this.input.getPosition() - 2;
			this.input.seekTo(filePos);
			try {
				final FrameInfo frame = FrameInfo.readFrame(this.input);
				return new long[] { getSampleOffset(frame), filePos };
			}
			catch (final DataFormatException e) {
				// Advance past the sync and search again
				filePos += 2;
			}
		}
	}

	// Calculates the sample offset of the given frame, automatically handling the
	// constant-block-size case.
	private long getSampleOffset(final FrameInfo frame) {
		Objects.requireNonNull(frame);
		if (frame.sampleOffset != -1) {
			return frame.sampleOffset;
		}
		else if (frame.frameIndex != -1) {
			return frame.frameIndex * this.streamInfo.maxBlockSize;
		}
		else {
			throw new AssertionError();
		}
	}

	// Closes the underlying input streams and discards object data.
	// This decoder object becomes invalid for any method calls or field usages.
	@Override
	public void close() throws IOException {
		if (this.input != null) {
			this.streamInfo = null;
			this.seekTable = null;
			this.frameDec = null;
			this.input.close();
			this.input = null;
		}
	}

}
