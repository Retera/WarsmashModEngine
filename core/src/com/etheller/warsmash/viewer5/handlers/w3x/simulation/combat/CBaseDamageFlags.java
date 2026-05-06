package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat;

public abstract class CBaseDamageFlags implements CDamageFlags {
	private static int ATTACK = 0b1;
	private static int RANGED = 0b10;
	private static int IGNOREINVUL = 0b100;
	private static int EXPLODE = 0b1000;
	private static int SUMMON = 0b10000;
	private static int NONLETHAL = 0b100000;
	private static int PASSLIMITEDMAGICIMMUNE = 0b1000000;

	private int flags = 0;

	public CBaseDamageFlags() {
	}

	protected CBaseDamageFlags(CBaseDamageFlags base) {
		this.flags = base.flags;
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

	@Override
	public boolean isOnlyDamageSummons() {
		return (this.flags & SUMMON) != 0;
	}

	@Override
	public void setOnlyDamageSummons(boolean summon) {
		this.flags = summon ? this.flags | SUMMON : this.flags & ~SUMMON;
	}

	@Override
	public boolean isNonlethal() {
		return (this.flags & NONLETHAL) != 0;
	}

	@Override
	public void setNonlethal(boolean nonlethal) {
		this.flags = nonlethal ? this.flags | NONLETHAL : this.flags & ~NONLETHAL;
	}

	@Override
	public boolean isPassLimitedMagicImmune() {
		return (this.flags & PASSLIMITEDMAGICIMMUNE) != 0;
	}

	@Override
	public void setPassLimitedMagicImmune(boolean passLimitedMagicImmune) {
		this.flags = passLimitedMagicImmune ? this.flags | PASSLIMITEDMAGICIMMUNE
				: this.flags & ~PASSLIMITEDMAGICIMMUNE;
	}
}
