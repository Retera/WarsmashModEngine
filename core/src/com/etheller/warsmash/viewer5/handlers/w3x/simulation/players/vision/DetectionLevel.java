package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision;

public enum DetectionLevel {
	DETECTION_1((byte) 1),
	DETECTION_2((byte) (1 << 2)),
	DETECTION_3((byte) (1 << 3)),
	DETECTION_4((byte) (1 << 4)),
	DETECTION_5((byte) (1 << 5)),
	DETECTION_6((byte) (1 << 6)),
	ALL_LEVELS((byte) ~0);

	private byte detectionMask;

	DetectionLevel(byte i) {
		this.detectionMask = i;
	}

}
