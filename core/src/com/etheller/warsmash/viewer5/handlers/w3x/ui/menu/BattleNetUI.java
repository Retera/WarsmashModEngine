package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32C;

import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.CheckBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.EditBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame.ListBoxSelelectionListener;
import com.etheller.warsmash.parsers.fdf.frames.ScrollBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.ScrollBarFrame.ScrollBarChangeListener;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextAreaFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.CBasePlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapInfoPane;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapListContainer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.PlayerSlotPaneListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.TeamSetupPane;

import net.warsmash.uberserver.ChannelServerMessageType;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.LobbyPlayerType;

public class BattleNetUI {
	private final GameUI rootFrame;
	private final Viewport uiViewport;
	private final Scene uiScene;
	private final DataSource dataSource;

	private final UIFrame battleNetMainFrame;
	private final UIFrame battleNetChangePasswordPanel;
	private final UIFrame battleNetChangeEmailPanel;
	private final UIFrame battleNetPasswordRecoveryPanel;
	private final UIFrame battleNetEmailBindPanel;
	private final UIFrame battleNetTOSPanel;
	private final UIFrame battleNetNewAccountPanel;
	private final UIFrame battleNetCancelBackdrop;
	private final GlueButtonFrame cancelButton;
	private final UIFrame battleNetOKBackdrop;
	private final UIFrame battleNetLoginPanel;
	private final SpriteFrame battleNetDoors;

	private final EditBoxFrame accountNameEditBox;
	private final EditBoxFrame passwordEditBox;
	private final GlueButtonFrame passwordRecoveryButton;

	private final BattleNetUIActionListener actionListener;
	private final StringFrame selectedRealmValue;
	private final GlueButtonFrame changeEmailButton;
	private final GlueButtonFrame changePasswordButton;
	private final GlueButtonFrame newAccountButton;
	private final GlueButtonFrame tosButton;
	private final Runnable exitLoginRunnable;
	private final GlueButtonFrame logonButton;
	private final UIFrame battleNetChatPanel;
	private final UIFrame chatPanel;
	private final UIFrame channelPanel;
	private final GlueButtonFrame quitBattleNetButton;
	private final BackdropFrame adFrame;
	private final BackdropFrame logoFrame;
	private final List<GlueButtonFrame> battleNetChatTopButtons = new ArrayList<>();
	private final GlueButtonFrame standardGameButton;
	private final GlueButtonFrame quickStandardGameButton;
	private final GlueButtonFrame standardTeamGameButton;
	private final GlueButtonFrame customGameButton;
	private final GlueButtonFrame tournamentButton;
	private final GlueButtonFrame ladderButton;
	private final GlueButtonFrame profileButton;
	// welcome panel:
	private final UIFrame welcomePanel;
	private final StringFrame welcomeNewItemCount;
	private final SimpleFrame welcomeNewsBoxContainer;
	private final StringFrame welcomeMOTDText;
	private final UIFrame welcomeUpcomingTournamentPanel;
	private final GlueButtonFrame welcomeEnterChatButton;
	private final SimpleFrame welcomeQuitBattleNetButtonContainer;
	private final SimpleFrame chatQuitBattleNetButtonContainer;
	private long gamingNetworkSessionToken;
	private String gamingNetworkMessageOfTheDay;
	private final EditBoxFrame naAccountName;
	private final EditBoxFrame naPassword;
	private final EditBoxFrame naRepeatPassword;
	private final GlueButtonFrame okButton;
	private final StringFrame chatChannelNameLabel;
	private final TextAreaFrame chatTextArea;
	private final GlueTextButtonFrame chatChannelButton;
	private final GlueButtonFrame channelPanelBackButton;
	private final EditBoxFrame channelNameField;
	private final GlueButtonFrame channelPanelJoinChannelButton;
	private final UIFrame battleNetCustomJoinPanel;
	private final GlueButtonFrame customJoinPanelBackButton;
	private String currentChannel;
	private final EditBoxFrame joinGameEditBox;
	private final GlueButtonFrame joinGameButton;
	private final GlueButtonFrame customJoinPanelCreateGameButton;
	private final GlueButtonFrame customJoinPanelLoadGameButton;
	private final UIFrame battleNetCustomCreatePanel;
	private final GlueButtonFrame customCreatePanelBackButton;
	private final ScrollBarFrame createGameSpeedSlider;
	private final StringFrame createGameSpeedValue;
	private final CheckBoxFrame publicGameRadio;
	private final CheckBoxFrame privateGameRadio;
	private final GlueButtonFrame customCreatePanelCreateButton;
	private String customCreatePanelCurrentSelectedMapPath;
	private final MapInfoPane customCreateMapInfoPane;
	private War3MapConfig customCreateCurrentMapConfig;
	private War3MapW3i customCreateCurrentMapInfo;
	private War3Map customCreateCurrentMap;
	private UIFrame battleNetGameChatroom;
	private IntIntMap gameChatroomServerSlotToMapSlot;
	private IntIntMap gameChatroomMapSlotToServerSlot;
	private final MapInfoPane gameChatroomMapInfoPane;
	private StringFrame gameChatroomGameNameValue;
	private GlueButtonFrame gameChatroomStartGameButton;
	private GlueButtonFrame gameChatroomCancelButton;
	private TextAreaFrame gameChatroomChatTextArea;
	private EditBoxFrame gameChatroomChatEditBox;
	private ListBoxFrame joinGameListBox;
	private TeamSetupPane gameChatroomTeamSetupPane;
	private War3MapConfig gameChatroomMapConfig;
	private War3Map gameChatroomMap;
	private War3MapW3i gameChatroomMapInfo;

