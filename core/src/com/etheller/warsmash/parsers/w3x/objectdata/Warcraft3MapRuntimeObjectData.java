package com.etheller.warsmash.parsers.w3x.objectdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.units.StandardObjectData.WarcraftData;
import com.etheller.warsmash.units.collapsed.CollapsedObjectData;
import com.etheller.warsmash.units.custom.ObjectDataChangeEntry;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.units.custom.WTSFile;
import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;
import com.google.common.io.LittleEndianDataInputStream;

public final class Warcraft3MapRuntimeObjectData {
	private final ObjectData units;
	private final ObjectData items;
	private final ObjectData destructibles;
	private final ObjectData doodads;
	private final ObjectData abilities;
	private final ObjectData buffs;
	private final ObjectData upgrades;
	private final DataTable standardUpgradeEffectMeta;
	private final List<ObjectData> datas;
	private transient Map<WorldEditorDataType, ObjectData> typeToData = new HashMap<>();
	private final WTS wts;

	public Warcraft3MapRuntimeObjectData(final ObjectData units, final ObjectData items, final ObjectData destructibles,
			final ObjectData doodads, final ObjectData abilities, final ObjectData buffs, final ObjectData upgrades,
			final DataTable standardUpgradeEffectMeta, final WTS wts) {
		this.units = units;
		this.items = items;
		this.destructibles = destructibles;
		this.doodads = doodads;
		this.abilities = abilities;
		this.buffs = buffs;
		this.upgrades = upgrades;
		this.standardUpgradeEffectMeta = standardUpgradeEffectMeta;
		this.datas = new ArrayList<>();
		this.datas.add(units);
		this.typeToData.put(WorldEditorDataType.UNITS, units);
		this.datas.add(items);
		this.typeToData.put(WorldEditorDataType.ITEM, items);
		this.datas.add(destructibles);
		this.typeToData.put(WorldEditorDataType.DESTRUCTIBLES, destructibles);
		this.datas.add(doodads);
		this.typeToData.put(WorldEditorDataType.DOODADS, doodads);
		this.datas.add(abilities);
		this.typeToData.put(WorldEditorDataType.ABILITIES, abilities);
		this.datas.add(buffs);
		this.typeToData.put(WorldEditorDataType.BUFFS_EFFECTS, buffs);
		this.datas.add(upgrades);
		this.typeToData.put(WorldEditorDataType.UPGRADES, upgrades);
		for (final ObjectData data : this.datas) {
		}
		this.wts = wts;
	}

	public ObjectData getDataByType(final WorldEditorDataType type) {
		return this.typeToData.get(type);
	}

	public ObjectData getUnits() {
		return this.units;
	}

	public ObjectData getItems() {
		return this.items;
	}

	public ObjectData getDestructibles() {
		return this.destructibles;
	}

	public ObjectData getDoodads() {
		return this.doodads;
	}

	public ObjectData getAbilities() {
		return this.abilities;
	}

	public ObjectData getBuffs() {
		return this.buffs;
	}

	public ObjectData getUpgrades() {
		return this.upgrades;
	}

	public DataTable getStandardUpgradeEffectMeta() {
		return this.standardUpgradeEffectMeta;
	}

	public List<ObjectData> getDatas() {
		return this.datas;
	}

	public WTS getWts() {
		return this.wts;
	}

	private static WTS loadWTS(final DataSource dataSource, final String name) throws IOException {
		final WTS wts = dataSource.has(name) ? new WTSFile(dataSource.getResourceAsStream(name)) : WTS.DO_NOTHING;
		return wts;
	}

	public static WTS loadWTS(final DataSource dataSource) throws IOException {
		return loadWTS(dataSource, "war3map.wts");
	}

