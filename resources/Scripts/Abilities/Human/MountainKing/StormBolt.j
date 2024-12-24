scope StormBolt initializer register
	struct StormBoltMissile extends Projectile
		real damage
		integer buffId
		real duration
		ability sourceAbility

		public static method create takes unit source, integer spellAlias, real launchX, real launchY, real launchFacing, real speed, boolean homing, abilitytarget target, real damage, integer buffId, real duration, ability sourceAbility returns thistype
			local thistype this = .allocate(source, spellAlias, launchX, launchY, launchFacing, speed, homing, target)
			set this.damage = damage
			set this.buffId = buffId
			set this.duration = duration
			set this.sourceAbility = sourceAbility
			return this
		endmethod


		method onHit takes abilitytarget target returns nothing
			unit targetUnit = unit(target)
			if targetUnit != null then
				unit casterUnit = GetProjectileSource(this)
				// check if they managed to deflect the projectile somehow
				if CheckUnitForAbilityProjReaction(targetUnit, casterUnit, this) then
					// check if they managed to block our spell effect with Amulet of Spell Shield
					if CheckUnitForAbilityEffectReaction(targetUnit, casterUnit, sourceAbility) then
						call AddUnitAbility(targetUnit, BuffStun.create(buffId, duration))
						call UnitDamageTarget(casterUnit, targetUnit, damage, false, true, ATTACK_TYPE_NORMAL, DAMAGE_TYPE_LIGHTNING, WEAPON_TYPE_WHOKNOWS)
					endif
				endif
			endif
		endmethod
	endstruct

	struct AbilityStormBolt extends AbilitySpellTargetWidget
		static constant integer ORDER_ID = OrderId("thunderbolt")

		real damage
		real projectileSpeed
		boolean projectileHomingEnabled
		integer buffId

		public static method create takes integer aliasId returns thistype
			return .allocate(aliasId, ORDER_ID)
		endmethod

		method populateData takes gameobject editorData, integer level returns nothing
			this.damage = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_DATA_A + I2S(level), 0)
			this.projectileSpeed = GetGameObjectFieldAsReal(editorData, ABILITY_FIELD_PROJECTILE_SPEED, 0)
			this.projectileHomingEnabled = GetGameObjectFieldAsBoolean(editorData, ABILITY_FIELD_PROJECTILE_HOMING_ENABLED, 0)
			this.buffId = GetGameObjectBuffID(editorData, level, 0)
		endmethod

		method doEffect takes unit caster, abilitytarget target returns boolean
			unit targetUnit = unit(target)
			if targetUnit != null then
				// TODO something better than AngleBetweenPoints, maybe
				StormBoltMissile.create(targetUnit, getAliasId(), GetUnitX(caster), GetUnitY(caster), AngleBetweenPoints(GetUnitLoc(caster), GetUnitLoc(targetUnit)), projectileSpeed, projectileHomingEnabled, target, damage, buffId, getDurationForTarget(targetUnit), this)
			endif
			return false
		endmethod
	endstruct

	private function register takes nothing returns nothing
		call RegisterAbilityStructType('AHtb', AbilityStormBolt)
	endfunction
endscope
