package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;

public class GlueButtonFrame extends AbstractRenderableFrame implements ClickableFrame {
	private UIFrame controlBackdrop;
	private UIFrame controlPushedBackdrop;
	private UIFrame controlDisabledBackdrop;
	private UIFrame controlMouseOverHighlight;

	private boolean enabled = true;
	private boolean highlightOnMouseOver;
	private boolean mouseOver = false;

	private UIFrame activeChild;

	private Runnable onClick;

	public GlueButtonFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setControlBackdrop(final UIFrame controlBackdrop) {
		this.controlBackdrop = controlBackdrop;
	}

	public void setControlPushedBackdrop(final UIFrame controlPushedBackdrop) {
		this.controlPushedBackdrop = controlPushedBackdrop;
	}

	public void setControlDisabledBackdrop(final UIFrame controlDisabledBackdrop) {
		this.controlDisabledBackdrop = controlDisabledBackdrop;
	}

	public void setControlMouseOverHighlight(final UIFrame controlMouseOverHighlight) {
		this.controlMouseOverHighlight = controlMouseOverHighlight;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
		}
		else {
			this.activeChild = this.controlDisabledBackdrop;
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
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
		}
		else {
			this.activeChild = this.controlDisabledBackdrop;
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.activeChild != null) {
			this.activeChild.render(batch, baseFont, glyphLayout);
		}
		if (this.mouseOver) {
			this.controlMouseOverHighlight.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
		if (this.enabled) {
			this.activeChild = this.controlPushedBackdrop;
		}
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
		if (this.enabled) {
			this.activeChild = this.controlBackdrop;
		}
	}

	@Override
	public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
		if (this.highlightOnMouseOver) {
			this.mouseOver = true;
		}
	}

	@Override
	public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
		this.mouseOver = false;
	}

	@Override
	public void onClick(final int button) {
		if (this.onClick != null) {
			this.onClick.run();
		}
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

}
