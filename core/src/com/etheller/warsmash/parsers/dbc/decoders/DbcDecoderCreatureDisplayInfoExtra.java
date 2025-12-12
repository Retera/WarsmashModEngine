package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.Arrays;

import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCreatureDisplayInfoExtra.CreatureDisplayInfoExtraRecord;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderCreatureDisplayInfoExtra implements DbcDecoder<CreatureDisplayInfoExtraRecord> {
	public static final DbcDecoderCreatureDisplayInfoExtra INSTANCE = new DbcDecoderCreatureDisplayInfoExtra();

	@Override
	public CreatureDisplayInfoExtraRecord readRecord(final BinaryReader reader) {
		return new CreatureDisplayInfoExtraRecord(reader);
	}

	public class CreatureDisplayInfoExtraRecord {

		private final int id;
		private final int displayRaceId;
		private final int displaySexId;
		private final int skinId;
		private final int faceId;
		private final int hairStyleId;
		private final int hairColorId;
		private final int facialHairId;
		private final int[] npcItemDisplay = new int[10];
		private final int bakeName;

		public CreatureDisplayInfoExtraRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			this.displayRaceId = reader.readInt32();
			this.displaySexId = reader.readInt32();
			this.skinId = reader.readInt32();
			this.faceId = reader.readInt32();
			this.hairStyleId = reader.readInt32();
			this.hairColorId = reader.readInt32();
			this.facialHairId = reader.readInt32();
			reader.readInt32Array(this.npcItemDisplay);
			this.bakeName = reader.readInt32();
		}

		@Override
		public String toString() {
			return "CreatureDisplayItemInfoExtraRecord [id=" + this.id + ", displayRaceId=" + this.displayRaceId
					+ ", displaySexId=" + this.displaySexId + ", skinId=" + this.skinId + ", faceId=" + this.faceId
					+ ", hairStyleId=" + this.hairStyleId + ", hairColorId=" + this.hairColorId + ", facialHairId="
					+ this.facialHairId + ", npcItemDisplay=" + Arrays.toString(this.npcItemDisplay) + ", bakeName="
					+ this.bakeName + "]";
		}

		public int getId() {
			return this.id;
		}

		public int getDisplayRaceId() {
			return this.displayRaceId;
		}

		public int getDisplaySexId() {
			return this.displaySexId;
		}

		public int getSkinId() {
			return this.skinId;
		}

		public int getFaceId() {
			return this.faceId;
		}

		public int getHairStyleId() {
			return this.hairStyleId;
		}

		public int getHairColorId() {
			return this.hairColorId;
		}

		public int getFacialHairId() {
			return this.facialHairId;
		}

		public int[] getNpcItemDisplay() {
			return this.npcItemDisplay;
		}

		public int getBakeName() {
			return this.bakeName;
		}

	}

}
