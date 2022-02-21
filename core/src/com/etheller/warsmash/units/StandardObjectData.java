package com.etheller.warsmash.units;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.util.WorldEditStrings;

public class StandardObjectData {
	private WorldEditStrings worldEditStrings;
	private DataSource source;

	public StandardObjectData(final DataSource dataSource) {
		this.source = dataSource;
		this.worldEditStrings = new WorldEditStrings(dataSource);
	}

	public WarcraftData getStandardUnits() {

		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable unitAbilities = new DataTable(this.worldEditStrings);
		final DataTable unitBalance = new DataTable(this.worldEditStrings);
		final DataTable unitData = new DataTable(this.worldEditStrings);
		final DataTable unitUI = new DataTable(this.worldEditStrings);
		final DataTable unitWeapons = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUnitStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUnitFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUnitStrings.txt"), true);

			unitAbilities.readSLK(this.source.getResourceAsStream("Units\\UnitAbilities.slk"));

			unitBalance.readSLK(this.source.getResourceAsStream("Units\\UnitBalance.slk"));

			unitData.readSLK(this.source.getResourceAsStream("Units\\UnitData.slk"));

			unitUI.readSLK(this.source.getResourceAsStream("Units\\UnitUI.slk"));

			unitWeapons.readSLK(this.source.getResourceAsStream("Units\\UnitWeapons.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Units\\UnitSkin.txt");
			if (unitSkin != null) {
				profile.readTXT(unitSkin, true);
			}
			final InputStream unitWeaponsFunc = this.source.getResourceAsStream("Units\\UnitWeaponsFunc.txt");
			if (unitWeaponsFunc != null) {
				profile.readTXT(unitWeaponsFunc, true);
			}
			final InputStream unitWeaponsSkin = this.source.getResourceAsStream("Units\\UnitWeaponsSkin.txt");
			if (unitWeaponsSkin != null) {
				profile.readTXT(unitWeaponsSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData();

		units.add(profile, "Profile", false);
		units.add(unitAbilities, "UnitAbilities", true);
		units.add(unitBalance, "UnitBalance", true);
		units.add(unitData, "UnitData", true);
		units.add(unitUI, "UnitUI", true);
		units.add(unitWeapons, "UnitWeapons", true);

		return units;
	}

	public WarcraftData getStandardItems() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable itemData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemStrings.txt"), true);
			itemData.readSLK(this.source.getResourceAsStream("Units\\ItemData.slk"));
			final InputStream itemSkin = this.source.getResourceAsStream("Units\\ItemSkin.txt");
			if (itemSkin != null) {
				profile.readTXT(itemSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData();

		units.add(profile, "Profile", false);
		units.add(itemData, "ItemData", true);

		return units;
	}

	public WarcraftData getStandardDestructables() {
		final DataTable destructableData = new DataTable(this.worldEditStrings);

		try {
			destructableData.readSLK(this.source.getResourceAsStream("Units\\DestructableData.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Units\\DestructableSkin.txt");
			if (unitSkin != null) {
				destructableData.readTXT(unitSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData();

		units.add(destructableData, "DestructableData", true);

		return units;
	}

	public WarcraftData getStandardDoodads() {

		final DataTable destructableData = new DataTable(this.worldEditStrings);

		try {
			destructableData.readSLK(this.source.getResourceAsStream("Doodads\\Doodads.slk"));
			final InputStream unitSkin = this.source.getResourceAsStream("Doodads\\DoodadSkins.txt");
			if (unitSkin != null) {
				destructableData.readTXT(unitSkin, true);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData();

		units.add(destructableData, "DoodadData", true);

		return units;
	}

	public DataTable getStandardUnitMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UnitMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardDestructableMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\DestructableMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardDoodadMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Doodads\\DoodadMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public WarcraftData getStandardAbilities() {

		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable abilityData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityStrings.txt"), true);

			final InputStream unitSkin = this.source.getResourceAsStream("Units\\AbilitySkin.txt");
			if (unitSkin != null) {
				profile.readTXT(unitSkin, true);
			}

			abilityData.readSLK(this.source.getResourceAsStream("Units\\AbilityData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData abilities = new WarcraftData();

		abilities.add(profile, "Profile", false);
		abilities.add(abilityData, "AbilityData", true);

		return abilities;
	}

	public WarcraftData getStandardAbilityBuffs() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable abilityData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CommonAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadAbilityStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\ItemAbilityStrings.txt"), true);

			abilityData.readSLK(this.source.getResourceAsStream("Units\\AbilityBuffData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData abilities = new WarcraftData();

		abilities.add(profile, "Profile", false);
		abilities.add(abilityData, "AbilityData", true);

		return abilities;
	}

	public WarcraftData getStandardUpgrades() {
		final DataTable profile = new DataTable(this.worldEditStrings);
		final DataTable upgradeData = new DataTable(this.worldEditStrings);

		try {
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\CampaignUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\HumanUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NeutralUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\NightElfUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\OrcUpgradeStrings.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUpgradeFunc.txt"), true);
			profile.readTXT(this.source.getResourceAsStream("Units\\UndeadUpgradeStrings.txt"), true);

			upgradeData.readSLK(this.source.getResourceAsStream("Units\\UpgradeData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final WarcraftData units = new WarcraftData();

		units.add(profile, "Profile", false);
		units.add(upgradeData, "UpgradeData", true);

		return units;
	}

	public DataTable getStandardUpgradeMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UpgradeMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardUpgradeEffectMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\UpgradeEffectMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardAbilityMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\AbilityMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getStandardAbilityBuffMeta() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readSLK(this.source.getResourceAsStream("Units\\AbilityBuffMetaData.slk"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getUnitEditorData() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readTXT(this.source.getResourceAsStream("UI\\UnitEditorData.txt"), true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public DataTable getWorldEditData() {
		final DataTable unitMetaData = new DataTable(this.worldEditStrings);
		try {
			unitMetaData.readTXT(this.source.getResourceAsStream("UI\\WorldEditData.txt"), true);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return unitMetaData;
	}

	public WorldEditStrings getWorldEditStrings() {
		return this.worldEditStrings;
	}

	public static class WarcraftData implements ObjectData {
		WorldEditStrings worldEditStrings;
		List<DataTable> tables = new ArrayList<>();
		Map<StringKey, DataTable> tableMap = new HashMap<>();
		Map<StringKey, WarcraftObject> units = new HashMap<>();

		public WarcraftData(final WorldEditStrings worldEditStrings) {
			this.worldEditStrings = worldEditStrings;
		}

		@Override
		public String getLocalizedString(final String key) {
			return this.worldEditStrings.getString(key);
		}

		public void add(final DataTable data, final String name, final boolean canMake) {
			this.tableMap.put(new StringKey(name), data);
			this.tables.add(data);
			if (canMake) {
				for (final String id : data.keySet()) {
					if (!this.units.containsKey(new StringKey(id))) {
						this.units.put(new StringKey(id), new WarcraftObject(data.get(id).getId(), this));
					}
				}
			}
		}

		public WarcraftData() {
		}

		public List<DataTable> getTables() {
			return this.tables;
		}

		public void setTables(final List<DataTable> tables) {
			this.tables = tables;
		}

		public DataTable getTable(final String tableName) {
			return this.tableMap.get(new StringKey(tableName));
		}

		@Override
		public GameObject get(final String id) {
			return this.units.get(new StringKey(id));
		}

		@Override
		public void setValue(final String id, final String field, final String value) {
			get(id).setField(field, value);
		}

		@Override
		public Set<String> keySet() {
			final Set<String> keySet = new HashSet<>();
			for (final StringKey key : this.units.keySet()) {
				keySet.add(key.getString());
			}
			return keySet;
		}

		public void cloneUnit(final String parentId, final String cloneId) {
			for (final DataTable table : this.tables) {
				final Element parentEntry = table.get(parentId);
				final LMUnit cloneUnit = new LMUnit(cloneId, table);
				for (final String key : parentEntry.keySet()) {
					cloneUnit.setField(key, parentEntry.getField(key));
				}
				table.put(cloneId, cloneUnit);
			}
			this.units.put(new StringKey(cloneId), new WarcraftObject(cloneId, this));
		}
	}

	public static class WarcraftObject implements GameObject {
		String id;
		WarcraftData dataSource;

		public WarcraftObject(final String id, final WarcraftData dataSource) {
			this.id = id;
			this.dataSource = dataSource;
		}

		@Override
		public void setField(final String field, final String value, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					element.setField(field, value, index);
					return;
				}
			}
		}

		@Override
		public String getField(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getField(field, index);
				}
			}
			return "";
		}

		@Override
		public int getFieldValue(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldValue(field, index);
				}
			}
			return 0;
		}

		@Override
		public void setField(final String field, final String value) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					element.setField(field, value);
					return;
				}
			}
			throw new IllegalArgumentException("no field");
		}

		@Override
		public String getField(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getField(field);
				}
			}
			return "";
		}

		@Override
		public int getFieldValue(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldValue(field);
				}
			}
			return 0;
		}

		@Override
		public float getFieldFloatValue(final String field) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldFloatValue(field);
				}
			}
			return 0f;
		}

		@Override
		public float getFieldFloatValue(final String field, final int index) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldFloatValue(field, index);
				}
			}
			return 0f;
		}

		/*
		 * (non-Javadoc) I'm not entirely sure this is still safe to use
		 *
		 * @see com.hiveworkshop.wc3.units.GameObject#getFieldAsList(java.lang. String)
		 */
		@Override
		public List<? extends GameObject> getFieldAsList(final String field, final ObjectData objectData) {
			for (final DataTable table : this.dataSource.getTables()) {
				final Element element = table.get(this.id);
				if ((element != null) && element.hasField(field)) {
					return element.getFieldAsList(field, objectData);
				}
			}
			return new ArrayList<>();// empty list if not found
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public ObjectData getTable() {
			return this.dataSource;
		}

		@Override
		public String getLegacyName() {
			final DataTable dataTable = this.dataSource.tableMap.get(new StringKey("UnitUI"));
			if (dataTable != null) {
				final Element element = dataTable.get(this.id);
				if (element != null) {
					return element.getField("name");
				}
				else {
					return null;
				}
			}
			return null;
		}

		// @Override
		// public String getName() {
		// return dataSource.profile.get(id).getName();
		// }
		@Override
		public String getName() {
			String name = getField("Name");
			boolean nameKnown = name.length() >= 1;
			if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
				final WarcraftObject other = (WarcraftObject) this.dataSource.get(getField("code").substring(0, 4));
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
					name = this.dataSource.getLocalizedString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						if (subName.startsWith("WESTRING")) {
							name += this.dataSource.getLocalizedString(subName);
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
				name = this.dataSource.getLocalizedString("WESTRING_UNKNOWN") + " '" + getId() + "'";
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
					suf = this.dataSource.getLocalizedString(suf);
				}
				if (!suf.startsWith(" ")) {
					name += " ";
				}
				name += suf;
			}
			return name;
		}

		BufferedImage storedImage = null;
		String storedImagePath = null;

		@Override
		public Set<String> keySet() {
			final Set<String> keySet = new HashSet<>();
			for (final DataTable table : this.dataSource.tables) {
				keySet.addAll(table.get(this.id).keySet());
			}
			return keySet;
		}
	}

	private StandardObjectData() {
	}
}
