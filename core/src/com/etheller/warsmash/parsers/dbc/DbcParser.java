package com.etheller.warsmash.parsers.dbc;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.util.BinaryReader;

public class DbcParser {
	private static final War3ID MAGIC_KEY = War3ID.fromString("WDBC");

	public static <T extends DbcRecord> void parse(final BinaryReader reader, final DbcDecoder<T> decoder,
			final DataTable table) {
		final DbcTable<T> parsedTable = DbcParser.<T>parse(reader, decoder);
		for (final DbcRecord record : parsedTable.getRecords()) {
			record.load(parsedTable.getStringMap(), table);
		}
	}

	public static <T> DbcTable<T> parse(final BinaryReader reader, final DbcDecoder<T> decoder) {
		final int magic = reader.readTag();
		if (magic != MAGIC_KEY.getValue()) {
			throw new IllegalArgumentException("DBC wrong magic: " + magic);
		}
		final long recordCount = reader.readUInt32();
		final long fieldsPerRecord = reader.readInt32();
		final long recordSize = reader.readUInt32();
		final long stringBlockSize = reader.readUInt32();
		final List<T> records = new ArrayList<>();
		for (long i = 0; i < recordCount; i++) {
			final int startPosition = reader.position();
			final int endPosition = (int) (startPosition + recordSize);
			records.add(decoder.readRecord(reader));
			final int positionAfterRead = reader.position();
			if (positionAfterRead != endPosition) {
				if (positionAfterRead > endPosition) {
					// NOTE this might be better handled if we just hand them a bytebuffer or stream
					// object with a limit
					throw new IllegalStateException("DBC decoder read too many bytes. Consumed "
							+ (positionAfterRead - startPosition) + " but was provided only " + recordSize);
				}
				reader.position(endPosition);
			}
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
		return new DbcTable<T>(stringMap, records);
	}
}
