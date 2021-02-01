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

import java.util.Objects;


/* 
 * Pairs an integer with an arbitrary object. Immutable structure.
 */
final class SizeEstimate<E> {
	
	/*---- Fields ----*/
	
	public final long sizeEstimate;  // Non-negative
	public final E encoder;  // Not null
	
	
	
	/*---- Constructors ----*/
	
	public SizeEstimate(long size, E enc) {
		if (size < 0)
			throw new IllegalArgumentException();
		sizeEstimate = size;
		encoder = Objects.requireNonNull(enc);
	}
	
	
	
	/*---- Methods ----*/
	
	// Returns this object if the size is less than or equal to the other object, otherwise returns other.
	public SizeEstimate<E> minimum(SizeEstimate<E> other) {
		Objects.requireNonNull(other);
		if (sizeEstimate <= other.sizeEstimate)
			return this;
		else
			return other;
	}
	
}
