package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface WarsmashBaseUI {
	void main();

	void update(float deltaTime);

	void render(SpriteBatch batch, GlyphLayout glyphLayout);

	void dispose();

	void resize(int width, int height);

	boolean keyDown(int keycode);

	boolean keyUp(int keycode);

	boolean touchDown(int screenX, int screenY, float worldScreenY, int button);

	boolean touchUp(int screenX, int screenY, float worldScreenY, int button);

	boolean touchDragged(int screenX, int screenY, float worldScreenY, int pointer);

	boolean mouseMoved(int screenX, int screenY, float worldScreenY);

	boolean scrolled(float amountX, float amountY);

	void gameClosed();

}
