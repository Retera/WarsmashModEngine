package com.etheller.warsmash.viewer5.handlers.blp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public class BlpTexture extends Texture {

	public BlpTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, handler, extension, pathSolver, fetchUrl);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		BufferedImage img;
		try {
			img = ImageIO.read(src);
			final com.badlogic.gdx.graphics.Texture texture = ImageUtils
					.getTexture(ImageUtils.forceBufferedImagesRGB(img));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			setGdxTexture(texture);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
