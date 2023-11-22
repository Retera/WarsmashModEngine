package net.warsmash.pathfinding.l1.contour;

import net.warsmash.pathfinding.l1.vertex.IPoint;

public class ContourVertex implements IPoint {
	public double x;
	public double y;
	public Segment segment;
	public int orientation;

	public ContourVertex(final double x, final double y, final Segment segment, final int orientation) {
		this.x = x;
		this.y = y;
		this.segment = segment;
		this.orientation = orientation;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
