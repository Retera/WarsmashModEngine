package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.moonwell;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver.TargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CAbilityMoonWell extends CAbilitySpellBase {
	private boolean autoCastActive = false;

	private SimulationRenderComponent waterRenderComponent;

	private float manaGained;
	private float hitPointsGained;
	private float autocastRequirement;
	private float waterHeight;
	private boolean regenerateOnlyAtNight;
	private boolean manaRegenActive = false;
	private float baseManaRegen;
	private int lastAutoCastCheckTick = 0;

	private final Rectangle recycleRect = new Rectangle();

	public CAbilityMoonWell(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		baseManaRegen = unit.getManaRegen();
		unit.setManaRegen(0);
		manaRegenActive = false;
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		waterRenderComponent.remove();
		enableManaRegen(unit);
	}

	private void disableManaRegen(final CUnit unit) {
		if (manaRegenActive) {
			unit.setManaRegen(0);
			manaRegenActive = false;
		}
	}

	private void enableManaRegen(final CUnit unit) {
		if (!manaRegenActive) {
			unit.setManaRegen(baseManaRegen);
			manaRegenActive = true;
		}
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (waterRenderComponent == null) {
			if (!isDisabled()) {
				final CUnitRace unitRace = unit.getUnitType().getRace();
				waterRenderComponent = game.spawnSpellEffectOnPoint(unit.getX(), unit.getY(), 0, getAlias(),
						CEffectType.EFFECT, unitRace.ordinal());
			}
		}
		else {
			if (isDisabled()) {
				waterRenderComponent.remove();
			}
			else {
				waterRenderComponent.setHeight(waterHeight * (unit.getMana() / unit.getMaximumMana()));
			}
		}
		if (regenerateOnlyAtNight) {
			if (game.isNight()) {
				enableManaRegen(unit);
			}
			else {
				disableManaRegen(unit);
			}
		}
		if (autoCastActive) {
			final int gameTurnTick = game.getGameTurnTick();
			if ((gameTurnTick >= lastAutoCastCheckTick) && (unit.getMana() > autocastRequirement)) {
				checkAutoCast(game, unit);
				lastAutoCastCheckTick = gameTurnTick + (int) (2.0f / WarsmashConstants.SIMULATION_STEP_TIME);
			}
		}
		super.onTick(game, unit);
	}

	private void checkAutoCast(final CSimulation game, final CUnit unit) {
		final float castRange = getCastRange();
		game.getWorldCollision().enumUnitsInRect(
				recycleRect.set(unit.getX() - castRange, unit.getY() - castRange, castRange * 2, castRange * 2),
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
			if ((lifeWanted > 0) && (availableCasterMana > 0)) {
				final float availableLifeOffered = availableCasterMana / hitPointsGained;
				final float lifeGained = Math.min(availableLifeOffered, lifeWanted);
				unitTarget.heal(game, lifeGained);
				availableCasterMana -= lifeGained * hitPointsGained;
			}
			if ((manaWanted > 0) && (availableCasterMana > 0)) {
				final float availableManaOffered = availableCasterMana / manaGained;
				final float manaGained = Math.min(availableManaOffered, manaWanted);
				unitTarget.setMana(mana + manaGained);
				availableCasterMana -= manaGained * this.manaGained;
			}
			if (availableCasterMana != caster.getMana()) {
				caster.setMana(availableCasterMana);
				game.createSpellEffectOnUnit(caster, getAlias(), CEffectType.CASTER);
				game.createSpellEffectOnUnit(unitTarget, getAlias(), CEffectType.SPECIAL);
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
	public void populateData(final MutableGameObject worldEditorAbility, final int level) {
		manaGained = worldEditorAbility.getFieldAsFloat(AbilityFields.MOON_WELL_MANA_GAINED, level);
		hitPointsGained = worldEditorAbility.getFieldAsFloat(AbilityFields.MOON_WELL_HIT_POINTS_GAINED, level);
		autocastRequirement = worldEditorAbility.getFieldAsFloat(AbilityFields.MOON_WELL_AUTOCAST_REQUIREMENT, level);
		waterHeight = worldEditorAbility.getFieldAsFloat(AbilityFields.MOON_WELL_WATER_HEIGHT, level);
		regenerateOnlyAtNight = worldEditorAbility.getFieldAsBoolean(AbilityFields.MOON_WELL_REGENERATE_ONLY_AT_NIGHT,
				level);

	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		return false;
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (target.canBeTargetedBy(game, unit, getTargetsAllowed())) {
			if (!unit.isMovementDisabled() || unit.canReach(target, getCastRange())) {
				receiver.targetOk(target);
			}
			else {
				receiver.targetOutsideRange();
			}
		}
		else {
			receiver.mustTargetType(TargetType.UNIT);
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
	public boolean isAutoCastOn() {
		return autoCastActive;
	}

	@Override
	public void setAutoCastOn(final boolean autoCastOn) {
		this.autoCastActive = autoCastOn;
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

}
