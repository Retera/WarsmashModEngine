package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderItemDisplayInfo.ItemDisplayInfoRecord;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderItemDisplayInfo implements DbcDecoder<ItemDisplayInfoRecord> {
	public static final DbcDecoderItemDisplayInfo INSTANCE = new DbcDecoderItemDisplayInfo();

	// size 100

	@Override
	public ItemDisplayInfoRecord readRecord(final BinaryReader reader) {
		return new ItemDisplayInfoRecord(reader);
	}

	public static final class ItemDisplayInfoRecord implements DbcRecord {
		private final int id;
		private final long[] modelName = new long[2];
		private final long[] modelTexture = new long[2];
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

		private ItemDisplayInfoRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
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
			final String idString = Integer.toString(this.id);
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

		@Override
		public String toString() {
			return "ItemDisplayInfoRecord [id=" + this.id + ", modelName=" + Arrays.toString(this.modelName)
					+ ", modelTexture=" + Arrays.toString(this.modelTexture) + ", inventoryIcon=" + this.inventoryIcon
					+ ", groundModel=" + this.groundModel + ", geosetGroup=" + Arrays.toString(this.geosetGroup)
					+ ", flags=" + this.flags + ", spellVisualId=" + this.spellVisualId + ", groupSoundIndex="
					+ this.groupSoundIndex + ", itemSize=" + this.itemSize + ", helmetGeosetVisId="
					+ this.helmetGeosetVisId + ", texture=" + Arrays.toString(this.texture) + ", itemVisual="
					+ this.itemVisual + "]";
		}

		public int getId() {
			return this.id;
		}

		public long[] getModelName() {
			return this.modelName;
		}

		public long[] getModelTexture() {
			return this.modelTexture;
		}

		public long getInventoryIcon() {
			return this.inventoryIcon;
		}

		public long getGroundModel() {
			return this.groundModel;
		}

		public long[] getGeosetGroup() {
			return this.geosetGroup;
		}

		public long getFlags() {
			return this.flags;
		}

		public long getSpellVisualId() {
			return this.spellVisualId;
		}

		public long getGroupSoundIndex() {
			return this.groupSoundIndex;
		}

		public long getItemSize() {
			return this.itemSize;
		}

		public long getHelmetGeosetVisId() {
			return this.helmetGeosetVisId;
		}

		public long[] getTexture() {
			return this.texture;
		}

		public long getItemVisual() {
			return this.itemVisual;
		}

	}

}
