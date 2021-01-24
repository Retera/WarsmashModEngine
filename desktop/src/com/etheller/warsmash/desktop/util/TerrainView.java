package com.etheller.warsmash.desktop.util;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import com.etheller.warsmash.WarsmashGdxMapGame;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.desktop.DesktopLauncher;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.etheller.warsmash.units.DataTable;

public class TerrainView {
	public static void main(final String[] args) {
		final DataTable warsmashIni = DesktopLauncher.loadWarsmashIni();
		final DataSource dataSources = WarsmashGdxMapGame.parseDataSources(warsmashIni);
		final War3Map war3Map = new War3Map(dataSources, warsmashIni.get("Map").getField("FilePath"));
		try {
			final War3MapW3e environmentFile = war3Map.readEnvironment();
			final TerrainViewPanel terrainViewPanel = new TerrainViewPanel(environmentFile);

			final JFrame frame = new JFrame("TerrainView");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setContentPane(new JScrollPane(terrainViewPanel));
			frame.setBounds(0, 0, 800, 600);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
