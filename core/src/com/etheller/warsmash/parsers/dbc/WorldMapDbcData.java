package com.etheller.warsmash.parsers.dbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.datasources.DataSource;

/**
 * The 0.5.3 alpha's world-map layout tables, read from DBFilesClient. The alpha
 * world map is tile-rect based: {@code WorldMapContinent.dbc} gives each
 * continent's span of 64x64 WDT tile indices, and {@code WorldMapArea.dbc} gives
 * one tile-rect per zone (plus which continent map it belongs to and the art
 * folder name under {@code Interface\WorldMap\}). {@code Map.dbc} links the dbc
 * map ids to the WDT directory names ("Azeroth", "Kalimdor") so a loaded WDT can
 * be matched to its continent.
 *
 * <p>
 * All rects are in WDT tile coordinates: x = tile column (increases eastward,
 * i.e. as WoW worldY decreases), y = tile row (increases southward, i.e. as WoW
 * worldX decreases), both inclusive of their upper index (a zone spanning
 * columns 28..36 is (36-28)+1 = 9 tiles wide).
 */
public class WorldMapDbcData {
	public static final float TILE_SIZE = 533.33333f;

	public final List<Continent> continents = new ArrayList<>();
	public final List<Zone> zones = new ArrayList<>();
	/** AreaTable.dbc rows by area id (the id the terrain's MCNK chunks carry). */
	public final Map<Integer, Area> areas = new HashMap<>();
	private final Map<Integer, Area> areasByKey = new HashMap<>();

	/**
	 * One AreaTable.dbc row. In 0.5.3 the table is [id, key, ?, parentKey, ...,
	 * name(13), ...]: {@code key} is a packed (zoneIndex << 16 | subzoneIndex)
	 * value and a subzone's {@code parentKey} is its zone's {@code key} (0 for a
	 * top-level zone).
	 */
	public static final class Area {
		public final int id;
		public final int key;
		public final int parentKey;
		public final String name;

		private Area(final int id, final int key, final int parentKey, final String name) {
			this.id = id;
			this.key = key;
			this.parentKey = parentKey;
			this.name = name;
		}
	}

	/**
	 * The zone name for a terrain chunk's area id (the top-level zone if the area
	 * is a subzone), or null when the id is unknown.
	 */
	public String getZoneNameForAreaId(final int areaId) {
		final Area area = this.areas.get(areaId);
		if (area == null) {
			return null;
		}
		if (area.parentKey != 0) {
			final Area parent = this.areasByKey.get(area.parentKey);
			if (parent != null) {
				return parent.name;
			}
		}
		return area.name;
	}

	/** The subzone name for a terrain chunk's area id, or "" when it is a top-level zone / unknown. */
	public String getSubZoneNameForAreaId(final int areaId) {
		final Area area = this.areas.get(areaId);
		if ((area == null) || (area.parentKey == 0)) {
			return "";
		}
		return area.name;
	}

	public static final class Continent {
		public final int mapId;
		/** WDT directory / world map art name from Map.dbc, e.g. "Azeroth". */
		public final String mapName;
		public final int tileX1, tileX2, tileY1, tileY2;

		private Continent(final int mapId, final String mapName, final int tileX1, final int tileX2, final int tileY1,
				final int tileY2) {
			this.mapId = mapId;
			this.mapName = mapName;
			this.tileX1 = tileX1;
			this.tileX2 = tileX2;
			this.tileY1 = tileY1;
			this.tileY2 = tileY2;
		}
	}

	public static final class Zone {
		public final int mapId;
		public final int areaId;
		/** Art folder name under Interface\WorldMap\, e.g. "Elwynn". */
		public final String name;
		public final int tileX1, tileX2, tileY1, tileY2;

		private Zone(final int mapId, final int areaId, final String name, final int tileX1, final int tileX2,
				final int tileY1, final int tileY2) {
			this.mapId = mapId;
			this.areaId = areaId;
			this.name = name;
			this.tileX1 = tileX1;
			this.tileX2 = tileX2;
			this.tileY1 = tileY1;
			this.tileY2 = tileY2;
		}

		public boolean containsTile(final float tileX, final float tileY) {
			return (tileX >= this.tileX1) && (tileX < (this.tileX2 + 1)) && (tileY >= this.tileY1)
					&& (tileY < (this.tileY2 + 1));
		}
	}

