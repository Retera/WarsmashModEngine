package com.etheller.warsmash.desktop.editor.w3m.scripted;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.etheller.warsmash.desktop.DesktopLauncher;
import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.parsers.w3x.w3i.Player;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.wpm.War3MapWpm;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.google.common.io.LittleEndianDataOutputStream;

public class ScriptedMapGeneratorMain {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <directory>");
			return;
		}
		final File directory = new File(args[0]);

		final DataTable warsmashIni = DesktopLauncher.loadWarsmashIni();
		final Element emulatorConstants = warsmashIni.get("Emulator");
		WarsmashConstants.loadConstants(emulatorConstants, warsmashIni);

		try {
			generateW3i(directory);
			generateW3e(directory);
			generateWpm(directory);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateW3i(final File directory) throws IOException, FileNotFoundException {
		final War3MapW3i mapInfo = new War3MapW3i(null);

		mapInfo.setName("Debug Mission");
		mapInfo.setAuthor("Retera");
		mapInfo.setDescription("A debug mission generated using open source tools.");
		mapInfo.setVersion(0);
		mapInfo.setEditorVersion(0);
		mapInfo.setRecommendedPlayers("1");

		final float[] cameraBounds = mapInfo.getCameraBounds();
		final int[] cameraBoundsComplements = mapInfo.getCameraBoundsComplements();
		for (int i = 0; i < 4; i++) {
			cameraBoundsComplements[i] = 4;
			cameraBounds[i] = 4 * 128f;
		}

		mapInfo.setLoadingScreenTitle("Journey to the Liberation");
		mapInfo.setLoadingScreenSubtitle("Debug Mission");
		mapInfo.setLoadingScreenText("A debug mission generated using open source tools.");
		mapInfo.setPrologueScreenTitle("");
		mapInfo.setPrologueScreenSubtitle("");
		mapInfo.setPrologueScreenText("");
		mapInfo.setCampaignBackground(-1);
		mapInfo.setTileset('S');

		final Player player = new Player();
		player.setId(0);
		player.setName("Player 1");
		mapInfo.getPlayers().add(player);

		try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(new FileOutputStream(
				new File(directory.getPath() + "/" + WarsmashConstants.MAP_CONTENTS_PREFIX + ".w3i")))) {
			mapInfo.save(stream);
		}
	}

	private static void generateW3e(final File directory) throws IOException, FileNotFoundException {
		final War3MapW3e mapInfo = new War3MapW3e(null);

		mapInfo.setTileset('S');
		final List<War3ID> groundTiles = mapInfo.getGroundTiles();
		groundTiles.add(War3ID.fromString("Sdir"));
		groundTiles.add(War3ID.fromString("Sgra"));

		final int[] mapSize = mapInfo.getMapSize();
		final float[] centerOffset = mapInfo.getCenterOffset();
		mapSize[0] = 64;
		mapSize[1] = 64;
		centerOffset[0] = (-32 * 128f);
		centerOffset[1] = (-32 * 128f);

		final Corner[][] corners = new Corner[mapSize[0] + 1][mapSize[1] + 1];
		for (int i = 0; i < (mapSize[0] + 1); i++) {
			for (int j = 0; j < (mapSize[1] + 1); j++) {
				final Corner corner = new Corner();
				corner.setGroundHeight((float) (Math.random()));
				corner.setGroundTexture(Math.random() < 0.5 ? 1 : 0);
				corners[i][j] = corner;
			}
		}
		mapInfo.setCorners(corners);

		try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(new FileOutputStream(
				new File(directory.getPath() + "/" + WarsmashConstants.MAP_CONTENTS_PREFIX + ".w3e")))) {
			mapInfo.save(stream);
		}
	}

	private static void generateWpm(final File directory) throws IOException, FileNotFoundException {
		final War3MapWpm mapInfo = new War3MapWpm(null);

		final int[] size = mapInfo.getSize();
		size[0] = 64 * 4;
		size[1] = 64 * 4;
		final short[] pathing = new short[size[0] * size[1]];

		mapInfo.setPathing(pathing);

		try (LittleEndianDataOutputStream stream = new LittleEndianDataOutputStream(new FileOutputStream(
				new File(directory.getPath() + "/" + WarsmashConstants.MAP_CONTENTS_PREFIX + ".wpm")))) {
			mapInfo.save(stream);
		}
	}
}
