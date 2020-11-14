package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.audio.Sound;

public class AudioBufferSource {
	public Sound buffer;

	public void connect(final AudioPanner panner) {

	}

	public void start(final int value, final float volume, final float pitch) {
		if (this.buffer != null) {
			this.buffer.play(volume, pitch, 0.0f);
		}
	}
}
