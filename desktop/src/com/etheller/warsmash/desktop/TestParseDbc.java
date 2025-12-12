package com.etheller.warsmash.desktop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.dbc.DbcDecoder;
import com.etheller.warsmash.parsers.dbc.DbcParser;
import com.etheller.warsmash.parsers.dbc.DbcTable;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCharStartOutfit;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCharStartOutfit.CharStartOutfitRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderChrRaces;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderChrRaces.ChrRacesRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCreatureDisplayInfoExtra;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderCreatureDisplayInfoExtra.CreatureDisplayInfoExtraRecord;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderItemDisplayInfo;
import com.etheller.warsmash.parsers.dbc.decoders.DbcDecoderItemDisplayInfo.ItemDisplayInfoRecord;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.WorldEditStrings;
import com.hiveworkshop.rms.util.BinaryReader;

public class TestParseDbc {

	public static void main(final String[] args) {
		final DataTable iniTable = DesktopLauncher.loadWarsmashIni("warsmash.ini");
		final DataSource dataSource = WarsmashGdxMapScreen.parseDataSources(iniTable);

		final WorldEditStrings worldEditStrings = new WorldEditStrings(dataSource);
		final DataTable myTable = new DataTable(worldEditStrings);
		final DbcTable<CharStartOutfitRecord> startOutfitTable = parseTable(dataSource,
				"DBFilesClient\\CharStartOutfit.dbc", DbcDecoderCharStartOutfit.INSTANCE);

		final DbcTable<ChrRacesRecord> racesTable = parseTable(dataSource, "DBFilesClient\\ChrRaces.dbc",
				DbcDecoderChrRaces.INSTANCE);

//		final DbcTable<CharSectionsRecord> sectionsTable = parseTable(dataSource, "DBFilesClient\\CharSections.dbc",
//				DbcDecoderCharSections.INSTANCE);

		final DbcTable<ItemDisplayInfoRecord> itemDisplayInfoTable = parseTable(dataSource,
				"DBFilesClient\\ItemDisplayInfo.dbc", DbcDecoderItemDisplayInfo.INSTANCE);

		final DbcTable<CreatureDisplayInfoExtraRecord> creatureDisplayInfoExtra = parseTable(dataSource,
				"DBFilesClient\\CreatureDisplayInfoExtra.dbc", DbcDecoderCreatureDisplayInfoExtra.INSTANCE);

		for (final CharStartOutfitRecord record : startOutfitTable.getRecords()) {
			System.out.println(record.toString());
		}

		CreatureDisplayInfoExtraRecord npcNightElf = null;
		final IntArray[] slotToDisplays = new IntArray[10];
		final List<String>[] slotToDisplayTexts = new ArrayList[10];
		for (int i = 0; i < slotToDisplays.length; i++) {
			slotToDisplays[i] = new IntArray();
			slotToDisplayTexts[i] = new ArrayList<>();
		}
		for (final var record : creatureDisplayInfoExtra.getRecords()) {
			final int[] npcItemDisplay = record.getNpcItemDisplay();

			final StringBuilder bigHex = new StringBuilder();
			for (final int i : npcItemDisplay) {
				bigHex.append(Integer.toHexString(i));
			}
			final String string = bigHex.toString();
			if (string.toLowerCase().equals("b733 a8c5 5ab1 8fcb 892d 29a8 1bc0 064f")) {
				System.out.println(record);
			}

			final LongMap<String> stringMap = creatureDisplayInfoExtra.getStringMap();
			final String bakedName = stringMap.get(record.getBakeName());
			if (bakedName.toLowerCase().startsWith("b733a8c55ab18fcb892d29a81bc0064f")) {
				System.out.println(record);
				npcNightElf = record;
			}
			for (int slot = 0; slot < record.getNpcItemDisplay().length; slot++) {
				final int itemId = record.getNpcItemDisplay()[slot];
				if (itemId != 0) {
					slotToDisplays[slot].add(itemId);
				}
			}
		}
		final IntMap<ItemDisplayInfoRecord> keyedRecords = new IntMap<>();
		for (final var record : itemDisplayInfoTable.getRecords()) {
			keyedRecords.put(record.getId(), record);
			boolean match = false;
			for (final int id : npcNightElf.getNpcItemDisplay()) {
				if (id == record.getId()) {
					match = true;
				}
			}
			if (match) {
				System.out.println(record);
				final long inventoryIcon = record.getInventoryIcon();
				if (inventoryIcon != 0) {
					System.out.println("InventoryIcon: " + itemDisplayInfoTable.getStringMap().get(inventoryIcon));
				}
				final long groundModel = record.getGroundModel();
				if (groundModel != 0) {
					System.out.println("GroundModel: " + itemDisplayInfoTable.getStringMap().get(groundModel));
				}

			}
		}
		final LongMap<String> itemDisplayInfoStrings = itemDisplayInfoTable.getStringMap();
		for (int slot = 0; slot < slotToDisplayTexts.length; slot++) {
			final IntArray displayIds = slotToDisplays[slot];
			final List<String> displayTextsAtSlot = slotToDisplayTexts[slot];
			for (int k = 0; k < displayIds.size; k++) {
				final int id = displayIds.get(k);
				final ItemDisplayInfoRecord itemDisplayInfoRecord = keyedRecords.get(id);
				final long inventoryIcon = itemDisplayInfoRecord.getInventoryIcon();
				if (inventoryIcon != 0) {
					final String string = itemDisplayInfoStrings.get(inventoryIcon);
					displayTextsAtSlot.add(string);
				}
				final long groundModel = itemDisplayInfoRecord.getGroundModel();
				if (groundModel != 0) {
					final String string = itemDisplayInfoStrings.get(groundModel);
					displayTextsAtSlot.add(string);
				}
			}
			System.out.println(displayTextsAtSlot.toString().substring(0, 1024));
		}

//		for (final var record : racesTable.getRecords()) {
//			System.out.println(record);
//			System.out.println(racesTable.getStringMap().get(record.getClientFileString()));
//		}
//
//		for (final var record : itemDisplayInfoTable.getRecords()) {
//			System.out.println(record);
//		}
//
//		for (final var record : sectionsTable.getRecords()) {
//			System.out.println(record);
//		}

//		System.out.println(myTable.keySet().size());
//		for (final String key : myTable.keySet()) {
//			final Element x = myTable.get(key);
//			System.out.println(key + ":");
//			for (final String fieldKey : x.keySet()) {
//				System.out.println(" - " + fieldKey + ": " + x.getField(fieldKey));
//			}
//		}
	}

	private static <T> DbcTable<T> parseTable(final DataSource dataSource, final String dbcFilePath,
			final DbcDecoder<T> decoder) {
		final DbcTable<T> dbcTable;
		try {
			dbcTable = DbcParser.parse(new BinaryReader(dataSource.read(dbcFilePath)), decoder);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return dbcTable;
	}

}
