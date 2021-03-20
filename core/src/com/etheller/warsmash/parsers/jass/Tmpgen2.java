package com.etheller.warsmash.parsers.jass;

import java.util.Scanner;

public class Tmpgen2 {

	public static void main(final String[] args) {
		// final HandleJassType eventType = globals.registerHandleType("event");

		final Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			final String[] splitLine = line.trim().split("\\s+");
			if (line.trim().startsWith("//")) {
				System.out.println(line);
			}
			else {
				if (splitLine.length > 3) {
					System.out.println(splitLine[2] + ",");
				}
				else {
					System.out.println();
				}
			}
		}

	}

}
