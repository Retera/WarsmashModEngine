//=================================================================================================
// Ability Utils
//=================================================================================================
// Functions defined in userspace rather than as natives for handling ability implementations.

globals
    constant string AB_LOCAL_STORE_KEY_ABILITYEDITORDATA                 = "_abilityEditorData"
    constant string AB_LOCAL_STORE_KEY_LEVELDATA                         = "_levelData"
    constant string AB_LOCAL_STORE_KEY_PARENTLEVELDATA                   = "_parentLevelData"
    constant string AB_LOCAL_STORE_KEY_PARENTCASTER                      = "_parentCaster"
    constant string AB_LOCAL_STORE_KEY_PARENTLOCALSTORE                  = "_parentLocalStore"
    constant string AB_LOCAL_STORE_KEY_ALIAS                             = "_alias"
    constant string AB_LOCAL_STORE_KEY_CODE                              = "_code"
    constant string AB_LOCAL_STORE_KEY_GAME                              = "_game"
    constant string AB_LOCAL_STORE_KEY_THISUNIT                          = "_thisUnit"
    constant string AB_LOCAL_STORE_KEY_ABILITY                           = "_ability"
    constant string AB_LOCAL_STORE_KEY_ITEM                              = "_item"
    constant string AB_LOCAL_STORE_KEY_ITEMSLOT                          = "_itemSlot"
    constant string AB_LOCAL_STORE_KEY_ITERATORCOUNT                     = "_i"
    constant string AB_LOCAL_STORE_KEY_BREAK                             = "_break"
    constant string AB_LOCAL_STORE_KEY_BUFFCASTINGUNIT                   = "_buffCastingUnit"
    constant string AB_LOCAL_STORE_KEY_NEWBEHAVIOR                       = "_newBehavior"
    constant string AB_LOCAL_STORE_KEY_FAILEDTOCAST                      = "_failedToCast#"
    constant string AB_LOCAL_STORE_KEY_TRANSFORMINGTOALT                 = "_transformingToAlt#"
    constant string AB_LOCAL_STORE_KEY_CHANNELING                        = "_channeling#"
    constant string AB_LOCAL_STORE_KEY_INTERRUPTED                       = "_interrupted#"
    constant string AB_LOCAL_STORE_KEY_PERIODICNEXTTICK                  = "_periodicNextTick#"
    constant string AB_LOCAL_STORE_KEY_CANTUSEREASON                     = "_cantUseReason"
    constant string AB_LOCAL_STORE_KEY_TOGGLEDABILITY                    = "_toggledAbility"
    constant string AB_LOCAL_STORE_KEY_FLEXABILITY                       = "_flexAbility"
    constant string AB_LOCAL_STORE_KEY_PAIRABILITY                       = "_pairAbility"
    constant string AB_LOCAL_STORE_KEY_AURAGROUP                         = "_auraGroup"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDFX                     = "_lastCreatedFx"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDLIGHTNING              = "_lastCreatedLtng"
    constant string AB_LOCAL_STORE_KEY_ENUMUNIT                          = "_enumUnit#"
    constant string AB_LOCAL_STORE_KEY_MATCHINGUNIT                      = "_matchingUnit#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDUNIT                   = "_lastCreatedUnit"
    constant string AB_LOCAL_STORE_KEY_LASTADDEDUNIT                     = "_lastAddedUnit"
    constant string AB_LOCAL_STORE_KEY_LASTREMOVEDDUNIT                  = "_lastRemovedUnit"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDABILITY                = "_lastCreatedAbility"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDBUFF                   = "_lastCreatedBuff"
    constant string AB_LOCAL_STORE_KEY_LASTADDEDABILITY                  = "_lastAddedAbility"
    constant string AB_LOCAL_STORE_KEY_LASTADDEDBUFF                     = "_lastAddedBuff"
    constant string AB_LOCAL_STORE_KEY_CURRENTLEVEL                      = "_currentLevel"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDUNITGROUP              = "_lastCreatedUnitGroup"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDUNITQUEUE              = "_lastCreatedUnitQueue"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDDESTBUFF               = "_lastCreatedDestBuff"
    constant string AB_LOCAL_STORE_KEY_ATTACKINGUNIT                     = "_attackingUnit#"
    constant string AB_LOCAL_STORE_KEY_ATTACKEDUNIT                      = "_attackedUnit#"
    constant string AB_LOCAL_STORE_KEY_ABILITYTARGETEDUNIT               = "_abilityTargetedUnit#"
    constant string AB_LOCAL_STORE_KEY_ABILITYTARGETEDDESTRUCTABLE       = "_abilityTargetedDestructable#"
    constant string AB_LOCAL_STORE_KEY_ABILITYTARGETEDITEM               = "_abilityTargetedItem#"
    constant string AB_LOCAL_STORE_KEY_ABILITYTARGETEDLOCATION           = "_abilityTargetedLocation#"
    constant string AB_LOCAL_STORE_KEY_EVENTABILITY                      = "_eventAbility#"
    constant string AB_LOCAL_STORE_KEY_EVENTABILITYID                    = "_eventAbilityId#"
    constant string AB_LOCAL_STORE_KEY_EVENTCASTINGUNIT                  = "_eventCastingUnit#"
    constant string AB_LOCAL_STORE_KEY_EVENTTARGETEDUNIT                 = "_eventTargetedUnit#"
    constant string AB_LOCAL_STORE_KEY_EVENTTARGETEDDESTRUCTABLE         = "_eventTargetedDestructable#"
    constant string AB_LOCAL_STORE_KEY_EVENTTARGETEDITEM                 = "_eventTargetedItem#"
    constant string AB_LOCAL_STORE_KEY_EVENTTARGETEDLOCATION             = "_eventTargetedLocation#"
    constant string AB_LOCAL_STORE_KEY_BASEDAMAGEDEALT                   = "_baseDamageDealt#"
    constant string AB_LOCAL_STORE_KEY_BONUSDAMAGEDEALT                  = "_bonusDamageDealt#"
    constant string AB_LOCAL_STORE_KEY_TOTALDAMAGEDEALT                  = "_totalDamageDealt#"
    constant string AB_LOCAL_STORE_KEY_WEAPONTYPE                        = "_weaponType#"
    constant string AB_LOCAL_STORE_KEY_ATTACKTYPE                        = "_attackType#"
    constant string AB_LOCAL_STORE_KEY_DAMAGETYPE                        = "_damageType#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDTIMER                  = "_lastCreatedTimer"
    constant string AB_LOCAL_STORE_KEY_LASTSTARTEDTIMER                  = "_lastStartedTimer"
    constant string AB_LOCAL_STORE_KEY_FIRINGTIMER                       = "_firingTimer"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDTODEVENT               = "_lastCreatedToDEvent"
    constant string AB_LOCAL_STORE_KEY_BUFFEDDEST                        = "_buffedDest#"
    constant string AB_LOCAL_STORE_KEY_ENUMDESTRUCTABLE                  = "_enumDest#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDPROJECTILE             = "_lastCreatedProjectile"
    constant string AB_LOCAL_STORE_KEY_THISPROJECTILE                    = "_thisProjectile#"
    constant string AB_LOCAL_STORE_KEY_PROJECTILEUNITTARGETS             = "_projUnitTargets#"
    constant string AB_LOCAL_STORE_KEY_PROJECTILEDESTTARGETS             = "_projDestTargets#"
    constant string AB_LOCAL_STORE_KEY_PROJECTILECURRENTLOC              = "_projCurrentLoc#"
    constant string AB_LOCAL_STORE_KEY_PROJECTILEHITUNIT                 = "_projHitUnit#"
    constant string AB_LOCAL_STORE_KEY_PROJECTILEHITDEST                 = "_projHitDest#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDVISIONMODIFIER         = "_lastCreatedVisionMod"
    constant string AB_LOCAL_STORE_KEY_ABILITYPAIREDUNIT                 = "_abilityPairedUnit#"
    constant string AB_LOCAL_STORE_KEY_LASTPARTNERABILITY                = "_lastPartnerAbility"
    constant string AB_LOCAL_STORE_KEY_ACTIVE_ALTITUDE_ADJUSTMENT        = "_activeAltAdj"
    constant string AB_LOCAL_STORE_KEY_WAITING_ANIMATION                 = "_morphTimer"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDNSSB                   = "_lastCreatedNSSB"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDSMB                    = "_lastCreatedSMB"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAEL                    = "_lastCreatedAEL"
    constant string AB_LOCAL_STORE_KEY_DAMAGEISATTACK                    = "_damageIsAttack#"
    constant string AB_LOCAL_STORE_KEY_DAMAGEISRANGED                    = "_damageIsRanged#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAPoDL                  = "_lastCreatedAPoDL"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAPrDL                  = "_lastCreatedAPrDL"
    constant string AB_LOCAL_STORE_KEY_PREDAMAGERESULT                   = "_preDamageResult#"
    constant string AB_LOCAL_STORE_KEY_PREDAMAGESTACKING                 = "_preDamageStacking#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDDTL                    = "_lastCreatedDTL"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDDTML                   = "_lastCreatedDTML"
    constant string AB_LOCAL_STORE_KEY_DAMAGEMODRESULT                   = "_damageModResult#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDFDTML                  = "_lastCreatedFDTML"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDDRE                    = "_lastCreatedDRE"
    constant string AB_LOCAL_STORE_KEY_KILLINGUNIT                       = "_killingUnit#"
    constant string AB_LOCAL_STORE_KEY_DYINGUNIT                         = "_dyingUnit#"
    constant string AB_LOCAL_STORE_KEY_DEATHRESULT                       = "_deathResult#"
    constant string AB_LOCAL_STORE_KEY_DEATHSTACKING                     = "_deathStacking#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAtkPRL                 = "_lastCreatedAtkPRL"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAbPRL                  = "_lastCreatedAbPRL"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDAbERL                  = "_lastCreatedAbERL"
    constant string AB_LOCAL_STORE_KEY_REACTIONALLOWHIT                  = "_reactionAllowHit#"
    constant string AB_LOCAL_STORE_KEY_ATTACKPROJ                        = "_attackProj#"
    constant string AB_LOCAL_STORE_KEY_ABILITYPROJ                       = "_abilityProj#"
    constant string AB_LOCAL_STORE_KEY_REACTIONABILITY                   = "_reactionAbility#"
    constant string AB_LOCAL_STORE_KEY_REACTIONABILITYCASTER             = "_reactionAbilityCaster#"
    constant string AB_LOCAL_STORE_KEY_REACTIONABILITYTARGET             = "_reactionAbilityTarget#"
    constant string AB_LOCAL_STORE_KEY_LASTCREATEDBCL                    = "_lastCreatedBCL"
    constant string AB_LOCAL_STORE_KEY_PRECHANGEBEHAVIOR                 = "_preChangeBehavior#"
    constant string AB_LOCAL_STORE_KEY_POSTCHANGEBEHAVIOR                = "_postChangeBehaviorj#"
    constant string AB_LOCAL_STORE_KEY_BEHAVIORONGOING                   = "_behaviorOngoing#"
	
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

