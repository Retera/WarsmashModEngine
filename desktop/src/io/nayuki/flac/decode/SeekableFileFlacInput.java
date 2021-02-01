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
import java.io.RandomAccessFile;
import java.util.Objects;


/**
 * A FLAC input stream based on a {@link RandomAccessFile}.
 */
public final class SeekableFileFlacInput extends AbstractFlacLowLevelInput {
	
	/*---- Fields ----*/
	
	// The underlying byte-based input stream to read from.
	private RandomAccessFile raf;
	
	
	
	/*---- Constructors ----*/
	
	public SeekableFileFlacInput(File file) throws IOException {
		super();
		Objects.requireNonNull(file);
		this.raf = new RandomAccessFile(file, "r");
	}
	
	
	
	/*---- Methods ----*/
	
	public long getLength() {
		try {
			return raf.length();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void seekTo(long pos) throws IOException {
		raf.seek(pos);
		positionChanged(pos);
	}
	
	
	protected int readUnderlying(byte[] buf, int off, int len) throws IOException {
		return raf.read(buf, off, len);
	}
	
	
	// Closes the underlying RandomAccessFile stream (very important).
	public void close() throws IOException {
		if (raf != null) {
			raf.close();
			raf = null;
			super.close();
		}
	}
	
}
