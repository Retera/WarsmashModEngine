package com.etheller.warsmash.units.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Deaod
 *
 */
public class WTSFile implements WTS {
	private final InputStream source;
	private final Map<Integer, String> trigStrings = new Hashtable<>();

	private static enum ParseState {
		NEXT_TRIGSTR,
		START_OF_DATA,
		END_OF_DATA;
	}

	private void parse() throws IOException {
		final BufferedReader sourceReader = new BufferedReader(
				new InputStreamReader(this.source, Charset.forName("utf-8")));
		ParseState state = ParseState.NEXT_TRIGSTR;

		// WTS files may start with a Byte Order Mark, which we will have to skip.
		sourceReader.mark(4);
		if (sourceReader.read() != 0xFEFF) {
			// first character not a BOM, unread the character.
			sourceReader.reset();
		}

		String currentLine = sourceReader.readLine();
		int id = 0;
		final StringBuilder data = new StringBuilder();

		while (currentLine != null) {
			switch (state) {
			case NEXT_TRIGSTR:
				if (currentLine.startsWith("STRING ")) {
					id = Integer.parseInt(currentLine.substring(7));
					state = ParseState.START_OF_DATA;
				}
				break;

			case START_OF_DATA:
				if (currentLine.startsWith("{")) {
					state = ParseState.END_OF_DATA;
				}
				break;

			case END_OF_DATA:
				if (currentLine.startsWith("}")) {
					this.trigStrings.put(id, data.toString());
					data.setLength(0);
					state = ParseState.NEXT_TRIGSTR;
				}
				else {
					if (!data.isEmpty()) {
						data.append('\n');
					}
					data.append(currentLine);
				}
				break;
			}
			currentLine = sourceReader.readLine();
		}
		sourceReader.close();
	}

	public WTSFile(final InputStream inputStream) throws IOException {
		this.source = inputStream;
		parse();
	}

	public WTSFile(final Path source) throws IOException {
		this(Files.newInputStream(source));
	}

	public WTSFile(final String sourcePath) throws IOException {
		this(Paths.get(sourcePath));
	}

	@Override
	public String get(final int index) {
		return this.trigStrings.get(index);
	}

}
