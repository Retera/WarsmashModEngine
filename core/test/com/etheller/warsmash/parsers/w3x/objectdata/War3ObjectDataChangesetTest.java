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
		final War3ObjectDataChangeset changeset = new War3ObjectDataChangeset('u');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				getClass().getClassLoader().getResourceAsStream("wowr_w3x/war3map.w3u"))) {
			changeset.load(stream, null, false);
		}

		assertEquals(2199, changeset.getCustom().size());

		java.io.File testFile = java.io.File.createTempFile("war3map", ".w3u");
		testFile.deleteOnExit();

		try (LittleEndianDataOutputStream out = new LittleEndianDataOutputStream(new java.io.FileOutputStream(testFile))) {
			changeset.save(out, false);
		}

		final War3ObjectDataChangeset changeset2 = new War3ObjectDataChangeset('u');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new java.io.FileInputStream(testFile))) {
			changeset.load(stream, null, false);
		}

		assertEquals(changeset.getCustom().size(), changeset2.getCustom().size());
	}

	@Test
	void testAbilitiesWoWReforged() throws IOException {
		final War3ObjectDataChangeset changeset = new War3ObjectDataChangeset('a');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(
				getClass().getClassLoader().getResourceAsStream("wowr_w3x/war3map.w3a"))) {
			changeset.load(stream, null, false);
		}

		assertEquals(2148, changeset.getCustom().size());

		java.io.File testFile = java.io.File.createTempFile("war3map", ".w3a");
		testFile.deleteOnExit();

		try (LittleEndianDataOutputStream out = new LittleEndianDataOutputStream(new java.io.FileOutputStream(testFile))) {
			changeset.save(out, false);
		}

		final War3ObjectDataChangeset changeset2 = new War3ObjectDataChangeset('a');
		try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new java.io.FileInputStream(testFile))) {
			changeset.load(stream, null, false);
		}

		assertEquals(changeset.getCustom().size(), changeset2.getCustom().size());
	}
}
