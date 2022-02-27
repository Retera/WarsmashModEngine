package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener.CPlayerStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CPlayer extends CBasePlayer {
	private final CRace race;
	private final float[] startLocation;
	private int gold = 500;
	private int lumber = 150;
	private int foodCap;
	private int foodUsed;
	private final Map<War3ID, Integer> rawcodeToTechtreeUnlocked = new HashMap<>();
	private final List<CUnit> heroes = new ArrayList<>();
	private final EnumMap<JassGameEventsWar3, List<CPlayerEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class);
	private float accumulatedLumberCost = 0.0f;
	private float accumulatedGoldCost = 0.0f;

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CPlayerStateNotifier stateNotifier = new CPlayerStateNotifier();

	public CPlayer(final CRace race, final float[] startLocation, final CBasePlayer configPlayer) {
		super(configPlayer);
		this.race = race;
		this.startLocation = startLocation;
	}

	public void setAlliance(final CPlayer other, final CAllianceType alliance, final boolean flag) {
		setAlliance(other.getId(), alliance, flag);
	}

	public CRace getRace() {
		return this.race;
	}

	public int getGold() {
		return this.gold;
	}

	public int getLumber() {
		return this.lumber;
	}

	public int getFoodCap() {
		return this.foodCap;
	}

	public int getFoodUsed() {
		return this.foodUsed;
	}

	public float[] getStartLocation() {
		return this.startLocation;
	}

	public void setGold(final int gold) {
		this.gold = gold;
		this.stateNotifier.goldChanged();
	}

	public void setLumber(final int lumber) {
		this.lumber = lumber;
		this.stateNotifier.lumberChanged();
	}

	public void setFoodCap(final int foodCap) {
		this.foodCap = foodCap;
		this.stateNotifier.foodChanged();
	}

	public void setFoodUsed(final int foodUsed) {
		this.foodUsed = foodUsed;
		this.stateNotifier.foodChanged();
	}

	public int getTechtreeUnlocked(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			return 0;
		}
		return techtreeUnlocked;
	}

	public void addTechtreeUnlocked(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, 1);
		}
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked + 1);
		}
	}

	public void removeTechtreeUnlocked(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, -1);
		}
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked - 1);
		}
	}

	public void addStateListener(final CPlayerStateListener listener) {
		this.stateNotifier.subscribe(listener);
	}

	public void removeStateListener(final CPlayerStateListener listener) {
		this.stateNotifier.unsubscribe(listener);
	}

	public void chargeFor(final CUnitType unitType) {
		this.lumber -= unitType.getLumberCost();
		this.gold -= unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public boolean charge(final int gold, final int lumber) {
		if ((this.lumber >= lumber) && (this.gold >= gold)) {
			this.lumber -= lumber;
			this.gold -= gold;
			this.stateNotifier.lumberChanged();
			this.stateNotifier.goldChanged();
			return true;
		}
		return false;
	}

	public boolean charge(final float gold, final float lumber){
		this.accumulatedLumberCost += lumber;
		this.accumulatedGoldCost += gold;
		int newGoldCost = (int)accumulatedGoldCost;
		int newLumberCost = (int)accumulatedLumberCost;
		if(this.charge(newGoldCost,newLumberCost)) {
			this.accumulatedLumberCost -= newLumberCost;
			this.accumulatedGoldCost -= newGoldCost;
			return true;
		}
		return false;
	}

	public void refundFor(final CUnitType unitType) {
		this.lumber += unitType.getLumberCost();
		this.gold += unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void refund(final int gold, final int lumber) {
		this.gold += gold;
		this.lumber += lumber;
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void setUnitFoodUsed(final CUnit unit, final int foodUsed) {
		this.foodUsed += unit.setFoodUsed(foodUsed);
		this.stateNotifier.foodChanged();
	}

	public void setUnitFoodMade(final CUnit unit, final int foodMade) {
		this.foodCap += unit.setFoodMade(foodMade);
		this.stateNotifier.foodChanged();
	}

	public void onHeroDeath(final CUnit hero) {
		this.stateNotifier.heroDeath();
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope.playerHeroRevivableScope(hero),
				JassGameEventsWar3.EVENT_PLAYER_HERO_REVIVABLE);
	}

	private void firePlayerUnitEvents(final CUnit hero, final CommonTriggerExecutionScope eventScope,
			final JassGameEventsWar3 eventType) {
		final List<CPlayerEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(hero, eventScope);
			}
		}
	}

	public List<CUnit> getHeroes() {
		return this.heroes;
	}

	public void fireHeroLevelEvents(final CUnit hero) {
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope.playerHeroRevivableScope(hero),
				JassGameEventsWar3.EVENT_PLAYER_HERO_LEVEL);
	}

	private List<CPlayerEvent> getOrCreateEventList(final JassGameEventsWar3 eventType) {
		List<CPlayerEvent> playerEvents = this.eventTypeToEvents.get(eventType);
		if (playerEvents == null) {
			playerEvents = new ArrayList<>();
			this.eventTypeToEvents.put(eventType, playerEvents);
		}
		return playerEvents;
	}

	private List<CPlayerEvent> getEventList(final JassGameEventsWar3 eventType) {
		return this.eventTypeToEvents.get(eventType);
	}

	@Override
	public RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType) {
		final CPlayerEvent playerEvent = new CPlayerEvent(globalScope, this, whichTrigger, eventType);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	@Override
	public void removeEvent(final CPlayerEvent playerEvent) {
		final List<CPlayerEvent> eventList = getEventList(playerEvent.getEventType());
		if (eventList != null) {
			eventList.remove(playerEvent);
		}
	}
}
