package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement;

public class CUnitPriorityLoopData {
	private boolean endLoop = false;
	private boolean endLevel = false;
	
	public void startLoop(int i) {
		this.endLevel = false;
	}
	
	public void preventOtherModificationsWithSamePriority() {
		this.endLevel = true;
	}
	
	public void preventAllOtherModifications() {
		this.endLevel = true;
		this.endLoop = true;
	}

	public boolean skipCurrentLevel() {
		return this.endLevel;
	}

	public boolean end() {
		return this.endLoop;
	}

	public void reset() {
		this.endLevel = false;
		this.endLoop = false;
	}

}
