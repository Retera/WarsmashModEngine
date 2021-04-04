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

import java.io.ByteArrayOutputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.MP3Decoder;
import javazoom.jl.decoder.OutputBuffer;

/** @author Nathan Sweet */
public class Mp3 {
	static public class Music extends OpenALMusic {
		// Note: This uses a slightly modified version of JLayer.

		private Bitstream bitstream;
		private OutputBuffer outputBuffer;
		private MP3Decoder decoder;

		public Music(final OpenALAudio audio, final FileHandle file) {
			super(audio, file);
			if (audio.noDevice) {
				return;
			}
			this.bitstream = new Bitstream(file.read());
			this.decoder = new MP3Decoder();
			this.bufferOverhead = 4096;
			try {
				final Header header = this.bitstream.readFrame();
				if (header == null) {
					throw new GdxRuntimeException("Empty MP3");
				}
				final int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
				this.outputBuffer = new OutputBuffer(channels, false);
				this.decoder.setOutputBuffer(this.outputBuffer);
				setup(channels, header.getSampleRate());
			}
			catch (final BitstreamException e) {
				throw new GdxRuntimeException("error while preloading mp3", e);
			}
		}

		@Override
		public int read(final byte[] buffer) {
			try {
				boolean setup = this.bitstream == null;
				if (setup) {
					this.bitstream = new Bitstream(this.file.read());
					this.decoder = new MP3Decoder();
				}

				int totalLength = 0;
				final int minRequiredLength = buffer.length - (OutputBuffer.BUFFERSIZE * 2);
				while (totalLength <= minRequiredLength) {
					final Header header = this.bitstream.readFrame();
					if (header == null) {
						break;
					}
					if (setup) {
						final int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
						this.outputBuffer = new OutputBuffer(channels, false);
						this.decoder.setOutputBuffer(this.outputBuffer);
						setup(channels, header.getSampleRate());
						setup = false;
					}
					try {
						this.decoder.decodeFrame(header, this.bitstream);
					}
					catch (final Exception ignored) {
						// JLayer's decoder throws ArrayIndexOutOfBoundsException sometimes!?
					}
					this.bitstream.closeFrame();

					final int length = this.outputBuffer.reset();
					System.arraycopy(this.outputBuffer.getBuffer(), 0, buffer, totalLength, length);
					totalLength += length;
				}
				return totalLength;
			}
			catch (final Throwable ex) {
				reset();
				throw new GdxRuntimeException("Error reading audio data.", ex);
			}
		}

		@Override
		public void reset() {
			if (this.bitstream == null) {
				return;
			}
			try {
				this.bitstream.close();
			}
			catch (final BitstreamException ignored) {
			}
			this.bitstream = null;
		}
	}

	static public class Sound extends OpenALSound {
		// Note: This uses a slightly modified version of JLayer.

		public Sound(final OpenALAudio audio, final FileHandle file) {
			super(audio);
			if (audio.noDevice) {
				return;
			}
			final ByteArrayOutputStream output = new ByteArrayOutputStream(4096);

			final Bitstream bitstream = new Bitstream(file.read());
			final MP3Decoder decoder = new MP3Decoder();

			try {
				OutputBuffer outputBuffer = null;
				int sampleRate = -1, channels = -1;
				while (true) {
					final Header header = bitstream.readFrame();
					if (header == null) {
						break;
					}
					if (outputBuffer == null) {
						channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
						outputBuffer = new OutputBuffer(channels, false);
						decoder.setOutputBuffer(outputBuffer);
						sampleRate = header.getSampleRate();
					}
					try {
						decoder.decodeFrame(header, bitstream);
					}
					catch (final Exception ignored) {
						// JLayer's decoder throws ArrayIndexOutOfBoundsException sometimes!?
					}
					bitstream.closeFrame();
					output.write(outputBuffer.getBuffer(), 0, outputBuffer.reset());
				}
				bitstream.close();
				setup(output.toByteArray(), channels, sampleRate);
			}
			catch (final Throwable ex) {
				throw new GdxRuntimeException("Error reading audio data.", ex);
			}
		}
	}
}
