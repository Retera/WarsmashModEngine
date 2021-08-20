package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.UnitIconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class CommandCardPopulatingAbilityVisitor implements CAbilityVisitor<Void> {
	private static final boolean ENABLE_PLACEHOLDERS = false;
	public static final CommandCardPopulatingAbilityVisitor INSTANCE = new CommandCardPopulatingAbilityVisitor();
	private CSimulation game;
	private CUnit unit;

	private CommandButtonListener commandButtonListener;
	private AbilityDataUI abilityDataUI;
	private int menuBaseOrderId;
	private boolean multiSelect;
	private boolean hasStop;
	private final CommandCardActivationReceiverPreviewCallback previewCallback = new CommandCardActivationReceiverPreviewCallback();
	private GameUI gameUI;
	private boolean hasCancel;

	public CommandCardPopulatingAbilityVisitor reset(final CSimulation game, final GameUI gameUI, final CUnit unit,
			final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI,
			final int menuBaseOrderId, final boolean multiSelect) {
		this.game = game;
		this.gameUI = gameUI;
		this.unit = unit;
		this.commandButtonListener = commandButtonListener;
		this.abilityDataUI = abilityDataUI;
		this.menuBaseOrderId = menuBaseOrderId;
		this.multiSelect = multiSelect;
		this.hasStop = false;
		this.hasCancel = false;
		return this;
	}

	@Override
	public Void accept(final CAbilityAttack ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			addCommandButton(ability, this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack, 0,
					false, false);
			boolean attackGroundEnabled = false;
			for (final CUnitAttack attack : this.unit.getAttacks()) {
				if (attack.getWeaponType().isAttackGroundSupported()) {
					attackGroundEnabled = true;
					break;
				}
			}
			if (attackGroundEnabled) {
				addCommandButton(ability, this.abilityDataUI.getAttackGroundUI(), ability.getHandleId(),
						OrderIds.attackground, 0, false, false);
			}
			if (!this.hasStop) {
				this.hasStop = true;
				addCommandButton(ability, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false, false);
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityMove ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			addCommandButton(ability, this.abilityDataUI.getMoveUI(), ability.getHandleId(), OrderIds.move, 0, false,
					false);
			addCommandButton(ability, this.abilityDataUI.getHoldPosUI(), 0, OrderIds.holdposition, 0, false, false);
			addCommandButton(ability, this.abilityDataUI.getPatrolUI(), ability.getHandleId(), OrderIds.patrol, 0,
					false, false);
			if (!this.hasStop) {
				this.hasStop = true;
				addCommandButton(ability, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false, false);
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityGeneric ability) {
		if (ENABLE_PLACEHOLDERS) {
			if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
				final AbilityUI abilityUI = this.abilityDataUI.getUI(ability.getRawcode());
				if (abilityUI != null) {
					addCommandButton(ability, abilityUI.getOnIconUI(), ability.getHandleId(), 0, 0, false, false);
				}
				else {
					addCommandButton(ability, this.abilityDataUI.getStopUI(), ability.getHandleId(), 0, 0, false,
							false);
				}
			}
		}
		return null;
	}

	@Override
	public Void accept(final GenericSingleIconActiveAbility ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			final AbilityUI ui = this.abilityDataUI.getUI(ability.getAlias());
			addCommandButton(ability, ability.isToggleOn() ? ui.getOffIconUI() : ui.getOnIconUI(),
					ability.getHandleId(), ability.getBaseOrderId(), 0, false, false, ability.getUIGoldCost(),
					ability.getUILumberCost(), 0);
		}
		return null;
	}

	@Override
	public Void accept(final GenericNoIconAbility ability) {
		return null;
	}

	@Override
	public Void accept(final CAbilityRally ability) {
		if (this.menuBaseOrderId == 0) {
			addCommandButton(ability, this.abilityDataUI.getRallyUI(), ability.getHandleId(), ability.getBaseOrderId(),
					0, false, false);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityColdArrows ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			final boolean autoCastActive = ability.isAutoCastActive();
			int autoCastId;
			if (autoCastActive) {
				autoCastId = OrderIds.coldarrows;
			}
			else {
				autoCastId = OrderIds.uncoldarrows;
			}
			final IconUI onIconUI = this.abilityDataUI.getUI(ability.getRawcode()).getOnIconUI();
			addCommandButton(ability, onIconUI, ability.getHandleId(), OrderIds.coldarrowstarg, autoCastId,
					autoCastActive, false);
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
		if ((this.menuBaseOrderId == ability.getBaseOrderId()) && ability.isIconShowing()) {
			for (final War3ID unitType : ability.getStructuresBuilt()) {
				final IconUI unitUI = this.abilityDataUI.getUnitUI(unitType);
				if (unitUI != null) {
					final CUnitType simulationUnitType = this.game.getUnitData().getUnitType(unitType);
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false,
							simulationUnitType.getGoldCost(), simulationUnitType.getLumberCost(),
							simulationUnitType.getFoodUsed());
				}
			}
		}
		else {
			if (this.multiSelect) {
				return;
			}
			addCommandButton(ability, buildUI, ability.getHandleId(), ability.getBaseOrderId(), 0, false, true);
		}
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton) {
		addCommandButton(ability, iconUI, handleId, orderId, autoCastOrderId, autoCastActive, menuButton, 0, 0, 0);
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton, final int goldCost,
			final int lumberCost, final int foodCost) {
		addCommandButton(ability, iconUI, iconUI.getToolTip(), iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
				handleId, orderId, autoCastOrderId, autoCastActive, menuButton, goldCost, lumberCost, foodCost);
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final String toolTip,
			final int buttonPosX, final int buttonPosY, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton, int goldCost,
			int lumberCost, int foodCost) {
		ability.checkCanUse(this.game, this.unit, orderId, this.previewCallback.reset());
		final boolean active = ((this.unit.getCurrentBehavior() != null)
				&& (orderId == this.unit.getCurrentBehavior().getHighlightOrderId()));
		final boolean disabled = ((ability != null) && ability.isDisabled()) || this.previewCallback.disabled;
		String uberTip = iconUI.getUberTip();
		if (disabled) {
			// dont show these on disabled
			goldCost = 0;
			lumberCost = 0;
			foodCost = 0;
		}
		if (this.previewCallback.isShowingRequirements()) {
			uberTip = this.previewCallback.getRequirementsText() + "|r" + uberTip;
		}
		this.commandButtonListener.commandButton(buttonPosX, buttonPosY,
				disabled ? iconUI.getIconDisabled() : iconUI.getIcon(), handleId, disabled ? 0 : orderId,
				autoCastOrderId, active, autoCastActive, menuButton, toolTip, uberTip, iconUI.getHotkey(), goldCost,
				lumberCost, foodCost);
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
	public Void accept(final CAbilityReviveHero ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			int heroIndex = 0;
			for (final CUnit playerHero : this.game.getPlayerHeroes(this.unit.getPlayerIndex())) {
				final CAbilityHero heroData = playerHero.getHeroData();
				if (playerHero.isDead() && (heroData != null) && heroData.isAwaitingRevive()
						&& !heroData.isReviving()) {

					final UnitIconUI unitUI = this.abilityDataUI.getUnitUI(playerHero.getTypeId());
					if (unitUI != null) {
						final CUnitType simulationUnitType = playerHero.getUnitType();
						final int goldCost = this.game.getGameplayConstants()
								.getHeroReviveGoldCost(simulationUnitType.getGoldCost(), heroData.getHeroLevel());
						final int lumberCost = this.game.getGameplayConstants()
								.getHeroReviveLumberCost(simulationUnitType.getLumberCost(), heroData.getHeroLevel());
						addCommandButton(ability, unitUI, unitUI.getReviveTip() + " - " + heroData.getProperName(),
								heroIndex++, 0, ability.getHandleId(), playerHero.getHandleId(), 0, false, false,
								goldCost, lumberCost, simulationUnitType.getFoodUsed());
					}
				}
			}
			if (this.unit.getBuildQueueTypes()[0] != null) {
				if (!this.hasCancel) {
					this.hasCancel = true;
					addCommandButton(ability, this.abilityDataUI.getCancelTrainUI(), ability.getHandleId(),
							OrderIds.cancel, 0, false, false);
				}
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityQueue ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			for (final War3ID unitType : ability.getUnitsTrained()) {
				final IconUI unitUI = this.abilityDataUI.getUnitUI(unitType);
				if (unitUI != null) {
					final CUnitType simulationUnitType = this.game.getUnitData().getUnitType(unitType);
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false,
							simulationUnitType.getGoldCost(), simulationUnitType.getLumberCost(),
							simulationUnitType.getFoodUsed());
				}
			}
			if (ENABLE_PLACEHOLDERS) {
				for (final War3ID unitType : ability.getResearchesAvailable()) {
					final CPlayer player = this.game.getPlayer(this.unit.getPlayerIndex());
					final IconUI unitUI = this.abilityDataUI.getUpgradeUI(unitType,
							player.getTechtreeUnlocked(unitType));
					if (unitUI != null) {
						addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false);
					}
				}
			}
			if (this.unit.getBuildQueueTypes()[0] != null) {
				if (!this.hasCancel) {
					this.hasCancel = true;
					addCommandButton(ability, this.abilityDataUI.getCancelTrainUI(), ability.getHandleId(),
							OrderIds.cancel, 0, false, false);
				}
			}
		}
		return null;
	}

	private final class CommandCardActivationReceiverPreviewCallback implements AbilityActivationReceiver {
		private boolean disabled;
		private final StringBuilder requirementsTextBuilder = new StringBuilder();

		public CommandCardActivationReceiverPreviewCallback reset() {
			this.disabled = false;
			this.requirementsTextBuilder.setLength(0);
			return this;
		}

		@Override
		public void useOk() {

		}

		@Override
		public void notEnoughResources(final ResourceType resource) {

		}

		@Override
		public void notAnActiveAbility() {

		}

		@Override
		public void missingRequirement(final War3ID type, final int level) {
			this.disabled = true;
			if (this.requirementsTextBuilder.length() == 0) {
				this.requirementsTextBuilder.append(CommandCardPopulatingAbilityVisitor.this.gameUI.getTemplates()
						.getDecoratedString("REQUIRESTOOLTIP"));
				this.requirementsTextBuilder.append("|n - ");
			}
			else {
				this.requirementsTextBuilder.append(" - ");
			}
			final CUnitType unitType = CommandCardPopulatingAbilityVisitor.this.game.getUnitData().getUnitType(type);
			this.requirementsTextBuilder
					.append(unitType == null ? "NOTEXTERN Unknown ('" + type + "')" : unitType.getName());
			this.requirementsTextBuilder.append("|n");
		}

		@Override
		public void casterMovementDisabled() {

		}

		@Override
		public void cargoCapacityUnavailable() {
		}

		@Override
		public void disabled() {
			this.disabled = true;
		}

		public boolean isShowingRequirements() {
			return this.requirementsTextBuilder.length() != 0;
		}

		public String getRequirementsText() {
			return this.requirementsTextBuilder.toString();
		}
	}

	@Override
	public Void accept(final CAbilityHero ability) {
		// TODO
		return null;
	}
}
