package com.etheller.warsmash.desktop.editor.w3m.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.terrain.TerrainEditorPanel;
import com.etheller.warsmash.units.DataTable;

public class WorldEditorFrame extends JFrame {
	public WorldEditorFrame(final DataTable warsmashIni) {
		super("Warsmash World Editor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final TerrainEditorPanel contentPane = new TerrainEditorPanel(warsmashIni);
		setContentPane(contentPane);
//		setIconImage(ImageUtils.getBLPImage(warsmashGdxGame.getCodebase(),
//				"ReplaceableTextures\\CommandButtons\\BTNGreenDragon.blp"));
		setJMenuBar(contentPane.createJMenuBar(this));
		pack();
		setLocationRelativeTo(null);

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(final WindowEvent e) {

			}

			@Override
			public void windowIconified(final WindowEvent e) {

			}

			@Override
			public void windowDeiconified(final WindowEvent e) {

			}

			@Override
			public void windowDeactivated(final WindowEvent e) {

			}

			@Override
			public void windowClosing(final WindowEvent e) {
				Gdx.app.exit();
			}

			@Override
			public void windowClosed(final WindowEvent e) {

			}

			@Override
			public void windowActivated(final WindowEvent e) {

			}
		});
	}

}
