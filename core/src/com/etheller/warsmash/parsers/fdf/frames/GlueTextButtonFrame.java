package com.etheller.warsmash.parsers.fdf.frames;

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
}
