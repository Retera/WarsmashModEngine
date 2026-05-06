package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.iterator.unit;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorCallStmt;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorStmt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionIterateUnitsInRangeOfLocationMatchingCondition implements ABAction {

	private static final Rectangle recycleRect = new Rectangle();

	private ABLocationCallback location;
	private ABFloatCallback range;
	private List<ABAction> iterationActions;
	private ABBooleanCallback condition;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget target = this.location.callback(caster, localStore, castId);
		final Float rangeVal = this.range.callback(caster, localStore, castId);

		recycleRect.set(target.getX() - rangeVal, target.getY() - rangeVal, rangeVal * 2, rangeVal * 2);
		localStore.game.getWorldCollision().enumUnitsInRect(recycleRect, new CUnitEnumFunction() {
			@Override
			public boolean call(final CUnit enumUnit) {
				if (enumUnit.canReach(target, rangeVal)) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId), enumUnit);
					if (condition == null || condition.callback(caster, localStore, castId)) {
						localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId), enumUnit);
						for (ABAction iterationAction : iterationActions) {
							iterationAction.runAction(caster, localStore, castId);
						}
					}
				}
				return false;
			}
		});
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ENUMUNIT, castId));
		localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.MATCHINGUNIT, castId));
	}

	@Override
	public void generateJassEquivalent(int indent, JassTextGenerator jassTextGenerator) {
		final List<JassTextGeneratorStmt> modifiedActionList = new ArrayList<>(this.iterationActions);
		modifiedActionList.add(0, new JassTextGeneratorCallStmt() {
			@Override
			public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
				return "SetLocalStoreUnitHandle(" + jassTextGenerator.getTriggerLocalStore()
						+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "), "
						+ "GetFilterUnit()" + ") // filter unit used intentionally as enum";
			}
		});
		if (this.condition != null) {
			modifiedActionList.add(0, new JassTextGeneratorStmt() {
				@Override
				public void generateJassEquivalent(int indent, final JassTextGenerator jassTextGenerator) {
					final StringBuilder sb = new StringBuilder();
					JassTextGenerator.Util.indent(indent, sb);
					sb.append("SetLocalStoreUnitHandle(" + jassTextGenerator.getTriggerLocalStore()
							+ ", AB_LOCAL_STORE_KEY_MATCHINGUNIT + I2S(" + jassTextGenerator.getCastId() + "), "
							+ "GetFilterUnit()" + ")");
					jassTextGenerator.println(sb.toString());

					sb.setLength(0);
					JassTextGenerator.Util.indent(indent, sb);
					sb.append("if not ");
					sb.append(ABActionIterateUnitsInRangeOfLocationMatchingCondition.this.condition
							.generateJassEquivalent(jassTextGenerator));
					sb.append(" then");
					jassTextGenerator.println(sb.toString());

					sb.setLength(0);
					JassTextGenerator.Util.indent(indent + 1, sb);
					sb.append("return false");
					jassTextGenerator.println(sb.toString());

					sb.setLength(0);
					JassTextGenerator.Util.indent(indent, sb);
					sb.append("endif");
					jassTextGenerator.println(sb.toString());
				}
			});
		}
		modifiedActionList.add(new JassTextGeneratorStmt() {
			@Override
			public void generateJassEquivalent(int indent, JassTextGenerator jassTextGenerator) {
				final StringBuilder sb = new StringBuilder();
				JassTextGenerator.Util.indent(indent, sb);
				sb.append("return false");
				jassTextGenerator.println(sb.toString());
			}
		});
		final String iterationActionsName = jassTextGenerator.createAnonymousFunction(modifiedActionList,
				"UnitsInRangeOfLocEnumActions");

		final StringBuilder sb = new StringBuilder();
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("// TODO: use a global filter");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call GroupEnumUnitsInRangeOfLoc(au_tempGroup, "
				+ this.location.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.range.generateJassEquivalent(jassTextGenerator) + ", Filter("
				+ jassTextGenerator.functionPointerByName(iterationActionsName) + "))");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
				+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "))");
		jassTextGenerator.println(sb.toString());

		sb.setLength(0);
		JassTextGenerator.Util.indent(indent, sb);
		sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
				+ ", AB_LOCAL_STORE_KEY_MATCHINGUNIT + I2S(" + jassTextGenerator.getCastId() + "))");
		jassTextGenerator.println(sb.toString());
	}
}
