//=====================================================
// StringList API                                      
//=====================================================
library StringListAPI
	type stringlist extends handle
	
	native CreateStringList takes nothing returns stringlist
	native StringListAdd takes stringlist whichList, string toAdd returns nothing
	native StringListRemove takes stringlist whichList, string toRemove returns boolean
	native StringListSize takes stringlist whichList returns integer 
	native StringListGet takes stringlist whichList, integer index returns string
endlibrary

//=====================================================
// TargetTypes API                                      
//=====================================================
// This is meant to map fairly directly to the target
// types in world editor.
library TargetTypesAPI requires StringListAPI
	type targettype extends handle
	type targettypes extends handle

	constant native ConvertTargetType takes integer x returns targettype

	globals
		constant targettype TARGET_TYPE_AIR                               = ConvertTargetType(0)
		constant targettype TARGET_TYPE_ALIVE                             = ConvertTargetType(1)
		constant targettype TARGET_TYPE_ALLIES                            = ConvertTargetType(2)
		constant targettype TARGET_TYPE_DEAD                              = ConvertTargetType(3)
		constant targettype TARGET_TYPE_DEBRIS                            = ConvertTargetType(4)
		constant targettype TARGET_TYPE_ENEMIES                           = ConvertTargetType(5)
		constant targettype TARGET_TYPE_GROUND                            = ConvertTargetType(6)
		constant targettype TARGET_TYPE_HERO                              = ConvertTargetType(7)
		constant targettype TARGET_TYPE_INVULNERABLE                      = ConvertTargetType(8)
		constant targettype TARGET_TYPE_ITEM                              = ConvertTargetType(9)
		constant targettype TARGET_TYPE_MECHANICAL                        = ConvertTargetType(10)
		constant targettype TARGET_TYPE_NEUTRAL                           = ConvertTargetType(11)
		constant targettype TARGET_TYPE_NONE                              = ConvertTargetType(12)
		constant targettype TARGET_TYPE_NONHERO                           = ConvertTargetType(13)
		constant targettype TARGET_TYPE_NONSAPPER                         = ConvertTargetType(14)
		constant targettype TARGET_TYPE_NOTSELF                           = ConvertTargetType(15)
		constant targettype TARGET_TYPE_ORGANIC                           = ConvertTargetType(16)
		constant targettype TARGET_TYPE_PLAYERUNITS                       = ConvertTargetType(17)
		constant targettype TARGET_TYPE_SAPPER                            = ConvertTargetType(18)
		constant targettype TARGET_TYPE_SELF                              = ConvertTargetType(19)
		constant targettype TARGET_TYPE_STRUCTURE                         = ConvertTargetType(20)
		constant targettype TARGET_TYPE_TERRAIN                           = ConvertTargetType(21)
		constant targettype TARGET_TYPE_TREE                              = ConvertTargetType(22)
		constant targettype TARGET_TYPE_VULNERABLE                        = ConvertTargetType(23)
		constant targettype TARGET_TYPE_WALL                              = ConvertTargetType(24)
		constant targettype TARGET_TYPE_WARD                              = ConvertTargetType(25)
		constant targettype TARGET_TYPE_ANCIENT                           = ConvertTargetType(26)
		constant targettype TARGET_TYPE_NONANCIENT                        = ConvertTargetType(27)
		constant targettype TARGET_TYPE_FRIEND                            = ConvertTargetType(28)
		constant targettype TARGET_TYPE_BRIDGE                            = ConvertTargetType(29)
		constant targettype TARGET_TYPE_DECORATION                        = ConvertTargetType(30)
		constant targettype TARGET_TYPE_NON_MAGIC_IMMUNE                  = ConvertTargetType(31)
		constant targettype TARGET_TYPE_NON_ETHEREAL                      = ConvertTargetType(32)
	endglobals

	native CreateTargetTypes takes nothing returns targettypes
	native TargetTypesAdd takes targettypes whichSet, targettype whichType returns boolean
	native TargetTypesRemove takes targettypes whichSet, targettype whichType returns boolean
	native TargetTypesContains takes targettypes whichSet, targettype whichType returns boolean
	// NOTE: always use one of the following two functions to parse a string to a target type
	// when reading from SLK. Some of the old data has stupid values such as "alli" or "ally"
	// or "allies" or "allied" interchangeably for `TARGET_TYPE_ALLIES`. The following
	// two functions parse all currently known values.
	native ParseTargetTypes takes stringlist whichTypeStringList returns targettypes
	// NOTE: the ParseTargetType function may return null or something null-ish if
	// the string could not be parsed
	native ParseTargetType takes string whichTypeString returns targettype
	
	// this is an unbound native, which for now has no function, but if we move from Java to C++ then
	// it would be needed for cleanup
	native DestroyTargetTypes takes targettypes x returns nothing

	struct TargetTypes
		// NOTE: this function might be faster than ParseTargetTypes but maybe not.
		// the Java one is stupid and does 3 memory allocations, but this one maybe
		// only allocates for the handles. is it better? Anyway, for posterity, this
		// can be considered identical to the implementation of ParseTargetTypes.
		// but you can invoke it with TargetTypes.parse so maybe that
		// will help users remember it is not a native, since generally
		// we don't support native struct members currently
		static method parse takes stringlist whichTypeStringList returns targettypes
			local targettypes result = CreateTargetTypes()
			local targettype parsedType
			local integer i = 0
			local integer l = StringListSize(whichTypeStringList)
			loop
				exitwhen i >= l

				set parsedType = ParseTargetType(StringListGet(whichTypeStringList, i))
				if parsedType != null then
					call TargetTypesAdd(result, parsedType)
				endif
				set i = i + 1
			endloop

			return result
		endmethod
	endstruct
endlibrary

