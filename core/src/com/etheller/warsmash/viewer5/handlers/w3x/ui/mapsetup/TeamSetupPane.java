package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

public class TeamSetupPane {
	private final List<PlayerSlotPane> playerSlots = new ArrayList<>();
	private final SimpleFrame container;

	public TeamSetupPane(final GameUI rootFrame, final Viewport uiViewport, final SimpleFrame container) {
		this.container = container;
	}

	public void setMap(final DataSource dataSource, final GameUI rootFrame, final Viewport uiViewport,
			final War3MapConfig config, final int playerCount) {
		for (final PlayerSlotPane playerSlotPane : this.playerSlots) {
			this.container.remove(playerSlotPane.getPlayerSlotFrame());
		}
		this.playerSlots.clear();
		for (int i = 0; i < playerCount; i++) {
			final CBasePlayer player = config.getPlayer(i);
			final PlayerSlotPane playerSlotPane = new PlayerSlotPane(rootFrame, uiViewport, this.container, i);
			this.playerSlots.add(playerSlotPane);
			if (i == 0) {
				playerSlotPane.getPlayerSlotFrame()
						.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.container, FramePoint.TOPLEFT, 0, 0));
			}
			else {
				playerSlotPane.getPlayerSlotFrame().addSetPoint(new SetPoint(FramePoint.TOPLEFT,
						this.playerSlots.get(i - 1).getPlayerSlotFrame(), FramePoint.BOTTOMLEFT, 0, 0));
			}
			playerSlotPane.setForPlayer(dataSource, rootFrame, uiViewport, player);
		}
		this.container.positionBounds(rootFrame, uiViewport);
	}
}
