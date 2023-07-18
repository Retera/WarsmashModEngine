package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold;

import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.cargohold.CBehaviorLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TeamType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityLoad extends AbstractGenericSingleIconActiveAbility {
	private float castRange;
	private Set<War3ID> allowedUnitTypes;
	private CBehaviorLoad behaviorLoad;

	public CAbilityLoad(final int handleId, final War3ID alias, final float castRange,
			final Set<War3ID> allowedUnitTypes) {
		super(handleId, alias);
		this.castRange = castRange;
		this.allowedUnitTypes = allowedUnitTypes;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorLoad = new CBehaviorLoad(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.load;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorLoad.reset(target);
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
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if((target instanceof CUnit)) {
			if (target.canBeTargetedBy(game, unit, unit.getCargoData().getTargetsAllowed(), receiver)) {
				if(target != unit) {
					if (((CUnit) target).getPlayerIndex() == unit.getPlayerIndex()) {
						if (this.allowedUnitTypes.isEmpty() || this.allowedUnitTypes.contains(((CUnit) target).getTypeId())) {
							if (!unit.isMovementDisabled() || unit.canReach(target, unit.getCargoData().getCastRange())) {
								receiver.targetOk(target);
							}
							else {
								receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
							}
						}
						else {
							receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_PEON);
						}
					}
					else {
						receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ONE_OF_YOUR_OWN_UNITS);
					}
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_SELF);
				}
			}
			// else receiver called by canBeTargetedBy(...)
		} else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	public float getCastRange() {
		return this.castRange;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public Set<War3ID> getAllowedUnitTypes() {
		return this.allowedUnitTypes;
	}

	public void setAllowedUnitTypes(final Set<War3ID> allowedUnitTypes) {
		this.allowedUnitTypes = allowedUnitTypes;
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}
}
