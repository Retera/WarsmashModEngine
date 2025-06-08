package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop;

public class ItemSellState {
	private int stock;
	private final int maxStock;
	private final int replenishIntervalTicks;
	private int nextStockReplenishGameTurnTick;
	private int currentIntervalTicks;

	public ItemSellState(final int maxStock, final int stockReplenishIntervalTicks) {
		this.maxStock = maxStock;
		this.replenishIntervalTicks = stockReplenishIntervalTicks;
	}

	public void init(final int gameTurnTick, final int stockStartDelayTicks) {
		this.nextStockReplenishGameTurnTick = gameTurnTick + stockStartDelayTicks;
		this.currentIntervalTicks = stockStartDelayTicks;
	}

	public void update(final int gameTurnTick) {
		if (this.stock < this.maxStock) {
			if (this.nextStockReplenishGameTurnTick == -1) {
				this.nextStockReplenishGameTurnTick = gameTurnTick + this.replenishIntervalTicks;
				this.currentIntervalTicks = this.replenishIntervalTicks;
			}
			if (gameTurnTick >= this.nextStockReplenishGameTurnTick) {
				this.stock++;
				if (this.stock < this.maxStock) {
					this.nextStockReplenishGameTurnTick = gameTurnTick + this.replenishIntervalTicks;
					this.currentIntervalTicks = this.replenishIntervalTicks;
				}
				else {
					this.nextStockReplenishGameTurnTick = -1;
				}
			}
		}
	}

	public int getStock() {
		return this.stock;
	}

	public int getNextStockReplenishGameTurnTick() {
		return this.nextStockReplenishGameTurnTick;
	}

	public int getCurrentIntervalTicks() {
		return this.currentIntervalTicks;
	}

	public boolean chargeStock(final int count) {
		if (this.stock >= count) {
			this.stock -= count;
			return true;
		}
		return false;
	}
}
