package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.floatingtext;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionCreateNumericFloatingTextOnUnit implements ABSingleAction {

	private ABUnitCallback target;
	private TextTagConfigType textType;
	private ABFloatCallback amount;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		float theAmount = 0;
		if (this.amount != null) {
			theAmount = this.amount.callback(caster, localStore, castId);
		}

		localStore.game.spawnTextTag(this.target.callback(caster, localStore, castId), caster.getPlayerIndex(),
				this.textType, (int) (theAmount));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "CreateIntTextTagFromConfig(" + this.target.generateJassEquivalent(jassTextGenerator)
				+ ", TEXT_TAG_CONFIG_TYPE_" + this.textType.name() + ", R2I("
				+ this.amount.generateJassEquivalent(jassTextGenerator) + "))";
	}
}
