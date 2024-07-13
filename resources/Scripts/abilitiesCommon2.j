// ability customization API types
type abilitytypeleveldata extends handle // GetHandleId(myAbilityTypeLevelData) is a crash case, dont' do it
type targettype extends handle
type texttagconfigtype extends handle
//type activeability extends ability (this comment is a reminder to either scap or implement this type for real)
type localstore extends handle // GetHandleId(myLocalStore) is a crash case, dont' do it
type destructablebuff extends handle // a buff that is applied to a destructable
type projectile extends handle
type gameobject extends handle
type worldeditordatatype extends handle
type nonstackingstatbuff extends handle
type nonstackingstatbufftype extends handle
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

constant native ConvertTargetType takes integer x returns targettype
constant native ConvertTextTagConfigType takes integer x returns texttagconfigtype
constant native ConvertWorldEditorDataType takes integer x returns worldeditordatatype
constant native ConvertNonStackingStatBuffType takes integer x returns nonstackingstatbufftype
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
	
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_UNITS                    = ConvertWorldEditorDataType(0)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_ITEMS                    = ConvertWorldEditorDataType(1)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_DESTRUCTABLES            = ConvertWorldEditorDataType(2)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_DOODADS                  = ConvertWorldEditorDataType(3)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_ABILITIES                = ConvertWorldEditorDataType(4)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_BUFFS_EFFECTS            = ConvertWorldEditorDataType(5)
	constant worldeditordatatype WORLD_EDITOR_DATA_TYPE_UPGRADES                 = ConvertWorldEditorDataType(6)
	
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MELEEATK                          = ConvertNonStackingStatBuffType(0)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MELEEATKPCT                       = ConvertNonStackingStatBuffType(1)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_RNGDATK                           = ConvertNonStackingStatBuffType(2)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_RNGDATKPCT                        = ConvertNonStackingStatBuffType(3)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_ATKSPD                            = ConvertNonStackingStatBuffType(4)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_DEF                               = ConvertNonStackingStatBuffType(5)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_DEFPCT                            = ConvertNonStackingStatBuffType(6)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_HPGEN                             = ConvertNonStackingStatBuffType(7)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_HPGENPCT                          = ConvertNonStackingStatBuffType(8)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXHPGENPCT                       = ConvertNonStackingStatBuffType(9)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MPGEN                             = ConvertNonStackingStatBuffType(10)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MPGENPCT                          = ConvertNonStackingStatBuffType(11)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXMPGENPCT                       = ConvertNonStackingStatBuffType(12)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MVSPD                             = ConvertNonStackingStatBuffType(13)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MVSPDPCT                          = ConvertNonStackingStatBuffType(14)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_HPSTEAL                           = ConvertNonStackingStatBuffType(15)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_THORNS                            = ConvertNonStackingStatBuffType(16)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_THORNSPCT                         = ConvertNonStackingStatBuffType(17)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXHP                             = ConvertNonStackingStatBuffType(18)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXHPPCT                          = ConvertNonStackingStatBuffType(19)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXMP                             = ConvertNonStackingStatBuffType(20)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_MAXMPPCT                          = ConvertNonStackingStatBuffType(21)
    // These are for parsing
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_ALLATK                            = ConvertNonStackingStatBuffType(22)
    constant nonstackingstatbufftype NON_STACKING_STAT_BUFF_TYPE_ALLATKPCT                         = ConvertNonStackingStatBuffType(23)
    
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
native GetLocalStoreNonStackingStatBuffHandle takes localstore whichLocalStore, string childKey returns nonstackingstatbuff
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
native SetLocalStoreNonStackingStatBuffHandle takes localstore whichLocalStore, string childKey, nonstackingstatbuff value returns boolean
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
// Ability API (general stuff)
//=================================================================================================

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
native SetProjectileDone takes projectile whichProjectile, boolean done returns nothing
native SetProjectileReflected takes projectile whichProjectile, boolean reflected returns nothing
native SetProjectileTargetUnit takes projectile whichProjectile, unit target returns nothing
native SetProjectileTargetLoc takes projectile whichProjectile, location target returns nothing
native IsProjectileReflected takes projectile whichProjectile returns boolean

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
// the idea that all non-unit widgets are visible is trivially disproven by the counter example of fog of war,
// so I'm guessing we will end up wanting to replace both of these with one "is valid target" native that takes into account
// whether it's a targeted effect for both units and nonunits
native IsValidTarget takes widget target, unit caster, abilitytypeleveldata abilData, integer level returns boolean

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
// So I assume that if you `CreateNonStackingStatBuff(NON_STACKING_STAT_BUFF_TYPE_DEF, "My Devotion Aura Thing", 10)`
// and also `CreateNonStackingStatBuff(NON_STACKING_STAT_BUFF_TYPE_DEF, "My Devotion Aura Thing", 12)` and
// add them together on a unit, since they are non stacking I'm guessing this means the unit would gain a total of 12
// and not 22 defense, because they were both applied by "My Devotion Aura Thing"??
native CreateNonStackingStatBuff takes nonstackingstatbufftype whichType, string stackingKey, real value returns nonstackingstatbuff

native AddUnitNonStackingStatBuff takes unit targetUnit, nonstackingstatbuff whichBuff returns nothing

native RemoveUnitNonStackingStatBuff takes unit targetUnit, nonstackingstatbuff whichBuff returns nothing

native RecomputeStatBuffsOnUnit takes unit targetUnit, nonstackingstatbufftype whichBuffType returns nothing

// NOTE: seems like, if you call `UpdateNonStackingStatBuff`, you probably also have to call `RecomputeStatBuffsOnUnit`,
// otherwise you update some invisible thing without applying it to the unit.
native UpdateNonStackingStatBuff takes nonstackingstatbuff whichBuff, real value returns nothing

// See "String2DamageType" for notes on how "String2Thing" native is probably not good, and you
// should probably use NON_STACKING_STAT_BUFF_TYPE_MVSPDPCT values
native String2NonStackingStatBuffType takes string x returns nonstackingstatbufftype

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
