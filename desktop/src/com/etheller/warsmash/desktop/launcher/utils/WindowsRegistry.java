package com.etheller.warsmash.desktop.launcher.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Oleg Ryaboy, based on work by Miguel Enriquez
 */
public class WindowsRegistry {

	/**
	 *
	 * @param location path in the registry
	 * @param key      registry key
	 * @return registry value or null if not found
	 */
	public static final String readRegistry(final String location, final String key) {
		try {
			// Run reg query, then read output with StreamReader (internal class)
			final Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

			final StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			final String output = reader.getResult();

			// Output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("REG_SZ")) {
				return null;
			}

			// Parse out the value
			final String[] parsed = output.split("REG_SZ");
			String finalout = parsed[parsed.length - 1];
			finalout = finalout.trim();
			return finalout;
		}
		catch (final Exception e) {
			return null;
		}

	}

	static class StreamReader extends Thread {
		private final InputStream is;
		private final StringWriter sw = new StringWriter();

		public StreamReader(final InputStream is) {
			this.is = is;
		}

		@Override
		public void run() {
			try {
				int c;
				while ((c = this.is.read()) != -1) {
					this.sw.write(c);
				}
			}
			catch (final IOException e) {
			}
		}

		public String getResult() {
			return this.sw.toString();
		}
	}

	public static void main(final String[] args) {

		// Sample usage
		final String value = WindowsRegistry.readRegistry(
				"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\" + "Explorer\\Shell Folders", "Personal");
		System.out.println(value);
	}
}