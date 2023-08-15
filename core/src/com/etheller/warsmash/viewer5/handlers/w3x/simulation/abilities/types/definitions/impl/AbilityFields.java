package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.util.War3ID;

public interface AbilityFields {
	public static final War3ID TARGETS_ALLOWED = War3ID.fromString("atar");
	public static final War3ID LEVELS = War3ID.fromString("alev");
	public static final War3ID CAST_RANGE = War3ID.fromString("aran");
	public static final War3ID DURATION = War3ID.fromString("adur");
	public static final War3ID HERO_DURATION = War3ID.fromString("ahdu");
	public static final War3ID AREA = War3ID.fromString("aare");
	public static final War3ID MANA_COST = War3ID.fromString("amcs");
	public static final War3ID COOLDOWN = War3ID.fromString("acdn");
	public static final War3ID CASTING_TIME = War3ID.fromString("acas");
	public static final War3ID AREA_OF_EFFECT = AREA;
	public static final War3ID BUFF = War3ID.fromString("abuf");
	public static final War3ID EFFECT = War3ID.fromString("aeff");

	public static final War3ID ANIM_NAMES = War3ID.fromString("aani");

	public static final War3ID PROJECTILE_SPEED = War3ID.fromString("amsp");
	public static final War3ID PROJECTILE_HOMING_ENABLED = War3ID.fromString("amho");

	public static final War3ID LIGHTNING = War3ID.fromString("alig");
	public static final War3ID REQUIRED_LEVEL = War3ID.fromString("arlv");
	public static final War3ID REQUIRED_LEVEL_SKIP = War3ID.fromString("alsk");


	final class FingerOfDeathArchimonde {
		private FingerOfDeathArchimonde() {}

		public static final War3ID GRAPHIC_DELAY = War3ID.fromString("Nfd1");
		public static final War3ID DAMAGE = War3ID.fromString("Nfd3");
		public static final War3ID GRAPHIC_DURATION = War3ID.fromString("Nfd2");
	}
	final class HealingSalve {
		private HealingSalve() {}

		public static final War3ID NO_TARGET_REQUIRED = War3ID.fromString("irl4");
		public static final War3ID ALLOW_WHEN_FULL = War3ID.fromString("irl3");
		public static final War3ID DISPEL_ON_ATTACK = War3ID.fromString("irl5");
		public static final War3ID MANA_REGENERATED = War3ID.fromString("irl2");
		public static final War3ID LIFE_REGENERATED = War3ID.fromString("irl1");
	}
	final class Invisibility {
		private Invisibility() {}

		public static final War3ID TRANSITION_TIME_SECONDS = War3ID.fromString("Ivs1");
	}
	final class LiquidFire {
		private LiquidFire() {}

		public static final War3ID REPAIRS_ALLOWED = War3ID.fromString("liq4");
		public static final War3ID MOVEMENT_SPEED_REDUCTION = War3ID.fromString("liq2");
		public static final War3ID ATTACK_SPEED_REDUCTION = War3ID.fromString("liq3");
		public static final War3ID EXTRA_DAMAGE_PER_SECOND = War3ID.fromString("liq1");
	}
	final class Pillage {
		private Pillage() {}

		public static final War3ID ACCUMULATION_STEP = War3ID.fromString("Sal2");
		public static final War3ID SALVAGE_COST_RATIO = War3ID.fromString("Sal1");
	}
	final class DivineShield {
		private DivineShield() {}

		public static final War3ID CAN_DEACTIVATE = War3ID.fromString("Hds1");
	}
	final class ManaShield {
		private ManaShield() {}

		public static final War3ID MANA_PER_HIT_POINT = War3ID.fromString("Nms1");
		public static final War3ID DAMAGE_ABSORBED_PERCENT = War3ID.fromString("Nms2");
	}
	final class MineExplodingGoblinLandMine {
		private MineExplodingGoblinLandMine() {}

		public static final War3ID ACTIVATION_DELAY = War3ID.fromString("Min1");
		public static final War3ID INVISIBILITY_TRANSITION_TIME = War3ID.fromString("Min2");
	}
	final class BuildTinyCastle {
		private BuildTinyCastle() {}

		public static final War3ID UNIT_CREATED_PER_PLAYER_RACE = War3ID.fromString("Ibl1");
	}
	final class CallToArmsPeasant {
		private CallToArmsPeasant() {}

		public static final War3ID NORMAL_FORM_UNIT = War3ID.fromString("Mil1");
		public static final War3ID ALTERNATE_FORM_UNIT = War3ID.fromString("Mil2");
	}
	final class Vengeance {
		private Vengeance() {}

		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Esvu");
		public static final War3ID NUMBER_OF_SUMMONED_UNITS = War3ID.fromString("Esv1");
	}
	final class GoldMineAbility {
		private GoldMineAbility() {}

		public static final War3ID MINING_DURATION = War3ID.fromString("Gld2");
		public static final War3ID MAX_GOLD = War3ID.fromString("Gld1");
		public static final War3ID MINING_CAPACITY = War3ID.fromString("Gld3");
	}
	final class Volcano {
		private Volcano() {}

		public static final War3ID WAVE_COUNT = War3ID.fromString("Nvc2");
		public static final War3ID ROCK_RING_COUNT = War3ID.fromString("Nvc1");
		public static final War3ID BUILDING_DAMAGE_FACTOR = War3ID.fromString("Nvc4");
		public static final War3ID WAVE_INTERVAL = War3ID.fromString("Nvc3");
		public static final War3ID HALF_DAMAGE_FACTOR = War3ID.fromString("Nvc6");
		public static final War3ID FULL_DAMAGE_AMOUNT = War3ID.fromString("Nvc5");
		public static final War3ID DESTRUCTIBLE_ID = War3ID.fromString("Nvcu");
	}
	final class SpellImmunity {
		private SpellImmunity() {}

		public static final War3ID MAGIC_DAMAGE_FACTOR = War3ID.fromString("mim1");
	}
	final class AttributeBonus {
		private AttributeBonus() {}

		public static final War3ID AGILITY_BONUS = War3ID.fromString("Iagi");
		public static final War3ID STRENGTH_BONUS = War3ID.fromString("Istr");
		public static final War3ID HIDE_BUTTON = War3ID.fromString("Ihid");
		public static final War3ID INTELLIGENCE_BONUS = War3ID.fromString("Iint");
	}
	final class ItemAttackBlackArrowBonus {
		private ItemAttackBlackArrowBonus() {}

		public static final War3ID EFFECT_ABILITY = War3ID.fromString("Iobu");
		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Idam");
		public static final War3ID CHANCE_TO_HIT_UNITS_PERCENT = War3ID.fromString("Iob2");
		public static final War3ID CHANCE_TO_HIT_HEROS_PERCENT = War3ID.fromString("Iob3");
		public static final War3ID CHANCE_TO_HIT_SUMMONS_PERCENT = War3ID.fromString("Iob4");
		public static final War3ID ENABLED_ATTACK_INDEX = War3ID.fromString("Iob5");
	}
	final class MindRot {
		private MindRot() {}

		public static final War3ID MANA_DRAINED_PER_SECOND = War3ID.fromString("Nmr1");
	}
	final class Banish {
		private Banish() {}

		public static final War3ID ATTACK_SPEED_REDUCTION_PERCENT = War3ID.fromString("Hbn2");
		public static final War3ID MOVEMENT_SPEED_REDUCTION_PERCENT = War3ID.fromString("Hbn1");
	}
	final class SleepAlways {
		private SleepAlways() {}

		public static final War3ID SLEEP_ONCE = War3ID.fromString("sla1");
		public static final War3ID ALLOW_ON_ANY_PLAYER_SLOT = War3ID.fromString("sla2");
	}
	final class HurlBoulder {
		private HurlBoulder() {}

		public static final War3ID DAMAGE = War3ID.fromString("Ctb1");
	}
	final class FlakCannons {
		private FlakCannons() {}

		public static final War3ID SMALL_DAMAGE_AMOUNT = War3ID.fromString("flk5");
		public static final War3ID MEDIUM_DAMAGE_RADIUS = War3ID.fromString("flk1");
		public static final War3ID SMALL_DAMAGE_RADIUS = War3ID.fromString("flk2");
		public static final War3ID FULL_DAMAGE_AMOUNT = War3ID.fromString("flk3");
		public static final War3ID MEDIUM_DAMAGE_AMOUNT = War3ID.fromString("flk4");
	}
	final class Unknown {
		private Unknown() {}

		public static final War3ID REQUIRED_UNIT_TYPE = War3ID.fromString("tpi1");
		public static final War3ID CONVERTED_UNIT_TYPE = War3ID.fromString("tpi2");
	}
	final class ItemManaBonus {
		private ItemManaBonus() {}

		public static final War3ID MAX_MANA_GAINED = War3ID.fromString("Iman");
	}
	final class SpiritLink {
		private SpiritLink() {}

		public static final War3ID MAXIMUM_NUMBER_OF_TARGETS = War3ID.fromString("spl2");
		public static final War3ID DISTRIBUTED_DAMAGE_FACTOR = War3ID.fromString("spl1");
	}
	final class CleavingAttack {
		private CleavingAttack() {}

		public static final War3ID DISTRIBUTED_DAMAGE_FACTOR = War3ID.fromString("nca1");
	}
	final class PossessionChanneling {
		private PossessionChanneling() {}

		public static final War3ID DAMAGE_AMPLIFICATION = War3ID.fromString("Pos2");
		public static final War3ID TARGET_IS_INVULNERABLE = War3ID.fromString("Pos3");
		public static final War3ID TARGET_IS_MAGIC_IMMUNE = War3ID.fromString("Pos4");
	}
	final class Possession {
		private Possession() {}

		public static final War3ID MAXIMUM_CREEP_LEVEL = War3ID.fromString("Pos1");
	}
	final class LightningShieldItem {
		private LightningShieldItem() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Idps");
	}
	final class SlamNeutralHostile {
		private SlamNeutralHostile() {}

