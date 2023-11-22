package net.warsmash.pathfinding.l1;

import net.warsmash.pathfinding.l1.vertex.IPoint;

public class Orientation {
	public static final double EPSILON = 1.1102230246251565e-16;
	public static final double ERRBOUND3 = (3.0 + (16.0 * EPSILON)) * EPSILON;

	public static double orientation3Exact(final IPoint m0, final IPoint m1, final IPoint m2) {
		final double p = ((m1.getY() * m2.getX()) + (-m2.getY() * m1.getX()))
				+ ((m0.getY() * m1.getX()) + (-m1.getY() * m0.getX()));
		final double n = (m0.getY() * m2.getX()) + (-m2.getY() * m0.getX());
		return p - n;
	}

	public static double orient(final IPoint a, final IPoint b, final IPoint c) {
		final double l = (a.getY() - c.getY()) * (b.getX() - c.getX());
		final double r = (a.getX() - c.getX()) * (b.getY() - c.getY());
		final double det = l - r;
		double s = 0;
		if (l > 0) {
			if (r <= 0) {
				return det;
			}
			else {
				s = l + r;
			}
		}
		else if (l < 0) {
			if (r >= 0) {
				return det;
			}
			else {
				s = -(l + r);
			}
		}
		else {
			return det;
		}
		final double tol = ERRBOUND3 * s;
		if ((det >= tol) || (det <= -tol)) {
			return det;
		}
		return orientation3Exact(a, b, c);
	}
}
