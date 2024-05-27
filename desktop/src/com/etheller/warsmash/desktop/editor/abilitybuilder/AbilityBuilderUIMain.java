package com.etheller.warsmash.desktop.editor.abilitybuilder;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class AbilityBuilderUIMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame frame = new JFrame("Ability Builder UI");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				AbilityBuilderUIPanel contentPane = createContentPane();
				frame.setContentPane(contentPane);
				frame.setJMenuBar(contentPane.createJMenuBar());
				frame.setBounds(0, 0, 800, 600);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}

			private AbilityBuilderUIPanel createContentPane() {
				return new AbilityBuilderUIPanel();
			}

		});
	}

}
