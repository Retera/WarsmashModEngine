package com.etheller.warsmash.viewer5.handlers.w3x.ui.menu;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.EditBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

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

	public BattleNetUI(final GameUI rootFrame, final Viewport uiViewport,
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
		this.battleNetCancelBackdrop = rootFrame.getFrameByName("CancelBackdrop", 0);
		this.battleNetCancelBackdrop.setVisible(false);
		this.cancelButton = (GlueButtonFrame) rootFrame.getFrameByName("CancelButton", 0);
		this.battleNetOKBackdrop = rootFrame.getFrameByName("OKBackdrop", 0);
		this.battleNetOKBackdrop.setVisible(false);

		// *******************************************
		// *
		// * Main Login Panel
		// *
		// ******

		this.battleNetLoginPanel = rootFrame.getFrameByName("LoginPanel", 0);
		this.battleNetLoginPanel.setVisible(false);

		this.accountNameEditBox = (EditBoxFrame) rootFrame.getFrameByName("AccountName", 0);
		this.passwordEditBox = (EditBoxFrame) rootFrame.getFrameByName("Password", 0);
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
		this.logonButton.setOnClick(new Runnable() {
			@Override
			public void run() {
				actionListener.logon(BattleNetUI.this.accountNameEditBox.getText(),
						BattleNetUI.this.passwordEditBox.getText());
			}
		});

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
		this.chatQuitBattleNetButtonContainer = (SimpleFrame) rootFrame
				.getFrameByName("ChatQuitBattleNetButtonContainer", 0);

		// ********************************
		// * The channel panel
		// ********************************
		this.channelPanel = rootFrame.getFrameByName("ChannelPanel", 0);
		this.channelPanel.setVisible(false);

		// ********************************
		// * The welcome panel
		// ********************************
		this.welcomePanel = rootFrame.getFrameByName("WelcomePanel", 0);
		this.welcomePanel.setVisible(false);
		this.welcomeNewItemCount = (StringFrame) rootFrame.getFrameByName("WelcomeNewItemCount", 0);
		rootFrame.setText(this.welcomeNewItemCount, "(0)");
		this.welcomeNewsBoxContainer = (SimpleFrame) rootFrame.getFrameByName("NewsBoxContainer", 0);
		this.welcomeMOTDText = (StringFrame) rootFrame.getFrameByName("WelcomeMOTDText", 0);
		rootFrame.setText(this.welcomeMOTDText,
				"This MOTD is set from source code and is not an externalized string. |cffdd00ffWarsmash|r engine is producing this message locally.|n|n |cff00ff00TODO:|r Modify the |cffdd00ffWarsmash|r engine sourcecode to download a message from the server to put here that admins can customize!");
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

	public void loginAccepted() {
		this.battleNetLoginPanel.setVisible(false);
		this.battleNetCancelBackdrop.setVisible(false);
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

	}

	public void hideWelcomeScreen() {
		this.welcomePanel.setVisible(false);
		this.quitBattleNetButton.setVisible(false);
	}

	public void hideCurrentScreen() {
		this.welcomePanel.setVisible(false);
		this.chatPanel.setVisible(false);
		this.welcomePanel.setVisible(false);
		this.quitBattleNetButton.setVisible(false);
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
}
