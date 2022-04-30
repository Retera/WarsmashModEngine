package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.MenuFrame;
import com.etheller.warsmash.parsers.fdf.frames.MenuFrame.MenuClickListener;
import com.etheller.warsmash.parsers.fdf.frames.PopupMenuFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;

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
		this.playerSlotFrame = (SimpleFrame) rootFrame.createFrameByType("SIMPLEFRAME", "PlayerSlot", container,
				"WITHCHILDREN", index);
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
		final List<MenuItem> nameMenuItems = new ArrayList<>();
		nameMenuItems.add(new MenuItem(rootFrame.getTemplates().getDecoratedString("OPEN"), -2));
		nameMenuItems.add(new MenuItem(rootFrame.getTemplates().getDecoratedString("CLOSED"), -2));
		nameMenuItems.add(new MenuItem(rootFrame.getTemplates().getDecoratedString("COMPUTER_NEWBIE"), -2));
		nameMenuItems.add(new MenuItem(rootFrame.getTemplates().getDecoratedString("COMPUTER_NORMAL"), -2));
		nameMenuItems.add(new MenuItem(rootFrame.getTemplates().getDecoratedString("COMPUTER_INSANE"), -2));
		setNameMenuTextByPlayer(rootFrame, player, nameMenuItems);
		rootFrame.setText(this.downloadValue, "");
		this.downloadValue.setVisible(false);
		setTextFromRacePreference(rootFrame, player);
		// TODO maybe caching instead of accessing data source?
		this.colorButtonValueFrame.setBackground(ImageUtils.getAnyExtensionTexture(dataSource, "ReplaceableTextures\\"
				+ ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(player.getColor()) + ".blp"));

		((MenuFrame) this.nameMenu.getPopupMenuFrame()).setItems(uiViewport, nameMenuItems);
		this.nameMenu.setMenuClickListener(new MenuClickListener() {
			@Override
			public void onClick(final int button, final int menuItemIndex) {
				switch (menuItemIndex) {
				case 0:
					// open
					player.setController(CMapControl.NONE);
					player.setSlotState(CPlayerSlotState.EMPTY);
					player.setAIDifficulty(null);
					break;
				case 1:
					// close
					player.setController(CMapControl.NONE);
					player.setSlotState(CPlayerSlotState.PLAYING);
					player.setAIDifficulty(null);
					break;
				case 2:
					player.setController(CMapControl.COMPUTER);
					player.setSlotState(CPlayerSlotState.PLAYING);
					player.setAIDifficulty(AIDifficulty.NEWBIE);
					break;
				case 3:
					player.setController(CMapControl.COMPUTER);
					player.setSlotState(CPlayerSlotState.PLAYING);
					player.setAIDifficulty(AIDifficulty.NORMAL);
					break;
				case 4:
					player.setController(CMapControl.COMPUTER);
					player.setSlotState(CPlayerSlotState.PLAYING);
					player.setAIDifficulty(AIDifficulty.INSANE);
					break;
				}
				setNameMenuTextByPlayer(rootFrame, player, nameMenuItems);
			}
		});

		this.raceMenu.setMenuClickListener(new MenuClickListener() {
			@Override
			public void onClick(final int button, final int menuItemIndex) {
				switch (menuItemIndex) {
				case 0:
					player.setRacePref(CRacePreference.RANDOM);
					break;
				case 1:
					player.setRacePref(CRacePreference.HUMAN);
					break;
				case 2:
					player.setRacePref(CRacePreference.ORC);
					break;
				case 3:
					player.setRacePref(CRacePreference.UNDEAD);
					break;
				case 4:
					player.setRacePref(CRacePreference.NIGHTELF);
					break;

				default:
					break;
				}
				setTextFromRacePreference(rootFrame, player);
			}
		});
		this.raceMenu.setEnabled(player.isRaceSelectable());
		this.teamButtonFrame.setEnabled(player.isRaceSelectable());
		this.colorButtonFrame.setEnabled(player.isRaceSelectable());
	}

	public void setNameMenuTextByPlayer(final GameUI rootFrame, final CPlayerJass player,
			final List<MenuItem> nameMenuItems) {
		String name = player.getName();
		final CPlayerSlotState slotState = player.getSlotState();
		final CMapControl controller = player.getController();
		AIDifficulty aiDifficulty = player.getAIDifficulty();
		if (slotState == CPlayerSlotState.EMPTY) {
			name = nameMenuItems.get(0).getText();
		}
		else if (slotState == CPlayerSlotState.PLAYING) {
			if (controller == CMapControl.USER) {
				nameMenuItems.add(new MenuItem(name, -2));
				this.nameMenu.setEnabled(false);
			}
			else if (controller == CMapControl.NONE) {
				name = nameMenuItems.get(1).getText();
			}
			else if (controller == CMapControl.COMPUTER) {
				if (aiDifficulty == null) {
					aiDifficulty = AIDifficulty.NORMAL;
				}
				switch (aiDifficulty) {
				case NEWBIE:
					name = nameMenuItems.get(2).getText();
					break;
				case INSANE:
					name = nameMenuItems.get(4).getText();
					break;
				case NORMAL:
					name = nameMenuItems.get(3).getText();
				default:
					break;
				}
			}
		}
		else {
			name = rootFrame.getTemplates().getDecoratedString("UNKNOWN");
		}
		rootFrame.setText(((StringFrame) ((GlueTextButtonFrame) this.nameMenu.getPopupTitleFrame()).getButtonText()),
				name);
	}

	public void setTextFromRacePreference(final GameUI rootFrame, final CPlayerJass player) {
		final MenuFrame menuFrame = (MenuFrame) this.raceMenu.getPopupMenuFrame();
		if (player.isRacePrefSet(CRacePreference.RANDOM)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					menuFrame.getMenuItem(0).getText());
		}
		else if (player.isRacePrefSet(CRacePreference.HUMAN)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					menuFrame.getMenuItem(1).getText());
		}
		else if (player.isRacePrefSet(CRacePreference.ORC)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					menuFrame.getMenuItem(2).getText());
		}
		else if (player.isRacePrefSet(CRacePreference.UNDEAD)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					menuFrame.getMenuItem(3).getText());
		}
		else if (player.isRacePrefSet(CRacePreference.NIGHTELF)) {
			rootFrame.setText(
					((StringFrame) ((GlueTextButtonFrame) this.raceMenu.getPopupTitleFrame()).getButtonText()),
					menuFrame.getMenuItem(4).getText());
		}
	}

	public UIFrame getPlayerSlotFrame() {
		return this.playerSlotFrame;
	}
}
