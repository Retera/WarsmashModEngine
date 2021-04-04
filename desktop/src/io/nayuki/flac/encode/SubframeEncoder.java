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
import java.util.Objects;


/* 
 * Calculates/estimates the encoded size of a subframe of audio sample data, and also performs the encoding to an output stream.
 */
public abstract class SubframeEncoder {
	
	/*---- Static functions ----*/
	
	// Computes/estimates the best way to encode the given vector of audio sample data at the given sample depth under
	// the given search criteria, returning a size estimate plus a new encoder object associated with that size.
	public static SizeEstimate<SubframeEncoder> computeBest(long[] samples, int sampleDepth, SearchOptions opt) {
		// Check arguments
		Objects.requireNonNull(samples);
		if (sampleDepth < 1 || sampleDepth > 33)
			throw new IllegalArgumentException();
		Objects.requireNonNull(opt);
		for (long x : samples) {
			x >>= sampleDepth - 1;
			if (x != 0 && x != -1)  // Check that the input actually fits the indicated sample depth
				throw new IllegalArgumentException();
		}
		
		// Encode with constant if possible
		SizeEstimate<SubframeEncoder> result = ConstantEncoder.computeBest(samples, 0, sampleDepth);
		if (result != null)
			return result;
		
		// Detect number of trailing zero bits
		int shift = computeWastedBits(samples);
		
		// Start with verbatim as fallback
		result = VerbatimEncoder.computeBest(samples, shift, sampleDepth);
		
		// Try fixed prediction encoding
		for (int order = opt.minFixedOrder; 0 <= order && order <= opt.maxFixedOrder; order++) {
			SizeEstimate<SubframeEncoder> temp = FixedPredictionEncoder.computeBest(
				samples, shift, sampleDepth, order, opt.maxRiceOrder);
			result = result.minimum(temp);
		}
		
		// Try linear predictive coding
		FastDotProduct fdp = new FastDotProduct(samples, Math.max(opt.maxLpcOrder, 0));
		for (int order = opt.minLpcOrder; 0 <= order && order <= opt.maxLpcOrder; order++) {
			SizeEstimate<SubframeEncoder> temp = LinearPredictiveEncoder.computeBest(
				samples, shift, sampleDepth, order, Math.min(opt.lpcRoundVariables, order), fdp, opt.maxRiceOrder);
			result = result.minimum(temp);
		}
		
		// Return the encoder found with the lowest bit length
		return result;
	}
	
	
	// Looks at each value in the array and computes the minimum number of trailing binary zeros
	// among all the elements. For example, computedwastedBits({0b10, 0b10010, 0b1100}) = 1.
	// If there are no elements or every value is zero (the former actually implies the latter), then
	// the return value is 0. This is because every zero value has an infinite number of trailing zeros.
	private static int computeWastedBits(long[] data) {
		Objects.requireNonNull(data);
		long accumulator = 0;
		for (long x : data)
			accumulator |= x;
		if (accumulator == 0)
			return 0;
		else {
			int result = Long.numberOfTrailingZeros(accumulator);
			assert 0 <= result && result <= 63;
			return result;
		}
	}
	
	
	
	/*---- Instance members ----*/
	
	protected final int sampleShift;  // Number of bits to shift each sample right by. In the range [0, sampleDepth].
	protected final int sampleDepth;  // Stipulate that each audio sample fits in a signed integer of this width. In the range [1, 33].
	
	
	// Constructs a subframe encoder on some data array with the given right shift (wasted bits) and sample depth.
	// Note that every element of the array must fit in a signed depth-bit integer and have at least 'shift' trailing binary zeros.
	// After the encoder object is created and when encode() is called, it must receive the same array length and values (but the object reference can be different).
	// Subframe encoders should not retain a reference to the sample data array because the higher-level encoder may request and
	// keep many size estimates coupled with encoder objects, but only utilize a small number of encoder objects in the end.
	protected SubframeEncoder(int shift, int depth) {
		if (depth < 1 || depth > 33 || shift < 0 || shift > depth)
			throw new IllegalArgumentException();
		sampleShift = shift;
		sampleDepth = depth;
	}
	
	
	// Encodes the given vector of audio sample data to the given bit output stream
	// using the current encoding method (dictated by subclasses and field values).
	// This requires the data array to have the same values (but not necessarily be the same object reference)
	// as the array that was passed to the constructor when this encoder object was created.
	public abstract void encode(long[] samples, BitOutputStream out) throws IOException;
	
	
	// Writes the subframe header to the given output stream, based on the given
	// type code (uint6) and this object's sampleShift field (a.k.a. wasted bits per sample).
	protected final void writeTypeAndShift(int type, BitOutputStream out) throws IOException {
		// Check arguments
		if ((type >>> 6) != 0)
			throw new IllegalArgumentException();
		Objects.requireNonNull(out);
		
		// Write some fields
		out.writeInt(1, 0);
		out.writeInt(6, type);
		
		// Write shift value in quasi-unary
		if (sampleShift == 0)
			out.writeInt(1, 0);
		else {
			out.writeInt(1, 1);
			for (int i = 0; i < sampleShift - 1; i++)
				out.writeInt(1, 0);
			out.writeInt(1, 1);
		}
	}
	
	
	// Writes the given value to the output stream as a signed (sampleDepth-sampleShift) bit integer.
	// Note that the value to being written is equal to the raw sample value shifted right by sampleShift.
	protected final void writeRawSample(long val, BitOutputStream out) throws IOException {
		int width = sampleDepth - sampleShift;
		if (width < 1 || width > 33)
			throw new IllegalStateException();
		long temp = val >> (width - 1);
		if (temp != 0 && temp != -1)
			throw new IllegalArgumentException();
		if (width <= 32)
			out.writeInt(width, (int)val);
		else {  // width == 33
			out.writeInt(1, (int)(val >>> 32));
			out.writeInt(32, (int)val);
		}
	}
	
	
	
