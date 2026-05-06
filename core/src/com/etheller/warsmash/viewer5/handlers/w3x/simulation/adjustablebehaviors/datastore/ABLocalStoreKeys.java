package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore;

public class ABLocalStoreKeys {
	// Core values
	public static final String ABILITYEDITORDATA = "_abilityEditorData";
	public static final String LEVELDATA = "_levelData";
	public static final String ALIAS = "_alias";
	public static final String CODE = "_code";
	public static final String BUFF = "_buff";
	public static final String ITEMSLOT = "_itemSlot";
	public static final String ITERATORCOUNT = "_i";
	public static final String BREAK = "_break";
	public static final String BUFFCASTINGUNIT = "_buffCastingUnit";

	public static final String PREVIOUSBEHAVIOR = "_previousBehavior";
	public static final String NEWBEHAVIOR = "_newBehavior";

	public static final String CASTINSTANCELEVEL = "_castInLvl#";

	public static final String FAILEDTOCAST = "_failedToCast#";
	public static final String TRANSFORMINGTOALT = "_transformingToAlt#";
	public static final String CHANNELING = "_channeling#";
	public static final String INTERRUPTED = "_interrupted#";
	public static final String PERIODICNEXTTICK = "_periodicNextTick#";
	public static final String PREVENTENDEVENTS = "_preventEndEvents#";

	public static final String CANTUSEREASON = "_cantUseReason";

	public static final String ISAUTOCASTTARGETING = "_isAutoCastTar";
	public static final String ISAUTOCAST = "_isAutoCast#";

	public static final String ISTOGGLEDABILITY = "_toggledAbility";
	public static final String ISFLEXABILITY = "_flexAbility";
	public static final String ISPAIRABILITY = "_pairAbility";
	public static final String ISABILITYLEVELED = "_isAbilityLeveld";
	
	
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
	public static final String CHAINUNIT = "_chainUnit#";
	public static final String COMPUNIT1 = "_compUnit1";
	public static final String COMPUNIT2 = "_compUnit2";
	
	// Item Names
	public static final String LASTCREATEDITEM = "_lastCreatedItem";
	
	// Destructable Names
	public static final String LASTCREATEDDESTRUCTABLE = "_lastCreatedDest";
	
	// Ability Names
	public static final String LASTCREATEDABILITY = "_lastCreatedAbility";
	public static final String LASTADDEDABILITY = "_lastAddedAbility";
	public static final String MATCHINGABILITY = "_matchingAbility";
	
	public static final String LASTCREATEDUNITGROUP = "_lastCreatedUnitGroup";
	public static final String LASTCREATEDUNITQUEUE = "_lastCreatedUnitQueue";

	public static final String LASTCREATEDDESTBUFF = "_lastCreatedDestBuff";
	public static final String ENUMDESTBUFF = "_enumDestBuff";
	public static final String MATCHINGDESTBUFF = "_matchingDestBuff";
	
	// Buff Names
	public static final String LASTCREATEDBUFF = "_lastCreatedBuff";
	public static final String LASTADDEDBUFF = "_lastAddedBuff";

	public static final String ENUMBUFF = "_enumBuff";
	public static final String MATCHINGBUFF = "_matchingBuff";
	
	//Generic Targeting
	public static final String ATTACKINGUNIT = "_attackingUnit#";
	public static final String ATTACKTARGET = "_attackTarget#";
	public static final String DAMAGINGUNIT = "_damagingUnit#";
	public static final String DAMAGEDUNIT = "_damagedUnit#";
	public static final String ATTACKIMPACTLOCATION = "_attackImpactLocation#";
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
	public static final String DAMAGECALC = "_damageCalc#";
	
	//Timers
	public static final String LASTCREATEDTIMER = "_lastCreatedTimer";
	public static final String LASTSTARTEDTIMER = "_lastStartedTimer";
	public static final String FIRINGTIMER = "_firingTimer";
	
	//Events
	public static final String LASTCREATEDTODEVENT = "_lastCreatedToDEvent";
	public static final String LASTCREATEDWIDEVENT = "_lastCreatedWidEvent";
	

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

	// AttackPostDamageListener Names
	public static final String LASTCREATEDAPoDL = "_lastCreatedAPoDL";

	// AttackPreDamageListener Names
	public static final String LASTCREATEDAPrDL = "_lastCreatedAPrDL";
	
	// DamageTakenListener Names
	public static final String LASTCREATEDDTL = "_lastCreatedDTL";

	// DamageTakenModificationListener Names
	public static final String LASTCREATEDDTML = "_lastCreatedDTML";

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
	
	// Autocast on/off actions
	public static final String WASAUTOCASTON = "_wasAutocastOn";
	public static final String ISAUTOCASTON = "_isAutocastOn";
	
	// Attack Modifier Names
	public static final String LASTCREATEDAMod = "_lastCreatedAMod";
	public static final String ATTACKSETTINGS = "_attackSettings#";
	public static final String ATTACKMODLOOP = "_attackModLoop#";
	public static final String THEATTACK = "_theAttack#";

	// Attack Settings
	public static final String LASTCREATEDASettings = "_lastCreatedASettings";
	
	//Unit State Listener
	public static final String LASTCREATEDUSL = "_lastCreatedUSL";
	public static final String LASTSTATELISTENERADDEDUNIT = "_lastStateListenerAddedUnit";
	
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
	
	public static String combineArgumentKey(String key) {
		return "_$" + key;
	}
	
	public static String combineUniqueValueKey(String key, int handleId) {
		return "_*" + handleId +"#" + key;
	}
}
