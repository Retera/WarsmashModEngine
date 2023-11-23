package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityAbilityBuilderActiveNoTarget extends CAbilityAbilityBuilderGenericActive {

	private ABBehavior behavior;
	private boolean castless;

	public CAbilityAbilityBuilderActiveNoTarget(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias, levelData, config, localStore);
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		super.onAdd(game, unit);
		determineCastless(unit);
	}
	
	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		determineCastless(unit);
	}
	
	protected void determineCastless(CUnit unit) {
		if (this.item != null || this.config.getDisplayFields() != null && this.config.getDisplayFields().getCastlessNoTarget() != null
				&& this.config.getDisplayFields().getCastlessNoTarget().callback(null, unit, localStore, castId)) {
			this.castless = true;
			this.behavior = null;
		} else {
			this.castless = false;
			this.behavior = this.createNoTargetBehavior(unit);
		}
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
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {

//		System.err.println("Checking queue notarg level: " + active + " orderID : " + orderId + " offID: " + this.getOffOrderId());
		if (castless && orderId == this.getBaseOrderId()) {
//			System.err.println("Castless");
			if (!caster.chargeMana(this.getChargedManaCost())) {
				game.getCommandErrorListener().showInterfaceError(caster.getPlayerIndex(),
						CommandStringErrorKeys.NOT_ENOUGH_MANA);
//				System.err.println("NoMana");
				return false;
			}
//			System.err.println("Had mana?");
			this.startCooldown(game, caster);
			this.runBeginCastingActions(game, caster, orderId);
			this.runEndCastingActions(game, caster, orderId);
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		this.castId++;
		if (castless) {
			return null;
		} else {
			this.runOnOrderIssuedActions(game, caster, orderId);
			this.behavior.setCastId(castId);
			return this.behavior.reset();
		}
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
		return false;
	}

	@Override
	protected boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		return true;
	}

	@Override
	protected boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		return true;
	}


}
