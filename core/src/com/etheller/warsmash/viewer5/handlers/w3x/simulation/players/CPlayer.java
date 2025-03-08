package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener.CPlayerStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
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
	private final List<CFogModifier> fogModifiers = new ArrayList<>();
	private final List<CFogModifier> fogModifiersAfterUnits = new ArrayList<>();
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

	private final Set<War3ID> disabledAbilities = new HashSet<>();

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CPlayerStateNotifier stateNotifier = new CPlayerStateNotifier();
	private float handicapXP = 1.0f;
	private float handicap = 0.9f;
	private final CPlayerFogOfWar fogOfWar;

	public CPlayer(final CRace race, final float[] startLocation, final CBasePlayer configPlayer,
			final CPlayerFogOfWar fogOfWar) {
		super(configPlayer);
		this.race = race;
		this.startLocation = startLocation;
		// Below: 32x32 cells to find the number of 128x128 cells
		this.fogOfWar = fogOfWar;
	}

	public CPlayerFogOfWar getFogOfWar() {
		return this.fogOfWar;
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

	public void addGold(final int gold) {
		setGold(getGold() + gold);
	}

	public void setLumber(final int lumber) {
		this.lumber = lumber;
		this.stateNotifier.lumberChanged();
	}

	public void addLumber(final int lumber) {
		setLumber(getLumber() + lumber);
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

	public void addTechtreeUnlocked(CSimulation simulation, final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, 1);
		}
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked + 1);
		}
		fireRequirementUpdateForAbilities(simulation, false);
	}

	public void setTechtreeUnlocked(CSimulation simulation, final War3ID rawcode, final int setToLevel) {
		final int prev = getTechtreeUnlocked(rawcode);
		this.rawcodeToTechtreeUnlocked.put(rawcode, setToLevel);
		fireRequirementUpdateForAbilities(simulation, prev > setToLevel);
	}

	public void removeTechtreeUnlocked(CSimulation simulation, final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			this.rawcodeToTechtreeUnlocked.put(rawcode, -1);
		}
		else {
			this.rawcodeToTechtreeUnlocked.put(rawcode, techtreeUnlocked - 1);
		}
		fireRequirementUpdateForAbilities(simulation, true);
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

	public void chargeFor(final CUpgradeType upgradeType) {
		final int unlockCount = getTechtreeUnlocked(upgradeType.getTypeId());
		this.lumber -= upgradeType.getLumberCost(unlockCount);
		this.gold -= upgradeType.getGoldCost(unlockCount);
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

	public void refundFor(final CUpgradeType upgradeType) {
		final int unlockCount = getTechtreeUnlocked(upgradeType.getTypeId());
		this.lumber += upgradeType.getLumberCost(unlockCount);
		this.gold += upgradeType.getGoldCost(unlockCount);
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

	public void firePlayerEvents(final CommonTriggerExecutionScope.PlayerEventScopeBuilder eventScopeBuilder,
			final JassGameEventsWar3 eventType) {
		final List<CPlayerEvent> eventList = getEventList(eventType);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(this, eventScopeBuilder.create(eventType, event.getTrigger(), this));
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
				final CUnitType unitType = game.getUnitData().getUnitType(entry.getKey());
				if ((unitType != null) && unitType.isHero()) {
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

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderNoTarget orderNoTarget) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_ORDER);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderScope(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_ORDER,
								event.getTrigger(), unit, orderNoTarget.getOrderId()));
			}
		}
	}

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderTargetPoint order) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_POINT_ORDER);
		if (eventList != null) {
			final AbilityPointTarget target = order.getTarget(game);
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderPointScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_POINT_ORDER, event.getTrigger(), unit,
								order.getOrderId(), target.x, target.y));
			}
		}
	}

	public void fireOrderEvents(final CUnit unit, final CSimulation game, final COrderTargetWidget order) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_TARGET_ORDER);
		if (eventList != null) {
			final CWidget target = order.getTarget(game);
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitOrderTargetScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_ISSUED_TARGET_ORDER, event.getTrigger(), unit,
								order.getOrderId(), target));
			}
		}
	}

	public void fireConstructFinishEvents(final CUnit unit, final CSimulation game, final CUnit constructingUnit) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_CONSTRUCT_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit,
						CommonTriggerExecutionScope.unitConstructFinishScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_CONSTRUCT_FINISH, event.getTrigger(), unit,
								constructingUnit));
			}
		}
	}

	public void fireTrainFinishEvents(final CUnit unit, final CSimulation game, final CUnit trainedUnit) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_TRAIN_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitTrainFinishScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_TRAIN_FINISH, event.getTrigger(), unit, trainedUnit));
			}
		}
	}

	public void fireResearchFinishEvents(final CUnit unit, final CSimulation game, final War3ID researched) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_RESEARCH_FINISH);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitResearchFinishScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_RESEARCH_FINISH, event.getTrigger(), unit, researched));
			}
		}
	}

	public void firePickUpItemEvents(final CUnit unit, final CItem item, final CSimulation game) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_PICKUP_ITEM);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(unit, CommonTriggerExecutionScope.unitPickupItemScope(
						JassGameEventsWar3.EVENT_PLAYER_UNIT_PICKUP_ITEM, event.getTrigger(), unit, item));
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

	public void addEvent(final CPlayerEvent playerEvent) {
		getOrCreateEventList(playerEvent.getEventType()).add(playerEvent);
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
			this.lumberGathered = value;
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
		if (techtreeMaxAllowed >= 0) {
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

	public void fireAbilityEffectEventsTarget(final CAbility spellAbility, final CUnit spellAbilityUnit,
			final CUnit spellTargetUnit, final War3ID alias) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellTargetUnitScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbility,
								spellAbilityUnit, spellTargetUnit, alias));
			}
		}
	}

	public void fireAbilityEffectEventsPoint(final CAbility spellAbility, final CUnit spellAbilityUnit,
			final AbilityPointTarget abilityPointTarget, final War3ID alias) {
		final List<CPlayerEvent> eventList = getEventList(JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellPointScope(
								JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT, event.getTrigger(), spellAbility,
								spellAbilityUnit, abilityPointTarget, alias));
			}
		}
	}

	public void fireSpellEventsNoTarget(JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit) {
		final List<CPlayerEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellNoTargetScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsPointTarget(JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final AbilityPointTarget abilityPointTarget) {
		final List<CPlayerEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellPointScope(eventId, event.getTrigger(), spellAbility,
								spellAbilityUnit, abilityPointTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsUnitTarget(JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CUnit unitTarget) {
		final List<CPlayerEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetUnitScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, unitTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsItemTarget(JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CItem itemTarget) {
		final List<CPlayerEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetItemScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, itemTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsDestructableTarget(JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CDestructable destTarget) {
		final List<CPlayerEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CPlayerEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetDestructableScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, destTarget, spellAbility.getAlias()));
			}
		}
	}

	public void addTechResearched(final CSimulation simulation, final War3ID techIdRawcodeId, final int levels) {
		final int previousUnlockCount = getTechtreeUnlocked(techIdRawcodeId);
		if (levels != 0) {
			final int setToLevel = previousUnlockCount + levels;
			setTechToLevel(simulation, techIdRawcodeId, setToLevel);
		}
		fireRequirementUpdateForAbilities(simulation, false);
	}

	public void setTechResearched(final CSimulation simulation, final War3ID techIdRawcodeId, final int setToLevel) {
		final int previousUnlockCount = getTechtreeUnlocked(techIdRawcodeId);
		if ((setToLevel > previousUnlockCount) || (setToLevel < previousUnlockCount)) {
			setTechToLevel(simulation, techIdRawcodeId, setToLevel);
		}
		fireRequirementUpdateForAbilities(simulation, false);
	}

	private void setTechToLevel(final CSimulation simulation, final War3ID techIdRawcodeId, final int setToLevel) {
		final int previousLevel = getTechtreeUnlocked(techIdRawcodeId);
		setTechtreeUnlocked(simulation, techIdRawcodeId, setToLevel);
		// terminate in progress upgrades of this kind for player
		final CUpgradeType upgradeType = simulation.getUpgradeData().getType(techIdRawcodeId);
		if (upgradeType != null) {
			for (final CUnit unit : simulation.getUnits()) {
				if (unit.getPlayerIndex() == getId()) {
					if (unit.isBuildQueueActive() && (unit.getBuildQueueTypes()[0] == QueueItemType.RESEARCH)
							&& (unit.getBuildQueue()[0].getValue() == techIdRawcodeId.getValue())) {
						unit.cancelBuildQueueItem(simulation, 0);
					}
					if (unit.getUnitType().getUpgradesUsed().contains(techIdRawcodeId)) {
						if (previousLevel != 0) {
							upgradeType.unapply(simulation, unit, previousLevel);
						}
						if (setToLevel != 0) {
							upgradeType.apply(simulation, unit, setToLevel);
						}
					}
				}
			}
			if (previousLevel != 0) {
				upgradeType.unapply(simulation, getId(), previousLevel);
			}
			if (setToLevel != 0) {
				upgradeType.apply(simulation, getId(), setToLevel);
			}
		}
	}

	public void addFogModifer(final CSimulation game, final CFogModifier fogModifier, boolean afterUnits) {
		if (afterUnits) {
			this.fogModifiersAfterUnits.add(fogModifier);
		}
		else {
			this.fogModifiers.add(fogModifier);
		}
		fogModifier.onAdd(game, this);
	}

	public void removeFogModifer(final CSimulation game, final CFogModifier fogModifier) {
		this.fogModifiers.remove(fogModifier);
		this.fogModifiersAfterUnits.remove(fogModifier);
		fogModifier.onRemove(game, this);
	}

	public void updateFogModifiers(final CSimulation game) {
		for (int i = this.fogModifiers.size() - 1; i >= 0; i--) {
			this.fogModifiers.get(i).update(game, this, game.getPathingGrid(), this.fogOfWar);
		}
	}

	public void updateFogModifiersAfterUnits(final CSimulation game) {
		for (int i = this.fogModifiersAfterUnits.size() - 1; i >= 0; i--) {
			this.fogModifiersAfterUnits.get(i).update(game, this, game.getPathingGrid(), this.fogOfWar);
		}
	}

	public void setAbilityEnabled(final CSimulation simulation, War3ID ability, boolean enabled) {
		if (enabled) {
			this.disabledAbilities.remove(ability);
		}
		else {
			this.disabledAbilities.add(ability);
		}
		fireRequirementUpdateForAbilities(simulation, !enabled);
	}

	public boolean isAbilityDisabled(War3ID abilityId) {
		return this.disabledAbilities.contains(abilityId);
	}

	public void fireRequirementUpdateForAbilities(final CSimulation simulation, final boolean disable) {
		simulation.fireRequirementUpdateForAbilities(this, disable);
	}
}
