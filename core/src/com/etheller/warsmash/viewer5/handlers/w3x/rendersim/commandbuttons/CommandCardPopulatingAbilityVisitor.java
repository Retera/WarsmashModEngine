package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons;

import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.BuffUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.UnitIconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType.UpgradeLevel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGenericDoNothing;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilitySellItems;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.jass.CAbilityJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityReviveHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition.JassOrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.jass.CAbilityTypeJassDefinition.JassOrderButtonType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade.CAbilityUpgrade;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
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
	private int localPlayerIndex;
	private boolean hasStop;
	private final CommandCardActivationReceiverPreviewCallback previewCallback = new CommandCardActivationReceiverPreviewCallback();
	private GameUI gameUI;
	private boolean hasCancel;

	public CommandCardPopulatingAbilityVisitor reset(final CSimulation game, final GameUI gameUI, final CUnit unit,
			final CommandButtonListener commandButtonListener, final AbilityDataUI abilityDataUI,
			final int menuBaseOrderId, final boolean multiSelect, final int localPlayerIndex) {
		this.game = game;
		this.gameUI = gameUI;
		this.unit = unit;
		this.commandButtonListener = commandButtonListener;
		this.abilityDataUI = abilityDataUI;
		this.menuBaseOrderId = menuBaseOrderId;
		this.multiSelect = multiSelect;
		this.localPlayerIndex = localPlayerIndex;
		this.hasStop = false;
		this.hasCancel = false;
		return this;
	}

	@Override
	public Void accept(final CAbilityAttack ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			boolean attackGroundEnabled = false;
			boolean showUI = false;
			for (final CUnitAttack attack : this.unit.getCurrentAttacks()) {
				if (attack.getWeaponType().isAttackGroundSupported()) {
					attackGroundEnabled = true;
				}
				if (attack.isShowUI()) {
					showUI = true;
				}
			}
			if (showUI) {
				addCommandButton(ability, this.abilityDataUI.getAttackUI(), ability.getHandleId(), OrderIds.attack, 0,
						false, false);
				if (attackGroundEnabled) {
					addCommandButton(ability, this.abilityDataUI.getAttackGroundUI(), ability.getHandleId(),
							OrderIds.attackground, 0, false, false);
				}
				if (!this.hasStop) {
					this.hasStop = true;
					addCommandButton(ability, this.abilityDataUI.getStopUI(), 0, OrderIds.stop, 0, false, false);
				}
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
	public Void accept(final CAbilityGenericDoNothing ability) {
		if (ENABLE_PLACEHOLDERS) {
			if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
				final AbilityUI abilityUI = this.abilityDataUI.getUI(ability.getAlias());
				if (abilityUI != null) {
					addCommandButton(ability, abilityUI.getOnIconUI(ability.getLevel() - 1), ability.getHandleId(), 0,
							0, false, false);
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
			final boolean autoCastOn = ability.isAutoCastOn();
			addCommandButton(ability,
					ability.isToggleOn() ? ui.getOffIconUI(ability.getLevel() - 1)
							: ui.getOnIconUI(ability.getLevel() - 1),
					ability.getHandleId(), ability.getBaseOrderId(),
					autoCastOn ? ability.getAutoCastOffOrderId() : ability.getAutoCastOnOrderId(), autoCastOn, false,
					ability.getUIGoldCost(), ability.getUILumberCost(), 0, ability.getUIManaCost(), -1);
		}
		return null;
	}

	@Override
	public Void accept(final CBuff ability) {
		final BuffUI buffUI = this.abilityDataUI.getBuffUI(ability.getAlias());
		addBuffIcon(ability, buffUI.getOnIconUI());
		return null;
	}

	@Override
	public Void accept(final CAbilityRoot ability) {
		return accept((GenericSingleIconActiveAbility) ability);
	}

	@Override
	public Void accept(final GenericNoIconAbility ability) {
		return null;
	}

	@Override
	public Void accept(final CAbilityReturnResources ability) {
		return null;
	}

	@Override
	public Void accept(final CAbilityNeutralBuilding ability) {
		if (this.menuBaseOrderId == 0) {
			if (ability.isShowSelectUnitButton()) {
				addCommandButton(ability, this.abilityDataUI.getNeutralInteractUI(), ability.getHandleId(),
						OrderIds.neutralinteract, 0, false, false);
			}
		}
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
			final IconUI onIconUI = this.abilityDataUI.getUI(ability.getAlias()).getOnIconUI(ability.getLevel() - 1);
			addCommandButton(ability, onIconUI, ability.getHandleId(), OrderIds.coldarrowstarg, autoCastId,
					autoCastActive, false);
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityJass ability) {
		if (ability.isIconShowing()) {
			for (final JassOrder order : ability.getType().getJassOrders()) {
				if (this.menuBaseOrderId == order.getContainerMenuOrderId()) {
					addCommandButton(ability, this.gameUI.loadTexture(order.getIconPath()),
							this.gameUI.loadTexture(
									AbilityDataUI.disable(order.getIconPath(), this.abilityDataUI.getDisabledPrefix())),
							order.getTip(), order.getUberTip(), order.getButtonPositionX(), order.getButtonPositionY(),
							ability.getHandleId(), order.getOrderId(),
							order.isAutoCastActive() ? order.getAutoCastOrderId() : order.getAutoCastUnOrderId(),
							order.isAutoCastActive(), order.getType() == JassOrderButtonType.MENU, order.getGoldCost(),
							order.getLumberCost(), order.getFoodCostDisplayOnly(), order.getManaCost(),
							order.getCharges(), order.getHotkey());
				}
			}
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
							simulationUnitType.getFoodUsed(), 0, -1);
				}
			}
		}
		else if (this.menuBaseOrderId == 0) {
			if (this.multiSelect) {
				return;
			}
			addCommandButton(ability, buildUI, ability.getHandleId(), ability.getBaseOrderId(), 0, false, true);
		}
	}

	private void addBuffIcon(final CBuff buff, final IconUI iconUI) {
		if (!this.multiSelect) {
			if (buff.isTimedLifeBar()) {
				if (this.unit.getPlayerIndex() == this.localPlayerIndex) {
					this.commandButtonListener.timedLifeBar(buff.getLevel(), iconUI.getToolTip(),
							buff.getDurationRemaining(this.game), buff.getDurationMax());
				}
			}
			else {
				this.commandButtonListener.buff(iconUI.getIcon(), buff.getLevel(), iconUI.getToolTip(),
						iconUI.getUberTip());
			}
		}
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton) {
		addCommandButton(ability, iconUI, handleId, orderId, autoCastOrderId, autoCastActive, menuButton, 0, 0, 0, 0,
				-1);
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton, final int goldCost,
			final int lumberCost, final int foodCost, final int manaCost, final int numberOverlay) {
		addCommandButton(ability, iconUI, iconUI.getToolTip(), iconUI.getButtonPositionX(), iconUI.getButtonPositionY(),
				handleId, orderId, autoCastOrderId, autoCastActive, menuButton, goldCost, lumberCost, foodCost,
				manaCost, numberOverlay);
	}

	private void addCommandButton(final CAbility ability, final IconUI iconUI, final String toolTip,
			final int buttonPosX, final int buttonPosY, final int handleId, final int orderId,
			final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton, final int goldCost,
			final int lumberCost, final int foodCost, final int manaCost, final int numberOverlay) {
		addCommandButton(ability, iconUI.getIcon(), iconUI.getIconDisabled(), toolTip, iconUI.getUberTip(), buttonPosX,
				buttonPosY, handleId, orderId, autoCastOrderId, autoCastActive, menuButton, goldCost, lumberCost,
				foodCost, manaCost, numberOverlay, iconUI.getHotkey());
	}

	private void addCommandButton(final CAbility ability, final Texture icon, final Texture iconDisabled,
			final String toolTip, String uberTip, final int buttonPosX, final int buttonPosY, final int handleId,
			final int orderId, final int autoCastOrderId, final boolean autoCastActive, final boolean menuButton,
			int goldCost, int lumberCost, int foodCost, final int manaCost, final int numberOverlay,
			final char hotkey) {
		boolean requiresPatron = false;
		if (this.unit.getPlayerIndex() != this.localPlayerIndex) {
			boolean controlShared = this.game.getPlayer(this.unit.getPlayerIndex()).hasAlliance(this.localPlayerIndex,
					CAllianceType.SHARED_CONTROL);
			if (!controlShared) {
				final CAbilityNeutralBuilding neutralBuildingData = this.unit.getNeutralBuildingData();
				if (neutralBuildingData != null) {
					final CUnit selectedPlayerUnit = neutralBuildingData.getSelectedPlayerUnit(this.localPlayerIndex);
					if (selectedPlayerUnit != null) {
						controlShared = true;
					}
					else {
						requiresPatron = true;
					}
				}
			}
			if (!controlShared && !requiresPatron) {
				return;
			}
		}
		ability.checkCanUse(this.game, this.unit, orderId, this.previewCallback.reset());
		if (!this.previewCallback.omitIconEntirely) {
			if (requiresPatron) {
				this.previewCallback
						.missingRequirement(this.gameUI.getTemplates().getDecoratedString("REQUIRESNEARPATRON"));
			}
			final boolean active = (this.unit.getCurrentBehavior() != null)
					&& (orderId == this.unit.getCurrentBehavior().getHighlightOrderId());
			final boolean disabled = ((ability != null) && ability.isDisabled()) || this.previewCallback.disabled;
			final float cooldownRemaining = this.previewCallback.cooldownRemaining;
			final float cooldownMax = this.previewCallback.cooldownMax;
			if (disabled) {
				// dont show these on disabled
				goldCost = 0;
				lumberCost = 0;
				foodCost = 0;
			}
			if (this.previewCallback.isShowingRequirements()) {
				uberTip = this.previewCallback.getRequirementsText() + "|r" + uberTip;
			}
			this.commandButtonListener.commandButton(buttonPosX, buttonPosY, disabled ? iconDisabled : icon, handleId,
					disabled ? 0 : orderId, autoCastOrderId, active, autoCastActive, menuButton, toolTip, uberTip,
					hotkey, goldCost, lumberCost, foodCost, manaCost, cooldownRemaining, cooldownMax, numberOverlay);
		}

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
								goldCost, lumberCost, simulationUnitType.getFoodUsed(), 0,
								playerHero.getHeroData().getHeroLevel());
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
					int goldCost, lumberCost;
					if (simulationUnitType.isHero()
							&& (this.game.getPlayer(this.unit.getPlayerIndex()).getHeroTokens() > 0)) {
						goldCost = 0;
						lumberCost = 0;
					}
					else {
						goldCost = simulationUnitType.getGoldCost();
						lumberCost = simulationUnitType.getLumberCost();
					}
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false,
							goldCost, lumberCost, simulationUnitType.getFoodUsed(), 0, -1);
				}
			}
			for (final War3ID unitType : ability.getResearchesAvailable()) {
				final CPlayer player = this.game.getPlayer(this.unit.getPlayerIndex());
				final int unlockCount = player.getTechtreeUnlocked(unitType);
				final IconUI unitUI = this.abilityDataUI.getUpgradeUI(unitType, unlockCount);
				if (unitUI != null) {
					final CUpgradeType simulationUpgradeType = this.game.getUpgradeData().getType(unitType);
					final int goldCost = simulationUpgradeType.getGoldCost(unlockCount);
					final int lumberCost = simulationUpgradeType.getLumberCost(unlockCount);
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false,
							goldCost, lumberCost, 0, 0, -1);
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
	public Void accept(final CAbilitySellItems ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			int itemIndex = 0;
			for (final War3ID unitType : ability.getItemsSold()) {
				final IconUI unitUI = this.abilityDataUI.getItemUI(unitType).getIconUI();
				if (unitUI != null) {
					final CItemType simulationUnitType = this.game.getItemData().getItemType(unitType);
					int goldCost, lumberCost;
					goldCost = simulationUnitType.getGoldCost();
					lumberCost = simulationUnitType.getLumberCost();
					addCommandButton(ability, unitUI, ability.getHandleId(), this.localPlayerIndex | (itemIndex << 8),
							0, false, false, goldCost, lumberCost, 0, 0, -1);
				}
				itemIndex++;
			}
		}
		return null;
	}

	@Override
	public Void accept(final CAbilityUpgrade ability) {
		if ((this.menuBaseOrderId == 0) && ability.isIconShowing()) {
			for (final War3ID unitType : ability.getUpgradesTo()) {
				final IconUI unitUI = this.abilityDataUI.getUnitUI(unitType);
				if (unitUI != null) {
					int relativeOffsetGold;
					int relativeOffsetLumber;
					final CUnitType existingUnitType = this.unit.getUnitType();
					if (this.game.getGameplayConstants().isRelativeUpgradeCosts()) {
						relativeOffsetGold = existingUnitType.getGoldCost();
						relativeOffsetLumber = existingUnitType.getLumberCost();
					}
					else {
						relativeOffsetGold = 0;
						relativeOffsetLumber = 0;
					}
					final CUnitType simulationUnitType = this.game.getUnitData().getUnitType(unitType);
					addCommandButton(ability, unitUI, ability.getHandleId(), unitType.getValue(), 0, false, false,
							simulationUnitType.getGoldCost() - relativeOffsetGold,
							simulationUnitType.getLumberCost() - relativeOffsetLumber,
							simulationUnitType.getFoodUsed() - existingUnitType.getFoodUsed(), 0, -1);
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
		private boolean omitIconEntirely;
		private boolean notEnoughMana;
		private final StringBuilder requirementsTextBuilder = new StringBuilder();
		private float cooldownRemaining;
		private float cooldownMax;

		public CommandCardActivationReceiverPreviewCallback reset() {
			this.disabled = false;
			this.omitIconEntirely = false;
			this.cooldownRemaining = 0;
			this.requirementsTextBuilder.setLength(0);
			return this;
		}

		@Override
		public void useOk() {

		}

		@Override
		public void unknownReasonUseNotOk() {

		}

		@Override
		public void notEnoughResources(final ResourceType resource) {
			if (resource == ResourceType.MANA) {
				this.notEnoughMana = true;
			}
		}

		@Override
		public void notAnActiveAbility() {

		}

		@Override
		public void missingRequirement(final War3ID type, final int level) {
			final CUnitType unitType = CommandCardPopulatingAbilityVisitor.this.game.getUnitData().getUnitType(type);
			String requirementString;
			if (unitType != null) {
				requirementString = unitType.getName();
			}
			else {
				final CUpgradeType upgradeType = CommandCardPopulatingAbilityVisitor.this.game.getUpgradeData()
						.getType(type);
				if (upgradeType != null) {
					final UpgradeLevel upgradeLevel = upgradeType.getLevel(level - 1);
					if (upgradeLevel != null) {
						requirementString = upgradeLevel.getName();
					}
					else {
						requirementString = "NOTEXTERN Unknown Level of Upgrade ('" + type + "')";
					}
				}
				else {
					requirementString = "NOTEXTERN Unknown ('" + type + "')";
				}
			}
			missingRequirement(requirementString);
		}

		@Override
		public void missingHeroLevelRequirement(final int level) {
			missingRequirement(String.format(CommandCardPopulatingAbilityVisitor.this.gameUI.getTemplates()
					.getDecoratedString("INFOPANEL_LEVEL").replace("%u", "%d"), level));
		}

		public void missingRequirement(final String requirementString) {
			this.disabled = true;
			if (this.requirementsTextBuilder.length() == 0) {
				this.requirementsTextBuilder.append(CommandCardPopulatingAbilityVisitor.this.gameUI.getTemplates()
						.getDecoratedString("REQUIRESTOOLTIP"));
				this.requirementsTextBuilder.append("|n - ");
			}
			else {
				this.requirementsTextBuilder.append(" - ");
			}
			this.requirementsTextBuilder.append(requirementString);
			this.requirementsTextBuilder.append("|n");
		}

		@Override
		public void noHeroSkillPointsAvailable() {
			this.disabled = true;
		}

		@Override
		public void techtreeMaximumReached() {
			this.omitIconEntirely = true;
		}

		@Override
		public void techItemAlreadyInProgress() {
			this.omitIconEntirely = true;
		}

		@Override
		public void casterMovementDisabled() {

		}

		@Override
		public void cargoCapacityUnavailable() {
		}

		@Override
		public void alreadyFullHealth() {
		}

		@Override
		public void disabled() {
			this.disabled = true;
		}

		@Override
		public void cooldownNotYetReady(final float cooldownRemaining, final float cooldownMax) {
			this.cooldownRemaining = cooldownRemaining;
			this.cooldownMax = cooldownMax;

		}

		@Override
		public void noChargesRemaining() {

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
		if ((this.menuBaseOrderId == OrderIds.skillmenu) && ability.isIconShowing()) {
			for (final War3ID unitType : ability.getSkillsAvailable()) {
				final AbilityUI abilityUI = this.abilityDataUI.getUI(unitType);
				if (abilityUI != null) {
					final int nextLevel = this.unit.getAbilityLevel(unitType) + 1;
					final IconUI learnIconUI = abilityUI.getLearnIconUI();
					addCommandButton(ability, learnIconUI, String.format(learnIconUI.getToolTip(), nextLevel),
							learnIconUI.getButtonPositionX(), learnIconUI.getButtonPositionY(), ability.getHandleId(),
							unitType.getValue(), 0, false, false, 0, 0, 0, 0, nextLevel);
				}
			}
		}
		else {
			if (this.multiSelect) {
				return null;
			}
			if (this.menuBaseOrderId == 0) {
				final int skillPoints = ability.getSkillPoints();
				addCommandButton(ability, this.abilityDataUI.getSelectSkillUI(), ability.getHandleId(),
						OrderIds.skillmenu, 0, false, true, 0, 0, 0, 0, skillPoints != 0 ? skillPoints : -1);
			}
		}
		return null;
	}
}
