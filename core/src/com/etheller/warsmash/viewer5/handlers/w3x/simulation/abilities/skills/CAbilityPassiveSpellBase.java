package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import java.util.EnumSet;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public abstract class CAbilityPassiveSpellBase extends AbilityGenericSingleIconPassiveAbility implements CAbilitySpell {
	private float castRange;
	private float areaOfEffect;
	private EnumSet<CTargetType> targetsAllowed;
	private float duration;
	private float heroDuration;
	private War3ID code;

	public CAbilityPassiveSpellBase(final int handleId, final War3ID code, final War3ID alias) {
		super(code, alias, handleId);
	}

	@Override
	public final void populate(final GameObject worldEditorAbility, final int level) {
		this.castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE + level, 0);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
		this.targetsAllowed = AbstractCAbilityTypeDefinition.getTargetsAllowed(worldEditorAbility, level);

		this.duration = worldEditorAbility.getFieldAsFloat(AbilityFields.DURATION + level, 0);
		this.heroDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.HERO_DURATION + level, 0);
		this.code = worldEditorAbility.getFieldAsWar3ID(AbilityFields.CODE, -1);

		populateData(worldEditorAbility, level);
	}

	public float getDurationForTarget(final CWidget target) {
		final CUnit unit = target.visit(AbilityTargetVisitor.UNIT);
		return getDurationForTarget(unit);
	}

	public float getDurationForTarget(final CUnit targetUnit) {
		if ((targetUnit != null) && targetUnit.isHero()) {
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

	public abstract void populateData(GameObject worldEditorAbility, int level);

	public float getCastRange() {
		return this.castRange;
	}

	public float getAreaOfEffect() {
		return areaOfEffect;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public void setAreaOfEffect(final float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	public void setHeroDuration(final float heroDuration) {
		this.heroDuration = heroDuration;
	}

	public War3ID getCode() {
		return code;
	}
}
