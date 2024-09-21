scope Devotion initializer register
	struct BuffDevotion extends BuffAuraBase
		real armorBonus
		boolean percentBonus
		
		public static method create takes integer aliasId, real armorBonus, boolean percentBonus returns thistype
			thistype this = .allocate(aliasId)
			this.armorBonus = armorBonus
			this.percentBonus = percentBonus
			return this
		endmethod

		method onBuffAdd takes unit target returns nothing

		endmethod

		method onBuffRemove takes unit target returns nothing

		endmethod
	endstruct

	struct AbilityDevotion extends AbilityAuraBase
		static constant integer ORDER_ID = OrderId("devotion")

		real armorBonus
		boolean percentBonus

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
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
