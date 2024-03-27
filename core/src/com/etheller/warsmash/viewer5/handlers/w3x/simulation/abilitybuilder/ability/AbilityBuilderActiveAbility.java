package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public interface AbilityBuilderActiveAbility extends AbilityBuilderAbility, CAutocastAbility, GenericSingleIconActiveAbility {
	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData();

	public AbilityBuilderConfiguration getConfig();

	public Map<String, Object> getLocalStore();
	
	public int getChargedManaCost();
	public void setCastRange(float castRange);

	public int getOffOrderId();
	
	public PrimaryTag getCastingPrimaryTag();
	
	public EnumSet<SecondaryTag> getCastingSecondaryTags();

	public void activate(final CSimulation game, final CUnit caster);
	
	public void deactivate(final CSimulation game, final CUnit caster);
	
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver);

	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver);

	public void runOnOrderIssuedActions(CSimulation game, CUnit caster, int orderId);

	public void runBeginCastingActions(CSimulation game, CUnit caster, int orderId);

	public void runEndCastingActions(CSimulation game, CUnit caster, int orderId);

	public void runChannelTickActions(CSimulation game, CUnit caster, int orderId);

	public void runEndChannelActions(CSimulation game, CUnit caster, int orderId);

	public void runCancelPreCastActions(CSimulation game, CUnit caster, int orderId);

	public boolean isSeparateOnAndOff();

	War3ID getOnTooltipOverride();

	War3ID getOffTooltipOverride();

}
