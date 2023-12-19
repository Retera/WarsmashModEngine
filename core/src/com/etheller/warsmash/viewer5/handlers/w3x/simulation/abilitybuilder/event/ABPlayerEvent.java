package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event;

import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class ABPlayerEvent extends CPlayerEvent {

	private CSimulation game;
	private CUnit caster;
	private Map<String, Object> localStore;
	private int castId;

	private ABCondition condition;
	private List<ABAction> actions;

	public ABPlayerEvent(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId, CPlayerJass player,
			JassGameEventsWar3 eventType, ABCondition condition, List<ABAction> actions) {
		super(game.getGlobalScope(), player, null, eventType, null);
		this.game = game;
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;

		this.condition = condition;
		this.actions = actions;
	}

	public void fire(final CUnit hero, final TriggerExecutionScope scope) {
		if (condition == null || condition.evaluate(game, caster, localStore, castId)) {
			if (scope instanceof CommonTriggerExecutionScope) {
				this.localStore.put(ABLocalStoreKeys.EVENTTARGETEDUNIT + castId,
						((CommonTriggerExecutionScope) scope).getSpellTargetUnit());
				this.localStore.put(ABLocalStoreKeys.EVENTTARGETEDLOCATION + castId,
						((CommonTriggerExecutionScope) scope).getSpellTargetPoint());
				this.localStore.put(ABLocalStoreKeys.EVENTTARGETEDITEM + castId,
						((CommonTriggerExecutionScope) scope).getSpellTargetItem());
				this.localStore.put(ABLocalStoreKeys.EVENTTARGETEDDESTRUCTABLE + castId,
						((CommonTriggerExecutionScope) scope).getSpellTargetDestructable());
				this.localStore.put(ABLocalStoreKeys.EVENTCASTINGUNIT + castId,
						((CommonTriggerExecutionScope) scope).getSpellAbilityUnit());
				this.localStore.put(ABLocalStoreKeys.EVENTABILITYID + castId,
						((CommonTriggerExecutionScope) scope).getSpellAbilityId());
				this.localStore.put(ABLocalStoreKeys.EVENTABILITY + castId,
						((CommonTriggerExecutionScope) scope).getSpellAbility());
			}
			if (actions != null) {
				for (ABAction action : actions) {
					action.runAction(game, caster, localStore, castId);
				}
			}
			this.localStore.remove(ABLocalStoreKeys.EVENTTARGETEDUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTTARGETEDLOCATION + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTTARGETEDITEM + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTTARGETEDDESTRUCTABLE + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTCASTINGUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTABILITYID + castId);
			this.localStore.remove(ABLocalStoreKeys.EVENTABILITY + castId);
		}
	}

	public void fire(final CPlayer player, final TriggerExecutionScope scope) {
		if (condition == null || condition.evaluate(game, caster, localStore, castId)) {
			if (actions != null) {
				for (ABAction action : actions) {
					action.runAction(game, caster, localStore, castId);
				}
			}
		}
	}

}
