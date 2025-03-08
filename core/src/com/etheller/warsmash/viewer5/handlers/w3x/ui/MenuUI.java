package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32C;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.SingleModelScreen;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.networking.WarsmashClient;
import com.etheller.warsmash.networking.WarsmashClientSendingOrderListener;
import com.etheller.warsmash.networking.WarsmashClientWriter;
import com.etheller.warsmash.parsers.fdf.GameSkin;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.EditBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame.ListBoxSelelectionListener;
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
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.util.DataSourceFileHandle;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.util.WorldEditStrings;
import com.etheller.warsmash.viewer5.FogSettings;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer.MapLoader;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListenerDelaying;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRaceManagerEntry;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.FocusableFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.DialogWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookup;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookupFile;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookupPath;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapInfoPane;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapListContainer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.PlayerSlotPaneListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.TeamSetupPane;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.BattleNetUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.BattleNetUIActionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMenuData;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMenuUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.menu.CampaignMission;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.sound.KeyedSounds;

import net.warsmash.map.NetMapDownloader;
import net.warsmash.uberserver.AccountCreationFailureReason;
import net.warsmash.uberserver.ChannelServerMessageType;
import net.warsmash.uberserver.GameCreationFailureReason;
import net.warsmash.uberserver.GamingNetwork;
import net.warsmash.uberserver.GamingNetworkConnection;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;
import net.warsmash.uberserver.HandshakeDeniedReason;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.JoinGameFailureReason;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.LobbyPlayerType;
import net.warsmash.uberserver.LoginFailureReason;
import net.warsmash.uberserver.ServerErrorMessageType;

public class MenuUI {
	private static final Vector2 screenCoordsVector = new Vector2();
	private static boolean ENABLE_NOT_YET_IMPLEMENTED_BUTTONS = false;

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
	private BeginGameInformation beginGameInformation;
	private LoadingMap loadingMap;
	private SpriteFrame loadingBackground;
	private boolean unifiedCampaignInfo = false;
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
	private Task gameListQueryTask;
	private boolean hideUI;
	private final DataTable miscData;
	private FogSettings menuFogSettings;

	public MenuUI(final DataSource dataSource, final Viewport uiViewport, final Scene uiScene, final MdxViewer viewer,
			final WarsmashGdxMultiScreenGame screenManager, final SingleModelScreen menuScreen,
			final DataTable warsmashIni, final RootFrameListener rootFrameListener,
			final GamingNetworkConnection gamingNetworkConnection, final String mapDownloadDir) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.uiScene = uiScene;
		this.viewer = viewer;
		this.screenManager = screenManager;
		this.menuScreen = menuScreen;
		this.warsmashIni = warsmashIni;
		this.rootFrameListener = rootFrameListener;
		this.gamingNetworkConnection = gamingNetworkConnection;

		this.widthRatioCorrection = getMinWorldWidth() / 1600f;
		this.heightRatioCorrection = getMinWorldHeight() / 1200f;

		this.profileManager = PlayerProfileManager.loadFromGdx();

		this.musicSLK = new DataTable(StringBundle.EMPTY);
		final String musicSLKPath = "UI\\SoundInfo\\Music.SLK";
		if (viewer.dataSource.has(musicSLKPath)) {
			try (InputStream miscDataTxtStream = viewer.dataSource.getResourceAsStream(musicSLKPath)) {
				this.musicSLK.readSLK(miscDataTxtStream);
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
		this.miscData = new DataTable(this.worldEditStrings);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscData.txt")) {
			this.miscData.readTXT(miscDataTxtStream, true);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		final Element zFogElement = this.miscData.get("MenuZFog");
		if (zFogElement != null) {
			this.menuFogSettings = FogSettings.parse(zFogElement, WarsmashConstants.GAME_VERSION);
		}

		gamingNetworkConnection.addListener(new GamingNetworkServerToClientListener() {
			private boolean creatingGame = false;
			private CurrentNetGameMapLookup currentNetGameMapLookup;
			private long lastJoinGameChecksum;

			@Override
			public void disconnected() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.err.println("Disconnected from server...");
						MenuUI.this.battleNetConnectDialog.setVisible(false);
						setMainMenuButtonsEnabled(true);
						MenuUI.this.dialog.showError("ERROR_ID_DISCONNECT", null);

						MenuUI.this.battleNetUI.hide();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.glueSpriteLayerCenter.setSequence("Death");
						MenuUI.this.menuState = MenuState.LEAVING_BATTLE_NET_FROM_LOGGED_IN;
					}
				});
			}

