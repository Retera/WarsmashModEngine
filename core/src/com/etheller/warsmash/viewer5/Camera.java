package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.Vector4;

public class Camera {
	private static final Vector3 vectorHeap = new Vector3();
	private static final Vector3 vectorHeap2 = new Vector3();
	private static final Vector3 vectorHeap3 = new Vector3();
	private static final Quaternion quatHeap = new Quaternion();
	private static final Matrix4 matHeap = new Matrix4();

	public final Rectangle rect;

	private boolean isPerspective;
	private float fov;
	private float aspect;

	public boolean isOrtho;
	private float leftClipPlane;
	private float rightClipPlane;
	private float bottomClipPlane;
	private float topClipPlane;

	private float nearClipPlane;
	private float farClipPlane;

	public final Vector3 location;
	public final Quaternion rotation;

	public Quaternion inverseRotation;
	/**
	 * World -> View.
	 */
	public final Matrix4 viewMatrix;
	/**
	 * View -> Clip.
	 */
	private final Matrix4 projectionMatrix;
	/**
	 * World -> Clip.
	 */
	public final Matrix4 viewProjectionMatrix;
	/**
	 * View -> World.
	 */
	private final Matrix4 inverseViewMatrix;
	/**
	 * Clip -> World.
	 */
	private final Matrix4 inverseViewProjectionMatrix;
	public final Vector3 directionX;
	public final Vector3 directionY;
	public final Vector3 directionZ;
	public final Vector3[] vectors;
	public final Vector3[] billboardedVectors;

	public final Vector4[] planes;
	private boolean dirty;

	public Camera() {
		// rencered viewport
		this.rect = new Rectangle();

		// perspective values
		this.isPerspective = true;
		this.fov = 0;
		this.aspect = 0;

		// Orthogonal values
		this.isOrtho = false;
		this.leftClipPlane = 0f;
		this.rightClipPlane = 0f;
		this.bottomClipPlane = 0f;
		this.topClipPlane = 0f;

		// Shared values
		this.nearClipPlane = 0f;
		this.farClipPlane = 0f;

		// World values
		this.location = new Vector3();
		this.rotation = new Quaternion();

		// Derived values.
		this.inverseRotation = new Quaternion();
		this.viewMatrix = new Matrix4();
		this.projectionMatrix = new Matrix4();
		this.viewProjectionMatrix = new Matrix4();
		this.inverseViewMatrix = new Matrix4();
		this.inverseViewProjectionMatrix = new Matrix4();
		this.directionX = new Vector3();
		this.directionY = new Vector3();
		this.directionZ = new Vector3();

		// First four vectors are the corners of a 2x2 rectangle, the last three vectors
		// are the unit axes
		this.vectors = new Vector3[] { new Vector3(-1, -1, 0), new Vector3(-1, 1, 0), new Vector3(1, 1, 0),
				new Vector3(1, -1, 0), new Vector3(1, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1) };

		// First four vectors are the corners of a 2x2 rectangle billboarded to the
		// camera, the last three vectors are the unit axes billboarded
		this.billboardedVectors = new Vector3[] { new Vector3(), new Vector3(), new Vector3(), new Vector3(),
				new Vector3(), new Vector3(), new Vector3() };

		// Left, right, top, bottom, near, far
		this.planes = new Vector4[] { new Vector4(), new Vector4(), new Vector4(), new Vector4(), new Vector4(),
				new Vector4() };

		this.dirty = true;
	}

	public void perspective(final float fov, final float aspect, final float near, final float far) {
		this.isPerspective = true;
		this.isOrtho = false;
		this.fov = fov;
		this.aspect = aspect;
		this.nearClipPlane = near;
		this.farClipPlane = far;

		this.dirty = true;
	}

	public void ortho(final float left, final float right, final float bottom, final float top, final float near,
			final float far) {
		this.isPerspective = false;
		this.isOrtho = true;
		this.leftClipPlane = left;
		this.rightClipPlane = right;
		this.bottomClipPlane = bottom;
		this.topClipPlane = top;
		this.nearClipPlane = near;
		this.farClipPlane = far;

		this.dirty = true;
	}

	public void viewport(final Rectangle viewport) {
		this.rect.set(viewport);

		this.aspect = viewport.width / viewport.height;

		this.dirty = true;
	}

	public float getAspect() {
		return this.aspect;
	}

	public void setLocation(final Vector3 location) {
		this.location.set(location);

		this.dirty = true;
	}

	public void move(final Vector3 offset) {
		this.location.add(offset);

		this.dirty = true;
	}

	public void setRotation(final Quaternion rotation) {
		this.rotation.set(rotation);

		this.dirty = true;
	}

	public void rotate(final Quaternion rotation) {
		this.rotation.mul(rotation);

		this.dirty = true;
	}

	public void setRotationAngles(final float horizontalAngle, final float verticalAngle) {
		this.rotation.idt();
//		this.rotateAngles(horizontalAngle, verticalAngle);
		throw new UnsupportedOperationException(
				"Ghostwolf called a function that does not exist, so I did not know what to do here");
	}

	public void rotateAround(final Quaternion rotation, final Vector3 point) {
		this.rotate(rotation);

		quatHeap.conjugate(); // TODO ?????????
		vectorHeap.set(this.location);
		vectorHeap.sub(point);
		rotation.transform(vectorHeap);
		vectorHeap.add(point);
		this.location.set(vectorHeap);
	}

