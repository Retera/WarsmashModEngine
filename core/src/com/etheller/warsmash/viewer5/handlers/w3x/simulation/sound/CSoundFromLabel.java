package com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound;

import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;

public class CSoundFromLabel implements CSound {

	private final UnitSound sound;
	private final boolean looping;
	private final boolean is3d;
	private final boolean stopWhenOutOfRange;
	private final int fadeInRate;
	private final int fadeOutRate;
	private final AudioContext audioContext;

	public CSoundFromLabel(final UnitSound sound, final AudioContext audioContext, final boolean looping,
			final boolean is3d, final boolean stopWhenOutOfRange, final int fadeInRate, final int fadeOutRate) {
		this.sound = sound;
		this.audioContext = audioContext;
		this.looping = looping;
		this.is3d = is3d;
		this.stopWhenOutOfRange = stopWhenOutOfRange;
		this.fadeInRate = fadeInRate;
		this.fadeOutRate = fadeOutRate;
	}

	@Override
	public void start() {
		this.sound.play(this.audioContext, 0, 0, 0);
	}

}
