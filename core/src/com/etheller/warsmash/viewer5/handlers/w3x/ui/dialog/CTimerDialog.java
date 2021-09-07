package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class CTimerDialog {
	private final CTimer timer;
	private final UIFrame timerDialogFrame;
	private final StringFrame valueFrame;
	private final StringFrame titleFrame;

	public CTimerDialog(final CTimer timer, final UIFrame timerDialogFrame, final StringFrame valueFrame,
			final StringFrame titleFrame) {
		this.timer = timer;
		this.timerDialogFrame = timerDialogFrame;
		this.valueFrame = valueFrame;
		this.titleFrame = titleFrame;
	}

	public void setTitle(final GameUI rootFrame, final String title) {
		rootFrame.setText(this.titleFrame, title);
	}

	public void setValue(final GameUI rootFrame, final String value) {
		rootFrame.setText(this.valueFrame, value);
	}

	public void setVisible(final boolean visible) {
		this.timerDialogFrame.setVisible(visible);
	}

	public void update(final GameUI rootFrame, final CSimulation simulation) {
		if (this.timerDialogFrame.isVisible() && (this.timer != null)) {
			final float remaining = this.timer.getRemaining(simulation);
			final int secondsRemaining = (int) remaining;
			final int minutes = secondsRemaining / 60;
			final int seconds = secondsRemaining % 60;

			rootFrame.setText(this.valueFrame, minutes + ":" + seconds);
		}
	}

}
