package com.etheller.warsmash.parsers.fdf;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.DataSourceFileHandle;

public class DynamicFontGeneratorHolder {
	private final DataSource dataSource;
	private final Element skin;
	private final Map<String, FontGeneratorHolder> fontNameToGenerator;

	public DynamicFontGeneratorHolder(final DataSource dataSource, final Element skin) {
		this.dataSource = dataSource;
		this.skin = skin;
		this.fontNameToGenerator = new HashMap<>();
	}

	public FontGeneratorHolder getFontGenerator(final String font) {
		FontGeneratorHolder fontGenerator = this.fontNameToGenerator.get(font);
		if (fontGenerator == null) {
			String fontName = this.skin.getField(font);
			if ((fontName == null) || fontName.isEmpty()) {
				if (this.dataSource.has(font)) {
					fontName = font;
				}
				else {
					throw new IllegalStateException("No such font: " + font);
				}
			}
			if (!this.dataSource.has(fontName)) {
				throw new IllegalStateException("No such font file: " + fontName + " (for \"" + font + "\")");
			}
			fontGenerator = new FontGeneratorHolder(
					new FreeTypeFontGenerator(new DataSourceFileHandle(this.dataSource, fontName)));
			this.fontNameToGenerator.put(font, fontGenerator);
		}
		return fontGenerator;
	}

	public void dispose() {
		for (final FontGeneratorHolder generator : this.fontNameToGenerator.values()) {
			generator.dispose();
		}
		this.fontNameToGenerator.clear();
	}
}
