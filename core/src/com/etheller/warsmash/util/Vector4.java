package com.etheller.warsmash.util;

import java.io.Serializable;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Encapsulates a 4D vector. Allows chaining operations by returning a reference
 * to itself in all modification methods.
 *
 * @author intrigus
 */
public class Vector4 implements Serializable, Vector<Vector4> {

	private static final char COMMA = ',';
	private static final char SPACE = ' ';
	private static final String OPENING_SQUARE_BRACKET = "[";
	private static final char CLOSING_SQUARE_BRACKET = ']';
	/** the x-component of this vector **/
	public float x;
	/** the y-component of this vector **/
	public float y;
	/** the z-component of this vector **/
	public float z;
	/** the w-component of this vector **/
	public float w;

	public static final Vector4 X = new Vector4(1, 0, 0, 0);
	public static final Vector4 Y = new Vector4(0, 1, 0, 0);
	public static final Vector4 Z = new Vector4(0, 0, 1, 0);
	public static final Vector4 W = new Vector4(0, 0, 0, 1);
	public static final Vector4 Zero = new Vector4(0, 0, 0, 0);

	private static final Matrix4 tmpMat = new Matrix4();

	/** Constructs a vector at (0,0,0,0) */
	public Vector4() {
	}

	/**
	 * Creates a vector with the given components
	 *
	 * @param x The x-component
	 * @param y The y-component
	 * @param z The z-component
	 * @param w The w-component
	 */
	public Vector4(final float x, final float y, final float z, final float w) {
		set(x, y, z, w);
	}

	/**
	 * Creates a vector from the given vector
	 *
	 * @param vector The vector
	 */
	public Vector4(final Vector4 vector) {
		set(vector);
	}

	/**
	 * Creates a vector from the given array. The array must have at least 4
	 * elements.
	 *
	 * @param values The array
	 */
	public Vector4(final float[] values) {
		set(values[0], values[1], values[2], values[3]);
	}

