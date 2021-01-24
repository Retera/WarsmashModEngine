package com.etheller.warsmash.desktop.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JPanel;

import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;

public class TerrainViewPanel extends JPanel {
	private final War3MapW3e environmentFile;
	private final Font baseFont;
	private final Font biggerFont;
	private int cliffMode = 0;

	public TerrainViewPanel(final War3MapW3e environmentFile) {
		this.environmentFile = environmentFile;
		add(Box.createRigidArea(new Dimension(this.environmentFile.getCorners()[0].length * 32,
				this.environmentFile.getCorners().length * 32)));
		this.baseFont = getFont();
		this.biggerFont = this.baseFont.deriveFont(24f);
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(final MouseEvent e) {

			}

			@Override
			public void mousePressed(final MouseEvent e) {
				TerrainViewPanel.this.cliffMode = (TerrainViewPanel.this.cliffMode + 1) % 4;
				repaint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {

			}

			@Override
			public void mouseEntered(final MouseEvent e) {

			}

			@Override
			public void mouseClicked(final MouseEvent e) {

			}
		});
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Corner[][] corners = this.environmentFile.getCorners();
		for (int i = 0; i < corners.length; i++) {
			final int length = corners[i].length;
			for (int j = 0; j < length; j++) {
				int base = 0;
				final int value;
				switch (this.cliffMode) {
				case 0:
					value = corners[i][j].getRamp();
					break;
				case 1:
					value = corners[i][j].getLayerHeight();
					base = 2;
					break;
				case 2:
					value = corners[i][j].getGroundTexture();
					break;
				case 3:
					value = corners[i][j].getCliffTexture();
					break;
				default:
					value = 0;
					break;
				}
				if (value != base) {
					g.setFont(this.biggerFont);
				}
				else {
					g.setFont(this.baseFont);
				}
				g.drawString(value + "", j * 32, (length - i - 1) * 32);
			}
		}
	}
}
