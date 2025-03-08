package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class MultiStockDelayProcessor {
	private final Map<War3ID, StockInfo> rawcodeToStockInfo = new HashMap<>();
	private final int replenishTimerNextUpdate = Integer.MAX_VALUE;

	public MultiStockDelayProcessor(List<War3ID> itemsSold) {
		for (final War3ID itemSold : itemsSold) {
			this.rawcodeToStockInfo.put(itemSold, new StockInfo());
		}
	}

	public void beginStockReplenish(final CSimulation game, final CUnit cUnit, final War3ID itemTypeId,
			final float replenishTime) {
//		beginStockReplenishWithoutNotify(game, itemTypeId, replenishTime);
		cUnit.fireCooldownsChangedEvent();
	}

//	private void beginStockReplenishWithoutNotify(final CSimulation game, final War3ID itemTypeId,
//			final float replenishTime) {
//		final int gameTurnTick = game.getGameTurnTick();
//		this.rawcodeToStockReplenishTime.put(itemTypeId.getValue(),
//				gameTurnTick + (int) StrictMath.ceil(replenishTime / WarsmashConstants.SIMULATION_STEP_TIME));
//		this.rawcodeToStockStartTime.put(itemTypeId.getValue(), gameTurnTick);
//	}
//
//	public int getStockDelayRemainingTicks(final CSimulation game, final CUnit cUnit, final War3ID abilityId) {
//		final int expireTime = this.rawcodeToStockReplenishTime.get(abilityId.getValue(), -1);
//		final int gameTurnTick = game.getGameTurnTick();
//		if ((expireTime == -1) || (expireTime <= gameTurnTick)) {
//			return 0;
//		}
//		return expireTime - gameTurnTick;
//	}
//
//	public int getStockDelayLengthDisplayTicks(final CSimulation game, final CUnit cUnit, final War3ID itemTypeId) {
//		final int startTime = this.rawcodeToStockStartTime.get(itemTypeId.getValue(), -1);
//		final int expireTime = this.rawcodeToStockReplenishTime.get(itemTypeId.getValue(), -1);
//		if ((startTime == -1) || (expireTime == -1)) {
//			return 0;
//		}
//		return expireTime - startTime;
//	}

	public void init(CSimulation game) {
		final int gameTurnTick = game.getGameTurnTick();
		for (final Map.Entry<War3ID, StockInfo> typeAndStock : this.rawcodeToStockInfo.entrySet()) {
			final War3ID itemSold = typeAndStock.getKey();
			final StockInfo stockInfo = typeAndStock.getValue();
			final CItemType itemType = game.getItemData().getItemType(itemSold);
			if (itemType != null) {
				final int startDelay = itemType.getStockStartDelay();
				if (startDelay == 0) {
					stockInfo.setStock(itemType.getStockMax());
				}
				else {
					stockInfo.setDelay(gameTurnTick,
							gameTurnTick + (int) StrictMath.ceil(startDelay / WarsmashConstants.SIMULATION_STEP_TIME));
				}
			}
		}
	}

	private void updateStock(CSimulation game, final CUnit cUnit) {
		final int gameTurnTick = game.getGameTurnTick();
		for (final Map.Entry<War3ID, StockInfo> typeAndStock : this.rawcodeToStockInfo.entrySet()) {
			final War3ID itemSold = typeAndStock.getKey();
			final StockInfo stockInfo = typeAndStock.getValue();
			final CItemType itemType = game.getItemData().getItemType(itemSold);
			if (itemType != null) {
				if (stockInfo.getStock() < itemType.getStockMax()) {
					final int elapsedBeyondStockEnd = gameTurnTick - stockInfo.getDelayEndTime();
					if (elapsedBeyondStockEnd >= 0) {
						final int intervalsCompleted = elapsedBeyondStockEnd / itemType.getStockReplenishInterval();
						final int timeRemaining = elapsedBeyondStockEnd % itemType.getStockReplenishInterval();
					}
				}
			}
		}
	}

	private static final class StockInfo {
		private int stock;
		private int delayEndTime;
		private int delayStartTime;

		public int getStock() {
			return this.stock;
		}

		public void setStock(int stock) {
			this.stock = stock;
		}

		public int getDelayEndTime() {
			return this.delayEndTime;
		}

		public int getDelayStartTime() {
			return this.delayStartTime;
		}

		public void setDelay(int delayStartTime, int delayEndTime) {
			this.delayEndTime = delayEndTime;
			this.delayStartTime = delayStartTime;
		}
	}
}
