package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderItemTypeData extends RenderWidgetTypeData<RenderItemType> {
	private static final String ITEM_FILE = "file"; // replaced from 'ifil'
	private static final String ITEM_MODEL_SCALE = "scale"; // replaced from 'isca'
	private static final String ITEM_RED = "colorR"; // replaced from 'iclr'
	private static final String ITEM_GREEN = "colorG"; // replaced from 'iclg'
	private static final String ITEM_BLUE = "colorB"; // replaced from 'iclb'

	private final DataTable miscData;

	public RenderItemTypeData(ObjectData itemObjectData, DataSource dataSource, War3MapViewer mapViewer,
			DataTable miscData) {
		super(itemObjectData, dataSource, mapViewer);
		this.miscData = miscData;
	}

	@Override
	protected RenderItemType createTypeData(War3ID key, GameObject row) {
		String path = row.getFieldAsString(ITEM_FILE, 0);

		if (path.toLowerCase().endsWith(".mdl") || path.toLowerCase().endsWith(".mdx")) {
			path = path.substring(0, path.length() - 4);
		}
		final MdxModel model = this.mapViewer.loadModelMdx(path);
		final MdxModel portraitModel = getPortraitModel(path, model);

		final Element misc = this.miscData.get("Misc");
		final String itemShadowFile = misc.getField("ItemShadowFile");
		final int itemShadowWidth = misc.getFieldValue("ItemShadowSize", 0);
		final int itemShadowHeight = misc.getFieldValue("ItemShadowSize", 1);
		final int itemShadowX = misc.getFieldValue("ItemShadowOffset", 0);
		final int itemShadowY = misc.getFieldValue("ItemShadowOffset", 1);

		RenderShadowType renderShadowType = null;
		if ((itemShadowFile != null) && !"_".equals(itemShadowFile)) {
			final float shadowX = itemShadowX;
			final float shadowY = itemShadowY;
			final float shadowWidth = itemShadowWidth;
			final float shadowHeight = itemShadowHeight;
			final String shadowTexture = getShadowTexture(itemShadowFile);
			if (this.dataSource.has(shadowTexture)) {
				renderShadowType = new RenderShadowType(shadowTexture, shadowX, shadowY, shadowWidth, shadowHeight);
			}
		}

		path += ".mdx";

		final float red = row.getFieldAsInteger(ITEM_RED, 0) / 255f;
		final float green = row.getFieldAsInteger(ITEM_GREEN, 0) / 255f;
		final float blue = row.getFieldAsInteger(ITEM_BLUE, 0) / 255f;

		final float modelScale = row.getFieldAsFloat(ITEM_MODEL_SCALE, 0);

		return new RenderItemType(model, portraitModel, renderShadowType, new Vector3(red, green, blue), modelScale);
	}

}
