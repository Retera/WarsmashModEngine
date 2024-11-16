package com.etheller.warsmash.desktop.editor.w3m.ui.editors.terrain;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxTerrainEditor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.desktop.editor.util.ExceptionPopup;
import com.etheller.warsmash.desktop.editor.w3m.ui.AbstractWorldEditorPanel;
import com.etheller.warsmash.desktop.editor.w3m.util.WorldEditArt;
import com.etheller.warsmash.networking.GameTurnManager;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.CanvasProvider;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer.MapLoader;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

public class TerrainEditorPanel extends AbstractWorldEditorPanel {
	private final JFileChooser userFileChooser = new JFileChooser();
	private final WarsmashGdxTerrainEditor warsmashGdxTerrainEditor;
	private final DataSource dataSource;
	private final LwjglCanvas lwjglCanvas;

	public TerrainEditorPanel(final DataTable warsmashIni) {
		final Element emulatorConstants = warsmashIni.get("Emulator");
		WarsmashConstants.loadConstants(emulatorConstants, warsmashIni);
		WarsmashConstants.LOAD_UNITS_FROM_WORLDEDIT_DATA = true;
		this.dataSource = WarsmashGdxMapScreen.parseDataSources(warsmashIni);
		final StandardObjectData standardObjectData = new StandardObjectData(this.dataSource);
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final WorldEditArt worldEditArt = new WorldEditArt(this.dataSource, worldEditData);

		this.warsmashGdxTerrainEditor = new WarsmashGdxTerrainEditor();
		setLayout(new BorderLayout());
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		config.gles30ContextMajorVersion = 3;
		config.gles30ContextMinorVersion = 3;
		this.lwjglCanvas = new LwjglCanvas(this.warsmashGdxTerrainEditor, config);
		final JToolBar toolBar = createToolbar(worldEditArt, worldEditData);
		toolBar.setFloatable(false);

		add(toolBar, BorderLayout.BEFORE_FIRST_LINE);
		add(BorderLayout.CENTER, this.lwjglCanvas.getCanvas());
		setPreferredSize(new Dimension(640, 480));

		this.userFileChooser.setFileFilter(new FileNameExtensionFilter("Map Files", "w3m", "w3x"));

	}

	@Override
	protected void createWindowSpecificToolbarButtons(final WorldEditArt worldEditArt, final DataTable worldEditorData,
			final JToolBar toolBar) {
		makeButton(worldEditArt, worldEditorData, toolBar, "select", "ToolBarIcon_Select", "WESTRING_TOOLBAR_SELECT");
	}

	public JMenuBar createJMenuBar(final JFrame frame) {
		final JMenuBar jMenuBar = new JMenuBar();

		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final int userResult = TerrainEditorPanel.this.userFileChooser.showOpenDialog(frame);
					if (userResult == JFileChooser.APPROVE_OPTION) {
						final File selectedFile = TerrainEditorPanel.this.userFileChooser.getSelectedFile();
						if (selectedFile != null) {
							System.out.println("selected file: " + selectedFile);

							final GameTurnManager turnManager = GameTurnManager.PAUSED;
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									try {
										final War3MapViewer viewer = new War3MapViewer(
												TerrainEditorPanel.this.dataSource, new CanvasProvider() {
													@Override
													public float getWidth() {
														return TerrainEditorPanel.this.lwjglCanvas.getCanvas()
																.getWidth();
													}

													@Override
													public float getHeight() {
														return TerrainEditorPanel.this.lwjglCanvas.getCanvas()
																.getHeight();
													}
												}, new War3MapConfig(WarsmashConstants.MAX_PLAYERS), turnManager);
										final War3Map map = War3MapViewer.beginLoadingMap(
												TerrainEditorPanel.this.dataSource, selectedFile.toString());
										final DataTable worldEditData = viewer.loadWorldEditData(map);
										final War3MapW3i mapInfo = map.readMapInformation();
										final MapLoader mapLoader = viewer.createMapLoader(map, mapInfo, 0);
										while (!mapLoader.process()) {
											System.out.println(mapLoader.getCompletionRatio());
										}
										final Element unitLights = worldEditData.get("UnitLights");
										final Element terrainLights = worldEditData.get("TerrainLights");
										final String tilesetString = String.valueOf(mapInfo.getTileset());
										final String unitLightString = unitLights.getField(tilesetString);
										final String terrainLightString = unitLights.getField(tilesetString);
										viewer.setDayNightModels(terrainLightString, unitLightString);
										System.out.println("map loader finished");
										TerrainEditorPanel.this.warsmashGdxTerrainEditor.loadViewer(viewer);
									}
									catch (final IOException exc) {
										exc.printStackTrace();
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												ExceptionPopup.display(exc);
											}
										});
									}
								}
							});
						}
					}
				}
				catch (final Exception exc) {
					exc.printStackTrace();
					ExceptionPopup.display(exc);
				}
			}
		});
		fileMenu.add(openItem);
		jMenuBar.add(fileMenu);

		return jMenuBar;
	}
}
