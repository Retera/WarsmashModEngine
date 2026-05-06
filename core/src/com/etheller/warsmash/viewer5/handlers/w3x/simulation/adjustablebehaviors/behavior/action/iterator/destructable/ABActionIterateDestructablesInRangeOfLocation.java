package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.iterator.destructable;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorCallStmt;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorStmt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructableEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionIterateDestructablesInRangeOfLocation implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();

	private ABLocationCallback location;
	private ABFloatCallback range;
	private List<ABAction> iterationActions;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget target = this.location.callback(caster, localStore, castId);
		final Float rangeVal = this.range.callback(caster, localStore, castId);

		recycleRect.set(target.getX() - rangeVal, target.getY() - rangeVal, rangeVal * 2, rangeVal * 2);
		localStore.game.getWorldCollision().enumDestructablesInRect(recycleRect, new CDestructableEnumFunction() {
			@Override
			public boolean call(final CDestructable enumDest) {
				if (enumDest.distance(target.getX(), target.getY()) < rangeVal) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMDESTRUCTABLE, castId), enumDest);
					for (final ABAction iterationAction : ABActionIterateDestructablesInRangeOfLocation.this.iterationActions) {
						iterationAction.runAction(caster, localStore, castId);
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMDESTRUCTABLE, castId));
	}

	@Override
	public void generateJassEquivalent(int indent, final JassTextGenerator jassTextGenerator) {
		final List<JassTextGeneratorStmt> modifiedActionList = new ArrayList<>(this.iterationActions);
		modifiedActionList.add(0, new JassTextGeneratorCallStmt() {
			@Override
			public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
				return "SetLocalStoreDestructableHandle(" + jassTextGenerator.getTriggerLocalStore()
						+ ", AB_LOCAL_STORE_KEY_ENUMDESTRUCTABLE + I2S(" + jassTextGenerator.getCastId() + "), "
						+ "GetEnumDestructable()" + ")";
			}
		});
		final String iterationActionsName = jassTextGenerator.createAnonymousFunction(modifiedActionList,
				"DestsInRangeEnumActions");

		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		// TODO this BJ function uses <= range instead of < range
		sb.append("call EnumDestructablesInCircleBJ(" + this.range.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.location.generateJassEquivalent(jassTextGenerator) + ", "
				+ jassTextGenerator.functionPointerByName(iterationActionsName) + ")");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
				+ ", AB_LOCAL_STORE_KEY_ENUMDESTRUCTABLE + I2S(" + jassTextGenerator.getCastId() + "))");
		jassTextGenerator.println(sb.toString());
	}

}
