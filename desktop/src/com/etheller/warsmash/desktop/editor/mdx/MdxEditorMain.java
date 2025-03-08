package com.etheller.warsmash.desktop.editor.mdx;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.etheller.warsmash.desktop.DesktopLauncher;
import com.etheller.warsmash.desktop.editor.mdx.ui.YseraFrame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxHandler.ShaderEnvironmentType;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;

public class MdxEditorMain {

	public static void main(final String[] args) {
		DesktopLauncher.loadExtensions();
		MdxViewer.DEFAULT_SHADER_ENV = ShaderEnvironmentType.GAME;

		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception exc) {
		}

		String iniPath = null;
		if (args.length > 0) {
			iniPath = args[0];
		}

		final DataTable warsmashIni = DesktopLauncher.loadWarsmashIni(iniPath);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final YseraFrame frame = new YseraFrame(warsmashIni);
				frame.setVisible(true);
			}
		});
	}

}