	public static WTS loadCampaignWTS(final DataSource dataSource) throws IOException {
		return loadWTS(dataSource, "war3campaign.wts");
	}

	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS)
			throws IOException {
		final WTS wts = loadWTS(dataSource);
		return load(dataSource, inlineWTS, wts);
	}

	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS,
			final WTS wts) throws IOException {
		final WTS campaignWTS = loadCampaignWTS(dataSource);
		return load(dataSource, inlineWTS, wts, campaignWTS);
	}

	public static Warcraft3MapRuntimeObjectData load(final DataSource dataSource, final boolean inlineWTS,
			final WTS wts, final WTS campaignWTS) throws IOException {

		final StandardObjectData standardObjectData = new StandardObjectData(dataSource);
		final WarcraftData standardUnits = standardObjectData.getStandardUnits();
		final WarcraftData standardItems = standardObjectData.getStandardItems();
		final WarcraftData standardDoodads = standardObjectData.getStandardDoodads();
		final WarcraftData standardDestructables = standardObjectData.getStandardDestructables();
		final WarcraftData abilities = standardObjectData.getStandardAbilities();
		final WarcraftData standardAbilityBuffs = standardObjectData.getStandardAbilityBuffs();
		final WarcraftData standardUpgrades = standardObjectData.getStandardUpgrades();

		final DataTable standardUnitMeta = standardObjectData.getStandardUnitMeta();
		final DataTable standardDoodadMeta = standardObjectData.getStandardDoodadMeta();
		final DataTable standardDestructableMeta = standardObjectData.getStandardDestructableMeta();
		final DataTable abilityMeta = standardObjectData.getStandardAbilityMeta();
		final DataTable standardAbilityBuffMeta = standardObjectData.getStandardAbilityBuffMeta();
		final DataTable standardUpgradeMeta = standardObjectData.getStandardUpgradeMeta();
		final DataTable standardUpgradeEffectMeta = standardObjectData.getStandardUpgradeEffectMeta();

		final War3ObjectDataChangeset unitChangeset = new War3ObjectDataChangeset('u');
		final War3ObjectDataChangeset itemChangeset = new War3ObjectDataChangeset('t');
		final War3ObjectDataChangeset doodadChangeset = new War3ObjectDataChangeset('d');
		final War3ObjectDataChangeset destructableChangeset = new War3ObjectDataChangeset('b');
		final War3ObjectDataChangeset abilityChangeset = new War3ObjectDataChangeset('a');
		final War3ObjectDataChangeset buffChangeset = new War3ObjectDataChangeset('h');
		final War3ObjectDataChangeset upgradeChangeset = new War3ObjectDataChangeset('q');

		if (dataSource.has("war3map.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3u")), wts,
					inlineWTS);
			// push unit changes to items.... as a Reign of Chaos support...
			Iterator<Entry<War3ID, ObjectDataChangeEntry>> entryIterator = unitChangeset.getOriginal().iterator();
			while (entryIterator.hasNext()) {
				final Entry<War3ID, ObjectDataChangeEntry> entry = entryIterator.next();
				final String rawcodeString = entry.toString();
				final String oldIdString = entry.getValue().getOldId().toString();
				if ((standardUnits.get(oldIdString) == null) && (standardItems.get(oldIdString) != null)) {
					itemChangeset.getOriginal().put(entry.getKey(), entry.getValue());
					entryIterator.remove();
				}
			}
			entryIterator = unitChangeset.getCustom().iterator();
			while (entryIterator.hasNext()) {
				final Entry<War3ID, ObjectDataChangeEntry> entry = entryIterator.next();
				final String rawcodeString = entry.toString();
				final String oldIdString = entry.getValue().getOldId().toString();
				if ((standardUnits.get(oldIdString) == null) && (standardItems.get(oldIdString) != null)) {
					itemChangeset.getCustom().put(entry.getKey(), entry.getValue());
					entryIterator.remove();
				}
			}
		}
		if (dataSource.has("war3mapSkin.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3u")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3u")),
					campaignWTS, inlineWTS);
		}
		// ================== REMOVE LATER =====================
		if (dataSource.has("war3mod.w3u")) {
			unitChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mod.w3u")), wts,
					inlineWTS);
		}
		// =====================================================
		if (dataSource.has("war3map.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3t")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3t")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3t")) {
			itemChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3t")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3d")) {
			doodadChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3d")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3d")) {
			doodadChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3d")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3b")) {
			destructableChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3b")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3b")) {
			destructableChangeset.load(
					new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3b")), wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3b")) {
			destructableChangeset.load(
					new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3b")), campaignWTS,
					inlineWTS);
		}
		if (dataSource.has("war3map.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3a")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3a")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3a")) {
			abilityChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3a")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3h")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3h")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3campaign.w3h")) {
			buffChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3h")),
					campaignWTS, inlineWTS);
		}
		if (dataSource.has("war3map.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3map.w3q")), wts,
					inlineWTS);
		}
		if (dataSource.has("war3mapSkin.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3mapSkin.w3q")),
					wts, inlineWTS);
		}
		if (dataSource.has("war3campaign.w3q")) {
			upgradeChangeset.load(new LittleEndianDataInputStream(dataSource.getResourceAsStream("war3campaign.w3q")),
					campaignWTS, inlineWTS);
		}

		final WorldEditStrings worldEditStrings = standardObjectData.getWorldEditStrings();
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.UNITS, standardUnits, standardUnitMeta,
				unitChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.ITEM, standardItems, standardUnitMeta,
				itemChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.DOODADS, standardDoodads, standardDoodadMeta,
				doodadChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.DESTRUCTIBLES, standardDestructables,
				standardDestructableMeta, destructableChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.ABILITIES, abilities, abilityMeta,
				abilityChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.BUFFS_EFFECTS, standardAbilityBuffs,
				standardAbilityBuffMeta, buffChangeset);
		CollapsedObjectData.apply(worldEditStrings, WorldEditorDataType.UPGRADES, standardUpgrades, standardUpgradeMeta,
				upgradeChangeset);

		return new Warcraft3MapRuntimeObjectData(standardUnits, standardItems, standardDestructables, standardDoodads,
				abilities, standardAbilityBuffs, standardUpgrades, standardUpgradeEffectMeta, wts);
	}
}