package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class ABTerrainRippleEffect extends ABTerrainEffect {

	private float startRadius;
	private float radius;
	
	private int[] rect;
	private int rectW;
	private int rectH;
	private float cx;
	private float cy;

	private float[] inverseTotalModBuffer;

	private boolean permanent;

	private float depth;
	private float spaceWaves;
	private float timeWaves;
	private boolean onlyNegative;
	private boolean radChanges;
	
	private float terrainRadius;
	private float terrainRadiusSq;

	public ABTerrainRippleEffect(CSimulation game, AbilityPointTarget loc, float startRadius, float endRadius,
			float depth, float period, int totalPeriodCount, int spaceWaves, int timeWaves, boolean onlyNegative) {
		super(period, totalPeriodCount);
		this.radius = endRadius;
		this.depth = depth;
		this.spaceWaves = (float) (spaceWaves * Math.PI);
		this.timeWaves = (float) (timeWaves * Math.PI);
		this.startRadius = startRadius;
		this.onlyNegative = onlyNegative;
		
		this.radChanges = this.startRadius != this.radius;
		this.terrainRadius = startRadius / Terrain.CELL_SIZE;
		this.terrainRadiusSq = this.terrainRadius * this.terrainRadius;

		this.rect = game.getTerrainModBufferSize(loc.x, loc.y, Math.max(startRadius, endRadius));
		this.rectW = (this.rect[2] - this.rect[0] + 1);
		this.rectH = (this.rect[3] - this.rect[1] + 1);
		this.cx = game.getTerrainSpaceX(loc.x) - rect[0];
		this.cy = game.getTerrainSpaceY(loc.y) - rect[1];
		this.inverseTotalModBuffer = new float[this.rectW * this.rectH];
	}

	@Override
	protected void onTick(CSimulation game, float timeRatio, float stopRatio) {
		float timeFactorRepetitions = (this.count + timeRatio) / this.totalCount;
		if (this.radChanges) {
			float currentRadiusSq = (this.startRadius + (this.radius - this.startRadius) * timeFactorRepetitions);
			currentRadiusSq /= Terrain.CELL_SIZE;
			this.terrainRadius = currentRadiusSq;
			this.terrainRadiusSq = currentRadiusSq * currentRadiusSq;
		}

		boolean changed = false;
		float[] terrainModBuffer = new float[this.inverseTotalModBuffer.length];
		
		for (int x = 0; x < this.rectW; x++) {
			for (int y = 0; y < this.rectH; y++) {
				float dz = 0;
				float dsq = (x - cx) * (x - cx) + (y - cy) * (y - cy);
				if (dsq <= this.terrainRadiusSq) {
					float d = Math.min(Math.max((float) Math.sqrt(dsq) / this.terrainRadius, 0.0f), 1.0f);
					dz = (float) ((this.depth * (Math.sin(d * this.spaceWaves + this.timeWaves * timeRatio))) * (1 - timeFactorRepetitions));
					if (this.stopping) {
						dz *= stopRatio;
					}
					dz += this.inverseTotalModBuffer[x + y * this.rectW];
					if (this.onlyNegative && (dz - this.inverseTotalModBuffer[x + y * this.rectW]) > 0) {
						dz = this.inverseTotalModBuffer[x + y * this.rectW];
					}
				} else if (this.inverseTotalModBuffer[x + y * this.rectW] != 0) {
					dz = this.inverseTotalModBuffer[x + y * this.rectW];
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

	@Override
	protected void onRemove(CSimulation game) {
		if (!permanent) {
			game.adjustTerrain(rect, inverseTotalModBuffer);
		}
	}

}
