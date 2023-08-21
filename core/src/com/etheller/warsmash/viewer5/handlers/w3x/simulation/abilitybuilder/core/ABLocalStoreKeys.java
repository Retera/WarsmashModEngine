package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core;

public class ABLocalStoreKeys {
	// Core values
	public static final String LEVELDATA = "_levelData";
	public static final String PARENTLEVELDATA = "_parentLevelData";
	public static final String PARENTCASTER = "_parentCaster";
	public static final String PARENTLOCALSTORE = "_parentLocalStore";
	public static final String ALIAS = "_alias";
	public static final String CODE = "_code";
	public static final String GAME = "_game";
	public static final String THISUNIT = "_thisUnit";
	public static final String ITERATORCOUNT = "_i";

	public static final String CHANNELING = "_channeling#";
	public static final String INTERRUPTED = "_interrupted#";
	public static final String PERIODICNEXTTICK = "_periodicNextTick#";

	public static final String CANTUSEREASON = "_cantUseReason";

	public static final String TOGGLEDABILITY = "_toggledAbility";
	public static final String FLEXABILITY = "_flexAbility";
	
	// Aura Template
	public static final String AURAGROUP = "_auraGroup";
	
	// FX names
	public static final String LASTCREATEDFX = "_lastCreatedFx";
	public static final String LASTCREATEDLIGHTNING = "_lastCreatedLtng";
	
	// Unit Names
	public static final String ENUMUNIT = "_enumUnit#";
	public static final String LASTADDEDUNIT = "_lastAddedUnit";
	public static final String LASTREMOVEDDUNIT = "_lastRemovedUnit";
	
	// Ability Names
	public static final String LASTCREATEDABILITY = "_lastCreatedAbility";
	public static final String LASTCREATEDBUFF = "_lastCreatedBuff";
	public static final String LASTADDEDABILITY = "_lastAddedAbility";
	public static final String LASTADDEDBUFF = "_lastAddedBuff";
	public static final String CURRENTLEVEL = "_currentLevel";
	public static final String LASTCREATEDUNITGROUP = "_lastCreatedUnitGroup";
	
	//Generic Targetting
	public static final String ATTACKINGUNIT = "_attackingUnit#";
	public static final String ATTACKEDUNIT = "_attackedUnit#";
	public static final String ABILITYTARGETEDUNIT = "_abilityTargetedUnit#";
	public static final String ABILITYTARGETEDLOCATION = "_abilityTargetedLocation#";
	
	//Generic Attacks
	public static final String BASEDAMAGEDEALT = "_baseDamageDealt#";
	public static final String BONUSDAMAGEDEALT = "_bonusDamageDealt#";
	public static final String TOTALDAMAGEDEALT = "_totalDamageDealt#";
	public static final String WEAPONTYPE = "_weaponType#";
	public static final String ATTACKTYPE = "_attackType#";
	public static final String DAMAGETYPE = "_damageType#";
	
	//Timers
	public static final String LASTCREATEDTIMER = "_lastCreatedTimer";
	public static final String LASTSTARTEDTIMER = "_lastCreatedTimer";
	public static final String FIRINGTIMER = "_firingTimer";
	
	
	

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
	
	public static String combineKey(String key, int castId) {
		return key + castId;
	}
	
	public static String combineUserKey(String key, int castId) {
		return "__" + key;
	}
	
	public static String combineUserInstanceKey(String key, int castId) {
		return "__" + key + "#" + castId;
	}
}
