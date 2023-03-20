package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.etheller.warsmash.viewer5.handlers.mdx.Geoset;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;

public class CollidableDoodadComponent {
	private final MdxComplexInstance instance;
	private final Geoset geoset;
	private final Rectangle geosetRotatedBounds;

	public CollidableDoodadComponent(final MdxComplexInstance instance, final Geoset geoset,
			final Rectangle geosetRotatedBounds) {
		this.instance = instance;
		this.geoset = geoset;
		this.geosetRotatedBounds = geosetRotatedBounds;
	}

	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	public Geoset getGeoset() {
		return this.geoset;
	}

	public boolean intersectRayWithGeosetSlow(final Ray ray, final Vector3 intersection) {
		return this.instance.intersectRayWithGeosetSlow(ray, this.geoset, intersection);
	}

	public Rectangle getGeosetRotatedBounds() {
		return this.geosetRotatedBounds;
	}
}
