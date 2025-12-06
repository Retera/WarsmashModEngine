package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mount;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityMount extends CAbilityNoTargetSpellBase {

	private CBuffMount buffMount;
	private War3ID buffId;
	private float speedAdjust;

	public CAbilityMount(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.mount;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level, 0);
		this.speedAdjust = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		super.onAdd(game, unit);
		this.buffMount = new CBuffMount(game.getHandleIdAllocator().createId(), this.buffId, this.speedAdjust);
	}

	@Override
	public boolean doEffect(final CSimulation game, final CUnit caster, final AbilityTarget target) {
		if (caster.getAbilities().contains(this.buffMount)) {
			caster.remove(game, this.buffMount);
		}
		else {
			caster.add(game, this.buffMount);
		}
		return false;
	}

}
