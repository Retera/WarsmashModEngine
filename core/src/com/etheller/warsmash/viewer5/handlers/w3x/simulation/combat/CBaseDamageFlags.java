package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public abstract class CBaseDamageFlags implements CDamageFlags {
	private static int ATTACK = 0b1;
	private static int RANGED = 0b10;
	private static int IGNOREINVUL = 0b100;
	private static int EXPLODE = 0b1000;

	private int flags = 0;

	public CBaseDamageFlags() {
	}

	@Override
	public boolean isAttack() {
		return (this.flags & ATTACK) != 0;
	}

	@Override
	public void setAttack(boolean attack) {
		this.flags = attack ? this.flags | ATTACK : this.flags & ~ATTACK;
	}

	@Override
	public boolean isRanged() {
		return (this.flags & RANGED) != 0;
	}

	@Override
	public void setRanged(boolean ranged) {
		this.flags = ranged ? this.flags | RANGED : this.flags & ~RANGED;
	}

	@Override
	public boolean isIgnoreInvulnerable() {
		return (this.flags & IGNOREINVUL) != 0;
	}

	@Override
	public void setIgnoreInvulnerable(boolean ignoreInvulnerable) {
		this.flags = ignoreInvulnerable ? this.flags | IGNOREINVUL : this.flags & ~IGNOREINVUL;
	}

	@Override
	public boolean isExplode() {
		return (this.flags & EXPLODE) != 0;
	}

	@Override
	public void setExplode(boolean explode) {
		this.flags = explode ? this.flags | EXPLODE : this.flags & ~EXPLODE;
	}
}
