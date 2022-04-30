package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.SingleModelScreen;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.networking.MultiplayerHack;
import com.etheller.warsmash.networking.WarsmashClient;
import com.etheller.warsmash.networking.WarsmashClientSendingOrderListener;
import com.etheller.warsmash.networking.WarsmashClientWriter;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.EditBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListenerDelaying;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.FocusableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.DialogWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapInfoPane;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapListContainer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.TeamSetupPane;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.BattleNetUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.BattleNetUIActionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMenuData;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMenuUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMission;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

import net.warsmash.uberserver.AccountCreationFailureReason;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkConnection;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;
import net.warsmash.uberserver.HandshakeDeniedReason;
import net.warsmash.uberserver.JoinGameFailureReason;
import net.warsmash.uberserver.LoginFailureReason;

public class MenuUI {
	private static final Vector2 screenCoordsVector = new Vector2();
	private static boolean ENABLE_NOT_YET_IMPLEMENTED_BUTTONS;

	private final DataSource dataSource;
	private final Scene uiScene;
	private final Viewport uiViewport;
	private final MdxViewer viewer;
	private final RootFrameListener rootFrameListener;
	private final float widthRatioCorrection;
	private final float heightRatioCorrection;
	private GameUI rootFrame;
	private SpriteFrame cursorFrame;

	private ClickableFrame mouseDownUIFrame;
	private ClickableFrame mouseOverUIFrame;
	private FocusableFrame focusUIFrame;

	private UIFrame mainMenuFrame;

	private SpriteFrame glueSpriteLayerTopRight;

	private SpriteFrame glueSpriteLayerTopLeft;
	private SpriteFrame glueSpriteLayerCenter;

	private WorldEditStrings worldEditStrings;

	private DataTable uiSoundsTable;

	private KeyedSounds uiSounds;

	private GlueTextButtonFrame singlePlayerButton;
	private GlueTextButtonFrame battleNetButton;
	private GlueTextButtonFrame localAreaNetworkButton;
	private GlueTextButtonFrame optionsButton;
	private GlueTextButtonFrame creditsButton;
	private GlueButtonFrame realmButton;
	private GlueTextButtonFrame exitButton;

	private final boolean quitting = false;

	private MenuState menuState;

	private UIFrame singlePlayerMenu;
	private UIFrame singlePlayerMainPanel;

	// single player skirmish menu ("Custom Game")
	private UIFrame skirmish;
	private UIFrame mapInfoPanel;
	private UIFrame advancedOptionsPanel;
	private GlueTextButtonFrame mapInfoButton;
	private GlueTextButtonFrame advancedOptionsButton;
	private UIFrame skirmishAdvancedOptionsPane;

	private UIFrame profilePanel;
	private EditBoxFrame newProfileEditBox;

	private GlueButtonFrame profileButton;
	private GlueTextButtonFrame campaignButton;
	private GlueTextButtonFrame loadSavedButton;
	private GlueTextButtonFrame viewReplayButton;
	private GlueTextButtonFrame customCampaignButton;
	private GlueTextButtonFrame skirmishButton;
	private GlueTextButtonFrame singlePlayerCancelButton;
	private GlueButtonFrame editionButton;

	private GlueTextButtonFrame skirmishCancelButton;

	private final WarsmashGdxMultiScreenGame screenManager;

	private final DataTable warsmashIni;

	private UnitSound glueScreenLoop;

	private SpriteFrame warcraftIIILogo;
	// Campaign
	private UIFrame campaignMenu;
	private SpriteFrame campaignFade;
	private GlueTextButtonFrame campaignBackButton;
	private UIFrame missionSelectFrame;
	private UIFrame campaignSelectFrame;
	private DataTable campaignStrings;
	private SpriteFrame campaignWarcraftIIILogo;
	private final SingleModelScreen menuScreen;

	private CampaignMenuData currentCampaign;
	private String[] campaignList;
	private CampaignMenuData[] campaignDatas;

	// BattleNet
	private BattleNetUI battleNetUI;

	private UnitSound mainMenuGlueScreenLoop;
	private GlueTextButtonFrame addProfileButton;
	private GlueTextButtonFrame deleteProfileButton;
	private GlueTextButtonFrame selectProfileButton;
	private final PlayerProfileManager profileManager;
	private StringFrame profileNameText;
	private UIFrame confirmDialog;
	private CampaignMenuUI campaignRootMenuUI;
	private CampaignMenuUI currentMissionSelectMenuUI;
	private UIFrame loadingFrame;
	private UIFrame loadingCustomPanel;
	private UIFrame loadingMeleePanel;
	private StringFrame loadingTitleText;
	private StringFrame loadingSubtitleText;
	private StringFrame loadingText;
	private SpriteFrame loadingBar;
	private String mapFilepathToStart;
	private LoadingMap loadingMap;
	private SpriteFrame loadingBackground;
	private boolean unifiedCampaignInfo;
	private MapInfoPane skirmishMapInfoPane;
	private War3MapConfig currentMapConfig;
	private final DataTable musicSLK;
	private Music[] currentMusics;
	private int currentMusicIndex;
	private boolean currentMusicRandomizeIndex;
	private final GamingNetworkConnection gamingNetworkConnection;
	private UIFrame battleNetConnectDialog;
	private StringFrame battleNetConnectInfoText;
	private GlueTextButtonFrame battleNetConnectCancelButton;
	private DialogWar3 dialog;

	public MenuUI(final DataSource dataSource, final Viewport uiViewport, final Scene uiScene, final MdxViewer viewer,
			final WarsmashGdxMultiScreenGame screenManager, final SingleModelScreen menuScreen,
			final DataTable warsmashIni, final RootFrameListener rootFrameListener,
			final GamingNetworkConnection gamingNetworkConnection) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.uiScene = uiScene;
		this.viewer = viewer;
		this.screenManager = screenManager;
		this.menuScreen = menuScreen;
		this.warsmashIni = warsmashIni;
		this.rootFrameListener = rootFrameListener;
		this.gamingNetworkConnection = gamingNetworkConnection;

		widthRatioCorrection = getMinWorldWidth() / 1600f;
		heightRatioCorrection = getMinWorldHeight() / 1200f;

		profileManager = PlayerProfileManager.loadFromGdx();

		musicSLK = new DataTable(StringBundle.EMPTY);
		final String musicSLKPath = "UI\\SoundInfo\\Music.SLK";
		if (viewer.dataSource.has(musicSLKPath)) {
			try (InputStream miscDataTxtStream = viewer.dataSource.getResourceAsStream(musicSLKPath)) {
				musicSLK.readSLK(miscDataTxtStream);
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}

		gamingNetworkConnection.addListener(new GamingNetworkServerToClientListener() {

			@Override
			public void disconnected() {
				Gdx.app.postRunnable(() -> {
					System.err.println("Disconnected from server...");
					battleNetConnectDialog.setVisible(false);
					setMainMenuButtonsEnabled(true);
					dialog.showError("ERROR_ID_DISCONNECT", null);
//						MenuUI.this.battleNetUI.hide();
//						playCurrentBattleNetGlueSpriteDeath();
//						MenuUI.this.glueSpriteLayerCenter.setSequence("Death");
//						MenuUI.this.menuState = MenuState.LEAVING_BATTLE_NET_FROM_LOGGED_IN;
				});
			}

			@Override
			public void loginOk(final long sessionToken, final String welcomeMessage) {
				Gdx.app.postRunnable(() -> {
					battleNetUI.loginAccepted(sessionToken, welcomeMessage);
					battleNetUI.getDoors().setSequence(PrimaryTag.DEATH, SequenceUtils.ALTERNATE);
					menuState = MenuState.GOING_TO_BATTLE_NET_WELCOME;
				});
			}

			@Override
			public void loginFailed(final LoginFailureReason loginFailureReason) {
				Gdx.app.postRunnable(() -> {
					String msg;
					switch (loginFailureReason) {
					case INVALID_CREDENTIALS:
						msg = "ERROR_ID_BADPASSWORD";
						break;
					case UNKNOWN_USER:
						msg = "ERROR_ID_UNKNOWNACCOUNT";
						break;
					default:
						msg = "ERROR_ID_INVALIDPARAMS";
						break;
					}
					dialog.showError(msg, null);
				});
			}

			@Override
			public void joinedChannel(final String channelName) {
				Gdx.app.postRunnable(() -> {
					battleNetUI.joinedChannel(channelName);
					battleNetUI.hideCurrentScreen();
					playCurrentBattleNetGlueSpriteDeath();
					menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL;
				});
			}

			@Override
			public void handshakeDenied(final HandshakeDeniedReason reason) {
				Gdx.app.postRunnable(() -> {
					battleNetConnectDialog.setVisible(true);
					rootFrame.setDecoratedText(battleNetConnectInfoText,
							"NETERROR_DEFAULTERROR");
				});
			}

			@Override
			public void handshakeAccepted() {
				Gdx.app.postRunnable(() -> {
					battleNetConnectDialog.setVisible(false);
					glueSpriteLayerTopLeft.setSequence("MainMenu Death");
					glueSpriteLayerTopRight.setSequence("MainMenu Death");
					setMainMenuButtonsEnabled(true);
					setMainMenuVisible(false);
					menuState = MenuState.GOING_TO_BATTLE_NET_LOGIN;
				});
			}

			@Override
			public void channelMessage(final String userName, final String message) {
				Gdx.app.postRunnable(() -> battleNetUI.channelMessage(userName, message));
			}

			@Override
			public void channelEmote(final String userName, final String message) {
				Gdx.app.postRunnable(() -> battleNetUI.channelEmote(userName, message));
			}

			@Override
			public void badSession() {
				Gdx.app.postRunnable(() -> dialog.showError("ERROR_ID_NOTLOGGEDON", null));
			}

			@Override
			public void accountCreationOk() {
				Gdx.app.postRunnable(() -> battleNetUI.accountCreatedOk());
			}

			@Override
			public void accountCreationFailed(final AccountCreationFailureReason reason) {
				Gdx.app.postRunnable(() -> {
					switch (reason) {
						default:
						case USERNAME_ALREADY_EXISTS:
							dialog.showError("ERROR_ID_NAMEUSED", null);
							break;
						}
				});
			}

			@Override
			public void joinedGame(final String gameName) {
				Gdx.app.postRunnable(() -> {
					battleNetUI.joinedChannel(gameName);
					battleNetUI.hideCurrentScreen();
					playCurrentBattleNetGlueSpriteDeath();
					menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL;
					dialog.showError("bruh program the join game function", null);
				});
			}

			@Override
			public void joinGameFailed(final JoinGameFailureReason joinGameFailureReason) {
				Gdx.app.postRunnable(() -> {
					switch (joinGameFailureReason) {
					case GAME_ALREADY_STARTED:
						dialog.showError("ERROR_ID_GAMECLOSED", null);
						break;
					case GAME_FULL:
						dialog.showError("ERROR_ID_GAMEFULL", null);
						break;
					case NO_SUCH_GAME:
					default:
						dialog.showError("NETERROR_JOINGAMENOTFOUND", null);
						break;
					}
				});
			}

			@Override
			public void beginGamesList() {
				// TODO Auto-generated method stub

			}

			@Override
			public void gamesListItem(final String gameName, final int openSlots, final int totalSlots) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endGamesList() {
				// TODO Auto-generated method stub

			}
		});
	}

