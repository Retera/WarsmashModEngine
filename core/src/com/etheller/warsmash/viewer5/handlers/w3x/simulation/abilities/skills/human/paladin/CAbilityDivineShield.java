package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityDivineShield extends CAbilityNoTargetSpellBase {

	private boolean canDeactivate;
	private War3ID buffId;

	public CAbilityDivineShield(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.divineshield;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.canDeactivate = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_A + level, 0);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		caster.add(simulation,
				new CBuffDivineShield(simulation.getHandleIdAllocator().createId(), this.buffId, getDuration()));
		return false;
	}
}