	/**
	 * Sets the vector to the given components
	 *
	 * @param x The x-component
	 * @param y The y-component
	 * @param z The z-component
	 * @param w The w-component
	 * @return this vector for chaining
	 */
	public Vector4 set(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	@Override
	public Vector4 set(final Vector4 vector) {
		return set(vector.x, vector.y, vector.z, vector.w);
	}

	/**
	 * Sets the components from the array. The array must have at least 4 elements
	 *
	 * @param values The array
	 * @return this vector for chaining
	 */
	public Vector4 set(final float[] values) {
		return set(values[0], values[1], values[2], values[3]);
	}

	@Override
	public Vector4 cpy() {
		return new Vector4(this);
	}

	@Override
	public Vector4 add(final Vector4 vector) {
		return add(vector.x, vector.y, vector.z, vector.w);
	}

	/**
	 * Adds the given vector to this component
	 *
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @param z The z-component of the other vector
	 * @param w The w-component of the other vector
	 * @return This vector for chaining.
	 */
	public Vector4 add(final float x, final float y, final float z, final float w) {
		return set(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	/**
	 * Adds the given value to all four components of the vector.
	 *
	 * @param values The value
	 * @return This vector for chaining
	 */
	public Vector4 add(final float values) {
		return set(x + values, y + values, z + values, w + values);
	}

	@Override
	public Vector4 sub(final Vector4 a_vec) {
		return sub(a_vec.x, a_vec.y, a_vec.z, a_vec.w);
	}

	/**
	 * Subtracts the other vector from this vector.
	 *
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @param z The z-component of the other vector
	 * @param w The w-component of the other vector
	 * @return This vector for chaining
	 */
	public Vector4 sub(final float x, final float y, final float z, final float w) {
		return set(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	/**
	 * Subtracts the given value from all components of this vector
	 *
	 * @param value The value
	 * @return This vector for chaining
	 */
	public Vector4 sub(final float value) {
		return set(x - value, y - value, z - value, w - value);
	}

	@Override
	public Vector4 scl(final float scalar) {
		return set(x * scalar, y * scalar, z * scalar, w * scalar);
	}

	@Override
	public Vector4 scl(final Vector4 other) {
		return set(x * other.x, y * other.y, z * other.z, w * other.w);
	}

	/**
	 * Scales this vector by the given values
	 *
	 * @param vx X value
	 * @param vy Y value
	 * @param vz Z value
	 * @param vw W value
	 * @return This vector for chaining
	 */
	public Vector4 scl(final float vx, final float vy, final float vz, final float vw) {
		return set(x * vx, y * vy, z * vz, z * vw);
	}

	@Override
	public Vector4 mulAdd(final Vector4 vec, final float scalar) {
		x += vec.x * scalar;
		y += vec.y * scalar;
		z += vec.z * scalar;
		w += vec.w * scalar;
		return this;
	}

	@Override
	public Vector4 mulAdd(final Vector4 vec, final Vector4 mulVec) {
		x += vec.x * mulVec.x;
		y += vec.y * mulVec.y;
		z += vec.z * mulVec.z;
		w += vec.w * mulVec.w;
		return this;
	}

	/** @return The euclidian length */
	public static float len(final float x, final float y, final float z, final float w) {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	@Override
	public float len() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	/** @return The squared euclidian length */
	public static float len2(final float x, final float y, final float z, final float w) {
		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	@Override
	public float len2() {
		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	/**
	 * @param vector The other vector
	 * @return Whether this and the other vector are equal
	 */
	public boolean idt(final Vector4 vector) {
		return (x == vector.x) && (y == vector.y) && (z == vector.z) && (w == vector.w);
	}

	/** @return The euclidian distance between the two specified vectors */
	public static float dst(final float x1, final float y1, final float z1, final float w1, final float x2,
			final float y2, final float z2, final float w2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		final float d = w2 - w1;
		return (float) Math.sqrt((a * a) + (b * b) + (c * c) + (d * d));
	}

	@Override
	public float dst(final Vector4 vector) {
		final float a = vector.x - x;
		final float b = vector.y - y;
		final float c = vector.z - z;
		final float d = vector.w - w;
		return (float) Math.sqrt((a * a) + (b * b) + (c * c) + (d * d));
	}

	/** @return the distance between this point and the given point */
	public float dst(final float x, final float y, final float z, final float w) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		final float d = w - this.w;
		return (float) Math.sqrt((a * a) + (b * b) + (c * c) + (d * d));
	}

	/** @return the squared distance between the given points */
	public static float dst2(final float x1, final float y1, final float z1, final float w1, final float x2,
			final float y2, final float z2, final float w2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		final float d = w2 - w1;
		return (a * a) + (b * b) + (c * c) + (d * d);
	}

	@Override
	public float dst2(final Vector4 point) {
		final float a = point.x - x;
		final float b = point.y - y;
		final float c = point.z - z;
		final float d = point.w - w;
		return (a * a) + (b * b) + (c * c) + (d * d);
	}

	/**
	 * Returns the squared distance between this point and the given point
	 *
	 * @param x The x-component of the other point
	 * @param y The y-component of the other point
	 * @param z The z-component of the other point
	 * @param w The w-component of the other point
	 * @return The squared distance
	 */
	public float dst2(final float x, final float y, final float z, final float w) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		final float d = w - this.w;
		return (a * a) + (b * b) + (c * c) + (d * d);
	}

	@Override
	public Vector4 nor() {
		final float len2 = len2();
		if ((len2 == 0f) || (len2 == 1f)) {
			return this;
		}
		return scl(1f / (float) Math.sqrt(len2));
	}

	/** @return The dot product between the two vectors */
	public static float dot(final float x1, final float y1, final float z1, final float w1, final float x2,
			final float y2, final float z2, final float w2) {
		return (x1 * x2) + (y1 * y2) + (z1 * z2) + (w1 * w2);
	}

	@Override
	public float dot(final Vector4 vector) {
		return (x * vector.x) + (y * vector.y) + (z * vector.z) + (w * vector.w);
	}

	/**
	 * Returns the dot product between this and the given vector.
	 *
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @param z The z-component of the other vector
	 * @param w The w-component of the other vector
	 * @return The dot product
	 */
	public float dot(final float x, final float y, final float z, final float w) {
		return (this.x * x) + (this.y * y) + (this.z * z) + (this.w * w);
	}

	@Override
	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	@Override
	public boolean isUnit(final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	@Override
	public boolean isZero() {
		return (x == 0) && (y == 0) && (z == 0);
	}

	@Override
	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	@Override
	public boolean isOnLine(final Vector4 other, final float epsilon) {
		throw new UnsupportedOperationException();
		// TODO
//		return len2((this.y * other.z) - (this.z * other.y), (this.z * other.x) - (this.x * other.z),
//				(this.x * other.y) - (this.y * other.x)) <= epsilon;
	}

	@Override
	public boolean isOnLine(final Vector4 other) {
		throw new UnsupportedOperationException();
		// TODO
//		return len2((this.y * other.z) - (this.z * other.y), (this.z * other.x) - (this.x * other.z),
//				(this.x * other.y) - (this.y * other.x)) <= MathUtils.FLOAT_ROUNDING_ERROR;
	}

	@Override
	public boolean isCollinear(final Vector4 other, final float epsilon) {
		return isOnLine(other, epsilon) && hasSameDirection(other);
	}

	@Override
	public boolean isCollinear(final Vector4 other) {
		return isOnLine(other) && hasSameDirection(other);
	}

	@Override
	public boolean isCollinearOpposite(final Vector4 other, final float epsilon) {
		return isOnLine(other, epsilon) && hasOppositeDirection(other);
	}

	@Override
	public boolean isCollinearOpposite(final Vector4 other) {
		return isOnLine(other) && hasOppositeDirection(other);
	}

	@Override
	public boolean isPerpendicular(final Vector4 vector) {
		return MathUtils.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular(final Vector4 vector, final float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean hasSameDirection(final Vector4 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean hasOppositeDirection(final Vector4 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Vector4 lerp(final Vector4 target, final float alpha) {
		// TODO
		x += alpha * (target.x - x);
		y += alpha * (target.y - y);
		z += alpha * (target.z - z);
		return this;
	}

	@Override
	public Vector4 interpolate(final Vector4 target, final float alpha, final Interpolation interpolator) {
		// TODO
		return lerp(target, interpolator.apply(0f, 1f, alpha));
	}

	@Override
	public String toString() {
		return OPENING_SQUARE_BRACKET +
				x + COMMA + SPACE +
				y + COMMA + SPACE +
				z + COMMA + SPACE +
				w + CLOSING_SQUARE_BRACKET;
	}

	@Override
	public Vector4 limit(final float limit) {
// TODO
		return limit2(limit * limit);
	}

	@Override
	public Vector4 limit2(final float limit2) {
		// TODO
		final float len2 = len2();
		if (len2 > limit2) {
			scl((float) Math.sqrt(limit2 / len2));
		}
		return this;
	}

	@Override
	public Vector4 setLength(final float len) {
		return setLength2(len * len);
	}

	@Override
	public Vector4 setLength2(final float len2) {
		final float oldLen2 = len2();
		return ((oldLen2 == 0) || (oldLen2 == len2)) ? this : scl((float) Math.sqrt(len2 / oldLen2));
	}

	@Override
	public Vector4 clamp(final float min, final float max) {
		final float len2 = len2();
		if (len2 == 0f) {
			return this;
		}
		final float max2 = max * max;
		if (len2 > max2) {
			return scl((float) Math.sqrt(max2 / len2));
		}
		final float min2 = min * min;
		if (len2 < min2) {
			return scl((float) Math.sqrt(min2 / len2));
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + NumberUtils.floatToIntBits(x);
		result = (prime * result) + NumberUtils.floatToIntBits(y);
		result = (prime * result) + NumberUtils.floatToIntBits(z);
		result = (prime * result) + NumberUtils.floatToIntBits(w);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Vector4 other = (Vector4) obj;

		return floatToIntBitsEquals(x, other.x)
				&& floatToIntBitsEquals(y, other.y)
				&& floatToIntBitsEquals(z, other.z)
				&& floatToIntBitsEquals(w, other.w);
	}

	private static boolean floatToIntBitsEquals(float a, float b) {
		return NumberUtils.floatToIntBits(a) == NumberUtils.floatToIntBits(b);
	}

	@Override
	public boolean epsilonEquals(final Vector4 other, final float epsilon) {
		return other != null && epsilonEquals(other.x, other.y, other.z, other.w, epsilon);
	}

	/**
	 * Compares this vector with the other vector, using the supplied epsilon for
	 * fuzzy equality testing.
	 *
	 * @return whether the vectors are the same.
	 */
	private boolean epsilonEquals(final float x, final float y, final float z, final float w, final float epsilon) {

		return epsilonEquals(x, this.x, epsilon)
				&& epsilonEquals(y, this.y, epsilon)
				&& epsilonEquals(z, this.z, epsilon)
				&& epsilonEquals(w, this.w, epsilon);
	}

	private static boolean epsilonEquals(float a, float b, float epsilon) {
		return Math.abs(a - b) < epsilon;
	}

	@Override
	public Vector4 setZero() {
		x = 0;
		y = 0;
		z = 0;
		w = 0;
		return this;
	}

	@Override
	public Vector4 setToRandomDirection() {
		throw new UnsupportedOperationException();
	}

	public static Vector4 transformMat4(final Vector4 out, final Vector4 a, final Matrix4 matrix) {
		final float x = a.x, y = a.y, z = a.z, w = a.w;
		final float[] m = matrix.val;
		out.x = (m[Matrix4.M00] * x) + (m[Matrix4.M01] * y) + (m[Matrix4.M02] * z) + (m[Matrix4.M03] * w);
		out.y = (m[Matrix4.M10] * x) + (m[Matrix4.M11] * y) + (m[Matrix4.M12] * z) + (m[Matrix4.M13] * w);
		out.z = (m[Matrix4.M20] * x) + (m[Matrix4.M21] * y) + (m[Matrix4.M22] * z) + (m[Matrix4.M23] * w);
		out.w = (m[Matrix4.M30] * x) + (m[Matrix4.M31] * y) + (m[Matrix4.M32] * z) + (m[Matrix4.M33] * w);
		return out;
	}
}