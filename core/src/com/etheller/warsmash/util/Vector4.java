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

	/** the x-component of this vector **/
	public float x;
	/** the y-component of this vector **/
	public float y;
	/** the z-component of this vector **/
	public float z;
	/** the w-component of this vector **/
	public float w;

	public final static Vector4 X = new Vector4(1, 0, 0, 0);
	public final static Vector4 Y = new Vector4(0, 1, 0, 0);
	public final static Vector4 Z = new Vector4(0, 0, 1, 0);
	public final static Vector4 W = new Vector4(0, 0, 0, 1);
	public final static Vector4 Zero = new Vector4(0, 0, 0, 0);

	private final static Matrix4 tmpMat = new Matrix4();

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
		this.set(x, y, z, w);
	}

	/**
	 * Creates a vector from the given vector
	 *
	 * @param vector The vector
	 */
	public Vector4(final Vector4 vector) {
		this.set(vector);
	}

	/**
	 * Creates a vector from the given array. The array must have at least 4
	 * elements.
	 *
	 * @param values The array
	 */
	public Vector4(final float[] values) {
		this.set(values[0], values[1], values[2], values[3]);
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
		return this.set(vector.x, vector.y, vector.z, vector.w);
	}

	/**
	 * Sets the components from the array. The array must have at least 4 elements
	 *
	 * @param values The array
	 * @return this vector for chaining
	 */
	public Vector4 set(final float[] values) {
		return this.set(values[0], values[1], values[2], values[3]);
	}

	@Override
	public Vector4 cpy() {
		return new Vector4(this);
	}

	@Override
	public Vector4 add(final Vector4 vector) {
		return this.add(vector.x, vector.y, vector.z, vector.w);
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
		return this.set(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	/**
	 * Adds the given value to all four components of the vector.
	 *
	 * @param values The value
	 * @return This vector for chaining
	 */
	public Vector4 add(final float values) {
		return this.set(this.x + values, this.y + values, this.z + values, this.w + values);
	}

	@Override
	public Vector4 sub(final Vector4 a_vec) {
		return this.sub(a_vec.x, a_vec.y, a_vec.z, a_vec.w);
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
		return this.set(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	/**
	 * Subtracts the given value from all components of this vector
	 *
	 * @param value The value
	 * @return This vector for chaining
	 */
	public Vector4 sub(final float value) {
		return this.set(this.x - value, this.y - value, this.z - value, this.w - value);
	}

	@Override
	public Vector4 scl(final float scalar) {
		return this.set(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
	}

	@Override
	public Vector4 scl(final Vector4 other) {
		return this.set(this.x * other.x, this.y * other.y, this.z * other.z, this.w * other.w);
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
		return this.set(this.x * vx, this.y * vy, this.z * vz, this.z * vw);
	}

	@Override
	public Vector4 mulAdd(final Vector4 vec, final float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		this.z += vec.z * scalar;
		this.w += vec.w * scalar;
		return this;
	}

	@Override
	public Vector4 mulAdd(final Vector4 vec, final Vector4 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		this.z += vec.z * mulVec.z;
		this.w += vec.w * mulVec.w;
		return this;
	}

	/** @return The euclidian length */
	public static float len(final float x, final float y, final float z, final float w) {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	@Override
	public float len() {
		return (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z) + (this.w * this.w));
	}

	/** @return The squared euclidian length */
	public static float len2(final float x, final float y, final float z, final float w) {
		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	@Override
	public float len2() {
		return (this.x * this.x) + (this.y * this.y) + (this.z * this.z) + (this.w * this.w);
	}

	/**
	 * @param vector The other vector
	 * @return Whether this and the other vector are equal
	 */
	public boolean idt(final Vector4 vector) {
		return (this.x == vector.x) && (this.y == vector.y) && (this.z == vector.z) && (this.w == vector.w);
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
		final float a = vector.x - this.x;
		final float b = vector.y - this.y;
		final float c = vector.z - this.z;
		final float d = vector.w - this.w;
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
		final float a = point.x - this.x;
		final float b = point.y - this.y;
		final float c = point.z - this.z;
		final float d = point.w - this.w;
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
		final float len2 = this.len2();
		if ((len2 == 0f) || (len2 == 1f)) {
			return this;
		}
		return this.scl(1f / (float) Math.sqrt(len2));
	}

	/** @return The dot product between the two vectors */
	public static float dot(final float x1, final float y1, final float z1, final float w1, final float x2,
			final float y2, final float z2, final float w2) {
		return (x1 * x2) + (y1 * y2) + (z1 * z2) + (w1 * w2);
	}

	@Override
	public float dot(final Vector4 vector) {
		return (this.x * vector.x) + (this.y * vector.y) + (this.z * vector.z) + (this.w * vector.w);
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
		return (this.x == 0) && (this.y == 0) && (this.z == 0);
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
		this.x += alpha * (target.x - this.x);
		this.y += alpha * (target.y - this.y);
		this.z += alpha * (target.z - this.z);
		return this;
	}

	@Override
	public Vector4 interpolate(final Vector4 target, final float alpha, final Interpolation interpolator) {
		// TODO
		return lerp(target, interpolator.apply(0f, 1f, alpha));
	}

	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
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
		result = (prime * result) + NumberUtils.floatToIntBits(this.x);
		result = (prime * result) + NumberUtils.floatToIntBits(this.y);
		result = (prime * result) + NumberUtils.floatToIntBits(this.z);
		result = (prime * result) + NumberUtils.floatToIntBits(this.w);
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
		if (NumberUtils.floatToIntBits(this.x) != NumberUtils.floatToIntBits(other.x)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(this.y) != NumberUtils.floatToIntBits(other.y)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(this.z) != NumberUtils.floatToIntBits(other.z)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(this.w) != NumberUtils.floatToIntBits(other.w)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean epsilonEquals(final Vector4 other, final float epsilon) {
		if (other == null) {
			return false;
		}
		if (Math.abs(other.x - this.x) > epsilon) {
			return false;
		}
		if (Math.abs(other.y - this.y) > epsilon) {
			return false;
		}
		if (Math.abs(other.z - this.z) > epsilon) {
			return false;
		}
		if (Math.abs(other.w - this.w) > epsilon) {
			return false;
		}
		return true;
	}

	/**
	 * Compares this vector with the other vector, using the supplied epsilon for
	 * fuzzy equality testing.
	 *
	 * @return whether the vectors are the same.
	 */
	public boolean epsilonEquals(final float x, final float y, final float z, final float w, final float epsilon) {
		if (Math.abs(x - this.x) > epsilon) {
			return false;
		}
		if (Math.abs(y - this.y) > epsilon) {
			return false;
		}
		if (Math.abs(z - this.z) > epsilon) {
			return false;
		}
		if (Math.abs(w - this.w) > epsilon) {
			return false;
		}
		return true;
	}

	@Override
	public Vector4 setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
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