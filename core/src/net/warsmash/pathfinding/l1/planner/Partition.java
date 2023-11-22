package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.vertex.IPoint;

public class Partition {
	public double x;
	public List<IPoint> left;
	public List<IPoint> right;
	public List<IPoint> on;
	public List<IPoint> vis;

	public Partition(final double x, final List<IPoint> left, final List<IPoint> right, final List<IPoint> on,
			final List<IPoint> vis) {
		this.x = x;
		this.left = left;
		this.right = right;
		this.on = on;
		this.vis = vis;
	}
}