//=================================================================================================
//=================================================================================================
// AbilitiesCommonLegacy (contains APIs that have not been moved to their own libraries yet)
//=================================================================================================
//=================================================================================================
library AbilitiesCommonLegacy requires TargetTypesAPI, AnimationTokensAPI

	// ability customization API types
	type abilitytypeleveldata extends handle // GetHandleId(myAbilityTypeLevelData) is a crash case, dont' do it
	type texttagconfigtype extends handle
	//type activeability extends ability (this comment is a reminder to either scap or implement this type for real)
	type localstore extends handle // GetHandleId(myLocalStore) is a crash case, dont' do it
	type destructablebuff extends handle // a buff that is applied to a destructable
	type projectile extends handle
	type gameobject extends handle
	type worldeditordatatype extends handle
	type nonstackingstatbonus extends handle
	type nonstackingstatbonustype extends handle
	type statemod extends handle
	type statemodtype extends handle
	type abilitybuilderconfiguration extends handle
	type autocasttype extends handle
	type abconftype extends handle

	type datafieldletter extends handle

	type abtimeofdayevent extends handle // Ability Builder time of day event (doesnt have handleid for now)  
	type abilitydisabletype extends handle
	type resourcetype extends handle

	// In general don't use abtimer unless you have to; was made to match json. 
	type abtimer extends timer // "call StartTimer(...)" on an abtimer will crash, must use "StartABTimer"

	// the IntExpr is like BoolExpr but it's for integers
	type intexpr extends handle

	constant native ConvertTextTagConfigType takes integer x returns texttagconfigtype
	constant native ConvertWorldEditorDataType takes integer x returns worldeditordatatype
	constant native ConvertNonStackingStatBonusType takes integer x returns nonstackingstatbonustype
	constant native ConvertStateModType takes integer x returns statemodtype
	constant native ConvertDataFieldLetter takes integer x returns datafieldletter
	constant native ConvertAutocastType takes integer x returns autocasttype
	constant native ConvertABConfType takes integer x returns abconftype
	constant native ConvertAbilityDisableType takes integer x returns abilitydisabletype
	constant native ConvertResourceType takes integer x returns resourcetype

	globals
		constant autocasttype AUTOCAST_TYPE_NONE                              = ConvertAutocastType(0)
		constant autocasttype AUTOCAST_TYPE_LOWESTHP                          = ConvertAutocastType(1)
		constant autocasttype AUTOCAST_TYPE_HIGESTHP                          = ConvertAutocastType(2)
		constant autocasttype AUTOCAST_TYPE_ATTACKTARGETING                   = ConvertAutocastType(3)
		constant autocasttype AUTOCAST_TYPE_ATTACKINGALLY                     = ConvertAutocastType(4)
		constant autocasttype AUTOCAST_TYPE_ATTACKINGENEMY                    = ConvertAutocastType(5)
		constant autocasttype AUTOCAST_TYPE_NEARESTVALID                      = ConvertAutocastType(6)
		constant autocasttype AUTOCAST_TYPE_NEARESTENEMY                      = ConvertAutocastType(7)
		constant autocasttype AUTOCAST_TYPE_NOTARGET                          = ConvertAutocastType(8)
		constant autocasttype AUTOCAST_TYPE_ATTACKREPLACEMENT                 = ConvertAutocastType(9)
		
		constant abilitydisabletype ABILITY_DISABLE_TYPE_REQUIREMENTS                      = ConvertAbilityDisableType(0)
		constant abilitydisabletype ABILITY_DISABLE_TYPE_CONSTRUCTION                      = ConvertAbilityDisableType(1)
		constant abilitydisabletype ABILITY_DISABLE_TYPE_TRANSFORMATION                    = ConvertAbilityDisableType(2)
		constant abilitydisabletype ABILITY_DISABLE_TYPE_TRIGGER                           = ConvertAbilityDisableType(3)
		constant abilitydisabletype ABILITY_DISABLE_TYPE_ATTACKDISABLED                    = ConvertAbilityDisableType(4)
		constant abilitydisabletype ABILITY_DISABLE_TYPE_PLAYER                            = ConvertAbilityDisableType(5)
		
		constant abconftype AB_CONF_TYPE_NORMAL_AUTOTARGET                 = ConvertABConfType(0)
		constant abconftype AB_CONF_TYPE_NORMAL_PAIRING                    = ConvertABConfType(1)
		constant abconftype AB_CONF_TYPE_NORMAL_FLEXTARGET_SIMPLE          = ConvertABConfType(2)
		constant abconftype AB_CONF_TYPE_NORMAL_UNITTARGET_SIMPLE          = ConvertABConfType(3)
		constant abconftype AB_CONF_TYPE_NORMAL_POINTTARGET_SIMPLE         = ConvertABConfType(4)
		constant abconftype AB_CONF_TYPE_NORMAL_NOTARGET_SIMPLE            = ConvertABConfType(5)
		constant abconftype AB_CONF_TYPE_NORMAL_FLEXTARGET                 = ConvertABConfType(6)
		constant abconftype AB_CONF_TYPE_NORMAL_UNITTARGET                 = ConvertABConfType(7)
		constant abconftype AB_CONF_TYPE_NORMAL_POINTTARGET                = ConvertABConfType(8)
		constant abconftype AB_CONF_TYPE_NORMAL_NOTARGET                   = ConvertABConfType(9)
		constant abconftype AB_CONF_TYPE_TOGGLE                            = ConvertABConfType(10)
		constant abconftype AB_CONF_TYPE_SMART                             = ConvertABConfType(11)
		constant abconftype AB_CONF_TYPE_PASSIVE                           = ConvertABConfType(12)
		constant abconftype AB_CONF_TYPE_TEMPLATE                          = ConvertABConfType(13)
		constant abconftype AB_CONF_TYPE_HIDDEN                            = ConvertABConfType(14)
		
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_GOLD                              = ConvertTextTagConfigType(0)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_LUMBER                            = ConvertTextTagConfigType(1)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_GOLD_BOUNTY                       = ConvertTextTagConfigType(2)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_LUMBER_BOUNTY                     = ConvertTextTagConfigType(3)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_XP                                = ConvertTextTagConfigType(4) // 1.32+ otherwise wont load
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_MISS_TEXT                         = ConvertTextTagConfigType(5)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_CRITICAL_STRIKE                   = ConvertTextTagConfigType(6)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_SHADOW_STRIKE                     = ConvertTextTagConfigType(7)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_MANA_BURN                         = ConvertTextTagConfigType(8)
		constant texttagconfigtype TEXT_TAG_CONFIG_TYPE_BASH                              = ConvertTextTagConfigType(9)
		
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_UNITS                    = ConvertWorldEditorDataType(0)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_ITEMS                    = ConvertWorldEditorDataType(1)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_DESTRUCTABLES            = ConvertWorldEditorDataType(2)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_DOODADS                  = ConvertWorldEditorDataType(3)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_ABILITIES                = ConvertWorldEditorDataType(4)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_BUFFS_EFFECTS            = ConvertWorldEditorDataType(5)
		constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_UPGRADES                 = ConvertWorldEditorDataType(6)
		
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MELEEATK                          = ConvertNonStackingStatBonusType(0)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MELEEATKPCT                       = ConvertNonStackingStatBonusType(1)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_RNGDATK                           = ConvertNonStackingStatBonusType(2)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_RNGDATKPCT                        = ConvertNonStackingStatBonusType(3)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_ATKSPD                            = ConvertNonStackingStatBonusType(4)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_DEF                               = ConvertNonStackingStatBonusType(5)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_DEFPCT                            = ConvertNonStackingStatBonusType(6)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_HPGEN                             = ConvertNonStackingStatBonusType(7)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_HPGENPCT                          = ConvertNonStackingStatBonusType(8)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXHPGENPCT                       = ConvertNonStackingStatBonusType(9)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MPGEN                             = ConvertNonStackingStatBonusType(10)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MPGENPCT                          = ConvertNonStackingStatBonusType(11)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXMPGENPCT                       = ConvertNonStackingStatBonusType(12)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MVSPD                             = ConvertNonStackingStatBonusType(13)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MVSPDPCT                          = ConvertNonStackingStatBonusType(14)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_HPSTEAL                           = ConvertNonStackingStatBonusType(15)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_THORNS                            = ConvertNonStackingStatBonusType(16)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_THORNSPCT                         = ConvertNonStackingStatBonusType(17)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXHP                             = ConvertNonStackingStatBonusType(18)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXHPPCT                          = ConvertNonStackingStatBonusType(19)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXMP                             = ConvertNonStackingStatBonusType(20)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_MAXMPPCT                          = ConvertNonStackingStatBonusType(21)
		// These are for parsing
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_ALLATK                            = ConvertNonStackingStatBonusType(22)
		constant nonstackingstatbonustype NON_STACKING_STAT_BONUS_TYPE_ALLATKPCT                         = ConvertNonStackingStatBonusType(23)
		
		constant statemodtype STATE_MOD_TYPE_ETHEREAL                          = ConvertStateModType(0)
		constant statemodtype STATE_MOD_TYPE_RESISTANT                         = ConvertStateModType(1)
		constant statemodtype STATE_MOD_TYPE_SLEEPING                          = ConvertStateModType(2)
		constant statemodtype STATE_MOD_TYPE_STUN                              = ConvertStateModType(3)
		constant statemodtype STATE_MOD_TYPE_MAGIC_IMMUNE                      = ConvertStateModType(4)
		constant statemodtype STATE_MOD_TYPE_MORPH_IMMUNE                      = ConvertStateModType(5)
		constant statemodtype STATE_MOD_TYPE_SNARED                            = ConvertStateModType(6)
		constant statemodtype STATE_MOD_TYPE_DISABLE_AUTO_ATTACK               = ConvertStateModType(7)
		constant statemodtype STATE_MOD_TYPE_DISABLE_AUTO_CAST                 = ConvertStateModType(8)
		constant statemodtype STATE_MOD_TYPE_DISABLE_ASSIST_ALLY               = ConvertStateModType(9)
		constant statemodtype STATE_MOD_TYPE_DISABLE_ATTACK                    = ConvertStateModType(10)
		constant statemodtype STATE_MOD_TYPE_DISABLE_MELEE_ATTACK              = ConvertStateModType(11)
		constant statemodtype STATE_MOD_TYPE_DISABLE_RANGED_ATTACK             = ConvertStateModType(12)
		constant statemodtype STATE_MOD_TYPE_DISABLE_SPECIAL_ATTACK            = ConvertStateModType(13)
		constant statemodtype STATE_MOD_TYPE_DISABLE_SPELLS                    = ConvertStateModType(14)
		constant statemodtype STATE_MOD_TYPE_DISABLE_UNIT_COLLISION            = ConvertStateModType(15)
		constant statemodtype STATE_MOD_TYPE_DISABLE_BUILDING_COLLISION        = ConvertStateModType(16)
		constant statemodtype STATE_MOD_TYPE_INVULNERABLE                      = ConvertStateModType(17)
		constant statemodtype STATE_MOD_TYPE_INVISIBLE                         = ConvertStateModType(18)
		constant statemodtype STATE_MOD_TYPE_DETECTOR                          = ConvertStateModType(19)
		constant statemodtype STATE_MOD_TYPE_DETECTED                          = ConvertStateModType(20)

		// These are exposing resource types from within the code system, not offering a mechanism for adding more. At the moment, if you
		// want to add more, you should edit the Java code and fork the engine, copy the ResourceType enum values in the Java, and then
		// add a corresponding entry below.
		constant resourcetype RESOURCE_TYPE_GOLD                              = ConvertResourceType(0)
		constant resourcetype RESOURCE_TYPE_LUMBER                            = ConvertResourceType(1)
		constant resourcetype RESOURCE_TYPE_FOOD                              = ConvertResourceType(2)
		constant resourcetype RESOURCE_TYPE_MANA                              = ConvertResourceType(3)
		
		constant datafieldletter DATA_FIELD_LETTER_A                                 = ConvertDataFieldLetter(0)
		constant datafieldletter DATA_FIELD_LETTER_B                                 = ConvertDataFieldLetter(1)
		constant datafieldletter DATA_FIELD_LETTER_C                                 = ConvertDataFieldLetter(2)
		constant datafieldletter DATA_FIELD_LETTER_D                                 = ConvertDataFieldLetter(3)
		constant datafieldletter DATA_FIELD_LETTER_E                                 = ConvertDataFieldLetter(4)
		constant datafieldletter DATA_FIELD_LETTER_F                                 = ConvertDataFieldLetter(5)
		constant datafieldletter DATA_FIELD_LETTER_G                                 = ConvertDataFieldLetter(6)
		constant datafieldletter DATA_FIELD_LETTER_H                                 = ConvertDataFieldLetter(7)
		constant datafieldletter DATA_FIELD_LETTER_I                                 = ConvertDataFieldLetter(8)
		constant datafieldletter DATA_FIELD_LETTER_J                                 = ConvertDataFieldLetter(9)

		constant string DEFAULT_ATTACH_POINTS = null /* at the moment, null is hardcoded to use defaults */
	endglobals                                                                                         

	//=================================================================================================
	// IntExpr API
	//=================================================================================================
	// These are like boolexpr, but they are for integers
	native IntExpr takes code func returns intexpr
	native DestroyIntExpr takes intexpr x returns nothing


	//=================================================================================================
	// Ability "user data" API (DEPRECATED)
	//=================================================================================================
	// used for just storing to a hashtable built into the ability, basically
	// (maybe later we replace it with direct use of jass hashtables)
	// TOTALLY SUPERCEDED BY LOCAL STORE API, THEY READ AND WRITE TO THE SAME STUFF
	// AND THESE MIGHT BE REMOVED IN FAVOR OF GetAbilityLocalStore
	native GetAbilityUserDataString takes ability whichAbility, string childKey returns string
	native GetAbilityUserDataInteger takes ability whichAbility, string childKey returns integer
	native GetAbilityUserDataBoolean takes ability whichAbility, string childKey returns boolean
	native GetAbilityUserDataAbilityTypeLevelDataHandle takes ability whichAbility, string childKey returns abilitytypeleveldata
	native GetAbilityUserDataAbilityHandle takes ability whichAbility, string childKey returns ability
	native GetAbilityUserDataUnitHandle takes ability whichAbility, string childKey returns unit
	native GetAbilityUserDataDestructableHandle takes ability whichAbility, string childKey returns destructable
	native GetAbilityUserDataDestructableBuffHandle takes ability whichAbility, string childKey returns destructablebuff

	// setters: return true if there was some previous value stored at the child key
	native SetAbilityUserDataString takes ability whichAbility, string childKey, string value returns boolean
	native SetAbilityUserDataInteger takes ability whichAbility, string childKey, integer value returns boolean
	native SetAbilityUserDataBoolean takes ability whichAbility, string childKey, boolean value returns boolean
	native SetAbilityUserDataAbilityTypeLevelDataHandle takes ability whichAbility, string childKey, abilitytypeleveldata value returns boolean
	native SetAbilityUserDataAbilityHandle takes ability whichAbility, string childKey, ability value returns boolean
	native SetAbilityUserDataUnitHandle takes ability whichAbility, string childKey, unit value returns boolean
	native SetAbilityUserDataDestructableHandle takes ability whichAbility, string childKey, destructable value returns boolean
	native SetAbilityUserDataDestructableBuffHandle takes ability whichAbility, string childKey, destructablebuff value returns boolean

	native HasAbilityUserData takes ability whichAbility, string childKey returns boolean

	native FlushParentAbilityUserData takes ability whichAbility returns nothing
	native FlushChildAbilityUserData takes ability whichAbility, string childKey returns boolean

	//=================================================================================================
	// Local Store API
	//=================================================================================================
	// one dimensional (smaller) hashtables
	// ... Also, the local stores are slightly different from Jass "hashtable" on Warsmash, because
	// of a performance thing. At the moment our emulator "hashtable" type is storing the data in its
	// wrapped Jass representation, whereas Local Store is storing it as the original Java thing
	// being represented. In principle, this means that "Get" functions on Local Store are less
	// performant in Jass than the equivalent "Load" functions from Jass hashtables on Warsmash,
	// because the handle wrappers are recreated when calling "get" for whatever,
	// but the computer memory storage used by live "localstore" objects will be a smaller amount
	// of memory, and when the non-jass system accesses them it might be faster.
	// Most likely the difference will be pointless, though.
	// - Above does not apply to "GetLocalStoreCode" and "SetLocalStoreCode" which are not
	// compatible with corresponding JSON. i.e. if you manage to define an ability in JSON but then
	// access it in JASS, the JSON "create subroutine" native that stores JSON instructions into
	// a local store are storing something that JASS can't load and would simply cause errors.
	// (I do not anticipate a need for that manner of cross compatibility, wherein we would run
	// both JASS and JSON simultaneously, however.)

	// The current ability implementations don't use "CreateLocalStore" because there already
	// is one by default in Ability Builder-based abilities. Instead they use "GetAbilityLocalStore(...)"
	// As such, "CreateLocalStore" is entirely a forward-thinking convenience with no current
	// purpose.
	// NOTE: then after writing the above, I started using "CreateLocalStore" for some hacks
	// in abilitiesUtils.j to try to match behaviors from json with stupid short term solution(s)
	native CreateLocalStore takes nothing returns localstore

	// Below are called "get" but they're the same idea as "load" natives on hashtables.
	native GetLocalStoreString takes localstore whichLocalStore, string childKey returns string
	// NOTE: there's some wonk in the json; it does a Warsmash thing and stores 'A000' and 97
	// as two different "kinds" of things. One is called War3ID and the other is called Integer.
	// At the moment "native GetLocalStoreInteger" has special handling, so if you try to look
	// up something of type War3ID on the jass side it will normalize this against integer,
	// and all will be the integer type, for consistency with jass. SetLocalStoreInteger
	// does not have the special handling at the time of writing, so this may impede your
	// ability to overwrite system values if they are required to be of type War3ID
	native GetLocalStoreInteger takes localstore whichLocalStore, string childKey returns integer
	native GetLocalStoreBoolean takes localstore whichLocalStore, string childKey returns boolean
	native GetLocalStoreReal takes localstore whichLocalStore, string childKey returns real             
	native GetLocalStoreCode takes localstore whichLocalStore, string childKey returns code
	native GetLocalStoreAbilityTypeLevelDataHandle takes localstore whichLocalStore, string childKey returns abilitytypeleveldata
	native GetLocalStoreAbilityHandle takes localstore whichLocalStore, string childKey returns ability
	native GetLocalStoreBuffHandle takes localstore whichLocalStore, string childKey returns buff
	native GetLocalStoreUnitHandle takes localstore whichLocalStore, string childKey returns unit
	native GetLocalStoreDestructableHandle takes localstore whichLocalStore, string childKey returns destructable
	native GetLocalStoreDestructableBuffHandle takes localstore whichLocalStore, string childKey returns destructablebuff
	native GetLocalStoreABTimeOfDayEventHandle takes localstore whichLocalStore, string childKey returns abtimeofdayevent
	native GetLocalStoreGameObjectHandle takes localstore whichLocalStore, string childKey returns gameobject
	native GetLocalStoreNonStackingStatBonusHandle takes localstore whichLocalStore, string childKey returns nonstackingstatbonus
	native GetLocalStoreProjectileHandle takes localstore whichLocalStore, string childKey returns projectile
	native GetLocalStoreLocationHandle takes localstore whichLocalStore, string childKey returns location
	native GetLocalStoreTimerHandle takes localstore whichLocalStore, string childKey returns timer
	native GetLocalStoreABTimerHandle takes localstore whichLocalStore, string childKey returns abtimer

	// below function used by some dumb stuff in abilitiesUtils.j; it was not originally part of JSON AbilityBuilder
	native GetLocalStoreLocalStoreHandle takes localstore whichLocalStore, string childKey returns localstore

	// setters: return true if there was some previous value stored at the child key
	native SetLocalStoreString takes localstore whichLocalStore, string childKey, string value returns boolean
	native SetLocalStoreInteger takes localstore whichLocalStore, string childKey, integer value returns boolean
	native SetLocalStoreBoolean takes localstore whichLocalStore, string childKey, boolean value returns boolean
	native SetLocalStoreReal takes localstore whichLocalStore, string childKey, real value returns boolean      
	native SetLocalStoreCode takes localstore whichLocalStore, string childKey, code func returns boolean
	native SetLocalStoreAbilityTypeLevelDataHandle takes localstore whichLocalStore, string childKey, abilitytypeleveldata value returns boolean
	native SetLocalStoreAbilityHandle takes localstore whichLocalStore, string childKey, ability value returns boolean
	native SetLocalStoreBuffHandle takes localstore whichLocalStore, string childKey, buff value returns boolean
	native SetLocalStoreUnitHandle takes localstore whichLocalStore, string childKey, unit value returns boolean
	native SetLocalStoreDestructableHandle takes localstore whichLocalStore, string childKey, destructable value returns boolean
	native SetLocalStoreDestructableBuffHandle takes localstore whichLocalStore, string childKey, destructablebuff value returns boolean
	native SetLocalStoreABTimeOfDayEventHandle takes localstore whichLocalStore, string childKey, abtimeofdayevent value returns boolean
	native SetLocalStoreGameObjectHandle takes localstore whichLocalStore, string childKey, gameobject value returns boolean
	native SetLocalStoreNonStackingStatBonusHandle takes localstore whichLocalStore, string childKey, nonstackingstatbonus value returns boolean
	native SetLocalStoreProjectileHandle takes localstore whichLocalStore, string childKey, projectile value returns boolean
	native SetLocalStoreTimerHandle takes localstore whichLocalStore, string childKey, timer value returns boolean
	native SetLocalStoreHandle takes localstore whichLocalStore, string childKey, handle value returns boolean

	native LocalStoreContainsKey takes localstore whichLocalStore, string childKey returns boolean

	native FlushParentLocalStore takes localstore whichLocalStore returns nothing
	native FlushChildLocalStore takes localstore whichLocalStore, string childKey returns boolean

	// this native is the same as calling FlushChildLocalStore on every key in the store whose
	// key name ends with `"#" + I2S(castId)`
	native LocalStoreCleanUpCastInstance takes localstore whichLocalStore, integer castId returns nothing

	// NOTE: only works on abilities defined by Ability Builder. At the moment, hard-coded engine abilities
	// dont have this
	native GetAbilityLocalStore takes ability whichAbility returns localstore

	// Returns the local store for the casted ability thing (or other active Ability Builder callback)
	native GetTriggerLocalStore takes nothing returns localstore

	// NOTE: java does not use destroy functions, this was kept for consistency with war3 stuff
	// but at the moment it is identical to FlushParentLocalStore to help you with performance
	native DestroyLocalStore takes localstore whichStore returns nothing

	//=================================================================================================
	// GameObject API
	//=================================================================================================

		// These are the state store that contains information loaded from SLK or INI "profile"

		// For general use I added a native "GetGameObjectById" that takes worldeditordatatype
		// and id, so we could for example say
		//   local gameobject paladinData = GetGameObjectById(WORLD_EDITOR_DATA_TYPE_UNITS, 'Hpal')
		//   local string name = GetGameObjectFieldAsString(paladinData, "Name", 0) // returns "Paladin"
		//   local string properName1 = GetGameObjectFieldAsString(paladinData, "Propernames", 0) // returns "Granis Darkhammer"
		//   local string properName2 = GetGameObjectFieldAsString(paladinData, "Propernames", 1) // returns "Jorn the Redeemer"
		//
		// ... however at the time of writing, GetGameObjectById is not getting used in existing code.
		// Intead of it, the AbilityBuilder uses GetLocalStoreGameObjectHandle in combination
		// with the hardcoded key "_abilityEditorData" to get the game object for the active ability,
		// which is populated inside the engine automatically rather than being set from JASS/json.

		native GetGameObjectFieldAsString takes gameobject editorData, string key, integer index returns string
		native GetGameObjectFieldAsInteger takes gameobject editorData, string key, integer index returns integer
		native GetGameObjectFieldAsReal takes gameobject editorData, string key, integer index returns real
		native GetGameObjectFieldAsBoolean takes gameobject editorData, string key, integer index returns boolean   
		native GetGameObjectFieldAsID takes gameobject editorData, string key, integer index returns integer
		native GetGameObjectFieldAsStringList takes gameobject editorData, string key returns stringlist
		// Below: a weird function that exists in warsmash, where if the thing was a list "a,b,c" we just
		// return it including commas even though the "AsString" version has that chopped up and indexed.
		// It should be functionally identical to the below brainstorm, although it is potentially
		// much more efficient because it is running an existing Warsmash utility in Java
		/*
		 * function GetGameObjectField takes gameobject editorData, string key returns string
		 *     local stringlist data = GetGameObjectFieldAsStringList(editorData, key)
		 *     local integer i = 0
		 *     local integer l = StringListSize(data)
		 * 	   local string result = ""
		 *     loop
		 *         exitwhen i >= l
		 *         if i > 0 then
		 *		       result = result + ","
		 *         endif
		 *	       result = result + StringListGet(data, i)
		 *		   set i = i + 1
		 *	   endloop
		 *
		 *     return result
		 * endfunction
		 */
		native GetGameObjectField takes gameobject editorData, string key returns string

		native GetGameObjectById takes worldeditordatatype whichDataType, integer aliasId returns gameobject


	//=================================================================================================
	// AbilityBuilderConfiguration API
	//=================================================================================================
	// Defines stuff for the "kind" of ability, such as in object editor

	// Create a configuration that can be assigned to an ability to describe what it
	// does if given to a unit.
	native CreateAbilityBuilderConfiguration takes nothing returns abilitybuilderconfiguration

	// Sets the Base Order ID
	native SetABConfCastId takes abilitybuilderconfiguration abc, string castId returns nothing

	// Sets the Base Order ID (turn off)
	native SetABConfUncastId takes abilitybuilderconfiguration abc, string castId returns nothing

	// Sets the Base Order ID for Auto Cast On
	native SetABConfAutoCastOnId takes abilitybuilderconfiguration abc, string castId returns nothing

	// Sets the Base Order ID for Auto Cast Off
	native SetABConfAutoCastOffId takes abilitybuilderconfiguration abc, string castId returns nothing

	// Sets the type of autocast available to the ability
	native SetABConfAutoCastType takes abilitybuilderconfiguration abc, autocasttype whichType returns nothing

	// Sets the type of ability to configure/create... See AB_CONF_TYPE_XYZ constants.
	native SetABConfType takes abilitybuilderconfiguration abc, abconftype whichType returns nothing

	// AbilityBuilderSpecialDisplayFields
	native SetABConfShowOnAndOffIcons takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfFoodCost takes abilitybuilderconfiguration abc, intexpr valueFunc returns nothing    
	native SetABConfGoldCost takes abilitybuilderconfiguration abc, intexpr valueFunc returns nothing
	native SetABConfLumberCost takes abilitybuilderconfiguration abc, intexpr valueFunc returns nothing 
	native SetABConfHideAreaCursor takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfInstantCast takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfCastlessNoTarget takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfToggleable takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfCastToggleOff takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfSeparateOnAndOff takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfAlternateUnitID takes abilitybuilderconfiguration abc, intexpr unitTypeIdFunc returns nothing

	// AbilityBuilderSpecialConfigFields
	native SetABConfBufferManaRequired takes abilitybuilderconfiguration abc, intexpr valueFunc returns nothing
	native SetABConfManaDrainedPerSecond takes abilitybuilderconfiguration abc, intexpr valueFunc returns nothing
	native SetABConfPointTargeted takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfTargetedSpell takes abilitybuilderconfiguration abc, boolexpr condition returns nothing
	native SetABConfAutoAcquireTarget takes abilitybuilderconfiguration abc, code actionFunc returns nothing
	native SetABConfPairAbilityId takes abilitybuilderconfiguration abc, intexpr abilityIdFunc returns nothing
	native SetABConfPairUnitId takes abilitybuilderconfiguration abc, intexpr unitIdFunc returns nothing

	// TODO for the "commandStringsErrorKey" generally I want this to be open-ended and disagree with current
	// AbilityBuilder design to have it locked down to an enum. So at the time of writing, if you pass a string
	// that isn't explicitly a constant value COMMAND_STRINGS_ERROR_KEY_<XYZ> then these natives would most
	// likely crash. Longterm, I'm happy to have them userspace strings that lookup other userspace values
	// from the file "Units\CommandStrings.txt" at which point they shouldn't be enums and shouldn't be
	// hardcoded in the engine as far as I can figure.
	native SetABConfPairUnitTypeError takes abilitybuilderconfiguration abc, string commandStringsErrorKey returns nothing
	native SetABConfCantTargetError takes abilitybuilderconfiguration abc, string commandStringsErrorKey returns nothing
	native SetABConfCantPairError takes abilitybuilderconfiguration abc, string commandStringsErrorKey returns nothing
	native SetABConfCantPairOffError takes abilitybuilderconfiguration abc, string commandStringsErrorKey returns nothing

	native AddABConfAddAbilityAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfAddDisabledAbilityAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfRemoveAbilityAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfRemoveDisabledAbilityAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfDeathPreCastAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfCancelPreCastAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfOrderIssuedAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfActivateAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfDeactivateAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfLevelChangeAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfBeginCastingAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfEndCastingAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfChannelTickAction takes abilitybuilderconfiguration abc, code func returns nothing
	native AddABConfEndChannelAction takes abilitybuilderconfiguration abc, code func returns nothing

	// Register this Ability Builder Configuration with the ability subsystem!! Now any unit
	// with an ability who inherits from this rawcode will have the configured code used for
	// that ability!
	native RegisterABConf takes integer codeId, abilitybuilderconfiguration abc returns nothing

	//=================================================================================================
	// AbilityBuilderAbility API
	//=================================================================================================
	// Stuff to deal with abilities. Takes an ability handle, but that handle needs to be
	// an ability defined with Ability Builder tools. If it's some other type of ability, 
	// these natives won't work.

	// Does the same thing as BlzEndUnitAbilityCooldown if user simulation includes newer patch
	native EndUnitAbilityCooldown takes unit whichUnit, integer whichAbilityId returns nothing

	// This is meant to be the same as EndUnitAbilityCooldown but you can use it on an ability handle
	// --- there's some kind of special handling so that it uses the Cooldown Group of items
	// --- (which seems busted and should be automatic either way? maybe? shouldn't Inventory ability handle that?)
	native EndAbilityCooldown takes unit caster, ability whichAbility returns nothing

	// Does the same thing as BlzStartUnitAbilityCooldown if user simulation includes newer patch
	native StartUnitAbilityCooldown takes unit whichUnit, integer whichAbilityId, real cooldown returns nothing

	// Below: uses the cooldown setting of the ability (should include level, etc)
	native StartUnitAbilityDefaultCooldown takes unit whichUnit, integer whichAbilityId returns nothing

	// This is meant to be the same as StartUnitAbilityCooldown but you can use it on an ability handle
	// --- there's some kind of special handling so that it uses the Cooldown Group of items
	// --- (which seems busted and should be automatic either way? maybe? shouldn't Inventory ability handle that?)
	//native StartAbilityCooldown takes unit caster, ability whichAbility, real cooldown returns nothing

	// Below: uses the cooldown setting of the ability (should include level, etc)
	native StartAbilityDefaultCooldown takes unit caster, ability whichAbility returns nothing
	
	// Does the same thing as BlzGetUnitAbilityCooldownRemaining if the user simulation includes newer patch
	native GetUnitAbilityCooldownRemaining takes unit whichUnit, integer whichAbilityId returns real
	native GetUnitAbilityCooldownLengthDisplay takes unit whichUnit, integer whichAbilityId returns real

	// NOTE: above notes are not quite correct... seems "End" cooldown functions are handling the item case
	// but only "start default" are handling item case... start (non-default) is passing thru rawcode
	// and would skip past item cooldown group, item ignore cooldown setting, etc

	// ===== Event Response =====

	// Returns a magic number for the spell cast... Basically if I cast store bolt on a peasant,
	// then cast storm bolt on a peon, the peasant's cast gets CastId 1 and the peon gets CastId 2
	// or something. It is stored on buffs in some cases.
	native GetTriggerCastId takes nothing returns integer

	// Gets the item that created the ability for the unit.
	// NOTE from Retera: is this a hack? shouldn't that probably be handled automatically?
	native GetAbilityItem takes ability whichAbility returns item

	//=================================================================================================
	// AbilityBuilderActiveAbility API
	//=================================================================================================
	// Similar to natives in AbilityBuilderAbility API section, these only work on abilities
	// defined with the Ability Builder tools. But as an additional requirement,
	// the ability is required to be an Active ability. Otherwise will cause crash or not work.

	// below: use on toggleable abilities; they return true if it wasn't already in that state and toggle was
	// a success.
	// (You have to provide the Caster because of dumb engine design,
	// which can't look it up for you. Thanks, Retera!)
	native AbilityActivate takes unit caster, ability whichAbility returns boolean
	native AbilityDeactivate takes unit caster, ability whichAbility returns boolean
	native IsToggleAbilityActive takes ability whichAbility returns boolean

	// NOTE: maybe eventually this could be replaced by BlzSetAbilityRealLevelField(whichAbility, ABILITY_RLF_CAST_RANGE, 0, value)
	// but at the moment it is not the same, because it sets the cast range independent of level
	// (the one that is actually used rather than an editor stat, which would change on skill level up)
	native SetAbilityCastRange takes ability whichAbility, real range returns nothing

	//=================================================================================================
	// Projectile API
	//=================================================================================================
	// Shoot projectiles using the built in engine to do so

	// the first 3 args are used as Event Response when triggering the "code" func handlers
	native CreateLocationTargetedCollisionProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile

	native CreateLocationTargetedProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, real speed, boolean homing, code onLaunch, code onHit returns projectile

	// I think this native creates the Impale effect, via a repeating delayed line of special effects spawned.
	// So I assume the repeating effects spawned have collision against units they hit.
	native CreateLocationTargetedPseudoProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile

	native CreateUnitTargetedCollisionProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile

	native CreateUnitTargetedProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit targetUnit, integer projectileId, real speed, boolean homing, code onLaunch, code onHit returns projectile

	// See "CreateLocationTargetedPseudoProjectile" but this one chases a unit (?)
	native CreateUnitTargetedPseudoProjectile takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile

	native SetAttackProjectileDamage takes projectile whichAttackProjectile, real damage returns nothing

	//=================================================================================================
	// Buff API
	//=================================================================================================
	// Like War3, buffs are abilities. GetUnitAbilityLevel(...) with some Buff ID should still work.
	// But this API is to provide the other needed functions.'
	// --- This also means stuff like GetAbilityCodeId and GetAbilityAliasId might work on Buffs (?)

	// TODO what does this native do?
	native AddUnitNonStackingDisplayBuff takes unit target, string stackingKey, buff whichBuff returns nothing

	// TODO what happens if you remove a buff with RemoveAbility natives?? seems broken...
	// For now, I guess if you add it with "add non stacking display buff" then you have to
	// remove it with this function, or else memory leaks stacking key stuff and you break the system
	native RemoveUnitNonStackingDisplayBuff takes unit target, string stackingKey, buff whichBuff returns nothing

	// NOTE: full function of the json native moved to CreatePassiveBuffAU
	// NOTE: sourceAbilLocalStore will be used as GetTriggerLocalStore() in the on add/remove actions
	// NOTE: castId will be used as GetTriggerCastId() in the on add/remove actions
	native CreatePassiveBuff takes integer buffId, boolean showIcon, code onAddAction, code onRemoveAction, effecttype artType, boolean showFx, boolean playSfx, localstore sourceAbilLocalStore, integer castId returns buff

	// see CreateTargetingBuffAU
	native CreateTargetingBuff takes integer buffId returns buff

	// see CreateTimedArtBuffAU
	native CreateTimedArtBuff takes integer buffId, real duration, boolean showIcon, effecttype artType returns buff

	// see CreateTimedBuffAU
	native CreateTimedBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff

	// see CreateTimedLifeBuffAU
	native CreateTimedLifeBuff takes integer buffId, real duration, boolean explode returns buff

	// see CreateTimedTargetingBuffAU
	native CreateTimedTargetingBuff takes integer buffId, real duration returns buff

	// see CreateTimedTickingBuffAU
	native CreateTimedTickingBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff

	// see CreateTimedTickingPausedBuffAU
	// - from what I could tell, a "paused" buff is one who is capable of being affected by unit pausing, not one who operates while paused
	// - the concept of "paused" for a unit on Warsmash was edited with Ability Builder changes. Rather than a shut down of unit tick,
	//   including the ticking of the unit's abilities, it only shuts down some stuff if its registered as pausable stuff
	//   (attempt to mimic nonsense behaviors on Warcraft; perhaps a cleaned version of this code would be a way to register
	//    unit specific timers that shut down if unit paused, and game timers that dont shut down if unit paused, but this
	//    would require a sensible programming language where the game-registered timers knew what unit to operate on via
	//    some local scoping mechanics)
	native CreateTimedTickingPausedBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff

	// see CreateTimedTickingPostDeathBuffAU
	native CreateTimedTickingPostDeathBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff

	// see CreateStunBuffAU
	native CreateStunBuff takes integer buffId, real duration returns buff

	//=================================================================================================
	// DestructableBuff API
	//=================================================================================================
	// NOTE from Retera: idk the details on this. Just trying to expose the system
	// to match Glasir json AbilityBuilder so its available in the Jass for now.

	native AddDestructableBuff takes destructable target, destructablebuff whichBuff returns nothing

	native RemoveDestructableBuff takes destructable target, destructablebuff whichBuff returns nothing

	native CreateDestructableBuff takes unit casterUnit, integer buffId, integer level, localstore sourceAbilLocalStore, code onAddAction, code onRemoveAction, code onDeathAction, integer castId returns destructablebuff


	//=================================================================================================
	// Event API
	//=================================================================================================
	// Trigger events implemented for ability builder... Where possible, it's probably better to just
	// literally use events from common.j instead. These are here for the purpose of 1:1 port from
	// json, which used its own natives that include additional scoping, such as the passing of
	// the localStore into the event actions by default.

	native CreateABTimeOfDayEvent takes code actions, real startTime, real endTime, string equalityId, unit caster, localstore localStore, integer castId returns abtimeofdayevent

	// links the event to game engine to fire... by default it is not hooked up
	native RegisterABTimeOfDayEvent takes abtimeofdayevent whichEvt returns nothing

	// links the event to the game engine to fire... but only if you didn't already link it up
	native RegisterUniqueABTimeOfDayEvent takes abtimeofdayevent whichEvt returns nothing

	// makes event stop firing (opposite of register)
	native UnregisterABTimeOfDayEvent takes abtimeofdayevent whichEvt returns nothing

	//=================================================================================================
	// Floating Text API
	//=================================================================================================

	// Warsmash was originally centering these. tried to turn it off when I was attempting
	// to match common.j stuff, but here you could turn it back on if you want
	native SetTextTagCentered takes texttag t, boolean flag returns nothing

	// Unlike CreateTextTag, this CreateTextTagFromConfig function will default all the stats of the text
	// tag based on a texttagconfigtype. Also in the future it might handle fog of war on text tag
	// for the source unit (pretty sure common.j text tags don't allow that feature)
	// At the moment these are expected to become desync prone if used in net code, so GetHandleId(t)
	// on a text tag "t" created with this native will always return -1, these do not add to the count,
	// and are intended to possibly exist in local client code. But "SetTextTag<Thing>" from common.j should
	// work on them.
	native CreateTextTagFromConfig takes unit sourceUnit, texttagconfigtype whichConfigType, string whatString returns texttag

	// below: this is probably dumb and we could probably use "CreateTextTagFromConfig" instead.
	// Retera's fault. Forget why this is here. It literally adds + or - prefix to number in text
	native CreateIntTextTagFromConfig takes unit sourceUnit, texttagconfigtype whichConfigType, integer whatNumber returns texttag

	//=================================================================================================
	// Game State
	//=================================================================================================

	// this is the Moonstone ability effect. It makes the clock purple or yellow
	native SetFalseTimeOfDay takes integer hour, integer minute, real duration returns nothing

	// Returns an ever-increasing number to indicate the "turn" that the game is on.
	// There are roughly 20 turns per second, unless someone changes the emulator constants.
	native GetGameTurnTick takes nothing returns integer

	// Returns the number of seconds between each game turn tick (probably 0.05)
	constant native GetSimulationStepTime takes nothing returns real
	globals
		// Moved this in from "abilities utils" file. Maybe it can lose the "au" suffix if
		// we fix both files later.
		constant real au_SIMULATION_STEP_TIME                            = GetSimulationStepTime()
	endglobals

	//=================================================================================================
	// Item API
	//=================================================================================================

	// returns the Rune of Rebirth unit ID, which there is a common.j native to set but not to get!
	native GetItemDropID takes item whichItem returns integer

	// returns true if the item type is perishable. NOTE: why do we need this(?)
	native IsItemIdPerishable takes integer itemId returns boolean

	//=================================================================================================
	// Unit API
	//=================================================================================================

	// might fire some code, might return false if you're blocked by spell shield, etc
	native CheckUnitForAbilityEffectReaction takes unit target, unit caster, ability whichAbility returns boolean

	// similar to the above, but allows for Defend to actually reflect the projectile or something
	native CheckUnitForAbilityProjReaction takes unit target, unit caster, projectile whichProjectile returns boolean

	// A targeted effect can't hit an invisible unit who would otherwise be a valid target, but an AoE can
	native IsUnitValidTarget takes unit target, unit caster, abilitytypeleveldata abilData, integer level, boolean targetedEffect returns boolean
	// Below, "GetUnitTargetError" returns a COMMAND_STRING_ERROR_KEY, or null if the target is targetable
	// by the caster when using the given target types. Also better than "IsUnitValidTarget"
	// because it takes arbitrary targettypes filter object -- so it does not require
	// ability type level data.
	native GetUnitTargetError takes unit target, unit caster, targettypes whichTargetTypes, boolean targetedEffect returns string
	// the idea that all non-unit widgets are visible is trivially disproven by the counter example of fog of war,
	// so I'm guessing we will end up wanting to replace both of these with one "is valid target" native that takes into account
	// whether it's a targeted effect for both units and nonunits
	native IsValidTarget takes widget target, unit caster, abilitytypeleveldata abilData, integer level returns boolean
	// Below, "GetTargetError" is the widget version of "GetUnitTargetError"
	native GetTargetError takes widget target, unit caster, targettypes whichTargetTypes returns string

	// UnitCanReach returns true if the unit can reach the target... it does some special
	//  calculations, including checking "within range" of any point on the pathing
	//  map of a target building or destructable, or checking collision radius
	//  of both units when applicable
	native UnitCanReach takes unit source, abilitytarget target, real radius returns boolean

	// NOTE: Percents dont work in the ability builder json "AddDefenseBonus"
	// according to a comment, so they might not be working
	// in the jass binding either. Maybe just use "BlzGetUnitArmor" for the base?
	// (NOTE: in addition to the above comment, "BlzGetUnitArmor" has been provided in Warsmash so it should function)
	// NOTE: This adds or subtracts from the GREEN number (+1.5) not the base.
	//native UnitAddDefenseBonus takes unit targetUnit, real defenseValue, boolean percentage returns nothing
	native UnitAddDefenseBonus takes unit targetUnit, real defenseValue returns nothing

	// green numbers
	native UnitSetTemporaryDefenseBonus takes unit targetUnit, real defenseValue returns nothing
	native UnitGetTemporaryDefenseBonus takes unit targetUnit returns real

	// As with any Blz native, using them would cause Warsmash to stop working on patches < 1.32, so here is a
	// non-blz binding to get the defense of a unit. "BlzGetUnitArmor" reroutes to this.
	native GetUnitDefense takes unit whichUnit returns real

	// At the moment, "HealUnit" is identical to setting UNIT_STATE_LIFE to a higher value.
	// However, in the foreseeable future, if we create "Heal Events" or whatever, then this
	// would fire them. Or, if we add the Orb of Fire effect from Reforged, which reduces
	// healing by 50% or whatever, this function would be affected, hypothetically. 
	native HealUnit takes unit whichUnit, real amount returns nothing

	// NOTE: below, YOU WOULD THINK this would be the same as `IssueImmediateOrder(whichUnit, "stop")`
	// however the function getting called by the JSON isn't, and was updating what the unit was doing
	// without firing the issued order system so it probably wouldn't fire order triggers, etc, and
	// so I added this native to make JASS match the JSON hypothetically. We should delete this and
	// use `IssueImmediateOrder(whichUnit, "stop")` in the future.
	native DoStopOrder takes unit whichUnit returns nothing

	// Causes a worker who is currently carrying gold or lumber to have their
	// carried resources instantly be gained to the player.
	native UnitInstantReturnResources takes unit whichWorker returns nothing

	// When we call RemoveUnit on something that the player had selected, sometimes we want to automatically
	// select something else. Instead of manipulating their selection directly with a bunch of busywork,
	// this tags a unit to replace a different one for selection... if the first was selected. If it's not,
	// nothing happens.
	native SetPreferredSelectionReplacement takes unit whichUnitIsGoingToBeRemoved, unit whichUnitWeWantToSelect returns nothing

	// The function of the resurrect ability
	native ResurrectUnit takes unit whichUnit returns nothing

	// returns true if unit is not dead
	native UnitAlive takes unit whichUnit returns boolean
	native WidgetAlive takes widget whichUnit returns boolean

	// If possible, this function checks if a unit is a worker and if so sends it back to work.
	// Currently this is called in the natively hacked in Stand Down ability, maybe we can move it to jass later
	// Also used in the JSON for Call to Arms.
	// It's OK to pass null as the input resource type. The function returns the type that
	// was actually selected -- so if you declare the "default" type to be lumber, but the worker was
	// carrying gold at the moment, the function might return RESOURCE_TYPE_GOLD to indicate that we
	// sent them back to a gold mine
	native SendUnitBackToWork takes unit whichUnit, resourcetype defaultResourceType returns resourcetype

	// SetUnitPathing lets units walk through stuff if I recall.. totally turns it off.
	// This natively, unlike that, enables the special movement type of windwalk and harvesting workers,
	// wherein the unit is a land unit but it can pass through other units. Use with "active = false"
	// to go back to default unit movement type.
	native SetUnitMovementTypeNoCollision takes unit whichUnit, boolean active returns nothing

	// This field only functions if we cancel construction or if we SetUnitExploded(...)
	// What it does is to make the unit use this buff as the one whose art to show when the
	// unit explodes, instead of the unit's special art blood splat.
	native SetUnitExplodeOnDeathBuffId takes unit whichUnit, integer buffId returns nothing
	// similar to above, this function unsets the buff ID -- not whether the unit explodes
	native UnsetUnitExplodeOnDeathBuffId takes unit whichUnit returns nothing

	native StartSacrificingUnit takes unit factory, unit toSacrifice, integer resultUnitId returns nothing

	// This is the low level function that for the moment is totally separate from whether that unit
	// is even allowed to be trained by that building... It also doesn't generate "issued order" events,
	// because it is not. It is here for feature parity with whatever Ability Builder was doing.
	// Where possible, do something like "IssueTrainOrderByIdBJ" instead.
	native StartTrainingUnit takes unit factory, integer trainedUnitId returns nothing

	native UnitLoopSpellSoundEffect takes unit onUnit, integer abilityAliasId returns nothing
	native UnitSpellSoundEffect takes unit onUnit, integer abilityAliasId returns nothing
	native UnitStopSpellSoundEffect takes unit onUnit, integer abilityAliasId returns nothing

	native IsUnitMovementDisabled takes unit whichUnit returns boolean

	native SetUnitAnimationByTag takes unit whichUnit, boolean force, primarytag animationName, secondarytags secondaryAnimationTags, real speedRatio, boolean allowRarityVariations returns nothing
	native SetUnitAnimationByTagWithDuration takes unit whichUnit, boolean force, primarytag animationName, secondarytags secondaryAnimationTags, real duration, boolean allowRarityVariations returns nothing
	// the walk animation does smart logic about whether to use "Walk" or "Walk Fast" or whatever
	native SetUnitAnimationToWalk takes unit whichUnit, boolean force, real currentMovementSpeed, boolean allowRarityVariations returns nothing
	native SetUnitAnimationByIndexEx takes unit whichUnit, boolean force, integer sequenceIndex, real speedRatio, boolean allowRarityVariations returns nothing
	native QueueUnitAnimationByTag takes unit whichUnit, primarytag animationName, secondarytags secondaryAnimationTags, boolean allowRarityVariations returns nothing
	// NOTE: the below functions are the same as AddUnitAnimationProperties from common.j, but use secondarytag for convenience,
	// and different because AddUnitAnimationProperties always calls to "ForceResetUnitCurrentAnimation" after adding/removing
	native AddUnitAnimationSecondaryTag takes unit whichUnit, secondarytag whichTag returns boolean
	native RemoveUnitAnimationSecondaryTag takes unit whichUnit, secondarytag whichTag returns boolean
	native ForceResetUnitCurrentAnimation takes unit whichUnit returns nothing

	// return BlzGetUnitRealField(whichUnit, UNIT_RF_CAST_POINT) // would be the same but we dont have that working yet
	native GetUnitCastPoint takes unit whichUnit returns real
	// return BlzGetUnitRealField(whichUnit, UNIT_RF_CAST_BACK_SWING) // would be the same but we dont have that working yet
	native GetUnitCastBackswingPoint takes unit whichUnit returns real

	// NOTE: a native unit.chargeMana exists, so we could bind that also, but it's the same as this fxn:
	function ChargeMana takes unit caster, integer manaCost returns boolean
		real mana = GetUnitState(caster, UNIT_STATE_MANA)
		if (mana >= manaCost) then
			SetUnitState(caster, UNIT_STATE_MANA, mana - manaCost)
			return true
		endif
		return false
	endfunction

	// This could have been written as a function instead of a native, but then that way it would
	// have to be maintained. This one is just reading the java variables, so if somebody
	// adds a damage type then this native updates likewise basically. Not sure why it
	// needs to exist, though; where possible, replace with references to DAMAGE_TYPE_FORCE
	// and friends (native ConvertDamageType)
	native String2DamageType takes string x returns damagetype

	// UnitGroup API extensions:

	// - enum units in range of unit... considers collision sizes of both units, so it's not quite the same
	//   as if we enumerated units in range of the location of the center unit
	native GroupEnumUnitsInRangeOfUnit takes group whichGroup, unit whichUnit, real radius, boolexpr filter returns nothing
	native GroupEnumUnitsInRangeOfUnitCounted takes group whichGroup, unit whichUnit, real radius, boolexpr filter, integer countLimit returns nothing

	// same function as "blz" versions, but the ones declared here are available regardless
	// of which game version being emulated:
	native GroupAddGroupFast takes group whichGroup, group addGroup returns integer
	native GroupRemoveGroupFast takes group whichGroup, group removeGroup returns integer
	native GroupGetSize takes group whichGroup returns integer
	native GroupUnitAt takes group whichGroup, integer index returns unit

	//=================================================================================================
	// Non Stacking Stat Buff API
	//=================================================================================================
	// These are not buffs. They appear to be stat modifiers, but scoped on a name.
	// So I assume that if you `CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_DEF, "My Devotion Aura Thing", 10)`
	// and also `CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_DEF, "My Devotion Aura Thing", 12)` and
	// add them together on a unit, since they are non stacking I'm guessing this means the unit would gain a total of 12
	// and not 22 defense, because they were both applied by "My Devotion Aura Thing"??
	native CreateNonStackingStatBonus takes nonstackingstatbonustype whichType, string stackingKey, real value returns nonstackingstatbonus

	native AddUnitNonStackingStatBonus takes unit targetUnit, nonstackingstatbonus whichBuff returns nothing

	native RemoveUnitNonStackingStatBonus takes unit targetUnit, nonstackingstatbonus whichBuff returns nothing

	native RecomputeStatBonusesOnUnit takes unit targetUnit, nonstackingstatbonustype whichBuffType returns nothing

	// NOTE: seems like, if you call `UpdateNonStackingStatBonus`, you probably also have to call `RecomputeStatBuffsOnUnit`,
	// otherwise you update some invisible thing without applying it to the unit.
	native UpdateNonStackingStatBonus takes nonstackingstatbonus whichBuff, real value returns nothing
	native GetNonStackingStatBonusType takes nonstackingstatbonus whichBonus returns nonstackingstatbonustype

	// See "String2DamageType" for notes on how "String2Thing" native is probably not good, and you
	// should probably use NON_STACKING_STAT_BONUS_TYPE_MVSPDPCT values
	native String2NonStackingStatBonusType takes string x returns nonstackingstatbonustype

	//=================================================================================================
	// State Mod Buff API
	//=================================================================================================
	// addable/removable edit to unit state. After you add or remove it, call the recompute function
	native CreateStateMod takes statemodtype whichType, integer value returns statemod

	native AddUnitStateMod takes unit targetUnit, statemod whichBuff returns nothing

	native RemoveUnitStateMod takes unit targetUnit, statemod whichBuff returns nothing

	native RecomputeStateModsOnUnit takes unit targetUnit, statemodtype whichBuffType returns nothing

	// NOTE: seems like, if you call `UpdateStateMod`, you probably also have to call `RecomputeStateModsOnUnit`,
	// otherwise you update some invisible thing without applying it to the unit.
	native UpdateStateMod takes statemod whichBuff, integer value returns nothing

	// See "String2DamageType" for notes on how "String2Thing" native is probably not good, and you
	// should probably use STATE_MOD_TYPE_INVULNERABLE values
	native String2StateModType takes string x returns statemodtype

	//=================================================================================================
	// Code API
	//=================================================================================================

	// redeclaration of native from common.ai, so that we can run a code func such as for ability
	native StartThread takes code func returns nothing

	// runs the code func, but where:
	// -  GetTriggerUnit()/GetSpellAbilityUnit() return the passed in unit
	// -  GetTriggerLocalStore() returns the passed in local store
	// -  GetTriggerCastId() returns the passed in cast id
	native StartAbilityBuilderThread takes code func, unit spellAbilityUnit, localstore triggerLocalStore, integer triggerCastId returns nothing


	//=================================================================================================
	// AbilityTypeLevelData API
	//=================================================================================================
	// This is exposed to the AbilityBuilder for some reason. At a glance, it looks like whatever
	// ability might be using these is probably broken, not MUI, and in need of fix. Worth testing.
	// For now the API is provided in order to achieve 1:1 parity with JSON AbilityBuilder code.
	native AbilityTypeLevelDataAddTargetAllowed takes abilitytypeleveldata whichData, integer level, targettype whichType returns nothing
	native AbilityTypeLevelDataRemoveTargetAllowed takes abilitytypeleveldata whichData, integer level, targettype whichType returns nothing

	// okay, maybe these function(s) below make a little more sense and are less broken than non MUI editing
	// of targets allowed (still probably better to use GameObject API though):
	native GetAbilityTypeLevelDataReal takes abilitytypeleveldata whichData, integer level, datafieldletter whichLetter returns real
	// still these functions are terrible, reinvent the parsing of SLK, exposing bad designs, shouldn't have been like this
	// ( Retera SLK stores everything as string, but all access to the data is meant to be through an API that converts to
	//   number or boolean with one methodology of conversion, not multiply copies, thus avoiding bugs. But this code copies
	//   out the string then re-parses to numbers on its own. The JSON version of this had signs of bugs, including
	//   a check where any "-" in a floating point number makes it 0, so "-2.00" will parse as 0.00 with these functions
	//   where as the one-for-all copy of the code in the parser used by the GameObject classes "GetAsReal" / "GetAsFloat"
	//   functions would not match this behavior... I patched the presumed bug in the jass copy, but now there are three copies
	//   !! and it should not be so, and the culprit is Ability Builder Type Level Data )
	native GetAbilityTypeLevelDataInteger takes abilitytypeleveldata whichData, integer level, datafieldletter whichLetter returns integer
	native GetAbilityTypeLevelDataID takes abilitytypeleveldata whichData, integer level, datafieldletter whichLetter returns integer
	native GetAbilityTypeLevelDataBoolean takes abilitytypeleveldata whichData, integer level, datafieldletter whichLetter returns boolean
	native GetAbilityTypeLevelDataString takes abilitytypeleveldata whichData, integer level, datafieldletter whichLetter returns string
	native GetAbilityTypeLevelUnitID takes abilitytypeleveldata whichData, integer level returns integer

	// NOTE: Regarding Warsmash development history, originally "type level data" here was created
	// as a high performance cache of data parsed by the GameObject api, so my note about how it is
	// "better to use GameObject API" would have been false because it would have been worse
	// performance. However, those types of micro optimizations become pointless when everything
	// is defined in user space. Even in the json ability builder, the equivalent to
	// "native GetAbilityTypeLevelDataReal" is doing float parsing of a string every time
	// that we query the value off of it. The idea that program variables are holding
	// the ability parameters for each level in a symbol table ("class") instead of
	// as strings in a property map is already lost. So, just do whatever you want.
	// It doesn't matter anymore, and "type level data" was too confusing to teach to
	// anyone anyway, and too time consuming to implement abilities for. It's a similar
	// reason to why the outside sees "ABILITY_RLF_CASTING_TIME = ConvertAbilityRealLevelField('acas')"
	// as this overcomplex nonsense on Reforged, whereas a World Editor user might advocate he should
	// have a native "SetAbilityDataReal" so that he can just call it on "Cast1" or "Cast2", the
	// casting time fields from world editor. He is completely estranged from how performance
	// is achieved in professional software, and has no access to the necessary information
	// nor any way to apply the information on Warcraft 3 if he had it, so it is most
	// likely the case that to even try is a fool's errand and we may as well simply
	// do our best writing whatever code is fun to write with what limited time
	// is given to us in life -- thus the "AbilitySpellBase" classes in Java, and my
	// video making Cluster Rockets in only an hour on YouTube, when I realized that
	// no one cares, it's all a waste of time to have done that, and it's fun to just
	// query GameObject once when the ability is created or levels up, even though
	// calling to a giant property map like that is worse performance. It just
	// doesn't matter. And likewise does ability builder call to something that
	// re-parses strings to floats just to get a floating point data field at runtime

	// returns 'B000' or whatever from `Stats - Buffs` in object editor; if multiple comma separated values,
	// it returns the first one.
	native GetAbilityTypeLevelDataFirstBuffId takes abilitytypeleveldata whichData, integer level returns integer

	native GetAbilityTypeLevelDataDurationNormal takes abilitytypeleveldata whichData, integer level returns real
	native GetAbilityTypeLevelDataDurationHero takes abilitytypeleveldata whichData, integer level returns real
	native GetAbilityTypeLevelDataCastTime takes abilitytypeleveldata whichData, integer level returns real
																						 
	//=================================================================================================
	// ABTimer API
	//=================================================================================================

	// Unlike CreateTimer, when this thing fires GetTriggerUnit, GetTriggerLocalStore, and GetTriggerCastId
	// will populate with these values... atm GetExpiredTimer not populated, it's not a jass timer,
	// but you can get the timer via the local store. The FIRINGTIMER key in utils is populated by the
	// engine itself (Yes, that's super redundant and pointless now that Ability Builder is ported to jass)
	native CreateABTimer takes unit caster, localstore localStore, integer castId, code actionsFunc returns abtimer

	native ABTimerSetRepeats takes abtimer whichTimer, boolean flag returns nothing
	native ABTimerSetTimeoutTime takes abtimer whichTimer, real timeout returns nothing
	native ABTimerStart takes abtimer whichTimer returns nothing
	native ABTimerStartRepeatingTimerWithDelay takes abtimer whichTimer, real delay returns nothing

	//=================================================================================================
	// Extra
	//=================================================================================================
	// these were already in the underlying system, exposed now because why not

	// returns the name of the Warsmash Java code class in use by the given ability... probably only 
	// useful for some hackery
	native WarsmashGetAbilityClassName takes ability whichAbility returns string

	// WarsmashGetRawcode2String('AHtb') == "AHtb"
	native WarsmashGetRawcode2String takes integer rawcode returns string

	// below have better performance than WarsmashGetRawcode2String and doesn't check if its length==4,
	// so don't use it with unchecked input. But it runs faster. Otherwise it's the same
	native Rawcode2String takes integer rawcode returns string
	// The opposite of the above. FourCC("AHtb") == 'AHtb'
	native FourCC takes string x returns integer

	native RemoveTriggerEvent takes trigger whichTrigger, event whichEvent returns nothing

	native LoadAgentHandle takes hashtable x, integer parentKey, integer childKey returns agent

