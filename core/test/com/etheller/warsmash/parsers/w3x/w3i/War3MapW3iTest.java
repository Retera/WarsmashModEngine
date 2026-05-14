package com.etheller.warsmash.util;

import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class War3MapW3iTest {

	@Test
	void testWoWReforged() throws IOException {
		War3MapW3i mapInfo;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				getClass().getClassLoader().getResourceAsStream("wowr_w3x/war3map.w3i"))) {
			mapInfo = new War3MapW3i(stream);
		}

		assertEquals(33, mapInfo.getVersion());
		assertEquals("TRIGSTR_004", mapInfo.getAuthor());
		assertEquals(12, mapInfo.getPlayers().size());
		assertEquals(3, mapInfo.getForces().size());

		java.io.File testFile = java.io.File.createTempFile("war3map_", ".w3i");
		testFile.deleteOnExit();

		try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(new java.io.FileOutputStream(testFile))) {
			mapInfo.save(stream);
		}

		War3MapW3i mapInfo2;
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new java.io.FileInputStream(testFile))) {
			mapInfo2 = new War3MapW3i(stream);
		}

		assertEquals(mapInfo.getVersion(), mapInfo2.getVersion());
		assertEquals(mapInfo.getAuthor(), mapInfo2.getAuthor());
		assertEquals(mapInfo.getPlayers().size(), mapInfo2.getPlayers().size());
	}
}
