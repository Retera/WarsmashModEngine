package com.etheller.warsmash.viewer5.handlers.tga;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public class TgaTexture extends RawOpenGLTextureResource {

	public TgaTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(final InputStream src, final Object options) {
		BufferedImage img;
		try {
			img = TgaFile.readTGA(this.fetchUrl, src);
			update(img, false);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}