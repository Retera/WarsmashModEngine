package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class CLeaderboard {
	private final UIFrame leaderboardFrame;
	private final StringFrame leaderboardTitle;

	public CLeaderboard(UIFrame leaderboardFrame, StringFrame leaderboardTitle) {
		this.leaderboardFrame = leaderboardFrame;
		this.leaderboardTitle = leaderboardTitle;
	}

	public void setTitle(GameUI rootFrame, String titleText) {
		rootFrame.setText(this.leaderboardTitle, titleText);
	}

	public void setVisible(boolean flag) {
		this.leaderboardFrame.setVisible(flag);
	}

	private static final class LeaderboardItem {
		private final String label;
		private final int value;
		private final int playerIndex;

		public LeaderboardItem(String label, int value, int playerIndex) {
			this.label = label;
			this.value = value;
			this.playerIndex = playerIndex;
		}
	}
}
