package com.etheller.warsmash.parsers.terrain;

public class Corner {
	boolean mapEdge;

	int groundTexture;
	float height;
	float waterHeight;
	boolean ramp;
	boolean blight;
	boolean water;
	boolean boundary;
	boolean cliff;
	boolean romp;
	int groundVariation;
	int cliffVariation;
	int cliffTexture;
	int layerHeight;

	public float finalGroundHeight() {
		return 0;
	}

	public float finalWaterHeight() {
		return 0;
	}
}
