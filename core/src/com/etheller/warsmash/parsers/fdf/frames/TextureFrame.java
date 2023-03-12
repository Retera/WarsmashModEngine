package com.etheller.warsmash.parsers.fdf.frames;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.lua.FourArgFunction;

public class TextureFrame extends AbstractRenderableFrame {
	public static final Vector4Definition DEFAULT_TEX_COORDS = new Vector4Definition(0, 1, 0, 1);
	private TextureRegion texture;
	private final boolean decorateFileNames;
	private final Vector4Definition texCoord;
	private Color color;

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
		if (this.color != null) {
			batch.setColor(this.color);
		}
		batch.draw(this.texture, this.renderBounds.x, this.renderBounds.y, this.renderBounds.width,
				this.renderBounds.height);
		if (this.color != null) {
			batch.setColor(1f, 1f, 1f, 1f);
		}
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public void setColor(final float r, final float g, final float b, final float a) {
		if (this.color == null) {
			this.color = new Color();
		}
		this.color.r = r;
		this.color.g = g;
		this.color.b = b;
		this.color.a = a;

	}

	public void setTexture(String file, final GameUI gameUI) {
		if (this.decorateFileNames) {
			file = gameUI.trySkinField(file);
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

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("SetTexture", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				final String text = arg.checkjstring();
				setTexture(text, luaEnvironment.getRootFrame());
				return LuaValue.NIL;
			}
		});
		table.set("SetVertexColor", new FourArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg, final LuaValue arg2,
					final LuaValue arg3) {
				setColor(arg.tofloat(), arg2.tofloat(), arg3.tofloat(),
						TextureFrame.this.color == null ? 1.0f : TextureFrame.this.color.a);
				return LuaValue.NIL;
			}
		});
	}
}
