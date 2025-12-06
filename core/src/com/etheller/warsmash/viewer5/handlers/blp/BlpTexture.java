package com.etheller.warsmash.viewer5.handlers.blp;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;
import com.etheller.warsmash.viewer5.RawOpenGLTextureResource;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

public class BlpTexture extends RawOpenGLTextureResource {

	public BlpTexture(final ModelViewer viewer, final ResourceHandler handler, final String extension,
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
			final ImageInputStream imageInputStream = ImageIO.createImageInputStream(src.getResourceAsStream());
			imageInputStream.mark();
			imageInputStream.skipBytes(3);
			final byte versionByte = imageInputStream.readByte();
			imageInputStream.reset();
			img = ImageIO.read(imageInputStream);
			update(img, versionByte != '2');
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}