function AddUnitBuffAU takes unit target, localstore sourceAbility, buff whichBuff returns nothing
	call AddUnitAbility(target, whichBuff)
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTADDEDBUFF, whichBuff)

function AddUnitNonStackingDisplayBuffAU takes unit target, localstore sourceAbility, string stackingKey, buff whichBuff returns nothing
	call AddUnitNonStackingDisplayBuff(target, stackingKey, whichBuff)
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTADDEDBUFF, whichBuff)
endfunction


function StoreCreatedBuffAU takes unit casterUnit, localstore sourceAbility, buff whichBuff returns nothing
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTCREATEDBUFF, theBuff)
	if not LocalStoreContainsKey(sourceAbility, AB_LOCAL_STORE_KEY_BUFFCASTINGUNIT) then
		call SetLocalStoreUnitHandle(sourceAbility, AB_LOCAL_STORE_KEY_BUFFCASTINGUNIT, casterUnit)
	endif
endfunction

function CreatePassiveBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, boolean showIcon, code onAddAction, code onRemoveAction, effecttype artType, boolean showFx, boolean playSfx, integer castId returns buff
	local buff theBuff = CreatePassiveBuff(buffId, showIcon, onAddAction, onRemoveAction, artType, showFx, playSfx, sourceAbility, castId)
	call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
	return theBuff
