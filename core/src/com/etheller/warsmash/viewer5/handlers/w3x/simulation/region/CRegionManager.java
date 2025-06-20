package com.etheller.warsmash.viewer5.handlers.w3x.simulation.region;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.Quadtree;
import com.etheller.warsmash.util.QuadtreeIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CRegionManager {
	private static Rectangle tempRect = new Rectangle();
	private final Quadtree<CRegion> regionTree;
	private final RegionChecker regionChecker = new RegionChecker();
	private final Map<Point, List<CRegion>> cellRegions;
	private final PathingGrid pathingGrid;

	public CRegionManager(final Rectangle entireMapBounds, final PathingGrid pathingGrid) {
		this.regionTree = new Quadtree<>(entireMapBounds);
		this.cellRegions = new HashMap<>();
		this.pathingGrid = pathingGrid;
	}

	public void addRectForRegion(final CRegion region, final Rectangle rect) {
		this.regionTree.add(region, rect);
	}

	public void removeRectForRegion(final CRegion region, final Rectangle rect) {
		this.regionTree.remove(region, rect);
	}

	/**
	 * Calls back on the enum function for every region that touches the given area.
	 * Sometimes, for performance, this algorithm is designed to call the enum
	 * function twice for the same region, because our expected use case is to store
	 * the regions in a set that guarantees uniqueness anyway (see CUnit and/or
	 * other uses of this method).
	 */
	public void checkRegions(final Rectangle area, final CRegionEnumFunction enumFunction) {
		this.regionTree.intersect(area, this.regionChecker.reset(enumFunction));
		if (this.regionChecker.includesComplex) {
			final int minX = this.pathingGrid.getCellX(area.x);
			final int minY = this.pathingGrid.getCellY(area.y);
			final int maxX = this.pathingGrid.getCellX(area.x + area.width);
			final int maxY = this.pathingGrid.getCellY(area.y + area.height);
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {

					final List<CRegion> cellRegionsAtPoint = this.cellRegions.get(new Point(x, y));
					if (cellRegionsAtPoint != null) {
						for (final CRegion region : cellRegionsAtPoint) {
							if (enumFunction.call(region)) {
								return;
							}
						}
					}
				}
			}
		}
	}

	private static final class RegionChecker implements QuadtreeIntersector<CRegion> {
		private CRegionEnumFunction delegate;
		private boolean includesComplex = false;

		public RegionChecker reset(final CRegionEnumFunction delegate) {
			this.delegate = delegate;
			return this;
		}

		@Override
		public boolean onIntersect(final CRegion intersectingObject) {
			if (intersectingObject.isComplexRegion()) {
				this.includesComplex = true;
				// handle this type of region differently
				return false;
			}
			return this.delegate.call(intersectingObject);
		}

	}

	public void addComplexRegionCells(final CRegion region, final Rectangle currentBounds) {
		final int minX = this.pathingGrid.getCellX(currentBounds.x);
		final int minY = this.pathingGrid.getCellY(currentBounds.y);
		final int maxX = this.pathingGrid.getCellX(currentBounds.x + currentBounds.width);
		final int maxY = this.pathingGrid.getCellY(currentBounds.y + currentBounds.height);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final Point xy = new Point(x, y);
				List<CRegion> list = this.cellRegions.get(xy);
				if (list == null) {
					this.cellRegions.put(xy, list = new ArrayList<>());
				}
				list.add(region);
			}
		}
	}

	public void removeComplexRegionCells(final CRegion region, final Rectangle currentBounds) {
		final int minX = this.pathingGrid.getCellX(currentBounds.x);
		final int minY = this.pathingGrid.getCellY(currentBounds.y);
		final int maxX = this.pathingGrid.getCellX(currentBounds.x + currentBounds.width);
		final int maxY = this.pathingGrid.getCellY(currentBounds.y + currentBounds.height);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final Point xy = new Point(x, y);
				final List<CRegion> list = this.cellRegions.get(xy);
				if (list != null) {
					list.remove(region);
				}
				if (list.isEmpty()) {
					this.cellRegions.remove(xy);
				}
			}
		}
	}

	public void computeNewMinimumComplexRegionBounds(final CRegion region, final Rectangle complexRegionBounds) {
		final int minX = this.pathingGrid.getCellX(complexRegionBounds.x);
		final int minY = this.pathingGrid.getCellY(complexRegionBounds.y);
		final int maxX = this.pathingGrid.getCellX(complexRegionBounds.x + complexRegionBounds.width);
		final int maxY = this.pathingGrid.getCellY(complexRegionBounds.y + complexRegionBounds.height);
		float newMinX = this.pathingGrid.getWorldX(this.pathingGrid.getWidth() - 1);
		float newMaxX = this.pathingGrid.getWorldX(0);
		float newMinY = this.pathingGrid.getWorldY(this.pathingGrid.getHeight() - 1);
		float newMaxY = this.pathingGrid.getWorldY(0);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final Point xy = new Point(x, y);
				final List<CRegion> list = this.cellRegions.get(xy);
				if (list != null) {
					if (list.contains(region)) {
						final float worldX = this.pathingGrid.getWorldX(x);
						final float worldY = this.pathingGrid.getWorldY(y);
						final float wMinX = worldX - 16f;
						final float wMinY = worldY - 16f;
						final float wMaxX = worldX + 15f;
						final float wMaxY = worldY + 15f;
						if (wMinX < newMinX) {
							newMinX = wMinX;
						}
						if (wMinY < newMinY) {
							newMinY = wMinY;
						}
						if (wMaxX > newMaxX) {
							newMaxX = wMaxX;
						}
						if (wMaxY > newMaxY) {
							newMaxY = wMaxY;
						}
					}
				}
			}
		}
		complexRegionBounds.set(newMinX, newMinY, newMaxX - newMinX, newMaxY - newMinY);
	}

	public void addComplexRegionCell(final CRegion region, final float x, final float y,
			final Rectangle boundsToUpdate) {
		final int cellX = this.pathingGrid.getCellX(x);
		final int cellY = this.pathingGrid.getCellY(y);
		final Point cellXY = new Point(cellX, cellY);
		List<CRegion> list = this.cellRegions.get(cellXY);
		if (list == null) {
			this.cellRegions.put(cellXY, list = new ArrayList<>());
		}
		list.add(region);
		final float worldX = this.pathingGrid.getWorldX(cellX);
		final float worldY = this.pathingGrid.getWorldY(cellY);
		final float wMinX = worldX - 16f;
		final float wMinY = worldY - 16f;
		boundsToUpdate.merge(tempRect.set(wMinX, wMinY, 31f, 31f));
	}

	public void clearComplexRegionCell(final CRegion region, final float x, final float y,
			final Rectangle boundsToUpdate) {
		final int cellX = this.pathingGrid.getCellX(x);
		final int cellY = this.pathingGrid.getCellY(y);
		final Point cellXY = new Point(cellX, cellY);
		final List<CRegion> list = this.cellRegions.get(cellXY);
		if (list != null) {
			list.remove(region);
		}
		computeNewMinimumComplexRegionBounds(region, boundsToUpdate);
	}

	public boolean isPointInComplexRegion(final CRegion region, final float x, final float y) {
		final int cellX = this.pathingGrid.getCellX(x);
		final int cellY = this.pathingGrid.getCellY(y);
		final Point cellXY = new Point(cellX, cellY);
		final List<CRegion> list = this.cellRegions.get(cellXY);
		if (list != null) {
			return list.contains(region);
		}
		return false;
	}

	public void onUnitEnterRegion(final CUnit unit, final CRegion region) {
		for (final CRegionTriggerEnter enterTrigger : region.getEnterTriggers()) {
			enterTrigger.fire(unit, region);
		}
	}

	public void onUnitLeaveRegion(final CUnit unit, final CRegion region) {
		for (final CRegionTriggerLeave leaveTrigger : region.getLeaveTriggers()) {
			leaveTrigger.fire(unit, region);
		}
	}

}
