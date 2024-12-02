package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityDropInstant;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemExperienceGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemFigurineSummon;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemLevelGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemManaBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemPermanentLifeGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityEntangledMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.neutral.CAbilityWayGate;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.eattree.CAbilityEatTree;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.moonwell.CAbilityMoonWell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityEntangleGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix.CAbilitySummonPhoenix;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.mountainking.CAbilityThunderBolt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonGrizzly;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonHawk;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.beastmaster.CAbilitySummonQuilbeast;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.darkranger.CAbilityCharm;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.sappers.CAbilityKaboom;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityClusterRockets;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityFactory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.neutral.tinker.CAbilityPocketFactory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter.CAbilityManaBurn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.keeper.CAbilityForceOfNature;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.moonpriestess.CAbilitySummonOwlScout;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.warden.CAbilityBlink;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster.CAbilityWhirlWind;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer.CAbilityChainLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.farseer.CAbilityFeralSpirit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.taurenchieftain.CAbilityWarStomp;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDarkRitual;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDeathCoil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight.CAbilityDeathPact;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionBlight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHoldBurrow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCargoHoldEntangledMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionGoldMineOverlayed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHarvestLumber;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionInvulnerable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemAttackBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemDefenseBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemLifeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemPermanentStatGain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionItemStatBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionPhoenixFire;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionShopPurchaseItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionShopSharing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionStandDown;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.CAbilityTypeDefinitionWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderDupe;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl.CAbilityTypeDefinitionAbilityTemplateBuilder;

public class CAbilityData {

	private final ObjectData abilityData;
	private Map<War3ID, CAbilityType<?>> aliasToAbilityType = new HashMap<>();
	private final Map<War3ID, CAbilityTypeDefinition> codeToAbilityTypeDefinition = new HashMap<>();

	public CAbilityData(final ObjectData abilityData) {
		this.abilityData = abilityData;
		this.aliasToAbilityType = new HashMap<>();
		registerCodes();
	}

