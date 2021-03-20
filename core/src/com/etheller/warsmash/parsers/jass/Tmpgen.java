package com.etheller.warsmash.parsers.jass;

import java.util.Scanner;

public class Tmpgen {

	public static void main(final String[] args) {
		// final HandleJassType eventType = globals.registerHandleType("event");

		final Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			if (line.startsWith("type ")) {
				final String[] splitLine = line.split("\\s+");
				System.out.println("final HandleJassType " + splitLine[1] + "Type = globals.registerHandleType(\""
						+ splitLine[1] + "\");");
			}
		}

	}

}
