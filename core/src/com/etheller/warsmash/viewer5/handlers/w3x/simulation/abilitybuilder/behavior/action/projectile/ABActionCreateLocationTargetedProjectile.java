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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile.ABProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;

public class ABActionCreateLocationTargetedProjectile implements ABAction {

	private ABUnitCallback source;
	private ABLocationCallback sourceLoc;
	private ABLocationCallback target;
	private ABIDCallback id;
	private ABFloatCallback speed;
	private ABBooleanCallback homing;
	
	private List<ABAction> onLaunch;
	private List<ABAction> onHit;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		float theSpeed = 0;
		boolean isHoming = true;
		CUnit theSource = source.callback(game, caster, localStore, castId);
		AbilityTarget sourceLocation = theSource;

		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		int level = (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);

		if (sourceLoc != null) {
			sourceLocation = sourceLoc.callback(game, caster, localStore, castId);
		}
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

		CAbilityProjectileListener listener = new ABProjectileListener(onLaunch, onHit, caster, localStore, castId);

		game.createProjectile(theSource, id.callback(game, caster, localStore, castId), sourceLocation.getX(),
				sourceLocation.getY(), (float) theSource.angleTo(theTarget), theSpeed, isHoming, theTarget, listener);
	}
}
