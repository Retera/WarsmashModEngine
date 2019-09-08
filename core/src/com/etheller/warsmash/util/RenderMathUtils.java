package com.etheller.warsmash.util;

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
		dest.set(left); // TODO better performance here, remove the extra copying
		dest.mul(right);
	}

	public static void mul(final Quaternion dest, final Quaternion left, final Quaternion right) {
		dest.set(left); // TODO better performance here, remove the extra copying
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
}
