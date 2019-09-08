package com.etheller.warsmash.viewer;

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

	private final Rectangle rect;

	private boolean isPerspective;
	private float fov;
	private float aspect;

	private boolean isOrtho;
	private float leftClipPlane;
	private float rightClipPlane;
	private float bottomClipPlane;
	private float topClipPlane;

	private float nearClipPlane;
	private float farClipPlane;

	private final Vector3 location;
	private final Quaternion rotation;

	public Quaternion inverseRotation;
	private final Matrix4 worldMatrix;
	private final Matrix4 projectionMatrix;
	private final Matrix4 worldProjectionMatrix;
	private final Matrix4 inverseWorldMatrix;
	private final Matrix4 inverseRotationMatrix;
	private final Matrix4 inverseWorldProjectionMatrix;
	private final Vector3 directionX;
	private final Vector3 directionY;
	private final Vector3 directionZ;
	private final Vector3[] vectors;
	private final Vector3[] billboardedVectors;

	private final Vector4[] planes;
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
		this.worldMatrix = new Matrix4();
		this.projectionMatrix = new Matrix4();
		this.worldProjectionMatrix = new Matrix4();
		this.inverseWorldMatrix = new Matrix4();
		this.inverseRotationMatrix = new Matrix4();
		this.inverseWorldProjectionMatrix = new Matrix4();
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
	}

	public void viewport(final Rectangle viewport) {
		this.rect.set(viewport);

		this.aspect = viewport.width / viewport.height;

		this.dirty = true;
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
			this.dirty = true;

			final Vector3 location = this.location;
			final Quaternion rotation = this.rotation;
			final Quaternion inverseRotation = this.inverseRotation;
			final Matrix4 worldMatrix = this.worldMatrix;
			final Matrix4 projectionMatrix = this.projectionMatrix;
			final Matrix4 worldProjectionMatrix = this.worldProjectionMatrix;
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

			rotation.toMatrix(projectionMatrix.val);
			worldMatrix.translate(vectorHeap.set(location).scl(-1));
			inverseRotation.set(rotation).conjugate();

			// World projection matrix
			// World space -> NDC space
			worldProjectionMatrix.set(projectionMatrix).mul(worldMatrix);

			// Recalculate the camera's frustum planes
			RenderMathUtils.unpackPlanes(this.planes, worldProjectionMatrix);

			// Inverse world matrix
			// Camera space -> world space
			this.inverseWorldMatrix.set(worldMatrix).inv();

			this.directionX.set(RenderMathUtils.VEC3_UNIT_X);
			inverseRotation.transform(this.directionX);
			this.directionY.set(RenderMathUtils.VEC3_UNIT_Y);
			inverseRotation.transform(this.directionY);
			this.directionZ.set(RenderMathUtils.VEC3_UNIT_Z);
			inverseRotation.transform(this.directionZ);

			// Inverse world projection matrix
			// NDC space -> World space
			this.inverseWorldProjectionMatrix.set(worldProjectionMatrix);
			this.inverseWorldProjectionMatrix.inv();

			for (int i = 0; i < 7; i++) {
				billboardedVectors[i].set(vectors[i]);
				inverseRotation.transform(billboardedVectors[i]);
			}
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
		return out.set(v).prj(this.inverseWorldMatrix);
	}

	public Vector3 worldToCamera(final Vector3 out, final Vector3 v) {
		return out.set(v).prj(this.worldMatrix);
	}

	public float[] screenToWorldRay(final float[] out, final Vector2 v) {
		final Vector3 a = vectorHeap;
		final Vector3 b = vectorHeap2;
		final Vector3 c = vectorHeap3;
		final float x = v.x;
		final float y = v.y;
		final Rectangle viewport = this.rect;

		// Intersection on the near-plane
		RenderMathUtils.unproject(a, c.set(x, y, 0), this.inverseWorldProjectionMatrix, viewport);

		// Intersection on the far-plane
		RenderMathUtils.unproject(b, c.set(x, y, 1), this.inverseWorldProjectionMatrix, viewport);

		out[0] = a.x;
		out[1] = a.y;
		out[2] = a.z;
		out[3] = b.x;
		out[4] = b.y;
		out[5] = b.z;

		return out;
	}
}
