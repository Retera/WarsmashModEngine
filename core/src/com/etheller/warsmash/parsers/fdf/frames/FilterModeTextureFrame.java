package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;

public class FilterModeTextureFrame extends TextureFrame {
	private int blendSrc;
	private int blendDst;

	public FilterModeTextureFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final Vector4Definition texCoord) {
		super(name, parent, decorateFileNames, texCoord);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		final int blendDstFunc = batch.getBlendDstFunc();
		final int blendSrcFunc = batch.getBlendSrcFunc();
		batch.setBlendFunction(this.blendSrc, this.blendDst);
		super.internalRender(batch, baseFont, glyphLayout);
		batch.setBlendFunction(blendSrcFunc, blendDstFunc);
	}

	public void setFilterMode(final FilterMode filterMode) {
		final int[] layerFilterMode = com.etheller.warsmash.viewer5.handlers.mdx.FilterMode.layerFilterMode(filterMode);
		this.blendSrc = layerFilterMode[0];
		this.blendDst = layerFilterMode[1];
	}

}
