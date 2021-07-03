package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CAbilityReviveHero extends AbstractCAbility {

	public CAbilityReviveHero(final int handleId) {
		super(handleId);
	}


	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		CUnit deadHero = game.getUnit(orderId);
		if (deadHero != null
			&& deadHero.getPlayerIndex()==unit.getPlayerIndex()) {
			CAbilityHero heroData = deadHero.getHeroData();
			if( heroData != null && heroData.isAwaitingRevive()) {
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				int heroReviveGoldCost = game.getGameplayConstants().getHeroReviveGoldCost(deadHero.getUnitType().getGoldCost(), heroData.getHeroLevel());
				int heroReviveLumberCost = game.getGameplayConstants().getHeroReviveLumberCost(deadHero.getUnitType().getGoldCost(), heroData.getHeroLevel());

				if (player.getGold() >= heroReviveGoldCost) {
					if (player.getLumber() >= heroReviveLumberCost) {
						if ((deadHero.getUnitType().getFoodUsed() == 0)
								|| ((player.getFoodUsed() + deadHero.getUnitType().getFoodUsed()) <= player.getFoodCap())) {
							receiver.useOk();
						}
						else {
							receiver.notEnoughResources(ResourceType.FOOD);
						}
					}
					else {
						receiver.notEnoughResources(ResourceType.LUMBER);
					}
				}
				else {
					receiver.notEnoughResources(ResourceType.GOLD);
				}
			}
		}
		else {
			/// ???
			receiver.useOk();
		}
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		CUnit deadHero = game.getUnit(orderId);
		if (deadHero != null
				&& deadHero.getPlayerIndex()==unit.getPlayerIndex()) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.cancel) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, AbilityTarget target) {
		return true;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (orderId == OrderIds.cancel) {
			caster.cancelBuildQueueItem(game, 0);
		}
		else {
			caster.queueRevivingHero(game, game.getUnit(orderId));
		}
		return null;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}
}
