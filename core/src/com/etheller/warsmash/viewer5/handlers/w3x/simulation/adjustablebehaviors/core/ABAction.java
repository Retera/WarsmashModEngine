package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core;

import com.etheller.warsmash.parsers.jass.JassTextGeneratorStmt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public interface ABAction extends JassTextGeneratorStmt {
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId);

}
