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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public abstract class CAbilitySpellBase extends AbstractGenericSingleIconNoSmartActiveAbility implements CAbilitySpell {
	private int manaCost;
	private float castRange;
	private float cooldown;
	private float castingTime;
	private EnumSet<CTargetType> targetsAllowed;
	private PrimaryTag castingPrimaryTag;
	private EnumSet<SecondaryTag> castingSecondaryTags;
	private float duration;
	private float heroDuration;
	private War3ID code;

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
		this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION, 0);
		this.heroDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.HERO_DURATION, 0);

		this.code = worldEditorAbility.getCode();

		populateData(worldEditorAbility, level);
	}

	public float getDurationForTarget(CWidget target) {
		CUnit unit = target.visit(AbilityTargetVisitor.UNIT);
		return getDurationForTarget(unit);
	}

	public float getDurationForTarget(CUnit targetUnit) {
		if(targetUnit != null && targetUnit.isHero()) {
			return getHeroDuration();
		}
		return getDuration();
	}

	public float getDuration() {
		return duration;
	}

	public float getHeroDuration() {
		return heroDuration;
	}

	public abstract void populateData(MutableGameObject worldEditorAbility, int level);

	public abstract boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target);

	public boolean doChannelTick(CSimulation simulation, CUnit caster, AbilityTarget target) {
		return false;
	}

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
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.useOk();
			return;
		}
		float cooldownRemaining = getCooldownRemaining(game, unit);
		if (cooldownRemaining > 0) {
			float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCode()) * WarsmashConstants.SIMULATION_STEP_TIME;
			receiver.cooldownNotYetReady(cooldownRemaining, cooldownLengthDisplay);
		}
		else if (unit.getMana() < this.manaCost) {
			receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
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

	public float getCooldownRemaining(final CSimulation game, final CUnit caster) {
		return caster.getCooldownRemainingTicks(game, getCode()) * WarsmashConstants.SIMULATION_STEP_TIME;
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

	public War3ID getCode() {
		return code;
	}
}
