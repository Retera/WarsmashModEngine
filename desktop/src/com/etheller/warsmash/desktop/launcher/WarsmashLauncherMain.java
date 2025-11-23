package com.etheller.warsmash.desktop.launcher;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class WarsmashLauncherMain {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new JFrame("Warsmash Launcher & Configuration");
				final WarsmashLauncherPanel panel = new WarsmashLauncherPanel();
				frame.setContentPane(panel);
				frame.setJMenuBar(panel.createJMenuBar());
				frame.setBounds(0, 0, 800, 600);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
