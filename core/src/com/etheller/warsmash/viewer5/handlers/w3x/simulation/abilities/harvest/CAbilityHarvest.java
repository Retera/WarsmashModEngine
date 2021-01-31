package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityHarvest extends AbstractGenericSingleIconActiveAbility {
	private final int damageToTree;
	private final int goldCapacity;
	private final int lumberCapacity;
	private CBehaviorHarvest behaviorHarvest;
	private CBehaviorReturnResources behaviorReturnResources;
	private int carriedResourceAmount;
	private ResourceType carriedResourceType;

	public CAbilityHarvest(final int handleId, final War3ID alias, final int damageToTree, final int goldCapacity,
			final int lumberCapacity) {
		super(handleId, alias);
		this.damageToTree = damageToTree;
		this.goldCapacity = goldCapacity;
		this.lumberCapacity = lumberCapacity;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorHarvest = new CBehaviorHarvest(unit, this);
		this.behaviorReturnResources = new CBehaviorReturnResources(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorHarvest.reset(target);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (isToggleOn() && (orderId == OrderIds.returnresources)) {
			return this.behaviorReturnResources.reset(game);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public int getBaseOrderId() {
		return isToggleOn() ? OrderIds.returnresources : OrderIds.harvest;
	}

	@Override
	public boolean isToggleOn() {
		return this.carriedResourceAmount > 0;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			for (final CAbility ability : targetUnit.getAbilities()) {
				if (ability instanceof CAbilityGoldMine) {
					receiver.targetOk(target);
					return;
				}
				else if ((this.carriedResourceType != null) && (ability instanceof CAbilityReturnResources)) {
					final CAbilityReturnResources abilityReturn = (CAbilityReturnResources) ability;
					if (abilityReturn.accepts(this.carriedResourceType)) {
						receiver.targetOk(target);
						return;
					}
				}
			}
			receiver.mustTargetResources();
		}
		else if (target instanceof CDestructable) {
			receiver.mustTargetResources();
		}
		else {
			receiver.mustTargetResources();
		}
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		innerCheckCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanSmartTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId == OrderIds.returnresources) && isToggleOn()) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	public int getDamageToTree() {
		return this.damageToTree;
	}

	public int getGoldCapacity() {
		return this.goldCapacity;
	}

	public int getLumberCapacity() {
		return this.lumberCapacity;
	}

	public int getCarriedResourceAmount() {
		return this.carriedResourceAmount;
	}

	public ResourceType getCarriedResourceType() {
		return this.carriedResourceType;
	}

	public void setCarriedResources(final ResourceType carriedResourceType, final int carriedResourceAmount) {
		this.carriedResourceType = carriedResourceType;
		this.carriedResourceAmount = carriedResourceAmount;
	}

	public CBehaviorHarvest getBehaviorHarvest() {
		return this.behaviorHarvest;
	}

	public CBehaviorReturnResources getBehaviorReturnResources() {
		return this.behaviorReturnResources;
	}

}
