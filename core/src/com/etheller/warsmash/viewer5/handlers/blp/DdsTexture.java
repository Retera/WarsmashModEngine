package com.etheller.warsmash.viewer5.handlers.blp;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public class DdsTexture extends RawOpenGLTextureResource {

	public DdsTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
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
			img = ImageIO.read(src.getResourceAsStream());
			update(img, false);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}