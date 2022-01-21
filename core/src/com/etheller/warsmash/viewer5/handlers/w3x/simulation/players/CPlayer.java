package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener.CPlayerStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;

public class CPlayer extends CBasePlayer {
	private final CRace race;
	private final float[] startLocation;
	private int gold;
	private int lumber;
	private int heroTokens;
	private int foodCap;
	private int foodUsed;
	private int foodCapCeiling = 101; // TODO should not have a default, I put 101 to make it stand out
	private final Map<War3ID, Integer> rawcodeToTechtreeUnlocked = new HashMap<>();
	private final Map<War3ID, Integer> rawcodeToTechtreeInProgress = new HashMap<>();
	private final Map<War3ID, Integer> rawcodeToTechtreeMaxAllowed = new HashMap<>();
	private final List<CUnit> heroes = new ArrayList<>();
	private final EnumMap<JassGameEventsWar3, List<CPlayerEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class);

	// Player state data
	private boolean givesBounty = false;
	private boolean alliedVictory = false;
	private int gameResult;
	private int placed;
	private boolean observerOnDeath;
	private boolean observer;
	private boolean unfollowable;
	private int goldUpkeepRate;
	private int lumberUpkeepRate;
	private int goldGathered;
	private int lumberGathered;
	private boolean noCreepSleep;

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CPlayerStateNotifier stateNotifier = new CPlayerStateNotifier();
	private float handicapXP = 1.0f;
	private float handicap = 0.9f;

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

	public int getHeroTokens() {
		return this.heroTokens;
	}

	public int getFoodCap() {
		return this.foodCap;
	}

	public int getFoodUsed() {
		return this.foodUsed;
	}

	public int getFoodCapCeiling() {
		return this.foodCapCeiling;
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

	public void setHeroTokens(final int heroTokens) {
		this.heroTokens = heroTokens;
		this.stateNotifier.heroTokensChanged();
	}

	public void setFoodCap(final int foodCap) {
		this.foodCap = foodCap;
		this.stateNotifier.foodChanged();
	}

	public void setFoodCapCeiling(final int foodCapCeiling) {
		this.foodCapCeiling = foodCapCeiling;
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

	public int getTechtreeInProgress(final War3ID rawcode) {
		final Integer techtreeInProgress = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeInProgress == null) {
			return 0;
		}
		return techtreeInProgress;
	}

	public int getTechtreeUnlockedOrInProgress(final War3ID rawcode) {
		return getTechtreeUnlocked(rawcode) + getTechtreeInProgress(rawcode);
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

	public void addTechtreeInProgress(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeInProgress.put(rawcode, 1);
		}
		else {
			this.rawcodeToTechtreeInProgress.put(rawcode, techtreeUnlocked + 1);
		}
	}

	public void removeTechtreeInProgress(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeInProgress.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeInProgress.put(rawcode, -1);
		}
		else {
			this.rawcodeToTechtreeInProgress.put(rawcode, techtreeUnlocked - 1);
		}
	}

	public void setTechtreeMaxAllowed(final War3ID war3id, final int maximum) {
		this.rawcodeToTechtreeMaxAllowed.put(war3id, maximum);
	}

	public int getTechtreeMaxAllowed(final War3ID war3id) {
		final Integer maxAllowed = this.rawcodeToTechtreeMaxAllowed.get(war3id);
		if (maxAllowed != null) {
			return maxAllowed;
		}
		return -1;
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
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope::playerHeroRevivableScope,
				JassGameEventsWar3.EVENT_PLAYER_HERO_REVIVABLE);
	}

	private void firePlayerUnitEvents(final CUnit hero,
			final CommonTriggerExecutionScope.UnitEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) {
		final List<CPlayerEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(hero, eventScopeBuilder.create(eventType, event.getTrigger(), hero));
			}
		}
	}

	public List<CUnit> getHeroes() {
		return this.heroes;
	}

	public int getHeroCount(final CSimulation game, final boolean includeInProgress) {
		if (!includeInProgress) {
			return this.heroes.size();
		}
		else {
			int heroInProgressCount = 0;
			for (final Map.Entry<War3ID, Integer> entry : this.rawcodeToTechtreeInProgress.entrySet()) {
				if (game.getUnitData().getUnitType(entry.getKey()).isHero()) {
					heroInProgressCount += entry.getValue();
				}
			}
			return this.heroes.size() + heroInProgressCount;
		}
	}

	public void fireHeroLevelEvents(final CUnit hero) {
		firePlayerUnitEvents(hero, CommonTriggerExecutionScope::playerHeroRevivableScope,
				JassGameEventsWar3.EVENT_PLAYER_HERO_LEVEL);
	}

	public void fireUnitDeathEvents(final CUnit dyingUnit, final CUnit killingUnit) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_DEATH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(dyingUnit, CommonTriggerExecutionScope.unitDeathScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_DEATH, event.getTrigger(), dyingUnit, killingUnit));
			}
		}
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
		final CPlayerEvent playerEvent = new CPlayerEvent(globalScope, this, whichTrigger, eventType, null);
		getOrCreateEventList(eventType).add(playerEvent);
		return playerEvent;
	}

	public RemovableTriggerEvent addUnitEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType, final TriggerBooleanExpression filter) {
		final CPlayerEvent playerEvent = new CPlayerEvent(globalScope, this, whichTrigger, eventType, filter);
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

	public void setPlayerState(final CSimulation simulation, final CPlayerState whichPlayerState, final int value) {
		switch (whichPlayerState) {
		case GAME_RESULT:
			this.gameResult = value;
			break;
		case RESOURCE_GOLD:
			setGold(value);
			break;
		case RESOURCE_LUMBER:
			setLumber(value);
			break;
		case RESOURCE_HERO_TOKENS:
			setHeroTokens(value);
			break;
		case RESOURCE_FOOD_CAP:
			setFoodCap(value);
			break;
		case RESOURCE_FOOD_USED:
			setFoodUsed(value);
			break;
		case FOOD_CAP_CEILING:
			setFoodCapCeiling(value);
			break;
		case ALLIED_VICTORY:
			this.alliedVictory = (value != 0);
			break;
		case GIVES_BOUNTY:
			this.givesBounty = (value != 0);
			break;
		case PLACED:
			this.placed = value;
		case OBSERVER_ON_DEATH:
			this.observerOnDeath = (value != 0);
		case OBSERVER:
			this.observer = (value != 0);
		case UNFOLLOWABLE:
			this.unfollowable = (value != 0);
		case GOLD_UPKEEP_RATE:
			this.goldUpkeepRate = value;
			break;
		case LUMBER_UPKEEP_RATE:
			this.lumberUpkeepRate = value;
			break;
		case GOLD_GATHERED:
			this.goldGathered = value;
			break;
		case LUMBER_GATHERED:
			this.goldGathered = value;
			break;
		case NO_CREEP_SLEEP:
			this.noCreepSleep = (value != 0);
			break;
		default:
			break;
		}
	}

	public int getPlayerState(final CSimulation simulation, final CPlayerState whichPlayerState) {
		switch (whichPlayerState) {
		case GAME_RESULT:
			return this.gameResult;
		case RESOURCE_GOLD:
			return getGold();
		case RESOURCE_LUMBER:
			return getLumber();
		case RESOURCE_HERO_TOKENS:
			return getHeroTokens();
		case RESOURCE_FOOD_CAP:
			return getFoodCap();
		case RESOURCE_FOOD_USED:
			return getFoodUsed();
		case FOOD_CAP_CEILING:
			return getFoodCapCeiling();
		case ALLIED_VICTORY:
			return this.alliedVictory ? 1 : 0;
		case GIVES_BOUNTY:
			return this.givesBounty ? 1 : 0;
		case PLACED:
			return this.placed;
		case OBSERVER_ON_DEATH:
			return this.observerOnDeath ? 1 : 0;
		case OBSERVER:
			return this.observer ? 1 : 0;
		case UNFOLLOWABLE:
			return this.unfollowable ? 1 : 0;
		case GOLD_UPKEEP_RATE:
			return this.goldUpkeepRate;
		case LUMBER_UPKEEP_RATE:
			return this.lumberUpkeepRate;
		case GOLD_GATHERED:
			return this.goldGathered;
		case LUMBER_GATHERED:
			return this.lumberGathered;
		case NO_CREEP_SLEEP:
			return this.noCreepSleep ? 1 : 0;
		default:
			return 0;
		}
	}

	public boolean isObserver() {
		return this.observer;
	}

	public boolean isTechtreeAllowedByMax(final War3ID techtree) {
		final int techtreeMaxAllowed = getTechtreeMaxAllowed(techtree);
		if (techtreeMaxAllowed > 0) {
			if (getTechtreeUnlockedOrInProgress(techtree) >= techtreeMaxAllowed) {
				return false;
			}
		}
		return true;
	}

	public void setHandicapXP(final float handicapXP) {
		this.handicapXP = handicapXP;
	}

	public float getHandicapXP() {
		return this.handicapXP;
	}

	public void setHandicap(final float handicap) {
		this.handicap = handicap;
	}

	public float getHandicap() {
		return this.handicap;
	}

	public void fireAbilityEffectEventsTarget(final CUnit spellAbilityUnit, final CUnit spellTargetUnit,
			final War3ID alias) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellEffectTargetScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbilityUnit,
								spellTargetUnit, alias));
			}
		}
	}

	public void fireAbilityEffectEventsPoint(final CUnit spellAbilityUnit, final AbilityPointTarget abilityPointTarget,
			final War3ID alias) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellEffectPointScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbilityUnit,
								abilityPointTarget, alias));
			}
		}
	}
}
