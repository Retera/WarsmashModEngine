package com.etheller.warsmash.viewer5.handlers.w3x;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public interface CollidableDoodadComponent {
	boolean intersectRayWithGeosetSlow(final Ray ray, final Vector3 intersection);

	Rectangle getGeosetRotatedBounds();

	boolean intersectRayWithCollision(final Ray ray, final Vector3 intersection, final boolean b, final boolean c);

	boolean isInterior();

	W3xScenePortraitLightManager getModelOnlyLightManager();
}