	public BattleNetUI(final GameUI rootFrame, final Viewport uiViewport, final Scene uiScene,
			final DataSource dataSource, final BattleNetUIActionListener actionListener) {
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
		this.uiScene = uiScene;
		this.dataSource = dataSource;
		this.actionListener = actionListener;
		// Create BattleNet frames
		this.battleNetMainFrame = rootFrame.createFrame("BattleNetMainFrame", rootFrame, 0, 0);
		this.battleNetMainFrame.setVisible(false);
		this.battleNetDoors = (SpriteFrame) rootFrame.getFrameByName("BattleNetMainBackground", 0);
		this.battleNetDoors.setVisible(false);
		this.battleNetChangePasswordPanel = rootFrame.getFrameByName("ChangePasswordPanel", 0);
		this.battleNetChangePasswordPanel.setVisible(false);
		this.battleNetChangeEmailPanel = rootFrame.getFrameByName("ChangeEmailPanel", 0);
		this.battleNetChangeEmailPanel.setVisible(false);
		this.battleNetPasswordRecoveryPanel = rootFrame.getFrameByName("PasswordRecoveryPanel", 0);
		this.battleNetPasswordRecoveryPanel.setVisible(false);
		this.battleNetEmailBindPanel = rootFrame.getFrameByName("EmailBindPanel", 0);
		this.battleNetEmailBindPanel.setVisible(false);

		// *******************************************
		// *
		// * Terms Of Service Panel
		// *
		// ******

		this.battleNetTOSPanel = rootFrame.getFrameByName("TOSPanel", 0);
		this.battleNetTOSPanel.setVisible(false);

		final UIFrame tosTextArea = rootFrame.getFrameByName("TOSTextArea", 0);

		this.battleNetNewAccountPanel = rootFrame.getFrameByName("NewAccountPanel", 0);
		this.battleNetNewAccountPanel.setVisible(false);

		this.naAccountName = (EditBoxFrame) rootFrame.getFrameByName("NAAccountName", 0);
		this.naPassword = (EditBoxFrame) rootFrame.getFrameByName("NAPassword", 0);
		this.naRepeatPassword = (EditBoxFrame) rootFrame.getFrameByName("NARepeatPassword", 0);

		this.battleNetCancelBackdrop = rootFrame.getFrameByName("CancelBackdrop", 0);
		this.battleNetCancelBackdrop.setVisible(false);
		this.cancelButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.battleNetOKBackdrop = rootFrame.getFrameByName("OKBackdrop", 0);
		this.battleNetOKBackdrop.setVisible(false);
		this.okButton = (GlueButtonFrame) rootFrame.getFrameByName("OKButton", 0);

		// *******************************************
		// *
		// * Main Login Panel
		// *
		// ******

		this.battleNetLoginPanel = rootFrame.getFrameByName("LoginPanel", 0);
		this.battleNetLoginPanel.setVisible(false);

		this.accountNameEditBox = (EditBoxFrame) rootFrame.getFrameByName("AccountName", 0);
		final Runnable logonRunnable = new Runnable() {
			@Override
			public void run() {
				actionListener.logon(BattleNetUI.this.accountNameEditBox.getText(),
						BattleNetUI.this.passwordEditBox.getText());
			}
		};
		this.accountNameEditBox.setOnEnter(logonRunnable);
		this.passwordEditBox = (EditBoxFrame) rootFrame.getFrameByName("Password", 0);
		this.passwordEditBox.setOnEnter(logonRunnable);
		this.passwordRecoveryButton = (GlueButtonFrame) rootFrame.getFrameByName("PasswordRecoveryButton", 0);
		this.passwordRecoveryButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.recoverPassword(BattleNetUI.this.accountNameEditBox.getText());
			}
		});
		this.selectedRealmValue = (StringFrame) rootFrame.getFrameByName("SelectedRealmValue", 0);
		this.changeEmailButton = (GlueButtonFrame) rootFrame.getFrameByName("ChangeEmailButton", 0);
		this.changeEmailButton.setEnabled(false);
		this.changePasswordButton = (GlueButtonFrame) rootFrame.getFrameByName("ChangePasswordButton", 0);
		this.newAccountButton = (GlueButtonFrame) rootFrame.getFrameByName("NewAccountButton", 0);
		this.newAccountButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				BattleNetUI.this.battleNetLoginPanel.setVisible(false);
				BattleNetUI.this.battleNetNewAccountPanel.setVisible(true);
				BattleNetUI.this.battleNetOKBackdrop.setVisible(true);
				BattleNetUI.this.okButton.setOnClick(new Runnable() {
					@Override
					public void run() {
						actionListener.createAccount(BattleNetUI.this.naAccountName.getText(),
								BattleNetUI.this.naPassword.getText(), BattleNetUI.this.naRepeatPassword.getText());
					}
				});
				BattleNetUI.this.cancelButton.setOnClick(new Runnable() {
					@Override
					public void run() {
						leaveNewAccountPanel();
					}
				});
			}
		});
		this.tosButton = (GlueButtonFrame) rootFrame.getFrameByName("TOSButton", 0);
		this.tosButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				boolean success = false;
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					try {
						Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
						success = true;
					}
					catch (final Exception e) {
						e.printStackTrace();
					}
				}
				if (!success) {
					BattleNetUI.this.battleNetLoginPanel.setVisible(false);
					BattleNetUI.this.battleNetCancelBackdrop.setVisible(false);
					BattleNetUI.this.battleNetTOSPanel.setVisible(true);
				}
			}
		});

		this.exitLoginRunnable = new Runnable() {
			@Override
			public void run() {
				BattleNetUI.this.actionListener.cancelLoginPrompt();
			}
		};

		this.logonButton = (GlueButtonFrame) rootFrame.getFrameByName("LogonButton", 0);
		this.logonButton.setOnClick(logonRunnable);

		this.battleNetChatPanel = rootFrame.createFrame("BattleNetChatPanel", rootFrame, 0, 0);
		this.battleNetChatPanel.setVisible(false);

		// ********************************
		// * The chat panel
		// ********************************
		this.adFrame = (BackdropFrame) rootFrame.getFrameByName("AdFrame", 0);
		this.adFrame.setVisible(false);
		this.logoFrame = (BackdropFrame) rootFrame.getFrameByName("LogoFrame", 0);
		this.logoFrame.setVisible(false);
		this.standardGameButton = (GlueButtonFrame) rootFrame.getFrameByName("StandardGameButton", 0);
		this.standardGameButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.standardGameButton);
		this.quickStandardGameButton = (GlueButtonFrame) rootFrame.getFrameByName("QuickStandardGameButton", 0);
		this.quickStandardGameButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.quickStandardGameButton);
		this.standardTeamGameButton = (GlueButtonFrame) rootFrame.getFrameByName("StandardTeamGameButton", 0);
		this.standardTeamGameButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.standardTeamGameButton);
		this.customGameButton = (GlueButtonFrame) rootFrame.getFrameByName("CustomGameButton", 0);
		this.customGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.openCustomGameMenu();
			}
		});
		this.battleNetChatTopButtons.add(this.customGameButton);
		this.tournamentButton = (GlueButtonFrame) rootFrame.getFrameByName("TournamentButton", 0);
		this.tournamentButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.tournamentButton);
		this.ladderButton = (GlueButtonFrame) rootFrame.getFrameByName("LadderButton", 0);
		this.ladderButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.ladderButton);
		this.profileButton = (GlueButtonFrame) rootFrame.getFrameByName("ProfileButton", 0);
		this.profileButton.setEnabled(false);
		this.battleNetChatTopButtons.add(this.profileButton);
		this.chatPanel = rootFrame.getFrameByName("ChatPanel", 0);
		this.chatPanel.setVisible(false);
		this.chatChannelNameLabel = (StringFrame) rootFrame.getFrameByName("ChatChannelNameLabel", 0);
		this.chatChannelButton = (GlueTextButtonFrame) rootFrame.getFrameByName("ChatChannelButton", 0);
		this.chatChannelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.showChannelChooserPanel();
			}
		});
		this.chatTextArea = (TextAreaFrame) rootFrame.getFrameByName("ChatTextArea", 0);
		final EditBoxFrame chatEditBox = (EditBoxFrame) rootFrame.getFrameByName("BattleNetChatEditBox", 0);
		chatEditBox.setFilterAllowAny();
		chatEditBox.setOnEnter(new Runnable() {
			@Override
			public void run() {
				actionListener.submitChatText(chatEditBox.getText());
				chatEditBox.setText("", rootFrame, uiViewport);
			}
		});
		this.chatQuitBattleNetButtonContainer = (SimpleFrame) rootFrame
				.getFrameByName("ChatQuitBattleNetButtonContainer", 0);

		// ********************************
		// * The channel panel
		// ********************************
		this.channelPanel = rootFrame.getFrameByName("ChannelPanel", 0);
		this.channelPanel.setVisible(false);

		this.channelPanelBackButton = (GlueButtonFrame) rootFrame.getFrameByName("BackButton", 0);
		this.channelPanelBackButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.returnToChat();
			}
		});
		this.channelNameField = (EditBoxFrame) rootFrame.getFrameByName("ChannelNameField", 0);
		this.channelPanelJoinChannelButton = (GlueButtonFrame) rootFrame.getFrameByName("JoinChannelButton", 0);
		final Runnable onJoinChannelClick = new Runnable() {
			@Override
			public void run() {
				actionListener.requestJoinChannel(BattleNetUI.this.channelNameField.getText());
			}
		};
		this.channelNameField.setOnEnter(onJoinChannelClick);
		this.channelPanelJoinChannelButton.setOnClick(onJoinChannelClick);

		// ********************************
		// * The welcome panel
		// ********************************
		this.welcomePanel = rootFrame.getFrameByName("WelcomePanel", 0);
		this.welcomePanel.setVisible(false);
		this.welcomeNewItemCount = (StringFrame) rootFrame.getFrameByName("WelcomeNewItemCount", 0);
		rootFrame.setText(this.welcomeNewItemCount, "(0)");
		this.welcomeNewsBoxContainer = (SimpleFrame) rootFrame.getFrameByName("NewsBoxContainer", 0);
		this.welcomeMOTDText = (StringFrame) rootFrame.getFrameByName("WelcomeMOTDText", 0);
		this.welcomeUpcomingTournamentPanel = rootFrame.getFrameByName("UpcomingTournamentPanel", 0);
		this.welcomeUpcomingTournamentPanel.setVisible(false);
		this.welcomeEnterChatButton = (GlueButtonFrame) rootFrame.getFrameByName("EnterChatButton", 0);
		this.welcomeEnterChatButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.enterDefaultChat();
			}
		});
		this.welcomeQuitBattleNetButtonContainer = (SimpleFrame) rootFrame
				.getFrameByName("WelcomeQuitBattleNetButtonContainer", 0);

		// ********************************
		// * The quit button
		// ********************************
		this.quitBattleNetButton = (GlueButtonFrame) rootFrame.getFrameByName("QuitBattleNetButton", 0);
		this.quitBattleNetButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.quitBattleNet();
			}
		});

		// *******************************************
		// *
		// * New Account Panel NCS (Patch 1.31ish)
		// *
		// ******
		final UIFrame newAccountPanelNCS = rootFrame.getFrameByName("NewAccountPanelNCS", 0);
		if (newAccountPanelNCS != null) {
			newAccountPanelNCS.setVisible(false);
		}

		// *******************************************
		// *
		// * Battle Net Custom Join Panel
		// *
		// ******

		this.battleNetCustomJoinPanel = rootFrame.createFrame("BattleNetCustomJoinPanel", rootFrame, 0, 0);
		this.battleNetCustomJoinPanel.setVisible(false);

		this.customJoinPanelBackButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.customJoinPanelBackButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.returnToChat();
			}
		});

		this.customJoinPanelCreateGameButton = (GlueButtonFrame) rootFrame.getFrameByName("CreateGameButton", 0);
		this.customJoinPanelCreateGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {

				// clear these out when leaving a screen
				BattleNetUI.this.customCreateCurrentMapConfig = null;
				BattleNetUI.this.customCreateCurrentMapInfo = null;
				BattleNetUI.this.customCreateCurrentMap = null;
				BattleNetUI.this.customCreatePanelCurrentSelectedMapPath = null;
				actionListener.showCreateGameMenu();
			}
		});

		this.customJoinPanelLoadGameButton = (GlueButtonFrame) rootFrame.getFrameByName("LoadGameButton", 0);
		this.customJoinPanelLoadGameButton.setEnabled(false);

		final SimpleFrame joinGameListContainer = (SimpleFrame) this.rootFrame.getFrameByName("JoinGameListContainer",
				0);
		this.joinGameListBox = (ListBoxFrame) this.rootFrame.createFrameByType("LISTBOX", "MapListBox",
				joinGameListContainer, "WITHCHILDREN", 0);
		this.joinGameListBox.setSetAllPoints(true);
		final StringFrame joinGameListLabel = (StringFrame) this.rootFrame.getFrameByName("JoinGameListLabel", 0);
		this.joinGameListBox.setFrameFont(joinGameListLabel.getFrameFont());

		this.joinGameEditBox = (EditBoxFrame) this.rootFrame.getFrameByName("JoinGameNameEditBox", 0);

		this.joinGameListBox.setSelectionListener(new ListBoxSelelectionListener() {
			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				if (newSelectedItem != null) {
					BattleNetUI.this.joinGameEditBox.setText(newSelectedItem, rootFrame, uiViewport);
				}
			}
		});
		joinGameListContainer.add(this.joinGameListBox);
		this.joinGameButton = (GlueButtonFrame) this.rootFrame.getFrameByName("JoinGameButton", 0);
		this.joinGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				final String text = BattleNetUI.this.joinGameEditBox.getText();
				if (text.isEmpty()) {
					actionListener.showError("NETERROR_NOGAMESPECIFIED");
				}
				else {
					actionListener.requestJoinGame(text);
				}

			}
		});

		// *******************************************
		// *
		// * Battle Net Custom Create Panel
		// *
		// ******
		this.battleNetCustomCreatePanel = rootFrame.createFrame("BattleNetCustomCreatePanel", rootFrame, 0, 0);
		this.battleNetCustomCreatePanel.setVisible(false);

		final EditBoxFrame customCreatePanelNameEditBox = (EditBoxFrame) rootFrame
				.getFrameByName("CreateGameNameEditBox", 0);

		this.customCreatePanelBackButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.customCreatePanelBackButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.openCustomGameMenu();
			}
		});

		this.customCreatePanelCreateButton = (GlueButtonFrame) rootFrame.getFrameByName("CreateGameButton", 0);

		final StringFrame mapListLabel = (StringFrame) this.rootFrame.getFrameByName("MapListLabel", 0);
		final MapListContainer mapListContainer = new MapListContainer(this.rootFrame, this.uiViewport,
				"MapListContainer", dataSource, mapListLabel.getFrameFont());
		mapListContainer.addSelectionListener(new ListBoxSelelectionListener() {
			String prevSelectedItem = "";

			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				if (newSelectedItem != null) {
					if (newSelectedItem.compareTo(this.prevSelectedItem) == 0) {
						return;
					}
					this.prevSelectedItem = newSelectedItem;

					BattleNetUI.this.customCreateCurrentMapConfig = null;
					BattleNetUI.this.customCreateCurrentMapInfo = null;

					BattleNetUI.this.customCreatePanelCurrentSelectedMapPath = newSelectedItem;

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
						war3MapConfig.setMapName(rootFrame.getTrigStr(mapInfo.getName()));
						war3MapConfig.setMapDescription(rootFrame.getTrigStr(mapInfo.getDescription()));
						BattleNetUI.this.customCreateMapInfoPane.setMap(rootFrame, uiViewport, map, mapInfo,
								war3MapConfig);
						BattleNetUI.this.customCreateCurrentMapConfig = war3MapConfig;
						BattleNetUI.this.customCreateCurrentMapInfo = mapInfo;
						BattleNetUI.this.customCreateCurrentMap = map;
					}
					catch (final Exception exc) {
						exc.printStackTrace();
						actionListener.showError("NETERROR_MAPLOADERROR");
					}
				}
				else {
					BattleNetUI.this.customCreatePanelCurrentSelectedMapPath = null;
				}
			}
		});

		this.createGameSpeedValue = (StringFrame) this.rootFrame.getFrameByName("CreateGameSpeedValue", 0);

		this.createGameSpeedSlider = (ScrollBarFrame) this.rootFrame.getFrameByName("CreateGameSpeedSlider", 0);
		this.createGameSpeedSlider.setChangeListener(new ScrollBarChangeListener() {
			@Override
			public void onChange(final GameUI gameUI, final Viewport uiViewport, final int newValue) {
				if ((newValue >= 0) && (newValue < LobbyGameSpeed.VALUES.length)) {
					gameUI.setDecoratedText(BattleNetUI.this.createGameSpeedValue,
							LobbyGameSpeed.VALUES[newValue].name());
				}
			}
		});
		this.createGameSpeedSlider.setValue(rootFrame, uiViewport, LobbyGameSpeed.NORMAL.ordinal());

		this.publicGameRadio = (CheckBoxFrame) this.rootFrame.getFrameByName("PublicGameRadio", 0);
		this.privateGameRadio = (CheckBoxFrame) this.rootFrame.getFrameByName("PrivateGameRadio", 0);

		this.publicGameRadio.setOnClick(new Runnable() {
			@Override
			public void run() {
				if (BattleNetUI.this.privateGameRadio.isEnabled()) {
					BattleNetUI.this.privateGameRadio.setChecked(!BattleNetUI.this.publicGameRadio.isChecked());
				}
				else {
					BattleNetUI.this.publicGameRadio.setChecked(true);
				}
			}
		});
		this.privateGameRadio.setOnClick(new Runnable() {
			@Override
			public void run() {
				BattleNetUI.this.publicGameRadio.setChecked(!BattleNetUI.this.privateGameRadio.isChecked());
			}
		});
		this.publicGameRadio.setChecked(true);
