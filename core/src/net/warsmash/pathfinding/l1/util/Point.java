package net.warsmash.pathfinding.l1.util;

import net.warsmash.pathfinding.l1.vertex.IPoint;

public class Point implements IPoint {
	public final double x, y;

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof IPoint) {
			final IPoint c = (IPoint) o;
			return ((int) (c.getX() * 100) == (int) (this.x * 100)) && ((int) (c.getY() * 100) == (int) (this.y * 100));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (((int) x & 0xFFFF) << 16) + ((int) y & 0xFFFF);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

}
