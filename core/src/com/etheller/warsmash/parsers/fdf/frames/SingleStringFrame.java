package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;

public class SingleStringFrame extends AbstractRenderableFrame {
	private Color color;
	private String text = "Default string";
	private final TextJustify justifyH;
	private final TextJustify justifyV;
	private final BitmapFont frameFont;
	private Color fontShadowColor;
	private float fontShadowOffsetX;
	private float fontShadowOffsetY;
	private float alpha = 1.0f;

	public SingleStringFrame(final String name, final UIFrame parent, final Color color, final TextJustify justifyH,
			final TextJustify justifyV, final BitmapFont frameFont) {
		super(name, parent);
		this.color = color;
		this.justifyH = justifyH;
		this.justifyV = justifyV;
		this.frameFont = frameFont;
		this.text = name;
	}

	public void setText(final String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		this.text = text;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public void setFontShadowColor(final Color fontShadowColor) {
		this.fontShadowColor = fontShadowColor;
	}

	public void setFontShadowOffsetX(final float fontShadowOffsetX) {
		this.fontShadowOffsetX = fontShadowOffsetX;
	}

	public void setFontShadowOffsetY(final float fontShadowOffsetY) {
		this.fontShadowOffsetY = fontShadowOffsetY;
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		glyphLayout.setText(this.frameFont, this.text);
		final float x;
		switch (this.justifyH) {
		case CENTER:
			x = this.renderBounds.x + ((this.renderBounds.width - glyphLayout.width) / 2);
			break;
		case RIGHT:
			x = (this.renderBounds.x + this.renderBounds.width) - glyphLayout.width;
			break;
		case LEFT:
		default:
			x = this.renderBounds.x;
			break;
		}
		final float y;
		switch (this.justifyV) {
		case MIDDLE:
			y = this.renderBounds.y + ((this.renderBounds.height + this.frameFont.getLineHeight()) / 2);
			break;
		case TOP:
			y = (this.renderBounds.y + this.renderBounds.height);
			break;
		case BOTTOM:
		default:
			y = this.renderBounds.y + this.frameFont.getLineHeight();
			break;
		}
		if (this.fontShadowColor != null) {
			this.frameFont.setColor(this.fontShadowColor.r, this.fontShadowColor.g, this.fontShadowColor.b,
					this.fontShadowColor.a * this.alpha);
			this.frameFont.draw(batch, this.text, x + this.fontShadowOffsetX, y + this.fontShadowOffsetY);
		}
		this.frameFont.setColor(this.color.r, this.color.g, this.color.b, this.color.a * this.alpha);
		this.frameFont.draw(batch, this.text, x, y);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;

	}

}
