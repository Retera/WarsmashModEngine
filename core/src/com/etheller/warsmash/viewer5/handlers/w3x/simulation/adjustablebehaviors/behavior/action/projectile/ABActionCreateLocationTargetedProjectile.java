package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
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

public class ABActionCreateLocationTargetedProjectile implements ABSingleAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABLocationCallback target;
	private ABIDCallback id;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;

	private List<ABAction> onLaunch;
	private List<ABAction> onHit;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		Float theSpeed = null;
		Boolean isHoming = null;
		final CUnit theSource = this.source.callback(caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		if (this.sourceLoc != null) {
			sourceLocation = this.sourceLoc.callback(caster, localStore, castId);
		}
		if (this.speed != null) {
			theSpeed = this.speed.callback(caster, localStore, castId);
		}
		if (this.homing != null) {
			isHoming = this.homing.callback(caster, localStore, castId);
		}

		final AbilityPointTarget theTarget = this.target.callback(caster, localStore, castId);

		final CAbilityProjectileListener listener = new ABProjectileListener(this.onLaunch, this.onHit, caster,
				localStore, castId);

		final CProjectile proj = localStore.game.createProjectile(theSource,
				this.id.callback(caster, localStore, castId), sourceLocation.getX(), sourceLocation.getY(),
				(float) AbilityTarget.angleBetween(sourceLocation, theTarget), theSpeed, isHoming, theTarget, listener);

		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.LASTCREATEDPROJECTILE, castId), proj);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String launchFunctionName = jassTextGenerator.createAnonymousFunction(this.onLaunch,
				"CreateLocationTargetedProjectileAU_OnLaunch");
		final String hitFunctionName = jassTextGenerator.createAnonymousFunction(this.onHit,
				"CreateLocationTargetedProjectileAU_OnHit");

		final String sourceUnitExpression = this.source.generateJassEquivalent(jassTextGenerator);
		String sourceLocExpression;
		if (this.sourceLoc != null) {
			sourceLocExpression = this.sourceLoc.generateJassEquivalent(jassTextGenerator);
		} else {
			sourceLocExpression = "GetUnitLoc(" + sourceUnitExpression + ")";
		}

		if (this.speed != null) {
			if (this.homing != null) {
				String homingExpression = "true";
				if (this.homing != null) {
					homingExpression = this.homing.generateJassEquivalent(jassTextGenerator);
				}
				return "CreateLocationTargetedProjectileAnySpeedAU(" + jassTextGenerator.getCaster() + ", "
						+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
						+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
						+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
						+ this.speed.generateJassEquivalent(jassTextGenerator) + ", " + homingExpression + ", "
						+ jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
						+ jassTextGenerator.functionPointerByName(hitFunctionName) + ")";

			} else {
				throw new UnsupportedOperationException();
			}
		} else if (this.homing != null) {
			throw new UnsupportedOperationException();
		}

		return "CreateLocationTargetedProjectileAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", "
				+ this.source.generateJassEquivalent(jassTextGenerator) + ", " + sourceLocExpression + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.id.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(launchFunctionName) + ", "
				+ jassTextGenerator.functionPointerByName(hitFunctionName) + ")";
	}
}
