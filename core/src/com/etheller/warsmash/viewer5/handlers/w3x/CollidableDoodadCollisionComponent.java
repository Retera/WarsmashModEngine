package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.viewer5.handlers.mdx.CollisionShape.IntersectableBox;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.hiveworkshop.rms.parsers.mdlx.MdlxCollisionGeometry;

public class CollidableDoodadCollisionComponent implements CollidableDoodadComponent {
	private final MdxComplexInstance instance;
	private final MdlxCollisionGeometry collisionGeometry;
	private final Rectangle geosetRotatedBounds;
	private final BoundingBox geosetBoundingBox;
	private final IntersectableBox collision;

	public CollidableDoodadCollisionComponent(final MdxComplexInstance instance,
			final MdlxCollisionGeometry collisionGeometry, final Rectangle geosetRotatedBounds,
			final BoundingBox geosetBoundingBox, final float[] min, final float[] max) {
		this.instance = instance;
		this.collisionGeometry = collisionGeometry;
		this.geosetRotatedBounds = geosetRotatedBounds;
		this.geosetBoundingBox = geosetBoundingBox;
		this.collision = new IntersectableBox(min, max);
	}

	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	@Override
	public boolean intersectRayWithGeosetSlow(final Ray ray, final Vector3 intersection) {
		return this.instance.intersectRayWithCollisionGeometrySlow(ray, this.collisionGeometry, intersection);
	}

	@Override
	public Rectangle getGeosetRotatedBounds() {
		return this.geosetRotatedBounds;
	}

	@Override
	public boolean intersectRayWithCollision(final Ray ray, final Vector3 intersection, final boolean b,
			final boolean c) {
		return this.collision.checkIntersect(ray, this.instance.nodes[0], intersection);
	}
}
