package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class ABTerrainBowlEffect extends ABTerrainEffect {

	private int[] rect;
	private int rectW;
	private int rectH;
	private float cx;
	private float cy;

	private float[] inverseTotalModBuffer;

	private boolean permanent;

	private float depth;

	private float terrainRadius;
	private float terrainRadiusSq;

	private float sinkTime;
	private float fillTime;
	private float startDepth;

	public ABTerrainBowlEffect(CSimulation game, AbilityPointTarget loc, float radius, float depth, float sinkTime,
			float staticTime, float fillTime, float stopDuration, float startDepth) {
		super(sinkTime + staticTime + fillTime, 1);
		this.depth = depth;
		this.sinkTime = sinkTime / (sinkTime + staticTime + fillTime);
		this.fillTime = (sinkTime + staticTime) / (sinkTime + staticTime + fillTime);
		this.startDepth = startDepth;

		this.stopDuration = stopDuration;

		this.terrainRadius = radius / Terrain.CELL_SIZE;
		this.terrainRadiusSq = this.terrainRadius * this.terrainRadius;

		this.rect = game.getTerrainModBufferSize(loc.x, loc.y, radius);
		this.rectW = (this.rect[2] - this.rect[0] + 1);
		this.rectH = (this.rect[3] - this.rect[1] + 1);
		this.cx = game.getTerrainSpaceX(loc.x) - rect[0];
		this.cy = game.getTerrainSpaceY(loc.y) - rect[1];
		this.inverseTotalModBuffer = new float[this.rectW * this.rectH];
	}

	@Override
	protected void onTick(CSimulation game, float timeRatio, float stopRatio) {
		if (this.stopping || timeRatio <= this.sinkTime || timeRatio >= this.fillTime) {
			boolean changed = false;
			float[] terrainModBuffer = new float[this.inverseTotalModBuffer.length];
			for (int x = 0; x < this.rectW; x++) {
				for (int y = 0; y < this.rectH; y++) {
					float dz = 0;
					float dsq = (x - cx) * (x - cx) + (y - cy) * (y - cy);
					if (dsq <= this.terrainRadiusSq) {
						float d = Math.min(Math.max((float) Math.sqrt(dsq) / this.terrainRadius, 0.0f), 1.0f);
						float timeFraction = 1;
						if (this.sinkTime <= timeRatio) {
							if (timeRatio > this.fillTime) {
								timeFraction = 1 - ((timeRatio - this.fillTime) / (1 - this.fillTime));
							}
						} else {
							timeFraction = timeRatio / this.sinkTime;
						}
						dz = (float) ((this.startDepth + timeFraction * this.depth) * Math.cos(d * 0.5 * Math.PI));
						if (this.stopping) {
							dz *= stopRatio;
						}
						dz += this.inverseTotalModBuffer[x + y * this.rectW];
					}

					if (dz != 0) {
						terrainModBuffer[x + y * this.rectW] = dz;
						changed = true;
					}
				}
			}

			if (changed) {
				game.adjustTerrain(rect, terrainModBuffer);
				for (int i = 0; i < terrainModBuffer.length; i++) {
					this.inverseTotalModBuffer[i] -= terrainModBuffer[i];
				}
			}
		}
	}

	@Override
	protected void onRemove(CSimulation game) {
		if (!permanent) {
			game.adjustTerrain(rect, inverseTotalModBuffer);
		}
	}

}