/*
 *	// below is the syntax of type cast, but it is not yet as simple as a native
 *
 *	native TypeCast takes handle x, structtype whichType returns whichType
 */
endlibrary

//=====================================================
// Ability Target API                             
//=====================================================
// So we don't yet have a language syntax to cast
// widget back to unit or item. Because we do not, and
// if we continue not to have this, you could imagine
// one solution to the problem might be to have
// a "unit targetUnit" and "item targetItem" and
// such of each type, and then to do some stupid
// "if targetUnit != null" then later
// "if targetItem != null" and so on.
// But that is wasteful, stores 4 variables, and
// takes 4 steps every time. Instead, here we
// make an API that lets us store 1 struct,
// and do 1 double dispatch to callback
// on its kind
library AbilityTargetAPI
	// NOTE: Redeclare "agent" in case we are on patch 1.22 (should have no effect on 1.24+)
	type agent extends handle
	type abilitytarget extends agent
	type abilitytargetvisitor extends handle
	// NOTE: Redeclare "widget" and "location" -- requires Warsmash to allow us to redeclare
	type location extends abilitytarget
	type widget extends abilitytarget

	native GetAbilityTargetX takes abilitytarget what returns real
	native GetAbilityTargetY takes abilitytarget what returns real

	native CreateAbilityTargetVisitor takes nothing returns abilitytargetvisitor
	native DestroyAbilityTargetVisitor takes abilitytargetvisitor x returns nothing
	// Calling to "AbilityTargetAcceptVisitor" will call back on one of the visit methods of the 
	// visitor based on the type, allowing you to essentially cast it back
	native AbilityTargetAcceptVisitor takes abilitytarget whichTarget, abilitytargetvisitor whichVisitor returns nothing

	interface AbilityTargetVisitor extends abilitytargetvisitor
		method visitUnit takes unit target returns nothing
		method visitItem takes item target returns nothing
		method visitDest takes destructable target returns nothing
		method visitLoc takes location target returns nothing
	endinterface

