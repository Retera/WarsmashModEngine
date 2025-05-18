package com.etheller.warsmash.parsers.wdt;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;

public class TestsWithWDT {

	public static void main(final String[] args) {
		System.out.println("Hello world");

		final DataSource dataSource = new FolderDataSourceDescriptor(
				"/home/etheller/WoW3_TheFrozenThrone/ModifiedMaps/Azeroth").createDataSource();
		try {
			final WdtMap map = new WdtMap(dataSource.read("Azeroth.wdt"));

			System.out.println(map.version);
			System.out.println(map.nDoodadNames);
			System.out.println(map.offsDoodadNames);
			System.out.println(map.nMapObjNames);
			System.out.println(map.offsMapObjNames);
			System.out.println(Arrays.toString(map.headerUnused));

			System.out.println(map.tileHeaders.size());
			int nonDuskwood = 0;
			int duskwood = 0;
			int tileHeaderTexMax = 0;
			int chunkLayerMax = 0;
			final Set<Integer> alphaSizes = new HashSet<>();
			for (final WdtMap.TileHeader header : map.tileHeaders) {
				boolean duskwoodish = false;
				for (final String texture : header.textureFileNames) {
					if (texture.toLowerCase().contains("duskwood")) {
						duskwoodish = true;
					}
				}
				if (duskwoodish) {
					duskwood++;
				}
				else {
					nonDuskwood++;
				}
				tileHeaderTexMax = Math.max(tileHeaderTexMax, header.textureFileNames.size());

				for (final Chunk chunk : header.chunks) {
					chunkLayerMax = Math.max(chunkLayerMax, chunk.getMapChunkLayers().size());

					if (chunk.getAlphaMaps() != null) {
						alphaSizes.add(chunk.getAlphaMaps().length);
					}
				}
			}
			System.out.println(duskwood);
			System.out.println(nonDuskwood);
			System.out.println("tile header tex max: " + tileHeaderTexMax);
			System.out.println("chunk layer max: " + chunkLayerMax);
			System.out.println(alphaSizes);

//			for(SMAreaInfo info: map.smAreaInfos) {
//				System.out.println(info);
//			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
