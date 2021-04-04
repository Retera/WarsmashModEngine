package com.etheller.warsmash.viewer5.handlers.w3x.ui.command;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface ActiveCommand {
	boolean finish(CSimulation simulation, CUnit selectedUnit, float mouseScreenX, float mouseScreenY);
}
