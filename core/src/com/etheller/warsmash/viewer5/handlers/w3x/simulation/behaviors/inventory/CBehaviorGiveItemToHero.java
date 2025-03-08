package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityShopPurhaseItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CBehaviorGiveItemToHero extends CAbstractRangedBehavior {
	private final CAbilityInventory inventory;
	private CItem targetItem;
	private CUnit targetHero;

	public CBehaviorGiveItemToHero(final CUnit unit, final CAbilityInventory inventory) {
		super(unit);
		this.inventory = inventory;
	}

	public CBehavior reset(final CSimulation game, final CItem targetItem, final CUnit targetHero) {
		this.targetItem = targetItem;
		this.targetHero = targetHero;
		return innerReset(game, targetHero);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, simulation.getGameplayConstants().getGiveItemRange());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.dropitem;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.inventory.dropItem(simulation, this.unit, this.targetItem, this.target.getX(), this.target.getY(), true);
		if (this.targetHero.getInventoryData() != null) {
			this.targetHero.getInventoryData().giveItem(simulation, this.targetHero, this.targetItem, false);
		}
		else {
			final CAbilityShopPurhaseItem shopPurchaseItemAbility = this.targetHero
					.getFirstAbilityOfType(CAbilityShopPurhaseItem.class);
			/*
			 * note: below: pawnable check is not enforced by ability targeting because of
			 * the dual targets "drop item @ unit" concept, which is a bit of a hack
			 */
			if ((shopPurchaseItemAbility != null) && this.targetItem.isPawnable()) {
				final CPlayer player = simulation.getPlayer(this.unit.getPlayerIndex());

				final int goldCost = this.targetItem.getItemType().getGoldCost();
				if (goldCost != 0) {
					final int goldGained = (int) StrictMath
							.ceil(goldCost * simulation.getGameplayConstants().getPawnItemRate());
					player.addGold(goldGained);
					simulation.unitGainResourceEvent(this.targetHero, player.getId(), ResourceType.GOLD, goldGained);
				}
				final int lumberCost = this.targetItem.getItemType().getLumberCost();
				if (lumberCost != 0) {
					final int lumberGained = (int) StrictMath
							.ceil(lumberCost * simulation.getGameplayConstants().getPawnItemRate());
					player.addLumber(lumberGained);
					simulation.unitGainResourceEvent(this.targetHero, player.getId(), ResourceType.LUMBER,
							lumberGained);
				}
				simulation.removeItem(this.targetItem);
				simulation.unitSoundEffectEvent(this.targetHero, shopPurchaseItemAbility.getAlias());
			}
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

}
