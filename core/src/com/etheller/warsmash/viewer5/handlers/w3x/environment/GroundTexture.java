package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import com.badlogic.gdx.graphics.GL30;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.ImageUtils.AnyExtensionImage;

public class GroundTexture {
	public int id;
	private String tileId;
	private int tileSize;
	private boolean buildable;
	public boolean extended;

	public GroundTexture(final String path, final Element terrainTileInfo, final DataSource dataSource, final GL30 gl)
			throws IOException {
		if (terrainTileInfo != null) {
			this.tileId = terrainTileInfo.getId();
			final String buildableFieldValue = terrainTileInfo.getField("buildable");
			this.buildable = buildableFieldValue.isEmpty() ? false : Integer.parseInt(buildableFieldValue) == 1;
		}
		else {
			this.buildable = true;
		}
		if (dataSource.has(path)) {
			final AnyExtensionImage imageInfo = ImageUtils.getAnyExtensionImageFixRGB(dataSource, path,
					"ground texture: " + this.tileId);
			loadImage(path, gl, imageInfo.getImageData(), imageInfo.isNeedsSRGBFix());
		}
	}

	public boolean isBuildable() {
		return this.buildable;
	}

	private void loadImage(final String path, final GL30 gl, final BufferedImage image, final boolean sRGBFix) {
		if (image == null) {
			throw new IllegalStateException(this.tileId + ": Missing ground texture: " + path);
		}
		final Buffer buffer = ImageUtils.getTextureBuffer(sRGBFix ? ImageUtils.forceBufferedImagesRGB(image) : image);
		final int width = image.getWidth();
		final int height = image.getHeight();

		this.tileSize = (int) (height * 0.25);
		this.extended = (width > height);

		this.id = gl.glGenTexture();
		gl.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, this.id);
		gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL30.GL_RGBA8, this.tileSize, this.tileSize,
				this.extended ? 32 : 16, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

		gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, width);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				buffer.position(((y * this.tileSize * width) + (x * this.tileSize)) * 4);
				gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, (y * 4) + x, this.tileSize, this.tileSize, 1,
						GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);

				if (this.extended) {
					buffer.position(((y * this.tileSize * width) + ((x + 4) * this.tileSize)) * 4);
					gl.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, (y * 4) + x + 16, this.tileSize,
							this.tileSize, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
				}
			}
		}
		gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, 0);
		gl.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
	}
}