//		this.privateGameRadio.setEnabled(false);

		{
			final SimpleFrame mapInfoPaneContainer = (SimpleFrame) this.rootFrame.getFrameByName("MapInfoPaneContainer",
					0);
			this.customCreateMapInfoPane = new MapInfoPane(this.rootFrame, this.uiViewport, mapInfoPaneContainer);
		}

		this.customCreatePanelCreateButton.setOnClick(new Runnable() {
			private final CRC32C mapChecksumCalculator = new CRC32C();

			@Override
			public void run() {
				if (BattleNetUI.this.customCreateCurrentMapInfo == null) {
					actionListener.showError("NETERROR_NOMAPSELECTED");
					return;
				}
				final String gameName = customCreatePanelNameEditBox.getText();
				if ((gameName == null) || gameName.isEmpty()) {
					actionListener.showError("NETERROR_NOGAMENAMESPECIFIED");
					return;
				}
				boolean nonSpace = false;
				for (int i = 0; i < gameName.length(); i++) {
					if (gameName.charAt(i) != ' ') {
						nonSpace = true;
					}
				}
				if (!nonSpace) {
					actionListener.showError("NETERROR_EMPTYGAMENAMESPECIFIED");
					return;
				}
				final int speedSliderValue = BattleNetUI.this.createGameSpeedSlider.getValue();

				LobbyGameSpeed gameSpeed;
				if ((speedSliderValue >= 0) && (speedSliderValue < LobbyGameSpeed.VALUES.length)) {
					gameSpeed = LobbyGameSpeed.VALUES[speedSliderValue];
				}
				else {
					actionListener.showError("NETERROR_DEFAULTERROR");
					return;
				}
				final HostedGameVisibility hostedGameVisibility = BattleNetUI.this.publicGameRadio.isChecked()
						? HostedGameVisibility.PUBLIC
						: HostedGameVisibility.PRIVATE;
				Jass2.loadConfig(BattleNetUI.this.customCreateCurrentMap, uiViewport, uiScene, rootFrame,
						BattleNetUI.this.customCreateCurrentMapConfig, WarsmashConstants.JASS_FILE_LIST).config();
				int mapPlayerSlots = 0;
				for (int i = 0; (i < (WarsmashConstants.MAX_PLAYERS - 4))
						&& (mapPlayerSlots < BattleNetUI.this.customCreateCurrentMapConfig.getPlayerCount()); i++) {
					if (BattleNetUI.this.customCreateCurrentMapConfig.getPlayer(i)
							.getController() == CMapControl.USER) {
						mapPlayerSlots++;
					}
				}

				final long mapChecksum = BattleNetUI.this.customCreateCurrentMap
						.computeChecksum(this.mapChecksumCalculator);

				String mapName = BattleNetUI.this.customCreatePanelCurrentSelectedMapPath;
				mapName = mapName.substring(Math.max(mapName.lastIndexOf('/'), mapName.lastIndexOf('\\')) + 1);

				actionListener.createGame(gameName, mapName, mapPlayerSlots, gameSpeed, hostedGameVisibility,
						mapChecksum, BattleNetUI.this.customCreateCurrentMap);
			}
		});

		// *******************************************
		// *
		// * Battle Net Custom Create Panel
		// *
		// ******
		this.battleNetGameChatroom = rootFrame.createFrame("GameChatroom", rootFrame, 0, 0);
		this.battleNetGameChatroom.setVisible(false);

		{
			final SimpleFrame teamSetupContainer = (SimpleFrame) this.rootFrame.getFrameByName("TeamSetupContainer", 0);
			this.gameChatroomTeamSetupPane = new TeamSetupPane(this.rootFrame, this.uiViewport, teamSetupContainer);
		}

		{
			final SimpleFrame mapInfoPaneContainer = (SimpleFrame) this.rootFrame.getFrameByName("MapInfoPaneContainer",
					0);
			this.gameChatroomMapInfoPane = new MapInfoPane(this.rootFrame, this.uiViewport, mapInfoPaneContainer);
		}

		this.gameChatroomGameNameValue = (StringFrame) this.rootFrame.getFrameByName("GameNameValue", 0);

		this.gameChatroomStartGameButton = (GlueButtonFrame) rootFrame.getFrameByName("StartGameButton", 0);

		this.gameChatroomStartGameButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.startGame();
			}
		});

		this.gameChatroomCancelButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.gameChatroomCancelButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.leaveCustomGame();
			}
		});

		this.gameChatroomChatTextArea = (TextAreaFrame) rootFrame.getFrameByName("ChatTextArea", 0);
		this.gameChatroomChatEditBox = (EditBoxFrame) rootFrame.getFrameByName("ChatEditBox", 0);
		this.gameChatroomChatEditBox.setFilterAllowAny();
		this.gameChatroomChatEditBox.setOnEnter(new Runnable() {
			@Override
			public void run() {
				actionListener.submitChatText(BattleNetUI.this.gameChatroomChatEditBox.getText());
				BattleNetUI.this.gameChatroomChatEditBox.setText("", rootFrame, uiViewport);
			}
		});
	}

	public void setTopButtonsVisible(final boolean flag) {
		for (final GlueButtonFrame frame : this.battleNetChatTopButtons) {
			frame.setVisible(flag);
		}
	}

	public SpriteFrame getDoors() {
		return this.battleNetDoors;
	}

	public void setVisible(final boolean b) {
		this.battleNetMainFrame.setVisible(b);
	}

	public void showLoginPrompt(final String selectedRealm) {
		this.rootFrame.setText(this.selectedRealmValue, selectedRealm);
		this.battleNetLoginPanel.setVisible(true);
		this.battleNetCancelBackdrop.setVisible(true);
		this.cancelButton.setOnClick(this.exitLoginRunnable);
	}

	public void hide() {
		this.battleNetLoginPanel.setVisible(false);
		this.battleNetCancelBackdrop.setVisible(false);
		this.battleNetChatPanel.setVisible(false);
		hideCurrentScreen();
	}

	public void loginAccepted(final long sessionToken, final String welcomeMessage) {
		this.battleNetLoginPanel.setVisible(false);
		this.battleNetCancelBackdrop.setVisible(false);
		this.gamingNetworkSessionToken = sessionToken;
		this.rootFrame.setText(this.welcomeMOTDText, welcomeMessage);
	}

	public void showWelcomeScreen() {
		setTopButtonsVisible(true);
		this.battleNetChatPanel.setVisible(true);
		this.welcomePanel.setVisible(true);
		this.quitBattleNetButton.setVisible(true);
		this.quitBattleNetButton.setParent(this.welcomeQuitBattleNetButtonContainer);
		this.quitBattleNetButton.setWidth(0); // TODO set width/height 0 probably shouldnt be necessary
		this.quitBattleNetButton.setHeight(0);
		this.quitBattleNetButton.clearFramePointAssignments();
		this.quitBattleNetButton.setSetAllPoints(true);
		this.quitBattleNetButton.positionBounds(this.rootFrame, this.uiViewport);
	}

	public void showCustomGameMenu() {
		this.battleNetCustomJoinPanel.setVisible(true);
	}

	public void showCustomGameCreateMenu() {
		this.battleNetCustomCreatePanel.setVisible(true);
	}

	public void hideWelcomeScreen() {
		this.welcomePanel.setVisible(false);
		this.quitBattleNetButton.setVisible(false);
	}

	public void hideCurrentScreen() {
		this.welcomePanel.setVisible(false);
		this.chatPanel.setVisible(false);
		this.channelPanel.setVisible(false);
		this.welcomePanel.setVisible(false);
		this.quitBattleNetButton.setVisible(false);
		this.battleNetCustomJoinPanel.setVisible(false);
		this.battleNetCustomCreatePanel.setVisible(false);
		this.battleNetGameChatroom.setVisible(false);
		setTopButtonsVisible(false);
	}

	public void showChatChannel() {
		setTopButtonsVisible(true);
		this.chatPanel.setVisible(true);
		this.quitBattleNetButton.setVisible(true);
		this.quitBattleNetButton.setParent(this.chatQuitBattleNetButtonContainer);
		this.quitBattleNetButton.setWidth(0); // TODO set width/height 0 probably shouldnt be necessary
		this.quitBattleNetButton.setHeight(0);
		this.quitBattleNetButton.clearFramePointAssignments();
		this.quitBattleNetButton.setSetAllPoints(true);
		this.quitBattleNetButton.positionBounds(this.rootFrame, this.uiViewport);
	}

	public void accountCreatedOk() {
		leaveNewAccountPanel();
	}

	private void leaveNewAccountPanel() {
		BattleNetUI.this.battleNetLoginPanel.setVisible(true);
		BattleNetUI.this.battleNetNewAccountPanel.setVisible(false);
		BattleNetUI.this.battleNetOKBackdrop.setVisible(false);
		BattleNetUI.this.cancelButton.setOnClick(BattleNetUI.this.exitLoginRunnable);
	}

	public long getGamingNetworkSessionToken() {
		return this.gamingNetworkSessionToken;
	}

	public void joinedChannel(final String channelName) {
		this.currentChannel = channelName;
		this.rootFrame.setText(this.chatChannelNameLabel, channelName);
		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString("BNET_JOIN_CHANNEL"),
				channelName);
		this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
	}

	public void joinedGame(final String gameName, final boolean host) {
		this.currentChannel = null;
		this.gameChatroomChatTextArea.removeAllItems();
		this.rootFrame.setText(this.gameChatroomGameNameValue, gameName);
		this.gameChatroomStartGameButton.setEnabled(host);
	}

	public void channelMessage(final String userName, final String message) {
		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString("CHATEVENT_ID_TALK"),
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_TALK_USER"), userName,
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_TALK_MESSAGE"), message);
		if (this.currentChannel == null) {
			this.gameChatroomChatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
		else {
			this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
	}

	public void channelEmote(final String userName, final String message) {
		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString("CHATEVENT_ID_EMOTE"),
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_EMOTE_MESSAGE"), userName, message);
		if (this.currentChannel == null) {
			this.gameChatroomChatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
		else {
			this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
	}

	public void channelServerMessage(final String userName, final ChannelServerMessageType messageType) {
		String msgKey;
		switch (messageType) {
		case JOIN_GAME: {
			msgKey = "NETMESSAGE_PLAYERJOINED";
			break;
		}
		case LEAVE_GAME: {
			msgKey = "NETMESSAGE_PLAYERLEFT";
			break;
		}
		default: {
			msgKey = "NETERROR_DEFAULTERROR";
			break;
		}
		}

		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString(msgKey), userName);
		if (this.currentChannel == null) {
			this.gameChatroomChatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
		else {
			this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
		}
	}

	public void showChannelMenu() {
		this.channelPanel.setVisible(true);
	}

	public String getCurrentChannel() {
		return this.currentChannel;
	}

	public void showCustomGameLobby() {
		this.battleNetGameChatroom.setVisible(true);
	}

	public void beginGamesList() {
		this.joinGameListBox.removeAllItems();
	}

	public void gamesListItem(final String gameName, final int openSlots, final int totalSlots) {
		this.joinGameListBox.addItem(gameName, this.rootFrame, this.uiViewport);
	}

	public void endGamesList() {

	}

	public void setJoinGamePreviewMap(final File mapLookupFile) {
		War3Map map;
		try {
			map = War3MapViewer.beginLoadingMap(this.dataSource, mapLookupFile.getPath());

			final War3MapW3i mapInfo = map.readMapInformation();
			final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
			this.rootFrame.setMapStrings(wtsFile);
			final War3MapConfig war3MapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
			setGameChatroomMap(map, mapInfo, war3MapConfig);
		}
		catch (final IOException e) {
			e.printStackTrace();
			this.actionListener.showError("NETERROR_MAPFILEREAD");
		}

	}

	private void setGameChatroomMap(final War3Map map, final War3MapW3i mapInfo, final War3MapConfig war3MapConfig)
			throws IOException {
		this.gameChatroomMap = map;
		this.gameChatroomMapInfo = mapInfo;
		this.gameChatroomMapConfig = war3MapConfig;
		for (int i = 0; (i < WarsmashConstants.MAX_PLAYERS) && (i < mapInfo.getPlayers().size()); i++) {
			final CBasePlayer player = war3MapConfig.getPlayer(i);
			player.setName(this.rootFrame.getTrigStr(mapInfo.getPlayers().get(i).getName()));
		}
		Jass2.loadConfig(map, this.uiViewport, this.uiScene, this.rootFrame, war3MapConfig,
				WarsmashConstants.JASS_FILE_LIST).config();
		final IntIntMap serverSlotToMapSlot = new IntIntMap(WarsmashConstants.MAX_PLAYERS);
		final IntIntMap mapSlotToServerSlot = new IntIntMap(WarsmashConstants.MAX_PLAYERS);
		int serverSlot = 0;
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			final CBasePlayer player = war3MapConfig.getPlayer(i);
			if (player.getController() == CMapControl.COMPUTER) {
				if (mapInfo.hasFlag(War3MapW3iFlags.FIXED_PLAYER_SETTINGS_FOR_CUSTOM_FORCES)) {
					player.setSlotState(CPlayerSlotState.PLAYING);
				}
			}
			else if (player.getController() == CMapControl.USER) {
				serverSlotToMapSlot.put(serverSlot, i);
				mapSlotToServerSlot.put(i, serverSlot);
				serverSlot++;
			}
		}
		this.gameChatroomServerSlotToMapSlot = serverSlotToMapSlot;
		this.gameChatroomMapSlotToServerSlot = mapSlotToServerSlot;
		this.gameChatroomMapInfoPane.setMap(this.rootFrame, this.uiViewport, map, mapInfo, war3MapConfig);
		this.gameChatroomTeamSetupPane.setMap(this.rootFrame, this.uiViewport, war3MapConfig,
				mapInfo.getPlayers().size(), mapInfo, new PlayerSlotPaneListener() {

					@Override
					public void setPlayerSlot(final int index, final LobbyPlayerType lobbyPlayerType) {
						final int serverSlot = BattleNetUI.this.gameChatroomMapSlotToServerSlot.get(index, -1);
						BattleNetUI.this.actionListener.gameLobbySetPlayerSlot(serverSlot, lobbyPlayerType);
					}

					@Override
					public void setPlayerRace(final int index, final int raceItemIndex) {
						final int serverSlot = BattleNetUI.this.gameChatroomMapSlotToServerSlot.get(index, -1);
						BattleNetUI.this.actionListener.gameLobbySetPlayerRace(serverSlot, raceItemIndex);
					}
				});

	}

	public void gameLobbySlotSetPlayerType(final int slot, final LobbyPlayerType playerType) {
		if (this.gameChatroomServerSlotToMapSlot != null) {
			final int mapSlot = this.gameChatroomServerSlotToMapSlot.get(slot, -1);
			if (mapSlot != -1) {
				System.err.println("mapSlot got mapping " + mapSlot + " for " + slot);
				final CBasePlayer player = this.gameChatroomMapConfig.getPlayer(mapSlot);
				switch (playerType) {
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
				this.gameChatroomTeamSetupPane.notifyPlayerDataUpdated(mapSlot, this.rootFrame, this.uiViewport,
						this.gameChatroomMapConfig, this.gameChatroomMapInfo);
			}
			else {
				System.err.println(
						"gameLobbySlotSetPlayerType(" + slot + "," + playerType + ") failed, no such map slot");
			}
		}
		else {
			System.err.println(
					"gameLobbySlotSetPlayerType(" + slot + "," + playerType + ") failed, no ServerSlotToMapSlot");
		}
	}

	public void gameLobbySlotSetPlayer(final int slot, final String userName) {
		if (this.gameChatroomServerSlotToMapSlot != null) {
			final int mapSlot = this.gameChatroomServerSlotToMapSlot.get(slot, -1);
			if (mapSlot != -1) {
				System.err.println("mapSlot got mapping " + mapSlot + " for " + slot);
				final CBasePlayer player = this.gameChatroomMapConfig.getPlayer(mapSlot);
				player.setName(userName);
				this.gameChatroomTeamSetupPane.notifyPlayerDataUpdated(mapSlot, this.rootFrame, this.uiViewport,
						this.gameChatroomMapConfig, this.gameChatroomMapInfo);
			}
			else {
				System.err.println("gameLobbySlotSetPlayer(" + slot + "," + userName + ") failed, no such map slot");
			}
		}
		else {
			System.err.println("gameLobbySlotSetPlayer(" + slot + "," + userName + ") failed, no ServerSlotToMapSlot");
		}
	}

	public void gameLobbySlotSetPlayerRace(final int slot, final int raceItemIndex) {
		if (this.gameChatroomServerSlotToMapSlot != null) {
			final int mapSlot = this.gameChatroomServerSlotToMapSlot.get(slot, -1);
			if (mapSlot != -1) {
				System.err.println("mapSlot got mapping " + mapSlot + " for " + slot);
				final CBasePlayer player = this.gameChatroomMapConfig.getPlayer(mapSlot);
				if (raceItemIndex == 0) {
					player.setRacePref(WarsmashConstants.RACE_MANAGER.getRandomRacePreference());
				}
				else {
					final CRace race = WarsmashConstants.RACE_MANAGER.getRace(raceItemIndex);
					final CRacePreference racePreference = WarsmashConstants.RACE_MANAGER
							.getRacePreferenceForRace(race);
					player.setRacePref(racePreference);
				}
				this.gameChatroomTeamSetupPane.notifyPlayerDataUpdated(mapSlot, this.rootFrame, this.uiViewport,
						this.gameChatroomMapConfig, this.gameChatroomMapInfo);
			}
			else {
				System.err.println(
						"gameLobbySlotSetPlayerRace(" + slot + "," + raceItemIndex + ") failed, no such map slot");
			}
		}
		else {
			System.err.println(
					"gameLobbySlotSetPlayerRace(" + slot + "," + raceItemIndex + ") failed, no ServerSlotToMapSlot");
		}
	}

	public void setJoinGamePreviewMapToHostedMap() {
		try {
			setGameChatroomMap(this.customCreateCurrentMap, this.customCreateCurrentMapInfo,
					this.customCreateCurrentMapConfig);
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getLastHostedGamePath() {
		return this.customCreatePanelCurrentSelectedMapPath;
	}

	public void clearJoinGamePreviewMap(final String mapPreviewName) {
		this.gameChatroomMapInfoPane.clearMap(this.rootFrame, this.uiViewport, mapPreviewName);
		this.gameChatroomTeamSetupPane.clearMap(this.rootFrame, this.uiViewport);
		this.gameChatroomServerSlotToMapSlot = null;
		this.gameChatroomMapSlotToServerSlot = null;
	}

	public IntIntMap getGameChatroomMapSlotToServerSlot() {
		return this.gameChatroomMapSlotToServerSlot;
	}

	public IntIntMap getGameChatroomServerSlotToMapSlot() {
		return this.gameChatroomServerSlotToMapSlot;
	}

	public War3Map getGameChatroomMap() {
		return this.gameChatroomMap;
	}

	public War3MapConfig getGameChatroomMapConfig() {
		return this.gameChatroomMapConfig;
	}

	public War3MapW3i getGameChatroomMapInfo() {
		return this.gameChatroomMapInfo;
	}
}
