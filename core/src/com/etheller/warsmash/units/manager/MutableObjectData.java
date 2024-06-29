package com.etheller.warsmash.units.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.custom.Change;
import com.etheller.warsmash.units.custom.ChangeMap;
import com.etheller.warsmash.units.custom.ObjectDataChangeEntry;
import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public final class MutableObjectData {
	private static final War3ID ROC_SUPPORT_URAC = War3ID.fromString("urac");
	private static final War3ID ROC_SUPPORT_UCAM = War3ID.fromString("ucam");
	private static final War3ID ROC_SUPPORT_USPE = War3ID.fromString("uspe");
	private static final War3ID ROC_SUPPORT_UBDG = War3ID.fromString("ubdg");

	private final WorldEditorDataType worldEditorDataType;
	private final ObjectData sourceSLKData;
	private final ObjectData sourceSLKMetaData;
	private final War3ObjectDataChangeset editorData;
	private Set<War3ID> cachedKeySet;
	private final Map<String, War3ID> metaNameToMetaId;
	private final Map<War3ID, MutableGameObject> cachedKeyToGameObject;
	private final MutableObjectDataChangeNotifier changeNotifier;
	private final WorldEditStrings worldEditStrings;

	public MutableObjectData(final WorldEditStrings worldEditStrings, final WorldEditorDataType worldEditorDataType,
			final ObjectData sourceSLKData, final ObjectData sourceSLKMetaData,
			final War3ObjectDataChangeset editorData) {
		this.worldEditStrings = worldEditStrings;
		this.worldEditorDataType = worldEditorDataType;
		resolveStringReferencesInNames(sourceSLKData);
		this.sourceSLKData = sourceSLKData;
		this.sourceSLKMetaData = sourceSLKMetaData;
		this.editorData = editorData;
		this.metaNameToMetaId = new HashMap<>();
		for (final String metaKeyString : sourceSLKMetaData.keySet()) {
			final War3ID metaKey = War3ID.fromString(metaKeyString);
			this.metaNameToMetaId.put(sourceSLKMetaData.get(metaKeyString).getField("field"), metaKey);
		}
		this.cachedKeyToGameObject = new HashMap<>();
		this.changeNotifier = new MutableObjectDataChangeNotifier();
	}

	// TODO remove this hack
	public War3ObjectDataChangeset getEditorData() {
		return this.editorData;
	}

	private void resolveStringReferencesInNames(final ObjectData sourceSLKData) {
		for (final String key : sourceSLKData.keySet()) {
			final GameObject gameObject = sourceSLKData.get(key);
			String name = gameObject.getField("Name");
			final String suffix = gameObject.getField("EditorSuffix");
			if (name.startsWith("WESTRING")) {
				if (!name.contains(" ")) {
					name = this.worldEditStrings.getString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						if (subName.startsWith("WESTRING")) {
							name += this.worldEditStrings.getString(subName);
						}
						else {
							name += subName;
						}
					}
				}
				if (name.startsWith("\"") && name.endsWith("\"")) {
					name = name.substring(1, name.length() - 1);
				}
				gameObject.setField("Profile", "Name", name);
			}
			if (suffix.startsWith("WESTRING")) {
				gameObject.setField("Profile", "EditorSuffix", this.worldEditStrings.getString(suffix));
			}
		}
	}

	public void mergeChangset(final War3ObjectDataChangeset changeset) {
		final List<War3ID> newObjects = new ArrayList<>();
		final Map<War3ID, War3ID> previousAliasToNewAlias = new HashMap<>();
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : changeset.getCustom()) {

//			final String newId = JOptionPane.showInputDialog("Choose UNIT ID");
			final War3ID nextDefaultEditorId = /* War3ID.fromString(newId); */getNextDefaultEditorId(
					War3ID.fromString(entry.getKey().charAt(0) + "000"));
			;
			System.out.println("Merging " + nextDefaultEditorId + " for  " + entry.getKey());
			// createNew API will notifier the changeNotifier
			final MutableGameObject newObject = createNew(nextDefaultEditorId, entry.getValue().getOldId(), false);
			for (final Map.Entry<War3ID, List<Change>> changeList : entry.getValue().getChanges()) {
				newObject.customUnitData.getChanges().add(changeList.getKey(), changeList.getValue());
			}
			newObjects.add(nextDefaultEditorId);
			previousAliasToNewAlias.put(entry.getKey(), nextDefaultEditorId);
		}
		final War3ID[] fieldsToCheck = this.worldEditorDataType == WorldEditorDataType.UNITS
				? new War3ID[] { War3ID.fromString("utra"), War3ID.fromString("uupt"), War3ID.fromString("ubui") }
				: new War3ID[] {};
		for (final War3ID unitId : newObjects) {
			final MutableGameObject unit = get(unitId);
			for (final War3ID field : fieldsToCheck) {
				final String techtreeString = unit.getFieldAsString(field, 0);
				final java.util.List<String> techList = Arrays.asList(techtreeString.split(","));
				final ArrayList<String> resultingTechList = new ArrayList<>();
				for (final String tech : techList) {
					if (tech.length() == 4) {
						final War3ID newTechId = previousAliasToNewAlias.get(War3ID.fromString(tech));
						if (newTechId != null) {
							resultingTechList.add(newTechId.toString());
						}
						else {
							resultingTechList.add(tech);
						}
					}
					else {
						resultingTechList.add(tech);
					}
				}
				final StringBuilder sb = new StringBuilder();
				for (final String tech : resultingTechList) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(tech);
				}
				unit.setField(field, 0, sb.toString());
			}
		}
		this.changeNotifier.objectsCreated(newObjects.toArray(new War3ID[newObjects.size()]));
	}

	public War3ObjectDataChangeset copySelectedObjects(final List<MutableGameObject> objectsToCopy) {
		final War3ObjectDataChangeset changeset = new War3ObjectDataChangeset(this.editorData.getExpectedKind());
		final War3ID[] fieldsToCheck = this.worldEditorDataType == WorldEditorDataType.UNITS
				? new War3ID[] { War3ID.fromString("utra"), War3ID.fromString("uupt"), War3ID.fromString("ubui") }
				: new War3ID[] {};
		final Map<War3ID, War3ID> previousAliasToNewAlias = new HashMap<>();
		for (final MutableGameObject gameObject : objectsToCopy) {
			final ObjectDataChangeEntry gameObjectUserDataToCopy;
			final ObjectDataChangeEntry gameObjectUserData;
			final War3ID alias = gameObject.getAlias();
			if (this.editorData.getOriginal().containsKey(alias)) {
				gameObjectUserDataToCopy = this.editorData.getOriginal().get(alias);
				final War3ID newAlias = getNextDefaultEditorId(
						War3ID.fromString(gameObject.getCode().charAt(0) + "000"), changeset, this.sourceSLKData);
				gameObjectUserData = new ObjectDataChangeEntry(gameObjectUserDataToCopy.getOldId(), newAlias);
			}
			else if (this.editorData.getCustom().containsKey(alias)) {
				gameObjectUserDataToCopy = this.editorData.getCustom().get(alias);
				gameObjectUserData = new ObjectDataChangeEntry(gameObjectUserDataToCopy.getOldId(),
						gameObjectUserDataToCopy.getNewId());
			}
			else {
				gameObjectUserDataToCopy = null;
				final War3ID newAlias = getNextDefaultEditorId(
						War3ID.fromString(gameObject.getCode().charAt(0) + "000"), changeset, this.sourceSLKData);
				gameObjectUserData = new ObjectDataChangeEntry(
						gameObject.isCustom() ? gameObject.getCode() : gameObject.getAlias(), newAlias);
			}
			if (gameObjectUserDataToCopy != null) {
				for (final Map.Entry<War3ID, List<Change>> changeEntry : gameObjectUserDataToCopy.getChanges()) {
					for (final Change change : changeEntry.getValue()) {
						final Change newChange = new Change();
						newChange.copyFrom(change);
						gameObjectUserData.getChanges().add(change.getId(), newChange);
					}
				}
			}
			previousAliasToNewAlias.put(gameObject.getAlias(), gameObjectUserData.getNewId());
			changeset.getCustom().put(gameObjectUserData.getNewId(), gameObjectUserData);
		}
		final MutableObjectData changeEditManager = new MutableObjectData(this.worldEditStrings,
				this.worldEditorDataType, this.sourceSLKData, this.sourceSLKMetaData, changeset);
		for (final War3ID unitId : changeEditManager.keySet()) {
			final MutableGameObject unit = changeEditManager.get(unitId);
			for (final War3ID field : fieldsToCheck) {
				final String techtreeString = unit.getFieldAsString(field, 0);
				final java.util.List<String> techList = Arrays.asList(techtreeString.split(","));
				final ArrayList<String> resultingTechList = new ArrayList<>();
				for (final String tech : techList) {
					if (tech.length() == 4) {
						final War3ID newTechId = previousAliasToNewAlias.get(War3ID.fromString(tech));
						if (newTechId != null) {
							resultingTechList.add(newTechId.toString());
						}
						else {
							resultingTechList.add(tech);
						}
					}
					else {
						resultingTechList.add(tech);
					}
				}
				final StringBuilder sb = new StringBuilder();
				for (final String tech : resultingTechList) {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(tech);
				}
				unit.setField(field, 0, sb.toString());
			}
		}
		return changeset;

	}

	public WorldEditorDataType getWorldEditorDataType() {
		return this.worldEditorDataType;
	}

	public ObjectData getSourceSLKMetaData() {
		return this.sourceSLKMetaData;
	}

	public void addChangeListener(final MutableObjectDataChangeListener listener) {
		this.changeNotifier.subscribe(listener);
	}

	public void removeChangeListener(final MutableObjectDataChangeListener listener) {
		this.changeNotifier.unsubscribe(listener);
	}

	/**
	 * Returns the set of all Unit IDs in the map, at the cost of a lot of time to
	 * go find them all.
	 *
	 * @return
	 */

	public Set<War3ID> keySet() {
		if (this.cachedKeySet == null) {
			final Set<War3ID> customUnitKeys = this.editorData.getCustom().keySet();
			final Set<War3ID> customKeys = new HashSet<>(customUnitKeys);
			for (final String standardUnitKey : this.sourceSLKData.keySet()) {
				if (standardUnitKey.length() > 4) {
					System.err.println("Omitting object data key because it is too long: " + standardUnitKey);
					continue;
				}
				customKeys.add(War3ID.fromString(standardUnitKey));
			}
			this.cachedKeySet = customKeys;
		}
		return this.cachedKeySet;
	}

	public void dropCachesHack() {
		this.cachedKeySet = null;
		this.cachedKeyToGameObject.clear();
	}

	public MutableGameObject get(final War3ID id) {
		MutableGameObject mutableGameObject = this.cachedKeyToGameObject.get(id);
		if (mutableGameObject == null) {
			if (this.editorData.getCustom().containsKey(id)) {
				final ObjectDataChangeEntry customUnitData = this.editorData.getCustom().get(id);
				GameObject parentWC3Object = this.sourceSLKData.get(customUnitData.getOldId().asStringValue());
				if (parentWC3Object == null) {
					System.err.println("Error parsing unit data: custom unit inherits from unknown id '"
							+ customUnitData.getOldId().asStringValue() + "'");
					parentWC3Object = GameObject.EMPTY;
				}
				mutableGameObject = new MutableGameObject(parentWC3Object, customUnitData);
				this.cachedKeyToGameObject.put(id, mutableGameObject);
			}
			else if (this.editorData.getOriginal().containsKey(id)) {
				final ObjectDataChangeEntry customUnitData = this.editorData.getOriginal().get(id);
				GameObject parentWC3Object = this.sourceSLKData.get(customUnitData.getOldId().asStringValue());
				if (parentWC3Object == null) {
					System.err.println("Error parsing unit data: standard unit modifies unknown id '"
							+ customUnitData.getOldId().asStringValue() + "'");
					parentWC3Object = GameObject.EMPTY;
				}
				mutableGameObject = new MutableGameObject(parentWC3Object, this.editorData.getOriginal().get(id));
				this.cachedKeyToGameObject.put(id, mutableGameObject);
			}
			else if (this.sourceSLKData.get(id.asStringValue()) != null) {
				GameObject parentWC3Object = this.sourceSLKData.get(id.asStringValue());
				if (parentWC3Object == null) {
					System.err.println("Error parsing unit data: id does not exist: '" + id.asStringValue() + "'");
					parentWC3Object = GameObject.EMPTY;
				}
				mutableGameObject = new MutableGameObject(parentWC3Object, null);
				this.cachedKeyToGameObject.put(id, mutableGameObject);
			}
		}
		return mutableGameObject;
	}

	public MutableGameObject createNew(final War3ID id, final War3ID parent) {
		return createNew(id, parent, true);
	}

	private MutableGameObject createNew(final War3ID id, final War3ID parent, final boolean fireListeners) {
		this.editorData.getCustom().put(id, new ObjectDataChangeEntry(parent, id));
		if (this.cachedKeySet != null) {
			this.cachedKeySet.add(id);
		}
		if (fireListeners) {
			this.changeNotifier.objectCreated(id);
		}
		return get(id);
	}

	public void remove(final War3ID id) {
		remove(id, true);
	}

	public void remove(final List<MutableGameObject> objects) {
		final List<War3ID> removedIds = new ArrayList<>();
		for (final MutableGameObject object : objects) {
			if (object.isCustom()) {
				remove(object.getAlias(), false);
				removedIds.add(object.getAlias());
			}
		}
		this.changeNotifier.objectsRemoved(removedIds.toArray(new War3ID[removedIds.size()]));
	}

	private MutableGameObject remove(final War3ID id, final boolean fireListeners) {
		final ObjectDataChangeEntry removedObject = this.editorData.getCustom().remove(id);
		final MutableGameObject removedMutableObj = this.cachedKeyToGameObject.remove(id);
		if (this.cachedKeySet != null) {
			this.cachedKeySet.remove(id);
		}
		if (fireListeners) {
			this.changeNotifier.objectRemoved(id);
		}
		return removedMutableObj /* might be null based on cache, don't use */;
	}

	private static boolean goodForId(final char c) {
		return Character.isDigit(c) || ((c >= 'A') && (c <= 'Z'));
	}

	public War3ID getNextDefaultEditorId(final War3ID startingId) {
		War3ID newId = startingId;
		while (this.editorData.getCustom().containsKeyCaseInsensitive(newId)
				|| (this.sourceSLKData.get(newId.toString()) != null) || !goodForId(newId.charAt(1))
				|| !goodForId(newId.charAt(2)) || !goodForId(newId.charAt(3))) {
			// TODO good code general solution
			if (newId.charAt(3) == 'Z') {
				if (newId.charAt(2) == 'Z') {
					if (newId.charAt(1) == 'Z') {
						newId = new War3ID(((newId.getValue() / (256 * 256 * 256)) * 256 * 256 * 256)
								+ (256 * 256 * 256) + '0' + ('0' * 256) + ('0' * 256 * 256));
					}
					else {
						newId = new War3ID(
								((newId.getValue() / (256 * 256)) * 256 * 256) + (256 * 256) + '0' + ('0' * 256));
					}
				}
				else {
					newId = new War3ID(((newId.getValue() / 256) * 256) + 256 + '0');
				}
			}
			else {
				newId = new War3ID(newId.getValue() + 1);
			}
		}
		return newId;
	}

	public static War3ID getNextDefaultEditorId(final War3ID startingId, final War3ObjectDataChangeset editorData,
			final ObjectData sourceSLKData) {
		War3ID newId = startingId;
		while (editorData.getCustom().containsKeyCaseInsensitive(newId) || (sourceSLKData.get(newId.toString()) != null)
				|| !goodForId(newId.charAt(1)) || !goodForId(newId.charAt(2)) || !goodForId(newId.charAt(3))) {
			newId = new War3ID(newId.getValue() + 1);
		}
		return newId;
	}

	private static final War3ID BUFF_EDITOR_NAME = War3ID.fromString("fnam");
	private static final War3ID BUFF_BUFFTIP = War3ID.fromString("ftip");
	private static final War3ID UNIT_CAMPAIGN = War3ID.fromString("ucam");
	private static final War3ID UNIT_EDITOR_SUFFIX = War3ID.fromString("unsf");
	private static final War3ID ABIL_EDITOR_SUFFIX = War3ID.fromString("ansf");
	private static final War3ID DESTRUCTABLE_EDITOR_SUFFIX = War3ID.fromString("bsuf");
	private static final War3ID BUFF_EDITOR_SUFFIX = War3ID.fromString("fnsf");
	private static final War3ID UPGRADE_EDITOR_SUFFIX = War3ID.fromString("gnsf");
	private static final War3ID HERO_PROPER_NAMES = War3ID.fromString("upro");

	private static final Set<War3ID> CATEGORY_FIELDS = new HashSet<>();
	private static final Set<War3ID> TEXT_FIELDS = new HashSet<>();
	private static final Set<War3ID> ICON_FIELDS = new HashSet<>();
	private static final Set<War3ID> FIELD_SETTINGS_FIELDS = new HashSet<>();

	static {
		// categorizing - I thought these would be changeFlags value "c", but no luck
		CATEGORY_FIELDS.add(War3ID.fromString("ubdg")); // is a building
		CATEGORY_FIELDS.add(War3ID.fromString("uspe")); // categorize special
		CATEGORY_FIELDS.add(War3ID.fromString("ucam")); // categorize campaign
		CATEGORY_FIELDS.add(War3ID.fromString("urac")); // race
		CATEGORY_FIELDS.add(War3ID.fromString("uine")); // in editor
		CATEGORY_FIELDS.add(War3ID.fromString("ucls")); // sort string (not a real field, fanmade)

		CATEGORY_FIELDS.add(War3ID.fromString("icla")); // item class

		CATEGORY_FIELDS.add(War3ID.fromString("bcat")); // destructible category

		CATEGORY_FIELDS.add(War3ID.fromString("dcat")); // doodad category

		CATEGORY_FIELDS.add(War3ID.fromString("aher")); // hero ability
		CATEGORY_FIELDS.add(War3ID.fromString("aite")); // item ability
		CATEGORY_FIELDS.add(War3ID.fromString("arac")); // ability race

		CATEGORY_FIELDS.add(War3ID.fromString("frac")); // buff race
		CATEGORY_FIELDS.add(War3ID.fromString("feff")); // is effect

		CATEGORY_FIELDS.add(War3ID.fromString("grac")); // upgrade race
		// field structure fields - doesn't seem to be changeFlags 's' like you might
		// hope
		FIELD_SETTINGS_FIELDS.add(War3ID.fromString("ubdg")); // unit is a builder
		FIELD_SETTINGS_FIELDS.add(War3ID.fromString("dvar")); // doodad variations
		FIELD_SETTINGS_FIELDS.add(War3ID.fromString("alev")); // ability level
		FIELD_SETTINGS_FIELDS.add(War3ID.fromString("glvl")); // upgrade max level
	}

	public final class MutableGameObject {
		private final GameObject parentWC3Object;
		private ObjectDataChangeEntry customUnitData;

		private void fireChangedEvent(final War3ID field, final int level) {
			final String changeFlags = MutableObjectData.this.sourceSLKMetaData.get(field.toString())
					.getField("changeFlags");
			if (CATEGORY_FIELDS.contains(field)) {
				MutableObjectData.this.changeNotifier.categoriesChanged(getAlias());
			}
			else if (changeFlags.contains("t")) {
				MutableObjectData.this.changeNotifier.textChanged(getAlias());
			}
			else if (changeFlags.contains("m")) {
				MutableObjectData.this.changeNotifier.modelChanged(getAlias());
			}
			else if (changeFlags.contains("i")) {
				MutableObjectData.this.changeNotifier.iconsChanged(getAlias());
			}
			else if (FIELD_SETTINGS_FIELDS.contains(field)) {
				MutableObjectData.this.changeNotifier.fieldsChanged(getAlias());
			}
		}

		public MutableGameObject(final GameObject parentWC3Object, final ObjectDataChangeEntry customUnitData) {
			this.parentWC3Object = parentWC3Object;
			if (parentWC3Object == null) {
				System.err.println(
						"Parent object is null for " + customUnitData.getNewId() + ":" + customUnitData.getOldId());
				throw new AssertionError("parentWC3Object cannot be null");
//				this.parentWC3Object = new Element("", new DataTable());
			}
			this.customUnitData = customUnitData;
		}

		public boolean hasCustomField(final War3ID field, final int level) {
			return getMatchingChange(field, level) != null;
		}

		public boolean hasEditorData() {
			return (this.customUnitData != null) && (this.customUnitData.getChanges().size() > 0);
		}

		public boolean isCustom() {
			return MutableObjectData.this.editorData.getCustom().containsKey(getAlias());
		}

		public void setField(final War3ID field, final int level, final String value) {
			if (value.equals(getFieldStringFromSLKs(field, level))) {
				if (!value.equals(getFieldAsString(field, level))) {
					fireChangedEvent(field, level);
				}
				else {
				}
				resetFieldToDefaults(field, level);
				return;
			}
			final Change matchingChange = getOrCreateMatchingChange(field, level);
			matchingChange.setStrval(value);
			matchingChange.setVartype(War3ObjectDataChangeset.VAR_TYPE_STRING);
			fireChangedEvent(field, level);
		}

		public void setField(final War3ID field, final int level, final boolean value) {
			if (value == (asInt(getFieldStringFromSLKs(field, level).trim()) == 1)) {
				if (value != getFieldAsBoolean(field, level)) {
					fireChangedEvent(field, level);
				}
				resetFieldToDefaults(field, level);
				return;
			}
			final Change matchingChange = getOrCreateMatchingChange(field, level);
			matchingChange.setBoolval(value);
			matchingChange.setVartype(War3ObjectDataChangeset.VAR_TYPE_BOOLEAN);
			fireChangedEvent(field, level);
		}

		public void setField(final War3ID field, final int level, final int value) {
			if (value == asInt(getFieldStringFromSLKs(field, level).trim())) {
				if (value != getFieldAsInteger(field, level)) {
					fireChangedEvent(field, level);
				}
				resetFieldToDefaults(field, level);
				return;
			}
			final Change matchingChange = getOrCreateMatchingChange(field, level);
			matchingChange.setLongval(value);
			matchingChange.setVartype(War3ObjectDataChangeset.VAR_TYPE_INT);
			fireChangedEvent(field, level);
		}

		public void resetFieldToDefaults(final War3ID field, final int level) {
			final Change existingChange = getMatchingChange(field, level);
			if ((existingChange != null) && (this.customUnitData != null)) {
				this.customUnitData.getChanges().delete(field, existingChange);
				fireChangedEvent(field, level);
			}
			return;
		}

		public void setField(final War3ID field, final int level, final float value) {
			if (Math.abs(value - asFloat(getFieldStringFromSLKs(field, level).trim())) < 0.00001f) {
				if (Math.abs(value - getFieldAsFloat(field, level)) > 0.00001f) {
					fireChangedEvent(field, level);
				}
				resetFieldToDefaults(field, level);
				return;
			}
			final Change matchingChange = getOrCreateMatchingChange(field, level);
			matchingChange.setRealval(value);
			final boolean unsigned = MutableObjectData.this.sourceSLKMetaData.get(field.asStringValue())
					.getField("type").equals("unreal");
			matchingChange.setVartype(
					unsigned ? War3ObjectDataChangeset.VAR_TYPE_UNREAL : War3ObjectDataChangeset.VAR_TYPE_REAL);
			fireChangedEvent(field, level);
		}

		private Change getOrCreateMatchingChange(final War3ID field, final int level) {
			if (this.customUnitData == null) {
				final War3ID war3Id = War3ID.fromString(this.parentWC3Object.getId());
				final ObjectDataChangeEntry newCustomUnitData = new ObjectDataChangeEntry(war3Id, War3ID.NONE);
				MutableObjectData.this.editorData.getOriginal().put(war3Id, newCustomUnitData);
				this.customUnitData = newCustomUnitData;
			}
			Change matchingChange = getMatchingChange(field, level);
			if (matchingChange == null) {
				final ChangeMap changeMap = this.customUnitData.getChanges();
				final List<Change> changeList = changeMap.get(field);
				matchingChange = new Change();
				matchingChange.setId(field);
				matchingChange.setLevel(level);
				if (MutableObjectData.this.editorData.extended()) {
					// dunno why, but Blizzard sure likes those dataptrs in the ability data
					// my code should grab 0 when the metadata lacks this field
					matchingChange.setDataptr(
							MutableObjectData.this.sourceSLKMetaData.get(field.asStringValue()).getFieldValue("data"));
				}
				if (changeList == null) {
					changeMap.add(field, matchingChange);
				}
				else {
					boolean insertedChange = false;
					for (int i = 0; i < changeList.size(); i++) {
						if (changeList.get(i).getLevel() > level) {
							insertedChange = true;
							changeList.add(i, matchingChange);
							break;
						}
					}
					if (!insertedChange) {
						changeList.add(changeList.size(), matchingChange);
					}
				}
			}
			return matchingChange;
		}

		public String getFieldAsString(final War3ID field, final int level) {
			final Change matchingChange = getMatchingChange(field, level);
			if (matchingChange != null) {
				if (matchingChange.getVartype() == War3ObjectDataChangeset.VAR_TYPE_INT) {
					return Integer.toString(matchingChange.getLongval());
				}
				if (matchingChange.getVartype() != War3ObjectDataChangeset.VAR_TYPE_STRING) {
					throw new IllegalStateException(
							"Requested string value of '" + field + "' from '" + this.parentWC3Object.getId()
									+ "', but this field was not a string! vartype=" + matchingChange.getVartype());
				}
				return matchingChange.getStrval();
			}
			// no luck with custom data, look at the standard data
			return getFieldStringFromSLKs(field, level);
		}

		private Change getMatchingChange(final War3ID field, final int level) {
			Change matchingChange = null;
			if (this.customUnitData == null) {
				return null;
			}
			final List<Change> changeList = this.customUnitData.getChanges().get(field);
			if (changeList != null) {
				for (final Change change : changeList) {
					if (change.getLevel() == level) {
						matchingChange = change;
						break;
					}
				}
			}
			return matchingChange;
		}

		public String readSLKTag(final String key) {
			if (MutableObjectData.this.metaNameToMetaId.containsKey(key)) {
				return getFieldAsString(MutableObjectData.this.metaNameToMetaId.get(key), 0);
			}
			if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
					&& key.startsWith("Data")) {
				for (final String metaKeyString : MutableObjectData.this.sourceSLKMetaData.keySet()) {
					final GameObject metaField = MutableObjectData.this.sourceSLKMetaData.get(metaKeyString);
					if ("data".equals(metaField.getField("field").toLowerCase())) {
						final String useSpecific = metaField.getField("useSpecific");
						final String[] specificUses = useSpecific.split(",");
						for (final String use : specificUses) {
							if (getAlias().asStringValue().equalsIgnoreCase(use)) {
								final int index = metaField.getFieldValue("index");
								if ((key.length() >= 5) && (key.charAt(4) == (('A' + index) - 1))) {
									final int level = Integer.parseInt(key.substring(5));
									return getFieldAsString(War3ID.fromString(metaField.getId()), level);
								}
							}
						}
					}
				}
			}
			return this.parentWC3Object.getField(key);
		}

		public boolean readSLKTagBoolean(final String key) {
			if (MutableObjectData.this.metaNameToMetaId.containsKey(key)) {
				return getFieldAsBoolean(MutableObjectData.this.metaNameToMetaId.get(key), 0);
			}
			if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
					&& key.startsWith("Data")) {
				for (final String metaKeyString : MutableObjectData.this.sourceSLKMetaData.keySet()) {
					final GameObject metaField = MutableObjectData.this.sourceSLKMetaData.get(metaKeyString);
					if ("data".equals(metaField.getField("field").toLowerCase())) {
						final String useSpecific = metaField.getField("useSpecific");
						final String[] specificUses = useSpecific.split(",");
						for (final String use : specificUses) {
							if (getAlias().asStringValue().equalsIgnoreCase(use)) {
								final int index = metaField.getFieldValue("index");
								if ((key.length() >= 5) && (key.charAt(4) == (('A' + index) - 1))) {
									final int level = Integer.parseInt(key.substring(5));
									return getFieldAsBoolean(War3ID.fromString(metaField.getId()), level);
								}
							}
						}
					}
				}
			}
			return this.parentWC3Object.getFieldValue(key) == 1;
		}

		public int readSLKTagInt(final String key) {
			if (MutableObjectData.this.metaNameToMetaId.containsKey(key)) {
				return getFieldAsInteger(MutableObjectData.this.metaNameToMetaId.get(key), 0);
			}
			if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
					&& key.startsWith("Data")) {
				for (final String metaKeyString : MutableObjectData.this.sourceSLKMetaData.keySet()) {
					final GameObject metaField = MutableObjectData.this.sourceSLKMetaData.get(metaKeyString);
					if ("data".equals(metaField.getField("field").toLowerCase())) {
						final String useSpecific = metaField.getField("useSpecific");
						final String[] specificUses = useSpecific.split(",");
						for (final String use : specificUses) {
							if (getAlias().asStringValue().equalsIgnoreCase(use)) {
								final int index = metaField.getFieldValue("index");
								if ((key.length() >= 5) && (key.charAt(4) == (('A' + index) - 1))) {
									final int level = Integer.parseInt(key.substring(5));
									return getFieldAsInteger(War3ID.fromString(metaField.getId()), level);
								}
							}
						}
					}
				}
			}
			return this.parentWC3Object.getFieldValue(key);
		}

		public float readSLKTagFloat(final String key) {
			if (MutableObjectData.this.metaNameToMetaId.containsKey(key)) {
				return getFieldAsFloat(MutableObjectData.this.metaNameToMetaId.get(key), 0);
			}
			if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
					&& key.startsWith("Data")) {
				for (final String metaKeyString : MutableObjectData.this.sourceSLKMetaData.keySet()) {
					final GameObject metaField = MutableObjectData.this.sourceSLKMetaData.get(metaKeyString);
					if ("data".equals(metaField.getField("field").toLowerCase())) {
						final String useSpecific = metaField.getField("useSpecific");
						final String[] specificUses = useSpecific.split(",");
						for (final String use : specificUses) {
							if (getAlias().asStringValue().equalsIgnoreCase(use)) {
								final int index = metaField.getFieldValue("index");
								if ((key.length() >= 5) && (key.charAt(4) == (('A' + index) - 1))) {
									final int level = Integer.parseInt(key.substring(5));
									return getFieldAsFloat(War3ID.fromString(metaField.getId()), level);
								}
							}
						}
					}
				}
			}
			try {
				return Float.parseFloat(this.parentWC3Object.getField(key));
			}
			catch (final NumberFormatException exc) {
				return Float.NaN;
			}
		}

		public String getName() {
			String name = getFieldAsString(MutableObjectData.this.editorData.getNameField(),
					MutableObjectData.this.worldEditorDataType == WorldEditorDataType.UPGRADES ? 1 : 0);
			boolean nameKnown = name.length() >= 1;
			if (!nameKnown && !readSLKTag("code").equals(getAlias().toString()) && (readSLKTag("code").length() >= 4)
					&& !isCustom()) {
				final MutableGameObject codeObject = get(War3ID.fromString(readSLKTag("code").substring(0, 4)));
				if (codeObject != null) {
					name = codeObject.getName();
					nameKnown = true;
				}
			}
			String suf = "";
			switch (MutableObjectData.this.worldEditorDataType) {
			case ABILITIES:
				suf = getFieldAsString(ABIL_EDITOR_SUFFIX, 0);
				break;
			case BUFFS_EFFECTS:
				final String editorName = getFieldAsString(BUFF_EDITOR_NAME, 0);
				if (!nameKnown && (editorName.length() > 1)) {
					name = editorName;
					nameKnown = true;
				}
				final String buffTip = getFieldAsString(BUFF_BUFFTIP, 0);
				if (!nameKnown && (buffTip.length() > 1)) {
					name = buffTip;
					nameKnown = true;
				}
				suf = getFieldAsString(BUFF_EDITOR_SUFFIX, 0);
				break;
			case DESTRUCTIBLES:
				suf = getFieldAsString(DESTRUCTABLE_EDITOR_SUFFIX, 0);
				break;
			case DOODADS:
				break;
			case ITEM:
				break;
			case UNITS:
				if (getFieldAsBoolean(UNIT_CAMPAIGN, 0) && Character.isUpperCase(getAlias().charAt(0))) {
					name = getFieldAsString(HERO_PROPER_NAMES, 0);
					if (name.contains(",")) {
						name = name.split(",")[0];
					}
				}
				suf = getFieldAsString(UNIT_EDITOR_SUFFIX, 0);
				break;
			case UPGRADES:
				suf = getFieldAsString(UPGRADE_EDITOR_SUFFIX, 1);
				break;
			}
			if (nameKnown/* && name.startsWith("WESTRING") */) {
				if (!name.contains(" ")) {
					// name = WEString.getString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						// if (subName.startsWith("WESTRING")) {
						// name += WEString.getString(subName);
						// } else {
						name += subName;
						// }
					}
				}
				if (name.startsWith("\"") && name.endsWith("\"")) {
					name = name.substring(1, name.length() - 1);
				}
			}
			if (!nameKnown) {
				name = MutableObjectData.this.worldEditStrings.getString("WESTRING_UNKNOWN") + " '"
						+ getAlias().toString() + "'";
			}
			if ((suf.length() > 0) && !suf.equals("_")) {
				// if (suf.startsWith("WESTRING")) {
				// suf = WEString.getString(suf);
				// }
				if (!suf.startsWith(" ")) {
					name += " ";
				}
				name += suf;
			}
			return name;
		}

		public String getLegacyName() {
			if (!isCustom()) {
				final String legacyNameIfAvailable = this.parentWC3Object.getLegacyName();
				if (legacyNameIfAvailable != null) {
					return legacyNameIfAvailable;
				}
			}
			return "custom_" + getAlias().toString();
		}

		private String getFieldStringFromSLKs(final War3ID field, final int level) {
			final GameObject metaData = MutableObjectData.this.sourceSLKMetaData.get(field.asStringValue());
			if (metaData == null) {
				if (MutableObjectData.this.worldEditorDataType == WorldEditorDataType.UNITS) {
					if (ROC_SUPPORT_URAC.equals(field)) {
						return this.parentWC3Object.getField("race");
					}
					else if (ROC_SUPPORT_UCAM.equals(field)) {
						return "0";
					}
					else if (ROC_SUPPORT_USPE.equals(field)) {
						return this.parentWC3Object.getField("special");
					}
					else if (ROC_SUPPORT_UBDG.equals(field)) {
						return this.parentWC3Object.getField("isbldg");
					}
				}
				throw new IllegalStateException("Program requested " + field.toString() + " from "
						+ MutableObjectData.this.worldEditorDataType);
			}
			if (this.parentWC3Object == null) {
				throw new IllegalStateException("corrupted unit, no parent unit id");
			}
			int index = metaData.getFieldValue("index");
			if (index != -1) {
				if (level > 0) {
					index = level - 1;
				}
				final String fieldStringValue = this.parentWC3Object
						.getField(getEditorMetaDataDisplayKey(level, metaData), index);
				return fieldStringValue;
			}
			final String fieldStringValue = this.parentWC3Object.getField(getEditorMetaDataDisplayKey(level, metaData));
			return fieldStringValue;
		}

		public int getFieldAsInteger(final War3ID field, final int level) {
			final Change matchingChange = getMatchingChange(field, level);
			if (matchingChange != null) {
				if (matchingChange.getVartype() != War3ObjectDataChangeset.VAR_TYPE_INT) {
					if (matchingChange.getVartype() == War3ObjectDataChangeset.VAR_TYPE_UNREAL) {
						return (int) matchingChange.getRealval();
					}
					throw new IllegalStateException(
							"Requested integer value of '" + field + "' from '" + this.parentWC3Object.getId()
									+ "', but this field was not an int! vartype=" + matchingChange.getVartype());
				}
				return matchingChange.getLongval();
			}
			// no luck with custom data, look at the standard data
			try {
				return Integer.parseInt(getFieldStringFromSLKs(field, level));
			}
			catch (final NumberFormatException e) {
				return 0;
			}
		}

		public boolean getFieldAsBoolean(final War3ID field, final int level) {
			final Change matchingChange = getMatchingChange(field, level);
			if (matchingChange != null) {
				if (matchingChange.getVartype() != War3ObjectDataChangeset.VAR_TYPE_BOOLEAN) {
					if (matchingChange.getVartype() == War3ObjectDataChangeset.VAR_TYPE_INT) {
						return matchingChange.getLongval() == 1;
					}
					else {
						throw new IllegalStateException(
								"Requested boolean value of '" + field + "' from '" + this.parentWC3Object.getId()
										+ "', but this field was not a bool! vartype=" + matchingChange.getVartype());
					}
				}
				return matchingChange.isBoolval();
			}
			// no luck with custom data, look at the standard data
			try {
				return Integer.parseInt(getFieldStringFromSLKs(field, level)) == 1;
			}
			catch (final NumberFormatException e) {
				return false;
			}
		}

		public float getFieldAsFloat(final War3ID field, final int level) {
			final Change matchingChange = getMatchingChange(field, level);
			if (matchingChange != null) {
				if ((matchingChange.getVartype() != War3ObjectDataChangeset.VAR_TYPE_REAL)
						&& (matchingChange.getVartype() != War3ObjectDataChangeset.VAR_TYPE_UNREAL)) {
					throw new IllegalStateException(
							"Requested float value of '" + field + "' from '" + this.parentWC3Object.getId()
									+ "', but this field was not a float! vartype=" + matchingChange.getVartype());
				}
				return matchingChange.getRealval();
			}
			// no luck with custom data, look at the standard data
			try {
				return Float.parseFloat(getFieldStringFromSLKs(field, level));
			}
			catch (final NumberFormatException e) {
				return 0;
			}
		}

		public War3ID getAlias() {
			if (this.customUnitData == null) {
				return War3ID.fromString(this.parentWC3Object.getId());
			}
			if (War3ID.NONE.equals(this.customUnitData.getNewId())) {
				return this.customUnitData.getOldId();
			}
			return this.customUnitData.getNewId();
		}

		public War3ID getCode() {
			if (this.customUnitData == null) {
				if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
						|| (MutableObjectData.this.worldEditorDataType == WorldEditorDataType.BUFFS_EFFECTS)) {
					return War3ID.fromString(this.parentWC3Object.getField("code"));
				}
				else {
					return War3ID.fromString(this.parentWC3Object.getId());
				}
			}
			if (War3ID.NONE.equals(this.customUnitData.getNewId())) {
				if ((MutableObjectData.this.worldEditorDataType == WorldEditorDataType.ABILITIES)
						|| (MutableObjectData.this.worldEditorDataType == WorldEditorDataType.BUFFS_EFFECTS)) {
					return War3ID.fromString(this.parentWC3Object.getField("code"));
				}
				else {
					return this.customUnitData.getOldId();
				}
			}
			return this.customUnitData.getOldId();
		}

	}

	private static int asInt(final String text) {
		if ("#VALUE!".equals(text)) {
			return 0;
		}
		return text == null ? 0
				: "".equals(text) ? 0 : "-".equals(text) ? 0 : "_".equals(text) ? 0 : Integer.parseInt(text);
	}

	private static float asFloat(final String text) {
		return text == null ? 0
				: "".equals(text) ? 0 : "-".equals(text) ? 0 : "_".equals(text) ? 0 : Float.parseFloat(text);
	}

	public enum WorldEditorDataType implements CHandle {
		UNITS("w3u"),
		ITEM("w3t"),
		DESTRUCTIBLES("w3b"),
		DOODADS("w3d"),
		ABILITIES("w3a"),
		BUFFS_EFFECTS("w3h"),
		UPGRADES("w3q");

		private String extension;

		private WorldEditorDataType(final String extension) {
			this.extension = extension;
		}

		public String getExtension() {
			return this.extension;
		}

		public static final WorldEditorDataType[] VALUES = values();

		@Override
		public int getHandleId() {
			return ordinal();
		}
	}

	public static String getEditorMetaDataDisplayKey(int level, final GameObject metaData) {
		final int index = metaData.getFieldValue("index");
		String metaDataName = metaData.getField("field");
		final int repeatCount = metaData.getFieldValue("repeat");
		final String upgradeHack = metaData.getField("appendIndex");
		final boolean repeats = (repeatCount > 0) && !"0".equals(upgradeHack);
		final int data = metaData.getFieldValue("data");
		if (data > 0) {
			metaDataName += (char) ('A' + (data - 1));
		}
		if ("1".equals(upgradeHack)) {
			final int upgradeExtensionLevel = level - 1;
			if (upgradeExtensionLevel > 0) {
				metaDataName += Integer.toString(upgradeExtensionLevel);
			}
		}
		else if (repeats && ((index == -1) || (repeatCount >= 10))) {
			if (level == 0) {
				level = 1;
			}
			if (repeatCount >= 10) {
				metaDataName += String.format("%2d", level).replace(' ', '0');
			}
			else {
				metaDataName += Integer.toString(level);
			}
		}
		return metaDataName;
	}

	public static String getDisplayAsRawDataName(final MutableGameObject gameObject) {
		String aliasString = gameObject.getAlias().toString();
		if (!gameObject.getAlias().equals(gameObject.getCode())) {
			aliasString += ":" + gameObject.getCode().toString();
		}
		return aliasString + " (" + gameObject.getName() + ")";
	}
}
