package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionPeriodicExecute implements ABAction {
	private final static int HARD_LOOP_CAP = 100;

	private List<ABAction> periodicActions;
	private ABFloatCallback delaySeconds;
	private ABBooleanCallback initialTick;

	private ABCallback unique;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		float nextActiveTick = 0;
		Object u = null;
		if (this.unique != null) {
			u = this.unique.callback(caster, localStore, castId);
			if (localStore
					.containsKey(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId) + "$" + u)) {
				nextActiveTick = localStore
						.getFloat(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId) + "$" + u);
			}
		} else {
			if (localStore.containsKey(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId))) {
				nextActiveTick = localStore
						.getFloat(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId));
			}
		}

		final int currentTick = localStore.game.getGameTurnTick();
		int iter = 0;
		boolean run = true;
		while (run && currentTick >= (int) nextActiveTick && iter < HARD_LOOP_CAP) {
			if (nextActiveTick <= 0) {
				if ((this.initialTick != null) && this.initialTick.callback(caster, localStore, castId)) {
					for (final ABAction periodicAction : this.periodicActions) {
						periodicAction.runAction(caster, localStore, castId);
						final Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
						if ((brk != null) && brk) {
							run = false;
							break;
						}
					}
				}
				nextActiveTick = currentTick + (this.delaySeconds.callback(caster, localStore, castId)
						/ WarsmashConstants.SIMULATION_STEP_TIME);
			} else {
				for (final ABAction periodicAction : this.periodicActions) {
					periodicAction.runAction(caster, localStore, castId);
					final Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
					if ((brk != null) && brk) {
						run = false;
						break;
					}
				}
				nextActiveTick += (this.delaySeconds.callback(caster, localStore, castId)
						/ WarsmashConstants.SIMULATION_STEP_TIME);
			}
			iter++;
		}

		if (this.unique != null) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId) + "$" + u,
					nextActiveTick);
		} else {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId), nextActiveTick);
		}
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		String uniquenessKeyExpression;
		if (this.unique != null) {
			uniquenessKeyExpression = "I2S(" + jassTextGenerator.getCastId() + ") + \"$\" + "
					+ this.unique.generateJassEquivalent(jassTextGenerator);
		} else {
			uniquenessKeyExpression = "I2S(" + jassTextGenerator.getCastId() + ")";
		}
		String initialTickExpression;
		if (this.initialTick != null) {
			initialTickExpression = this.initialTick.generateJassEquivalent(jassTextGenerator);
		} else {
			initialTickExpression = "false";
		}

		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("if PeriodicExecuteAU(");
		sb.append(jassTextGenerator.getTriggerLocalStore());
		sb.append(", ");
		sb.append(this.delaySeconds.generateJassEquivalent(jassTextGenerator));
		sb.append(", ");
		sb.append(initialTickExpression);
		sb.append(", ");
		sb.append(uniquenessKeyExpression);
		sb.append(") then");
		jassTextGenerator.println(sb.toString());
		final int childIndent = indent + 1;
		if (this.periodicActions != null) {
			for (final ABAction action : this.periodicActions) {
				action.generateJassEquivalent(childIndent, jassTextGenerator);
			}
		}
		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("endif");
		jassTextGenerator.println(sb.toString());
	}
}