			@Override
			public void loginOk(final long sessionToken, final String welcomeMessage) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.loginAccepted(sessionToken, welcomeMessage);
						MenuUI.this.battleNetUI.getDoors().setSequence(PrimaryTag.DEATH, SequenceUtils.ALTERNATE);
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_WELCOME;
					}
				});
			}

			@Override
			public void loginFailed(final LoginFailureReason loginFailureReason) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
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
						MenuUI.this.dialog.showError(msg, null);
					}
				});
			}

			@Override
			public void joinedChannel(final String channelName) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.joinedChannel(channelName);
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						final boolean insideTopBarMode = isInsideTopBarMode();
						if (!insideTopBarMode) {
							MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE;
						}
						else {
							MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL;
						}
					}
				});
			}

			@Override
			public void handshakeDenied(final HandshakeDeniedReason reason) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetConnectDialog.setVisible(true);
						String msg;
						switch (reason) {
						case BAD_GAME_VERSION: {
							msg = "ERROR_ID_VERSION_BAD";
							break;
						}
						default: {
							msg = "NETERROR_DEFAULTERROR";
							break;
						}
						}
						MenuUI.this.rootFrame.setDecoratedText(MenuUI.this.battleNetConnectInfoText, msg);
					}
				});
			}

			@Override
			public void handshakeAccepted() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetConnectDialog.setVisible(false);
						MenuUI.this.glueSpriteLayerTopLeft.setSequence("MainMenu Death");
						MenuUI.this.glueSpriteLayerTopRight.setSequence("MainMenu Death");
						setMainMenuButtonsEnabled(true);
						setMainMenuVisible(false);
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_LOGIN;
					}
				});
			}

			@Override
			public void channelMessage(final String userName, final String message) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.channelMessage(userName, message);
					}
				});
			}

			@Override
			public void channelEmote(final String userName, final String message) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.channelEmote(userName, message);
					}
				});
			}

			@Override
			public void channelServerMessage(final String userName, final ChannelServerMessageType messageType) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.channelServerMessage(userName, messageType);
					}
				});
			}

			@Override
			public void badSession() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.dialog.showError("ERROR_ID_NOTLOGGEDON", null);
					}
				});
			}

			@Override
			public void accountCreationOk() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.accountCreatedOk();
					}
				});
			}

			@Override
			public void accountCreationFailed(final AccountCreationFailureReason reason) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						switch (reason) {
						default:
						case USERNAME_ALREADY_EXISTS:
							MenuUI.this.dialog.showError("ERROR_ID_NAMEUSED", null);
							break;
						}
					}
				});
			}

			@Override
			public void joinedGame(final String gameName, final String mapName, final long mapChecksum) {
				this.lastJoinGameChecksum = mapChecksum;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						boolean host = false;
						if (creatingGame) {
							host = true;
							creatingGame = false;
							MenuUI.this.battleNetUI.setJoinGamePreviewMapToHostedMap();
							currentNetGameMapLookup = new CurrentNetGameMapLookupPath(
									MenuUI.this.battleNetUI.getLastHostedGamePath());
						}
						else {
							// joining game as non-host
							final String mapNameLower = mapName.toLowerCase(Locale.US);
							if (!(mapNameLower.endsWith(".w3m") || mapNameLower.endsWith(".w3x")
									|| mapNameLower.endsWith(".wsm"))) {
								// error, not a map file
								System.err.println("ERROR!!! NOT A MAP FILE: " + mapName);
								gamingNetworkConnection
										.leaveGame(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
								currentNetGameMapLookup = null;
							}
							else {
								final String mapNameOnly = mapName
										.substring(Math.max(mapName.lastIndexOf('/'), mapName.lastIndexOf('\\')) + 1);
								final String mapFileLookupName = mapDownloadDir + File.separator + mapNameOnly;
								File mapLookupFile = new File(mapFileLookupName);
								boolean hasMap = false;
								final CRC32C checksumCalculator = new CRC32C();
								War3Map map = null;
								int tildeIndex = 1;
								while (mapLookupFile.exists()) {
									try {
										map = War3MapViewer.beginLoadingMap(dataSource, mapLookupFile.getPath());
										final long localHDDMapChecksum = map.computeChecksum(checksumCalculator);
										if (localHDDMapChecksum == mapChecksum) {
											hasMap = true;
											break;
										}
									}
									catch (final Exception e) {
										e.printStackTrace();
									}
									final int dotIndex = mapFileLookupName.lastIndexOf('.');
									mapLookupFile = new File(mapFileLookupName.substring(0, dotIndex) + "~"
											+ tildeIndex++ + mapFileLookupName.substring(dotIndex));
								}
								currentNetGameMapLookup = new CurrentNetGameMapLookupFile(mapLookupFile);
								if (hasMap) {
									MenuUI.this.battleNetUI.setJoinGamePreviewMap(mapLookupFile);
								}
								else {
									MenuUI.this.battleNetUI.clearJoinGamePreviewMap(mapNameOnly);
									gamingNetworkConnection
											.requestMap(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
								}

							}
						}

						MenuUI.this.battleNetUI.joinedGame(gameName, host);
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CUSTOM_GAME_LOBBY;
					}
				});
			}

			@Override
			public void joinGameFailed(final JoinGameFailureReason reason) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						switch (reason) {
						case GAME_ALREADY_STARTED:
							MenuUI.this.dialog.showError("ERROR_ID_GAMECLOSED", null);
							break;
						case GAME_FULL:
							MenuUI.this.dialog.showError("ERROR_ID_GAMEFULL", null);
							break;
						case SESSION_ERROR:
							MenuUI.this.dialog.showError("NETERROR_JOINGAMEFAILED", null);
							break;
						default:
						case NO_SUCH_GAME:
							MenuUI.this.dialog.showError("NETERROR_JOINGAMENOTFOUND", null);
							break;
						}
					}
				});
			}

			@Override
			public void serverErrorMessage(final ServerErrorMessageType messageType) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						switch (messageType) {
						case UPLOAD_MAP_FAILED:
							MenuUI.this.dialog.showError("NETERROR_MAPFILEWRITE", null);
							break;
						default:
						case ERROR_HANDLING_REQUEST:
							MenuUI.this.dialog.showError("NETERROR_DEFAULTERROR", null);
							break;
						}
					}
				});
			}

			@Override
			public void gameCreationFailed(final GameCreationFailureReason reason) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						switch (reason) {
						default:
						case GAME_NAME_ALREADY_USED:
							MenuUI.this.dialog.showError("NETERROR_CREATEGAMEFAILED", null);
							break;
						}
					}
				});
			}

			@Override
			public void gameCreationOk() {
				System.out.println("NOTE: Server says we created a game OK");
				// NOTE: immediately following this the server should tell us we joined a game
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						creatingGame = true;
					}
				});
			}

			@Override
			public void beginGamesList() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.beginGamesList();
					}
				});
			}

			@Override
			public void gamesListItem(final String gameName, final int openSlots, final int totalSlots) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.gamesListItem(gameName, openSlots, totalSlots);
					}
				});
			}

			@Override
			public void endGamesList() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.battleNetUI.endGamesList();
					}
				});
			}

			private NetMapDownloader netMapDownloader;

			@Override
			public void beginSendMap() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						if (currentNetGameMapLookup instanceof CurrentNetGameMapLookupFile) {
							final File mapFile = ((CurrentNetGameMapLookupFile) currentNetGameMapLookup).getFile();
							netMapDownloader = new NetMapDownloader(mapFile);
						}
						else {
							throw new IllegalStateException();
						}
					}
				});
			}

			@Override
			public void sendMapData(final int sequenceNumber, final ByteBuffer data) {
				final ByteBuffer gdxThreadBuffer = ByteBuffer.allocate(data.remaining());
				gdxThreadBuffer.clear();
				gdxThreadBuffer.put(data);
				gdxThreadBuffer.flip();
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.out.println("receiving map: " + sequenceNumber + ", " + gdxThreadBuffer.remaining());
						netMapDownloader.receive(sequenceNumber, gdxThreadBuffer);
					}
				});
			}

			@Override
			public void endSendMap(final int sequenceNumber) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.out.println("receiving map finishing: " + sequenceNumber);
						final long checksum = netMapDownloader.finish(sequenceNumber);
						if (currentNetGameMapLookup instanceof CurrentNetGameMapLookupFile) {
							if ((checksum == lastJoinGameChecksum) && netMapDownloader.isSequenceNumberingOK()) {
								final File mapFile = ((CurrentNetGameMapLookupFile) currentNetGameMapLookup).getFile();
								MenuUI.this.battleNetUI.setJoinGamePreviewMap(mapFile);
							}
							else {
								MenuUI.this.dialog.showError("NETERROR_MAPFILEINCOMPLETE", null);
								gamingNetworkConnection
										.leaveGame(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
							}
						}
						else {
							throw new IllegalStateException();
						}
					}
				});
			}

			@Override
			public void gameLobbySlotSetPlayerType(final int slot, final LobbyPlayerType playerType) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.err.println("gameLobbySlotSetPlayerType " + slot + " " + playerType);
						MenuUI.this.battleNetUI.gameLobbySlotSetPlayerType(slot, playerType);
					}
				});
			}

			@Override
			public void gameLobbySlotSetPlayer(final int slot, final String userName) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.err.println("gameLobbySlotSetPlayer " + slot + " " + userName);
						MenuUI.this.battleNetUI.gameLobbySlotSetPlayer(slot, userName);
					}
				});
			}

			@Override
			public void gameLobbySlotSetPlayerRace(final int slot, final int raceItemIndex) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						System.err.println("gameLobbySlotSetPlayerRace " + slot + " " + raceItemIndex);
						MenuUI.this.battleNetUI.gameLobbySlotSetPlayerRace(slot, raceItemIndex);
					}
				});
			}

			@Override
			public void gameLobbyStartGame(final byte[] hostIpAddressBytes, final short hostUdpPort,
					final int yourServerPlayerSlot) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						MenuUI.this.campaignMenu.setVisible(false);
						MenuUI.this.campaignBackButton.setVisible(false);
						MenuUI.this.missionSelectFrame.setVisible(false);
						MenuUI.this.campaignSelectFrame.setVisible(false);
						MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
						MenuUI.this.campaignRootMenuUI.setVisible(false);
						MenuUI.this.currentMissionSelectMenuUI.setVisible(false);
						MenuUI.this.skirmish.setVisible(false);
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.beginGameInformation = new BeginGameInformation();
						MenuUI.this.beginGameInformation.gameMapLookup = currentNetGameMapLookup;
						MenuUI.this.beginGameInformation.sessionToken = MenuUI.this.battleNetUI
								.getGamingNetworkSessionToken();
						MenuUI.this.beginGameInformation.hostInetAddress = hostIpAddressBytes;
						MenuUI.this.beginGameInformation.hostUdpPort = hostUdpPort & 0xFFFF;
						final IntIntMap gameChatroomServerSlotToMapSlot = MenuUI.this.battleNetUI
								.getGameChatroomServerSlotToMapSlot();
						MenuUI.this.beginGameInformation.serverSlotToMapSlot = gameChatroomServerSlotToMapSlot;
						MenuUI.this.beginGameInformation.mapSlotToServerSlot = MenuUI.this.battleNetUI
								.getGameChatroomMapSlotToServerSlot();
						MenuUI.this.beginGameInformation.localPlayerIndex = gameChatroomServerSlotToMapSlot
								.get(yourServerPlayerSlot, -1);
						MenuUI.this.currentMapConfig = MenuUI.this.battleNetUI.getGameChatroomMapConfig();
						MenuUI.this.menuState = MenuState.GOING_TO_MAP;
					}
				});
			}
		});
	}

	public float getHeightRatioCorrection() {
		return this.heightRatioCorrection;
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, WarsmashConstants.GAME_VERSION),
				this.uiViewport, this.uiScene, this.viewer, 0, WTS.DO_NOTHING);

		this.menuScreen.setModel(this.rootFrame.getSkinField("GlueSpriteLayerBackground"), this.menuFogSettings);
		this.rootFrameListener.onCreate(this.rootFrame);
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}

		this.campaignStrings = new DataTable(StringBundle.EMPTY);
		final String campaignStringPath = this.rootFrame.trySkinField("CampaignFile");
		if (this.dataSource.has(campaignStringPath)) {
			try (InputStream campaignStringStream = this.dataSource.getResourceAsStream(campaignStringPath)) {
				this.campaignStrings.readTXT(campaignStringStream, true);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			try (InputStream campaignStringStream = this.dataSource
					.getResourceAsStream("UI\\CampaignInfoClassic.txt")) {
				this.campaignStrings.readTXT(campaignStringStream, true);
				this.unifiedCampaignInfo = true;
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Create main menu
		this.mainMenuFrame = this.rootFrame.createFrame("MainMenuFrame", this.rootFrame, 0, 0);

		this.warcraftIIILogo = (SpriteFrame) this.rootFrame.getFrameByName("WarCraftIIILogo", 0);
		this.rootFrame.setSpriteFrameModel(this.warcraftIIILogo, this.rootFrame.getSkinField("MainMenuLogo"));
		this.warcraftIIILogo.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.mainMenuFrame, FramePoint.TOPLEFT,
				GameUI.convertX(this.uiViewport, 0.13f), GameUI.convertY(this.uiViewport, -0.08f)));
		setMainMenuVisible(false);
		this.rootFrame.getFrameByName("RealmSelect", 0).setVisible(false);

		this.glueSpriteLayerTopRight = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopRight", this.rootFrame, "", 0);
		this.glueSpriteLayerTopRight.setSetAllPoints(true);
		final String topRightModel = this.rootFrame.getSkinField("GlueSpriteLayerTopRight");
		this.rootFrame.setSpriteFrameModel(this.glueSpriteLayerTopRight, topRightModel);
		this.glueSpriteLayerTopRight.setSequence("MainMenu Birth");

		this.glueSpriteLayerTopLeft = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerTopLeft", this.rootFrame, "", 0);
		this.glueSpriteLayerTopLeft.setSetAllPoints(true);
		final String topLeftModel = this.rootFrame.getSkinField("GlueSpriteLayerTopLeft");
		this.rootFrame.setSpriteFrameModel(this.glueSpriteLayerTopLeft, topLeftModel);
		this.glueSpriteLayerTopLeft.setSequence("MainMenu Birth");

		this.glueSpriteLayerCenter = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
				"SmashGlueSpriteLayerCenter", this.rootFrame, "", 0);
		this.glueSpriteLayerCenter.setSetAllPoints(true);
		final String centerModel = this.rootFrame.getSkinField("GlueSpriteLayerCenter");
		this.rootFrame.setSpriteFrameModel(this.glueSpriteLayerCenter, centerModel);
		this.glueSpriteLayerCenter.setVisible(false);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(1024.0f);
		if (WarsmashConstants.CATCH_CURSOR) {
			Gdx.input.setCursorCatched(true);
		}

		this.battleNetConnectDialog = this.rootFrame.createFrame("BattleNetConnectDialog", this.rootFrame, 0, 0);
		this.battleNetConnectDialog.setVisible(false);
		this.battleNetConnectDialog.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
		this.battleNetConnectInfoText = (StringFrame) this.rootFrame.getFrameByName("ConnectInfoText", 0);
		this.battleNetConnectCancelButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("ConnectButton", 0);
		this.battleNetConnectCancelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.gamingNetworkConnection.userRequestDisconnect();
				MenuUI.this.battleNetConnectDialog.setVisible(false);
				setMainMenuButtonsEnabled(true);
			}
		});

		// Main Menu interactivity
		this.singlePlayerButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("SinglePlayerButton", 0);
		this.battleNetButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("BattleNetButton", 0);
		this.realmButton = (GlueButtonFrame) this.rootFrame.getFrameByName("RealmButton", 0);
		this.localAreaNetworkButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("LocalAreaNetworkButton", 0);
		this.optionsButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("OptionsButton", 0);
		this.creditsButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("CreditsButton", 0);
		this.exitButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("ExitButton", 0);
		this.editionButton = (GlueButtonFrame) this.rootFrame.getFrameByName("EditionButton", 0);

		if (this.editionButton != null) {
			this.editionButton.setOnClick(new Runnable() {
				@Override
				public void run() {
					WarsmashConstants.GAME_VERSION = WarsmashConstants.GAME_VERSION == 1 ? 0 : 1;
					MenuUI.this.glueSpriteLayerTopLeft.setSequence("MainMenu Death");
					MenuUI.this.glueSpriteLayerTopRight.setSequence("MainMenu Death");
					setMainMenuVisible(false);
					MenuUI.this.menuState = MenuState.RESTARTING;
				}
			});
		}

		this.localAreaNetworkButton.setEnabled(true);
		this.optionsButton.setEnabled(false);
		this.creditsButton.setEnabled(false);

		this.exitButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("MainMenu Death");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("MainMenu Death");
				setMainMenuVisible(false);
				MenuUI.this.menuState = MenuState.QUITTING;
			}
		});

		this.singlePlayerButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("MainMenu Death");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("MainMenu Death");
				setMainMenuVisible(false);
				MenuUI.this.menuState = MenuState.GOING_TO_SINGLE_PLAYER;
			}
		});

		this.battleNetButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				setMainMenuButtonsEnabled(false);
				MenuUI.this.rootFrame.setDecoratedText(MenuUI.this.battleNetConnectInfoText, "BNET_CONNECT_INIT");
				MenuUI.this.battleNetConnectDialog.positionBounds(MenuUI.this.rootFrame, MenuUI.this.uiViewport);
				MenuUI.this.battleNetConnectDialog.setVisible(true);
				if (MenuUI.this.gamingNetworkConnection.userRequestConnect()) {
					MenuUI.this.gamingNetworkConnection.handshake(WarsmashConstants.getGameId(),
							GamingNetwork.GAME_VERSION_DATA);
				}
				else {
					MenuUI.this.battleNetConnectDialog.setVisible(false);
					setMainMenuButtonsEnabled(true);
					MenuUI.this.dialog.showError("ERROR_ID_CANTCONNECT", new Runnable() {
						@Override
						public void run() {

						}
					});
				}
			}
		});
		this.realmButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.battleNetConnectDialog.setVisible(true);
			}
		});

		// Create single player
		this.singlePlayerMenu = this.rootFrame.createFrame("SinglePlayerMenu", this.rootFrame, 0, 0);
		this.singlePlayerMenu.setVisible(false);

		this.profilePanel = this.rootFrame.getFrameByName("ProfilePanel", 0);
		this.profilePanel.setVisible(false);

		this.newProfileEditBox = (EditBoxFrame) this.rootFrame.getFrameByName("NewProfileEditBox", 0);
		this.newProfileEditBox.setOnChange(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.addProfileButton
						.setEnabled(!MenuUI.this.profileManager.hasProfile(MenuUI.this.newProfileEditBox.getText()));
			}
		});
		final StringFrame profileListText = (StringFrame) this.rootFrame.getFrameByName("ProfileListText", 0);
		final SimpleFrame profileListContainer = (SimpleFrame) this.rootFrame.getFrameByName("ProfileListContainer", 0);
		final ListBoxFrame profileListBox = (ListBoxFrame) this.rootFrame.createFrameByType("LISTBOX", "ListBoxWar3",
				profileListContainer, "WITHCHILDREN", 0);
		profileListBox.setSetAllPoints(true);
		profileListBox.setFrameFont(profileListText.getFrameFont());
		for (final PlayerProfile profile : this.profileManager.getProfiles()) {
			profileListBox.addItem(profile.getName(), this.rootFrame, this.uiViewport);
		}
		profileListContainer.add(profileListBox);

		this.addProfileButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("AddProfileButton", 0);
		this.deleteProfileButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("DeleteProfileButton", 0);
		this.selectProfileButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("SelectProfileButton", 0);
		this.selectProfileButton.setEnabled(false);
		this.deleteProfileButton.setEnabled(false);
		this.addProfileButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				final String newProfileName = MenuUI.this.newProfileEditBox.getText();
				if (!newProfileName.isEmpty() && !MenuUI.this.profileManager.hasProfile(newProfileName)) {
					MenuUI.this.profileManager.addProfile(newProfileName);
					profileListBox.addItem(newProfileName, MenuUI.this.rootFrame, MenuUI.this.uiViewport);
					MenuUI.this.addProfileButton.setEnabled(false);
				}
			}
		});
		this.deleteProfileButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				final int selectedIndex = profileListBox.getSelectedIndex();
				final boolean validSelect = (selectedIndex >= 0)
						&& (selectedIndex < MenuUI.this.profileManager.getProfiles().size());
				if (validSelect) {
					if (MenuUI.this.profileManager.getProfiles().size() > 1) {
						final PlayerProfile profileToRemove = MenuUI.this.profileManager.getProfiles()
								.get(selectedIndex);
						final String removeProfileName = profileToRemove.getName();
						final boolean deletingCurrentProfile = removeProfileName
								.equals(MenuUI.this.profileManager.getCurrentProfile());
						MenuUI.this.profileManager.removeProfile(profileToRemove);
						profileListBox.removeItem(selectedIndex, MenuUI.this.rootFrame, MenuUI.this.uiViewport);
						if (deletingCurrentProfile) {
							setCurrentProfile(MenuUI.this.profileManager.getProfiles().get(0).getName());
						}
					}
				}
			}
		});
		this.selectProfileButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				final int selectedIndex = profileListBox.getSelectedIndex();
				final boolean validSelect = (selectedIndex >= 0)
						&& (selectedIndex < MenuUI.this.profileManager.getProfiles().size());
				if (validSelect) {
					final PlayerProfile profileToSelect = MenuUI.this.profileManager.getProfiles().get(selectedIndex);
					final String selectedProfileName = profileToSelect.getName();
					setCurrentProfile(selectedProfileName);

					MenuUI.this.glueSpriteLayerTopLeft.setSequence("RealmSelection Death");
					MenuUI.this.profilePanel.setVisible(false);
					MenuUI.this.menuState = MenuState.SINGLE_PLAYER;
					setSinglePlayerButtonsEnabled(false);
				}

			}

		});
		profileListBox.setSelectionListener(new ListBoxSelelectionListener() {
			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				final boolean validSelect = newSelectedItem != null;
				MenuUI.this.selectProfileButton.setEnabled(validSelect);
				MenuUI.this.deleteProfileButton.setEnabled(validSelect);
			}
		});

		this.singlePlayerMainPanel = this.rootFrame.getFrameByName("MainPanel", 0);

		// Single Player Interactivity
		this.profileButton = (GlueButtonFrame) this.rootFrame.getFrameByName("ProfileButton", 0);
		this.campaignButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("CampaignButton", 0);
		this.loadSavedButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("LoadSavedButton", 0);
		this.viewReplayButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("ViewReplayButton", 0);
		this.customCampaignButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("CustomCampaignButton", 0);
		this.skirmishButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("SkirmishButton", 0);

		this.singlePlayerCancelButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("CancelButton", 0);

		this.profileNameText = (StringFrame) this.rootFrame.getFrameByName("ProfileNameText", 0);
		this.rootFrame.setText(this.profileNameText, this.profileManager.getCurrentProfile());

		setSinglePlayerButtonsEnabled(true);

		this.profileButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("RealmSelection Birth");
				setSinglePlayerButtonsEnabled(false);
				MenuUI.this.menuState = MenuState.SINGLE_PLAYER_PROFILE;
			}
		});

		this.campaignButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
				MenuUI.this.singlePlayerMenu.setVisible(false);
				MenuUI.this.profilePanel.setVisible(false);
				MenuUI.this.menuState = MenuState.GOING_TO_CAMPAIGN;
			}
		});

		this.skirmishButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
				MenuUI.this.singlePlayerMenu.setVisible(false);
				MenuUI.this.profilePanel.setVisible(false);
				MenuUI.this.menuState = MenuState.GOING_TO_SINGLE_PLAYER_SKIRMISH;
			}
		});

		this.singlePlayerCancelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				if (MenuUI.this.menuState == MenuState.SINGLE_PLAYER_PROFILE) {
					MenuUI.this.glueSpriteLayerTopLeft.setSequence("RealmSelection Death");
					MenuUI.this.profilePanel.setVisible(false);
				}
				else {
					MenuUI.this.glueSpriteLayerTopLeft.setSequence("SinglePlayer Death");
				}
				MenuUI.this.glueSpriteLayerTopRight.setSequence("SinglePlayer Death");
				MenuUI.this.singlePlayerMenu.setVisible(false);
				MenuUI.this.menuState = MenuState.GOING_TO_MAIN_MENU;
			}
		});

		// Create skirmish UI
		this.skirmish = this.rootFrame.createFrame("Skirmish", this.rootFrame, 0, 0);
		this.skirmish.setVisible(false);

		this.mapInfoButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("MapInfoButton", 0);
		this.advancedOptionsButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("AdvancedOptionsButton", 0);
		this.mapInfoPanel = this.rootFrame.getFrameByName("MapInfoPanel", 0);
		this.advancedOptionsPanel = this.rootFrame.getFrameByName("AdvancedOptionsPanel", 0);
		final SimpleFrame mapInfoPaneContainer = (SimpleFrame) this.rootFrame.getFrameByName("MapInfoPaneContainer", 0);
		final SimpleFrame advancedOptionsPaneContainer = (SimpleFrame) this.rootFrame
				.getFrameByName("AdvancedOptionsPaneContainer", 0);
		this.skirmishAdvancedOptionsPane = this.rootFrame.createFrame("AdvancedOptionsPane",
				advancedOptionsPaneContainer, 0, 0);
		this.skirmishAdvancedOptionsPane.setSetAllPoints(true);
		advancedOptionsPaneContainer.add(this.skirmishAdvancedOptionsPane);
		this.skirmishMapInfoPane = new MapInfoPane(this.rootFrame, this.uiViewport, mapInfoPaneContainer);

		final SimpleFrame teamSetupContainer = (SimpleFrame) this.rootFrame.getFrameByName("TeamSetupContainer", 0);
		final TeamSetupPane teamSetupPane = new TeamSetupPane(this.rootFrame, this.uiViewport, teamSetupContainer);

		final GlueTextButtonFrame playGameButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("PlayGameButton",
				0);
		final MapListContainer mapListContainer = new MapListContainer(this.rootFrame, this.uiViewport,
				"MapListContainer", this.dataSource, profileListText.getFrameFont());
		mapListContainer.addSelectionListener(new ListBoxSelelectionListener() {
			War3Map lastMapListMap;
			String prevSelectedItem = "";

			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				if (newSelectedItem != null) {
					if (newSelectedItem.compareTo(this.prevSelectedItem) == 0) {
						return;
					}
					this.prevSelectedItem = newSelectedItem;

					try {
						final War3Map map = War3MapViewer.beginLoadingMap(MenuUI.this.dataSource, newSelectedItem);
						if (this.lastMapListMap != null) {
							try {
								this.lastMapListMap.close();
							}
							catch (final IOException e) {
								e.printStackTrace();
							}
							this.lastMapListMap = map;
						}
						final War3MapW3i mapInfo = map.readMapInformation();
						final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
						MenuUI.this.rootFrame.setMapStrings(wtsFile);
						final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
						for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
							final CBasePlayer player = war3MapConfig.getPlayer(i);
							player.setName(MenuUI.this.rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
						}
						war3MapConfig.setMapName("NOTEXTERN: default name string");
						war3MapConfig.setMapDescription("NOTEXTERN: default description string");
						Jass2.loadConfig(map, MenuUI.this.uiViewport, MenuUI.this.uiScene, MenuUI.this.rootFrame,
								war3MapConfig, WarsmashConstants.JASS_FILE_LIST).config();
						boolean foundFirstHuman = false;
						boolean foundFirstComp = false;
						for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
							final CBasePlayer player = war3MapConfig.getPlayer(i);
							if (player.getController() == CMapControl.USER) {
								if (!foundFirstHuman) {
									player.setSlotState(CPlayerSlotState.PLAYING);
									player.setName(MenuUI.this.profileManager.getCurrentProfile());
									foundFirstHuman = true;
								}
							}
							else if (player.getController() == CMapControl.COMPUTER) {
								if (!foundFirstComp
										|| mapInfo.hasFlag(War3MapW3iFlags.FIXED_PLAYER_SETTINGS_FOR_CUSTOM_FORCES)) {
									player.setSlotState(CPlayerSlotState.PLAYING);
									foundFirstComp = true;
								}
							}
						}
						MenuUI.this.skirmishMapInfoPane.setMap(MenuUI.this.rootFrame, MenuUI.this.uiViewport, map,
								mapInfo, war3MapConfig);
						teamSetupPane.setMap(MenuUI.this.rootFrame, MenuUI.this.uiViewport, war3MapConfig,
								mapInfo.getPlayers().size(), mapInfo,
								new PlayerSlotPaneListenerImplementation(teamSetupPane, war3MapConfig, mapInfo,
										MenuUI.this.rootFrame, MenuUI.this.uiViewport));
						MenuUI.this.currentMapConfig = war3MapConfig;
					}
					catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		playGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				final String selectedItem = mapListContainer.getSelectedItem();
				if (selectedItem != null) {
					MenuUI.this.campaignMenu.setVisible(false);
					MenuUI.this.campaignBackButton.setVisible(false);
					MenuUI.this.missionSelectFrame.setVisible(false);
					MenuUI.this.campaignSelectFrame.setVisible(false);
					MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
					MenuUI.this.campaignRootMenuUI.setVisible(false);
					MenuUI.this.currentMissionSelectMenuUI.setVisible(false);
					MenuUI.this.skirmish.setVisible(false);
					MenuUI.this.glueSpriteLayerTopLeft.setSequence("Death");
					MenuUI.this.glueSpriteLayerTopRight.setSequence("Death");
					MenuUI.this.beginGameInformation = new BeginGameInformation();
					MenuUI.this.beginGameInformation.gameMapLookup = new CurrentNetGameMapLookupPath(selectedItem);
					int localPlayerIndex = -1;
					for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
						final CBasePlayer configPlayer = MenuUI.this.currentMapConfig.getPlayer(i);
						if ((configPlayer.getSlotState() == CPlayerSlotState.PLAYING)
								&& (configPlayer.getController() == CMapControl.USER)) {
							localPlayerIndex = i;
							break;
						}
					}
					MenuUI.this.beginGameInformation.localPlayerIndex = localPlayerIndex;
					MenuUI.this.menuState = MenuState.GOING_TO_MAP;
				}

			}
		});

		this.skirmishCancelButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("CancelButton", 0);
		this.skirmishCancelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Death");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Death");
				MenuUI.this.skirmish.setVisible(false);
				MenuUI.this.menuState = MenuState.GOING_TO_SINGLE_PLAYER;

			}
		});

		// Create Campaign UI

		this.campaignMenu = this.rootFrame.createFrame("CampaignMenu", this.rootFrame, 0, 0);
		this.campaignMenu.setVisible(false);
		this.campaignFade = (SpriteFrame) this.rootFrame.getFrameByName("SlidingDoors", 0);
		this.campaignFade.setVisible(false);
		this.campaignBackButton = (GlueTextButtonFrame) this.rootFrame.getFrameByName("BackButton", 0);
		this.campaignBackButton.setVisible(false);
		this.missionSelectFrame = this.rootFrame.getFrameByName("MissionSelectFrame", 0);
		this.missionSelectFrame.setVisible(false);
		final StringFrame missionName = (StringFrame) this.rootFrame.getFrameByName("MissionName", 0);
		final StringFrame missionNameHeader = (StringFrame) this.rootFrame.getFrameByName("MissionNameHeader", 0);

		this.campaignSelectFrame = this.rootFrame.getFrameByName("CampaignSelectFrame", 0);
		this.campaignSelectFrame.setVisible(false);

		this.campaignWarcraftIIILogo = (SpriteFrame) this.rootFrame.getFrameByName("WarCraftIIILogo", 0);
		this.rootFrame.setSpriteFrameModel(this.campaignWarcraftIIILogo, this.rootFrame.getSkinField("MainMenuLogo"));
		this.campaignWarcraftIIILogo.setVisible(false);
		this.campaignWarcraftIIILogo
				.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this.campaignMenu, FramePoint.TOPRIGHT,
						GameUI.convertX(this.uiViewport, -0.13f), GameUI.convertY(this.uiViewport, -0.08f)));
		this.campaignRootMenuUI = new CampaignMenuUI(null, this.campaignMenu, this.rootFrame, this.uiViewport);
		this.campaignRootMenuUI.setVisible(false);
		this.campaignRootMenuUI.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this.campaignMenu, FramePoint.TOPRIGHT,
				GameUI.convertX(this.uiViewport, -0.0f), GameUI.convertY(this.uiViewport, -0.12f)));
		this.campaignRootMenuUI.setWidth(GameUI.convertX(this.uiViewport, 0.30f));
		this.campaignRootMenuUI.setHeight(GameUI.convertY(this.uiViewport, 0.42f));
		this.rootFrame.add(this.campaignRootMenuUI);

		this.campaignBackButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				if (MenuUI.this.menuState == MenuState.MISSION_SELECT) {
					MenuUI.this.currentMissionSelectMenuUI.setVisible(false);
					MenuUI.this.missionSelectFrame.setVisible(false);
					MenuUI.this.menuState = MenuState.CAMPAIGN;
				}
				else {
					MenuUI.this.campaignMenu.setVisible(false);
					MenuUI.this.campaignBackButton.setVisible(false);
					MenuUI.this.missionSelectFrame.setVisible(false);
					MenuUI.this.campaignSelectFrame.setVisible(false);
					MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
					MenuUI.this.campaignRootMenuUI.setVisible(false);
					MenuUI.this.campaignFade.setSequence("Birth");
					MenuUI.this.menuState = MenuState.LEAVING_CAMPAIGN;
				}
			}
		});
		final Element campaignIndex = this.campaignStrings.get("Index");
		if (campaignIndex != null) {
			this.campaignList = campaignIndex.getField("CampaignList").split(",");
			this.campaignDatas = new CampaignMenuData[this.campaignList.length];
			for (int i = 0; i < this.campaignList.length; i++) {
				final String campaign = this.campaignList[i];
				final Element campaignElement = this.campaignStrings.get(campaign);
				if (campaignElement != null) {
					final CampaignMenuData newCampaign = new CampaignMenuData(campaignElement);
					this.campaignDatas[i] = newCampaign;
					if (this.currentCampaign == null) {
						this.currentCampaign = newCampaign;
					}

				}
			}
			for (final CampaignMenuData campaign : this.campaignDatas) {
				if (campaign != null) {
					final CampaignMenuUI missionSelectMenuUI = new CampaignMenuUI(null, this.campaignMenu,
							this.rootFrame, this.uiViewport);
					missionSelectMenuUI.setVisible(false);
					missionSelectMenuUI
							.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this.campaignMenu, FramePoint.TOPRIGHT,
									GameUI.convertX(this.uiViewport, -0.0f), GameUI.convertY(this.uiViewport, -0.12f)));
					missionSelectMenuUI.setWidth(GameUI.convertX(this.uiViewport, 0.30f));
					missionSelectMenuUI.setHeight(GameUI.convertY(this.uiViewport, 0.42f));
					this.rootFrame.add(missionSelectMenuUI);

					for (final CampaignMission mission : campaign.getMissions()) {
						missionSelectMenuUI.addButton(mission.getHeader(), mission.getMissionName(), new Runnable() {
							@Override
							public void run() {
								MenuUI.this.campaignMenu.setVisible(false);
								MenuUI.this.campaignBackButton.setVisible(false);
								MenuUI.this.missionSelectFrame.setVisible(false);
								MenuUI.this.campaignSelectFrame.setVisible(false);
								MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
								MenuUI.this.campaignRootMenuUI.setVisible(false);
								MenuUI.this.currentMissionSelectMenuUI.setVisible(false);
								MenuUI.this.campaignFade.setSequence("Birth");
								try {
									loadAndCacheMapConfigs(mission.getMapFilename());
								}
								catch (final IOException e) {
									e.printStackTrace();
								}
								int localPlayerIndex = -1;
								for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
									final CBasePlayer player = MenuUI.this.currentMapConfig.getPlayer(i);
									if (player.getController() == CMapControl.USER) {
										player.setSlotState(CPlayerSlotState.PLAYING);
										player.setName(MenuUI.this.profileManager.getCurrentProfile());
										if (localPlayerIndex == -1) {
											localPlayerIndex = i;
										}
									}
								}
								MenuUI.this.beginGameInformation = new BeginGameInformation();
								MenuUI.this.beginGameInformation.gameMapLookup = new CurrentNetGameMapLookupPath(
										mission.getMapFilename());
								MenuUI.this.beginGameInformation.localPlayerIndex = localPlayerIndex;
							}
						});
					}

					this.campaignRootMenuUI.addButton(campaign.getHeader(), campaign.getName(), new Runnable() {
						@Override
						public void run() {
							if (campaign != MenuUI.this.currentCampaign) {
								MenuUI.this.campaignMenu.setVisible(false);
								MenuUI.this.campaignBackButton.setVisible(false);
								MenuUI.this.missionSelectFrame.setVisible(false);
								MenuUI.this.campaignSelectFrame.setVisible(false);
								MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
								MenuUI.this.campaignRootMenuUI.setVisible(false);
								MenuUI.this.campaignFade.setSequence("Birth");
								MenuUI.this.currentCampaign = campaign;
								MenuUI.this.currentMissionSelectMenuUI = missionSelectMenuUI;
								MenuUI.this.menuState = MenuState.GOING_TO_MISSION_SELECT;
							}
							else {
								MenuUI.this.campaignSelectFrame.setVisible(false);
								MenuUI.this.campaignRootMenuUI.setVisible(false);
								MenuUI.this.currentMissionSelectMenuUI.setVisible(true);
								MenuUI.this.missionSelectFrame.setVisible(true);
								MenuUI.this.menuState = MenuState.MISSION_SELECT;
							}
							MenuUI.this.rootFrame.setDecoratedText(missionName, campaign.getName());
							MenuUI.this.rootFrame.setDecoratedText(missionNameHeader, campaign.getHeader());
						}
					});
					if (campaign == MenuUI.this.currentCampaign) {
						MenuUI.this.currentMissionSelectMenuUI = missionSelectMenuUI;
					}
				}
			}
		}
		else {
			this.campaignButton.setEnabled(false);
		}

		this.confirmDialog = this.rootFrame.createFrame("DialogWar3", this.rootFrame, 0, 0);
		this.confirmDialog.setVisible(false);

		this.loadingFrame = this.rootFrame.createFrame("Loading", this.rootFrame, 0, 0);
		this.loadingFrame.setVisible(false);
		this.loadingCustomPanel = this.rootFrame.getFrameByName("LoadingCustomPanel", 0);
		this.loadingCustomPanel.setVisible(false);
		this.loadingTitleText = (StringFrame) this.rootFrame.getFrameByName("LoadingTitleText", 0);
		this.loadingSubtitleText = (StringFrame) this.rootFrame.getFrameByName("LoadingSubtitleText", 0);
		this.loadingText = (StringFrame) this.rootFrame.getFrameByName("LoadingText", 0);
		this.loadingBar = (SpriteFrame) this.rootFrame.getFrameByName("LoadingBar", 0);
		this.loadingBackground = (SpriteFrame) this.rootFrame.getFrameByName("LoadingBackground", 0);

		this.loadingMeleePanel = this.rootFrame.getFrameByName("LoadingMeleePanel", 0);
		this.loadingMeleePanel.setVisible(false);

		this.battleNetUI = new BattleNetUI(this.rootFrame, this.uiViewport, this.uiScene, this.dataSource,
				new BattleNetUIActionListener() {

					@Override
					public void cancelLoginPrompt() {
						MenuUI.this.battleNetUI.hide();
						MenuUI.this.battleNetUI.getDoors().setSequence(PrimaryTag.DEATH);
						MenuUI.this.menuScreen.unAlternateModelBackToNormal();
						MenuUI.this.menuState = MenuState.LEAVING_BATTLE_NET;
						MenuUI.this.gamingNetworkConnection.userRequestDisconnect();
					}

					@Override
					public void recoverPassword(final String text) {

					}

					@Override
					public void logon(final String accountName, final String password) {
						if (accountName.isEmpty()) {
							MenuUI.this.dialog.showError("ERROR_ID_NAMEBLANK", null);
						}
						else if (password.isEmpty()) {
							MenuUI.this.dialog.showError("NETERROR_NOPASSWORD", null);
						}
						else {
							final char[] passwordData = getPasswordData(password);
							MenuUI.this.gamingNetworkConnection.login(accountName, passwordData);
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
						MenuUI.this.battleNetUI.hide();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.glueSpriteLayerCenter.setSequence("Death");
						MenuUI.this.menuState = MenuState.LEAVING_BATTLE_NET_FROM_LOGGED_IN;
						MenuUI.this.gamingNetworkConnection.userRequestDisconnect();
					}

					@Override
					public void openCustomGameMenu() {
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						final boolean insideTopBarMode = isInsideTopBarMode();
						if (insideTopBarMode) {
							MenuUI.this.glueSpriteLayerCenter.setSequence("Death");
						}
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU;
						if (MenuUI.this.gameListQueryTask != null) {
							MenuUI.this.gameListQueryTask.cancel();
						}
						MenuUI.this.gameListQueryTask = Timer.instance().scheduleTask(new Task() {
							@Override
							public void run() {
								MenuUI.this.gamingNetworkConnection
										.queryGamesList(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
							}
						}, 0.0f, 5.0f);
					}

					@Override
					public void enterDefaultChat() {
						requestEnterDefaultChat();
					}

					@Override
					public void returnToChat() {
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						if (MenuUI.this.battleNetUI.getCurrentChannel() == null) {
							MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_WELCOME;
						}
						else {
							final boolean insideTopBarMode = isInsideTopBarMode();
							if (insideTopBarMode) {
								MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL;
							}
							else {
								MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE;
							}
						}
					}

					@Override
					public void requestJoinChannel(final String text) {
						MenuUI.this.gamingNetworkConnection
								.joinChannel(MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), text);
					}

					@Override
					public void createAccount(final String username, final String password,
							final String repeatPassword) {
						if (!password.equals(repeatPassword)) {
							MenuUI.this.dialog.showError("NETERROR_PASSWORDMISMATCH", null);
						}
						else if (username.isEmpty()) {
							MenuUI.this.dialog.showError("ERROR_ID_NAMEBLANK", null);
						}
						else if (password.isEmpty()) {
							MenuUI.this.dialog.showError("NETERROR_NOPASSWORD", null);
						}
						else if (username.length() < 3) {
							// TODO checks like this should be server side!!!
							MenuUI.this.dialog.showError("NETERROR_USERNAMETOOSHORT", null);
						}
						else if (password.length() < 3) {
							MenuUI.this.dialog.showError("NETERROR_PASSWORDTOOSHORT", null);
						}
						else {
							final char[] passwordData = getPasswordData(password);
							MenuUI.this.gamingNetworkConnection.createAccount(username, passwordData);
						}
					}

					@Override
					public void submitChatText(final String text) {
						if (text.startsWith("/me ")) {
							MenuUI.this.gamingNetworkConnection.emoteMessage(
									MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), text.substring(4));
						}
						else {
							MenuUI.this.gamingNetworkConnection
									.chatMessage(MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), text);
						}
					}

					@Override
					public void showChannelChooserPanel() {
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CHANNEL_MENU;

					}

					@Override
					public void showCreateGameMenu() {
						MenuUI.this.battleNetUI.hideCurrentScreen();
						playCurrentBattleNetGlueSpriteDeath();
						MenuUI.this.menuState = MenuState.GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU;
					}

					@Override
					public void requestJoinGame(final String text) {
						MenuUI.this.gamingNetworkConnection
								.joinGame(MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), text);
					}

					@Override
					public void showError(final String errorKey) {
						MenuUI.this.dialog.showError(errorKey, null);
					}

					@Override
					public void createGame(final String gameName, final String mapPath, final int mapPlayerSlots,
							final LobbyGameSpeed gameSpeed, final HostedGameVisibility hostedGameVisibility,
							final long mapChecksum, final War3Map map) {
						MenuUI.this.gamingNetworkConnection.createGame(
								MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), gameName, mapPath,
								mapPlayerSlots, gameSpeed, hostedGameVisibility, mapChecksum);
						final SeekableByteChannel inputChannel = map.getInternalMpqContentsDataSource()
								.getInputChannel();
						try {
							inputChannel.position(0);
							final ByteBuffer mapDataBuffer = ByteBuffer.allocate(1300).clear();
							int mapSeqNo = 0;
							while (inputChannel.read(mapDataBuffer) != -1) {
								mapDataBuffer.flip();
								MenuUI.this.gamingNetworkConnection.uploadMapData(
										MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), mapSeqNo++,
										mapDataBuffer);
								mapDataBuffer.clear();
							}
							MenuUI.this.gamingNetworkConnection
									.mapDone(MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), mapSeqNo);
						}
						catch (final IOException e) {
							throw new IllegalStateException(e);
						}
					}

					@Override
					public void leaveCustomGame() {
						MenuUI.this.gamingNetworkConnection
								.leaveGame(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
					}

					@Override
					public void gameLobbySetPlayerRace(final int serverSlot, final int raceItemIndex) {
						MenuUI.this.gamingNetworkConnection.gameLobbySetPlayerRace(
								MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), serverSlot, raceItemIndex);
					}

					@Override
					public void gameLobbySetPlayerSlot(final int serverSlot, final LobbyPlayerType lobbyPlayerType) {
						MenuUI.this.gamingNetworkConnection.gameLobbySetPlayerSlot(
								MenuUI.this.battleNetUI.getGamingNetworkSessionToken(), serverSlot, lobbyPlayerType);
					}

					@Override
					public void startGame() {
						MenuUI.this.gamingNetworkConnection
								.gameLobbyStartGame(MenuUI.this.battleNetUI.getGamingNetworkSessionToken());
					}
				});

		this.dialog = new DialogWar3(this.rootFrame, this.uiViewport);

		// position all
		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);

		this.menuState = MenuState.GOING_TO_MAIN_MENU;

		loadSounds();

		try {
			final String glueLoopField = this.rootFrame.getSkinField("GlueScreenLoop");
			this.mainMenuGlueScreenLoop = this.uiSounds.getSound(glueLoopField);
		}
		catch (final Exception exc) {
			exc.printStackTrace();
			this.mainMenuGlueScreenLoop = new UnitSound(0, 0, 0, 0, 0, 0, false);
		}
		this.glueScreenLoop = this.mainMenuGlueScreenLoop;
		this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
	}

	public void show() {
		playMusic(this.rootFrame.trySkinField("GlueMusic"), true, 0);
		this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
	}

	private void internalStartMap(final String mapFilename, final int localPlayerIndex) {
		this.loadingFrame.setVisible(true);
		this.loadingBar.setVisible(true);
		this.loadingCustomPanel.setVisible(true);
		final DataSource codebase = WarsmashGdxMapScreen.parseDataSources(this.warsmashIni);
		final GameTurnManager turnManager;
		turnManager = GameTurnManager.PAUSED;
		final War3MapViewer viewer = new War3MapViewer(codebase, this.screenManager, this.currentMapConfig,
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

			final int campaignBackground = mapInfo.getCampaignBackground();
			int animationSequenceIndex;
			final String campaignScreenModel;
			if (campaignBackground == -1) {
				animationSequenceIndex = 0;
				String skinKey = "Default";
				for (int j = 0; j < WarsmashConstants.RACE_MANAGER.getEntryCount(); j++) {
					final CRaceManagerEntry entry = WarsmashConstants.RACE_MANAGER.get(j);
					final CRacePreference racePreference = WarsmashConstants.RACE_MANAGER
							.getRacePreferenceById(entry.getRacePrefId());
					if (this.currentMapConfig.getPlayer(localPlayerIndex).isRacePrefSet(racePreference)) {
						skinKey = entry.getKey();
						break;
					}
				}
				// NOTE: this is a heavy reload to get the user skin, because MeleeUI loads it
				// again so much later in the map load pipeline. It's probably possible to
				// optimize that, so that the stuff we load here is passed downstream to MeleeUI
				// and only loaded once.
				final GameSkin userSkin = GameUI.loadSkin(map, skinKey);
				campaignScreenModel = userSkin.getSkin().getField("LoadingMeleeBackground");
			}
			else {
				final Element loadingScreens = worldEditData.get("LoadingScreens");
				final String key = String.format("%2s", Integer.toString(campaignBackground)).replace(' ', '0');
				animationSequenceIndex = loadingScreens.getFieldValue(key, 2);
				campaignScreenModel = loadingScreens.getField(key, 3);
			}

			this.menuScreen.setModel(null, null);
			this.rootFrame.setSpriteFrameModel(this.loadingBackground, campaignScreenModel);
			this.loadingBackground.setSequence(animationSequenceIndex);
			this.rootFrame.setSpriteFrameModel(this.loadingBar, this.rootFrame.getSkinField("LoadingProgressBar"));
			this.loadingBar.setSequence(0);
			this.loadingBar.setFrameByRatio(0.0f);
			this.loadingBar.setZDepth(0.25f);
			this.rootFrame.setText(this.loadingTitleText, getStringWithWTS(wts, mapInfo.getLoadingScreenTitle()));
			this.rootFrame.setText(this.loadingSubtitleText, getStringWithWTS(wts, mapInfo.getLoadingScreenSubtitle()));
			this.loadingText.setJustifyV(TextJustify.TOP);
			this.rootFrame.setText(this.loadingText, getStringWithWTS(wts, mapInfo.getLoadingScreenText()));
			this.loadingMap = new LoadingMap(viewer, map, mapInfo);

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
		this.mainMenuFrame.setVisible(false);

		try {
			loadAndCacheMapConfigs(mapFilename);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}

		MenuUI.this.campaignMenu.setVisible(false);
		MenuUI.this.campaignBackButton.setVisible(false);
		MenuUI.this.missionSelectFrame.setVisible(false);
		MenuUI.this.campaignSelectFrame.setVisible(false);
		MenuUI.this.campaignWarcraftIIILogo.setVisible(false);
		MenuUI.this.campaignRootMenuUI.setVisible(false);
		if (this.currentMissionSelectMenuUI != null) {
			MenuUI.this.currentMissionSelectMenuUI.setVisible(false);
		}
		MenuUI.this.skirmish.setVisible(false);
		MenuUI.this.glueSpriteLayerTopLeft.setSequence("Death");
		MenuUI.this.glueSpriteLayerTopRight.setSequence("Death");
		MenuUI.this.beginGameInformation = new BeginGameInformation();
		MenuUI.this.beginGameInformation.gameMapLookup = new CurrentNetGameMapLookupPath(mapFilename);
		int localPlayerIndex = -1;
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CBasePlayer player = this.currentMapConfig.getPlayer(i);
			if (player.getController() == CMapControl.USER) {
				player.setSlotState(CPlayerSlotState.PLAYING);
//					player.setName(MenuUI.this.profileManager.getCurrentProfile());
//					break;
				if (localPlayerIndex == -1) {
					localPlayerIndex = i;
				}
			}
		}
		MenuUI.this.beginGameInformation.localPlayerIndex = localPlayerIndex;
//		this.beginGameInformation.loadingStarted = true;
		MenuUI.this.menuState = MenuState.GOING_TO_MAP;
	}

	private void loadAndCacheMapConfigs(final String mapFilename) throws IOException {
		final War3Map map = War3MapViewer.beginLoadingMap(MenuUI.this.dataSource, mapFilename);
		final War3MapW3i mapInfo = map.readMapInformation();
		final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
		MenuUI.this.rootFrame.setMapStrings(wtsFile);
		final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
		for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
			final CBasePlayer player = war3MapConfig.getPlayer(i);
			player.setName(MenuUI.this.rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
		}
		Jass2.loadConfig(map, MenuUI.this.uiViewport, MenuUI.this.uiScene, MenuUI.this.rootFrame, war3MapConfig,
				WarsmashConstants.JASS_FILE_LIST).config();
		MenuUI.this.currentMapConfig = war3MapConfig;
	}

	private void setCurrentProfile(final String selectedProfileName) {
		this.profileManager.setCurrentProfile(selectedProfileName);
		this.rootFrame.setText(MenuUI.this.profileNameText, selectedProfileName);
	}

	protected void setSinglePlayerButtonsEnabled(final boolean b) {
		this.profileButton.setEnabled(b);
		this.campaignButton.setEnabled(b);
		this.loadSavedButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		this.viewReplayButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		if (this.customCampaignButton != null) {
			this.customCampaignButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		}
		this.skirmishButton.setEnabled(b);
		this.singlePlayerCancelButton.setEnabled(b);
	}

	private void setMainMenuButtonsEnabled(final boolean b) {
		this.singlePlayerButton.setEnabled(b);
		this.battleNetButton.setEnabled(b);
		this.realmButton.setEnabled(b);
		this.localAreaNetworkButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		this.optionsButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		this.creditsButton.setEnabled(b && ENABLE_NOT_YET_IMPLEMENTED_BUTTONS);
		this.exitButton.setEnabled(b);
		if (this.editionButton != null) {
			this.editionButton.setEnabled(b);
		}
	}

	private void setMainMenuVisible(final boolean visible) {
		this.mainMenuFrame.setVisible(visible);
		this.warcraftIIILogo.setVisible(visible);
	}

	public void resize() {
		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		if (!this.hideUI) {
			final BitmapFont font = this.rootFrame.getFont();
			final BitmapFont font20 = this.rootFrame.getFont20();
			font.setColor(Color.YELLOW);
			if (WarsmashConstants.SHOW_FPS) {
				final String fpsString = "FPS: " + Gdx.graphics.getFramesPerSecond();
				glyphLayout.setText(font, fpsString);
				font.draw(batch, fpsString, (getMinWorldWidth() - glyphLayout.width) / 2,
						1100 * this.heightRatioCorrection);
			}
			this.rootFrame.render(batch, font20, glyphLayout);
		}
	}

	private float getMinWorldWidth() {
		if (this.uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) this.uiViewport).getMinWorldWidth();
		}
		return this.uiViewport.getWorldWidth();
	}

	private float getMinWorldHeight() {
		if (this.uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) this.uiViewport).getMinWorldHeight();
		}
		return this.uiViewport.getWorldHeight();
	}

	public void update(final float deltaTime) {
		if ((this.beginGameInformation != null) && (this.menuState != MenuState.GOING_TO_MAP)) {
			this.campaignFade.setVisible(false);
			if (!this.beginGameInformation.loadingStarted) {
				if (this.beginGameInformation.gameMapLookup instanceof CurrentNetGameMapLookupFile) {
					internalStartMap(((CurrentNetGameMapLookupFile) this.beginGameInformation.gameMapLookup).getFile()
							.getAbsolutePath(), this.beginGameInformation.localPlayerIndex);
				}
				else if (this.beginGameInformation.gameMapLookup instanceof CurrentNetGameMapLookupPath) {
					internalStartMap(((CurrentNetGameMapLookupPath) this.beginGameInformation.gameMapLookup).getPath(),
							this.beginGameInformation.localPlayerIndex);
				}
				else {
					throw new RuntimeException("Begin game information failed");
				}
				this.beginGameInformation.loadingStarted = true;
				return;
			}
			else {
				if (this.loadingMap != null) {
					final int localPlayerIndex = this.beginGameInformation.localPlayerIndex;
					try {
						if (this.loadingMap.activeMapLoader != null) {
							if (this.loadingMap.activeMapLoader.process()) {
								CPlayerUnitOrderListener uiOrderListener;
								final WarsmashClient warsmashClient;
								if (this.beginGameInformation.hostInetAddress != null) {

									try {
										final InetAddress byAddress = InetAddress
												.getByAddress(this.beginGameInformation.hostInetAddress);
										System.err.println("Connecting to address: " + byAddress);
										warsmashClient = new WarsmashClient(byAddress,
												this.beginGameInformation.hostUdpPort, this.loadingMap.viewer,
												this.beginGameInformation.sessionToken,
												this.beginGameInformation.serverSlotToMapSlot);
									}
									catch (final UnknownHostException e) {
										throw new RuntimeException(e);
									}
									catch (final IOException e) {
										throw new RuntimeException(e);
									}
									final WarsmashClientWriter warsmashClientWriter = warsmashClient.getWriter();
									warsmashClientWriter.joinGame();
									warsmashClientWriter.send();
									uiOrderListener = new WarsmashClientSendingOrderListener(warsmashClientWriter);
								}
								else {
									final War3MapViewer mapViewer = this.loadingMap.viewer;
									final CPlayerUnitOrderExecutor executor = new CPlayerUnitOrderExecutor(
											this.loadingMap.viewer.simulation, localPlayerIndex);
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

								// TODO not cast menu screen
								MenuUI.this.screenManager.setScreen(new WarsmashGdxMapScreen(this.loadingMap.viewer,
										this.screenManager, (WarsmashGdxMenuScreen) this.menuScreen, uiOrderListener));
								this.loadingMap = null;
								this.beginGameInformation = null;

								this.loadingBar.setVisible(false);
								this.loadingFrame.setVisible(false);
								this.loadingBackground.setVisible(false);
								if (warsmashClient != null) {
									warsmashClient.startThread();
								}
							}
							else {
								this.loadingBar.setFrameByRatio(this.loadingMap.activeMapLoader.getCompletionRatio());
							}
						}
						else {
							this.loadingMap.activeMapLoader = this.loadingMap.viewer
									.createMapLoader(this.loadingMap.map, this.loadingMap.mapInfo, localPlayerIndex);
						}
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
					return;
				}
			}
		}
		if (this.currentMusics != null) {
			if ((this.currentMusics[this.currentMusicIndex] != null)
					&& !this.currentMusics[this.currentMusicIndex].isPlaying()) {
				if (this.currentMusicRandomizeIndex) {
					this.currentMusicIndex = (int) (Math.random() * this.currentMusics.length);
				}
				else {
					this.currentMusicIndex = (this.currentMusicIndex + 1) % this.currentMusics.length;
				}
				this.currentMusics[this.currentMusicIndex].play();
			}
		}
		if ((this.focusUIFrame != null) && !this.focusUIFrame.isVisibleOnScreen()) {
			setFocusFrame(getNextFocusFrame());
		}

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = this.uiViewport.getScreenX();
		final int maxX = minX + this.uiViewport.getScreenWidth();
		final int minY = this.uiViewport.getScreenY();
		final int maxY = minY + this.uiViewport.getScreenHeight();

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			if (WarsmashConstants.CATCH_CURSOR) {
				Gdx.input.setCursorPosition(mouseX, mouseY);
			}
		}

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);
		this.cursorFrame.setSequence("Normal");

		if (this.glueSpriteLayerTopRight.isSequenceEnded() && this.glueSpriteLayerTopLeft.isSequenceEnded()
				&& (!this.campaignFade.isVisible() || this.campaignFade.isSequenceEnded())
				&& (!this.battleNetUI.getDoors().isVisible() || this.battleNetUI.getDoors().isSequenceEnded())) {
			switch (this.menuState) {
			case GOING_TO_MAIN_MENU:
				this.glueSpriteLayerTopLeft.setSequence("MainMenu Birth");
				this.glueSpriteLayerTopRight.setSequence("MainMenu Birth");
				if (this.battleNetUI.getDoors().isVisible()) {
					this.battleNetUI.getDoors().setVisible(false);
					this.battleNetUI.setVisible(false);
				}
				this.menuState = MenuState.MAIN_MENU;
				break;
			case MAIN_MENU:
				setMainMenuVisible(true);
				this.glueSpriteLayerTopLeft.setSequence("MainMenu Stand");
				this.glueSpriteLayerTopRight.setSequence("MainMenu Stand");
				break;
			case GOING_TO_BATTLE_NET_LOGIN:
				this.glueSpriteLayerTopLeft.setSequence("Death");
				this.glueSpriteLayerTopRight.setSequence("Death");
				MenuUI.this.battleNetUI.setVisible(true);
				final SpriteFrame doors = MenuUI.this.battleNetUI.getDoors();
				doors.setVisible(true);
				doors.setSequence(PrimaryTag.BIRTH);
				this.menuState = MenuState.GOING_TO_BATTLE_NET_LOGIN_PART2;
				break;
			case GOING_TO_BATTLE_NET_LOGIN_PART2:
				MenuUI.this.menuScreen.alternateModelToBattlenet();
				this.battleNetUI.showLoginPrompt(this.gamingNetworkConnection.getGatewayString());
				this.menuState = MenuState.BATTLE_NET_LOGIN;
				break;
			case LEAVING_BATTLE_NET_FROM_LOGGED_IN:
				MenuUI.this.menuScreen.unAlternateModelBackToNormal();
				this.glueSpriteLayerCenter.setVisible(false);
				playMusic(this.rootFrame.trySkinField("GlueMusic"), true, 0);
				// no break
			case LEAVING_BATTLE_NET:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("Birth");
				this.menuState = MenuState.GOING_TO_MAIN_MENU;
				break;
			case GOING_TO_BATTLE_NET_WELCOME:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetWelcome Birth");
				this.glueSpriteLayerCenter.setVisible(true);
				this.glueSpriteLayerCenter.setSequence("Birth");
				this.menuState = MenuState.BATTLE_NET_WELCOME;
				playMusic(this.rootFrame.trySkinField("ChatMusic"), true, 0);
				break;
			case BATTLE_NET_WELCOME:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetWelcome Stand");
				this.battleNetUI.showWelcomeScreen();
				this.glueSpriteLayerCenter.setSequence("Stand");
				this.menuState = MenuState.BATTLE_NET_WELCOME;
				break;
			case GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustom Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustom Birth");
				this.menuState = MenuState.BATTLE_NET_CUSTOM_GAME_MENU;
				break;
			case BATTLE_NET_CUSTOM_GAME_MENU:
				this.battleNetUI.showCustomGameMenu();
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustom Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustom Stand");
				break;
			case GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Birth");
				this.menuState = MenuState.BATTLE_NET_CREATE_CUSTOM_GAME_MENU;
				break;
			case BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
				this.battleNetUI.showCustomGameCreateMenu();
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Stand");
				break;
			case GOING_TO_BATTLE_NET_CHANNEL_MENU:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChannel Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChannel Birth");
				this.menuState = MenuState.BATTLE_NET_CHANNEL_MENU;
				break;
			case BATTLE_NET_CHANNEL_MENU:
				this.battleNetUI.showChannelMenu();
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChannel Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChannel Stand");
				break;
			case GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE:
				this.glueSpriteLayerCenter.setVisible(true);
				this.glueSpriteLayerCenter.setSequence("Birth");
			case GOING_TO_BATTLE_NET_CHAT_CHANNEL:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Birth");
				this.menuState = MenuState.BATTLE_NET_CHAT_CHANNEL;
				break;
			case BATTLE_NET_CHAT_CHANNEL:
				this.battleNetUI.showChatChannel();
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Stand");
				break;
			case GOING_TO_BATTLE_NET_CUSTOM_GAME_LOBBY:
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("MultiplayerPreGameChat Birth");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("MultiplayerPreGameChat Birth");
				this.menuState = MenuState.BATTLE_NET_CUSTOM_GAME_LOBBY;
				break;
			case BATTLE_NET_CUSTOM_GAME_LOBBY:
				this.battleNetUI.showCustomGameLobby();
				MenuUI.this.glueSpriteLayerTopLeft.setSequence("MultiplayerPreGameChat Stand");
				MenuUI.this.glueSpriteLayerTopRight.setSequence("MultiplayerPreGameChat Stand");
				break;
			case GOING_TO_SINGLE_PLAYER:
				this.glueSpriteLayerTopLeft.setSequence("SinglePlayer Birth");
				this.glueSpriteLayerTopRight.setSequence("SinglePlayer Birth");
				this.menuState = MenuState.SINGLE_PLAYER;
				break;
			case GOING_TO_MAP:
				if ((this.beginGameInformation != null) && (this.beginGameInformation.sessionToken != 0)) {
					this.menuState = MenuState.BATTLE_NET_CUSTOM_GAME_LOBBY;
				}
				else {
					this.menuState = MenuState.SINGLE_PLAYER;
				}
				break;
			case LEAVING_CAMPAIGN:
				this.glueSpriteLayerTopLeft.setSequence("Birth");
				this.glueSpriteLayerTopRight.setSequence("Birth");
				if (this.campaignFade.isVisible()) {
					this.campaignFade.setSequence("Death");
				}
				this.glueScreenLoop.stop();
				this.glueScreenLoop = this.mainMenuGlueScreenLoop;
				this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
				this.menuScreen.setModel(this.rootFrame.getSkinField("GlueSpriteLayerBackground"),
						this.menuFogSettings);
				this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
				this.menuState = MenuState.GOING_TO_SINGLE_PLAYER;
				break;
			case SINGLE_PLAYER:
				this.singlePlayerMenu.setVisible(true);
				this.campaignFade.setVisible(false);
				setSinglePlayerButtonsEnabled(true);
				this.glueSpriteLayerTopLeft.setSequence("SinglePlayer Stand");
				this.glueSpriteLayerTopRight.setSequence("SinglePlayer Stand");
				break;
			case GOING_TO_SINGLE_PLAYER_SKIRMISH:
				this.glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Birth");
				this.glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Birth");
				this.menuState = MenuState.SINGLE_PLAYER_SKIRMISH;
				break;
			case SINGLE_PLAYER_SKIRMISH:
				this.skirmish.setVisible(true);
				this.mapInfoPanel.setVisible(true);
				this.advancedOptionsPanel.setVisible(false);
				this.glueSpriteLayerTopLeft.setSequence("SinglePlayerSkirmish Stand");
				this.glueSpriteLayerTopRight.setSequence("SinglePlayerSkirmish Stand");
				break;
			case GOING_TO_CAMPAIGN:
				this.glueSpriteLayerTopLeft.setSequence("Death");
				this.glueSpriteLayerTopRight.setSequence("Death");
				this.campaignMenu.setVisible(true);
				this.campaignFade.setVisible(true);
				this.campaignFade.setSequence("Birth");
				this.menuState = MenuState.GOING_TO_CAMPAIGN_PART2;
				break;
			case GOING_TO_CAMPAIGN_PART2: {
				final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
				final String currentCampaignAmbientSound = this.rootFrame
						.trySkinField(this.currentCampaign.getAmbientSound());
				this.menuScreen.setModel(currentCampaignBackgroundModel,
						this.currentCampaign.getBackgroundFogSettings());
				this.glueScreenLoop.stop();
				this.glueScreenLoop = this.uiSounds.getSound(currentCampaignAmbientSound);
				this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
				final DataTable skinData = this.rootFrame.getSkinData();
				final String cursorSkin = getRaceNameByCursorID(this.currentCampaign.getCursor());
				this.rootFrame.setSpriteFrameModel(this.cursorFrame, skinData.get(cursorSkin).getField("Cursor"));

				this.campaignFade.setSequence("Death");
				this.menuState = MenuState.CAMPAIGN;
				break;
			}
			case CAMPAIGN:
				this.campaignMenu.setVisible(true);
				this.campaignBackButton.setVisible(true);
				this.campaignWarcraftIIILogo.setVisible(true);
				this.campaignSelectFrame.setVisible(true);
				this.campaignRootMenuUI.setVisible(true);
				break;
			case GOING_TO_MISSION_SELECT: {
				final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
				final String currentCampaignAmbientSound = this.rootFrame
						.trySkinField(this.currentCampaign.getAmbientSound());
				this.menuScreen.setModel(currentCampaignBackgroundModel,
						this.currentCampaign.getBackgroundFogSettings());
				this.glueScreenLoop.stop();
				this.glueScreenLoop = this.uiSounds.getSound(currentCampaignAmbientSound);
				this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
				final DataTable skinData = this.rootFrame.getSkinData();
				final String cursorSkin = getRaceNameByCursorID(this.currentCampaign.getCursor());
				this.rootFrame.setSpriteFrameModel(this.cursorFrame, skinData.get(cursorSkin).getField("Cursor"));

				this.campaignFade.setSequence("Death");
				this.menuState = MenuState.MISSION_SELECT;
				break;
			}
			case MISSION_SELECT:
				this.campaignMenu.setVisible(true);
				this.campaignBackButton.setVisible(true);
				this.campaignWarcraftIIILogo.setVisible(true);
				this.currentMissionSelectMenuUI.setVisible(true);
				this.missionSelectFrame.setVisible(true);
				break;
			case GOING_TO_SINGLE_PLAYER_PROFILE:
				this.glueSpriteLayerTopLeft.setSequence("RealmSelection Birth");
				this.menuState = MenuState.SINGLE_PLAYER_PROFILE;
				break;
			case SINGLE_PLAYER_PROFILE:
				this.profilePanel.setVisible(true);
				setSinglePlayerButtonsEnabled(true);
				this.glueSpriteLayerTopLeft.setSequence("RealmSelection Stand");
				// TODO the below should probably be some generic focusing thing when we enter a
				// new view?
				if ((this.newProfileEditBox != null) && this.newProfileEditBox.isFocusable()) {
					setFocusFrame(this.newProfileEditBox);
				}
				break;
			case QUITTING:
				Gdx.app.exit();
				break;
			case RESTARTING:
				MenuUI.this.screenManager
						.setScreen(new WarsmashGdxMenuScreen(MenuUI.this.warsmashIni, this.screenManager));
				break;
			default:
				break;
			}
		}

	}

	private FocusableFrame getNextFocusFrame() {
		return this.rootFrame.getNextFocusFrame();
	}

	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame != null) {
			if (clickedUIFrame instanceof ClickableFrame) {
				this.mouseDownUIFrame = (ClickableFrame) clickedUIFrame;
				this.mouseDownUIFrame.mouseDown(this.rootFrame, this.uiViewport);
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
		if (this.focusUIFrame != null) {
			this.focusUIFrame.onFocusLost();
		}
		this.focusUIFrame = clickedFocusableFrame;
		if (this.focusUIFrame != null) {
			this.focusUIFrame.onFocusGained();
		}
	}

	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				final String soundKey = this.mouseDownUIFrame.getSoundKey();
				if (soundKey != null) {
					this.uiSounds.getSound(soundKey).play(this.uiScene.audioContext, 0, 0, 0);
				}
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		this.mouseDownUIFrame = null;
		return false;
	}

	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		if (this.mouseDownUIFrame != null) {
			this.mouseDownUIFrame.mouseDragged(this.rootFrame, this.uiViewport, screenCoordsVector.x,
					screenCoordsVector.y);
		}
		return false;
	}

	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = this.rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (mousedUIFrame != this.mouseOverUIFrame) {
			if (this.mouseOverUIFrame != null) {
				this.mouseOverUIFrame.mouseExit(this.rootFrame, this.uiViewport);
			}
			if (mousedUIFrame instanceof ClickableFrame) {
				this.mouseOverUIFrame = (ClickableFrame) mousedUIFrame;
				this.mouseOverUIFrame.mouseEnter(this.rootFrame, this.uiViewport);
			}
			else {
				this.mouseOverUIFrame = null;
			}
		}
		return false;
	}

	private void loadSounds() {
		this.worldEditStrings = new WorldEditStrings(this.dataSource);
		this.uiSoundsTable = new DataTable(this.worldEditStrings);
		try {
			try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\SoundInfo\\UISounds.slk")) {
				this.uiSoundsTable.readSLK(miscDataTxtStream);
			}
			try (InputStream miscDataTxtStream = this.dataSource
					.getResourceAsStream("UI\\SoundInfo\\AmbienceSounds.slk")) {
				this.uiSoundsTable.readSLK(miscDataTxtStream);
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		this.uiSounds = new KeyedSounds(this.uiSoundsTable, this.dataSource);
	}

	public KeyedSounds getUiSounds() {
		return this.uiSounds;
	}

	private static final class PlayerSlotPaneListenerImplementation implements PlayerSlotPaneListener {
		private final TeamSetupPane teamSetupPane;
		private final War3MapConfig war3MapConfig;
		private final War3MapW3i mapInfo;
		private final GameUI rootFrame;
		private final Viewport uiViewport;

		private PlayerSlotPaneListenerImplementation(final TeamSetupPane teamSetupPane,
				final War3MapConfig war3MapConfig, final War3MapW3i mapInfo, final GameUI rootFrame,
				final Viewport uiViewport) {
			this.teamSetupPane = teamSetupPane;
			this.war3MapConfig = war3MapConfig;
			this.mapInfo = mapInfo;
			this.rootFrame = rootFrame;
			this.uiViewport = uiViewport;
		}

		@Override
		public void setPlayerSlot(final int index, final LobbyPlayerType lobbyPlayerType) {
			final CBasePlayer player = this.war3MapConfig.getPlayer(index);
			switch (lobbyPlayerType) {
			case OPEN:
				player.setController(CMapControl.NONE);
				player.setSlotState(CPlayerSlotState.EMPTY);
				player.setAIDifficulty(null);
				break;
			case CLOSED:
				player.setController(CMapControl.NONE);
				player.setSlotState(CPlayerSlotState.PLAYING);
				player.setAIDifficulty(null);
				break;
			case COMPUTER_NEWBIE:
				player.setController(CMapControl.COMPUTER);
				player.setSlotState(CPlayerSlotState.PLAYING);
				player.setAIDifficulty(AIDifficulty.NEWBIE);
				break;
			case COMPUTER_NORMAL:
				player.setController(CMapControl.COMPUTER);
				player.setSlotState(CPlayerSlotState.PLAYING);
				player.setAIDifficulty(AIDifficulty.NORMAL);
				break;
			case COMPUTER_INSANE:
				player.setController(CMapControl.COMPUTER);
				player.setSlotState(CPlayerSlotState.PLAYING);
				player.setAIDifficulty(AIDifficulty.INSANE);
				break;
			case USER:
				player.setController(CMapControl.USER);
				player.setSlotState(CPlayerSlotState.PLAYING);
				player.setAIDifficulty(null);
				break;
			}
			this.teamSetupPane.notifyPlayerDataUpdated(index, this.rootFrame, this.uiViewport, this.war3MapConfig,
					this.mapInfo);
		}

		@Override
		public void setPlayerRace(final int index, final int raceItemIndex) {
			final CBasePlayer player = this.war3MapConfig.getPlayer(index);
			if (raceItemIndex == 0) {
				player.setRacePref(WarsmashConstants.RACE_MANAGER.getRandomRacePreference());
			}
			else {
				final CRace race = WarsmashConstants.RACE_MANAGER.getRace(raceItemIndex);
				final CRacePreference racePreference = WarsmashConstants.RACE_MANAGER.getRacePreferenceForRace(race);
				player.setRacePref(racePreference);
			}
			this.teamSetupPane.notifyPlayerDataUpdated(index, this.rootFrame, this.uiViewport, this.war3MapConfig,
					this.mapInfo);
		}
	}

	private static enum MenuState {
		GOING_TO_MAIN_MENU,
		MAIN_MENU,
		GOING_TO_BATTLE_NET_LOGIN,
		GOING_TO_BATTLE_NET_LOGIN_PART2,
		BATTLE_NET_LOGIN,
		LEAVING_BATTLE_NET,
		LEAVING_BATTLE_NET_FROM_LOGGED_IN,
		GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU,
		BATTLE_NET_CUSTOM_GAME_MENU,
		GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU,
		BATTLE_NET_CREATE_CUSTOM_GAME_MENU,
		GOING_TO_BATTLE_NET_CHANNEL_MENU,
		BATTLE_NET_CHANNEL_MENU,
		GOING_TO_BATTLE_NET_WELCOME,
		BATTLE_NET_WELCOME,
		GOING_TO_SINGLE_PLAYER,
		LEAVING_CAMPAIGN,
		SINGLE_PLAYER,
		GOING_TO_SINGLE_PLAYER_SKIRMISH,
		SINGLE_PLAYER_SKIRMISH,
		GOING_TO_MAP,
		GOING_TO_CAMPAIGN,
		GOING_TO_CAMPAIGN_PART2,
		GOING_TO_MISSION_SELECT,
		MISSION_SELECT,
		CAMPAIGN,
		GOING_TO_SINGLE_PLAYER_PROFILE,
		SINGLE_PLAYER_PROFILE,
		GOING_TO_LOADING_SCREEN,
		QUITTING,
		RESTARTING,
		GOING_TO_BATTLE_NET_CHAT_CHANNEL,
		GOING_TO_BATTLE_NET_CHAT_CHANNEL_FROM_OUTSIDE,
		BATTLE_NET_CHAT_CHANNEL,
		GOING_TO_BATTLE_NET_CUSTOM_GAME_LOBBY,
		BATTLE_NET_CUSTOM_GAME_LOBBY;
	}

	public void hide() {
		this.glueScreenLoop.stop();
		stopMusic();
	}

	public void dispose() {
		if (this.rootFrame != null) {
			this.rootFrame.dispose();
		}
	}

	public boolean keyDown(final int keycode) {
		if (this.focusUIFrame != null) {
			this.focusUIFrame.keyDown(keycode);
		}
		return false;
	}

	public boolean keyUp(final int keycode) {
		if (keycode == Input.Keys.TAB) {
			// accessibility tab focus ui
			final List<FocusableFrame> focusableFrames = this.rootFrame.getFocusableFrames();
			final int indexOf = focusableFrames.indexOf(this.focusUIFrame) + 1;
			for (int i = indexOf; i < focusableFrames.size(); i++) {
				final FocusableFrame focusableFrame = focusableFrames.get(i);
				if (focusableFrame.isVisibleOnScreen() && focusableFrame.isFocusable()) {
					setFocusFrame(focusableFrame);
					break;
				}
			}
		}
		else {
			if (this.focusUIFrame != null) {
				this.focusUIFrame.keyUp(keycode);
			}
		}
		return false;
	}

	public boolean keyTyped(final char character) {
		if (this.focusUIFrame != null) {
			this.focusUIFrame.keyTyped(character);
		}
		if ((character == 'z') && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
			this.hideUI = !this.hideUI;
			this.uiScene.show = !this.uiScene.show;
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
		switch (this.menuState) {
		default:
		case GOING_TO_MAIN_MENU:
		case MAIN_MENU:
			this.glueScreenLoop.stop();
			this.glueScreenLoop = this.mainMenuGlueScreenLoop;
			this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
			this.menuScreen.setModel(this.rootFrame.getSkinField("GlueSpriteLayerBackground"), this.menuFogSettings);
			this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
			break;
		case CAMPAIGN:
		case MISSION_SELECT:
			final String currentCampaignBackgroundModel = getCurrentBackgroundModel();
			final String currentCampaignAmbientSound = this.rootFrame
					.trySkinField(this.currentCampaign.getAmbientSound());
			this.menuScreen.setModel(currentCampaignBackgroundModel, this.currentCampaign.getBackgroundFogSettings());
			this.glueScreenLoop.stop();
			this.glueScreenLoop = this.uiSounds.getSound(currentCampaignAmbientSound);
			this.glueScreenLoop.play(this.uiScene.audioContext, 0f, 0f, 0f);
			final DataTable skinData = this.rootFrame.getSkinData();
			final String cursorSkin = getRaceNameByCursorID(this.currentCampaign.getCursor());
			this.rootFrame.setSpriteFrameModel(this.cursorFrame, skinData.get(cursorSkin).getField("Cursor"));
			break;
		case BATTLE_NET_CUSTOM_GAME_LOBBY: {
			this.menuScreen.setModel(this.rootFrame.getSkinField("GlueSpriteLayerBackground"), this.menuFogSettings);
			MenuUI.this.menuScreen.alternateModelToBattlenet();
			this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
			requestEnterDefaultChat();
			break;
		}
		}
//		MenuUI.this.campaignFade.setSequence("Death");
//		this.campaignFade.setVisible(true);
//		this.menuState = MenuState.MISSION_SELECT;
	}

	private String getRaceNameByCursorID(final int cursorId) {
		final CRaceManagerEntry raceEntry = WarsmashConstants.RACE_MANAGER.get(getRaceByCursorID(cursorId));
		return raceEntry.getKey();
	}

	private CRace getRaceByCursorID(final int cursorId) {
		final int raceId = cursorId + 1;
		final CRace race = WarsmashConstants.RACE_MANAGER.getRace(raceId);
		if (race == null) {
			return WarsmashConstants.RACE_MANAGER.getRace(1); // when in doubt, default to human
		}
		return race;
	}

	private String getCurrentBackgroundModel() {
		final String background = this.currentCampaign.getBackground();
		final String versionedBackground = background;
		if (this.rootFrame.hasSkinField(versionedBackground)) {
			return this.rootFrame.getSkinField(versionedBackground);
		}
		return this.rootFrame.getSkinField(background);
	}

	private static final class LoadingMap {

		private final War3MapViewer viewer;
		private final War3Map map;
		private final War3MapW3i mapInfo;

		private MapLoader activeMapLoader = null;

		public LoadingMap(final War3MapViewer viewer, final War3Map map, final War3MapW3i mapInfo) {
			this.viewer = viewer;
			this.map = map;
			this.mapInfo = mapInfo;
		}

	}

	private void stopMusic() {
		if (this.currentMusics != null) {
			for (final Music music : this.currentMusics) {
				if (music != null) {
					music.stop();
				}
			}
			this.currentMusics = null;
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
				if (this.musicSLK.get(musicPath) != null) {
					musicPath = this.musicSLK.get(musicPath).getField("FileNames");
				}
				final String[] moreSplitMusics = musicPath.split(",");
				for (final String finalSplitPath : moreSplitMusics) {
					musicPaths.add(finalSplitPath);
				}
			}
			final String[] musics = musicPaths.toArray(new String[musicPaths.size()]);

			this.currentMusics = new Music[musics.length];
			int validMusicCount = 0;
			for (int i = 0; i < musics.length; i++) {
				if (this.viewer.dataSource.has(musics[i])) {
					final Music newMusic = Gdx.audio
							.newMusic(new DataSourceFileHandle(this.viewer.dataSource, musics[i]));
					newMusic.setVolume(1.0f);
					this.currentMusics[i] = newMusic;
					validMusicCount++;
				}
			}
			if (this.currentMusics.length != validMusicCount) {
				final Music[] fixedList = new Music[validMusicCount];
				int fixedListIndex = 0;
				for (int i = 0; i < this.currentMusics.length; i++) {
					if (this.currentMusics[i] != null) {
						fixedList[fixedListIndex++] = this.currentMusics[i];
					}
				}
				this.currentMusics = fixedList;
			}
			if (random) {
				index = (int) (Math.random() * this.currentMusics.length);
			}
			this.currentMusicIndex = index;
			this.currentMusicRandomizeIndex = random;
			if (this.currentMusics[index] != null) {
				this.currentMusics[index].play();
			}
		}
		return null;
	}

	public void playCurrentBattleNetGlueSpriteDeath() {
		switch (MenuUI.this.menuState) {
		case BATTLE_NET_LOGIN:
		case GOING_TO_BATTLE_NET_LOGIN:
		case GOING_TO_BATTLE_NET_LOGIN_PART2:
			this.battleNetUI.getDoors().setSequence(PrimaryTag.DEATH);
			break;
		case BATTLE_NET_CHAT_CHANNEL:
		case GOING_TO_BATTLE_NET_CHAT_CHANNEL:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChatRoom Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChatRoom Death");
			break;
		case BATTLE_NET_CUSTOM_GAME_MENU:
		case GOING_TO_BATTLE_NET_CUSTOM_GAME_MENU:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustom Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustom Death");
			break;
		case BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
		case GOING_TO_BATTLE_NET_CREATE_CUSTOM_GAME_MENU:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetCustomCreate Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetCustomCreate Death");
			break;
		case BATTLE_NET_CHANNEL_MENU:
		case GOING_TO_BATTLE_NET_CHANNEL_MENU:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetChannel Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetChannel Death");
			break;
		case BATTLE_NET_CUSTOM_GAME_LOBBY:
		case GOING_TO_BATTLE_NET_CUSTOM_GAME_LOBBY:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("MultiplayerPreGameChat Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("MultiplayerPreGameChat Death");
			break;
		default:
		case BATTLE_NET_WELCOME:
		case GOING_TO_BATTLE_NET_WELCOME:
			MenuUI.this.glueSpriteLayerTopLeft.setSequence("BattleNetWelcome Death");
			MenuUI.this.glueSpriteLayerTopRight.setSequence("BattleNetWelcome Death");
			break;
		}
		if (this.gameListQueryTask != null) {
			this.gameListQueryTask.cancel();
			this.gameListQueryTask = null;
		}
	}

	private boolean isInsideTopBarMode() {
		boolean insideTopBarMode = false;
		switch (MenuUI.this.menuState) {
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

	private void requestEnterDefaultChat() {
		MenuUI.this.gamingNetworkConnection.joinChannel(MenuUI.this.battleNetUI.getGamingNetworkSessionToken(),
				"Frozen Throne USA-1"); // TODO
										// maybe
										// not
										// hardcode
										// this
	}
}
