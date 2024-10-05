scope ThunderClap initializer register
    struct AbilityThunderClap extends AbilitySpellNoTarget
        public static constant integer ORDER_ID = OrderId("thunderclap")
        
	real damage
	integer buffId
	real attackSpeedReductionPercent
	real movementSpeedReductionPercent
	filterfunc areaUnitFilter
        
        public static method create takes integer aliasId returns thistype
		local thistype this =  .allocate(aliasId, ORDER_ID)
		this.areaUnitFilter = Filter(method this.unitInRangeEnum)
		return this
        endmethod
        
        method populateData takes gameobject editorData, integer level returns nothing
		this.damage = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
		this.buffId = GetGameObjectBuffID(editorData, level, 0)
		this.attackSpeedReductionPercent = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_D + I2S(level), 0)
		this.movementSpeedReductionPercent = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_C + I2S(level), 0)
        endmethod

	private method unitInRangeEnum takes nothing returns boolean
		unit enumUnit = GetFilterUnit()
		unit caster = this.behavior.behavingUnit // NOTE: not good API to get caster, what should API be instead?
		if (not IsUnitAlly(enumUnit, GetOwningPlayer(caster)) and GetUnitTargetError(enumUnit, caster, getTargetsAllowed(), false) == null) then
			if CheckUnitForAbilityEffectReaction(enumUnit, caster, this) then
				call AddUnitAbility(enumUnit, BuffTimedSlow.create(buffId, getDurationForTarget(enumUnit), "BHtc", attackSpeedReductionPercent, movementSpeedReductionPercent))
				call UnitDamageTarget(caster, enumUnit, damage, false, true, ATTACK_TYPE_NORMAL, DAMAGE_TYPE_LIGHTNING, WEAPON_TYPE_WHOKNOWS)
			endif
		endif
		return false
	endmethod
        
        method doEffect takes unit caster returns boolean
		call GroupEnumUnitsInRangeOfUnit(null, caster, getAreaOfEffect(), this.areaUnitFilter)
		call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_CASTER, GetUnitX(caster), GetUnitY(caster)))
		return false
        endmethod
    
    endstruct

    private function register takes nothing returns nothing
        call RegisterAbilityStructType('AHtc', AbilityThunderClap)
    endfunction
endscope
