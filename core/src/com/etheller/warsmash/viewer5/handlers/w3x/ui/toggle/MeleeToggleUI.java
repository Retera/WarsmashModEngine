package com.etheller.warsmash.viewer5.handlers.w3x.ui.toggle;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerColor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.MeleeUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashBaseUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashToggleableUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.WarsmashUI;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CTimerDialog;

public class MeleeToggleUI implements WarsmashUI {
	private final MeleeUI meleeUI;
	private WarsmashToggleableUI currentUI;
	private final List<WarsmashToggleableUI> allUIs;
	private int lastWidth;
	private int lastHeight;

	public MeleeToggleUI(final MeleeUI meleeUI, final List<WarsmashToggleableUI> allUIs) {
		this.meleeUI = meleeUI;
		this.allUIs = allUIs;
		this.currentUI = allUIs.get(0);
	}

	public void setCurrentUI(final int index) {
		if (this.currentUI != null) {
			this.currentUI.onHide();
		}
		this.currentUI = this.allUIs.get(index);
		this.currentUI.resize(this.lastWidth, this.lastHeight);
		this.currentUI.onShow();
	}

	@Override
	public void showInterfaceError(final int playerIndex, final String message) {
		this.meleeUI.showInterfaceError(playerIndex, message);
	}

	@Override
	public void showCommandErrorWithoutSound(final int playerIndex, final String message) {
		this.meleeUI.showCommandErrorWithoutSound(playerIndex, message);
	}

	@Override
	public void showUpgradeCompleteAlert(final int playerIndex, final War3ID queuedRawcode, final int level) {
		this.meleeUI.showUpgradeCompleteAlert(playerIndex, queuedRawcode, level);
	}

	@Override
	public CScriptDialog createScriptDialog(final GlobalScope globalScope) {
		return this.meleeUI.createScriptDialog(globalScope);
	}

	@Override
	public void clearDialog(final CScriptDialog dialog) {
		this.meleeUI.clearDialog(dialog);
	}

	@Override
	public void destroyDialog(final CScriptDialog dialog) {
		this.meleeUI.destroyDialog(dialog);
	}

	@Override
	public CScriptDialogButton createScriptDialogButton(final CScriptDialog dialog, final String buttonText,
			final char hotkeyInt) {
		return this.meleeUI.createScriptDialogButton(dialog, buttonText, hotkeyInt);
	}

	@Override
	public GameCameraManager getCameraManager() {
		return this.meleeUI.getCameraManager();
	}

	@Override
	public void main() {
		for (final WarsmashBaseUI ui : this.allUIs) {
			ui.main();
		}
	}

	@Override
	public void update(final float deltaTime) {
		for (final WarsmashBaseUI ui : this.allUIs) {
			ui.update(deltaTime);
		}
	}

	@Override
	public void render(final SpriteBatch batch, final GlyphLayout glyphLayout) {
		this.currentUI.render(batch, glyphLayout);
	}

	@Override
	public void dispose() {
		for (final WarsmashBaseUI ui : this.allUIs) {
			ui.dispose();
		}
	}

	@Override
	public void resize(final int width, final int height) {
		this.lastWidth = width;
		this.lastHeight = height;
		this.currentUI.resize(width, height);
//		for (final WarsmashBaseUI ui : this.allUIs) {
//			ui.resize(width, height);
//		}
	}

	@Override
	public boolean keyDown(final int keycode) {
		if ((keycode == Input.Keys.Z)
				&& (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT))) {
			setCurrentUI((this.allUIs.indexOf(this.currentUI) + 1) % this.allUIs.size());
			return true;
		}
		return this.currentUI.keyDown(keycode);
	}

	@Override
	public boolean keyUp(final int keycode) {
		return this.currentUI.keyUp(keycode);
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		return this.currentUI.touchDown(screenX, screenY, worldScreenY, button);
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		return this.currentUI.touchUp(screenX, screenY, worldScreenY, button);
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		return this.currentUI.touchDragged(screenX, screenY, worldScreenY, pointer);
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		return this.currentUI.mouseMoved(screenX, screenY, worldScreenY);
	}

	@Override
	public boolean scrolled(final float amountX, final float amountY) {
		return this.currentUI.scrolled(amountX, amountY);
	}

	@Override
	public void gameClosed() {
		for (final WarsmashBaseUI ui : this.allUIs) {
			ui.gameClosed();
		}
	}

	@Override
	public Music playMusic(final String musicField, final boolean random, final int index) {
		return this.meleeUI.playMusic(musicField, random, index);
	}

	@Override
	public Music setMapMusic(String musicField, boolean random, int index) {
		return this.meleeUI.setMapMusic(musicField, random, index);
	}

	@Override
	public void playMapMusic() {
		this.meleeUI.playMapMusic();
	}

	@Override
	public Music playMusicEx(String musicField, boolean random, int index, int fromMSecs, int fadeInMSecs) {
		return this.meleeUI.playMusicEx(musicField, random, index, fromMSecs, fadeInMSecs);
	}

	@Override
	public void stopMusic(boolean fadeOut) {
		this.meleeUI.stopMusic(fadeOut);
	}

	@Override
	public void resumeMusic() {
		this.meleeUI.resumeMusic();
	}

	@Override
	public void setMusicVolume(int volume) {
		this.meleeUI.setMusicVolume(volume);
	}

	@Override
	public void setMusicPlayPosition(int millisecs) {
		this.meleeUI.setMusicPlayPosition(millisecs);
	}

	@Override
	public Scene getUiScene() {
		return this.meleeUI.getUiScene();
	}

	@Override
	public CTimerDialog createTimerDialog(final CTimer timer) {
		return this.meleeUI.createTimerDialog(timer);
	}

	@Override
	public void removedUnit(final CUnit whichUnit) {
		this.meleeUI.removedUnit(whichUnit);
	}

	@Override
	public void removedItem(final CItem whichItem) {
		this.meleeUI.removedItem(whichItem);
	}

	@Override
	public void displayTimedText(final float x, final float y, final float duration, final String message) {
		this.meleeUI.displayTimedText(x, y, duration, message);
	}

	@Override
	public void clearTextMessages() {
		this.meleeUI.clearTextMessages();
	}

	@Override
	public void showInterface(boolean show, float fadeDuration) {
		this.meleeUI.showInterface(show, fadeDuration);
	}

	@Override
	public void setCinematicScene(int portraitUnitId, CPlayerColor color, String speakerTitle, String text,
			float sceneDuration, float voiceoverDuration) {
		this.meleeUI.setCinematicScene(portraitUnitId, color, speakerTitle, text, sceneDuration, voiceoverDuration);
	}

	@Override
	public void enableUserControl(boolean value) {
		this.meleeUI.enableUserControl(value);
	}

	@Override
	public void endCinematicScene() {
		this.meleeUI.endCinematicScene();
	}

	@Override
	public void forceCinematicSubtitles(boolean value) {
		this.meleeUI.forceCinematicSubtitles(value);
	}
}
