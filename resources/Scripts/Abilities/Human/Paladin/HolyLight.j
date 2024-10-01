scope HolyLight initializer register
	struct AbilityHolyLight extends AbilitySpellTargetWidget
		static constant integer ORDER_ID = OrderId("holybolt")

		real healAmount

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
			this.healAmount = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
		endmethod

                method checkTargetUnit takes unit caster, widget target returns nothing
			unit targetUnit = unit(target)
			if targetUnit != null then
                                boolean undead = IsUnitType(target, UNIT_TYPE_UNDEAD)
                                boolean ally = IsUnitAlly(target, GetOwningPlayer(caster))
                                if undead != ally then
                                        if ally and GetUnitState(target, UNIT_STATE_LIFE) >= GetUnitState(target, UNIT_STATE_MAX_LIFE) then
                                                call FailTargetCheckWithMessage(this.abilityButton, COMMAND_STRING_ERROR_KEY_ALREADY_AT_FULL_HEALTH)
                                        else
                                                call PassTargetCheck(this.abilityButton, target)
                                        endif
                                else
                                        call FailTargetCheckWithMessage(this.abilityButton, COMMAND_STRING_ERROR_KEY_MUST_TARGET_FRIENDLY_LIVING_UNITS_OR_ENEMY_UNDEAD_UNITS)
                                endif
                        else
                                call FailTargetCheckWithMessage(this.abilityButton, COMMAND_STRING_ERROR_KEY_MUST_TARGET_A_UNIT_WITH_THIS_ACTION)
                        endif
                endmethod

		method doEffect takes unit caster, abilitytarget target returns boolean
			unit targetUnit = unit(target)
			if targetUnit != null then
				if IsUnitType(targetUnit, UNIT_TYPE_UNDEAD) then
					if CheckUnitForAbilityEffectReaction(targetUnit, caster, this) then
						call UnitDamageTarget(caster, targetUnit, healAmount * 0.5, false, true, ATTACK_TYPE_NORMAL, DAMAGE_TYPE_DIVINE, WEAPON_TYPE_WHOKNOWS)
						call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_TARGET, targetUnit, DEFAULT_ATTACH_POINTS))
					endif
				else
					call HealUnit(targetUnit, healAmount)
					call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_TARGET, targetUnit, DEFAULT_ATTACH_POINTS))
				endif
			endif
			return false
		endmethod
	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHhb', AbilityHolyLight)
	endfunction
endscope
