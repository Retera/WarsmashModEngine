package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;

public class SimpleButtonFrame extends AbstractRenderableFrame implements ClickableFrame {

	private UIFrame controlBackdrop;
	private UIFrame controlPushedBackdrop;
	private UIFrame controlDisabledBackdrop;
	private UIFrame controlMouseOverHighlight;

	private boolean enabled = true;
	private boolean highlightOnMouseOver;
	private boolean mouseOver = false;
	private boolean pushed = false;

	private UIFrame activeChild;
	private UIFrame activeTextChild;

	private UIFrame buttonText;
	private UIFrame disabledText;
	private UIFrame highlightText;

	private UIFrame pushedText;
	private UIFrame pushedHighlightText;

	private Runnable onClick;

	public SimpleButtonFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setControlBackdrop(final UIFrame controlBackdrop) {
		this.controlBackdrop = controlBackdrop;
		if (this.activeChild == null) {
			this.activeChild = controlBackdrop;
		}
	}

	public void setControlPushedBackdrop(final UIFrame controlPushedBackdrop) {
		this.controlPushedBackdrop = controlPushedBackdrop;
	}

	public void setControlDisabledBackdrop(final UIFrame controlDisabledBackdrop) {
		this.controlDisabledBackdrop = controlDisabledBackdrop;
	}

	public void setControlMouseOverHighlight(final UIFrame controlMouseOverHighlight) {
		this.controlMouseOverHighlight = controlMouseOverHighlight;
		this.highlightOnMouseOver |= controlMouseOverHighlight != null;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
			this.activeTextChild = this.buttonText;
		}
		else {
			this.activeChild = this.controlDisabledBackdrop;
			this.activeTextChild = this.disabledText;
		}
	}

	public void setHighlightOnMouseOver(final boolean highlightOnMouseOver) {
		this.highlightOnMouseOver = highlightOnMouseOver;
	}

	public void setOnClick(final Runnable onClick) {
		this.onClick = onClick;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		if (this.controlBackdrop != null) {
			this.controlBackdrop.positionBounds(gameUI, viewport);
		}
		if (this.controlPushedBackdrop != null) {
			this.controlPushedBackdrop.positionBounds(gameUI, viewport);
		}
		if (this.controlDisabledBackdrop != null) {
			this.controlDisabledBackdrop.positionBounds(gameUI, viewport);
		}
		if (this.controlMouseOverHighlight != null) {
			this.controlMouseOverHighlight.positionBounds(gameUI, viewport);
		}
		if (this.buttonText != null) {
			this.buttonText.positionBounds(gameUI, viewport);
		}
		if (this.pushedText != null) {
			this.pushedText.positionBounds(gameUI, viewport);
		}
		if (this.disabledText != null) {
			this.disabledText.positionBounds(gameUI, viewport);
		}
		if (this.highlightText != null) {
			this.highlightText.positionBounds(gameUI, viewport);
		}
		if (this.pushedHighlightText != null) {
			this.pushedHighlightText.positionBounds(gameUI, viewport);
		}
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
			this.activeTextChild = this.buttonText;
		}
		else {
			this.activeChild = this.controlDisabledBackdrop;
			this.activeTextChild = this.disabledText;
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.activeChild != null) {
			this.activeChild.render(batch, baseFont, glyphLayout);
		}
		if (this.activeTextChild != null) {
			this.activeTextChild.render(batch, baseFont, glyphLayout);
		}
		if (this.mouseOver) {
			this.controlMouseOverHighlight.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
		if (this.enabled) {
			this.activeChild = this.controlPushedBackdrop;
			this.pushed = true;
			this.activeTextChild = this.mouseOver ? this.pushedHighlightText : this.pushedText;
		}
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
			this.activeTextChild = this.mouseOver ? this.highlightText : this.buttonText;
		}
		this.pushed = false;
	}

	@Override
	public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
		if (this.highlightOnMouseOver) {
			this.mouseOver = true;
			if (this.enabled) {
				this.activeTextChild = this.pushed ? this.pushedHighlightText : this.highlightText;
			}
		}
	}

	@Override
	public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
		this.mouseOver = false;
		if (this.enabled) {
			this.activeTextChild = this.pushed ? this.pushedText : this.buttonText;
		}
	}

	@Override
	public void onClick(final int button) {
		if (this.onClick != null) {
			this.onClick.run();
		}
	}

	@Override
	public void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y) {

	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.enabled && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.enabled && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.enabled && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}

	public void setButtonText(final UIFrame buttonText) {
		this.buttonText = buttonText;
	}

	public void setPushedHighlightText(final UIFrame pushedHighlightText) {
		this.pushedHighlightText = pushedHighlightText;
	}

	public void setPushedText(final UIFrame pushedText) {
		this.pushedText = pushedText;
	}

	public void setHighlightText(final UIFrame highlightText) {
		this.highlightText = highlightText;
		this.highlightOnMouseOver |= highlightText != null;
	}

	public void setDisabledText(final UIFrame disabledText) {
		this.disabledText = disabledText;
	}
}
