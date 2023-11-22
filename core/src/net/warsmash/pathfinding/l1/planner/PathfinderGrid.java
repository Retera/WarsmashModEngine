package net.warsmash.pathfinding.l1.planner;

public interface PathfinderGrid {

	int getWidth();

	int getHeight();

	boolean isPathable(int x, int y);

	PathfinderGrid createTranspose();

}