// BELOW - a re-make of AbilityTarget entirely in userspace. Maybe we could keep it,
// but for now we are using a NATIVE equivalent to interoperate with what
// already exists on Warsmash
/**
	interface AbilityTargetVisitor
		method visitUnit takes unit target returns nothing
		method visitItem takes item target returns nothing
		method visitDest takes destructable target returns nothing
		method visitLoc takes location target returns nothing
	endinterface

	interface AbilityTarget
		method accept takes AbilityTargetVisitor visitor returns nothing
	endinterface

	struct AbilityTargetUnit extends AbilityTarget
		unit value
		public static method create takes unit value return thistype
			local thistype this = .allocate()
			set this.value = value
			return this
		endmethod
		method accept takes AbilityTargetVisitor visitor returns nothing
			call visitor.visitUnit(value)
		endmethod
	endstruct

	struct AbilityTargetItem extends AbilityTarget
		item value
		public static method create takes item value return thistype
			local thistype this = .allocate()
			set this.value = value
			return this
		endmethod
		method accept takes AbilityTargetVisitor visitor returns nothing
			call visitor.visitItem(value)
		endmethod
	endstruct

	struct AbilityTargetDest extends AbilityTarget
		destructable value
		public static method create takes destructable value return thistype
			local thistype this = .allocate()
			set this.value = value
			return this
		endmethod
		method accept takes AbilityTargetVisitor visitor returns nothing
			call visitor.visitDest(value)
		endmethod
	endstruct

	struct AbilityTargetLoc extends AbilityTarget
		location value
		public static method create takes location value return thistype
			local thistype this = .allocate()
			set this.value = value
			return this
		endmethod
		method accept takes AbilityTargetVisitor visitor returns nothing
			call visitor.visitLoc(value)
		endmethod
	endstruct
*/
endlibrary

//=====================================================
// Behavior API                                        
//=====================================================
/* Each unit is always performing 1 behavior at a time; about 20 times per
 * second, every time that the game logic updates, the engine will call
 * "update" on the unit's behavior, and that should implement the action
 * of whatever the unit is currently doing. The return value of the behavior's
 * update method decides what behavior to change to on the next step of game
 * logic after this one.
 * So i.e. if the behavior wishes to terminate, it should return
 * "unit.pollNextOrderBehavior()". If the behavior wishes to continue, it should
 * return itself. If it wishes to jump the unit to performing a different action
 * then it should return that other behavior.
 */
library BehaviorAPI requires AbilityTargetAPI
	type behavior extends handle
	type rangedbehavior extends behavior
	type behaviorcategory extends handle
	
	constant native ConvertBehaviorCategory takes integer x returns behaviorcategory
	
	globals
		constant behaviorcategory BEHAVIOR_CATEGORY_IDLE                              = ConvertBehaviorCategory(0)
		constant behaviorcategory BEHAVIOR_CATEGORY_MOVEMENT                          = ConvertBehaviorCategory(1)
		constant behaviorcategory BEHAVIOR_CATEGORY_ATTACK                            = ConvertBehaviorCategory(2)
		constant behaviorcategory BEHAVIOR_CATEGORY_SPELL                             = ConvertBehaviorCategory(3)
	endglobals
	
	native GetUnitMoveFollowBehavior takes unit whichUnit, integer highlightOrderId, widget whichFollowTarget returns behavior
	native GetUnitMovePointBehavior takes unit whichUnit, integer highlightOrderId, real targetX, real targetY returns behavior
	native GetUnitMovePointBehaviorLoc takes unit whichUnit, integer highlightOrderId, location whichLocation returns behavior
	native GetUnitAttackMovePointBehavior takes unit whichUnit, real targetX, real targetY returns behavior
	native GetUnitAttackMovePointBehaviorLoc takes unit whichUnit, location whichLocation returns behavior
	native GetUnitAttackWidgetBehavior takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, widget whichAttackTarget returns behavior
	native GetUnitAttackGroundBehavior takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, real attackGroundX, real attackGroundY returns behavior
	native GetUnitAttackGroundBehaviorLoc takes unit whichUnit, integer highlightOrderId, integer whichUnitAttackIndex, location attackGroundLoc returns behavior
	// The code func passed below must be "takes nothing returns behavior", and
	// the behavior it returns is what the unit will do next after each frame of the behavior we are defining
	// so it should return itself until it is finished. "How does it return itself??" you ask? Just use the
	// GetBehavingBehavior() native within its callback.
	// --- DEPRECATED (do not use "CreateAbilityBehavior" in favor of "extends behavior")
	native CreateAbilityBehavior takes nothing returns behavior
	native DestroyAbilityBehavior takes behavior x returns nothing
	// GetBehavingUnit returns the unit who owns the behavior if you use it within the handler "code func"
	// of a behavior that you define
	// --- DEPRECATED (do not use "GetBehavingUnit"; prefer a unit member in the behavior struct you create)
	//native GetBehavingUnit takes nothing returns unit
	// --- DEPRECATED (do not use "GetBehavingBehavior"; prefer keyword "this" in the behavior struct you create)
	//native GetBehavingBehavior takes nothing returns behavior
	
	// this function will read from the unit's list of shift click orders they are given,
	// poll the next item from that queue (modifying it) and return the top next
	// item to perform. So, in your custom behavior you should
	// return this when you are certain the unit has completed the ability.
	native UnitPollNextOrderBehavior takes unit whichUnit returns behavior
	
	interface Behavior extends behavior
		method update takes nothing returns behavior
		
		/* extra utility added later to detect when a unit starts a behavior.
		 * For a while the Glasir/AbilityBuilder stuff tried to use this to detect
		 * when a unit starts doing something, like attacking to break invisibility,
		 * but that's broken and will likely not work. begin() is called when the unit
		 * starts walking over to another unit to attack, or when the unit
		 * starts walking over to cast a spell, and not when the cast begins. It
		 * is simply a notification of the low level machinery moving forward,
		 * and not a Warcraft III trigger event.
		 */
		method begin takes nothing returns nothing defaults nothing
		
		/* see comment on begin. (interruptable ness was added by Glasir)
		 */
		method end takes boolean interrupted returns nothing defaults nothing
		
		/*
		 * Tells the command card which ability icon to highlight green.
		 * NOTE: This method has to be desync-safe, meaning that it should
		 * be safe to call from within "getLocalPlayer()" blocks. It is
		 * tagged with the "constant method" syntax, which currently does
		 * nothing, to remind you of this. For best results, it should
		 * probably be a small method that returns a single value.
		 * ( The UI is going to call this on you on one computer but not the other!! )
		 * NOTE: Actually, to make it impossible for the jass programmer to do this
		 * wrong, for now the underlying mapping between behavior in the engine
		 * and behavior jass interface differs slightly... After calling "begin()",
		 * the engine will call "getHighlightOrderId()" once to check it, and then subsequent 
		 * accessor methods (such as from the UI) will look at the engine's snapshot
		 * of the value, so that it's impossible for a UI system to launch jass
		 * code and trigger a desync
		 */
		constant method getHighlightOrderId takes nothing returns integer
		
		/*
		 * Returns true if we are allow to interrupt this behavior.
		 * NOTE: This method has to be desync-safe, meaning that it should
		 * be safe to call from within "getLocalPlayer()" blocks. It is
		 * tagged with the "constant method" syntax, which currently does
		 * nothing, to remind you of this. For best results, it should
		 * probably be a small method that returns a single value.
		 * ( It's less likely to be called from UI than getHighlightOrderId(), but might as
		 *   well be treated similarly )
		 */
		constant method interruptable takes nothing returns boolean
		
		/* added by Glasir. used in some json abilities to try to look at what
		 * a unit was doing. the categories added were IDLE, MOVEMENT, ATTACK, and
		 * SPELL but I have not reviewed this further in depth.
		 *
		 * NOTE: This method has to be desync-safe, meaning that it should
		 * be safe to call from within "getLocalPlayer()" blocks. It is
		 * tagged with the "constant method" syntax, which currently does
		 * nothing, to remind you of this. For best results, it should
		 * probably be a small method that returns a single value.
		 * ( It's less likely to be called from UI than getHighlightOrderId(), but might as
		 *   well be treated similarly )
		 */
		constant method getBehaviorCategory takes nothing returns behaviorcategory
	endinterface

	// (do not use "CreateRangedBehavior" in favor of "extends behavior")
	native CreateRangedBehavior takes nothing returns rangedbehavior
	native DestroyRangedBehavior takes rangedbehavior x returns nothing
	// ranged behavior is an interface for inter-operating with
	// the move ability to make a behavior that moves into range
	interface RangedBehavior extends rangedbehavior
		// first part: same interface as Behavior, see Behavior
		// for comment. We could do "extends Behavior" except
		// that it needed to hook up to the native "rangedbehavior" interface
		method update takes nothing returns behavior
		method begin takes nothing returns nothing defaults nothing
		method end takes boolean interrupted returns nothing defaults nothing
		constant method getHighlightOrderId takes nothing returns integer
		constant method interruptable takes nothing returns boolean
		constant method getBehaviorCategory takes nothing returns behaviorcategory
		// above is the same interface as Behavior
		method isWithinRange takes nothing returns boolean
		method endMove takes boolean interrupted returns nothing
		method getTarget takes nothing returns abilitytarget
	endinterface

	// "Abstract Ranged Behavior" uses Java code to natively define a default version of
	// "Ranged Behavior" so that you can use a bunch of good defaults, and with high performance.
	// Maybe later, for additional customization, the "Ranged Behavior" interface can have
	// some "struct AbstractRangedBehavior extends RangedBehavior" that accomplishes
	// the same stuff as this, but in user space and not natively.
	type abstractrangedbehavior extends rangedbehavior
	native CreateAbstractRangedBehavior takes unit x returns abstractrangedbehavior
	native DestroyAbstractRangedBehavior takes abstractrangedbehavior x returns nothing

	native AbstractRangedBehaviorResetI takes abstractrangedbehavior whichBehavior, abilitytarget target returns behavior
	native AbstractRangedBehaviorResetII takes abstractrangedbehavior whichBehavior, abilitytarget target, boolean disableCollision returns behavior
	native GetRangedBehaviorTarget takes rangedbehavior whichBehavior returns abilitytarget
	native GetAbstractRangedBehaviorSourceUnit takes abstractrangedbehavior whichBehavior returns unit
	
	interface NativeAbstractRangedBehaviorInterface extends abstractrangedbehavior
		// update is only called on this when we are within range
		method update takes boolean withinFacingWindow returns behavior
		// note that "begin" is called when we start moving over there
		// into range, not when the guy starts doing the thing.
		// Don't use it for breaking invisibility or that sort of thing.
		method begin takes nothing returns nothing defaults nothing
		method end takes boolean interrupted returns nothing defaults nothing
		constant method getHighlightOrderId takes nothing returns integer
		constant method interruptable takes nothing returns boolean
		constant method getBehaviorCategory takes nothing returns behaviorcategory
		method isWithinRange takes nothing returns boolean
		method endMove takes boolean interrupted returns nothing

		method updateOnInvalidTarget takes nothing returns behavior
		method isTargetStillValid takes nothing returns boolean
		method resetBeforeMoving takes nothing returns nothing
	endinterface

	struct NativeAbstractRangedBehavior extends NativeAbstractRangedBehaviorInterface
		unit behavingUnit // this member is a repeat against the java, hoping member here offers quick performant lookup
		abilitytarget target // this member is a repeat against the java, hoping member here offers quick performant lookup

		static method create takes unit behavingUnit returns thistype
			local thistype this = .allocate(behavingUnit)
			set this.behavingUnit = behavingUnit
			return this
		endmethod

		method innerReset takes abilitytarget x returns behavior
			set this.target = x
			return AbstractRangedBehaviorResetI(this, x)
		endmethod
		method innerResetEx takes abilitytarget x, boolean disableCollision returns behavior
			set this.target = x
			return AbstractRangedBehaviorResetII(this, x, disableCollision)
		endmethod

		method getTarget takes nothing returns abilitytarget
			return this.target // GetRangedBehaviorTarget(this)
		endmethod

		method getUnit takes nothing returns unit
			return this.behavingUnit // GetAbstractRangedBehaviorSourceUnit(this)
		endmethod
	endstruct
endlibrary


//=====================================================
// IconUI API                                        
//=====================================================
// This might be useful or something but probably does
// not work yet
library IconUI
	type iconui extends handle
	native GetUnitIconUI takes integer unitId returns iconui
	native GetAbilityOnIconUI takes integer abilityId, integer level returns iconui
	native GetAbilityOffIconUI takes integer abilityId, integer level returns iconui
	native GetAbilityLearnIconUI takes integer abilityId returns iconui
	native GetItemIconUI takes integer itemId returns iconui
endlibrary

//=====================================================
// HandleList API                                      
//=====================================================
// This is a "dumb" API, based on unit group but for handles
// in general. That's not super useful for the jass
// programmers yet but it gives us a way to communicate
// a list back to the engine, such as for natives that
// might take handlelist
library HandleListAPI
	type handlelist extends handle
	
	native CreateHandleList takes nothing returns handlelist
	native HandleListAdd takes handlelist whichList, handle toAdd returns nothing
	native HandleListRemove takes handlelist whichList, handle toRemove returns boolean
	native HandleListGet takes handlelist whichList, integer index returns handle
	native HandleListSize takes handlelist whichList returns integer
endlibrary

