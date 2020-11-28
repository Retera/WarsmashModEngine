package com.etheller.warsmash.viewer5;

import com.badlogic.gdx.graphics.Texture;

public interface ViewerTextureRenderable {
	// TODO bind method makes more sense here

	int getGlTarget();

	int getGlHandle();

	class GdxViewerTextureRenderable implements ViewerTextureRenderable {
		private final com.badlogic.gdx.graphics.Texture gdxTexture;

		public GdxViewerTextureRenderable(final Texture texture) {
			this.gdxTexture = texture;
		}

		@Override
		public int getGlTarget() {
			return this.gdxTexture.glTarget;
		}

		@Override
		public int getGlHandle() {
			return this.gdxTexture.getTextureObjectHandle();
		}
	}
}
