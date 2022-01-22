package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.blight;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityBlight extends AbstractGenericNoIconAbility {

	private boolean createsBlight;
	private float expansionAmount;
	private float areaOfEffect;
	private int ticksPerBlightExpansion;

	private float currentArea;
	private int lastExpansionTick;

	public CAbilityBlight(final int handleId, final War3ID alias, final boolean createsBlight,
			final float expansionAmount, final float areaOfEffect, final float gameSecondsPerBlightExpansion) {
		super(handleId, alias);
		this.createsBlight = createsBlight;
		this.expansionAmount = expansionAmount;
		this.areaOfEffect = areaOfEffect;
		setGameSecondsPerBlightExpansion(gameSecondsPerBlightExpansion);
	}

	public void setGameSecondsPerBlightExpansion(final float gameSecondsPerBlightExpansion) {
		this.ticksPerBlightExpansion = (int) StrictMath
				.ceil(gameSecondsPerBlightExpansion / WarsmashConstants.SIMULATION_STEP_TIME);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (!isDisabled()) {
			final int currentTick = game.getGameTurnTick();
			if ((currentTick - this.lastExpansionTick) >= this.ticksPerBlightExpansion) {
				if (this.currentArea < this.areaOfEffect) {
					this.currentArea = Math.min(this.areaOfEffect, this.currentArea + this.expansionAmount);
					game.setBlight(unit.getX(), unit.getY(), this.currentArea, this.createsBlight);
				}
				this.lastExpansionTick = currentTick;
			}
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

	public void setCreatesBlight(final boolean createsBlight) {
		this.createsBlight = createsBlight;
	}

	public void setExpansionAmount(final float expansionAmount) {
		this.expansionAmount = expansionAmount;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

}
