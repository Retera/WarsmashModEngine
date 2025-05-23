package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.io.IOException;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;

public class GroundTextureWdt extends GroundTexture {
	public Texture texture;

	public GroundTextureWdt(final String path, final Element terrainTileInfo, final DataSource dataSource,
			final GL30 gl) throws IOException {
		super(path, terrainTileInfo, dataSource, gl);
		try {
			this.texture = ImageUtils.getAnyExtensionTexture(dataSource, path);
		}
		catch (final Exception exc) {
			this.texture = ImageUtils.getAnyExtensionTexture(dataSource, path.replace(".blp", "_s.blp")); // TODO
		}
		this.id = this.texture.getTextureObjectHandle();
	}
}
