package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionSetExplodesOnDeath implements ABAction {

	private ABUnitCallback unit;
	private ABBooleanCallback explodes;
	private ABIDCallback buffId;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(caster, localStore, castId);
		targetUnit.setExplodesOnDeath(this.explodes.callback(caster, localStore, castId));
		if (this.buffId != null) {
			targetUnit.setExplodesOnDeathBuffId(this.buffId.callback(caster, localStore, castId));
		}
	}

	@Override
	public void generateJassEquivalent(int indent, JassTextGenerator jassTextGenerator) {
		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call SetUnitExploded(");
		sb.append(this.unit.generateJassEquivalent(jassTextGenerator));
		sb.append(", ");
		sb.append(this.explodes.generateJassEquivalent(jassTextGenerator));
		sb.append(")");
		jassTextGenerator.println(sb.toString());

		if (this.buffId != null) {
			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("call SetUnitExplodeOnDeathBuffId(");
			sb.append(this.unit.generateJassEquivalent(jassTextGenerator));
			sb.append(", ");
			sb.append(this.buffId.generateJassEquivalent(jassTextGenerator));
			sb.append(")");
			jassTextGenerator.println(sb.toString());
		}
	}

}