	/*---- Helper structure ----*/
	
	// Represents options for how to search the encoding parameters for a subframe. It is used directly by
	// SubframeEncoder.computeBest() and indirectly by its sub-calls. Objects of this class are immutable.
	public static final class SearchOptions {
		
		/*-- Fields --*/
		
		// The range of orders to test for fixed prediction mode, possibly none.
		// The values satisfy (minFixedOrder = maxFixedOrder = -1) || (0 <= minFixedOrder <= maxFixedOrder <= 4).
		public final int minFixedOrder;
		public final int maxFixedOrder;
		
		// The range of orders to test for linear predictive coding (LPC) mode, possibly none.
		// The values satisfy (minLpcOrder = maxLpcOrder = -1) || (1 <= minLpcOrder <= maxLpcOrder <= 32).
		// Note that the FLAC subset format requires maxLpcOrder <= 12 when sampleRate <= 48000.
		public final int minLpcOrder;
		public final int maxLpcOrder;
		
		// How many LPC coefficient variables to try rounding both up and down.
		// In the range [0, 30]. Note that each increase by one will double the search time!
		public final int lpcRoundVariables;
		
		// The maximum partition order used in Rice coding. The minimum is not configurable and always 0.
		// In the range [0, 15]. Note that the FLAC subset format requires maxRiceOrder <= 8.
		public final int maxRiceOrder;
		
		
		/*-- Constructors --*/
		
		// Constructs a search options object based on the given values,
		// throwing an IllegalArgumentException if and only if they are nonsensical.
		public SearchOptions(int minFixedOrder, int maxFixedOrder, int minLpcOrder, int maxLpcOrder, int lpcRoundVars, int maxRiceOrder) {
			// Check argument ranges
			if ((minFixedOrder != -1 || maxFixedOrder != -1) &&
					!(0 <= minFixedOrder && minFixedOrder <= maxFixedOrder && maxFixedOrder <= 4))
				throw new IllegalArgumentException();
			if ((minLpcOrder != -1 || maxLpcOrder != -1) &&
					!(1 <= minLpcOrder && minLpcOrder <= maxLpcOrder && maxLpcOrder <= 32))
				throw new IllegalArgumentException();
			if (lpcRoundVars < 0 || lpcRoundVars > 30)
				throw new IllegalArgumentException();
			if (maxRiceOrder < 0 || maxRiceOrder > 15)
				throw new IllegalArgumentException();
			
			// Copy arguments to fields
			this.minFixedOrder = minFixedOrder;
			this.maxFixedOrder = maxFixedOrder;
			this.minLpcOrder = minLpcOrder;
			this.maxLpcOrder = maxLpcOrder;
			this.lpcRoundVariables = lpcRoundVars;
			this.maxRiceOrder = maxRiceOrder;
		}
		
		
		/*-- Constants for recommended defaults --*/
		
		// Note that these constants are for convenience only, and offer little promises in terms of API stability.
		// For example, there is no expectation that the set of search option names as a whole,
		// or the values of each search option will remain the same from version to version.
		// Even if a search option retains the same value across code versions, the underlying encoder implementation
		// can change in such a way that the encoded output is not bit-identical or size-identical across versions.
		// Therefore, treat these search options as suggestions that strongly influence the encoded FLAC output,
		// but *not* as firm guarantees that the same audio data with the same options will forever produce the same result.
		
		// These search ranges conform to the FLAC subset format.
		public static final SearchOptions SUBSET_ONLY_FIXED = new SearchOptions(0, 4, -1, -1, 0, 8);
		public static final SearchOptions SUBSET_MEDIUM     = new SearchOptions(0, 1,  2,  8, 0, 5);
		public static final SearchOptions SUBSET_BEST       = new SearchOptions(0, 1,  2, 12, 0, 8);
		public static final SearchOptions SUBSET_INSANE     = new SearchOptions(0, 4,  1, 12, 4, 8);
		
		// These cannot guarantee that an encoded file conforms to the FLAC subset (i.e. they are lax).
		public static final SearchOptions LAX_MEDIUM = new SearchOptions(0, 1, 2, 22, 0, 15);
		public static final SearchOptions LAX_BEST   = new SearchOptions(0, 1, 2, 32, 0, 15);
		public static final SearchOptions LAX_INSANE = new SearchOptions(0, 1, 2, 32, 4, 15);
		
	}
	
}
