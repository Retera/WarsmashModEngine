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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.nayuki.flac.decode.FlacDecoder;
import io.nayuki.flac.encode.BitOutputStream;


/**
 * Represents precisely all the fields of a seek table metadata block. Mutable structure,
 * not thread-safe. Also has methods for parsing and serializing this structure to/from bytes.
 * All fields and objects can be modified freely when no method call is active.
 * @see FlacDecoder
 */
public final class SeekTable {
	
	/*---- Fields ----*/
	
	/**
	 * The list of seek points in this seek table. It is okay to replace this
	 * list as needed (the initially constructed list object is not special).
	 */
	public List<SeekPoint> points;
	
	
	
	/*---- Constructors ----*/
	
	/**
	 * Constructs a blank seek table with an initially empty
	 * list of seek points. (Note that the empty state is legal.)
	 */
	public SeekTable() {
		points = new ArrayList<>();
	}
	
	
	/**
	 * Constructs a seek table by parsing the given byte array representing the metadata block.
	 * (The array must contain only the metadata payload, without the type or length fields.)
	 * <p>This constructor does not check the validity of the seek points, namely the ordering
	 * of seek point offsets, so calling {@link#checkValues()} on the freshly constructed object
	 * can fail. However, this does guarantee that every point's frameSamples field is a uint16.</p>
	 * @param b the metadata block's payload data to parse (not {@code null})
	 * @throws NullPointerException if the array is {@code null}
	 * @throws IllegalArgumentException if the array length
	 * is not a multiple of 18 (size of each seek point)
	 */
	public SeekTable(byte[] b) {
		this();
		Objects.requireNonNull(b);
		if (b.length % 18 != 0)
			throw new IllegalArgumentException("Data contains a partial seek point");
		try {
			DataInput in = new DataInputStream(new ByteArrayInputStream(b));
			for (int i = 0; i < b.length; i += 18) {
				SeekPoint p = new SeekPoint();
				p.sampleOffset = in.readLong();
				p.fileOffset = in.readLong();
				p.frameSamples = in.readUnsignedShort();
				points.add(p);
			}
			// Skip closing the in-memory streams
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	
	
	/*---- Methods ----*/
	
	/**
	 * Checks the state of this object and returns silently if all these criteria pass:
	 * <ul>
	 * 	 <li>No object is {@code null}</li>
	 *   <li>The frameSamples field of each point is a uint16 value</li>
	 *   <li>All points with sampleOffset = &minus;1 (i.e. 0xFFF...FFF) are at the end of the list</li>
	 *   <li>All points with sampleOffset &ne; &minus;1 have strictly increasing
	 *   values of sampleOffset and non-decreasing values of fileOffset</li>
	 * </ul>
	 * @throws NullPointerException if the list or an element is {@code null}
	 * @throws IllegalStateException if the current list of seek points is contains invalid data
	 */
	public void checkValues() {
		// Check list and each point
		Objects.requireNonNull(points);
		for (SeekPoint p : points) {
			Objects.requireNonNull(p);
			if ((p.frameSamples & 0xFFFF) != p.frameSamples)
				throw new IllegalStateException("Frame samples outside uint16 range");
		}
		
		// Check ordering of points
		for (int i = 1; i < points.size(); i++) {
			SeekPoint p = points.get(i);
			if (p.sampleOffset != -1) {
				SeekPoint q = points.get(i - 1);
				if (p.sampleOffset <= q.sampleOffset)
					throw new IllegalStateException("Sample offsets out of order");
				if (p.fileOffset < q.fileOffset)
					throw new IllegalStateException("File offsets out of order");
			}
		}
	}
	
	
	/**
	 * Writes all the points of this seek table as a metadata block to the specified output stream,
	 * also indicating whether it is the last metadata block. (This does write the type and length
	 * fields for the metadata block, unlike the constructor which takes an array without those fields.)
	 * @param last whether the metadata block is the final one in the FLAC file
	 * @param out the output stream to write to (not {@code null})
	 * @throws NullPointerException if the output stream is {@code null}
	 * @throws IllegalStateException if there are too many
	 * @throws IOException if an I/O exception occurred
	 * seek points (> 932067) or {@link#checkValues()} fails
	 */
	public void write(boolean last, BitOutputStream out) throws IOException {
		// Check arguments and state
		Objects.requireNonNull(out);
		Objects.requireNonNull(points);
		if (points.size() > ((1 << 24) - 1) / 18)
			throw new IllegalStateException("Too many seek points");
		checkValues();
		
		// Write metadata block header
		out.writeInt(1, last ? 1 : 0);
		out.writeInt(7, 3);
		out.writeInt(24, points.size() * 18);
		
		// Write each seek point
		for (SeekPoint p : points) {
			out.writeInt(32, (int)(p.sampleOffset >>> 32));
			out.writeInt(32, (int)(p.sampleOffset >>>  0));
			out.writeInt(32, (int)(p.fileOffset >>> 32));
			out.writeInt(32, (int)(p.fileOffset >>>  0));
			out.writeInt(16, p.frameSamples);
		}
	}
	
	
	
	/*---- Helper structure ----*/
	
	/**
	 * Represents a seek point entry in a seek table. Mutable structure, not thread-safe.
	 * This class itself does not check the correctness of data, but other classes might.
	 * <p>A seek point with data (sampleOffset = x, fileOffset = y, frameSamples = z) means
	 * that at byte position (y + (byte offset of foremost audio frame)) in the file,
	 * a FLAC frame begins (with the sync sequence), that frame has sample offset x
	 * (where sample 0 is defined as the start of the audio stream),
	 * and the frame contains z samples per channel.
	 * @see SeekTable
	 */
	public static final class SeekPoint {
		
		/**
		 * The sample offset in the audio stream, a uint64 value.
		 * A value of -1 (i.e. 0xFFF...FFF) means this is a placeholder point.
		 */
		public long sampleOffset;
		
		/**
		 * The byte offset relative to the start of the foremost frame, a uint64 value.
		 * If sampleOffset is -1, then this value is ignored.
		 */
		public long fileOffset;
		
		/**
		 * The number of audio samples in the target block/frame, a uint16 value.
		 * If sampleOffset is -1, then this value is ignored.
		 */
		public int frameSamples;
		
	}
	
}
