package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.attacksettings.ABAttackSettingsCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile.ABProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionCreateUnitTargetedProjectile implements ABSingleAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABUnitCallback target;
	private ABIDCallback id;
	private ABAttackSettingsCallback settings;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;

	private List<ABAction> onLaunch;
	private List<ABAction> onHit;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CUnit theSource = this.source.callback(game, caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(game, caster, localStore, castId);
		}

		final CUnit theTarget = this.target.callback(game, caster, localStore, castId);

		final CAbilityProjectileListener listener = new ABProjectileListener(this.onLaunch, this.onHit, caster,
				localStore, castId);

		CProjectile proj = null;
		if (id != null) {
			Float theSpeed = null;
			Boolean isHoming = null;
			if (this.speed != null) {
				theSpeed = this.speed.callback(game, caster, localStore, castId);
			}
			if (this.homing != null) {
				isHoming = this.homing.callback(game, caster, localStore, castId);
			}
			proj = game.createProjectile(theSource, this.id.callback(game, caster, localStore, castId),
					sourceLocation.getX(), sourceLocation.getY(),
					(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theSpeed, isHoming, theTarget,
					listener);
		} else if (settings != null) {
			proj = game.createProjectile(theSource, this.settings.callback(game, caster, localStore, castId),
					sourceLocation.getX(), sourceLocation.getY(),
					(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theTarget, listener);
		}

		localStore.put(ABLocalStoreKeys.LASTCREATEDPROJECTILE + castId, proj);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String onLaunchFunc = jassTextGenerator.createAnonymousFunction(this.onLaunch,
				"CreateUnitTargetedProjectileAU_OnLaunch");
		final String onHitFunc = jassTextGenerator.createAnonymousFunction(this.onHit,
				"CreateUnitTargetedProjectileAU_OnHit");

		final String sourceUnitExpression = this.source.generateJassEquivalent(jassTextGenerator);
		String sourceLocExpression;
		if (this.sourceLoc != null) {
			sourceLocExpression = this.sourceLoc.generateJassEquivalent(jassTextGenerator);
		} else {
			sourceLocExpression = "GetUnitLoc(" + sourceUnitExpression + ")";
		}

		if (this.speed != null) {
			if (this.homing != null) {
				return "CreateUnitTargetedProjectileAnySpeedAU(" + jassTextGenerator.getCaster() + ", "
						+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
						+ sourceUnitExpression + ", " + sourceLocExpression + ", "
						+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.speed.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.homing.generateJassEquivalent(jassTextGenerator) + ", "
						+ jassTextGenerator.functionPointerByName(onLaunchFunc) + ", "
						+ jassTextGenerator.functionPointerByName(onHitFunc) + ")";

			} else {
				throw new UnsupportedOperationException();
			}
		} else if (this.homing != null) {
			throw new UnsupportedOperationException();
		}

		return "CreateUnitTargetedProjectileAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
				+ sourceUnitExpression + ", " + sourceLocExpression + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(onLaunchFunc) + ", "
				+ jassTextGenerator.functionPointerByName(onHitFunc) + ")";
	}
}
