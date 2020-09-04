package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;

public class StringFrame extends AbstractRenderableFrame {
	private Color color;
	private String text = "Default string";
	private final TextJustify justifyH;
	private final TextJustify justifyV;
	private final BitmapFont frameFont;

	public StringFrame(final String name, final UIFrame parent, final Color color, final TextJustify justifyH,
			final TextJustify justifyV, final BitmapFont frameFont) {
		super(name, parent);
		this.color = color;
		this.justifyH = justifyH;
		this.justifyV = justifyV;
		this.frameFont = frameFont;
		this.text = name;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.frameFont.setColor(this.color);
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
		this.frameFont.draw(batch, this.text, x, y);
	}

	@Override
	protected void innerPositionBounds(final Viewport viewport) {
	}

}
