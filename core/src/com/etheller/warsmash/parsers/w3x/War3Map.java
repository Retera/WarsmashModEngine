package com.etheller.warsmash.parsers.w3x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.MpqDataSource;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.custom.WTS;
import com.google.common.io.LittleEndianDataInputStream;

import mpq.MPQArchive;
import mpq.MPQException;

/**
 * Warcraft 3 map (W3X and W3M).
 */
public class War3Map implements DataSource {

	private CompoundDataSource dataSource;
	private MpqDataSource internalMpqContentsDataSource;

	public War3Map(final DataSource dataSource, final String mapFileName) {
		try {
			// Slightly complex. Here's the theory:
			// 1.) Copy map into RAM
			// 2.) Setup a Data Source that will read assets
			// from either the map or the game, giving the map priority.
			SeekableByteChannel sbc;
			try (InputStream mapStream = dataSource.getResourceAsStream(mapFileName)) {
				final byte[] mapData = IOUtils.toByteArray(mapStream);
				sbc = new SeekableInMemoryByteChannel(mapData);
				this.internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc), sbc);
				this.dataSource = new CompoundDataSource(Arrays.asList(dataSource, this.internalMpqContentsDataSource));
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}
	}

	public War3MapW3i readMapInformation() throws IOException {
		War3MapW3i mapInfo;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.w3i"))) {
			mapInfo = new War3MapW3i(stream);
		}
		return mapInfo;
	}

	public War3MapW3e readEnvironment() throws IOException {
		War3MapW3e environment;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.w3e"))) {
			environment = new War3MapW3e(stream);
		}
		return environment;
	}

	public War3MapWpm readPathing() throws IOException {
		War3MapWpm pathingMap;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.wpm"))) {
			pathingMap = new War3MapWpm(stream);
		}
		return pathingMap;
	}

	public War3MapDoo readDoodads() throws IOException {
		War3MapDoo doodadsFile;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.doo"))) {
			doodadsFile = new War3MapDoo(stream);
		}
		return doodadsFile;
	}

	public War3MapUnitsDoo readUnits() throws IOException {
		War3MapUnitsDoo unitsFile;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3mapUnits.doo"))) {
			unitsFile = new War3MapUnitsDoo(stream);
		}
		return unitsFile;
	}

	public Warcraft3MapObjectData readModifications() throws IOException {
		final Warcraft3MapObjectData changes = Warcraft3MapObjectData.load(this.dataSource, true);
		return changes;
	}

	public Warcraft3MapObjectData readModifications(final WTS preloadedWTS) throws IOException {
		final Warcraft3MapObjectData changes = Warcraft3MapObjectData.load(this.dataSource, true, preloadedWTS);
		return changes;
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) throws IOException {
		return this.dataSource.getResourceAsStream(filepath);
	}

	@Override
	public File getFile(final String filepath) throws IOException {
		return this.dataSource.getFile(filepath);
	}

	@Override
	public boolean has(final String filepath) {
		return this.dataSource.has(filepath);
	}

	@Override
	public ByteBuffer read(final String path) throws IOException {
		return this.dataSource.read(path);
	}

	@Override
	public Collection<String> getListfile() {
		return this.internalMpqContentsDataSource.getListfile();
	}

	@Override
	public void close() throws IOException {
		this.dataSource.close();
	}

	public CompoundDataSource getCompoundDataSource() {
		return this.dataSource;
	}
}
