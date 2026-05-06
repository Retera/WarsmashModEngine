package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff;

import java.util.List;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABTimedPausedExpirationBuff extends ABTimedBuff implements CPausedTickingBuff {

	public ABTimedPausedExpirationBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, boolean showIcon, final int castId,
			final boolean leveled, final boolean positive, final boolean dispellable) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, onAddActions,
				onRemoveActions, onExpireActions, showIcon, castId, leveled, positive, dispellable);
	}

	public ABTimedPausedExpirationBuff(int handleId, War3ID alias, ABLocalDataStore localStore, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar, List<ABAction> onAddActions,
			List<ABAction> onRemoveActions, List<ABAction> onExpireActions, final int castId, final boolean leveled,
			final boolean positive, final boolean dispellable) {
		super(handleId, alias, localStore, sourceAbility, sourceUnit, duration, showTimedLifeBar, onAddActions,
				onRemoveActions, onExpireActions, castId, leveled, positive, dispellable);
	}

}
