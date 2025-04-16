package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAddStunBuff implements ABSingleAction {

	private ABUnitCallback unit;
	private ABIDCallback buffId;
	private ABFloatCallback duration;

	private ABBooleanCallback showIcon;
	private ABBooleanCallback leveled;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		War3ID alias = null;
		boolean isLeveled = false;
		boolean isShowIcon = true;
		if (buffId == null) {
			alias = War3ID.fromString("BSTN");
		} else {
			alias = buffId.callback(game, caster, localStore, castId);
		}
		if (leveled != null) {
			isLeveled = leveled.callback(game, caster, localStore, castId);
		} else {
			isLeveled = (boolean) localStore.getOrDefault(ABLocalStoreKeys.ISABILITYLEVELED, false);
		}
		if (showIcon != null) {
			isShowIcon = showIcon.callback(game, caster, localStore, castId);
		}
		final ABBuffStun ability = new ABBuffStun(game.getHandleIdAllocator().createId(), alias,
				(CAbility) localStore.get(ABLocalStoreKeys.ABILITY), caster,
				this.duration.callback(game, caster, localStore, castId), isLeveled);
		ability.setIconShowing(isShowIcon);

		this.unit.callback(game, caster, localStore, castId).add(game, ability);
		localStore.put(ABLocalStoreKeys.LASTADDEDBUFF, ability);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "AddUnitAbility(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", CreateStunBuffAU("
				+ jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.buffId.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + "))";
	}
}
