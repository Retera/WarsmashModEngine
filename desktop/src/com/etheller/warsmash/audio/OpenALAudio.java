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

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_PAUSED;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_STOPPED;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alListener;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;

/** @author Nathan Sweet */
public class OpenALAudio implements Audio {
	private final int deviceBufferSize;
	private final int deviceBufferCount;
	private IntArray idleSources, allSources;
	private LongMap<Integer> soundIdToSource;
	private IntMap<Long> sourceToSoundId;
	private long nextSoundId = 0;
	private final ObjectMap<String, Class<? extends OpenALSound>> extensionToSoundClass = new ObjectMap();
	private final ObjectMap<String, Class<? extends OpenALMusic>> extensionToMusicClass = new ObjectMap();
	private OpenALSound[] recentSounds;
	private int mostRecetSound = -1;

	Array<OpenALMusic> music = new Array(false, 1, OpenALMusic.class);
	boolean noDevice = false;

	public OpenALAudio() {
		this(16, 9, 512);
	}

	public OpenALAudio(final int simultaneousSources, final int deviceBufferCount, final int deviceBufferSize) {
		this.deviceBufferSize = deviceBufferSize;
		this.deviceBufferCount = deviceBufferCount;

		registerSound("ogg", Ogg.Sound.class);
		registerMusic("ogg", Ogg.Music.class);
		registerSound("wav", Wav.Sound.class);
		registerMusic("wav", Wav.Music.class);
		registerSound("mp3", Mp3.Sound.class);
		registerMusic("mp3", Mp3.Music.class);
		registerSound("flac", Flac.Sound.class);
		registerMusic("flac", Flac.Music.class);

		try {
			AL.create();
		}
		catch (final LWJGLException ex) {
			this.noDevice = true;
			ex.printStackTrace();
			return;
		}

		this.allSources = new IntArray(false, simultaneousSources);
		for (int i = 0; i < simultaneousSources; i++) {
			final int sourceID = alGenSources();
			if (alGetError() != AL_NO_ERROR) {
				break;
			}
			this.allSources.add(sourceID);
		}
		this.idleSources = new IntArray(this.allSources);
		this.soundIdToSource = new LongMap<Integer>();
		this.sourceToSoundId = new IntMap<Long>();

		final FloatBuffer orientation = BufferUtils.createFloatBuffer(6)
				.put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f }).flip();
		alListener(AL_ORIENTATION, orientation);
		final FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).flip();
		alListener(AL_VELOCITY, velocity);
		final FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).flip();
		alListener(AL_POSITION, position);

		this.recentSounds = new OpenALSound[simultaneousSources];
	}

	public void registerSound(final String extension, final Class<? extends OpenALSound> soundClass) {
		if (extension == null) {
			throw new IllegalArgumentException("extension cannot be null.");
		}
		if (soundClass == null) {
			throw new IllegalArgumentException("soundClass cannot be null.");
		}
		this.extensionToSoundClass.put(extension, soundClass);
	}

	public void registerMusic(final String extension, final Class<? extends OpenALMusic> musicClass) {
		if (extension == null) {
			throw new IllegalArgumentException("extension cannot be null.");
		}
		if (musicClass == null) {
			throw new IllegalArgumentException("musicClass cannot be null.");
		}
		this.extensionToMusicClass.put(extension, musicClass);
	}

	@Override
	public OpenALSound newSound(final FileHandle file) {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		final Class<? extends OpenALSound> soundClass = this.extensionToSoundClass.get(file.extension().toLowerCase());
		if (soundClass == null) {
			throw new GdxRuntimeException("Unknown file extension for sound: " + file);
		}
		try {
			return soundClass.getConstructor(new Class[] { OpenALAudio.class, FileHandle.class }).newInstance(this,
					file);
		}
		catch (final Exception ex) {
			throw new GdxRuntimeException("Error creating sound " + soundClass.getName() + " for file: " + file, ex);
		}
	}

	@Override
	public OpenALMusic newMusic(final FileHandle file) {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null.");
		}
		final Class<? extends OpenALMusic> musicClass = this.extensionToMusicClass.get(file.extension().toLowerCase());
		if (musicClass == null) {
			throw new GdxRuntimeException("Unknown file extension for music: " + file);
		}
		try {
			return musicClass.getConstructor(new Class[] { OpenALAudio.class, FileHandle.class }).newInstance(this,
					file);
		}
		catch (final Exception ex) {
			throw new GdxRuntimeException("Error creating music " + musicClass.getName() + " for file: " + file, ex);
		}
	}

	int obtainSource(final boolean isMusic) {
		if (this.noDevice) {
			return 0;
		}
		for (int i = 0, n = this.idleSources.size; i < n; i++) {
			final int sourceId = this.idleSources.get(i);
			final int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
			if ((state != AL_PLAYING) && (state != AL_PAUSED)) {
				if (isMusic) {
					this.idleSources.removeIndex(i);
				}
				else {
					if (this.sourceToSoundId.containsKey(sourceId)) {
						final long soundId = this.sourceToSoundId.get(sourceId);
						this.sourceToSoundId.remove(sourceId);
						this.soundIdToSource.remove(soundId);
					}

					final long soundId = this.nextSoundId++;
					this.sourceToSoundId.put(sourceId, soundId);
					this.soundIdToSource.put(soundId, sourceId);
				}
				alSourceStop(sourceId);
				alSourcei(sourceId, AL_BUFFER, 0);
				AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
				AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
				AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 1f);
				return sourceId;
			}
		}
		return -1;
	}

	void freeSource(final int sourceID) {
		if (this.noDevice) {
			return;
		}
		alSourceStop(sourceID);
		alSourcei(sourceID, AL_BUFFER, 0);
		if (this.sourceToSoundId.containsKey(sourceID)) {
			final long soundId = this.sourceToSoundId.remove(sourceID);
			this.soundIdToSource.remove(soundId);
		}
		this.idleSources.add(sourceID);
	}

	void freeBuffer(final int bufferID) {
		if (this.noDevice) {
			return;
		}
		for (int i = 0, n = this.idleSources.size; i < n; i++) {
			final int sourceID = this.idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				if (this.sourceToSoundId.containsKey(sourceID)) {
					final long soundId = this.sourceToSoundId.remove(sourceID);
					this.soundIdToSource.remove(soundId);
				}
				alSourceStop(sourceID);
				alSourcei(sourceID, AL_BUFFER, 0);
			}
		}
	}

	void stopSourcesWithBuffer(final int bufferID) {
		if (this.noDevice) {
			return;
		}
		for (int i = 0, n = this.idleSources.size; i < n; i++) {
			final int sourceID = this.idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				if (this.sourceToSoundId.containsKey(sourceID)) {
					final long soundId = this.sourceToSoundId.remove(sourceID);
					this.soundIdToSource.remove(soundId);
				}
				alSourceStop(sourceID);
			}
		}
	}

	void pauseSourcesWithBuffer(final int bufferID) {
		if (this.noDevice) {
			return;
		}
		for (int i = 0, n = this.idleSources.size; i < n; i++) {
			final int sourceID = this.idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				alSourcePause(sourceID);
			}
		}
	}

	void resumeSourcesWithBuffer(final int bufferID) {
		if (this.noDevice) {
			return;
		}
		for (int i = 0, n = this.idleSources.size; i < n; i++) {
			final int sourceID = this.idleSources.get(i);
			if (alGetSourcei(sourceID, AL_BUFFER) == bufferID) {
				if (alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PAUSED) {
					alSourcePlay(sourceID);
				}
			}
		}
	}

	public void update() {
		if (this.noDevice) {
			return;
		}
		for (int i = 0; i < this.music.size; i++) {
			this.music.items[i].update();
		}
	}

	public long getSoundId(final int sourceId) {
		if (!this.sourceToSoundId.containsKey(sourceId)) {
			return -1;
		}
		return this.sourceToSoundId.get(sourceId);
	}

	public void stopSound(final long soundId) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		alSourceStop(sourceId);
	}

	public void pauseSound(final long soundId) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		alSourcePause(sourceId);
	}

	public void resumeSound(final long soundId) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		if (alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PAUSED) {
			alSourcePlay(sourceId);
		}
	}

	public void setSoundGain(final long soundId, final float volume) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setSoundLooping(final long soundId, final boolean looping) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		alSourcei(sourceId, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setSoundPitch(final long soundId, final float pitch) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setSoundPan(final long soundId, final float pan, final float volume) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);

		AL10.alSource3f(sourceId, AL10.AL_POSITION, MathUtils.cos(((pan - 1) * MathUtils.PI) / 2), 0,
				MathUtils.sin(((pan + 1) * MathUtils.PI) / 2));
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setSoundPosition(final long soundId, final float x, final float y, final float z,
			final boolean is3DSound, final float maxDistance, final float refDistance) {
		if (!this.soundIdToSource.containsKey(soundId)) {
			return;
		}
		final int sourceId = this.soundIdToSource.get(soundId);

		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, refDistance);
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 1.0f);
		AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, is3DSound ? AL10.AL_FALSE : AL10.AL_TRUE);
	}

	public void dispose() {
		if (this.noDevice) {
			return;
		}
		for (int i = 0, n = this.allSources.size; i < n; i++) {
			final int sourceID = this.allSources.get(i);
			final int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
			if (state != AL_STOPPED) {
				alSourceStop(sourceID);
			}
			alDeleteSources(sourceID);
		}

		this.sourceToSoundId.clear();
		this.soundIdToSource.clear();

		AL.destroy();
		while (AL.isCreated()) {
			try {
				Thread.sleep(10);
			}
			catch (final InterruptedException e) {
			}
		}
	}

	@Override
	public boolean switchOutputDevice(final String deviceIdentifier) {
		return true;
	}

	@Override
	public String[] getAvailableOutputDevices() {
		return new String[0];
	}

	@Override
	public AudioDevice newAudioDevice(final int sampleRate, final boolean isMono) {
		if (this.noDevice) {
			return new AudioDevice() {
				@Override
				public void writeSamples(final float[] samples, final int offset, final int numSamples) {
				}

				@Override
				public void writeSamples(final short[] samples, final int offset, final int numSamples) {
				}

				@Override
				public void setVolume(final float volume) {
				}

				@Override
				public boolean isMono() {
					return isMono;
				}

				@Override
				public int getLatency() {
					return 0;
				}

				@Override
				public void dispose() {
				}

				@Override
				public void pause() {
				}

				@Override
				public void resume() {
				}
			};
		}
		return new OpenALAudioDevice(this, sampleRate, isMono, this.deviceBufferSize, this.deviceBufferCount);
	}

	@Override
	public AudioRecorder newAudioRecorder(final int samplingRate, final boolean isMono) {
		if (this.noDevice) {
			return new AudioRecorder() {
				@Override
				public void read(final short[] samples, final int offset, final int numSamples) {
				}

				@Override
				public void dispose() {
				}
			};
		}
		return new JavaSoundAudioRecorder(samplingRate, isMono);
	}

	/**
	 * Retains a list of the most recently played sounds and stops the sound played
	 * least recently if necessary for a new sound to play
	 */
	protected void retain(final OpenALSound sound, final boolean stop) {
		// Move the pointer ahead and wrap
		this.mostRecetSound++;
		this.mostRecetSound %= this.recentSounds.length;

		if (stop) {
			// Stop the least recent sound (the one we are about to bump off the buffer)
			if (this.recentSounds[this.mostRecetSound] != null) {
				this.recentSounds[this.mostRecetSound].stop();
			}
		}

		this.recentSounds[this.mostRecetSound] = sound;
	}

	/** Removes the disposed sound from the least recently played list */
	public void forget(final OpenALSound sound) {
		for (int i = 0; i < this.recentSounds.length; i++) {
			if (this.recentSounds[i] == sound) {
				this.recentSounds[i] = null;
			}
		}
	}
}
