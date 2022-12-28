package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import java.util.EnumSet;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public abstract class CAbilitySpellBase extends AbstractGenericSingleIconNoSmartActiveAbility {
	private int manaCost;
	private float castRange;
	private float cooldown;
	private float castingTime;
	private EnumSet<CTargetType> targetsAllowed;
	private float cooldownRemaining;
	private PrimaryTag castingPrimaryTag;
	private EnumSet<SecondaryTag> castingSecondaryTags;

	public CAbilitySpellBase(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	public final void populate(MutableGameObject worldEditorAbility, int level) {
		manaCost = worldEditorAbility.getFieldAsInteger(AbilityFields.MANA_COST, level);
		castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE, level);
		cooldown = worldEditorAbility.getFieldAsFloat(AbilityFields.COOLDOWN, level);
		castingTime = worldEditorAbility.getFieldAsFloat(AbilityFields.CASTING_TIME, level);
		targetsAllowed = CTargetType
				.parseTargetTypeSet(worldEditorAbility.getFieldAsString(AbilityFields.TARGETS_ALLOWED, level));
		String animNames = worldEditorAbility.getFieldAsString(AbilityFields.ANIM_NAMES, 0);

		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		castingSecondaryTags = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, castingSecondaryTags, animNames);
		if (primaryTags.isEmpty()) {
			castingPrimaryTag = null;
		}
		else {
			castingPrimaryTag = primaryTags.iterator().next();
		}
		if (castingSecondaryTags.isEmpty()) {
			castingSecondaryTags = SequenceUtils.SPELL;
		}

		populateData(worldEditorAbility, level);
	}

	public abstract void populateData(MutableGameObject worldEditorAbility, int level);

	public abstract boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target);

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		// TODO instead of ability cooldown, unit should have a per-code cooldown
		// pool probably so that when items are removed and added the item cooldown
		// is retained
		if (this.cooldownRemaining > 0) {
			this.cooldownRemaining -= WarsmashConstants.SIMULATION_STEP_TIME;
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.cooldownRemaining > 0) {
			receiver.cooldownNotYetReady(this.cooldownRemaining, this.cooldown);
		}
		else if (unit.getMana() < this.manaCost) {
			receiver.notEnoughResources(ResourceType.MANA);
		}
		else {
			receiver.useOk();
		}
	}

	public int getManaCost() {
		return manaCost;
	}

	@Override
	public int getUIManaCost() {
		return manaCost;
	}

	public float getCastRange() {
		return castRange;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getCastingTime() {
		return castingTime;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return targetsAllowed;
	}

	public float getCooldownRemaining() {
		return cooldownRemaining;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	public void setCastRange(float castRange) {
		this.castRange = castRange;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public void setCastingTime(float castingTime) {
		this.castingTime = castingTime;
	}

	public void setTargetsAllowed(EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setCooldownRemaining(float cooldownRemaining) {
		this.cooldownRemaining = cooldownRemaining;
	}

	public PrimaryTag getCastingPrimaryTag() {
		return castingPrimaryTag;
	}

	public EnumSet<SecondaryTag> getCastingSecondaryTags() {
		return castingSecondaryTags;
	}

}
