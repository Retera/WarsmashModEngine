scope Resurrect initializer register
    struct AbilityResurrect extends AbilitySpellNoTarget
        public static constant integer ORDER_ID = OrderId("resurrection")
        
	integer numberOfCorpsesRaised
	group resurrectGroup
	filterfunc deadUnitFilter
        
        public static method create takes integer aliasId returns thistype
		local thistype this =  .allocate(aliasId, ORDER_ID)
		this.deadUnitFilter = Filter(method this.unitInRangeFilter)
		this.resurrectGroup = CreateGroup()
		return this
        endmethod
        
        method populateData takes gameobject editorData, integer level returns nothing
		this.numberOfCorpsesRaised = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
        endmethod

	private method unitInRangeFilter takes nothing returns boolean
		unit filterUnit = GetFilterUnit()
		unit caster = this.behavior.behavingUnit // NOTE: not good API to get caster, what should API be instead?
		return GetUnitTargetError(filterUnit, caster, getTargetsAllowed(), false) == null
	endmethod

	private method unitInRangeEnum takes nothing returns nothing
		unit enumUnit = GetEnumUnit()
		call ResurrectUnit(enumUnit)
		call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_TARGET, enumUnit, DEFAULT_ATTACH_POINTS))
	endmethod

	method checkTarget takes unit caster returns nothing
		call GroupClear(resurrectGroup)
		call GroupEnumUnitsInRangeOfUnitCounted(resurrectGroup, caster, getAreaOfEffect(), this.deadUnitFilter, this.numberOfCorpsesRaised)
		if GroupGetSize(resurrectGroup) == 0 then
			call FailTargetCheckWithMessage(this.abilityButton, COMMAND_STRING_ERROR_KEY_THERE_ARE_NO_CORPSES_OF_FRIENDLY_UNITS_NEARBY)
		else
			call PassTargetCheck(this.abilityButton, null)
		endif
	endmethod
        
        method doEffect takes unit caster returns boolean
		call GroupClear(resurrectGroup)
		call GroupEnumUnitsInRangeOfUnitCounted(resurrectGroup, caster, getAreaOfEffect(), this.deadUnitFilter, this.numberOfCorpsesRaised)
		call ForGroup(resurrectGroup, method this.unitInRangeEnum)
		call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_CASTER, caster, DEFAULT_ATTACH_POINTS))
		return false
        endmethod
    
    endstruct

    private function register takes nothing returns nothing
        call RegisterAbilityStructType('AHre', AbilityResurrect)
    endfunction
endscope
