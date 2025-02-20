package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;

public interface CPlayerAPI {
	CBasePlayer getPlayer(int index);

	void setColor(CPlayerJass player, CPlayerColor color);
}
