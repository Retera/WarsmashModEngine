package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABUnitStateListener implements CUnitStateListener {
	private CUnit caster;
	private ABLocalDataStore localStore;
	private int castId;

	private List<ABAction> onLifeChanged = null;
	private List<ABAction> onManaChanged = null;
	private List<ABAction> onOrdersChanged = null;
	private List<ABAction> onQueueChanged = null;
	private List<ABAction> onRallyPointChanged = null;
	private List<ABAction> onWaypointsChanged = null;
	private List<ABAction> onHeroStatsChanged = null;
	private List<ABAction> onInventoryChanged = null;
	private List<ABAction> onAttacksChanged = null;
	private List<ABAction> onAbilitiesChanged = null;
	private List<ABAction> onUpgradesChanged = null;
	private List<ABAction> onHideStateChanged = null;

	public ABUnitStateListener(CUnit caster, ABLocalDataStore localStore, int castId, List<ABAction> onLifeChanged,
			List<ABAction> onManaChanged, List<ABAction> onOrdersChanged, List<ABAction> onQueueChanged,
			List<ABAction> onRallyPointChanged, List<ABAction> onWaypointsChanged, List<ABAction> onHeroStatsChanged,
			List<ABAction> onInventoryChanged, List<ABAction> onAttacksChanged, List<ABAction> onAbilitiesChanged,
			List<ABAction> onUpgradesChanged, List<ABAction> onHideStateChanged) {
		super();
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;
		this.onLifeChanged = onLifeChanged;
		this.onManaChanged = onManaChanged;
		this.onOrdersChanged = onOrdersChanged;
		this.onQueueChanged = onQueueChanged;
		this.onRallyPointChanged = onRallyPointChanged;
		this.onWaypointsChanged = onWaypointsChanged;
		this.onHeroStatsChanged = onHeroStatsChanged;
		this.onInventoryChanged = onInventoryChanged;
		this.onAttacksChanged = onAttacksChanged;
		this.onAbilitiesChanged = onAbilitiesChanged;
		this.onUpgradesChanged = onUpgradesChanged;
		this.onHideStateChanged = onHideStateChanged;
	}

	public void setOnLifeChanged(List<ABAction> onLifeChanged) {
		this.onLifeChanged = onLifeChanged;
	}

	public void setOnManaChanged(List<ABAction> onManaChanged) {
		this.onManaChanged = onManaChanged;
	}

	public void setOnOrdersChanged(List<ABAction> onOrdersChanged) {
		this.onOrdersChanged = onOrdersChanged;
	}

	public void setOnQueueChanged(List<ABAction> onQueueChanged) {
		this.onQueueChanged = onQueueChanged;
	}

	public void setOnRallyPointChanged(List<ABAction> onRallyPointChanged) {
		this.onRallyPointChanged = onRallyPointChanged;
	}

	public void setOnWaypointsChanged(List<ABAction> onWaypointsChanged) {
		this.onWaypointsChanged = onWaypointsChanged;
	}

	public void setOnHeroStatsChanged(List<ABAction> onHeroStatsChanged) {
		this.onHeroStatsChanged = onHeroStatsChanged;
	}

	public void setOnInventoryChanged(List<ABAction> onInventoryChanged) {
		this.onInventoryChanged = onInventoryChanged;
	}

	public void setOnAttacksChanged(List<ABAction> onAttacksChanged) {
		this.onAttacksChanged = onAttacksChanged;
	}

	public void setOnAbilitiesChanged(List<ABAction> onAbilitiesChanged) {
		this.onAbilitiesChanged = onAbilitiesChanged;
	}

	public void setOnUpgradesChanged(List<ABAction> onUpgradesChanged) {
		this.onUpgradesChanged = onUpgradesChanged;
	}

	public void setOnHideStateChanged(List<ABAction> onHideStateChanged) {
		this.onHideStateChanged = onHideStateChanged;
	}

	@Override
	public void lifeChanged() {
		if (onLifeChanged != null) {
			for (ABAction action : onLifeChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void manaChanged() {
		if (onManaChanged != null) {
			for (ABAction action : onManaChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void ordersChanged() {
		if (onOrdersChanged != null) {
			for (ABAction action : onOrdersChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void queueChanged() {
		if (onQueueChanged != null) {
			for (ABAction action : onQueueChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void rallyPointChanged() {
		if (onRallyPointChanged != null) {
			for (ABAction action : onRallyPointChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void waypointsChanged() {
		if (onWaypointsChanged != null) {
			for (ABAction action : onWaypointsChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void heroStatsChanged() {
		if (onHeroStatsChanged != null) {
			for (ABAction action : onHeroStatsChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void inventoryChanged() {
		if (onInventoryChanged != null) {
			for (ABAction action : onInventoryChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void attacksChanged() {
		if (onAttacksChanged != null) {
			for (ABAction action : onAttacksChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void abilitiesChanged() {
		if (onAbilitiesChanged != null) {
			for (ABAction action : onAbilitiesChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void upgradesChanged() {
		if (onUpgradesChanged != null) {
			for (ABAction action : onUpgradesChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void hideStateChanged() {
		if (onHideStateChanged != null) {
			for (ABAction action : onHideStateChanged) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

}
