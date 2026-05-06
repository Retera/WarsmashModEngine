package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.buff;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.buff.defaultbuffs.ABBuffStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionAddStunBuff implements ABSingleAction {

	private ABUnitCallback unit;
	private ABIDCallback buffId;
	private ABFloatCallback duration;

	private ABBooleanCallback showIcon;
	private ABBooleanCallback leveled;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		War3ID alias = null;
		boolean isLeveled = false;
		boolean isShowIcon = true;
		if (buffId == null) {
			alias = War3ID.fromString("BSTN");
		} else {
			alias = buffId.callback(caster, localStore, castId);
		}
		if (leveled != null) {
			isLeveled = leveled.callback(caster, localStore, castId);
		} else {
			isLeveled = localStore.getBooleanOrDefault(ABLocalStoreKeys.ISABILITYLEVELED, false);
		}
		if (showIcon != null) {
			isShowIcon = showIcon.callback(caster, localStore, castId);
		}
		final ABBuffStun ability = new ABBuffStun(localStore.game.getHandleIdAllocator().createId(), alias, localStore,
				localStore.originAbility, caster, this.duration.callback(caster, localStore, castId), isLeveled);
		ability.setIconShowing(isShowIcon);

		this.unit.callback(caster, localStore, castId).add(localStore.game, ability);
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
