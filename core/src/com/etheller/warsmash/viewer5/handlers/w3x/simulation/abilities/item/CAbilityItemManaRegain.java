package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityItemManaRegain extends AbstractGenericSingleIconNoSmartActiveAbility {
	public static final War3ID CODE = War3ID.fromString("AIma");
	private final int manaToRegain;
	private final float cooldown;

	public CAbilityItemManaRegain(final int handleId, final War3ID code, final War3ID alias, final int manaToRegain,
			final float cooldown) {
		super(handleId, code, alias);
		this.manaToRegain = manaToRegain;
		this.cooldown = cooldown;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (orderId == getBaseOrderId()) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final float cooldownRemaining = CAbilitySpellBase.getCooldownRemaining(game, unit, CODE);
		if (cooldownRemaining > 0) {
			final float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, CODE)
					* WarsmashConstants.SIMULATION_STEP_TIME;
			receiver.cooldownNotYetReady(cooldownRemaining, cooldownLengthDisplay);
		}
		else if (unit.getMana() >= unit.getMaximumMana()) {
			receiver.activationCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_MANA);
		}
		else {
			receiver.useOk();
		}
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.replenishmana;
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if ((target == null) && (orderId == getBaseOrderId())) {
			caster.restoreMana(game, this.manaToRegain);
			game.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
			caster.beginCooldown(game, CODE, cooldown);
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
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
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.ITEM;
	}
}
