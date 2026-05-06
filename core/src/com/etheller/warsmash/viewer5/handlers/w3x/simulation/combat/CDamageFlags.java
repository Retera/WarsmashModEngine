package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public interface CDamageFlags {

	boolean isAttack();

	void setAttack(boolean attack);

	boolean isRanged();

	void setRanged(boolean ranged);

	boolean isIgnoreInvulnerable();

	void setIgnoreInvulnerable(boolean ignoreInvulnerable);

	boolean isExplode();

	void setExplode(boolean explode);

	boolean isOnlyDamageSummons();

	void setOnlyDamageSummons(boolean callback);

	boolean isNonlethal();

	void setNonlethal(boolean callback);

	boolean isPassLimitedMagicImmune();

	void setPassLimitedMagicImmune(boolean limitedMagicImmune);

	CDamageFlags copy();

}
