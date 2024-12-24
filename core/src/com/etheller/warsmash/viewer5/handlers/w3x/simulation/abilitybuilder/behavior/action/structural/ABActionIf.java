package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABActionIf implements ABAction {

	private ABCondition condition;
	private List<ABAction> thenActions;
	private List<ABAction> elseActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		if (this.condition.evaluate(game, caster, localStore, castId)) {
			for (final ABAction periodicAction : this.thenActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
		}
		else {
			for (final ABAction periodicAction : this.elseActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("if ");
		sb.append(this.condition.generateJassEquivalent(jassTextGenerator));
		sb.append(" then");
		jassTextGenerator.println(sb.toString());
		final int childIndent = indent + 1;
		if (this.thenActions != null) {
			for (final ABAction action : this.thenActions) {
				action.generateJassEquivalent(childIndent, jassTextGenerator);
			}
		}
		if (this.elseActions != null) {
			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("else");
			jassTextGenerator.println(sb.toString());
			for (final ABAction action : this.elseActions) {
				action.generateJassEquivalent(childIndent, jassTextGenerator);
			}
		}
		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("endif");
		jassTextGenerator.println(sb.toString());
	}
}
