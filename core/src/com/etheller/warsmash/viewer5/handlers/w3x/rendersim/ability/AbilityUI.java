package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

public class AbilityUI {
	private final IconUI learnIconUI;
	private final IconUI onIconUI;
	private final IconUI offIconUI;
	private final List<String> casterArt;
	private final List<String> targetArt;
	private final List<String> specialArt;
	private final List<String> effectArt;
	private final List<String> areaEffectArt;
	private final List<String> missileArt;
	private final String effectSound;
	private final String effectSoundLooped;

	public AbilityUI(final IconUI learnIconUI, final IconUI onIconUI, final IconUI offIconUI,
			final List<String> casterArt, final List<String> targetArt, final List<String> specialArt,
			final List<String> effectArt, final List<String> areaEffectArt, final List<String> missileArt,
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

	public String getCasterArt(final int index) {
		return tryGet(this.casterArt, index);
	}

	public String getTargetArt(final int index) {
		return tryGet(this.targetArt, index);
	}

	public String getSpecialArt(final int index) {
		return tryGet(this.specialArt, index);
	}

	public String getEffectArt(final int index) {
		return tryGet(this.effectArt, index);
	}

	public String getAreaEffectArt(final int index) {
		return tryGet(this.areaEffectArt, index);
	}

	public String getMissileArt(final int index) {
		return tryGet(this.missileArt, index);
	}

	public String getEffectSound() {
		return this.effectSound;
	}

	public String getEffectSoundLooped() {
		return this.effectSoundLooped;
	}

	private static String tryGet(final List<String> items, final int index) {
		if (index < items.size()) {
			return items.get(index);
		}
		return items.get(items.size() - 1);
	}
}