		public static final War3ID DAMAGE = War3ID.fromString("Ctc1");
		public static final War3ID EXTRA_DAMAGE_TO_TARGET = War3ID.fromString("Ctc2");
		public static final War3ID MOVEMENT_SPEED_REDUCTION = War3ID.fromString("Ctc3");
		public static final War3ID ATTACK_SPEED_REDUCTION = War3ID.fromString("Ctc4");
	}
	final class KaboomGoblinSapper {
		private KaboomGoblinSapper() {}

		public static final War3ID BUILDING_DAMAGE_FACTOR = War3ID.fromString("Sds1");
		public static final War3ID EXPLODES_ON_DEATH = War3ID.fromString("Sds6");
	}
	final class ItemFigurineSummon {
		private ItemFigurineSummon() {}

		public static final War3ID SUMMON_2_AMOUNT = War3ID.fromString("Isn2");
		public static final War3ID SUMMON_1_AMOUNT = War3ID.fromString("Isn1");
		public static final War3ID SUMMON_1_UNIT_TYPE = War3ID.fromString("Ist1");
		public static final War3ID SUMMON_2_UNIT_TYPE = War3ID.fromString("Ist2");
	}
	final class ItemTownPortal {
		private ItemTownPortal() {}

		public static final War3ID USE_TELEPORT_CLUSTERING = War3ID.fromString("Itp2");
		public static final War3ID MAXIMUM_NUMBER_OF_UNITS = War3ID.fromString("Itpm");
	}
	final class Ensnare {
		private Ensnare() {}

		public static final War3ID AIR_UNIT_LOWER_DURATION = War3ID.fromString("Ens1");
		public static final War3ID MELEE_ATTACK_RANGE = War3ID.fromString("Ens3");
		public static final War3ID AIR_UNIT_HEIGHT = War3ID.fromString("Ens2");
	}
	final class TornadoSpinTornado {
		private TornadoSpinTornado() {}

		public static final War3ID MINIMUM_HIT_INTERVAL_SECONDS = War3ID.fromString("Tsp2");
		public static final War3ID AIR_TIME_SECONDS = War3ID.fromString("Tsp1");
	}
	final class StormEarthAndFire {
		private StormEarthAndFire() {}

		public static final War3ID SUMMONED_UNIT_TYPES = War3ID.fromString("Nef1");
	}
	final class EntangleGoldMine {
		private EntangleGoldMine() {}

		public static final War3ID RESULTING_UNIT_TYPE = War3ID.fromString("ent1");
	}
	final class ManaFlare {
		private ManaFlare() {}

		public static final War3ID CASTER_ONLY_SPLASH = War3ID.fromString("mfl6");
		public static final War3ID DAMAGE_COOLDOWN = War3ID.fromString("mfl5");
		public static final War3ID HERO_MAXIMUM_DAMAGE = War3ID.fromString("mfl4");
		public static final War3ID UNIT_MAXIMUM_DAMAGE = War3ID.fromString("mfl3");
		public static final War3ID HERO_DAMAGE_PER_MANA_POINT = War3ID.fromString("mfl2");
		public static final War3ID UNIT_DAMAGE_PER_MANA_POINT = War3ID.fromString("mfl1");
	}
	final class BlightedGoldMineAbility {
		private BlightedGoldMineAbility() {}

		public static final War3ID RADIUS_OF_MINING_RING = War3ID.fromString("Bgm4");
		public static final War3ID MAX_NUMBER_OF_MINERS = War3ID.fromString("Bgm3");
		public static final War3ID INTERVAL_DURATION = War3ID.fromString("Bgm2");
		public static final War3ID GOLD_PER_INTERVAL = War3ID.fromString("Bgm1");
	}
	final class ExhumeCorpses {
		private ExhumeCorpses() {}

		public static final War3ID MAXIMUM_NUMBER_OF_CORPSES = War3ID.fromString("exh1");
		public static final War3ID UNIT_TYPE = War3ID.fromString("exhu");
	}
	final class LightningShield {
		private LightningShield() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Lsh1");
	}
	final class DeathPact {
		private DeathPact() {}

		public static final War3ID LIFE_CONVERTED_TO_MANA = War3ID.fromString("Udp1");
		public static final War3ID LIFE_CONVERTED_TO_LIFE = War3ID.fromString("Udp2");
		public static final War3ID MANA_CONVERSION_AS_PERCENT = War3ID.fromString("Udp3");
		public static final War3ID LIFE_CONVERSION_AS_PERCENT = War3ID.fromString("Udp4");
		public static final War3ID LEAVE_TARGET_ALIVE = War3ID.fromString("Udp5");
	}
	final class SpellDamageReduction {
		private SpellDamageReduction() {}

		public static final War3ID DAMAGE_BONUS = War3ID.fromString("isr1");
		public static final War3ID DAMAGE_REDUCTION = War3ID.fromString("isr2");
	}
	final class RootAncients {
		private RootAncients() {}

		public static final War3ID ROOTED_WEAPONS = War3ID.fromString("Roo1");
		public static final War3ID UPROOTED_DEFENSE_TYPE = War3ID.fromString("Roo4");
		public static final War3ID UPROOTED_WEAPONS = War3ID.fromString("Roo2");
		public static final War3ID ROOTED_TURNING = War3ID.fromString("Roo3");
	}
	final class DarkConversionMalganis {
		private DarkConversionMalganis() {}

		public static final War3ID RACE_TO_CONVERT = War3ID.fromString("Ndc1");
		public static final War3ID CONVERSION_UNIT = War3ID.fromString("Ndc2");
	}
	final class PhoenixFire {
		private PhoenixFire() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("pxf2");
		public static final War3ID INITIAL_DAMAGE = War3ID.fromString("pxf1");
	}
	final class ChaosCargoLoad {
		private ChaosCargoLoad() {}

		public static final War3ID UNIT_TYPE_ALLOWED = War3ID.fromString("Chl1");
	}
	final class Sleep {
		private Sleep() {}

		public static final War3ID STUN_DURATION = War3ID.fromString("Usl1");
	}
	final class AoeDamageUponDeathSapper {
		private AoeDamageUponDeathSapper() {}

		public static final War3ID FULL_DAMAGE_RADIUS = War3ID.fromString("Dda1");
		public static final War3ID FULL_DAMAGE_AMOUNT = War3ID.fromString("Dda2");
		public static final War3ID PARTIAL_DAMAGE_RADIUS = War3ID.fromString("Dda3");
		public static final War3ID PARTIAL_DAMAGE_AMOUNT = War3ID.fromString("Dda4");
	}
	final class InnerFire {
		private InnerFire() {}

		public static final War3ID AUTOCAST_RANGE = War3ID.fromString("Inf3");
		public static final War3ID LIFE_REGEN_RATE = War3ID.fromString("Inf4");
		public static final War3ID DAMAGE_INCREASE_PERCENT = War3ID.fromString("Inf1");
		public static final War3ID DEFENSE_INCREASE = War3ID.fromString("Inf2");
	}
	final class StaffOfSanctuary {
		private StaffOfSanctuary() {}

		public static final War3ID BUILDING_TYPES_ALLOWED = War3ID.fromString("Nsa1");
		public static final War3ID UNIT_REGENERATION_DELAY = War3ID.fromString("Nsa3");
		public static final War3ID HERO_REGENERATION_DELAY = War3ID.fromString("Nsa2");
		public static final War3ID HIT_POINTS_PER_SECOND = War3ID.fromString("Nsa5");
		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Nsa4");
	}
	final class LightningAttack {
		private LightningAttack() {}

		public static final War3ID GRAPHIC_DELAY = War3ID.fromString("Lit1");
		public static final War3ID GRAPHIC_DURATION = War3ID.fromString("Lit2");
	}
	final class CreateCorpse {
		private CreateCorpse() {}

		public static final War3ID RADIUS_OF_GRAVESTONES = War3ID.fromString("Gyd2");
		public static final War3ID RADIUS_OF_CORPSES = War3ID.fromString("Gyd3");
		public static final War3ID MAXIMUM_NUMBER_OF_CORPSES = War3ID.fromString("Gyd1");
		public static final War3ID CORPSE_UNIT_TYPE = War3ID.fromString("Gydu");
	}
	final class SlowPoison {
		private SlowPoison() {}

