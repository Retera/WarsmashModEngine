package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CAbilityNeutralBuilding extends AbstractGenericAliasedAbility {
	private final class CUnitEnumFunctionImplementation implements CUnitEnumFunction {
		private final int maxMapPlayers;
		private final CSimulation game;
		private final CUnit unit;
		private boolean updated = false;

		private CUnitEnumFunctionImplementation(final int maxMapPlayers, final CSimulation game, final CUnit unit) {
			this.maxMapPlayers = maxMapPlayers;
			this.game = game;
			this.unit = unit;
		}

		@Override
		public boolean call(final CUnit enumUnit) {
			final int playerIndex = enumUnit.getPlayerIndex();
			if (playerIndex < this.maxMapPlayers) {
				if (CAbilityNeutralBuilding.this.selectedPlayerUnit[playerIndex] == null) {
					if (canSelectUnit(this.game, this.unit, enumUnit)) {
						selectUnit(this.game, enumUnit, playerIndex);
						this.updated = true;
					}
				}
			}
			return false;
		}
	}

	private static final int UNIT_CHECK_DELAY = (int) (StrictMath.ceil(3.00f / WarsmashConstants.SIMULATION_STEP_TIME));

	private static final int INTERACTION_TYPE_ANY_UNIT_W_INVENTORY = 1;
	private static final int INTERACTION_TYPE_ANY_NON_BUILDING = 2;
	private static final int INTERACTION_TYPE_ANY = 4;
	private static final int INTERACTION_TYPE_ANY_ANE2 = 16;

	private float activationRadius;
	private int interactionType;
	private boolean showSelectUnitButton;
	private boolean showUnitIndicator;
	private final boolean onlySelectAllies;

	private int nextUpdateTick = 0;
	private final CUnit[] selectedPlayerUnit = new CUnit[WarsmashConstants.MAX_PLAYERS];
	private final SimulationRenderComponent[] selectedPlayerUnitFx = new SimulationRenderComponent[WarsmashConstants.MAX_PLAYERS];
	private final Rectangle recycleRect = new Rectangle();

	public CAbilityNeutralBuilding(final int handleId, final War3ID code, final War3ID alias,
			final float activationRadius, final int interactionType, final boolean showSelectUnitButton,
			final boolean showUnitIndicator, final boolean onlySelectAllies) {
		super(handleId, code, alias);
		this.activationRadius = activationRadius;
		this.interactionType = interactionType;
		this.showSelectUnitButton = showSelectUnitButton;
		this.showUnitIndicator = showUnitIndicator;
		this.onlySelectAllies = onlySelectAllies;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		final int gameTurnTick = game.getGameTurnTick();
		if (gameTurnTick >= this.nextUpdateTick) {
			boolean searchUnits = false;
			final int maxMapPlayers = WarsmashConstants.MAX_PLAYERS - 4;
			for (int i = 0; i < maxMapPlayers; i++) {
				final CUnit selectedUnit = this.selectedPlayerUnit[i];
				if (!canSelectUnit(game, unit, selectedUnit)) {
					unselectUnit(i);
					searchUnits = true;
				}
			}
			if (searchUnits) {
				final CUnitEnumFunctionImplementation perUnitCallback = new CUnitEnumFunctionImplementation(
						maxMapPlayers, game, unit);
				game.getWorldCollision().enumUnitsInRect(this.recycleRect.set(unit.getX() - this.activationRadius,
						unit.getY() - this.activationRadius, this.activationRadius * 2, this.activationRadius * 2),
						perUnitCallback);
				unit.notifyOrdersChanged();
			}
			this.nextUpdateTick = gameTurnTick + UNIT_CHECK_DELAY;
		}
	}

	private void unselectUnit(final int i) {
		if (this.selectedPlayerUnit[i] != null) {
			this.selectedPlayerUnit[i].notifyOrdersChanged();
			this.selectedPlayerUnit[i] = null;
		}
		if (this.selectedPlayerUnitFx[i] != null) {
			this.selectedPlayerUnitFx[i].remove();
			this.selectedPlayerUnitFx[i] = null;
		}
	}

	private boolean canSelectUnit(final CSimulation game, final CUnit shop, final CUnit unit) {
		if ((unit == null) || !shop.canReach(unit, this.activationRadius) || unit.isDead()
				|| (this.onlySelectAllies && !unit.isUnitAlly(game.getPlayer(shop.getPlayerIndex())))) {
			return false;
		}
		switch (this.interactionType) {
		case INTERACTION_TYPE_ANY:
		case INTERACTION_TYPE_ANY_ANE2:
			return true;
		case INTERACTION_TYPE_ANY_NON_BUILDING:
			return !unit.isBuilding();
		case INTERACTION_TYPE_ANY_UNIT_W_INVENTORY:
			return unit.getInventoryData() != null;
		}
		return false;
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId) {
		// TODO Auto-generated method stub

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final CWidget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (this.showSelectUnitButton && (orderId == OrderIds.neutralinteract)
				&& canSelectUnit(game, caster, targetUnit)) {
			unselectUnit(playerIndex);
			selectUnit(game, targetUnit, playerIndex);
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final AbilityPointTarget point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex,
			final int orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.showSelectUnitButton && (orderId == OrderIds.neutralinteract)
				&& canSelectUnit(game, unit, target.visit(AbilityTargetVisitor.UNIT))) {
			receiver.targetOk(target);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, int playerIndex,
			final int orderId, final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	public float getActivationRadius() {
		return this.activationRadius;
	}

	public int getInteractionType() {
		return this.interactionType;
	}

	public boolean isShowSelectUnitButton() {
		return this.showSelectUnitButton;
	}

	public boolean isShowUnitIndicator() {
		return this.showUnitIndicator;
	}

	public void setActivationRadius(final float activationRadius) {
		this.activationRadius = activationRadius;
	}

	public void setInteractionType(final int interactionType) {
		this.interactionType = interactionType;
	}

	public void setShowSelectUnitButton(final boolean showSelectUnitButton) {
		this.showSelectUnitButton = showSelectUnitButton;
	}

	public void setShowUnitIndicator(final boolean showUnitIndicator) {
		this.showUnitIndicator = showUnitIndicator;
	}

	public CUnit getSelectedPlayerUnit(final int playerIndex) {
		return this.selectedPlayerUnit[playerIndex];
	}

	private void selectUnit(final CSimulation game, final CUnit enumUnit, final int playerIndex) {
		this.selectedPlayerUnit[playerIndex] = enumUnit;
		if (this.showUnitIndicator) {
			this.selectedPlayerUnitFx[playerIndex] = game.createPersistentSpellEffectOnUnit(enumUnit, getAlias(),
					CEffectType.TARGET, 0);
		}
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
