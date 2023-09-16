package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityAbilityBuilderActiveAutoTarget extends AbstractGenericSingleIconNoSmartActiveAbility implements AbilityBuilderAbility {
	private CBehaviorAbilityBuilderBase behavior;

	List<CAbilityTypeAbilityBuilderLevelData> levelData;
	private AbilityBuilderConfiguration config;
	private Map<String, Object> localStore;
	private int orderId;
	private int autoCastOnId = 0;
	private int autoCastOffId = 0;
	private boolean autocasting = false;
	private PrimaryTag castingPrimaryTag;
	private EnumSet<SecondaryTag> castingSecondaryTags;

	private float cooldown = 0;
	private int manaCost = 0;

	private int castId = 0;
	
	public CAbilityAbilityBuilderActiveAutoTarget(int handleId, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();

		GameObject editorData = (GameObject) localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA);
		final String animNames = editorData.getField(AbilityFields.ANIM_NAMES);
		final EnumSet<AnimationTokens.PrimaryTag> primaryTags = EnumSet.noneOf(AnimationTokens.PrimaryTag.class);
		this.castingSecondaryTags = EnumSet.noneOf(AnimationTokens.SecondaryTag.class);
		Sequence.populateTags(primaryTags, this.castingSecondaryTags, animNames);
		if (primaryTags.isEmpty()) {
			this.castingPrimaryTag = null;
		} else {
			this.castingPrimaryTag = primaryTags.iterator().next();
		}
		if (this.castingSecondaryTags.isEmpty()) {
			this.castingSecondaryTags = SequenceUtils.SPELL;
		}
	}
	
	@Override
	public void setLevel(int level) {
		super.setLevel(level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();
		CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
		CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(game, unit, this.localStore, castId);
			}
		}
	}

	@Override
	public void startCooldown(CSimulation game, CUnit unit) {
		unit.beginCooldown(game, getAlias(), this.cooldown);
	}

	@Override
	public int getBaseOrderId() {
		return this.orderId;
	}

	@Override
	public int getUIManaCost() {
		return this.manaCost;
	}

	public PrimaryTag getCastingPrimaryTag() {
		return this.castingPrimaryTag;
	}

	public EnumSet<SecondaryTag> getCastingSecondaryTags() {
		return this.castingSecondaryTags;
	}

	public List<CAbilityTypeAbilityBuilderLevelData> getLevelData() {
		return this.levelData;
	}

	public AbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	public Map<String, Object> getLocalStore() {
		return this.localStore;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.FLEXABILITY, this);
		this.behavior = new CBehaviorAbilityBuilderBase(unit, config, localStore, this);
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	protected void innerCheckCanUse(CSimulation game, CUnit unit, int orderId, AbilityActivationReceiver receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.useOk();
			return;
		}
		final float cooldownRemaining = unit.getCooldownRemainingTicks(game, getAlias());
		if (cooldownRemaining > 0) {
			final float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getAlias())
					* WarsmashConstants.SIMULATION_STEP_TIME;
			receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME, cooldownLengthDisplay);
		} else if (unit.getMana() < this.manaCost) {
			receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
		} else {
			innerCheckCanUseSpell(game, unit, orderId, receiver);
		}
	}

	protected void innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (config.getExtraCastConditions() != null) {
			boolean result = true;
			for (ABCondition condition : config.getExtraCastConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.useOk();
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.activationCheckFailed(failReason);
				} else {
					receiver.unknownReasonUseNotOk();
				}
			}
		} else {
			receiver.useOk();
		}
	}

	@Override
	public int getAutoCastOnOrderId() {
		return this.autoCastOnId;
	}

	@Override
	public int getAutoCastOffOrderId() {
		return this.autoCastOffId;
	}

	@Override
	public boolean isAutoCastOn() {
		return this.autocasting;
	}

	@Override
	public void setAutoCastOn(final boolean autoCastOn) {
		this.autocasting = autoCastOn;
	}

	@Override
	public CBehavior beginNoTarget(CSimulation game, CUnit caster, int orderId) {
		castId++;
		CWidget target = autoTarget(game, caster);
		if (target != null) {
			this.behavior.setCastId(castId);
			return this.behavior.reset(target);
		} else {
			return null;
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		receiver.targetOk(null);
	}
	
	public CWidget autoTarget(CSimulation game, CUnit caster) {
		CWidget target = null;

		if (this.config.getSpecialFields() != null && this.config.getSpecialFields().getAutoAquireTarget() != null) {
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			
			for (ABAction action : this.config.getSpecialFields().getAutoAquireTarget()) {
				action.runAction(game, caster, this.localStore, castId);
			}
			
			target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
			if (target == null) {
				target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
				if (target == null) {
					target = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
				}
			}
		}
		return target;
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {

		CWidget prevTarget = null;
		prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId);
		if (prevTarget == null) {
			prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId);
			if (prevTarget == null) {
				prevTarget = (CWidget) this.localStore.get(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId);
			}
		}
		if (target == prevTarget) {
			innerCheckCanTargetSpell(game, unit, orderId, prevTarget, receiver);
		} else {
			receiver.orderIdNotAccepted();
		}
	}


	protected void innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (this.config.getExtraTargetConditions() != null) {
			final CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + castId, targetUnit);
			final CDestructable targetDest = target.visit(AbilityTargetVisitor.DESTRUCTABLE);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + castId, targetDest);
			final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
			this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + castId, targetItem);
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, castId);
			}
			if (result) {
				receiver.targetOk(targetUnit);
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					receiver.targetCheckFailed(failReason);
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
				}
			}
		} else {
			receiver.targetOk(target);
		}
	}
	


	// Unused

	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, CWidget target) {
		return null;
	}
	
	@Override
	public CBehavior begin(CSimulation game, CUnit caster, int orderId, AbilityPointTarget point) {
		return null;
	}

	
	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public boolean isToggleOn() {
		return false;
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {
	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {
	}
}
