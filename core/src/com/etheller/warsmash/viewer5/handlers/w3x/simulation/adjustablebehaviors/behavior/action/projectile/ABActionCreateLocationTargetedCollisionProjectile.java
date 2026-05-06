package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.projectile.ABCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionCreateLocationTargetedCollisionProjectile implements ABSingleAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABLocationCallback target;
	private ABIDCallback id;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;

	private List<ABAction> onLaunch;
	private List<ABAction> onPreHits;
	private List<ABBooleanCallback> canHitTarget;
	private List<ABAction> onHit;

	private ABIntegerCallback maxHits;
	private ABIntegerCallback hitsPerTarget;
	private ABFloatCallback radius;
	private ABFloatCallback startingRadius;
	private ABFloatCallback endingRadius;
	private ABFloatCallback collisionInterval;
	private ABBooleanCallback provideCounts;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		Float theSpeed = null;
		Boolean isHoming = null;
		int theMaxHits = 0;
		int theHitsPerTarget = 1;
		float theStartingRadius = 0;
		float theEndingRadius = 0;
		float theCollisionInterval = 0.05f;
		boolean isProvideCounts = false;
		final CUnit theSource = this.source.callback(caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(caster, localStore, castId);
		}
		if (this.maxHits != null) {
			theMaxHits = this.maxHits.callback(caster, localStore, castId);
		}
		if (this.hitsPerTarget != null) {
			theHitsPerTarget = this.hitsPerTarget.callback(caster, localStore, castId);
		}
		if (this.radius != null) {
			final float rad = this.radius.callback(caster, localStore, castId);
			theStartingRadius = rad;
			theEndingRadius = rad;
		} else {
			if (this.endingRadius != null) {
				theStartingRadius = this.startingRadius.callback(caster, localStore, castId);
				theEndingRadius = this.endingRadius.callback(caster, localStore, castId);
			} else {
				final float rad = this.startingRadius.callback(caster, localStore, castId);
				theStartingRadius = rad;
				theEndingRadius = rad;
			}
		}
		if (this.collisionInterval != null) {
			theCollisionInterval = this.collisionInterval.callback(caster, localStore, castId);
		}
		if (this.provideCounts != null) {
			isProvideCounts = this.provideCounts.callback(caster, localStore, castId);
		}

		if (this.speed != null) {
			theSpeed = this.speed.callback(caster, localStore, castId);
		}
		if (this.homing != null) {
			isHoming = this.homing.callback(caster, localStore, castId);
		}

		final AbilityPointTarget theTarget = this.target.callback(caster, localStore, castId);

		final ABCollisionProjectileListener listener = new ABCollisionProjectileListener(this.onLaunch, this.onPreHits,
				this.canHitTarget, this.onHit, caster, localStore, castId);

		final CProjectile proj = localStore.game.createCollisionProjectile(theSource,
				this.id.callback(caster, localStore, castId), sourceLocation.getX(), sourceLocation.getY(),
				(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theSpeed, isHoming, theTarget,
				theMaxHits, theHitsPerTarget, theStartingRadius, theEndingRadius, theCollisionInterval, listener,
				isProvideCounts);

		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.LASTCREATEDPROJECTILE, castId), proj);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String launchFunctionName = jassTextGenerator.createAnonymousFunction(this.onLaunch,
				"CreateLocationTargetedCollisionProjectileAU_OnLaunch");
		final String preHitsFunctionName = jassTextGenerator.createAnonymousFunction(this.onPreHits,
				"CreateLocationTargetedCollisionProjectileAU_OnPreHits");
		final String canHitTargetFunctionName = jassTextGenerator.createAnonymousBooleanFunction(this.canHitTarget,
				"CreateLocationTargetedCollisionProjectileAU_CanHitTarget");
		final String hitFunctionName = jassTextGenerator.createAnonymousFunction(this.onHit,
				"CreateLocationTargetedCollisionProjectileAU_OnHit");

		final String sourceUnitExpression = this.source.generateJassEquivalent(jassTextGenerator);
		String sourceLocExpression;
		if (this.sourceLoc != null) {
			sourceLocExpression = this.sourceLoc.generateJassEquivalent(jassTextGenerator);
		} else {
			sourceLocExpression = "GetUnitLoc(" + sourceUnitExpression + ")";
		}

		String maxHitsExpression = "0";
		if (this.maxHits != null) {
			maxHitsExpression = this.maxHits.generateJassEquivalent(jassTextGenerator);
		}

		String hitsPerTargetExpression = "1";
		if (this.hitsPerTarget != null) {
			hitsPerTargetExpression = this.hitsPerTarget.generateJassEquivalent(jassTextGenerator);
		}

		String startingRadiusExpression;
		String endingRadiusExpression;
		if (this.radius != null) {
			final String radiusExpression = this.radius.generateJassEquivalent(jassTextGenerator);
			startingRadiusExpression = radiusExpression;
			endingRadiusExpression = radiusExpression;
		} else {
			if (this.endingRadius != null) {
				startingRadiusExpression = this.startingRadius.generateJassEquivalent(jassTextGenerator);
				endingRadiusExpression = this.endingRadius.generateJassEquivalent(jassTextGenerator);
			} else {
				final String radiusExpression = this.startingRadius.generateJassEquivalent(jassTextGenerator);
				startingRadiusExpression = radiusExpression;
				endingRadiusExpression = radiusExpression;
			}
		}

		String collisionIntervalExpression = "0.05";
		if (this.collisionInterval != null) {
			collisionIntervalExpression = this.collisionInterval.generateJassEquivalent(jassTextGenerator);
		}

		String provideCountsExpression = "false";
		if (this.provideCounts != null) {
			provideCountsExpression = this.provideCounts.generateJassEquivalent(jassTextGenerator);
		}
		if (this.speed != null) {
			if (this.homing != null) {
				String homingExpression = "true";
				if (this.homing != null) {
					homingExpression = this.homing.generateJassEquivalent(jassTextGenerator);
				}
				return "CreateLocationTargetedCollisionProjectileAnySpeedAU(" + jassTextGenerator.getCaster() + ", "
						+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
						+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
						+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.speed.generateJassEquivalent(jassTextGenerator) + ", " + homingExpression + ", "
						+ jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
						+ jassTextGenerator.functionPointerByName(preHitsFunctionName) + ", Condition("
						+ jassTextGenerator.functionPointerByName(canHitTargetFunctionName) + "), "
						+ jassTextGenerator.functionPointerByName(hitFunctionName) + ", " + maxHitsExpression + ", "
						+ hitsPerTargetExpression + ", " + startingRadiusExpression + ", " + endingRadiusExpression
						+ ", " + collisionIntervalExpression + ", " + provideCountsExpression + ")";

			} else {
				throw new UnsupportedOperationException();
			}
		} else if (this.homing != null) {
			throw new UnsupportedOperationException();
		}

		return "CreateLocationTargetedCollisionProjectileAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
				+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(preHitsFunctionName) + ", Condition("
				+ jassTextGenerator.functionPointerByName(canHitTargetFunctionName) + "), "
				+ jassTextGenerator.functionPointerByName(hitFunctionName) + ", " + maxHitsExpression + ", "
				+ hitsPerTargetExpression + ", " + startingRadiusExpression + ", " + endingRadiusExpression + ", "
				+ collisionIntervalExpression + ", " + provideCountsExpression + ")";
	}
}
