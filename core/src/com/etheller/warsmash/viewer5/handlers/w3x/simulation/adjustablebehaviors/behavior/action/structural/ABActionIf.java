package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionIf implements ABAction {

	private ABBooleanCallback condition;
	private List<ABAction> thenActions;
	private List<ABAction> elseActions;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		if (condition.callback(caster, localStore, castId)) {
			if (this.thenActions != null) {
				for (ABAction periodicAction : thenActions) {
					periodicAction.runAction(caster, localStore, castId);
				}
			}
		} else {
			if (this.elseActions != null) {
				for (final ABAction periodicAction : this.elseActions) {
					periodicAction.runAction(caster, localStore, castId);
				}
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
