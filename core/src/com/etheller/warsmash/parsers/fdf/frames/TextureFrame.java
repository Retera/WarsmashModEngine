package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TextureFrame extends AbstractRenderableFrame {
	private final TextureRegion texture;

	public TextureFrame(final String name, final UIFrame parent, final TextureRegion texture) {
		super(name, parent);
		this.texture = texture;
	}

	@Override
	public void render(final SpriteBatch batch) {
		batch.draw(this.texture, this.renderBounds.x, this.renderBounds.y, this.renderBounds.width,
				this.renderBounds.height);
	}

	@Override
	protected void innerPositionBounds(final Viewport viewport) {
	}

}
