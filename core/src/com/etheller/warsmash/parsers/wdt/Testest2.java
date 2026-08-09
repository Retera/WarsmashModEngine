package com.etheller.warsmash.parsers.wdt;

import java.util.Scanner;

public class Testest2 {
	public static void main(final String[] args) {

		final Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			final String nextLine = scanner.nextLine();
			final String thing = nextLine.substring(nextLine.indexOf('"') + 1, nextLine.lastIndexOf('"'));

//			System.out.println("private static final int " + thing + " = ('" + thing.charAt(0) + "' << 24) | ('"
//					+ thing.charAt(1) + "' << 16) | ('" + thing.charAt(2) + "' << 8) | ('" + thing.charAt(3) + "');");

			System.out.println("case " + thing + ":");

		}
		scanner.close();

	}

}