//=====================================================
// OrderButton API                                        
//=====================================================
library OrderButtonAPI requires BehaviorAPI, IconUI
	type orderbutton extends handle
	type orderbuttontype extends handle

	constant native ConvertOrderButtonType takes integer x returns orderbuttontype
	
	globals
		constant orderbuttontype ORDERBUTTON_INSTANT_NO_TARGET                 = ConvertOrderButtonType(0)
		constant orderbuttontype ORDERBUTTON_UNIT_TARGET                       = ConvertOrderButtonType(1)
		constant orderbuttontype ORDERBUTTON_POINT_TARGET                      = ConvertOrderButtonType(2)
		constant orderbuttontype ORDERBUTTON_UNIT_OR_POINT_TARGET              = ConvertOrderButtonType(3)
		constant orderbuttontype ORDERBUTTON_INSTANT_NO_TARGET_NO_INTERRUPT    = ConvertOrderButtonType(4)
		constant orderbuttontype ORDERBUTTON_PASSIVE                           = ConvertOrderButtonType(5)
		constant orderbuttontype ORDERBUTTON_MENU                              = ConvertOrderButtonType(6)
		
		constant string COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_FOOD                                                                      = "Nofood"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CREATE_UNIT_DUE_TO_MAXIMUM_FOOD_LIMIT                                      = "Maxsupply"
		constant string COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_GOLD                                                                      = "Nogold"
		constant string COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_LUMBER                                                                    = "Nolumber"
		constant string COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA                                                                      = "Nomana"
		constant string COMMAND_STRING_ERROR_KEY_SPELL_IS_NOT_READY_YET                                                               = "Cooldown"
		constant string COMMAND_STRING_ERROR_KEY_CARGO_CAPACITY_UNAVAILABLE                                                           = "Noroom"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_LOAD_TARGET                                                                = "Canttransport"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_DEVOUR_TARGET                                                              = "Cantdevour"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CAST_CYCLONE_ON_THIS_TARGET                                                = "Cantcyclone"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CAST_FERAL_SPIRIT_ON_THIS_TARGET                                           = "Cantspiritwolf"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CAST_POSSESSION_ON_THIS_TARGET                                             = "Cantpossess"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CAST_MANA_BURN_ON_THIS_TARGET                                              = "Cantmanaburn"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_CAPABLE_OF_ATTACKING                                              = "Onlyattackers"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_AN_ENTANGLED_GOLD_MINE                                                 = "Notentangledmine"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_A_HAUNTED_GOLD_MINE                                                    = "Notblightedmine"
		constant string COMMAND_STRING_ERROR_KEY_THAT_GOLD_MINE_IS_ALREADY_ENTANGLED                                                  = "Alreadyentangled"
		constant string COMMAND_STRING_ERROR_KEY_THAT_GOLD_MINE_IS_ALREADY_HAUNTED                                                    = "Alreadyblightedmine"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_TREE_OR_AN_ENTANGLED_GOLD_MINE                                         = "Targetwispresources"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_HAUNTED_GOLD_MINE                                                      = "Targetblightedmine"
		constant string COMMAND_STRING_ERROR_KEY_MUST_ENTANGLE_GOLD_MINE_FIRST                                                        = "Entangleminefirst"
		constant string COMMAND_STRING_ERROR_KEY_MUST_HAUNT_GOLD_MINE_FIRST                                                           = "Blightminefirst"
		constant string COMMAND_STRING_ERROR_KEY_THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_WISPS                                           = "Entangledminefull"
		constant string COMMAND_STRING_ERROR_KEY_THAT_GOLD_MINE_CANT_SUPPORT_ANY_MORE_ACOLYTES                                        = "Blightringfull"
		constant string COMMAND_STRING_ERROR_KEY_THE_SELECTED_ACOLYTE_IS_ALREADY_MINING                                               = "Acolytealreadymining"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_A_MINE_CONTROLLED_BY_ANOTHER_PLAYER                                    = "Nototherplayersmine"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_GOLD_MINE                                                              = "Targgetmine"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_RESOURCES                                                                = "Targgetresources"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_HUMAN_BUILDING                                                         = "Humanbuilding"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_UNDEAD_BUILDING                                                       = "Undeadbuilding"
		constant string COMMAND_STRING_ERROR_KEY_THAT_BUILDING_IS_CURRENTLY_UNDER_CONSTRUCTION                                        = "Underconstruction"
		constant string COMMAND_STRING_ERROR_KEY_THE_BUILDING_IS_ALREADY_UNDER_CONSTRUCTION                                           = "Alreadyrebuilding"
		constant string COMMAND_STRING_ERROR_KEY_THAT_CREATURE_IS_TOO_POWERFUL                                                        = "Creeptoopowerful"
		constant string COMMAND_STRING_ERROR_KEY_THAT_UNIT_IS_ALREADY_HIBERNATING                                                     = "Hibernating"
		constant string COMMAND_STRING_ERROR_KEY_THAT_UNIT_IS_ALREADY_LEASHED                                                         = "Magicleashed"
		constant string COMMAND_STRING_ERROR_KEY_THAT_UNIT_IS_IMMUNE_TO_MAGIC                                                         = "Immunetomagic"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_FRIENDLY_LIVING_UNITS_OR_ENEMY_UNDEAD_UNITS                              = "Holybolttarget"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_ENEMY_LIVING_UNITS_OR_FRIENDLY_UNDEAD_UNITS                              = "Deathcoiltarget"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_OR_BLIGHTED_GROUND                                                = "Dispelmagictarget"
		constant string COMMAND_STRING_ERROR_KEY_THAT_TREE_IS_OCCUPIED_TARGET_A_VACANT_TREE                                           = "Treeoccupied"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_MERGE_WITH_THAT_UNIT                                                       = "Coupletarget"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_HIPPOGRYPH                                                             = "Mounthippogryphtarget"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ARCHER                                                                = "Archerridertarget"
		constant string COMMAND_STRING_ERROR_KEY_MUST_EXPLORE_THERE_FIRST                                                             = "Cantsee"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_BUILD_THERE                                                                = "Cantplace"
		constant string COMMAND_STRING_ERROR_KEY_TARGETED_LOCATION_IS_OUTSIDE_OF_THE_MAP_BOUNDARY                                     = "Outofbounds"
		constant string COMMAND_STRING_ERROR_KEY_MUST_SUMMON_STRUCTURES_UPON_BLIGHT                                                   = "Offblight"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_BUILD_SO_CLOSE_TO_THE_GOLD_MINE                                            = "Tooclosetomine"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_CREATE_A_GOLD_MINE_SO_CLOSE_TO_THE_TOWN                                    = "Tooclosetohall"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_BUILD_AWAY_FROM_A_SHORELINE                                                = "Notonshoreline"
		constant string COMMAND_STRING_ERROR_KEY_A_NEWLY_CONSTRUCTED_UNIT_HAS_NO_ROOM_TO_BE_PLACED                                    = "Buildingblocked"
		constant string COMMAND_STRING_ERROR_KEY_A_UNIT_COULD_NOT_BE_TELEPORTED                                                       = "Teleportfail"
		constant string COMMAND_STRING_ERROR_KEY_SOMETHING_IS_BLOCKING_THAT_TREE_STUMP                                                = "Stumpblocked"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_LAND_THERE                                                                 = "Cantland"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_ROOT_THERE                                                                 = "Cantroot"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_NO_LONGER_ROOTABLE                                                         = "Cantrootunit"
		constant string COMMAND_STRING_ERROR_KEY_MUST_ROOT_ADJACENT_TO_A_GOLD_MINE_TO_ENTANGLE_IT                                     = "Mustroottoentangle"
		constant string COMMAND_STRING_ERROR_KEY_MUST_ROOT_CLOSER_TO_THE_GOLD_MINE                                                    = "Mustbeclosertomine"
		constant string COMMAND_STRING_ERROR_KEY_GOLD_MINE_COULDNT_BE_ENTANGLED                                                       = "Minenotentangleable"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_OUTSIDE_RANGE                                                              = "Notinrange"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_INSIDE_MINIMUM_RANGE                                                       = "UnderRange"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_THIS_UNIT                                                           = "Notthisunit"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_WITH_THIS_ACTION                                                  = "Targetunit"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_BUILDING_OR_TREE                                                       = "Targetstructuretree"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_GROUND_UNIT                                                            = "Targetground"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_AIR_UNIT                                                              = "Targetair"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_BUILDING                                                               = "Targetstructure"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_WARD                                                                   = "Targetward"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ITEM                                                                  = "Targetitem"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_TREE                                                                   = "Targettree"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_BUILDING_OR_A_MECHANICAL_UNIT                                          = "Targetrepair"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_BRIDGE                                                                 = "Targetbridge"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_NAVAL_UNIT                                                             = "Targetnaval"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_ONE_OF_YOUR_OWN_UNITS                                                    = "Targetowned"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_FRIENDLY_UNIT                                                          = "Targetally"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_NEUTRAL_UNIT                                                           = "Targetneutral"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ENEMY_UNIT                                                            = "Targetenemy"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_YOU_CAN_CONTROL                                                   = "Targetcontrol"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_HERO                                                                   = "Targethero"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ENEMY_HERO                                                            = "Targetenemyhero"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_CORPSE                                                                 = "Targetcorpse"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_FLESHY_CORPSE                                                          = "Targetfleshycorpse"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_SKELETAL_CORPSE                                                        = "Targetbonecorpse"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_UNDEAD_UNIT                                                           = "Targetundead"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_MECHANICAL_UNIT                                                        = "Targetmechanical"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_MOVEABLE_UNITS                                                           = "Targetmoveable"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ORGANIC_GROUND_UNIT                                                   = "Targetorganicground"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ANCIENT                                                               = "Targetancient"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ARMORED_TRANSPORT                                                     = "Targetarmoredtransport"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_WITH_MANA                                                         = "Targetmanauser"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_PEON                                                                   = "Targetbunkerunit"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_WISP                                                                   = "Targetwisp"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ACOLYTE                                                               = "Targetacolyte"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_SACRIFICIAL_PIT                                                        = "Targetpit"
		constant string COMMAND_STRING_ERROR_KEY_THAT_TREE_IS_OCCUPIED_BY_AN_OWL                                                      = "Needemptytree"
		constant string COMMAND_STRING_ERROR_KEY_THAT_TREE_IS_NOT_OCCUPIED_BY_AN_OWL                                                  = "Needowltree"
		constant string COMMAND_STRING_ERROR_KEY_THERE_ARE_NO_USABLE_CORPSES_NEARBY                                                   = "Cantfindcorpse"
		constant string COMMAND_STRING_ERROR_KEY_THERE_ARE_NO_CORPSES_OF_FRIENDLY_UNITS_NEARBY                                        = "Cantfindfriendlycorpse"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_UNITS                                                               = "Nounits"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_GROUND_UNITS                                                        = "Noground"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_AIR_UNITS                                                           = "Noair"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_BUILDINGS                                                           = "Nostructure"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_WARDS                                                               = "Noward"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_ITEMS                                                               = "Noitem"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_DEBRIS                                                              = "Nodebris"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_TREES                                                               = "Notree"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_WALLS                                                               = "Nowall"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_BRIDGES                                                             = "Nobridge"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_BUILDING_HAS_BEEN_FROZEN                                                      = "Notfrozenbldg"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_NAVAL_UNITS                                                         = "Nonaval"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_FRIENDLY_TOWN_HALL                                                       = "Nottownhall"
		constant string COMMAND_STRING_ERROR_KEY_THERE_ARE_NO_FRIENDLY_TOWN_HALLS_TO_TOWN_PORTAL_TO                                   = "Notownportalhalls"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_SELF                                                                = "Notself"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_YOUR_OWN_UNITS                                                      = "Notowned"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_FRIENDLY_UNITS                                                      = "Notfriendly"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_NEUTRAL_UNITS                                                       = "Notneutral"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_ENEMY_UNITS                                                         = "Notenemy"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_UNITS_INSIDE_A_BUILDING_OR_TRANSPORT                                = "Notcargo"
		constant string COMMAND_STRING_ERROR_KEY_THAT_TARGET_IS_NOT_VISIBLE_ON_THE_MAP                                                = "Nothidden"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_CARRIED_ITEMS                                                       = "Nothiddenitem"
		constant string COMMAND_STRING_ERROR_KEY_THAT_TARGET_IS_INVULNERABLE                                                          = "Notinvulnerable"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_HEROES                                                              = "Nohero"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_MUST_BE_LIVING                                                                = "Notcorpse"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_FLESHY_CORPSES                                                      = "Notfleshycorpse"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_SKELETAL_CORPSES                                                    = "Notbonecorpse"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_ORGANIC_UNITS                                                            = "Notmechanical"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_ORGANIC_UNITS                                                       = "Notorganic"
		constant string COMMAND_STRING_ERROR_KEY_CASTER_MOVEMENT_HAS_BEEN_DISABLED                                                    = "Notdisabled"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_ATTACK_THERE                                                               = "Cantattackloc"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_THERE                                                               = "Canttargetloc"
		constant string COMMAND_STRING_ERROR_KEY_INVENTORY_IS_FULL                                                                    = "Inventoryfull"
		constant string COMMAND_STRING_ERROR_KEY_SELECT_A_UNIT_WITH_AN_INVENTORY                                                      = "Inventoryinteract"
		constant string COMMAND_STRING_ERROR_KEY_ONLY_UNITS_WITH_AN_INVENTORY_CAN_PICK_UP_ITEMS                                       = "NeedInventory"
		constant string COMMAND_STRING_ERROR_KEY_ONLY_HEROES_THAT_HAVE_LEARNED_SPELLS_NOT_IN_COOLDOWN_CAN_USE_THIS_ITEM               = "Needretrainablehero"
		constant string COMMAND_STRING_ERROR_KEY_A_HERO_MUST_BE_NEARBY                                                                = "Neednearbyhero"
		constant string COMMAND_STRING_ERROR_KEY_A_VALID_PATRON_MUST_BE_NEARBY                                                        = "Neednearbypatron"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_SAPPERS                                                             = "Notsapper"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_ANCIENTS                                                            = "Notancient"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_SUMMONED_UNITS                                                      = "Notsummoned"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_TRANSPORTS_OR_BUNKERS                                               = "Nottransport"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_BEING_UNSUMMONED                                                           = "Notunsummoned"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_ILLUSIONS                                                           = "Notillusion"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_MORPHING_UNITS                                                      = "Notmorphing"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_A_DRUID_OF_THE_TALON                                                = "Notdot"
		constant string COMMAND_STRING_ERROR_KEY_ILLUSIONS_ARE_UNABLE_TO_HARVEST                                                      = "Illusionscantharvest"
		constant string COMMAND_STRING_ERROR_KEY_ILLUSIONS_CANNOT_PICK_UP_ITEMS                                                       = "Illusionscantpickup"
		constant string COMMAND_STRING_ERROR_KEY_THIS_UNIT_IS_IMMUNE_TO_POLYMORPH                                                     = "Cantpolymorphunit"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TARGET_AN_UNDEAD_UNIT                                                      = "Notundead"
		constant string COMMAND_STRING_ERROR_KEY_HERO_IS_AT_MAX_LEVEL                                                                 = "Heromaxed"
		constant string COMMAND_STRING_ERROR_KEY_HERO_HAS_FULL_HEALTH                                                                 = "HPmaxed"
		constant string COMMAND_STRING_ERROR_KEY_HERO_HAS_FULL_MANA                                                                   = "Manamaxed"
		constant string COMMAND_STRING_ERROR_KEY_ALREADY_AT_FULL_MANA_AND_HEALTH                                                      = "HPmanamaxed"
		constant string COMMAND_STRING_ERROR_KEY_ALREADY_AT_FULL_HEALTH                                                               = "UnitHPmaxed"
		constant string COMMAND_STRING_ERROR_KEY_ALREADY_AT_FULL_MANA                                                                 = "UnitManaMaxed"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_NOT_DAMAGED                                                                = "RepairHPmaxed"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_ALREADY_BEING_HEALED                                                       = "Alreadybeinghealed"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_IS_ALREADY_BEING_REPAIRED                                                     = "Alreadybeingrepaired"
		constant string COMMAND_STRING_ERROR_KEY_SACRIFICIAL_PIT_IS_ALREADY_SACRIFICING_AN_ACOLYTE                                    = "Pitalreadysacrificing"
		constant string COMMAND_STRING_ERROR_KEY_OUT_OF_STOCK                                                                         = "Outofstock"
		constant string COMMAND_STRING_ERROR_KEY_COOLDOWN_OUT_OF_STOCK                                                                = "Cooldownstock"
		constant string COMMAND_STRING_ERROR_KEY_ITEM_MUST_REMAIN_IN_YOUR_INVENTORY                                                   = "Cantdrop"
		constant string COMMAND_STRING_ERROR_KEY_ITEM_CANNOT_BE_PAWNED                                                                = "Cantpawn"
		constant string COMMAND_STRING_ERROR_KEY_NO_PEASANTS_COULD_BE_FOUND                                                           = "Calltoarms"
		constant string COMMAND_STRING_ERROR_KEY_NO_TOWN_HALLS_COULD_BE_FOUND_THAT_CAN_CONVERT_PEASANTS_INTO_MILITIA                  = "Calltoarmspeasant"
		constant string COMMAND_STRING_ERROR_KEY_NO_MILITIA_COULD_BE_FOUND                                                            = "Backtowork"
		constant string COMMAND_STRING_ERROR_KEY_NO_TOWN_HALLS_COULD_BE_FOUND_THAT_CAN_CONVERT_MILITIA_INTO_PEASANTS                  = "Backtoworkmilitia"
		constant string COMMAND_STRING_ERROR_KEY_NO_PEONS_COULD_BE_FOUND                                                              = "BattleStations"
		constant string COMMAND_STRING_ERROR_KEY_REPLACE_THIS_ERROR_MESSAGE_WITH_SOMETHING_MEANINGFUL                                 = "Replaceme"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_BUILDING_HAS_LIQUID_FIRE                                                      = "Notliquidfirebldg"
		constant string COMMAND_STRING_ERROR_KEY_ETHEREAL_UNITS_CAN_ONLY_BE_HIT_BY_SPELLS_AND_MAGIC_DAMAGE                            = "Notethereal"
		constant string COMMAND_STRING_ERROR_KEY_TARGET_HAS_NO_STEALABLE_BUFFS                                                        = "Needstealbuff"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_MELEE_ATTACKER                                                         = "Needmeleeattacker"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_RANGED_ATTACKER                                                        = "Needrangedattacker"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_SPECIAL_ATTACKER                                                       = "Needspecialattacker"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ATTACK_UNIT                                                           = "Needattacker"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_CASTER                                                                 = "Needcaster"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ATTACK_UNIT_OR_A_CASTER                                               = "Needattackerorcaster"
		constant string COMMAND_STRING_ERROR_KEY_NO_STRUCTURES_ARE_AVAILABLE_TO_TELEPORT_THE_TARGET_TO                                = "Nopreservationtarget"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_TRANSFORM_THIS_ITEM                                                        = "Canttransformitem"
		constant string COMMAND_STRING_ERROR_KEY_THIS_UNIT_HAS_ALREADY_BEEN_MARKED_BY_FIRE                                            = "Notmocunit"
		constant string COMMAND_STRING_ERROR_KEY_CANT_IMPALE_THIS_UNIT                                                                = "Cantimpale"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ENEMY_UNIT_WITH_POSITIVE_BUFFS                                        = "Needpositivebuff"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_AN_ENEMY_UNIT_WITH_POSITIVE_BUFFS_OR_A_SUMMONED_UNIT                     = "Needposbufforsummoned"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_FRIENDLY_UNIT_WITH_NEGATIVE_BUFFS                                      = "Neednegativebuff"
		constant string COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA_TO_ABSORB                                                            = "Absorbmana"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_PICK_UP_THIS_ITEM                                                          = "Canttakeitem"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_DROP_THIS_ITEM                                                             = "Cantdropitem"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_THIS_ITEM                                                              = "Cantuseitem"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_POWERUPS                                                               = "Notpowerup"
		constant string COMMAND_STRING_ERROR_KEY_THIS_ITEM_IS_COOLING_DOWN                                                            = "Itemcooldown"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_FIND_COUPLE_TARGET                                                         = "Cantfindcoupletarget"
		constant string COMMAND_STRING_ERROR_KEY_THIS_UNIT_HAS_A_DISABLED_INVENTORY                                                   = "Notdisabledinventory"
		constant string COMMAND_STRING_ERROR_KEY_THIS_UNIT_HAS_RESISTANT_SKIN                                                         = "Resistantskin"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_USE_INVULNERABILITY_GRANTING_SPELLS_OR_ITEMS                               = "Notinvulnerablespell"
		constant string COMMAND_STRING_ERROR_KEY_MUST_TARGET_SUMMONED_UNITS                                                           = "Needsummoned"
		constant string COMMAND_STRING_ERROR_KEY_UNABLE_TO_SUBMERGE_THERE                                                             = "Cantsubmergethere"
		constant string COMMAND_STRING_ERROR_KEY_THIS_UNIT_HAS_ALREADY_BEEN_STRICKEN_WITH_DOOM                                        = "Alreadydoomed"
	endglobals

	native CreateOrderButton takes orderbuttontype targetType, integer orderId returns orderbutton
	native DestroyOrderButton takes orderbutton whichOrder returns nothing
	native SetOrderButtonAutoCastOrderId takes orderbutton whichOrder, integer autoCastOrderId returns nothing
	native SetOrderButtonUnAutoCastOrderId takes orderbutton whichOrder, integer autoCastOrderId returns nothing
	// if you set container id, it would only show if you also added a menu-type icon
	native SetOrderButtonContainerMenuOrderId takes orderbutton whichOrder, integer containerMenuOrderId returns nothing
	native SetOrderButtonDisabled takes orderbutton whichOrder, boolean disabled returns nothing
	native SetOrderButtonManaCost takes orderbutton whichOrder, integer costAmount returns nothing
	native SetOrderButtonGoldCost takes orderbutton whichOrder, integer costAmount returns nothing
	native SetOrderButtonLumberCost takes orderbutton whichOrder, integer costAmount returns nothing
	native SetOrderButtonFoodCost takes orderbutton whichOrder, integer costAmount returns nothing
	native SetOrderButtonCharges takes orderbutton whichOrder, integer charges returns nothing
	native SetOrderButtonAutoCastActive takes orderbutton whichOrder, boolean active returns nothing
	native SetOrderButtonIconPath takes orderbutton whichOrder, string iconPath returns nothing
	native SetOrderButtonButtonPositionX takes orderbutton whichOrder, integer buttonPositionX returns nothing
	native SetOrderButtonButtonPositionY takes orderbutton whichOrder, integer buttonPositionY returns nothing
	native SetOrderButtonToolTip takes orderbutton whichOrder, string tip returns nothing
	native SetOrderButtonUberTip takes orderbutton whichOrder, string uberTip returns nothing
	// NOTE: On 1.31+, we should probably change SetHotKey to take one of the OSKEY constants
	native SetOrderButtonHotKey takes orderbutton whichOrder, string hotkey returns nothing
	
	// NOTE: maybe this is useful, but it does not look up AOE/"PreviewBuildUnitId", so
	// you will have to do those on your own with Set functions.
	native SetOrderButtonByIconUI takes orderbutton whichOrder, iconui whichUI returns nothing
	
	// NOTE: below is intended for buildings, root, build tiny, etc and will show a freeze frame of the stand animation of the
	// model at the given modelPath

	// TODO choosing pathing map and target mouse model both were discontinued because it couldnt support
	// "Is Can Build On" world editor nonsense. Do we want to bring them back later? Maybe only the model path?
	//native SetOrderButtonTargetingMouseModel takes orderbutton whichOrder, string modelPath returns nothing
	//native SetOrderButtonTargetPathingMap takes orderbutton whichOrder, string pathingMapFilePath returns nothing
	native SetOrderButtonPreviewBuildUnitId takes orderbutton whichOrder, integer unitId returns nothing
	native SetOrderButtonAOE takes orderbutton whichOrder, real radius returns nothing

	native GetOrderButtonOrderId takes orderbutton whichOrder returns integer
	native GetOrderButtonAutoCastOrderId takes orderbutton whichOrder returns integer
	native GetOrderButtonUnAutoCastOrderId takes orderbutton whichOrder returns integer
	native GetOrderButtonContainerMenuOrderId takes orderbutton whichOrder returns integer
	native IsOrderButtonDisabled takes orderbutton whichOrder returns boolean
	native GetOrderButtonManaCost takes orderbutton whichOrder returns integer
	native GetOrderButtonGoldCost takes orderbutton whichOrder returns integer
	native GetOrderButtonLumberCost takes orderbutton whichOrder returns integer
	native GetOrderButtonFoodCost takes orderbutton whichOrder returns integer
	native GetOrderButtonCharges takes orderbutton whichOrder returns integer
	native IsOrderButtonAutoCastActive takes orderbutton whichOrder returns boolean
	// --DEPRECATED-- native GetOrderButtonIconPath takes orderbutton whichOrder returns string
	native GetOrderButtonButtonPositionX takes orderbutton whichOrder returns integer
	native GetOrderButtonButtonPositionY takes orderbutton whichOrder returns integer
	native GetOrderButtonToolTip takes orderbutton whichOrder returns string
	native GetOrderButtonUberTip takes orderbutton whichOrder returns string
	// NOTE: On 1.31+, we should probably change GetHotKey to return one of the OSKEY constants
	native GetOrderButtonHotKey takes orderbutton whichOrder returns string

	//native GetOrderButtonTargetingMouseModel takes orderbutton whichOrder returns string
	//native GetOrderButtonTargetPathingMap takes orderbutton whichOrder returns string
	native GetOrderButtonPreviewBuildUnitId takes orderbutton whichOrder returns integer
	native GetOrderButtonAOE takes orderbutton whichOrder returns real
	
	native FailUsableCheckOnRequirement takes orderbutton callbackOwner, integer requiredTechTypeId, integer requiredLevel returns nothing
	native FailUsableCheckOnHeroLevelRequirement takes orderbutton callbackOwner, integer requiredLevel returns nothing
	native FailUsableCheckOnCooldown takes orderbutton callbackOwner, real cooldownRemaining, real cooldown returns nothing
	native FailUsableCheckTechMaxReached takes orderbutton callbackOwner returns nothing
	// NOTE: dont call FailCheckWithMessage with an english input, prefer instead to
	// use one of the keys from UI\CommandStrings.txt in the [ErrorKeys] and this
	// will ensure that if the user plays in Spanish or Chinese, that the error
	// will show in Spanish or Chinese. Note that the special string
	// "Nomana" in this callback will turn the icon blue before it is even clicked.
	native FailUsableCheckWithMessage takes orderbutton callbackOwner, string messageKey returns nothing
	native FailTargetCheckWithMessage takes orderbutton callbackOwner, string messageKey returns nothing
	// NOTE: "finalizedTarget" is pretty useless. In theory it lets you pass the check but
	// based on a newer/better target that you provided. In practice, probably just
	// always pass the same target. Maybe we can delete it in a later version.
	native PassTargetCheck takes orderbutton callbackOwner, handle finalizedTarget returns nothing
	native PassUsableCheck takes orderbutton callbackOwner returns nothing
	
	interface OrderButtonNoTarget extends orderbutton
		static method create takes integer orderId returns OrderButtonNoTarget defaults .allocate(ORDERBUTTON_INSTANT_NO_TARGET, orderId)
		
		method checkUsable takes unit caster, ability source returns nothing
		
		method checkTarget takes unit caster, ability source returns nothing
		
		method begin takes unit caster, ability source returns behavior
		
		method onCancelFromQueue takes unit caster, ability source returns nothing defaults nothing
	endinterface
	
	interface OrderButtonTargetWidget extends orderbutton
		static method create takes integer orderId returns OrderButtonTargetWidget defaults .allocate(ORDERBUTTON_UNIT_TARGET, orderId)
		
		method checkUsable takes unit caster, ability source returns nothing
		
		method checkTargetUnit takes unit caster, ability source, unit target returns nothing
		
		method beginUnit takes unit caster, ability source, unit target returns behavior
		
		method checkTargetItem takes unit caster, ability source, item target returns nothing
		
		method beginItem takes unit caster, ability source, item target returns behavior
		
		method checkTargetDestructable takes unit caster, ability source, destructable target returns nothing
		
		method beginDestructable takes unit caster, ability source, destructable target returns behavior
		
		method onCancelFromQueue takes unit caster, ability source returns nothing defaults nothing
	endinterface
	
	interface OrderButtonTargetLocation extends orderbutton
		static method create takes integer orderId returns OrderButtonTargetLocation defaults .allocate(ORDERBUTTON_POINT_TARGET, orderId)
		
		method checkUsable takes unit caster, ability source returns nothing
		
		method checkTargetLoc takes unit caster, ability source, location target returns nothing
		
		method beginLoc takes unit caster, ability source, location target returns behavior
		
		method onCancelFromQueue takes unit caster, ability source returns nothing defaults nothing
	endinterface
	
	interface OrderButtonTarget extends orderbutton
		static method create takes integer orderId returns OrderButtonTarget defaults .allocate(ORDERBUTTON_UNIT_OR_POINT_TARGET, orderId)
		
		method checkUsable takes unit caster, ability source returns nothing
		
		method checkTargetUnit takes unit caster, ability source, unit target returns nothing
		
		method beginUnit takes unit caster, ability source, unit target returns behavior
		
		method checkTargetItem takes unit caster, ability source, item target returns nothing
		
		method beginItem takes unit caster, ability source, item target returns behavior
		
		method checkTargetDestructable takes unit caster, ability source, destructable target returns nothing
		
		method beginDestructable takes unit caster, ability source, destructable target returns behavior
		
		method checkTargetLoc takes unit caster, ability source, location target returns nothing
		
		method beginLoc takes unit caster, ability source, location target returns behavior
		
		method onCancelFromQueue takes unit caster, ability source returns nothing defaults nothing
	endinterface
	
	interface OrderButtonInstant extends orderbutton
		static method create takes integer orderId returns OrderButtonInstant defaults .allocate(ORDERBUTTON_INSTANT_NO_TARGET_NO_INTERRUPT, orderId)
		
		method checkUsable takes unit caster, ability source returns nothing
		
		method checkTarget takes unit caster, ability source returns nothing
		
		// this is not a "begin" method, as it returns no behavior
		method use takes unit caster, ability source returns nothing
	endinterface
	
	interface OrderButtonPassive extends orderbutton
		static method create takes integer orderId returns OrderButtonPassive defaults .allocate(ORDERBUTTON_PASSIVE, orderId)
	endinterface
	
	interface OrderButtonMenu extends orderbutton
		static method create takes integer orderId returns OrderButtonMenu defaults .allocate(ORDERBUTTON_MENU, orderId)
	endinterface
endlibrary

