package com.etheller.warsmash.parsers.dbc.decoders;

import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCharHairGeosets.CharHairGeosetsRecord;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderCharHairGeosets implements DbcDecoder<CharHairGeosetsRecord> {
	public static final DbcDecoderCharHairGeosets INSTANCE = new DbcDecoderCharHairGeosets();

	@Override
	public CharHairGeosetsRecord readRecord(final BinaryReader reader) {
		return new CharHairGeosetsRecord(reader);
	}

	public class CharHairGeosetsRecord {
		private final int id; // uint32
		private final int raceID; // uint32
		private final int sexID; // uint32
		private final int variationID; // uint32
		private final int geosetID; // uint32
		private final int showscalp; // uint32

		public CharHairGeosetsRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			this.raceID = reader.readInt32();
			this.sexID = reader.readInt32();
			this.variationID = reader.readInt32();
			this.geosetID = reader.readInt32();
			this.showscalp = reader.readInt32();
		}

		@Override
		public String toString() {
			return "CharHairGeosetsRecord [id=" + this.id + ", raceID=" + this.raceID + ", sexID=" + this.sexID
					+ ", variationID=" + this.variationID + ", geosetID=" + this.geosetID + ", showscalp="
					+ this.showscalp + "]";
		}

	}

}
