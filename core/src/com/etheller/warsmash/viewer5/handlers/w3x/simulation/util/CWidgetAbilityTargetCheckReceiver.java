package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class CWidgetAbilityTargetCheckReceiver implements AbilityTargetCheckReceiver<CWidget> {
	public static final CWidgetAbilityTargetCheckReceiver INSTANCE = new CWidgetAbilityTargetCheckReceiver();

	private CWidget target;

	public CWidgetAbilityTargetCheckReceiver reset() {
		this.target = null;
		return this;
	}

	@Override
	public void targetOk(final CWidget target) {
		this.target = target;
	}

	@Override
	public void notAnActiveAbility() {
		this.target = null;
	}

	@Override
	public void orderIdNotAccepted() {
		this.target = null;
	}

	@Override
	public void targetCheckFailed(String commandStringErrorKey) {
		this.target = null;
	}

	public CWidget getTarget() {
		return this.target;
	}

}
