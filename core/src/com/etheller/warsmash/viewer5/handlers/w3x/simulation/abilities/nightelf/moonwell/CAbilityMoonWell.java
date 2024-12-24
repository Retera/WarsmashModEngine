package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.moonwell;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentModel;

public class CAbilityMoonWell extends CAbilitySpellBase implements CAutocastAbility {
	private boolean autoCastActive = false;

	private SimulationRenderComponentModel waterRenderComponent;

	private float manaGained;
	private float hitPointsGained;
	private float autocastRequirement;
	private float waterHeight;
	private boolean regenerateOnlyAtNight;
	private boolean manaRegenActive = false;
	private float baseManaRegen;
	private int lastAutoCastCheckTick = 0;

	private final Rectangle recycleRect = new Rectangle();

	private float areaOfEffect;

	public CAbilityMoonWell(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.baseManaRegen = unit.getManaRegen();
		unit.setManaRegen(0);
		this.manaRegenActive = false;
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		removeWaterRenderComponent();
		enableManaRegen(unit);
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		removeWaterRenderComponent();
	}

	private void disableManaRegen(final CUnit unit) {
		if (this.manaRegenActive) {
			unit.setManaRegen(0);
			this.manaRegenActive = false;
		}
	}

	private void enableManaRegen(final CUnit unit) {
		if (!this.manaRegenActive) {
			unit.setManaRegen(this.baseManaRegen);
			this.manaRegenActive = true;
		}
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (this.waterRenderComponent == null) {
			if (!isDisabled()) {
				final CUnitRace unitRace = unit.getUnitType().getRace();
				this.waterRenderComponent = game.spawnSpellEffectOnPoint(unit.getX(), unit.getY(), 0, getAlias(),
						CEffectType.EFFECT, unitRace.ordinal());
			}
		}
		else {
			if (isDisabled()) {
				removeWaterRenderComponent();
			}
			else {
				this.waterRenderComponent.setHeight(this.waterHeight * (unit.getMana() / unit.getMaximumMana()));
			}
		}
		if (this.regenerateOnlyAtNight) {
			if (game.isNight()) {
				enableManaRegen(unit);
			}
			else {
				disableManaRegen(unit);
			}
		}
		if (this.autoCastActive) {
			final int gameTurnTick = game.getGameTurnTick();
			if (gameTurnTick >= this.lastAutoCastCheckTick && unit.getMana() > this.autocastRequirement) {
				checkAutoCast(game, unit);
				this.lastAutoCastCheckTick = gameTurnTick + (int) (2.0f / WarsmashConstants.SIMULATION_STEP_TIME);
			}
		}
		super.onTick(game, unit);
	}

	private void removeWaterRenderComponent() {
		if (this.waterRenderComponent != null) {
			this.waterRenderComponent.remove();
			this.waterRenderComponent = null;
		}
	}

	private void checkAutoCast(final CSimulation game, final CUnit unit) {
		final float castRange = getCastRange();
		game.getWorldCollision().enumUnitsInRect(
				this.recycleRect.set(unit.getX() - castRange, unit.getY() - castRange, castRange * 2, castRange * 2),
				new CUnitEnumFunction() {
					@Override
					public boolean call(final CUnit enumUnit) {
						if (unit.canReach(enumUnit, castRange)
								&& enumUnit.canBeTargetedBy(game, unit, getTargetsAllowed())) {
							unit.order(game, getBaseOrderId(), enumUnit);
						}
						return false;
					}
				});
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		final CUnit unitTarget = target.visit(AbilityTargetVisitor.UNIT);
		if (unitTarget != null) {
			final float life = unitTarget.getLife();
			final int maximumLife = unitTarget.getMaximumLife();
			final float mana = unitTarget.getMana();
			final int maximumMana = unitTarget.getMaximumMana();
			final float lifeWanted = life > maximumLife ? 0 : maximumLife - life;
			final float manaWanted = mana > maximumMana ? 0 : maximumMana - mana;
			float availableCasterMana = caster.getMana();
			if (lifeWanted > 0 && availableCasterMana > 0) {
				final float availableLifeOffered = availableCasterMana / this.hitPointsGained;
				final float lifeGained = Math.min(availableLifeOffered, lifeWanted);
				unitTarget.heal(game, lifeGained);
				availableCasterMana -= lifeGained * this.hitPointsGained;
			}
			if (manaWanted > 0 && availableCasterMana > 0) {
				final float availableManaOffered = availableCasterMana / this.manaGained;
				final float manaGained = Math.min(availableManaOffered, manaWanted);
				unitTarget.setMana(mana + manaGained);
				availableCasterMana -= manaGained * this.manaGained;
			}
			if (availableCasterMana != caster.getMana()) {
				caster.setMana(availableCasterMana);
				game.createTemporarySpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
				game.createTemporarySpellEffectOnUnit(unitTarget, getAlias(), CEffectType.SPECIAL);
			}
		}
		return caster.pollNextOrderBehavior(game);
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
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.manaGained = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.hitPointsGained = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.autocastRequirement = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
		this.waterHeight = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_D + level, 0);
		this.regenerateOnlyAtNight = worldEditorAbility.getFieldAsBoolean(AbilityFields.DATA_E + level, 0);
		this.areaOfEffect = worldEditorAbility.getFieldAsFloat(AbilityFields.AREA_OF_EFFECT + level, 0);
		setCastRange(this.areaOfEffect); // TODO use cast range as a smart right click interact radius
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		return false;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target.canBeTargetedBy(game, unit, getTargetsAllowed(), receiver)) {
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				receiver.targetOk(target);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		}
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public AutocastType getAutocastType() {
		return AutocastType.NEARESTVALID;
	}

	@Override
	public boolean isAutoCastOn() {
		return this.autoCastActive;
	}

	@Override
	public void setAutoCastOn(final CUnit caster, final boolean autoCastOn) {
		this.autoCastActive = autoCastOn;
		caster.setAutocastAbility(autoCastOn ? this : null);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.replenish;
	}

	@Override
	public int getAutoCastOnOrderId() {
		return OrderIds.replenishon;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return OrderIds.replenishoff;
	}

	@Override
	public void setAutoCastOff() {
		this.autoCastActive = false;
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		this.checkCanTarget(game, unit, orderId, target, receiver);
	}

	@Override
	public void checkCanAutoTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanAutoTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

}
