package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;

public interface CPlayerJass {
	int getId();

	void setTeam(int team);

	void setStartLocationIndex(int startLocIndex);

	void forceStartLocation(int startLocIndex);

	void setColor(int colorIndex);

	void setAlliance(int otherPlayerIndex, CAllianceType whichAllianceSetting, boolean value);

	boolean hasAlliance(int otherPlayerIndex, CAllianceType allianceType);

	void setTaxRate(int otherPlayerIndex, CPlayerState whichResource, int rate);

	void setRacePref(CRacePreference whichRacePreference);

	void setRaceSelectable(boolean selectable);

	void setController(CMapControl mapControl);

	void setName(String name);

	void setOnScoreScreen(boolean flag);

	int getTeam();

	int getStartLocationIndex();

	int getColor();

	boolean isSelectable();

	CMapControl getController();

	CPlayerSlotState getSlotState();

	int getTaxRate(int otherPlayerIndex, CPlayerState whichResource);

	boolean isRacePrefSet(CRacePreference pref);

	String getName();
}
