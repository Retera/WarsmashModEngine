scope DivineShield initializer register
    struct BuffDevotion extends BuffTimed
        public static method create takes integer aliasId, real duration returns thistype
            return .allocate(aliasId, duration)
        endmethod
        
        method isTimedLifeBar takes nothing returns boolean
            return false
        endmethod
        
        method onBuffAdd takes unit target returns nothing
            SetUnitInvulnerable(target, true)
        endmethod
        
        method onBuffRemove takes unit target returns nothing
            SetUnitInvulnerable(target, false)
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
        
        method doEffect takes unit caster, abilitytarget target returns boolean
            call AddUnitAbility(caster, BuffDevotion.create(buffId, getDuration()))
            return false
        endmethod
    
    endstruct

    private function register takes nothing returns nothing
        call RegisterAbilityStructType('AHds', AbilityDivineShield)
    endfunction
endscope
