package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.BackdropCornerFlags;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.viewer5.Scene;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.parsers.mdlx.MdlxMaterial;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture.WrapMode;

public class SmartBackdropFrame extends SpriteFrame {
	private final boolean decorateFileNames;
	private final boolean tileBackground;
	private final String backgroundString;
	private final EnumSet<BackdropCornerFlags> cornerFlags;
	private final float cornerSize;
	private final float backgroundSize;
	private final Vector4Definition backgroundInsets;
	private final String edgeFileString;

	public SmartBackdropFrame(final String name, final UIFrame parent, final Scene scene, final Viewport uiViewport,
			final boolean decorateFileNames, final boolean tileBackground, final String backgroundString,
			final EnumSet<BackdropCornerFlags> cornerFlags, final float cornerSize, final float backgroundSize,
			final Vector4Definition backgroundInsets, final String edgeFileString) {
		super(name, parent, scene, uiViewport);
		this.decorateFileNames = decorateFileNames;
		this.tileBackground = tileBackground;
		this.backgroundString = backgroundString;
		this.cornerFlags = cornerFlags;
		this.cornerSize = cornerSize;
		this.backgroundSize = backgroundSize;
		this.backgroundInsets = backgroundInsets;
		this.edgeFileString = edgeFileString;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		generateBackdropModel();
		super.innerPositionBounds(gameUI, viewport);
	}

	private MdlxModel generateBackdropModel() {
		final MdlxModel model = new MdlxModel();
		final int edgeFileMaterialId = generateMaterial(model, this.edgeFileString, true);
		final int backgroundMaterialId = generateMaterial(model, this.backgroundString, this.tileBackground);
		final MdlxGeoset edgeGeoset = new MdlxGeoset();
		final float[] edgeGeosetVertices = new float[32 * 4];
		return model;
	}

	private int generateMaterial(final MdlxModel model, final String path, final boolean wrap) {
		final MdlxTexture edgeFileReference = new MdlxTexture();
		if (wrap) {
			edgeFileReference.setWrapMode(WrapMode.REPEAT_BOTH);
		}
		edgeFileReference.setPath(path);
		final int textureId = model.getTextures().size();
		model.getTextures().add(edgeFileReference);
		final MdlxMaterial edgeFileMaterial = new MdlxMaterial();
		final MdlxLayer edgeFileMaterialLayer = new MdlxLayer();
		edgeFileMaterialLayer.setAlpha(1.0f);
		edgeFileMaterialLayer.setFilterMode(FilterMode.BLEND);
		edgeFileMaterialLayer.setTextureId(textureId);
		edgeFileMaterial.getLayers().add(edgeFileMaterialLayer);
		final int materialId = model.getMaterials().size();
		model.getMaterials().add(edgeFileMaterial);
		return materialId;
	}

}
