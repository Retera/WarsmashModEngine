package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.bloodmage.phoenix;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityPhoenixFire extends AbstractGenericNoIconAbility {

	private float initialDamage;
	private float damagePerSecond;
	private float areaOfEffect;
	private float cooldown;
	private float duration;
	private EnumSet<CTargetType> targetsAllowed;

	private int lastAttackTurnTick;

	public CAbilityPhoenixFire(int handleId, War3ID alias, float initialDamage, float damagePerSecond,
			float areaOfEffect, float cooldown, float duration, final EnumSet<CTargetType> targetsAllowed) {
		super(handleId, alias);
		this.initialDamage = initialDamage;
		this.damagePerSecond = damagePerSecond;
		this.areaOfEffect = areaOfEffect;
		this.cooldown = cooldown;
		this.duration = duration;
		this.targetsAllowed = targetsAllowed;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
		final int cooldownTicks = (int) (this.cooldown / WarsmashConstants.SIMULATION_STEP_TIME);
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick > (this.lastAttackTurnTick + cooldownTicks)) {
			game.getWorldCollision().enumUnitsInRect(new Rectangle(unit.getX() - this.areaOfEffect,
					unit.getY() - this.areaOfEffect, this.areaOfEffect * 2, this.areaOfEffect * 2), enumUnit -> {
						if (unit.canReach(enumUnit, this.areaOfEffect)
								&& enumUnit.canBeTargetedBy(game, unit, this.targetsAllowed)) {
							unit.getAttacks().get(0).launch(game, unit, enumUnit, this.initialDamage,
									CUnitAttackListener.DO_NOTHING);
							this.lastAttackTurnTick = gameTurnTick;
//							return true;
						}
						return false;
					});
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit cUnit) {

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
		return null;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		return null;
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	public void checkCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.notAnActiveAbility();
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		receiver.notAnActiveAbility();
	}

	public float getInitialDamage() {
		return this.initialDamage;
	}

	public float getDamagePerSecond() {
		return this.damagePerSecond;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public float getCooldown() {
		return this.cooldown;
	}

	public float getDuration() {
		return this.duration;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return this.targetsAllowed;
	}

	public void setInitialDamage(float initialDamage) {
		this.initialDamage = initialDamage;
	}

	public void setDamagePerSecond(float damagePerSecond) {
		this.damagePerSecond = damagePerSecond;
	}

	public void setAreaOfEffect(float areaOfEffect) {
		this.areaOfEffect = areaOfEffect;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void setTargetsAllowed(EnumSet<CTargetType> targetsAllowed) {
		this.targetsAllowed = targetsAllowed;
	}

}
