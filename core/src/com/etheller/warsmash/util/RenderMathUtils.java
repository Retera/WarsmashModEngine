package com.etheller.warsmash.util;

import java.util.List;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public enum RenderMathUtils {
	;
	public static final Quaternion QUAT_DEFAULT = new Quaternion(0, 0, 0, 1);
	public static final Vector3 VEC3_ONE = new Vector3(1, 1, 1);
	public static final Vector3 VEC3_UNIT_X = new Vector3(1, 0, 0);
	public static final Vector3 VEC3_UNIT_Y = new Vector3(0, 1, 0);
	public static final Vector3 VEC3_UNIT_Z = new Vector3(0, 0, 1);
	public static final float[] FLOAT_VEC3_ZERO = new float[] { 0, 0, 0 };
	public static final float[] FLOAT_QUAT_DEFAULT = new float[] { 0, 0, 0, 1 };
	public static final float[] FLOAT_VEC3_ONE = new float[] { 1, 1, 1 };

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
		out.val[Matrix4.M01] = (xy + wz) * sx;
		out.val[Matrix4.M02] = (xz - wy) * sx;
		out.val[Matrix4.M03] = 0;
		out.val[Matrix4.M10] = (xy - wz) * sy;
		out.val[Matrix4.M11] = (1 - (xx + zz)) * sy;
		out.val[Matrix4.M12] = (yz + wx) * sy;
		out.val[Matrix4.M13] = 0;
		out.val[Matrix4.M20] = (xz + wy) * sz;
		out.val[Matrix4.M21] = (yz - wx) * sz;
		out.val[Matrix4.M22] = (1 - (xx + yy)) * sz;
		out.val[Matrix4.M23] = 0;
		out.val[Matrix4.M30] = (v.x + pivot.x) - ((out.val[Matrix4.M00] * pivot.x) + (out.val[Matrix4.M10] * pivot.y)
				+ (out.val[Matrix4.M20] * pivot.z));
		out.val[Matrix4.M31] = (v.y + pivot.y) - ((out.val[Matrix4.M01] * pivot.x) + (out.val[Matrix4.M11] * pivot.y)
				+ (out.val[Matrix4.M21] * pivot.z));
		out.val[Matrix4.M32] = (v.z + pivot.z) - ((out.val[Matrix4.M02] * pivot.x) + (out.val[Matrix4.M12] * pivot.y)
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
		out.val[Matrix4.M01] = (xy + wz) * sx;
		out.val[Matrix4.M02] = (xz - wy) * sx;
		out.val[Matrix4.M03] = 0;
		out.val[Matrix4.M10] = (xy - wz) * sy;
		out.val[Matrix4.M11] = (1 - (xx + zz)) * sy;
		out.val[Matrix4.M12] = (yz + wx) * sy;
		out.val[Matrix4.M13] = 0;
		out.val[Matrix4.M20] = (xz + wy) * sz;
		out.val[Matrix4.M21] = (yz - wx) * sz;
		out.val[Matrix4.M22] = (1 - (xx + yy)) * sz;
		out.val[Matrix4.M23] = 0;
		out.val[Matrix4.M30] = v.x;
		out.val[Matrix4.M31] = v.y;
		out.val[Matrix4.M32] = v.z;
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
		out.val[Matrix4.M01] = 0;
		out.val[Matrix4.M02] = 0;
		out.val[Matrix4.M03] = 0;
		out.val[Matrix4.M10] = 0;
		out.val[Matrix4.M11] = f;
		out.val[Matrix4.M12] = 0;
		out.val[Matrix4.M13] = 0;
		out.val[Matrix4.M20] = 0;
		out.val[Matrix4.M21] = 0;
		out.val[Matrix4.M23] = -1;
		out.val[Matrix4.M30] = 0;
		out.val[Matrix4.M31] = 0;
		out.val[Matrix4.M33] = 0;
		if (!Double.isNaN(far) && !Double.isInfinite(far)) {
			nf = 1 / (near - far);
			out.val[Matrix4.M22] = (far + near) * nf;
			out.val[Matrix4.M32] = (2 * far * near) * nf;
		}
		else {
			out.val[Matrix4.M22] = -1;
			out.val[Matrix4.M32] = -2 * near;
		}
		return out;
	}

	public static Matrix4 ortho(final Matrix4 out, final float left, final float right, final float bottom,
			final float top, final float near, final float far) {
		final float lr = 1 / (left - right);
		final float bt = 1 / (bottom - top);
		final float nf = 1 / (near - far);
		out.val[Matrix4.M00] = -2 * lr;
		out.val[Matrix4.M01] = 0;
		out.val[Matrix4.M02] = 0;
		out.val[Matrix4.M03] = 0;

		out.val[Matrix4.M10] = 0;
		out.val[Matrix4.M11] = -2 * bt;
		out.val[Matrix4.M12] = 0;
		out.val[Matrix4.M13] = 0;

		out.val[Matrix4.M20] = 0;
		out.val[Matrix4.M21] = 0;
		out.val[Matrix4.M22] = 2 * nf;
		out.val[Matrix4.M23] = 0;

		out.val[Matrix4.M30] = (left + right) * lr;
		out.val[Matrix4.M31] = (top + bottom) * bt;
		out.val[Matrix4.M32] = (far + near) * nf;
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
		final float y = 1 - ((2 * (v.y - viewport.y)) / viewport.height);
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

	public static int testCell(final Vector4[] planes, final int left, final int right, final int bottom, final int top,
			int first) {
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

	public static float distance2Plane2(final Vector4 plane, final int px, final int py) {
		return (plane.x * px) + (plane.y * py) + plane.w;
	}

	public static int testSphere(final Vector4[] planes, final float x, final float y, final float z, final int r,
			int first) {
		if (first == -1) {
			first = 0;
		}

		for (int i = 0; i < 6; i++) {
			final int index = (first + i) % 6;

			if (distanceToPlane3(planes[index], x, y, z) <= -r) {
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

}
