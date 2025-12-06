package com.etheller.warsmash.viewer5.handlers.tga;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.etheller.warsmash.datasources.SourcedData;
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
	protected void load(final SourcedData src, final Object options) {
		BufferedImage img;
		try {
			img = TgaFile.readTGA(this.fetchUrl, src.getResourceAsStream());
			update(img, false);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}