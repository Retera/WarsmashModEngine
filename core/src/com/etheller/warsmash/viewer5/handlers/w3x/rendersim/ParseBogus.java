package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ParseBogus {

	public static void main(final String[] args) {
		final File bogusDataFile = new File("E:\\Games\\Warcraft III Patch 1.22\\Logs\\MyReteraTest.pre");
		final float[][] cubeIndexToData = new float[9][44];
		try {
			final List<String> allLines = Files.readAllLines(bogusDataFile.toPath());
			for (final String line : allLines) {
				final int cubeStringIndex = line.indexOf("Cube");
				if (cubeStringIndex != -1) {
					final int colonStringIndex = line.indexOf(':');
					final int cubeIndex = Integer.parseInt(line.substring(cubeStringIndex + 4, colonStringIndex));
					final int dataIndex = Integer.parseInt(line.substring(line.indexOf('"') + 1, cubeStringIndex));
					final float value = Float.parseFloat(line.substring(colonStringIndex + 1, line.indexOf(".txt")));
					cubeIndexToData[cubeIndex][dataIndex] = value;
				}
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		final boolean[] spun = new boolean[9];
		for (int j = 0; j < cubeIndexToData[0].length; j++) {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cubeIndexToData.length; i++) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				float value = cubeIndexToData[i][j];
				if (value > 280) {
					spun[i] = true;
				}
				else if ((value < 100) && spun[i]) {
					value += 360;
				}
				sb.append(Math.toRadians(value));
			}
			System.out.println(sb);
		}
	}

}
