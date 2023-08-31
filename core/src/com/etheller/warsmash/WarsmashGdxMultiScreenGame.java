package com.etheller.warsmash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.etheller.warsmash.viewer5.CanvasProvider;

import java.util.function.Consumer;

public class WarsmashGdxMultiScreenGame extends Game implements CanvasProvider {
	private Consumer<WarsmashGdxMultiScreenGame> onCreate;

	public WarsmashGdxMultiScreenGame(Consumer<WarsmashGdxMultiScreenGame> onCreate) {
		this.onCreate = onCreate;
	}

	@Override
	public void create() {
		onCreate.accept(this);
	}

	@Override
	public float getWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getHeight() {
		return Gdx.graphics.getHeight();
	}

}
