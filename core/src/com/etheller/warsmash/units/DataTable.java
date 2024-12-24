package com.etheller.warsmash.units;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.util.StringBundle;

public class DataTable implements ObjectData {
	public static boolean DEBUG = false;

	Map<StringKey, Element> dataTable = new LinkedHashMap<>();

	private final StringBundle worldEditStrings;

	public DataTable(final StringBundle worldEditStrings) {
		this.worldEditStrings = worldEditStrings;
	}

	@Override
	public String getLocalizedString(final String key) {
		return this.worldEditStrings.getString(key);
	}

	@Override
	public Set<String> keySet() {
		final Set<String> outputKeySet = new HashSet<>();
		final Set<StringKey> internalKeySet = this.dataTable.keySet();
		for (final StringKey key : internalKeySet) {
			outputKeySet.add(key.getString());
		}
		return outputKeySet;
	}

	public void readTXT(final InputStream inputStream) {
		try {
			readTXT(inputStream, false);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void readTXT(final File f) {
		readTXT(f, false);
	}

	public void readTXT(final File f, final boolean canProduce) {
		try {
			readTXT(new FileInputStream(f), canProduce);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void readSLK(final File f) {
		try {
			readSLK(new FileInputStream(f));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void readTXT(final InputStream txt, final boolean canProduce) throws IOException {
		if (txt == null) {
			return;
		}
		final BufferedReader reader = new BufferedReader(new InputStreamReader(txt, "utf-8"));
		// BOM marker will only appear on the very beginning
		reader.mark(4);
		if ('\ufeff' != reader.read()) {
			reader.reset(); // not the BOM marker
		}

		String input = "";
		Element currentUnit = null;
		final boolean first = true;
		while ((input = reader.readLine()) != null) {
			if (DEBUG) {
				System.out.println(input);
			}
			if (input.startsWith("//")) {
				continue;
			}
			if (input.startsWith("[") && input.contains("]")) {
				final int start = input.indexOf("[") + 1;
				final int end = input.indexOf("]");
				final String newKey = input.substring(start, end);
				final String newKeyBase = newKey;
				currentUnit = this.dataTable.get(new StringKey(newKey));
				if (currentUnit == null) {
					currentUnit = new Element(newKey, this);
					if (canProduce) {
						currentUnit = new LMUnit(newKey, this);
						this.dataTable.put(new StringKey(newKey), currentUnit);
					}
				}
			}
			else if (input.contains("=")) {
				final int eIndex = input.indexOf("=");
				final String fieldValue = input.substring(eIndex + 1);
				final StringBuilder builder = new StringBuilder();
				boolean withinQuotedString = false;
				final String fieldName = input.substring(0, eIndex);
				boolean wasSlash = false;
				final List<String> values = new ArrayList<>();
				for (int i = 0; i < fieldValue.length(); i++) {
					final char c = fieldValue.charAt(i);
					final boolean isSlash = c == '/';
					if (isSlash && wasSlash && !withinQuotedString) {
						builder.setLength(builder.length() - 1);
						break; // comment starts here
					}
					if (c == '\"') {
						withinQuotedString = !withinQuotedString;
					}
					else if (!withinQuotedString && (c == ',')) {
						values.add(builder.toString().trim());
						builder.setLength(0); // empty buffer
					}
					else {
						builder.append(c);
					}
					wasSlash = isSlash;
				}
				if (builder.length() > 0) {
					if (currentUnit == null) {
						System.out.println("null for " + input);
					}
					values.add(builder.toString().trim());
				}
				currentUnit.setField(fieldName, values);
			}
		}

		reader.close();
	}

	public void readSLK(final InputStream txt) throws IOException {
		if (txt == null) {
			return;
		}
		final BufferedReader reader = new BufferedReader(new InputStreamReader(txt, "utf-8"));

		String input = "";
		Element currentUnit = null;
		input = reader.readLine();
		if (!input.contains("ID")) {
			System.err.println("Formatting of SLK is unusual.");
		}
		input = reader.readLine();
		while (input.startsWith("P;") || input.startsWith("F;")) {
			input = reader.readLine();
		}
		final int yIndex = input.indexOf("Y") + 1;
		final int xIndex = input.indexOf("X") + 1;
		int colCount = 0;
		int rowCount = 0;
		boolean flipMode = false;
		if (xIndex > yIndex) {
			colCount = Integer.parseInt(input.substring(xIndex, input.lastIndexOf(";")));
			rowCount = Integer.parseInt(input.substring(yIndex, xIndex - 2));
		}
		else {
			rowCount = Integer.parseInt(input.substring(yIndex, input.lastIndexOf(";")));
			colCount = Integer.parseInt(input.substring(xIndex, yIndex - 2));
			flipMode = true;
		}
		int rowStartCount = 0;
		String[] dataNames = new String[colCount];
		int col = 0;
		int lastFieldId = 0;
		while ((input = reader.readLine()) != null) {
			if (DEBUG) {
				System.out.println(input);
			}
			if (input.startsWith("E")) {
				break;
			}
			if (input.startsWith("O;")) {
				continue;
			}
			if (input.contains("X1;") || input.endsWith(";X1")) {
				rowStartCount++;
				col = 0;
			}
			else {
				col++;
			}
			String kInput;
			if (input.startsWith("F;")) {
				kInput = reader.readLine();
				if (DEBUG) {
					System.out.println(kInput);
				}
			}
			else {
				kInput = input;
			}
			if (rowStartCount <= 1) {
				final int subXIndex = input.indexOf("X");
				final int subYIndex = input.indexOf("Y");
				if ((subYIndex >= 0) && (subYIndex < subXIndex)) {
					final int eIndex = kInput.indexOf("K");
					final int fieldIdEndIndex = kInput != input ? input.length() : eIndex - 1;
					if ((eIndex == -1) || (kInput.charAt(eIndex - 1) != ';')) {
						continue;
					}
					final int fieldId;
					if (subXIndex < 0) {
						if (lastFieldId == 0) {
							rowStartCount++;
						}
						fieldId = lastFieldId + 1;
					}
					else {
						fieldId = Integer.parseInt(input.substring(subXIndex + 1, fieldIdEndIndex));
					}

					final int quotationIndex = kInput.indexOf("\"");
					if ((fieldId - 1) >= dataNames.length) {
						dataNames = Arrays.copyOf(dataNames, fieldId);
					}
					if (quotationIndex == -1) {
						dataNames[fieldId - 1] = kInput.substring(eIndex + 1);
					}
					else {
						dataNames[fieldId - 1] = kInput.substring(quotationIndex + 1, kInput.lastIndexOf("\""));
					}
					lastFieldId = fieldId;
					continue;
				}
				else {
					int eIndex = kInput.indexOf("K");
					if ((eIndex == -1) || (kInput.charAt(eIndex - 1) != ';')) {
						continue;
					}
					final int fieldId;
					if (subXIndex < 0) {
						if (lastFieldId == 0) {
							rowStartCount++;
						}
						fieldId = lastFieldId + 1;
					}
					else {
						if (flipMode && input.contains("Y") && (input == kInput)) {
							eIndex = Math.min(subYIndex, eIndex);
						}
						final String afterX = input.substring(subXIndex + 1);
						int afterXSemicolon = afterX.indexOf(';');
						if (afterXSemicolon == -1) {
							afterXSemicolon = afterX.length();
						}
						fieldId = Integer.parseInt(afterX.substring(0, afterXSemicolon));
//						final int fieldIdEndIndex = kInput != input ? input.length() : eIndex - 1;
					}

					final int quotationIndex = kInput.indexOf("\"");
					if ((fieldId - 1) >= dataNames.length) {
						dataNames = Arrays.copyOf(dataNames, fieldId);
					}
					if (quotationIndex == -1) {
						dataNames[fieldId - 1] = kInput.substring(eIndex + 1, kInput.length());
					}
					else {
						dataNames[fieldId - 1] = kInput.substring(quotationIndex + 1, kInput.lastIndexOf("\""));
					}
					lastFieldId = fieldId;
					continue;
				}
			}
			if (input.contains("X1;") || ((input != kInput) && input.endsWith("X1"))) {
				final int start = kInput.indexOf("\"") + 1;
				final int end = kInput.lastIndexOf("\"");
				if ((start - 1) != end) {
					final String newKey = kInput.substring(start, end);
					currentUnit = this.dataTable.get(new StringKey(newKey));
					if (currentUnit == null) {
						currentUnit = new Element(newKey, this);
						this.dataTable.put(new StringKey(newKey), currentUnit);
					}
				}
			}
			else if (kInput.contains("K")) {
				final int subXIndex = input.indexOf("X");
				int eIndex = kInput.indexOf("K");
				if (flipMode && kInput.contains("Y")) {
					eIndex = Math.min(kInput.indexOf("Y"), eIndex);
				}
				final int nIndex = kInput.indexOf("N");
				final int eIndexCutoff = ((nIndex != -1) && (nIndex < eIndex)) ? nIndex : eIndex;
				final int fieldIdEndIndex = kInput != input ? input.length() : eIndexCutoff - 1;
				final int fieldId = (subXIndex == -1) || (subXIndex > fieldIdEndIndex) ? 1
						: Integer.parseInt(input.substring(subXIndex + 1, fieldIdEndIndex));
				String fieldValue = kInput.substring(eIndex + 1);
				if (dataNames[fieldId - 1] != null) {
					if ((fieldValue.length() > 1) && fieldValue.startsWith("\"") && fieldValue.endsWith("\"")) {
						fieldValue = fieldValue.substring(1, fieldValue.length() - 1);
					}
					final int indexOfComma = fieldValue.indexOf(",");
					if (indexOfComma != -1) {
						final String[] splitLine = fieldValue.split(",");
						for (int splitChunkId = 0; splitChunkId < splitLine.length; splitChunkId++) {
							currentUnit.setField(dataNames[fieldId - 1], splitLine[splitChunkId], splitChunkId);
						}
					}
					else {
						currentUnit.setField(dataNames[fieldId - 1], fieldValue);
					}
				}
			}
		}

		reader.close();
	}

	@Override
	public Element get(final String id) {
		return this.dataTable.get(new StringKey(id));
	}

	@Override
	public void cloneUnit(final String parentId, final String cloneId) {
		final Element parentEntry = get(parentId);
		if (parentEntry != null) {
			final LMUnit cloneUnit = new LMUnit(cloneId, this);
			for (final String key : parentEntry.keySet()) {
				final List<String> fieldList = parentEntry.getFieldAsList(key);
				for (int i = 0; i < fieldList.size(); i++) {
					cloneUnit.setField(key, fieldList.get(i), i);
				}
			}
			put(cloneId, cloneUnit);
		}
	}

	@Override
	public void inheritFrom(String childKey, String parentKey) {
		final Element childEntry = get(childKey);
		final Element parentEntry = get(parentKey);
		if (parentEntry != null) {
			if (childEntry != null) {
				for (final String key : parentEntry.keySet()) {
					if (!childEntry.hasField(key)) {
						final List<String> fieldList = parentEntry.getFieldAsList(key);
						for (int i = 0; i < fieldList.size(); i++) {
							childEntry.setField(key, fieldList.get(i), i);
						}
					}
				}
			}
			else {
				cloneUnit(parentKey, childKey);
			}
		}
	}

	@Override
	public void setValue(final String slk, final String id, final String field, final String value) {
		get(id).setField(field, value);
	}

	public void put(final String id, final Element e) {
		this.dataTable.put(new StringKey(id), e);
	}
}