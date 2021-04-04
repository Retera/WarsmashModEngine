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
 * Speeds up computations of a signal vector's autocorrelation by avoiding redundant
 * arithmetic operations. Acts as a helper class for LinearPredictiveEncoder.
 * Objects of this class are intended to be immutable, but can't enforce it because
 * they store a reference to a caller-controlled array without making a private copy.
 */
final class FastDotProduct {
	
	/*---- Fields ----*/
	
	// Not null, and precomputed.length <= data.length.
	private long[] data;
	
	// precomputed[i] = dotProduct(0, i, data.length - i). In other words, it is the sum
	// the of products of all unordered pairs of elements whose indices differ by i.
	private double[] precomputed;
	
	
	
	/*---- Constructors ----*/
	
	// Constructs a fast dot product calculator over the given array, with the given maximum difference in indexes.
	// This pre-calculates some dot products and caches them so that future queries can be answered faster.
	// To avoid the cost of copying the entire vector, a reference to the array is saved into this object.
	// The values from data array are still needed when dotProduct() is called, thus no other code is allowed to modify the values.
	public FastDotProduct(long[] data, int maxDelta) {
		// Check arguments
		this.data = Objects.requireNonNull(data);
		if (maxDelta < 0 || maxDelta >= data.length)
			throw new IllegalArgumentException();
		
		// Precompute some dot products
		precomputed = new double[maxDelta + 1];
		for (int i = 0; i < precomputed.length; i++) {
			double sum = 0;
			for (int j = 0; i + j < data.length; j++)
				sum += (double)data[j] * data[i + j];
			precomputed[i] = sum;
		}
	}
	
	
	
	/*---- Methods ----*/
	
	// Returns the dot product of data[off0 : off0 + len] with data[off1 : off1 + len],
	// i.e. data[off0]*data[off1] + data[off0+1]*data[off1+1] + ... + data[off0+len-1]*data[off1+len-1],
	// with potential rounding error. Note that all the endpoints must lie within the bounds
	// of the data array. Also, this method requires abs(off0 - off1) <= maxDelta.
	public double dotProduct(int off0, int off1, int len) {
		if (off0 > off1)  // Symmetric case
			return dotProduct(off1, off0, len);
		
		// Check arguments
		if (off0 < 0 || off1 < 0 || len < 0 || data.length - len < off1)
			throw new IndexOutOfBoundsException();
		assert off0 <= off1;
		int delta = off1 - off0;
		if (delta > precomputed.length)
			throw new IllegalArgumentException();
		
		// Add up a small number of products to remove from the precomputed sum
		double removal = 0;
		for (int i = 0; i < off0; i++)
			removal += (double)data[i] * data[i + delta];
		for (int i = off1 + len; i < data.length; i++)
			removal += (double)data[i] * data[i - delta];
		return precomputed[delta] - removal;
	}
	
}
