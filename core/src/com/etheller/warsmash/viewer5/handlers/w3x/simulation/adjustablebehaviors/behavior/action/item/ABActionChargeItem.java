package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.item;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability.ABAbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.item.ABItemCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionChargeItem implements ABSingleAction {

	private ABItemCallback item;
	private ABBooleanCallback checkForPerish;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CItem it = null;
		if (this.item == null) {
			final ABAbilityBuilderAbility ability = localStore.originAbility;

			it = ability.getItem();
		} else {
			it = this.item.callback(caster, localStore, castId);
		}

		it.setCharges(it.getCharges() - 1);
		if ((this.checkForPerish != null) && this.checkForPerish.callback(caster, localStore, castId)
				&& it.getItemType().isPerishable() && (it.getCharges() == 0)) {
			it.forceDropIfHeld(localStore.game);
			localStore.game.removeItem(it);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		String itemExpression = "null";
		if (this.item != null) {
			itemExpression = this.item.generateJassEquivalent(jassTextGenerator);
		}
		String checkForPerishExpression = "false";
		if (this.checkForPerish != null) {
			checkForPerishExpression = this.checkForPerish.generateJassEquivalent(jassTextGenerator);
		}
		return "ChargeItemAU(" + itemExpression + ", " + checkForPerishExpression + ", "
				+ jassTextGenerator.getTriggerLocalStore() + ")";
	}
}
