package com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.ClickConsumingTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.GlueTextButtonFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;

public class DialogWar3 {

	private final GameUI rootFrame;
	private final Viewport uiViewport;
	private final UIFrame dialogWar3;
	private final StringFrame dialogWar3Text;
	private final GlueTextButtonFrame dialogWar3ButtonOk;
	private final GlueTextButtonFrame dialogWar3ButtonNo;
	private final GlueTextButtonFrame dialogWar3ButtonYes;
	private final UIFrame dialogWar3ButtonOkBackdrop;
	private final UIFrame dialogWar3ButtonNoBackdrop;
	private final UIFrame dialogWar3ButtonYesBackdrop;
	private final float defaultHeight;
	private final BackdropFrame dialogWar3Icon;

	public DialogWar3(final GameUI rootFrame, final Viewport uiViewport) {
		this.rootFrame = rootFrame;
		this.uiViewport = uiViewport;
		this.dialogWar3 = rootFrame.createFrame("DialogWar3", rootFrame, 0, 0);
		this.dialogWar3.addAnchor(new AnchorDefinition(FramePoint.CENTER, 0, 0));
		this.dialogWar3Text = (StringFrame) rootFrame.getFrameByName("DialogText", 0);
		this.dialogWar3ButtonOk = (GlueTextButtonFrame) rootFrame.getFrameByName("DialogButtonOK", 0);
		this.dialogWar3ButtonNo = (GlueTextButtonFrame) rootFrame.getFrameByName("DialogButtonNo", 0);
		this.dialogWar3ButtonYes = (GlueTextButtonFrame) rootFrame.getFrameByName("DialogButtonYes", 0);
		this.dialogWar3Icon = (BackdropFrame) rootFrame.getFrameByName("DialogIcon", 0);
		this.dialogWar3ButtonOkBackdrop = rootFrame.getFrameByName("DialogButtonOKBackdrop", 0);
		this.dialogWar3ButtonNoBackdrop = rootFrame.getFrameByName("DialogButtonNoBackdrop", 0);
		this.dialogWar3ButtonYesBackdrop = rootFrame.getFrameByName("DialogButtonYesBackdrop", 0);
		this.defaultHeight = this.dialogWar3.getAssignedHeight();
		this.dialogWar3.setVisible(false);

		final TextureFrame modalDialogBlacknessScreenCover = new ClickConsumingTextureFrame(null, this.rootFrame, false,
				null);
		modalDialogBlacknessScreenCover.setTexture("Textures\\Black32.blp", rootFrame);
		modalDialogBlacknessScreenCover.setColor(1.0f, 1.0f, 1.0f, 0.5f);
		modalDialogBlacknessScreenCover.setSetAllPoints(true);
		((SimpleFrame) this.dialogWar3).add(0, modalDialogBlacknessScreenCover);
	}

	public void showMessage(final String message, final Runnable runnable) {
		showMessage(DialogIcon.MESSAGE, message, runnable);
	}

	public void showError(final String message, final Runnable runnable) {
		showMessage(DialogIcon.ERROR, message, runnable);
	}

	public void showMessage(final DialogIcon icon, final String message, final Runnable runnable) {
		this.rootFrame.setDecoratedText(this.dialogWar3Text, message);
		this.dialogWar3ButtonOkBackdrop.setVisible(true);
		this.dialogWar3ButtonNoBackdrop.setVisible(false);
		this.dialogWar3ButtonYesBackdrop.setVisible(false);
		this.dialogWar3ButtonOk.setOnClick(new Runnable() {
			@Override
			public void run() {
				if (runnable != null) {
					runnable.run();
				}
				DialogWar3.this.dialogWar3.setVisible(false);
			}
		});
		this.dialogWar3.setHeight(this.dialogWar3Text.getPredictedViewportHeight() + (this.defaultHeight * .75f));
		this.dialogWar3.positionBounds(this.rootFrame, this.uiViewport);
		this.dialogWar3.setVisible(true);
		this.dialogWar3Icon.setBackground(this.rootFrame.loadTexture(icon.getPath()));
	}

	public void show(final String message, final DialogWar3Listener listener) {
		this.rootFrame.setDecoratedText(this.dialogWar3Text, message);
		this.dialogWar3ButtonOkBackdrop.setVisible(false);
		this.dialogWar3ButtonNoBackdrop.setVisible(true);
		this.dialogWar3ButtonYesBackdrop.setVisible(true);
		this.dialogWar3ButtonYes.setOnClick(new Runnable() {
			@Override
			public void run() {
				listener.yes();
				DialogWar3.this.dialogWar3.setVisible(false);
			}
		});
		this.dialogWar3ButtonNo.setOnClick(new Runnable() {
			@Override
			public void run() {
				listener.no();
				DialogWar3.this.dialogWar3.setVisible(false);
			}
		});
		this.dialogWar3Icon.setBackground(this.rootFrame.loadTexture(DialogIcon.QUESTION.getPath()));
		this.dialogWar3.setHeight(this.dialogWar3Text.getPredictedViewportHeight() + (this.defaultHeight * .75f));
		this.dialogWar3.positionBounds(this.rootFrame, this.uiViewport);
		this.dialogWar3.setVisible(true);
	}

	public static interface DialogWar3Listener {
		void yes();

		void no();
	}

	public static enum DialogIcon {
		ERROR("UI\\Widgets\\Glues\\dialogbox-error.blp"), MESSAGE("UI\\Widgets\\Glues\\dialogbox-message.blp"),
		QUESTION("UI\\Widgets\\Glues\\dialogbox-question.blp");

		private String path;

		private DialogIcon(final String path) {
			this.path = path;
		}

		public String getPath() {
			return this.path;
		}
	}
}
