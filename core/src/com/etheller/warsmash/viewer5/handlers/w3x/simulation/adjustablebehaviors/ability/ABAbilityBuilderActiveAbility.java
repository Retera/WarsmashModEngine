package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public interface ABAbilityBuilderActiveAbility extends ABAbilityBuilderAbility, CAutocastAbility, GenericSingleIconActiveAbility {
	public List<ABAbilityBuilderAbilityTypeLevelData> getLevelData();

	public ABAbilityBuilderConfiguration getConfig();

	public ABLocalDataStore getLocalStore();
	
	public int getChargedManaCost();
	public void setCastRange(float castRange);
	public boolean ignoreCastTime();
	
	public EnumSet<CTargetType> getTargetsAllowed();

	public int getOffOrderId();
	
	public PrimaryTag getCastingPrimaryTag();
	
	public EnumSet<SecondaryTag> getCastingSecondaryTags();

	public void activate(final CSimulation game, final CUnit caster);
	
	public void deactivate(final CSimulation game, final CUnit caster);
	
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver);

	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			boolean autoOrder, final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			boolean autoOrder, final AbilityTargetCheckReceiver<Void> receiver);

	public void internalBegin(CSimulation game, CUnit theCaster, int orderId, boolean b, AbilityTarget theTarget);

	public void runOnOrderIssuedActions(CSimulation game, CUnit caster, int orderId);

	public void runBeginCastingActions(CSimulation game, CUnit caster, int orderId);

	public void runEndCastingActions(CSimulation game, CUnit caster, int orderId);

	public void runChannelTickActions(CSimulation game, CUnit caster, int orderId);

	public void runEndChannelActions(CSimulation game, CUnit caster, int orderId);

	public void runCancelPreCastActions(CSimulation game, CUnit caster, int orderId);

	public boolean isSeparateOnAndOff();

	War3ID getOnTooltipOverride();

	War3ID getOffTooltipOverride();

	boolean isActive();
	
	public void cleanupInputs();
	public void cleanupInputs(int castId);

	public boolean isMenuAbility();

}
