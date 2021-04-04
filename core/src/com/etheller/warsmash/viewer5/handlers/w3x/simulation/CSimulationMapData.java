package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

/**
 * Provides limited access to game map data when necessary for the game
 * simulation logic. Do not add methods to this to query anything that isn't
 * going to be network sync'ed.
 */
public interface CSimulationMapData {
	short getTerrainPathing(float x, float y);
}
