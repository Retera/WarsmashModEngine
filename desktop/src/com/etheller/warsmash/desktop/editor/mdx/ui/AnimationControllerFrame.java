package com.etheller.warsmash.desktop.editor.mdx.ui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.etheller.warsmash.WarsmashPreviewApplication;
import com.etheller.warsmash.desktop.editor.mdx.listeners.YseraGUIListener;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

public class AnimationControllerFrame extends JFrame implements YseraGUIListener {
	private final AnimationControllerPanel animationControllerPanel;

	public AnimationControllerFrame(final WarsmashPreviewApplication warsmashPreviewApplication) {
		super("Animation Controller");
		this.animationControllerPanel = new AnimationControllerPanel(warsmashPreviewApplication);
		setContentPane(this.animationControllerPanel);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		pack();
	}

	@Override
	public void openModel(final MdlxModel model) {
		this.animationControllerPanel.openModel(model);
	}

	@Override
	public void stateChanged() {
		this.animationControllerPanel.stateChanged();
	}
}
