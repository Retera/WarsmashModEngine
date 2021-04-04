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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import io.nayuki.flac.common.FrameInfo;


/* 
 * Calculates/estimates the encoded size of a frame of audio sample data
 * (including the frame header), and also performs the encoding to an output stream.
 */
final class FrameEncoder {
	
	/*---- Static functions ----*/
	
	public static SizeEstimate<FrameEncoder> computeBest(int sampleOffset, long[][] samples, int sampleDepth, int sampleRate, SubframeEncoder.SearchOptions opt) {
		FrameEncoder enc = new FrameEncoder(sampleOffset, samples, sampleDepth, sampleRate);
		int numChannels = samples.length;
		@SuppressWarnings("unchecked")
		SizeEstimate<SubframeEncoder>[] encoderInfo = new SizeEstimate[numChannels];
		if (numChannels != 2) {
			enc.metadata.channelAssignment = numChannels - 1;
			for (int i = 0; i < encoderInfo.length; i++)
				encoderInfo[i] = SubframeEncoder.computeBest(samples[i], sampleDepth, opt);
		} else {  // Explore the 4 stereo encoding modes
			long[] left  = samples[0];
			long[] right = samples[1];
			long[] mid  = new long[samples[0].length];
			long[] side = new long[mid.length];
			for (int i = 0; i < mid.length; i++) {
				mid[i] = (left[i] + right[i]) >> 1;
				side[i] = left[i] - right[i];
			}
			SizeEstimate<SubframeEncoder> leftInfo  = SubframeEncoder.computeBest(left , sampleDepth, opt);
			SizeEstimate<SubframeEncoder> rightInfo = SubframeEncoder.computeBest(right, sampleDepth, opt);
			SizeEstimate<SubframeEncoder> midInfo   = SubframeEncoder.computeBest(mid  , sampleDepth, opt);
			SizeEstimate<SubframeEncoder> sideInfo  = SubframeEncoder.computeBest(side , sampleDepth + 1, opt);
			long mode1Size = leftInfo.sizeEstimate + rightInfo.sizeEstimate;
			long mode8Size = leftInfo.sizeEstimate + sideInfo.sizeEstimate;
			long mode9Size = rightInfo.sizeEstimate + sideInfo.sizeEstimate;
			long mode10Size = midInfo.sizeEstimate + sideInfo.sizeEstimate;
			long minimum = Math.min(Math.min(mode1Size, mode8Size), Math.min(mode9Size, mode10Size));
			if (mode1Size == minimum) {
				enc.metadata.channelAssignment = 1;
				encoderInfo[0] = leftInfo;
				encoderInfo[1] = rightInfo;
			} else if (mode8Size == minimum) {
				enc.metadata.channelAssignment = 8;
				encoderInfo[0] = leftInfo;
				encoderInfo[1] = sideInfo;
			} else if (mode9Size == minimum) {
				enc.metadata.channelAssignment = 9;
				encoderInfo[0] = sideInfo;
				encoderInfo[1] = rightInfo;
			} else if (mode10Size == minimum) {
				enc.metadata.channelAssignment = 10;
				encoderInfo[0] = midInfo;
				encoderInfo[1] = sideInfo;
			} else
				throw new AssertionError();
		}
		
		// Add up subframe sizes
		long size = 0;
		enc.subEncoders = new SubframeEncoder[encoderInfo.length];
		for (int i = 0; i < enc.subEncoders.length; i++) {
			size += encoderInfo[i].sizeEstimate;
			enc.subEncoders[i] = encoderInfo[i].encoder;
		}
		
		// Count length of header (always in whole bytes)
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try (BitOutputStream bitout = new BitOutputStream(bout)) {
				enc.metadata.writeHeader(bitout);
			}
			bout.close();
			size += bout.toByteArray().length * 8;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		
		// Count padding and footer
		size = (size + 7) / 8;  // Round up to nearest byte
		size += 2;  // CRC-16
		return new SizeEstimate<>(size, enc);
	}
	
	
	
	/*---- Fields ----*/
	
	public FrameInfo metadata;
	private SubframeEncoder[] subEncoders;
	
	
	
	/*---- Constructors ----*/
	
	public FrameEncoder(int sampleOffset, long[][] samples, int sampleDepth, int sampleRate) {
		metadata = new FrameInfo();
		metadata.sampleOffset = sampleOffset;
		metadata.sampleDepth = sampleDepth;
		metadata.sampleRate = sampleRate;
		metadata.blockSize = samples[0].length;
		metadata.channelAssignment = samples.length - 1;
	}
	
	
	
	/*---- Public methods ----*/
	
	public void encode(long[][] samples, BitOutputStream out) throws IOException {
		// Check arguments
		Objects.requireNonNull(samples);
		Objects.requireNonNull(out);
		if (samples[0].length != metadata.blockSize)
			throw new IllegalArgumentException();
		
		metadata.writeHeader(out);
		
		int chanAsgn = metadata.channelAssignment;
		if (0 <= chanAsgn && chanAsgn <= 7) {
			for (int i = 0; i < samples.length; i++)
				subEncoders[i].encode(samples[i], out);
		} else if (8 <= chanAsgn || chanAsgn <= 10) {
			long[] left  = samples[0];
			long[] right = samples[1];
			long[] mid  = new long[metadata.blockSize];
			long[] side = new long[metadata.blockSize];
			for (int i = 0; i < metadata.blockSize; i++) {
				mid[i] = (left[i] + right[i]) >> 1;
				side[i] = left[i] - right[i];
			}
			if (chanAsgn == 8) {
				subEncoders[0].encode(left, out);
				subEncoders[1].encode(side, out);
			} else if (chanAsgn == 9) {
				subEncoders[0].encode(side, out);
				subEncoders[1].encode(right, out);
			} else if (chanAsgn == 10) {
				subEncoders[0].encode(mid, out);
				subEncoders[1].encode(side, out);
			} else
				throw new AssertionError();
		} else
			throw new AssertionError();
		out.alignToByte();
		out.writeInt(16, out.getCrc16());
	}
	
}
