package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

import java.util.Map;

public class AbilityActivationErrorHandler {
	private final int localPlayerIndex;
	private final Map<String, UnitSound> errorStringToSound;
	private final UnitSound errorSound;

	public AbilityActivationErrorHandler(final int localPlayerIndex, final String errorString, Map<String, UnitSound> errorStringToSound, final UnitSound errorSound) {
		this.localPlayerIndex = localPlayerIndex;
		this.errorStringToSound = errorStringToSound;
		this.errorSound = errorSound;
	}

	public void onClick(final CommandErrorListener commandErrorListener, final AudioContext worldSceneAudioContext,
						final RenderUnit commandedUnit, String errorString) {
		commandErrorListener.showCommandErrorWithoutSound(this.localPlayerIndex, errorString);
		UnitSound specificErrorSound = errorStringToSound.get(errorString);
		if (specificErrorSound == null) {
			specificErrorSound = this.errorSound;
		}
		specificErrorSound.playUnitResponse(worldSceneAudioContext, commandedUnit);
	}
}
