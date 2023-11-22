package net.warsmash.pathfinding.l1.planner;

import java.util.List;

import net.warsmash.pathfinding.l1.vertex.Vertex;

public class Bucket {
	public double y0;
	public double y1;
	public Vertex top;
	public Vertex bottom;
	public List<Vertex> left;
	public List<Vertex> right;
	public List<Vertex> on;

	public Bucket(final double y0, final double y1, final Vertex top, final Vertex bottom, final List<Vertex> left,
			final List<Vertex> right, final List<Vertex> on) {
		this.y0 = y0;
		this.y1 = y1;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.on = on;
	}

}
