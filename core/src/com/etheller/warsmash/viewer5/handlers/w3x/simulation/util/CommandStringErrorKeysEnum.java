package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public enum CommandStringErrorKeysEnum {
	NOT_ENOUGH_FOOD("Nofood"), UNABLE_TO_CREATE_UNIT_DUE_TO_MAXIMUM_FOOD_LIMIT("Maxsupply"), NOT_ENOUGH_GOLD("Nogold"),
	NOT_ENOUGH_LUMBER("Nolumber"), NOT_ENOUGH_MANA("Nomana"), SPELL_IS_NOT_READY_YET("Cooldown"),
	CARGO_CAPACITY_UNAVAILABLE("Noroom"), UNABLE_TO_LOAD_TARGET("Canttransport"), UNABLE_TO_DEVOUR_TARGET("Cantdevour"),
	UNABLE_TO_CAST_CYCLONE_ON_THIS_TARGET("Cantcyclone"), UNABLE_TO_CAST_FERAL_SPIRIT_ON_THIS_TARGET("Cantspiritwolf"),
	UNABLE_TO_CAST_POSSESSION_ON_THIS_TARGET("Cantpossess"), UNABLE_TO_CAST_MANA_BURN_ON_THIS_TARGET("Cantmanaburn"),
	MUST_TARGET_A_UNIT_CAPABLE_OF_ATTACKING("Onlyattackers"), UNABLE_TO_USE_AN_ENTANGLED_GOLD_MINE("Notentangledmine"),
	UNABLE_TO_USE_A_HAUNTED_GOLD_MINE("Notblightedmine"), THAT_GOLD_MINE_IS_ALREADY_ENTANGLED("Alreadyentangled"),
	THAT_GOLD_MINE_IS_ALREADY_HAUNTED("Alreadyblightedmine"),
	MUST_TARGET_A_TREE_OR_AN_ENTANGLED_GOLD_MINE("Targetwispresources"),
	MUST_TARGET_A_HAUNTED_GOLD_MINE("Targetblightedmine"), MUST_ENTANGLE_GOLD_MINE_FIRST("Entangleminefirst"),
	MUST_HAUNT_GOLD_MINE_FIRST("Blightminefirst"), THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_WISPS("Entangledminefull"),
	THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_ACOLYTES("Blightringfull"),
	THE_SELECTED_ACOLYTE_IS_ALREADY_MINING("Acolytealreadymining"),
	UNABLE_TO_USE_A_MINE_CONTROLLED_BY_ANOTHER_PLAYER("Nototherplayersmine"), MUST_TARGET_A_GOLD_MINE("Targgetmine"),
	MUST_TARGET_RESOURCES("Targgetresources"), MUST_TARGET_A_HUMAN_BUILDING("Humanbuilding"),
	MUST_TARGET_AN_UNDEAD_BUILDING("Undeadbuilding"),
	THAT_BUILDING_IS_CURRENTLY_UNDER_CONSTRUCTION("Underconstruction"),
	THE_BUILDING_IS_ALREADY_UNDER_CONSTRUCTION("Alreadyrebuilding"), THAT_CREATURE_IS_TOO_POWERFUL("Creeptoopowerful"),
	THAT_UNIT_IS_ALREADY_HIBERNATING("Hibernating"), THAT_UNIT_IS_ALREADY_LEASHED("Magicleashed"),
	THAT_UNIT_IS_IMMUNE_TO_MAGIC("Immunetomagic"),
	MUST_TARGET_FRIENDLY_LIVING_UNITS_OR_ENEMY_UNDEAD_UNITS("Holybolttarget"),
	MUST_TARGET_ENEMY_LIVING_UNITS_OR_FRIENDLY_UNDEAD_UNITS("Deathcoiltarget"),
	MUST_TARGET_A_UNIT_OR_BLIGHTED_GROUND("Dispelmagictarget"),
	THAT_TREE_IS_OCCUPIED_TARGET_A_VACANT_TREE("Treeoccupied"), UNABLE_TO_MERGE_WITH_THAT_UNIT("Coupletarget"),
	MUST_TARGET_A_HIPPOGRYPH("Mounthippogryphtarget"), MUST_TARGET_AN_ARCHER("Archerridertarget"),
	MUST_EXPLORE_THERE_FIRST("Cantsee"), UNABLE_TO_BUILD_THERE("Cantplace"),
	TARGETED_LOCATION_IS_OUTSIDE_OF_THE_MAP_BOUNDARY("Outofbounds"), MUST_SUMMON_STRUCTURES_UPON_BLIGHT("Offblight"),
	UNABLE_TO_BUILD_SO_CLOSE_TO_THE_GOLD_MINE("Tooclosetomine"),
	UNABLE_TO_CREATE_A_GOLD_MINE_SO_CLOSE_TO_THE_TOWN("Tooclosetohall"),
	UNABLE_TO_BUILD_AWAY_FROM_A_SHORELINE("Notonshoreline"),
	A_NEWLY_CONSTRUCTED_UNIT_HAS_NO_ROOM_TO_BE_PLACED("Buildingblocked"),
	A_UNIT_COULD_NOT_BE_TELEPORTED("Teleportfail"), SOMETHING_IS_BLOCKING_THAT_TREE_STUMP("Stumpblocked"),
	UNABLE_TO_LAND_THERE("Cantland"), UNABLE_TO_ROOT_THERE("Cantroot"), TARGET_IS_NO_LONGER_ROOTABLE("Cantrootunit"),
	MUST_ROOT_ADJACENT_TO_A_GOLD_MINE_TO_ENTANGLE_IT("Mustroottoentangle"),
	MUST_ROOT_CLOSER_TO_THE_GOLD_MINE("Mustbeclosertomine"), GOLD_MINE_COULDNT_BE_ENTANGLED("Minenotentangleable"),
	TARGET_IS_OUTSIDE_RANGE("Notinrange"), TARGET_IS_INSIDE_MINIMUM_RANGE("UnderRange"),
	UNABLE_TO_TARGET_THIS_UNIT("Notthisunit"), MUST_TARGET_A_UNIT_WITH_THIS_ACTION("Targetunit"),
	MUST_TARGET_A_BUILDING_OR_TREE("Targetstructuretree"), MUST_TARGET_A_GROUND_UNIT("Targetground"),
	MUST_TARGET_AN_AIR_UNIT("Targetair"), MUST_TARGET_A_BUILDING("Targetstructure"), MUST_TARGET_A_WARD("Targetward"),
	MUST_TARGET_AN_ITEM("Targetitem"), MUST_TARGET_A_TREE("Targettree"),
	MUST_TARGET_A_BUILDING_OR_A_MECHANICAL_UNIT("Targetrepair"), MUST_TARGET_A_BRIDGE("Targetbridge"),
	MUST_TARGET_A_NAVAL_UNIT("Targetnaval"), MUST_TARGET_ONE_OF_YOUR_OWN_UNITS("Targetowned"),
	MUST_TARGET_A_FRIENDLY_UNIT("Targetally"), MUST_TARGET_A_NEUTRAL_UNIT("Targetneutral"),
	MUST_TARGET_AN_ENEMY_UNIT("Targetenemy"), MUST_TARGET_A_UNIT_YOU_CAN_CONTROL("Targetcontrol"),
	MUST_TARGET_A_HERO("Targethero"), MUST_TARGET_AN_ENEMY_HERO("Targetenemyhero"),
	MUST_TARGET_A_CORPSE("Targetcorpse"), MUST_TARGET_A_FLESHY_CORPSE("Targetfleshycorpse"),
	MUST_TARGET_A_SKELETAL_CORPSE("Targetbonecorpse"), MUST_TARGET_AN_UNDEAD_UNIT("Targetundead"),
	MUST_TARGET_A_MECHANICAL_UNIT("Targetmechanical"), MUST_TARGET_MOVEABLE_UNITS("Targetmoveable"),
	MUST_TARGET_AN_ORGANIC_GROUND_UNIT("Targetorganicground"), MUST_TARGET_AN_ANCIENT("Targetancient"),
	MUST_TARGET_AN_ARMORED_TRANSPORT("Targetarmoredtransport"), MUST_TARGET_A_UNIT_WITH_MANA("Targetmanauser"),
	MUST_TARGET_A_PEON("Targetbunkerunit"), MUST_TARGET_A_WISP("Targetwisp"), MUST_TARGET_AN_ACOLYTE("Targetacolyte"),
	MUST_TARGET_A_SACRIFICIAL_PIT("Targetpit"), THAT_TREE_IS_OCCUPIED_BY_AN_OWL("Needemptytree"),
	THAT_TREE_IS_NOT_OCCUPIED_BY_AN_OWL("Needowltree"), THERE_ARE_NO_USABLE_CORPSES_NEARBY("Cantfindcorpse"),
	THERE_ARE_NO_CORPSES_OF_FRIENDLY_UNITS_NEARBY("Cantfindfriendlycorpse"), UNABLE_TO_TARGET_UNITS("Nounits"),
	UNABLE_TO_TARGET_GROUND_UNITS("Noground"), UNABLE_TO_TARGET_AIR_UNITS("Noair"),
	UNABLE_TO_TARGET_BUILDINGS("Nostructure"), UNABLE_TO_TARGET_WARDS("Noward"), UNABLE_TO_TARGET_ITEMS("Noitem"),
	UNABLE_TO_TARGET_DEBRIS("Nodebris"), UNABLE_TO_TARGET_TREES("Notree"), UNABLE_TO_TARGET_WALLS("Nowall"),
	UNABLE_TO_TARGET_BRIDGES("Nobridge"), TARGET_BUILDING_HAS_BEEN_FROZEN("Notfrozenbldg"),
	UNABLE_TO_TARGET_NAVAL_UNITS("Nonaval"), MUST_TARGET_FRIENDLY_TOWN_HALL("Nottownhall"),
	THERE_ARE_NO_FRIENDLY_TOWN_HALLS_TO_TOWN_PORTAL_TO("Notownportalhalls"), UNABLE_TO_TARGET_SELF("Notself"),
	UNABLE_TO_TARGET_YOUR_OWN_UNITS("Notowned"), UNABLE_TO_TARGET_FRIENDLY_UNITS("Notfriendly"),
	UNABLE_TO_TARGET_NEUTRAL_UNITS("Notneutral"), UNABLE_TO_TARGET_ENEMY_UNITS("Notenemy"),
	UNABLE_TO_TARGET_UNITS_INSIDE_A_BUILDING_OR_TRANSPORT("Notcargo"),
	THAT_TARGET_IS_NOT_VISIBLE_ON_THE_MAP("Nothidden"), UNABLE_TO_TARGET_CARRIED_ITEMS("Nothiddenitem"),
	THAT_TARGET_IS_INVULNERABLE("Notinvulnerable"), UNABLE_TO_TARGET_HEROES("Nohero"),
	TARGET_MUST_BE_LIVING("Notcorpse"), UNABLE_TO_TARGET_FLESHY_CORPSES("Notfleshycorpse"),
	UNABLE_TO_TARGET_SKELETAL_CORPSES("Notbonecorpse"), MUST_TARGET_ORGANIC_UNITS("Notmechanical"),
	UNABLE_TO_TARGET_ORGANIC_UNITS("Notorganic"), CASTER_MOVEMENT_HAS_BEEN_DISABLED("Notdisabled"),
	UNABLE_TO_ATTACK_THERE("Cantattackloc"), UNABLE_TO_TARGET_THERE("Canttargetloc"),
	INVENTORY_IS_FULL("Inventoryfull"), SELECT_A_UNIT_WITH_AN_INVENTORY("Inventoryinteract"),
	ONLY_UNITS_WITH_AN_INVENTORY_CAN_PICK_UP_ITEMS("NeedInventory"),
	ONLY_HEROES_THAT_HAVE_LEARNED_SPELLS_NOT_IN_COOLDOWN_CAN_USE_THIS_ITEM("Needretrainablehero"),
	A_HERO_MUST_BE_NEARBY("Neednearbyhero"), A_VALID_PATRON_MUST_BE_NEARBY("Neednearbypatron"),
	UNABLE_TO_TARGET_SAPPERS("Notsapper"), UNABLE_TO_TARGET_ANCIENTS("Notancient"),
	UNABLE_TO_TARGET_SUMMONED_UNITS("Notsummoned"), UNABLE_TO_TARGET_TRANSPORTS_OR_BUNKERS("Nottransport"),
	TARGET_IS_BEING_UNSUMMONED("Notunsummoned"), UNABLE_TO_TARGET_ILLUSIONS("Notillusion"),
	UNABLE_TO_TARGET_MORPHING_UNITS("Notmorphing"), UNABLE_TO_TARGET_A_DRUID_OF_THE_TALON("Notdot"),
	ILLUSIONS_ARE_UNABLE_TO_HARVEST("Illusionscantharvest"), ILLUSIONS_CANNOT_PICK_UP_ITEMS("Illusionscantpickup"),
	THIS_UNIT_IS_IMMUNE_TO_POLYMORPH("Cantpolymorphunit"), UNABLE_TO_TARGET_AN_UNDEAD_UNIT("Notundead"),
	HERO_IS_AT_MAX_LEVEL("Heromaxed"), HERO_HAS_FULL_HEALTH("HPmaxed"), HERO_HAS_FULL_MANA("Manamaxed"),
	ALREADY_AT_FULL_MANA_AND_HEALTH("HPmanamaxed"), ALREADY_AT_FULL_HEALTH("UnitHPmaxed"),
	ALREADY_AT_FULL_MANA("UnitManaMaxed"), TARGET_IS_NOT_DAMAGED("RepairHPmaxed"),
	TARGET_IS_ALREADY_BEING_HEALED("Alreadybeinghealed"), TARGET_IS_ALREADY_BEING_REPAIRED("Alreadybeingrepaired"),
	SACRIFICIAL_PIT_IS_ALREADY_SACRIFICING_AN_ACOLYTE("Pitalreadysacrificing"), OUT_OF_STOCK("Outofstock"),
	COOLDOWN_OUT_OF_STOCK("Cooldownstock"), ITEM_MUST_REMAIN_IN_YOUR_INVENTORY("Cantdrop"),
	ITEM_CANNOT_BE_PAWNED("Cantpawn"), NO_PEASANTS_COULD_BE_FOUND("Calltoarms"),
	NO_TOWN_HALLS_COULD_BE_FOUND_THAT_CAN_CONVERT_PEASANTS_INTO_MILITIA("Calltoarmspeasant"),
	NO_MILITIA_COULD_BE_FOUND("Backtowork"),
	NO_TOWN_HALLS_COULD_BE_FOUND_THAT_CAN_CONVERT_MILITIA_INTO_PEASANTS("Backtoworkmilitia"),
	NO_PEONS_COULD_BE_FOUND("BattleStations"), REPLACE_THIS_ERROR_MESSAGE_WITH_SOMETHING_MEANINGFUL("Replaceme"),
	TARGET_BUILDING_HAS_LIQUID_FIRE("Notliquidfirebldg"),
	ETHEREAL_UNITS_CAN_ONLY_BE_HIT_BY_SPELLS_AND_MAGIC_DAMAGE("Notethereal"),
	TARGET_HAS_NO_STEALABLE_BUFFS("Needstealbuff"), MUST_TARGET_A_MELEE_ATTACKER("Needmeleeattacker"),
	MUST_TARGET_A_RANGED_ATTACKER("Needrangedattacker"), MUST_TARGET_A_SPECIAL_ATTACKER("Needspecialattacker"),
	MUST_TARGET_AN_ATTACK_UNIT("Needattacker"), MUST_TARGET_A_CASTER("Needcaster"),
	MUST_TARGET_AN_ATTACK_UNIT_OR_A_CASTER("Needattackerorcaster"),
	NO_STRUCTURES_ARE_AVAILABLE_TO_TELEPORT_THE_TARGET_TO("Nopreservationtarget"),
	UNABLE_TO_TRANSFORM_THIS_ITEM("Canttransformitem"), THIS_UNIT_HAS_ALREADY_BEEN_MARKED_BY_FIRE("Notmocunit"),
	CANT_IMPALE_THIS_UNIT("Cantimpale"), MUST_TARGET_AN_ENEMY_UNIT_WITH_POSITIVE_BUFFS("Needpositivebuff"),
	MUST_TARGET_AN_ENEMY_UNIT_WITH_POSITIVE_BUFFS_OR_A_SUMMONED_UNIT("Needposbufforsummoned"),
	MUST_TARGET_A_FRIENDLY_UNIT_WITH_NEGATIVE_BUFFS("Neednegativebuff"), NOT_ENOUGH_MANA_TO_ABSORB("Absorbmana"),
	UNABLE_TO_PICK_UP_THIS_ITEM("Canttakeitem"), UNABLE_TO_DROP_THIS_ITEM("Cantdropitem"),
	UNABLE_TO_USE_THIS_ITEM("Cantuseitem"), UNABLE_TO_USE_POWERUPS("Notpowerup"),
	THIS_ITEM_IS_COOLING_DOWN("Itemcooldown"), UNABLE_TO_FIND_COUPLE_TARGET("Cantfindcoupletarget"),
	THIS_UNIT_HAS_A_DISABLED_INVENTORY("Notdisabledinventory"), THIS_UNIT_HAS_RESISTANT_SKIN("Resistantskin"),
	UNABLE_TO_USE_INVULNERABILITY_GRANTING_SPELLS_OR_ITEMS("Notinvulnerablespell"),
	MUST_TARGET_SUMMONED_UNITS("Needsummoned"), UNABLE_TO_SUBMERGE_THERE("Cantsubmergethere"),
	THIS_UNIT_HAS_ALREADY_BEEN_STRICKEN_WITH_DOOM("Alreadydoomed");

	private String key;

	CommandStringErrorKeysEnum(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
}
