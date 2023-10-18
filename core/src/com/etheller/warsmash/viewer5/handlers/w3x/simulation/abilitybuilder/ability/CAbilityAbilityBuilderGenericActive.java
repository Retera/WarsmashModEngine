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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ManaDepletedCheckTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;

public abstract class CAbilityAbilityBuilderGenericActive extends AbstractGenericSingleIconNoSmartActiveAbility
		implements AbilityBuilderActiveAbility {
	protected List<CAbilityTypeAbilityBuilderLevelData> levelData;
	protected AbilityBuilderConfiguration config;
	protected Map<String, Object> localStore;
	protected int orderId;
	protected int unorderId = 0;
	protected int autoCastOnId = 0;
	protected int autoCastOffId = 0;
	protected boolean autocasting = false;
	protected boolean toggleable = false;
	protected boolean separateOnAndOff = false;
	protected boolean active = false;
	protected boolean allowCastlessDeactivate = true;
	protected PrimaryTag castingPrimaryTag;
	protected EnumSet<SecondaryTag> castingSecondaryTags;

	protected CItem item = null;

	protected float cooldown = 0;
	protected int manaCost = 0;
	protected float area = Float.NaN;

	protected int bufferMana = 0;
	private ManaDepletedCheckTimer timer;
	private NonStackingStatBuff manaDrain;

	protected int castId = 0;

	public CAbilityAbilityBuilderGenericActive(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		orderId = OrderIdUtils.getOrderId(config.getCastId());
		if (config.getUncastId() != null) {
			unorderId = OrderIdUtils.getOrderId(config.getUncastId());
		}
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}

		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();
		if (levelDataLevel.getArea() > 0) {
			this.area = levelDataLevel.getArea();
		} else {
			this.area = Float.NaN;
		}

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
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		localStore.put(ABLocalStoreKeys.CURRENTLEVEL, level);
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();
		setArea(game, unit);
		determineToggleableFields(game, unit);
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(game, unit, this.localStore, castId);
			}
		}
	}

	private void determineToggleableFields(CSimulation game, CUnit unit) {
		if (config.getDisplayFields() != null && config.getDisplayFields().getSeparateOnAndOff() != null) {
			this.separateOnAndOff = config.getDisplayFields().getSeparateOnAndOff().callback(game, unit, localStore, castId);
		}
		if (config.getDisplayFields() != null && config.getDisplayFields().getToggleable() != null) {
			this.toggleable = config.getDisplayFields().getToggleable().callback(game, unit, localStore, castId);
		}
		if (toggleable && config.getDisplayFields() != null && config.getDisplayFields().getAlternateUnitId() != null) {
			if (unit.getTypeId().equals(config.getDisplayFields().getAlternateUnitId().callback(game, unit, localStore, castId))) {
				this.active = true;
			}
		}
		if (config.getSpecialFields() != null && config.getSpecialFields().getBufferManaRequired() != null) {
			this.bufferMana = config.getSpecialFields().getBufferManaRequired().callback(game, unit, localStore, castId);
		}
		if (this.toggleable) { 
			localStore.put(ABLocalStoreKeys.TOGGLEDABILITY, this);
			int manaPerSec = 0;
			if (config.getSpecialFields() != null && config.getSpecialFields().getManaDrainedPerSecond() != null) {
				manaPerSec = config.getSpecialFields().getManaDrainedPerSecond().callback(game, unit, localStore, castId);
			}
			if (manaPerSec != 0) {
				if (manaDrain == null) {
					manaDrain = new NonStackingStatBuff(NonStackingStatBuffType.MPGEN, NonStackingStatBuff.ALLOW_STACKING_KEY, (-1 * manaPerSec));
				} else {
					manaDrain.setValue((-1 * manaPerSec));
				}
				if (this.timer == null) {
					timer = new ManaDepletedCheckTimer(unit, this);
				}
			} else {
				this.manaDrain = null;
				this.timer = null;
			}
			
		}
	}

	protected void setArea(CSimulation game, CUnit unit) {
		CAbilityTypeAbilityBuilderLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		if (levelDataLevel.getArea() > 0 && (this.config.getDisplayFields() == null
				|| this.config.getDisplayFields().getHideAreaCursor() == null || !this.config.getDisplayFields()
						.getHideAreaCursor().callback(game, unit, localStore, this.getLevel()))) {
			this.area = levelDataLevel.getArea();
		} else {
			this.area = Float.NaN;
		}
	}

	@Override
	public void startCooldown(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			unit.beginCooldown(game, cdID, this.cooldown);
		}
	}
	
	private War3ID getCooldownId() {
		if (this.item != null) {
			if (item.getItemType().isIgnoreCooldown()) {
				return War3ID.NONE;
			} else {
				if (item.getItemType().getCooldownGroup() != null) {
					return item.getItemType().getCooldownGroup();
				}
			}
		}
		return getCode();
	}

	@Override
	public void setItemAbility(final CItem item, int slot) {
		this.item = item;
		this.localStore.put(ABLocalStoreKeys.ITEM, item);
		this.localStore.put(ABLocalStoreKeys.ITEMSLOT, slot);
	}

	@Override
	public int getBaseOrderId() {
		return this.orderId;
	}

	@Override
	public int getOffOrderId() {
		return this.unorderId;
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
	public int getUIManaCost() {
		return this.manaCost + this.bufferMana;
	}
	
	@Override
	public int getChargedManaCost() {
		return this.manaCost;
	}

	@Override
	public float getUIAreaOfEffect() {
		return this.area;
	}

	@Override
	public boolean isSeparateOnAndOff() {
		return separateOnAndOff;
	}

	@Override
	public boolean isToggleOn() {
		return this.toggleable && this.active;
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

	protected ABBehavior createNoTargetBehavior(CUnit unit) {
		ABBehavior beh = new CBehaviorAbilityBuilderNoTarget(unit, localStore, this);
		if (this.item != null || (this.config.getDisplayFields() != null && this.config.getDisplayFields().getInstantCast() != null
				&& this.config.getDisplayFields().getInstantCast().callback(null, unit, localStore, castId))) {
			beh.setInstant(true);
		}
		return beh;
	}

	protected ABBehavior createRangedBehavior(CUnit unit) {
		ABBehavior beh = new CBehaviorAbilityBuilderBase(unit, localStore, this);
		if (this.item != null || (this.config.getDisplayFields() != null && this.config.getDisplayFields().getInstantCast() != null
				&& this.config.getDisplayFields().getInstantCast().callback(null, unit, localStore, castId))) {
			beh.setInstant(true);
		}
		return beh;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.useOk();
			return;
		}
		final int cooldownRemaining = unit.getCooldownRemainingTicks(game, getCooldownId());
		
		if (this.toggleable && this.active) {
			if (cooldownRemaining > 0 && !(receiver instanceof MeleeUIAbilityActivationReceiver)) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME, cooldownLengthDisplay);
			}
			receiver.useOk();
		} else {
			if (cooldownRemaining > 0) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME, cooldownLengthDisplay);
			} else if (unit.getMana() < (this.manaCost + this.bufferMana)) {
				receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
			} else {
				innerCheckExtraCastConditions(game, unit, orderId, receiver);
			}
		}
	}

	protected void innerCheckExtraCastConditions(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (innerCheckCanUseSpell(game, unit, orderId, receiver)) {
			if (config.getExtraCastConditions() != null) {
				boolean result = true;
				for (ABCondition condition : config.getExtraCastConditions()) {
					result = result && condition.evaluate(game, unit, localStore, -1);
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
	}

	protected abstract boolean innerCheckCanUseSpell(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver);

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		} else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		}
		else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}
	
	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.targetOk(null);
		}
		else if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTargetNoTarget(game, unit, orderId, receiver);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
			if (innerCheckTargetInRange(unit, target)) {
				localStore.put(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + -1, target);
				String extraFailReason = innerCheckExtraTargetConditions(game, unit, orderId);
				localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + -1);
				if (extraFailReason != null) {
					if (!extraFailReason.equals("unknown")) {
						receiver.targetCheckFailed(extraFailReason);
					} else {
						receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THERE);
					}
				} else {
					receiver.targetOk(target);
				}
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
			}
		}
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver) {
		if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
			if (innerCheckTargetTargetable(game, unit, target)) {
				if (innerCheckTargetInRange(unit, target)) {
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDUNIT + -1,
							target.visit(AbilityTargetVisitor.UNIT));
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDITEM + -1,
							target.visit(AbilityTargetVisitor.ITEM));
					this.localStore.put(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + -1,
							target.visit(AbilityTargetVisitor.DESTRUCTABLE));
					String extraFailReason = innerCheckExtraTargetConditions(game, unit, orderId);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDUNIT + -1);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDITEM + -1);
					this.localStore.remove(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE + -1);
					if (extraFailReason != null) {
						if (!extraFailReason.equals("unknown")) {
							receiver.targetCheckFailed(extraFailReason);
						} else {
							receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
						}
					} else {
						receiver.targetOk(target);
					}
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_IS_OUTSIDE_RANGE);
				}
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
			}
		}
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (innerCheckCanTargetSpell(game, unit, orderId, receiver)) {
			receiver.targetOk(null);
		}
	}

	protected boolean innerCheckCastOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return (!this.active && orderId == getBaseOrderId()) || ((this.active || this.separateOnAndOff) && orderId == getOffOrderId());
	}

	protected boolean innerCheckTargetTargetable(CSimulation game, CUnit unit, CWidget target) {
		return target.canBeTargetedBy(game, unit, this.levelData.get(this.getLevel() - 1).getTargetsAllowed());
	}

	protected boolean innerCheckTargetInRange(CUnit unit, AbilityTarget target) {
		return !unit.isMovementDisabled()
				|| unit.canReach(target, this.levelData.get(this.getLevel() - 1).getCastRange());
	}

	protected String innerCheckExtraTargetConditions(CSimulation game, CUnit unit, int orderId) {
		if (config.getExtraTargetConditions() != null) {
			boolean result = true;
			for (ABCondition condition : config.getExtraTargetConditions()) {
				result = result && condition.evaluate(game, unit, localStore, -1);
			}
			if (result) {
				return null;
			} else {
				String failReason = (String) localStore.get(ABLocalStoreKeys.CANTUSEREASON);
				if (failReason != null) {
					return failReason;
				} else {
					return "unknown";
				}
			}
		} else {
			return null;
		}
	}

	protected abstract boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId, CWidget target,
			AbilityTargetCheckReceiver<CWidget> receiver);

	protected abstract boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityPointTarget target, AbilityTargetCheckReceiver<AbilityPointTarget> receiver);

	protected abstract boolean innerCheckCanTargetSpell(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver);

	@Override
	public int getUIFoodCost() {
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getFoodCost() != null) {
			CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
			CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
			return this.config.getDisplayFields().getFoodCost().callback(game, unit, localStore, castId);
		}
		return 0;
	}

	@Override
	public int getUIGoldCost() {
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getGoldCost() != null) {
			CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
			CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
			return this.config.getDisplayFields().getGoldCost().callback(game, unit, localStore, castId);
		}
		return 0;
	}

	@Override
	public int getUILumberCost() {
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getLumberCost() != null) {
			CSimulation game = (CSimulation) this.localStore.get(ABLocalStoreKeys.GAME);
			CUnit unit = (CUnit) this.localStore.get(ABLocalStoreKeys.THISUNIT);
			return this.config.getDisplayFields().getLumberCost().callback(game, unit, localStore, castId);
		}
		return 0;
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		localStore.put(ABLocalStoreKeys.GAME, game);
		localStore.put(ABLocalStoreKeys.THISUNIT, unit);
		setArea(game, unit);
		determineToggleableFields(game, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (this.toggleable && this.active) {
			deactivate(game, unit);
		}
		if (config.getOnRemoveAbility() != null) {
			for (ABAction action : config.getOnRemoveAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (this.toggleable && this.active) {
			deactivate(game, unit);
		}
		if (config.getOnDeathPreCast() != null) {
			for (ABAction action : config.getOnDeathPreCast()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}
	
	@Override
	public void runBeginCastingActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnBeginCasting() != null) {
			for (ABAction action : config.getOnBeginCasting()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}
	
	@Override
	public void runEndCastingActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnEndCasting() != null) {
			for (ABAction action : config.getOnEndCasting()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
		if (this.toggleable) { 
			if (orderId == this.getBaseOrderId()) {
				this.activate(game, caster);
			}
			if (orderId == this.getOffOrderId()) {
				this.deactivate(game, caster);
			}
		}
	}
	
	@Override
	public void runChannelTickActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnChannelTick() != null) {
			for (ABAction action : config.getOnChannelTick()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}
	
	@Override
	public void runEndChannelActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnEndChannel() != null) {
			for (ABAction action : config.getOnEndChannel()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}
	
	@Override
	public void runCancelPreCastActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnCancelPreCast() != null) {
			for (ABAction action : config.getOnCancelPreCast()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}
	
	@Override
	public void activate(final CSimulation game, final CUnit caster) {
		System.err.println("Activating!");
		this.active = true;
		if (this.manaDrain != null) {
			this.timer.start(game);
			caster.addNonStackingStatBuff(manaDrain);
		}
		if (config.getOnActivate() != null) {
			for (ABAction action : config.getOnActivate()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public void deactivate(final CSimulation game, final CUnit caster) {
		System.err.println("Deactivating!");
		this.active = false;
		if (this.manaDrain != null) {
			timer.pause(game);
			caster.removeNonStackingStatBuff(manaDrain);
		}
		if (config.getOnDeactivate() != null) {
			for (ABAction action : config.getOnDeactivate()) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		System.err.println("Checking queue top level: " + active + " orderID : " + orderId + " offID: " + this.getOffOrderId());
		if (this.allowCastlessDeactivate && this.toggleable && this.active && orderId == this.getOffOrderId()) {
			this.deactivate(game, caster);
			return false;
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {

	}
	
	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}
}
