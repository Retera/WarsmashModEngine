/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.etheller.warsmash.audio;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.etheller.warsmash.audio.Wav.WavInputStream;

import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.decode.FlacDecoder;

public class Flac {
	static public class Music extends OpenALMusic {
		private WavInputStream input;

		public Music(final OpenALAudio audio, final FileHandle file) {
			super(audio, file);
			try {
				this.input = new WavInputStream(new ByteArrayInputStream(makeItWav(file)), file);
			}
			catch (final IOException ex) {
				throw new GdxRuntimeException("Error reading FLAC file: " + this.file, ex);
			}
			if (audio.noDevice) {
				return;
			}
			setup(this.input.channels, this.input.sampleRate);
		}

		@Override
		public int read(final byte[] buffer) {
			if (this.input == null) {
				try {
					this.input = new WavInputStream(new ByteArrayInputStream(makeItWav(this.file)), this.file);
				}
				catch (final IOException ex) {
					throw new GdxRuntimeException("Error reading FLAC file: " + this.file, ex);
				}
				setup(this.input.channels, this.input.sampleRate);
			}
			try {
				return this.input.read(buffer);
			}
			catch (final IOException ex) {
				throw new GdxRuntimeException("Error reading FLAC file: " + this.file, ex);
			}
		}

		@Override
		public void reset() {
			StreamUtils.closeQuietly(this.input);
			this.input = null;
		}
	}

	static public class Sound extends OpenALSound {
		public Sound(final OpenALAudio audio, final FileHandle file) {
			super(audio);
			if (audio.noDevice) {
				return;
			}

			WavInputStream input = null;
			try {
				input = new WavInputStream(new ByteArrayInputStream(makeItWav(file)), file);
				setup(StreamUtils.copyStreamToByteArray(input, input.dataRemaining), input.channels, input.sampleRate);
			}
			catch (final IOException ex) {
				throw new GdxRuntimeException("Error reading FLAC file: " + file, ex);
			}
			finally {
				StreamUtils.closeQuietly(input);
			}
		}
	}

	private static byte[] makeItWav(final FileHandle file) throws IOException {
		// Decode input FLAC file
		StreamInfo streamInfo;
		int[][] samples;
		try (FlacDecoder dec = new FlacDecoder(file.readBytes())) {

			// Handle metadata header blocks
			while (dec.readAndHandleMetadataBlock() != null) {
				;
			}
			streamInfo = dec.streamInfo;
			if ((streamInfo.sampleDepth % 8) != 0) {
				throw new UnsupportedOperationException("Only whole-byte sample depth supported");
			}

			// Decode every block
			samples = new int[streamInfo.numChannels][(int) streamInfo.numSamples];
			for (int off = 0;;) {
				final int len = dec.readAudioBlock(samples, off);
				if (len == 0) {
					break;
				}
				off += len;
			}
		}

		// Check audio MD5 hash
		final byte[] expectHash = streamInfo.md5Hash;
		if (Arrays.equals(expectHash, new byte[16])) {
			System.err.println("Warning: MD5 hash field was blank");
		}
		else if (!Arrays.equals(StreamInfo.getMd5Hash(samples, streamInfo.sampleDepth), expectHash)) {
			throw new DataFormatException("MD5 hash check failed");
			// Else the hash check passed
		}

		// Start writing WAV output file
		final int bytesPerSample = streamInfo.sampleDepth / 8;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(baos))) {
			// Header chunk
			final int sampleDataLen = samples[0].length * streamInfo.numChannels * bytesPerSample;
			out.writeInt(0x52494646); // "RIFF"
			writeLittleInt32(out, sampleDataLen + 36);
			out.writeInt(0x57415645); // "WAVE"

			// Metadata chunk
			out.writeInt(0x666D7420); // "fmt "
			writeLittleInt32(out, 16);
			writeLittleInt16(out, 0x0001);
			writeLittleInt16(out, streamInfo.numChannels);
			writeLittleInt32(out, streamInfo.sampleRate);
			writeLittleInt32(out, streamInfo.sampleRate * streamInfo.numChannels * bytesPerSample);
			writeLittleInt16(out, streamInfo.numChannels * bytesPerSample);
			writeLittleInt16(out, streamInfo.sampleDepth);

			// Audio data chunk ("data")
			out.writeInt(0x64617461); // "data"
			writeLittleInt32(out, sampleDataLen);
			for (int i = 0; i < samples[0].length; i++) {
				for (int j = 0; j < samples.length; j++) {
					final int val = samples[j][i];
					if (bytesPerSample == 1) {
						out.write(val + 128); // Convert to unsigned, as per WAV PCM conventions
					}
					else { // 2 <= bytesPerSample <= 4
						for (int k = 0; k < bytesPerSample; k++) {
							out.write(val >>> (k * 8)); // Little endian
						}
					}
				}
			}
			return baos.toByteArray();
		}
	}

	// Helper members for writing WAV files

	private static void writeLittleInt16(final DataOutputStream out, final int x) throws IOException {
		out.writeShort(Integer.reverseBytes(x) >>> 16);
	}

	private static void writeLittleInt32(final DataOutputStream out, final int x) throws IOException {
		out.writeInt(Integer.reverseBytes(x));
	}

}
