package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.util.CBuffTimed;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CBuffWhirlWindCaster extends CBuffTimed {
	private final CAbilityWhirlWind abilityImmolation;
	private int nextDamageTick;
	private final Rectangle recycleRect = new Rectangle();
	private StateModBuff disableAttack;

	public CBuffWhirlWindCaster(final int handleId, final War3ID alias, final CAbilityWhirlWind abilityImmolation,
			float duration) {
		super(handleId, alias, alias, duration);
		this.abilityImmolation = abilityImmolation;
	}

	@Override
	protected void onBuffAdd(CSimulation game, CUnit unit) {
		unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.SPIN);
		unit.getUnitAnimationListener().forceResetCurrentAnimation();
		unit.addUnitType(game, CUnitTypeJass.MAGIC_IMMUNE);
		this.disableAttack = new StateModBuff(StateModBuffType.DISABLE_ATTACK, 1);
		unit.addStateModBuff(this.disableAttack);
	}

	@Override
	protected void onBuffRemove(CSimulation game, CUnit unit) {
		unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.SPIN);
		unit.getUnitAnimationListener().forceResetCurrentAnimation();
		unit.removeUnitType(game, CUnitTypeJass.MAGIC_IMMUNE);
		unit.removeStateModBuff(this.disableAttack);
	}

	@Override
	public void onTick(final CSimulation game, final CUnit caster) {
		super.onTick(game, caster);
		final int currentTick = game.getGameTurnTick();
		if (currentTick >= this.nextDamageTick) {
			final int delayTicks = (int) (this.abilityImmolation.getDamageInterval()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final float areaOfEffect = CBuffWhirlWindCaster.this.abilityImmolation.getAreaOfEffect();
			this.nextDamageTick = currentTick + delayTicks;
			this.recycleRect.set(caster.getX() - areaOfEffect, caster.getY() - areaOfEffect, areaOfEffect * 2,
					areaOfEffect * 2);
			game.getWorldCollision().enumUnitsInRect(this.recycleRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit enumUnit) {
					if (caster.canReach(enumUnit, areaOfEffect) && enumUnit.canBeTargetedBy(game, caster,
							CBuffWhirlWindCaster.this.abilityImmolation.getTargetsAllowed())) {
						enumUnit.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.NORMAL,
								CWeaponSoundTypeJass.WHOKNOWS.name(),
								CBuffWhirlWindCaster.this.abilityImmolation.getDamagePerSecond());
					}
					return false;
				}
			});
		}
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

}
