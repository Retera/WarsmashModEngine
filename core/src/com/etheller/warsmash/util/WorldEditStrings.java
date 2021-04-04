package com.etheller.warsmash.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.etheller.warsmash.datasources.DataSource;

public class WorldEditStrings implements StringBundle {
	private ResourceBundle bundle;
	private ResourceBundle bundlegs;

	public WorldEditStrings(final DataSource dataSource) {
		if (dataSource.has("UI\\WorldEditStrings.txt")) {
			try (InputStream fis = dataSource.getResourceAsStream("UI\\WorldEditStrings.txt");
					InputStreamReader reader = new InputStreamReader(fis, "utf-8")) {
				this.bundle = new PropertyResourceBundle(reader);
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		try (InputStream fis = dataSource.getResourceAsStream("UI\\WorldEditGameStrings.txt");
				InputStreamReader reader = new InputStreamReader(fis, "utf-8")) {
			this.bundlegs = new PropertyResourceBundle(reader);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getString(String string) {
		try {
			while (string.toUpperCase().startsWith("WESTRING")) {
				string = internalGetString(string);
			}
			return string;
		}
		catch (final MissingResourceException exc) {
			try {
				return this.bundlegs.getString(string.toUpperCase());
			}
			catch (final MissingResourceException exc2) {
				return string;
			}
		}
	}

	private String internalGetString(final String key) {
		if (this.bundle == null) {
			return this.bundlegs.getString(key.toUpperCase());
		}
		try {
			String string = this.bundle.getString(key.toUpperCase());
			if ((string.charAt(0) == '"') && (string.length() >= 2) && (string.charAt(string.length() - 1) == '"')) {
				string = string.substring(1, string.length() - 1);
			}
			return string;
		}
		catch (final MissingResourceException exc) {
			return this.bundlegs.getString(key.toUpperCase());
		}
	}

	@Override
	public String getStringCaseSensitive(final String key) {
		if (this.bundle == null) {
			return this.bundlegs.getString(key);
		}
		try {
			return this.bundle.getString(key);
		}
		catch (final MissingResourceException exc) {
			return this.bundlegs.getString(key);
		}
	}
}
