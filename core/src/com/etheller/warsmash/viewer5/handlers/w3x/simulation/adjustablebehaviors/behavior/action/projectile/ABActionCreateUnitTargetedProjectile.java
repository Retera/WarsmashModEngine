package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.attacksettings.ABAttackSettingsCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.projectile.ABProjectileListener;
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
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final CUnit theSource = this.source.callback(caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(caster, localStore, castId);
		}

		final CUnit theTarget = this.target.callback(caster, localStore, castId);

		final CAbilityProjectileListener listener = new ABProjectileListener(this.onLaunch, this.onHit, caster,
				localStore, castId);

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
