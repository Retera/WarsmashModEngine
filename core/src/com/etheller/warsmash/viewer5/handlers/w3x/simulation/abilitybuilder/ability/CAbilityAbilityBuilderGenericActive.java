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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public abstract class CAbilityAbilityBuilderGenericActive extends AbstractGenericSingleIconNoSmartActiveAbility
		implements AbilityBuilderAbility {
	protected List<CAbilityTypeAbilityBuilderLevelData> levelData;
	protected AbilityBuilderConfiguration config;
	protected Map<String, Object> localStore;
	protected int orderId;
	protected int autoCastOnId = 0;
	protected int autoCastOffId = 0;
	protected boolean autocasting = false;
	protected PrimaryTag castingPrimaryTag;
	protected EnumSet<SecondaryTag> castingSecondaryTags;

	protected CItem item = null;

	protected float cooldown = 0;
	protected int manaCost = 0;
	protected float area = Float.NaN;

	protected int castId = 0;

	public CAbilityAbilityBuilderGenericActive(int handleId, War3ID code, War3ID alias,
			List<CAbilityTypeAbilityBuilderLevelData> levelData, AbilityBuilderConfiguration config,
			Map<String, Object> localStore) {
		super(handleId, code, alias);
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
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(game, unit, this.localStore, castId);
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
		return this.manaCost;
	}

	@Override
	public float getUIAreaOfEffect() {
		return this.area;
	}

	@Override
	public boolean isToggleOn() {
		return false;
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
		ABBehavior beh = new CBehaviorAbilityBuilderNoTarget(unit, config, localStore, this);
		if (this.item != null || (this.config.getDisplayFields() != null && this.config.getDisplayFields().getInstantCast() != null
				&& this.config.getDisplayFields().getInstantCast().callback(null, unit, localStore, castId))) {
			beh.setInstant(true);
		}
		return beh;
	}

	protected ABBehavior createRangedBehavior(CUnit unit) {
		ABBehavior beh = new CBehaviorAbilityBuilderBase(unit, config, localStore, this);
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
		if (cooldownRemaining > 0) {
			final float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
					* WarsmashConstants.SIMULATION_STEP_TIME;
			receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
					cooldownLengthDisplay);
		} else if (unit.getMana() < this.manaCost) {
			receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
		} else {
			innerCheckExtraCastConditions(game, unit, orderId, receiver);
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
		return orderId == getBaseOrderId();
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
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (config.getOnRemoveAbility() != null) {
			for (ABAction action : config.getOnRemoveAbility()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onDeath(CSimulation game, CUnit unit) {
		if (config.getOnDeathPreCast() != null) {
			for (ABAction action : config.getOnDeathPreCast()) {
				action.runAction(game, unit, localStore, castId);
			}
		}
	}

	@Override
	public void onTick(CSimulation game, CUnit unit) {

	}

	@Override
	public void onCancelFromQueue(CSimulation game, CUnit unit, int orderId) {

	}
}
