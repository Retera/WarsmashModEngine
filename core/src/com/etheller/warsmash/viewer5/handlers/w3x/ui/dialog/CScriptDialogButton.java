package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;

public class CScriptDialogButton {
	private final GlueTextButtonFrame buttonFrame;
	private final StringFrame buttonText;

	public CScriptDialogButton(final GlueTextButtonFrame buttonFrame, final StringFrame buttonText) {
		this.buttonFrame = buttonFrame;
		this.buttonText = buttonText;
	}

	public GlueTextButtonFrame getButtonFrame() {
		return this.buttonFrame;
	}

	public void setText(final GameUI rootFrame, final String text) {
		rootFrame.setText(this.buttonText, text);
	}

	public void setupEvents(final CScriptDialog dialog) {
		this.buttonFrame.setOnClick(new Runnable() {
			@Override
			public void run() {
				dialog.onButtonClick(CScriptDialogButton.this);
			}
		});
	}
}
