package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CAbilityEntangledMine extends CAbilityOverlayedMine implements CAbilitySpell {

	private int goldPerInterval;
	private float intervalDuration;

	private int activeGoldIntervalIndex;
	private int nextGoldTick;

	public CAbilityEntangledMine(final int handleId, final War3ID code, final War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= this.nextGoldTick) {
			this.nextGoldTick = gameTurnTick + (int) (this.intervalDuration / WarsmashConstants.SIMULATION_STEP_TIME);
			final CAbilityCargoHold cargoData = unit.getCargoData();
			if (cargoData != null) {
				this.activeGoldIntervalIndex = (this.activeGoldIntervalIndex + 1) % cargoData.getCargoCapacity();
				if (this.activeGoldIntervalIndex >= cargoData.getCargoCount()) {
					return;
				}
			}
			final CAbilityGoldMinable parentGoldMineAbility = getParentGoldMineAbility();
			if (parentGoldMineAbility != null) {
				final int totalGoldAvailable = parentGoldMineAbility.getGold();
				if (totalGoldAvailable > 0) {
					final CPlayer player = game.getPlayer(unit.getPlayerIndex());
					final int goldGained = Math.min(totalGoldAvailable, this.goldPerInterval);
					player.addGold(goldGained);
					parentGoldMineAbility.setGold(totalGoldAvailable - goldGained);
					game.unitGainResourceEvent(unit, player.getId(), ResourceType.GOLD, goldGained);
				}
				if (parentGoldMineAbility.getGold() == 0) {
					unit.kill(game);
				}
			}
		}
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
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
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void populate(final GameObject worldEditorAbility, final int level) {
		this.goldPerInterval = worldEditorAbility.getFieldAsInteger(AbilityFields.DATA_A + level, 0);
		this.intervalDuration = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

}
