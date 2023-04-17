package com.etheller.warsmash.desktop.editor.w3m.ui.editors.terrain;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.WarsmashGdxTerrainEditor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.desktop.editor.util.ExceptionPopup;
import com.etheller.warsmash.desktop.editor.w3m.ui.AbstractWorldEditorPanel;
import com.etheller.warsmash.desktop.editor.w3m.util.WorldEditArt;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.StandardObjectData;

public class TerrainEditorPanel extends AbstractWorldEditorPanel {
	private final JFileChooser userFileChooser = new JFileChooser();

	public TerrainEditorPanel(final DataTable warsmashIni) {
		final DataSource dataSource = WarsmashGdxMapScreen.parseDataSources(warsmashIni);
		final StandardObjectData standardObjectData = new StandardObjectData(dataSource);
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final WorldEditArt worldEditArt = new WorldEditArt(dataSource, worldEditData);

		final WarsmashGdxTerrainEditor warsmashGdxTerrainEditor = new WarsmashGdxTerrainEditor();
		setLayout(new BorderLayout());
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL30 = true;
		config.gles30ContextMajorVersion = 3;
		config.gles30ContextMinorVersion = 3;
		final LwjglCanvas lwjglCanvas = new LwjglCanvas(warsmashGdxTerrainEditor, config);
		final JToolBar toolBar = createToolbar(worldEditArt, worldEditData);
		toolBar.setFloatable(false);

		add(toolBar, BorderLayout.BEFORE_FIRST_LINE);
		add(BorderLayout.CENTER, lwjglCanvas.getCanvas());
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
					final int userResult = userFileChooser.showOpenDialog(frame);
					if (userResult == JFileChooser.APPROVE_OPTION) {
						final File selectedFile = userFileChooser.getSelectedFile();
						if (selectedFile != null) {
							System.out.println("selected file: " + selectedFile);
						}
					}
				}
				catch (final Exception exc) {
					ExceptionPopup.display(exc);
				}
			}
		});
		fileMenu.add(openItem);
		jMenuBar.add(fileMenu);

		return jMenuBar;
	}
}
