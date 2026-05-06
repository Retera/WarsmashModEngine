package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.attacksettings.ABAttackSettingsCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs.ABNearestUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.iterstructs.ABUnitComparator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionCreateUnitTargetedBouncingProjectile implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABUnitCallback target;

	private ABIDCallback id;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;
	private ABAttackSettingsCallback settings;

	private List<ABAction> onLaunch;
	private List<ABAction> onHit;

	private ABBooleanCallback condition;
	private ABIntegerCallback sort;

	private ABFloatCallback range;
	private ABIntegerCallback bounces;
	private ABFloatCallback bounceDelay;

	private ABBooleanCallback allowMultipleBouncesPerUnit;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnit theSource = caster;
		if (source != null) {
			theSource = this.source.callback(caster, localStore, castId);
		}
		AbilityTarget sourceLocation = theSource;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(caster, localStore, castId);
		}

		final CUnit theTarget = this.target.callback(caster, localStore, castId);

		final boolean multiBounce;
		if (allowMultipleBouncesPerUnit != null) {
			multiBounce = allowMultipleBouncesPerUnit.callback(caster, localStore, castId);
		} else {
			multiBounce = localStore.game.getGameplayConstants().isAllowMultiBounce();
		}
		final Set<CUnit> hitUnits;
		if (!multiBounce) {
			hitUnits = new HashSet<>();
			hitUnits.add(theTarget);
		} else {
			hitUnits = null;
		}

		final CAbilityProjectileListener listener = new CAbilityProjectileListener() {

			@Override
			public void onLaunch(CSimulation game, CProjectile projectile, AbilityTarget target) {
				if (onLaunch != null) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
					for (ABAction action : onLaunch) {
						action.runAction(caster, localStore, castId);
					}
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
				}
			}

			@Override
			public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
				if (onHit != null) {
					CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
					CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId), targetUnit);
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId), targetDest);
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId),
							new AbilityPointTarget(projectile.getX(), projectile.getY()));
					for (ABAction action : onHit) {
						action.runAction(caster, localStore, castId);
					}
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId));
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId));
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId));
				}
				startPerformJump(caster, localStore, castId, theTarget, multiBounce, hitUnits,
						bounces.callback(caster, localStore, castId));
			}
		};

		CProjectile proj = null;
		if (id != null) {
			Float theSpeed = null;
			Boolean isHoming = null;
			if (this.speed != null) {
				theSpeed = this.speed.callback(caster, localStore, castId);
			}
			if (this.homing != null) {
				isHoming = this.homing.callback(caster, localStore, castId);
			}
			proj = localStore.game.createProjectile(theSource, this.id.callback(caster, localStore, castId),
					sourceLocation.getX(), sourceLocation.getY(),
					(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theSpeed, isHoming, theTarget,
					listener);
		} else if (settings != null) {
			proj = localStore.game.createProjectile(theSource, this.settings.callback(caster, localStore, castId),
					sourceLocation.getX(), sourceLocation.getY(),
					(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theTarget, listener);
		}

		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.LASTCREATEDPROJECTILE, castId), proj);
	}

	private void startPerformJump(final CUnit caster, final ABLocalDataStore localStore, final int castId,
			final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (remainingJumps <= 0) {
			return;
		}
		float delay = 0;
		if (bounceDelay != null) {
			delay = bounceDelay.callback(caster, localStore, castId);
		}
		if (delay > 0) {
			CTimer runner = new CTimer() {
				@Override
				public void onFire(CSimulation simulation) {
					performJump(caster, localStore, castId, originUnitTarget, multiBounce, hitUnits, remainingJumps);
				}
			};
			runner.setTimeoutTime(delay);
			runner.start(localStore.game);
		} else {
			performJump(caster, localStore, castId, originUnitTarget, multiBounce, hitUnits, remainingJumps);
		}
	}

	private void performJump(final CUnit caster, final ABLocalDataStore localStore, final int castId,
			final CUnit originUnitTarget, final boolean multiBounce, final Set<CUnit> hitUnits,
			final int remainingJumps) {
		if (originUnitTarget == null) {
			return;
		}

		final Float rangeVal = this.range.callback(caster, localStore, castId);

		List<CUnit> foundUnits = new ArrayList<>();
		recycleRect.set(originUnitTarget.getX() - rangeVal, originUnitTarget.getY() - rangeVal, rangeVal * 2,
				rangeVal * 2);
		localStore.game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (originUnitTarget.canReach(enumUnit, rangeVal) && (multiBounce || !hitUnits.contains(enumUnit))) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId), enumUnit);
					if (condition == null || condition.callback(caster, localStore, castId)) {
						foundUnits.add(enumUnit);
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId));

		final CUnit jumpUnit;
		if (foundUnits.size() > 0) {
			if (sort != null) {
				ABUnitComparator comp = new ABUnitComparator(caster, localStore, castId, sort);
				foundUnits.sort(comp);
			} else {
				foundUnits.sort(ABNearestUnitComparator.INSTANCE);
			}

			jumpUnit = foundUnits.get(0);
		} else {
			jumpUnit = null;
		}

		if (jumpUnit != null) {

			if (!multiBounce) {
				hitUnits.add(jumpUnit);
			}

			final CAbilityProjectileListener listener = new CAbilityProjectileListener() {

				@Override
				public void onLaunch(CSimulation game, CProjectile projectile, AbilityTarget target) {
					if (onLaunch != null) {
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId),
								projectile);
						for (ABAction action : onLaunch) {
							action.runAction(caster, localStore, castId);
						}
						localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
					}
				}

				@Override
				public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
					if (onHit != null) {
						CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
						CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId),
								projectile);
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId),
								targetUnit);
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId),
								targetDest);
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId),
								new AbilityPointTarget(projectile.getX(), projectile.getY()));
						for (ABAction action : onHit) {
							action.runAction(caster, localStore, castId);
						}
						localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId));
						localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId));
						localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
						localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId));
					}
					startPerformJump(caster, localStore, castId, jumpUnit, multiBounce, hitUnits,
							bounces.callback(caster, localStore, castId));
				}
			};

			CProjectile proj = null;
			if (id != null) {
				Float theSpeed = null;
				Boolean isHoming = null;
				if (this.speed != null) {
					theSpeed = this.speed.callback(caster, localStore, castId);
				}
				if (this.homing != null) {
					isHoming = this.homing.callback(caster, localStore, castId);
				}
				proj = localStore.game.createProjectile(caster, this.id.callback(caster, localStore, castId),
						originUnitTarget.getX(), originUnitTarget.getY(),
						(float) AbilityTarget.angleBetween(originUnitTarget, jumpUnit), theSpeed, isHoming, jumpUnit,
						listener);
			} else if (settings != null) {
				proj = localStore.game.createProjectile(caster, this.settings.callback(caster, localStore, castId),
						originUnitTarget.getX(), originUnitTarget.getY(),
						(float) AbilityTarget.angleBetween(originUnitTarget, jumpUnit), jumpUnit, listener);
			}

			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.LASTCREATEDPROJECTILE, castId), proj);
		}

	}

}
