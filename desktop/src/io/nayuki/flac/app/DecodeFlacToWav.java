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

package io.nayuki.flac.app;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.decode.FlacDecoder;


/**
 * Decodes a FLAC file to an uncompressed PCM WAV file. Overwrites output file if already exists.
 * Runs silently if successful, otherwise prints error messages to standard error.
 * <p>Usage: java DecodeFlacToWav InFile.flac OutFile.wav</p>
 * <p>Requirements on the FLAC file:</p>
 * <ul>
 *   <li>Sample depth is 8, 16, 24, or 32 bits (not 4, 17, 23, etc.)</li>
 *   <li>Contains no ID3v1 or ID3v2 tags, or other data unrecognized by the FLAC format</li>
 *   <li>Correct total number of samples (not zero) is stored in stream info block</li>
 *   <li>Every frame has a correct header, subframes do not overflow the sample depth,
 *   and other strict checks enforced by this decoder library</li>
 * </ul>
 */
public final class DecodeFlacToWav {
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 2) {
			System.err.println("Usage: java DecodeFlacToWav InFile.flac OutFile.wav");
			System.exit(1);
			return;
		}
		File inFile  = new File(args[0]);
		File outFile = new File(args[1]);
		
		// Decode input FLAC file
		StreamInfo streamInfo;
		int[][] samples;
		try (FlacDecoder dec = new FlacDecoder(inFile)) {
			
			// Handle metadata header blocks
			while (dec.readAndHandleMetadataBlock() != null);
			streamInfo = dec.streamInfo;
			if (streamInfo.sampleDepth % 8 != 0)
				throw new UnsupportedOperationException("Only whole-byte sample depth supported");
			
			// Decode every block
			samples = new int[streamInfo.numChannels][(int)streamInfo.numSamples];
			for (int off = 0; ;) {
				int len = dec.readAudioBlock(samples, off);
				if (len == 0)
					break;
				off += len;
			}
		}
		
		// Check audio MD5 hash
		byte[] expectHash = streamInfo.md5Hash;
		if (Arrays.equals(expectHash, new byte[16]))
			System.err.println("Warning: MD5 hash field was blank");
		else if (!Arrays.equals(StreamInfo.getMd5Hash(samples, streamInfo.sampleDepth), expectHash))
			throw new DataFormatException("MD5 hash check failed");
		// Else the hash check passed
		
		// Start writing WAV output file
		int bytesPerSample = streamInfo.sampleDepth / 8;
		try (DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outFile)))) {
			DecodeFlacToWav.out = out;
			
			// Header chunk
			int sampleDataLen = samples[0].length * streamInfo.numChannels * bytesPerSample;
			out.writeInt(0x52494646);  // "RIFF"
			writeLittleInt32(sampleDataLen + 36);
			out.writeInt(0x57415645);  // "WAVE"
			
			// Metadata chunk
			out.writeInt(0x666D7420);  // "fmt "
			writeLittleInt32(16);
			writeLittleInt16(0x0001);
			writeLittleInt16(streamInfo.numChannels);
			writeLittleInt32(streamInfo.sampleRate);
			writeLittleInt32(streamInfo.sampleRate * streamInfo.numChannels * bytesPerSample);
			writeLittleInt16(streamInfo.numChannels * bytesPerSample);
			writeLittleInt16(streamInfo.sampleDepth);
			
			// Audio data chunk ("data")
			out.writeInt(0x64617461);  // "data"
			writeLittleInt32(sampleDataLen);
			for (int i = 0; i < samples[0].length; i++) {
				for (int j = 0; j < samples.length; j++) {
					int val = samples[j][i];
					if (bytesPerSample == 1)
						out.write(val + 128);  // Convert to unsigned, as per WAV PCM conventions
					else {  // 2 <= bytesPerSample <= 4
						for (int k = 0; k < bytesPerSample; k++)
							out.write(val >>> (k * 8));  // Little endian
					}
				}
			}
		}
	}
	
	
	// Helper members for writing WAV files
	
	private static DataOutputStream out;
	
	private static void writeLittleInt16(int x) throws IOException {
		out.writeShort(Integer.reverseBytes(x) >>> 16);
	}
	
	private static void writeLittleInt32(int x) throws IOException {
		out.writeInt(Integer.reverseBytes(x));
	}
	
}
