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
	

    
    group au_tempGroup = CreateGroup()
    group au_swapGroup
endglobals

function AddUnitBuffAU takes unit target, localstore sourceAbility, buff whichBuff returns nothing
	call AddUnitAbility(target, whichBuff)
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTADDEDBUFF, whichBuff)
endfunction

function AddUnitNonStackingDisplayBuffAU takes unit target, localstore sourceAbility, string stackingKey, buff whichBuff returns nothing
	call AddUnitNonStackingDisplayBuff(target, stackingKey, whichBuff)
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTADDEDBUFF, whichBuff)
endfunction


function StoreCreatedBuffAU takes unit casterUnit, localstore sourceAbility, buff whichBuff returns nothing
	call SetLocalStoreAbilityHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTCREATEDBUFF, whichBuff)
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

function CreateTimedTickingBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, integer castId returns buff
    local buff theBuff = CreateTimedTickingBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTickingPausedBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, integer castId returns buff
    local buff theBuff = CreateTimedTickingPausedBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateTimedTickingPostDeathBuffAU takes unit casterUnit, localstore sourceAbility, integer buffId, real duration, boolean showTimedLifeBar, code onAddAction, code onRemoveAction, code onExpireAction, code onTickAction, boolean showIcon, effecttype artType, integer castId returns buff
    local buff theBuff = CreateTimedTickingPostDeathBuff(buffId, duration, showTimedLifeBar, onAddAction, onRemoveAction, onExpireAction, onTickAction, showIcon, artType, sourceAbility, castId)
    call StoreCreatedBuffAU(casterUnit, sourceAbility, theBuff)
    return theBuff
endfunction

function CreateStunBuffAU takes localstore localStore, integer buffId, real duration returns buff
    local buff theBuff = CreateStunBuff(buffId, duration)
	call SetLocalStoreAbilityHandle(localStore, AB_LOCAL_STORE_KEY_LASTCREATEDBUFF, theBuff)
    return theBuff
endfunction

function CreateDestructableBuffAU takes unit casterUnit, integer buffId, localstore sourceAbilLocalStore, code onAddAction, code onRemoveAction, code onDeathAction, integer castId returns destructablebuff
    local destructablebuff theBuff = CreateDestructableBuff(casterUnit, buffId, GetLocalStoreInteger(sourceAbilLocalStore, AB_LOCAL_STORE_KEY_CURRENTLEVEL), sourceAbilLocalStore, onAddAction, onRemoveAction, onDeathAction, castId)
	call SetLocalStoreDestructableBuffHandle(sourceAbilLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDDESTBUFF, theBuff)
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
		set it = GetAbilityItem(GetLocalStoreAbilityHandle(whichLocalStore, AB_LOCAL_STORE_KEY_ABILITY))
	else
		set it = whichItem
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

function StoreCreatedProjectileAU takes localstore sourceAbility, integer castId, projectile whichProjectile returns nothing
	call SetLocalStoreProjectileHandle(sourceAbility, AB_LOCAL_STORE_KEY_LASTCREATEDPROJECTILE + I2S(castId), whichProjectile)
endfunction

function CreateLocationTargetedCollisionProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile
	local projectile theProjectile = CreateLocationTargetedCollisionProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, collisionInterval, provideCounts)
	call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
	return theProjectile
endfunction

function CreateLocationTargetedCollisionProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile
    // NOTE: at the time of writing, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA is populated by the game engine and does not need to be "Set" prior to "Get".
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
	return CreateLocationTargetedCollisionProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, collisionInterval, provideCounts)
endfunction

function CreateLocationTargetedProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, real speed, boolean homing, code onLaunch, code onHit returns projectile
	local projectile theProjectile = CreateLocationTargetedProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onHit)
	call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
	return theProjectile
endfunction

function CreateLocationTargetedProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, code onLaunch, code onHit returns projectile
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
	return CreateLocationTargetedProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onHit)
endfunction

function CreateLocationTargetedPseudoProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile
    local projectile theProjectile = CreateLocationTargetedPseudoProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, whichEffectType, effectArtIndex, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, projectileStepInterval, projectileArtSkip, provideCounts)
    call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
    return theProjectile
