package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

public class BuffUI {
	private final IconUI onIconUI;
	private final List<EffectAttachmentUI> targetArt;
	private final List<EffectAttachmentUI> specialArt;
	private final List<EffectAttachmentUI> effectArt;
	private final List<EffectAttachmentUI> missileArt;
	private final String effectSound;
	private final String effectSoundLooped;

	public BuffUI(final IconUI onIconUI, final List<EffectAttachmentUI> targetArt,
			final List<EffectAttachmentUI> specialArt, final List<EffectAttachmentUI> effectArt,
			final List<EffectAttachmentUI> missileArt, final String effectSound, final String effectSoundLooped) {
		this.onIconUI = onIconUI;
		this.targetArt = targetArt;
		this.specialArt = specialArt;
		this.effectArt = effectArt;
		this.missileArt = missileArt;
		this.effectSound = effectSound;
		this.effectSoundLooped = effectSoundLooped;
	}

	public IconUI getOnIconUI() {
		return this.onIconUI;
	}

	public EffectAttachmentUI getTargetArt(final int index) {
		return AbilityUI.tryGet(this.targetArt, index);
	}

	public EffectAttachmentUI getSpecialArt(final int index) {
		return AbilityUI.tryGet(this.specialArt, index);
	}

	public EffectAttachmentUI getEffectArt(final int index) {
		return AbilityUI.tryGet(this.effectArt, index);
	}

	public EffectAttachmentUI getMissileArt(final int index) {
		return AbilityUI.tryGet(this.missileArt, index);
	}

	public String getEffectSound() {
		return this.effectSound;
	}

	public String getEffectSoundLooped() {
		return this.effectSoundLooped;
	}

	public List<EffectAttachmentUI> getTargetArt() {
		return targetArt;
	}

	public List<EffectAttachmentUI> getSpecialArt() {
		return specialArt;
	}

	public List<EffectAttachmentUI> getEffectArt() {
		return effectArt;
	}

	public List<EffectAttachmentUI> getMissileArt() {
		return missileArt;
	}

}
