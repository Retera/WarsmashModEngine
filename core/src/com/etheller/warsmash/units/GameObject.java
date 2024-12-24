package com.etheller.warsmash.units;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;

public interface GameObject {

	public void setField(String field, String value);

	public void setField(String slk, String field, String value);

	public void setField(String slk, String field, String value, int index);

	public void clearFieldList(String slk, String field);

	public String getField(String field);

	public List<String> getFieldAsList(String field);

	public String getField(String field, int index);

	public int getFieldValue(String field);

	public int getFieldValue(String field, int index);

	public float getFieldFloatValue(String field);

	public float getFieldFloatValue(String field, int index);

	public default String getFieldAsString(final String field, final int index) {
		return getField(field, index);
	}

	public default int getFieldAsInteger(final String field, final int index) {
		return getFieldValue(field, index);
	}

	public default float getFieldAsFloat(final String field, final int index) {
		return getFieldFloatValue(field, index);
	}

	public default boolean getFieldAsBoolean(final String field, final int index) {
		return getFieldValue(field, index) != 0;
	}

	public default War3ID getFieldAsWar3ID(final String field, final int index) {
		return War3ID.fromString(getField(field, index));
	}

	public default int readSLKTagInt(final String field) {
		return getFieldValue(field);
	}

	public default String readSLKTag(final String field) {
		return getField(field);
	}

	public default float readSLKTagFloat(final String field) {
		return getFieldFloatValue(field);
	}

	public default boolean readSLKTagBoolean(final String field) {
		return getFieldValue(field) != 0;
	}

	public List<? extends GameObject> getFieldAsList(String field, ObjectData objectData);

	public String getId();

	public ObjectData getTable();

	public String getName();

	public String getLegacyName();

	public Set<String> keySet();

	GameObject EMPTY = new GameObject() {
		@Override
		public void setField(final String field, final String value) {
		}

		@Override
		public void setField(final String slk, final String field, final String value, final int index) {
		}

		@Override
		public void setField(final String slk, final String field, final String value) {
		}

		@Override
		public void clearFieldList(final String slk, final String field) {
		}

		@Override
		public Set<String> keySet() {
			return Collections.emptySet();
		}

		@Override
		public ObjectData getTable() {
			return null;
		}

		@Override
		public String getName() {
			return "<No data>";
		}

		@Override
		public String getId() {
			return "0000";
		}

		@Override
		public int getFieldValue(final String field, final int index) {
			return 0;
		}

		@Override
		public int getFieldValue(final String field) {
			return 0;
		}

		@Override
		public float getFieldFloatValue(final String field) {
			return 0;
		}

		@Override
		public float getFieldFloatValue(final String field, final int index) {
			return 0;
		}

		@Override
		public List<? extends GameObject> getFieldAsList(final String field, final ObjectData objectData) {
			return Collections.emptyList();
		}

		@Override
		public String getField(final String field, final int index) {
			return "";
		}

		@Override
		public String getField(final String field) {
			return "";
		}

		@Override
		public List<String> getFieldAsList(final String field) {
			return Collections.emptyList();
		}

		@Override
		public String getLegacyName() {
			return "custom_0000";
		}
	};

}