	public void setRotationAround(final Quaternion rotation, final Vector3 point) {
		this.setRotation(rotation);
		;

		final float length = vectorHeap.set(this.location).sub(point).len();

		quatHeap.conjugate(); // TODO ?????????
		vectorHeap.set(RenderMathUtils.VEC3_UNIT_Z);
		quatHeap.transform(vectorHeap);
		vectorHeap.scl(length);
		this.location.set(vectorHeap.add(point));
	}

	public void setRotationAroundAngles(final float horizontalAngle, final float verticalAngle, final Vector3 point) {
		quatHeap.idt();
		RenderMathUtils.rotateX(quatHeap, quatHeap, verticalAngle);
		RenderMathUtils.rotateY(quatHeap, quatHeap, horizontalAngle);

		this.setRotationAround(quatHeap, point);
	}

	public void face(final Vector3 point, final Vector3 worldUp) {
		matHeap.setToLookAt(this.location, point, worldUp);
		matHeap.getRotation(this.rotation);

		this.dirty = true;
	}

	public void moveToAndFace(final Vector3 location, final Vector3 target, final Vector3 worldUp) {
		this.location.set(location);
		this.face(target, worldUp);
	}

	public void reset() {
		this.location.set(0, 0, 0);
		this.rotation.idt();

		this.dirty = true;
	}

	public void update() {
		if (this.dirty) {
			final Vector3 location = this.location;
			final Quaternion rotation = this.rotation;
			final Quaternion inverseRotation = this.inverseRotation;
			final Matrix4 viewMatrix = this.viewMatrix;
			final Matrix4 projectionMatrix = this.projectionMatrix;
			final Matrix4 viewProjectionMatrix = this.viewProjectionMatrix;
			final Vector3[] vectors = this.vectors;
			final Vector3[] billboardedVectors = this.billboardedVectors;

			if (this.isPerspective) {
				RenderMathUtils.perspective(projectionMatrix, this.fov, this.aspect, this.nearClipPlane,
						this.farClipPlane);
			}
			else {
				RenderMathUtils.ortho(projectionMatrix, this.leftClipPlane, this.rightClipPlane, this.bottomClipPlane,
						this.topClipPlane, this.nearClipPlane, this.farClipPlane);
			}

			rotation.toMatrix(viewMatrix.val);
			viewMatrix.translate(vectorHeap.set(location).scl(-1));
			inverseRotation.set(rotation).conjugate();

			// World projection matrix
			// World space -> NDC space
			viewProjectionMatrix.set(projectionMatrix).mul(viewMatrix);

			// Recalculate the camera's frustum planes
			RenderMathUtils.unpackPlanes(this.planes, viewProjectionMatrix);

			// Inverse world matrix
			// Camera space -> world space
			this.inverseViewMatrix.set(viewMatrix).inv();

			this.directionX.set(RenderMathUtils.VEC3_UNIT_X);
			inverseRotation.transform(this.directionX);
			this.directionY.set(RenderMathUtils.VEC3_UNIT_Y);
			inverseRotation.transform(this.directionY);
			this.directionZ.set(RenderMathUtils.VEC3_UNIT_Z);
			inverseRotation.transform(this.directionZ);

			// Inverse world projection matrix
			// NDC space -> World space
			this.inverseViewProjectionMatrix.set(viewProjectionMatrix);
			this.inverseViewProjectionMatrix.inv();

			for (int i = 0; i < 7; i++) {
				billboardedVectors[i].set(vectors[i]);
				inverseRotation.transform(billboardedVectors[i]);
			}
			this.dirty = false;
		}
	}

	public boolean testSphere(final Vector3 center, final float radius) {
		for (final Vector4 plane : this.planes) {
			if (RenderMathUtils.distanceToPlane(plane, center) <= -radius) {
				return false;
			}
		}
		return true;
	}

	public Vector3 cameraToWorld(final Vector3 out, final Vector3 v) {
		return out.set(v).prj(this.inverseViewMatrix);
	}

	public Vector3 worldToCamera(final Vector3 out, final Vector3 v) {
		return out.set(v).prj(this.viewMatrix);
	}

	public Vector2 worldToScreen(final Vector2 out, final Vector3 v) {
		final Rectangle viewport = this.rect;

		vectorHeap.set(v);
		vectorHeap.prj(this.viewProjectionMatrix);

		out.x = Math.round(((vectorHeap.x + 1) / 2) * viewport.width);
		out.y = ((Gdx.graphics.getHeight() - viewport.y - viewport.height) + (viewport.height))
				- Math.round(((vectorHeap.y + 1) / 2) * viewport.height);

		return out;
	}

	public float[] screenToWorldRay(final float[] out, final Vector2 v) {
		final Vector3 a = vectorHeap;
		final Vector3 b = vectorHeap2;
		final Vector3 c = vectorHeap3;
		final float x = v.x;
		final float y = v.y;
		final Rectangle viewport = this.rect;

		// Intersection on the near-plane
		RenderMathUtils.unproject(a, c.set(x, y, 0), this.inverseViewProjectionMatrix, viewport);

		// Intersection on the far-plane
		RenderMathUtils.unproject(b, c.set(x, y, 1), this.inverseViewProjectionMatrix, viewport);

		out[0] = a.x;
		out[1] = a.y;
		out[2] = a.z;
		out[3] = b.x;
		out[4] = b.y;
		out[5] = b.z;

		return out;
	}
}
