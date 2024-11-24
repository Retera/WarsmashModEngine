package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import java.util.EnumMap;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapFlag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapPlacement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameSpeed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CGameType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDensity;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CMapDifficulty;

public class War3MapConfig implements CPlayerAPI {
	private String mapName;
	private String mapDescription;
	private int teamCount;
	private int playerCount;
	private final War3MapConfigStartLoc[] startLocations;
	private final CBasePlayer[] players;
	private final EnumMap<CGameType, Boolean> gameTypeToSupported = new EnumMap<>(CGameType.class);
	private final EnumMap<CMapFlag, Boolean> mapFlagToEnabled = new EnumMap<>(CMapFlag.class);
	private CMapPlacement placement;
	private CGameSpeed gameSpeed;
	private CMapDifficulty gameDifficulty;
	private CMapDensity resourceDensity;
	private CMapDensity creatureDensity;
	private CGameType gameTypeSelected;

	public War3MapConfig(final int maxPlayers) {
		// TODO should this be WarsmashConstants.MAX_PLAYERS instead of local
		// constructor arg?
		this.startLocations = new War3MapConfigStartLoc[maxPlayers];
		this.players = new CBasePlayer[maxPlayers];
		for (int i = 0; i < maxPlayers; i++) {
			this.startLocations[i] = new War3MapConfigStartLoc();
			this.players[i] = new War3MapConfigPlayer(i);
			if (i >= (maxPlayers - 4)) {
				this.players[i].setController(CMapControl.NEUTRAL);
			}
		}
	}

	public void setMapName(final String mapName) {
		this.mapName = mapName;
	}

	public void setMapDescription(final String mapDescription) {
		this.mapDescription = mapDescription;
	}

	public String getMapName() {
		return this.mapName;
	}

	public String getMapDescription() {
		return this.mapDescription;
	}

	public void setTeamCount(final int teamCount) {
		this.teamCount = teamCount;
	}

	public void setPlayerCount(final int playerCount) {
		this.playerCount = playerCount;
	}

	public void defineStartLocation(final int whichStartLoc, final float x, final float y) {
		final War3MapConfigStartLoc startLoc = this.startLocations[whichStartLoc];
		startLoc.setX(x);
		startLoc.setY(y);
	}

	public War3MapConfigStartLoc getStartLoc(final int whichStartLoc) {
		return this.startLocations[whichStartLoc];
	}

	public void setGameTypeSupported(final CGameType gameType, final boolean supported) {
		this.gameTypeToSupported.put(gameType, supported);
	}

	public void setMapFlag(final CMapFlag mapFlag, final boolean set) {
		this.mapFlagToEnabled.put(mapFlag, set);
	}

	public void setPlacement(final CMapPlacement placement) {
		this.placement = placement;
	}

	public void setGameSpeed(final CGameSpeed gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	public void setGameDifficulty(final CMapDifficulty gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}

	public void setResourceDensity(final CMapDensity resourceDensity) {
		this.resourceDensity = resourceDensity;
	}

	public void setCreatureDensity(final CMapDensity creatureDensity) {
		this.creatureDensity = creatureDensity;
	}

	public int getTeamCount() {
		return this.teamCount;
	}

	public int getPlayerCount() {
		return this.playerCount;
	}

	public boolean isGameTypeSupported(final CGameType gameType) {
		final Boolean supported = this.gameTypeToSupported.get(gameType);
		return (supported != null) && supported;
	}

	public CGameType getGameTypeSelected() {
		return this.gameTypeSelected;
	}

	public boolean isMapFlagSet(final CMapFlag mapFlag) {
		final Boolean flag = this.mapFlagToEnabled.get(mapFlag);
		return (flag != null) && flag;
	}

	public CMapPlacement getPlacement() {
		return this.placement;
	}

	public CGameSpeed getGameSpeed() {
		return this.gameSpeed;
	}

	public CMapDifficulty getGameDifficulty() {
		return this.gameDifficulty;
	}

	public CMapDensity getResourceDensity() {
		return this.resourceDensity;
	}

	public CMapDensity getCreatureDensity() {
		return this.creatureDensity;
	}

	public float getStartLocationX(final int startLocIndex) {
		return this.startLocations[startLocIndex].getX();
	}

	public float getStartLocationY(final int startLocIndex) {
		return this.startLocations[startLocIndex].getY();
	}

	@Override
	public CBasePlayer getPlayer(final int index) {
		return this.players[index];
	}

	@Override
	public void setColor(CPlayerJass player, CPlayerColor color) {
		player.setColor(color.ordinal());
	}
}
