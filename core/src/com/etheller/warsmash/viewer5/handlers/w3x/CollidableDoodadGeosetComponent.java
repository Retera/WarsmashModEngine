package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.viewer5.handlers.mdx.CollisionShape.IntersectableBox;
import com.etheller.warsmash.viewer5.handlers.mdx.Geoset;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;

public class CollidableDoodadGeosetComponent implements CollidableDoodadComponent {
	private final MdxComplexInstance instance;
	private final Geoset geoset;
	private final Rectangle geosetRotatedBounds;
	private final BoundingBox geosetBoundingBox;
	private final IntersectableBox collision;
	private final boolean interior;

	public CollidableDoodadGeosetComponent(final MdxComplexInstance instance, final Geoset geoset,
			final Rectangle geosetRotatedBounds, final BoundingBox geosetBoundingBox, final boolean interior) {
		this.instance = instance;
		this.geoset = geoset;
		this.geosetRotatedBounds = geosetRotatedBounds;
		this.geosetBoundingBox = geosetBoundingBox;
		this.interior = interior;
		this.collision = new IntersectableBox(geoset.mdlxGeoset.extent.min, geoset.mdlxGeoset.extent.max);
	}

	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	public Geoset getGeoset() {
		return this.geoset;
	}

	@Override
	public boolean intersectRayWithGeosetSlow(final Ray ray, final Vector3 intersection) {
		return this.instance.intersectRayWithGeosetSlow(ray, this.geoset, intersection);
	}

	@Override
	public Rectangle getGeosetRotatedBounds() {
		return this.geosetRotatedBounds;
	}

	@Override
	public boolean intersectRayWithCollision(final Ray ray, final Vector3 intersection, final boolean b,
			final boolean c) {
		return this.collision.checkIntersect(ray, this.instance, intersection);
	}

	@Override
	public boolean isInterior() {
		return this.interior;
	}
}