endfunction

function CreateTargetingBuffAU takes unit casterUnit, localstore sourceAbility, integer castId, integer buffId returns buff
	local buff theBuff = CreateTargetingBuff(buffId)
	call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
	return theBuff
endfunction

function CreateTimedArtBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean showIcon, effecttype artType returns buff
	local buff theBuff = CreateTimedArtBuff(buffId, duration, showIcon, artType)
	call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
	return theBuff
endfunction

function CreateTimedBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, boolean showIcon, effecttype artType returns buff
    local buff theBuff = CreateTimedBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, showIcon, artType)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedLifeBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean explode returns buff
    local buff theBuff = CreateTimedLifeBuff(buffId, duration, explode)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTargetingBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration returns buff
    local buff theBuff = CreateTimedTargetingBuff(buffId, duration)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTickingBuffAU takes unit casterUnit, localstore sourceAbility, takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbility, integer castId returns buff
    local buff theBuff = CreateTimedTickingBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTickingPausedBuffAU takes unit casterUnit, localstore sourceAbility, takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbility, integer castId returns buff
    local buff theBuff = CreateTimedTickingPausedBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTickingPostDeathBuffAU takes unit casterUnit, localstore sourceAbility, takes integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, localstore sourceAbility, integer castId returns buff
    local buff theBuff = CreateTimedTickingPostDeathBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateDestructableBuffAU takes unit casterUnit, integer buffId, localstore sourceAbilLocalStore, code onAddAction, code onRemoveAction, code onDeathAction, integer castId returns destructablebuff
    local destructablebuff theBuff = CreateDestructableBuff(casterUnit, buffId, GetLocalStoreInteger(sourceAbilLocalStore, AB_LOCAL_STORE_KEY_CURRENTLEVEL), sourceAbilLocalStore, onAddAction, onRemoveAction, onDeathAction, castId)
	call SetLocalStoreDestructableBuffHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTCREATEDDESTBUFF, theBuff)
    return theBuff
