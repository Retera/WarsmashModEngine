package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public abstract class CAbilityPassiveSpellBase extends AbilityGenericSingleIconPassiveAbility implements CAbilitySpell {
	private float castRange;
	private float areaOfEffect;
	private EnumSet<CTargetType> targetsAllowed;
	private float duration;
	private float heroDuration;
	private War3ID code;

	public CAbilityPassiveSpellBase(int handleId, War3ID alias) {
		super(alias, handleId);
	}

	@Override
	public final void populate(final MutableObjectData.MutableGameObject worldEditorAbility, final int level) {
		this.castRange = worldEditorAbility.getFieldAsFloat(AbilityFields.CAST_RANGE, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
		this.targetsAllowed = CTargetType
				.parseTargetTypeSet(worldEditorAbility.getFieldAsString(AbilityFields.TARGETS_ALLOWED, level));

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

	public abstract void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level);


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

	public void setAreaOfEffect(float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	public void setTargetsAllowed(final EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void setHeroDuration(float heroDuration) {
		this.heroDuration = heroDuration;
	}

	public War3ID getCode() {
		return code;
	}
}
