package com.etheller.warsmash.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(final String[] args) {
		final Pattern pattern = Pattern.compile("^\\[(.+?)\\]");
		final Matcher matcher = pattern.matcher("[boat] // ocean");
		if (matcher.matches()) {
			final String name = matcher.group(1).trim().toLowerCase();
			System.out.println(name);
		}
		else {
			System.out.println("no match");
		}
	}

}
