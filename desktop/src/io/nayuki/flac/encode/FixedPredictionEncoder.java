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
 * Under the fixed prediction coding mode of some order, this provides size calculations on and bitstream encoding of audio sample data.
 */
final class FixedPredictionEncoder extends SubframeEncoder {
	
	// Computes the best way to encode the given values under the fixed prediction coding mode of the given order,
	// returning a size plus a new encoder object associated with the input arguments. The maxRiceOrder argument
	// is used by the Rice encoder to estimate the size of coding the residual signal.
	public static SizeEstimate<SubframeEncoder> computeBest(long[] samples, int shift, int depth, int order, int maxRiceOrder) {
		FixedPredictionEncoder enc = new FixedPredictionEncoder(samples, shift, depth, order);
		samples = LinearPredictiveEncoder.shiftRight(samples, shift);
		LinearPredictiveEncoder.applyLpc(samples, COEFFICIENTS[order], 0);
		long temp = RiceEncoder.computeBestSizeAndOrder(samples, order, maxRiceOrder);
		enc.riceOrder = (int)(temp & 0xF);
		long size = 1 + 6 + 1 + shift + order * depth + (temp >>> 4);
		return new SizeEstimate<SubframeEncoder>(size, enc);
	}
	
	
	
	private final int order;
	public int riceOrder;
	
	
	public FixedPredictionEncoder(long[] samples, int shift, int depth, int order) {
		super(shift, depth);
		if (order < 0 || order >= COEFFICIENTS.length || samples.length < order)
			throw new IllegalArgumentException();
		this.order = order;
	}
	
	
	public void encode(long[] samples, BitOutputStream out) throws IOException {
		Objects.requireNonNull(samples);
		Objects.requireNonNull(out);
		if (samples.length < order)
			throw new IllegalArgumentException();
		
		writeTypeAndShift(8 + order, out);
		samples = LinearPredictiveEncoder.shiftRight(samples, sampleShift);
		
		for (int i = 0; i < order; i++)  // Warmup
			writeRawSample(samples[i], out);
		LinearPredictiveEncoder.applyLpc(samples, COEFFICIENTS[order], 0);
		RiceEncoder.encode(samples, order, riceOrder, out);
	}
	
	
	// The linear predictive coding (LPC) coefficients for fixed prediction of orders 0 to 4 (inclusive).
	private static final int[][] COEFFICIENTS = {
		{},
		{1},
		{2, -1},
		{3, -3, 1},
		{4, -6, 4, -1},
	};
	
}
