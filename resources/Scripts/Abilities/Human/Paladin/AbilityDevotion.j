scope Devotion initializer register
	struct BuffDevotion extends BuffAuraBase
		nonstackingstatbufftype statModifierType
		nonstackingstatbuff statModifier
		
		public static method create takes integer aliasId, real armorBonus, boolean percentBonus returns thistype
			thistype this = .allocate(aliasId)
			if percentBonus then
			    this.statModifierType = NON_STACKING_STAT_BUFF_TYPE_DEFPCT
			else
			    this.statModifierType = NON_STACKING_STAT_BUFF_TYPE_DEF
			endif
			this.statModifier = CreateNonStackingStatBuff(this.statModifierType, "BHad", armorBonus)
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing
            AddUnitNonStackingStatBuff(target, this.statModifier)
            RecomputeStatBuffsOnUnit(target, this.statModifierType)
		endmethod

		method onBuffRemove takes unit target returns nothing
            RemoveUnitNonStackingStatBuff(target, this.statModifier)
            RecomputeStatBuffsOnUnit(target, this.statModifierType)
		endmethod
	endstruct

	struct AbilityDevotion extends AbilityAuraBase
		static constant integer ORDER_ID = OrderId("devotion")

		real armorBonus
		boolean percentBonus

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method populateAuraData takes gameobject editorData, integer level returns nothing
			this.armorBonus = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
			this.percentBonus = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_DATA_B + I2S(level), 0)
		endmethod

		method createBuff takes unit source, unit enumUnit returns BuffAuraBase
			return BuffDevotion.create(getBuffId(), this.armorBonus, this.percentBonus)
		endmethod

	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHad', AbilityDevotion)
	endfunction
endscope
