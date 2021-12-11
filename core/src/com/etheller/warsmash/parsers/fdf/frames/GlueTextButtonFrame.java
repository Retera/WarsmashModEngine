package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;

public class GlueTextButtonFrame extends GlueButtonFrame {
	private UIFrame buttonText;

	public GlueTextButtonFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setButtonText(final UIFrame buttonText) {
		this.buttonText = buttonText;
	}

	public UIFrame getButtonText() {
		return this.buttonText;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		if (this.buttonText != null) {
			this.buttonText.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		if (this.buttonText != null) {
			this.buttonText.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (this.buttonText instanceof StringFrame) {
			final StringFrame stringButtonText = (StringFrame) this.buttonText;
			final Color fontColor = enabled ? stringButtonText.getFontOriginalColor()
					: stringButtonText.getFontDisabledColor();
			if (fontColor != null) {
				stringButtonText.setColor(fontColor);
			}
		}
	}

	@Override
	protected void onMouseEnter() {
		super.onMouseEnter();
		if (isEnabled()) {
			if (this.buttonText instanceof StringFrame) {
				final StringFrame stringFrame = (StringFrame) this.buttonText;
				final Color fontHighlightColor = stringFrame.getFontHighlightColor();
				if (fontHighlightColor != null) {
					stringFrame.setColor(fontHighlightColor);
				}
			}
		}
	}

	@Override
	protected void onMouseExit() {
		super.onMouseExit();
		if (isEnabled()) {
			if (this.buttonText instanceof StringFrame) {
				final StringFrame stringFrame = (StringFrame) this.buttonText;
				final Color fontOriginalColor = stringFrame.getFontOriginalColor();
				if (fontOriginalColor != null) {
					stringFrame.setColor(fontOriginalColor);
				}
			}
		}
	}
}
