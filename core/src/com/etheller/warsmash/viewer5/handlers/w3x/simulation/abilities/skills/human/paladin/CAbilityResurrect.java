package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.paladin;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;

public class CAbilityResurrect extends CAbilityNoTargetSpellBase  {
	private float areaOfEffect;
	private int numberOfCorpsesRaised;

	public CAbilityResurrect(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return 0;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.numberOfCorpsesRaised = worldEditorAbility.getFieldAsInteger(AbilityFields.Resurrection.NUMBER_OF_CORPSES_RAISED, level);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT, level);
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		simulation.getWorldCollision().enumUnitsInRange(caster.getX(),
				caster.getY(),
				this.areaOfEffect,
				(enumUnit) -> {
					if (!enumUnit.isUnitAlly(simulation.getPlayer(caster.getPlayerIndex())) && enumUnit.canBeTargetedBy(simulation, caster, getTargetsAllowed())) {
						// do a thing
					}
					return false;
				});
		return false;
	}
}
