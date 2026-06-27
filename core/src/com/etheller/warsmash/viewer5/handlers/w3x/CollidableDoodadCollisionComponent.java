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
	private final boolean interior;
	/** World-space floor light samples for this surface (x,y,z triples) and their colours (r,g,b triples),
	 * parallel arrays. Null until set. Used to light units by the ground they stand on. */
	private float[] floorSampleWorldXYZ;
	private float[] floorSampleRGB;
	/** 1 per floor sample: 1 = exterior vertex (use the dynamic skylight colour), 0 = interior (use rgb). */
	private float[] floorSampleExterior;

	public CollidableDoodadCollisionComponent(final MdxComplexInstance instance,
			final MdlxCollisionGeometry collisionGeometry, final Rectangle geosetRotatedBounds,
			final BoundingBox geosetBoundingBox, final float[] min, final float[] max, final boolean interior) {
		this.instance = instance;
		this.collisionGeometry = collisionGeometry;
		this.geosetRotatedBounds = geosetRotatedBounds;
		this.geosetBoundingBox = geosetBoundingBox;
		this.collision = new IntersectableBox(min, max);
		this.interior = interior;
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
		return this.collision.checkIntersect(ray, this.instance, intersection);
	}

	@Override
	public boolean isInterior() {
		return this.interior;
	}

	@Override
	public W3xSceneLightManager getModelOnlyLightManager() {
		// Serve the "served" manager (for WMO surfaces this includes the WMO MOLT lights) so units walking
		// onto this surface are lit like the doodads on it, not like the surface itself.
		return this.instance.getServedModelOnlyLightManager();
	}

	@Override
	public float[] getServedInteriorAmbient() {
		return this.instance.servedInteriorAmbient;
	}

	/** Provides this surface's floor light samples in WORLD space (x,y,z triples) with parallel colours
	 * (r,g,b triples) and exterior flags (1 per sample). See WmoPortingModel2 floor samples + War3MapViewer
	 * placement. */
	public void setFloorLightSamples(final float[] worldXYZ, final float[] rgb, final float[] exterior) {
		this.floorSampleWorldXYZ = worldXYZ;
		this.floorSampleRGB = rgb;
		this.floorSampleExterior = exterior;
	}

	@Override
	public boolean sampleNearestFloorColor(final float x, final float y, final float z, final float[] exteriorColor,
			final float[] outRgb) {
		final float[] xyz = this.floorSampleWorldXYZ;
		final float[] rgb = this.floorSampleRGB;
		if ((xyz == null) || (xyz.length == 0)) {
			return false;
		}
		int nearest = -1;
		float nearestDistSq = Float.MAX_VALUE;
		for (int i = 0; i < xyz.length; i += 3) {
			final float dx = xyz[i] - x;
			final float dy = xyz[i + 1] - y;
			final float dz = xyz[i + 2] - z;
			final float distSq = (dx * dx) + (dy * dy) + (dz * dz);
			if (distSq < nearestDistSq) {
				nearestDistSq = distSq;
				nearest = i;
			}
		}
		if (nearest < 0) {
			return false;
		}
		final int sampleIndex = nearest / 3;
		final boolean exterior = (this.floorSampleExterior != null) && (sampleIndex < this.floorSampleExterior.length)
				&& (this.floorSampleExterior[sampleIndex] != 0f);
		if (exterior && (exteriorColor != null)) {
			// Exterior floor vertex: its baked MOCV is black; use the dynamic daylight colour instead so a unit
			// at the indoor/outdoor border is lit by the sky, not flashed black.
			outRgb[0] = exteriorColor[0];
			outRgb[1] = exteriorColor[1];
			outRgb[2] = exteriorColor[2];
		}
		else {
			outRgb[0] = rgb[nearest];
			outRgb[1] = rgb[nearest + 1];
			outRgb[2] = rgb[nearest + 2];
		}
		return true;
	}
}
