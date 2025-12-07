package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcRecord;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderItemDisplayInfo implements DbcDecoder {

	@Override
	public long getRecordSize() {
		return 164;
	}

	@Override
	public DbcRecord readRecord(final BinaryReader reader) {
		return new ItemDisplayInfoRecord(reader);
	}

	private static final class ItemDisplayInfoRecord implements DbcRecord {
		private final War3ID id;
		private final long[] modelName = new long[10];
		private final long[] modelTexture = new long[10];
		private final long inventoryIcon;
		private final long groundModel;
		private final long[] geosetGroup = new long[4];
		private final long flags;
		private final long spellVisualId;
		private final long groupSoundIndex;
		private final long itemSize;
		private final long helmetGeosetVisId;
		private final long[] texture = new long[8];
		private final long itemVisual;

		public ItemDisplayInfoRecord(final BinaryReader reader) {
			this.id = new War3ID(reader.readInt32());
			reader.readUInt32Array(this.modelName);
			reader.readUInt32Array(this.modelTexture);
			this.inventoryIcon = reader.readUInt32();
			this.groundModel = reader.readUInt32();
			reader.readUInt32Array(this.geosetGroup);
			this.flags = reader.readUInt32();
			this.spellVisualId = reader.readUInt32();
			this.groupSoundIndex = reader.readUInt32();
			this.itemSize = reader.readUInt32();
			this.helmetGeosetVisId = reader.readUInt32();
			reader.readUInt32Array(this.texture);
			this.itemVisual = reader.readUInt32();
		}

		@Override
		public void load(final LongMap<String> stringsTable, final DataTable output) {
			final String idString = this.id.toString();
			final String[] modelName = new String[this.modelName.length];
			for (int i = 0; i < this.modelName.length; i++) {
				modelName[i] = stringsTable.get(this.modelName[i]);
			}
			final String[] modelTexture = new String[this.modelTexture.length];
			for (int i = 0; i < this.modelTexture.length; i++) {
				modelTexture[i] = stringsTable.get(this.modelTexture[i]);
			}
			final String inventoryIcon = stringsTable.get(this.inventoryIcon);
			final String groundModel = stringsTable.get(this.groundModel);

			final String[] texture = new String[this.texture.length];
			for (int i = 0; i < this.texture.length; i++) {
				texture[i] = stringsTable.get(this.texture[i]);
			}
			final Element element = new Element(idString, output);
			element.setField("ModelName", Arrays.asList(modelName));
			element.setField("ModelTexture", Arrays.asList(modelTexture));
			element.setField("InventoryIcon", inventoryIcon);
			element.setField("GroundModel", groundModel);
			final List<String> geosetGroupData = new ArrayList<>();
			for (final long geosetGroupValue : this.geosetGroup) {
				geosetGroupData.add(Long.toString(geosetGroupValue));
			}
			element.setField("Flags", Long.toString(this.flags));
			element.setField("SpellVisualID", Long.toString(this.spellVisualId));
			element.setField("GroupSoundIndex", Long.toString(this.groupSoundIndex));
			element.setField("ItemSize", Long.toString(this.itemSize));
			element.setField("HelmetGeosetVisID", Long.toString(this.helmetGeosetVisId));
			element.setField("Texture", Arrays.asList(texture));
			element.setField("ItemVisual", Long.toString(this.itemVisual));
			output.put(idString, element);
		}
	}

}
