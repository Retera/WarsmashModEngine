scope Blizzard initializer register
	// TODO: would be great for TargetFilter to be a lambda instead
	private struct TargetFilter
		filterfunc filter
		abilitytarget target
		real areaOfEffect
		unit caster
		targettypes targetsAllowed

		private method filterImpl takes nothing returns boolean
			unit possibleTarget = GetFilterUnit()
			return UnitCanReach(possibleTarget, target, areaOfEffect) and (GetUnitTargetError(possibleTarget, caster, targetsAllowed, false) == null)
		endmethod

		public static method create takes nothing returns thistype
			local thistype this = .allocate()
			set this.filter = Filter(method this.filterImpl)
			return this
		endmethod

		method destroy takes nothing returns nothing
			call DestroyBoolExpr(filter)
			call this.deallocate()
		endmethod

		method reset takes abilitytarget target, real areaOfEffect, unit caster, targettypes targetsAllowed returns filterfunc
			set this.target = target
			set this.areaOfEffect = areaOfEffect
			set this.caster = caster
			set this.targetsAllowed = targetsAllowed
			return this.filter
		endmethod
	endstruct

	struct AbilityBlizzard extends AbilitySpellTargetLocation
		static constant integer ORDER_ID = OrderId("blizzard")

		real buildingReduction
		real damage
		real damagePerSecond
		real maximumDamagePerWave
		integer shardCount
		integer waveCount
		real waveDelay
		integer effectId

		integer currentWave
		integer nextWaveTick
		boolean waveForDamage = false
		rect recycleRect = Rect(0,0,0,0)
		group damageTargets = CreateGroup()
		TargetFilter filter = TargetFilter.create()

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method destroy takes nothing returns nothing
			call this.filter.destroy()
			call this.deallocate()
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
			this.buildingReduction = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_D + I2S(level), 0)
			this.damage = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_B + I2S(level), 0)
			this.damagePerSecond = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_E + I2S(level), 0)
			this.maximumDamagePerWave = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_F + I2S(level), 0)
			this.shardCount = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_DATA_C + I2S(level), 0)
			this.waveCount = GetGameObjectFieldAsInteger(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
	
			this.waveDelay = getCastingTime()
			setCastingTime(0) // dont use the casting time field normally
			call SetOrderButtonAOE(this.abilityButton, getAreaOfEffect())
			this.effectId = GetGameObjectEffectID(editorData, level)
		endmethod

		method doEffect takes unit caster, abilitytarget target returns boolean
			this.currentWave = 0
			this.waveForDamage = false
			this.nextWaveTick = GetGameTurnTick() + R2I(Math.ceil(this.waveDelay / au_SIMULATION_STEP_TIME))
			return true
		endmethod

		method doChannelTick takes unit caster, abilitytarget target returns boolean
			if (GetGameTurnTick() >= this.nextWaveTick) then
				real targetX = GetAbilityTargetX(target)
				real targetY = GetAbilityTargetY(target)
				real waveDelay
				real areaOfEffect = getAreaOfEffect()
				if (this.waveForDamage) then
					this.currentWave++
					waveDelay = this.waveDelay
					this.waveForDamage = false
					call GroupClear(damageTargets)
					call SetRect(recycleRect, targetX - areaOfEffect, targetY - areaOfEffect, targetX + areaOfEffect, targetY + areaOfEffect)
					call GroupEnumUnitsInRect(damageTargets, recycleRect, filter.reset(target, areaOfEffect, caster, getTargetsAllowed()))
					real damagePerTarget = this.damage
					real damageTargetsSize = GroupGetSize(damageTargets)
					if ((damagePerTarget * damageTargetsSize) > maximumDamagePerWave) then
						damagePerTarget = maximumDamagePerWave / damageTargetsSize
					endif
					real damagePerTargetBuilding = damagePerTarget * buildingReduction
					integer damageTargetIndex = 0
					unit damageTarget = null
					real thisTargetDamage
					loop
						exitwhen damageTargetIndex >= damageTargetsSize

						damageTarget = GroupUnitAt(damageTargets, damageTargetIndex)
						if IsUnitType(damageTarget, UNIT_TYPE_STRUCTURE) then
							thisTargetDamage = damagePerTargetBuilding
						else
							thisTargetDamage = damagePerTarget
						endif
						call UnitDamageTarget(caster, damageTarget, thisTargetDamage, false, true, ATTACK_TYPE_NORMAL, DAMAGE_TYPE_COLD, WEAPON_TYPE_WHOKNOWS)

						damageTargetIndex++
					endloop
				else
					real randomAngle
					real randomDistance
					integer i = 0
					loop
						exitwhen i >= this.shardCount

						randomAngle = GetRandomReal(0, bj_PI * 2)
						randomDistance = GetRandomReal(0, areaOfEffect)
						call DestroyEffect(AddSpellEffectById(this.effectId, EFFECT_TYPE_EFFECT, targetX + Cos(randomAngle) * randomDistance, targetY + Sin(randomAngle) * randomDistance))
						call UnitSpellSoundEffect(caster, this.effectId)

						i++
					endloop
					waveDelay = 0.80
					this.waveForDamage = true
				endif
				this.nextWaveTick = GetGameTurnTick() + R2I(Math.ceil(waveDelay / au_SIMULATION_STEP_TIME))
			endif
			return this.currentWave < this.waveCount
		endmethod
	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHbz', AbilityBlizzard)
	endfunction
endscope
