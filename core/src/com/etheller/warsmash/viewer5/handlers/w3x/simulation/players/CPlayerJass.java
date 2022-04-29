package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;
import com.etheller.interpreter.ast.scope.trigger.Trigger;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
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

	void setSlotState(CPlayerSlotState slotState);

	void setAIDifficulty(AIDifficulty aiDifficulty);

	void setName(String name);

	void setOnScoreScreen(boolean flag);

	int getTeam();

	int getStartLocationIndex();

	int getColor();

	boolean isRaceSelectable();

	CMapControl getController();

	CPlayerSlotState getSlotState();

	AIDifficulty getAIDifficulty();

	int getTaxRate(int otherPlayerIndex, CPlayerState whichResource);

	boolean isRacePrefSet(CRacePreference pref);

	String getName();

	RemovableTriggerEvent addEvent(final GlobalScope globalScope, final Trigger whichTrigger,
			final JassGameEventsWar3 eventType);

	void removeEvent(CPlayerEvent playerEvent);
}
