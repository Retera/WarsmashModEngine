package com.etheller.warsmash.parsers.wmo;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import mpq.ArchivedFile;
import mpq.ArchivedFileExtractor;
import mpq.ArchivedFileStream;
import mpq.HashLookup;
import mpq.MPQArchive;
import mpq.MPQException;

public class TestsWithWMO {

	public static void main(final String[] args) {

		final File sampleWMO = new File(
				"/home/eric/Games/WoW3_TheFrozenThrone/wow/Files/Data/World/wmo/Kalimdor/Buildings/OrcGreatHall/OrcGreatHall.wmo.MPQ");

		final ArchivedFileExtractor extractor = new ArchivedFileExtractor();
		SeekableByteChannel sbc;
		final ByteBuffer dataBuffer;
		try {
			sbc = Files.newByteChannel(sampleWMO.toPath(), EnumSet.of(StandardOpenOption.READ));
			final MPQArchive mpqArchive = new MPQArchive(sbc);

			String internalName = sampleWMO.getName();
			internalName = internalName.substring(0, internalName.lastIndexOf('.'));
			System.out.println(internalName);
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

		try {
			final WorldModelObject wmo = new WorldModelObject(dataBuffer);

			System.out.println(wmo.version);

			System.out.println(wmo.getHeaders().getTextureFileNames());
			System.out.println(wmo.getHeaders().getTextureFileNamesOffsetLookup());

			System.out.println("getnTextures: " + wmo.getHeaders().getnTextures());
			System.out.println("getnGroups: " + wmo.getHeaders().getnGroups());
			System.out.println("getnPortals: " + wmo.getHeaders().getnPortals());
			System.out.println("getnLights: " + wmo.getHeaders().getnLights());
			System.out.println(wmo.getHeaders().getTextureFileNames().size());

			for (final WmoMaterial material : wmo.getHeaders().getMaterials()) {
				System.out.println("Material:");
				System.out.println(" - DiffuseNameIndex: " + material.getDiffuseNameIndex());
			}

			for (final ModelObjectGroup group : wmo.getGroups()) {
				System.out.println("Group: " + group.getGroupName());
				for (final GroupBatch batch : group.getBatches()) {
					System.out.println("Batch:");
					System.out.println(" - TextureID: " + batch.getTexture());
					System.out.println(" - MaterialID: " + batch.getMaterialId());
				}
			}

		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
