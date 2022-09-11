package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

public class AbilityUI {
	private final IconUI learnIconUI;
	private final IconUI onIconUI;
	private final IconUI offIconUI;
	private final List<EffectAttachmentUI> casterArt;
	private final List<EffectAttachmentUI> targetArt;
	private final List<EffectAttachmentUI> specialArt;
	private final List<EffectAttachmentUI> effectArt;
	private final List<EffectAttachmentUI> areaEffectArt;
	private final List<EffectAttachmentUI> missileArt;
	private final String effectSound;
	private final String effectSoundLooped;

	public AbilityUI(final IconUI learnIconUI, final IconUI onIconUI, final IconUI offIconUI,
			final List<EffectAttachmentUI> casterArt, final List<EffectAttachmentUI> targetArt,
			final List<EffectAttachmentUI> specialArt, final List<EffectAttachmentUI> effectArt,
			final List<EffectAttachmentUI> areaEffectArt, final List<EffectAttachmentUI> missileArt,
			final String effectSound, final String effectSoundLooped) {
		this.learnIconUI = learnIconUI;
		this.onIconUI = onIconUI;
		this.offIconUI = offIconUI;
		this.casterArt = casterArt;
		this.targetArt = targetArt;
		this.specialArt = specialArt;
		this.effectArt = effectArt;
		this.areaEffectArt = areaEffectArt;
		this.missileArt = missileArt;
		this.effectSound = effectSound;
		this.effectSoundLooped = effectSoundLooped;
	}

	public IconUI getLearnIconUI() {
		return this.learnIconUI;
	}

	public IconUI getOnIconUI() {
		return this.onIconUI;
	}

	public IconUI getOffIconUI() {
		return this.offIconUI;
	}

	public EffectAttachmentUI getCasterArt(final int index) {
		return tryGet(this.casterArt, index);
	}

	public EffectAttachmentUI getTargetArt(final int index) {
		return tryGet(this.targetArt, index);
	}

	public EffectAttachmentUI getSpecialArt(final int index) {
		return tryGet(this.specialArt, index);
	}

	public EffectAttachmentUI getEffectArt(final int index) {
		return tryGet(this.effectArt, index);
	}

	public EffectAttachmentUI getAreaEffectArt(final int index) {
		return tryGet(this.areaEffectArt, index);
	}

	public EffectAttachmentUI getMissileArt(final int index) {
		return tryGet(this.missileArt, index);
	}

	public String getEffectSound() {
		return this.effectSound;
	}

	public String getEffectSoundLooped() {
		return this.effectSoundLooped;
	}

	protected static EffectAttachmentUI tryGet(final List<EffectAttachmentUI> items, final int index) {
		if (index < items.size()) {
			return items.get(index);
		}
		return items.get(items.size() - 1);
	}
}
