package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.neutral;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityRanged;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ExternStringMsgTargetCheckReceiver;

public class CAbilityWayGate extends AbstractGenericAliasedAbility
		implements GenericNoIconAbility, CAbilityRanged, CAbilitySpell {
	private static final War3ID CODE = War3ID.fromString("Awrp");
	private static final int ORDER_ID = OrderIds.smart;
	private float teleportAreaWidth;
	private float teleportAreaHeight;
	private float range;
	private boolean gateEnabled;
	private AbilityPointTarget destination;

	public CAbilityWayGate(int handleId, War3ID alias) {
		super(handleId, CODE, alias);
	}

	@Override
	public void populate(GameObject worldEditorAbility, int level) {
		this.teleportAreaWidth = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.teleportAreaHeight = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.range = Math.min(this.teleportAreaWidth, this.teleportAreaHeight) / 2;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {

	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {

	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {

	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {

	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {

	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		final CUnit unitTarget = target.visit(AbilityTargetVisitor.UNIT);
		if (unitTarget != null) {
			if (!unitTarget.isMovementDisabled()) {
				final float targetX = target.getX();
				if (Math.abs(targetX - caster.getX()) <= (this.teleportAreaWidth / 2)) {
					final float targetY = target.getY();
					if (Math.abs(targetY - caster.getY()) <= (this.teleportAreaHeight / 2)) {
						game.spawnTemporarySpellEffectOnPoint(targetX, targetY, 0, CAbilityMove.CODE,
								CEffectType.SPECIAL, 0);
						unitTarget.setPointAndCheckUnstuck(this.destination.x, this.destination.y, game);
						game.spawnTemporarySpellEffectOnPoint(target.getX(), target.getY(), 0, CAbilityMove.CODE,
								CEffectType.SPECIAL, 0);
					}
				}
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit caster, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == ORDER_ID) {
			final CUnit unitTarget = target.visit(AbilityTargetVisitor.UNIT);
			if (unitTarget != null) {
				if (!unitTarget.isMovementDisabled()) {
					final float targetX = target.getX();
					if (Math.abs(targetX - caster.getX()) <= (this.teleportAreaWidth / 2)) {
						final float targetY = target.getY();
						if (Math.abs(targetY - caster.getY()) <= (this.teleportAreaHeight / 2)) {
							receiver.targetOk(target);
						}
						else {
							receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
						}
					}
					else {
						receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
					}
				}
				else {
					receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_MOVEABLE_UNITS);
				}
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_MOVEABLE_UNITS);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return true;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.MOVEMENT;
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		if ((orderId == ORDER_ID) && this.gateEnabled) {
			receiver.useOk();
		}
		else {
			receiver.unknownReasonUseNotOk();
		}
	}

	@Override
	public float getCastRange() {
		return this.range;
	}

	@Override
	public <T> T visit(CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	public boolean isGateEnabled() {
		return this.gateEnabled;
	}

	public void setGateEnabled(boolean gateEnabled) {
		this.gateEnabled = gateEnabled;
	}

	public AbilityPointTarget getDestination() {
		return this.destination;
	}

	public void setDestination(AbilityPointTarget destination) {
		this.destination = destination;
	}

	public static CAbilityWayGate getWayGateAbility(final CSimulation game, final CUnit caster, final CUnit transport,
			final boolean ignoreRange, final boolean ignoreDisabled) {
		for (final CAbility potentialLoadAbility : transport.getAbilities()) {
			if (potentialLoadAbility instanceof CAbilityWayGate) {
				final CAbilityWayGate abilityLoad = (CAbilityWayGate) potentialLoadAbility;
				final BooleanAbilityActivationReceiver transportUnitReceiver = BooleanAbilityActivationReceiver.INSTANCE;
				abilityLoad.checkCanUse(game, transport, ORDER_ID, transportUnitReceiver);
				// NOTE: disabled load ability should enable later in case of under construction
				// entangled gold mine
				if (transportUnitReceiver.isOk() || (ignoreDisabled && abilityLoad.isDisabled())) {
					final ExternStringMsgTargetCheckReceiver<CWidget> transportUnitTargetCheckReceiver = ExternStringMsgTargetCheckReceiver
							.getInstance();
					abilityLoad.checkCanTarget(game, transport, ORDER_ID, caster,
							transportUnitTargetCheckReceiver.reset());
					if ((transportUnitTargetCheckReceiver.getTarget() != null)
							|| (ignoreRange && (transportUnitTargetCheckReceiver
									.getExternStringKey() == CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE))) {
						return abilityLoad;
					}
				}
			}
		}
		return null;
	}

}
