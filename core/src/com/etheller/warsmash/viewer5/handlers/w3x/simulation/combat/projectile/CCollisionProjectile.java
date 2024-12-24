package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile;

import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntMap;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public class CCollisionProjectile extends CProjectile {

	private static final Rectangle recycleRect = new Rectangle();

	private final CAbilityCollisionProjectileListener projectileListener;

	private int maxHits = 0;
	private int maxHitsPerTarget = 0;

	private int collisionInterval = 1;

	private int nextCollisionTick = 0;

	private float collisionRadius = 0;

	private float finalCollisionRadius = 0;
	private float startingCollisionRadius = 0;
	private float distanceToTarget = 0;

	private IntMap<Integer> collisions = new IntMap<>();
	private int hits = 0;

	private boolean provideCounts = false;

	public CCollisionProjectile(final float x, final float y, final float speed, final AbilityTarget target,
			boolean homingEnabled, final CUnit source, final int maxHits, final int hitsPerTarget,
			final float startingRadius, final float finalRadius, final float collisionInterval,
			final CAbilityCollisionProjectileListener projectileListener, boolean provideCounts) {
		super(x, y, speed, target, homingEnabled, source);
		this.projectileListener = projectileListener;

		this.maxHits = maxHits;
		if (this.maxHits <= 0) {
			this.maxHits = 0;
			this.hits = -1;
		}
		this.maxHitsPerTarget = hitsPerTarget;
		this.startingCollisionRadius = startingRadius;
		this.finalCollisionRadius = finalRadius;
		
		this.provideCounts = provideCounts;

		final float dtsx = getTargetX() - this.x;
		final float dtsy = getTargetY() - this.y;
		this.distanceToTarget = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		this.collisionInterval = (int) (collisionInterval / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	protected void onHitTarget(CSimulation game) {
		// Not used
	}

	protected boolean canHitTarget(CSimulation game, CWidget target) {
		return this.projectileListener.canHitTarget(game, target);
	}

	protected void onHitTarget(CSimulation game, CWidget target) {
		projectileListener.onHit(game, this, target);
	}

	@Override
	public boolean update(final CSimulation game) {
		if (this.nextCollisionTick == 0) {
			this.nextCollisionTick = game.getGameTurnTick();
		}

		final float dtsx = getTargetX() - this.x;
		final float dtsy = getTargetY() - this.y;
		final float c = (float) Math.sqrt((dtsx * dtsx) + (dtsy * dtsy));

		final float d1x = dtsx / c;
		final float d1y = dtsy / c;

		float travelDistance = Math.min(c, this.getSpeed() * WarsmashConstants.SIMULATION_STEP_TIME);
		this.done = c <= travelDistance;
		if (this.done) {
			travelDistance = c;
		}

		final float dx = d1x * travelDistance;
		final float dy = d1y * travelDistance;

		this.x = this.x + dx;
		this.y = this.y + dy;

		if (game.getGameTurnTick() >= this.nextCollisionTick) {
			if (this.collisionRadius != this.finalCollisionRadius) {
				this.collisionRadius = this.startingCollisionRadius
						+ (this.finalCollisionRadius - this.startingCollisionRadius)
								* (1 - (c / this.distanceToTarget));
			}
			AbilityPointTarget loc = new AbilityPointTarget(this.x, this.y);
			this.projectileListener.setCurrentLocation(loc);
			recycleRect.set(this.getX() - collisionRadius, this.getY() - collisionRadius, collisionRadius * 2,
					collisionRadius * 2);
			
			if (provideCounts ) {
				AtomicInteger destCount = new AtomicInteger(0);
				AtomicInteger unitCount = new AtomicInteger(0);
				game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
					@Override
					public boolean call(CDestructable enumDestructable) {
						if (hits < maxHits && collisions.get(enumDestructable.getHandleId(), 0) < maxHitsPerTarget
								&& enumDestructable.distance(loc.getX(), loc.getY()) < collisionRadius && canHitTarget(game, enumDestructable)) {
							destCount.incrementAndGet();
						}
						return false;
					}
				});
				game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
					@Override
					public boolean call(final CUnit enumUnit) {
						if (hits < maxHits && collisions.get(enumUnit.getHandleId(), 0) < maxHitsPerTarget
								&& enumUnit.canReach(loc, collisionRadius) && canHitTarget(game, enumUnit)) {
							unitCount.incrementAndGet();
						}
						return false;
					}
				});
				
				this.projectileListener.setUnitTargets(unitCount.get());
				this.projectileListener.setDestructableTargets(destCount.get());
			}
			
			this.projectileListener.onPreHits(game, this, loc);
			
			
			game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
				@Override
				public boolean call(CDestructable enumDestructable) {
					if (hits < maxHits && collisions.get(enumDestructable.getHandleId(), 0) < maxHitsPerTarget
							&& enumDestructable.distance(loc.getX(), loc.getY()) < collisionRadius && canHitTarget(game, enumDestructable)) {
						onHitTarget(game, enumDestructable);
						if (maxHits > 0) {
							hits++;
						}
						collisions.put(enumDestructable.getHandleId(), collisions.get(enumDestructable.getHandleId(), 0) + 1);
					}
					return false;
				}
			});
			game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit enumUnit) {
					if (hits < maxHits && collisions.get(enumUnit.getHandleId(), 0) < maxHitsPerTarget
							&& enumUnit.canReach(loc, collisionRadius) && canHitTarget(game, enumUnit)) {
						onHitTarget(game, enumUnit);
						if (maxHits > 0) {
							hits++;
						}
						collisions.put(enumUnit.getHandleId(), collisions.get(enumUnit.getHandleId(), 0) + 1);
					}
					return false;
				}
			});
			this.nextCollisionTick = game.getGameTurnTick() + this.collisionInterval;
		}
		this.done |= hits >= maxHits;

		return this.done;
	}
}
