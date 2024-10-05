scope Avatar initializer register
	struct BuffAvatar extends BuffTimed
		// TODO originally this constant was private, but private is bugged on static members
		static constant string AVATAR_BUFF_STACKING_KEY = "BHav"
		 
		nonstackingstatbonus hitPointBonus
		nonstackingstatbonus damageBonus
		nonstackingstatbonus defenseBonus
		statemod magicImmuneBonus

		public static method create takes integer buffId, real duration, integer hitPointBonus, integer damageBonus, real defenseBonus returns thistype
			thistype this = allocate(buffId, duration)
			this.hitPointBonus = CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_MAXHP, AVATAR_BUFF_STACKING_KEY, hitPointBonus)
			this.damageBonus = CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_ALLATK, AVATAR_BUFF_STACKING_KEY, damageBonus)
			this.defenseBonus = CreateNonStackingStatBonus(NON_STACKING_STAT_BONUS_TYPE_DEF, AVATAR_BUFF_STACKING_KEY, defenseBonus)
			this.magicImmuneBonus = CreateStateMod(STATE_MOD_TYPE_MAGIC_IMMUNE, 1)
			return this
		endmethod

		method isTimedLifeBar takes nothing returns boolean
			return true
		endmethod
		
		method onBuffAdd takes unit target returns nothing
			AddUnitNonStackingStatBonus(target, this.hitPointBonus)
			AddUnitNonStackingStatBonus(target, this.damageBonus)
			AddUnitNonStackingStatBonus(target, this.defenseBonus)
			AddUnitStateMod(target, this.magicImmuneBonus)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_MAXHP)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_ALLATK)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_DEF)
			RecomputeStateModsOnUnit(target, STATE_MOD_TYPE_MAGIC_IMMUNE)
		endmethod
		
		method onBuffRemove takes unit target returns nothing
			RemoveUnitNonStackingStatBonus(target, this.hitPointBonus)
			RemoveUnitNonStackingStatBonus(target, this.damageBonus)
			RemoveUnitNonStackingStatBonus(target, this.defenseBonus)
			RemoveUnitStateMod(target, this.magicImmuneBonus)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_MAXHP)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_ALLATK)
			RecomputeStatBonusesOnUnit(target, NON_STACKING_STAT_BONUS_TYPE_DEF)
			RecomputeStateModsOnUnit(target, STATE_MOD_TYPE_MAGIC_IMMUNE)
		endmethod
		
	endstruct

    struct AbilityAvatar extends AbilitySpellNoTarget
        public static constant integer ORDER_ID = OrderId("avatar")
       
 	public static constant integer AVATAR_BUFF = 'BHav'
	//integer buffId
	integer hitPointBonus
	integer damageBonus
	real defenseBonus
        
        public static method create takes integer aliasId returns thistype
		return .allocate(aliasId, ORDER_ID)
        endmethod
        
        method populateData takes gameobject editorData, integer level returns nothing
		this.hitPointBonus = R2I(Math.floor(GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_B + I2S(level), 0)))
		this.damageBonus = R2I(Math.floor(GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_C + I2S(level), 0)))
		this.defenseBonus = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
		setCastingPrimaryTag(PrimaryTags.MORPH)
        endmethod

        method doEffect takes unit caster returns boolean
		call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_CASTER, caster, DEFAULT_ATTACH_POINTS))
		call AddUnitAbility(caster, BuffAvatar.create(AVATAR_BUFF, getDuration(), hitPointBonus, damageBonus, defenseBonus))
		return false
        endmethod
    
    endstruct

    private function register takes nothing returns nothing
        call RegisterAbilityStructType('AHav', AbilityAvatar)
    endfunction
endscope