//=====================================================
// AbilityAPI API                                        
//=====================================================
// Use this to create an ability that can 
// be given to a unit. It is the lower level,
// and if you want the "easy" version, prefer
// to use the default generic base classes
// defined later on.
library AbilityAPI requires OrderButtonAPI
	type abilitycategory extends handle
	
	constant native ConvertAbilityCategory takes integer x returns abilitycategory
	
	globals
		constant abilitycategory ABILITY_CATEGORY_ATTACK                            = ConvertAbilityCategory(0)
		constant abilitycategory ABILITY_CATEGORY_MOVEMENT                          = ConvertAbilityCategory(1)
		constant abilitycategory ABILITY_CATEGORY_CORE                              = ConvertAbilityCategory(2)
		constant abilitycategory ABILITY_CATEGORY_PASSIVE                           = ConvertAbilityCategory(3)
		constant abilitycategory ABILITY_CATEGORY_SPELL                             = ConvertAbilityCategory(4)
		constant abilitycategory ABILITY_CATEGORY_ITEM                              = ConvertAbilityCategory(5)
		constant abilitycategory ABILITY_CATEGORY_BUFF                              = ConvertAbilityCategory(6)
	endglobals

	// setting or getting ability level will probably crash for some built-in abilities that fundamentally lack "level"
	// ( we can change it to return 0 later )
	native SetAbilityLevel takes unit abilityUnit, ability whichAbility, integer level returns nothing

	native GetAbilityLevel takes ability whichAbility returns integer

	// NOTE: do not call the native below, it exists only for interoperating with "extends" keyword
	private native CreateJassAbility takes integer aliasId returns ability
	
	interface Ability extends ability
		static method create takes integer aliasId returns Ability defaults .allocate(aliasId)
		
		method populate takes gameobject editorData, integer level returns nothing
		
		/* should fire when ability added to unit */
		method onAdd takes unit whichUnit returns nothing
		
		/* should fire when ability removed from unit */
		method onRemove takes unit whichUnit returns nothing
		
		/* should fire for "permanent" abilities that are kept across unit type change */
		method onSetUnitType takes unit whichUnit returns nothing
		
		/* Glasir added the Ability Disable Type for the ability builder JSON
		 * universe of functions, so that it is aware of different disables and
		 * handles them differently. I'm not sure if I like that because I
		 * don't think the guy doing the disable would want to have to specify,
		 * but that's how it is right now. The current disable types are
		 * REQUIREMENTS, CONSTRUCTION, TRANSFORMATION, TRIGGER, ATTACKDISABLED,
		 * and PLAYER. Note that common.j natives to disable abilities always use
		 * TRIGGER, which would be wrong if we implement abilities themselves in
		 * jass, since for example Silence would want to use ATTACKDISABLED */
		method onSetDisabled takes boolean disabled, abilitydisabletype whichType returns nothing defaults nothing
		
		method onSetIconShowing takes boolean iconShowing returns nothing defaults nothing
		
		method onSetPermanent takes boolean permanent returns nothing defaults nothing
		
		constant method getAbilityCategory takes nothing returns abilitycategory

		/* maybe later we can replace getLevel and setLevel with "operator level" if that feature is added */
		method getLevel takes nothing returns integer defaults GetAbilityLevel(this)

		method setLevel takes unit abilityUnit, integer level returns integer defaults SetAbilityLevel(abilityUnit, this, level)
	endinterface
	
	native AbilityAddOrderButton takes Ability whichAbility, orderbutton whichOrder returns nothing
	native AbilityRemoveOrderButton takes Ability whichAbility, orderbutton whichOrder returns nothing
	native RegisterAbilityStructType takes integer codeId, structtype whichStructType returns nothing

	// ==========================================================================================
	// ========== Some general stuff ==============================
	// ==========================================================================================
	// On Warcraft 3, folks always made abilities using "UnitAddAbility(myUnit, 'AUan')" for example
	// if we wanted to provide the unit with Animate Dead. That native enforced a rule that a unit
	// could only have 1 of an ability. But elsewhere the game violated its own rule, such as
	// when an item would add an ability to a unit; two "Claws of Attack +3" stack by adding
	// 'AIat' to the unit twice, not by modifying the statistical values on the unit's one
	// and only instance of 'AIat'.
	// In premise, the following example:
	//```
	//    if BlzGetUnitAbility(myUnit, 'AIat') == null then
	//        call AddUnitAbility(myUnit, CreateAbility('AIat'))
	//    endif
	//```
	// ... would be nearly
	// identical to "call UnitAddAbility(myUnit, 'AUan')" but different because decomposing into
	// these lower level natives gives us more control, and allows us to violate the rule,
	// similar to items. In the case of Ability Builder, it also allows us to save a reference
	// to the exact ability created.
	native CreateAbility takes integer whichAbilityId returns ability

	// These do the same thing as similar Blz functions if user simulation includes newer patch,
	// but are distinct because our simulation will include them regardless of patch version
	native GetUnitAbility takes unit whichUnit, integer whichAbilityId returns ability
	native GetUnitAbilityByIndex takes unit whichUnit, integer index returns ability

	// Unlike "UnitAddAbility" for the rawcode based ones, this uses reversed name "AddUnitAbility"
	// which staples an existing ability handle onto a unit. Maybe we could rename it
	// to "UnitAddAbilityHandle" if this ends up too confusing in the future
	native AddUnitAbility takes unit whichUnit, ability whichAbility returns nothing

	// same idea here, see comment on AddUnitAbility. Returns true if something was removed.
	native RemoveUnitAbility takes unit whichUnit, ability whichAbiilty returns nothing

	native GetAbilityAliasId takes ability whichAbility returns integer
	native GetAbilityCodeId takes ability whichAbility returns integer

	// might be the same as BlzUnitHideAbility, but this one is for ability handles
	native SetAbilityIconShowing takes ability whichAbility, boolean showing returns nothing

	// below is some low level thing, ability builder changes added concept of disable type so it
	// was exposed here, but maybe you want to always use type ABILITY_DISABLE_TYPE_TRIGGER so we don't bork the low level
	// system
	native SetAbilityDisabled takes unit abilityUnit, ability whichAbility, boolean disabled, abilitydisabletype reason returns nothing

	native SetAbilityPermanent takes ability whichAbility, boolean flag returns nothing
	native IsAbilityPermanent takes ability whichAbiilty returns boolean
	
	// below will crash unless used on jass-defined abilities
	native SetAbilityPhysical takes Ability whichAbility, boolean flag returns nothing
	native SetAbilityUniversal takes Ability whichAbility, boolean flag returns nothing
	native IsAbilityPhysical takes Ability whichAbility returns boolean
	native IsAbilityUniversal takes Ability whichAbility returns boolean
	native SetAbilityEnabledWhileUpgrading takes Ability whichAbility, boolean flag returns nothing
	native SetAbilityEnabledWhileUnderConstruction takes Ability whichAbility, boolean flag returns nothing
	
	// below are for helping make abilities probably. In the AbilityBuilder changes, it was noted
	// that some abilities will tick while the unit is paused and some will not. Originally Retera
	// had created a system where all abilities have "onTick()" and it is called if their unit
	// is unpaused. To try to facilitate this function still being called for abilites on a paused
	// unit, the Java-side implementation of things turned into a clustertruck of checks. There was
	// such a thing called a "Paused" buff and if you look through it all, the "Paused" buff is the
	// one that isn't paused. It's a buff that still ticks when the unit is paused, by hacking.
	// That's pretty gross. If we can port everything to jass and ditch the hardcoded stuff, we
	// might as well ditch Paused buffs as a concept in favor of this:
	// YOUR ABILITY EITHER REGISTERS TO TICK WITH THE UNIT, OR WITH THE GAME, OR IT DOES NOT.
	// YOU DECIDE. (Remember to destroy your event and trigger .)
	native TriggerRegisterOnTick takes trigger t returns event
	native TriggerRegisterOnUnitTick takes trigger t, unit tickSource returns event

	// Yellow "Not enough food" messages -- in particular, a function for display such messages.
	// They are typically at the bottom of the screen in gold, above the interface.
	native ShowInterfaceError takes player whichPlayer, string errorString returns nothing
endlibrary

//=====================================================
// GenericAbilityBaseTypes                                        
//=====================================================
// These are utilities that you can extend when
// making an ability or behavior that are intended
// to reduce the effort required to making common
// types of abilities
library GenericAbilityBaseTypes requires AbilityAPI, BehaviorAPI
	// Provide common access to the ability button
	// (in case we are wrapped in a SpellBook who needs to
	//  set our button's menu ID, etc)
	struct GenericSingleIconAbilityBase extends Ability
		integer alias
		orderbutton abilityButton
		
		method innerPopulate takes gameobject editorData, integer level returns nothing
		endmethod
		
		method populate takes gameobject editorData, integer level returns nothing
			local iconui iconAtLevel = GetAbilityOnIconUI(this.alias, level - 1)
			call SetOrderButtonByIconUI(this.abilityButton, iconAtLevel)
			call innerPopulate(editorData, level)
		endmethod
	
		method onAdd takes unit whichUnit returns nothing
		endmethod
		
		method onRemove takes unit whichUnit returns nothing
		endmethod
		
		method onSetUnitType takes unit whichUnit returns nothing
		endmethod
	
		method onSetDisabled takes boolean disabled, abilitydisabletype whichType returns nothing
		endmethod
		
		method onSetIconShowing takes boolean iconShowing returns nothing
		endmethod
		
		method onSetPermanent takes boolean permanent returns nothing
		endmethod
		
		// Utility methods, maybe later these could be added on Ability itself somehow
		method setPhysical takes boolean value returns nothing
			call SetAbilityPhysical(this, value)
		endmethod
		
		method setUniversal takes boolean value returns nothing
			call SetAbilityUniversal(this, value)
		endmethod
		
		method isPhysical takes nothing returns boolean
			return IsAbilityPhysical(this)
		endmethod
		
		method isUniversal takes nothing returns boolean
			return IsAbilityUniversal(this)
		endmethod
		
		method getCodeId takes nothing returns integer
			return GetAbilityCodeId(this)
		endmethod
		
		method getAliasId takes nothing returns integer
			return GetAbilityAliasId(this)
		endmethod
	endstruct

	scope TargetWidget
		private interface ActiveAbilityInterface extends GenericSingleIconAbilityBase
			method checkUsable takes unit caster returns nothing
			
			method checkTargetUnit takes unit caster, unit target returns nothing
			
			method beginUnit takes unit caster, unit target returns behavior
			
			method checkTargetItem takes unit caster, item target returns nothing
			
			method beginItem takes unit caster, item target returns behavior
			
			method checkTargetDestructable takes unit caster, destructable target returns nothing
			
			method beginDestructable takes unit caster, destructable target returns behavior
		endinterface
	
		private struct OrderButtonImpl extends OrderButtonTargetWidget
			ActiveAbilityInterface parent
		
			public static method create takes integer orderId, ActiveAbilityInterface parent returns OrderButtonImpl
				local OrderButtonImpl this = .allocate(orderId)
				set this.parent = parent
				return this
			endmethod
		
			method checkUsable takes unit caster, ability source returns nothing
				call this.parent.checkUsable(caster)
			endmethod
			
			method checkTargetUnit takes unit caster, ability source, unit target returns nothing
				call this.parent.checkTargetUnit(caster, target)
			endmethod
			
			method beginUnit takes unit caster, ability source, unit target returns behavior
				return this.parent.beginUnit(caster, target)
			endmethod
			
			method checkTargetItem takes unit caster, ability source, item target returns nothing
				call this.parent.checkTargetItem(caster, target)
			endmethod
			
			method beginItem takes unit caster, ability source, item target returns behavior
				return this.parent.beginItem(caster, target)
			endmethod
			
			method checkTargetDestructable takes unit caster, ability source, destructable target returns nothing
				call this.parent.checkTargetDestructable(caster, target)
			endmethod
			
			method beginDestructable takes unit caster, ability source, destructable target returns behavior
				return this.parent.beginDestructable(caster, target)
			endmethod
		endstruct

		/* abstract */ struct AbstractGenericActiveAbilityTargetWidget extends ActiveAbilityInterface
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonImpl.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
			
			// NOTE that ActiveAbilityInterface methods are missing here,
			// so you need to implement those 
		endstruct
	endscope
	
	scope TargetLocation
		private interface ActiveAbilityInterface extends GenericSingleIconAbilityBase
			method checkUsable takes unit caster returns nothing
			
			method checkTargetLoc takes unit caster, location target returns nothing
			
			method beginLoc takes unit caster, location target returns behavior
		endinterface
	
		private struct OrderButtonImpl extends OrderButtonTargetLocation
			ActiveAbilityInterface parent
		
			public static method create takes integer orderId, ActiveAbilityInterface parent returns OrderButtonImpl
				local OrderButtonImpl this = .allocate(orderId)
				set this.parent = parent
				return this
			endmethod
		
			method checkUsable takes unit caster, ability source returns nothing
				call this.parent.checkUsable(caster)
			endmethod
			
			method checkTargetLoc takes unit caster, ability source, location target returns nothing
				call this.parent.checkTargetLoc(caster, target)
			endmethod
			
			method beginLoc takes unit caster, ability source, location target returns behavior
				return this.parent.beginLoc(caster, target)
			endmethod
		endstruct

		/* abstract */ struct AbstractGenericActiveAbilityTargetLocation extends ActiveAbilityInterface
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonImpl.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
			
			// NOTE that ActiveAbilityInterface methods are missing here,
			// so you need to implement those 
		endstruct
	endscope
	
	scope TargetWidgetOrLocation
		private interface ActiveAbilityInterface extends GenericSingleIconAbilityBase
			method checkUsable takes unit caster returns nothing
			
			method checkTargetUnit takes unit caster, unit target returns nothing
			
			method beginUnit takes unit caster, unit target returns behavior
			
			method checkTargetItem takes unit caster, item target returns nothing
			
			method beginItem takes unit caster, item target returns behavior
			
			method checkTargetDestructable takes unit caster, destructable target returns nothing
			
			method beginDestructable takes unit caster, destructable target returns behavior
			
			method checkTargetLoc takes unit caster, location target returns nothing
			
			method beginLoc takes unit caster, location target returns behavior
		endinterface
	
		private struct OrderButtonImpl extends OrderButtonTarget
			ActiveAbilityInterface parent
		
			public static method create takes integer orderId, ActiveAbilityInterface parent returns OrderButtonImpl
				local OrderButtonImpl this = .allocate(orderId)
				set this.parent = parent
				return this
			endmethod
		
			method checkUsable takes unit caster, ability source returns nothing
				call this.parent.checkUsable(caster)
			endmethod
			
			method checkTargetUnit takes unit caster, ability source, unit target returns nothing
				call this.parent.checkTargetUnit(caster, target)
			endmethod
			
			method beginUnit takes unit caster, ability source, unit target returns behavior
				return this.parent.beginUnit(caster, target)
			endmethod
			
			method checkTargetItem takes unit caster, ability source, item target returns nothing
				call this.parent.checkTargetItem(caster, target)
			endmethod
			
			method beginItem takes unit caster, ability source, item target returns behavior
				return this.parent.beginItem(caster, target)
			endmethod
			
			method checkTargetDestructable takes unit caster, ability source, destructable target returns nothing
				call this.parent.checkTargetDestructable(caster, target)
			endmethod
			
			method beginDestructable takes unit caster, ability source, destructable target returns behavior
				return this.parent.beginDestructable(caster, target)
			endmethod
			
			method checkTargetLoc takes unit caster, ability source, location target returns nothing
				call this.parent.checkTargetLoc(caster, target)
			endmethod
			
			method beginLoc takes unit caster, ability source, location target returns behavior
				return this.parent.beginLoc(caster, target)
			endmethod
		endstruct

		/* abstract */ struct AbstractGenericActiveAbilityTargetWidgetOrLocation extends ActiveAbilityInterface
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonImpl.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
			
			// NOTE that ActiveAbilityInterface methods are missing here,
			// so you need to implement those 
		endstruct
	endscope
	
	scope NoTarget
		private interface ActiveAbilityInterface extends GenericSingleIconAbilityBase
			method checkUsable takes unit caster returns nothing
		
			method checkTarget takes unit caster returns nothing
			
			method begin takes unit caster returns behavior
		endinterface
	
		private struct OrderButtonImpl extends OrderButtonNoTarget
			ActiveAbilityInterface parent
		
			public static method create takes integer orderId, ActiveAbilityInterface parent returns OrderButtonImpl
				local OrderButtonImpl this = .allocate(orderId)
				set this.parent = parent
				return this
			endmethod
		
			method checkUsable takes unit caster, ability source returns nothing
				call this.parent.checkUsable(caster)
			endmethod
			
			method checkTarget takes unit caster, ability source returns nothing
				call this.parent.checkTarget(caster)
			endmethod
			
			method begin takes unit caster, ability source returns behavior
				return this.parent.begin(caster)
			endmethod
		endstruct

		/* abstract */ struct AbstractGenericActiveAbilityNoTarget extends ActiveAbilityInterface
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonImpl.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
			
			// NOTE that ActiveAbilityInterface methods are missing here,
			// so you need to implement those 
		endstruct
	endscope
	
	scope InstantNoInterrupt
		private interface ActiveAbilityInterface extends GenericSingleIconAbilityBase
			method checkUsable takes unit caster returns nothing
		
			method checkTarget takes unit caster returns nothing
			
			method use takes unit caster returns nothing
		endinterface
	
		private struct OrderButtonImpl extends OrderButtonInstant
			ActiveAbilityInterface parent
		
			public static method create takes integer orderId, ActiveAbilityInterface parent returns OrderButtonImpl
				local OrderButtonImpl this = .allocate(orderId)
				set this.parent = parent
				return this
			endmethod
		
			method checkUsable takes unit caster, ability source returns nothing
				call this.parent.checkUsable(caster)
			endmethod
		
			method checkTarget takes unit caster, ability source returns nothing
				call this.parent.checkTarget(caster)
			endmethod
			
			method use takes unit caster, ability source returns nothing
				return this.parent.use(caster)
			endmethod
		endstruct

		/* abstract */ struct AbstractGenericActiveAbilityInstant extends ActiveAbilityInterface
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonImpl.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
			
			// NOTE that ActiveAbilityInterface methods are missing here,
			// so you need to implement those 
		endstruct
	endscope
	
	scope Passive
	
		/* abstract */ struct AbstractGenericPassiveAbility extends GenericSingleIconAbilityBase
		
			public static method create takes integer aliasId, integer orderId returns thistype
				local thistype this = .allocate(aliasId)
				set this.alias = aliasId
				set this.abilityButton = OrderButtonPassive.create(orderId, this)
				call AbilityAddOrderButton(this, this.abilityButton)
				return this
			endmethod
		endstruct
	endscope
endlibrary

//=====================================================
// Buff API                                        
//=====================================================
library BuffAPI requires AbilityAPI
	private native CreateJassBuff takes integer codeId, integer aliasId returns buff
	interface Buff extends buff
		static method create takes integer aliasId returns thistype defaults .allocate(aliasId, aliasId)
		method onAdd takes unit target returns nothing
		method onRemove takes unit target returns nothing
		method onDeath takes unit target returns nothing
		method getDurationRemaining takes unit target returns real
		method getDurationMax takes nothing returns real
		method isTimedLifeBar takes nothing returns boolean
		method getAliasId takes nothing returns integer defaults GetAbilityAliasId(this)
		method getCodeId takes nothing returns integer defaults GetAbilityCodeId(this)
	endinterface
	
	struct BuffTimed extends Buff
		effect fx
		real duration
		integer expireTick
		trigger tickTrigger

		public static method create takes integer aliasId, real duration returns thistype
			local thistype this = .allocate(aliasId)
			this.duration = duration
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
		endmethod

		method onBuffRemove takes unit target returns nothing
		endmethod

		method refreshExpiration takes nothing returns nothing
			integer durationTicks = R2I(this.duration / au_SIMULATION_STEP_TIME)
			this.expireTick = GetGameTurnTick() + durationTicks
		endmethod

		method onAdd takes unit target returns nothing
			onBuffAdd(target)
			this.fx = AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_TARGET, target, DEFAULT_ATTACH_POINTS)
			this.tickTrigger = CreateTrigger()
			call TriggerAddAction(this.tickTrigger, method this.onTick)
			refreshExpiration()
			call TriggerRegisterOnUnitTick(this.tickTrigger, target)
		endmethod

		method onRemove takes unit target returns nothing
			onBuffRemove(target)
			call DestroyEffect(fx)
			call DestroyTrigger(this.tickTrigger)
		endmethod

		method onDeath takes unit target returns nothing
			call RemoveUnitAbility(target, this)
		endmethod

		method onTick takes nothing returns nothing
			unit target = GetTriggerUnit()
			integer currentTick = GetGameTurnTick()
			if (currentTick >= this.expireTick) then
				call RemoveUnitAbility(target, this)
			endif
		endmethod

		method getDurationMax takes nothing returns real
			return this.duration
		endmethod

		method getDurationRemaining takes unit target returns real
			integer currentTick = GetGameTurnTick()
			integer remaining = this.expireTick - currentTick
			if remaining < 0 then
				remaining = 0
			endif
			return remaining * au_SIMULATION_STEP_TIME
		endmethod
	endstruct

	struct BuffStun extends BuffTimed
		public static method create takes integer aliasId, real duration returns thistype
			return .allocate(aliasId, duration)
		endmethod

		method onBuffAdd takes unit target returns nothing
			call UnitAddType(target, UNIT_TYPE_STUNNED)
		endmethod

		method onBuffRemove takes unit target returns nothing
			call UnitRemoveType(target, UNIT_TYPE_STUNNED)
		endmethod

		method isTimedLifeBar takes nothing returns boolean
			return false
		endmethod
	endstruct

	struct BuffTimedSlow extends BuffTimed
		nonstackingstatbonus movementSpeedBonus
		nonstackingstatbonus attackSpeedBonus

		public static method create takes integer aliasId, real duration, string stackingKey, real attackSpeedReductionPercent, real movementSpeedReductionPercent returns thistype
			thistype this = .allocate(aliasId, duration)
			this.attackSpeedBonus = CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_ATKSPD, stackingKey, -attackSpeedReductionPercent)
			this.movementSpeedBonus = CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_MVSPDPCT, stackingKey, -movementSpeedReductionPercent)
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
			AddUnitNonStackingStatBonus(target, this.movementSpeedBonus)
			AddUnitNonStackingStatBonus(target, this.attackSpeedBonus)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_ATKSPD)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_MVSPDPCT)
		endmethod

		method onBuffRemove takes unit target returns nothing
			RemoveUnitNonStackingStatBonus(target, this.movementSpeedBonus)
			RemoveUnitNonStackingStatBonus(target, this.attackSpeedBonus)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_ATKSPD)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_MVSPDPCT)
		endmethod

		method isTimedLifeBar takes nothing returns boolean
			return false
		endmethod
	endstruct

	struct BuffTimedStatBonus extends BuffTimed
		nonstackingstatbonus statBonus
		nonstackingstatbonustype statBonusType

		public static method create takes integer aliasId, real duration, nonstackingstatbonus statBonus returns thistype
			thistype this = .allocate(aliasId, duration)
			this.statBonus = statBonus
			// (NOTE: in the long distant future, you could imagine that we might call "GetNonStackingStatBonusType(...)"
			//   in onBuffAdd and onBuffRemove instead of caching it here. But for now, in my current handle system,
			//   the "ConvertXYZ" call to create NON_STACKING_STAT_BONUS_TYPE_DEF constant, and the getter method,
			//   both separately allocate a handle wrapper)
			this.statBonusType = GetNonStackingStatBonusType(statBonus)
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
			AddUnitNonStackingStatBonus(target, this.statBonus)
			RecomputeStatBonusesOnUnit(target, this.statBonusType)
		endmethod

		method onBuffRemove takes unit target returns nothing
			RemoveUnitNonStackingStatBonus(target, this.statBonus)
			RecomputeStatBonusesOnUnit(target, this.statBonusType)
		endmethod

		method isTimedLifeBar takes nothing returns boolean
			return false
		endmethod
	endstruct

	struct BuffTimedLife extends BuffTimed
		boolean explode

		public static method create takes integer aliasId, real duration, boolean explode returns thistype
			local thistype this = .allocate(aliasId, duration)
			this.explode = explode
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
			if this.explode then
				call SetUnitExploded(target, true)
				call SetUnitExplodeOnDeathBuffId(target, this.getAliasId())
			endif
		endmethod

		method onBuffRemove takes unit target returns nothing
			call KillUnit(target)
		endmethod

		method isTimedLifeBar takes nothing returns boolean
			return true
		endmethod
	endstruct
