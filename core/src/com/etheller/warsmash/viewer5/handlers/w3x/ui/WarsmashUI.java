package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.audio.Music;
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialog;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CScriptDialogButton;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.dialog.CTimerDialog;

public interface WarsmashUI extends CommandErrorListener, WarsmashBaseUI {

	CScriptDialog createScriptDialog(GlobalScope globalScope);

	void clearDialog(CScriptDialog dialog);

	void destroyDialog(CScriptDialog dialog);

	CScriptDialogButton createScriptDialogButton(CScriptDialog dialog, String buttonText, char hotkeyInt);

	GameCameraManager getCameraManager();

	Music playMusic(String musicField, boolean random, int index);

	Scene getUiScene();

	CTimerDialog createTimerDialog(CTimer timer);

	void removedUnit(CUnit whichUnit);

	void removedItem(CItem whichItem);

	void displayTimedText(float x, float y, float duration, String message);

}
