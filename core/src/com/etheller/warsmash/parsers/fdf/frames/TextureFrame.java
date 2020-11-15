package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class TextureFrame extends AbstractRenderableFrame {
	private TextureRegion texture;
	private final boolean decorateFileNames;
	private final Vector4Definition texCoord;

	public TextureFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final Vector4Definition texCoord) {
		super(name, parent);
		this.decorateFileNames = decorateFileNames;
		this.texCoord = texCoord;
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.texture == null) {
			return;
		}
		batch.draw(this.texture, this.renderBounds.x, this.renderBounds.y, this.renderBounds.width,
				this.renderBounds.height);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
	}

	public void setTexture(String file, final GameUI gameUI) {
		if (this.decorateFileNames) {
			file = gameUI.getSkinField(file);
		}
		final Texture texture = gameUI.loadTexture(file);
		if (texture != null) {
			setTexture(texture);
		}
	}

	public void setTexCoord(final float x, final float y, final float z, final float w) {
		this.texCoord.set(x, y, z, w);
		if (this.texture != null) {
			this.texture.setRegion(this.texCoord.getX(), this.texCoord.getZ(), this.texCoord.getY(),
					this.texCoord.getW());
		}
	}

	public void setTexture(final Texture texture) {
		if (texture == null) {
			this.texture = null;
			return;
		}
		final TextureRegion texRegion;
		if (this.texCoord != null) {
			texRegion = new TextureRegion(texture, this.texCoord.getX(), this.texCoord.getZ(), this.texCoord.getY(),
					this.texCoord.getW());
		}
		else {
			texRegion = new TextureRegion(texture);
		}
		this.texture = texRegion;
	}

	public void setTexture(final TextureRegion texture) {
		this.texture = texture;
	}
}
