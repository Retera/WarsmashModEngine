scope DivineShield initializer register
    struct BuffDivineShield extends BuffTimed
	statemod invulnerableBonus = CreateStateMod(STATE_MOD_TYPE_INVULNERABLE, 1)

        public static method create takes integer aliasId, real duration returns thistype
            return .allocate(aliasId, duration)
        endmethod
        
        method isTimedLifeBar takes nothing returns boolean
            return false
        endmethod
        
        method onBuffAdd takes unit target returns nothing
            AddUnitStateMod(target, invulnerableBonus)
	    RecomputeStateModsOnUnit(target, STATE_MOD_TYPE_INVULNERABLE)
        endmethod
        
        method onBuffRemove takes unit target returns nothing
            RemoveUnitStateMod(target, invulnerableBonus)
	    RecomputeStateModsOnUnit(target, STATE_MOD_TYPE_INVULNERABLE)
        endmethod
    endstruct

    struct AbilityDivineShield extends AbilitySpellInstant
        public static constant integer ORDER_ID = OrderId("divineshield")
        
        boolean canDeactivate
        integer buffId
        
        public static method create takes integer aliasId returns thistype
            return .allocate(aliasId, ORDER_ID)
        endmethod
        
        method populateData takes gameobject editorData, integer level returns nothing
            this.canDeactivate = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
            this.buffId = GetGameObjectBuffID(editorData, level, 0)
        endmethod
        
        method doEffect takes unit caster returns boolean
            call AddUnitAbility(caster, BuffDivineShield.create(buffId, getDuration()))
            return false
        endmethod
    
    endstruct

    private function register takes nothing returns nothing
        call RegisterAbilityStructType('AHds', AbilityDivineShield)
    endfunction
endscope
