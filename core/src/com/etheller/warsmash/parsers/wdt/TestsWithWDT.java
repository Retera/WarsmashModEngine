package com.etheller.warsmash.parsers.wdt;

import java.io.IOException;
import java.util.Arrays;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;

public class TestsWithWDT {

	public static void main(String[] args) {
		System.out.println("Hello world");

		DataSource dataSource = new FolderDataSourceDescriptor(
				"/home/etheller/WoW3_TheFrozenThrone/ModifiedMaps/Azeroth").createDataSource();
		try {
			WdtMap map = new WdtMap(dataSource.read("Azeroth.wdt"));

			System.out.println(map.version);
			System.out.println(map.nDoodadNames);
			System.out.println(map.offsDoodadNames);
			System.out.println(map.nMapObjNames);
			System.out.println(map.offsMapObjNames);
			System.out.println(Arrays.toString(map.headerUnused));
			
			System.out.println(map.tileHeaders.size());
			int nonDuskwood = 0;
			int duskwood = 0;
			for(WdtMap.TileHeader header: map.tileHeaders) {
				boolean duskwoodish = false;
				for(String texture: header.textureFileNames) {
					if(texture.toLowerCase().contains("duskwood")) {
						duskwoodish = true;
					}
				}
				if(duskwoodish) {
					duskwood ++ ;
				} else {
					nonDuskwood ++;
				}
			}
			System.out.println(duskwood);
			System.out.println(nonDuskwood);
			
//			for(SMAreaInfo info: map.smAreaInfos) {
//				System.out.println(info);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
