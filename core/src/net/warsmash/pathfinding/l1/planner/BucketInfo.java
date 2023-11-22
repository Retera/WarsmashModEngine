package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.vertex.IPoint;

public class BucketInfo {
	public List<IPoint> left;
	public List<IPoint> right;
	public List<IPoint> on;
	public IPoint steiner0;
	public IPoint steiner1;
	public double y0;
	public double y1;

	public BucketInfo(final List<IPoint> left, final List<IPoint> right, final List<IPoint> on, final IPoint steiner0,
			final IPoint steiner1, final double y0, final double y1) {
		this.left = left;
		this.right = right;
		this.on = on;
		this.steiner0 = steiner0;
		this.steiner1 = steiner1;
		this.y0 = y0;
		this.y1 = y1;
	}
}
