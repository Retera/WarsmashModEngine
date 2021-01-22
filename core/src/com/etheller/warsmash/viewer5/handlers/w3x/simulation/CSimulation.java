package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.parsers.w3x.w3i.Player;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CAbilityData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CDestructableData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CPathfindingProcessor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderController;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class CSimulation {
	private final CAbilityData abilityData;
	private final CUnitData unitData;
	private final CDestructableData destructableData;
	private final List<CUnit> units;
	private final List<CUnit> newUnits;
	private final List<CDestructable> destructables;
	private final List<CPlayer> players;
	private final List<CAttackProjectile> projectiles;
	private final List<CAttackProjectile> newProjectiles;
	private final HandleIdAllocator handleIdAllocator;
	private transient final SimulationRenderController simulationRenderController;
	private int gameTurnTick = 0;
	private final PathingGrid pathingGrid;
	private final CWorldCollision worldCollision;
	private final CPathfindingProcessor pathfindingProcessor;
	private final CGameplayConstants gameplayConstants;
	private final Random seededRandom;
	private float currentGameDayTimeElapsed;
	private final Map<Integer, CUnit> handleIdToUnit = new HashMap<>();
	private final Map<Integer, CDestructable> handleIdToDestructable = new HashMap<>();
	private final Map<Integer, CAbility> handleIdToAbility = new HashMap<>();
	private transient CommandErrorListener commandErrorListener;

	public CSimulation(final DataTable miscData, final MutableObjectData parsedUnitData,
			final MutableObjectData parsedDestructableData, final MutableObjectData parsedAbilityData,
			final SimulationRenderController simulationRenderController, final PathingGrid pathingGrid,
			final Rectangle entireMapBounds, final Random seededRandom, final List<Player> playerInfos,
			final CommandErrorListener commandErrorListener) {
		this.gameplayConstants = new CGameplayConstants(miscData);
		this.simulationRenderController = simulationRenderController;
		this.pathingGrid = pathingGrid;
		this.abilityData = new CAbilityData(parsedAbilityData);
		this.unitData = new CUnitData(parsedUnitData, this.abilityData, this.simulationRenderController);
		this.destructableData = new CDestructableData(parsedDestructableData, simulationRenderController);
		this.units = new ArrayList<>();
		this.newUnits = new ArrayList<>();
		this.destructables = new ArrayList<>();
		this.projectiles = new ArrayList<>();
		this.newProjectiles = new ArrayList<>();
		this.handleIdAllocator = new HandleIdAllocator();
		this.worldCollision = new CWorldCollision(entireMapBounds, this.gameplayConstants.getMaxCollisionRadius());
		this.pathfindingProcessor = new CPathfindingProcessor(pathingGrid, this.worldCollision);
		this.seededRandom = seededRandom;
		this.players = new ArrayList<>();
		for (int i = 0; i < (WarsmashConstants.MAX_PLAYERS - 4); i++) {
			CPlayer newPlayer;
			if (i < playerInfos.size()) {
				final Player playerInfo = playerInfos.get(i);
				newPlayer = new CPlayer(playerInfo.getId(), CMapControl.values()[playerInfo.getType()],
						playerInfo.getName(), CRace.parseRace(playerInfo.getRace()), playerInfo.getStartLocation());
			}
			else {
				newPlayer = new CPlayer(i, CMapControl.NONE, "Default string", CRace.OTHER, new float[] { 0, 0 });
			}
			this.players.add(newPlayer);
		}
		this.players.add(new CPlayer(this.players.size(), CMapControl.NEUTRAL,
				miscData.getLocalizedString("WESTRING_PLAYER_NA"), CRace.OTHER, new float[] { 0, 0 }));
		this.players.add(new CPlayer(this.players.size(), CMapControl.NEUTRAL,
				miscData.getLocalizedString("WESTRING_PLAYER_NV"), CRace.OTHER, new float[] { 0, 0 }));
		this.players.add(new CPlayer(this.players.size(), CMapControl.NEUTRAL,
				miscData.getLocalizedString("WESTRING_PLAYER_NE"), CRace.OTHER, new float[] { 0, 0 }));
		final CPlayer neutralPassive = new CPlayer(this.players.size(), CMapControl.NEUTRAL,
				miscData.getLocalizedString("WESTRING_PLAYER_NP"), CRace.OTHER, new float[] { 0, 0 });
		this.players.add(neutralPassive);
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

	public List<CUnit> getUnits() {
		return this.units;
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing, final BufferedImage buildingPathingPixelMap,
			final RemovablePathingMapInstance pathingInstance) {
		final CUnit unit = this.unitData.create(this, playerIndex, typeId, x, y, facing, buildingPathingPixelMap,
				this.handleIdAllocator, pathingInstance);
		this.newUnits.add(unit);
		this.handleIdToUnit.put(unit.getHandleId(), unit);
		this.worldCollision.addUnit(unit);
		return unit;
	}

	public CDestructable createDestructable(final War3ID typeId, final float x, final float y,
			final RemovablePathingMapInstance pathingInstance, final RemovablePathingMapInstance pathingInstanceDeath) {
		final CDestructable dest = this.destructableData.create(this, typeId, x, y, this.handleIdAllocator,
				pathingInstance, pathingInstanceDeath);
		this.handleIdToDestructable.put(dest.getHandleId(), dest);
		this.destructables.add(dest);
		return dest;
	}

	public CUnit createUnit(final War3ID typeId, final int playerIndex, final float x, final float y,
			final float facing) {
		return this.simulationRenderController.createUnit(this, typeId, playerIndex, x, y, facing);
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

	public CAttackProjectile createProjectile(final CUnit source, final float launchX, final float launchY,
			final float launchFacing, final CUnitAttackMissile attack, final AbilityTarget target, final float damage,
			final int bounceIndex) {
		final CAttackProjectile projectile = this.simulationRenderController.createAttackProjectile(this, launchX,
				launchY, launchFacing, source, attack, target, damage, bounceIndex);
		this.newProjectiles.add(projectile);
		return projectile;
	}

	public void createInstantAttackEffect(final CUnit source, final CUnitAttackInstant attack, final CWidget target) {
		this.simulationRenderController.createInstantAttackEffect(this, source, attack, target);
	}

	public PathingGrid getPathingGrid() {
		return this.pathingGrid;
	}

	public List<Point2D.Float> findNaiveSlowPath(final CUnit ignoreIntersectionsWithThisUnit,
			final CUnit ignoreIntersectionsWithThisSecondUnit, final float startX, final float startY,
			final Point2D.Float goal, final PathingGrid.MovementType movementType, final float collisionSize,
			final boolean allowSmoothing) {
		return this.pathfindingProcessor.findNaiveSlowPath(ignoreIntersectionsWithThisUnit,
				ignoreIntersectionsWithThisSecondUnit, startX, startY, goal, movementType, collisionSize,
				allowSmoothing);
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
			}
		}
		finishAddingNewUnits();
		final Iterator<CAttackProjectile> projectileIterator = this.projectiles.iterator();
		while (projectileIterator.hasNext()) {
			final CAttackProjectile projectile = projectileIterator.next();
			if (projectile.update(this)) {
				projectileIterator.remove();
			}
		}
		this.projectiles.addAll(this.newProjectiles);
		this.newProjectiles.clear();
		this.gameTurnTick++;
		this.currentGameDayTimeElapsed = (this.currentGameDayTimeElapsed + WarsmashConstants.SIMULATION_STEP_TIME)
				% this.gameplayConstants.getGameDayLength();
	}

	private void finishAddingNewUnits() {
		this.units.addAll(this.newUnits);
		this.newUnits.clear();
	}

	public float getGameTimeOfDay() {
		return (this.currentGameDayTimeElapsed / this.gameplayConstants.getGameDayLength())
				* this.gameplayConstants.getGameDayHours();
	}

	public int getGameTurnTick() {
		return this.gameTurnTick;
	}

	public CWorldCollision getWorldCollision() {
		return this.worldCollision;
	}

	public CGameplayConstants getGameplayConstants() {
		return this.gameplayConstants;
	}

	public Random getSeededRandom() {
		return this.seededRandom;
	}

	public void unitDamageEvent(final CUnit damagedUnit, final String weaponSound, final String armorType) {
		this.simulationRenderController.spawnUnitDamageSound(damagedUnit, weaponSound, armorType);
	}

	public void unitConstructedEvent(final CUnit constructingUnit, final CUnit constructedStructure) {
		this.simulationRenderController.spawnUnitConstructionSound(constructingUnit, constructedStructure);
	}

	public CPlayer getPlayer(final int index) {
		return this.players.get(index);
	}

	public CommandErrorListener getCommandErrorListener() {
		return this.commandErrorListener;
	}

	public void unitConstructFinishEvent(final CUnit constructedStructure) {
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

	public void unitRepositioned(final CUnit cUnit) {
		this.simulationRenderController.unitRepositioned(cUnit);
	}

	public void unitsLoaded() {
		// called on startup after the system loads the map's units layer, but not any
		// custom scripts yet
		finishAddingNewUnits();
		for (final CUnit unit : this.units) {
			final CPlayer player = this.players.get(unit.getPlayerIndex());
			player.setUnitFoodUsed(unit, unit.getUnitType().getFoodUsed());
			player.setUnitFoodMade(unit, unit.getUnitType().getFoodMade());
		}
	}
}
