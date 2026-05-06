package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitqueue;

import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public abstract class ABUnitQueueCallback implements ABCallback {

	abstract public Queue<CUnit> callback(final CUnit caster, final ABLocalDataStore localStore, final int castId);
}
