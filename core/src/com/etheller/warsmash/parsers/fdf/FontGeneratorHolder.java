package com.etheller.warsmash.parsers.fdf;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.IntMap;

public class FontGeneratorHolder {
	private final FreeTypeFontGenerator generator;
	private final IntMap<BitmapFont> sizeToFont;

	public FontGeneratorHolder(final FreeTypeFontGenerator generator) {
		this.generator = generator;
		this.sizeToFont = new IntMap<>();
	}

	public BitmapFont generateFont(final FreeTypeFontParameter parameter) {
		BitmapFont font = this.sizeToFont.get(parameter.size);
		if (font == null) {
			parameter.incremental = true; //enable incremental to support non-ascii characters like chinese when free-type fonts contains the char.
			font = this.generator.generateFont(parameter);
			this.sizeToFont.put(parameter.size, font);
		}
		return font;
	}

	public void dispose() {
		this.generator.dispose();
		// TODO maybe dispose the fonts
	}
}
