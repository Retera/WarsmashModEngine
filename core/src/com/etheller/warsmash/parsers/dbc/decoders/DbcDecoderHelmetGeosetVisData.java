package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.Arrays;

import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderHelmetGeosetVisData.HelmetGeosetVisDataRecord;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderHelmetGeosetVisData implements DbcDecoder<HelmetGeosetVisDataRecord> {
	public static final DbcDecoderHelmetGeosetVisData INSTANCE = new DbcDecoderHelmetGeosetVisData();

	@Override
	public HelmetGeosetVisDataRecord readRecord(final BinaryReader reader) {
		return new HelmetGeosetVisDataRecord(reader);
	}

	public class HelmetGeosetVisDataRecord {

		private final int id; // uint32
		private final int[] defaultFlags = new int[32];
		private final int[] preferredFlags = new int[32];
		private final int[] hideFlags = new int[32];

		public HelmetGeosetVisDataRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			reader.readInt32Array(this.defaultFlags);
			reader.readInt32Array(this.preferredFlags);
			reader.readInt32Array(this.hideFlags);
		}

		@Override
		public String toString() {
			return "HelmetGeosetVisDataRecord [id=" + this.id + ", defaultFlags=" + Arrays.toString(this.defaultFlags)
					+ ", preferredFlags=" + Arrays.toString(this.preferredFlags) + ", hideFlags="
					+ Arrays.toString(this.hideFlags) + "]";
		}

	}

}
