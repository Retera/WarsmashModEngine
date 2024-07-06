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

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_INVALID_VALUE;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcef;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author Nathan Sweet */
public class OpenALAudioDevice implements AudioDevice {
	static private final int bytesPerSample = 2;

	private final OpenALAudio audio;
	private final int channels;
	private IntBuffer buffers;
	private int sourceID = -1;
	private final int format, sampleRate;
	private boolean isPlaying;
	private float volume = 1;
	private float renderedSeconds;

	private final float secondsPerBuffer;
	private byte[] bytes;
	private final int bufferSize;
	private final int bufferCount;
	private final ByteBuffer tempBuffer;

	public OpenALAudioDevice(final OpenALAudio audio, final int sampleRate, final boolean isMono, final int bufferSize,
			final int bufferCount) {
		this.audio = audio;
		this.channels = isMono ? 1 : 2;
		this.bufferSize = bufferSize;
		this.bufferCount = bufferCount;
		this.format = this.channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
		this.sampleRate = sampleRate;
		this.secondsPerBuffer = (float) bufferSize / bytesPerSample / this.channels / sampleRate;
		this.tempBuffer = BufferUtils.createByteBuffer(bufferSize);
	}

	@Override
	public void writeSamples(final short[] samples, final int offset, final int numSamples) {
		if ((this.bytes == null) || (this.bytes.length < (numSamples * 2))) {
			this.bytes = new byte[numSamples * 2];
		}
		final int end = Math.min(offset + numSamples, samples.length);
		for (int i = offset, ii = 0; i < end; i++) {
			final short sample = samples[i];
			this.bytes[ii++] = (byte) (sample & 0xFF);
			this.bytes[ii++] = (byte) ((sample >> 8) & 0xFF);
		}
		writeSamples(this.bytes, 0, numSamples * 2);
	}

	@Override
	public void writeSamples(final float[] samples, final int offset, final int numSamples) {
		if ((this.bytes == null) || (this.bytes.length < (numSamples * 2))) {
			this.bytes = new byte[numSamples * 2];
		}
		final int end = Math.min(offset + numSamples, samples.length);
		for (int i = offset, ii = 0; i < end; i++) {
			float floatSample = samples[i];
			floatSample = MathUtils.clamp(floatSample, -1f, 1f);
			final int intSample = (int) (floatSample * 32767);
			this.bytes[ii++] = (byte) (intSample & 0xFF);
			this.bytes[ii++] = (byte) ((intSample >> 8) & 0xFF);
		}
		writeSamples(this.bytes, 0, numSamples * 2);
	}

	public void writeSamples(final byte[] data, int offset, int length) {
		if (length < 0) {
			throw new IllegalArgumentException("length cannot be < 0.");
		}

		if (this.sourceID == -1) {
			this.sourceID = this.audio.obtainSource(true);
			if (this.sourceID == -1) {
				return;
			}
			if (this.buffers == null) {
				this.buffers = BufferUtils.createIntBuffer(this.bufferCount);
				alGenBuffers(this.buffers);
				if (alGetError() != AL_NO_ERROR) {
					throw new GdxRuntimeException("Unabe to allocate audio buffers.");
				}
			}
			alSourcei(this.sourceID, AL_LOOPING, AL_FALSE);
			alSourcef(this.sourceID, AL_GAIN, this.volume);
			// Fill initial buffers.
			int queuedBuffers = 0;
			for (int i = 0; i < this.bufferCount; i++) {
				final int bufferID = this.buffers.get(i);
				final int written = Math.min(this.bufferSize, length);
				this.tempBuffer.clear();
				this.tempBuffer.put(data, offset, written).flip();
				alBufferData(bufferID, this.format, this.tempBuffer, this.sampleRate);
				alSourceQueueBuffers(this.sourceID, bufferID);
				length -= written;
				offset += written;
				queuedBuffers++;
			}
			// Queue rest of buffers, empty.
			this.tempBuffer.clear().flip();
			for (int i = queuedBuffers; i < this.bufferCount; i++) {
				final int bufferID = this.buffers.get(i);
				alBufferData(bufferID, this.format, this.tempBuffer, this.sampleRate);
				alSourceQueueBuffers(this.sourceID, bufferID);
			}
			alSourcePlay(this.sourceID);
			this.isPlaying = true;
		}

		while (length > 0) {
			final int written = fillBuffer(data, offset, length);
			length -= written;
			offset += written;
		}
	}

	/** Blocks until some of the data could be buffered. */
	private int fillBuffer(final byte[] data, final int offset, final int length) {
		final int written = Math.min(this.bufferSize, length);

		outer: while (true) {
			int buffers = alGetSourcei(this.sourceID, AL_BUFFERS_PROCESSED);
			while (buffers-- > 0) {
				final int bufferID = alSourceUnqueueBuffers(this.sourceID);
				if (bufferID == AL_INVALID_VALUE) {
					break;
				}
				this.renderedSeconds += this.secondsPerBuffer;

				this.tempBuffer.clear();
				this.tempBuffer.put(data, offset, written).flip();
				alBufferData(bufferID, this.format, this.tempBuffer, this.sampleRate);

				alSourceQueueBuffers(this.sourceID, bufferID);
				break outer;
			}
			// Wait for buffer to be free.
			try {
				Thread.sleep((long) (1000 * this.secondsPerBuffer));
			}
			catch (final InterruptedException ignored) {
			}
		}

		// A buffer underflow will cause the source to stop.
		if (!this.isPlaying || (alGetSourcei(this.sourceID, AL_SOURCE_STATE) != AL_PLAYING)) {
			alSourcePlay(this.sourceID);
			this.isPlaying = true;
		}

		return written;
	}

	public void stop() {
		if (this.sourceID == -1) {
			return;
		}
		this.audio.freeSource(this.sourceID);
		this.sourceID = -1;
		this.renderedSeconds = 0;
		this.isPlaying = false;
	}

	public boolean isPlaying() {
		if (this.sourceID == -1) {
			return false;
		}
		return this.isPlaying;
	}

	@Override
	public void setVolume(final float volume) {
		this.volume = volume;
		if (this.sourceID != -1) {
			alSourcef(this.sourceID, AL_GAIN, volume);
		}
	}

	public float getPosition() {
		if (this.sourceID == -1) {
			return 0;
		}
		return this.renderedSeconds + alGetSourcef(this.sourceID, AL11.AL_SEC_OFFSET);
	}

	public void setPosition(final float position) {
		this.renderedSeconds = position;
	}

	public int getChannels() {
		return this.format == AL_FORMAT_STEREO16 ? 2 : 1;
	}

	public int getRate() {
		return this.sampleRate;
	}

	@Override
	public void dispose() {
		if (this.buffers == null) {
			return;
		}
		if (this.sourceID != -1) {
			this.audio.freeSource(this.sourceID);
			this.sourceID = -1;
		}
		alDeleteBuffers(this.buffers);
		this.buffers = null;
	}

	@Override
	public boolean isMono() {
		return this.channels == 1;
	}

	@Override
	public int getLatency() {
		return (int) (this.secondsPerBuffer * this.bufferCount * 1000);
	}

	@Override
	public void pause() {
		// A buffer underflow will cause the source to stop.
	}

	@Override
	public void resume() {
		// Automatically resumes when samples are written
	}
}
