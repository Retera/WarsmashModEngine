package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.projectile;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABCollisionProjectileListener implements CAbilityCollisionProjectileListener {

	private List<ABAction> onLaunch;
	private List<ABAction> onPreHits;
	private List<ABBooleanCallback> canHitTarget;
	private List<ABAction> onHit;

	private CUnit caster;
	private ABLocalDataStore localStore;
	private int castId;

	public ABCollisionProjectileListener(List<ABAction> onLaunch, List<ABAction> onPreHits,
			List<ABBooleanCallback> canHitTarget, List<ABAction> onHit, CUnit caster, ABLocalDataStore localStore,
			int castId) {
		super();
		this.onLaunch = onLaunch;
		this.onPreHits = onPreHits;
		this.canHitTarget = canHitTarget;
		this.onHit = onHit;
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;
	}

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
	public void onPreHits(CSimulation game, CProjectile projectile, AbilityPointTarget loc) {
		if (onPreHits != null) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
			for (ABAction action : onPreHits) {
				action.runAction(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
		}
	}

	@Override
	public boolean canHitTarget(CSimulation game, CWidget target) {
		boolean result = true;
		if (canHitTarget != null) {
			CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId), targetUnit);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId), targetDest);
			for (ABBooleanCallback condition : canHitTarget) {
				result = result && condition.callback(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId));
		}
		return result;
	}

	@Override
	public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
		if (onHit != null) {
			CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId), targetUnit);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId), targetDest);
			for (ABAction action : onHit) {
				action.runAction(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
		}
	}

	@Override
	public void setUnitTargets(int units) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEUNITTARGETS, castId), units);
	}

	@Override
	public void setDestructableTargets(int dests) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEDESTTARGETS, castId), dests);
	}

	@Override
	public void setCurrentLocation(AbilityPointTarget loc) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId), loc);
	}

}
