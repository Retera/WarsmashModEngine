package com.etheller.warsmash;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.MpqDataSourceDescriptor;

public class TestMain {
	public static void main(final String[] args) {
		final MpqDataSourceDescriptor desc = new MpqDataSourceDescriptor("E:\\Backups\\Warcraft\\Data\\127\\Z.mpq");
		final DataSource createDataSource = desc.createDataSource();
		try {
			final InputStream cliffZ = createDataSource.getResourceAsStream("ReplaceableTextures\\Cliff\\Cliff0.blp");
			final BufferedImage img = ImageIO.read(cliffZ);
			JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
