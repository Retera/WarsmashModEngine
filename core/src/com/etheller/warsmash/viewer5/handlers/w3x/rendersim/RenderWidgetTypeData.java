package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.MdxAssetLoader;

public abstract class RenderWidgetTypeData<T> {
	protected final ObjectData objectData;
	protected final DataSource dataSource;
	protected final MdxAssetLoader mapViewer;
	private final Map<War3ID, T> unitIdToTypeData = new HashMap<>();

	public RenderWidgetTypeData(ObjectData objectData, DataSource dataSource, MdxAssetLoader mapViewer) {
		this.objectData = objectData;
		this.dataSource = dataSource;
		this.mapViewer = mapViewer;
	}

	public final T get(War3ID key) {
		T unitTypeData = this.unitIdToTypeData.get(key);
		if (unitTypeData != null) {
			return unitTypeData;
		}
		final GameObject row = this.objectData.get(key);
		if (row != null) {
			unitTypeData = createTypeData(key, row);
			this.unitIdToTypeData.put(key, unitTypeData);
		}
		return unitTypeData;
	}

	protected final String getShadowTexture(String unitShadow) {
		String shadowTexture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".blp";
		if (!this.dataSource.has(shadowTexture)) {
			shadowTexture = "ReplaceableTextures\\Shadows\\" + unitShadow + ".dds"; // fallback
		}
		return shadowTexture;
	}

	protected final MdxModel getPortraitModel(final String path, final MdxModel model) {
		MdxModel portraitModel;
		final String portraitPath = path.substring(0, path.length() - 4) + "_portrait.mdx";
		if (this.dataSource.has(portraitPath)) {
			portraitModel = this.mapViewer.loadModelMdx(portraitPath);
		}
		else {
			portraitModel = model;
		}
		return portraitModel;
	}

	protected abstract T createTypeData(War3ID key, GameObject row);
}
