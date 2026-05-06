
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.state;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABUnitStateListener;

public class ABActionCreateUnitStateListener implements ABAction {

	private List<ABAction> onLifeChanged;
	private List<ABAction> onManaChanged;
	private List<ABAction> onOrdersChanged;
	private List<ABAction> onQueueChanged;
	private List<ABAction> onRallyPointChanged;
	private List<ABAction> onWaypointsChanged;
	private List<ABAction> onHeroStatsChanged;
	private List<ABAction> onInventoryChanged;
	private List<ABAction> onAttacksChanged;
	private List<ABAction> onAbilitiesChanged;
	private List<ABAction> onUpgradesChanged;
	private List<ABAction> onHideStateChanged;

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		ABUnitStateListener modifier = new ABUnitStateListener(caster, localStore, castId, onLifeChanged, onManaChanged,
				onOrdersChanged, onQueueChanged, onRallyPointChanged, onWaypointsChanged, onHeroStatsChanged,
				onInventoryChanged, onAttacksChanged, onAbilitiesChanged, onUpgradesChanged, onHideStateChanged);

		localStore.put(ABLocalStoreKeys.LASTCREATEDUSL, modifier);
	}
}