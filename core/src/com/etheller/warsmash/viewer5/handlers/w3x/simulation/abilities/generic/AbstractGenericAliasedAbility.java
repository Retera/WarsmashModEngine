package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public abstract class AbstractGenericAliasedAbility extends AbstractCAbility implements CLevelingAbility {
	private final War3ID alias;
	private int level = 1;

	public AbstractGenericAliasedAbility(final int handleId, final War3ID alias) {
		super(handleId);
		this.alias = alias;
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	public War3ID getAlias() {
		return this.alias;
	}

	@Override
	public final int getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(final int level) {
		this.level = level;
	}
}
