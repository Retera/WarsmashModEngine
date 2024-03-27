package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.interpreter.ast.util.CHandle;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.Aliased;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public interface CDestructableBuff extends CHandle, Aliased {
	/* should fire when ability added to unit */
	void onAdd(CSimulation game, CDestructable dest);

	/* should fire when ability removed from unit */
	void onRemove(CSimulation game, CDestructable dest);

	void onDeath(CSimulation game, CDestructable dest);
	
	int getLevel();

	void setLevel(int level);
}
