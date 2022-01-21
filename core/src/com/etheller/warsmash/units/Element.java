package com.etheller.warsmash.units;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Element extends HashedGameObject {

	public Element(final String id, final DataTable table) {
		super(id, table);
	}

	public List<GameObject> builds() {
		return getFieldAsList("Builds", this.parentTable);
	}

	public List<GameObject> requires() {
		final List<GameObject> requirements = getFieldAsList("Requires", this.parentTable);
		final List<Integer> reqLvls = requiresLevels();
		return requirements;
	}

	public List<Integer> requiresLevels() {
		final String stringList = getField("Requiresamount");
		final String[] listAsArray = stringList.split(",");
		final LinkedList<Integer> output = new LinkedList<>();
		if ((listAsArray != null) && (listAsArray.length > 0) && !listAsArray[0].equals("")) {
			for (final String levelString : listAsArray) {
				final Integer level = Integer.parseInt(levelString);
				if (level != null) {
					output.add(level);
				}
			}
		}
		return output;
	}

	public List<GameObject> parents() {
		return getFieldAsList("Parents", this.parentTable);
	}

	public List<GameObject> children() {
		return getFieldAsList("Children", this.parentTable);
	}

	public List<GameObject> requiredBy() {
		return getFieldAsList("RequiredBy", this.parentTable);
	}

	public List<GameObject> trains() {
		return getFieldAsList("Trains", this.parentTable);
	}

	public List<GameObject> upgrades() {
		return getFieldAsList("Upgrade", this.parentTable);
	}

	public List<GameObject> researches() {
		return getFieldAsList("Researches", this.parentTable);
	}

	public List<GameObject> dependencyOr() {
		return getFieldAsList("DependencyOr", this.parentTable);
	}

	public List<GameObject> abilities() {
		return getFieldAsList("abilList", this.parentTable);
	}

	HashMap<String, List<Element>> hashedLists = new HashMap<>();

	@Override
	public String toString() {
		return getField("Name");
	}

	public int getTechTier() {
		final String tier = getField("Custom Field: TechTier");
		if (tier == null) {
			return -1;
		}
		return Integer.parseInt(tier);
	}

	public void setTechTier(final int i) {
		setField("Custom Field: TechTier", i + "");
	}

	public int getTechDepth() {
		final String tier = getField("Custom Field: TechDepth");
		if (tier == null) {
			return -1;
		}
		return Integer.parseInt(tier);
	}

	public void setTechDepth(final int i) {
		setField("Custom Field: TechDepth", i + "");
	}

	public String getIconPath() {
		String artField = getField("Art");
		if (artField.indexOf(',') != -1) {
			artField = artField.substring(0, artField.indexOf(','));
		}
		return artField;
	}

	public String getUnitId() {
		return this.id;
	}

	@Override
	public String getName() {
		String name = getField("Name");
		boolean nameKnown = name.length() >= 1;
		if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
			final Element other = (Element) this.parentTable.get(getField("code").substring(0, 4));
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
			name = this.parentTable.getLocalizedString("WESTRING_UNKNOWN") + " '" + getUnitId() + "'";
		}
		if (getField("campaign").startsWith("1") && Character.isUpperCase(getUnitId().charAt(0))) {
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

	@Override
	public String getLegacyName() {
		return null;
	}

	public void addParent(final String parentId) {
		String parentField = getField("Parents");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Parents", parentField);
		}
	}

	public void addChild(final String parentId) {
		String parentField = getField("Children");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Children", parentField);
		}
	}

	public void addRequiredBy(final String parentId) {
		String parentField = getField("RequiredBy");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("RequiredBy", parentField);
		}
	}

	public void addResearches(final String parentId) {
		String parentField = getField("Researches");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Researches", parentField);
		}
	}
}
