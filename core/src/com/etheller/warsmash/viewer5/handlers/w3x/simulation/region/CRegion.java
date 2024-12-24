package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.interpreter.ast.scope.trigger.RemovableTriggerEvent;

public class CRegion {
	private Rectangle currentBounds;
	private boolean complexRegion;
	private final List<CRegionTriggerEnter> enterTriggers = new ArrayList<>();
	private final List<CRegionTriggerLeave> leaveTriggers = new ArrayList<>();

	public void addRect(final Rectangle rect, final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			this.currentBounds = new Rectangle(rect);
			regionManager.addRectForRegion(this, this.currentBounds);
		}
		else {
			if (!this.complexRegion) {
				convertToComplexRegionAndAddRect(rect, regionManager);
			}
			else {
				complexRegionAddRect(rect, regionManager);
			}
		}
	}

	public void clearRect(final Rectangle rect, final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			return;
		}
		if (this.complexRegion) {
			regionManager.removeRectForRegion(this, this.currentBounds);
			regionManager.removeComplexRegionCells(this, rect);
			regionManager.computeNewMinimumComplexRegionBounds(this, this.currentBounds);
			regionManager.addRectForRegion(this, this.currentBounds);
		}
		else {
			this.complexRegion = true;
			regionManager.addComplexRegionCells(this, this.currentBounds);
			regionManager.removeComplexRegionCells(this, rect);
		}
	}

	public void remove(final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			return;
		}
		if (this.complexRegion) {
			regionManager.removeComplexRegionCells(this, this.currentBounds);
		}
		regionManager.removeRectForRegion(this, this.currentBounds);
	}

	public void addCell(final float x, final float y, final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			this.complexRegion = true;
			this.currentBounds = new Rectangle(x, y, 0, 0);
			regionManager.addComplexRegionCell(this, x, y, this.currentBounds);
			regionManager.addRectForRegion(this, this.currentBounds);
		}
		else {
			regionManager.removeRectForRegion(this, this.currentBounds);
			if (!this.complexRegion) {
				regionManager.addComplexRegionCells(this, this.currentBounds);
				this.complexRegion = true;
			}
			regionManager.addComplexRegionCell(this, x, y, this.currentBounds);
			regionManager.addRectForRegion(this, this.currentBounds);
		}
	}

	public void clearCell(final float x, final float y, final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			return;
		}
		else {
			regionManager.removeRectForRegion(this, this.currentBounds);
			if (!this.complexRegion) {
				regionManager.addComplexRegionCells(this, this.currentBounds);
				this.complexRegion = true;
			}
			regionManager.clearComplexRegionCell(this, x, y, this.currentBounds);
			regionManager.addRectForRegion(this, this.currentBounds);
		}
	}

	private void complexRegionAddRect(final Rectangle rect, final CRegionManager regionManager) {
		regionManager.removeRectForRegion(this, this.currentBounds);
		regionManager.addComplexRegionCells(this, rect);
		this.currentBounds = this.currentBounds.merge(rect);
		regionManager.addRectForRegion(this, this.currentBounds);
	}

	private void convertToComplexRegionAndAddRect(final Rectangle rect, final CRegionManager regionManager) {
		regionManager.removeRectForRegion(this, this.currentBounds);
		this.complexRegion = true;
		regionManager.addComplexRegionCells(this, this.currentBounds);
		regionManager.addComplexRegionCells(this, rect);
		this.currentBounds = this.currentBounds.merge(rect);
		regionManager.addRectForRegion(this, this.currentBounds);
	}

	public Rectangle getCurrentBounds() {
		return this.currentBounds;
	}

	public void setCurrentBounds(final Rectangle currentBounds) {
		this.currentBounds = currentBounds;
	}

	public boolean isComplexRegion() {
		return this.complexRegion;
	}

	public void setComplexRegion(final boolean complexRegion) {
		this.complexRegion = complexRegion;
	}

	public boolean contains(final float x, final float y, final CRegionManager regionManager) {
		if (this.currentBounds == null) {
			return false;
		}
		if (this.complexRegion) {
			return regionManager.isPointInComplexRegion(this, x, y);
		}
		return this.currentBounds.contains(x, y);
	}

	public List<CRegionTriggerEnter> getEnterTriggers() {
		return this.enterTriggers;
	}

	public List<CRegionTriggerLeave> getLeaveTriggers() {
		return this.leaveTriggers;
	}

	public RemovableTriggerEvent add(final CRegionTriggerEnter triggerEnter) {
		this.enterTriggers.add(triggerEnter);
		return new RemovableTriggerEvent(triggerEnter.getTrigger()) {
			@Override
			public void remove() {
				CRegion.this.enterTriggers.remove(triggerEnter);
			}
		};
	}

	public RemovableTriggerEvent add(final CRegionTriggerLeave leaveTrigger) {
		this.leaveTriggers.add(leaveTrigger);
		return new RemovableTriggerEvent(leaveTrigger.getTrigger()) {
			@Override
			public void remove() {
				CRegion.this.leaveTriggers.remove(leaveTrigger);
			}
		};
	}
}
