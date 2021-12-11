package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.PopupMenuFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;

public class PlayerSlotPane {
	private final SimpleFrame playerSlotFrame;
	private final StringFrame downloadValue;
	private final PopupMenuFrame nameMenu;
	private final PopupMenuFrame raceMenu;
	private final GlueButtonFrame teamButtonFrame;
	private final GlueButtonFrame colorButtonFrame;
	private final BackdropFrame colorButtonValueFrame;
	private final int index;

	public PlayerSlotPane(final GameUI rootFrame, final Viewport uiViewport, final SimpleFrame container,
			final int index) {
		this.index = index;
		this.playerSlotFrame = (SimpleFrame) rootFrame.createFrame("PlayerSlot", container, 0, index);
		container.add(this.playerSlotFrame);

		this.downloadValue = (StringFrame) rootFrame.getFrameByName("DownloadValue", index);
		this.nameMenu = (PopupMenuFrame) rootFrame.getFrameByName("NameMenu", index);
		this.raceMenu = (PopupMenuFrame) rootFrame.getFrameByName("RaceMenu", index);
		this.teamButtonFrame = (GlueButtonFrame) rootFrame.getFrameByName("TeamButton", index);
		this.colorButtonFrame = (GlueButtonFrame) rootFrame.getFrameByName("ColorButton", index);

		// TODO this is a hole in my API to need to do this -- instead it should
		// probably inflate all frames within a frame by default
		this.colorButtonValueFrame = (BackdropFrame) rootFrame.createFrame("ColorButtonValue", this.colorButtonFrame, 0,
				index);
		this.playerSlotFrame.add(this.colorButtonValueFrame);
	}

	public void setForPlayer(final DataSource dataSource, final GameUI rootFrame, final Viewport uiViewport,
			final CPlayerJass player) {
		String name = player.getName();
		if (name == null) {
			name = "PLAYER" + this.index;
		}
		rootFrame.setText(this.downloadValue, "");
		this.downloadValue.setVisible(false);
		rootFrame.setText(((StringFrame) ((GlueTextButtonFrame) this.nameMenu.getPopupTitleFrame()).getButtonText()),
				name);
		if (player.isRacePrefSet(CRacePreference.RANDOM)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					"RANDOM");
		}
		else if (player.isRacePrefSet(CRacePreference.HUMAN)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					"HUMAN");
		}
		else if (player.isRacePrefSet(CRacePreference.ORC)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()), "ORC");
		}
		else if (player.isRacePrefSet(CRacePreference.UNDEAD)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					"UNDEAD");
		}
		else if (player.isRacePrefSet(CRacePreference.NIGHTELF)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					"NIGHTELF");
		}
		// TODO maybe caching instead of accessing data source?
		this.colorButtonValueFrame.setBackground(ImageUtils.getAnyExtensionTexture(dataSource, "ReplaceableTextures\\"
				+ ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(player.getColor()) + ".blp"));

	}

	public UIFrame getPlayerSlotFrame() {
		return this.playerSlotFrame;
	}
}
