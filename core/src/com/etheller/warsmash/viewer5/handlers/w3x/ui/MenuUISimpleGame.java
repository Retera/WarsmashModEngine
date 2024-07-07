package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.networking.WarsmashClient;
import com.etheller.warsmash.networking.WarsmashClientSendingOrderListener;
import com.etheller.warsmash.networking.WarsmashClientWriter;
import com.etheller.warsmash.networking.uberserver.GamingNetworkConnectionImpl;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
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
import com.etheller.warsmash.viewer5.FogSettings;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderExecutor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListenerDelaying;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookupFile;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.CurrentNetGameMapLookupPath;

public class MenuUISimpleGame implements WarsmashMenuUI {

	private final DataSource dataSource;
	private final Viewport uiViewport;
	private final Scene uiScene;
	private final MdxViewer viewer;
	private final WarsmashGdxMultiScreenGame game;
	private final WarsmashGdxMenuScreen menuScreen;
	private final DataTable warsmashIni;
	private final RootFrameListener rootFrameListener;
	private final GamingNetworkConnectionImpl gamingNetworkConnectionImpl;
	private final String mapDownloadDir;
	private GameUI rootFrame;
	private Stage stage;
	private TextButton singlePlayerButton;
	private TextButton.TextButtonStyle textButtonStyle;
	private TextButton multiPlayerButton;
	private TextButton quitButton;
	private TextButton singlePlayerBackButton;
	private TextButton singlePlayerDebugMissionButton;
	private War3MapConfig currentMapConfig;
	private LoadingMap loadingMap;
	private BeginGameInformation beginGameInformation;
	private Music[] currentMusics;
	private final DataTable musicSLK;
	private int currentMusicIndex;
	private boolean currentMusicRandomizeIndex;

