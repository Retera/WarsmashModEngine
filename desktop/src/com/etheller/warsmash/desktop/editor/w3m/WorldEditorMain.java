package com.etheller.warsmash.desktop.editor.w3m;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.etheller.warsmash.desktop.DesktopLauncher;
import com.etheller.warsmash.desktop.editor.w3m.ui.WorldEditorFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;

public class WorldEditorMain {
	public static void main(final String[] args) {
		DesktopLauncher.loadExtensions();
		MdxViewer.DEFAULT_SHADER_ENV = ShaderEnvironmentType.GAME;

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception exc) {
		}

		final DataTable warsmashIni = DesktopLauncher.loadWarsmashIni();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final WorldEditorFrame frame = new WorldEditorFrame(warsmashIni);
				frame.setVisible(true);
			}
		});
	}
}
