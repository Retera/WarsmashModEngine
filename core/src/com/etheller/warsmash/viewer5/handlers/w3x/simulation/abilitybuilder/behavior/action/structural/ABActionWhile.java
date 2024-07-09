package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionWhile implements ABAction {

	private ABCondition condition;
	private List<ABAction> loopActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		while (this.condition.evaluate(game, caster, localStore, castId)) {
			for (final ABAction periodicAction : this.loopActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
			final Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
			if ((brk != null) && brk) {
				break;
			}
		}
	}

	@Override
	public void generateJassEquivalent(int indent, JassTextGenerator jassTextGenerator) {
		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("loop");
		jassTextGenerator.println(sb.toString());
		final int childIndent = indent + 1;
		sb.setLength(0);
		JassTextGenerator.Util.indent(childIndent, sb);
		sb.append("exitwhen not ");
		sb.append(this.condition.generateJassEquivalent(jassTextGenerator));
		jassTextGenerator.println(sb.toString());

		for (final ABAction action : this.loopActions) {
			action.generateJassEquivalent(childIndent, jassTextGenerator);
		}

		sb.setLength(0);
		JassTextGenerator.Util.indent(childIndent, sb);
		sb.append("if ");
		sb.append(jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_BREAK", JassTextGeneratorType.Boolean));
		sb.append(" then");
		jassTextGenerator.println(sb.toString());

		final int breakBranchIndent = childIndent + 1;
		sb.setLength(0);
		JassTextGenerator.Util.indent(breakBranchIndent, sb);
		sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
				+ ", AB_LOCAL_STORE_KEY_BREAK)");
		jassTextGenerator.println(sb.toString());
		sb.setLength(0);
		JassTextGenerator.Util.indent(breakBranchIndent, sb);
		sb.append("exitwhen true // break");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(childIndent, sb);
		sb.append("endif");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("endloop");
		jassTextGenerator.println(sb.toString());
	}
}
