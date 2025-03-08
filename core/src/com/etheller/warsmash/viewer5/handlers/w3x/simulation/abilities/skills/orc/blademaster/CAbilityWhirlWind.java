package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.blademaster;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityWhirlWind extends CAbilityNoTargetSpellBase {

	private float damagePerSecond;
	private War3ID buffId;
	private float areaOfEffect;

	public CAbilityWhirlWind(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.whirlwind;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.damagePerSecond = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		caster.add(simulation, new CBuffWhirlWindCaster(simulation.getHandleIdAllocator().createId(), this.buffId, this,
				getDuration()));
		return false;
	}

	public float getDamagePerSecond() {
		return this.damagePerSecond;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public float getDamageInterval() {
		return 1.0f;
	}
}
