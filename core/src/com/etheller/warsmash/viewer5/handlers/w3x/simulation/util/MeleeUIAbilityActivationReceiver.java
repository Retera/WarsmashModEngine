package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class MeleeUIAbilityActivationReceiver implements AbilityActivationReceiver {
	private final AbilityActivationErrorHandler noGoldError;
	private final AbilityActivationErrorHandler noLumberError;
	private final AbilityActivationErrorHandler noFoodError;
	private final AbilityActivationErrorHandler noManaError;
	private final AbilityActivationErrorHandler genericError;
	private final AbilityActivationErrorHandler cooldownError;

	private boolean ok = false;
	private CommandErrorListener commandErrorListener;
	private AudioContext worldSceneAudioContext;
	private RenderUnit commandedUnit;

	public MeleeUIAbilityActivationReceiver(final AbilityActivationErrorHandler noGoldError,
			final AbilityActivationErrorHandler noLumberError, final AbilityActivationErrorHandler noFoodError,
			final AbilityActivationErrorHandler noManaError, final AbilityActivationErrorHandler genericError,
			final AbilityActivationErrorHandler cooldownError) {
		this.noGoldError = noGoldError;
		this.noLumberError = noLumberError;
		this.noFoodError = noFoodError;
		this.noManaError = noManaError;
		this.genericError = genericError;
		this.cooldownError = cooldownError;
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

	@Override
	public void notEnoughResources(final ResourceType resource) {
		switch (resource) {
		case GOLD:
			this.noGoldError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
			break;
		case LUMBER:
			this.noLumberError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
			break;
		case FOOD:
			this.noFoodError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
			break;
		case MANA:
			this.noManaError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
			break;
		}
	}

	@Override
	public void cooldownNotYetReady(final float cooldownRemaining, final float cooldown) {
		this.cooldownError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void notAnActiveAbility() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void missingRequirement(final War3ID type, final int level) {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void missingHeroLevelRequirement(final int level) {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void noHeroSkillPointsAvailable() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void techtreeMaximumReached() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void casterMovementDisabled() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void cargoCapacityUnavailable() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	@Override
	public void disabled() {
		this.genericError.onClick(this.commandErrorListener, this.worldSceneAudioContext, this.commandedUnit);
	}

	public boolean isUseOk() {
		return this.ok;
	}

}
