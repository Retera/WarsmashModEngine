package com.etheller.warsmash.parsers.dbc;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcParser {
	private static final War3ID MAGIC_KEY = War3ID.fromString("WDBC");

	public static void parse(final BinaryReader reader, final DbcDecoder decoder, final DataTable table) {
		final int magic = reader.readTag();
		if (magic != MAGIC_KEY.getValue()) {
			throw new IllegalArgumentException("DBC wrong magic: " + magic);
		}
		final long recordCount = reader.readUInt32();
		final long fieldsPerRecord = reader.readInt32();
		final long recordSize = reader.readUInt32();
		final long stringBlockSize = reader.readUInt32();
		final long decoderRecordSize = decoder.getRecordSize();
		if (recordSize != decoderRecordSize) {
			throw new IllegalArgumentException(
					"Bad DBC decoder, record size mismatch: " + recordSize + " != " + decoderRecordSize);
		}
		final List<DbcRecord> records = new ArrayList<>();
		for (long i = 0; i < recordCount; i++) {
			records.add(decoder.readRecord(reader));
		}
		final LongMap<String> stringMap = new LongMap<>();
		final StringBuilder stringEntryBuilder = new StringBuilder();
		long lastKey = 0;
		for (long i = 0; i < stringBlockSize; i++) {
			final byte nextByte = reader.readInt8();
			if (nextByte == 0) {
				stringMap.put(lastKey, stringEntryBuilder.toString());
				stringEntryBuilder.setLength(0);
				lastKey = i + 1;
			}
			else {
				stringEntryBuilder.append((char) nextByte);
			}
		}
		for (final DbcRecord record : records) {
			record.load(stringMap, table);
		}
	}
}
