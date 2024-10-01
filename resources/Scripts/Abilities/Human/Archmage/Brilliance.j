scope Brilliance initializer register
	struct BuffBrilliance extends BuffAuraBase
		nonstackingstatbonustype statModifierType
		nonstackingstatbonus statModifier
		
		public static method create takes integer aliasId, real manaRegenerationIncrease, boolean percentBonus returns thistype
			thistype this = .allocate(aliasId)
			if percentBonus then
			    this.statModifierType = NON_STACKING_STAT_BONUS_TYPE_MPGENPCT
			else
			    this.statModifierType = NON_STACKING_STAT_BONUS_TYPE_MPGEN
			endif
			this.statModifier = CreateNonStackingStatBonus(this.statModifierType, "BHab", manaRegenerationIncrease)
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
			AddUnitNonStackingStatBonus(target, this.statModifier)
			RecomputeStatBonusesOnUnit(target, this.statModifierType)
		endmethod

		method onBuffRemove takes unit target returns nothing
			RemoveUnitNonStackingStatBonus(target, this.statModifier)
			RecomputeStatBonusesOnUnit(target, this.statModifierType)
		endmethod
	endstruct

	struct AbilityBrilliance extends AbilityAuraBase
		static constant integer ORDER_ID = OrderId("brilliance")

		real manaRegenerationIncrease
		boolean percentBonus

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method populateAuraData takes gameobject editorData, integer level returns nothing
			this.manaRegenerationIncrease = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
			this.percentBonus = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_DATA_B + I2S(level), 0)
		endmethod

		method createBuff takes unit source, unit enumUnit returns BuffAuraBase
			return BuffBrilliance.create(getBuffId(), this.manaRegenerationIncrease, this.percentBonus)
		endmethod

	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHab', AbilityBrilliance)
	endfunction
endscope
