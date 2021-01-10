package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;

public abstract class AbstractGenericNoIconAbility extends AbstractCAbility implements GenericNoIconAbility {
	private final War3ID alias;

	public AbstractGenericNoIconAbility(final int handleId, final War3ID alias) {
		super(handleId);
		this.alias = alias;
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId) {
		return true;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public War3ID getAlias() {
		return this.alias;
	}

}
