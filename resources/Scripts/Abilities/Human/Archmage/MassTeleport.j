scope MassTeleport initializer register
	struct AbilityMassTeleport extends AbilitySpellTargetWidget
		static constant integer ORDER_ID = OrderId("massteleport")

		integer numberOfUnitsTeleported
		boolean useTeleportClustering
		real castingDelay

		integer channelEndTick
		effect sourceAreaEffect
		effect targetAreaEffect
		filterfunc teleportedTargetFilter = Filter(method this.unitInRangeFilter)

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method destroy takes nothing returns nothing
			call DestroyBoolExpr(teleportedTargetFilter)
			call this.deallocate()
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
			this.numberOfUnitsTeleported = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
			this.useTeleportClustering = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_DATA_C + I2S(level), 0)
			this.castingDelay = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_B + I2S(level), 0)
		endmethod

		method doEffect takes unit caster, abilitytarget target returns boolean
			this.channelEndTick = GetGameTurnTick() + R2I(Math.ceil(castingDelay / au_SIMULATION_STEP_TIME))
			this.sourceAreaEffect = AddSpellEffectById(getAliasId(), EFFECT_TYPE_AREA_EFFECT, GetUnitX(caster), GetUnitY(caster))
			this.targetAreaEffect = AddSpellEffectById(getAliasId(), EFFECT_TYPE_AREA_EFFECT, GetAbilityTargetX(target), GetAbilityTargetY(target))
			call DestroyEffect(AddSpellEffectTargetById(getAliasId(), EFFECT_TYPE_CASTER, target, DEFAULT_ATTACH_POINTS))
			unit targetUnit = unit(target)
			if targetUnit != null then
				call UnitAddType(targetUnit, UNIT_TYPE_STUNNED)
			endif
			return true
		endmethod

		private method unitInRangeFilter takes nothing returns boolean
			unit filterUnit = GetFilterUnit()
			unit caster = this.behavior.behavingUnit // TODO better API for getting caster
			return filterUnit != caster and GetUnitTargetError(filterUnit, caster, getTargetsAllowed(), false) == null
		endmethod

		method doChannelTick takes unit caster, abilitytarget target returns boolean
			integer gameTurnTick = GetGameTurnTick()
			if (gameTurnTick >= this.channelEndTick) then
				group teleportingUnits = CreateGroup()
				real casterX = GetUnitX(caster)
				real casterY = GetUnitY(caster)
				real targetX = GetAbilityTargetX(target)
				real targetY = GetAbilityTargetY(target)
				call GroupEnumUnitsInRangeCounted(teleportingUnits, casterX, casterY, getAreaOfEffect(), teleportedTargetFilter, numberOfUnitsTeleported)
				call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, casterX, casterY))
				call SetUnitPosition(caster, targetX, targetY)
				call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, GetUnitX(caster), GetUnitY(caster)))
				integer teleportingUnitIndex = 0
				integer teleportingUnitsCount = GroupGetSize(teleportingUnits)
				unit teleportingUnit
				if (this.useTeleportClustering) then
					loop
						exitwhen teleportingUnitIndex >= teleportingUnitsCount
						teleportingUnit = GroupUnitAt(teleportingUnits, teleportingUnitIndex)
						call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, GetUnitX(teleportingUnit), GetUnitY(teleportingUnit)))
						call SetUnitPosition(teleportingUnit, targetX, targetY)
						call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, GetUnitX(teleportingUnit), GetUnitY(teleportingUnit)))
						teleportingUnitIndex++
					endloop

				else
					loop
						exitwhen teleportingUnitIndex >= teleportingUnitsCount
						teleportingUnit = GroupUnitAt(teleportingUnits, teleportingUnitIndex)
						call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, GetUnitX(teleportingUnit), GetUnitY(teleportingUnit)))
						call SetUnitPosition(teleportingUnit, targetX + (GetUnitX(teleportingUnit) - casterX), targetY + (GetUnitY(teleportingUnit) - casterY))
						call DestroyEffect(AddSpellEffectById(getAliasId(), EFFECT_TYPE_SPECIAL, GetUnitX(teleportingUnit), GetUnitY(teleportingUnit)))
						
						teleportingUnitIndex++
					endloop

				endif
				call DestroyGroup(teleportingUnits)
				return false
			endif
			return true
		endmethod

		method doChannelEnd takes unit caster, abilitytarget target, boolean interrupted returns nothing
			call DestroyEffect(sourceAreaEffect)
			sourceAreaEffect = null
			call DestroyEffect(targetAreaEffect)
			targetAreaEffect = null
			unit targetUnit = unit(target)
			if targetUnit != null then
				call UnitRemoveType(targetUnit, UNIT_TYPE_STUNNED)
			endif
		endmethod
	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHmt', AbilityMassTeleport)
	endfunction
endscope
