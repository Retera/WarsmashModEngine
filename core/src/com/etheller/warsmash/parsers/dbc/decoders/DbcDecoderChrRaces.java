package com.etheller.warsmash.parsers.dbc.decoders;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderChrRaces.ChrRacesRecord;
import com.etheller.warsmash.units.DataTable;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderChrRaces implements DbcDecoder<ChrRacesRecord> {
	public static final DbcDecoderChrRaces INSTANCE = new DbcDecoderChrRaces();

	// size 100

	@Override
	public ChrRacesRecord readRecord(final BinaryReader reader) {
		return new ChrRacesRecord(reader);
	}

	public class ChrRacesRecord implements DbcRecord {
		private int id; // uint32_t
		private int flags; // uint32_t
		private int factionID; // uint32_t
		private int maleDisplayId; // uint32_t
		private int femaleDisplayId; // uint32_t
		private int clientPrefix; // stringref (likely an index or reference)
		private float mountScale; // float
		private int baseLanguage; // uint32_t
		private int creatureType; // uint32_t
		private int loginEffectSpellID; // uint32_t
		private int combatStunSpellID; // uint32_t
		private int resSicknessSpellID; // uint32_t
		private int splashSoundID; // uint32_t
		private int startingTaxiNodes; // uint32_t
		private int clientFileString; // stringref (likely an index or reference)
		private int cinematicSequenceID; // uint32_t
		private int nameLang; // langstringref (likely an index or reference)

		// Constructor that reads the data from a BinaryReader (like in your example)
		public ChrRacesRecord(final BinaryReader reader) {
			this.id = reader.readInt32();
			this.flags = reader.readInt32();
			this.factionID = reader.readInt32();
			this.maleDisplayId = reader.readInt32();
			this.femaleDisplayId = reader.readInt32();
			this.clientPrefix = reader.readInt32();
			this.mountScale = reader.readFloat32();
			this.baseLanguage = reader.readInt32();
			this.creatureType = reader.readInt32();
			this.loginEffectSpellID = reader.readInt32();
			this.combatStunSpellID = reader.readInt32();
			this.resSicknessSpellID = reader.readInt32();
			this.splashSoundID = reader.readInt32();
			this.startingTaxiNodes = reader.readInt32();
			this.clientFileString = reader.readInt32();
			this.cinematicSequenceID = reader.readInt32();
			this.nameLang = reader.readInt32();
		}

		// Getters and setters
		public int getId() {
			return this.id;
		}

		public void setId(final int id) {
			this.id = id;
		}

		public int getFlags() {
			return this.flags;
		}

		public void setFlags(final int flags) {
			this.flags = flags;
		}

		public int getFactionID() {
			return this.factionID;
		}

		public void setFactionID(final int factionID) {
			this.factionID = factionID;
		}

		public int getMaleDisplayId() {
			return this.maleDisplayId;
		}

		public void setMaleDisplayId(final int maleDisplayId) {
			this.maleDisplayId = maleDisplayId;
		}

		public int getFemaleDisplayId() {
			return this.femaleDisplayId;
		}

		public void setFemaleDisplayId(final int femaleDisplayId) {
			this.femaleDisplayId = femaleDisplayId;
		}

		public int getClientPrefix() {
			return this.clientPrefix;
		}

		public void setClientPrefix(final int clientPrefix) {
			this.clientPrefix = clientPrefix;
		}

		public float getMountScale() {
			return this.mountScale;
		}

		public void setMountScale(final float mountScale) {
			this.mountScale = mountScale;
		}

		public int getBaseLanguage() {
			return this.baseLanguage;
		}

		public void setBaseLanguage(final int baseLanguage) {
			this.baseLanguage = baseLanguage;
		}

		public int getCreatureType() {
			return this.creatureType;
		}

		public void setCreatureType(final int creatureType) {
			this.creatureType = creatureType;
		}

		public int getLoginEffectSpellID() {
			return this.loginEffectSpellID;
		}

		public void setLoginEffectSpellID(final int loginEffectSpellID) {
			this.loginEffectSpellID = loginEffectSpellID;
		}

		public int getCombatStunSpellID() {
			return this.combatStunSpellID;
		}

		public void setCombatStunSpellID(final int combatStunSpellID) {
			this.combatStunSpellID = combatStunSpellID;
		}

		public int getResSicknessSpellID() {
			return this.resSicknessSpellID;
		}

		public void setResSicknessSpellID(final int resSicknessSpellID) {
			this.resSicknessSpellID = resSicknessSpellID;
		}

		public int getSplashSoundID() {
			return this.splashSoundID;
		}

		public void setSplashSoundID(final int splashSoundID) {
			this.splashSoundID = splashSoundID;
		}

		public int getStartingTaxiNodes() {
			return this.startingTaxiNodes;
		}

		public void setStartingTaxiNodes(final int startingTaxiNodes) {
			this.startingTaxiNodes = startingTaxiNodes;
		}

		public int getClientFileString() {
			return this.clientFileString;
		}

		public void setClientFileString(final int clientFileString) {
			this.clientFileString = clientFileString;
		}

		public int getCinematicSequenceID() {
			return this.cinematicSequenceID;
		}

		public void setCinematicSequenceID(final int cinematicSequenceID) {
			this.cinematicSequenceID = cinematicSequenceID;
		}

		public int getNameLang() {
			return this.nameLang;
		}

		public void setNameLang(final int nameLang) {
			this.nameLang = nameLang;
		}

		@Override
		public void load(final LongMap<String> stringsTable, final DataTable output) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "ChrRacesRecord [id=" + this.id + ", flags=" + this.flags + ", factionID=" + this.factionID
					+ ", maleDisplayId=" + this.maleDisplayId + ", femaleDisplayId=" + this.femaleDisplayId
					+ ", clientPrefix=" + this.clientPrefix + ", mountScale=" + this.mountScale + ", baseLanguage="
					+ this.baseLanguage + ", creatureType=" + this.creatureType + ", loginEffectSpellID="
					+ this.loginEffectSpellID + ", combatStunSpellID=" + this.combatStunSpellID
					+ ", resSicknessSpellID=" + this.resSicknessSpellID + ", splashSoundID=" + this.splashSoundID
					+ ", startingTaxiNodes=" + this.startingTaxiNodes + ", clientFileString=" + this.clientFileString
					+ ", cinematicSequenceID=" + this.cinematicSequenceID + ", nameLang=" + this.nameLang + "]";
		}

	}
}
