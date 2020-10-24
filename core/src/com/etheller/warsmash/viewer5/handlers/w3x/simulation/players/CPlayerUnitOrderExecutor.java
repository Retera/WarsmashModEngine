package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.StringMsgTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;

public class CPlayerUnitOrderExecutor implements CPlayerUnitOrderListener {
	private final CSimulation game;
	private final CommandErrorListener errorListener;
	private final StringMsgTargetCheckReceiver<?> targetCheckReceiver = new StringMsgTargetCheckReceiver<>();
	private final StringMsgAbilityActivationReceiver abilityActivationReceiver = new StringMsgAbilityActivationReceiver();

	private <T> StringMsgTargetCheckReceiver<T> targetCheckReceiver() {
		return (StringMsgTargetCheckReceiver<T>) this.targetCheckReceiver.reset();
	}

	public CPlayerUnitOrderExecutor(final CSimulation game, final CommandErrorListener errorListener) {
		this.game = game;
		this.errorListener = errorListener;
	}

	@Override
	public boolean issueTargetOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final int targetHandleId, final boolean queue) {
		final CUnit unit = this.game.getUnit(unitHandleId);
		final CAbility ability = this.game.getAbility(abilityHandleId);
		ability.checkCanUse(this.game, unit, orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final CUnit target = this.game.getUnit(targetHandleId);
			final StringMsgTargetCheckReceiver<CWidget> targetReceiver = this.<CWidget>targetCheckReceiver();
			ability.checkCanTarget(this.game, unit, orderId, target, targetReceiver);
			if (targetReceiver.getTarget() != null) {
				ability.onOrder(this.game, unit, orderId, target, queue);
				return true;
			}
			else {
				this.errorListener.showCommandError(targetReceiver.getMessage());
				return false;
			}
		}
		else {
			this.errorListener.showCommandError(this.abilityActivationReceiver.getMessage());
			return false;
		}
	}

	@Override
	public boolean issuePointOrder(final int unitHandleId, final int abilityHandleId, final int orderId, final float x,
			final float y, final boolean queue) {
		final CUnit unit = this.game.getUnit(unitHandleId);
		final CAbility ability = this.game.getAbility(abilityHandleId);
		ability.checkCanUse(this.game, unit, orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final Vector2 target = new Vector2(x, y);
			final StringMsgTargetCheckReceiver<Vector2> targetReceiver = this.<Vector2>targetCheckReceiver();
			ability.checkCanTarget(this.game, unit, orderId, target, targetReceiver);
			if (targetReceiver.getTarget() != null) {
				ability.onOrder(this.game, unit, orderId, target, queue);
				return true;
			}
			else {
				this.errorListener.showCommandError(targetReceiver.getMessage());
				return false;
			}
		}
		else {
			this.errorListener.showCommandError(this.abilityActivationReceiver.getMessage());
			return false;
		}
	}

	@Override
	public boolean issueImmediateOrder(final int unitHandleId, final int abilityHandleId, final int orderId,
			final boolean queue) {
		final CUnit unit = this.game.getUnit(unitHandleId);
		final CAbility ability = this.game.getAbility(abilityHandleId);
		ability.checkCanUse(this.game, unit, orderId, this.abilityActivationReceiver.reset());
		if (this.abilityActivationReceiver.isUseOk()) {
			final StringMsgTargetCheckReceiver<Void> targetReceiver = this.<Void>targetCheckReceiver();
			ability.checkCanTargetNoTarget(this.game, unit, orderId, targetReceiver);
			if (targetReceiver.getTarget() != null) {
				ability.onOrderNoTarget(this.game, unit, orderId, queue);
				return true;
			}
			else {
				this.errorListener.showCommandError(targetReceiver.getMessage());
				return false;
			}
		}
		else {
			this.errorListener.showCommandError(this.abilityActivationReceiver.getMessage());
			return false;
		}
	}

}
