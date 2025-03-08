package com.etheller.warsmash.desktop.editor.mdx.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.etheller.warsmash.WarsmashPreviewApplication;
import com.etheller.warsmash.units.DataTable;

public class YseraFrame extends JFrame {
	public YseraFrame(final DataTable warsmashIni) {
		super("Warsmash Model Viewer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final WarsmashPreviewApplication warsmashPreviewApplication = new WarsmashPreviewApplication(warsmashIni);
		final YseraPanel contentPane = new YseraPanel(warsmashPreviewApplication);
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
