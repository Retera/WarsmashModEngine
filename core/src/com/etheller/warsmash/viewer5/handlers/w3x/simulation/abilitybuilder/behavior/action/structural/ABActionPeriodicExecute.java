package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionPeriodicExecute implements ABAction {

	private List<ABAction> periodicActions;
	private ABFloatCallback delaySeconds;
	private ABBooleanCallback initialTick;

	private ABCallback unique;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		int nextActiveTick = 0;
		Object u = null;
		if (this.unique != null) {
			u = this.unique.callback(game, caster, localStore, castId);
			if (localStore.containsKey(ABLocalStoreKeys.PERIODICNEXTTICK + castId + "$" + u)) {
				nextActiveTick = (int) localStore.get(ABLocalStoreKeys.PERIODICNEXTTICK + castId + "$" + u);
			}
		}
		else {
			if (localStore.containsKey(ABLocalStoreKeys.PERIODICNEXTTICK + castId)) {
				nextActiveTick = (int) localStore.get(ABLocalStoreKeys.PERIODICNEXTTICK + castId);
			}
		}

		final int currentTick = game.getGameTurnTick();
		if (currentTick >= nextActiveTick) {
			final int delayTicks = (int) (this.delaySeconds.callback(game, caster, localStore, castId)
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			if (nextActiveTick == 0) {
				nextActiveTick = currentTick + delayTicks;
				if ((this.initialTick != null) && this.initialTick.callback(game, caster, localStore, castId)) {
					for (final ABAction periodicAction : this.periodicActions) {
						periodicAction.runAction(game, caster, localStore, castId);
					}
				}
			}
			else {
				nextActiveTick = currentTick + delayTicks;
				for (final ABAction periodicAction : this.periodicActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}

		if (this.unique != null) {
			localStore.put(ABLocalStoreKeys.PERIODICNEXTTICK + castId + "$" + u, nextActiveTick);
		}
		else {
			localStore.put(ABLocalStoreKeys.PERIODICNEXTTICK + castId, nextActiveTick);
		}
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		String uniquenessKeyExpression;
		if (this.unique != null) {
			uniquenessKeyExpression = "I2S(" + jassTextGenerator.getCastId() + ") + \"$\" + "
					+ this.unique.generateJassEquivalent(jassTextGenerator);
		}
		else {
			uniquenessKeyExpression = "I2S(" + jassTextGenerator.getCastId() + ")";
		}
		String initialTickExpression;
		if (this.initialTick != null) {
			initialTickExpression = this.initialTick.generateJassEquivalent(jassTextGenerator);
		}
		else {
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
