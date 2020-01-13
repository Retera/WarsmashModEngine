package com.etheller.warsmash.viewer5;

public abstract class AudioPanner {
	public abstract void setPosition(float x, float y, float z);

	public float maxDistance;
	public float refDistance;

	public abstract void connect(AudioDestination destination);
}