endlibrary

//=====================================================
// AbilityFieldDefaults                                        
//=====================================================
// These are some expected/default names for ability
// fields. Note that these are strings; these do not
// at all prohibit you from looking up other ability
// fields with other names beyond these.
library AbilityFieldDefaults requires AbilitiesCommonLegacy
	globals
		constant string ABILITY_FIELD_CODE                              = "code"
		constant string ABILITY_FIELD_DATA_A                            = "DataA"
		constant string ABILITY_FIELD_DATA_B                            = "DataB"
		constant string ABILITY_FIELD_DATA_C                            = "DataC"
		constant string ABILITY_FIELD_DATA_D                            = "DataD"
		constant string ABILITY_FIELD_DATA_E                            = "DataE"
		constant string ABILITY_FIELD_DATA_F                            = "DataF"
		constant string ABILITY_FIELD_DATA_G                            = "DataG"
		constant string ABILITY_FIELD_DATA_H                            = "DataH"
		constant string ABILITY_FIELD_UNIT_ID                           = "UnitID"
		constant string ABILITY_FIELD_TARGETS_ALLOWED                   = "targs"
		constant string ABILITY_FIELD_LEVELS                            = "levels"
		constant string ABILITY_FIELD_CAST_RANGE                        = "Rng"
		constant string ABILITY_FIELD_DURATION                          = "Dur"
		constant string ABILITY_FIELD_HERO_DURATION                     = "HeroDur"
		constant string ABILITY_FIELD_AREA                              = "Area"
		constant string ABILITY_FIELD_MANA_COST                         = "Cost"
		constant string ABILITY_FIELD_COOLDOWN                          = "Cool"
		constant string ABILITY_FIELD_CASTING_TIME                      = "Cast"
		constant string ABILITY_FIELD_AREA_OF_EFFECT                    = "Area"
		constant string ABILITY_FIELD_BUFF                              = "BuffID"
		constant string ABILITY_FIELD_EFFECT                            = "EfctID"
		constant string ABILITY_FIELD_ANIM_NAMES                        = "Animnames"
		constant string ABILITY_FIELD_PROJECTILE_SPEED                  = "Missilespeed"
		constant string ABILITY_FIELD_PROJECTILE_HOMING_ENABLED         = "MissileHoming"
		constant string ABILITY_FIELD_LIGHTNING                         = "LightningEffect"
		constant string ABILITY_FIELD_REQUIRED_LEVEL                    = "reqLevel"
		constant string ABILITY_FIELD_REQUIRED_LEVEL_SKIP               = "levelSkip"
		constant string ABILITY_FIELD_CHECK_DEPENDENCIES                = "checkDep"
		constant string ABILITY_FIELD_REQUIREMENTS                      = "Requires"
		constant string ABILITY_FIELD_REQUIREMENT_LEVELS                = "Requiresamount"
	endglobals

	function GetGameObjectIDSmart takes gameobject editorData, string metaKey, integer level, integer index returns integer
		string levelKey
		if level == -1 then
			levelKey = ""
		else
			levelKey = I2S(level)
		endif
		return GetGameObjectFieldAsID(editorData, metaKey + levelKey, index)
	endfunction

	function GetGameObjectBuffID takes gameobject editorData, integer level, integer index returns integer
		return GetGameObjectIDSmart(editorData, ABILITY_FIELD_BUFF, level, index)
	endfunction

	function GetGameObjectEffectID takes gameobject editorData, integer level returns integer
		return GetGameObjectIDSmart(editorData, ABILITY_FIELD_EFFECT, level, 0)
	endfunction

	function GetGameObjectLightningID takes gameobject editorData, integer index returns integer
		return GetGameObjectIDSmart(editorData, ABILITY_FIELD_LIGHTNING, -1, index)
	endfunction
endlibrary

//=====================================================
// AnimationTokenAPI                              
//=====================================================
// This API defines some utilities for selecting the
// animations on a unit. Some of the enum values
// are the same or extremely similar to the ones
// on Reforged and its prepatches, but these values
// are not exactly the same.
//
// By being different, and differently declared,
// we can be sure that these values are available even
// if our Warsmash user provides us with an older
// Warcraft III patch from before the Reforged
// prepatches that does not declare the list of
// "animtype" and "subanimtype" as those guys call them,
// or in the Warsmash naming convention what we call
// "primarytag" and "secondarytag". 
library AnimationTokensAPI
	type primarytag extends handle
	type secondarytag extends handle
	// NOTE: the primarytags and secondarytags are really just
	// the Java type of EnumSet<PrimaryTag> and EnumSet<SecondaryTag>
	// so their natives are duplicates of the "targettypes" enums,
	// which are again just EnumSet<TargetType>. If we could
	// perhaps create a generic EnumSet type in the future,
	// maybe we could express the same thing here in user script
	type primarytags extends handle
	type secondarytags extends handle
	
	private native ConvertPrimaryTag takes integer x returns primarytag
	private native ConvertSecondaryTag takes integer x returns secondarytag
	
	native CreatePrimaryTags takes nothing returns primarytags
	native PrimaryTagsAdd takes primarytags whichSet, primarytag whichType returns boolean
	native PrimaryTagsRemove takes primarytags whichSet, primarytag whichType returns boolean
	native PrimaryTagsContains takes primarytags whichSet, primarytag whichType returns boolean
	native PrimaryTagsSize takes primarytags whichSet returns integer
	native PrimaryTagsAny takes primarytags whichSet returns primarytag /* returns null if empty, else returns one of the values */
	native DestroyPrimaryTags takes primarytags whichSet returns nothing
	
	native CreateSecondaryTags takes nothing returns secondarytags
	native SecondaryTagsAdd takes secondarytags whichSet, secondarytag whichType returns boolean
	native SecondaryTagsRemove takes secondarytags whichSet, secondarytag whichType returns boolean
	native SecondaryTagsContains takes secondarytags whichSet, secondarytag whichType returns boolean
	native SecondaryTagsSize takes secondarytags whichSet returns integer
	native SecondaryTagsAny takes secondarytags whichSet returns secondarytag /* returns null if empty, else returns one of the values */
	native DestroySecondaryTags takes secondarytags whichSet returns nothing
	
	// This function splits up a string of the form "Attack Spell Channel Alternate - 2" into bits,
	// and then adds ATTACK and SPELL enum tags to the primary set, and CHANNEL and ALTERNATE tags to the
	// secondary set, for example. This allows us to later accurately instruct a unit to play
	// the animation(s) designated by the string, but in a way that is fast and does not
	// require us to process text, so we can spam it a lot during gameplay.
	// (If the "primaryTags" and "secondaryTags" arguments were non-empty prior to calling
	//  this function, all of their contents are removed and reset before the parsing/populating)
	native PopulateTags takes primarytags primaryTags, secondarytags secondaryTags, string animationSelector returns nothing
	
	struct PrimaryTags extends primarytags
		public static constant primarytag ATTACK 		= ConvertPrimaryTag(0)
		public static constant primarytag BIRTH 		= ConvertPrimaryTag(1)
		// public static constant primarytag CINEMATIC 	= ConvertPrimaryTag(?)
		public static constant primarytag DEATH 		= ConvertPrimaryTag(2)
		public static constant primarytag DECAY 		= ConvertPrimaryTag(3)
		public static constant primarytag DISSIPATE 	= ConvertPrimaryTag(4)
		public static constant primarytag MORPH 		= ConvertPrimaryTag(5)
		public static constant primarytag PORTRAIT 		= ConvertPrimaryTag(6)
		public static constant primarytag SLEEP 		= ConvertPrimaryTag(7)
		//public static constant primarytag SPELL 		= ConvertPrimaryTag(?)
		public static constant primarytag STAND 		= ConvertPrimaryTag(8)
		public static constant primarytag WALK 			= ConvertPrimaryTag(9)
		
		method add takes primarytag x returns boolean
			return PrimaryTagsAdd(this, x)
		endmethod
		
		method remove takes primarytag x returns boolean
			return PrimaryTagsRemove(this, x) 
		endmethod
		
		method contains takes primarytag x returns boolean
			return PrimaryTagsContains(this, x)
		endmethod
		
		method size takes nothing returns integer
			return PrimaryTagsSize(this)
		endmethod
		
		method isEmpty takes nothing returns boolean
			return size() == 0
		endmethod
		
		method any takes nothing returns primarytag
			return PrimaryTagsAny(this)
		endmethod
	endstruct
	
	
	struct SecondaryTags extends secondarytags
		public static constant secondarytag ALTERNATE   = ConvertSecondaryTag(0)
		public static constant secondarytag ALTERNATEEX = ConvertSecondaryTag(1)
		public static constant secondarytag BONE        = ConvertSecondaryTag(2)
		public static constant secondarytag CHAIN       = ConvertSecondaryTag(3)
		public static constant secondarytag CHANNEL     = ConvertSecondaryTag(4)
		public static constant secondarytag COMPLETE    = ConvertSecondaryTag(5)
		public static constant secondarytag CRITICAL    = ConvertSecondaryTag(6)
		public static constant secondarytag DEFEND      = ConvertSecondaryTag(7)
		public static constant secondarytag DRAIN       = ConvertSecondaryTag(8)
		public static constant secondarytag EATTREE     = ConvertSecondaryTag(9)
		public static constant secondarytag FAST        = ConvertSecondaryTag(10)
		public static constant secondarytag FILL        = ConvertSecondaryTag(11)
		public static constant secondarytag FLAIL       = ConvertSecondaryTag(12)
		public static constant secondarytag FLESH       = ConvertSecondaryTag(13)
		public static constant secondarytag FIFTH       = ConvertSecondaryTag(14)
		public static constant secondarytag FIRE        = ConvertSecondaryTag(15)
		public static constant secondarytag FIRST       = ConvertSecondaryTag(16)
		public static constant secondarytag FIVE        = ConvertSecondaryTag(17)
		public static constant secondarytag FOUR        = ConvertSecondaryTag(18)
		public static constant secondarytag FOURTH      = ConvertSecondaryTag(19)
		public static constant secondarytag GOLD        = ConvertSecondaryTag(20)
		public static constant secondarytag HIT         = ConvertSecondaryTag(21)
		public static constant secondarytag LARGE       = ConvertSecondaryTag(22)
		public static constant secondarytag LEFT        = ConvertSecondaryTag(23)
		public static constant secondarytag LIGHT       = ConvertSecondaryTag(24)
		public static constant secondarytag LOOPING     = ConvertSecondaryTag(25)
		public static constant secondarytag LUMBER      = ConvertSecondaryTag(26)
		public static constant secondarytag MEDIUM      = ConvertSecondaryTag(27)
		public static constant secondarytag MODERATE    = ConvertSecondaryTag(28)
		public static constant secondarytag OFF         = ConvertSecondaryTag(29)
		public static constant secondarytag ONE         = ConvertSecondaryTag(30)
		public static constant secondarytag PUKE        = ConvertSecondaryTag(31)
		public static constant secondarytag READY       = ConvertSecondaryTag(32)
		public static constant secondarytag RIGHT       = ConvertSecondaryTag(33)
		public static constant secondarytag SECOND      = ConvertSecondaryTag(34)
		public static constant secondarytag SEVERE      = ConvertSecondaryTag(35)
		public static constant secondarytag SLAM        = ConvertSecondaryTag(36)
		public static constant secondarytag SMALL       = ConvertSecondaryTag(37)
		public static constant secondarytag SPIKED      = ConvertSecondaryTag(38)
		public static constant secondarytag SPIN        = ConvertSecondaryTag(39)
		public static constant secondarytag SPELL       = ConvertSecondaryTag(40)
		public static constant secondarytag CINEMATIC   = ConvertSecondaryTag(41)
		public static constant secondarytag SWIM        = ConvertSecondaryTag(42)
		public static constant secondarytag TALK        = ConvertSecondaryTag(43)
		public static constant secondarytag THIRD       = ConvertSecondaryTag(44)
		public static constant secondarytag THREE       = ConvertSecondaryTag(45)
		public static constant secondarytag THROW       = ConvertSecondaryTag(46)
		public static constant secondarytag TWO         = ConvertSecondaryTag(47)
		public static constant secondarytag TURN        = ConvertSecondaryTag(48)
		public static constant secondarytag VICTORY     = ConvertSecondaryTag(49)
		public static constant secondarytag WORK        = ConvertSecondaryTag(50)
		public static constant secondarytag WOUNDED     = ConvertSecondaryTag(51)
		public static constant secondarytag UPGRADE     = ConvertSecondaryTag(52)
		
		method add takes secondarytag x returns boolean
			return SecondaryTagsAdd(this, x)
		endmethod
		
		method remove takes secondarytag x returns boolean
			return SecondaryTagsRemove(this, x) 
		endmethod
		
		method contains takes secondarytag x returns boolean
			return SecondaryTagsContains(this, x)
		endmethod
		
		method size takes nothing returns integer
			return SecondaryTagsSize(this)
		endmethod
		
		method isEmpty takes nothing returns boolean
			return size() == 0
		endmethod
		
		method any takes nothing returns secondarytag
			return SecondaryTagsAny(this)
		endmethod
	endstruct
	
	
endlibrary