endfunction

function CreateLocationTargetedPseudoProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, location target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
    return CreateLocationTargetedPseudoProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, whichEffectType, effectArtIndex, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, projectileStepInterval, projectileArtSkip, provideCounts)
endfunction

function CreateUnitTargetedCollisionProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile
	local projectile theProjectile = CreateUnitTargetedCollisionProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, collisionInterval, provideCounts)
	call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
	return theProjectile
endfunction

function CreateUnitTargetedCollisionProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real collisionInterval, boolean provideCounts returns projectile
    // NOTE: at the time of writing, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA is populated by the game engine and does not need to be "Set" prior to "Get".
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
	return CreateUnitTargetedCollisionProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, collisionInterval, provideCounts)
endfunction

function CreateUnitTargetedProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit targetUnit, integer projectileId, real speed, boolean homing, code onLaunch, code onHit returns projectile
	local projectile theProjectile = CreateUnitTargetedProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, targetUnit, projectileId, speed, homing, onLaunch, onHit)
	call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
	return theProjectile
endfunction

function CreateUnitTargetedProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit targetUnit, integer projectileId, code onLaunch, code onHit returns projectile
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
	return CreateUnitTargetedProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, targetUnit, projectileId, speed, homing, onLaunch, onHit)
endfunction

function CreateUnitTargetedPseudoProjectileAnySpeedAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, real speed, boolean homing, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile
    local projectile theProjectile = CreateUnitTargetedPseudoProjectile(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, whichEffectType, effectArtIndex, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, projectileStepInterval, projectileArtSkip, provideCounts)
    call StoreCreatedProjectileAU(sourceAbility, castId, theProjectile)
    return theProjectile
endfunction

function CreateUnitTargetedPseudoProjectileAU takes unit casterUnit, localstore sourceAbility, integer castId, unit sourceUnit, location sourceLocation, unit target, integer projectileId, effecttype whichEffectType, integer effectArtIndex, code onLaunch, code onPreHits, boolexpr canHitTarget, code onHit, integer maxHits, integer hitsPerTarget, real startingRadius, real endingRadius, real projectileStepInterval, integer projectileArtSkip, boolean provideCounts returns projectile
    local gameobject editorData = GetLocalStoreGameObjectHandle(sourceAbility, AB_LOCAL_STORE_KEY_ABILITYEDITORDATA)
    local real speed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
    local boolean homing = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
    return CreateUnitTargetedPseudoProjectileAnySpeedAU(casterUnit, sourceAbility, castId, sourceUnit, sourceLocation, target, projectileId, whichEffectType, effectArtIndex, speed, homing, onLaunch, onPreHits, canHitTarget, onHit, maxHits, hitsPerTarget, startingRadius, endingRadius, projectileStepInterval, projectileArtSkip, provideCounts)
endfunction

function CheckAbilityEffectReactionAU takes unit caster, localstore localStore, integer castId, unit target, ability whichAbility, code onHitFunc, code onBlockFunc returns nothing
    // TODO use of "StartAbilityBuilderThread" here is most likely not identical to what JSON was doing, because code executed after
    // this function can happen before the new simulated "thread" runs even though it should probably run before the game engine proceeds.
    // If that's a problem, we could maybe change this in the future to have a different native to inline immediately run the code func
	if CheckUnitForAbilityEffectReaction(target, caster, whichAbility) then
	    call StartAbilityBuilderThread(onHitFunc, caster, localStore, castId)
	else
	    call StartAbilityBuilderThread(onBlockFunc, caster, localStore, castId)
	endif
endfunction

function CheckAbilityProjReactionAU takes unit caster, localstore localStore, integer castId, unit target, projectile whichProjectile, code onHitFunc, code onBlockFunc returns nothing
    // TODO use of "StartAbilityBuilderThread" here is most likely not identical to what JSON was doing, because code executed after
    // this function can happen before the new simulated "thread" runs even though it should probably run before the game engine proceeds.
    // If that's a problem, we could maybe change this in the future to have a different native to inline immediately run the code func
	if CheckUnitForAbilityProjReaction(target, caster, whichProjectile) then
	    call StartAbilityBuilderThread(onHitFunc, caster, localStore, castId)
	else
	    call StartAbilityBuilderThread(onBlockFunc, caster, localStore, castId)
	endif
