package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.Arrays;

import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCharSections.CharSectionsRecord;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderCharSections implements DbcDecoder<CharSectionsRecord> {
	public static final DbcDecoderCharSections INSTANCE = new DbcDecoderCharSections();

	@Override
	public CharSectionsRecord readRecord(final BinaryReader reader) {
		return new CharSectionsRecord(reader);
	}

	public class CharSectionsRecord {
		private final int id; // uint32
		private final int raceID; // uint32
		private final int sexID; // uint32
		private final int baseSection; // uint32
		private final int variationIndex; // uint32
		private final int colorIndex; // uint32
		private final int[] textureName = new int[3]; // stringref[3]
		private final int flags; // uint32

		public CharSectionsRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			this.raceID = reader.readInt32();
			this.sexID = reader.readInt32();
			this.baseSection = reader.readInt32();
			this.variationIndex = reader.readInt32();
			this.colorIndex = reader.readInt32();
			reader.readInt32Array(this.textureName);
			this.flags = reader.readInt32();
		}

		@Override
		public String toString() {
			return "CharSectionsRecord [id=" + this.id + ", raceID=" + this.raceID + ", sexID=" + this.sexID
					+ ", baseSection=" + this.baseSection + ", variationIndex=" + this.variationIndex + ", colorIndex="
					+ this.colorIndex + ", textureName=" + Arrays.toString(this.textureName) + ", flags=" + this.flags
					+ "]";
		}
	}

}
