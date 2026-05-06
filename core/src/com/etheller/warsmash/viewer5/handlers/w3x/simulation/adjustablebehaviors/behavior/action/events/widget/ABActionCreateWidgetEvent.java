package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.events.widget;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.widget.ABWidgetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.event.ABWidgetEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABActionCreateWidgetEvent implements ABAction {

	private ABWidgetCallback widget;
	private ABBooleanCallback condition;
	private List<ABAction> actions;

	private JassGameEventsWar3 eventType;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {

		final ABWidgetEvent event = new ABWidgetEvent(caster, localStore, castId,
				this.widget.callback(caster, localStore, castId), this.eventType, this.condition, this.actions);

		localStore.put(ABLocalStoreKeys.LASTCREATEDWIDEVENT, event);
	}
}
