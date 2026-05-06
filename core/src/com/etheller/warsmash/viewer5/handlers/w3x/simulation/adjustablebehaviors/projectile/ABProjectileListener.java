package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.projectile;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABProjectileListener implements CAbilityProjectileListener {

	private List<ABAction> onLaunch;
	private List<ABAction> onHit;

	private CUnit caster;
	private ABLocalDataStore localStore;
	private int castId;

	public ABProjectileListener(List<ABAction> onLaunch, List<ABAction> onHit, CUnit caster,
			ABLocalDataStore localStore, int castId) {
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
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
			for (ABAction action : onLaunch) {
				action.runAction(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
		}
	}

	@Override
	public void onHit(CSimulation game, CProjectile projectile, AbilityTarget target) {
		if (onHit != null) {
			CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId), projectile);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId), targetUnit);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId), targetDest);
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId),
					new AbilityPointTarget(projectile.getX(), projectile.getY()));
			for (ABAction action : onHit) {
				action.runAction(caster, localStore, castId);
			}
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITUNIT, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEHITDEST, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THISPROJECTILE, castId));
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILECURRENTLOC, castId));
		}
	}

}
