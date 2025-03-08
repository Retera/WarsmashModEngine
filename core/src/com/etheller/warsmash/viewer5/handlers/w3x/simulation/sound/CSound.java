package com.etheller.warsmash.viewer5.handlers.w3x.simulation.sound;

public interface CSound {
	void start();

	float getPredictedDuration();

	float getRemainingTimeToPlayOnTheDesyncLocalComputer();
}
