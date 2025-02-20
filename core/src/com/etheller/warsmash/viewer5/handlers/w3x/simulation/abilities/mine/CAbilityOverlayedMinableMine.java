package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityOverlayedMinableMine extends CAbilityOverlayedMine implements CAbilityGoldMinable {
	private float miningDuration;
	private int miningCapacity;
	private final List<CBehaviorHarvest> activeMiners;
	private boolean wasEmpty;

	public CAbilityOverlayedMinableMine(final int handleId, final War3ID code, final War3ID alias, final int maxGold,
			final float miningDuration, final int miningCapacity) {
		super(handleId, code, alias);
		this.miningDuration = miningDuration;
		this.miningCapacity = miningCapacity;
		this.activeMiners = new ArrayList<>();
		this.wasEmpty = this.activeMiners.isEmpty();
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		final boolean empty = this.activeMiners.isEmpty();
		if (empty != this.wasEmpty) {
			if (empty) {
				if (unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.WORK)) {
					unit.getUnitAnimationListener().forceResetCurrentAnimation();
				}
			}
			else {
				if (unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.WORK)) {
					unit.getUnitAnimationListener().forceResetCurrentAnimation();
				}
			}
			this.wasEmpty = empty;
		}
		final CAbilityGoldMinable parentGoldMineAbility = getParentGoldMineAbility();
		for (int i = this.activeMiners.size() - 1; i >= 0; i--) {
			final CBehaviorHarvest activeMiner = this.activeMiners.get(i);
			if (game.getGameTurnTick() >= activeMiner.getPopoutFromMineTurnTick()) {

				int goldMined;
				if (parentGoldMineAbility.getGold() > 0) {
					goldMined = Math.min(parentGoldMineAbility.getGold(), activeMiner.getGoldCapacity());
					parentGoldMineAbility.setGold(parentGoldMineAbility.getGold() - goldMined);
				}
				else {
					goldMined = 0;
				}
				activeMiner.popoutFromMine(goldMined);
				this.activeMiners.remove(i);
			}
		}
		if (parentGoldMineAbility.getGold() <= 0) {
			unit.setLife(game, 0);
		}
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
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public int getGold() {
		return getParentGoldMineAbility().getGold();
	}

	@Override
	public void setGold(final int gold) {
		getParentGoldMineAbility().setGold(gold);
	}

	@Override
	public int getActiveMinerCount() {
		return this.activeMiners.size();
	}

	@Override
	public void addMiner(final CBehaviorHarvest miner) {
		this.activeMiners.add(miner);
	}

	@Override
	public int getMiningCapacity() {
		return this.miningCapacity;
	}

	@Override
	public float getMiningDuration() {
		return this.miningDuration;
	}

	public void setMiningCapacity(final int miningCapacity) {
		this.miningCapacity = miningCapacity;
	}

	public void setMiningDuration(final float miningDuration) {
		this.miningDuration = miningDuration;
	}

	@Override
	public boolean isBaseMine() {
		return false;
	}
}
