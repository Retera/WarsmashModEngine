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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.encode.BitOutputStream;
import io.nayuki.flac.encode.FlacEncoder;
import io.nayuki.flac.encode.RandomAccessFileOutputStream;
import io.nayuki.flac.encode.SubframeEncoder;


/**
 * Encodes an uncompressed PCM WAV file to a FLAC file.
 * Overwrites the output file if it already exists.
 * <p>Usage: java EncodeWavToFlac InFile.wav OutFile.flac</p>
 * <p>Requirements on the WAV file:</p>
 * <ul>
 *   <li>Sample depth is 8, 16, 24, or 32 bits (not 4, 17, 23, etc.)</li>
 *   <li>Number of channels is between 1 to 8 inclusive</li>
 *   <li>Sample rate is less than 2<sup>20</sup> hertz</li>
 * </ul>
 */
public final class EncodeWavToFlac {
	
	public static void main(String[] args) throws IOException {
		// Handle command line arguments
		if (args.length != 2) {
			System.err.println("Usage: java EncodeWavToFlac InFile.wav OutFile.flac");
			System.exit(1);
			return;
		}
		File inFile  = new File(args[0]);
		File outFile = new File(args[1]);
		
		// Read WAV file headers and audio sample data
		int[][] samples;
		int sampleRate;
		int sampleDepth;
		try (InputStream in = new BufferedInputStream(new FileInputStream(inFile))) {
			// Parse and check WAV header
			if (!readString(in, 4).equals("RIFF"))
				throw new DataFormatException("Invalid RIFF file header");
			readLittleUint(in, 4);  // Remaining data length
			if (!readString(in, 4).equals("WAVE"))
				throw new DataFormatException("Invalid WAV file header");
			
			// Handle the format chunk
			if (!readString(in, 4).equals("fmt "))
				throw new DataFormatException("Unrecognized WAV file chunk");
			if (readLittleUint(in, 4) != 16)
				throw new DataFormatException("Unsupported WAV file type");
			if (readLittleUint(in, 2) != 0x0001)
				throw new DataFormatException("Unsupported WAV file codec");
			int numChannels = readLittleUint(in, 2);
			if (numChannels < 0 || numChannels > 8)
				throw new RuntimeException("Too many (or few) audio channels");
			sampleRate = readLittleUint(in, 4);
			if (sampleRate <= 0 || sampleRate >= (1 << 20))
				throw new RuntimeException("Sample rate too large or invalid");
			int byteRate = readLittleUint(in, 4);
			int blockAlign = readLittleUint(in, 2);
			sampleDepth = readLittleUint(in, 2);
			if (sampleDepth == 0 || sampleDepth > 32 || sampleDepth % 8 != 0)
				throw new RuntimeException("Unsupported sample depth");
			int bytesPerSample = sampleDepth / 8;
			if (bytesPerSample * numChannels != blockAlign)
				throw new RuntimeException("Invalid block align value");
			if (bytesPerSample * numChannels * sampleRate != byteRate)
				throw new RuntimeException("Invalid byte rate value");
			
			// Handle the data chunk
			if (!readString(in, 4).equals("data"))
				throw new DataFormatException("Unrecognized WAV file chunk");
			int sampleDataLen = readLittleUint(in, 4);
			if (sampleDataLen <= 0 || sampleDataLen % (numChannels * bytesPerSample) != 0)
				throw new DataFormatException("Invalid length of audio sample data");
			int numSamples = sampleDataLen / (numChannels * bytesPerSample);
			samples = new int[numChannels][numSamples];
			for (int i = 0; i < numSamples; i++) {
				for (int ch = 0; ch < numChannels; ch++) {
					int val = readLittleUint(in, bytesPerSample);
					if (sampleDepth == 8)
						val -= 128;
					else
						val = (val << (32 - sampleDepth)) >> (32 - sampleDepth);
					samples[ch][i] = val;
				}
			}
			// Note: There might be chunks after "data", but they can be ignored
		}
		
		// Open output file and encode samples to FLAC
		try (RandomAccessFile raf = new RandomAccessFile(outFile, "rw")) {
			raf.setLength(0);  // Truncate an existing file
			BitOutputStream out = new BitOutputStream(
				new BufferedOutputStream(new RandomAccessFileOutputStream(raf)));
			out.writeInt(32, 0x664C6143);
			
			// Populate and write the stream info structure
			StreamInfo info = new StreamInfo();
			info.sampleRate = sampleRate;
			info.numChannels = samples.length;
			info.sampleDepth = sampleDepth;
			info.numSamples = samples[0].length;
			info.md5Hash = StreamInfo.getMd5Hash(samples, sampleDepth);
			info.write(true, out);
			
			// Encode all frames
			new FlacEncoder(info, samples, 4096, SubframeEncoder.SearchOptions.SUBSET_BEST, out);
			out.flush();
			
			// Rewrite the stream info metadata block, which is
			// located at a fixed offset in the file by definition
			raf.seek(4);
			info.write(true, out);
			out.flush();
		}
	}
	
	
	// Reads len bytes from the given stream and interprets them as a UTF-8 string.
	private static String readString(InputStream in, int len) throws IOException {
		byte[] temp = new byte[len];
		for (int i = 0; i < temp.length; i++) {
			int b = in.read();
			if (b == -1)
				throw new EOFException();
			temp[i] = (byte)b;
		}
		return new String(temp, StandardCharsets.UTF_8);
	}
	
	
	// Reads n bytes (0 <= n <= 4) from the given stream, interpreting
	// them as an unsigned integer encoded in little endian.
	private static int readLittleUint(InputStream in, int n) throws IOException {
		int result = 0;
		for (int i = 0; i < n; i++) {
			int b = in.read();
			if (b == -1)
				throw new EOFException();
			result |= b << (i * 8);
		}
		return result;
	}
	
}
