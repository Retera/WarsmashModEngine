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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;


/* 
 * Under the linear predictive coding (LPC) mode of some order, this provides size estimates on and bitstream encoding of audio sample data.
 */
final class LinearPredictiveEncoder extends SubframeEncoder {
	
	// Computes a good way to encode the given values under the linear predictive coding (LPC) mode of the given order,
	// returning a size plus a new encoder object associated with the input arguments. This process of minimizing the size
	// has an enormous search space, and it is impossible to guarantee the absolute optimal solution. The maxRiceOrder argument
	// is used by the Rice encoder to estimate the size of coding the residual signal. The roundVars argument controls
	// how many different coefficients are tested rounding both up and down, resulting in exponential time behavior.
	public static SizeEstimate<SubframeEncoder> computeBest(long[] samples, int shift, int depth, int order, int roundVars, FastDotProduct fdp, int maxRiceOrder) {
		// Check arguments
		if (order < 1 || order > 32)
			throw new IllegalArgumentException();
		if (roundVars < 0 || roundVars > order || roundVars > 30)
			throw new IllegalArgumentException();
		
		LinearPredictiveEncoder enc = new LinearPredictiveEncoder(samples, shift, depth, order, fdp);
		samples = shiftRight(samples, shift);
		
		final double[] residues;
		Integer[] indices = null;
		int scaler = 1 << enc.coefShift;
		if (roundVars > 0) {
			residues = new double[order];
			indices = new Integer[order];
			for (int i = 0; i < order; i++) {
				residues[i] = Math.abs(Math.round(enc.realCoefs[i] * scaler) - enc.realCoefs[i] * scaler);
				indices[i] = i;
			}
			Arrays.sort(indices, new Comparator<Integer>() {
				public int compare(Integer x, Integer y) {
					return Double.compare(residues[y], residues[x]);
				}
			});
		} else
			residues = null;
		
		long bestSize = Long.MAX_VALUE;
		int[] bestCoefs = enc.coefficients.clone();
		for (int i = 0; i < (1 << roundVars); i++) {
			for (int j = 0; j < roundVars; j++) {
				int k = indices[j];
				double coef = enc.realCoefs[k];
				int val;
				if (((i >>> j) & 1) == 0)
					val = (int)Math.floor(coef * scaler);
				else
					val = (int)Math.ceil(coef * scaler);
				enc.coefficients[order - 1 - k] = Math.max(Math.min(val, (1 << (enc.coefDepth - 1)) - 1), -(1 << (enc.coefDepth - 1)));
			}
			
			long[] newData = roundVars > 0 ? samples.clone() : samples;
			applyLpc(newData, enc.coefficients, enc.coefShift);
			long temp = RiceEncoder.computeBestSizeAndOrder(newData, order, maxRiceOrder);
			long size = 1 + 6 + 1 + shift + order * depth + (temp >>> 4);
			if (size < bestSize) {
				bestSize = size;
				bestCoefs = enc.coefficients.clone();
				enc.riceOrder = (int)(temp & 0xF);
			}
		}
		enc.coefficients = bestCoefs;
		return new SizeEstimate<SubframeEncoder>(bestSize, enc);
	}
	
	
	
