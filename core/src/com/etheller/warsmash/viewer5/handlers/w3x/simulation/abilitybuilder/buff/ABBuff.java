package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractCBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public abstract class ABBuff extends AbstractCBuff {
	protected static int TIMEDLIFE = 0b1;
	protected static int NEGATIVE = 0b10;
	protected static int DISPELLABLE = 0b100;
	protected static int LEVELED = 0b1000;
	protected static int HERO = 0b10000;
	protected static int PHYSICAL = 0b100000;
	protected static int MAGIC = 0b1000000;
	protected static int AURA = 0b10000000;
	
	protected int flags = DISPELLABLE;

	public ABBuff(int handleId, War3ID code, War3ID alias) {
		super(handleId, code, alias);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}
	
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}
	
	public void setTimedLifeBar(boolean timedLife) {
		this.flags = timedLife ? this.flags | TIMEDLIFE : this.flags & ~TIMEDLIFE;
	}

	@Override
	public boolean isTimedLifeBar() {
		return ((this.flags & TIMEDLIFE) != 0);
	}
	
	public void setPositive(boolean positive) {
		this.flags = positive ? this.flags & ~NEGATIVE : this.flags | NEGATIVE;
	}

	@Override
	public boolean isPositive() {
		return ((this.flags & NEGATIVE) == 0);
	}
	
	public void setLeveled(boolean leveled) {
		this.flags = leveled ? this.flags | LEVELED : this.flags & ~LEVELED;
	}

	@Override
	public boolean isLeveled() {
		return  ((this.flags & LEVELED) != 0);
	}
	
	public void setDispellable(boolean dispellable) {
		this.flags = dispellable ? this.flags | DISPELLABLE : this.flags & ~DISPELLABLE;
	}

	@Override
	public boolean isDispellable() {
		return  ((this.flags & DISPELLABLE) != 0);
	}
	
	public void setHero(boolean hero) {
		this.flags = hero ? this.flags | HERO : this.flags & ~HERO;
	}

	@Override
	public boolean isHero() {
		return ((this.flags & HERO) != 0);
	}
	
	public void setPhysical(boolean physical) {
		this.flags = physical ? this.flags | PHYSICAL : this.flags & ~PHYSICAL;
	}

	@Override
	public boolean isPhysical() {
		return  ((this.flags & PHYSICAL) != 0);
	}
	
	public void setMagic(boolean magic) {
		this.flags = magic ? this.flags | MAGIC : this.flags & ~MAGIC;
	}

	@Override
	public boolean isMagic() {
		return  ((this.flags & MAGIC) != 0);
	}
	
	public void setAura(boolean aura) {
		this.flags = aura ? this.flags | AURA : this.flags & ~AURA;
	}

	@Override
	public boolean isAura() {
		return  ((this.flags & AURA) != 0);
	}
}
