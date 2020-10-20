package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

public class AbilityIconUI {
	private final IconUI learnIconUI;
	private final IconUI onIconUI;
	private final IconUI offIconUI;

	public AbilityIconUI(final IconUI learnIconUI, final IconUI onIconUI, final IconUI offIconUI) {
		this.learnIconUI = learnIconUI;
		this.onIconUI = onIconUI;
		this.offIconUI = offIconUI;
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
}