	private final int order;
	private final double[] realCoefs;
	private int[] coefficients;
	private final int coefDepth;
	private final int coefShift;
	public int riceOrder;
	
	
	public LinearPredictiveEncoder(long[] samples, int shift, int depth, int order, FastDotProduct fdp) {
		super(shift, depth);
		int numSamples = samples.length;
		if (order < 1 || order > 32 || numSamples < order)
			throw new IllegalArgumentException();
		this.order = order;
		
		// Set up matrix to solve linear least squares problem
		double[][] matrix = new double[order][order + 1];
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix[r].length; c++) {
				double val;
				if (c >= r)
					val = fdp.dotProduct(r, c, samples.length - order);
				else
					val = matrix[c][r];
				matrix[r][c] = val;
			}
		}
		
		// Solve matrix, then examine range of coefficients
		realCoefs = solveMatrix(matrix);
		double maxCoef = 0;
		for (double x : realCoefs)
			maxCoef = Math.max(Math.abs(x), maxCoef);
		int wholeBits = maxCoef >= 1 ? (int)(Math.log(maxCoef) / Math.log(2)) + 1 : 0;
		
		// Quantize and store the coefficients
		coefficients = new int[order];
		coefDepth = 15;  // The maximum possible
		coefShift = coefDepth - 1 - wholeBits;
		for (int i = 0; i < realCoefs.length; i++) {
			double coef = realCoefs[realCoefs.length - 1 - i];
			int val = (int)Math.round(coef * (1 << coefShift));
			coefficients[i] = Math.max(Math.min(val, (1 << (coefDepth - 1)) - 1), -(1 << (coefDepth - 1)));
		}
	}
	
	
	// Solves an n * (n+1) augmented matrix (which modifies its values as a side effect),
	// returning a new solution vector of length n.
	private static double[] solveMatrix(double[][] mat) {
		// Gauss-Jordan elimination algorithm
		int rows = mat.length;
		int cols = mat[0].length;
		if (rows + 1 != cols)
			throw new IllegalArgumentException();
		
		// Forward elimination
		int numPivots = 0;
		for (int j = 0; j < rows && numPivots < rows; j++) {
			int pivotRow = rows;
			double pivotMag = 0;
			for (int i = numPivots; i < rows; i++) {
				if (Math.abs(mat[i][j]) > pivotMag) {
					pivotMag = Math.abs(mat[i][j]);
					pivotRow = i;
				}
			}
			if (pivotRow == rows)
				continue;
			
			double[] temp = mat[numPivots];
			mat[numPivots] = mat[pivotRow];
			mat[pivotRow] = temp;
			pivotRow = numPivots;
			numPivots++;
			
			double factor = mat[pivotRow][j];
			for (int k = 0; k < cols; k++)
				mat[pivotRow][k] /= factor;
			mat[pivotRow][j] = 1;
			
			for (int i = pivotRow + 1; i < rows; i++) {
				factor = mat[i][j];
				for (int k = 0; k < cols; k++)
					mat[i][k] -= mat[pivotRow][k] * factor;
				mat[i][j] = 0;
			}
		}
		
		// Back substitution
		double[] result = new double[rows];
		for (int i = numPivots - 1; i >= 0; i--) {
			int pivotCol = 0;
			while (pivotCol < cols && mat[i][pivotCol] == 0)
				pivotCol++;
			if (pivotCol == cols)
				continue;
			result[pivotCol] = mat[i][cols - 1];
			
			for (int j = i - 1; j >= 0; j--) {
				double factor = mat[j][pivotCol];
				for (int k = 0; k < cols; k++)
					mat[j][k] -= mat[i][k] * factor;
				mat[j][pivotCol] = 0;
			}
		}
		return result;
	}
	
	
	public void encode(long[] samples, BitOutputStream out) throws IOException {
		Objects.requireNonNull(samples);
		Objects.requireNonNull(out);
		if (samples.length < order)
			throw new IllegalArgumentException();
		
		writeTypeAndShift(32 + order - 1, out);
		samples = shiftRight(samples, sampleShift);
		
		for (int i = 0; i < order; i++)  // Warmup
			writeRawSample(samples[i], out);
		out.writeInt(4, coefDepth - 1);
		out.writeInt(5, coefShift);
		for (int x : coefficients)
			out.writeInt(coefDepth, x);
		applyLpc(samples, coefficients, coefShift);
		RiceEncoder.encode(samples, order, riceOrder, out);
	}
	
	
	
	/*---- Static helper functions ----*/
	
	// Applies linear prediction to data[coefs.length : data.length] so that newdata[i] =
	// data[i] - ((data[i-1]*coefs[0] + data[i-2]*coefs[1] + ... + data[i-coefs.length]*coefs[coefs.length]) >> shift).
	// By FLAC parameters, each data[i] must fit in a signed 33-bit integer, each coef must fit in signed int15, and coefs.length <= 32.
	// When these preconditions are met, they guarantee the lack of arithmetic overflow in the computation and results,
	// and each value written back to the data array fits in a signed int53.
	static void applyLpc(long[] data, int[] coefs, int shift) {
		// Check arguments and arrays strictly
		Objects.requireNonNull(data);
		Objects.requireNonNull(coefs);
		if (coefs.length > 32 || shift < 0 || shift > 63)
			throw new IllegalArgumentException();
		for (long x : data) {
			x >>= 32;
			if (x != 0 && x != -1)  // Check if it fits in signed int33
				throw new IllegalArgumentException();
		}
		for (int x : coefs) {
			x >>= 14;
			if (x != 0 && x != -1)  // Check if it fits in signed int15
				throw new IllegalArgumentException();
		}
		
		// Perform the LPC convolution/FIR
		for (int i = data.length - 1; i >= coefs.length; i--) {
			long sum = 0;
			for (int j = 0; j < coefs.length; j++)
				sum += data[i - 1 - j] * coefs[j];
			long val = data[i] - (sum >> shift);
			if ((val >> 52) != 0 && (val >> 52) != -1)  // Check if it fits in signed int53
				throw new AssertionError();
			data[i] = val;
		}
	}
	
	
	// Returns a new array where each result[i] = data[i] >> shift.
	static long[] shiftRight(long[] data, int shift) {
		Objects.requireNonNull(data);
		if (shift < 0 || shift > 63)
			throw new IllegalArgumentException();
		long[] result = new long[data.length];
		for (int i = 0; i < data.length; i++)
			result[i] = data[i] >> shift;
		return result;
	}
	
}
