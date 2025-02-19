package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass;

import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityJass extends AbstractGenericAliasedAbility {
	private final CAbilityTypeJassDefinition typeDefinition;
	private CommonTriggerExecutionScope jassAbilityBasicScope;

	public CAbilityJass(final int handleId, final War3ID code, final War3ID alias, final CAbilityTypeJassDefinition type) {
		super(handleId, code, alias);
		this.typeDefinition = type;
	}

	public CommonTriggerExecutionScope getJassAbilityBasicScope() {
		return this.jassAbilityBasicScope;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.jassAbilityBasicScope = CommonTriggerExecutionScope.jassAbilityBasicScope(this, unit, getAlias());
		this.typeDefinition.onAdd(game, this, unit);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.typeDefinition.onRemove(game, this, unit);
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		this.typeDefinition.onTick(game, this, unit);
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		this.typeDefinition.onDeath(game, this, cUnit);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
		this.typeDefinition.onCancelFromQueue(game, this, unit, orderId);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.typeDefinition.begin(game, this, caster, orderId, target);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return this.typeDefinition.begin(game, this, caster, orderId, point);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return this.typeDefinition.beginNoTarget(game, this, caster, orderId);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		this.typeDefinition.checkCanTarget(game, this, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		this.typeDefinition.checkCanTarget(game, this, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		this.typeDefinition.checkCanTargetNoTarget(game, this, unit, orderId, receiver);
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		this.typeDefinition.checkCanUse(game, this, unit, orderId, receiver);
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return this.typeDefinition.checkBeforeQueue(game, this, caster, orderId, target);
	}

	public CAbilityTypeJassDefinition getType() {
		return this.typeDefinition;
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isMagic() {
		return true;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.SPELL;
	}
}
