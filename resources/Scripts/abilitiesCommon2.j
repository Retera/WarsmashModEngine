// ability customization API types
type abilitytypeleveldata extends handle
type targettype extends handle
type texttagconfigtype extends handle
//type activeability extends ability
type localstore extends handle
type destructablebuff extends handle // a buff that is applied to a destructable
type projectile extends handle
type gameobject extends handle
type worldeditordatatype extends handle

type abtimeofdayevent // Ability Builder time of day event (doesnt have handleid for now)

constant native ConvertTargetType takes integer x returns targettype
constant native ConvertTextTagConfigType takes integer x returns texttagconfigtype
constant native ConvertWorldEditorDataType takes integer x returns worldeditordatatype

globals
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
	
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_UNITS                    = ConvertWorldEditorDataType(0)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_ITEMS                    = ConvertWorldEditorDataType(1)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_DESTRUCTABLES            = ConvertWorldEditorDataType(2)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_DOODADS                  = ConvertWorldEditorDataType(3)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_ABILITIES                = ConvertWorldEditorDataType(4)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_BUFFS_EFFECTS            = ConvertWorldEditorDataType(5)
	constant gameobjecttype WORLD_EDITOR_DATA_TYPE_UPGRADES                 = ConvertWorldEditorDataType(6)
endglobals

//=================================================================================================
// AbilityTypeLevelData API
//=================================================================================================
// This is exposed to the AbilityBuilder for some reason. At a glance, it looks like whatever
// ability might be using these is probably broken, not MUI, and in need of fix. Worth testing.
// For now the API is provided in order to achieve 1:1 parity with JSON AbilityBuilder code.
native AbilityTypeLevelDataAddTargetAllowed takes abilitytypeleveldata whichData, integer level, targettype whichType returns nothing
native AbilityTypeLevelDataRemoveTargetAllowed takes abilitytypeleveldata whichData, integer level, targettype whichType returns nothing

//=================================================================================================
// Ability "user data" API
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

native CreateLocalStore takes nothing returns localstore

native GetLocalStoreString takes localstore whichLocalStore, string childKey returns string
native GetLocalStoreInteger takes localstore whichLocalStore, string childKey returns integer
native GetLocalStoreBoolean takes localstore whichLocalStore, string childKey returns boolean
native GetLocalStoreAbilityTypeLevelDataHandle takes localstore whichLocalStore, string childKey returns abilitytypeleveldata
native GetLocalStoreAbilityHandle takes localstore whichLocalStore, string childKey returns ability
native GetLocalStoreUnitHandle takes localstore whichLocalStore, string childKey returns unit
native GetLocalStoreDestructableHandle takes localstore whichLocalStore, string childKey returns destructable
native GetLocalStoreDestructableBuffHandle takes localstore whichLocalStore, string childKey returns destructablebuff
native GetLocalStoreABTimeOfDayEventHandle takes localstore whichLocalStore, string childKey returns abtimeofdayevent

// setters: return true if there was some previous value stored at the child key
native SetLocalStoreString takes localstore whichLocalStore, string childKey, string value returns boolean
native SetLocalStoreInteger takes localstore whichLocalStore, string childKey, integer value returns boolean
native SetLocalStoreBoolean takes localstore whichLocalStore, string childKey, boolean value returns boolean
native SetLocalStoreAbilityTypeLevelDataHandle takes localstore whichLocalStore, string childKey, abilitytypeleveldata value returns boolean
native SetLocalStoreAbilityHandle takes localstore whichLocalStore, string childKey, ability value returns boolean
native SetLocalStoreUnitHandle takes localstore whichLocalStore, string childKey, unit value returns boolean
native SetLocalStoreDestructableHandle takes localstore whichLocalStore, string childKey, destructable value returns boolean
native SetLocalStoreDestructableBuffHandle takes localstore whichLocalStore, string childKey, destructablebuff value returns boolean
native SetLocalStoreABTimeOfDayEventHandle takes localstore whichLocalStore, string childKey, abtimeofdayevent value returns boolean

native LocalStoreContainsKey takes localstore whichLocalStore, string childKey returns boolean

native FlushParentLocalStore takes localstore whichLocalStore returns nothing
native FlushChildLocalStore takes localstore whichLocalStore, string childKey returns boolean

// NOTE: only works on abilities defined by Ability Builder. At the moment, hard-coded engine abilities
// dont have this
native GetAbilityLocalStore takes ability whichAbility returns localstore

// Returns the local store for the casted ability thing (or other active Ability Builder callback)
native GetTriggerLocalStore takes nothing returns localstore

// NOTE: java does not use destroy functions, this was kept for consistency with war3 stuff
// but at the moment it is identical to FlushParentLocalStore to help you with performance
native DestroyLocalStore takes localstore returns nothing

//=================================================================================================
// Ability API (general stuff)
//=================================================================================================

// These do the same thing as similar Blz functions if user simulation includes newer patch,
// but are distinct because our simulation will include them regardless of patch version
native GetUnitAbility takes unit whichUnit, integer whichAbilityId returns ability
native GetUnitAbilityByIndex takes unit whichUnit, integer index returns ability

// Unlike "UnitAddAbility" for the rawcode based ones, this uses reversed name "AddUnitAbility"
// which staples an existing ability handle onto a unit. Maybe we could rename it
// to "UnitAddAbilityHandle" if this ends up too confusing in the future
native AddUnitAbility takes unit whichUnit, ability whichAbility returns nothing

// same idea here, see comment on AddUnitAbility. Returns true if something was removed.
native RemoveUnitAbility takes unit whichUnit, ability whichAbiilty reutrns nothing

native GetAbilityAliasId takes ability whichAbility returns integer
native GetAbilityCodeId takes ability whichAbility returns integer

// might be the same as BlzUnitHideAbility, but this one is for ability handles
native SetAbilityIconShowing takes ability whichAbility, boolean showing returns nothing

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
native StartAbilityDefaultCooldown takes unit caster, takes ability whichAbility returns nothing

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

// NOTE: maybe eventually this could be replaced by BlzSetAbilityRealLevelField(whichAbility, ABILITY_RLF_CAST_RANGE, 0, value)
// but at the moment it is not the same, because it sets the cast range independent of level
// (the one that is actually used rather than an editor stat, which would change on skill level up)
native SetAbilityCastRange takes ability whichAbility, real range returns nothing


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
native AddUnitNonStackingDisplayBuff takes unit target, string stackingKey, buff whichBuff returns nothing

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
native CreateTimedTickingPausedBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff

// see CreateTimedTickingPostDeathBuffAU
native CreateTimedTickingPostDeathBuff takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbilLocalStore, integer castId returns buff


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

//=================================================================================================
// Extra
//=================================================================================================
// these were already in the underlying system, exposed now because why not

// returns the name of the Warsmash Java code class in use by the given ability... probably only 
// useful for some hackery
native WarsmashGetAbilityClassName takes ability whichAbility returns string

// WarsmashGetRawcode2String('AHtb') == "AHtb"
native WarsmashGetRawcode2String takes integer rawcode returns string
