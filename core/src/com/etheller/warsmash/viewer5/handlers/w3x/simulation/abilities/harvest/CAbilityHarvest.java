package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackNormal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityHarvest extends AbstractGenericSingleIconActiveAbility {
	private int damageToTree;
	private int goldCapacity;
	private int lumberCapacity;
	private float castRange;
	private float duration;
	private CBehaviorHarvest behaviorHarvest;
	private CBehaviorReturnResources behaviorReturnResources;
	private int carriedResourceAmount;
	private ResourceType carriedResourceType;
	private CUnitAttack treeAttack;
	private CWidget lastHarvestTarget;
	private CBehaviorAttack behaviorTreeAttack;

	public CAbilityHarvest(final int handleId, final War3ID code, final War3ID alias, final int damageToTree, final int goldCapacity,
			final int lumberCapacity, final float castRange, final float duration) {
		super(handleId, code, alias);
		this.damageToTree = damageToTree;
		this.goldCapacity = goldCapacity;
		this.lumberCapacity = lumberCapacity;
		this.castRange = castRange;
		this.duration = duration;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

		this.behaviorTreeAttack = new CBehaviorAttack(unit);
		this.behaviorHarvest = new CBehaviorHarvest(unit, this);
		this.behaviorReturnResources = new CBehaviorReturnResources(unit, this);

		final List<CUnitAttack> unitAttacks = unit.getUnitSpecificAttacks();
		CUnitAttack bestFitTreeAttack = null;
		for (final CUnitAttack attack : unitAttacks) {
			if (attack.getTargetsAllowed().contains(CTargetType.TREE)) {
				bestFitTreeAttack = attack;
			}
		}
		this.treeAttack = new CUnitAttackNormal(
				bestFitTreeAttack == null ? 0.433f : bestFitTreeAttack.getAnimationBackswingPoint(),
				bestFitTreeAttack == null ? 0.433f : bestFitTreeAttack.getAnimationDamagePoint(), CAttackType.NORMAL,
				this.duration, 0, 1, this.damageToTree * 2, 0, (int) this.castRange,
				bestFitTreeAttack == null ? 250 : bestFitTreeAttack.getRangeMotionBuffer(),
				bestFitTreeAttack == null ? false : bestFitTreeAttack.isShowUI(),
				bestFitTreeAttack == null ? EnumSet.of(CTargetType.TREE) : bestFitTreeAttack.getTargetsAllowed(),
				bestFitTreeAttack == null ? "AxeMediumChop" : bestFitTreeAttack.getWeaponSound(),
				bestFitTreeAttack == null ? CWeaponType.NORMAL : bestFitTreeAttack.getWeaponType());
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return this.behaviorHarvest.reset(game, target);
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
			if(this.goldCapacity <= 0){
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_TREE);
				return;
			}
			final CUnit targetUnit = (CUnit) target;
			for (final CAbility ability : targetUnit.getAbilities()) {
				if (ability instanceof CAbilityGoldMinable) {
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
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_RESOURCES);
		}
		else if (target instanceof CDestructable) {
			if(this.lumberCapacity <= 0){
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_GOLD_MINE);
				return;
			}
			if (target.canBeTargetedBy(game, unit, this.treeAttack.getTargetsAllowed(), receiver)) {
				receiver.targetOk(target);
			}
			// else receiver called by "canBeTargetedBy"
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_RESOURCES);
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

	public CUnitAttack getTreeAttack() {
		return this.treeAttack;
	}

	public void setLastHarvestTarget(final CWidget lastHarvestTarget) {
		this.lastHarvestTarget = lastHarvestTarget;
	}

	public CWidget getLastHarvestTarget() {
		return this.lastHarvestTarget;
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	public CBehaviorAttack getBehaviorTreeAttack() {
		return this.behaviorTreeAttack;
	}

	public void setDamageToTree(final int damageToTree) {
		this.damageToTree = damageToTree;
	}

	public void setGoldCapacity(final int goldCapacity) {
		this.goldCapacity = goldCapacity;
	}

	public void setLumberCapacity(final int lumberCapacity) {
		this.lumberCapacity = lumberCapacity;
	}

	public void setCastRange(final float castRange) {
		this.castRange = castRange;
	}

	public void setDuration(final float duration) {
		this.duration = duration;
	}

	@Override
	public boolean isPhysical() {
		return true;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
