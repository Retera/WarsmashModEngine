package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile.ABCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class ABActionCreateLocationTargetedPseudoProjectile implements ABSingleAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABLocationCallback target;
	private ABIDCallback id;
	private CEffectType effectType;
	private ABIntegerCallback effectArtIndex;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;

	private List<ABAction> onLaunch;
	private List<ABAction> onPreHits;
	private List<ABCondition> canHitTarget;
	private List<ABAction> onHit;

	private ABIntegerCallback maxHits;
	private ABIntegerCallback hitsPerTarget;
	private ABFloatCallback radius;
	private ABFloatCallback startingRadius;
	private ABFloatCallback endingRadius;
	private ABFloatCallback projectileStepInterval;
	private ABIntegerCallback projectileArtSkip;
	private ABBooleanCallback provideCounts;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float theSpeed = 0;
		boolean isHoming = true;
		int theMaxHits = 0;
		int theHitsPerTarget = 1;
		float theStartingRadius = 0;
		float theEndingRadius = 0;
		float theCollisionInterval = 0.25f;
		CEffectType theEffectType = CEffectType.SPECIAL;
		int theEffectArtIndex = 0;
		boolean isProvideCounts = false;
		final CUnit theSource = this.source.callback(game, caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;
		int theArtSkip = 1;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(game, caster, localStore, castId);
		}
		if (this.effectType != null) {
			theEffectType = this.effectType;
		}
		if (this.effectArtIndex != null) {
			theEffectArtIndex = this.effectArtIndex.callback(game, caster, localStore, castId);
		}

		if (this.maxHits != null) {
			theMaxHits = this.maxHits.callback(game, caster, localStore, castId);
		}
		if (this.hitsPerTarget != null) {
			theHitsPerTarget = this.hitsPerTarget.callback(game, caster, localStore, castId);
		}
		if (this.radius != null) {
			final float rad = this.radius.callback(game, caster, localStore, castId);
			theStartingRadius = rad;
			theEndingRadius = rad;
		}
		else {
			if (this.endingRadius != null) {
				theStartingRadius = this.startingRadius.callback(game, caster, localStore, castId);
				theEndingRadius = this.endingRadius.callback(game, caster, localStore, castId);
			}
			else {
				final float rad = this.startingRadius.callback(game, caster, localStore, castId);
				theStartingRadius = rad;
				theEndingRadius = rad;
			}
		}
		if (this.projectileStepInterval != null) {
			theCollisionInterval = this.projectileStepInterval.callback(game, caster, localStore, castId);
		}
		if (this.projectileArtSkip != null) {
			theArtSkip = this.projectileArtSkip.callback(game, caster, localStore, castId);
		}
		if (this.provideCounts != null) {
			isProvideCounts = this.provideCounts.callback(game, caster, localStore, castId);
		}

		final GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		final int level = (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);

		if (this.speed != null) {
			theSpeed = this.speed.callback(game, caster, localStore, castId);
		}
		else {
			theSpeed = editorData.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);
		}
		if (this.homing != null) {
			isHoming = this.homing.callback(game, caster, localStore, castId);
		}
		else {
			isHoming = editorData.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, 0);
		}

		final AbilityPointTarget theTarget = this.target.callback(game, caster, localStore, castId);

		final ABCollisionProjectileListener listener = new ABCollisionProjectileListener(this.onLaunch, this.onPreHits,
				this.canHitTarget, this.onHit, caster, localStore, castId);

		final CProjectile proj = game.createPseudoProjectile(theSource,
				this.id.callback(game, caster, localStore, castId), theEffectType, theEffectArtIndex,
				sourceLocation.getX(), sourceLocation.getY(), (float) theSource.angleTo(theTarget), theSpeed,
				theCollisionInterval, theArtSkip, isHoming, theTarget, theMaxHits, theHitsPerTarget, theStartingRadius,
				theEndingRadius, listener, isProvideCounts);

		localStore.put(ABLocalStoreKeys.LASTCREATEDPROJECTILE + castId, proj);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String launchFunctionName = jassTextGenerator.createAnonymousFunction(this.onLaunch,
				"CreateLocationTargetedPseudoProjectileAU_OnLaunch");
		final String preHitsFunctionName = jassTextGenerator.createAnonymousFunction(this.onPreHits,
				"CreateLocationTargetedPseudoProjectileAU_OnPreHits");
		final String canHitTargetFunctionName = jassTextGenerator.createAnonymousBooleanFunction(this.canHitTarget,
				"CreateLocationTargetedPseudoProjectileAU_CanHitTarget");
		final String hitFunctionName = jassTextGenerator.createAnonymousFunction(this.onHit,
				"CreateLocationTargetedPseudoProjectileAU_OnHit");

		CEffectType theEffectType = CEffectType.SPECIAL;
		if (this.effectType != null) {
			theEffectType = this.effectType;
		}
		final String effectTypeExpression = "EFFECT_TYPE_" + theEffectType.name();
		String theEffectArtIndex = "0";
		if (this.effectArtIndex != null) {
			theEffectArtIndex = this.effectArtIndex.generateJassEquivalent(jassTextGenerator);
		}

		final String sourceUnitExpression = this.source.generateJassEquivalent(jassTextGenerator);
		String sourceLocExpression;
		if (this.sourceLoc != null) {
			sourceLocExpression = this.sourceLoc.generateJassEquivalent(jassTextGenerator);
		}
		else {
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
		}
		else {
			if (this.endingRadius != null) {
				startingRadiusExpression = this.startingRadius.generateJassEquivalent(jassTextGenerator);
				endingRadiusExpression = this.endingRadius.generateJassEquivalent(jassTextGenerator);
			}
			else {
				final String radiusExpression = this.startingRadius.generateJassEquivalent(jassTextGenerator);
				startingRadiusExpression = radiusExpression;
				endingRadiusExpression = radiusExpression;
			}
		}

		String collisionIntervalExpression = "0.25";
		if (this.projectileStepInterval != null) {
			collisionIntervalExpression = this.projectileStepInterval.generateJassEquivalent(jassTextGenerator);
		}

		String artSkipExpression = "1";
		if (this.projectileArtSkip != null) {
			artSkipExpression = this.projectileArtSkip.generateJassEquivalent(jassTextGenerator);
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
				return "CreateLocationTargetedPseudoProjectileAnySpeedAU(" + jassTextGenerator.getCaster() + ", "
						+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
						+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
						+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.id.generateJassEquivalent(jassTextGenerator) + ", " + effectTypeExpression + ", "
						+ theEffectArtIndex + ", " + this.speed.generateJassEquivalent(jassTextGenerator) + ", "
						+ homingExpression + ", " + jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
						+ jassTextGenerator.functionPointerByName(preHitsFunctionName) + ", Condition("
						+ jassTextGenerator.functionPointerByName(canHitTargetFunctionName) + "), "
						+ jassTextGenerator.functionPointerByName(hitFunctionName) + ", " + maxHitsExpression + ", "
						+ hitsPerTargetExpression + ", " + startingRadiusExpression + ", " + endingRadiusExpression
						+ ", " + collisionIntervalExpression + ", " + artSkipExpression + ", " + provideCountsExpression
						+ ")";

			}
			else {
				throw new UnsupportedOperationException();
			}
		}
		else if (this.homing != null) {
			throw new UnsupportedOperationException();
		}

		return "CreateLocationTargetedPseudoProjectileAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
				+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ", " + effectTypeExpression + ", "
				+ theEffectArtIndex + ", " + jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(preHitsFunctionName) + ", Condition("
				+ jassTextGenerator.functionPointerByName(canHitTargetFunctionName) + "), "
				+ jassTextGenerator.functionPointerByName(hitFunctionName) + ", " + maxHitsExpression + ", "
				+ hitsPerTargetExpression + ", " + startingRadiusExpression + ", " + endingRadiusExpression + ", "
				+ collisionIntervalExpression + ", " + artSkipExpression + ", " + provideCountsExpression + ")";
	}
}
