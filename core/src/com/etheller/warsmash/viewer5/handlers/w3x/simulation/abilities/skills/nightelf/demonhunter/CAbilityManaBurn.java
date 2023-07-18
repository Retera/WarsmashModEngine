package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.nightelf.demonhunter;

import com.etheller.warsmash.units.manager.MutableObjectData;
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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
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

	public CAbilityManaBurn(int handleId, War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.manaburn;
	}

	@Override
	public void populateData(MutableObjectData.MutableGameObject worldEditorAbility, int level) {
		this.lightningId = AbstractCAbilityTypeDefinition.getLightningId(worldEditorAbility, level);
		this.maxManaDrained = worldEditorAbility.getFieldAsFloat(AbilityFields.ManaBurn.MAX_MANA_DRAINED, level);
		this.boltDelay = worldEditorAbility.getFieldAsFloat(AbilityFields.ManaBurn.BOLT_DELAY, level);
		this.boltLifetime = worldEditorAbility.getFieldAsFloat(AbilityFields.ManaBurn.BOLT_LIFETIME, level);
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
									   AbilityTargetCheckReceiver<CWidget> receiver) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
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
	public boolean doEffect(CSimulation simulation, CUnit caster, AbilityTarget target) {
		CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			SimulationRenderComponentLightning lightning = simulation.createLightning(caster, lightningId, targetUnit);
			simulation.createSpellEffectOnUnit(targetUnit, getAlias(), CEffectType.TARGET);
			int boltDelayEndTick =
					simulation.getGameTurnTick() + (int) StrictMath.ceil(boltDelay / WarsmashConstants.SIMULATION_STEP_TIME);
			int boltLifetimeEndTick =
					simulation.getGameTurnTick() + (int) StrictMath.ceil(boltLifetime / WarsmashConstants.SIMULATION_STEP_TIME);
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

		public CEffectManaBurnBolt(int boltLifetimeEndTick, int boltDelayEndTick, float maxManaDrained, CUnit caster,
								   CUnit targetUnit, SimulationRenderComponentLightning boltFx) {
			this.boltLifetimeEndTick = boltLifetimeEndTick;
			this.boltDelayEndTick = boltDelayEndTick;
			this.maxManaDrained = maxManaDrained;
			this.caster = caster;
			this.targetUnit = targetUnit;
			this.boltFx = boltFx;
		}

		@Override
		public boolean update(CSimulation game) {
			int gameTurnTick = game.getGameTurnTick();
			if (gameTurnTick >= boltDelayEndTick) {
				float targetMana = targetUnit.getMana();
				float manaDamage = StrictMath.min(targetMana, this.maxManaDrained);
				targetUnit.setMana(targetMana - manaDamage);
				targetUnit.damage(game, caster, CAttackType.SPELLS, CDamageType.FIRE,
						CWeaponSoundTypeJass.WHOKNOWS.name(), manaDamage);
				game.spawnTextTag(targetUnit, caster.getPlayerIndex(), TextTagConfigType.MANA_BURN, (int) manaDamage);
				this.boltDelayEndTick = Integer.MAX_VALUE;
			}
			boolean done = gameTurnTick >= boltLifetimeEndTick;
			if (done) {
				boltFx.remove();
			}
			return done;
		}
	}
}
