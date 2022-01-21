package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.QueueIconListener;

public class QueueIcon extends AbstractRenderableFrame implements ClickableActionFrame {

	private TextureFrame iconFrame;
	private final QueueIconListener clickListener;
	private float defaultWidth;
	private float defaultHeight;
	private final int queueIconIndexId;

	private String toolTip;
	private String uberTip;

	public QueueIcon(final String name, final UIFrame parent, final QueueIconListener clickListener,
			final int queueIconIndexId) {
		super(name, parent);
		this.clickListener = clickListener;
		this.queueIconIndexId = queueIconIndexId;
	}

	public void set(final TextureFrame iconFrame) {
		this.iconFrame = iconFrame;
		setVisible(true);
	}

	public void clear() {
		setVisible(false);
	}

	public void setTexture(final Texture texture) {
		this.iconFrame.setTexture(texture);
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
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public void onClick(final int button) {
		this.clickListener.queueIconClicked(this.queueIconIndexId);
	}

	@Override
	public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {

	}

	@Override
	public void setWidth(final float width) {
		this.defaultWidth = width;
		super.setWidth(width);
	}

	@Override
	public void setHeight(final float height) {
		this.defaultHeight = height;
		super.setHeight(height);
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
		this.iconFrame.setWidth(this.defaultWidth * 0.95f);
		this.iconFrame.setHeight(this.defaultHeight * 0.95f);
		positionBounds(gameUI, uiViewport);
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
		this.iconFrame.setWidth(this.defaultWidth);
		this.iconFrame.setHeight(this.defaultHeight);
		positionBounds(gameUI, uiViewport);
	}

	@Override
	public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return null;
	}

	public void setToolTip(final String toolTip) {
		this.toolTip = toolTip;
	}

	public void setUberTip(final String uberTip) {
		this.uberTip = uberTip;
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
	public int getToolTipFoodCost() {
		return 0;
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
	public int getToolTipManaCost() {
		return 0;
	}
}
