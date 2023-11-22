package net.warsmash.pathfinding.l1.planner;

import net.warsmash.pathfinding.l1.util.GridUtil;

public class SizedGridView implements PathfinderGrid {
	private final byte[][] clearanceMap;
	private final int clearanceRequired;

	public SizedGridView(final byte[][] clearanceMap, final int clearanceRequired) {
		this.clearanceMap = clearanceMap;
		this.clearanceRequired = clearanceRequired;
	}

	@Override
	public int getWidth() {
		return clearanceMap.length;
	}

	@Override
	public int getHeight() {
		return clearanceMap[0].length;
	}

	@Override
	public boolean isPathable(final int x, final int y) {
		return clearanceMap[x][y] >= clearanceRequired;
	}

	@Override
	public PathfinderGrid createTranspose() {
		return GridUtil.transpose(this);
	}

}
