package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABCollisionProjectileListener implements CAbilityCollisionProjectileListener {
	
	private List<ABAction> onLaunch;
	private List<ABAction> onPreHits;
	private List<ABCondition> canHitTarget;
	private List<ABAction> onHit;
	
	private CUnit caster;
	private Map<String, Object> localStore;
	private int castId;

	public ABCollisionProjectileListener(List<ABAction> onLaunch, List<ABAction> onPreHits, List<ABCondition> canHitTarget, List<ABAction> onHit, CUnit caster,
			Map<String, Object> localStore, int castId) {
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
			localStore.put(ABLocalStoreKeys.THISPROJECTILE+castId, projectile);
			for (ABAction action : onLaunch) {
				action.runAction(game, caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.THISPROJECTILE+castId);
		}
	}

	@Override
	public void onPreHits(CSimulation game, CProjectile projectile, AbilityPointTarget loc) {
		if (onPreHits != null) {
			localStore.put(ABLocalStoreKeys.THISPROJECTILE+castId, projectile);
			for (ABAction action : onPreHits) {
				action.runAction(game, caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.THISPROJECTILE+castId);
		}
	}

	@Override
	public boolean canHitTarget(CSimulation game, CWidget target) {
		boolean result = true;
		if (canHitTarget != null) {
			CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			localStore.put(ABLocalStoreKeys.PROJECTILEHITUNIT+castId, targetUnit);
			localStore.put(ABLocalStoreKeys.PROJECTILEHITDEST+castId, targetDest);
			for (ABCondition condition : canHitTarget) {
				result = result && condition.evaluate(game, caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.PROJECTILEHITUNIT+castId);
			localStore.remove(ABLocalStoreKeys.PROJECTILEHITDEST+castId);
		}
		return result;
	}

	@Override
	public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
		if (onHit != null) {
			CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			localStore.put(ABLocalStoreKeys.THISPROJECTILE+castId, projectile);
			localStore.put(ABLocalStoreKeys.PROJECTILEHITUNIT+castId, targetUnit);
			localStore.put(ABLocalStoreKeys.PROJECTILEHITDEST+castId, targetDest);
			for (ABAction action : onHit) {
				action.runAction(game, caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.PROJECTILEHITUNIT+castId);
			localStore.remove(ABLocalStoreKeys.PROJECTILEHITDEST+castId);
			localStore.remove(ABLocalStoreKeys.THISPROJECTILE+castId);
		}
	}

	@Override
	public void setUnitTargets(int units) {
		localStore.put(ABLocalStoreKeys.PROJECTILEUNITTARGETS+castId, units);
	}

	@Override
	public void setDestructableTargets(int dests) {
		localStore.put(ABLocalStoreKeys.PROJECTILEDESTTARGETS+castId, dests);
	}

	@Override
	public void setCurrentLocation(AbilityPointTarget loc) {
		localStore.put(ABLocalStoreKeys.PROJECTILECURRENTLOC+castId, loc);
	}

}
