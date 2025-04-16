package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import java.util.List;
import java.util.Map;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimedPausedExpirationBuff extends ABTimedBuff implements CPausedTickingBuff {

	public ABTimedPausedExpirationBuff(int handleId, War3ID alias, CAbility sourceAbility, CUnit sourceUnit, float duration, boolean showTimedLifeBar,
			Map<String, Object> localStore, List<ABAction> onAddActions, List<ABAction> onRemoveActions,
			List<ABAction> onExpireActions, boolean showIcon, final int castId, final boolean leveled,
			final boolean positive, final boolean dispellable) {
		super(handleId, alias, sourceAbility, sourceUnit, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions,
				showIcon, castId, leveled, positive, dispellable);
	}

	public ABTimedPausedExpirationBuff(int handleId, War3ID alias, CAbility sourceAbility,
			CUnit sourceUnit, float duration, boolean showTimedLifeBar,
			Map<String, Object> localStore, List<ABAction> onAddActions, List<ABAction> onRemoveActions,
			List<ABAction> onExpireActions, final int castId, final boolean leveled, final boolean positive,
			final boolean dispellable) {
		super(handleId, alias, sourceAbility, sourceUnit, duration, showTimedLifeBar, localStore, onAddActions, onRemoveActions, onExpireActions,
				castId, leveled, positive, dispellable);
	}

}
