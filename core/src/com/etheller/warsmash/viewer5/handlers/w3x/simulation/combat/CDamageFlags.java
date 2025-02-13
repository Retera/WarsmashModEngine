package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public interface CDamageFlags {

	boolean isAttack();

	boolean isRanged();

	boolean isIgnoreInvulnerable();

	void setAttack(boolean attack);

	void setRanged(boolean ranged);

	void setIgnoreInvulnerable(boolean ignoreInvulnerable);

	boolean isExplode();

	void setExplode(boolean explode);

}
