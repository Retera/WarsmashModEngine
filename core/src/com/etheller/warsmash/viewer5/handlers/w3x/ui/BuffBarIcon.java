package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.AbstractClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;

public class BuffBarIcon extends AbstractClickableActionFrame implements ClickableActionFrame {
	private TextureFrame iconFrame;

	private String toolTip;
	private String uberTip;
	private boolean positive;
	private boolean leveled;

	public BuffBarIcon(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setIconFrame(final TextureFrame iconFrame) {
		this.iconFrame = iconFrame;
	}

	public void set(final Texture texture, final String toolTip, final String uberTip, final boolean positive, final boolean leveled, final int level) {
		this.iconFrame.setTexture(texture);
		this.positive = positive;
		this.leveled = leveled;
		this.setLevel(level);
		this.setToolTip(toolTip);
		this.setUberTip(uberTip);
		setVisible(true);
	}

	public void clear(/* final Texture texture */) {
//		this.iconFrame.setTexture(texture);
		setVisible(false);
//		setVisible(true);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.iconFrame.positionBounds(gameUI, viewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.iconFrame.render(batch, baseFont, glyphLayout);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return null;
	}

	public void setToolTip(final String toolTip) {
		if (toolTip != null && !toolTip.isBlank()) {
			if (this.positive) {
				this.toolTip = "|c0000FF00" + toolTip + "|r";
			} else {
				this.toolTip = "|c00FF0000" + toolTip + "|r";
			}
		} else {
			this.toolTip = toolTip;
		}
	}

	public void setUberTip(final String uberTip) {
		if (uberTip != null) {
			if (this.leveled) {
				this.uberTip = "|c007d7d7dLevel:|r " + this.level + "|n|n" + uberTip;
			} else {
				this.uberTip = uberTip;
			}
		} else {
			this.uberTip = "|c007d7d7dLevel:|r " + this.level;
		}
	}

	@Override
	public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public String getSoundKey() {
		return null;
	}

	@Override
	public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void onClick(final int button) {
	}

	@Override
	public String getToolTip() {
		return this.toolTip;
	}

	@Override
	public String getUberTip() {
		return this.uberTip;
	}

	@Override
	public int getToolTipGoldCost() {
		return 0;
	}

	@Override
	public int getToolTipLumberCost() {
		return 0;
	}

	@Override
	public int getToolTipFoodCost() {
		return 0;
	}

	@Override
	public int getToolTipManaCost() {
		return 0;
	}
}
