package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CEffect;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CWeaponSoundTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponentLightning;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class CAbilityManaBurn extends CAbilityTargetSpellBase {
	private War3ID lightningId;
	private float maxManaDrained;
	private float boltDelay;
	private float boltLifetime;

	public CAbilityManaBurn(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.manaburn;
	}

	@Override
	public void populateData(final GameObject worldEditorAbility, final int level) {
		this.lightningId = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level);
		this.maxManaDrained = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_A + level, 0);
		this.boltDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_B + level, 0);
		this.boltLifetime = worldEditorAbility.getFieldAsFloat(AbilityFields.DATA_C + level, 0);
	}

	@Override
	protected void innerCheckCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			if (targetUnit.getMana() > 0) {
				super.innerCheckCanTarget(game, unit, orderId, target, receiver);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_MANA);
			}
		}
		else {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
		}
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit caster, final AbilityTarget target) {
		final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningId,
					targetUnit);
			simulation.createTemporarySpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
			final int boltDelayEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(boltDelay / WarsmashConstants.SIMULATION_STEP_TIME);
			final int boltLifetimeEndTick = simulation.getGameTurnTick()
					+ (int) StrictMath.ceil(boltLifetime / WarsmashConstants.SIMULATION_STEP_TIME);
			simulation.registerEffect(new CEffectManaBurnBolt(boltLifetimeEndTick, boltDelayEndTick,
					this.maxManaDrained, caster, targetUnit, lightning));
		}
		return false;
	}

	private static final class CEffectManaBurnBolt implements CEffect {

		private final int boltLifetimeEndTick;
		private final float maxManaDrained;
		private final CUnit caster;
		private final CUnit targetUnit;
		private final SimulationRenderComponentLightning boltFx;
		private int boltDelayEndTick;

		public CEffectManaBurnBolt(final int boltLifetimeEndTick, final int boltDelayEndTick,
				final float maxManaDrained, final CUnit caster, final CUnit targetUnit,
				final SimulationRenderComponentLightning boltFx) {
			this.boltLifetimeEndTick = boltLifetimeEndTick;
			this.boltDelayEndTick = boltDelayEndTick;
			this.maxManaDrained = maxManaDrained;
			this.caster = caster;
			this.targetUnit = targetUnit;
			this.boltFx = boltFx;
		}

		@Override
		public boolean update(final CSimulation game) {
			final int gameTurnTick = game.getGameTurnTick();
			if (gameTurnTick >= boltDelayEndTick) {
				final float targetMana = targetUnit.getMana();
				final float manaDamage = StrictMath.min(targetMana, this.maxManaDrained);
				targetUnit.setMana(targetMana - manaDamage);
				targetUnit.damage(game, caster, false, true, CAttackType.SPELLS, CDamageType.FIRE,
						CWeaponSoundTypeJass.WHOKNOWS.name(), manaDamage);
				game.spawnTextTag(targetUnit, caster.getPlayerIndex(), TextTagConfigType.MANA_BURN, (int) manaDamage);
				this.boltDelayEndTick = Integer.MAX_VALUE;
			}
			final boolean done = gameTurnTick >= boltLifetimeEndTick;
			if (done) {
				boltFx.remove();
			}
			return done;
		}
	}
}
