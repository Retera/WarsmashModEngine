package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.floatingtext;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class ABActionCreateFloatingTextOnUnit implements ABSingleAction {

	private ABUnitCallback target;
	private TextTagConfigType textType;
	private ABStringCallback message;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		localStore.game.spawnTextTag(this.target.callback(caster, localStore, castId), caster.getPlayerIndex(),
				this.textType, this.message.callback(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "CreateTextTagFromConfig(" + this.target.generateJassEquivalent(jassTextGenerator)
				+ ", TEXT_TAG_CONFIG_TYPE_" + this.textType.name() + ", "
				+ this.message.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
