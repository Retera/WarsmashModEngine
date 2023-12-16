package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.undead.deathknight;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityDeathPact extends CAbilityTargetSpellBase {

	private boolean leaveTargetAlive;
	private boolean lifeConversionAsValue;
	private boolean manaConversionAsValue;
	private float lifeConvertedToLife;
	private float lifeConvertedToMana;

	public CAbilityDeathPact(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.deathpact;
	}

	@Override
	protected void innerCheckCanUseSpell(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final boolean fullMana = unit.getMana() >= unit.getMaximumMana();
		final boolean fullHealth = unit.getLife() >= unit.getMaximumLife();
		if (leaveTargetAlive) {
			// Observed some bizarre edge case in testing that was attempted to be repeated
			// here, but it
			// might be incorrect
			if ((lifeConvertedToMana != 0) && fullMana) {
				if (lifeConvertedToLife != 0) {
					if (fullHealth) {
						receiver.activationCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_MANA_AND_HEALTH);
					}
					else {
						super.innerCheckCanUseSpell(game, unit, orderId, receiver);
					}
				}
				else {
					receiver.activationCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_MANA);
				}
			}
			else {
				super.innerCheckCanUseSpell(game, unit, orderId, receiver);
			}
		}
		else {
			// Below is the sane case, so if you have a problem with the "leave target
			// alive" branch above
			// (which to my knowledge is not used in melee emulation) then maybe copy the
			// following as the only
			// used case (since that would be sane anyway)
			if (lifeConvertedToLife != 0) {
				if (lifeConvertedToMana != 0) {
					if (fullMana && fullHealth) {
						receiver.activationCheckFailed(CommandStringErrorKeys.ALREADY_AT_FULL_MANA_AND_HEALTH);
					}
					else {
						super.innerCheckCanUseSpell(game, unit, orderId, receiver);
					}
				}
				else {
					if (fullHealth) {
						receiver.activationCheckFailed(CommandStringErrorKeys.HERO_HAS_FULL_HEALTH);
					}
					else {
						super.innerCheckCanUseSpell(game, unit, orderId, receiver);
					}
				}
			}
			else {
				if (fullMana) {
					receiver.activationCheckFailed(CommandStringErrorKeys.HERO_HAS_FULL_MANA);
				}
				else {
					super.innerCheckCanUseSpell(game, unit, orderId, receiver);
				}
			}
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			if (targetUnit.getClassifications().contains(CUnitClassification.UNDEAD)) {
				// funny stupid error case, this is to mimic the emulation and is nothing but
				// spaghetti
				// code. Best guess, maybe Wand of Mana Stealing came first?
				if (manaConversionAsValue && (lifeConvertedToMana != 0) && (targetUnit.getMana() == 0)) {
					receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_MANA);
				}
				else {
					super.innerCheckCanTarget(game, unit, orderId, target, receiver);
				}
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_UNDEAD_UNIT);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		leaveTargetAlive = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_E + level, 0);
		lifeConversionAsValue = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_D + level, 0);
		manaConversionAsValue = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_C + level, 0);
		lifeConvertedToLife = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		lifeConvertedToMana = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		simulation.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final float targetUnitLife = targetUnit.getLife();
			float lifeDrained = 0;
			if (lifeConvertedToLife != 0) {
				if (lifeConversionAsValue) {
					lifeDrained += lifeConvertedToLife;
					caster.heal(simulation, -lifeConvertedToLife);
				}
				else {
					final float lifeGained = lifeConvertedToLife * targetUnitLife;
					caster.heal(simulation, lifeGained);
				}
			}
			if (lifeConvertedToMana != 0) {
				if (manaConversionAsValue) {
					lifeDrained += lifeConvertedToMana;
					caster.restoreMana(simulation, -lifeConvertedToMana);
				}
				else {
					final float manaGained = lifeConvertedToMana * targetUnitLife;
					caster.restoreMana(simulation, manaGained);
				}
			}
			targetUnit.setLife(simulation, targetUnit.getLife() - lifeDrained);
			if (!leaveTargetAlive) {
				targetUnit.setRaisable(false);
				targetUnit.setDecays(false);
				targetUnit.kill(simulation);
			}
			simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
		}
		return false;
	}
}
