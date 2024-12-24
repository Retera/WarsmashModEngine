package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core;

public class ABLocalStoreKeys {
	// Core values
	public static final String ABILITYEDITORDATA = "_abilityEditorData";
	public static final String LEVELDATA = "_levelData";
	public static final String PARENTLEVELDATA = "_parentLevelData";
	public static final String PARENTCASTER = "_parentCaster";
	public static final String PARENTLOCALSTORE = "_parentLocalStore";
	public static final String ALIAS = "_alias";
	public static final String CODE = "_code";
	public static final String GAME = "_game";
	public static final String THISUNIT = "_thisUnit";
	public static final String ABILITY = "_ability";
	public static final String ITEM = "_item";
	public static final String ITEMSLOT = "_itemSlot";
	public static final String ITERATORCOUNT = "_i";
	public static final String BREAK = "_break";
	public static final String BUFFCASTINGUNIT = "_buffCastingUnit";
	
	public static final String NEWBEHAVIOR = "_newBehavior";

	public static final String FAILEDTOCAST = "_failedToCast#";
	public static final String TRANSFORMINGTOALT = "_transformingToAlt#";
	public static final String CHANNELING = "_channeling#";
	public static final String INTERRUPTED = "_interrupted#";
	public static final String PERIODICNEXTTICK = "_periodicNextTick#";

	public static final String CANTUSEREASON = "_cantUseReason";

	public static final String TOGGLEDABILITY = "_toggledAbility";
	public static final String FLEXABILITY = "_flexAbility";
	public static final String PAIRABILITY = "_pairAbility";
	
	// Aura Template
	public static final String AURAGROUP = "_auraGroup";
	
	// FX names
	public static final String LASTCREATEDFX = "_lastCreatedFx";
	public static final String LASTCREATEDLIGHTNING = "_lastCreatedLtng";
	
	// Unit Names
	public static final String ENUMUNIT = "_enumUnit#";
	public static final String MATCHINGUNIT = "_matchingUnit#";
	public static final String LASTCREATEDUNIT = "_lastCreatedUnit";
	public static final String LASTADDEDUNIT = "_lastAddedUnit";
	public static final String LASTREMOVEDDUNIT = "_lastRemovedUnit";
	
	// Ability Names
	public static final String LASTCREATEDABILITY = "_lastCreatedAbility";
	public static final String LASTCREATEDBUFF = "_lastCreatedBuff";
	public static final String LASTADDEDABILITY = "_lastAddedAbility";
	public static final String LASTADDEDBUFF = "_lastAddedBuff";
	public static final String CURRENTLEVEL = "_currentLevel";
	
	public static final String LASTCREATEDUNITGROUP = "_lastCreatedUnitGroup";
	public static final String LASTCREATEDUNITQUEUE = "_lastCreatedUnitQueue";

	public static final String LASTCREATEDDESTBUFF = "_lastCreatedDestBuff";
	
	//Generic Targeting
	public static final String ATTACKINGUNIT = "_attackingUnit#";
	public static final String ATTACKEDUNIT = "_attackedUnit#";
	public static final String ABILITYTARGETEDUNIT = "_abilityTargetedUnit#";
	public static final String ABILITYTARGETEDDESTRUCTABLE = "_abilityTargetedDestructable#";
	public static final String ABILITYTARGETEDITEM = "_abilityTargetedItem#";
	public static final String ABILITYTARGETEDLOCATION = "_abilityTargetedLocation#";

	//Event Targeting
	public static final String EVENTABILITY = "_eventAbility#";
	public static final String EVENTABILITYID = "_eventAbilityId#";
	public static final String EVENTCASTINGUNIT = "_eventCastingUnit#";
	public static final String EVENTTARGETEDUNIT = "_eventTargetedUnit#";
	public static final String EVENTTARGETEDDESTRUCTABLE = "_eventTargetedDestructable#";
	public static final String EVENTTARGETEDITEM = "_eventTargetedItem#";
	public static final String EVENTTARGETEDLOCATION = "_eventTargetedLocation#";
	
	//Generic Attacks
	public static final String BASEDAMAGEDEALT = "_baseDamageDealt#";
	public static final String BONUSDAMAGEDEALT = "_bonusDamageDealt#";
	public static final String TOTALDAMAGEDEALT = "_totalDamageDealt#";
	public static final String WEAPONTYPE = "_weaponType#";
	public static final String ATTACKTYPE = "_attackType#";
	public static final String DAMAGETYPE = "_damageType#";
	
	//Timers
	public static final String LASTCREATEDTIMER = "_lastCreatedTimer";
	public static final String LASTSTARTEDTIMER = "_lastStartedTimer";
	public static final String FIRINGTIMER = "_firingTimer";
	
