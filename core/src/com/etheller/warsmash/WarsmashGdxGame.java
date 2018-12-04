package com.etheller.warsmash;

import java.nio.charset.Charset;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.etheller.warsmash.util.War3ID;

public class WarsmashGdxGame extends ApplicationAdapter {

	@Override
	public void create() {
		final War3ID id = War3ID.fromString("ipea");
		System.out.println(id.getValue());
		for (final byte b : "Hello World".getBytes(Charset.forName("utf-8"))) {
			System.out.println(b + " - " + (char) b);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void dispose() {
	}
}
