package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.w3x.w3i.Force;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;

public class TeamSetupPane {
	private final List<PlayerSlotPane> playerSlots = new ArrayList<>();
	private final SimpleFrame container;

	public TeamSetupPane(final GameUI rootFrame, final Viewport uiViewport, final SimpleFrame container) {
		this.container = container;
	}

	public void setMap(final DataSource dataSource, final GameUI rootFrame, final Viewport uiViewport,
			final War3MapConfig config, final int playerCount, final War3MapW3i mapInfo) {
		for (final PlayerSlotPane playerSlotPane : this.playerSlots) {
			this.container.remove(playerSlotPane.getPlayerSlotFrame());
		}
		this.playerSlots.clear();
		int usedSlots = 0;
		if (mapInfo.hasFlag(War3MapW3iFlags.FIXED_PLAYER_SETTINGS_FOR_CUSTOM_FORCES)) {
			UIFrame lastFrame = null;
			int forceIndex = 0;
			for (final Force force : mapInfo.getForces()) {
				final StringFrame forceLabelString = rootFrame.createStringFrame(
						"SmashForce" + forceIndex + "NameLabel", rootFrame, null, TextJustify.LEFT, TextJustify.MIDDLE,
						0.01f);
				rootFrame.setText(forceLabelString, rootFrame.getTrigStr(force.getName()));
				for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (usedSlots < playerCount); i++) {
					final CBasePlayer player = config.getPlayer(i);
					if (player.getController() == CMapControl.NONE) {
						continue;
					}
					final int playerMaskBit = 1 << i;
					final boolean forceContainsPlayer = (force.getPlayerMasks() & playerMaskBit) != 0;
					if (forceContainsPlayer) {
						final PlayerSlotPane playerSlotPane = new PlayerSlotPane(rootFrame, uiViewport, this.container,
								i);
						this.playerSlots.add(playerSlotPane);
						if (lastFrame == null) {
							playerSlotPane.getPlayerSlotFrame().addSetPoint(
									new SetPoint(FramePoint.TOPLEFT, this.container, FramePoint.TOPLEFT, 0, 0));
						}
						else {
							playerSlotPane.getPlayerSlotFrame().addSetPoint(
									new SetPoint(FramePoint.TOPLEFT, lastFrame, FramePoint.BOTTOMLEFT, 0, 0));
						}
						lastFrame = playerSlotPane.getPlayerSlotFrame();
						playerSlotPane.setForPlayer(dataSource, rootFrame, uiViewport, player);
						usedSlots++;
					}
				}
				forceIndex++;
			}
		}
		else {
			for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (usedSlots < playerCount); i++) {
				final CBasePlayer player = config.getPlayer(i);
				if (player.getController() == CMapControl.NONE) {
					continue;
				}
				final PlayerSlotPane playerSlotPane = new PlayerSlotPane(rootFrame, uiViewport, this.container, i);
				this.playerSlots.add(playerSlotPane);
				if (usedSlots == 0) {
					playerSlotPane.getPlayerSlotFrame()
							.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.container, FramePoint.TOPLEFT, 0, 0));
				}
				else {
					playerSlotPane.getPlayerSlotFrame().addSetPoint(new SetPoint(FramePoint.TOPLEFT,
							this.playerSlots.get(usedSlots - 1).getPlayerSlotFrame(), FramePoint.BOTTOMLEFT, 0, 0));
				}
				playerSlotPane.setForPlayer(dataSource, rootFrame, uiViewport, player);
				usedSlots++;
			}
		}
		this.container.positionBounds(rootFrame, uiViewport);
	}
}