	public MenuUISimpleGame(DataSource dataSource, Viewport uiViewport, Scene uiScene, MdxViewer viewer,
			WarsmashGdxMultiScreenGame game, WarsmashGdxMenuScreen warsmashGdxMenuScreen, DataTable warsmashIni,
			RootFrameListener rootFrameListener, GamingNetworkConnectionImpl gamingNetworkConnectionImpl,
			String mapDownloadDir) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.uiScene = uiScene;
		this.viewer = viewer;
		this.game = game;
		this.menuScreen = warsmashGdxMenuScreen;
		this.warsmashIni = warsmashIni;
		this.rootFrameListener = rootFrameListener;
		this.gamingNetworkConnectionImpl = gamingNetworkConnectionImpl;
		this.mapDownloadDir = mapDownloadDir;

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
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	@Override
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		final DataTable miscData = new DataTable(null /* no strings for now */);
		try (InputStream miscDataTxtStream = this.dataSource.getResourceAsStream("UI\\MiscData.txt")) {
			miscData.readTXT(miscDataTxtStream, true);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		final FogSettings menuFogSettings = new FogSettings();
		final Element zFogElement = miscData.get("MenuZFog");
		if (zFogElement != null) {
			final int styleValue = zFogElement.getFieldAsInteger("Style", WarsmashConstants.GAME_VERSION) + 1;
			menuFogSettings.setStyleByIndex(styleValue);
			menuFogSettings.start = zFogElement.getFieldAsFloat("Start", WarsmashConstants.GAME_VERSION);
			menuFogSettings.end = zFogElement.getFieldAsFloat("End", WarsmashConstants.GAME_VERSION);
			menuFogSettings.density = zFogElement.getFieldAsFloat("Density", WarsmashConstants.GAME_VERSION);
			final float a = zFogElement.getFieldAsFloat("Color", WarsmashConstants.GAME_VERSION * 4) / 255f;
			final float r = zFogElement.getFieldAsFloat("Color", 1 + (WarsmashConstants.GAME_VERSION * 4)) / 255f;
			final float g = zFogElement.getFieldAsFloat("Color", 2 + (WarsmashConstants.GAME_VERSION * 4)) / 255f;
			final float b = zFogElement.getFieldAsFloat("Color", 3 + (WarsmashConstants.GAME_VERSION * 4)) / 255f;
			menuFogSettings.color = new Color(r, g, b, a);
		}
		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, WarsmashConstants.GAME_VERSION),
				this.uiViewport, this.uiScene, this.viewer, 0, WTS.DO_NOTHING);
		try {
			this.rootFrame.loadTOCFile(this.rootFrame.getSkinField("MenuTOC"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		this.stage = new Stage(this.uiViewport, this.menuScreen.getBatch());

		this.menuScreen.setModel(this.rootFrame.getSkinField("GlueSpriteLayerBackground"), menuFogSettings);
		this.rootFrameListener.onCreate(this.rootFrame);

		final TextureFrame textureFrame = this.rootFrame.createTextureFrame("Logo", this.rootFrame, true,
				TextureFrame.DEFAULT_TEX_COORDS);
		textureFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.rootFrame, FramePoint.TOPLEFT,
				GameUI.convertX(this.uiViewport, 0.01f), GameUI.convertY(this.uiViewport, -0.01f)));
		textureFrame.setWidth(GameUI.convertX(this.uiViewport, 0.15f));
		textureFrame.setHeight(GameUI.convertY(this.uiViewport, 0.075f));
		textureFrame.setTexture("GlueSpriteLayerLogo", this.rootFrame);

		createStyles();
		final Group singlePlayerGroup = createSinglePlayerGroup();
		final Group mainMenuGroup = createMainMenuGroup();
		this.stage.setRoot(mainMenuGroup);
		this.singlePlayerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuUISimpleGame.this.stage.setRoot(singlePlayerGroup);
			}
		});
		this.singlePlayerBackButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuUISimpleGame.this.stage.setRoot(mainMenuGroup);
			}
		});
		this.singlePlayerDebugMissionButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startMap("Maps\\DebugMission.afm");
			}
		});
		this.quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);
	}

	private Group createSinglePlayerGroup() {
		final Group singlePlayerGroup = new Group();
		this.singlePlayerBackButton = createTextButton(singlePlayerGroup, "MAINMENU_BACK", 0.5f, 0.1f, 0.2f, 0.05f);
		this.singlePlayerDebugMissionButton = createTextButton(singlePlayerGroup, "MAINMENU_PLAY_DEBUG", 0.5f, 0.45f,
				0.2f, 0.05f);
		return singlePlayerGroup;
	}

	private Group createMainMenuGroup() {
		final Group mainMenuGroup = new Group();
		this.singlePlayerButton = createTextButton(mainMenuGroup, "MAINMENU_SINGLE_PLAYER", 0.5f, 0.45f, 0.2f, 0.05f);
		this.multiPlayerButton = createTextButton(mainMenuGroup, "MAINMENU_MULTI_PLAYER", 0.5f, 0.39f, 0.2f, 0.05f);
		this.multiPlayerButton.setVisible(false);
		this.quitButton = createTextButton(mainMenuGroup, "MAINMENU_QUIT", 0.5f, 0.1f, 0.2f, 0.05f);
		return mainMenuGroup;
	}

	@Override
	public void show() {
		playMusic(this.rootFrame.trySkinField("GlueMusic"), true, 0);
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void update(float deltaTime) {
		if ((this.beginGameInformation != null) && (isDoneWithUIAnimationGoingToMap())) {
			if (!this.beginGameInformation.loadingStarted) {
				if (this.beginGameInformation.gameMapLookup instanceof CurrentNetGameMapLookupFile) {
					internalStartMap(((CurrentNetGameMapLookupFile) this.beginGameInformation.gameMapLookup).getFile()
							.getAbsolutePath());
				}
				else if (this.beginGameInformation.gameMapLookup instanceof CurrentNetGameMapLookupPath) {
					internalStartMap(((CurrentNetGameMapLookupPath) this.beginGameInformation.gameMapLookup).getPath());
				}
				else {
					throw new RuntimeException("Begin game information failed");
				}
				this.beginGameInformation.loadingStarted = true;
				return;
			}
			else {
				if (this.loadingMap != null) {
					int localPlayerIndex = this.beginGameInformation.localPlayerIndex;
					try {
						this.loadingMap.viewer.loadMap(this.loadingMap.map, this.loadingMap.mapInfo, localPlayerIndex);
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
					CPlayerUnitOrderListener uiOrderListener;
					final WarsmashClient warsmashClient;
					if (this.beginGameInformation.hostInetAddress != null) {

						try {
							final InetAddress byAddress = InetAddress
									.getByAddress(this.beginGameInformation.hostInetAddress);
							System.err.println("Connecting to address: " + byAddress);
							warsmashClient = new WarsmashClient(byAddress, this.beginGameInformation.hostUdpPort,
									this.loadingMap.viewer, this.beginGameInformation.sessionToken,
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

					this.game.setScreen(new WarsmashGdxMapScreen(this.loadingMap.viewer, this.game, this.menuScreen,
							uiOrderListener));
					this.loadingMap = null;
					this.beginGameInformation = null;

					showLoadingBars(false);
					if (warsmashClient != null) {
						warsmashClient.startThread();
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
		this.stage.act(deltaTime);
	}

	private boolean isDoneWithUIAnimationGoingToMap() {
		return true;
	}

	@Override
	public void render(SpriteBatch batch, GlyphLayout glyphLayout) {
		final BitmapFont font20 = this.rootFrame.getFont20();
		this.rootFrame.render(batch, font20, glyphLayout);
		batch.end();
		this.stage.draw();
		batch.begin();
	}

	@Override
	public void dispose() {
		this.stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float worldScreenY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float worldScreenY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float worldScreenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY, float worldScreenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(this.menuScreen);
	}

	private void loadAndCacheMapConfigs(final String mapFilename) throws IOException {
		final War3Map map = War3MapViewer.beginLoadingMap(this.dataSource, mapFilename);
		final War3MapW3i mapInfo = map.readMapInformation();
		final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
		this.rootFrame.setMapStrings(wtsFile);
		final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
		for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
			final CBasePlayer player = war3MapConfig.getPlayer(i);
			player.setName(this.rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
		}
		Jass2.loadConfig(map, this.uiViewport, this.uiScene, this.rootFrame, war3MapConfig,
				WarsmashConstants.JASS_FILE_LIST).config();
		this.currentMapConfig = war3MapConfig;
	}

	private void internalStartMap(final String mapFilename) {
		showLoadingBars(true);
		final DataSource codebase = WarsmashGdxMapScreen.parseDataSources(this.warsmashIni);
		final GameTurnManager turnManager;
		turnManager = GameTurnManager.PAUSED;
		final War3MapViewer viewer = new War3MapViewer(codebase, this.game, this.currentMapConfig, turnManager);

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
			int animationSequenceIndex;
			final String campaignScreenModel;
			if (campaignBackground == -1) {
				animationSequenceIndex = 0;
				campaignScreenModel = this.rootFrame.trySkinField("LoadingMeleeBackground");
			}
			else {
				final Element loadingScreens = worldEditData.get("LoadingScreens");
				final String key = String.format("%2s", Integer.toString(campaignBackground)).replace(' ', '0');
				animationSequenceIndex = loadingScreens.getFieldValue(key, 2);
				campaignScreenModel = loadingScreens.getField(key, 3);
			}

			spawnLoadingScreen(campaignScreenModel, animationSequenceIndex,
					getStringWithWTS(wts, mapInfo.getLoadingScreenTitle()),
					getStringWithWTS(wts, mapInfo.getLoadingScreenSubtitle()),
					getStringWithWTS(wts, mapInfo.getLoadingScreenText()));
			this.loadingMap = new LoadingMap(viewer, map, mapInfo);

		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void spawnLoadingScreen(String campaignScreenModel, int animationSequenceIndex, String title,
			String subtitle, String fullText) {
		// TODO Auto-generated method stub

	}

	private static String getStringWithWTS(final WTS wts, String string) {
		if (string.startsWith("TRIGSTR_")) {
			string = wts.get(Integer.parseInt(string.substring(8)));
		}
		return string;
	}

	@Override
	public void startMap(String mapFilename) {
		hideMainMenu();

		try {
			loadAndCacheMapConfigs(mapFilename);
			this.beginGameInformation = new BeginGameInformation();
			int localPlayerIndex = -1;
			for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
				final CBasePlayer player = this.currentMapConfig.getPlayer(i);
				if (player.getController() == CMapControl.USER) {
					player.setSlotState(CPlayerSlotState.PLAYING);
//						player.setName(this.profileManager.getCurrentProfile());
//						break;
					if (localPlayerIndex == -1) {
						localPlayerIndex = i;
					}
				}
			}
			this.beginGameInformation.localPlayerIndex = localPlayerIndex;
			this.beginGameInformation.loadingStarted = true;
		}
		catch (final IOException e) {
			e.printStackTrace();
		}

		this.beginGameInformation = new BeginGameInformation();
		this.beginGameInformation.gameMapLookup = new CurrentNetGameMapLookupPath(mapFilename);
		this.beginGameInformation.localPlayerIndex = -1;

	}

	private void hideMainMenu() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReturnFromGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize() {
		// TODO Auto-generated method stub

	}

	private TextButton createTextButton(Group group, String text, float x, float y, float width, float height) {
		final TextButton button = new TextButton(this.rootFrame.getTemplates().getDecoratedString(text),
				this.textButtonStyle);
		button.align(Align.center);
		group.addActor(button);
		button.setBounds(GameUI.convertX(this.uiViewport, x), GameUI.convertY(this.uiViewport, y),
				GameUI.convertX(this.uiViewport, width), GameUI.convertY(this.uiViewport, height));
		return button;
	}

	private void createStyles() {
		final NinePatch buttonBackdropNinePatch = new NinePatch(
				new Texture(
						new DataSourceFileHandle(this.dataSource, this.rootFrame.getSkinField("MenuButtonNinePatch"))),
				8, 8, 8, 8);
		final NinePatch buttonBackdropNinePatchHover = new NinePatch(new Texture(
				new DataSourceFileHandle(this.dataSource, this.rootFrame.getSkinField("MenuButtonNinePatchHover"))), 8,
				8, 8, 8);
		final NinePatch buttonBackdropNinePatchDown = new NinePatch(new Texture(
				new DataSourceFileHandle(this.dataSource, this.rootFrame.getSkinField("MenuButtonNinePatchDown"))), 8,
				8, 8, 8);
		this.textButtonStyle = new TextButton.TextButtonStyle();
		this.textButtonStyle.up = new NinePatchDrawable(buttonBackdropNinePatch);
		this.textButtonStyle.down = new NinePatchDrawable(buttonBackdropNinePatchDown);
		this.textButtonStyle.over = new NinePatchDrawable(buttonBackdropNinePatchHover);
		this.textButtonStyle.font = this.rootFrame.generateFont(0.035f);
		this.textButtonStyle.fontColor = Color.WHITE;
		this.textButtonStyle.overFontColor = Color.YELLOW;
		this.textButtonStyle.downFontColor = Color.GRAY;
	}

	private void showLoadingBars(boolean show) {

	}

	private static final class LoadingMap {

		private final War3MapViewer viewer;
		private final War3Map map;
		private final War3MapW3i mapInfo;

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
}
