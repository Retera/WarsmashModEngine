package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup.MapListContainer;

import net.warsmash.uberserver.LobbyGameSpeed;

public class BattleNetUI {
	private final GameUI rootFrame;
	private final Viewport uiViewport;

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

	public BattleNetUI(final GameUI rootFrame, final Viewport uiViewport, final DataSource dataSource,
			final BattleNetUIActionListener actionListener) {
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
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
				actionListener.showCreateGameMenu();
			}
		});

		this.customJoinPanelLoadGameButton = (GlueButtonFrame) rootFrame.getFrameByName("LoadGameButton", 0);
		this.customJoinPanelLoadGameButton.setEnabled(false);

		final SimpleFrame joinGameListContainer = (SimpleFrame) this.rootFrame.getFrameByName("JoinGameListContainer",
				0);
		final ListBoxFrame joinGameListBox = (ListBoxFrame) this.rootFrame.createFrameByType("LISTBOX", "MapListBox",
				joinGameListContainer, "WITHCHILDREN", 0);
		joinGameListBox.setSetAllPoints(true);
		final StringFrame joinGameListLabel = (StringFrame) this.rootFrame.getFrameByName("JoinGameListLabel", 0);
		joinGameListBox.setFrameFont(joinGameListLabel.getFrameFont());

		this.joinGameEditBox = (EditBoxFrame) this.rootFrame.getFrameByName("JoinGameNameEditBox", 0);

		final List<String> testItems = new ArrayList<>();
		testItems.add("Retera???'s game (1/4)");
		for (final String displayItemPath : testItems) {
			joinGameListBox.addItem(displayItemPath, this.rootFrame, this.uiViewport);
		}
		joinGameListBox.setSelectionListener(new ListBoxSelelectionListener() {
			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				if (newSelectedItem != null) {
					BattleNetUI.this.joinGameEditBox.setText(newSelectedItem, rootFrame, uiViewport);
				}
			}
		});
		joinGameListContainer.add(joinGameListBox);
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

		this.customCreatePanelBackButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.customCreatePanelBackButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.openCustomGameMenu();
			}
		});

		final StringFrame mapListLabel = (StringFrame) this.rootFrame.getFrameByName("MapListLabel", 0);
		final MapListContainer mapListContainer = new MapListContainer(this.rootFrame, this.uiViewport,
				"MapListContainer", dataSource, mapListLabel.getFrameFont());
		mapListContainer.addSelectionListener(new ListBoxSelelectionListener() {
			@Override
			public void onSelectionChanged(final int newSelectedIndex, final String newSelectedItem) {
				if (newSelectedItem != null) {
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
		this.privateGameRadio.setEnabled(false);

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

	public void channelMessage(final String userName, final String message) {
		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString("CHATEVENT_ID_TALK"),
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_TALK_USER"), userName,
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_TALK_MESSAGE"), message);
		this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
	}

	public void channelEmote(final String userName, final String message) {
		final String messageText = String.format(this.rootFrame.getTemplates().getDecoratedString("CHATEVENT_ID_EMOTE"),
				this.rootFrame.getTemplates().getDecoratedString("CHATCOLOR_EMOTE_MESSAGE"), userName, message);
		this.chatTextArea.addItem(messageText, this.rootFrame, this.uiViewport);
	}

	public void showChannelMenu() {
		this.channelPanel.setVisible(true);
	}

	public String getCurrentChannel() {
		return this.currentChannel;
	}
}
