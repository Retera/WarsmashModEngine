package com.etheller.warsmash.util;

public class WarsmashConstants {
	public static final int MAX_PLAYERS = 16;
	public static final int REPLACEABLE_TEXTURE_LIMIT = 64;
	public static final float SIMULATION_STEP_TIME = 1 / 20f;
	public static final int PORT_NUMBER = 6115;
	public static final float BUILDING_CONSTRUCT_START_LIFE = 0.1f;
	public static final int BUILD_QUEUE_SIZE = 7;
	// It looks like in Patch 1.22, "Particle" in video settings will change this
	// factor:
	// Low - unknown ?
	// Medium - 1.0f
	// High - 1.5f
	public static final float MODEL_DETAIL_PARTICLE_FACTOR = 1.5f;
	public static final float MODEL_DETAIL_PARTICLE_FACTOR_INVERSE = 1f / MODEL_DETAIL_PARTICLE_FACTOR;
}
