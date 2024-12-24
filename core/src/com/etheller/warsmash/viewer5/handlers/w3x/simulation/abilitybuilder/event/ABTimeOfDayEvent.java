package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation.TimeOfDayEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABTimeOfDayEvent implements TimeOfDayEvent {

	private CSimulation game;
	private CUnit caster;
	private Map<String, Object> localStore;
	private int castId;

	private List<ABAction> actions;
	private float startTime;
	private float endTime;

	private String equalityId;

	public ABTimeOfDayEvent(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId,
			List<ABAction> actions, float startTime, float endTime, String equalityId) {
		super();
		this.game = game;
		this.caster = caster;
		this.localStore = localStore;
		this.castId = castId;
		this.actions = actions;
		this.startTime = startTime;
		this.endTime = endTime;
		this.equalityId = equalityId;
	}

	@Override
	public void fire() {
		if (actions != null) {
			for (ABAction eventAction : actions) {
				eventAction.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public boolean isMatching(double timeOfDayBefore) {
		return startTime <= endTime ? timeOfDayBefore >= startTime && timeOfDayBefore < endTime
				: timeOfDayBefore >= startTime || timeOfDayBefore < endTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ABTimeOfDayEvent other = (ABTimeOfDayEvent) obj;
		if (equalityId != null) {
			return Objects.equals(equalityId, other.equalityId);
		}
		return Objects.equals(actions, other.actions) && castId == other.castId && Objects.equals(caster, other.caster)
				&& Float.floatToIntBits(endTime) == Float.floatToIntBits(other.endTime)
				&& Objects.equals(equalityId, other.equalityId) && Objects.equals(game, other.game)
				&& Objects.equals(localStore, other.localStore)
				&& Float.floatToIntBits(startTime) == Float.floatToIntBits(other.startTime);
	}

	public String toString() {
		return "Event Active from " + this.startTime + " to " + this.endTime + " (EqId: " + this.equalityId + ")";
	}

}
