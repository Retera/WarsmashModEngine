package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.MultiSelectionIconListener;

public class MultiSelectionIcon extends AbstractRenderableFrame implements ClickableActionFrame {

	public static final float HP_BAR_HEIGHT_RATIO = 0.175f;
	public static final float HP_BAR_SPACING_RATIO = 0.02f;
	private TextureFrame iconFrame;
	private final MultiSelectionIconListener clickListener;
	private float defaultWidth;
	private float defaultHeight;
	private final int queueIconIndexId;

	private String toolTip;
	private String uberTip;
	private boolean focused;
	private SimpleStatusBarFrame hpBarFrame;
	private SimpleStatusBarFrame manaBarFrame;

	public MultiSelectionIcon(final String name, final UIFrame parent, final MultiSelectionIconListener clickListener,
			final int queueIconIndexId) {
		super(name, parent);
		this.clickListener = clickListener;
		this.queueIconIndexId = queueIconIndexId;
	}

	public void set(final TextureFrame iconFrame, final SimpleStatusBarFrame hpBarFrame,
			final SimpleStatusBarFrame manaBarFrame) {
		this.iconFrame = iconFrame;
		this.hpBarFrame = hpBarFrame;
		this.manaBarFrame = manaBarFrame;
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
		this.hpBarFrame.positionBounds(gameUI, viewport);
		this.manaBarFrame.positionBounds(gameUI, viewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.iconFrame.render(batch, baseFont, glyphLayout);
		this.hpBarFrame.render(batch, baseFont, glyphLayout);
		this.manaBarFrame.render(batch, baseFont, glyphLayout);
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
		this.clickListener.multiSelectIconClicked(this.queueIconIndexId);
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

	private void innerSetDimensions(final float newWidth, final float newHeight) {
		this.iconFrame.setWidth(newWidth);
		this.iconFrame.setHeight(newHeight);
		this.hpBarFrame.setWidth(newWidth * 1.05f);
		this.hpBarFrame.setHeight(newHeight * HP_BAR_HEIGHT_RATIO);
		this.manaBarFrame.setWidth(newWidth * 1.05f);
		this.manaBarFrame.setHeight(newHeight * HP_BAR_HEIGHT_RATIO);
	}

	public void showUnFocused(final GameUI gameUI, final Viewport uiViewport) {
		final float newWidth = this.defaultWidth * 0.75f;
		final float newHeight = this.defaultHeight * 0.75f;
		innerSetDimensions(newWidth, newHeight);
		positionBounds(gameUI, uiViewport);
		this.focused = false;
	}

	public void showFocused(final GameUI gameUI, final Viewport uiViewport) {
		innerSetDimensions(this.defaultWidth, this.defaultHeight);
		positionBounds(gameUI, uiViewport);
		this.focused = true;
	}

	public void showMousePressed(final GameUI gameUI, final Viewport uiViewport) {
		final float ratio = this.focused ? 0.95f : 0.70f;
		final float newWidth = this.defaultWidth * ratio;
		final float newHeight = this.defaultHeight * ratio;
		innerSetDimensions(newWidth, newHeight);
		positionBounds(gameUI, uiViewport);
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
		this.clickListener.multiSelectIconPress(this.queueIconIndexId);
		showMousePressed(gameUI, uiViewport);
	}

	public void showMouseReleased(final GameUI gameUI, final Viewport uiViewport) {
		final float ratio = this.focused ? 1.00f : 0.75f;
		final float newWidth = this.defaultWidth * ratio;
		final float newHeight = this.defaultHeight * ratio;
		innerSetDimensions(newWidth, newHeight);
		positionBounds(gameUI, uiViewport);
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
		this.clickListener.multiSelectIconRelease(this.queueIconIndexId);
		showMouseReleased(gameUI, uiViewport);
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

	public void setLifeRatioRemaining(final float lifeRatioRemaining) {
		this.hpBarFrame.getBarFrame().setColor(Math.min(1.0f, 2.0f - (lifeRatioRemaining * 2)),
				Math.min(1.0f, lifeRatioRemaining * 2), 0, 1.0f);
		this.hpBarFrame.setValue(lifeRatioRemaining);
	}

	public void setManaRatioRemaining(final float lifeRatioRemaining) {
		this.manaBarFrame.setValue(lifeRatioRemaining);
	}

	public void setManaBarVisible(final boolean visible) {
		this.manaBarFrame.setVisible(visible);
	}
}
