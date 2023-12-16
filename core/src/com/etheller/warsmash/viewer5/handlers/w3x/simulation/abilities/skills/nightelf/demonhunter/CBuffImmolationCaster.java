package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CBuffImmolationCaster extends AbstractCBuff {
	private final CAbilityImmolation abilityImmolation;
	private SimulationRenderComponent fx;
	private int nextDamageTick;
	private final Rectangle recycleRect = new Rectangle();

	public CBuffImmolationCaster(final int handleId, final War3ID alias, final CAbilityImmolation abilityImmolation) {
		super(handleId, alias, alias);
		this.abilityImmolation = abilityImmolation;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.fx = game.createPersistentSpellEffectOnUnit(unit, getAlias(), CEffectType.TARGET, 0);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.fx.remove();
	}

	@Override
	public void onTick(final CSimulation game, final CUnit caster) {
		final int currentTick = game.getGameTurnTick();
		if (currentTick >= this.nextDamageTick) {
			final int delayTicks = (int) (this.abilityImmolation.getDuration()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final float areaOfEffect = CBuffImmolationCaster.this.abilityImmolation.getAreaOfEffect();
			this.nextDamageTick = currentTick + delayTicks;
			this.recycleRect.set(caster.getX() - areaOfEffect, caster.getY() - areaOfEffect, areaOfEffect * 2,
					areaOfEffect * 2);
			game.getWorldCollision().enumUnitsInRect(this.recycleRect, new CUnitEnumFunction() {
				@Override
				public boolean call(final CUnit enumUnit) {
					if (caster.canReach(enumUnit, areaOfEffect) && enumUnit.canBeTargetedBy(game, caster,
							CBuffImmolationCaster.this.abilityImmolation.getTargetsAllowed())) {
						enumUnit.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.FIRE, CWeaponSoundTypeJass.WHOKNOWS.name(),
								CBuffImmolationCaster.this.abilityImmolation.getDamagePerInterval());
						game.createPersistentSpellEffectOnUnit(enumUnit, CBuffImmolationCaster.this.abilityImmolation.getBuffId(),
								CEffectType.SPECIAL, 0).remove();
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
	public float getDurationRemaining(final CSimulation game, final CUnit unit) {
		return 0;
	}

	@Override
	public float getDurationMax() {
		return 0;
	}

	@Override
	public boolean isTimedLifeBar() {
		return false;
	}

}
