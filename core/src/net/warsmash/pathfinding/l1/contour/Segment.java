package net.warsmash.pathfinding.l1.contour;

public class Segment {
	public double start;
	public double end;
	public boolean direction;
	public double height;
	public boolean visited;
	public Segment next;
	public Segment prev;

	public Segment(final double start, final double end, final boolean direction, final double height) {
		this.start = start;
		this.end = end;
		this.direction = direction;
		this.height = height;
		this.visited = false;
		this.next = null;
		this.prev = null;
	}
}