endfunction

function CreateNonStackingStatBuffAU takes localstore whichLocalStore, nonstackingstatbonustype whichType, string stackingKey, real value returns nonstackingstatbonus
    local nonstackingstatbonus theBuff = CreateNonStackingStatBonus(whichType, stackingKey, value)
    call SetLocalStoreNonStackingStatBonusHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDNSSB, theBuff)
    return theBuff
endfunction

function UnitDamageTargetAU takes unit whichUnit, widget target, real amount, boolean attack, boolean ranged, attacktype attackType, damagetype damageType, boolean ignoreLTEZero returns boolean
    if not ignoreLTEZero or amount > 0 then
        call UnitDamageTarget(whichUnit, target, amount, attack, ranged, attackType, damageType, WEAPON_TYPE_WHOKNOWS)
    endif
endfunction

function GetAbilityDataAsFloatAU takes localstore x, datafieldletter whichDataField returns real
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataReal(d, lvl, whichDataField)
endfunction

function GetAbilityDataAsBooleanAU takes localstore x, datafieldletter whichDataField returns boolean
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataBoolean(d, lvl, whichDataField)
endfunction

function GetAbilityDataAsIntegerAU takes localstore x, datafieldletter whichDataField returns integer
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataInteger(d, lvl, whichDataField)
endfunction 

function GetAbilityDataAsIDAU takes localstore x, datafieldletter whichDataField returns integer
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataID(d, lvl, whichDataField)
endfunction 

function GetAbilityDataAsStringAU takes localstore x, datafieldletter whichDataField returns string
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataString(d, lvl, whichDataField)
endfunction

function GetAbilityUnitIDAU takes localstore x returns integer
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelUnitID(d, lvl)
endfunction

function GetFirstBuffIdAU takes localstore x returns integer
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataFirstBuffId(d, lvl)
endfunction

function GetAbilityDurationForTargetAU takes localstore x, unit target returns real
    local localstore x = GetTriggerLocalStore()
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    if IsUnitType(target, UNIT_TYPE_HERO) or IsUnitType(target, UNIT_TYPE_RESISTANT) then
        return GetAbilityTypeLevelDataDurationHero(d, lvl)
    endif
    return GetAbilityTypeLevelDataDurationNormal(d, lvl)
endfunction

function GetAbilityDurationAU takes localstore x returns real
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataDurationNormal(d, lvl)
endfunction

