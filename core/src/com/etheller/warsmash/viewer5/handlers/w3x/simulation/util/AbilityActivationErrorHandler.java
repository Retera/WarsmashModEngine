package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class AbilityActivationErrorHandler {
	private final int localPlayerIndex;
	private final String errorString;
	private final UnitSound errorSound;

	public AbilityActivationErrorHandler(final int localPlayerIndex, final String errorString,
			final UnitSound errorSound) {
		this.localPlayerIndex = localPlayerIndex;
		this.errorString = errorString;
		this.errorSound = errorSound;
	}

	public void onClick(final CommandErrorListener commandErrorListener, final AudioContext worldSceneAudioContext,
			final RenderUnit commandedUnit) {
		commandErrorListener.showCommandError(this.localPlayerIndex, this.errorString);
		this.errorSound.playUnitResponse(worldSceneAudioContext, commandedUnit);
	}
}
