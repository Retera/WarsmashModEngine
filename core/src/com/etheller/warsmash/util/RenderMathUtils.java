package com.etheller.warsmash.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public enum RenderMathUtils {
	;
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	public static final Quaternion QUAT_DEFAULT = new Quaternion(0, 0, 0, 1);
	public static final Vector3 VEC3_ONE = new Vector3(1, 1, 1);
	public static final Vector3 VEC3_UNIT_X = new Vector3(1, 0, 0);
	public static final Vector3 VEC3_UNIT_Y = new Vector3(0, 1, 0);
	public static final Vector3 VEC3_UNIT_Z = new Vector3(0, 0, 1);
	public static final float[] FLOAT_VEC3_ZERO = new float[] { 0, 0, 0 };
	public static final float[] FLOAT_QUAT_DEFAULT = new float[] { 0, 0, 0, 1 };
	public static final float[] FLOAT_VEC3_ONE = new float[] { 1, 1, 1 };
	public static final float HALF_PI = (float) (Math.PI / 2.0);

	// copied from ghostwolf and
	// https://www.blend4web.com/api_doc/libs_gl-matrix2.js.html
	public static void fromRotationTranslationScaleOrigin(final Quaternion q, final Vector3 v, final Vector3 s,
			final Matrix4 out, final Vector3 pivot) {
		final float x = q.x;
		final float y = q.y;
		final float z = q.z;
		final float w = q.w;
		final float x2 = x + x;
		final float y2 = y + y;
		final float z2 = z + z;
		final float xx = x * x2;
		final float xy = x * y2;
		final float xz = x * z2;
		final float yy = y * y2;
		final float yz = y * z2;
		final float zz = z * z2;
		final float wx = w * x2;
		final float wy = w * y2;
		final float wz = w * z2;
		final float sx = s.x;
		final float sy = s.y;
		final float sz = s.z;
		out.val[Matrix4.M00] = (1 - (yy + zz)) * sx;
		out.val[Matrix4.M10] = (xy + wz) * sx;
		out.val[Matrix4.M20] = (xz - wy) * sx;
		out.val[Matrix4.M30] = 0;
		out.val[Matrix4.M01] = (xy - wz) * sy;
		out.val[Matrix4.M11] = (1 - (xx + zz)) * sy;
		out.val[Matrix4.M21] = (yz + wx) * sy;
		out.val[Matrix4.M31] = 0;
		out.val[Matrix4.M02] = (xz + wy) * sz;
		out.val[Matrix4.M12] = (yz - wx) * sz;
		out.val[Matrix4.M22] = (1 - (xx + yy)) * sz;
		out.val[Matrix4.M32] = 0;
		out.val[Matrix4.M03] = (v.x + pivot.x) - ((out.val[Matrix4.M00] * pivot.x) + (out.val[Matrix4.M01] * pivot.y)
				+ (out.val[Matrix4.M02] * pivot.z));
		out.val[Matrix4.M13] = (v.y + pivot.y) - ((out.val[Matrix4.M10] * pivot.x) + (out.val[Matrix4.M11] * pivot.y)
				+ (out.val[Matrix4.M12] * pivot.z));
		out.val[Matrix4.M23] = (v.z + pivot.z) - ((out.val[Matrix4.M20] * pivot.x) + (out.val[Matrix4.M21] * pivot.y)
				+ (out.val[Matrix4.M22] * pivot.z));
		out.val[Matrix4.M33] = 1;
	}

	// copied from
	// https://www.blend4web.com/api_doc/libs_gl-matrix2.js.html
	public static void fromRotationTranslationScale(final Quaternion q, final Vector3 v, final Vector3 s,
			final Matrix4 out) {
		final float x = q.x;
		final float y = q.y;
		final float z = q.z;
		final float w = q.w;
		final float x2 = x + x;
		final float y2 = y + y;
		final float z2 = z + z;
		final float xx = x * x2;
		final float xy = x * y2;
		final float xz = x * z2;
		final float yy = y * y2;
		final float yz = y * z2;
		final float zz = z * z2;
		final float wx = w * x2;
		final float wy = w * y2;
		final float wz = w * z2;
		final float sx = s.x;
		final float sy = s.y;
		final float sz = s.z;
		out.val[Matrix4.M00] = (1 - (yy + zz)) * sx;
		out.val[Matrix4.M10] = (xy + wz) * sx;
		out.val[Matrix4.M20] = (xz - wy) * sx;
		out.val[Matrix4.M30] = 0;
		out.val[Matrix4.M01] = (xy - wz) * sy;
		out.val[Matrix4.M11] = (1 - (xx + zz)) * sy;
		out.val[Matrix4.M21] = (yz + wx) * sy;
		out.val[Matrix4.M31] = 0;
		out.val[Matrix4.M02] = (xz + wy) * sz;
		out.val[Matrix4.M12] = (yz - wx) * sz;
		out.val[Matrix4.M22] = (1 - (xx + yy)) * sz;
		out.val[Matrix4.M32] = 0;
		out.val[Matrix4.M03] = v.x;
		out.val[Matrix4.M13] = v.y;
		out.val[Matrix4.M23] = v.z;
		out.val[Matrix4.M33] = 1;
	}

	public static void mul(final Matrix4 dest, final Matrix4 left, final Matrix4 right) {
		dest.set(left); // TODO better performance here, remove the extra
						// copying
		dest.mul(right);
	}

	public static void mul(final Quaternion dest, final Quaternion left, final Quaternion right) {
		dest.set(left); // TODO better performance here, remove the extra
						// copying
		dest.mul(right);
	}

	public static Quaternion rotateX(final Quaternion out, final Quaternion a, float rad) {
		rad *= 0.5;

		final float ax = a.x, ay = a.y, az = a.z, aw = a.w;
		final float bx = (float) Math.sin(rad), bw = (float) Math.cos(rad);

		out.x = (ax * bw) + (aw * bx);
		out.y = (ay * bw) + (az * bx);
		out.z = (az * bw) - (ay * bx);
		out.w = (aw * bw) - (ax * bx);
		return out;
	}

	public static Quaternion rotateY(final Quaternion out, final Quaternion a, float rad) {
		rad *= 0.5;

		final float ax = a.x, ay = a.y, az = a.z, aw = a.w;
		final float by = (float) Math.sin(rad), bw = (float) Math.cos(rad);

		out.x = (ax * bw) - (az * by);
		out.y = (ay * bw) + (aw * by);
		out.z = (az * bw) + (ax * by);
		out.w = (aw * bw) - (ay * by);
		return out;
	}

	public static Quaternion rotateZ(final Quaternion out, final Quaternion a, float rad) {
		rad *= 0.5;

		final float ax = a.x, ay = a.y, az = a.z, aw = a.w;
		final float bz = (float) Math.sin(rad), bw = (float) Math.cos(rad);

		out.x = (ax * bw) + (ay * bz);
		out.y = (ay * bw) - (ax * bz);
		out.z = (az * bw) + (aw * bz);
		out.w = (aw * bw) - (az * bz);
		return out;
	}

	public static Matrix4 perspective(final Matrix4 out, final float fovy, final float aspect, final float near,
			final float far) {
		final float f = 1.0f / (float) Math.tan(fovy / 2), nf;
		out.val[Matrix4.M00] = f / aspect;
		out.val[Matrix4.M10] = 0;
		out.val[Matrix4.M20] = 0;
		out.val[Matrix4.M30] = 0;
		out.val[Matrix4.M01] = 0;
		out.val[Matrix4.M11] = f;
		out.val[Matrix4.M21] = 0;
		out.val[Matrix4.M31] = 0;
		out.val[Matrix4.M02] = 0;
		out.val[Matrix4.M12] = 0;
		out.val[Matrix4.M32] = -1;
		out.val[Matrix4.M03] = 0;
		out.val[Matrix4.M13] = 0;
		out.val[Matrix4.M33] = 0;
		if (!Double.isNaN(far) && !Double.isInfinite(far)) {
			nf = 1 / (near - far);
			out.val[Matrix4.M22] = (far + near) * nf;
			out.val[Matrix4.M23] = (2 * far * near) * nf;
		}
		else {
			out.val[Matrix4.M22] = -1;
			out.val[Matrix4.M23] = -2 * near;
		}
		return out;
	}

	public static Matrix4 ortho(final Matrix4 out, final float left, final float right, final float bottom,
			final float top, final float near, final float far) {
		final float lr = 1 / (left - right);
		final float bt = 1 / (bottom - top);
		final float nf = 1 / (near - far);
		out.val[Matrix4.M00] = -2 * lr;
		out.val[Matrix4.M10] = 0;
		out.val[Matrix4.M20] = 0;
		out.val[Matrix4.M30] = 0;

		out.val[Matrix4.M01] = 0;
		out.val[Matrix4.M11] = -2 * bt;
		out.val[Matrix4.M21] = 0;
		out.val[Matrix4.M31] = 0;

		out.val[Matrix4.M02] = 0;
		out.val[Matrix4.M12] = 0;
		out.val[Matrix4.M22] = 2 * nf;
		out.val[Matrix4.M32] = 0;

		out.val[Matrix4.M03] = (left + right) * lr;
		out.val[Matrix4.M13] = (top + bottom) * bt;
		out.val[Matrix4.M23] = (far + near) * nf;
		out.val[Matrix4.M33] = 1;
		return out;
	}

	public static void unpackPlanes(final Vector4[] planes, final Matrix4 m) {
		final float a00 = m.val[Matrix4.M00], a01 = m.val[Matrix4.M01], a02 = m.val[Matrix4.M02],
				a03 = m.val[Matrix4.M03], a10 = m.val[Matrix4.M10], a11 = m.val[Matrix4.M11], a12 = m.val[Matrix4.M12],
				a13 = m.val[Matrix4.M13], a20 = m.val[Matrix4.M20], a21 = m.val[Matrix4.M21], a22 = m.val[Matrix4.M22],
				a23 = m.val[Matrix4.M23], a30 = m.val[Matrix4.M30], a31 = m.val[Matrix4.M31], a32 = m.val[Matrix4.M32],
				a33 = m.val[Matrix4.M33];

		// Left clipping plane
		Vector4 plane = planes[0];
		plane.x = a30 + a00;
		plane.y = a31 + a01;
		plane.z = a32 + a02;
		plane.w = a33 + a03;

		// Right clipping plane
		plane = planes[1];
		plane.x = a30 - a00;
		plane.y = a31 - a01;
		plane.z = a32 - a02;
		plane.w = a33 - a03;

		// Top clipping plane
		plane = planes[2];
		plane.x = a30 - a10;
		plane.y = a31 - a11;
		plane.z = a32 - a12;
		plane.w = a33 - a13;

		// Bottom clipping plane
		plane = planes[3];
		plane.x = a30 + a10;
		plane.y = a31 + a11;
		plane.z = a32 + a12;
		plane.w = a33 + a13;

		// Near clipping plane
		plane = planes[4];
		plane.x = a30 + a20;
		plane.y = a31 + a21;
		plane.z = a32 + a22;
		plane.w = a33 + a23;

		// Far clipping plane
		plane = planes[5];
		plane.x = a30 - a20;
		plane.y = a31 - a21;
		plane.z = a32 - a22;
		plane.w = a33 - a23;

		normalizePlane(planes[0], planes[0]);
		normalizePlane(planes[1], planes[1]);
		normalizePlane(planes[2], planes[2]);
		normalizePlane(planes[3], planes[3]);
		normalizePlane(planes[4], planes[4]);
		normalizePlane(planes[5], planes[5]);
	}

	public static void normalizePlane(final Vector4 out, final Vector4 plane) {
		final float len = Vector3.len(plane.x, plane.y, plane.z);

		out.x = plane.x / len;
		out.y = plane.y / len;
		out.z = plane.z / len;
		out.w = plane.w / len;
	}

	public static float distanceToPlane(final Vector4 plane, final Vector3 point) {
		return (plane.x * point.x) + (plane.y * point.y) + (plane.z * point.z) + plane.w;
	}

	private static final Vector4 heap = new Vector4();

	public static Vector3 unproject(final Vector3 out, final Vector3 v, final Matrix4 inverseMatrix,
			final Rectangle viewport) {
		final float x = ((2 * (v.x - viewport.x)) / viewport.width) - 1;
		final float y = ((2 * (v.y - viewport.y)) / viewport.height) - 1;
		final float z = (2 * v.z) - 1;

		heap.set(x, y, z, 1);
		Vector4.transformMat4(heap, heap, inverseMatrix);
		out.set(heap.x / heap.w, heap.y / heap.w, heap.z / heap.w);

		return out;
	}

	public static int testCell(final List<Vector4> planes, final int left, final int right, final int bottom,
			final int top, int first) {
		if (first == -1) {
			first = 0;
		}

		for (int i = 0; i < 6; i++) {
			final int index = (first + i) % 6;
			final Vector4 plane = planes.get(index);

			if ((distance2Plane2(plane, left, bottom) < 0) && (distance2Plane2(plane, left, top) < 0)
					&& (distance2Plane2(plane, right, top) < 0) && (distance2Plane2(plane, right, bottom) < 0)) {
				return index;
			}
		}

		return -1;
	}

	public static int testCell(final Vector4[] planes, final float left, final float right, final float bottom,
			final float top, int first) {
		if (first == -1) {
			first = 0;
		}

		for (int i = 0; i < 6; i++) {
			final int index = (first + i) % 6;
			final Vector4 plane = planes[index];

			if ((distance2Plane2(plane, left, bottom) < 0) && (distance2Plane2(plane, left, top) < 0)
					&& (distance2Plane2(plane, right, top) < 0) && (distance2Plane2(plane, right, bottom) < 0)) {
				return index;
			}
		}

		return -1;
	}

	public static float distance2Plane2(final Vector4 plane, final float px, final float py) {
		return (plane.x * px) + (plane.y * py) + plane.w;
	}

	public static int testSphere(final Vector4[] planes, final float x, final float y, final float z, final float r,
			int first) {
		if (first == -1) {
			first = 0;
		}

		for (int i = 0; i < 6; i++) {
			final int index = (first + i) % 6;

			if (distanceToPlane3(planes[index], x, y, z) < -r) {
				return index;
			}
		}

		return -1;
	}

	public static float distanceToPlane3(final Vector4 plane, final float px, final float py, final float pz) {
		return (plane.x * px) + (plane.y * py) + (plane.z * pz) + plane.w;
	}

	public static float randomInRange(final float a, final float b) {
		return (float) (a + (Math.random() * (b - a)));
	}

	public static float clamp(final float x, final float minVal, final float maxVal) {
		return Math.min(Math.max(x, minVal), maxVal);
	}

	public static float lerp(final float a, final float b, final float t) {
		return a + (t * (b - a));
	}

	public static float hermite(final float a, final float b, final float c, final float d, final float t) {
		final float factorTimes2 = t * t;
		final float factor1 = (factorTimes2 * ((2 * t) - 3)) + 1;
		final float factor2 = (factorTimes2 * (t - 2)) + t;
		final float factor3 = factorTimes2 * (t - 1);
		final float factor4 = factorTimes2 * (3 - (2 * t));
		return (a * factor1) + (b * factor2) + (c * factor3) + (d * factor4);
	}

	public static float bezier(final float a, final float b, final float c, final float d, final float t) {
		final float invt = 1 - t;
		final float factorTimes2 = t * t;
		final float inverseFactorTimesTwo = invt * invt;
		final float factor1 = inverseFactorTimesTwo * invt;
		final float factor2 = 3 * t * inverseFactorTimesTwo;
		final float factor3 = 3 * factorTimes2 * invt;
		final float factor4 = factorTimes2 * t;

		return (a * factor1) + (b * factor2) + (c * factor3) + (d * factor4);
	}

	public static final float EPSILON = 0.000001f;

	public static float[] slerp(final float[] out, final float[] a, final float[] b, final float t) {
		final float ax = a[0], ay = a[1], az = a[2], aw = a[3];
		float bx = b[0], by = b[1], bz = b[2], bw = b[3];

		float omega, cosom, sinom, scale0, scale1;

		// calc cosine
		cosom = (ax * bx) + (ay * by) + (az * bz) + (aw * bw);
		// adjust signs (if necessary)
		if (cosom < 0.0) {
			cosom = -cosom;
			bx = -bx;
			by = -by;
			bz = -bz;
			bw = -bw;
		}
		// calculate coefficients
		if ((1.0 - cosom) > EPSILON) {
			// standard case (slerp)
			omega = (float) Math.acos(cosom);
			sinom = (float) Math.sin(omega);
			scale0 = (float) (Math.sin((1.0 - t) * omega) / sinom);
			scale1 = (float) (Math.sin(t * omega) / sinom);
		}
		else {
			// "from" and "to" quaternions are very close
			// ... so we can do a linear interpolation
			scale0 = 1.0f - t;
			scale1 = t;
		}
		// calculate final values
		out[0] = (scale0 * ax) + (scale1 * bx);
		out[1] = (scale0 * ay) + (scale1 * by);
		out[2] = (scale0 * az) + (scale1 * bz);
		out[3] = (scale0 * aw) + (scale1 * bw);

		return out;
	}

	private static final float[] sqlerpHeap1 = new float[4];
	private static final float[] sqlerpHeap2 = new float[4];

	public static float[] sqlerp(final float[] out, final float[] a, final float[] b, final float[] c, final float[] d,
			final float t) {
		slerp(sqlerpHeap1, a, d, t);
		slerp(sqlerpHeap2, b, c, t);
		slerp(out, sqlerpHeap1, sqlerpHeap2, 2 * t * (1 - t));
		return out;
	}

	static Vector3 best = new Vector3();
	static Vector3 tmp = new Vector3();
	static Vector3 tmp1 = new Vector3();
	static Vector3 tmp2 = new Vector3();
	static Vector3 tmp3 = new Vector3();

	/**
	 * Intersects the given ray with list of triangles. Returns the nearest
	 * intersection point in intersection
	 *
	 * @param ray          The ray
	 * @param vertices     the vertices
	 * @param indices      the indices, each successive 3 shorts index the 3
	 *                     vertices of a triangle
	 * @param vertexSize   the size of a vertex in floats
	 * @param intersection The nearest intersection point (optional)
	 * @return Whether the ray and the triangles intersect.
	 */
	public static boolean intersectRayTriangles(final Ray ray, final float[] vertices, final int[] indices,
			final int vertexSize, final Vector3 intersection) {
		float min_dist = Float.MAX_VALUE;
		boolean hit = false;

		if ((indices.length % 3) != 0) {
			throw new RuntimeException("triangle list size is not a multiple of 3");
		}

		for (int i = 0; i < indices.length; i += 3) {
			final int i1 = indices[i] * vertexSize;
			final int i2 = indices[i + 1] * vertexSize;
			final int i3 = indices[i + 2] * vertexSize;

			final boolean result = Intersector.intersectRayTriangle(ray,
					tmp1.set(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]),
					tmp2.set(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]),
					tmp3.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), tmp);

			if (result == true) {
				final float dist = ray.origin.dst2(tmp);
				if (dist < min_dist) {
					min_dist = dist;
					best.set(tmp);
					hit = true;
				}
			}
		}

		if (hit == false) {
			return false;
		}
		else {
			if (intersection != null) {
				intersection.set(best);
			}
			return true;
		}
	}

	/**
	 * Intersects the given ray with list of triangles. Returns the nearest
	 * intersection point in intersection
	 *
	 * @param ray          The ray
	 * @param vertices     the vertices
	 * @param indices      the indices, each successive 3 shorts index the 3
	 *                     vertices of a triangle
	 * @param vertexSize   the size of a vertex in floats
	 * @param intersection The nearest intersection point (optional)
	 * @return Whether the ray and the triangles intersect.
	 */
	public static boolean intersectRayTriangles(final Ray ray, final float[] vertices, final int[] indices,
			final int vertexSize, final Vector3 worldLocation, final float facingRadians, final Vector3 intersection) {
		float min_dist = Float.MAX_VALUE;
		boolean hit = false;

		if ((indices.length % 3) != 0) {
			throw new RuntimeException("triangle list size is not a multiple of 3");
		}

		final float facingX_X = (float) Math.cos(facingRadians);
		final float facingX_Y = (float) Math.sin(facingRadians);
		final double halfPi = Math.PI / 2;
		final float facingY_X = (float) Math.cos(facingRadians + halfPi);
		final float facingY_Y = (float) Math.sin(facingRadians + halfPi);
		for (int i = 0; i < indices.length; i += 3) {
			final int i1 = indices[i] * vertexSize;
			final int i2 = indices[i + 1] * vertexSize;
			final int i3 = indices[i + 2] * vertexSize;

			final boolean result = Intersector.intersectRayTriangle(ray,
					tmp1.set((vertices[i1] * facingX_X) + (vertices[i1 + 1] * facingY_X) + worldLocation.x,
							(vertices[i1] * facingX_Y) + (vertices[i1 + 1] * facingY_Y) + worldLocation.y,
							vertices[i1 + 2] + worldLocation.z),
					tmp2.set((vertices[i2] * facingX_X) + (vertices[i2 + 1] * facingY_X) + worldLocation.x,
							(vertices[i2] * facingX_Y) + (vertices[i2 + 1] * facingY_Y) + worldLocation.y,
							vertices[i2 + 2] + worldLocation.z),
					tmp3.set((vertices[i3] * facingX_X) + (vertices[i3 + 1] * facingY_X) + worldLocation.x,
							(vertices[i3] * facingX_Y) + (vertices[i3 + 1] * facingY_Y) + worldLocation.y,
							vertices[i3 + 2] + worldLocation.z),
					tmp);

			if (result == true) {
				final float dist = ray.origin.dst2(tmp);
				if (dist < min_dist) {
					min_dist = dist;
					best.set(tmp);
					hit = true;
				}
			}
		}

		if (hit == false) {
			return false;
		}
		else {
			if (intersection != null) {
				intersection.set(best);
			}
			return true;
		}
	}

	// ==== All of the following "wrap" calls are horribly inefficient. Eventually
	// they should be removed entirely with better design.
	// Until that happens, be sure to only call them during setup and not while the
	// simulation is live. Otherwise you'll probably get some
	// bad lag (and memory leaks?).
	public static ShortBuffer wrapFaces(final int[] faces) {
		final ShortBuffer wrapper = ByteBuffer.allocateDirect(faces.length * 2).order(ByteOrder.nativeOrder())
				.asShortBuffer();
		for (final int face : faces) {
			wrapper.put((short) face);
		}
		wrapper.clear();
		return wrapper;
	}

	public static ByteBuffer wrap(final byte[] skin) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(skin.length).order(ByteOrder.nativeOrder());
		wrapper.put(skin);
		wrapper.clear();
		return wrapper;
	}

	public static FloatBuffer wrap(final float[] positions) {
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(positions.length * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		wrapper.put(positions);
		wrapper.clear();
		return wrapper;
	}

	public static IntBuffer wrap(final int[] positions) {
		final IntBuffer wrapper = ByteBuffer.allocateDirect(positions.length * 4).order(ByteOrder.nativeOrder())
				.asIntBuffer();
		wrapper.put(positions);
		wrapper.clear();
		return wrapper;
	}

	public static ByteBuffer wrapAsBytes(final int[] positions) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(positions.length).order(ByteOrder.nativeOrder());
		for (final int face : positions) {
			wrapper.put((byte) face);
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrap(final short[] cornerTextures) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(cornerTextures.length).order(ByteOrder.nativeOrder());
		for (final short face : cornerTextures) {
			wrapper.put((byte) face);
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrapShort(final short[] cornerTextures) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(cornerTextures.length * 2).order(ByteOrder.nativeOrder());
		for (final short face : cornerTextures) {
			wrapper.putShort(face);
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrapPairs(final float[][] quadVertices) {
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(quadVertices.length * 8).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		for (int i = 0; i < quadVertices.length; i++) {
			for (int j = 0; j < 2; j++) {
				wrapper.put(quadVertices[i][j]);
			}
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrap(final int[][] quadIndices) {
		final IntBuffer wrapper = ByteBuffer.allocateDirect(quadIndices.length * 3 * 4).order(ByteOrder.nativeOrder())
				.asIntBuffer();
		for (int i = 0; i < quadIndices.length; i++) {
			for (int j = 0; j < 3; j++) {
				wrapper.put(quadIndices[i][j]);
			}
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrap(final List<float[]> vertices) {
		if (vertices.isEmpty()) {
			return null;
		}
		final int expectedNumberOfFloats = vertices.get(0).length;
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(vertices.size() * expectedNumberOfFloats * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		for (final float[] subArray : vertices) {
			for (final float f : subArray) {
				wrapper.put(f);
			}
		}
		wrapper.clear();
		return wrapper;
	}

	public static Buffer wrapFaces(final List<int[]> indices) {
		if (indices.isEmpty()) {
			return null;
		}
		final int expectedNumberOfValues = indices.get(0).length;
		final ShortBuffer wrapper = ByteBuffer.allocateDirect(indices.size() * expectedNumberOfValues * 2)
				.order(ByteOrder.nativeOrder()).asShortBuffer();
		for (final int[] subArray : indices) {
			for (final int value : subArray) {
				wrapper.put((short) value);
			}
		}
		wrapper.clear();
		return wrapper;
	}
}
