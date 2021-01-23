package com.etheller.warsmash.util;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Plane.PlaneSide;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class FixedIntersector {

	private final static Vector3 v0 = new Vector3();
	private final static Vector3 v1 = new Vector3();
	private final static Vector3 v2 = new Vector3();

	private static final Plane p = new Plane(new Vector3(), 0);
	private static final Vector3 i = new Vector3();

	/**
	 * Intersect a {@link Ray} and a triangle, returning the intersection point in
	 * intersection.
	 *
	 * @param ray          The ray
	 * @param t1           The first vertex of the triangle
	 * @param t2           The second vertex of the triangle
	 * @param t3           The third vertex of the triangle
	 * @param intersection The intersection point (optional)
	 * @return True in case an intersection is present.
	 */
	public static boolean intersectRayTriangle(final Ray ray, final Vector3 t1, final Vector3 t2, final Vector3 t3,
			final Vector3 intersection) {
		if (t2.epsilonEquals(t3)) {
			return false;
		}
		final Vector3 edge1 = v0.set(t2).sub(t1);
		final Vector3 edge2 = v1.set(t3).sub(t1);

		final Vector3 pvec = v2.set(ray.direction).crs(edge2);
		float det = edge1.dot(pvec);
		if (MathUtils.isZero(det)) {
			p.set(t1, t2, t3);
			if ((p.testPoint(ray.origin) == PlaneSide.OnPlane)
					&& Intersector.isPointInTriangle(ray.origin, t1, t2, t3)) {
				if (intersection != null) {
					intersection.set(ray.origin);
				}
				return true;
			}
			return false;
		}

		det = 1.0f / det;

		final Vector3 tvec = i.set(ray.origin).sub(t1);
		final float u = tvec.dot(pvec) * det;
		if ((u < 0.0f) || (u > 1.0f)) {
			return false;
		}

		final Vector3 qvec = tvec.crs(edge1);
		final float v = ray.direction.dot(qvec) * det;
		if ((v < 0.0f) || ((u + v) > 1.0f)) {
			return false;
		}

		final float t = edge2.dot(qvec) * det;
		if (t < 0) {
			return false;
		}

		if (intersection != null) {
			if (t <= MathUtils.FLOAT_ROUNDING_ERROR) {
				intersection.set(ray.origin);
			}
			else {
				ray.getEndPoint(intersection, t);
			}
		}

		return true;
	}
}