	private void registerCodes() {
		// ----Human----
		// Paladin:
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHhb"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityHolyLight(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHds"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDivineShield(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHre"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityResurrect(handleId, alias)));
		// Archmage
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHwe"), new CAbilityTypeDefinitionSpellBase(
//				(handleId, alias) -> new CAbilitySummonWaterElemental(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHbz"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityBlizzard(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHab"), new CAbilityTypeDefinitionSpellBase(
//				(handleId, alias) -> new CAbilityBrilliance(handleId, alias, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHmt"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityMassTeleport(handleId, alias)));
		// Mountain King:
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHtb"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderBolt(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHtc"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderClap(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANfb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityThunderBolt(handleId, alias)));
//		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHav"),
//				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityAvatar(handleId, alias)));

		// Blood Mage:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Apxf"), new CAbilityTypeDefinitionPhoenixFire());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHpx"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonPhoenix(handleId, alias)));

		// ----Orc----
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOsf"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityFeralSpirit(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOcl"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityChainLightning(handleId, alias)));

		// Tauren Chieftain
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOws"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityWarStomp(handleId, alias)));

		// Burrow:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abun"), new CAbilityTypeDefinitionCargoHoldBurrow());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Astd"), new CAbilityTypeDefinitionStandDown());

		// ----Night Elf----
		// Keeper
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEfn"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityForceOfNature(handleId, alias)));

		// Demon Hunter:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEim"), new CAbilityTypeDefinitionImmolation());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEmb"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityManaBurn(handleId, alias)));

		// Moon Priestess
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEst"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonOwlScout(handleId, alias)));

		// Warden
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AEbl"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityBlink(handleId, alias)));

		// ----Undead----
		// Death Knight
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdc"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDeathCoil(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdp"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDeathPact(handleId, alias)));

		// Light
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUdr"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityDarkRitual(handleId, alias)));

		// Entangled Mine:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aenc"),
				new CAbilityTypeDefinitionCargoHoldEntangledMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aent"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityEntangleGoldMine(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aegm"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityEntangledMine(handleId, alias, alias)));

		// Ancients:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aeat"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityEatTree(handleId, alias)));

		// Moon Well
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ambt"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityMoonWell(handleId, alias)));
		// ----Neutral----
		// Dark Ranger:
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANch"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityCharm(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIco"), // Item Command
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityCharm(handleId, alias)));
		// Tinker
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANcs"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityClusterRockets(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANfy"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityFactory(handleId, alias, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsy"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityPocketFactory(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Asds"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityKaboom(handleId, alias)));

		// Beastmaster
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsg"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonGrizzly(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsq"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonQuilbeast(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANsw"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilitySummonHawk(handleId, alias)));

		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AHca"), new CAbilityTypeDefinitionColdArrows());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agld"), new CAbilityTypeDefinitionGoldMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Agl2"), new CAbilityTypeDefinitionGoldMineOverlayed());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abgm"), new CAbilityTypeDefinitionBlightedGoldMine());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Abli"), new CAbilityTypeDefinitionBlight());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aaha"), new CAbilityTypeDefinitionAcolyteHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Artn"), new CAbilityTypeDefinitionReturnResources());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahar"), new CAbilityTypeDefinitionHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Awha"), new CAbilityTypeDefinitionWispHarvest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Ahrl"), new CAbilityTypeDefinitionHarvestLumber());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ANcl"), new CAbilityTypeDefinitionChannelTest());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AUcs"), new CAbilityTypeDefinitionCarrionSwarmDummy());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AInv"), new CAbilityTypeDefinitionInventory());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Arep"), new CAbilityTypeDefinitionHumanRepair());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aren"), new CAbilityTypeDefinitionRepair());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Arst"), new CAbilityTypeDefinitionRepair());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Avul"), new CAbilityTypeDefinitionInvulnerable());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Apit"), new CAbilityTypeDefinitionShopPurchaseItem());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aneu"), new CAbilityTypeDefinitionNeutralBuilding());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aall"), new CAbilityTypeDefinitionShopSharing());
		// this.codeToAbilityTypeDefinition.put(War3ID.fromString("Acoi"), new
		// CAbilityTypeDefinitionCoupleInstant());
		this.codeToAbilityTypeDefinition.put(CAbilityItemHeal.CODE, new CAbilityTypeDefinitionItemHeal());
		this.codeToAbilityTypeDefinition.put(CAbilityItemManaRegain.CODE, new CAbilityTypeDefinitionItemManaRegain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIat"), new CAbilityTypeDefinitionItemAttackBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIab"), new CAbilityTypeDefinitionItemStatBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIim"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIsm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIam"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIxm"),
				new CAbilityTypeDefinitionItemPermanentStatGain());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIde"), new CAbilityTypeDefinitionItemDefenseBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIml"), new CAbilityTypeDefinitionItemLifeBonus());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AImm"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityItemManaBonus(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIfs"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemFigurineSummon(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AImi"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemPermanentLifeGain(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIem"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityItemExperienceGain(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AIlm"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityItemLevelGain(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Acar"), new CAbilityTypeDefinitionCargoHold());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aloa"), new CAbilityTypeDefinitionLoad());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Adro"), new CAbilityTypeDefinitionDrop());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Adri"), new CAbilityTypeDefinitionSpellBase(
				(handleId, alias) -> new CAbilityDropInstant(handleId, alias, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Aroo"), new CAbilityTypeDefinitionRoot());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("ARal"), new CAbilityTypeDefinitionRally());
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("Awrp"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityWayGate(handleId, alias)));
		this.codeToAbilityTypeDefinition.put(War3ID.fromString("AOww"),
				new CAbilityTypeDefinitionSpellBase((handleId, alias) -> new CAbilityWhirlWind(handleId, alias)));

		System.err.println("========================================================================");
		System.err.println("Starting to load ability builder");
		System.err.println("========================================================================");

		if (true) {
			AbilityBuilderParserUtil.loadAbilityBuilderFiles(behavior -> {
				if (behavior.getType().equals(AbilityBuilderType.TEMPLATE)) {
					for (final AbilityBuilderDupe dupe : behavior.getIds()) {
						this.codeToAbilityTypeDefinition.put(War3ID.fromString(dupe.getId()),
								new CAbilityTypeDefinitionAbilityTemplateBuilder(behavior));
					}
				}
				else {
					for (final AbilityBuilderDupe dupe : behavior.getIds()) {
						final AbilityBuilderConfiguration config = new AbilityBuilderConfiguration(behavior, dupe);
						this.codeToAbilityTypeDefinition.put(War3ID.fromString(config.getId()),
								config.createDefinition());
					}
				}
			});
		}

		System.err.println("========================================================================");
		System.err.println("registered abilities");
		System.err.println("========================================================================");

	}

	public void registerJassType(final War3ID war3id, final CAbilityTypeJassDefinition whichAbilityType) {
		this.codeToAbilityTypeDefinition.put(war3id, whichAbilityType);
	}

	public void registerAbilityBuilderType(final War3ID war3id, final AbilityBuilderConfiguration configuration) {
		this.codeToAbilityTypeDefinition.put(war3id, configuration.createDefinition());
	}

	public CAbilityType<?> getAbilityType(final War3ID alias) {
		CAbilityType<?> abilityType = this.aliasToAbilityType.get(alias);
		if (abilityType == null) {
			final GameObject gameObject = this.abilityData.get(alias);
			if (gameObject == null) {
				return null;
			}
			final War3ID code = War3ID.fromString(gameObject.readSLKTag("code"));
			final CAbilityTypeDefinition abilityTypeDefinition = this.codeToAbilityTypeDefinition.get(code);
			if (abilityTypeDefinition != null) {
				abilityType = abilityTypeDefinition.createAbilityType(alias, gameObject);
				this.aliasToAbilityType.put(alias, abilityType);
			}
		}
		return abilityType;
	}

	public int getHeroRequiredLevel(final CSimulation game, final War3ID alias, final int currentLevelOfAbility) {
		// TODO maybe use CAbilityType for this to avoid hashtable lookups and just do
		// fast symbol table resolution.
		// (i.e. like all other fields of CAbilityType). For now I didn't bother because
		// I wanted to just have this working.
		final GameObject mutableGameObject = this.abilityData.get(alias);
		int levelSkip = mutableGameObject.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL_SKIP, 0);
		if (levelSkip == 0) {
			levelSkip = game.getGameplayConstants().getHeroAbilityLevelSkip();
		}
		final int baseRequiredLevel = mutableGameObject.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL, 0);
		return baseRequiredLevel + (currentLevelOfAbility * levelSkip);
	}

	public CAbility createAbility(final War3ID abilityId, final int handleId) {
		final CAbilityType<?> abilityType = getAbilityType(abilityId);
		if (abilityType != null) {
			return abilityType.createAbility(handleId);
		}
		return new CAbilityGenericDoNothing(abilityId, abilityId, handleId);
	}
}
