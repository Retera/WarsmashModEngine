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

public abstract class CAbilitySpellBase extends AbstractGenericSingleIconNoSmartActiveAbility implements CAbilitySpell {
	private int manaCost;
	private float castRange;
	private float cooldown;
	private float castingTime;
	private EnumSet<CTargetType> targetsAllowed;
	private float cooldownRemaining;
	private PrimaryTag castingPrimaryTag;
	private EnumSet<SecondaryTag> castingSecondaryTags;

	public CAbilitySpellBase(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public final void populate(final MutableGameObject worldEditorAbility, final int level) {
		this.manaCost = worldEditorAbility.getFieldAsInteger(AbilityFields.MANA_COST, level);
		this.castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE, level);
		this.cooldown = worldEditorAbility.readSLKTagFloat("Cool" + level);
		this.castingTime = worldEditorAbility.getFieldAsFloat(AbilityFields.CASTING_TIME, level);
		this.targetsAllowed = CTargetType
				.parseTargetTypeSet(worldEditorAbility.getFieldAsString(AbilityFields.TARGETS_ALLOWED, level));
		final String animNames = worldEditorAbility.getFieldAsString(AbilityFields.ANIM_NAMES, 0);

		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		this.castingSecondaryTags = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, this.castingSecondaryTags, animNames);
		if (primaryTags.isEmpty()) {
			this.castingPrimaryTag = null;
		}
		else {
			this.castingPrimaryTag = primaryTags.iterator().next();
		}
		if (this.castingSecondaryTags.isEmpty()) {
			this.castingSecondaryTags = SequenceUtils.SPELL;
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
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
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
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.useOk();
			return;
		}
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
		return this.manaCost;
	}

	@Override
	public int getUIManaCost() {
		return this.manaCost;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getCooldown() {
		return this.cooldown;
	}

	public float getCastingTime() {
		return this.castingTime;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public float getCooldownRemaining() {
		return this.cooldownRemaining;
	}

	public void setManaCost(final int manaCost) {
		this.manaCost = manaCost;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public void setCooldown(final float cooldown) {
		this.cooldown = cooldown;
	}

	public void setCastingTime(final float castingTime) {
		this.castingTime = castingTime;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setCooldownRemaining(final float cooldownRemaining) {
		this.cooldownRemaining = cooldownRemaining;
	}

	public PrimaryTag getCastingPrimaryTag() {
		return this.castingPrimaryTag;
	}

	public void setCastingPrimaryTag(final PrimaryTag castingPrimaryTag) {
		this.castingPrimaryTag = castingPrimaryTag;
	}

	public EnumSet<SecondaryTag> getCastingSecondaryTags() {
		return this.castingSecondaryTags;
	}

	public void setCastingSecondaryTags(final EnumSet<SecondaryTag> castingSecondaryTags) {
		this.castingSecondaryTags = castingSecondaryTags;
	}

}
