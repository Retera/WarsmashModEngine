package com.etheller.warsmash;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CodeCounter {

	public static void main(final String[] args) {
		final int sourceLines = countFile(new File("src/com/etheller"));
		System.out.println(sourceLines);
	}

	public static int countFile(final File file) {
		if (file.isDirectory()) {
			int sum = 0;
			for (final File subFile : file.listFiles()) {
				sum += countFile(subFile);
			}
			return sum;
		}
		else {
			try {
				if (file.getName().toLowerCase().endsWith(".java")) {
					return Files.readAllLines(file.toPath()).size();
				}
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

}
