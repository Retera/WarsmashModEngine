package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class ABAbilityBuilderActiveNoTarget extends ABAbilityBuilderGenericActive {

	private ABBehavior behavior;
	private boolean castless;

	public ABAbilityBuilderActiveNoTarget(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration config,
			ABLocalDataStore localStore) {
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
		if (this.item != null || this.config.getDisplayFields() != null
				&& this.config.getDisplayFields().getCastlessNoTarget() != null && this.config.getDisplayFields()
						.getCastlessNoTarget().callback(unit, localStore, ABConstants.NO_CAST_ID)) {
			this.castless = true;
			this.behavior = null;
		} else {
			this.castless = false;
			this.behavior = this.createNoTargetBehavior(unit);
		}
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityPointTarget point) {
		return null;
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder,
			final AbilityTarget target) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);

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
			caster.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CHANNEL, this, null);
			caster.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_CAST, this, null);
			caster.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_EFFECT, this, null);
			caster.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_FINISH, this, null);
			caster.fireSpellEvents(game, JassGameEventsWar3.EVENT_UNIT_SPELL_ENDCAST, this, null);
			this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
			return false;
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
		return super.checkBeforeQueue(game, caster, orderId, autoOrder, target);
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId, boolean autoOrder) {
		this.internalBegin(game, caster, orderId, autoOrder, null);
		if (castless) {
			return null;
		} else {
			this.behavior.setCastId(castId);
			return this.behavior.reset(orderId, autoOrder);
		}
	}

	@Override
	public void internalBegin(CSimulation game, CUnit caster, int orderId, boolean autoOrder, AbilityTarget target) {
		this.castId = ABConstants.incrementCastId(this.castId);
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.CASTINSTANCELEVEL, castId), this.getLevel());
		if (!castless) {
			this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ISAUTOCAST, castId), autoOrder);
			this.localStore.put(ABLocalStoreKeys.PREVIOUSBEHAVIOR, caster.getCurrentBehavior());
			this.runOnOrderIssuedActions(game, caster, orderId);
		}
	}

	@Override
	public void cleanupInputs(int theCastId) {
		this.localStore.remove(ABLocalStoreKeys.PREVIOUSBEHAVIOR);
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