	//Events
	public static final String LASTCREATEDTODEVENT = "_lastCreatedToDEvent";
	

	// Dest Names
	public static final String BUFFEDDEST = "_buffedDest#";
	public static final String ENUMDESTRUCTABLE = "_enumDest#";
	
	//Projectiles
	public static final String LASTCREATEDPROJECTILE = "_lastCreatedProjectile";
	public static final String THISPROJECTILE = "_thisProjectile#";
	public static final String PROJECTILEUNITTARGETS = "_projUnitTargets#";
	public static final String PROJECTILEDESTTARGETS = "_projDestTargets#";
	public static final String PROJECTILECURRENTLOC = "_projCurrentLoc#";
	public static final String PROJECTILEHITUNIT = "_projHitUnit#";
	public static final String PROJECTILEHITDEST = "_projHitDest#";
	
	//vision
	public static final String LASTCREATEDVISIONMODIFIER = "_lastCreatedVisionMod";
	
	
	// Pairing
	public static final String ABILITYPAIREDUNIT = "_abilityPairedUnit#";
	public static final String LASTPARTNERABILITY = "_lastPartnerAbility";
	
	// Transforming
	public static final String ACTIVE_ALTITUDE_ADJUSTMENT = "_activeAltAdj";
	public static final String WAITING_ANIMATION = "_morphTimer";
	
	

	// NonStackingStatBuff Names
	public static final String LASTCREATEDNSSB = "_lastCreatedNSSB";
	
	// StateModBuff Names
	public static final String LASTCREATEDSMB = "_lastCreatedSMB";

	// AttackEvasionListener Names
	public static final String LASTCREATEDAEL = "_lastCreatedAEL";
	public static final String DAMAGEISATTACK = "_damageIsAttack#";
	public static final String DAMAGEISRANGED = "_damageIsRanged#";

	// AttackPostDamageListener Names
	public static final String LASTCREATEDAPoDL = "_lastCreatedAPoDL";

	// AttackPreDamageListener Names
	public static final String LASTCREATEDAPrDL = "_lastCreatedAPrDL";
	public static final String PREDAMAGERESULT = "_preDamageResult#";
	public static final String PREDAMAGESTACKING = "_preDamageStacking#";
	
	// DamageTakenListener Names
	public static final String LASTCREATEDDTL = "_lastCreatedDTL";

	// DamageTakenModificationListener Names
	public static final String LASTCREATEDDTML = "_lastCreatedDTML";
	public static final String DAMAGEMODRESULT = "_damageModResult#";

	// DamageTakenModificationListener Names
	public static final String LASTCREATEDFDTML = "_lastCreatedFDTML";
	
	// DeathReplacementEffect Names
	public static final String LASTCREATEDDRE = "_lastCreatedDRE";
	public static final String KILLINGUNIT = "_killingUnit#";
	public static final String DYINGUNIT = "_dyingUnit#";
	public static final String DEATHRESULT = "_deathResult#";
	public static final String DEATHSTACKING = "_deathStacking#";
	
	// Reaction Names
	public static final String LASTCREATEDAtkPRL = "_lastCreatedAtkPRL";
	public static final String LASTCREATEDAbPRL = "_lastCreatedAbPRL";
	public static final String LASTCREATEDAbERL = "_lastCreatedAbERL";
	public static final String REACTIONALLOWHIT = "_reactionAllowHit#";
	public static final String ATTACKPROJ = "_attackProj#";
	public static final String ABILITYPROJ = "_abilityProj#";
	public static final String REACTIONABILITY = "_reactionAbility#";
	public static final String REACTIONABILITYCASTER = "_reactionAbilityCaster#";
	public static final String REACTIONABILITYTARGET = "_reactionAbilityTarget#";
	
	// Behavior Change Names
	public static final String LASTCREATEDBCL = "_lastCreatedBCL";
	public static final String PRECHANGEBEHAVIOR = "_preChangeBehavior#";
	public static final String POSTCHANGEBEHAVIOR = "_postChangeBehaviorj#";
	public static final String BEHAVIORONGOING = "_behaviorOngoing#";
	
	public static String combineKey(String key, int castId) {
		return key + castId;
	}
	
	public static String combineUserKey(String key, int castId) {
		return "__" + key;
	}
	
	public static String combineUserInstanceKey(String key, int castId) {
		return "__" + key + "#" + castId;
	}
	
	public static String combineSubroutineKey(String key, int castId) {
		return "_!" + key;
	}
	
	public static String combineSubroutineInstanceKey(String key, int castId) {
		return "_!" + key + "#" + castId;
	}
}
