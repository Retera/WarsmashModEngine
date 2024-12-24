package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class MeleeUIAbilityActivationReceiver implements AbilityActivationReceiver {
	private final int localPlayerIndex;
	private final UiSoundLookup uiSoundLookup;

	private boolean ok = false;
	private CommandErrorListener commandErrorListener;
	private AudioContext worldSceneAudioContext;
	private RenderUnit commandedUnit;

	public MeleeUIAbilityActivationReceiver(int localPlayerIndex, UiSoundLookup uiSoundLookup) {
		this.localPlayerIndex = localPlayerIndex;
		this.uiSoundLookup = uiSoundLookup;
	}

	public MeleeUIAbilityActivationReceiver reset(final CommandErrorListener commandErrorListener,
			final AudioContext worldSceneAudioContext, final RenderUnit commandedUnit) {
		this.commandErrorListener = commandErrorListener;
		this.worldSceneAudioContext = worldSceneAudioContext;
		this.commandedUnit = commandedUnit;
		this.ok = false;
		return this;
	}

	@Override
	public void useOk() {
		this.ok = true;
	}

	public void onClick(final CommandErrorListener commandErrorListener, final AudioContext worldSceneAudioContext,
						final RenderUnit commandedUnit, String errorString) {
		commandErrorListener.showCommandErrorWithoutSound(this.localPlayerIndex, errorString);
		UnitSound specificErrorSound = uiSoundLookup.getSound(errorString);
		specificErrorSound.playUnitResponse(worldSceneAudioContext, commandedUnit);
	}
	
	@Override
	public void unknownReasonUseNotOk() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void activationCheckFailed(String commandStringErrorKey) {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, commandStringErrorKey);
	}

	@Override
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, CommandStringErrorKeys.SPELL_IS_NOT_READY_YET);
	}

	@Override
	public void notAnActiveAbility() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void missingRequirement(final War3ID type, final int level) {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void missingHeroLevelRequirement(final int level) {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void noHeroSkillPointsAvailable() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void techtreeMaximumReached() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void techItemAlreadyInProgress() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void disabled() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}

	@Override
	public void noChargesRemaining() {
		onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit, "");
	}


	public boolean isUseOk() {
		return this.ok;
	}

}