library AbilitySpellBaseTypes requires GenericAbilityBaseTypes, AbilityFieldDefaults, AnimationTokensAPI, AbilityTargetAPI

	module AbilitySpell
		integer manaCost
		real castRange
		real cooldown
		real castingTime
		targettypes targetsAllowed
		primarytag castingPrimaryTag
		SecondaryTags castingSecondaryTags
		real duration
		real heroDuration
		real areaOfEffect
		
		method innerPopulate takes gameobject editorData, integer level returns nothing
			this.manaCost = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_MANA_COST + I2S(level), 0)
			call SetOrderButtonManaCost(this.abilityButton, manaCost)
			this.castRange = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_CAST_RANGE + I2S(level), 0)
			this.cooldown = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_COOLDOWN + I2S(level), 0)
			this.castingTime = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_CASTING_TIME + I2S(level), 0)
			this.areaOfEffect = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_AREA_OF_EFFECT + I2S(level), 0)
			
			integer requiredLevel = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_REQUIRED_LEVEL + I2S(level), 0)
			if this.targetsAllowed != null then
				// if we are leveling up the skill, cleanup the previous targeting list
				call DestroyTargetTypes(this.targetsAllowed)
			endif
			this.targetsAllowed = ParseTargetTypes(GetGameObjectFieldAsStringList(editorData, ABILITY_FIELD_TARGETS_ALLOWED + I2S(level)))
			if ((requiredLevel < 6) and not this.isPhysical() and not this.isUniversal()) then
				call TargetTypesAdd(this.targetsAllowed, TARGET_TYPE_NON_MAGIC_IMMUNE)
			endif
			if this.isPhysical() and not this.isUniversal() then
				call TargetTypesAdd(this.targetsAllowed, TARGET_TYPE_NON_ETHEREAL)
			endif
			
			string animNames = GetGameObjectField(editorData, ABILITY_FIELD_ANIM_NAMES)
			PrimaryTags primaryTags = PrimaryTags.create()
			this.castingSecondaryTags = SecondaryTags.create()
			call PopulateTags(primaryTags, this.castingSecondaryTags, animNames)
			this.castingPrimaryTag = primaryTags.any()
			primaryTags.destroy()
			if (this.castingSecondaryTags.isEmpty()) then
				this.castingSecondaryTags.add(SecondaryTags.SPELL)
			endif
			this.duration = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DURATION + I2S(level), 0)
			this.heroDuration = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_HERO_DURATION + I2S(level), 0)
			
			populateData(editorData, level)
		endmethod
		
		method checkUsable takes unit caster returns nothing
			integer cooldownCode = this.getCodeId() // I guess if you wanted stacking, change this to alias ID?
			real cooldownRemaining = GetUnitAbilityCooldownRemaining(caster, cooldownCode)
			if cooldownRemaining > 0 then
				real cooldownLengthDisplay = GetUnitAbilityCooldownLengthDisplay(caster, cooldownCode)
				call FailUsableCheckOnCooldown(this.abilityButton, cooldownRemaining, cooldownLengthDisplay)
			elseif GetUnitState(caster, UNIT_STATE_MANA) < this.manaCost then
				// the special "not enough mana" string turns the icon blue
				call FailUsableCheckWithMessage(this.abilityButton, COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA)
			else
				call checkUsableSpell(caster)
			endif
		endmethod
		
		method checkUsableSpell takes unit caster returns nothing
			call PassUsableCheck(this.abilityButton)
		endmethod
		
		method getDurationForTarget takes unit targetUnit returns real
			if ((targetUnit != null) and IsUnitType(targetUnit, UNIT_TYPE_HERO)) then
				return this.heroDuration
			endif
			return this.duration
		endmethod

		// NOTE: maybe the below "get" methods are pointless, but it might
		//       allow some custom ability in the future to override the
		//       method to make the value dynamic based on when it is
		//       requested or something
		method getCastRange takes nothing returns real
			return this.castRange
		endmethod

		method getManaCost takes nothing returns real
			return this.manaCost
		endmethod

		method getCooldown takes nothing returns real
			return this.cooldown
		endmethod
	
		method getDuration takes nothing returns real
			return this.duration
		endmethod

		method getTargetsAllowed takes nothing returns targettypes
			return this.targetsAllowed
		endmethod

		method getCastingTime takes nothing returns real
			return this.castingTime
		endmethod

		method setCastingTime takes real castingTime returns nothing
			this.castingTime = castingTime
		endmethod

		method getAreaOfEffect takes nothing returns real
			return this.areaOfEffect
		endmethod

		method setCastingPrimaryTag takes primarytag whichTag returns nothing
			this.castingPrimaryTag = whichTag
		endmethod

		// required by native API (see "interface Ability")
		constant method getAbilityCategory takes nothing returns abilitycategory
			return ABILITY_CATEGORY_SPELL
		endmethod
	endmodule

	private struct AbilityTargetStillAliveAndTargetableVisitor extends AbilityTargetVisitor
		unit caster
		targettypes targetsAllowed
		boolean result
		
		method reset takes unit caster, targettypes targetsAllowed returns thistype
			set this.caster = caster
			set this.targetsAllowed = targetsAllowed
			return this
		endmethod

		method visitUnit takes unit target returns nothing
			result = UnitAlive(target) and not IsUnitHidden(target) and GetTargetError(target, caster, targetsAllowed) == null 
		endmethod
		method visitItem takes item target returns nothing
			result = WidgetAlive(target) and IsItemVisible(target) and GetTargetError(target, caster, targetsAllowed) == null 
		endmethod
		method visitDest takes destructable target returns nothing
			result = WidgetAlive(target) and GetTargetError(target, caster, targetsAllowed) == null
		endmethod
		method visitLoc takes location target returns nothing
			result = true
		endmethod

		static constant thistype INSTANCE = thistype.create()

		static method evaluate takes unit caster, targettypes targetsAllowed, abilitytarget target returns boolean
			call AbilityTargetAcceptVisitor(target, INSTANCE.reset(caster, targetsAllowed))
			return INSTANCE.result
		endmethod

	endstruct
	private struct AbilityTargetTargetableVisitor extends AbilityTargetVisitor
		unit caster
		targettypes targetsAllowed
		boolean result
		
		method reset takes unit caster, targettypes targetsAllowed returns thistype
			set this.caster = caster
			set this.targetsAllowed = targetsAllowed
			return this
		endmethod

		method visitUnit takes unit target returns nothing
			result = GetTargetError(target, caster, targetsAllowed) == null 
		endmethod
		method visitItem takes item target returns nothing
			result = GetTargetError(target, caster, targetsAllowed) == null 
		endmethod
		method visitDest takes destructable target returns nothing
			result = GetTargetError(target, caster, targetsAllowed) == null
		endmethod
		method visitLoc takes location target returns nothing
			result = true
		endmethod

		static constant thistype INSTANCE = thistype.create()

		static method evaluate takes unit caster, targettypes targetsAllowed, abilitytarget target returns boolean
			call AbilityTargetAcceptVisitor(target, INSTANCE.reset(caster, targetsAllowed))
			return INSTANCE.result
		endmethod

	endstruct
	
	module BehaviorSpellTarget
		AbilitySpellTargetInterface sourceAbility
		integer castStartTick
		boolean doneEffect
		boolean channeling

		public static method create takes unit whichUnit, AbilitySpellTargetInterface abil returns thistype
			local thistype this = .allocate(whichUnit)
			set this.sourceAbility = abil
			return this
		endmethod

		method reset takes abilitytarget target returns thistype
			this.castStartTick = 0
			this.doneEffect = false
			this.channeling = false
			return this.innerReset(target)
		endmethod

		method isWithinRange takes nothing returns boolean
			if (this.channeling) then
				return true // dont run away after channeling begins
			endif
			return UnitCanReach(getUnit(), getTarget(), sourceAbility.getCastRange())
		endmethod

		method endChannel takes boolean interrupted returns nothing
			local unit behavingUnit = getUnit()
			call UnitStopSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
			call this.sourceAbility.doChannelEnd(behavingUnit, getTarget(), interrupted)
		endmethod

		method update takes boolean withinFacingWindow returns behavior
			unit behavingUnit = getUnit()
			call SetUnitAnimationByTag(behavingUnit, false, this.sourceAbility.castingPrimaryTag, this.sourceAbility.castingSecondaryTags, 1.0, true)
			integer gameTurnTick = GetGameTurnTick()
			if (this.castStartTick == 0) then
				this.castStartTick = gameTurnTick
			endif
			integer ticksSinceCast = gameTurnTick - this.castStartTick
			integer castPointTicks = R2I(GetUnitCastPoint(behavingUnit) / au_SIMULATION_STEP_TIME)
			integer backswingTicks = R2I(GetUnitCastBackswingPoint(behavingUnit) / au_SIMULATION_STEP_TIME)
			if ((ticksSinceCast >= castPointTicks) or (ticksSinceCast >= backswingTicks)) then
				boolean wasEffectDone = this.doneEffect
				boolean wasChanneling = this.channeling
				if (not wasEffectDone) then
					this.doneEffect = true
					// NOTE: in the future, maybe call "checkUsable" here instead of a custom "charge mana"
					// function, then just literally deduct the mana or something. That would
					// require "checkUsable" to be less stupid and not call Pass/Fail natives,
					// and to instead work the same in jass as java
					if (not ChargeMana(behavingUnit, this.sourceAbility.getManaCost())) then
						// if the unit had enough mana to click the icon of this, but was mana-drained while walking
						// from over there to here before completing the cast, we must pop up the error
						// independent of "this.checkUsable"
						call ShowInterfaceError(GetOwningPlayer(behavingUnit), COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA)
						return UnitPollNextOrderBehavior(behavingUnit)
					endif
					call StartUnitAbilityCooldown(behavingUnit, this.sourceAbility.getCodeId(), this.sourceAbility.getCooldown())
					this.channeling = this.sourceAbility.doEffect(behavingUnit, getTarget())
					if (this.channeling) then
						call UnitLoopSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
					else
						call UnitSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
					endif
				endif
				if this.channeling then
					set this.channeling = this.sourceAbility.doChannelTick(behavingUnit, getTarget())
				endif
				if wasEffectDone and wasChanneling and not this.channeling then
					call this.endChannel(false)
				endif
			endif
			if ((ticksSinceCast >= backswingTicks) and not this.channeling) then
				return UnitPollNextOrderBehavior(behavingUnit)
			endif
			return this
		endmethod

		constant method getHighlightOrderId takes nothing returns integer
			return GetOrderButtonOrderId(this.sourceAbility.abilityButton)
		endmethod

		constant method interruptable takes nothing returns boolean
			return true
		endmethod

		constant method getBehaviorCategory takes nothing returns behaviorcategory
			return BEHAVIOR_CATEGORY_SPELL
		endmethod

		private method checkEndChannel takes boolean interrupted returns nothing
			if (this.channeling) then
				this.channeling = false
				endChannel(interrupted)
			endif
		endmethod

		method begin takes nothing returns nothing
		endmethod

		method end takes boolean interrupted returns nothing
			checkEndChannel(interrupted)
		endmethod

		method endMove takes boolean interrupted returns nothing
			checkEndChannel(interrupted)
		endmethod

		method updateOnInvalidTarget takes nothing returns behavior
			return UnitPollNextOrderBehavior(getUnit())
		endmethod

		method isTargetStillValid takes nothing returns boolean
			if this.doneEffect then
				// allows us to channel "at" something that died.
				// If you hit a bug with that, then fix it here.
				return true
			endif
			return AbilityTargetStillAliveAndTargetableVisitor.evaluate(getUnit(), sourceAbility.targetsAllowed, getTarget())
		endmethod

		method resetBeforeMoving takes nothing returns nothing
			this.castStartTick = 0
		endmethod
	endmodule

	scope TargetWidget
		private interface AbilitySpellInterface extends AbstractGenericActiveAbilityTargetWidget
			method populateData takes gameobject editorData, integer level returns nothing
			method doEffect takes unit caster, abilitytarget target returns boolean
			method doChannelTick takes unit caster, abilitytarget target returns boolean defaults false
			method doChannelEnd takes unit caster, abilitytarget target returns nothing defaults nothing
		endinterface
		
		private struct AbilitySpellTargetInterface extends AbilitySpellInterface
			implement AbilitySpell
		endstruct

		private struct BehaviorSpellImpl extends NativeAbstractRangedBehavior
			implement BehaviorSpellTarget
		endstruct

		struct AbilitySpellTargetWidget extends AbilitySpellTargetInterface
			BehaviorSpellImpl behavior

			method onAdd takes unit whichUnit returns nothing
				this.behavior = BehaviorSpellImpl.create(whichUnit, this)
			endmethod

			method checkSpellTarget takes unit caster, widget target returns nothing
				call PassTargetCheck(this.abilityButton, target)
			endmethod

			private method checkTargetWidget takes unit caster, widget target returns nothing
				string targetError = GetTargetError(target, caster, this.targetsAllowed)
				if (targetError != null) then
					call FailTargetCheckWithMessage(this.abilityButton, targetError)
				else
					if (not IsUnitMovementDisabled(caster) or UnitCanReach(caster, target, getCastRange())) then
						call this.checkSpellTarget(caster, target)
					else
						call FailTargetCheckWithMessage(COMMAND_STRING_ERROR_KEY_TARGET_IS_OUTSIDE_RANGE)
					endif
				endif
			endmethod

			method checkTargetUnit takes unit caster, unit target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginUnit takes unit caster, unit target returns behavior
				return behavior.reset(target)
			endmethod
			
			method checkTargetItem takes unit caster, item target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginItem takes unit caster, item target returns behavior
				return behavior.reset(target)
			endmethod
			
			method checkTargetDestructable takes unit caster, destructable target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginDestructable takes unit caster, destructable target returns behavior
				return behavior.reset(target)
			endmethod
		endstruct
	endscope

	scope TargetLocation
		private interface AbilitySpellInterface extends AbstractGenericActiveAbilityTargetLocation
			method populateData takes gameobject editorData, integer level returns nothing
			method doEffect takes unit caster, abilitytarget target returns boolean
			method doChannelTick takes unit caster, abilitytarget target returns boolean defaults false
			method doChannelEnd takes unit caster, abilitytarget target returns nothing defaults nothing
		endinterface
		
		private struct AbilitySpellTargetInterface extends AbilitySpellInterface
			implement AbilitySpell
		endstruct

		private struct BehaviorSpellImpl extends NativeAbstractRangedBehavior
			implement BehaviorSpellTarget
		endstruct

		struct AbilitySpellTargetLocation extends AbilitySpellTargetInterface
			BehaviorSpellImpl behavior

			method onAdd takes unit whichUnit returns nothing
				this.behavior = BehaviorSpellImpl.create(whichUnit, this)
			endmethod

			method checkSpellTarget takes unit caster, location target returns nothing
				call PassTargetCheck(this.abilityButton, target)
			endmethod

			method checkTargetLoc takes unit caster, location target returns nothing
				if (not IsUnitMovementDisabled(caster) or UnitCanReach(caster, target, getCastRange())) then
					call this.checkSpellTarget(caster, target)
				else
					call FailTargetCheckWithMessage(COMMAND_STRING_ERROR_KEY_TARGET_IS_OUTSIDE_RANGE)
				endif
			endmethod

			method beginLoc takes unit caster, location target returns behavior
				return behavior.reset(target)
			endmethod
		endstruct
	endscope

	scope TargetWidgetOrLocation
		private interface AbilitySpellInterface extends AbstractGenericActiveAbilityTargetWidgetOrLocation
			method populateData takes gameobject editorData, integer level returns nothing
			method doEffect takes unit caster, abilitytarget target returns boolean
			method doChannelTick takes unit caster, abilitytarget target returns boolean defaults false
			method doChannelEnd takes unit caster, abilitytarget target returns nothing defaults nothing
		endinterface
		
		private struct AbilitySpellTargetInterface extends AbilitySpellInterface
			implement AbilitySpell
		endstruct

		private struct BehaviorSpellImpl extends NativeAbstractRangedBehavior
			implement BehaviorSpellTarget
		endstruct

		struct AbilitySpellTargetWidgetOrLocation extends AbilitySpellTargetInterface
			BehaviorSpellImpl behavior

			method onAdd takes unit whichUnit returns nothing
				this.behavior = BehaviorSpellImpl.create(whichUnit, this)
			endmethod

			method checkSpellTarget takes unit caster, abilitytarget target returns nothing
				call PassTargetCheck(this.abilityButton, target)
			endmethod

			private method checkTargetWidget takes unit caster, widget target returns nothing
				string targetError = GetTargetError(target, caster, this.targetsAllowed)
				if (targetError != null) then
					call FailTargetCheckWithMessage(this.abilityButton, targetError)
				else
					if (not IsUnitMovementDisabled(caster) or UnitCanReach(caster, target, getCastRange())) then
						call this.checkSpellTarget(caster, target)
					else
						call FailTargetCheckWithMessage(COMMAND_STRING_ERROR_KEY_TARGET_IS_OUTSIDE_RANGE)
					endif
				endif
			endmethod

			method checkTargetUnit takes unit caster, unit target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginUnit takes unit caster, unit target returns behavior
				return behavior.reset(target)
			endmethod
			
			method checkTargetItem takes unit caster, item target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginItem takes unit caster, item target returns behavior
				return behavior.reset(target)
			endmethod
			
			method checkTargetDestructable takes unit caster, destructable target returns nothing
				checkTargetWidget(caster, target)
			endmethod
			
			method beginDestructable takes unit caster, destructable target returns behavior
				return behavior.reset(target)
			endmethod

			method checkTargetLoc takes unit caster, location target returns nothing
				if (not IsUnitMovementDisabled(caster) or UnitCanReach(caster, target, getCastRange())) then
					call this.checkSpellTarget(caster, target)
				else
					call FailTargetCheckWithMessage(COMMAND_STRING_ERROR_KEY_TARGET_IS_OUTSIDE_RANGE)
				endif
			endmethod

			method beginLoc takes unit caster, location target returns behavior
				return behavior.reset(target)
			endmethod
		endstruct
	endscope

	scope NoTarget
		private interface AbilitySpellInterface extends AbstractGenericActiveAbilityNoTarget
			method populateData takes gameobject editorData, integer level returns nothing
			method doEffect takes unit caster, abilitytarget target returns boolean
			method doChannelTick takes unit caster, abilitytarget target returns boolean defaults false
			method doChannelEnd takes unit caster, abilitytarget target returns nothing defaults nothing
		endinterface
		
		private struct AbilitySpellTargetInterface extends AbilitySpellInterface
			implement AbilitySpell
		endstruct
		
		private struct BehaviorSpellNoTarget extends Behavior
			unit behavingUnit
			AbilitySpellTargetInterface sourceAbility
			integer castStartTick
			boolean doneEffect
			boolean channeling

			public static method create takes unit whichUnit, AbilitySpellTargetInterface abil returns thistype
				local thistype this = .allocate()
				set this.behavingUnit = whichUnit
				set this.sourceAbility = abil
				return this
			endmethod

			method reset takes nothing returns thistype
				this.castStartTick = 0
				this.doneEffect = false
				this.channeling = true
				return this
			endmethod

			method endChannel takes boolean interrupted returns nothing
				call UnitStopSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
				call this.sourceAbility.doChannelEnd(behavingUnit, null, interrupted)
			endmethod

			method update takes nothing returns behavior
				call SetUnitAnimationByTag(behavingUnit, false, this.sourceAbility.castingPrimaryTag, this.sourceAbility.castingSecondaryTags, 1.0, true)
				integer gameTurnTick = GetGameTurnTick()
				if (this.castStartTick == 0) then
					this.castStartTick = gameTurnTick
				endif
				integer ticksSinceCast = gameTurnTick - this.castStartTick
				integer castPointTicks = R2I(GetUnitCastPoint(behavingUnit) / au_SIMULATION_STEP_TIME)
				integer backswingTicks = R2I(GetUnitCastBackswingPoint(behavingUnit) / au_SIMULATION_STEP_TIME)
				if ((ticksSinceCast >= castPointTicks) or (ticksSinceCast >= backswingTicks)) then
					boolean wasEffectDone = this.doneEffect
					boolean wasChanneling = this.channeling
					if (not wasEffectDone) then
						this.doneEffect = true
						// NOTE: in the future, maybe call "checkUsable" here instead of a custom "charge mana"
						// function, then just literally deduct the mana or something. That would
						// require "checkUsable" to be less stupid and not call Pass/Fail natives,
						// and to instead work the same in jass as java
						if (not ChargeMana(behavingUnit, this.sourceAbility.getManaCost())) then
							// if the unit had enough mana to click the icon of this, but was mana-drained while walking
							// from over there to here before completing the cast, we must pop up the error
							// independent of "this.checkUsable"
							call ShowInterfaceError(GetOwningPlayer(behavingUnit), COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA)
							return UnitPollNextOrderBehavior(behavingUnit)
						endif
						call StartUnitAbilityCooldown(behavingUnit, this.sourceAbility.getCodeId(), this.sourceAbility.getCooldown())
						this.channeling = this.sourceAbility.doEffect(behavingUnit, null)
						if (this.channeling) then
							call UnitLoopSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
						else
							call UnitSpellSoundEffect(behavingUnit, this.sourceAbility.getAliasId())
						endif
					endif
					if this.channeling then
						set this.channeling = this.sourceAbility.doChannelTick(behavingUnit, null)
					endif
					if wasEffectDone and wasChanneling and not this.channeling then
						call this.endChannel(false)
					endif
				endif
				if ((ticksSinceCast >= backswingTicks) and not this.channeling) then
					return UnitPollNextOrderBehavior(behavingUnit)
				endif
				return this
			endmethod

			constant method getHighlightOrderId takes nothing returns integer
				return GetOrderButtonOrderId(this.sourceAbility.abilityButton)
			endmethod

			constant method interruptable takes nothing returns boolean
				return true
			endmethod

			constant method getBehaviorCategory takes nothing returns behaviorcategory
				return BEHAVIOR_CATEGORY_SPELL
			endmethod

			private method checkEndChannel takes boolean interrupted returns nothing
				if (this.channeling) then
					this.channeling = false
					endChannel(interrupted)
				endif
			endmethod

			method begin takes nothing returns nothing
			endmethod

			method end takes boolean interrupted returns nothing
				checkEndChannel(interrupted)
			endmethod

		endstruct

		struct AbilitySpellNoTarget extends AbilitySpellTargetInterface
			BehaviorSpellNoTarget behavior

			method onAdd takes unit whichUnit returns nothing
				this.behavior = BehaviorSpellNoTarget.create(whichUnit, this)
			endmethod

			method checkTarget takes unit caster returns nothing
				call PassTargetCheck(this.abilityButton, null)
			endmethod

			method begin takes unit caster returns behavior
				return behavior.reset()
			endmethod
		endstruct
	endscope

	scope InstantNoInterrupt
		private interface AbilitySpellInterface extends AbstractGenericActiveAbilityInstant
			method populateData takes gameobject editorData, integer level returns nothing
			method doEffect takes unit caster, abilitytarget target returns boolean
		endinterface
		
		private struct AbilitySpellTargetInterface extends AbilitySpellInterface
			implement AbilitySpell
		endstruct
		
		struct AbilitySpellInstant extends AbilitySpellTargetInterface

			method checkTarget takes unit caster returns nothing
				call PassTargetCheck(this.abilityButton, null)
			endmethod
			
			method use takes unit caster returns nothing
				// NOTE: in the future, maybe call "checkUsable" here instead of a custom "charge mana"
				// function, then just literally deduct the mana or something. That would
				// require "checkUsable" to be less stupid and not call Pass/Fail natives,
				// and to instead work the same in jass as java
				if (not ChargeMana(caster, this.getManaCost())) then
					// if the unit had enough mana to click the icon of this, but was mana-drained while walking
					// from over there to here before completing the cast, we must pop up the error
					// independent of "this.checkUsable"
					call ShowInterfaceError(GetOwningPlayer(caster), COMMAND_STRING_ERROR_KEY_NOT_ENOUGH_MANA)
					return
				endif
				call StartUnitAbilityCooldown(caster, this.getCodeId(), this.getCooldown())
				call this.doEffect(caster, null)
			endmethod
		endstruct
	endscope

	scope Passive
		private interface AbilitySpellInterface extends AbstractGenericPassiveAbility
			method populateData takes gameobject editorData, integer level returns nothing
		endinterface
		
		struct AbilitySpellPassive extends AbilitySpellInterface
			implement AbilitySpell
		endstruct
	endscope
endlibrary

//==============================================
// MathUtils
//==============================================
library MathUtils
	// "struct math" is based on code by Wietlol
	// from here: https://www.hiveworkshop.com/threads/rounding-function-in-jass.288720/#post-3097507
	// (when I reached out to Wietlol, he agreed that it was fine to have this included in Warsmash,
	//  although he suggested these functions should be natives for performance; so I am taking
	//  this to mean that he provides this code to us under the terms of the Warsmash licensing
	//  or any similar software license of our choice)
	struct Math
		// Rounding
		static method floor takes real r returns real
		    if r < 0 then
			return -I2R(R2I(-r))
		    endif
		    return I2R(R2I(r))
		endmethod
	       
		static method ceil takes real r returns real
		    if floor(r) == r then
			return r
		    elseif r < 0 then
			return -(I2R(R2I(-r)) + 1.0)
		    endif
		    return I2R(R2I(r)) + 1.0
		endmethod
	       
		static method round takes real r returns real
		    if r > 0 then
			return I2R(R2I(r + 0.5))
		    endif
		    return I2R(R2I(r - 0.5))
		endmethod
	endstruct
endlibrary

//=====================================================
// ProjectileAPI                              
//=====================================================
// This can probably interoperate with the stuff from
// "ability builder" / local stores, but the base
// type projectile will not require them
library ProjectileAPI requires AbilityTargetAPI, AbilitiesCommonLegacy
	native SetProjectileDone takes projectile whichProjectile, boolean done returns nothing
	native SetProjectileReflected takes projectile whichProjectile, boolean reflected returns nothing
	native SetProjectileTargetUnit takes projectile whichProjectile, unit target returns nothing
	native SetProjectileTargetLoc takes projectile whichProjectile, location target returns nothing
	native IsProjectileReflected takes projectile whichProjectile returns boolean
	native GetProjectileX takes projectile whichProjectile returns real
	native GetProjectileY takes projectile whichProjectile returns real
	native GetProjectileSource takes projectile whichProjectile returns unit

	native CreateJassProjectile takes unit source, integer spellAlias, real launchX, real launchY, real launchFacing, real speed, boolean homing, abilitytarget target returns projectile

	interface Projectile extends projectile
		method onHit takes abilitytarget whichTarget returns nothing
		method onLaunch takes abilitytarget whichTarget returns nothing defaults nothing
	endinterface
endlibrary

library AuraAPI requires AbilityAPI, BuffAPI, MathUtils
	struct BuffAuraBase extends BuffTimed
		public constant static real AURA_BUFF_DECAY_TIME = 3.00
		public constant static integer AURA_BUFF_DECAY_TIME_TICKS = R2I(Math.ceil(AURA_BUFF_DECAY_TIME / au_SIMULATION_STEP_TIME))

		public static method create takes integer alias returns thistype
			return .allocate(alias, AURA_BUFF_DECAY_TIME)
		endmethod

		constant method getDurationMax takes nothing returns real
			return 0 // publish as if we are infinite duration so it wont flash black
		endmethod

		constant method getDurationRemaining takes unit target returns real
			return 0
		endmethod

		constant method isTimedLifeBar takes nothing returns boolean
			return false
		endmethod
	endstruct

	struct AbilityAuraBase extends AbilitySpellPassive
		public constant static real AURA_PERIODIC_CHECK_TIME = 2.00
		public constant static integer AURA_PERIODIC_CHECK_TIME_TICKS = R2I(Math.ceil(AURA_PERIODIC_CHECK_TIME / au_SIMULATION_STEP_TIME))

		integer buffId
		effect fx
		integer nextAreaCheck = 0
		trigger tickTrigger
		filterfunc enumFilter
		unit source

		public static method create takes integer alias, integer orderId returns thistype
			local thistype this = .allocate(alias, orderId)
			this.enumFilter = Filter(method this.unitInRangeEnum)
			return this
		endmethod

		public method destroy takes nothing returns nothing
			call DestroyBoolExpr(enumFilter)
			call this.deallocate()
		endmethod

		method populateAuraData takes gameobject editorData, integer level returns nothing
		endmethod

		method createBuff takes unit source, unit target returns BuffAuraBase
			return null
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
			this.buffId = GetGameObjectBuffID(editorData, level, 0)
			populateAuraData(editorData, level)
		endmethod

		method onAdd takes unit source returns nothing
			this.source = source
			this.fx = AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_TARGET, source, DEFAULT_ATTACH_POINTS)
			this.tickTrigger = CreateTrigger()
			call TriggerAddAction(this.tickTrigger, method this.onTick)
			call TriggerRegisterOnUnitTick(this.tickTrigger, source)
		endmethod

		method onRemove takes unit source returns nothing
			call DestroyEffect(this.fx)
			call DestroyTrigger(this.tickTrigger)
		endmethod

		private method unitInRangeEnum takes nothing returns boolean
			unit enumUnit = GetFilterUnit()
			if (GetUnitTargetError(enumUnit, source, getTargetsAllowed(), false) == null) then
				BuffAuraBase existingBuff = BuffAuraBase(GetUnitAbility(enumUnit, getBuffId()))
				boolean addNewBuff = false
				integer level = getLevel()
				if (existingBuff == null) then
					addNewBuff = true
				else
					if (GetAbilityLevel(existingBuff) < level) then
						call RemoveUnitAbility(enumUnit, existingBuff)
						addNewBuff = true
					else
						call existingBuff.refreshExpiration()
					endif
				endif
				if (addNewBuff) then
					BuffAuraBase newBuff = createBuff(source, enumUnit)
					call AddUnitAbility(enumUnit, newBuff)
				endif
			endif
			return false
		endmethod

		method onTick takes nothing returns nothing
			integer gameTurnTick = GetGameTurnTick()
			if (gameTurnTick >= nextAreaCheck) then
				call GroupEnumUnitsInRangeOfUnit(null, source, getAreaOfEffect(), this.enumFilter)
				nextAreaCheck = gameTurnTick + AURA_PERIODIC_CHECK_TIME_TICKS
			endif
		endmethod

		method getBuffId takes nothing returns integer
			return buffId
		endmethod
	endstruct
endlibrary
