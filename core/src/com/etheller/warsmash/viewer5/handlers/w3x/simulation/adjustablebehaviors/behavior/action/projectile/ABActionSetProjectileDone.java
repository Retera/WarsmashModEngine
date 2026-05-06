package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.projectile;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.projectile.ABProjectileCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;

public class ABActionSetProjectileDone implements ABSingleAction {

	private ABProjectileCallback projectile;
	private ABBooleanCallback done;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {

		final CProjectile proj = this.projectile.callback(caster, localStore, castId);

		proj.setDone(this.done.callback(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "SetProjectileDone(" + this.projectile.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.done.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
