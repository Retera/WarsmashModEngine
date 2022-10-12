package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.root;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root.CBehaviorRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root.CBehaviorUproot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityRoot extends AbstractGenericSingleIconNoSmartActiveAbility {
	private boolean rooted;
	
	private int rootedWeaponsAttackBits;
	private int uprootedWeaponsAttackBits;
	private boolean rootedTurning;
	private CDefenseType uprootedDefenseType;
	private float duration;
	private float offDuration;
	
	private List<CAbility> rootedAbilities = new ArrayList<>();
	private List<CAbility> uprootedAbilities = new ArrayList<>();

	private CBehaviorRoot behaviorRoot;
	private CBehaviorUproot behaviorUproot;
	private CBehaviorMove moveBehavior;
	
	public CAbilityRoot(int handleId, War3ID alias, int rootedWeaponsAttackBits, int uprootedWeaponsAttackBits,
			boolean rootedTurning, CDefenseType uprootedDefenseType, float duration, float offDuration) {
		super(handleId, alias);
		this.rootedWeaponsAttackBits = rootedWeaponsAttackBits;
		this.uprootedWeaponsAttackBits = uprootedWeaponsAttackBits;
		this.rootedTurning = rootedTurning;
		this.uprootedDefenseType = uprootedDefenseType;
		this.duration = duration;
		this.offDuration = offDuration;
	}

	@Override
	public int getBaseOrderId() {
		return rooted ? OrderIds.unroot : OrderIds.root;
	}

	@Override
	public boolean isToggleOn() {
		return rooted;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		uprootedAbilities.clear();
		rootedAbilities.clear();
		for(CAbility ability: unit.getAbilities()) {
			if(ability instanceof CAbilityMove) {
				uprootedAbilities.add(ability);
			} else if (ability instanceof CAbilityAttack || ability instanceof CAbilityRoot) {
			} else {
				rootedAbilities.add(ability);
			}
		}
		behaviorRoot = new CBehaviorRoot(unit, this);
		behaviorUproot = new CBehaviorUproot(unit, this);
		moveBehavior = unit.getMoveBehavior();
		this.rooted = true;
		for(CAbility ability: uprootedAbilities) {
			unit.remove(game, ability);
		}
		setRooted(false, unit, game);
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		if(!rooted && orderId == OrderIds.root) {
			return behaviorRoot.reset(point);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		if(rooted && orderId == OrderIds.unroot) {
			return behaviorUproot.reset();
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if(!rooted && orderId == OrderIds.root) {
			receiver.targetOk(target);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if(rooted && orderId == OrderIds.unroot) {
			receiver.targetOk(null);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	public boolean isRooted() {
		return rooted;
	}

	public void setRooted(boolean rooted, CUnit unit, CSimulation game) {
		boolean rooting = !this.rooted && rooted;
		boolean uprooting = this.rooted && !rooted;
		this.rooted = rooted;
		if(rooting) {
			game.getWorldCollision().removeUnit(unit);
			for(CAbility ability: uprootedAbilities) {
				unit.remove(game, ability);
			}
			for(CAbility ability: rootedAbilities) {
				unit.add(game, ability);
			}
			unit.setMoveBehavior(null);
			unit.setStructure(true);
			game.getWorldCollision().addUnit(unit);
		} else if(uprooting) {
			game.getWorldCollision().removeUnit(unit);
			for(CAbility ability: rootedAbilities) {
				unit.remove(game, ability);
			}
			for(CAbility ability: uprootedAbilities) {
				unit.add(game, ability);
			}
			unit.setMoveBehavior(moveBehavior);
			unit.setStructure(false);
			game.getWorldCollision().addUnit(unit);
		}
	}

	public int getRootedWeaponsAttackBits() {
		return rootedWeaponsAttackBits;
	}

	public void setRootedWeaponsAttackBits(int rootedWeaponsAttackBits) {
		this.rootedWeaponsAttackBits = rootedWeaponsAttackBits;
	}

	public int getUprootedWeaponsAttackBits() {
		return uprootedWeaponsAttackBits;
	}

	public void setUprootedWeaponsAttackBits(int uprootedWeaponsAttackBits) {
		this.uprootedWeaponsAttackBits = uprootedWeaponsAttackBits;
	}

	public boolean isRootedTurning() {
		return rootedTurning;
	}

	public void setRootedTurning(boolean rootedTurning) {
		this.rootedTurning = rootedTurning;
	}

	public CDefenseType getUprootedDefenseType() {
		return uprootedDefenseType;
	}

	public void setUprootedDefenseType(CDefenseType uprootedDefenseType) {
		this.uprootedDefenseType = uprootedDefenseType;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getOffDuration() {
		return offDuration;
	}

	public void setOffDuration(float offDuration) {
		this.offDuration = offDuration;
	}

	public List<CAbility> getRootedAbilities() {
		return rootedAbilities;
	}
	
	public List<CAbility> getUprootedAbilities() {
		return uprootedAbilities;
	}
	
	@Override
	public <T> T visit(CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}
	
}
