package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.orc.chieftain;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityNoTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;

public class CAbilityReincarnation extends CAbilityNoTargetSpellBase {
	private float delay;
	private War3ID buffId;
	private float castingTime;

	public CAbilityReincarnation(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	public void populateData(final MutableGameObject worldEditorAbility, final int level) {
		this.delay = worldEditorAbility.getFieldAsFloat(AbilityFields.REINCARNATION_DELAY_1,
				level);
		this.buffId = AbstractCAbilityTypeDefinition.getBuffId(worldEditorAbility, level);
		this.castingTime = worldEditorAbility.getFieldAsFloat(AbilityFields.CASTING_TIME, level);
	}

	// TODO Which order ID?
	@Override
	public int getBaseOrderId() {
		return 0;
	}

	@Override
	public void onBeforeDeath(CSimulation game, CUnit cUnit) {
		heroDies(this, game, cUnit, cUnit.getMaximumLife(), cUnit.getMaximumMana(), delay);
	}

	public static void heroDies(AbstractGenericAliasedAbility ability, CSimulation game, CUnit cUnit, float life, float mana, float delay) {
		// prevent from dying
		cUnit.setLife(game, life);

		if (mana >= 0) {
			cUnit.setMana(mana);
		}

		// hide from map
		cUnit.setHidden(true);
		cUnit.setInvulnerable(true);
		cUnit.setPaused(true);

		// effect
		// TODO How to remove when hero is revived.
		game.createSpellEffectOnUnit(cUnit, ability.getAlias(), CEffectType.CASTER);

		final CTimer revivalTimer = new CTimer() {
			@Override
			public void onFire() {
				cUnit.setHidden(false);
				cUnit.setInvulnerable(false);
				cUnit.setPaused(false);

				// effect
				game.createSpellEffectOnUnit(cUnit, ability.getAlias(), CEffectType.TARGET);
			}
		};
		revivalTimer.setRepeats(false);
		revivalTimer.setTimeoutTime(delay);
		revivalTimer.start(game);
	}

	@Override
	public boolean doEffect(final CSimulation simulation, final CUnit unit, final AbilityTarget target) {
		return false;
	}

	public War3ID getBuffId() {
		return this.buffId;
	}

	public float getCastingTime() {
		return this.castingTime;
	}

	public void setBuffId(final War3ID buffId) {
		this.buffId = buffId;
	}
}
