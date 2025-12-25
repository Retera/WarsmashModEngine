package com.etheller.warsmash.parsers.wmo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.etheller.warsmash.datasources.SourcedData;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.PathSolver;

import mpq.ArchivedFile;
import mpq.ArchivedFileExtractor;
import mpq.ArchivedFileStream;
import mpq.HashLookup;
import mpq.MPQArchive;
import mpq.MPQException;

public class WmoMpqPortingModel extends WmoPortingModel2 {
	public WmoMpqPortingModel(final WmoPortingHandler handler, final ModelViewer viewer, final String extension,
			final PathSolver pathSolver, final String fetchUrl) {
		super(handler, viewer, extension, pathSolver, fetchUrl);
	}

	@Override
	public void load(final SourcedData src, final Object options) {
		final ArchivedFileExtractor extractor = new ArchivedFileExtractor();
		SeekableByteChannel sbc;
		final ByteBuffer dataBuffer;
		try {
			sbc = new SeekableInMemoryByteChannel(src.read().array());
			final MPQArchive mpqArchive = new MPQArchive(sbc);

			String internalName = this.fetchUrl;
			internalName = internalName.substring(internalName.replace("\\", "/").lastIndexOf('/') + 1);
			internalName = internalName.substring(0, internalName.lastIndexOf('.'));
			final ArchivedFile file = mpqArchive.lookupHash2(new HashLookup(internalName));
			final ArchivedFileStream stream = new ArchivedFileStream(sbc, extractor, file);

			dataBuffer = ByteBuffer.allocate(file.fileSize);
			while (stream.read(dataBuffer) != -1) {
				;
			}
			dataBuffer.flip();
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		catch (final MPQException e) {
			throw new RuntimeException(e);
		}

		super.load(new SourcedData() {

			@Override
			public ByteBuffer read() {
				return dataBuffer;
			}

			@Override
			public InputStream getResourceAsStream() {
				throw new UnsupportedOperationException();
			}
		}, options);
	}

}
