package com.etheller.warsmash.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class HashedGameObject implements GameObject {
	HashMap<StringKey, List<String>> fields = new HashMap<>();
	String id;
	ObjectData parentTable;

	transient HashMap<String, List<GameObject>> hashedLists = new HashMap<>();

	public HashedGameObject(final String id, final ObjectData table) {
		this.id = id;
		this.parentTable = table;
	}

	public int size(){
		return this.fields.size();
	}

	@Override
	public void setField(final String slk, final String field, final String value) {
		setField(field, value);
	}

	public void setField(final String field, final String value) {
		final StringKey key = new StringKey(field);
		List<String> list = this.fields.get(key);
		if (list == null) {
			list = new ArrayList<>();
			this.fields.put(key, list);
			list.add(value);
		}
		else {
			list.set(0, value);
		}
	}

	public void setField(final String field, final List<String> value) {
		final StringKey key = new StringKey(field);
		if (value.isEmpty()) {
			this.fields.remove(key);
		}
		else {
			this.fields.put(key, value);
		}
	}

	@Override
	public String getField(final String field) {
		final String value = "";
		if (this.fields.get(new StringKey(field)) != null) {
			final List<String> list = this.fields.get(new StringKey(field));
			final StringBuilder sb = new StringBuilder();
			if (list != null) {
				for (final String str : list) {
					if (sb.length() != 0) {
						sb.append(',');
					}
					sb.append(str);
				}
				return sb.toString();
			}
		}
		return value;
	}

	@Override
	public List<String> getFieldAsList(final String field) {
		return this.fields.get(new StringKey(field));
	}

	public boolean hasField(final String field) {
		return this.fields.containsKey(new StringKey(field));
	}

	@Override
	public int getFieldValue(final String field) {
		int i = 0;
		try {
			i = Integer.parseInt(getField(field).trim());
		}
		catch (final NumberFormatException e) {

		}
		return i;
	}

	@Override
	public float getFieldFloatValue(final String field) {
		float i = 0;
		try {
			i = Float.parseFloat(getField(field).trim());
		}
		catch (final NumberFormatException e) {

		}
		return i;
	}

	@Override
	public float getFieldFloatValue(final String field, final int index) {
		return getFieldWithDefaultValue(field, index, 0f);
	}

	public float getFieldWithDefaultValue(final String field, int index, final float defaultValue) {
		float i = defaultValue;
		{
			if (index < 0) {
				index = 0;
			}
			final List<String> fieldList = this.fields.get(new StringKey(field));
			if (fieldList != null) {
				final List<String> list = fieldList;
				if (list != null) {
					if (list.size() > index) {
						try {
							i = Float.parseFloat(list.get(index).trim());
						}
						catch (final NumberFormatException e) {

						}
					}
				}
			}
		}
		return i;
	}

	@Override
	public void setField(final String slk, final String field, final String value, final int index) {
		setField(field, value, index);
	}

	public void setField(final String field, final String value, final int index) {
		final StringKey key = new StringKey(field);
		List<String> list = this.fields.get(key);
		if (list == null) {
			if (index == 0) {
				list = new ArrayList<>();
				this.fields.put(key, list);
				list.add(value);
			}
			else {
				list = new ArrayList<>();
				for (int k = 0; k < index; k++) {
					list.add("");
				}
				list.add(value);
			}
		}
		else {
			if (list.size() == index) {
				list.add(value);
			}
			else {
				for (int k = list.size(); k <= index; k++) {
					list.add("");
				}
				list.set(index, value);
			}
		}
	}

	@Override
	public void clearFieldList(final String slk, final String field) {
		this.fields.remove(new StringKey(field));
	}

	@Override
	public String getField(final String field, int index) {
		if (index < 0) {
			index = 0;
		}
		String value = "";
		if (this.fields.get(new StringKey(field)) != null) {
			final List<String> list = this.fields.get(new StringKey(field));
			if (list != null) {
				if (list.size() > index) {
					value = list.get(index);
				}
				else if (list.size() > 0) {
					value = list.get(list.size() - 1);
				}
			}
		}
		return value;
	}

	@Override
	public int getFieldValue(final String field, final int index) {
		return getFieldWithDefaultValue(field, index, 0);
	}

	public int getFieldWithDefaultValue(final String field, int index, final int defaultValue) {
		int i = defaultValue;
		{
			if (index < 0) {
				index = 0;
			}
			final List<String> fieldList = this.fields.get(new StringKey(field));
			if (fieldList != null) {
				final List<String> list = fieldList;
				if (list != null) {
					if (list.size() > index) {
						try {
							i = Integer.parseInt(list.get(index).trim());
						}
						catch (final NumberFormatException e) {
							try {
								i = (int) Float.parseFloat(list.get(index).trim());
							}
							catch (final NumberFormatException e2) {

							}
						}
					}
				}
			}
		}
		return i;
	}

	@Override
	public List<GameObject> getFieldAsList(final String field, final ObjectData parentTable) {
		List<GameObject> fieldAsList;
		fieldAsList = new ArrayList<>();
		final String stringList = getField(field);
		final String[] listAsArray = stringList.split(",");
		if ((listAsArray != null) && (listAsArray.length > 0)) {
			for (final String buildingId : listAsArray) {
				final GameObject referencedUnit = parentTable.get(buildingId);
				if (referencedUnit != null) {
					fieldAsList.add(referencedUnit);
				}
			}
		}
		return fieldAsList;
	}

	@Override
	public String toString() {
		return getField("Name");
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		String name = getField("Name");
		boolean nameKnown = name.length() >= 1;
		if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
			final GameObject other = this.parentTable.get(getField("code").substring(0, 4));
			if (other != null) {
				name = other.getName();
				nameKnown = true;
			}
		}
		if (!nameKnown && (getField("EditorName").length() > 1)) {
			name = getField("EditorName");
			nameKnown = true;
		}
		if (!nameKnown && (getField("Editorname").length() > 1)) {
			name = getField("Editorname");
			nameKnown = true;
		}
		if (!nameKnown && (getField("BuffTip").length() > 1)) {
			name = getField("BuffTip");
			nameKnown = true;
		}
		if (!nameKnown && (getField("Bufftip").length() > 1)) {
			name = getField("Bufftip");
			nameKnown = true;
		}
		if (nameKnown && name.startsWith("WESTRING")) {
			if (!name.contains(" ")) {
				name = this.parentTable.getLocalizedString(name);
			}
			else {
				final String[] names = name.split(" ");
				name = "";
				for (final String subName : names) {
					if (name.length() > 0) {
						name += " ";
					}
					if (subName.startsWith("WESTRING")) {
						name += this.parentTable.getLocalizedString(subName);
					}
					else {
						name += subName;
					}
				}
			}
			if (name.startsWith("\"") && name.endsWith("\"")) {
				name = name.substring(1, name.length() - 1);
			}
			setField("Name", name);
		}
		if (!nameKnown) {
			name = this.parentTable.getLocalizedString("WESTRING_UNKNOWN") + " '" + getId() + "'";
		}
		if (getField("campaign").startsWith("1") && Character.isUpperCase(getId().charAt(0))) {
			name = getField("Propernames");
			if (name.contains(",")) {
				name = name.split(",")[0];
			}
		}
		String suf = getField("EditorSuffix");
		if ((suf.length() > 0) && !suf.equals("_")) {
			if (suf.startsWith("WESTRING")) {
				suf = this.parentTable.getLocalizedString(suf);
			}
			if (!suf.startsWith(" ")) {
				name += " ";
			}
			name += suf;
		}
		return name;
	}

	public void addToList(final String parentId, final String list) {
		String parentField = getField(list);
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField(list, parentField);
		}
	}

	@Override
	public ObjectData getTable() {
		return this.parentTable;
	}

	@Override
	public Set<String> keySet() {
		final Set<String> keySet = new HashSet<>();
		for (final StringKey key : this.fields.keySet()) {
			keySet.add(key.getString());
		}
		return keySet;
	}
}
