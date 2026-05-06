package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.TriggerExecutionScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.interpreter.ast.scope.trigger.TriggerBooleanExpression;
import com.etheller.interpreter.ast.scope.variableevent.CLimitOp;
import com.etheller.interpreter.ast.scope.variableevent.VariableEvent;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.TextTag;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityCollisionProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAbilityProjectileListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CCollisionProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CJassProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CPseudoProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CPlayerAPI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfigStartLoc;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CDestructableData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CItemData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUpgradeData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManagerEntry;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.FalseTimeOfDay;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class CSimulation implements CPlayerAPI, CFogMaskSettings {
	private final CAbilityData abilityData;
	private final CUnitData unitData;
	private final CDestructableData destructableData;
	private final CItemData itemData;
	private final CUpgradeData upgradeData;
	private final List<CUnit> units;
	private final List<CUnit> newUnits;
	private final List<CUnit> removedUnits;
	private final List<CDestructable> destructables;
	private final List<CDestructable> removedDestructables;
	private final List<CItem> items;
	private final List<CPlayer> players;
	private final List<CPlayerUnitOrderExecutor> defaultPlayerUnitOrderExecutors;
	private final List<CEffect> projectiles;
	private final List<CEffect> newProjectiles;
	private final HandleIdAllocator handleIdAllocator;
	private transient final SimulationRenderController simulationRenderController;
	private int gameTurnTick = 0;
	private final PathingGrid pathingGrid;
	private final CWorldCollision worldCollision;
	private final CPathfindingProcessor[] pathfindingProcessors;
	private final int mapVersion;
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
	private final List<Trigger> addedOnTickTriggers = new ArrayList<>();
	private final List<Trigger> removedOnTickTriggers = new ArrayList<>();
	private final LinkedList<Trigger> onTickTriggers = new LinkedList<>();
	private transient CommandErrorListener commandErrorListener;
	private final CRegionManager regionManager;
	private final List<TimeOfDayEvent> timeOfDayVariableEvents = new ArrayList<>();
	private final EnumMap<JassGameEventsWar3, List<CGlobalEvent>> eventTypeToEvents = new EnumMap<>(
			JassGameEventsWar3.class);
	private boolean timeOfDaySuspended;
	private Float nextGameTime = null;
	private FalseTimeOfDay falseTimeOfDay = null;
	private boolean daytime;
	private final Set<CDestructable> ownedTreeSet = new HashSet<>();
	private GlobalScope globalScope;
	private boolean fogMaskEnabled = true;
	private boolean fogEnabled = true;
	private final List<Runnable> postUpdateCallbacks = new ArrayList<>();
	private final List<Runnable> runningPostUpdateCallbacks = new ArrayList<>();

	public CSimulation(final War3MapConfig config, final int mapVersion, final DataTable miscData,
			final ObjectData parsedUnitData, final ObjectData parsedItemData, final ObjectData parsedDestructableData,
			final ObjectData parsedAbilityData, final ObjectData parsedUpgradeData,
			final DataTable standardUpgradeEffectMeta, final SimulationRenderController simulationRenderController,
			final PathingGrid pathingGrid, final Rectangle entireMapBounds, final Random seededRandom,
			final CommandErrorListener commandErrorListener) {
		this.mapVersion = mapVersion;
		this.gameplayConstants = new CGameplayConstants(miscData);
		CFogModifier.setConstants(this.gameplayConstants);
		this.simulationRenderController = simulationRenderController;
		this.pathingGrid = pathingGrid;
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.upgradeData = new CUpgradeData(this.gameplayConstants, parsedUpgradeData, standardUpgradeEffectMeta);
		this.unitData = new CUnitData(this.gameplayConstants, parsedUnitData, this.abilityData, this.upgradeData,
				this.simulationRenderController);
		this.unitData.getAllUnitTypesLightweight(); // Quick hack to avoid lag on first cast of Mech Critter
		this.destructableData = new CDestructableData(parsedDestructableData, simulationRenderController);
		this.itemData = new CItemData(parsedItemData);
		this.units = new ArrayList<>();
		this.newUnits = new ArrayList<>();
		this.removedUnits = new ArrayList<>();
		this.destructables = new ArrayList<>();
		this.removedDestructables = new ArrayList<>();
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
		this.defaultPlayerUnitOrderExecutors = new ArrayList<>();
		final List<CPlayer> neutralPlayers = new ArrayList<>();
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CBasePlayer configPlayer = config.getPlayer(i);
			final War3MapConfigStartLoc startLoc = config.getStartLoc(configPlayer.getStartLocationIndex());
			CRace defaultRace = null;
			if (configPlayer.isRacePrefSet(WarsmashConstants.RACE_MANAGER.getRandomRacePreference())) {
				final CRaceManagerEntry raceEntry = WarsmashConstants.RACE_MANAGER
						.get(seededRandom.nextInt(WarsmashConstants.RACE_MANAGER.getEntryCount()));
				defaultRace = WarsmashConstants.RACE_MANAGER.getRace(raceEntry.getRaceId());
			} else {
				for (int j = 0; j < WarsmashConstants.RACE_MANAGER.getEntryCount(); j++) {
					final CRaceManagerEntry entry = WarsmashConstants.RACE_MANAGER.get(j);
					final CRace race = WarsmashConstants.RACE_MANAGER.getRace(entry.getRaceId());
					final CRacePreference racePreference = WarsmashConstants.RACE_MANAGER
							.getRacePreferenceById(entry.getRacePrefId());
					if (configPlayer.isRacePrefSet(racePreference)) {
						defaultRace = race;
						break;
					}
				}
			}
			final CPlayer newPlayer = new CPlayer(defaultRace, new float[] { startLoc.getX(), startLoc.getY() },
					configPlayer, new CPlayerFogOfWar(pathingGrid));
			newPlayer.setAIDifficulty(configPlayer.getAIDifficulty());
			this.players.add(newPlayer);
			this.defaultPlayerUnitOrderExecutors.add(new CPlayerUnitOrderExecutor(this, i));
			if ((newPlayer.getController() == CMapControl.NEUTRAL) && (i < (WarsmashConstants.MAX_PLAYERS - 4))) {
				neutralPlayers.add(newPlayer);
			}
		}
		final CPlayer neutralAggressive = this.players.get(this.players.size() - 4);
		neutralAggressive.setName(miscData.getLocalizedString("WESTRING_PLAYER_NA"));
		neutralAggressive.setPlayerState(this, CPlayerState.GIVES_BOUNTY, 1);
		this.players.get(this.players.size() - 3).setName(miscData.getLocalizedString("WESTRING_PLAYER_NV"));
		this.players.get(this.players.size() - 2).setName(miscData.getLocalizedString("WESTRING_PLAYER_NE"));
		final CPlayer neutralPassive = this.players.get(this.players.size() - 1);
		neutralPassive.setName(miscData.getLocalizedString("WESTRING_PLAYER_NP"));

		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CPlayer cPlayer = this.players.get(i);
			cPlayer.setAlliance(neutralPassive, CAllianceType.PASSIVE, true);
			neutralPassive.setAlliance(cPlayer, CAllianceType.PASSIVE, true);
			for (final CPlayer otherNeutral : neutralPlayers) {
				cPlayer.setAlliance(otherNeutral, CAllianceType.PASSIVE, true);
				otherNeutral.setAlliance(cPlayer, CAllianceType.PASSIVE, true);
			}
		}

		this.commandErrorListener = commandErrorListener;

		final CTimer fogUpdateTimer = new CTimer() {
			@Override
			public void onFire(final CSimulation simulation) {
				updateFogOfWar();
			}
		};
		fogUpdateTimer.setRepeats(true);
		fogUpdateTimer.setTimeoutTime(1.0f);
		fogUpdateTimer.start(this);
	}
	
	public char getTileset() {
		return this.simulationRenderController.getTileset();
	}

	public CUnitData getUnitData() {
		return this.unitData;
	}

	public CUpgradeData getUpgradeData() {
		return this.upgradeData;
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
		if (this.activeTimers.contains(timer)) {
			this.activeTimers.remove(timer);
		}
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
			final float facing, final BufferedImage buildingPathingPixelMap, int editorConfigHitPointPercent,
			int editorConfigManaAmount) {
		final CUnit unit = this.unitData.create(this, playerIndex, typeId, x, y, facing, buildingPathingPixelMap,
				this.handleIdAllocator, editorConfigHitPointPercent, editorConfigManaAmount);
		this.newUnits.add(unit);
		this.handleIdToUnit.put(unit.getHandleId(), unit);
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
		this.worldCollision.addItem(item);
		return item;
	}

	public CItem createItem(final War3ID alias, final float unitX, final float unitY) {
		return this.simulationRenderController.createItem(this, alias, unitX, unitY);
	}

	public void updateItemModel(CItem item) {
		this.simulationRenderController.updateItemModel(item);
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		return this.createUnit(typeId, playerIndex, x, y, facing, false);
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing, final boolean constructing) {
		final CUnit createdUnit = this.simulationRenderController.createUnit(this, typeId, playerIndex, x, y, facing);
		createdUnit.setConstructing(constructing);
		if (createdUnit != null) {
			setupCreatedUnit(createdUnit);
			if (createdUnit.getCollisionRectangle() == null) {
				this.worldCollision.addUnit(createdUnit);
			} // else the unit was probably injected into collision before default behaviors
			createdUnit.performDefaultBehavior(this);
			if (createdUnit.isHero()) {
				heroCreateEvent(createdUnit);
			}
		}
		return createdUnit;
	}

	public CUnit createUnitSimple(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		final CUnit newUnit = createUnit(typeId, playerIndex, x, y, facing);
		if (newUnit != null) {
			final CPlayer player = getPlayer(playerIndex);
			final CUnitType newUnitType = newUnit.getUnitType();
			final int foodUsed = newUnitType.getFoodUsed();
			final int foodMade = newUnitType.getFoodMade();
			newUnit.setFoodUsed(foodUsed);
			newUnit.setFoodMade(foodMade);
			player.setUnitFoodUsed(newUnit, foodUsed);
			if (newUnitType.getFoodMade() != 0) {
				player.setUnitFoodMade(newUnit, foodMade);
			}
			player.addTechtreeUnlocked(this, typeId);
			// nudge unit
			newUnit.setPointAndCheckUnstuck(x, y, this);
			if (!newUnit.isBuilding()) {
				newUnit.getUnitAnimationListener().playAnimation(false, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 1.0f,
						true);
				newUnit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND, SequenceUtils.EMPTY, true);
			}
		}
		return newUnit;
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
			final int bounceIndex, final CUnitAttackListener attackListener, final CUnitAttackSettings settings) {
		final CAttackProjectile projectile = this.simulationRenderController.createAttackProjectile(this, launchX,
				launchY, launchFacing, source, attack, target, damage, bounceIndex, attackListener, settings);
		this.newProjectiles.add(projectile);
		return projectile;
	}

	public CAbilityProjectile createProjectile(final CUnit source, final War3ID spellAlias, final float launchX,
			final float launchY, final float launchFacing, final Float speed, final Boolean homing,
			final AbilityTarget target, final CAbilityProjectileListener projectileListener) {
		final CAbilityProjectile projectile = this.simulationRenderController.createProjectile(this, launchX, launchY,
				launchFacing, speed, homing, source, spellAlias, target, projectileListener);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, projectile, target);
		return projectile;
	}

	public CAbilityProjectile createProjectile(final CUnit source, final CUnitAttackSettings settings,
			final float launchX, final float launchY, final float launchFacing, final AbilityTarget target,
			final CAbilityProjectileListener projectileListener) {
		final CAbilityProjectile projectile = this.simulationRenderController.createProjectile(this, launchX, launchY,
				launchFacing, source, settings, target, projectileListener);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, projectile, target);
		return projectile;
	}

	public CJassProjectile createProjectile(final CUnit source, final War3ID spellAlias, final float launchX,
			final float launchY, final float launchFacing, final Float speed, final Boolean homing,
			final AbilityTarget target) {
		final CJassProjectile projectile = this.simulationRenderController.createJassProjectile(this, launchX, launchY,
				launchFacing, speed, homing, source, spellAlias, target);
		this.newProjectiles.add(projectile);
		return projectile;
	}

	public CCollisionProjectile createCollisionProjectile(final CUnit source, final War3ID spellAlias,
			final float launchX, final float launchY, final float launchFacing, final Float speed, final Boolean homing,
			final AbilityTarget target, final int maxHits, final int hitsPerTarget, final float startingRadius,
			final float finalRadius, final float collisionInterval,
			final CAbilityCollisionProjectileListener projectileListener, final boolean provideCounts) {
		final CCollisionProjectile projectile = this.simulationRenderController.createCollisionProjectile(this, launchX,
				launchY, launchFacing, speed, homing, source, spellAlias, target, maxHits, hitsPerTarget,
				startingRadius, finalRadius, collisionInterval, projectileListener, provideCounts);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, projectile, target);
		return projectile;
	}

	public CPseudoProjectile createPseudoProjectile(final CUnit source, final War3ID spellAlias,
			final CEffectType effectType, final int effectArtIndex, final float launchX, final float launchY,
			final float launchFacing, final float speed, final float projectileStepInterval,
			final int projectileArtSkip, final boolean homing, final AbilityTarget target, final int maxHits,
			final int hitsPerTarget, final float startingRadius, final float finalRadius,
			final CAbilityCollisionProjectileListener projectileListener, final boolean provideCounts) {
		final CPseudoProjectile projectile = this.simulationRenderController.createPseudoProjectile(this, launchX,
				launchY, launchFacing, speed, projectileStepInterval, projectileArtSkip, homing, source, spellAlias,
				effectType, effectArtIndex, target, maxHits, hitsPerTarget, startingRadius, finalRadius,
				projectileListener, provideCounts);
		this.newProjectiles.add(projectile);
		projectileListener.onLaunch(this, projectile, target);
		return projectile;
	}

	public void registerEffect(final CEffect effect) {
		this.newProjectiles.add(effect);
	}

	public SimulationRenderComponentLightning createLightning(final CUnit source, final War3ID lightningId,
			final CUnit target) {
		return this.simulationRenderController.createLightning(this, lightningId, source, target);
	}

	public SimulationRenderComponentLightning createLightning(final CUnit source, final War3ID lightningId,
			final CUnit target, final Float duration) {
		return this.simulationRenderController.createLightning(this, lightningId, source, target, duration);
	}

	public SimulationRenderComponentLightning createAbilityLightning(final CUnit source, final War3ID lightningId,
			final int lightningIndex, final CUnit target) {
		return this.simulationRenderController.createAbilityLightning(this, lightningId, source, target,
				lightningIndex);
	}

	public SimulationRenderComponentLightning createAbilityLightning(final CUnit source, final War3ID lightningId,
			final int lightningIndex, final CUnit target, final Float duration) {
		return this.simulationRenderController.createAbilityLightning(this, lightningId, source, target, lightningIndex,
				duration);
	}

	public SimulationRenderComponent createStaticUberSplat(float x, float y, War3ID id) {
		return this.simulationRenderController.createStaticUberSplat(x, y, id);
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

	protected void updateFogOfWar() {
		for (final CPlayer player : this.players) {
			player.getFogOfWar().convertVisibleToFogged();
			player.updateFogModifiers(this);
		}
		for (final CUnit unit : this.units) {
			unit.updateFogOfWar(this);
		}
		for (final CPlayer player : this.players) {
			player.updateFogModifiersAfterUnits(this);
		}
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
				getPlayerHeroes(unit.getPlayerIndex()).remove(unit);
				unit.onRemove(this);
			}
		}
		finishAddingNewUnits();
		for (final CDestructable destructable : this.removedDestructables) {
			this.simulationRenderController.removeDestructable(destructable);
			destructable.onRemove(this);
		}
		this.removedDestructables.clear();
		final Iterator<CEffect> projectileIterator = this.projectiles.iterator();
		while (projectileIterator.hasNext()) {
			final CEffect projectile = projectileIterator.next();
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
		final float timeOfDayBefore = getGameTimeOfDay();
		if (this.falseTimeOfDay != null) {
			if (this.nextGameTime != null) {
				this.falseTimeOfDay.setTimeOfDay(this.nextGameTime);
				this.nextGameTime = null;
			}
			if (!this.falseTimeOfDay.tick()) {
				this.falseTimeOfDay = null;
			}
		} else {
			if (this.nextGameTime != null) {
				this.currentGameDayTimeElapsed = (this.nextGameTime / this.gameplayConstants.getGameDayHours())
						* this.gameplayConstants.getGameDayLength();
				this.nextGameTime = null;
			} else if (!this.timeOfDaySuspended) {
				this.currentGameDayTimeElapsed = (this.currentGameDayTimeElapsed
						+ WarsmashConstants.SIMULATION_STEP_TIME) % this.gameplayConstants.getGameDayLength();
			}
		}
		final float timeOfDayAfter = getGameTimeOfDay();
		this.daytime = (timeOfDayAfter >= this.gameplayConstants.getDawnTimeGameHours())
				&& (timeOfDayAfter < this.gameplayConstants.getDuskTimeGameHours());
		for (final CTimer timer : this.addedTimers) {
			internalRegisterTimer(timer);
		}
		this.addedTimers.clear();
		for (final CTimer timer : this.removedTimers) {
			internalUnregisterTimer(timer);
		}
		this.removedTimers.clear();
		final Set<CTimer> timers = new HashSet<>();
		for (final CTimer timer : this.activeTimers) {
			if (!timers.add(timer)) {
				throw new IllegalStateException("Duplicate timer add: " + timer);
			}
		}
		while (!this.activeTimers.isEmpty() && (this.activeTimers.peek().getEngineFireTick() <= this.gameTurnTick)) {
			this.activeTimers.pop().fire(this);
		}
		checkTimeOfDayEvents(timeOfDayBefore, timeOfDayAfter);
		this.onTickTriggers.addAll(this.addedOnTickTriggers);
		this.addedOnTickTriggers.clear();
		for (final Trigger trigger : this.onTickTriggers) {
			final TriggerExecutionScope triggerScope = trigger.getTriggerExecutionScope();
			if (trigger.evaluate(this.globalScope, triggerScope)) {
				trigger.execute(this.globalScope, triggerScope);
			}
		}
		this.onTickTriggers.removeAll(this.removedOnTickTriggers);
		this.removedOnTickTriggers.clear();

		this.globalScope.runThreads();

		this.runningPostUpdateCallbacks.clear();
		this.runningPostUpdateCallbacks.addAll(this.postUpdateCallbacks);
		this.postUpdateCallbacks.clear();
		for (final Runnable runnable : this.runningPostUpdateCallbacks) {
			runnable.run();
		}
	}

	public void removeUnit(final CUnit unit) {
		unit.setHidden(true);
		final CPlayer player = this.getPlayer(unit.getPlayerIndex());
		if (unit.getFoodMade() != 0) {
			player.setUnitFoodMade(unit, 0);
		}
		if (unit.getFoodUsed() != 0) {
			player.setUnitFoodUsed(unit, 0);
		}
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
			getPlayerHeroes(unit.getPlayerIndex()).remove(unit);
			unit.onRemove(this);
		}
		this.removedUnits.clear();
	}

	public float getGameTimeOfDay() {
		if ((this.falseTimeOfDay != null) && this.falseTimeOfDay.isInitialized()) {
			return this.falseTimeOfDay.getTimeOfDay();
		}
		return (this.currentGameDayTimeElapsed / this.gameplayConstants.getGameDayLength())
				* this.gameplayConstants.getGameDayHours();
	}

	public void setGameTimeOfDay(final float value) {
		this.nextGameTime = value;
	}

	private void checkTimeOfDayEvents(final float timeOfDayBefore, final float timeOfDayAfter) {
		for (final TimeOfDayEvent timeOfDayEvent : this.timeOfDayVariableEvents) {
			if (!timeOfDayEvent.isMatching(timeOfDayBefore) && timeOfDayEvent.isMatching(timeOfDayAfter)) {
				timeOfDayEvent.fire();
			}
		}
	}

	public void addFalseTimeOfDay(final int hour, final int minute, final float duration) {
		final float timeOfDayBefore = getGameTimeOfDay();
		this.falseTimeOfDay = new FalseTimeOfDay(hour, minute,
				(int) (duration / WarsmashConstants.SIMULATION_STEP_TIME));
		checkTimeOfDayEvents(timeOfDayBefore, getGameTimeOfDay());
	}

	public boolean isFalseTimeOfDay() {
		return ((this.falseTimeOfDay != null) && this.falseTimeOfDay.isInitialized());
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

	public void unitUpgradingEvent(final CUnit cUnit, final War3ID upgradeIdType) {
		this.simulationRenderController.unitUpgradingEvent(cUnit, upgradeIdType);
	}

	public void unitCancelUpgradingEvent(final CUnit cUnit, final War3ID upgradeIdType) {
		this.simulationRenderController.unitCancelUpgradingEvent(cUnit, upgradeIdType);
	}

	@Override
	public CPlayer getPlayer(final int index) {
		return this.players.get(index);
	}

	public CPlayerUnitOrderExecutor getDefaultPlayerUnitOrderExecutor(final int index) {
		return this.defaultPlayerUnitOrderExecutors.get(index);
	}

	public CommandErrorListener getCommandErrorListener() {
		return this.commandErrorListener;
	}

	public void unitConstructFinishEvent(final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitConstructionFinishSound(constructedStructure);
	}

	public void unitUpgradeFinishEvent(final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitUpgradeFinishSound(constructedStructure);
	}

	public void createDeathExplodeEffect(final CUnit cUnit, final War3ID explodesOnDeathBuffId) {
		this.simulationRenderController.spawnDeathExplodeEffect(cUnit, explodesOnDeathBuffId);
	}

	public HandleIdAllocator getHandleIdAllocator() {
		return this.handleIdAllocator;
	}

	public void unitTrainedEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}

	public void researchFinishEvent(final CUnit cUnit, final War3ID queuedRawcode, final int level) {
		getCommandErrorListener().showUpgradeCompleteAlert(cUnit.getPlayerIndex(), queuedRawcode, level);
	}

	public void heroReviveEvent(final CUnit trainingUnit, final CUnit trainedUnit) {
		this.simulationRenderController.heroRevived(trainedUnit);
		this.simulationRenderController.spawnUnitReadySound(trainedUnit);
	}

	public void unitRepositioned(final CUnit cUnit) {
		this.simulationRenderController.unitRepositioned(cUnit);
	}

	public void unitGainResourceEvent(final CUnit unit, final int playerIndex, final ResourceType resourceType,
			final int amount) {
		switch (resourceType) {
		case GOLD: {
			spawnTextTag(unit, playerIndex, TextTagConfigType.GOLD, amount);
			break;
		}
		case LUMBER: {
			spawnTextTag(unit, playerIndex, TextTagConfigType.LUMBER, amount);
			break;
		}
		}
	}

	public TextTag spawnTextTag(final CUnit unit, final int playerIndex, final TextTagConfigType type,
			final int amount) {
		return this.simulationRenderController.spawnTextTag(unit, type, amount);
	}

	public TextTag spawnTextTag(final CUnit unit, final int playerIndex, final TextTagConfigType type,
			final String message) {
		return this.simulationRenderController.spawnTextTag(unit, type, message);
	}

	public TextTag createTextTag() {
		return this.simulationRenderController.createTextTag();
	}

	public void destroyTextTag(final TextTag textTag) {
		this.simulationRenderController.destroyTextTag(textTag);
	}

	public void unitGainLevelEvent(final CUnit unit, boolean showEffect) {
		this.players.get(unit.getPlayerIndex()).fireHeroLevelEvents(unit);
		if (showEffect) {
			this.simulationRenderController.spawnGainLevelEffect(unit);
		}
	}

	public void heroCreateEvent(final CUnit hero) {
		getPlayerHeroes(hero.getPlayerIndex()).add(hero);
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
		finishAddingNewUnits();
		for (final CUnit unit : this.units) {
			final CPlayer player = this.players.get(unit.getPlayerIndex());
			player.setUnitFoodUsed(unit, unit.getUnitType().getFoodUsed());
			player.setUnitFoodMade(unit, unit.getUnitType().getFoodMade());
			player.addTechtreeUnlocked(this, unit.getTypeId());
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

	public void createTemporarySpellEffectOnUnit(final CUnit unit, final War3ID alias, final CEffectType effectType) {
		this.simulationRenderController.spawnTemporarySpellEffectOnUnit(unit, alias, effectType);
	}

	public SimulationRenderComponentModel createPersistentSpellEffectOnUnit(final CUnit unit, final War3ID alias,
			final CEffectType effectType) {
		return this.simulationRenderController.spawnPersistentSpellEffectOnUnit(unit, alias, effectType);
	}

	public SimulationRenderComponentModel createPersistentSpellEffectOnUnit(final CUnit unit, final War3ID alias,
			final CEffectType effectType, final int index) {
		return this.simulationRenderController.spawnPersistentSpellEffectOnUnit(unit, alias, effectType, index);
	}

	public SimulationRenderComponent unitSoundEffectEvent(final CUnit caster, final War3ID alias) {
		return this.simulationRenderController.spawnAbilitySoundEffect(caster, alias);
	}

	public SimulationRenderComponent unitLoopSoundEffectEvent(final CUnit caster, final War3ID alias) {
		return this.simulationRenderController.loopAbilitySoundEffect(caster, alias);
	}

	public void unitStopSoundEffectEvent(final CUnit caster, final War3ID alias) {
		this.simulationRenderController.stopAbilitySoundEffect(caster, alias);
	}

	public SimulationRenderComponent locationSoundEffectEvent(final float x, final float y, final War3ID alias, final boolean looping) {
		return this.simulationRenderController.spawnAbilitySoundEffect(x, y, alias, looping);
	}

	public void unitPreferredSelectionReplacement(final CUnit unit, final CUnit newUnit) {
		this.simulationRenderController.unitPreferredSelectionReplacement(unit, newUnit);
	}

	public void registerTimeOfDayEvent(final TimeOfDayEvent timeOfDayEvent) {
		this.timeOfDayVariableEvents.add(timeOfDayEvent);
	}

	public boolean isTimeOfDayEventRegistered(final TimeOfDayEvent timeOfDayEvent) {
		return this.timeOfDayVariableEvents.contains(timeOfDayEvent);
	}

	public void unregisterTimeOfDayEvent(final TimeOfDayEvent timeOfDayEvent) {
		this.timeOfDayVariableEvents.remove(timeOfDayEvent);
	}

	public void registerOnTickEvent(final Trigger trigger) {
		this.addedOnTickTriggers.add(trigger);
	}

	public void unregisterOnTickEvent(final Trigger trigger) {
		this.removedOnTickTriggers.add(trigger);
	}

	public RemovableTriggerEvent registerTimeOfDayEvent(final GlobalScope globalScope, final Trigger trigger,
			final CLimitOp opcode, final double doubleValue) {
		final TimeOfDayVariableEvent timeOfDayVariableEvent = new TimeOfDayVariableEvent(trigger, opcode, doubleValue,
				globalScope);
		this.timeOfDayVariableEvents.add(timeOfDayVariableEvent);
		return new RemovableTriggerEvent(trigger) {
			@Override
			public void remove() {
				CSimulation.this.timeOfDayVariableEvents.remove(timeOfDayVariableEvent);
			}
		};
	}

	public RemovableTriggerEvent registerGameEvent(final GlobalScope globalScope, final Trigger trigger,
			final JassGameEventsWar3 gameEvent) {
		System.err.println("Game event not yet implemented: " + gameEvent);
		return new RemovableTriggerEvent(trigger) {
			@Override
			public void remove() {
			}
		};
	}

	private List<CGlobalEvent> getOrCreateEventList(final JassGameEventsWar3 eventType) {
		List<CGlobalEvent> eventList = this.eventTypeToEvents.get(eventType);
		if (eventList == null) {
			eventList = new ArrayList<>();
			this.eventTypeToEvents.put(eventType, eventList);
		}
		return eventList;
	}

	protected List<CGlobalEvent> getEventList(final JassGameEventsWar3 eventType) {
		return this.eventTypeToEvents.get(eventType);
	}

	public RemovableTriggerEvent registerGlobalUnitEvent(final Trigger whichTrigger, final JassGameEventsWar3 eventType,
			final TriggerBooleanExpression filter) {
		final CGlobalEvent newEvent = new CGlobalWidgetEvent(this, this.globalScope, whichTrigger, eventType, filter);
		getOrCreateEventList(eventType).add(newEvent);
		return newEvent;
	}

	public void addGlobalEvent(final CGlobalEvent newEvent) {
		getOrCreateEventList(newEvent.getEventType()).add(newEvent);
	}

	public void removeGlobalEvent(final CGlobalEvent globalEvent) {
		final List<CGlobalEvent> eventList = getEventList(globalEvent.getEventType());
		if (eventList != null) {
			eventList.remove(globalEvent);
		}
	}

	public void fireSpellEventsNoTarget(final JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit) {
		final List<CGlobalEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CGlobalEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellNoTargetScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsPointTarget(final JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final AbilityPointTarget abilityPointTarget) {
		final List<CGlobalEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CGlobalEvent event : eventList) {
				event.fire(spellAbilityUnit,
						CommonTriggerExecutionScope.unitSpellPointScope(eventId, event.getTrigger(), spellAbility,
								spellAbilityUnit, abilityPointTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsUnitTarget(final JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CUnit unitTarget) {
		final List<CGlobalEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CGlobalEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetUnitScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, unitTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsItemTarget(final JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CItem itemTarget) {
		final List<CGlobalEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CGlobalEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetItemScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, itemTarget, spellAbility.getAlias()));
			}
		}
	}

	public void fireSpellEventsDestructableTarget(final JassGameEventsWar3 eventId, final CAbility spellAbility,
			final CUnit spellAbilityUnit, final CDestructable destTarget) {
		final List<CGlobalEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CGlobalEvent event : eventList) {
				event.fire(spellAbilityUnit, CommonTriggerExecutionScope.unitSpellTargetDestructableScope(eventId,
						event.getTrigger(), spellAbility, spellAbilityUnit, destTarget, spellAbility.getAlias()));
			}
		}
	}

	public void heroDeathEvent(final CUnit cUnit) {
		this.simulationRenderController.heroDeathEvent(cUnit);
	}

	public void heroDissipateEvent(final CUnit cUnit) {
		getPlayer(cUnit.getPlayerIndex()).onHeroDeath(cUnit);
	}

	public void removeItem(final CItem cItem) {
		cItem.forceDropIfHeld(this);
		cItem.setHidden(true); // TODO fix
		cItem.setLife(this, 0);
	}

	public void removeDestructable(CDestructable dest) {
		dest.setLife(this, 0);
		this.removedDestructables.add(dest);
	}

	public SimulationRenderComponentModel createSpellEffectOverDestructable(final CUnit source,
			final CDestructable target, final War3ID alias, final float artAttachmentHeight) {
		return this.simulationRenderController.createSpellEffectOverDestructable(source, target, alias,
				artAttachmentHeight);
	}

	public SimulationRenderComponentModel spawnSpellEffectOnPoint(final float x, final float y, final float facing,
			final War3ID alias, final CEffectType effectType, final int index) {
		return this.simulationRenderController.spawnSpellEffectOnPoint(x, y, facing, alias, effectType, index);
	}

	public void spawnTemporarySpellEffectOnPoint(final float x, final float y, final float facing, final War3ID alias,
			final CEffectType effectType, final int index) {
		this.simulationRenderController.spawnTemporarySpellEffectOnPoint(x, y, facing, alias, effectType, index);
	}

	public void tagTreeOwned(final CDestructable target) {
		this.ownedTreeSet.add(target);
	}

	public void untagTreeOwned(final CDestructable target) {
		this.ownedTreeSet.remove(target);
	}

	public boolean isTreeOwned(final CDestructable tree) {
		return this.ownedTreeSet.contains(tree);
	}

	public static interface TimeOfDayEvent {
		public void fire();

		public boolean isMatching(double timeOfDayBefore);
	}

	private static final class TimeOfDayVariableEvent extends VariableEvent implements TimeOfDayEvent {
		private final GlobalScope globalScope;

		public TimeOfDayVariableEvent(final Trigger trigger, final CLimitOp limitOp, final double doubleValue,
				final GlobalScope globalScope) {
			super(trigger, limitOp, doubleValue);
			this.globalScope = globalScope;
		}

		@Override
		public void fire() {
			this.fire(this.globalScope);
		}
	}

	public RemovableTriggerEvent registerEventPlayerDefeat(final GlobalScope globalScope, final Trigger whichTrigger,
			final CPlayerJass whichPlayer) {
		throw new UnsupportedOperationException("registerEventPlayerDefeat is NYI");
	}

	public RemovableTriggerEvent registerEventPlayerVictory(final GlobalScope globalScope, final Trigger whichTrigger,
			final CPlayerJass whichPlayer) {
		throw new UnsupportedOperationException("registerEventPlayerVictory is NYI");
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

	public void setBlight(final float x, final float y, final float radius, final boolean blighted) {
		this.simulationRenderController.setBlight(x, y, radius, blighted);
	}

	public void unitUpdatedType(final CUnit unit, final War3ID typeId, final boolean updatePortrait) {
		this.simulationRenderController.unitUpdatedType(unit, typeId, updatePortrait);
	}

	private void setupCreatedUnit(final CUnit unit) {
		final CUnitType unitTypeInstance = unit.getUnitType();
		final int manaInitial = unitTypeInstance.getManaInitial();
		final int speed = unitTypeInstance.getSpeed();
		this.unitData.addDefaultAbilitiesToUnit(this, this.handleIdAllocator, unitTypeInstance, true, manaInitial,
				speed, unit);
		this.unitData.applyPlayerUpgradesToUnit(this, unit.getPlayerIndex(), unitTypeInstance, unit);
		final BufferedImage buildingPathingPixelMap = unitTypeInstance.getBuildingPathingPixelMap();
		if (buildingPathingPixelMap != null) {
			unit.regeneratePathingInstance(this, buildingPathingPixelMap);
		}
	}

	public void changeUnitScale(final CUnit unit, final float scale, boolean multiplier) {
		this.simulationRenderController.changeUnitScale(unit, scale, multiplier);
	}

	public void changeUnitColor(final CUnit unit, final int playerIndex) {
		this.simulationRenderController.changeUnitColor(unit, playerIndex);
	}

	public void changeUnitVertexColor(final CUnit unit, final Color color) {
		this.simulationRenderController.changeUnitVertexColor(unit, color);
	}

	public void changeUnitVertexColor(final CUnit unit, final float r, final float g, final float b) {
		this.simulationRenderController.changeUnitVertexColor(unit, r, g, b);
	}

	public void changeUnitVertexColor(final CUnit unit, final float r, final float g, final float b, final float a) {
		this.simulationRenderController.changeUnitVertexColor(unit, r, g, b, a);
	}

	public float[] getUnitVertexColor(final CUnit unit) {
		return this.simulationRenderController.getUnitVertexColor(unit);
	}

	public void setGlobalScope(final GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	public GlobalScope getGlobalScope() {
		return this.globalScope;
	}

	public int[] getTerrainModBufferSize(float x, float y, float width, float height) {
		return this.simulationRenderController.getTerrainModBufferSize(x, y, width, height);
	}

	public int[] getTerrainModBufferSize(float centerX, float centerY, float radius) {
		return this.simulationRenderController.getTerrainModBufferSize(centerX, centerY, radius);
	}

	public void adjustTerrain(int[] rect, float[] modBuffer) {
		this.simulationRenderController.adjustTerrain(rect, modBuffer);
	}

	public void adjustTerrain(float x, float y, float i) {
		this.simulationRenderController.adjustTerrain(x, y, i);
	}

	public float getTerrainSpaceX(final float x) {
		return this.simulationRenderController.getTerrainSpaceX(x);
	}

	public float getTerrainSpaceY(final float y) {
		return this.simulationRenderController.getTerrainSpaceY(y);
	}

	public int getTerrainHeight(final float x, final float y) {
		return this.simulationRenderController.getTerrainHeight(x, y);
	}

	public boolean isTerrainRomp(final float x, final float y) {
		return this.simulationRenderController.isTerrainRomp(x, y);
	}

	public boolean isTerrainWater(final float x, final float y) {
		return this.simulationRenderController.isTerrainWater(x, y);
	}

	public int getMapVersion() {
		return this.mapVersion;
	}

	public boolean isMapReignOfChaos() {
		return this.mapVersion <= 24;
	}

	public void setFogMaskEnabled(final boolean enable) {
		this.fogMaskEnabled = enable;
	}

	@Override
	public boolean isFogMaskEnabled() {
		return this.fogMaskEnabled;
	}

	public void setFogEnabled(final boolean fogEnabled) {
		this.fogEnabled = fogEnabled;
	}

	@Override
	public boolean isFogEnabled() {
		return this.fogEnabled;
	}

	@Override
	public byte getFogStateFromSettings(byte mask) {
		final CFogState state = CFogState.getByMask(mask);
		switch (state) {
		case MASKED:
			if (this.fogMaskEnabled) {
				if (this.fogEnabled) {
					return CFogState.MASKED.getMask();
				} else {
					return CFogState.MASKED.getMask();
				}
			} else if (this.fogEnabled) {
				return CFogState.FOGGED.getMask();
			} else {
				return CFogState.VISIBLE.getMask();
			}
		case FOGGED:
			if (this.fogMaskEnabled) {
				if (this.fogEnabled) {
					return CFogState.FOGGED.getMask();
				} else {
					return CFogState.VISIBLE.getMask();
				}
			} else if (this.fogEnabled) {
				return CFogState.FOGGED.getMask();
			} else {
				return CFogState.VISIBLE.getMask();
			}
		case VISIBLE:
			return state.getMask();
		}
		return 0;
	}

	@Override
	public void setColor(CPlayerJass player, CPlayerColor color) {
		final int previousColor = player.getColor();
		final int newColor = color.ordinal();
		player.setColor(newColor);
		for (final CUnit unit : this.units) {
			if (unit.getPlayerIndex() == player.getId()) {
				this.simulationRenderController.changeUnitPlayerColor(unit, previousColor, newColor);
			}
		}
		for (final CUnit unit : this.newUnits) {
			if (unit.getPlayerIndex() == player.getId()) {
				this.simulationRenderController.changeUnitPlayerColor(unit, previousColor, newColor);
			}
		}
	}

	public void fireRequirementUpdateForAbilities(CPlayer player, boolean disable) {
		this.postUpdateCallbacks.add(new Runnable() {
			@Override
			public void run() {
				for (final CUnit unit : getUnits()) {
					if (unit.getPlayerIndex() == player.getId()) {
						unit.checkDisabledAbilities(CSimulation.this, disable);
					}
				}
			}
		});
	}

}
