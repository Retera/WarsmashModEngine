package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.warden;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityBlink extends CAbilityPointTargetSpellBase {

	private float minimumRange;
	private float maximumRange;

	public CAbilityBlink(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.blink;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		maximumRange = worldEditorAbility.getFieldAsFloat(AbilityFields.Blink.MAXIMUM_RANGE, level);
		minimumRange = worldEditorAbility.getFieldAsFloat(AbilityFields.Blink.MINIMUM_RANGE, level);
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
									   AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (unit.distance(target) < minimumRange) {
			receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_INSIDE_MINIMUM_RANGE);
		}
		else {
			super.innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		float casterX = caster.getX();
		float casterY = caster.getY();
		simulation.spawnSpellEffectOnPoint(casterX, casterY, 0, getAlias(), CEffectType.AREA_EFFECT, 0).remove();
		double distance = caster.distance(target);
		float resultingX = target.getX();
		float resultingY = target.getY();
		if (distance > maximumRange) {
			double angleTo = caster.angleTo(target);
			resultingX = casterX + (float) (StrictMath.cos(angleTo) * maximumRange);
			resultingY = casterY + (float) (StrictMath.sin(angleTo) * maximumRange);
		}
		caster.setPointAndCheckUnstuck(resultingX, resultingY, simulation);
		simulation.spawnSpellEffectOnPoint(caster.getX(), caster.getY(), 0, getAlias(), CEffectType.SPECIAL, 0).remove();
		return false;
	}
}
