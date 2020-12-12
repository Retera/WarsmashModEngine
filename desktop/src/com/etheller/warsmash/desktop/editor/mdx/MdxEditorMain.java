package com.etheller.warsmash.desktop.editor.mdx;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.etheller.warsmash.desktop.DesktopLauncher;
import com.etheller.warsmash.desktop.editor.mdx.ui.YseraFrame;
import com.etheller.warsmash.units.DataTable;

public class MdxEditorMain {

	public static void main(final String[] args) {
		DesktopLauncher.loadExtensions();

		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception exc) {
		}

		final DataTable warsmashIni = DesktopLauncher.loadWarsmashIni();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final YseraFrame frame = new YseraFrame(warsmashIni);
				frame.setVisible(true);
			}
		});
	}

}