endfunction

function CreateABTimeOfDayEventAU takes code actions, real startTime, real endTime, string equalityId, unit caster, localstore localStore, integer castId returns abtimeofdayevent
	local abtimeofdayevent evt = CreateABTimeOfDayEvent(actions, startTime, endTime, equalityId, caster, localStore, castId)
	call SetLocalStoreABTimeOfDayEventHandle(localStore, AB_LOCAL_STORE_KEY_LASTCREATEDTODEVENT, evt)
	return evt
endfunction

function ChargeItemAU takes item whichItem, boolean checkForPerish, localstore whichLocalStore returns nothing
	local item it
	if whichItem == null then
		it = GetAbilityItem(GetLocalStoreAbilityHandle(whichLocalStore, AB_LOCAL_STORE_KEY_ABILITY))
	else
		it = whichItem
	endif
	call SetItemCharges(GetItemCharges(it) - 1)
	if checkForPerish and IsItemIdPerishable(whichItem) and GetItemCharges(it) == 0 then
		// NOTE: it.forceDropIfHeld(game) was called here in the original AB json native,
		// but it is also called by the game engine in RemoveItem (skipped in translation)
		call RemoveItem(it)
	endif
endfunction

function GiveResourcesToPlayerAU takes player whichPlayer, integer gold, integer lumber returns nothing
	call SetPlayerState(whichPlayer, PLAYER_STATE_RESOURCE_GOLD, GetPlayerState(whichPlayer, PLAYER_STATE_RESOURCE_GOLD) + gold)
	call SetPlayerState(whichPlayer, PLAYER_STATE_RESOURCE_LUMBER, GetPlayerState(whichPlayer, PLAYER_STATE_RESOURCE_LUMBER) + lumber)
endfunction

function CreateLocationTargetedCollisionProjectileAU takes unit casterUnit, localstore sourceAbility, unit sourceUnit, location sourceLocation, location target, integer projectileId, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile
	local projectile theProjectile
	set theProjectile = CreateLocationTargetedCollisionProjectile(sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onPreHits, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, collisionInterval, provideCounts)
	call StoreCreatedProjectileAU(casterUnit, sourceAbility, theProjectile)
	return theProjectile
endfunction

function CheckAbilityEffectReactionAU takes unit target, ability whichAbility, code onHitFunc, code onBlockFunc returns nothing
	
endfunction