	/**
	 * Loads the tables; missing or unreadable dbc files simply leave the lists
	 * empty (callers fall back to a mapless world map).
	 */
	public static WorldMapDbcData load(final DataSource dataSource) {
		final WorldMapDbcData data = new WorldMapDbcData();
		try {
			final DbcFile mapDbc = DbcFile.read(dataSource, "DBFilesClient\\Map.dbc");
			final DbcFile continentDbc = DbcFile.read(dataSource, "DBFilesClient\\WorldMapContinent.dbc");
			final DbcFile areaDbc = DbcFile.read(dataSource, "DBFilesClient\\WorldMapArea.dbc");
			final DbcFile areaTableDbc = DbcFile.read(dataSource, "DBFilesClient\\AreaTable.dbc");
			if (areaTableDbc != null) {
				for (int i = 0; i < areaTableDbc.recordCount; i++) {
					final Area area = new Area(areaTableDbc.getInt(i, 0), areaTableDbc.getInt(i, 1),
							areaTableDbc.getInt(i, 3), areaTableDbc.getString(i, 13));
					data.areas.put(area.id, area);
					data.areasByKey.put(area.key, area);
				}
			}
			if ((mapDbc == null) || (continentDbc == null) || (areaDbc == null)) {
				return data;
			}
			// Map.dbc: [id, internalName(str), ...]
			// WorldMapContinent.dbc: [id, mapId, tileX1, tileX2, tileY1, tileY2, f, f]
			for (int i = 0; i < continentDbc.recordCount; i++) {
				final int mapId = continentDbc.getInt(i, 1);
				String mapName = null;
				for (int m = 0; m < mapDbc.recordCount; m++) {
					if (mapDbc.getInt(m, 0) == mapId) {
						mapName = mapDbc.getString(m, 1);
						break;
					}
				}
				if (mapName == null) {
					continue;
				}
				data.continents.add(new Continent(mapId, mapName, continentDbc.getInt(i, 2), continentDbc.getInt(i, 3),
						continentDbc.getInt(i, 4), continentDbc.getInt(i, 5)));
			}
			// WorldMapArea.dbc: [id, mapId, areaId, tileX1, tileX2, tileY1, tileY2, name(str)]
			for (int i = 0; i < areaDbc.recordCount; i++) {
				final int areaId = areaDbc.getInt(i, 2);
				if (areaId == 0) {
					// The areaId-0 rows are the whole-continent rects ("Azeroth",
					// "Kalimdor"), not zones; keeping them would make every hover/click
					// on the continent view match one giant zone.
					continue;
				}
				data.zones.add(new Zone(areaDbc.getInt(i, 1), areaId, areaDbc.getString(i, 7),
						areaDbc.getInt(i, 3), areaDbc.getInt(i, 4), areaDbc.getInt(i, 5), areaDbc.getInt(i, 6)));
			}
		}
		catch (final Exception exc) {
			System.err.println("Unable to load world map dbc data: " + exc);
		}
		return data;
	}

	public Continent getContinentByMapName(final String mapName) {
		for (final Continent continent : this.continents) {
			if (continent.mapName.equalsIgnoreCase(mapName)) {
				return continent;
			}
		}
		return null;
	}

	/**
	 * The zone rect best matching a tile position among the given zones. The dbc
	 * rects are coarse bounding boxes that overlap at the borders (Hillsbrad's box
	 * overlaps Arathi's and Alterac's, Stranglethorn's overlaps Blasted Lands'), so
	 * of the rects containing the point the one whose center is nearest wins,
	 * rather than whichever happens to come first in the table. Returns the
	 * 0-based index into {@code zones}, or -1.
	 */
	public static int findBestZoneIndex(final List<Zone> zones, final float tileX, final float tileY) {
		int best = -1;
		float bestDistance = Float.MAX_VALUE;
		for (int i = 0; i < zones.size(); i++) {
			final Zone zone = zones.get(i);
			if (!zone.containsTile(tileX, tileY)) {
				continue;
			}
			final float centerX = (zone.tileX1 + zone.tileX2 + 1) / 2f;
			final float centerY = (zone.tileY1 + zone.tileY2 + 1) / 2f;
			// Normalize by the rect's own extents so a big zone's center isn't
			// penalized against a small neighbor sitting at its edge.
			final float dx = (tileX - centerX) / ((zone.tileX2 - zone.tileX1) + 1);
			final float dy = (tileY - centerY) / ((zone.tileY2 - zone.tileY1) + 1);
			final float distance = (dx * dx) + (dy * dy);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = i;
			}
		}
		return best;
	}

	public List<Zone> getZonesForMapId(final int mapId) {
		final List<Zone> result = new ArrayList<>();
		for (final Zone zone : this.zones) {
			if (zone.mapId == mapId) {
				result.add(zone);
			}
		}
		return result;
	}

	/** Minimal WDBC reader (header + int/float cells + string block). */
	private static final class DbcFile {
		private final ByteBuffer records;
		private final byte[] stringBlock;
		private final int recordCount;
		private final int fieldCount;
		private final int recordSize;

		private DbcFile(final ByteBuffer records, final byte[] stringBlock, final int recordCount, final int fieldCount,
				final int recordSize) {
			this.records = records;
			this.stringBlock = stringBlock;
			this.recordCount = recordCount;
			this.fieldCount = fieldCount;
			this.recordSize = recordSize;
		}

		public int getInt(final int record, final int field) {
			return this.records.getInt((record * this.recordSize) + (field * 4));
		}

		public String getString(final int record, final int field) {
			int offset = getInt(record, field);
			if ((offset < 0) || (offset >= this.stringBlock.length)) {
				return "";
			}
			final StringBuilder sb = new StringBuilder();
			while ((offset < this.stringBlock.length) && (this.stringBlock[offset] != 0)) {
				sb.append((char) (this.stringBlock[offset] & 0xFF));
				offset++;
			}
			return sb.toString();
		}

		public static DbcFile read(final DataSource dataSource, final String path) throws IOException {
			try (InputStream stream = dataSource.getResourceAsStream(path)) {
				if (stream == null) {
					return null;
				}
				final byte[] bytes = stream.readAllBytes();
				final ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
				if ((buffer.remaining() < 20) || (buffer.getInt(0) != (('C' << 24) | ('B' << 16) | ('D' << 8) | 'W'))) {
					return null;
				}
				final int recordCount = buffer.getInt(4);
				final int fieldCount = buffer.getInt(8);
				final int recordSize = buffer.getInt(12);
				final int stringSize = buffer.getInt(16);
				final int recordsEnd = 20 + (recordCount * recordSize);
				if ((recordsEnd + stringSize) > bytes.length) {
					return null;
				}
				final ByteBuffer records = ByteBuffer.wrap(bytes, 20, recordCount * recordSize).slice()
						.order(ByteOrder.LITTLE_ENDIAN);
				final byte[] stringBlock = new byte[stringSize];
				System.arraycopy(bytes, recordsEnd, stringBlock, 0, stringSize);
				return new DbcFile(records, stringBlock, recordCount, fieldCount, recordSize);
			}
		}
	}
}
