package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public class CUnitDeathReplacementResult {
	private boolean reviving;
	private boolean reincarnating;

	public CUnitDeathReplacementResult() {
		this.reviving = false;
		this.reincarnating = false;
	}

	public boolean isReviving() {
		return reviving;
	}

	public void setReviving(boolean reviving) {
		this.reviving = reviving;
	}

	public boolean isReincarnating() {
		return reincarnating;
	}

	public void setReincarnating(boolean reincarnating) {
		this.reincarnating = reincarnating;
	}



}
