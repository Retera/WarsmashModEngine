package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.math.Vector3;

public class ModelInstance {
	public Model model;
	public TextureMapper textureMapper;

	public int left = -1;
	public int right = -1;
	public int bottom = -1;
	public int top = -1;
	public int plane = -1;
	public int depth = 0;

	public Vector3 worldLocation;
	public Vector3 worldScale;
	public Scene scene;
	public boolean rendered;
	public int cullFrame;
	public int updateFrame;

	public boolean isVisible(final Camera camera) {
		// TODO Auto-generated method stub
		return false;
	}

	public void update(final float dt, final Scene scene) {
		// TODO Auto-generated method stub

	}

	public boolean isBatched() {
		// TODO Auto-generated method stub
		return false;
	}

	public void renderOpaque() {
		// TODO Auto-generated method stub

	}

	public void renderTranslucent() {
		// TODO Auto-generated method stub

	}
}
