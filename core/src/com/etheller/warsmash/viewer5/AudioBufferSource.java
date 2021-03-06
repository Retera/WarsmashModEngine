package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.audio.Sound;
import com.etheller.warsmash.viewer5.gl.Extensions;

public class AudioBufferSource {
	public Sound buffer;
	private AudioPanner panner;

	public void connect(final AudioPanner panner) {
		this.panner = panner;
	}

	public void start(final int value, final float volume, final float pitch, final boolean looping) {
		if (this.buffer != null) {
			if (!this.panner.listener.is3DSupported() || this.panner.isWithinListenerDistance()) {
				Extensions.audio.play(this.buffer, volume, pitch, this.panner.x, this.panner.y, this.panner.z,
						this.panner.listener.is3DSupported(), this.panner.maxDistance, this.panner.refDistance,
						looping);
			}
		}
	}
}
