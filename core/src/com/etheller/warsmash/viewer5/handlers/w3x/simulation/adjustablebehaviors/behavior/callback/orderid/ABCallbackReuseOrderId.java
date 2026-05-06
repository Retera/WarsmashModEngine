package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.orderid;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;

public class ABCallbackReuseOrderId extends ABOrderIdCallback {

	private ABStringCallback name;

	@Override
	public Integer callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final ABAbilityBuilderConfiguration config = (localStore.originAbility)
				.getConfig();
		final String keyS = name.callback(caster, localStore, castId);
		if (config.getReuseCallbacks() != null) {
			ABCallback callback = config.getReuseCallbacks().get(keyS);
			if (callback != null && callback instanceof ABOrderIdCallback) {
				return ((ABOrderIdCallback) callback).callback(caster, localStore, castId);
			} else {
				System.err.println(
						"Trying to run ReuseOrderIdCallback, but key is missing or callback was the wrong type: "
								+ keyS);
			}
		} else {
			System.err.println("Trying to run ReuseOrderIdCallback, but none defined");
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
