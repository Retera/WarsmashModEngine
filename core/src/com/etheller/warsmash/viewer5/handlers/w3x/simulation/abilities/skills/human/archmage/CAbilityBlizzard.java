package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.human.archmage;

import java.util.EnumSet;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilityPointTargetSpellBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CAbilityBlizzard extends CAbilityPointTargetSpellBase {
	private float buildingReduction;
	private float damage;
	private float damagePerSecond;
	private float maximumDamagePerWave;
	private int shardCount;
	private int waveCount;
	private float waveDelay;
	private float areaOfEffect;
	private War3ID effectId;

	public CAbilityBlizzard(int handleId, War3ID alias, int manaCost, float castRange, float cooldown,
			float castingTime, EnumSet<CTargetType> targetsAllowed, PrimaryTag castingPrimaryTag,
			EnumSet<SecondaryTag> castingSecondaryTags, float buildingReduction, float damage, float damagePerSecond,
			float maximumDamagePerWave, int shardCount, int waveCount, float waveDelay, float areaOfEffect,
			War3ID effectId) {
		super(handleId, alias);
		this.buildingReduction = buildingReduction;
		this.damage = damage;
		this.damagePerSecond = damagePerSecond;
		this.maximumDamagePerWave = maximumDamagePerWave;
		this.shardCount = shardCount;
		this.waveCount = waveCount;
		this.waveDelay = waveDelay;
		this.areaOfEffect = areaOfEffect;
		this.effectId = effectId;
	}

	@Override
	public void populateData(MutableGameObject worldEditorAbility, int level) {

	}

	@Override
	public int getBaseOrderId() {
		return OrderIds.blizzard;
	}

	@Override
	public boolean doEffect(CSimulation simulation, CUnit unit, AbilityTarget target) {
		// TODO Auto-generated method stub
		return false;
	}

}
