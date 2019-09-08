package com.etheller.warsmash;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.wc3.mpq.Codebase;
import com.hiveworkshop.wc3.mpq.FileCodebase;

public class WarsmashGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private BitmapFont font;
	private Codebase codebase;
	private Texture texture;

	@Override
	public void create() {
		this.codebase = new FileCodebase(new File("C:/MPQBuild/War3.mpq/war3.mpq"));

		final War3ID id = War3ID.fromString("ipea");
		try {
			final String path = "terrainart\\lordaeronsummer\\lords_dirt.blp";
			final boolean has = this.codebase.has(path);
			final BufferedImage img = ImageIO.read(this.codebase.getResourceAsStream(path));
			this.texture = ImageUtils.getTexture(ImageUtils.forceBufferedImagesRGB(img));
			this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		final int srcFunc = this.batch.getBlendSrcFunc();
		final int dstFunc = this.batch.getBlendDstFunc();

		this.batch.enableBlending();
		this.batch.begin();
//		this.font.draw(this.batch, "Hello World", 100, 100);
		this.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.batch.draw(this.texture, 0, 0);
		this.batch.end();
		this.batch.setBlendFunction(srcFunc, dstFunc);
	}

	@Override
	public void dispose() {
	}
}
