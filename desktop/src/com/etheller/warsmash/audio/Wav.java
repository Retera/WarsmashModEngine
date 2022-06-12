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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

public class Wav {
	public static class Music extends OpenALMusic {
		private WavInputStream input;

		public Music(final OpenALAudio audio, final FileHandle file) {
			super(audio, file);
			this.input = new WavInputStream(file);
			if (audio.noDevice) {
				return;
			}
			setup(this.input.channels, this.input.sampleRate);
		}

		@Override
		public int read(final byte[] buffer) {
			if (this.input == null) {
				this.input = new WavInputStream(this.file);
				setup(this.input.channels, this.input.sampleRate);
			}
			try {
				return this.input.read(buffer);
			}
			catch (final IOException ex) {
				throw new GdxRuntimeException("Error reading WAV file: " + this.file, ex);
			}
		}

		@Override
		public void reset() {
			StreamUtils.closeQuietly(this.input);
			this.input = null;
		}
	}

	public static class Sound extends OpenALSound {
		public Sound(final OpenALAudio audio, final FileHandle file) {
			super(audio);
			if (audio.noDevice) {
				return;
			}

			WavInputStream input = null;
			try {
				input = new WavInputStream(file);
				setup(StreamUtils.copyStreamToByteArray(input, input.dataRemaining), input.channels, input.sampleRate);
			}
			catch (final IOException ex) {
				throw new GdxRuntimeException("Error reading WAV file: " + file, ex);
			}
			finally {
				StreamUtils.closeQuietly(input);
			}
		}
	}

	/** @author Nathan Sweet */
	public static class WavInputStream extends FilterInputStream {

		public int channels, sampleRate, dataRemaining;

		public WavInputStream(final FileHandle file) {
			this(file.read(), file);
		}

		public WavInputStream(final InputStream stream, final Object loggableFile) {
			super(stream);
			try {
				if ((read() != 'R') || (read() != 'I') || (read() != 'F') || (read() != 'F')) {
					throw new GdxRuntimeException("RIFF header not found: " + loggableFile);
				}

				skipFully(4);

				if ((read() != 'W') || (read() != 'A') || (read() != 'V') || (read() != 'E')) {
					throw new GdxRuntimeException("Invalid wave file header: " + loggableFile);
				}

				final int fmtChunkLength = seekToChunk('f', 'm', 't', ' ');

				// http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
				// http://soundfile.sapp.org/doc/WaveFormat/
				final int type = (read() & 0xff) | ((read() & 0xff) << 8);
				if (type != 1) {
					String name;
					switch (type) {
					case 0x0002:
						name = "ADPCM";
						break;
					case 0x0003:
						name = "IEEE float";
						break;
					case 0x0006:
						name = "8-bit ITU-T G.711 A-law";
						break;
					case 0x0007:
						name = "8-bit ITU-T G.711 u-law";
						break;
					case 0xFFFE:
						name = "Extensible";
						break;
					default:
						name = "Unknown";
					}
					throw new GdxRuntimeException(
							"WAV files must be PCM, unsupported format: " + name + " (" + type + ")");
				}

				this.channels = (read() & 0xff) | ((read() & 0xff) << 8);
				if ((this.channels != 1) && (this.channels != 2)) {
					throw new GdxRuntimeException("WAV files must have 1 or 2 channels: " + this.channels);
				}

				this.sampleRate = (read() & 0xff) | ((read() & 0xff) << 8) | ((read() & 0xff) << 16)
						| ((read() & 0xff) << 24);

				skipFully(6);

				final int bitsPerSample = (read() & 0xff) | ((read() & 0xff) << 8);
				if (bitsPerSample != 16) {
					throw new GdxRuntimeException("WAV files must have 16 bits per sample: " + bitsPerSample);
				}

				skipFully(fmtChunkLength - 16);

				this.dataRemaining = seekToChunk('d', 'a', 't', 'a');
			}
			catch (final Throwable ex) {
				StreamUtils.closeQuietly(this);
				throw new GdxRuntimeException("Error reading WAV file: " + loggableFile, ex);
			}
		}

		private int seekToChunk(final char c1, final char c2, final char c3, final char c4) throws IOException {
			while (true) {
				boolean found = read() == c1;
				found &= read() == c2;
				found &= read() == c3;
				found &= read() == c4;
				final int chunkLength = (read() & 0xff) | ((read() & 0xff) << 8) | ((read() & 0xff) << 16)
						| ((read() & 0xff) << 24);
				if (chunkLength == -1) {
					throw new IOException("Chunk not found: " + c1 + c2 + c3 + c4);
				}
				if (found) {
					return chunkLength;
				}
				skipFully(chunkLength);
			}
		}

		private void skipFully(int count) throws IOException {
			while (count > 0) {
				final long skipped = this.in.skip(count);
				if (skipped <= 0) {
					throw new EOFException("Unable to skip.");
				}
				count -= skipped;
			}
		}

		@Override
		public int read(final byte[] buffer) throws IOException {
			if (this.dataRemaining == 0) {
				return -1;
			}
			final int length = Math.min(super.read(buffer), this.dataRemaining);
			if (length == -1) {
				return -1;
			}
			this.dataRemaining -= length;
			return length;
		}
	}
}
