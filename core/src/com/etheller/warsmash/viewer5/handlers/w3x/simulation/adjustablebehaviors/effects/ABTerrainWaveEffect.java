package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.effects;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.Terrain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;

public class ABTerrainWaveEffect extends ABTerrainEffect {

	private AbilityPointTarget targetLoc;

	private float trailStart;
	private float leadEnd;

	private int[] rect;
	private int rectW;
	private int rectH;
	private float distX;
	private float distY;
	private float slope;

	private float[] inverseTotalModBuffer;

	private boolean permanent;

	private float depth;

	private float terrainRadius;
	private float terrainRadiusSq;

	private float terrainStartX;
	private float terrainStartY;
	private float terrainTargetX;

	public ABTerrainWaveEffect(CSimulation game, AbilityPointTarget start, AbilityPointTarget target, float radius,
			float depth, float distance, float speed, int trailTime, int count) {
		super((distance / speed) * 1000 + trailTime, count);

		Vector2 v = new Vector2(target.x - start.x, target.y - start.y);
		v = v.nor();
		this.targetLoc = new AbilityPointTarget(start.x + v.x * distance, start.y + v.y * distance);
		this.depth = depth;

		float travelTime = (distance / speed) * 1000;
		this.trailStart = trailTime / (travelTime + trailTime);
		this.leadEnd = travelTime / (travelTime + trailTime);

		this.terrainRadius = radius / Terrain.CELL_SIZE;
		this.terrainRadiusSq = this.terrainRadius * this.terrainRadius;

		this.rect = game.getTerrainModBufferSize(Math.min(start.x, targetLoc.x) - radius,
				Math.min(start.y, targetLoc.y) - radius,
				Math.max(start.x, targetLoc.x) - Math.min(start.x, targetLoc.x) + radius,
				Math.max(start.y, targetLoc.y) - Math.min(start.y, targetLoc.y) + radius);
		this.rectW = (this.rect[2] - this.rect[0] + 1);
		this.rectH = (this.rect[3] - this.rect[1] + 1);

		this.terrainStartX = game.getTerrainSpaceX(start.x) - this.rect[0];
		this.terrainStartY = game.getTerrainSpaceY(start.y) - this.rect[1];
		this.terrainTargetX = game.getTerrainSpaceX(targetLoc.x) - this.rect[0];
		float terrainTargetY = game.getTerrainSpaceY(targetLoc.y) - this.rect[1];
		this.distX = terrainTargetX - terrainStartX;
		this.distY = terrainTargetY - terrainStartY;
		this.slope = this.distY / this.distX;

		this.inverseTotalModBuffer = new float[this.rectW * this.rectH];
	}

	@Override
	protected void onTick(CSimulation game, float timeRatio, float stopRatio) {
		boolean changed = false;
		float[] terrainModBuffer = new float[this.inverseTotalModBuffer.length];

		final float leadX = timeRatio <= this.leadEnd ? terrainStartX + ((timeRatio / this.leadEnd) * this.distX)
				: terrainTargetX;
		final float tailX = this.trailStart <= timeRatio
				? terrainStartX + (((timeRatio - this.trailStart) / (1 - this.trailStart)) * this.distX)
				: terrainStartX;
		final float projX = this.terrainStartX + this.distX * timeRatio;
		final float dpx;
		final float dpy;
		if ((projX <= leadX || terrainTargetX <= terrainStartX)
				&& (leadX <= projX || terrainStartX <= terrainTargetX)) {
			if ((tailX <= projX || terrainTargetX <= terrainStartX)
					&& (projX <= tailX || terrainStartX <= terrainTargetX)) {
				dpx = projX;
				dpy = this.terrainStartY + this.distY * timeRatio;
			} else {
				dpx = tailX;
				dpy = (terrainStartY + ((tailX - terrainStartX) * slope));
			}
		} else {
			dpx = leadX;
			dpy = (terrainStartY + ((leadX - terrainStartX) * slope));
		}

		for (int x = 0; x < this.rectW; x++) {
			for (int y = 0; y < this.rectH; y++) {
				float dz = 0;
				float dsq = (x - dpx) * (x - dpx) + (y - dpy) * (y - dpy);
				if (dsq <= this.terrainRadiusSq) {
					float d = Math.min(Math.max((float) Math.sqrt(dsq) / this.terrainRadius, 0.0f), 1.0f);
					dz = (float) (-0.5 * this.depth * (Math.cos(d * Math.PI) + 1));
					if (timeRatio > 0.89999998) {
						dz *= (1.0 - ((timeRatio - 0.89999998) / 0.10000002));
					}
					if (this.stopping) {
						dz *= stopRatio;
					}
					dz += this.inverseTotalModBuffer[x + y * this.rectW];
				} else {
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
