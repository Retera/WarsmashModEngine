package com.etheller.warsmash.desktop;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.google.common.io.Files;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;

public class ConvertMySounds {
	static File fromFolder = new File("C:\\Warsmash\\127-png-gdx2");
	static File toFolder = new File("C:\\Warsmash\\");
	
	static Set<String> goodStrings = new HashSet<>();

	public static void main(String[] args) {
		process(fromFolder);
		
	}

	private static void process(File folder) {
		if (folder.isDirectory()) {
			for (File subFile : folder.listFiles()) {
				process(subFile);
			}
		} else {
			String subPath = folder.getPath().substring(fromFolder.getPath().length());
			if(true || goodStrings.contains(subPath.substring(1).toLowerCase())) {
				String outputPathname = toFolder.getPath() + subPath;
				if (folder.getPath().toLowerCase().endsWith(".blp")) {
					// convert to PNG
					try (InputStream warsmashIniInputStream = new FileInputStream(folder)) {
						final BufferedImage footmanAwtImage = ImageIO.read(warsmashIniInputStream);
						BufferedImage forcedRGB = ImageUtils.forceBufferedImagesRGB(footmanAwtImage);
						File output = new File(outputPathname.substring(0, outputPathname.length() - 4) + ".png");
						output.getParentFile().mkdirs();
						BufferedImage convertedImage = new BufferedImage(footmanAwtImage.getWidth(), footmanAwtImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics2D createGraphics = convertedImage.createGraphics();
						createGraphics.drawImage(forcedRGB, 0, 0, null);
						createGraphics.dispose();
						ImageIO.write(convertedImage, "png",
								output);
					} catch (final FileNotFoundException e) {
						throw new RuntimeException(e);
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				} else if (folder.getPath().toLowerCase().endsWith(".tga")) {
					
					try (InputStream warsmashIniInputStream = new FileInputStream(folder)) {
						final BufferedImage footmanAwtImage = TgaFile.readTGA(folder.getPath(), warsmashIniInputStream);
						File output = new File(outputPathname.substring(0, outputPathname.length() - 4) + ".png");
						output.getParentFile().mkdirs();
						ImageIO.write(footmanAwtImage, "png",
								output);
					} catch (final FileNotFoundException e) {
						throw new RuntimeException(e);
					} catch (final IOException e) {
						throw new RuntimeException(e);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				} else {
					try {
						File output = new File(outputPathname);
						output.getParentFile().mkdirs();
						Files.copy(folder, output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
