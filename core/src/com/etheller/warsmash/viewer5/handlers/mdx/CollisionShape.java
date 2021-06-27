package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.Bounds;
import com.etheller.warsmash.viewer5.GenericNode;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCollisionShape;

public class CollisionShape extends GenericObject {
	private static Vector3 intersectHeap = new Vector3();
	private static Vector3 intersectHeap2 = new Vector3();
	private static Matrix4 intersectMatrixHeap = new Matrix4();
	private static Ray intersectRayHeap = new Ray();
	private Intersectable intersectable;

	public CollisionShape(final MdxModel model, final MdlxCollisionShape object, final int index) {
		super(model, object, index);
		final float[][] vertices = object.getVertices();

		switch (object.getType()) {
		case BOX:
			this.intersectable = new IntersectableBox(vertices[0], vertices[1]);
			break;
		case CYLINDER:
			this.intersectable = null; // TODO
			break;
		case PLANE:
			this.intersectable = null; // TODO
			break;
		case SPHERE:
			this.intersectable = new IntersectableSphere(vertices[0], object.getBoundsRadius());
			break;
		}
	}

	public boolean checkIntersect(final Ray ray, final MdxNode mdxNode, final Vector3 intersection) {
		if (this.intersectable != null) {
			return this.intersectable.checkIntersect(ray, mdxNode, intersection);
		}
		return false;
	}

	private static interface Intersectable {
		boolean checkIntersect(final Ray ray, final MdxNode mdxNode, final Vector3 intersection);
	}

	private static final class IntersectableBox implements Intersectable {
		private final BoundingBox boundingBox;

		public IntersectableBox(final float[] vertex1, final float[] vertex2) {
			this.boundingBox = new BoundingBox(new Vector3(vertex1), new Vector3(vertex2));
		}

		@Override
		public boolean checkIntersect(final Ray ray, final MdxNode mdxNode, final Vector3 intersection) {
			intersectMatrixHeap.set(mdxNode.worldMatrix);
			Matrix4.inv(intersectMatrixHeap.val);
			intersectHeap.set(ray.origin);
			intersectHeap2.set(ray.direction);
			intersectHeap2.add(ray.origin);
			intersectHeap.prj(intersectMatrixHeap);
			intersectHeap2.prj(intersectMatrixHeap);
			intersectHeap2.sub(intersectHeap);
			intersectRayHeap.set(intersectHeap, intersectHeap2);
			if (Intersector.intersectRayBounds(intersectRayHeap, this.boundingBox, intersection)) {
				intersection.prj(mdxNode.worldMatrix);
				return true;
			}
			return false;
		}
	}

	private static final class IntersectableSphere implements Intersectable {
		private final Vector3 center;
		private final float radius;

		public IntersectableSphere(final float[] center, final float radius) {
			this.center = new Vector3(center);
			this.radius = radius;
		}

		@Override
		public boolean checkIntersect(final Ray ray, final MdxNode mdxNode, final Vector3 intersection) {
			intersectHeap.set(this.center);
			intersectHeap.prj(mdxNode.worldMatrix);
			if (Intersector.intersectRaySphere(ray, intersectHeap, this.radius, intersection)) {
				return true;
			}
			return false;
		}
	}

	public static boolean intersectRayTriangles(final Ray ray, final GenericNode mdxNode, final float[] vertices,
			final int[] indices, final int vertexSize, final Vector3 intersection) {
		intersectMatrixHeap.set(mdxNode.worldMatrix);
		Matrix4.inv(intersectMatrixHeap.val);
		intersectHeap.set(ray.origin);
		intersectHeap2.set(ray.direction);
		intersectHeap2.add(ray.origin);
		intersectHeap.prj(intersectMatrixHeap);
		intersectHeap2.prj(intersectMatrixHeap);
		intersectHeap2.sub(intersectHeap);
		intersectRayHeap.set(intersectHeap, intersectHeap2);
		if (RenderMathUtils.intersectRayTriangles(intersectRayHeap, vertices, indices, vertexSize, intersection)) {
			intersection.prj(mdxNode.worldMatrix);
			return true;
		}
		return false;
	}

	public static boolean intersectRayBounds(final Bounds bounds, final Matrix4 worldMatrix, final Ray ray,
			final Vector3 intersection) {
		intersectMatrixHeap.set(worldMatrix);
		Matrix4.inv(intersectMatrixHeap.val);
		intersectHeap.set(ray.origin);
		intersectHeap2.set(ray.direction);
		intersectHeap2.add(ray.origin);
		intersectHeap.prj(intersectMatrixHeap);
		intersectHeap2.prj(intersectMatrixHeap);
		intersectHeap2.sub(intersectHeap);
		intersectRayHeap.set(intersectHeap, intersectHeap2);
		if (bounds.intersectRay(intersectRayHeap, intersection)) {
			intersection.prj(worldMatrix);
			return true;
		}
		return false;
	}
}
