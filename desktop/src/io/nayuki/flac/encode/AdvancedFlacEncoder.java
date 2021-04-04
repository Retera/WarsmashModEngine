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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.nayuki.flac.common.StreamInfo;


public final class AdvancedFlacEncoder {
	
	public AdvancedFlacEncoder(StreamInfo info, int[][] samples, int baseSize, int[] sizeMultiples, SubframeEncoder.SearchOptions opts, BitOutputStream out) throws IOException {
		int numSamples = samples[0].length;
		
		// Calculate compressed sizes for many block positions and sizes
		@SuppressWarnings("unchecked")
		SizeEstimate<FrameEncoder>[][] encoderInfo = new SizeEstimate[sizeMultiples.length][(numSamples + baseSize - 1) / baseSize];
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < encoderInfo[0].length; i++) {
			double progress = (double)i / encoderInfo[0].length;
			double timeRemain = (System.currentTimeMillis() - startTime) / 1000.0 / progress * (1 - progress);
			System.err.printf("\rprogress=%.2f%%    timeRemain=%ds", progress * 100, Math.round(timeRemain));
			
			int pos = i * baseSize;
			for (int j = 0; j < encoderInfo.length; j++) {
				int n = Math.min(sizeMultiples[j] * baseSize, numSamples - pos);
				long[][] subsamples = getRange(samples, pos, n);
				encoderInfo[j][i] = FrameEncoder.computeBest(pos, subsamples, info.sampleDepth, info.sampleRate, opts);
			}
		}
		System.err.println();
		
		// Initialize arrays to prepare for dynamic programming
		FrameEncoder[] bestEncoders = new FrameEncoder[encoderInfo[0].length];
		long[] bestSizes = new long[bestEncoders.length];
		Arrays.fill(bestSizes, Long.MAX_VALUE);
		
		// Use dynamic programming to calculate optimum block size switching
		for (int i = 0; i < encoderInfo.length; i++) {
			for (int j = bestSizes.length - 1; j >= 0; j--) {
				long size = encoderInfo[i][j].sizeEstimate;
				if (j + sizeMultiples[i] < bestSizes.length)
					size += bestSizes[j + sizeMultiples[i]];
				if (size < bestSizes[j]) {
					bestSizes[j] = size;
					bestEncoders[j] = encoderInfo[i][j].encoder;
				}
			}
		}
		
		// Do the actual encoding and writing
		info.minBlockSize = 0;
		info.maxBlockSize = 0;
		info.minFrameSize = 0;
		info.maxFrameSize = 0;
		List<Integer> blockSizes = new ArrayList<>();
		for (int i = 0; i < bestEncoders.length; ) {
			FrameEncoder enc = bestEncoders[i];
			int pos = i * baseSize;
			int n = Math.min(enc.metadata.blockSize, numSamples - pos);
			blockSizes.add(n);
			if (info.minBlockSize == 0 || n < info.minBlockSize)
				info.minBlockSize = Math.max(n, 16);
			info.maxBlockSize = Math.max(n, info.maxBlockSize);
			
			long[][] subsamples = getRange(samples, pos, n);
			long startByte = out.getByteCount();
			bestEncoders[i].encode(subsamples, out);
			i += (n + baseSize - 1) / baseSize;
			
			long frameSize = out.getByteCount() - startByte;
			if (frameSize < 0 || (int)frameSize != frameSize)
				throw new AssertionError();
			if (info.minFrameSize == 0 || frameSize < info.minFrameSize)
				info.minFrameSize = (int)frameSize;
			if (frameSize > info.maxFrameSize)
				info.maxFrameSize = (int)frameSize;
		}
	}
	
	
	// Returns the subrange array[ : ][off : off + len] upcasted to long.
	private static long[][] getRange(int[][] array, int off, int len) {
		long[][] result = new long[array.length][len];
		for (int i = 0; i < array.length; i++) {
			int[] src = array[i];
			long[] dest = result[i];
			for (int j = 0; j < len; j++)
				dest[j] = src[off + j];
		}
		return result;
	}
	
}