		public static final War3ID STACKING_TYPE = War3ID.fromString("Spo4");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Spo3");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Spo2");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Spo1");
	}
	final class Defend {
		private Defend() {}

		public static final War3ID DAMAGE_TAKEN_PERCENT = War3ID.fromString("Def1");
		public static final War3ID DAMAGE_DEALT_PERCENT = War3ID.fromString("Def2");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Def3");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Def4");
		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Def5");
		public static final War3ID CHANCE_TO_DEFLECT = War3ID.fromString("Def6");
		public static final War3ID DEFLECT_DAMAGE_TAKEN_PIERCING = War3ID.fromString("Def7");
		public static final War3ID DEFLECT_DAMAGE_TAKEN_SPELLS = War3ID.fromString("Def8");
	}
	final class ItemCommand {
		private ItemCommand() {}

		public static final War3ID MAXIMUM_CREEP_LEVEL = War3ID.fromString("Icre");
	}
	final class BlackArrow {
		private BlackArrow() {}

		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Nba1");
		public static final War3ID NUMBER_OF_SUMMONED_UNITS = War3ID.fromString("Nba2");
		public static final War3ID SUMMONED_UNIT_DURATION_SECONDS = War3ID.fromString("Nba3");
		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Nbau");
	}
	final class EngineeringUpgrade {
		private EngineeringUpgrade() {}

		public static final War3ID ABILITY_UPGRADE_2 = War3ID.fromString("Neg4");
		public static final War3ID ABILITY_UPGRADE_3 = War3ID.fromString("Neg5");
		public static final War3ID ABILITY_UPGRADE_4 = War3ID.fromString("Neg6");
		public static final War3ID MOVE_SPEED_BONUS = War3ID.fromString("Neg1");
		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Neg2");
		public static final War3ID ABILITY_UPGRADE_1 = War3ID.fromString("Neg3");
	}
	final class Demolish {
		private Demolish() {}

		public static final War3ID DAMAGE_MULTIPLIER_UNITS = War3ID.fromString("Nde3");
		public static final War3ID DAMAGE_MULTIPLIER_HEROES = War3ID.fromString("Nde4");
		public static final War3ID CHANCE_TO_DEMOLISH = War3ID.fromString("Nde1");
		public static final War3ID DAMAGE_MULTIPLIER_BUILDINGS = War3ID.fromString("Nde2");
	}
	final class ItemTemporarySpeedBonus {
		private ItemTemporarySpeedBonus() {}

		public static final War3ID MOVEMENT_SPEED_INCREASE = War3ID.fromString("Ispi");
	}
	final class ChangeTimeOfDay {
		private ChangeTimeOfDay() {}

		public static final War3ID NEW_TIME_OF_DAY_HOUR = War3ID.fromString("ict1");
		public static final War3ID NEW_TIME_OF_DAY_MINUTE = War3ID.fromString("ict2");
	}
	final class AbolishMagic {
		private AbolishMagic() {}

		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("Adm2");
		public static final War3ID MANA_LOSS = War3ID.fromString("Adm1");
	}
	final class LoadGoblinZeppelin {
		private LoadGoblinZeppelin() {}

		public static final War3ID ALLOWED_UNIT_TYPE = War3ID.fromString("Loa1");
	}
	final class HarvestGoldAndLumber {
		private HarvestGoldAndLumber() {}

		public static final War3ID GOLD_CAPACITY = War3ID.fromString("Har3");
		public static final War3ID DAMAGE_TO_TREE = War3ID.fromString("Har1");
		public static final War3ID LUMBER_CAPACITY = War3ID.fromString("Har2");
	}
	final class UnstableConcoction {
		private UnstableConcoction() {}

		public static final War3ID MAX_DAMAGE = War3ID.fromString("Uco5");
		public static final War3ID MOVE_SPEED_BONUS = War3ID.fromString("Uco6");
	}
	final class AcidBomb {
		private AcidBomb() {}

		public static final War3ID ARMOR_PENALTY = War3ID.fromString("Nab3");
		public static final War3ID PRIMARY_DAMAGE = War3ID.fromString("Nab4");
		public static final War3ID SECONDARY_DAMAGE = War3ID.fromString("Nab5");
		public static final War3ID DAMAGE_INTERVAL = War3ID.fromString("Nab6");
		public static final War3ID MOVEMENT_SPEED_REDUCTION_PERCENT = War3ID.fromString("Nab1");
		public static final War3ID ATTACK_SPEED_REDUCTION_PERCENT = War3ID.fromString("Nab2");
	}
	final class AnimateDead {
		private AnimateDead() {}

		public static final War3ID NUMBER_OF_CORPSES_RAISED = War3ID.fromString("Uan1");
		public static final War3ID INHERIT_UPGRADES = War3ID.fromString("Uan3");
		public static final War3ID RAISED_UNITS_ARE_INVULNERABLE = War3ID.fromString("Hre2");
	}
	final class Immolation {
		private Immolation() {}

		public static final War3ID DAMAGE_PER_INTERVAL = War3ID.fromString("Eim1");
		public static final War3ID MANA_DRAINED_PER_SECOND = War3ID.fromString("Eim2");
		public static final War3ID BUFFER_MANA_REQUIRED = War3ID.fromString("Eim3");
	}
	final class WindWalk {
		private WindWalk() {}

		public static final War3ID MOVEMENT_SPEED_INCREASE_PERCENT = War3ID.fromString("Owk2");
		public static final War3ID TRANSITION_TIME = War3ID.fromString("Owk1");
		public static final War3ID BACKSTAB_DAMAGE_ENABLED = War3ID.fromString("Owk4");
		public static final War3ID BACKSTAB_DAMAGE = War3ID.fromString("Owk3");
	}
	final class HolyLight {
		private HolyLight() {}

		public static final War3ID AMOUNT_HEALED_OR_DAMAGED = War3ID.fromString("Hhb1");
	}
	final class ItemLevelGain {
		private ItemLevelGain() {}

		public static final War3ID LEVELS_GAINED = War3ID.fromString("Ilev");
	}
	final class Berserk {
		private Berserk() {}

		public static final War3ID ATTACK_SPEED_INCREASE = War3ID.fromString("bsk2");
		public static final War3ID DAMAGE_TAKEN_INCREASE = War3ID.fromString("bsk3");
		public static final War3ID MOVEMENT_SPEED_INCREASE = War3ID.fromString("bsk1");
	}
	final class DarkSummoning {
		private DarkSummoning() {}

		public static final War3ID MAXIMUM_UNITS = War3ID.fromString("Uds1");
		public static final War3ID CASTING_DELAY_SECONDS = War3ID.fromString("Uds2");
	}
	final class BattleStations {
		private BattleStations() {}

		public static final War3ID SUMMON_BUSY_UNITS = War3ID.fromString("Btl2");
		public static final War3ID ALLOWED_UNIT_TYPE = War3ID.fromString("Btl1");
	}
	final class CommandAura {
		private CommandAura() {}

		public static final War3ID ATTACK_DAMAGE_INCREASE = War3ID.fromString("Cac1");
	}
	final class SpellShieldRune {
		private SpellShieldRune() {}

		public static final War3ID SHIELD_COOLDOWN_TIME = War3ID.fromString("Nse1");
	}
	final class SpikedCarapace {
		private SpikedCarapace() {}

		public static final War3ID RETURNED_DAMAGE_FACTOR = War3ID.fromString("Uts1");
		public static final War3ID RECEIVED_DAMAGE_FACTOR = War3ID.fromString("Uts2");
		public static final War3ID DEFENSE_BONUS = War3ID.fromString("Uts3");
	}
	final class RainOfChaosArchimonde {
		private RainOfChaosArchimonde() {}

		public static final War3ID NUMBER_OF_UNITS_CREATED = War3ID.fromString("Nrc2");
		public static final War3ID ABILITY_FOR_UNIT_CREATION = War3ID.fromString("Nrc1");
	}
	final class ItemPlaceGoblinLandMine {
		private ItemPlaceGoblinLandMine() {}

		public static final War3ID UNIT_TYPE = War3ID.fromString("ipmu");
	}
	final class BreathOfFire {
		private BreathOfFire() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Nbf5");
	}
	final class ItemAttackCorruptionBonus {
		private ItemAttackCorruptionBonus() {}

		public static final War3ID ARMOR_PENALTY = War3ID.fromString("Iarp");
		public static final War3ID DAMAGE_BONUS_DICE = War3ID.fromString("Idic");
	}
	final class AnimateDeadNeutralHostile {
		private AnimateDeadNeutralHostile() {}

		public static final War3ID NUMBER_OF_CORPSES_RAISED = War3ID.fromString("Cad1");
	}
	final class DevourMagic {
		private DevourMagic() {}

		public static final War3ID IGNORE_FRIENDLY_BUFFS = War3ID.fromString("dvm6");
		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("dvm5");
		public static final War3ID MANA_PER_BUFF = War3ID.fromString("dvm4");
		public static final War3ID LIFE_PER_BUFF = War3ID.fromString("dvm3");
		public static final War3ID MANA_PER_UNIT = War3ID.fromString("dvm2");
		public static final War3ID LIFE_PER_UNIT = War3ID.fromString("dvm1");
	}
	final class HealingSpray {
		private HealingSpray() {}

		public static final War3ID WAVE_COUNT = War3ID.fromString("Nhs6");
		public static final War3ID DAMAGE_INTERVAL = War3ID.fromString("Ncs2");
		public static final War3ID MISSILE_COUNT = War3ID.fromString("Ncs3");
		public static final War3ID MAX_DAMAGE = War3ID.fromString("Ncs4");
		public static final War3ID BUILDING_DAMAGE_FACTOR = War3ID.fromString("Ncs5");
		public static final War3ID DAMAGE_AMOUNT = War3ID.fromString("Ncs1");
	}
	final class CarrionSwarm {
		private CarrionSwarm() {}

		public static final War3ID DAMAGE = War3ID.fromString("Ucs1");
		public static final War3ID MAX_DAMAGE = War3ID.fromString("Ucs2");
		public static final War3ID DISTANCE = War3ID.fromString("Ucs3");
		public static final War3ID FINAL_AREA = War3ID.fromString("Ucs4");
	}
	final class LifeRegenerationAuraNeutral {
		private LifeRegenerationAuraNeutral() {}

		public static final War3ID PERCENTAGE = War3ID.fromString("Arm2");
		public static final War3ID AMOUNT_REGENERATED = War3ID.fromString("Arm1");
	}
	final class MountHippogryphOld {
		private MountHippogryphOld() {}

		public static final War3ID PARTNER_UNIT_TYPE = War3ID.fromString("coa1");
		public static final War3ID RESULTING_UNIT_TYPE = War3ID.fromString("coau");
	}
	final class Parasite {
		private Parasite() {}

		public static final War3ID SUMMONED_UNIT_DURATION = War3ID.fromString("Npa6");
		public static final War3ID SUMMONED_UNIT_COUNT = War3ID.fromString("Npa5");
	}
	final class MountHippogryph {
		private MountHippogryph() {}

		public static final War3ID MOVE_TO_PARTNER = War3ID.fromString("coa2");
	}
	final class Avatar {
		private Avatar() {}

		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Hav3");
		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Hav4");
		public static final War3ID DEFENSE_BONUS = War3ID.fromString("Hav1");
		public static final War3ID HIT_POINT_BONUS = War3ID.fromString("Hav2");
	}
	final class Charm {
		private Charm() {}

		public static final War3ID MAXIMUM_CREEP_LEVEL = War3ID.fromString("Nch1");
	}
	final class ItemLifeBonus {
		private ItemLifeBonus() {}

		public static final War3ID MAX_LIFE_GAINED = War3ID.fromString("Ilif");
	}
	final class EntangledGoldMineAbility {
		private EntangledGoldMineAbility() {}

		public static final War3ID GOLD_PER_INTERVAL = War3ID.fromString("Egm1");
		public static final War3ID INTERVAL_DURATION = War3ID.fromString("Egm2");
	}
	final class FanOfKnives {
		private FanOfKnives() {}

		public static final War3ID DAMAGE_PER_TARGET = War3ID.fromString("Efk1");
		public static final War3ID MAXIMUM_TOTAL_DAMAGE = War3ID.fromString("Efk2");
		public static final War3ID MAXIMUM_NUMBER_OF_TARGETS = War3ID.fromString("Efk3");
		public static final War3ID MAXIMUM_SPEED_ADJUSTMENT = War3ID.fromString("Efk4");
	}
	final class Reincarnation {
		private Reincarnation() {}

		public static final War3ID REINCARNATION_DELAY = War3ID.fromString("Ore1");
	}
	final class Taunt {
		private Taunt() {}

		public static final War3ID MAX_UNITS = War3ID.fromString("Tau3");
		public static final War3ID PREFER_FRIENDLIES = War3ID.fromString("Tau2");
		public static final War3ID PREFER_HOSTILES = War3ID.fromString("Tau1");
	}
	final class Cripple {
		private Cripple() {}

		public static final War3ID MOVEMENT_SPEED_REDUCTION_PERCENT = War3ID.fromString("Cri1");
		public static final War3ID ATTACK_SPEED_REDUCTION_PERCENT = War3ID.fromString("Cri2");
		public static final War3ID DAMAGE_REDUCTION = War3ID.fromString("Cri3");
	}
	final class ItemDamageBonus {
		private ItemDamageBonus() {}

		public static final War3ID ATTACK_BONUS = War3ID.fromString("Iatt");
	}
	final class ItemAttackSpeedBonusGlovesOfHaste {
		private ItemAttackSpeedBonusGlovesOfHaste() {}

		public static final War3ID ATTACK_SPEED_INCREASE = War3ID.fromString("Isx1");
	}
	final class Silence {
		private Silence() {}

		public static final War3ID ATTACKS_PREVENTED = War3ID.fromString("Nsi1");
		public static final War3ID MOVEMENT_SPEED_MODIFIER = War3ID.fromString("Nsi3");
		public static final War3ID CHANCE_TO_MISS_PERCENT = War3ID.fromString("Nsi2");
		public static final War3ID ATTACK_SPEED_MODIFIER = War3ID.fromString("Nsi4");
	}
	final class FeralSpirit {
		private FeralSpirit() {}

		public static final War3ID SUMMONED_UNIT = War3ID.fromString("Osf1");
		public static final War3ID NUMBER_OF_SUMMONED_UNITS = War3ID.fromString("Osf2");
	}
	final class SearingArrows {
		private SearingArrows() {}

		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Hfa1");
	}
	final class Blizzard {
		private Blizzard() {}

		public static final War3ID DAMAGE = War3ID.fromString("Hbz2");
		public static final War3ID NUMBER_OF_SHARDS = War3ID.fromString("Hbz3");
		public static final War3ID NUMBER_OF_WAVES = War3ID.fromString("Hbz1");
		public static final War3ID MAXIMUM_DAMAGE_PER_WAVE = War3ID.fromString("Hbz6");
		public static final War3ID BUILDING_REDUCTION = War3ID.fromString("Hbz4");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Hbz5");
	}
	final class RoboGoblin {
		private RoboGoblin() {}

		public static final War3ID DEFENSE_BONUS = War3ID.fromString("Nrg6");
		public static final War3ID STRENGTH_BONUS = War3ID.fromString("Nrg5");
	}
	final class AncestralSpirit {
		private AncestralSpirit() {}

		public static final War3ID LIFE_RESTORED_FACTOR = War3ID.fromString("ast1");
		public static final War3ID MANA_RESTORED_FACTOR = War3ID.fromString("ast2");
	}
	final class Curse {
		private Curse() {}

		public static final War3ID CHANCE_TO_MISS = War3ID.fromString("Crs");
	}
	final class WarDrums {
		private WarDrums() {}

		public static final War3ID ATTACK_DAMAGE_INCREASE = War3ID.fromString("Akb1");
	}
	final class DiseaseCloudAbomination {
		private DiseaseCloudAbomination() {}

		public static final War3ID DURATION_OF_PLAGUE_WARD = War3ID.fromString("Apl3");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Apl2");
		public static final War3ID AURA_DURATION = War3ID.fromString("Apl1");
		public static final War3ID PLAGUE_WARD_UNIT_TYPE = War3ID.fromString("Aplu");
	}
	final class FlareGun {
		private FlareGun() {}

		public static final War3ID DETECTION_TYPE = War3ID.fromString("Ifa1");
		public static final War3ID DELAY_FOR_TARGET_EFFECT = War3ID.fromString("Idel");
	}
	final class SoulPreservationMalganis {
		private SoulPreservationMalganis() {}

		public static final War3ID UNIT_TO_PRESERVE = War3ID.fromString("Nsl1");
	}
	final class Polymorph {
		private Polymorph() {}

		public static final War3ID MORPH_UNITS_AIR = War3ID.fromString("Ply3");
		public static final War3ID MORPH_UNITS_AMPHIBIOUS = War3ID.fromString("Ply4");
		public static final War3ID MAXIMUM_CREEP_LEVEL = War3ID.fromString("Ply1");
		public static final War3ID MORPH_UNITS_GROUND = War3ID.fromString("Ply2");
		public static final War3ID MORPH_UNITS_WATER = War3ID.fromString("Ply5");
	}
	final class HealingWardWitchDoctor {
		private HealingWardWitchDoctor() {}

		public static final War3ID WARD_UNIT_TYPE = War3ID.fromString("hwdu");
	}
	final class AbsorbMana {
		private AbsorbMana() {}

		public static final War3ID MAXIMUM_LIFE_ABSORBED = War3ID.fromString("abs1");
		public static final War3ID MAXIMUM_MANA_ABSORBED = War3ID.fromString("abs2");
	}
	final class VampiricPotion {
		private VampiricPotion() {}

		public static final War3ID LIFE_STEAL_AMOUNT = War3ID.fromString("ipv2");
		public static final War3ID DAMAGE_BONUS = War3ID.fromString("ipv1");
		public static final War3ID AMOUNT_IS_RAW_VALUE = War3ID.fromString("ipv3");
	}
	final class WaygateAbility {
		private WaygateAbility() {}

		public static final War3ID TELEPORT_AREA_HEIGHT = War3ID.fromString("Wrp2");
		public static final War3ID TELEPORT_AREA_WIDTH = War3ID.fromString("Wrp1");
	}
	final class Doom {
		private Doom() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Ndo1");
		public static final War3ID NUMBER_OF_SUMMONED_UNITS = War3ID.fromString("Ndo2");
		public static final War3ID SUMMONED_UNIT_DURATION_SECONDS = War3ID.fromString("Ndo3");
		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Ndou");
	}
	final class Shockwave {
		private Shockwave() {}

		public static final War3ID DAMAGE = War3ID.fromString("Osh1");
		public static final War3ID DISTANCE = War3ID.fromString("Osh3");
		public static final War3ID MAXIMUM_DAMAGE = War3ID.fromString("Osh2");
		public static final War3ID FINAL_AREA = War3ID.fromString("Osh4");
	}
	final class Transmute {
		private Transmute() {}

		public static final War3ID ALLOW_BOUNTY = War3ID.fromString("Ntm4");
		public static final War3ID MAX_CREEP_LEVEL = War3ID.fromString("Ntm3");
		public static final War3ID LUMBER_COST_FACTOR = War3ID.fromString("Ntm2");
		public static final War3ID GOLD_COST_FACTOR = War3ID.fromString("Ntm1");
	}
	final class Heal {
		private Heal() {}

		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Hea1");
	}
	final class MonsterLure {
		private MonsterLure() {}

		public static final War3ID ACTIVATION_DELAY = War3ID.fromString("imo2");
		public static final War3ID NUMBER_OF_LURES = War3ID.fromString("imo1");
		public static final War3ID LURE_INTERVAL_SECONDS = War3ID.fromString("imo3");
		public static final War3ID LURE_UNIT_TYPE = War3ID.fromString("imou");
	}
	final class Starfall {
		private Starfall() {}

		public static final War3ID DAMAGE_DEALT = War3ID.fromString("Esf1");
		public static final War3ID BUILDING_REDUCTION = War3ID.fromString("Esf3");
		public static final War3ID DAMAGE_INTERVAL = War3ID.fromString("Esf2");
	}
	final class GhostVisible {
		private GhostVisible() {}

		public static final War3ID DOES_NOT_BLOCK_BUILDINGS = War3ID.fromString("Eth2");
		public static final War3ID IMMUNE_TO_MORPH_EFFECTS = War3ID.fromString("Eth1");
	}
	final class Channel {
		private Channel() {}

		public static final War3ID OPTIONS = War3ID.fromString("Ncl3");
		public static final War3ID ART_DURATION = War3ID.fromString("Ncl4");
		public static final War3ID DISABLE_OTHER_ABILITIES = War3ID.fromString("Ncl5");
		public static final War3ID BASE_ORDER_ID = War3ID.fromString("Ncl6");
		public static final War3ID FOLLOW_THROUGH_TIME = War3ID.fromString("Ncl1");
		public static final War3ID TARGET_TYPE = War3ID.fromString("Ncl2");
	}
	final class SummonWaterElemental {
		private SummonWaterElemental() {}

		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Hwe1");
		public static final War3ID SUMMONED_UNIT_COUNT = War3ID.fromString("Hwe2");
	}
	final class EnduranceAura {
		private EnduranceAura() {}

		public static final War3ID ATTACK_SPEED_INCREASE_PERCENT = War3ID.fromString("Oae2");
		public static final War3ID MOVEMENT_SPEED_INCREASE_PERCENT = War3ID.fromString("Oae1");
	}
	final class Slow {
		private Slow() {}

		public static final War3ID ALWAYS_AUTOCAST = War3ID.fromString("Slo3");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Slo2");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Slo1");
	}
	final class MassTeleport {
		private MassTeleport() {}

		public static final War3ID NUMBER_OF_UNITS_TELEPORTED = War3ID.fromString("Hmt1");
		public static final War3ID CASTING_DELAY = War3ID.fromString("Hmt2");
		public static final War3ID USE_TELEPORT_CLUSTERING = War3ID.fromString("Hmt3");
	}
	final class VampiricAura {
		private VampiricAura() {}

		public static final War3ID ATTACK_DAMAGE_STOLEN_PERCENT = War3ID.fromString("Uav1");
	}
	final class StasisTrap {
		private StasisTrap() {}

		public static final War3ID STUN_DURATION = War3ID.fromString("Sta4");
		public static final War3ID DETONATION_RADIUS = War3ID.fromString("Sta3");
		public static final War3ID DETECTION_RADIUS = War3ID.fromString("Sta2");
		public static final War3ID ACTIVATION_DELAY = War3ID.fromString("Sta1");
		public static final War3ID WARD_UNIT_TYPE = War3ID.fromString("Stau");
	}
	final class Detonate {
		private Detonate() {}

		public static final War3ID MANA_LOSS_PER_UNIT = War3ID.fromString("Dtn1");
		public static final War3ID DAMAGE_TO_SUMMONED_UNITS = War3ID.fromString("Dtn2");
	}
	final class StormBolt {
		private StormBolt() {}

		public static final War3ID DAMAGE = War3ID.fromString("Htb1");
	}
	final class ItemManaRegainLesser {
		private ItemManaRegainLesser() {}

		public static final War3ID MANA_POINTS_GAINED = War3ID.fromString("Impg");
	}
	final class ItemLifeSteal {
		private ItemLifeSteal() {}

		public static final War3ID LIFE_STOLEN_PER_ATTACK = War3ID.fromString("Ivam");
	}
	final class ChainLightning {
		private ChainLightning() {}

		public static final War3ID DAMAGE_PER_TARGET = War3ID.fromString("Ocl1");
		public static final War3ID DAMAGE_REDUCTION_PER_TARGET = War3ID.fromString("Ocl3");
		public static final War3ID NUMBER_OF_TARGETS_HIT = War3ID.fromString("Ocl2");
	}
	final class UnholyAura {
		private UnholyAura() {}

		public static final War3ID MOVEMENT_SPEED_INCREASE_PERCENT = War3ID.fromString("Uau1");
		public static final War3ID LIFE_REGENERATION_INCREASE_PERCENT = War3ID.fromString("Uau2");
		public static final War3ID PERCENT_BONUS = War3ID.fromString("Uau3");
	}
	final class ForceOfNature {
		private ForceOfNature() {}

		public static final War3ID NUMBER_OF_SUMMONED_UNITS = War3ID.fromString("Efn1");
		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Efnu");
	}
	final class ItemTemporaryAreaArmorBonus {
		private ItemTemporaryAreaArmorBonus() {}

		public static final War3ID MANA_POINTS_GAINED = War3ID.fromString("Imp2");
		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Ihp2");
	}
	final class WarClub {
		private WarClub() {}

		public static final War3ID ATTACH_DELAY = War3ID.fromString("gra1");
		public static final War3ID REMOVE_DELAY = War3ID.fromString("gra2");
		public static final War3ID DISABLED_ATTACK_INDEX = War3ID.fromString("gra3");
		public static final War3ID ENABLED_ATTACK_INDEX = War3ID.fromString("gra4");
		public static final War3ID MAXIMUM_ATTACKS = War3ID.fromString("gra5");
	}
	final class DarkPortalArchimonde {
		private DarkPortalArchimonde() {}

		public static final War3ID SPAWNED_UNITS = War3ID.fromString("Ndp1");
		public static final War3ID MINIMUM_NUMBER_OF_UNITS = War3ID.fromString("Ndp2");
		public static final War3ID MAXIMUM_NUMBER_OF_UNITS = War3ID.fromString("Ndp3");
	}
	final class ShadowStrike {
		private ShadowStrike() {}

		public static final War3ID DECAYING_DAMAGE = War3ID.fromString("Esh1");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Esh3");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Esh2");
		public static final War3ID INITIAL_DAMAGE = War3ID.fromString("Esh5");
		public static final War3ID DECAY_POWER = War3ID.fromString("Esh4");
	}
	final class Pulverize {
		private Pulverize() {}

		public static final War3ID CHANCE_TO_STOMP_PERCENT = War3ID.fromString("War1");
		public static final War3ID FULL_DAMAGE_RADIUS = War3ID.fromString("War3");
		public static final War3ID DAMAGE_DEALT = War3ID.fromString("War2");
		public static final War3ID HALF_DAMAGE_RADIUS = War3ID.fromString("War4");
	}
	final class Ghost {
		private Ghost() {}

		public static final War3ID AUTO_ACQUIRE_ATTACK_TARGETS = War3ID.fromString("Gho1");
		public static final War3ID DOES_NOT_BLOCK_BUILDINGS = War3ID.fromString("Gho3");
		public static final War3ID IMMUNE_TO_MORPH_EFFECTS = War3ID.fromString("Gho2");
	}
	final class Spy {
		private Spy() {}

		public static final War3ID LUMBER_COST_PER_USE = War3ID.fromString("Nsp2");
		public static final War3ID GOLD_COST_PER_STRUCTURE = War3ID.fromString("Nsp1");
		public static final War3ID DETECTION_TYPE = War3ID.fromString("Nsp3");
	}
	final class FrostArmor {
		private FrostArmor() {}

		public static final War3ID ARMOR_DURATION = War3ID.fromString("Ufa1");
		public static final War3ID ARMOR_BONUS = War3ID.fromString("Ufa2");
	}
	final class NeutralBuilding {
		private NeutralBuilding() {}

		public static final War3ID INTERACTION_TYPE = War3ID.fromString("Neu2");
		public static final War3ID SHOW_SELECT_UNIT_BUTTON = War3ID.fromString("Neu3");
		public static final War3ID SHOW_UNIT_INDICATOR = War3ID.fromString("Neu4");
		public static final War3ID ACTIVATION_RADIUS = War3ID.fromString("Neu1");
	}
	final class InventoryHero {
		private InventoryHero() {}

		public static final War3ID DROP_ITEMS_ON_DEATH = War3ID.fromString("inv2");
		public static final War3ID ITEM_CAPACITY = War3ID.fromString("inv1");
		public static final War3ID CAN_GET_ITEMS = War3ID.fromString("inv4");
		public static final War3ID CAN_USE_ITEMS = War3ID.fromString("inv3");
		public static final War3ID CAN_DROP_ITEMS = War3ID.fromString("inv5");
	}
	final class UnholyFrenzy {
		private UnholyFrenzy() {}

		public static final War3ID ATTACK_SPEED_BONUS_PERCENT = War3ID.fromString("Uhf1");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Uhf2");
	}
	final class SoulBurn {
		private SoulBurn() {}

		public static final War3ID DAMAGE_AMOUNT = War3ID.fromString("Nso1");
		public static final War3ID DAMAGE_PENALTY = War3ID.fromString("Nso3");
		public static final War3ID DAMAGE_PERIOD = War3ID.fromString("Nso2");
		public static final War3ID ATTACK_SPEED_REDUCTION_PERCENT = War3ID.fromString("Nso5");
		public static final War3ID MOVEMENT_SPEED_REDUCTION_PERCENT = War3ID.fromString("Nso4");
	}
	final class ItemHealManaRegain {
		private ItemHealManaRegain() {}

		public static final War3ID MANA_POINTS_RESTORED = War3ID.fromString("Imps");
		public static final War3ID HIT_POINTS_RESTORED = War3ID.fromString("Ihps");
	}
	final class Purge {
		private Purge() {}

		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("Prg3");
		public static final War3ID UNIT_PAUSE_DURATION = War3ID.fromString("Prg4");
		public static final War3ID MOVEMENT_UPDATE_FREQUENCY = War3ID.fromString("Prg1");
		public static final War3ID ATTACK_UPDATE_FREQUENCY = War3ID.fromString("Prg2");
		public static final War3ID HERO_PAUSE_DURATION = War3ID.fromString("Prg5");
		public static final War3ID MANA_LOSS = War3ID.fromString("Prg6");
	}
	final class ColdArrows {
		private ColdArrows() {}

		public static final War3ID EXTRA_DAMAGE = War3ID.fromString("Hca1");
		public static final War3ID STACK_FLAGS = War3ID.fromString("Hca4");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Hca2");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Hca3");
	}
	final class PoisonArrows {
		private PoisonArrows() {}

		public static final War3ID EXTRA_DAMAGE = War3ID.fromString("Poa1");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Poa4");
		public static final War3ID STACKING_TYPE = War3ID.fromString("Poa5");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Poa2");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Poa3");
	}
	final class Tornado {
		private Tornado() {}

		public static final War3ID SUMMONED_UNIT_TYPE = War3ID.fromString("Ntou");
	}
	final class FarSight {
		private FarSight() {}

		public static final War3ID DETECTION_TYPE = War3ID.fromString("Ofs1");
	}
	final class WarStompNeutralHostile {
		private WarStompNeutralHostile() {}

		public static final War3ID DAMAGE = War3ID.fromString("Wrs1");
		public static final War3ID TERRAIN_DEFORMATION_DURATION_MS = War3ID.fromString("Wrs3");
		public static final War3ID TERRAIN_DEFORMATION_AMPLITUDE = War3ID.fromString("Wrs2");
	}
	final class LifeDrain {
		private LifeDrain() {}

		public static final War3ID HIT_POINTS_DRAINED = War3ID.fromString("Ndr1");
		public static final War3ID MANA_POINTS_DRAINED = War3ID.fromString("Ndr2");
		public static final War3ID DRAIN_INTERVAL_SECONDS = War3ID.fromString("Ndr3");
		public static final War3ID BONUS_MANA_FACTOR = War3ID.fromString("Ndr8");
		public static final War3ID BONUS_MANA_DECAY = War3ID.fromString("Ndr9");
		public static final War3ID LIFE_TRANSFERRED_PER_SECOND = War3ID.fromString("Ndr4");
		public static final War3ID MANA_TRANSFERRED_PER_SECOND = War3ID.fromString("Ndr5");
		public static final War3ID BONUS_LIFE_FACTOR = War3ID.fromString("Ndr6");
		public static final War3ID BONUS_LIFE_DECAY = War3ID.fromString("Ndr7");
	}
	final class Earthquake {
		private Earthquake() {}

		public static final War3ID DAMAGE_PER_SECOND_TO_BUILDINGS = War3ID.fromString("Oeq2");
		public static final War3ID EFFECT_DELAY = War3ID.fromString("Oeq1");
		public static final War3ID FINAL_AREA = War3ID.fromString("Oeq4");
		public static final War3ID UNITS_SLOWED_PERCENT = War3ID.fromString("Oeq3");
	}
	final class ThunderClap {
		private ThunderClap() {}

		public static final War3ID AOE_DAMAGE = War3ID.fromString("Htc1");
		public static final War3ID SPECIFIC_TARGET_DAMAGE = War3ID.fromString("Htc2");
		public static final War3ID MOVEMENT_SPEED_REDUCTION_PERCENT = War3ID.fromString("Htc3");
		public static final War3ID ATTACK_SPEED_REDUCTION_PERCENT = War3ID.fromString("Htc4");
	}
	final class Bladestorm {
		private Bladestorm() {}

		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Oww2");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Oww1");
	}
	final class GatherWispGoldAndLumber {
		private GatherWispGoldAndLumber() {}

		public static final War3ID LUMBER_PER_INTERVAL = War3ID.fromString("Wha1");
		public static final War3ID ART_ATTACHMENT_HEIGHT = War3ID.fromString("Wha3");
		public static final War3ID INTERVALS_BEFORE_CHANGING_TREES = War3ID.fromString("Wha2");
	}
	final class ReplenishManaAndLife {
		private ReplenishManaAndLife() {}

		public static final War3ID AUTOCAST_REQUIREMENT = War3ID.fromString("Mbt3");
		public static final War3ID WATER_HEIGHT = War3ID.fromString("Mbt4");
		public static final War3ID REGENERATE_ONLY_AT_NIGHT = War3ID.fromString("Mbt5");
		public static final War3ID MANA_GAINED = War3ID.fromString("Mbt1");
		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Mbt2");
	}
	final class Factory {
		private Factory() {}

		public static final War3ID LEASH_RANGE = War3ID.fromString("Nfy2");
		public static final War3ID SPAWN_INTERVAL = War3ID.fromString("Nfy1");
		public static final War3ID SPAWN_UNIT_ID = War3ID.fromString("Nfyu");
	}
	final class ControlMagic {
		private ControlMagic() {}

		public static final War3ID MANA_PER_SUMMONED_HITPOINT = War3ID.fromString("Cmg2");
		public static final War3ID CHARGE_FOR_CURRENT_LIFE = War3ID.fromString("Cmg3");
	}
	final class ItemIllusions {
		private ItemIllusions() {}

		public static final War3ID DAMAGE_DEALT_PERCENT_OF_NORMAL = War3ID.fromString("Iild");
		public static final War3ID DAMAGE_RECEIVED_MULTIPLIER = War3ID.fromString("Iilw");
	}
	final class ClusterRockets {
		private ClusterRockets() {}

		public static final War3ID EFFECT_DURATION = War3ID.fromString("Ncs6");
	}
	final class Cannibalize {
		private Cannibalize() {}

		public static final War3ID MAX_HIT_POINTS = War3ID.fromString("Can2");
		public static final War3ID HIT_POINTS_PER_SECOND = War3ID.fromString("Can1");
	}
	final class EntanglingRoots {
		private EntanglingRoots() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Eer1");
	}
	final class ItemManaRegeneration {
		private ItemManaRegeneration() {}

		public static final War3ID MANA_REGENERATION_BONUS_AS_FRACTION_OF_NORMAL = War3ID.fromString("Imrp");
	}
	final class ThornsAura {
		private ThornsAura() {}

		public static final War3ID DAMAGE_DEALT_TO_ATTACKERS = War3ID.fromString("Eah1");
		public static final War3ID DAMAGE_IS_PERCENT_RECEIVED = War3ID.fromString("Eah2");
	}
	final class FaerieFire {
		private FaerieFire() {}

		public static final War3ID DEFENSE_REDUCTION = War3ID.fromString("Fae1");
		public static final War3ID ALWAYS_AUTOCAST = War3ID.fromString("Fae2");
	}
	final class Dismount {
		private Dismount() {}

		public static final War3ID PARTNER_UNIT_TYPE_ONE = War3ID.fromString("dcp1");
		public static final War3ID PARTNER_UNIT_TYPE_TWO = War3ID.fromString("dcp2");
	}
	final class Reveal {
		private Reveal() {}

		public static final War3ID LUMBER_COST = War3ID.fromString("Ndt2");
		public static final War3ID DETECTION_TYPE = War3ID.fromString("Ndt3");
		public static final War3ID GOLD_COST = War3ID.fromString("Ndt1");
	}
	final class ItemChainDispel {
		private ItemChainDispel() {}

		public static final War3ID MANA_LOSS_PER_UNIT = War3ID.fromString("idc1");
		public static final War3ID MAXIMUM_DISPELLED_UNITS = War3ID.fromString("idc3");
		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("idc2");
	}
	final class Rejuvenation {
		private Rejuvenation() {}

		public static final War3ID ALLOW_WHEN_FULL = War3ID.fromString("Rej3");
		public static final War3ID NO_TARGET_REQUIRED = War3ID.fromString("Rej4");
		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Rej1");
		public static final War3ID MANA_POINTS_GAINED = War3ID.fromString("Rej2");
	}
	final class ChemicalRage {
		private ChemicalRage() {}

		public static final War3ID MOVE_SPEED_BONUS_INFO_PANEL_ONLY = War3ID.fromString("Ncr5");
		public static final War3ID ATTACK_SPEED_BONUS_INFO_PANEL_ONLY = War3ID.fromString("Ncr6");
	}
	final class ShadowMeld {
		private ShadowMeld() {}

		public static final War3ID ACTION_DURATION = War3ID.fromString("Shm3");
		public static final War3ID DAY_OR_NIGHT_DURATION = War3ID.fromString("Shm2");
		public static final War3ID FADE_DURATION = War3ID.fromString("Shm1");
	}
	final class DetectorSentryWard {
		private DetectorSentryWard() {}

		public static final War3ID DETECTION_TYPE = War3ID.fromString("Det1");
	}
	final class Stampede {
		private Stampede() {}

		public static final War3ID DAMAGE_RADIUS = War3ID.fromString("Nst4");
		public static final War3ID DAMAGE_AMOUNT = War3ID.fromString("Nst3");
		public static final War3ID DAMAGE_DELAY = War3ID.fromString("Nst5");
		public static final War3ID BEAST_COLLISION_RADIUS = War3ID.fromString("Nst2");
		public static final War3ID BEASTS_PER_SECOND = War3ID.fromString("Nst1");
	}
	final class Replenish {
		private Replenish() {}

		public static final War3ID MINIMUM_LIFE_REQUIRED = War3ID.fromString("Rpb3");
		public static final War3ID MAXIMUM_UNITS_AFFECTED = War3ID.fromString("Rpb6");
		public static final War3ID MINIMUM_MANA_REQUIRED = War3ID.fromString("Rpb4");
		public static final War3ID MAXIMUM_UNITS_CHARGED_TO_CASTER = War3ID.fromString("Rpb5");
	}
	final class FeedbackSpellBreaker {
		private FeedbackSpellBreaker() {}

		public static final War3ID SUMMONED_DAMAGE = War3ID.fromString("fbk5");
		public static final War3ID DAMAGE_RATIO_HEROS_PERCENT = War3ID.fromString("fbk4");
		public static final War3ID MAX_MANA_DRAINED_HEROS = War3ID.fromString("fbk3");
		public static final War3ID DAMAGE_RATIO_UNITS_PERCENT = War3ID.fromString("fbk2");
		public static final War3ID MAX_MANA_DRAINED_UNITS = War3ID.fromString("fbk1");
	}
	final class BrillianceAura {
		private BrillianceAura() {}

		public static final War3ID MANA_REGENERATION_INCREASE = War3ID.fromString("Hab1");
		public static final War3ID PERCENT_BONUS = War3ID.fromString("Hab2");
	}
	final class BlightDispelSmall {
		private BlightDispelSmall() {}

		public static final War3ID CREATES_BLIGHT = War3ID.fromString("Bli2");
		public static final War3ID EXPANSION_AMOUNT = War3ID.fromString("Bli1");
	}
	final class Tranquility {
		private Tranquility() {}

		public static final War3ID LIFE_HEALED = War3ID.fromString("Etq1");
		public static final War3ID BUILDING_REDUCTION = War3ID.fromString("Etq3");
		public static final War3ID HEAL_INTERVAL = War3ID.fromString("Etq2");
	}
	final class CriticalStrike {
		private CriticalStrike() {}

		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Ocr3");
		public static final War3ID DAMAGE_MULTIPLIER = War3ID.fromString("Ocr2");
		public static final War3ID NEVER_MISS = War3ID.fromString("Ocr5");
		public static final War3ID CHANCE_TO_EVADE = War3ID.fromString("Ocr4");
		public static final War3ID CHANCE_TO_CRITICAL_STRIKE = War3ID.fromString("Ocr1");
	}
	final class ItemMoveSpeedBonus {
		private ItemMoveSpeedBonus() {}

		public static final War3ID MOVEMENT_SPEED_BONUS = War3ID.fromString("Imvb");
	}
	final class CargoHoldOrcBurrow {
		private CargoHoldOrcBurrow() {}

		public static final War3ID CARGO_CAPACITY = War3ID.fromString("Car1");
	}
	final class Blink {
		private Blink() {}

		public static final War3ID MAXIMUM_RANGE = War3ID.fromString("Ebl1");
		public static final War3ID MINIMUM_RANGE = War3ID.fromString("Ebl2");
	}
	final class Sentinel {
		private Sentinel() {}

		public static final War3ID IN_FLIGHT_SIGHT_RADIUS = War3ID.fromString("Esn1");
		public static final War3ID HOVERING_HEIGHT = War3ID.fromString("Esn3");
		public static final War3ID HOVERING_SIGHT_RADIUS = War3ID.fromString("Esn2");
		public static final War3ID NUMBER_OF_OWLS = War3ID.fromString("Esn4");
	}
	final class ManaBurn {
		private ManaBurn() {}

		public static final War3ID MAX_MANA_DRAINED = War3ID.fromString("Emb1");
		public static final War3ID BOLT_LIFETIME = War3ID.fromString("Emb3");
		public static final War3ID BOLT_DELAY = War3ID.fromString("Emb2");
	}
	final class BattleRoar {
		private BattleRoar() {}

		public static final War3ID DAMAGE_INCREASE = War3ID.fromString("Nbr1");
	}
	final class Devour {
		private Devour() {}

		public static final War3ID MAX_CREEP_LEVEL = War3ID.fromString("Dev1");
	}
	final class DevourCargo {
		private DevourCargo() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Dev2");
		public static final War3ID MAXIMUM_CREEP_LEVEL = War3ID.fromString("Dev3");
	}
	final class DevotionAura {
		private DevotionAura() {}

		public static final War3ID ARMOR_BONUS = War3ID.fromString("Had1");
		public static final War3ID PERCENT_BONUS = War3ID.fromString("Had2");
	}
	final class SpellBook {
		private SpellBook() {}

		public static final War3ID BASE_ORDER_ID = War3ID.fromString("spb5");
		public static final War3ID SHARED_SPELL_COOLDOWN = War3ID.fromString("spb2");
		public static final War3ID SPELL_LIST = War3ID.fromString("spb1");
		public static final War3ID MAXIMUM_SPELLS = War3ID.fromString("spb4");
		public static final War3ID MINIMUM_SPELLS = War3ID.fromString("spb3");
	}
	final class ItemAreaDetection {
		private ItemAreaDetection() {}

		public static final War3ID DETECTION_RADIUS = War3ID.fromString("Idet");
	}
	final class SpiderAttack {
		private SpiderAttack() {}

		public static final War3ID SPIDER_CAPACITY = War3ID.fromString("Spa1");
	}
	final class Cyclone {
		private Cyclone() {}

		public static final War3ID CAN_BE_DISPELLED = War3ID.fromString("cyc1");
	}
	final class OrbOfAnnihilation {
		private OrbOfAnnihilation() {}

		public static final War3ID HALF_DAMAGE_RADIUS = War3ID.fromString("fak5");
		public static final War3ID FULL_DAMAGE_RADIUS = War3ID.fromString("fak4");
		public static final War3ID SMALL_DAMAGE_FACTOR = War3ID.fromString("fak3");
		public static final War3ID MEDIUM_DAMAGE_FACTOR = War3ID.fromString("fak2");
		public static final War3ID DAMAGE_BONUS = War3ID.fromString("fak1");
	}
	final class ItemAttackDamageGain {
		private ItemAttackDamageGain() {}

		public static final War3ID ATTACK_MODIFICATION = War3ID.fromString("Iaa1");
	}
	final class Inferno {
		private Inferno() {}

		public static final War3ID DURATION = War3ID.fromString("Uin2");
		public static final War3ID IMPACT_DELAY = War3ID.fromString("Uin3");
		public static final War3ID SUMMONED_UNIT = War3ID.fromString("Uin4");
		public static final War3ID DAMAGE = War3ID.fromString("Uin1");
	}
	final class Metamorphosis {
		private Metamorphosis() {}

		public static final War3ID MORPHING_FLAGS = War3ID.fromString("Eme2");
		public static final War3ID NORMAL_FORM_UNIT = War3ID.fromString("Eme1");
		public static final War3ID LANDING_DELAY_TIME = War3ID.fromString("Eme4");
		public static final War3ID ALTITUDE_ADJUSTMENT_DURATION = War3ID.fromString("Eme3");
		public static final War3ID ALTERNATE_FORM_HIT_POINT_BONUS = War3ID.fromString("Eme5");
		public static final War3ID ALTERNATE_FORM_UNIT = War3ID.fromString("Emeu");
	}
	final class DeathAndDecay {
		private DeathAndDecay() {}

		public static final War3ID MAX_LIFE_DRAINED_PER_SECOND_PERCENT = War3ID.fromString("Udd1");
		public static final War3ID BUILDING_REDUCTION = War3ID.fromString("Udd2");
	}
	final class Unknownsca1Grunt {
		private Unknownsca1Grunt() {}

		public static final War3ID NEW_UNIT_TYPE = War3ID.fromString("Cha1");
	}
	final class BuildingDamageAuraTornado {
		private BuildingDamageAuraTornado() {}

		public static final War3ID SMALL_DAMAGE_PER_SECOND = War3ID.fromString("Tdg5");
		public static final War3ID SMALL_DAMAGE_RADIUS = War3ID.fromString("Tdg4");
		public static final War3ID MEDIUM_DAMAGE_PER_SECOND = War3ID.fromString("Tdg3");
		public static final War3ID MEDIUM_DAMAGE_RADIUS = War3ID.fromString("Tdg2");
		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Tdg1");
	}
	final class Evasion {
		private Evasion() {}

		public static final War3ID CHANCE_TO_EVADE = War3ID.fromString("Eev1");
	}
	final class GlyphOfFortification {
		private GlyphOfFortification() {}

		public static final War3ID UPGRADE_LEVELS = War3ID.fromString("Igl1");
		public static final War3ID UPGRADE_TYPE = War3ID.fromString("Iglu");
	}
	final class Roar {
		private Roar() {}

		public static final War3ID DEFENSE_INCREASE = War3ID.fromString("Roa2");
		public static final War3ID LIFE_REGENERATION_RATE = War3ID.fromString("Roa3");
		public static final War3ID DAMAGE_INCREASE_PERCENT = War3ID.fromString("Roa1");
		public static final War3ID PREFER_FRIENDLIES = War3ID.fromString("Roa6");
		public static final War3ID MAX_UNITS = War3ID.fromString("Roa7");
		public static final War3ID MANA_REGEN = War3ID.fromString("Roa4");
		public static final War3ID PREFER_HOSTILES = War3ID.fromString("Roa5");
	}
	final class ItemReincarnation {
		private ItemReincarnation() {}

		public static final War3ID RESTORED_MANA_1_FOR_CURRENT = War3ID.fromString("irc3");
		public static final War3ID RESTORED_LIFE = War3ID.fromString("irc2");
		public static final War3ID DELAY_AFTER_DEATH_SECONDS = War3ID.fromString("Ircd");
	}
	final class ChargeGoldAndLumber {
		private ChargeGoldAndLumber() {}

		public static final War3ID CHARGE_OWNING_PLAYER = War3ID.fromString("Ans6");
		public static final War3ID BASE_ORDER_ID = War3ID.fromString("Ans5");
	}
	final class Impale {
		private Impale() {}

		public static final War3ID WAVE_DISTANCE = War3ID.fromString("Uim1");
		public static final War3ID WAVE_TIME_SECONDS = War3ID.fromString("Uim2");
		public static final War3ID DAMAGE_DEALT = War3ID.fromString("Uim3");
		public static final War3ID AIR_TIME_SECONDS = War3ID.fromString("Uim4");
	}
	final class LocustSwarm {
		private LocustSwarm() {}

		public static final War3ID DAMAGE_RETURN_FACTOR = War3ID.fromString("Uls4");
		public static final War3ID DAMAGE_RETURN_THRESHOLD = War3ID.fromString("Uls5");
		public static final War3ID NUMBER_OF_SWARM_UNITS = War3ID.fromString("Uls1");
		public static final War3ID UNIT_RELEASE_INTERVAL_SECONDS = War3ID.fromString("Uls2");
		public static final War3ID MAX_SWARM_UNITS_PER_TARGET = War3ID.fromString("Uls3");
		public static final War3ID SWARM_UNIT_TYPE = War3ID.fromString("Ulsu");
	}
	final class Resurrection {
		private Resurrection() {}

		public static final War3ID NUMBER_OF_CORPSES_RAISED = War3ID.fromString("Hre1");
	}
	final class DeathCoil {
		private DeathCoil() {}

		public static final War3ID AMOUNT_HEALED_OR_DAMAGED = War3ID.fromString("Udc1");
	}
	final class BundleOfLumber {
		private BundleOfLumber() {}

		public static final War3ID LUMBER_GIVEN = War3ID.fromString("Ilum");
	}
	final class ItemArmorBonus {
		private ItemArmorBonus() {}

		public static final War3ID DEFENSE_BONUS = War3ID.fromString("Idef");
	}
	final class Bash {
		private Bash() {}

		public static final War3ID CHANCE_TO_BASH = War3ID.fromString("Hbh1");
		public static final War3ID CHANCE_TO_MISS = War3ID.fromString("Hbh4");
		public static final War3ID NEVER_MISS = War3ID.fromString("Hbh5");
		public static final War3ID DAMAGE_MULTIPLIER = War3ID.fromString("Hbh2");
		public static final War3ID DAMAGE_BONUS = War3ID.fromString("Hbh3");
	}
	final class ReturnGold {
		private ReturnGold() {}

		public static final War3ID ACCEPTS_LUMBER = War3ID.fromString("Rtn2");
		public static final War3ID ACCEPTS_GOLD = War3ID.fromString("Rtn1");
	}
	final class MirrorImage {
		private MirrorImage() {}

		public static final War3ID DAMAGE_DEALT_PERCENT = War3ID.fromString("Omi2");
		public static final War3ID NUMBER_OF_IMAGES = War3ID.fromString("Omi1");
		public static final War3ID ANIMATION_DELAY = War3ID.fromString("Omi4");
		public static final War3ID DAMAGE_TAKEN_PERCENT = War3ID.fromString("Omi3");
	}
	final class StaffOfPreservation {
		private StaffOfPreservation() {}

		public static final War3ID BUILDING_TYPES_ALLOWED = War3ID.fromString("Npr1");
	}
	final class DestroyerForm {
		private DestroyerForm() {}

		public static final War3ID LIFE_REGENERATION_RATE_PER_SECOND = War3ID.fromString("ave5");
	}
	final class RaiseDead {
		private RaiseDead() {}

		public static final War3ID UNITS_SUMMONED_TYPE_ONE = War3ID.fromString("Rai1");
		public static final War3ID UNIT_TYPE_TWO = War3ID.fromString("Rai4");
		public static final War3ID UNITS_SUMMONED_TYPE_TWO = War3ID.fromString("Rai2");
		public static final War3ID UNIT_TYPE_ONE = War3ID.fromString("Rai3");
		public static final War3ID UNIT_TYPE_FOR_LIMIT_CHECK = War3ID.fromString("Raiu");
	}
	final class HealingWardAuraHealingWard {
		private HealingWardAuraHealingWard() {}

		public static final War3ID AMOUNT_OF_HIT_POINTS_REGENERATED = War3ID.fromString("Oar1");
		public static final War3ID PERCENTAGE = War3ID.fromString("Oar2");
	}
	final class ItemHealingLesser {
		private ItemHealingLesser() {}

		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Ihpg");
	}
	final class PocketFactory {
		private PocketFactory() {}

		public static final War3ID SPAWN_INTERVAL = War3ID.fromString("Nsy1");
		public static final War3ID SPAWN_UNIT_DURATION = War3ID.fromString("Nsy3");
		public static final War3ID SPAWN_UNIT_ID = War3ID.fromString("Nsy2");
		public static final War3ID LEASH_RANGE = War3ID.fromString("Nsy5");
		public static final War3ID SPAWN_UNIT_OFFSET = War3ID.fromString("Nsy4");
		public static final War3ID FACTORY_UNIT_ID = War3ID.fromString("Nsyu");
	}
	final class ItemImmolation {
		private ItemImmolation() {}

		public static final War3ID DAMAGE_PER_DURATION = War3ID.fromString("Icfd");
		public static final War3ID MANA_USED_PER_SECOND = War3ID.fromString("Icfm");
		public static final War3ID EXTRA_MANA_REQUIRED = War3ID.fromString("Icfx");
	}
	final class PoisonSting {
		private PoisonSting() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Poi1");
		public static final War3ID STACKING_TYPE = War3ID.fromString("Poi4");
		public static final War3ID ATTACK_SPEED_FACTOR = War3ID.fromString("Poi2");
		public static final War3ID MOVEMENT_SPEED_FACTOR = War3ID.fromString("Poi3");
	}
	final class Incinerate {
		private Incinerate() {}

		public static final War3ID DEATH_DAMAGE_HALF_AREA = War3ID.fromString("Nic5");
		public static final War3ID DEATH_DAMAGE_HALF_AMOUNT = War3ID.fromString("Nic4");
		public static final War3ID DEATH_DAMAGE_DELAY = War3ID.fromString("Nic6");
		public static final War3ID BONUS_DAMAGE_MULTIPLIER = War3ID.fromString("Nic1");
		public static final War3ID DEATH_DAMAGE_FULL_AREA = War3ID.fromString("Nic3");
		public static final War3ID DEATH_DAMAGE_FULL_AMOUNT = War3ID.fromString("Nic2");
	}
	final class CarrionBeetles {
		private CarrionBeetles() {}

		public static final War3ID MAX_UNITS_SUMMONED = War3ID.fromString("Ucb5");
		public static final War3ID KILL_ON_CASTER_DEATH = War3ID.fromString("Ucb6");
	}
	final class Renew {
		private Renew() {}

		public static final War3ID POWERBUILD_COST = War3ID.fromString("Rep3");
		public static final War3ID POWERBUILD_RATE = War3ID.fromString("Rep4");
		public static final War3ID REPAIR_COST_RATIO = War3ID.fromString("Rep1");
		public static final War3ID REPAIR_TIME_RATIO = War3ID.fromString("Rep2");
		public static final War3ID NAVAL_RANGE_BONUS = War3ID.fromString("Rep5");
	}
	final class AntiMagicShell {
		private AntiMagicShell() {}

		public static final War3ID MANA_LOSS = War3ID.fromString("Ams4");
		public static final War3ID SHIELD_LIFE = War3ID.fromString("Ams3");
		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Ams2");
		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("Ams1");
	}
	final class Flare {
		private Flare() {}

		public static final War3ID EFFECT_DELAY = War3ID.fromString("Fla2");
		public static final War3ID DETECTION_TYPE = War3ID.fromString("Fla1");
		public static final War3ID FLARE_COUNT = War3ID.fromString("Fla3");
	}
	final class HardenedSkin {
		private HardenedSkin() {}

		public static final War3ID MINIMUM_DAMAGE = War3ID.fromString("Ssk2");
		public static final War3ID CHANCE_TO_REDUCE_DAMAGE_PERCENT = War3ID.fromString("Ssk1");
		public static final War3ID INCLUDE_MELEE_DAMAGE = War3ID.fromString("Ssk5");
		public static final War3ID INCLUDE_RANGED_DAMAGE = War3ID.fromString("Ssk4");
		public static final War3ID IGNORED_DAMAGE = War3ID.fromString("Ssk3");
	}
	final class ItemRecall {
		private ItemRecall() {}

		public static final War3ID MAXIMUM_NUMBER_OF_UNITS = War3ID.fromString("Irec");
	}
	final class ItemAntiMagicShell {
		private ItemAntiMagicShell() {}

		public static final War3ID MAGIC_DAMAGE_REDUCTION = War3ID.fromString("Ixs2");
		public static final War3ID DAMAGE_TO_SUMMONED_UNITS = War3ID.fromString("Ixs1");
	}
	final class Bloodlust {
		private Bloodlust() {}

		public static final War3ID SCALING_FACTOR = War3ID.fromString("Blo3");
		public static final War3ID MOVEMENT_SPEED_INCREASE_PERCENT = War3ID.fromString("Blo2");
		public static final War3ID ATTACK_SPEED_INCREASE_PERCENT = War3ID.fromString("Blo1");
	}
	final class TrueshotAura {
		private TrueshotAura() {}

		public static final War3ID DAMAGE_BONUS_PERCENT = War3ID.fromString("Ear1");
		public static final War3ID MELEE_BONUS = War3ID.fromString("Ear2");
		public static final War3ID RANGED_BONUS = War3ID.fromString("Ear3");
		public static final War3ID FLAT_BONUS = War3ID.fromString("Ear4");
	}
	final class FlameStrike {
		private FlameStrike() {}

		public static final War3ID HALF_DAMAGE_DEALT = War3ID.fromString("Hfs3");
		public static final War3ID HALF_DAMAGE_INTERVAL = War3ID.fromString("Hfs4");
		public static final War3ID FULL_DAMAGE_DEALT = War3ID.fromString("Hfs1");
		public static final War3ID FULL_DAMAGE_INTERVAL = War3ID.fromString("Hfs2");
		public static final War3ID BUILDING_REDUCTION = War3ID.fromString("Hfs5");
		public static final War3ID MAXIMUM_DAMAGE = War3ID.fromString("Hfs6");
	}
	final class ItemLifeRegeneration {
		private ItemLifeRegeneration() {}

		public static final War3ID HIT_POINTS_REGENERATED_PER_SECOND = War3ID.fromString("Ihpr");
	}
	final class ItemExperienceGain {
		private ItemExperienceGain() {}

		public static final War3ID EXPERIENCE_GAINED = War3ID.fromString("Ixpg");
	}
	final class CorrosiveBreath {
		private CorrosiveBreath() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("Cor1");
	}
	final class CargoHoldDeathNeutralHostile {
		private CargoHoldDeathNeutralHostile() {}

		public static final War3ID MOVEMENT_UPDATE_FREQUENCY = War3ID.fromString("Chd1");
		public static final War3ID ATTACK_UPDATE_FREQUENCY = War3ID.fromString("Chd2");
		public static final War3ID SUMMONED_UNIT_DAMAGE = War3ID.fromString("Chd3");
	}
	final class ItemDispel {
		private ItemDispel() {}

		public static final War3ID DAMAGE_TO_SUMMONED_UNITS = War3ID.fromString("Idid");
		public static final War3ID MANA_LOSS_PER_UNIT = War3ID.fromString("Idim");
	}
	final class ChestOfGold {
		private ChestOfGold() {}

		public static final War3ID GOLD_GIVEN = War3ID.fromString("Igol");
	}
	final class ItemSightRangeBonus {
		private ItemSightRangeBonus() {}

		public static final War3ID SIGHT_RANGE_BONUS = War3ID.fromString("Isib");
	}
	final class AerialShackles {
		private AerialShackles() {}

		public static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("mls1");
	}
	final class EatTree {
		private EatTree() {}

		public static final War3ID RIP_DELAY = War3ID.fromString("Eat1");
		public static final War3ID EAT_DELAY = War3ID.fromString("Eat2");
		public static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Eat3");
	}
	final class FrostNova {
		private FrostNova() {}

		public static final War3ID AREA_OF_EFFECT_DAMAGE = War3ID.fromString("Ufn1");
		public static final War3ID SPECIFIC_TARGET_DAMAGE = War3ID.fromString("Ufn2");
	}
	final class SummonLavaSpawn {
		private SummonLavaSpawn() {}

		public static final War3ID MAX_HITPOINT_FACTOR = War3ID.fromString("Nlm4");
		public static final War3ID SPLIT_ATTACK_COUNT = War3ID.fromString("Nlm3");
		public static final War3ID GENERATION_COUNT = War3ID.fromString("Nlm6");
		public static final War3ID LIFE_DURATION_SPLIT_BONUS = War3ID.fromString("Nlm5");
		public static final War3ID SPLIT_DELAY = War3ID.fromString("Nlm2");
	}
	final class SpawnSkeletonProbablyBlackArrow {
		private SpawnSkeletonProbablyBlackArrow() {}

		public static final War3ID UNIT_TYPE = War3ID.fromString("Sod2");
		public static final War3ID NUMBER_OF_UNITS = War3ID.fromString("Sod1");
	}
	final class MechanicalCritter {
		private MechanicalCritter() {}

		public static final War3ID NUMBER_OF_UNITS_CREATED = War3ID.fromString("mec1");
	}

}
