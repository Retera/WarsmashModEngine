package com.etheller.warsmash.viewer5.gl;

import com.badlogic.gdx.audio.Sound;
import com.etheller.warsmash.viewer5.AudioContext;

public interface AudioExtension {
	AudioContext createContext(boolean world);

	float getDuration(Sound sound);

	void play(Sound buffer, final float volume, final float pitch, final float x, final float y, final float z,
			final boolean is3DSound, float maxDistance, float refDistance, boolean looping);
}
