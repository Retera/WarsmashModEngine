package com.etheller.warsmash.parsers.w3x;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.Checksum;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.etheller.warsmash.datasources.CompoundDataSource;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.FolderDataSource;
import com.etheller.warsmash.datasources.MpqDataSource;
import com.etheller.warsmash.parsers.w3x.doo.War3MapDoo;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapRuntimeObjectData;
import com.etheller.warsmash.parsers.w3x.unitsdoo.War3MapUnitsDoo;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3r.War3MapW3r;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.custom.WTS;
import com.google.common.io.LittleEndianDataInputStream;

import mpq.MPQArchive;
import mpq.MPQException;

/**
 * Warcraft 3 map (W3X and W3M).
 */
public class War3Map implements DataSource {

	private CompoundDataSource baseDataSource;
	private DataSource dataSource;
	private DataSource internalMpqContentsDataSource;

	public War3Map(final DataSource dataSource, final String mapFileName) {
		try {
			// Slightly complex. Here's the theory:
			// 1.) Copy map into RAM
			// 2.) Setup a Data Source that will read assets
			// from either the map or the game, giving the map priority.

			SeekableByteChannel sbc;
			sbc = new SeekableInMemoryByteChannel(dataSource.read(mapFileName).array());
			this.internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc), sbc);
			this.baseDataSource = new CompoundDataSource(Arrays.asList(dataSource, this.internalMpqContentsDataSource));
			this.dataSource = this.baseDataSource;
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}
	}

	public War3Map(final DataSource dataSource, final File mapFileName) {
		try {
			if (mapFileName.isDirectory()) {
				this.internalMpqContentsDataSource = new FolderDataSource(mapFileName.toPath());
			}
			else {
				// Slightly complex. Here's the theory:
				// 1.) Copy map into RAM
				// 2.) Setup a Data Source that will read assets
				// from either the map or the game, giving the map priority.
				SeekableByteChannel sbc;
				try (InputStream mapStream = new FileInputStream(mapFileName)) {
					final byte[] mapData = IOUtils.toByteArray(mapStream);
					sbc = new SeekableInMemoryByteChannel(mapData);
					this.internalMpqContentsDataSource = new MpqDataSource(new MPQArchive(sbc), sbc);
				}
			}
			this.baseDataSource = new CompoundDataSource(Arrays.asList(dataSource, this.internalMpqContentsDataSource));
			this.dataSource = this.baseDataSource;
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}
	}

	public MpqDataSource getInternalMpqContentsDataSource() {
		// TODO remove cast
		return (MpqDataSource) this.internalMpqContentsDataSource;
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

	public War3MapDoo readDoodads(final War3MapW3i war3MapW3i) throws IOException {
		War3MapDoo doodadsFile;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.doo"))) {
			doodadsFile = new War3MapDoo(stream, war3MapW3i);
		}
		return doodadsFile;
	}

	public War3MapUnitsDoo readUnits(final War3MapW3i war3MapW3i) throws IOException {
		War3MapUnitsDoo unitsFile;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3mapUnits.doo"))) {
			unitsFile = new War3MapUnitsDoo(stream, war3MapW3i);
		}
		return unitsFile;
	}

	public War3MapW3r readRegions() throws IOException {
		War3MapW3r unitsFile;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				this.dataSource.getResourceAsStream("war3map.w3r"))) {
			unitsFile = new War3MapW3r(stream);
		}
		return unitsFile;
	}

	public Warcraft3MapRuntimeObjectData readModifications() throws IOException {
		final Warcraft3MapRuntimeObjectData changes = Warcraft3MapRuntimeObjectData.load(this.dataSource, true);
		return changes;
	}

	public Warcraft3MapRuntimeObjectData readModifications(final WTS preloadedWTS) throws IOException {
		final Warcraft3MapRuntimeObjectData changes = Warcraft3MapRuntimeObjectData.load(this.dataSource, true,
				preloadedWTS);
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
	public File getDirectory(final String filepath) throws IOException {
		return this.dataSource.getDirectory(filepath);
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
		return this.dataSource.getListfile();
	}

	@Override
	public void close() throws IOException {
		this.dataSource.close();
	}

	public DataSource getCompoundDataSource() {
		return this.baseDataSource;
	}

	public void setDataSource(DataSource tilesetSource) {
		this.dataSource = tilesetSource;
	}

	public long computeChecksum(final Checksum checksum) {
		final SeekableByteChannel inputChannel = getInternalMpqContentsDataSource().getInputChannel();
		try {
			final ByteBuffer byteBuffer = ByteBuffer.allocate(8 * 1024);
			inputChannel.position(0);
			int result;
			byteBuffer.clear();
			checksum.reset();
			while ((result = inputChannel.read(byteBuffer)) != -1) {
				byteBuffer.flip();
				checksum.update(byteBuffer);
				byteBuffer.clear();
			}
			return checksum.getValue();
		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
