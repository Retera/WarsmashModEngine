scope WarStomp
    struct AbilityWarStomp extends AbilitySpellNoTarget
		static constant integer ORDER_ID = OrderId("stomp")
		
        real damage
        integer buffId
		filterfunc enumFilter
        
        public static method create takes integer aliasId returns thistype
            local thistype this = .allocate(aliasId, ORDER_ID)
			this.enumFilter = Filter(method this.unitInRangeEnum)
            return this
        endmethod

	method destroy takes nothing returns nothing
		call DestroyBoolExpr(enumFilter)
		call this.deallocate()
	endmethod
        
        method populateData takes gameobject editorData, integer level returns nothing
            set damage = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
            set buffId = GetGameObjectBuffID(editorData, level, 0)
        endmethod
        
		private method unitInRangeEnum takes nothing returns boolean
			unit enumUnit = GetFilterUnit()
			if (GetUnitTargetError(enumUnit, source, getTargetsAllowed(), false) == null) then
// check effect reaction
			endif
			return false
		endmethod
    
        method doEffect takes unit caster, abilitytarget target returns boolean
			call GroupEnumUnitsInRangeOfUnit(null, caster, getAreaOfEffect(), this.enumFilter)
            return false
        endmethod
    endstruct

endscope
