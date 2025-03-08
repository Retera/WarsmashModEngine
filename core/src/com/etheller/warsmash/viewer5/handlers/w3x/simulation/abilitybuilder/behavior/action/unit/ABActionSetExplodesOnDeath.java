package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionSetExplodesOnDeath implements ABAction {

	private ABUnitCallback unit;
	private ABBooleanCallback explodes;
	private ABIDCallback buffId;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(game, caster, localStore, castId);
		targetUnit.setExplodesOnDeath(this.explodes.callback(game, caster, localStore, castId));
		if (this.buffId != null) {
			targetUnit.setExplodesOnDeathBuffId(this.buffId.callback(game, caster, localStore, castId));
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
