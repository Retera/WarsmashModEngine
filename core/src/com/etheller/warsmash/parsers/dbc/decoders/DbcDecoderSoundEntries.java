package com.etheller.warsmash.parsers.dbc.decoders;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcRecord;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcDecoderSoundEntries implements DbcDecoder {

	@Override
	public long getRecordSize() {
		return 136;
	}

	@Override
	public DbcRecord readRecord(final BinaryReader reader) {
		return new SoundEntriesRecord(reader);
	}

	private static final class SoundEntriesRecord implements DbcRecord {
		private final War3ID id;
		private final int soundType;
		private final int nameRef;
		private final int[] fileRef = new int[10];
		private final int[] frequency = new int[10];
		private final int directoryBaseRef;
		private final float volume;
		private final float pitch;
		private final float pitchVariation;
		private final int priority;
		private final int channel;
		private final int flags;
		private final float minDistance;
		private final float maxDistance;
		private final float distanceCutoff;
		private final int eaxDef;

		public SoundEntriesRecord(final BinaryReader reader) {
			this.id = new War3ID(reader.readInt32());
			this.soundType = reader.readInt32();
			this.nameRef = reader.readInt32();
			for (int i = 0; i < this.fileRef.length; i++) {
				this.fileRef[i] = reader.readInt32();
			}
			for (int i = 0; i < this.frequency.length; i++) {
				this.frequency[i] = reader.readInt32();
			}
			this.directoryBaseRef = reader.readInt32();
			this.volume = reader.readFloat32();
			this.pitch = reader.readFloat32();
			this.pitchVariation = reader.readFloat32();
			this.priority = reader.readInt32();
			this.channel = reader.readInt32();
			this.flags = reader.readInt32();
			this.minDistance = reader.readFloat32();
			this.maxDistance = reader.readFloat32();
			this.distanceCutoff = reader.readFloat32();
			this.eaxDef = reader.readInt32();

		}

		@Override
		public void load(final LongMap<String> stringsTable, final DataTable output) {
			final String idString = this.id.toString();
			final String soundName = stringsTable.get(this.nameRef);
			final Element element = new Element(soundName, output);
			element.setField("SoundType", Integer.toString(this.soundType));
			element.setField("SoundName", soundName);
			System.err.println("SOUNDNAME: " + soundName);
			final StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < this.fileRef.length; i++) {
				final int fileRefAtIndex = this.fileRef[i];
				final String fileRefString = stringsTable.get(fileRefAtIndex);
				if (!fileRefString.isEmpty()) {
					if (!stringBuilder.isEmpty()) {
						stringBuilder.append(',');
					}
					stringBuilder.append(fileRefString);
				}
			}
			element.setField("FileNames", stringBuilder.toString());
			stringBuilder.setLength(0);
			for (int i = 0; i < this.frequency.length; i++) {
				final int frequencyAtIndex = this.frequency[i];
				stringBuilder.append(frequencyAtIndex);
				stringBuilder.append(',');
			}
			element.setField("Frequency", stringBuilder.toString());
			element.setField("DirectoryBase", stringsTable.get(this.directoryBaseRef));
			element.setField("Volume", Integer.toString((int) Math.ceil(this.volume * 127)));
			element.setField("Pitch", Float.toString(this.pitch));
			element.setField("PitchVariance", Float.toString(this.pitchVariation));
			element.setField("Priority", Integer.toString(this.priority));
			element.setField("Channel", Integer.toString(this.channel));
			final List<String> flags = new ArrayList<>();
			if ((this.flags & 0x0020) != 0) {
				flags.add("NODUPLICATES");
			}
			if ((this.flags & 0x0200) != 0) {
				flags.add("LOOPING");
			}
			if ((this.flags & 0x0400) != 0) {
				flags.add("VARY_PITCH");
			}
			if ((this.flags & 0x0800) != 0) {
				flags.add("VARY_VOLUME");
			}
			element.setField("Flags", flags);
			element.setField("MinDistance", Float.toString(this.minDistance));
			element.setField("MaxDistance", Float.toString(this.maxDistance));
			element.setField("DistanceCutoff", Float.toString(this.distanceCutoff));
			element.setField("EAXFlags", Integer.toString(this.eaxDef));
			output.put(soundName, element);
		}
	}

}
