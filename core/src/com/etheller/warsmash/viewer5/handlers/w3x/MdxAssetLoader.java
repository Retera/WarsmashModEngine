package com.etheller.warsmash.viewer5.handlers.w3x;

import java.awt.image.BufferedImage;

import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;

public interface MdxAssetLoader {
	public MdxModel loadModelMdx(final String path);

	public BufferedImage loadPathingTexture(String pathingTexture);
}