function GetAbilityCastTimeAU takes localstore x returns real
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(x, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(x, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return GetAbilityTypeLevelDataCastTime(d, lvl)
endfunction

function SetLocalStoreUserStringAU takes localstore whichLocalStore, string childKey, string value returns boolean
    return SetLocalStoreString(whichLocalStore, "__" + childKey, value)
endfunction

function SetLocalStoreUserCastStringAU takes localstore whichLocalStore, string childKey, integer castId, string value returns boolean
    return SetLocalStoreString(whichLocalStore, "__" + childKey + "#" + I2S(castId), value)
endfunction

function SetLocalStoreUserIntegerAU takes localstore whichLocalStore, string childKey, integer value returns boolean
    return SetLocalStoreInteger(whichLocalStore, "__" + childKey, value)
endfunction

function SetLocalStoreUserCastIntegerAU takes localstore whichLocalStore, string childKey, integer castId, integer value returns boolean
    return SetLocalStoreInteger(whichLocalStore, "__" + childKey + "#" + I2S(castId), value)
endfunction

function SetLocalStoreUserRealAU takes localstore whichLocalStore, string childKey, real value returns boolean
    return SetLocalStoreReal(whichLocalStore, "__" + childKey, value)
endfunction

function SetLocalStoreUserCastRealAU takes localstore whichLocalStore, string childKey, integer castId, real value returns boolean
    return SetLocalStoreReal(whichLocalStore, "__" + childKey + "#" + I2S(castId), value)
endfunction

function SetLocalStoreUserBooleanAU takes localstore whichLocalStore, string childKey, boolean value returns boolean
    return SetLocalStoreBoolean(whichLocalStore, "__" + childKey, value)
endfunction

function SetLocalStoreUserCastBooleanAU takes localstore whichLocalStore, string childKey, integer castId, boolean value returns boolean
    return SetLocalStoreBoolean(whichLocalStore, "__" + childKey + "#" + I2S(castId), value)
endfunction

function SetLocalStoreUserHandleAU takes localstore whichLocalStore, string childKey, handle value returns boolean
    return SetLocalStoreHandle(whichLocalStore, "__" + childKey, value)
endfunction

function SetLocalStoreUserCastHandleAU takes localstore whichLocalStore, string childKey, integer castId, handle value returns boolean
    return SetLocalStoreHandle(whichLocalStore, "__" + childKey + "#" + I2S(castId), value)
endfunction

function StoreStringLocallyAU takes localstore whichLocalStore, string childKey, integer castId, string value, boolean instanceValue returns boolean
    if instanceValue then
        return SetLocalStoreUserCastStringAU(whichLocalStore, childKey, castId, value)
    else
        return SetLocalStoreUserStringAU(whichLocalStore, childKey, value)
    endif
endfunction

function StoreIntegerLocallyAU takes localstore whichLocalStore, string childKey, integer castId, integer value, boolean instanceValue returns boolean
    if instanceValue then
        return SetLocalStoreUserCastIntegerAU(whichLocalStore, childKey, castId, value)
    else
        return SetLocalStoreUserIntegerAU(whichLocalStore, childKey, value)
    endif
endfunction

function StoreBooleanLocallyAU takes localstore whichLocalStore, string childKey, integer castId, boolean value, boolean instanceValue returns boolean
    if instanceValue then
        return SetLocalStoreUserCastBooleanAU(whichLocalStore, childKey, castId, value)
    else
        return SetLocalStoreUserBooleanAU(whichLocalStore, childKey, value)
    endif
endfunction

function StoreHandleLocallyAU takes localstore whichLocalStore, string childKey, integer castId, handle value, boolean instanceValue returns boolean
    if instanceValue then
        return SetLocalStoreUserCastHandleAU(whichLocalStore, childKey, castId, value)
    else
        return SetLocalStoreUserHandleAU(whichLocalStore, childKey, value)
    endif
endfunction

function GetLocalStoreUserNonStackingStatBonusHandleAU takes localstore whichLocalStore, string childKey returns nonstackingstatbonus
    return GetLocalStoreNonStackingStatBonusHandle(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastNonStackingStatBonusHandleAU takes localstore whichLocalStore, string childKey, integer castId returns nonstackingstatbonus
    return GetLocalStoreNonStackingStatBonusHandle(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredNonStackingStatBuffAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns nonstackingstatbonus
    if instanceValue then
        return GetLocalStoreUserCastNonStackingStatBonusHandleAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserNonStackingStatBonusHandleAU(whichLocalStore, childKey)
    endif
endfunction

function GetLocalStoreUserAbilityHandleAU takes localstore whichLocalStore, string childKey returns ability
    return GetLocalStoreAbilityHandle(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastAbilityHandleAU takes localstore whichLocalStore, string childKey, integer castId returns ability
    return GetLocalStoreAbilityHandle(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredAbilityAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns ability
    if instanceValue then
        return GetLocalStoreUserCastAbilityHandleAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserAbilityHandleAU(whichLocalStore, childKey)
    endif
endfunction

function GetLocalStoreUserRealAU takes localstore whichLocalStore, string childKey returns real
    return GetLocalStoreReal(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastRealAU takes localstore whichLocalStore, string childKey, integer castId returns real
    return GetLocalStoreReal(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredRealAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns real
    if instanceValue then
        return GetLocalStoreUserCastRealAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserRealAU(whichLocalStore, childKey)
    endif
endfunction

function GetLocalStoreUserBooleanAU takes localstore whichLocalStore, string childKey returns boolean
    return GetLocalStoreBoolean(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastBooleanAU takes localstore whichLocalStore, string childKey, integer castId returns boolean
    return GetLocalStoreBoolean(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredBooleanAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns boolean
    if instanceValue then
        return GetLocalStoreUserCastBooleanAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserBooleanAU(whichLocalStore, childKey)
    endif
endfunction

function GetLocalStoreUserIntegerAU takes localstore whichLocalStore, string childKey returns integer
    return GetLocalStoreInteger(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastIntegerAU takes localstore whichLocalStore, string childKey, integer castId returns integer
    return GetLocalStoreInteger(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredIntegerAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns integer
    if instanceValue then
        return GetLocalStoreUserCastIntegerAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserIntegerAU(whichLocalStore, childKey)
    endif
endfunction

function GetLocalStoreUserBuffHandleAU takes localstore whichLocalStore, string childKey returns buff
    return GetLocalStoreBuffHandle(whichLocalStore, "__" + childKey)
endfunction 

function GetLocalStoreUserCastBuffHandleAU takes localstore whichLocalStore, string childKey, integer castId returns buff
    return GetLocalStoreBuffHandle(whichLocalStore, "__" + childKey + "#" + I2S(castId))
endfunction 

function GetStoredBuffAU takes localstore whichLocalStore, string childKey, integer castId, boolean instanceValue returns buff
    if instanceValue then
        return GetLocalStoreUserCastBuffHandleAU(whichLocalStore, childKey, castId)
    else
        return GetLocalStoreUserBuffHandleAU(whichLocalStore, childKey)
    endif
endfunction

// NOTE: So, in Warsmash, some objects (such as buffs) have a "tick" method that fires about 20
// times per second, whenever the lock-step synchronized game does a step of game logic.
// In those situations, you might be inside of a callback that is called on every "tick,"
// but you actually only want to perform an action every so many seconds. Rather than
// implementing your own check every time, or whatever, PeriodicExecuteAU can be called
// on each tick and it will return true when you should _actually_ perform your logic.
// (Maybe in the future we could just replace this with a timer)
function PeriodicExecuteAU takes localstore whichLocalStore, real delaySeconds, boolean initialTick, string uniquenessKey returns boolean
    local integer nextActiveTick = GetLocalStoreInteger(whichLocalStore, AB_LOCAL_STORE_KEY_PERIODICNEXTTICK + uniquenessKey)
    local integer currentTick = GetGameTurnTick()
    local integer delayTicks
    local boolean runActions = false
    if currentTick >= nextActiveTick then
        set delayTicks = R2I(delaySeconds / au_SIMULATION_STEP_TIME)
        
        if nextActiveTick != 0 or initialTick then
            set runActions = true
        endif
        set nextActiveTick = currentTick + delayTicks
        call SetLocalStoreInteger(whichLocalStore, AB_LOCAL_STORE_KEY_PERIODICNEXTTICK + uniquenessKey, nextActiveTick)
    endif
    return runActions
endfunction

function StoreSubroutineAU takes localstore whichLocalStore, string key, code func returns nothing
    call SetLocalStoreCode(whichLocalStore, "_!" + key, func)
endfunction

function StoreCastSubroutineAU takes localstore whichLocalStore, string key, integer castId, code func returns nothing
    call SetLocalStoreCode(whichLocalStore, "_!" + key + "#" + I2S(castId), func)
endfunction

function CreateSubroutineAU takes localstore whichLocalStore, string key, integer castId, boolean instanceValue, code func returns nothing
    if instanceValue then
        call StoreCastSubroutineAU(whichLocalStore, key, castId, func)
    else
        call StoreSubroutineAU(whichLocalStore, key, func)
    endif
endfunction

function RunSubroutineAU takes unit caster, localstore whichLocalStore, integer castId, string key, boolean instanceValue returns nothing
    local string innerKey = "_!" + key
    local code subroutine
    if instanceValue then
        set innerKey = innerKey + "#" + I2S(castId)
    endif
    set subroutine = GetLocalStoreCode(whichLocalStore, innerKey)
    if subroutine != null then
        call StartAbilityBuilderThread(subroutine, caster, whichLocalStore, castId)
    endif
endfunction

function CreateTimerDelayedAU takes unit caster, localstore whichLocalStore, integer castId, real timeout, boolean repeats, code actionsFunc, boolean startTimer, real delay returns abtimer
    local abtimer t = CreateABTimer(caster, whichLocalStore, castId, actionsFunc)
    call ABTimerSetTimeoutTime(t, timeout) 
    call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDTIMER, t)
    
    if repeats then                                  
        call ABTimerSetRepeats(t, true)
        if startTimer then
            call ABTimerStartRepeatingTimerWithDelay(t, delay)
            call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTSTARTEDTIMER, t)
        endif
    else       
        if startTimer then
            call ABTimerStart(t)
            call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTSTARTEDTIMER, t)
        endif
    endif
    return t
endfunction

function CreateTimerAU takes unit caster, localstore whichLocalStore, integer castId, real timeout, boolean repeats, code actionsFunc, boolean startTimer returns abtimer
    local abtimer t = CreateABTimer(caster, whichLocalStore, castId, actionsFunc)
    call ABTimerSetTimeoutTime(t, timeout) 
    call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDTIMER, t)
                                     
    if repeats then                                  
        call ABTimerSetRepeats(t, true)
    endif
    if startTimer then
        call ABTimerStart(t)
        call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTSTARTEDTIMER, t)
    endif       
    return t
endfunction

function StartTimerAU takes abtimer t, localstore localStore returns nothing
    call ABTimerStart(t)
    call SetLocalStoreHandle(localStore, AB_LOCAL_STORE_KEY_LASTSTARTEDTIMER, t)
endfunction

function AddMpAU takes unit target, real amount, boolean percent returns nothing
    local real maxMana = GetUnitState(target, UNIT_STATE_MAX_MANA)
    if percent then
        call SetUnitState(target, UNIT_STATE_MANA, RMaxBJ(RMinBJ(GetUnitState(target, UNIT_STATE_MANA) + amount * maxMana, maxMana), 0))
    else
        call SetUnitState(target, UNIT_STATE_MANA, RMaxBJ(RMinBJ(GetUnitState(target, UNIT_STATE_MANA) + amount, maxMana), 0)) 
    endif
endfunction

function HealUnitAU takes unit target, real amount, boolean percent returns nothing
    if percent then
        call HealUnit(target, amount * GetUnitState(target, UNIT_STATE_MAX_LIFE))
    else
        call HealUnit(target, amount)
    endif
endfunction

function AddNewAbilityAU takes localstore whichLocalStore, unit whichUnit, integer whichAbilityId returns ability
    local ability abil = CreateAbility(whichAbilityId)
    call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDABILITY, abil)
    call AddUnitAbility(whichUnit, abil)
    return abil
endfunction

function IsWorkerAbilityAU takes ability whichAbility returns boolean
    local integer code = GetAbilityCodeId(whichAbility)
    if code == 'Aaha' then //CAbilityAcolyteHarvest 
        return true 
    elseif code == 'Ahar' then //CAbilityHarvest
        return true 
    elseif code == 'Awha' then //CAbilityWispHarvest
        return true 
    elseif code == 'AHbu' then //CAbilityHumanBuild
        return true 
    elseif code == 'Arep' then //CAbilityHumanRepair
        return true 
    elseif code == 'AGbu' then //CAbilityNagaBuild
        return true 
    elseif code == 'ANbu' then //CAbilityNeutralBuild
        return true 
    elseif code == 'AEbu' then //CAbilityNightElfBuild
        return true 
    elseif code == 'AObu' then //CAbilityOrcBuild
        return true 
    elseif code == 'Aren' then //CAbilityRepair
        return true 
    elseif code == 'Arst' then //CAbilityRepair (maybe later on Warsmash "Restore" will have a different code class if it's found to be legitimately functionally different)
        return true 
    elseif code == 'AUbu' then //CAbilityUndeadBuild
        return true 
    endif
    return false
endfunction

function DisableWorkerAbilitiesAU takes unit whichUnit, boolean flag returns nothing
    local ability a
    local integer idx = 0
    loop
        set a = GetUnitAbilityByIndex(whichUnit, idx)
        exitwhen a == null
        if IsWorkerAbilityAU(a) then
            call SetAbilityIconShowing(a, not flag)
            // NOTE: At the time of writing, the "disable worker abilities" json native was set up
            // to always publish TRANSFORMATION as the reason in Ability Builder, so we are just
            // matching that
            call SetAbilityDisabled(whichUnit, a, flag, ABILITY_DISABLE_TYPE_TRANSFORMATION)
        endif
        set idx = idx + 1
    endloop
endfunction

function MergeUnitsAU takes localstore whichLocalStore, unit unit1, unit unit2, integer newUnitId, location loc, real facing, player whichPlayer, boolean resetHpMp returns unit
    local real newHPPcnt = ((GetUnitState(unit1, UNIT_STATE_LIFE) / GetUnitState(unit1, UNIT_STATE_MAX_LIFE)) + (GetUnitState(unit2, UNIT_STATE_LIFE) / GetUnitState(unit2, UNIT_STATE_MAX_LIFE))) / 2
    local real newMPPcnt = ((GetUnitState(unit1, UNIT_STATE_MANA) / GetUnitState(unit1, UNIT_STATE_MAX_MANA)) + (GetUnitState(unit2, UNIT_STATE_MANA) / GetUnitState(unit2, UNIT_STATE_MAX_MANA))) / 2
    local unit createdUnit = CreateUnit(whichPlayer, newUnitId, GetLocationX(loc), GetLocationY(loc), facing)
    if not resetHpMp then
        call SetUnitState(createdUnit, UNIT_STATE_LIFE, newHPPcnt * GetUnitState(createdUnit, UNIT_STATE_MAX_LIFE))
        call SetUnitState(createdUnit, UNIT_STATE_MANA, newMPPcnt * GetUnitState(createdUnit, UNIT_STATE_MAX_MANA))
    endif
    call SetPreferredSelectionReplacement(unit1, createdUnit)
    call SetPreferredSelectionReplacement(unit2, createdUnit)
    call RemoveUnit(unit1)
    call RemoveUnit(unit2)
    call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDUNIT, createdUnit)
    return createdUnit
endfunction

function SendUnitBackToWorkAU_TimerActions takes nothing returns nothing
    call SendUnitBackToWork(GetTriggerUnit())
endfunction

function SendUnitBackToWorkAU takes unit whichUnit, localstore localStore, integer castId returns nothing
    // below: doesn't quite match the JSON, which usedthe original caster
    // as the timer's trigger unit, and referenced the "whichUnit" via better language features jass
    // doesn't have (lambda capture)... So, passing "whichUnit" as the caster unit in the ABTimer
    // is used as an instancing hack here. Better system for scoping (vJASS etc) would be nice in the future
    local abtimer t = CreateABTimer(whichUnit, localStore, castId, function SendUnitBackToWorkAU_TimerActions)
    call ABTimerSetRepeats(t, false)
    call ABTimerSetTimeoutTime(0)
    call ABTimerStart(t)
endfunction

function SetUnitHpAU takes unit whichUnit, real amount, boolean percent returns nothing
    if percent then
        call SetUnitState(whichUnit, UNIT_STATE_LIFE, amount * GetUnitState(whichUnit, UNIT_STATE_MAX_LIFE))
    else
        call SetUnitState(whichUnit, UNIT_STATE_LIFE, amount)
    endif
endfunction      

function SetUnitMpAU takes unit whichUnit, real amount, boolean percent returns nothing
    local real maxMana = GetUnitState(whichUnit, UNIT_STATE_MAX_MANA)
    if percent then
        call SetUnitState(whichUnit, UNIT_STATE_MANA, RMaxBJ(RMinBJ(amount * maxMana, maxMana), 0))
    else
        call SetUnitState(whichUnit, UNIT_STATE_MANA, RMaxBJ(RMinBJ(amount, maxMana), 0)) 
    endif
endfunction

function GroupAddUnitAU takes localstore localStore, group whichGroup, unit whichUnit returns nothing
    call GroupAddUnit(whichGroup, whichUnit)
    call SetLocalStoreHandle(whichGroup, AB_LOCAL_STORE_KEY_LASTADDEDUNIT, whichUnit)
endfunction

function CreateGroupAU takes localstore localStore returns group
    local group g = CreateGroup()
    call SetLocalStoreHandle(localStore, AB_LOCAL_STORE_KEY_LASTCREATEDUNITGROUP, g)
    return g
endfunction

function CreateNamedGroupAU takes localstore localStore, string name returns group
    local group g = CreateGroupAU(localStore)
    call SetLocalStoreHandle(localStore, "_unitgroup_" + name, g)
    return g
endfunction

function GroupRemoveUnitAU takes localstore localStore, group whichGroup, unit whichUnit returns nothing
    call GroupRemoveUnit(whichGroup, whichUnit)
    call SetLocalStoreHandle(localStore, AB_LOCAL_STORE_KEY_LASTREMOVEDDUNIT, whichUnit)
endfunction

function CreateQueueAU takes localstore localStore returns group
    local group g = CreateGroup()
    call SetLocalStoreHandle(localStore, AB_LOCAL_STORE_KEY_LASTCREATEDUNITQUEUE, g)
    return g
endfunction

function CreateNamedQueueAU takes localstore localStore, string name returns group
    local group g = CreateQueueAU(localStore)
    call SetLocalStoreHandle(localStore, "_unitqueue_" + name, g)
    return g
endfunction

function AddUnitAbilityAU takes localstore localStore, unit whichUnit, ability whichAbility returns nothing
    call AddUnitAbility(whichUnit, whichAbility)
    call SetLocalStoreHandle(localStore, AB_LOCAL_STORE_KEY_LASTADDEDABILITY, whichAbility)
endfunction

function CreateAbilityFromIdAU takes localstore whichLocalStore, integer whichAbilityId returns ability
    local ability abil = CreateAbility(whichAbilityId)
    call SetLocalStoreHandle(whichLocalStore, AB_LOCAL_STORE_KEY_LASTCREATEDABILITY, abil)
    return abil
endfunction

function IsUnitValidTargetAU takes unit target, unit caster, boolean targetedEffect, localstore localStore returns boolean
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(localStore, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(localStore, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return IsUnitValidTarget(target, caster, d, lvl, targetedEffect)
endfunction

function IsValidTargetAU takes widget target, unit caster, localstore localStore returns boolean
    local abilitytypeleveldata d = GetLocalStoreAbilityTypeLevelDataHandle(localStore, AB_LOCAL_STORE_KEY_LEVELDATA)
    local integer lvl = GetLocalStoreInteger(localStore, AB_LOCAL_STORE_KEY_CURRENTLEVEL) - 1 // starts at 0
    return IsValidTarget(target, caster, d, lvl)
endfunction

function IsUnitMaxHpAU takes unit u returns boolean
    if u != null then
        return GetUnitState(u, UNIT_STATE_LIFE) >= GetUnitState(u, UNIT_STATE_MAX_LIFE)
    endif
    return false
endfunction

function IsUnitMaxMpAU takes unit u returns boolean
    if u != null then
        return GetUnitState(u, UNIT_STATE_MANA) >= GetUnitState(u, UNIT_STATE_MAX_MANA)
    endif
    return false
endfunction

function GetProjectileHitWidgetAU takes nothing returns widget
    local localstore x = GetTriggerLocalStore()
    local integer castId = GetTriggerCastId()
    local unit hitUnit = GetLocalStoreUnitHandle(x, AB_LOCAL_STORE_KEY_PROJECTILEHITUNIT + I2S(castId))
    local destructable hitDest = GetLocalStoreDestructableHandle(x, AB_LOCAL_STORE_KEY_PROJECTILEHITDEST + I2S(castId))
endfunction

// different from PolarProjectionBJ because it doesnt involve DEGTORAD
function PolarProjectionAU takes location source, real dist, real angle returns location
    local real x = GetLocationX(source) + dist * Cos(angle)
    local real y = GetLocationY(source) + dist * Sin(angle)
    return Location(x, y)
endfunction

// same as polar Proj
function AngleBetweenPointsAU takes location locA, location locB returns real
    return Atan2(GetLocationY(locB) - GetLocationY(locA), GetLocationX(locB) - GetLocationX(locA))
endfunction

//function AddSpellEffectLocAU takes integer abilityId, effecttype effectType, 
