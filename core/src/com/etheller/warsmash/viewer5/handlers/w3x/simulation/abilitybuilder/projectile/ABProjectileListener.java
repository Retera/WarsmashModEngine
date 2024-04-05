package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.projectile;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABProjectileListener implements CAbilityProjectileListener {
	
	private List<ABAction> onLaunch;
	private List<ABAction> onHit;
	
	private CUnit caster;
	private Map<String, Object> localStore;
	private int castId;

	public ABProjectileListener(List<ABAction> onLaunch, List<ABAction> onHit, CUnit caster,
			Map<String, Object> localStore, int castId) {
		super();
		this.onLaunch = onLaunch;
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


}
