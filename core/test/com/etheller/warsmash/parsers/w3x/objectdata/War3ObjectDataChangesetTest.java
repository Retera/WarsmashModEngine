package com.etheller.warsmash.parsers.w3x.objectdata;

import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class War3ObjectDataChangesetTest {

	@Test
	void testUnitsWoWReforged() throws IOException {
		final War3ObjectDataChangeset unitChangeset = new War3ObjectDataChangeset('u');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				getClass().getClassLoader().getResourceAsStream("wowr_w3x/war3map.w3u"))) {
			unitChangeset.load(stream, null, false);
		}

		assertEquals(2199, unitChangeset.getCustom().size());

		java.io.File testFile = java.io.File.createTempFile("war3map_", ".w3u");
		testFile.deleteOnExit();

		try (LittleEndianDataOutputStream out = new LittleEndianDataOutputStream(new java.io.FileOutputStream(testFile))) {
			unitChangeset.save(out, false);
		}

		final War3ObjectDataChangeset unitChangeset2 = new War3ObjectDataChangeset('u');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new java.io.FileInputStream(testFile))) {
			unitChangeset.load(stream, null, false);
		}

		assertEquals(unitChangeset.getCustom().size(), unitChangeset2.getCustom().size());
	}
}
