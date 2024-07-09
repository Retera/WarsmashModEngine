package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionFor implements ABAction {

	private ABIntegerCallback times;
	private List<ABAction> actions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final int max = this.times.callback(game, caster, localStore, castId);
		for (int i = 0; i < max; i++) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ITERATORCOUNT, castId), i);
			for (final ABAction iterationAction : this.actions) {
				iterationAction.runAction(game, caster, localStore, castId);
			}
			final Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
			if ((brk != null) && brk) {
				break;
			}
		}
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		final StringBuilder sb = new StringBuilder();

		final String keyExpression = "AB_LOCAL_STORE_KEY_ITERATORCOUNT + I2S(" + jassTextGenerator.getCastId() + ")";
		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call " + jassTextGenerator.setUserDataExpr(keyExpression, JassTextGeneratorType.Integer, "0"));
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("loop");
		jassTextGenerator.println(sb.toString());
		final int childIndent = indent + 1;
		sb.setLength(0);
		JassTextGenerator.Util.indent(childIndent, sb);
		sb.append("exitwhen ");
		sb.append(jassTextGenerator.getUserDataExpr(keyExpression, JassTextGeneratorType.Integer));
		sb.append(" >= ");
		sb.append(this.times.generateJassEquivalent(jassTextGenerator));
		jassTextGenerator.println(sb.toString());

		for (final ABAction action : this.actions) {
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
		JassTextGenerator.Util.indent(childIndent, sb);
		sb.append("call " + jassTextGenerator.setUserDataExpr(keyExpression, JassTextGeneratorType.Integer,
				jassTextGenerator.getUserDataExpr(keyExpression, JassTextGeneratorType.Integer) + " + 1"));
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("endloop");
		jassTextGenerator.println(sb.toString());
	}
}
