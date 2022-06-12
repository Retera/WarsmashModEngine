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
import com.badlogic.gdx.utils.StreamUtils;

/** @author Nathan Sweet */
public class Ogg {
	public static class Music extends OpenALMusic {
		private OggInputStream input;
		private OggInputStream previousInput;

		public Music(final OpenALAudio audio, final FileHandle file) {
			super(audio, file);
			if (audio.noDevice) {
				return;
			}
			this.input = new OggInputStream(file.read());
			setup(this.input.getChannels(), this.input.getSampleRate());
		}

		@Override
		public int read(final byte[] buffer) {
			if (this.input == null) {
				this.input = new OggInputStream(this.file.read(), this.previousInput);
				setup(this.input.getChannels(), this.input.getSampleRate());
				this.previousInput = null; // release this reference
			}
			return this.input.read(buffer);
		}

		@Override
		public void reset() {
			StreamUtils.closeQuietly(this.input);
			this.previousInput = null;
			this.input = null;
		}

		@Override
		protected void loop() {
			StreamUtils.closeQuietly(this.input);
			this.previousInput = this.input;
			this.input = null;
		}
	}

	public static class Sound extends OpenALSound {
		public Sound(final OpenALAudio audio, final FileHandle file) {
			super(audio);
			if (audio.noDevice) {
				return;
			}
			OggInputStream input = null;
			try {
				input = new OggInputStream(file.read());
				final ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
				final byte[] buffer = new byte[2048];
				while (!input.atEnd()) {
					final int length = input.read(buffer);
					if (length == -1) {
						break;
					}
					output.write(buffer, 0, length);
				}
				setup(output.toByteArray(), input.getChannels(), input.getSampleRate());
			}
			finally {
				StreamUtils.closeQuietly(input);
			}
		}
	}
}
