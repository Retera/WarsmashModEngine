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
import java.util.Objects;


/**
 * A FLAC input stream based on a fixed byte array.
 */
public final class ByteArrayFlacInput extends AbstractFlacLowLevelInput {
	
	/*---- Fields ----*/
	
	// The underlying byte array to read from.
	private byte[] data;
	private int offset;
	
	
	
	/*---- Constructors ----*/
	
	public ByteArrayFlacInput(byte[] b) {
		super();
		data = Objects.requireNonNull(b);
		offset = 0;
	}
	
	
	
	/*---- Methods ----*/
	
	public long getLength() {
		return data.length;
	}
	
	
	public void seekTo(long pos) {
		offset = (int)pos;
		positionChanged(pos);
	}
	
	
	protected int readUnderlying(byte[] buf, int off, int len) {
		if (off < 0 || off > buf.length || len < 0 || len > buf.length - off)
			throw new ArrayIndexOutOfBoundsException();
		int n = Math.min(data.length - offset, len);
		if (n == 0)
			return -1;
		System.arraycopy(data, offset, buf, off, n);
		offset += n;
		return n;
	}
	
	
	// Discards data buffers and invalidates this stream. Because this class and its superclass
	// only use memory and have no native resources, it's okay to simply let a ByteArrayFlacInput
	// be garbage-collected without calling close().
	public void close() throws IOException {
		if (data != null) {
			data = null;
			super.close();
		}
	}
	
}
