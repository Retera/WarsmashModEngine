package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.projectile;

import java.util.List;
import java.util.Map;

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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile.ABCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionCreateLocationTargetedCollisionProjectile implements ABAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABLocationCallback target;
	private ABIDCallback id;
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
	private ABFloatCallback collisionInterval;
	private ABBooleanCallback provideCounts;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float theSpeed = 0;
		boolean isHoming = true;
		int theMaxHits = 0;
		int theHitsPerTarget = 1;
		float theStartingRadius = 0;
		float theEndingRadius = 0;
		float theCollisionInterval = 0.05f;
		boolean isProvideCounts = false;
		CUnit theSource = source.callback(game, caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		if (sourceLoc != null) {
			sourceLocation = sourceLoc.callback(game, caster, localStore, castId);
		}
		if (maxHits != null) {
			theMaxHits = maxHits.callback(game, caster, localStore, castId);
		}
		if (hitsPerTarget != null) {
			theHitsPerTarget = hitsPerTarget.callback(game, caster, localStore, castId);
		}
		if (radius != null ) { 
			float rad = radius.callback(game, caster, localStore, castId);
			theStartingRadius = rad;
			theEndingRadius = rad;
		} else {
			if (endingRadius != null) {
				theStartingRadius = startingRadius.callback(game, caster, localStore, castId);
				theEndingRadius = endingRadius.callback(game, caster, localStore, castId);
			} else {
				float rad = startingRadius.callback(game, caster, localStore, castId);
				theStartingRadius = rad;
				theEndingRadius = rad;
			}
		}
		if (collisionInterval != null) {
			theCollisionInterval = collisionInterval.callback(game, caster, localStore, castId);
		}
		if (provideCounts != null) {
			isProvideCounts = provideCounts.callback(game, caster, localStore, castId);
		}
		

		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		int level = (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);

		if (speed != null) {
			theSpeed = speed.callback(game, caster, localStore, castId);
		} else {
			theSpeed = editorData.getFieldAsFloat(AbilityFields.PROJECTILE_SPEED, 0);
		}
		if (homing != null) {
			isHoming = homing.callback(game, caster, localStore, castId);
		} else {
			isHoming = editorData.getFieldAsBoolean(AbilityFields.PROJECTILE_HOMING_ENABLED, 0);
		}

		AbilityPointTarget theTarget = target.callback(game, caster, localStore, castId);

		ABCollisionProjectileListener listener = new ABCollisionProjectileListener(onLaunch, onPreHits, canHitTarget, onHit, caster, localStore, castId);

		CProjectile proj = game.createCollisionProjectile(theSource, id.callback(game, caster, localStore, castId), sourceLocation.getX(),
				sourceLocation.getY(), (float) theSource.angleTo(theTarget), theSpeed, isHoming, theTarget, theMaxHits,
				theHitsPerTarget, theStartingRadius, theEndingRadius, theCollisionInterval, listener, isProvideCounts);

		localStore.put(ABLocalStoreKeys.LASTCREATEDPROJECTILE+castId, proj);
	}
}
