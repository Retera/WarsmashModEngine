package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButton;

public class CommandCardIcon extends AbstractRenderableFrame {

	private TextureFrame iconFrame;
	private SpriteFrame cooldownFrame;
	private SpriteFrame autocastFrame;
	private CommandButton commandButton;

	public CommandCardIcon(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void set(final TextureFrame iconFrame, final SpriteFrame cooldownFrame, final SpriteFrame autocastFrame) {
		this.iconFrame = iconFrame;
		this.cooldownFrame = cooldownFrame;
		this.autocastFrame = autocastFrame;
	}

	public void setCommandButton(final CommandButton commandButton) {
		this.commandButton = commandButton;
		if (commandButton == null) {
			this.iconFrame.setVisible(false);
			this.cooldownFrame.setVisible(false);
			this.autocastFrame.setVisible(false);
		}
		else {
			if (commandButton.isEnabled()) {
				this.iconFrame.setTexture(commandButton.getIcon());
			}
			else {
				this.iconFrame.setTexture(commandButton.getDisabledIcon());
			}
			if (commandButton.getCooldownRemaining() <= 0) {
				this.cooldownFrame.setVisible(false);
			}
			else {
				this.cooldownFrame.setVisible(true);
				this.cooldownFrame.setSequence(PrimaryTag.STAND);
				this.cooldownFrame.setAnimationSpeed(commandButton.getCooldown());
				this.cooldownFrame
						.setFrameByRatio(1 - (commandButton.getCooldownRemaining() / commandButton.getCooldown()));
			}
			this.autocastFrame.setVisible(commandButton.isAutoCastActive());
		}
	}

	public void setCommandButtonData(final Texture texture, final int orderId) {
		this.iconFrame.setVisible(true);
		this.cooldownFrame.setVisible(false);
		this.autocastFrame.setVisible(false);
		this.iconFrame.setTexture(texture);
	}

	@Override
	protected void innerPositionBounds(final Viewport viewport) {
		this.iconFrame.positionBounds(viewport);
		this.cooldownFrame.positionBounds(viewport);
		this.autocastFrame.positionBounds(viewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.iconFrame.render(batch, baseFont, glyphLayout);
		this.cooldownFrame.render(batch, baseFont, glyphLayout);
		this.autocastFrame.render(batch, baseFont, glyphLayout);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return null;
	}
}
