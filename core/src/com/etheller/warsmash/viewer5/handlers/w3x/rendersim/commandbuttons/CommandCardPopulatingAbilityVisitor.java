package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.AbstractCAbilityBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CommandCardPopulatingAbilityVisitor implements CAbilityVisitor<Void> {
	public static final CommandCardPopulatingAbilityVisitor INSTANCE = new CommandCardPopulatingAbilityVisitor();
	private CSimulation game;
	private CUnit unit;

	private CommandButtonListener commandButtonListener;
	private AbilityDataUI abilityDataUI;
	private int menuBaseOrderId;
	private boolean hasStop;
	private boolean underConstruction;

	public CommandCardPopulatingAbilityVisitor reset(final CSimulation game, final CUnit unit,
			final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI,
			final int menuBaseOrderId, final boolean underConstruction) {
		this.game = game;
		this.unit = unit;
		this.commandButtonListener = commandButtonListener;
		this.abilityDataUI = abilityDataUI;
		this.menuBaseOrderId = menuBaseOrderId;
		this.underConstruction = underConstruction;
		this.hasStop = false;
		return this;
	}

	@Override
	public Void accept(final CAbilityAttack ability) {
		if ((this.menuBaseOrderId == 0) && !this.underConstruction) {
			addCommandButton(ability, this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack, 0,
					false, false);
			if (!this.hasStop) {
				this.hasStop = true;
				addCommandButton(null, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false, false);
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityMove ability) {
		if ((this.menuBaseOrderId == 0) && !this.underConstruction) {
			addCommandButton(ability, this.abilityDataUI.getMoveUI(), ability.getHandleId(), OrderIds.move, 0, false,
					false);
			addCommandButton(ability, this.abilityDataUI.getHoldPosUI(), ability.getHandleId(), OrderIds.holdposition,
					0, false, false);
			addCommandButton(ability, this.abilityDataUI.getPatrolUI(), ability.getHandleId(), OrderIds.patrol, 0,
					false, false);
			if (!this.hasStop) {
				this.hasStop = true;
				addCommandButton(null, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false, false);
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityGeneric ability) {
		if ((this.menuBaseOrderId == 0) && !this.underConstruction) {
			addCommandButton(ability, this.abilityDataUI.getUI(ability.getRawcode()).getOnIconUI(),
					ability.getHandleId(), 0, 0, false, false);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityColdArrows ability) {
		if ((this.menuBaseOrderId == 0) && !this.underConstruction) {
			final boolean autoCastActive = ability.isAutoCastActive();
			int autoCastId;
			if (autoCastActive) {
				autoCastId = OrderIds.coldarrows;
			}
			else {
				autoCastId = OrderIds.uncoldarrows;
			}
			addCommandButton(ability, this.abilityDataUI.getUI(ability.getRawcode()).getOnIconUI(),
					ability.getHandleId(), OrderIds.coldarrowstarg, autoCastId, autoCastActive, false);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityOrcBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildOrcUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityHumanBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildHumanUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityNightElfBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildNightElfUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityUndeadBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildUndeadUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityNagaBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildNagaUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityNeutralBuild ability) {
		handleBuildMenu(ability, this.abilityDataUI.getBuildNeutralUI());
		return null;
	}

	private void handleBuildMenu(final AbstractCAbilityBuild ability, final IconUI buildUI) {
		if ((this.menuBaseOrderId == ability.getBaseOrderId()) && !this.underConstruction) {
			for (final War3ID unitType : ability.getStructuresBuilt()) {
				final IconUI unitUI = this.abilityDataUI.getUnitUI(unitType);
				if (unitUI != null) {
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false);
				}
			}
		}
		else {
			addCommandButton(ability, buildUI, ability.getHandleId(), ability.getBaseOrderId(), 0, false, true);
		}
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton) {
		final boolean active = ((this.unit.getCurrentBehavior() != null)
				&& (orderId == this.unit.getCurrentBehavior().getHighlightOrderId()));
		this.commandButtonListener.commandButton(iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
				iconUI.getIcon(), handleId, orderId, autoCastOrderId, active, autoCastActive, menuButton);
	}

	@Override
	public Void accept(final CAbilityBuildInProgress ability) {
		if (this.menuBaseOrderId == 0) {
			addCommandButton(ability, this.abilityDataUI.getCancelBuildUI(), ability.getHandleId(), OrderIds.cancel, 0,
					false, false);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityQueue ability) {
		if ((this.menuBaseOrderId == 0) && !this.underConstruction) {
			for (final War3ID unitType : ability.getUnitsTrained()) {
				final IconUI unitUI = this.abilityDataUI.getUnitUI(unitType);
				if (unitUI != null) {
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false);
				}
			}
			for (final War3ID unitType : ability.getResearchesAvailable()) {
				final CPlayer player = this.game.getPlayer(this.unit.getPlayerIndex());
				final IconUI unitUI = this.abilityDataUI.getUpgradeUI(unitType, player.getTechtreeUnlocked(unitType));
				if (unitUI != null) {
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false);
				}
			}
		}
		return null;
	}
}
