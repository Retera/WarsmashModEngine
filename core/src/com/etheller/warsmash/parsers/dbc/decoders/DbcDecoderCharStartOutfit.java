package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCharStartOutfit.CharStartOutfitRecord;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderCharStartOutfit implements DbcDecoder<CharStartOutfitRecord> {
	public static final DbcDecoderCharStartOutfit INSTANCE = new DbcDecoderCharStartOutfit();

	// size 152

	@Override
	public CharStartOutfitRecord readRecord(final BinaryReader reader) {
		return new CharStartOutfitRecord(reader);
	}

	public static final class CharStartOutfitRecord implements DbcRecord {
		private final int id;
		private final short raceId;
		private final short classId;
		private final short sexId;
		private final short outfitId;
		private final long[] itemId = new long[12];
		private final long[] displayItemId = new long[12];
		private final long[] inventoryType = new long[12];

		private CharStartOutfitRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			this.raceId = reader.readUInt8();
			this.classId = reader.readUInt8();
			this.sexId = reader.readUInt8();
			this.outfitId = reader.readUInt8();
			reader.readUInt32Array(this.itemId);
			reader.readUInt32Array(this.displayItemId);
			reader.readUInt32Array(this.inventoryType);
		}

		private static List<String> convert(final long[] data) {
			final List<String> convertedData = new ArrayList<>();
			for (final long value : data) {
				convertedData.add(Long.toString(value));
			}
			return convertedData;
		}

		@Override
		public void load(final LongMap<String> stringsTable, final DataTable output) {
			final String idString = Integer.toString(this.classId);

			final Element element = new Element(idString, output);
			element.setField("RaceID", Short.toString(this.raceId));
			element.setField("ClassID", Short.toString(this.classId));
			element.setField("SexID", Short.toString(this.sexId));
			element.setField("OutfitID", Short.toString(this.outfitId));

			element.setField("ItemID", convert(this.itemId));
			element.setField("DisplayItemID", convert(this.displayItemId));
			element.setField("InventoryType", convert(this.inventoryType));
			output.put(idString, element);
		}

		@Override
		public String toString() {
			return "CharStartOutfitRecord [id=" + this.id + ", raceId=" + this.raceId + ", classId=" + this.classId
					+ ", sexId=" + this.sexId + ", outfitId=" + this.outfitId + ", itemId="
					+ Arrays.toString(this.itemId) + ", displayItemId=" + Arrays.toString(this.displayItemId)
					+ ", inventoryType=" + Arrays.toString(this.inventoryType) + "]";
		}
	}

}
