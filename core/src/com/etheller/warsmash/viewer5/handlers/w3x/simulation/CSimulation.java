package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CPlayerAPI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfigStartLoc;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CDestructableData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CItemData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class CSimulation implements CPlayerAPI {
	private final CAbilityData abilityData;
	private final CUnitData unitData;
	private final CDestructableData destructableData;
	private final CItemData itemData;
	private final List<CUnit> units;
	private final List<CUnit> newUnits;
	private final List<CUnit> removedUnits;
	private final List<CDestructable> destructables;
	private final List<CItem> items;
	private final List<CPlayer> players;
	private final List<CAttackProjectile> projectiles;
	private final List<CAttackProjectile> newProjectiles;
	private final HandleIdAllocator handleIdAllocator;
	private transient final SimulationRenderController simulationRenderController;
	private int gameTurnTick = 0;
	private final PathingGrid pathingGrid;
	private final CWorldCollision worldCollision;
	private final CPathfindingProcessor[] pathfindingProcessors;
	private final CGameplayConstants gameplayConstants;
	private final Random seededRandom;
	private float currentGameDayTimeElapsed;
	private final Map<Integer, CUnit> handleIdToUnit = new HashMap<>();
	private final Map<Integer, CDestructable> handleIdToDestructable = new HashMap<>();
	private final Map<Integer, CItem> handleIdToItem = new HashMap<>();
	private final Map<Integer, CAbility> handleIdToAbility = new HashMap<>();
	private final LinkedList<CTimer> activeTimers = new LinkedList<>();
	private final List<CTimer> addedTimers = new ArrayList<>();
	private final List<CTimer> removedTimers = new ArrayList<>();
	private transient CommandErrorListener commandErrorListener;
	private final CRegionManager regionManager;
	private final List<TimeOfDayVariableEvent> timeOfDayVariableEvents = new ArrayList<>();
	private boolean timeOfDaySuspended;
	private boolean daytime;

	public CSimulation(final War3MapConfig config, final DataTable miscData, final MutableObjectData parsedUnitData,
			final MutableObjectData parsedItemData, final MutableObjectData parsedDestructableData,
			final MutableObjectData parsedAbilityData, final SimulationRenderController simulationRenderController,
			final PathingGrid pathingGrid, final Rectangle entireMapBounds, final Random seededRandom,
			final CommandErrorListener commandErrorListener) {
		this.gameplayConstants = new CGameplayConstants(miscData);
		this.simulationRenderController = simulationRenderController;
		this.pathingGrid = pathingGrid;
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.unitData = new CUnitData(this.gameplayConstants, parsedUnitData, this.abilityData,
				this.simulationRenderController);
		this.destructableData = new CDestructableData(parsedDestructableData, simulationRenderController);
		this.itemData = new CItemData(parsedItemData);
		this.units = new ArrayList<>();
		this.newUnits = new ArrayList<>();
		this.removedUnits = new ArrayList<>();
		this.destructables = new ArrayList<>();
		this.items = new ArrayList<>();
		this.projectiles = new ArrayList<>();
		this.newProjectiles = new ArrayList<>();
		this.handleIdAllocator = new HandleIdAllocator();
		this.worldCollision = new CWorldCollision(entireMapBounds, this.gameplayConstants.getMaxCollisionRadius());
		this.regionManager = new CRegionManager(entireMapBounds, pathingGrid);
		this.pathfindingProcessors = new CPathfindingProcessor[WarsmashConstants.MAX_PLAYERS];
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			this.pathfindingProcessors[i] = new CPathfindingProcessor(pathingGrid, this.worldCollision);
		}
		this.seededRandom = seededRandom;
		this.players = new ArrayList<>();
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CBasePlayer configPlayer = config.getPlayer(i);
			final War3MapConfigStartLoc startLoc = config.getStartLoc(configPlayer.getStartLocationIndex());
			final CRace defaultRace = CRace.ORC;
			final CPlayer newPlayer = new CPlayer(defaultRace, new float[] { startLoc.getX(), startLoc.getY() },
					configPlayer);
			if (WarsmashConstants.LOCAL_TEMP_TEST_ALL_PLAYERS_PLAYING) {
				if (i < config.getPlayerCount()) {
					newPlayer.setSlotState(CPlayerSlotState.PLAYING);
				}
			}
			this.players.add(newPlayer);
		}
		this.players.get(this.players.size() - 4).setName(miscData.getLocalizedString("WESTRING_PLAYER_NA"));
		this.players.get(this.players.size() - 3).setName(miscData.getLocalizedString("WESTRING_PLAYER_NV"));
		this.players.get(this.players.size() - 2).setName(miscData.getLocalizedString("WESTRING_PLAYER_NE"));
		final CPlayer neutralPassive = this.players.get(this.players.size() - 1);
		neutralPassive.setName(miscData.getLocalizedString("WESTRING_PLAYER_NP"));

		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CPlayer cPlayer = this.players.get(i);
			cPlayer.setAlliance(neutralPassive, CAllianceType.PASSIVE, true);
			neutralPassive.setAlliance(cPlayer, CAllianceType.PASSIVE, true);
		}

		this.commandErrorListener = commandErrorListener;

	}

	public CUnitData getUnitData() {
		return this.unitData;
	}

	public CAbilityData getAbilityData() {
		return this.abilityData;
	}

	public CDestructableData getDestructableData() {
		return this.destructableData;
	}

	public CItemData getItemData() {
		return this.itemData;
	}

	public List<CUnit> getUnits() {
		return this.units;
	}

	public List<CDestructable> getDestructables() {
		return this.destructables;
	}

	public void registerTimer(final CTimer timer) {
		this.addedTimers.add(timer);
	}

	public void unregisterTimer(final CTimer timer) {
		this.removedTimers.add(timer);
	}

	private void internalRegisterTimer(final CTimer timer) {
		final ListIterator<CTimer> listIterator = this.activeTimers.listIterator();
		while (listIterator.hasNext()) {
			final CTimer nextTimer = listIterator.next();
			if (nextTimer.getEngineFireTick() > timer.getEngineFireTick()) {
				listIterator.previous();
				listIterator.add(timer);
				return;
			}
		}
		this.activeTimers.addLast(timer);
	}

	public void internalUnregisterTimer(final CTimer timer) {
		this.activeTimers.remove(timer);
	}

	public CUnit internalCreateUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing, final BufferedImage buildingPathingPixelMap,
			final RemovablePathingMapInstance pathingInstance) {
		final CUnit unit = this.unitData.create(this, playerIndex, typeId, x, y, facing, buildingPathingPixelMap,
				this.handleIdAllocator, pathingInstance);
		this.newUnits.add(unit);
		this.handleIdToUnit.put(unit.getHandleId(), unit);
		this.worldCollision.addUnit(unit);
		if (unit.getHeroData() != null) {
			this.heroCreateEvent(unit);
		}
		return unit;
	}

	public CDestructable internalCreateDestructable(final War3ID typeId, final float x, final float y,
			final RemovablePathingMapInstance pathingInstance, final RemovablePathingMapInstance pathingInstanceDeath) {
		final CDestructable dest = this.destructableData.create(this, typeId, x, y, this.handleIdAllocator,
				pathingInstance, pathingInstanceDeath);
		this.handleIdToDestructable.put(dest.getHandleId(), dest);
		this.worldCollision.addDestructable(dest);
		this.destructables.add(dest);
		dest.setBlighted(dest.checkIsOnBlight(this));
		return dest;
	}

	public CItem internalCreateItem(final War3ID alias, final float unitX, final float unitY) {
		final CItem item = this.itemData.create(this, alias, unitX, unitY, this.handleIdAllocator.createId());
		this.handleIdToItem.put(item.getHandleId(), item);
		this.items.add(item);
		return item;
	}

	public CItem createItem(final War3ID alias, final float unitX, final float unitY) {
		return this.simulationRenderController.createItem(this, alias, unitX, unitY);
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		return this.simulationRenderController.createUnit(this, typeId, playerIndex, x, y, facing);
	}

	public CDestructable createDestructable(final War3ID typeId, final float x, final float y, final float facing,
			final float scale, final int variation) {
		return this.simulationRenderController.createDestructable(typeId, x, y, facing, scale, variation);
	}

	public CDestructable createDestructableZ(final War3ID typeId, final float x, final float y, final float z,
			final float facing, final float scale, final int variation) {
		return this.simulationRenderController.createDestructableZ(typeId, x, y, z, facing, scale, variation);
	}

	public CUnit getUnit(final int handleId) {
		return this.handleIdToUnit.get(handleId);
	}

	public CAbility getAbility(final int handleId) {
		return this.handleIdToAbility.get(handleId);
	}

	protected void onAbilityAddedToUnit(final CUnit unit, final CAbility ability) {
		this.handleIdToAbility.put(ability.getHandleId(), ability);
	}

	protected void onAbilityRemovedFromUnit(final CUnit unit, final CAbility ability) {
		this.handleIdToAbility.remove(ability.getHandleId());
	}

	public CAttackProjectile createProjectile(final CUnit source, final float launchX, final float launchY,
			final float launchFacing, final CUnitAttackMissile attack, final AbilityTarget target, final float damage,
			final int bounceIndex, final CUnitAttackListener attackListener) {
		final CAttackProjectile projectile = this.simulationRenderController.createAttackProjectile(this, launchX,
				launchY, launchFacing, source, attack, target, damage, bounceIndex, attackListener);
		this.newProjectiles.add(projectile);
		return projectile;
	}

	public void createInstantAttackEffect(final CUnit source, final CUnitAttackInstant attack, final CWidget target) {
		this.simulationRenderController.createInstantAttackEffect(this, source, attack, target);
	}

	public PathingGrid getPathingGrid() {
		return this.pathingGrid;
	}

	public void findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
			final boolean allowSmoothing, final CBehaviorMove queueItem) {
		final int playerIndex = queueItem.getUnit().getPlayerIndex();
		this.pathfindingProcessors[playerIndex].findNaiveSlowPath(ignoreIntersectionsWithThisUnit,
				ignoreIntersectionsWithThisSecondUnit, startX, startY, goal, movementType, collisionSize,
				allowSmoothing, queueItem);
	}

	public void removeFromPathfindingQueue(final CBehaviorMove behaviorMove) {
		final int playerIndex = behaviorMove.getUnit().getPlayerIndex();
		this.pathfindingProcessors[playerIndex].removeFromPathfindingQueue(behaviorMove);
	}

	public void update() {
		final Iterator<CUnit> unitIterator = this.units.iterator();
		while (unitIterator.hasNext()) {
			final CUnit unit = unitIterator.next();
			if (unit.update(this)) {
				unitIterator.remove();
				for (final CAbility ability : unit.getAbilities()) {
					this.handleIdToAbility.remove(ability.getHandleId());
				}
				this.handleIdToUnit.remove(unit.getHandleId());
				this.simulationRenderController.removeUnit(unit);
				this.getPlayerHeroes(unit.getPlayerIndex()).remove(unit);
				unit.onRemove(this);
			}
		}
		this.finishAddingNewUnits();
		final Iterator<CAttackProjectile> projectileIterator = this.projectiles.iterator();
		while (projectileIterator.hasNext()) {
			final CAttackProjectile projectile = projectileIterator.next();
			if (projectile.update(this)) {
				projectileIterator.remove();
			}
		}
		this.projectiles.addAll(this.newProjectiles);
		this.newProjectiles.clear();
		for (final CPathfindingProcessor pathfindingProcessor : this.pathfindingProcessors) {
			pathfindingProcessor.update(this);
		}
		this.gameTurnTick++;
		final float timeOfDayBefore = this.getGameTimeOfDay();
		if (!this.timeOfDaySuspended) {
			this.currentGameDayTimeElapsed = (this.currentGameDayTimeElapsed + WarsmashConstants.SIMULATION_STEP_TIME)
					% this.gameplayConstants.getGameDayLength();
		}
		final float timeOfDayAfter = this.getGameTimeOfDay();
		this.daytime = (timeOfDayAfter >= this.gameplayConstants.getDawnTimeGameHours())
				&& (timeOfDayAfter < this.gameplayConstants.getDuskTimeGameHours());
		for (final CTimer timer : this.addedTimers) {
			internalRegisterTimer(timer);
		}
		this.addedTimers.clear();
		while (!this.activeTimers.isEmpty() && (this.activeTimers.peek().getEngineFireTick() <= this.gameTurnTick)) {
			this.activeTimers.pop().fire(this);
		}
		for (final CTimer timer : this.removedTimers) {
			internalUnregisterTimer(timer);
		}
		this.removedTimers.clear();
		for (final TimeOfDayVariableEvent timeOfDayEvent : this.timeOfDayVariableEvents) {
			if (!timeOfDayEvent.isMatching(timeOfDayBefore) && timeOfDayEvent.isMatching(timeOfDayAfter)) {
				timeOfDayEvent.fire();
			}
		}

	}

	public void removeUnit(final CUnit unit) {
		this.removedUnits.add(unit);
	}

	private void finishAddingNewUnits() {
		this.units.addAll(this.newUnits);
		this.newUnits.clear();
		for (final CUnit unit : this.removedUnits) {
			this.units.remove(unit);
			for (final CAbility ability : unit.getAbilities()) {
				this.handleIdToAbility.remove(ability.getHandleId());
			}
			this.handleIdToUnit.remove(unit.getHandleId());
			this.simulationRenderController.removeUnit(unit);
			this.getPlayerHeroes(unit.getPlayerIndex()).remove(unit);
			unit.onRemove(this);
		}
		this.removedUnits.clear();
	}

	public float getGameTimeOfDay() {
		return (this.currentGameDayTimeElapsed / this.gameplayConstants.getGameDayLength())
				* this.gameplayConstants.getGameDayHours();
	}

	public void setGameTimeOfDay(final float value) {
		final float elapsed = value / this.gameplayConstants.getGameDayHours();
		this.currentGameDayTimeElapsed = elapsed * this.gameplayConstants.getGameDayLength();
	}

	public int getGameTurnTick() {
		return this.gameTurnTick;
	}

	public CWorldCollision getWorldCollision() {
		return this.worldCollision;
	}

	public CRegionManager getRegionManager() {
		return this.regionManager;
	}

	public CGameplayConstants getGameplayConstants() {
		return this.gameplayConstants;
	}

	public Random getSeededRandom() {
		return this.seededRandom;
	}

	public void unitDamageEvent(final CUnit damagedUnit, final String weaponSound, final String armorType) {
		this.simulationRenderController.spawnDamageSound(damagedUnit, weaponSound, armorType);
	}

	public void destructableDamageEvent(final CDestructable damagedDestructable, final String weaponSound,
			final String armorType) {
		this.simulationRenderController.spawnDamageSound(damagedDestructable, weaponSound, armorType);
	}

	public void itemDamageEvent(final CItem damageItem, final String weaponSound, final String armorType) {
		this.simulationRenderController.spawnDamageSound(damageItem, weaponSound, armorType);
	}

	public void unitConstructedEvent(final CUnit constructingUnit, final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitConstructionSound(constructingUnit, constructedStructure);
	}

	@Override
	public CPlayer getPlayer(final int index) {
		return this.players.get(index);
	}

	public CommandErrorListener getCommandErrorListener(final int playerIndex) {
		return this.commandErrorListener;
	}

	public void unitConstructFinishEvent(final CUnit constructedStructure) {
		final CPlayer player = this.getPlayer(constructedStructure.getPlayerIndex());
		player.addTechtreeUnlocked(constructedStructure.getTypeId());
		this.simulationRenderController.spawnUnitConstructionFinishSound(constructedStructure);
	}

	public void createBuildingDeathEffect(final CUnit cUnit) {
		this.simulationRenderController.spawnBuildingDeathEffect(cUnit);
	}

	public HandleIdAllocator getHandleIdAllocator() {
		return this.handleIdAllocator;
	}

	public void unitTrainedEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}

	public void heroReviveEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.heroRevived(trainedUnit);
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}

	public void unitRepositioned(final CUnit cUnit) {
		this.simulationRenderController.unitRepositioned(cUnit);
	}

	public void unitGainResourceEvent(final CUnit unit, final ResourceType resourceType, final int amount) {
		this.simulationRenderController.spawnGainResourceTextTag(unit, resourceType, amount);
	}

	public void unitGainLevelEvent(final CUnit unit) {
		this.players.get(unit.getPlayerIndex()).fireHeroLevelEvents(unit);
		this.simulationRenderController.spawnGainLevelEffect(unit);
	}

	public void heroCreateEvent(final CUnit hero) {
		this.getPlayerHeroes(hero.getPlayerIndex()).add(hero);
	}

	public void unitPickUpItemEvent(final CUnit cUnit, final CItem item) {
		this.simulationRenderController.spawnUIUnitGetItemSound(cUnit, item);
	}

	public void unitDropItemEvent(final CUnit cUnit, final CItem item) {
		this.simulationRenderController.spawnUIUnitDropItemSound(cUnit, item);
	}

	public List<CUnit> getPlayerHeroes(final int playerIndex) {
		return this.players.get(playerIndex).getHeroes();
	}

	public void unitsLoaded() {
		// called on startup after the system loads the map's units layer, but not any
		// custom scripts yet
		this.finishAddingNewUnits();
		for (final CUnit unit : this.units) {
			final CPlayer player = this.players.get(unit.getPlayerIndex());
			player.setUnitFoodUsed(unit, unit.getUnitType().getFoodUsed());
			player.setUnitFoodMade(unit, unit.getUnitType().getFoodMade());
			player.addTechtreeUnlocked(unit.getTypeId());
		}
	}

	public CWidget getWidget(final int handleId) {
		final CUnit unit = this.handleIdToUnit.get(handleId);
		if (unit != null) {
			return unit;
		}
		final CDestructable destructable = this.handleIdToDestructable.get(handleId);
		if (destructable != null) {
			return destructable;
		}
		final CItem item = this.handleIdToItem.get(handleId);
		if (item != null) {
			return item;
		}
		return null;
	}

	public void createEffectOnUnit(final CUnit unit, final String effectPath) {
		this.simulationRenderController.spawnEffectOnUnit(unit, effectPath);
	}

	public void createSpellEffectOnUnit(final CUnit unit, final War3ID alias) {
		this.simulationRenderController.spawnSpellEffectOnUnit(unit, alias);
	}

	public void unitSoundEffectEvent(final CUnit caster, final War3ID alias) {
		this.simulationRenderController.spawnAbilitySoundEffect(caster, alias);
	}

	public void unitPreferredSelectionReplacement(final CUnit unit, final CUnit newUnit) {
		this.simulationRenderController.unitPreferredSelectionReplacement(unit, newUnit);
	}

	public RemovableTriggerEvent registerTimeOfDayEvent(final GlobalScope globalScope, final Trigger trigger,
			final CLimitOp opcode, final double doubleValue) {
		final TimeOfDayVariableEvent timeOfDayVariableEvent = new TimeOfDayVariableEvent(trigger, opcode, doubleValue,
				globalScope);
		this.timeOfDayVariableEvents.add(timeOfDayVariableEvent);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
				CSimulation.this.timeOfDayVariableEvents.remove(timeOfDayVariableEvent);
			}
		};
	}

	public RemovableTriggerEvent registerGameEvent(final GlobalScope globalScope, final Trigger trigger,
			final JassGameEventsWar3 gameEvent) {
		System.err.println("Game event not yet implemented: " + gameEvent);
		return new RemovableTriggerEvent() {
			@Override
			public void remove() {
			}
		};
	}

	public void heroDeathEvent(final CUnit cUnit) {
		this.simulationRenderController.heroDeathEvent(cUnit);
	}

	public void heroDissipateEvent(final CUnit cUnit) {
		this.getPlayer(cUnit.getPlayerIndex()).onHeroDeath(cUnit);
	}

	public void removeItem(final CItem cItem) {
		cItem.setHidden(true); // TODO fix
		cItem.setLife(this, 0);
	}

	private static final class TimeOfDayVariableEvent extends VariableEvent {
		private final GlobalScope globalScope;

		public TimeOfDayVariableEvent(final Trigger trigger, final CLimitOp limitOp, final double doubleValue,
				final GlobalScope globalScope) {
			super(trigger, limitOp, doubleValue);
			this.globalScope = globalScope;
		}

		public void fire() {
			this.fire(this.globalScope);
		}
	}

	public RemovableTriggerEvent registerEventPlayerDefeat(final GlobalScope globalScope, final Trigger whichTrigger,
			final CPlayerJass whichPlayer) {
		if (true) {
			throw new UnsupportedOperationException("registerEventPlayerDefeat is NYI");
		}
		return RemovableTriggerEvent.DO_NOTHING;
	}

	public RemovableTriggerEvent registerEventPlayerVictory(final GlobalScope globalScope, final Trigger whichTrigger,
			final CPlayerJass whichPlayer) {
		if (true) {
			throw new UnsupportedOperationException("registerEventPlayerVictory is NYI");
		}
		return RemovableTriggerEvent.DO_NOTHING;
	}

	public void setAllItemTypeSlots(final int slots) {
		System.err.println(
				"Ignoring call to set all item type slots to: " + slots + " (marketplace is not yet implemented)");
	}

	public void setAllUnitTypeSlots(final int slots) {
		System.err.println(
				"Ignoring call to set all unit type slots to: " + slots + " (marketplace is not yet implemented)");
	}

	public void setTimeOfDaySuspended(final boolean flag) {
		this.timeOfDaySuspended = flag;

	}

	public boolean isDay() {
		return this.daytime;
	}

	public boolean isNight() {
		return !this.daytime;
	}

	public void updateIsPlaying(final War3MapConfig mapConfig) {
		for (int i = 0; i < mapConfig.getPlayerCount(); i++) {
			this.players.get(i).setSlotState(CPlayerSlotState.PLAYING);
		}
	}

}
