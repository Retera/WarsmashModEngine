package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.audio.Sound;

public class AudioBufferSource {
	public Sound buffer;

	public void connect(final AudioPanner panner) {

	}

	public void start(final int value) {
		if (this.buffer != null) {
			this.buffer.play(1);
		}
	}
}