	public float getHeightRatioCorrection() {
		return heightRatioCorrection;
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		rootFrame = new GameUI(dataSource, GameUI.loadSkin(dataSource, WarsmashConstants.GAME_VERSION),
				uiViewport, uiScene, viewer, 0, WTS.DO_NOTHING);

		rootFrameListener.onCreate(rootFrame);
		try {
			rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}

		campaignStrings = new DataTable(StringBundle.EMPTY);
		final String campaignStringPath = rootFrame.trySkinField("CampaignFile");
		if (dataSource.has(campaignStringPath)) {
			try (InputStream campaignStringStream = dataSource.getResourceAsStream(campaignStringPath)) {
				campaignStrings.readTXT(campaignStringStream, true);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			try (InputStream campaignStringStream = dataSource
					.getResourceAsStream("UI\\CampaignInfoClassic.txt")) {
				campaignStrings.readTXT(campaignStringStream, true);
				unifiedCampaignInfo = true;
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Create main menu
		mainMenuFrame = rootFrame.createFrame("MainMenuFrame", rootFrame, 0, 0);

		warcraftIIILogo = (SpriteFrame) rootFrame.getFrameByName("WarCraftIIILogo", 0);
		rootFrame.setSpriteFrameModel(warcraftIIILogo, rootFrame.getSkinField("MainMenuLogo"));
		warcraftIIILogo.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mainMenuFrame, FramePoint.TOPLEFT,
				GameUI.convertX(uiViewport, 0.13f), GameUI.convertY(uiViewport, -0.08f)));
		setMainMenuVisible(false);
		rootFrame.getFrameByName("RealmSelect", 0).setVisible(false);

		glueSpriteLayerTopRight = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopRight", rootFrame, "", 0);
		glueSpriteLayerTopRight.setSetAllPoints(true);
		final String topRightModel = rootFrame.getSkinField("GlueSpriteLayerTopRight");
		rootFrame.setSpriteFrameModel(glueSpriteLayerTopRight, topRightModel);
		glueSpriteLayerTopRight.setSequence("MainMenu Birth");

		glueSpriteLayerTopLeft = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopLeft", rootFrame, "", 0);
		glueSpriteLayerTopLeft.setSetAllPoints(true);
		final String topLeftModel = rootFrame.getSkinField("GlueSpriteLayerTopLeft");
		rootFrame.setSpriteFrameModel(glueSpriteLayerTopLeft, topLeftModel);
		glueSpriteLayerTopLeft.setSequence("MainMenu Birth");

		glueSpriteLayerCenter = (SpriteFrame) rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerCenter", rootFrame, "", 0);
		glueSpriteLayerCenter.setSetAllPoints(true);
		final String centerModel = rootFrame.getSkinField("GlueSpriteLayerCenter");
		rootFrame.setSpriteFrameModel(glueSpriteLayerCenter, centerModel);
		glueSpriteLayerCenter.setVisible(false);

		cursorFrame = (SpriteFrame) rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", rootFrame,
				"", 0);
		rootFrame.setSpriteFrameModel(cursorFrame, rootFrame.getSkinField("Cursor"));
		cursorFrame.setSequence("Normal");
		cursorFrame.setZDepth(1024.0f);
		if (WarsmashConstants.CATCH_CURSOR) {
			Gdx.input.setCursorCatched(true);
		}

		battleNetConnectDialog = rootFrame.createFrame("BattleNetConnectDialog", rootFrame, 0, 0);
		battleNetConnectDialog.setVisible(false);
		battleNetConnectDialog.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
		battleNetConnectInfoText = (StringFrame) rootFrame.getFrameByName("ConnectInfoText", 0);
		battleNetConnectCancelButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ConnectButton", 0);
		battleNetConnectCancelButton.setOnClick(() -> {
			gamingNetworkConnection.userRequestDisconnect();
			battleNetConnectDialog.setVisible(false);
			setMainMenuButtonsEnabled(true);
		});

		// Main Menu interactivity
		singlePlayerButton = (GlueTextButtonFrame) rootFrame.getFrameByName("SinglePlayerButton", 0);
		battleNetButton = (GlueTextButtonFrame) rootFrame.getFrameByName("BattleNetButton", 0);
		realmButton = (GlueButtonFrame) rootFrame.getFrameByName("RealmButton", 0);
		localAreaNetworkButton = (GlueTextButtonFrame) rootFrame.getFrameByName("LocalAreaNetworkButton", 0);
		optionsButton = (GlueTextButtonFrame) rootFrame.getFrameByName("OptionsButton", 0);
		creditsButton = (GlueTextButtonFrame) rootFrame.getFrameByName("CreditsButton", 0);
		exitButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ExitButton", 0);
		editionButton = (GlueButtonFrame) rootFrame.getFrameByName("EditionButton", 0);

		if (editionButton != null) {
			editionButton.setOnClick(() -> {
				WarsmashConstants.GAME_VERSION = (WarsmashConstants.GAME_VERSION == 1 ? 0 : 1);
				glueSpriteLayerTopLeft.setSequence("MainMenu Death");
				glueSpriteLayerTopRight.setSequence("MainMenu Death");
				setMainMenuVisible(false);
				menuState = MenuState.RESTARTING;
			});
		}

		localAreaNetworkButton.setEnabled(false);
		optionsButton.setEnabled(false);
		creditsButton.setEnabled(false);

		exitButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("MainMenu Death");
			glueSpriteLayerTopRight.setSequence("MainMenu Death");
			setMainMenuVisible(false);
			menuState = MenuState.QUITTING;
		});

		singlePlayerButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("MainMenu Death");
			glueSpriteLayerTopRight.setSequence("MainMenu Death");
			setMainMenuVisible(false);
			menuState = MenuState.GOING_TO_SINGLE_PLAYER;
		});

		battleNetButton.setOnClick(() -> {
			setMainMenuButtonsEnabled(false);
			rootFrame.setDecoratedText(battleNetConnectInfoText, "BNET_CONNECT_INIT");
			battleNetConnectDialog.positionBounds(rootFrame, uiViewport);
			battleNetConnectDialog.setVisible(true);
			if (gamingNetworkConnection.userRequestConnect()) {
				gamingNetworkConnection.handshake(WarsmashConstants.getGameId(),
						GamingNetwork.GAME_VERSION_DATA);
			}
			else {
				battleNetConnectDialog.setVisible(false);
				setMainMenuButtonsEnabled(true);
				dialog.showError("ERROR_ID_CANTCONNECT", () -> {

				});
			}
		});
		realmButton.setOnClick(() -> battleNetConnectDialog.setVisible(true));

		// Create single player
		singlePlayerMenu = rootFrame.createFrame("SinglePlayerMenu", rootFrame, 0, 0);
		singlePlayerMenu.setVisible(false);

		profilePanel = rootFrame.getFrameByName("ProfilePanel", 0);
		profilePanel.setVisible(false);

		newProfileEditBox = (EditBoxFrame) rootFrame.getFrameByName("NewProfileEditBox", 0);
		newProfileEditBox.setOnChange(() -> addProfileButton
				.setEnabled(!profileManager.hasProfile(newProfileEditBox.getText())));
		final StringFrame profileListText = (StringFrame) rootFrame.getFrameByName("ProfileListText", 0);
		final SimpleFrame profileListContainer = (SimpleFrame) rootFrame.getFrameByName("ProfileListContainer", 0);
		final ListBoxFrame profileListBox = (ListBoxFrame) rootFrame.createFrameByType("LISTBOX", "ListBoxWar3",
				profileListContainer, "WITHCHILDREN", 0);
		profileListBox.setSetAllPoints(true);
		profileListBox.setFrameFont(profileListText.getFrameFont());
		for (final PlayerProfile profile : profileManager.getProfiles()) {
			profileListBox.addItem(profile.getName(), rootFrame, uiViewport);
		}
		profileListContainer.add(profileListBox);

		addProfileButton = (GlueTextButtonFrame) rootFrame.getFrameByName("AddProfileButton", 0);
		deleteProfileButton = (GlueTextButtonFrame) rootFrame.getFrameByName("DeleteProfileButton", 0);
		selectProfileButton = (GlueTextButtonFrame) rootFrame.getFrameByName("SelectProfileButton", 0);
		selectProfileButton.setEnabled(false);
		deleteProfileButton.setEnabled(false);
		addProfileButton.setOnClick(() -> {
			final String newProfileName = newProfileEditBox.getText();
			if (!newProfileName.isEmpty() && !profileManager.hasProfile(newProfileName)) {
				profileManager.addProfile(newProfileName);
				profileListBox.addItem(newProfileName, rootFrame, uiViewport);
				addProfileButton.setEnabled(false);
			}
		});
		deleteProfileButton.setOnClick(() -> {
			final int selectedIndex = profileListBox.getSelectedIndex();
			final boolean validSelect = (selectedIndex >= 0)
					&& (selectedIndex < profileManager.getProfiles().size());
			if (validSelect) {
				if (profileManager.getProfiles().size() > 1) {
					final PlayerProfile profileToRemove = profileManager.getProfiles()
							.get(selectedIndex);
					final String removeProfileName = profileToRemove.getName();
					final boolean deletingCurrentProfile = removeProfileName
							.equals(profileManager.getCurrentProfile());
					profileManager.removeProfile(profileToRemove);
					profileListBox.removeItem(selectedIndex, rootFrame, uiViewport);
					if (deletingCurrentProfile) {
						setCurrentProfile(profileManager.getProfiles().get(0).getName());
					}
				}
			}
		});
		selectProfileButton.setOnClick(() -> {
			final int selectedIndex = profileListBox.getSelectedIndex();
			final boolean validSelect = (selectedIndex >= 0)
					&& (selectedIndex < profileManager.getProfiles().size());
			if (validSelect) {
				final PlayerProfile profileToSelect = profileManager.getProfiles().get(selectedIndex);
				final String selectedProfileName = profileToSelect.getName();
				setCurrentProfile(selectedProfileName);

				glueSpriteLayerTopLeft.setSequence("RealmSelection Death");
				profilePanel.setVisible(false);
				menuState = MenuState.SINGLE_PLAYER;
				setSinglePlayerButtonsEnabled(false);
			}

		});
		profileListBox.setSelectionListener((newSelectedIndex, newSelectedItem) -> {
			final boolean validSelect = newSelectedItem != null;
			selectProfileButton.setEnabled(validSelect);
			deleteProfileButton.setEnabled(validSelect);
		});

		singlePlayerMainPanel = rootFrame.getFrameByName("MainPanel", 0);

		// Single Player Interactivity
		profileButton = (GlueButtonFrame) rootFrame.getFrameByName("ProfileButton", 0);
		campaignButton = (GlueTextButtonFrame) rootFrame.getFrameByName("CampaignButton", 0);
		loadSavedButton = (GlueTextButtonFrame) rootFrame.getFrameByName("LoadSavedButton", 0);
		viewReplayButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ViewReplayButton", 0);
		customCampaignButton = (GlueTextButtonFrame) rootFrame.getFrameByName("CustomCampaignButton", 0);
		skirmishButton = (GlueTextButtonFrame) rootFrame.getFrameByName("SkirmishButton", 0);

		singlePlayerCancelButton = (GlueTextButtonFrame) rootFrame.getFrameByName("CancelButton", 0);

		profileNameText = (StringFrame) rootFrame.getFrameByName("ProfileNameText", 0);
		rootFrame.setText(profileNameText, profileManager.getCurrentProfile());

		setSinglePlayerButtonsEnabled(true);

		profileButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("RealmSelection Birth");
			setSinglePlayerButtonsEnabled(false);
			menuState = MenuState.SINGLE_PLAYER_PROFILE;
		});

		campaignButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
			glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
			singlePlayerMenu.setVisible(false);
			profilePanel.setVisible(false);
			menuState = MenuState.GOING_TO_CAMPAIGN;
		});

		skirmishButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
			glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
			singlePlayerMenu.setVisible(false);
			profilePanel.setVisible(false);
			menuState = MenuState.GOING_TO_SINGLE_PLAYER_SKIRMISH;
		});

		singlePlayerCancelButton.setOnClick(() -> {
			if (menuState == MenuState.SINGLE_PLAYER_PROFILE) {
				glueSpriteLayerTopLeft.setSequence("RealmSelection Death");
				profilePanel.setVisible(false);
			}
			else {
				glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
			}
			glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
			singlePlayerMenu.setVisible(false);
			menuState = MenuState.GOING_TO_MAIN_MENU;
		});

		// Create skirmish UI
		skirmish = rootFrame.createFrame("Skirmish", rootFrame, 0, 0);
		skirmish.setVisible(false);

		mapInfoButton = (GlueTextButtonFrame) rootFrame.getFrameByName("MapInfoButton", 0);
		advancedOptionsButton = (GlueTextButtonFrame) rootFrame.getFrameByName("AdvancedOptionsButton", 0);
		mapInfoPanel = rootFrame.getFrameByName("MapInfoPanel", 0);
		advancedOptionsPanel = rootFrame.getFrameByName("AdvancedOptionsPanel", 0);
		final SimpleFrame mapInfoPaneContainer = (SimpleFrame) rootFrame.getFrameByName("MapInfoPaneContainer", 0);
		final SimpleFrame advancedOptionsPaneContainer = (SimpleFrame) rootFrame
				.getFrameByName("AdvancedOptionsPaneContainer", 0);
		skirmishAdvancedOptionsPane = rootFrame.createFrame("AdvancedOptionsPane",
				advancedOptionsPaneContainer, 0, 0);
		skirmishAdvancedOptionsPane.setSetAllPoints(true);
		advancedOptionsPaneContainer.add(skirmishAdvancedOptionsPane);
		skirmishMapInfoPane = new MapInfoPane(rootFrame, uiViewport, mapInfoPaneContainer);

		final SimpleFrame teamSetupContainer = (SimpleFrame) rootFrame.getFrameByName("TeamSetupContainer", 0);
		final TeamSetupPane teamSetupPane = new TeamSetupPane(rootFrame, uiViewport, teamSetupContainer);

		final GlueTextButtonFrame playGameButton = (GlueTextButtonFrame) rootFrame.getFrameByName("PlayGameButton",
				0);
		final MapListContainer mapListContainer = new MapListContainer(rootFrame, uiViewport,
				"MapListContainer", dataSource, profileListText.getFrameFont());
		mapListContainer.addSelectionListener((newSelectedIndex, newSelectedItem) -> {
			if (newSelectedItem != null) {
				try {
					final War3Map map = War3MapViewer.beginLoadingMap(dataSource, newSelectedItem);
					final War3MapW3i mapInfo = map.readMapInformation();
					final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
					rootFrame.setMapStrings(wtsFile);
					final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
					for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
						final CBasePlayer player = war3MapConfig.getPlayer(i);
						player.setName(rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
					}
					Jass2.loadConfig(map, uiViewport, uiScene, rootFrame,
							war3MapConfig, "Scripts\\common.j", "Scripts\\Blizzard.j", "Scripts\\war3map.j")
							.config();
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CBasePlayer player = war3MapConfig.getPlayer(i);
						if (player.getController() == CMapControl.USER) {
							player.setSlotState(CPlayerSlotState.PLAYING);
							player.setName(profileManager.getCurrentProfile());
							break;
						}
					}
					skirmishMapInfoPane.setMap(rootFrame, uiViewport, map,
							mapInfo, war3MapConfig);
					teamSetupPane.setMap(map, rootFrame, uiViewport, war3MapConfig,
							mapInfo.getPlayers().size(), mapInfo);
					currentMapConfig = war3MapConfig;
				}
				catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
		playGameButton.setOnClick(() -> {
			final String selectedItem = mapListContainer.getSelectedItem();
			if (selectedItem != null) {
				campaignMenu.setVisible(false);
				campaignBackButton.setVisible(false);
				missionSelectFrame.setVisible(false);
				campaignSelectFrame.setVisible(false);
				campaignWarcraftIIILogo.setVisible(false);
				campaignRootMenuUI.setVisible(false);
				currentMissionSelectMenuUI.setVisible(false);
				skirmish.setVisible(false);
				glueSpriteLayerTopLeft.setSequence("Death");
				glueSpriteLayerTopRight.setSequence("Death");
				mapFilepathToStart = selectedItem;
				menuState = MenuState.GOING_TO_MAP;
			}

		});

		skirmishCancelButton = (GlueTextButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		skirmishCancelButton.setOnClick(() -> {
			glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Death");
			glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Death");
			skirmish.setVisible(false);
			menuState = MenuState.GOING_TO_SINGLE_PLAYER;

		});

		// Create Campaign UI

		campaignMenu = rootFrame.createFrame("CampaignMenu", rootFrame, 0, 0);
		campaignMenu.setVisible(false);
		campaignFade = (SpriteFrame) rootFrame.getFrameByName("SlidingDoors", 0);
		campaignFade.setVisible(false);
		campaignBackButton = (GlueTextButtonFrame) rootFrame.getFrameByName("BackButton", 0);
		campaignBackButton.setVisible(false);
		missionSelectFrame = rootFrame.getFrameByName("MissionSelectFrame", 0);
		missionSelectFrame.setVisible(false);
		final StringFrame missionName = (StringFrame) rootFrame.getFrameByName("MissionName", 0);
		final StringFrame missionNameHeader = (StringFrame) rootFrame.getFrameByName("MissionNameHeader", 0);

		campaignSelectFrame = rootFrame.getFrameByName("CampaignSelectFrame", 0);
		campaignSelectFrame.setVisible(false);

		campaignWarcraftIIILogo = (SpriteFrame) rootFrame.getFrameByName("WarCraftIIILogo", 0);
		rootFrame.setSpriteFrameModel(campaignWarcraftIIILogo, rootFrame.getSkinField("MainMenuLogo"));
		campaignWarcraftIIILogo.setVisible(false);
		campaignWarcraftIIILogo
				.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, campaignMenu, FramePoint.TOPRIGHT,
						GameUI.convertX(uiViewport, -0.13f), GameUI.convertY(uiViewport, -0.08f)));
		campaignRootMenuUI = new CampaignMenuUI(null, campaignMenu, rootFrame, uiViewport);
		campaignRootMenuUI.setVisible(false);
		campaignRootMenuUI.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, campaignMenu, FramePoint.TOPRIGHT,
				GameUI.convertX(uiViewport, -0.0f), GameUI.convertY(uiViewport, -0.12f)));
		campaignRootMenuUI.setWidth(GameUI.convertX(uiViewport, 0.30f));
		campaignRootMenuUI.setHeight(GameUI.convertY(uiViewport, 0.42f));
		rootFrame.add(campaignRootMenuUI);

		campaignBackButton.setOnClick(() -> {
			if (menuState == MenuState.MISSION_SELECT) {
				currentMissionSelectMenuUI.setVisible(false);
				missionSelectFrame.setVisible(false);
				menuState = MenuState.CAMPAIGN;
			}
			else {
				campaignMenu.setVisible(false);
				campaignBackButton.setVisible(false);
				missionSelectFrame.setVisible(false);
				campaignSelectFrame.setVisible(false);
				campaignWarcraftIIILogo.setVisible(false);
				campaignRootMenuUI.setVisible(false);
				campaignFade.setSequence("Birth");
				menuState = MenuState.LEAVING_CAMPAIGN;
			}
		});
		final Element campaignIndex = campaignStrings.get("Index");
		campaignList = campaignIndex.getField("CampaignList").split(",");
		campaignDatas = new CampaignMenuData[campaignList.length];
		for (int i = 0; i < campaignList.length; i++) {
			final String campaign = campaignList[i];
			final Element campaignElement = campaignStrings.get(campaign);
			if (campaignElement != null) {
				final CampaignMenuData newCampaign = new CampaignMenuData(campaignElement);
				campaignDatas[i] = newCampaign;
				if (currentCampaign == null) {
					currentCampaign = newCampaign;
				}

			}
		}
		for (final CampaignMenuData campaign : campaignDatas) {
			if (campaign != null) {
				final CampaignMenuUI missionSelectMenuUI = new CampaignMenuUI(null, campaignMenu, rootFrame,
						uiViewport);
				missionSelectMenuUI.setVisible(false);
				missionSelectMenuUI
						.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, campaignMenu, FramePoint.TOPRIGHT,
								GameUI.convertX(uiViewport, -0.0f), GameUI.convertY(uiViewport, -0.12f)));
				missionSelectMenuUI.setWidth(GameUI.convertX(uiViewport, 0.30f));
				missionSelectMenuUI.setHeight(GameUI.convertY(uiViewport, 0.42f));
				rootFrame.add(missionSelectMenuUI);

				for (final CampaignMission mission : campaign.getMissions()) {
					missionSelectMenuUI.addButton(mission.getHeader(), mission.getMissionName(), () -> {
						campaignMenu.setVisible(false);
						campaignBackButton.setVisible(false);
						missionSelectFrame.setVisible(false);
						campaignSelectFrame.setVisible(false);
						campaignWarcraftIIILogo.setVisible(false);
						campaignRootMenuUI.setVisible(false);
						currentMissionSelectMenuUI.setVisible(false);
						campaignFade.setSequence("Birth");
						mapFilepathToStart = mission.getMapFilename();
					});
				}

				campaignRootMenuUI.addButton(campaign.getHeader(), campaign.getName(), () -> {
					if (!campaign.equals(currentCampaign)) {
						campaignMenu.setVisible(false);
						campaignBackButton.setVisible(false);
						missionSelectFrame.setVisible(false);
						campaignSelectFrame.setVisible(false);
						campaignWarcraftIIILogo.setVisible(false);
						campaignRootMenuUI.setVisible(false);
						campaignFade.setSequence("Birth");
						currentCampaign = campaign;
						currentMissionSelectMenuUI = missionSelectMenuUI;
						menuState = MenuState.GOING_TO_MISSION_SELECT;
					}
					else {
						campaignSelectFrame.setVisible(false);
						campaignRootMenuUI.setVisible(false);
						currentMissionSelectMenuUI.setVisible(true);
						missionSelectFrame.setVisible(true);
						menuState = MenuState.MISSION_SELECT;
					}
					rootFrame.setDecoratedText(missionName, campaign.getName());
					rootFrame.setDecoratedText(missionNameHeader, campaign.getHeader());
				});
				if (campaign.equals(currentCampaign)) {
					currentMissionSelectMenuUI = missionSelectMenuUI;
				}
			}
		}

		confirmDialog = rootFrame.createFrame("DialogWar3", rootFrame, 0, 0);
		confirmDialog.setVisible(false);

		loadingFrame = rootFrame.createFrame("Loading", rootFrame, 0, 0);
		loadingFrame.setVisible(false);
		loadingCustomPanel = rootFrame.getFrameByName("LoadingCustomPanel", 0);
		loadingCustomPanel.setVisible(false);
		loadingTitleText = (StringFrame) rootFrame.getFrameByName("LoadingTitleText", 0);
		loadingSubtitleText = (StringFrame) rootFrame.getFrameByName("LoadingSubtitleText", 0);
		loadingText = (StringFrame) rootFrame.getFrameByName("LoadingText", 0);
		loadingBar = (SpriteFrame) rootFrame.getFrameByName("LoadingBar", 0);
		loadingBackground = (SpriteFrame) rootFrame.getFrameByName("LoadingBackground", 0);

		loadingMeleePanel = rootFrame.getFrameByName("LoadingMeleePanel", 0);
		loadingMeleePanel.setVisible(false);

		battleNetUI = new BattleNetUI(rootFrame, uiViewport, dataSource,
				new BattleNetUIActionListener() {
					@Override
					public void cancelLoginPrompt() {
						battleNetUI.hide();
						battleNetUI.getDoors().setSequence(PrimaryTag.DEATH);
						menuScreen.unAlternateModelBackToNormal();
						menuState = MenuState.LEAVING_BATTLE_NET;
						gamingNetworkConnection.userRequestDisconnect();
					}

					@Override
					public void recoverPassword(final String text) {

					}

					@Override
					public void logon(final String accountName, final String password) {
						if (accountName.isEmpty()) {
							dialog.showError("ERROR_ID_NAMEBLANK", null);
						}
						else if (password.isEmpty()) {
							dialog.showError("NETERROR_NOPASSWORD", null);
						}
						else {
							final char[] passwordData = getPasswordData(password);
							gamingNetworkConnection.login(accountName, passwordData);
						}
					}

					private char[] getPasswordData(final String password) {
						final int nPasswordChars = password.length();
						final char[] passwordData = new char[nPasswordChars];
						password.getChars(0, nPasswordChars, passwordData, 0);
						return passwordData;
					}

					@Override
					public void quitBattleNet() {
						battleNetUI.hide();
						playCurrentBattleNetGlueSpriteDeath();
						glueSpriteLayerCenter.setSequence("Death");
						menuState = MenuState.LEAVING_BATTLE_NET_FROM_LOGGED_IN;
						gamingNetworkConnection.userRequestDisconnect();
					}

					@Override
					public void openCustomGameMenu() {
						battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						final boolean insideTopBarMode = isInsideTopBarMode();
						if (insideTopBarMode) {
							glueSpriteLayerCenter.setSequence("Death");
						}
						menuState = MenuState.GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU;
					}

					@Override
					public void enterDefaultChat() {
						gamingNetworkConnection.joinChannel(
								battleNetUI.getGamingNetworkSessionToken(), "Frozen Throne USA-1"); // TODO
																												// maybe
																												// not
																												// hardcode
																												// this
					}

					@Override
					public void returnToChat() {
						battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						if (battleNetUI.getCurrentChannel() == null) {
							menuState = MenuState.GOING_TO_BATTLE_NET_WELCOME;
						}
						else {
							final boolean insideTopBarMode = isInsideTopBarMode();
							if (insideTopBarMode) {
								menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL;
							}
							else {
								menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE;
							}
						}
					}

					@Override
					public void requestJoinChannel(final String text) {
						gamingNetworkConnection
								.joinChannel(battleNetUI.getGamingNetworkSessionToken(), text);
					}

					@Override
					public void createAccount(final String username, final String password,
							final String repeatPassword) {
						if (!password.equals(repeatPassword)) {
							dialog.showError("NETERROR_PASSWORDMISMATCH", null);
						}
						else if (username.isEmpty()) {
							dialog.showError("ERROR_ID_NAMEBLANK", null);
						}
						else if (password.isEmpty()) {
							dialog.showError("NETERROR_NOPASSWORD", null);
						}
						else if (username.length() < 3) {
							// TODO checks like this should be server side!!!
							dialog.showError("NETERROR_USERNAMETOOSHORT", null);
						}
						else if (password.length() < 3) {
							dialog.showError("NETERROR_PASSWORDTOOSHORT", null);
						}
						else {
							final char[] passwordData = getPasswordData(password);
							gamingNetworkConnection.createAccount(username, passwordData);
						}
					}

					@Override
					public void submitChatText(final String text) {
						if (text.startsWith("/me ")) {
							gamingNetworkConnection.emoteMessage(
									battleNetUI.getGamingNetworkSessionToken(), text.substring(4));
						}
						else {
							gamingNetworkConnection
									.chatMessage(battleNetUI.getGamingNetworkSessionToken(), text);
						}
					}

					@Override
					public void showChannelChooserPanel() {
						battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						menuState = MenuState.GOING_TO_BATTLE_NET_CHANNEL_MENU;

					}

					@Override
					public void showCreateGameMenu() {
						battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						menuState = MenuState.GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU;
					}

					@Override
					public void requestJoinGame(final String text) {
						gamingNetworkConnection
								.joinGame(battleNetUI.getGamingNetworkSessionToken(), text);
					}

					@Override
					public void showError(final String errorKey) {
						dialog.showError(errorKey, null);
					}
				});

		dialog = new DialogWar3(rootFrame, uiViewport);

		// position all
		rootFrame.positionBounds(rootFrame, uiViewport);

		menuState = MenuState.GOING_TO_MAIN_MENU;

		loadSounds();

		final String glueLoopField = rootFrame.getSkinField("GlueScreenLoop");
		mainMenuGlueScreenLoop = uiSounds.getSound(glueLoopField);
		glueScreenLoop = mainMenuGlueScreenLoop;
		glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
	}

	public void show() {
		playMusic(rootFrame.trySkinField("GlueMusic"), true, 0);
		glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
	}

	private void internalStartMap(final String mapFilename) {
		loadingFrame.setVisible(true);
		loadingBar.setVisible(true);
		loadingCustomPanel.setVisible(true);
		final DataSource codebase = WarsmashGdxMapScreen.parseDataSources(warsmashIni);
		final GameTurnManager turnManager = GameTurnManager.PAUSED;
		final War3MapViewer viewer = new War3MapViewer(codebase, screenManager, currentMapConfig,
				turnManager);

		if (WarsmashGdxMapScreen.ENABLE_AUDIO) {
			viewer.worldScene.enableAudio();
			viewer.enableAudio();
		}
		try {
			final War3Map map = War3MapViewer.beginLoadingMap(codebase, mapFilename);
			final War3MapW3i mapInfo = map.readMapInformation();
			final DataTable worldEditData = viewer.loadWorldEditData(map);
			final WTS wts = viewer.preloadWTS(map);

			final int loadingScreen = mapInfo.getLoadingScreen();
			System.out.println("LOADING SCREEN INT: " + loadingScreen);
			final int campaignBackground = mapInfo.getCampaignBackground();
			final Element loadingScreens = worldEditData.get("LoadingScreens");
			final String key = String.format("%2s", campaignBackground).replace(' ', '0');
			final int animationSequenceIndex = loadingScreens.getFieldValue(key, 2);
			final String campaignScreenModel = loadingScreens.getField(key, 3);

			menuScreen.setModel(null);
			rootFrame.setSpriteFrameModel(loadingBackground, campaignScreenModel);
			loadingBackground.setSequence(animationSequenceIndex);
			rootFrame.setSpriteFrameModel(loadingBar, rootFrame.getSkinField("LoadingProgressBar"));
			loadingBar.setSequence(0);
			loadingBar.setFrameByRatio(0.5f);
			loadingBar.setZDepth(0.25f);
			rootFrame.setText(loadingTitleText, getStringWithWTS(wts, mapInfo.getLoadingScreenTitle()));
			rootFrame.setText(loadingSubtitleText, getStringWithWTS(wts, mapInfo.getLoadingScreenSubtitle()));
			loadingText.setJustifyV(TextJustify.TOP);
			rootFrame.setText(loadingText, getStringWithWTS(wts, mapInfo.getLoadingScreenText()));
			loadingMap = new LoadingMap(viewer, map, mapInfo);

		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getStringWithWTS(final WTS wts, String string) {
		if (string.startsWith("TRIGSTR_")) {
			string = wts.get(Integer.parseInt(string.substring(8)));
		}
		return string;
	}

	public void startMap(final String mapFilename) {
		mainMenuFrame.setVisible(false);

		try {
			final War3Map map = War3MapViewer.beginLoadingMap(dataSource, mapFilename);
			final War3MapW3i mapInfo = map.readMapInformation();
			final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
			rootFrame.setMapStrings(wtsFile);
			final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
			for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
				final CBasePlayer player = war3MapConfig.getPlayer(i);
				player.setName(rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
			}
			Jass2.loadConfig(map, uiViewport, uiScene, rootFrame, war3MapConfig,
					"Scripts\\common.j", "Scripts\\Blizzard.j", "war3map.j").config();
			for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
				final CBasePlayer player = war3MapConfig.getPlayer(i);
				if (player.getController() == CMapControl.USER) {
					player.setSlotState(CPlayerSlotState.PLAYING);
//					player.setName(MenuUI.this.profileManager.getCurrentProfile());
//					break;
				}
			}
			currentMapConfig = war3MapConfig;
		}
		catch (final IOException e) {
			e.printStackTrace();
		}

		internalStartMap(mapFilename);
	}

	private void setCurrentProfile(final String selectedProfileName) {
		profileManager.setCurrentProfile(selectedProfileName);
		rootFrame.setText(profileNameText, selectedProfileName);
	}

	protected void setSinglePlayerButtonsEnabled(final boolean b) {
		profileButton.setEnabled(b);
		campaignButton.setEnabled(b);
		loadSavedButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		viewReplayButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		customCampaignButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		skirmishButton.setEnabled(b);
		singlePlayerCancelButton.setEnabled(b);
	}

	private void setMainMenuButtonsEnabled(final boolean b) {
		singlePlayerButton.setEnabled(b);
		battleNetButton.setEnabled(b);
		realmButton.setEnabled(b);
		localAreaNetworkButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		optionsButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		creditsButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		exitButton.setEnabled(b);
		if (editionButton != null) {
			editionButton.setEnabled(b);
		}
	}

	private void setMainMenuVisible(final boolean visible) {
		mainMenuFrame.setVisible(visible);
		warcraftIIILogo.setVisible(visible);
	}

	public void resize() {

	}

	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		final BitmapFont font = rootFrame.getFont();
		final BitmapFont font20 = rootFrame.getFont20();
		font.setColor(Color.YELLOW);
		final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
		glyphLayout.setText(font, fpsString);
		font.draw(batch, fpsString, (getMinWorldWidth() - glyphLayout.width) / 2, 1100 * heightRatioCorrection);
		rootFrame.render(batch, font20, glyphLayout);
	}

	private float getMinWorldWidth() {
		if (uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) uiViewport).getMinWorldWidth();
		}
		return uiViewport.getWorldWidth();
	}

	private float getMinWorldHeight() {
		if (uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) uiViewport).getMinWorldHeight();
		}
		return uiViewport.getWorldHeight();
	}

	public void update(final float deltaTime) {
		if ((mapFilepathToStart != null) && (menuState != MenuState.GOING_TO_MAP)) {
			campaignFade.setVisible(false);
			internalStartMap(mapFilepathToStart);
			mapFilepathToStart = null;
			return;
		}
		if (loadingMap != null) {
			int localPlayerIndex = MultiplayerHack.LP_VAL;
			try {
				loadingMap.viewer.loadMap(loadingMap.map, loadingMap.mapInfo, localPlayerIndex);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			// TODO not cast menu screen
			CPlayerUnitOrderListener uiOrderListener;
			final WarsmashClient warsmashClient;
			if (MultiplayerHack.MULTIPLAYER_HACK_SERVER_ADDR != null) {
				try {
					warsmashClient = new WarsmashClient(
							InetAddress.getByName(MultiplayerHack.MULTIPLAYER_HACK_SERVER_ADDR),
							loadingMap.viewer);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
				final WarsmashClientWriter warsmashClientWriter = warsmashClient.getWriter();
				warsmashClientWriter.joinGame();
				warsmashClientWriter.send();
				uiOrderListener = new WarsmashClientSendingOrderListener(warsmashClientWriter);
			}
			else {
				final War3MapViewer mapViewer = loadingMap.viewer;
				for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
					final CBasePlayer configPlayer = mapViewer.getMapConfig().getPlayer(i);
					if ((configPlayer.getSlotState() == CPlayerSlotState.PLAYING)
							&& (configPlayer.getController() == CMapControl.USER)) {
						localPlayerIndex = i;
						break;
					}
				}
				mapViewer.setLocalPlayerIndex(localPlayerIndex);
				final CPlayerUnitOrderExecutor executor = new CPlayerUnitOrderExecutor(
						loadingMap.viewer.simulation, localPlayerIndex);
				final CPlayerUnitOrderListenerDelaying delayingListener = new CPlayerUnitOrderListenerDelaying(
						executor);
				uiOrderListener = delayingListener;
				warsmashClient = null;
				mapViewer.setGameTurnManager(new GameTurnManager() {
					@Override
					public void turnCompleted(final int gameTurnTick) {
						delayingListener.publishDelayedActions();
					}

					@Override
					public int getLatestCompletedTurn() {
						return Integer.MAX_VALUE;
					}

					@Override
					public void framesSkipped(final float skippedCount) {

					}
				});
			}

			screenManager.setScreen(new WarsmashGdxMapScreen(loadingMap.viewer, screenManager,
					(WarsmashGdxMenuScreen) menuScreen, uiOrderListener));
			loadingMap = null;

			loadingBar.setVisible(false);
			loadingFrame.setVisible(false);
			loadingBackground.setVisible(false);
			if (MultiplayerHack.MULTIPLAYER_HACK_SERVER_ADDR != null) {
				warsmashClient.startThread();
			}
			return;
		}
		if (currentMusics != null) {
			if (!currentMusics[currentMusicIndex].isPlaying()) {
				if (currentMusicRandomizeIndex) {
					currentMusicIndex = (int) (Math.random() * currentMusics.length);
				}
				else {
					currentMusicIndex = (currentMusicIndex + 1) % currentMusics.length;
				}
				currentMusics[currentMusicIndex].play();
			}
		}
		if ((focusUIFrame != null) && !focusUIFrame.isVisibleOnScreen()) {
			setFocusFrame(getNextFocusFrame());
		}

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		final int minX = uiViewport.getScreenX();
		final int maxX = minX + uiViewport.getScreenWidth();
		final int minY = uiViewport.getScreenY();
		final int maxY = minY + uiViewport.getScreenHeight();

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		int mouseY = baseMouseY;
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			if (WarsmashConstants.CATCH_CURSOR) {
				Gdx.input.setCursorPosition(mouseX, mouseY);
			}
		}

		screenCoordsVector.set(mouseX, mouseY);
		uiViewport.unproject(screenCoordsVector);
		cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);
		cursorFrame.setSequence("Normal");

		if (glueSpriteLayerTopRight.isSequenceEnded() && glueSpriteLayerTopLeft.isSequenceEnded()
				&& (!campaignFade.isVisible() || campaignFade.isSequenceEnded())
				&& (!battleNetUI.getDoors().isVisible() || battleNetUI.getDoors().isSequenceEnded())) {
			switch (menuState) {
				case GOING_TO_MAIN_MENU:
					glueSpriteLayerTopLeft.setSequence("MainMenu Birth");
					glueSpriteLayerTopRight.setSequence("MainMenu Birth");
					if (battleNetUI.getDoors().isVisible()) {
						battleNetUI.getDoors().setVisible(false);
						battleNetUI.setVisible(false);
					}
					menuState = MenuState.MAIN_MENU;
					break;
				case MAIN_MENU:
					setMainMenuVisible(true);
					glueSpriteLayerTopLeft.setSequence("MainMenu Stand");
					glueSpriteLayerTopRight.setSequence("MainMenu Stand");
					break;
				case GOING_TO_BATTLE_NET_LOGIN:
					glueSpriteLayerTopLeft.setSequence("Death");
					glueSpriteLayerTopRight.setSequence("Death");
					battleNetUI.setVisible(true);
					final SpriteFrame doors = battleNetUI.getDoors();
					doors.setVisible(true);
					doors.setSequence(PrimaryTag.BIRTH);
					menuState = MenuState.GOING_TO_BATTLE_NET_LOGIN_PART2;
					break;
				case GOING_TO_BATTLE_NET_LOGIN_PART2:
					menuScreen.alternateModelToBattlenet();
					battleNetUI.showLoginPrompt(gamingNetworkConnection.getGatewayString());
					menuState = MenuState.BATTLE_NET_LOGIN;
					break;
				case LEAVING_BATTLE_NET_FROM_LOGGED_IN:
					menuScreen.unAlternateModelBackToNormal();
					glueSpriteLayerCenter.setVisible(false);
					playMusic(rootFrame.trySkinField("GlueMusic"), true, 0);
					// no break
				case LEAVING_BATTLE_NET:
					glueSpriteLayerTopLeft.setSequence("Birth");
					glueSpriteLayerTopRight.setSequence("Birth");
					menuState = MenuState.GOING_TO_MAIN_MENU;
					break;
				case GOING_TO_BATTLE_NET_WELCOME:
					glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Birth");
					glueSpriteLayerTopRight.setSequence("BattleNetWelcome Birth");
					glueSpriteLayerCenter.setVisible(true);
					glueSpriteLayerCenter.setSequence("Birth");
					menuState = MenuState.BATTLE_NET_WELCOME;
					playMusic(rootFrame.trySkinField("ChatMusic"), true, 0);
					break;
				case BATTLE_NET_WELCOME:
					glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Stand");
					glueSpriteLayerTopRight.setSequence("BattleNetWelcome Stand");
					battleNetUI.showWelcomeScreen();
					glueSpriteLayerCenter.setSequence("Stand");
					menuState = MenuState.BATTLE_NET_WELCOME;
					break;
				case GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU:
					glueSpriteLayerTopLeft.setSequence("BattleNetCustom Birth");
					glueSpriteLayerTopRight.setSequence("BattleNetCustom Birth");
					menuState = MenuState.BATTLE_NET_CUSTOM_GAME_MENU;
					break;
				case BATTLE_NET_CUSTOM_GAME_MENU:
					battleNetUI.showCustomGameMenu();
					glueSpriteLayerTopLeft.setSequence("BattleNetCustom Stand");
					glueSpriteLayerTopRight.setSequence("BattleNetCustom Stand");
					break;
				case GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
					glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Birth");
					glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Birth");
					menuState = MenuState.BATTLE_NET_CREATE_CUSTOM_GAME_MENU;
					break;
				case BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
					battleNetUI.showCustomGameCreateMenu();
					glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Stand");
					glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Stand");
					break;
				case GOING_TO_BATTLE_NET_CHANNEL_MENU:
					glueSpriteLayerTopLeft.setSequence("BattleNetChannel Birth");
					glueSpriteLayerTopRight.setSequence("BattleNetChannel Birth");
					menuState = MenuState.BATTLE_NET_CHANNEL_MENU;
					break;
				case BATTLE_NET_CHANNEL_MENU:
					battleNetUI.showChannelMenu();
					glueSpriteLayerTopLeft.setSequence("BattleNetChannel Stand");
					glueSpriteLayerTopRight.setSequence("BattleNetChannel Stand");
					break;
				case GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE:
					glueSpriteLayerCenter.setVisible(true);
					glueSpriteLayerCenter.setSequence("Birth");
					break;
				case GOING_TO_BATTLE_NET_CHAT_CHANNEL:
					glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Birth");
					glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Birth");
					menuState = MenuState.BATTLE_NET_CHAT_CHANNEL;
					break;
				case BATTLE_NET_CHAT_CHANNEL:
					battleNetUI.showChatChannel();
					glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Stand");
					glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Stand");
					break;
				case GOING_TO_SINGLE_PLAYER:
					glueSpriteLayerTopLeft.setSequence("SinglePlayer Birth");
					glueSpriteLayerTopRight.setSequence("SinglePlayer Birth");
					menuState = MenuState.SINGLE_PLAYER;
					break;
				case GOING_TO_MAP:
					menuState = MenuState.SINGLE_PLAYER;
					break;
				case LEAVING_CAMPAIGN:
					glueSpriteLayerTopLeft.setSequence("Birth");
					glueSpriteLayerTopRight.setSequence("Birth");
					if (campaignFade.isVisible()) {
						campaignFade.setSequence("Death");
					}
					glueScreenLoop.stop();
					glueScreenLoop = mainMenuGlueScreenLoop;
					glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
					menuScreen.setModel(rootFrame.getSkinField("GlueSpriteLayerBackground"));
					rootFrame.setSpriteFrameModel(cursorFrame, rootFrame.getSkinField("Cursor"));
					menuState = MenuState.GOING_TO_SINGLE_PLAYER;
					break;
				case SINGLE_PLAYER:
					singlePlayerMenu.setVisible(true);
					campaignFade.setVisible(false);
					setSinglePlayerButtonsEnabled(true);
					glueSpriteLayerTopLeft.setSequence("SinglePlayer Stand");
					glueSpriteLayerTopRight.setSequence("SinglePlayer Stand");
					break;
				case GOING_TO_SINGLE_PLAYER_SKIRMISH:
					glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Birth");
					glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Birth");
					menuState = MenuState.SINGLE_PLAYER_SKIRMISH;
					break;
				case SINGLE_PLAYER_SKIRMISH:
					skirmish.setVisible(true);
					mapInfoPanel.setVisible(true);
					advancedOptionsPanel.setVisible(false);
					glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Stand");
					glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Stand");
					break;
				case GOING_TO_CAMPAIGN:
					glueSpriteLayerTopLeft.setSequence("Death");
					glueSpriteLayerTopRight.setSequence("Death");
					campaignMenu.setVisible(true);
					campaignFade.setVisible(true);
					campaignFade.setSequence("Birth");
					menuState = MenuState.GOING_TO_CAMPAIGN_PART2;
					break;
				case GOING_TO_CAMPAIGN_PART2: {
					final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
					final String currentCampaignAmbientSound = rootFrame
							.trySkinField(currentCampaign.getAmbientSound());
					menuScreen.setModel(currentCampaignBackgroundModel);
					glueScreenLoop.stop();
					glueScreenLoop = uiSounds.getSound(currentCampaignAmbientSound);
					glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
					final DataTable skinData = rootFrame.getSkinData();
					final String cursorSkin = getRaceNameByCursorID(currentCampaign.getCursor());
					rootFrame.setSpriteFrameModel(cursorFrame, skinData.get(cursorSkin).getField("Cursor"));

					campaignFade.setSequence("Death");
					menuState = MenuState.CAMPAIGN;
					break;
				}
				case CAMPAIGN:
					campaignMenu.setVisible(true);
					campaignBackButton.setVisible(true);
					campaignWarcraftIIILogo.setVisible(true);
					campaignSelectFrame.setVisible(true);
					campaignRootMenuUI.setVisible(true);
					break;
				case GOING_TO_MISSION_SELECT: {
					final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
					final String currentCampaignAmbientSound = rootFrame
							.trySkinField(currentCampaign.getAmbientSound());
					menuScreen.setModel(currentCampaignBackgroundModel);
					glueScreenLoop.stop();
					glueScreenLoop = uiSounds.getSound(currentCampaignAmbientSound);
					glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
					final DataTable skinData = rootFrame.getSkinData();
					final String cursorSkin = getRaceNameByCursorID(currentCampaign.getCursor());
					rootFrame.setSpriteFrameModel(cursorFrame, skinData.get(cursorSkin).getField("Cursor"));

					campaignFade.setSequence("Death");
					menuState = MenuState.MISSION_SELECT;
					break;
				}
				case MISSION_SELECT:
					campaignMenu.setVisible(true);
					campaignBackButton.setVisible(true);
					campaignWarcraftIIILogo.setVisible(true);
					currentMissionSelectMenuUI.setVisible(true);
					missionSelectFrame.setVisible(true);
					break;
				case GOING_TO_SINGLE_PLAYER_PROFILE:
					glueSpriteLayerTopLeft.setSequence("RealmSelection Birth");
					menuState = MenuState.SINGLE_PLAYER_PROFILE;
					break;
				case SINGLE_PLAYER_PROFILE:
					profilePanel.setVisible(true);
					setSinglePlayerButtonsEnabled(true);
					glueSpriteLayerTopLeft.setSequence("RealmSelection Stand");
					// TODO the below should probably be some generic focusing thing when we enter a
					// new view?
					if ((newProfileEditBox != null) && newProfileEditBox.isFocusable()) {
						setFocusFrame(newProfileEditBox);
					}
					break;
				case QUITTING:
					Gdx.app.exit();
					break;
				case RESTARTING:
					screenManager
							.setScreen(new WarsmashGdxMenuScreen(warsmashIni, screenManager));
					break;
				default:
					break;
			}
		}

	}

	private FocusableFrame getNextFocusFrame() {
		return rootFrame.getNextFocusFrame();
	}

	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame != null) {
			if (clickedUIFrame instanceof ClickableFrame) {
				mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				mouseDownUIFrame.mouseDown(rootFrame, uiViewport);
			}
			if (clickedUIFrame instanceof FocusableFrame) {
				final FocusableFrame clickedFocusableFrame = (FocusableFrame) clickedUIFrame;
				if (clickedFocusableFrame.isFocusable()) {
					setFocusFrame(clickedFocusableFrame);
				}
			}
		}
		return false;
	}

	private void setFocusFrame(final FocusableFrame clickedFocusableFrame) {
		if (focusUIFrame != null) {
			focusUIFrame.onFocusLost();
		}
		focusUIFrame = clickedFocusableFrame;
		if (focusUIFrame != null) {
			focusUIFrame.onFocusGained();
		}
	}

	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (mouseDownUIFrame != null) {
			if (clickedUIFrame.equals(mouseDownUIFrame)) {
				mouseDownUIFrame.onClick(button);
				uiSounds.getSound("GlueScreenClick").play(uiScene.audioContext, 0, 0, 0);
			}
			mouseDownUIFrame.mouseUp(rootFrame, uiViewport);
		}
		mouseDownUIFrame = null;
		return false;
	}

	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		if (mouseDownUIFrame != null) {
			mouseDownUIFrame.mouseDragged(rootFrame, uiViewport, screenCoordsVector.x,
					screenCoordsVector.y);
		}
		return false;
	}

	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		screenCoordsVector.set(screenX, screenY);
		uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (!mousedUIFrame.equals(mouseOverUIFrame)) {
			if (mouseOverUIFrame != null) {
				mouseOverUIFrame.mouseExit(rootFrame, uiViewport);
			}
			if (mousedUIFrame instanceof ClickableFrame) {
				mouseOverUIFrame = (ClickableFrame) mousedUIFrame;
				mouseOverUIFrame.mouseEnter(rootFrame, uiViewport);
			}
			else {
				mouseOverUIFrame = null;
			}
		}
		return false;
	}

	private void loadSounds() {
		worldEditStrings = new WorldEditStrings(dataSource);
		uiSoundsTable = new DataTable(worldEditStrings);
		try {
			try (InputStream miscDataTxtStream = dataSource.getResourceAsStream("UI\\SoundInfo\\UISounds.slk")) {
				uiSoundsTable.readSLK(miscDataTxtStream);
			}
			try (InputStream miscDataTxtStream = dataSource
					.getResourceAsStream("UI\\SoundInfo\\AmbienceSounds.slk")) {
				uiSoundsTable.readSLK(miscDataTxtStream);
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		uiSounds = new KeyedSounds(uiSoundsTable, dataSource);
	}

	public KeyedSounds getUiSounds() {
		return uiSounds;
	}

	private enum MenuState {
		GOING_TO_MAIN_MENU, MAIN_MENU, GOING_TO_BATTLE_NET_LOGIN, GOING_TO_BATTLE_NET_LOGIN_PART2, BATTLE_NET_LOGIN,
		LEAVING_BATTLE_NET, LEAVING_BATTLE_NET_FROM_LOGGED_IN, GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU,
		BATTLE_NET_CUSTOM_GAME_MENU, GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU, BATTLE_NET_CREATE_CUSTOM_GAME_MENU,
		GOING_TO_BATTLE_NET_CHANNEL_MENU, BATTLE_NET_CHANNEL_MENU, GOING_TO_BATTLE_NET_WELCOME, BATTLE_NET_WELCOME,
		GOING_TO_SINGLE_PLAYER, LEAVING_CAMPAIGN, SINGLE_PLAYER, GOING_TO_SINGLE_PLAYER_SKIRMISH,
		SINGLE_PLAYER_SKIRMISH, GOING_TO_MAP, GOING_TO_CAMPAIGN, GOING_TO_CAMPAIGN_PART2, GOING_TO_MISSION_SELECT,
		MISSION_SELECT, CAMPAIGN, GOING_TO_SINGLE_PLAYER_PROFILE, SINGLE_PLAYER_PROFILE, GOING_TO_LOADING_SCREEN,
		QUITTING, RESTARTING, GOING_TO_BATTLE_NET_CHAT_CHANNEL, GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE,
		BATTLE_NET_CHAT_CHANNEL
	}

	public void hide() {
		glueScreenLoop.stop();
		stopMusic();
	}

	public void dispose() {
		if (rootFrame != null) {
			rootFrame.dispose();
		}
	}

	public boolean keyDown(final int keycode) {
		if (focusUIFrame != null) {
			focusUIFrame.keyDown(keycode);
		}
		return false;
	}

	public boolean keyUp(final int keycode) {
		if (keycode == Input.Keys.TAB) {
			// accessibility tab focus ui
			final List<FocusableFrame> focusableFrames = rootFrame.getFocusableFrames();
			final int indexOf = focusableFrames.indexOf(focusUIFrame) + 1;
			IntStream.range(indexOf, focusableFrames.size())
					.mapToObj(focusableFrames::get)
					.filter(focusableFrame -> focusableFrame.isVisibleOnScreen() && focusableFrame.isFocusable())
					.findFirst()
					.ifPresent(this::setFocusFrame);
		}
		else {
			if (focusUIFrame != null) {
				focusUIFrame.keyUp(keycode);
			}
		}
		return false;
	}

	public boolean keyTyped(final char character) {
		if (focusUIFrame != null) {
			focusUIFrame.keyTyped(character);
		}
		return false;
	}

	public void onReturnFromGame() {
//		MenuUI.this.campaignMenu.setVisible(true);
//		MenuUI.this.campaignBackButton.setVisible(true);
//		MenuUI.this.missionSelectFrame.setVisible(true);
//		MenuUI.this.campaignSelectFrame.setVisible(false);
//		MenuUI.this.campaignWarcraftIIILogo.setVisible(true);
//		MenuUI.this.campaignRootMenuUI.setVisible(false);
//		MenuUI.this.currentMissionSelectMenuUI.setVisible(true);
		switch (menuState) {
		default:
		case GOING_TO_MAIN_MENU:
		case MAIN_MENU:
			glueScreenLoop.stop();
			glueScreenLoop = mainMenuGlueScreenLoop;
			glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
			menuScreen.setModel(rootFrame.getSkinField("GlueSpriteLayerBackground"));
			rootFrame.setSpriteFrameModel(cursorFrame, rootFrame.getSkinField("Cursor"));
			break;
		case CAMPAIGN:
		case MISSION_SELECT:
			final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
			final String currentCampaignAmbientSound = rootFrame
					.trySkinField(currentCampaign.getAmbientSound());
			menuScreen.setModel(currentCampaignBackgroundModel);
			glueScreenLoop.stop();
			glueScreenLoop = uiSounds.getSound(currentCampaignAmbientSound);
			glueScreenLoop.play(uiScene.audioContext, 0f, 0f, 0f);
			final DataTable skinData = rootFrame.getSkinData();
			final String cursorSkin = getRaceNameByCursorID(currentCampaign.getCursor());
			rootFrame.setSpriteFrameModel(cursorFrame, skinData.get(cursorSkin).getField("Cursor"));
			break;
		}
//		MenuUI.this.campaignFade.setSequence("Death");
//		this.campaignFade.setVisible(true);
//		this.menuState = MenuState.MISSION_SELECT;
	}

	private static String getRaceNameByCursorID(final int cursorId) {
		return getRaceByCursorID(cursorId).name();
	}

	private static CRace getRaceByCursorID(final int cursorId) {
		final CRace race;
		final int raceId = cursorId + 1;
		if ((raceId >= CRace.VALUES.length) || ((race = CRace.VALUES[raceId]) == null)) {
			return CRace.HUMAN; // when in doubt, default to human
		}
		return race;
	}

	private String getCurrentBackgroundModel() {
		final String background = currentCampaign.getBackground();
		final String versionedBackground = background;
		if (rootFrame.hasSkinField(versionedBackground)) {
			return rootFrame.getSkinField(versionedBackground);
		}
		return rootFrame.getSkinField(background);
	}

	private static final class LoadingMap {

		private final War3MapViewer viewer;
		private final War3Map map;
		private final War3MapW3i mapInfo;

		private LoadingMap(final War3MapViewer viewer, final War3Map map, final War3MapW3i mapInfo) {
			this.viewer = viewer;
			this.map = map;
			this.mapInfo = mapInfo;
		}

	}

	private void stopMusic() {
		if (currentMusics != null) {
			for (final Music music : currentMusics) {
				music.stop();
			}
			currentMusics = null;
		}
	}

	public Music playMusic(final String musicField, final boolean random, int index) {
		if (WarsmashConstants.ENABLE_MUSIC) {
			stopMusic();

			final String[] semicolonMusics = musicField.split(";");
			final List<String> musicPaths = new ArrayList<>();
			for (String musicPath : semicolonMusics) {
				// dumb support for comma as well as semicolon, I wonder if we can
				// clean this up, simplify?
				if (musicSLK.get(musicPath) != null) {
					musicPath = musicSLK.get(musicPath).getField("FileNames");
				}
				final String[] moreSplitMusics = musicPath.split(",");
				Collections.addAll(musicPaths, moreSplitMusics);
			}
			final String[] musics = musicPaths.toArray(new String[0]);

			if (random) {
				index = (int) (Math.random() * musics.length);
			}
			currentMusics = new Music[musics.length];
			for (int i = 0; i < musics.length; i++) {
				final Music newMusic = Gdx.audio.newMusic(new DataSourceFileHandle(viewer.dataSource, musics[i]));
				newMusic.setVolume(1.0f);
				currentMusics[i] = newMusic;
			}
			currentMusicIndex = index;
			currentMusicRandomizeIndex = random;
			currentMusics[index].play();
		}
		return null;
	}

	public void playCurrentBattleNetGlueSpriteDeath() {
		switch (menuState) {
		case BATTLE_NET_CHAT_CHANNEL:
		case GOING_TO_BATTLE_NET_CHAT_CHANNEL:
			glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Death");
			glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Death");
			break;
		case BATTLE_NET_CUSTOM_GAME_MENU:
		case GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU:
			glueSpriteLayerTopLeft.setSequence("BattleNetCustom Death");
			glueSpriteLayerTopRight.setSequence("BattleNetCustom Death");
			break;
		case BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
		case GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
			glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Death");
			glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Death");
			break;
		case BATTLE_NET_CHANNEL_MENU:
		case GOING_TO_BATTLE_NET_CHANNEL_MENU:
			glueSpriteLayerTopLeft.setSequence("BattleNetChannel Death");
			glueSpriteLayerTopRight.setSequence("BattleNetChannel Death");
			break;
		default:
		case BATTLE_NET_WELCOME:
		case GOING_TO_BATTLE_NET_WELCOME:
			glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Death");
			glueSpriteLayerTopRight.setSequence("BattleNetWelcome Death");
			break;
		}
	}

	private boolean isInsideTopBarMode() {
		boolean insideTopBarMode = false;
		switch (menuState) {
			case GOING_TO_MAIN_MENU:
			case MAIN_MENU:
			case GOING_TO_BATTLE_NET_LOGIN:
			case GOING_TO_BATTLE_NET_LOGIN_PART2:
			case BATTLE_NET_LOGIN:
			case GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU:
			case LEAVING_BATTLE_NET:
			case LEAVING_BATTLE_NET_FROM_LOGGED_IN:
			case BATTLE_NET_CUSTOM_GAME_MENU:
			case GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
			case BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
			case GOING_TO_SINGLE_PLAYER:
			case LEAVING_CAMPAIGN:
			case SINGLE_PLAYER:
			case GOING_TO_SINGLE_PLAYER_SKIRMISH:
			case SINGLE_PLAYER_SKIRMISH:
			case GOING_TO_MAP:
			case GOING_TO_CAMPAIGN:
			case GOING_TO_CAMPAIGN_PART2:
			case GOING_TO_MISSION_SELECT:
			case MISSION_SELECT:
			case CAMPAIGN:
			case GOING_TO_SINGLE_PLAYER_PROFILE:
			case SINGLE_PLAYER_PROFILE:
			case GOING_TO_LOADING_SCREEN:
			case QUITTING:
			case RESTARTING:
				break;
			case GOING_TO_BATTLE_NET_CHANNEL_MENU:
			case BATTLE_NET_CHANNEL_MENU:
			case GOING_TO_BATTLE_NET_CHAT_CHANNEL:
			case GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE:
			case BATTLE_NET_CHAT_CHANNEL:
			case GOING_TO_BATTLE_NET_WELCOME:
			case BATTLE_NET_WELCOME:
				insideTopBarMode = true;
				break;
			}
		return insideTopBarMode;
	}
}
