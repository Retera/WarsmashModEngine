package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public interface CollidableDoodadComponent {
	boolean intersectRayWithGeosetSlow(final Ray ray, final Vector3 intersection);

	Rectangle getGeosetRotatedBounds();

	boolean intersectRayWithCollision(final Ray ray, final Vector3 intersection, final boolean b, final boolean c);

	boolean isInterior();

	W3xSceneLightManager getModelOnlyLightManager();

	int getLightOmitOffsetOverride();

	/**
	 * Flat interior ambient (RGB 0..1) to apply to a unit standing on this surface,
	 * or null if this surface provides none (non-interior). See RenderUnit.
	 */
	float[] getServedInteriorAmbient();

	/**
	 * Samples the baked floor light colour nearest to world point (x,y,z) into
	 * outRgb (length 3); returns false if this surface has no floor light samples
	 * (outRgb untouched). If the nearest floor vertex is an EXTERIOR vertex (MOCV
	 * alpha 0) and exteriorColor is non-null, exteriorColor is written instead of
	 * the black baked colour, so a unit at the indoor/outdoor border picks up
	 * daylight rather than flashing black. Used to light units by the ground they
	 * stand on. See RenderUnit.
	 */
	boolean sampleNearestFloorColor(float x, float y, float z, float[] exteriorColor, float[] outRgb);
}
