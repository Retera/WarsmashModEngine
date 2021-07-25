package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandCardCommandListener;

public class CommandCardIcon extends AbstractRenderableFrame implements ClickableActionFrame {

	private TextureFrame iconFrame;
	private TextureFrame activeHighlightFrame;
	private SpriteFrame cooldownFrame;
	private SpriteFrame autocastFrame;
	private float defaultWidth;
	private float defaultHeight;
	private int abilityHandleId;
	private int orderId;
	private int autoCastOrderId;
	private boolean autoCastActive;
	private final CommandCardCommandListener commandCardCommandListener;
	private boolean menuButton;
	private String tip;
	private String uberTip;
	private int tipGoldCost;
	private int tipLumberCost;
	private int tipFoodCost;
	private char hotkey;

	public CommandCardIcon(final String name, final UIFrame parent,
			final CommandCardCommandListener commandCardCommandListener) {
		super(name, parent);
		this.commandCardCommandListener = commandCardCommandListener;
	}

	public void set(final TextureFrame iconFrame, final TextureFrame activeHighlightFrame,
			final SpriteFrame cooldownFrame, final SpriteFrame autocastFrame) {
		this.iconFrame = iconFrame;
		this.activeHighlightFrame = activeHighlightFrame;
		this.cooldownFrame = cooldownFrame;
		this.autocastFrame = autocastFrame;
	}

	public void clear() {
		this.iconFrame.setVisible(false);
		if (this.activeHighlightFrame != null) {
			this.activeHighlightFrame.setVisible(false);
		}
		this.cooldownFrame.setVisible(false);
		if (this.autocastFrame != null) {
			this.autocastFrame.setVisible(false);
		}
		setVisible(false);
		this.hotkey = '\0';
	}

	public void setCommandButtonData(final Texture texture, final int abilityHandleId, final int orderId,
			final int autoCastOrderId, final boolean active, final boolean autoCastActive, final boolean menuButton,
			final String tip, final String uberTip, final char hotkey, final int goldCost, final int lumberCost,
			final int foodCost) {
		this.menuButton = menuButton;
		this.hotkey = hotkey;
		setVisible(true);
		this.iconFrame.setVisible(true);
		if (this.activeHighlightFrame != null) {
			this.activeHighlightFrame.setVisible(active);
		}
		this.cooldownFrame.setVisible(false);
		if (this.autocastFrame != null) {
			this.autocastFrame.setVisible(autoCastOrderId != 0);
			if (autoCastOrderId != 0) {
				if (this.autoCastActive != autoCastActive) {
					if (autoCastActive) {
						this.autocastFrame.setSequence(PrimaryTag.STAND);
					}
					else {
						this.autocastFrame.setSequence(-1);
					}
				}
				this.autoCastActive = autoCastActive;
			}
		}
		this.iconFrame.setTexture(texture);
		this.abilityHandleId = abilityHandleId;
		this.orderId = orderId;
		this.autoCastOrderId = autoCastOrderId;
		this.tip = tip;
		this.uberTip = uberTip;
		this.tipGoldCost = goldCost;
		this.tipLumberCost = lumberCost;
		this.tipFoodCost = foodCost;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.iconFrame.positionBounds(gameUI, viewport);
		if (this.activeHighlightFrame != null) {
			this.activeHighlightFrame.positionBounds(gameUI, viewport);
		}
		this.cooldownFrame.positionBounds(gameUI, viewport);
		if (this.autocastFrame != null) {
			this.autocastFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.iconFrame.render(batch, baseFont, glyphLayout);
		if (this.activeHighlightFrame != null) {
			this.activeHighlightFrame.render(batch, baseFont, glyphLayout);
		}
		this.cooldownFrame.render(batch, baseFont, glyphLayout);
		if (this.autocastFrame != null) {
			this.autocastFrame.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (((button == Input.Buttons.LEFT) && (this.orderId != 0))
					|| ((button == Input.Buttons.RIGHT) && (this.autoCastOrderId != 0)) || this.menuButton) {
				return this;
			}
		}
		return super.touchDown(screenX, screenY, button);
	}

	public boolean checkHotkey(final char c, final int keycode) {
		if ((c == this.hotkey) || (Character.toUpperCase(c) == this.hotkey)
				|| ((this.hotkey == 0x7E) && (keycode == Input.Keys.ESCAPE))) {
			onClick(Input.Buttons.LEFT);
			return true;
		}
		return false;
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchUp(screenX, screenY, button);
	}

	public boolean isMenuButton() {
		return this.menuButton;
	}

	@Override
	public void onClick(final int button) {
		if (button == Input.Buttons.LEFT) {
			if (this.menuButton) {
				this.commandCardCommandListener.openMenu(this.orderId);
			}
			else {
				this.commandCardCommandListener.onClick(this.abilityHandleId, this.orderId, false);
			}
		}
		else if (button == Input.Buttons.RIGHT) {
			this.commandCardCommandListener.onClick(this.abilityHandleId, this.autoCastOrderId, true);
		}
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

	@Override
	public String getToolTip() {
		return this.tip;
	}

	@Override
	public String getUberTip() {
		return this.uberTip;
	}

	@Override
	public int getToolTipGoldCost() {
		return this.tipGoldCost;
	}

	@Override
	public int getToolTipLumberCost() {
		return this.tipLumberCost;
	}

	@Override
	public int getToolTipFoodCost() {
		return this.tipFoodCost;
	}
}
