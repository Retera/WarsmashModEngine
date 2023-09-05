package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CAbilityBlightedGoldMine extends CAbilityOverlayedMine {
	public static final int NO_MINER = -1;
	private int goldPerInterval;
	private float intervalDuration;
	private int maxNumberOfMiners;
	private float radiusOfMiningRing;
	private final CBehaviorAcolyteHarvest[] activeMiners;
	private final Vector2[] minerLocs;
	private int currentActiveMinerCount;
	private int lastIncomeTick;

	private final List<SimulationRenderComponent> spellEffects = new ArrayList<>();

	public CAbilityBlightedGoldMine(final int handleId, final War3ID alias, final int goldPerInterval,
			final float intervalDuration, final int maxNumberOfMiners, final float radiusOfMiningRing) {
		super(handleId, alias);
		this.goldPerInterval = goldPerInterval;
		this.intervalDuration = intervalDuration;
		this.maxNumberOfMiners = maxNumberOfMiners;
		this.radiusOfMiningRing = radiusOfMiningRing;
		this.activeMiners = new CBehaviorAcolyteHarvest[maxNumberOfMiners];
		this.minerLocs = new Vector2[maxNumberOfMiners];
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		for (int i = 0; i < this.minerLocs.length; i++) {
			final double angleSize = (StrictMath.PI * 2) / this.maxNumberOfMiners;
			final double thisMinerAngle = (angleSize * i) + (StrictMath.PI / 2);
			final float harvestStandX = unit.getX()
					+ (float) (StrictMath.cos(thisMinerAngle) * this.radiusOfMiningRing);
			final float harvestStandY = unit.getY()
					+ (float) (StrictMath.sin(thisMinerAngle) * this.radiusOfMiningRing);
			this.minerLocs[i] = new Vector2(harvestStandX, harvestStandY);
			final SimulationRenderComponent spellEffect = game.spawnSpellEffectOnPoint(harvestStandX, harvestStandY,
					(float) (StrictMath.toDegrees(thisMinerAngle)), getAlias(), CEffectType.EFFECT, 0);
			this.spellEffects.add(spellEffect);
		}
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		for (final SimulationRenderComponent spellEffect : this.spellEffects) {
			spellEffect.remove();
		}
		this.spellEffects.clear();
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {
		super.onDeath(game, cUnit);
		for (final SimulationRenderComponent spellEffect : this.spellEffects) {
			spellEffect.remove();
		}
		this.spellEffects.clear();
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (this.currentActiveMinerCount > 0) {
			final float currentInterval = this.intervalDuration
					* (this.maxNumberOfMiners / this.currentActiveMinerCount);
			final int nextIncomeTick = this.lastIncomeTick
					+ (int) (currentInterval / WarsmashConstants.SIMULATION_STEP_TIME);
			final int currentTurnTick = game.getGameTurnTick();
			final CAbilityGoldMinable parentGoldMineAbility = getParentGoldMineAbility();
			final int totalGoldAvailable = parentGoldMineAbility.getGold();
			if ((currentTurnTick >= nextIncomeTick) && (parentGoldMineAbility != null) && (totalGoldAvailable > 0)) {
				this.lastIncomeTick = currentTurnTick;
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				final int goldGained = Math.min(totalGoldAvailable, this.goldPerInterval);
				player.addGold(goldGained);
				parentGoldMineAbility.setGold(totalGoldAvailable - goldGained);
				game.unitGainResourceEvent(unit, player.getId(), ResourceType.GOLD, goldGained);
			}
		}
//		final boolean empty = this.activeMiners.isEmpty();
//		if (empty != this.wasEmpty) {
//			if (empty) {
//				unit.getUnitAnimationListener().removeSecondaryTag(SecondaryTag.WORK);
//			}
//			else {
//				unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.WORK);
//			}
//			this.wasEmpty = empty;
//		}
//		for (int i = this.activeMiners.size() - 1; i >= 0; i--) {
//			final CBehaviorHarvest activeMiner = this.activeMiners.get(i);
//			if (game.getGameTurnTick() >= activeMiner.getPopoutFromMineTurnTick()) {
//
//				final int goldMined = Math.min(this.gold, activeMiner.getGoldCapacity());
//				this.gold -= goldMined;
//				if (this.gold <= 0) {
//					unit.setLife(game, 0);
//				}
//				activeMiner.popoutFromMine(goldMined);
//				this.activeMiners.remove(i);
//			}
//		}
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

	public int tryAddMiner(final CUnit acolyte, final CBehaviorAcolyteHarvest behaviorAcolyteHarvest) {
		if (behaviorAcolyteHarvest == null) {
			throw new NullPointerException();
		}
		int minerIndex = NO_MINER;
		double minerDistSq = Float.MAX_VALUE;
		for (int i = 0; i < this.activeMiners.length; i++) {
			if (this.activeMiners[i] == null) {
				final double thisMineDistSq = acolyte.distanceSquaredNoCollision(this.minerLocs[i].x,
						this.minerLocs[i].y);
				if (thisMineDistSq < minerDistSq) {
					minerIndex = i;
					minerDistSq = thisMineDistSq;
				}
			}
		}
		if (minerIndex != NO_MINER) {
			this.activeMiners[minerIndex] = behaviorAcolyteHarvest;
			this.currentActiveMinerCount++;
		}
		return minerIndex;
	}

	public void removeMiner(final CBehaviorAcolyteHarvest behaviorAcolyteHarvest) {
		if (behaviorAcolyteHarvest == null) {
			throw new NullPointerException();
		}
		for (int i = 0; i < this.activeMiners.length; i++) {
			if (this.activeMiners[i] == behaviorAcolyteHarvest) {
				this.activeMiners[i] = null;
				this.currentActiveMinerCount--;
			}
		}
	}

	public int getMaxNumberOfMiners() {
		return this.maxNumberOfMiners;
	}

	public float getRadiusOfMiningRing() {
		return this.radiusOfMiningRing;
	}

	public void setGoldPerInterval(final int goldPerInterval) {
		this.goldPerInterval = goldPerInterval;
	}

	public void setIntervalDuration(final float intervalDuration) {
		this.intervalDuration = intervalDuration;
	}

	public void setMaxNumberOfMiners(final int maxNumberOfMiners) {
		this.maxNumberOfMiners = maxNumberOfMiners;
	}

	public void setRadiusOfMiningRing(final float radiusOfMiningRing) {
		this.radiusOfMiningRing = radiusOfMiningRing;
	}

	public Vector2 getMinerLoc(final int index) {
		return this.minerLocs[index];
	}
}
