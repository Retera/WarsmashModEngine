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

public class LowercaseAll {
	static File fromFolder = new File("C:\\Users\\micro\\source\\repos\\warsmash-phone\\project\\assets");
	static File toFolder = new File("C:\\Warsmash\\LowerCaseAssets");

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
			if (true || goodStrings.contains(subPath.substring(1).toLowerCase())) {
				String outputPathname = toFolder.getPath() + subPath;
				try {
					File output = new File(outputPathname.toLowerCase());
					output.getParentFile().mkdirs();
					Files.copy(folder, output);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
