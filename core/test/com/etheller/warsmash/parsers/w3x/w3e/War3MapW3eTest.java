package com.etheller.warsmash.parsers.w3x.w3e;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class War3MapW3eTest {

	@Test
	void testWoWReforged() throws IOException {
		War3MapW3e mapInfo;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				getClass().getClassLoader().getResourceAsStream("wowr_w3x/war3map.w3e"))) {
			mapInfo = new War3MapW3e(stream);
		}

		assertEquals(12, mapInfo.getVersion());
		assertEquals(50, mapInfo.getGroundTiles().size());
		assertEquals(4, mapInfo.getCliffTiles().size());

		java.io.File testFile = java.io.File.createTempFile("war3map", ".w3e");
		testFile.deleteOnExit();

		try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(new java.io.FileOutputStream(testFile))) {
			mapInfo.save(stream);
		}

		War3MapW3e mapInfo2;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new java.io.FileInputStream(testFile))) {
			mapInfo2 = new War3MapW3e(stream);
		}

		assertEquals(mapInfo.getVersion(), mapInfo2.getVersion());
		assertEquals(mapInfo.getGroundTiles(), mapInfo2.getGroundTiles());
		assertEquals(mapInfo.getCliffTiles(), mapInfo2.getCliffTiles());
	}
}
