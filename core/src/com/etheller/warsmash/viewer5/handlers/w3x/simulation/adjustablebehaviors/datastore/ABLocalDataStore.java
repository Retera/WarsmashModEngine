package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;

public abstract class ABLocalDataStore implements ABDataStore {

	public CSimulation game = null;
	public CUnit originUnit = null;
	public ABAbilityBuilderAbility originAbility = null;
	public CItem originItem = null;
	public CPlayer originPlayer = null;
	public CUpgradeEffect originUpgrade = null;

	public int getLevelDataInstanceLevel(int castId) {
		if (castId != ABConstants.NO_CAST_ID) {
			return this.getInt(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId)) - 1;
		}
		if (this.originAbility != null) {
			return this.originAbility.getLevel() - 1;
		}
		return 0;
	}
}
