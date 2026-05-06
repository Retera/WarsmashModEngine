package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.event;

import java.util.List;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABPlayerEvent extends CPlayerEvent {

	private CUnit caster;
	private ABLocalDataStore localStore;
	private int castId;

	private ABBooleanCallback condition;
	private List<ABAction> actions;

	public ABPlayerEvent(CUnit caster, ABLocalDataStore localStore, int castId, CPlayerJass player,
			JassGameEventsWar3 eventType, ABBooleanCallback condition, List<ABAction> actions) {
		super(localStore.game.getGlobalScope(), player, null, eventType, null);
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;

		this.condition = condition;
		this.actions = actions;
	}

	public void fire(final CUnit hero, final TriggerExecutionScope scope) {
		if (condition == null || condition.callback(caster, localStore, castId)) {
			if (scope instanceof CommonTriggerExecutionScope) {
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDUNIT, castId),
						((CommonTriggerExecutionScope) scope).getSpellTargetUnit());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDLOCATION, castId),
						((CommonTriggerExecutionScope) scope).getSpellTargetPoint());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDITEM, castId),
						((CommonTriggerExecutionScope) scope).getSpellTargetItem());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDDESTRUCTABLE, castId),
						((CommonTriggerExecutionScope) scope).getSpellTargetDestructable());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTCASTINGUNIT, castId),
						((CommonTriggerExecutionScope) scope).getSpellAbilityUnit());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTABILITYID, castId),
						((CommonTriggerExecutionScope) scope).getSpellAbilityId());
				this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTABILITY, castId),
						((CommonTriggerExecutionScope) scope).getSpellAbility());
			}
			if (actions != null) {
				for (ABAction action : actions) {
					action.runAction(caster, localStore, castId);
				}
			}
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDUNIT, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDLOCATION, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDITEM, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTTARGETEDDESTRUCTABLE, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTCASTINGUNIT, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTABILITYID, castId));
			this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.EVENTABILITY, castId));
		}
	}

	public void fire(final CPlayer player, final TriggerExecutionScope scope) {
		if (condition == null || condition.callback(caster, localStore, castId)) {
			if (actions != null) {
				for (ABAction action : actions) {
					action.runAction(caster, localStore, castId);
				}
			}
		}
	}

}
