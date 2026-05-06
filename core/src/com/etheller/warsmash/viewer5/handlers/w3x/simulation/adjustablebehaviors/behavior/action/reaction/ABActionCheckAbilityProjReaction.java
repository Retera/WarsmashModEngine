package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.reaction;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionCheckAbilityProjReaction implements ABSingleAction {

	private ABUnitCallback target;
	private ABProjectileCallback projectile;
	private List<ABAction> onHitActions;
	private List<ABAction> onBlockActions;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		CUnit tarU = caster;
		if (this.target != null) {
			tarU = this.target.callback(caster, localStore, castId);
		}
		if (tarU.checkForAbilityProjReaction(localStore.game, caster,
				this.projectile.callback(caster, localStore, castId))) {
			if (this.onHitActions != null) {
				for (final ABAction periodicAction : this.onHitActions) {
					periodicAction.runAction(caster, localStore, castId);
				}
			}
		}
		else {
			if (this.onBlockActions != null) {
				for (final ABAction periodicAction : this.onBlockActions) {
					periodicAction.runAction(caster, localStore, castId);
				}
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		final String onBlockFunc = jassTextGenerator.createAnonymousFunction(this.onBlockActions,
				"CheckAbilityProjReactionAU_OnBlock");
		final String onHitFunc = jassTextGenerator.createAnonymousFunction(this.onHitActions,
				"CheckAbilityProjReactionAU_OnHit");
		String tarU;
		if (this.target != null) {
			tarU = this.target.generateJassEquivalent(jassTextGenerator);
		}
		else {
			tarU = jassTextGenerator.getCaster();
		}
		return "CheckAbilityProjReactionAU(" + jassTextGenerator.getCaster() + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ", " + jassTextGenerator.getCastId() + ", " + tarU + ", "
				+ this.projectile.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(onHitFunc) + ", "
				+ jassTextGenerator.functionPointerByName(onBlockFunc) + ")";
	}

	@Override
	public void generateJassEquivalent(final int indent, final JassTextGenerator jassTextGenerator) {
		String tarU;
		if (this.target != null) {
			tarU = this.target.generateJassEquivalent(jassTextGenerator);
		}
		else {
			tarU = jassTextGenerator.getCaster();
		}

		final StringBuilder sb = new StringBuilder();
		if ((this.onHitActions == null) || this.onHitActions.isEmpty()) {
			if ((this.onBlockActions != null) && !this.onBlockActions.isEmpty()) {
				JassTextGenerator.Util.indent(indent, sb);
				sb.append("if not CheckUnitForAbilityProjReaction(");
				sb.append(tarU);
				sb.append(", ");
				sb.append(jassTextGenerator.getCaster());
				sb.append(", ");
				sb.append(this.projectile.generateJassEquivalent(jassTextGenerator));
				sb.append(") then");
				jassTextGenerator.println(sb.toString());

				for (final ABAction onHitAction : this.onBlockActions) {
					onHitAction.generateJassEquivalent(indent + 1, jassTextGenerator);
				}

				sb.setLength(0);
				JassTextGenerator.Util.indent(indent, sb);
				sb.append("endif");
				jassTextGenerator.println(sb.toString());
			}
		}
		else {
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("if CheckUnitForAbilityProjReaction(");
			sb.append(tarU);
			sb.append(", ");
			sb.append(jassTextGenerator.getCaster());
			sb.append(", ");
			sb.append(this.projectile.generateJassEquivalent(jassTextGenerator));
			sb.append(") then");
			jassTextGenerator.println(sb.toString());

			for (final ABAction onHitAction : this.onHitActions) {
				onHitAction.generateJassEquivalent(indent + 1, jassTextGenerator);
			}

			if ((this.onBlockActions != null) && !this.onBlockActions.isEmpty()) {
				sb.setLength(0);
				JassTextGenerator.Util.indent(indent, sb);
				sb.append("else");
				jassTextGenerator.println(sb.toString());

				for (final ABAction onHitAction : this.onBlockActions) {
					onHitAction.generateJassEquivalent(indent + 1, jassTextGenerator);
				}
			}

			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("endif");
			jassTextGenerator.println(sb.toString());
		}

	}
}
