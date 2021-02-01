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

package io.nayuki.flac.encode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Objects;


/* 
 * An adapter from RandomAccessFile to OutputStream. These objects have no buffer, so seek()
 * and write() can be safely interleaved. Also, objects of this class have no direct
 * native resources - so it is safe to discard a RandomAccessFileOutputStream object without
 * closing it, as long as other code will close() the underlying RandomAccessFile object.
 */
public final class RandomAccessFileOutputStream extends OutputStream {
	
	/*---- Fields ----*/
	
	private RandomAccessFile out;
	
	
	
	/*---- Constructors ----*/
	
	public RandomAccessFileOutputStream(RandomAccessFile raf) {
		this.out = Objects.requireNonNull(raf);
	}
	
	
	
	/*---- Methods ----*/
	
	public long getPosition() throws IOException {
		return out.getFilePointer();
	}
	
	
	public void seek(long pos) throws IOException {
		out.seek(pos);
	}
	
	
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
	
	
	public void close() throws IOException {
		if (out != null) {
			out.close();
			out = null;
		}
	}
	
}
