package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.ability;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericSingleIconNoSmartActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbilityFields;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABTargetTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.timer.ABManaDepletedCheckTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.types.impl.ABAbilityBuilderAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;

public abstract class ABAbilityBuilderGenericActive extends AbstractGenericSingleIconNoSmartActiveAbility
		implements ABAbilityBuilderActiveAbility {
	protected List<ABAbilityBuilderAbilityTypeLevelData> levelData;
	protected ABAbilityBuilderConfiguration config;
	protected ABLocalDataStore localStore;
	protected int orderId;
	protected int unorderId = 0;
	protected int autoCastOnId = 0;
	protected int autoCastOffId = 0;
	protected boolean autocasting = false;
	protected AutocastType autocastType = AutocastType.NONE;
	protected boolean toggleable = false;
	protected boolean separateOnAndOff = false;
	protected boolean active = false;
	protected boolean allowCastlessDeactivate = true;
	protected PrimaryTag castingPrimaryTag;
	protected EnumSet<SecondaryTag> castingSecondaryTags;

	protected byte clickDisabled = 0;

	protected CItem item = null;

	protected float cooldown = 0;
	protected int manaCost = 0;
	protected float area = 0;
	protected float range = 0;
	protected float castTime = 0;
	private boolean ignoreCastTime = false;

	protected boolean hideAreaCursor = false;
	private Float areaCursorOverride;

	protected int bufferMana = 0;
	private ABManaDepletedCheckTimer timer;
	private NonStackingStatBuff manaDrain;

	protected int castId = ABConstants.STARTING_CAST_ID;
	private War3ID onTooltipOverride = null;
	private War3ID offTooltipOverride = null;
	private EnumSet<CTargetType> targetsAllowed;
	private boolean dispel = false;
	private boolean physical = false;
	private boolean magic = true;
	private boolean universal = false;

	protected Set<String> uniqueFlags = null;

	private boolean isMenu = false;
	private int visibleMenuId = 0;

	public ABAbilityBuilderGenericActive(int handleId, War3ID code, War3ID alias,
			List<ABAbilityBuilderAbilityTypeLevelData> levelData, ABAbilityBuilderConfiguration config,
			ABLocalDataStore localStore) {
		super(handleId, code, alias);
		this.levelData = levelData;
		this.config = config;
		this.localStore = localStore;
		localStore.originAbility = this;

		if (config.getCastId() != null) {
			orderId = OrderIdUtils.getOrderId(config.getCastId());
		} else {
			orderId = 0;
		}
		if (config.getUncastId() != null) {
			unorderId = OrderIdUtils.getOrderId(config.getUncastId());
		}
		if (config.getAutoCastOnId() != null) {
			autoCastOnId = OrderIdUtils.getOrderId(config.getAutoCastOnId());
		}
		if (config.getAutoCastOffId() != null) {
			autoCastOffId = OrderIdUtils.getOrderId(config.getAutoCastOffId());
		}
		if (config.getAutoCastType() != null) {
			autocastType = config.getAutoCastType();
		}

		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
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
		final int levels = editorData.getFieldAsInteger(AbilityFields.LEVELS, 0);
		localStore.put(ABLocalStoreKeys.ISABILITYLEVELED, levels > 1);
	}

	@Override
	public int getAbilityIntField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldValue(field);
	}

	@Override
	public float getAbilityFloatField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldFloatValue(field);
	}

	@Override
	public String getAbilityStringField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getField(field);
	}

	@Override
	public boolean getAbilityBooleanField(String field) {
		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		return editorData.getFieldValue(field) != 0;
	}

	@Override
	public void setLevel(CSimulation game, CUnit unit, int level) {
		super.setLevel(game, unit, level);
		setSpellFields(game, unit);
		determineToggleableFields(game, unit);
		if (config.getOnLevelChange() != null) {
			for (ABAction action : config.getOnLevelChange()) {
				action.runAction(unit, this.localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onAdd(CSimulation game, CUnit unit) {
		setSpellFields(game, unit);
		determineToggleableFields(game, unit);
		if (config.getOnAddAbility() != null) {
			for (ABAction action : config.getOnAddAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onAddDisabled(CSimulation game, CUnit unit) {
		localStore.game = game;
		localStore.originUnit = unit;
		localStore.originPlayer = game.getPlayer(unit.getPlayerIndex());
		addInitialUniqueFlags(game, unit);
		setSpellFields(game, unit);
		determineToggleableFields(game, unit);
		if (config.getOnAddDisabledAbility() != null) {
			for (ABAction action : config.getOnAddDisabledAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void onRemoveDisabled(CSimulation game, CUnit unit) {
		if (config.getOnRemoveDisabledAbility() != null) {
			for (ABAction action : config.getOnRemoveDisabledAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	private void addInitialUniqueFlags(CSimulation game, CUnit unit) {
		if (this.config.getInitialUniqueFlags() != null && !this.config.getInitialUniqueFlags().isEmpty()) {
			this.uniqueFlags = new HashSet<>();
			for (ABStringCallback flag : this.config.getInitialUniqueFlags()) {
				this.uniqueFlags.add(flag.callback(unit, localStore, ABConstants.NO_CAST_ID));
			}
		}
	}

	private void determineToggleableFields(CSimulation game, CUnit unit) {
		if (config.getDisplayFields() != null && config.getDisplayFields().getSeparateOnAndOff() != null) {
			this.separateOnAndOff = config.getDisplayFields().getSeparateOnAndOff().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (config.getDisplayFields() != null && config.getDisplayFields().getToggleable() != null) {
			this.toggleable = config.getDisplayFields().getToggleable().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (config.getDisplayFields() != null && config.getDisplayFields().getCastToggleOff() != null) {
			this.allowCastlessDeactivate = !config.getDisplayFields().getCastToggleOff().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (config.getDisplayFields() != null && config.getDisplayFields().getAlternateUnitId() != null) {
			if (unit.getTypeId().equals(config.getDisplayFields().getAlternateUnitId().callback(unit, localStore,
					ABConstants.NO_CAST_ID))) {
				this.active = true;
			}
		}
		if (config.getSpecialFields() != null && config.getSpecialFields().getBufferManaRequired() != null) {
			this.bufferMana = config.getSpecialFields().getBufferManaRequired().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (this.toggleable) {
			localStore.put(ABLocalStoreKeys.ISTOGGLEDABILITY, this);
			int manaPerSec = 0;
			if (config.getSpecialFields() != null && config.getSpecialFields().getManaDrainedPerSecond() != null) {
				manaPerSec = config.getSpecialFields().getManaDrainedPerSecond().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (manaPerSec != 0) {
				if (manaDrain == null) {
					manaDrain = new NonStackingStatBuff(NonStackingStatBuffType.MPGEN,
							NonStackingStatBuff.ALLOW_STACKING_KEY, (-1 * manaPerSec));
				} else {
					manaDrain.setValue((-1 * manaPerSec));
				}
				if (this.timer == null) {
					timer = new ABManaDepletedCheckTimer(unit, this);
				}
			} else {
				this.manaDrain = null;
				this.timer = null;
			}

			if (config.getDisplayFields() != null && config.getDisplayFields().getAlternateUnitId() != null) {
				if (unit.getTypeId().equals(config.getDisplayFields().getAlternateUnitId().callback(unit, localStore,
						ABConstants.NO_CAST_ID))) {
					this.active = true;
				}
			}
		}
	}

	protected void setSpellFields(CSimulation game, CUnit unit) {
		ABAbilityBuilderAbilityTypeLevelData levelDataLevel = this.levelData.get(this.getLevel() - 1);
		this.manaCost = levelDataLevel.getManaCost();
		this.cooldown = levelDataLevel.getCooldown();
		this.range = levelDataLevel.getCastRange();
		this.area = levelDataLevel.getArea();
		this.castTime = levelDataLevel.getCastTime();
		if (this.config.getOverrideFields() != null) {
			if (this.config.getOverrideFields().getAreaOverride() != null) {
				this.area = this.config.getOverrideFields().getAreaOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getRangeOverride() != null) {
				this.range = this.config.getOverrideFields().getRangeOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getCastTimeOverride() != null) {
				this.castTime = this.config.getOverrideFields().getCastTimeOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getIgnoreCastTime() != null) {
				this.ignoreCastTime = this.config.getOverrideFields().getIgnoreCastTime().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getCooldownOverride() != null) {
				this.cooldown = this.config.getOverrideFields().getCooldownOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getManaCostOverride() != null) {
				this.manaCost = this.config.getOverrideFields().getManaCostOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getAutocastTypeOverride() != null) {
				this.autocastType = this.config.getOverrideFields().getAutocastTypeOverride().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}

			if (this.config.getOverrideFields().getOnTooltipOverride() != null) {
				this.onTooltipOverride = this.config.getOverrideFields().getOnTooltipOverride().callback(unit,
						localStore, ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getOffTooltipOverride() != null) {
				this.offTooltipOverride = this.config.getOverrideFields().getOffTooltipOverride().callback(unit,
						localStore, ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getPhysicalSpell() != null) {
				this.physical = this.config.getOverrideFields().getPhysicalSpell().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
				this.magic = this.physical ? false : this.magic; // Spells that just declare physical:true default to
																	// magic:false
			}
			if (this.config.getOverrideFields().getMagicSpell() != null) {
				this.magic = this.config.getOverrideFields().getMagicSpell().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getDispel() != null) {
				this.dispel = this.config.getOverrideFields().getDispel().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
			if (this.config.getOverrideFields().getUniversalSpell() != null) {
				this.universal = this.config.getOverrideFields().getUniversalSpell().callback(unit, localStore,
						ABConstants.NO_CAST_ID);
			}
		}

		GameObject editorData = localStore.get(ABLocalStoreKeys.ABILITYEDITORDATA, GameObject.class);
		final int requiredLevel = editorData.getFieldAsInteger(AbilityFields.REQUIRED_LEVEL, 0);
		this.targetsAllowed = levelDataLevel.getTargetsAllowed();
		if ((requiredLevel < 6 || game.getGameplayConstants().isMagicImmuneResistsUltimates()) && !isPhysical()
				&& !isUniversal()) {
			this.targetsAllowed.add(CTargetType.NON_MAGIC_IMMUNE);
			if (isDispel()) {
				this.targetsAllowed.add(CTargetType.LIMITED_MAGIC_IMMUNE);
			}
		}
		if (isPhysical() && !isUniversal()) {
			this.targetsAllowed.add(CTargetType.NON_ETHEREAL);
		}

		if (this.config.getOverrideFields() != null
				&& this.config.getOverrideFields().getExtraTargetsAllowed() != null) {
			for (ABTargetTypeCallback target : this.config.getOverrideFields().getExtraTargetsAllowed()) {
				this.targetsAllowed.add(target.callback(unit, localStore, requiredLevel));
			}
		}
		if (this.config.getOverrideFields() != null
				&& this.config.getOverrideFields().getExcludedTargetsAllowed() != null) {
			for (ABTargetTypeCallback target : this.config.getOverrideFields().getExcludedTargetsAllowed()) {
				this.targetsAllowed.remove(target.callback(unit, localStore, requiredLevel));
			}
		}

		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getHideAreaCursor() != null) {
			this.hideAreaCursor = this.config.getDisplayFields().getHideAreaCursor().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getAreaCursorOverride() != null) {
			this.areaCursorOverride = this.config.getDisplayFields().getAreaCursorOverride().callback(unit, localStore,
					ABConstants.NO_CAST_ID);
		}
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getIsMenu() != null) {
			this.isMenu = this.config.getDisplayFields().getIsMenu().callback(unit, localStore, this.getLevel());
			if (this.isMenu) {
				if (this.config.getDisplayFields().getMenuId() != null) {
					this.orderId = this.config.getDisplayFields().getMenuId().callback(unit, localStore,
							ABConstants.NO_CAST_ID);
				} else {
					if (this.orderId == 0) {
						this.orderId = this.getHandleId();
					}
				}
			}
		}
	}

	@Override
	public void startCooldown(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			unit.beginCooldown(game, cdID, this.cooldown);
		}
	}

	@Override
	public float getCooldownRemainingTicks(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			return unit.getCooldownRemainingTicks(game, cdID);
		}
		return unit.getCooldownRemainingTicks(game, this.getCode());
	}

	@Override
	public void resetCooldown(CSimulation game, CUnit unit) {
		War3ID cdID = getCooldownId();
		if (cdID != War3ID.NONE) {
			unit.beginCooldown(game, cdID, 0);
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
		this.localStore.originItem = item;
		this.localStore.put(ABLocalStoreKeys.ITEMSLOT, slot);
	}

	@Override
	public CItem getItem() {
		return this.item;
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

	public List<ABAbilityBuilderAbilityTypeLevelData> getLevelData() {
		return this.levelData;
	}

	public ABAbilityBuilderConfiguration getConfig() {
		return this.config;
	}

	public ABLocalDataStore getLocalStore() {
		return this.localStore;
	}

	@Override
	public boolean hasUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			return this.uniqueFlags.contains(flag);
		}
		return false;
	}

	public void addUniqueFlag(String flag) {
		if (this.uniqueFlags == null) {
			this.uniqueFlags = new HashSet<>();
		}
		this.uniqueFlags.add(flag);
	}

	public void removeUniqueFlag(String flag) {
		if (this.uniqueFlags != null) {
			this.uniqueFlags.remove(flag);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUniqueValue(String key, Class<T> cls) {
		Object o = this.localStore.get(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
		if (o != null && o.getClass() == cls) {
			return (T) o;
		}
		return null;
	}

	public void addUniqueValue(Object item, String key) {
		this.localStore.put(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()), item);
	}

	public void removeUniqueValue(String key) {
		this.localStore.remove(ABLocalStoreKeys.combineUniqueValueKey(key, this.getHandleId()));
	}

	@Override
	public War3ID getOnTooltipOverride() {
		return onTooltipOverride;
	}

	@Override
	public War3ID getOffTooltipOverride() {
		return offTooltipOverride;
	}

	@Override
	public int getUIManaCost() {
		return (this.toggleable && this.active && this.allowCastlessDeactivate) ? 0 : this.manaCost + this.bufferMana;
	}

	@Override
	public int getChargedManaCost() {
		return this.manaCost;
	}

	@Override
	public float getUIAreaOfEffect() {
		return ((this.area == 0 && (this.areaCursorOverride == null || this.areaCursorOverride == 0))
				|| this.hideAreaCursor) ? Float.NaN
						: (this.areaCursorOverride == null ? this.area : this.areaCursorOverride);
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getArea() {
		return area;
	}

	public float getCastRange() {
		return range;
	}

	public void setCastRange(float range) {
		this.range = range;
	}

	public float getCastTime() {
		return castTime;
	}

	public void setCastTime(float castTime) {
		this.castTime = castTime;
	}

	public boolean ignoreCastTime() {
		return this.ignoreCastTime;
	}

	public EnumSet<CTargetType> getTargetsAllowed() {
		return targetsAllowed;
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
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean isClickDisabled() {
		return this.clickDisabled != 0;
	}

	@Override
	public final void setClickDisabled(final boolean disabled, final CAbilityDisableType type) {
		if (disabled) {
			this.clickDisabled |= type.getMask();
		} else {
			this.clickDisabled &= ~type.getMask();
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
	public void setAutoCastOn(final CSimulation simulation, final CUnit caster, final boolean autoCastOn,
			final boolean notify) {
		this.localStore.put(ABLocalStoreKeys.WASAUTOCASTON, this.autocasting);
		this.autocasting = autoCastOn;
		if (notify) {
			caster.setAutocastAbility(simulation, autoCastOn ? this : null);
		}
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTON, autoCastOn);
		if (this.config.getOnChangeAutoCast() != null) {
			for (ABAction action : this.config.getOnChangeAutoCast()) {
				action.runAction(caster, localStore, ABConstants.NO_CAST_ID);
			}
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTON);
		this.localStore.remove(ABLocalStoreKeys.WASAUTOCASTON);
	}

	@Override
	public AutocastType getAutocastType() {
		return autocastType;
	}

	protected ABBehavior createNoTargetBehavior(CUnit unit) {
		ABBehavior beh = new ABBehaviorAbilityBuilderNoTarget(unit, localStore, this);
		if (this.item != null || (this.config.getDisplayFields() != null
				&& this.config.getDisplayFields().getInstantCast() != null && this.config.getDisplayFields()
						.getInstantCast().callback(unit, localStore, ABConstants.NO_CAST_ID))) {
			beh.setInstant(true);
		}
		if (this.config.getSpecialFields() != null && this.config.getSpecialFields().getBehaviorCategory() != null) {
			beh.setBehaviorCategory(this.config.getSpecialFields().getBehaviorCategory());
		}
		return beh;
	}

	protected ABBehavior createRangedBehavior(CUnit unit) {
		ABBehavior beh = new ABBehaviorAbilityBuilderBase(unit, localStore, this);
		if (this.item != null || (this.config.getDisplayFields() != null
				&& this.config.getDisplayFields().getInstantCast() != null && this.config.getDisplayFields()
						.getInstantCast().callback(unit, localStore, ABConstants.NO_CAST_ID))) {
			beh.setInstant(true);
		}
		if (this.config.getSpecialFields() != null && this.config.getSpecialFields().getBehaviorCategory() != null) {
			beh.setBehaviorCategory(this.config.getSpecialFields().getBehaviorCategory());
		}
		return beh;
	}

	protected void innerCheckCooldownDisabled(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final int cooldownRemaining = unit.getCooldownRemainingTicks(game, getCooldownId());
		if (this.toggleable && this.active && this.allowCastlessDeactivate) {
			if (cooldownRemaining > 0 && !(receiver instanceof MeleeUIAbilityActivationReceiver)) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
						cooldownLengthDisplay);
			}
		} else {
			if (cooldownRemaining > 0) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
						cooldownLengthDisplay);
			}
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.useOk();
			return;
		}
		final int cooldownRemaining = unit.getCooldownRemainingTicks(game, getCooldownId());

		if (this.toggleable && this.active && this.allowCastlessDeactivate) {
			if (cooldownRemaining > 0 && !(receiver instanceof MeleeUIAbilityActivationReceiver)) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
						cooldownLengthDisplay);
			}
			receiver.useOk();
		} else {
			if (cooldownRemaining > 0) {
				float cooldownLengthDisplay = unit.getCooldownLengthDisplayTicks(game, getCooldownId())
						* WarsmashConstants.SIMULATION_STEP_TIME;
				receiver.cooldownNotYetReady(cooldownRemaining * WarsmashConstants.SIMULATION_STEP_TIME,
						cooldownLengthDisplay);
			} else if (unit.getMana() < (this.manaCost + this.bufferMana)) {
				receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_MANA);
			} else {
				CPlayer p = game.getPlayer(unit.getPlayerIndex());
				if (this.getUIGoldCost() > p.getGold()) {
					receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_GOLD);
				} else if (this.getUILumberCost() > p.getLumber()) {
					receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_LUMBER);
				} else if (this.getUIFoodCost() > 0 && this.getUIFoodCost() > p.getFoodCap() - p.getFoodUsed()) {
					receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_FOOD);
				} else {
					innerCheckExtraCastConditions(game, unit, orderId, receiver);
				}
			}
		}
	}

	protected void innerCheckExtraCastConditions(CSimulation game, CUnit unit, int orderId,
			AbilityActivationReceiver receiver) {
		if (innerCheckCanUseSpell(game, unit, orderId, receiver)) {
			if (config.getExtraCastConditions() != null) {
				boolean result = true;
				for (ABBooleanCallback condition : config.getExtraCastConditions()) {
					result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
				}
				String failReason = (String) localStore.remove(ABLocalStoreKeys.CANTUSEREASON);
				if (result) {
					receiver.useOk();
				} else {
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
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);
		if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		} else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		} else {
			receiver.orderIdNotAccepted();
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
	}

	@Override
	public void checkCanAutoTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (orderId == getBaseOrderId()) {
			this.localStore.put(
					ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, ABConstants.NO_CAST_ID),
					target.visit(AbilityTargetVisitor.UNIT));
			this.localStore.put(
					ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, ABConstants.NO_CAST_ID),
					target.visit(AbilityTargetVisitor.ITEM));
			this.localStore.put(
					ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, ABConstants.NO_CAST_ID),
					target.visit(AbilityTargetVisitor.DESTRUCTABLE));
			if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
				if (innerCheckTargetTargetable(game, unit, target, receiver)) {
					if (innerCheckTargetInRange(unit, target)) {
						String extraFailReason = innerCheckExtraAutoTargetConditions(game, unit, orderId);
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
				}
			}
			this.localStore
					.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, ABConstants.NO_CAST_ID));
			this.localStore
					.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, ABConstants.NO_CAST_ID));
			this.localStore.remove(
					ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, ABConstants.NO_CAST_ID));
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);
		if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTarget(game, unit, orderId, target, receiver);
		} else if (orderId == OrderIds.smart) {
			innerCheckCanSmartTarget(game, unit, orderId, target, receiver);
		} else {
			receiver.orderIdNotAccepted();
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
	}

	@Override
	public void checkCanAutoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (orderId == getBaseOrderId()) {
			if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
				if (innerCheckTargetInRange(unit, target)) {
					localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION,
							ABConstants.NO_CAST_ID), target);
					String extraFailReason = innerCheckExtraAutoTargetConditions(game, unit, orderId);
					localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION,
							ABConstants.NO_CAST_ID));
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
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId, boolean autoOrder,
			final AbilityTargetCheckReceiver<Void> receiver) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);
		if ((orderId != 0) && ((orderId == getAutoCastOffOrderId()) || (orderId == getAutoCastOnOrderId()))) {
			receiver.targetOk(null);
		} else if (innerCheckCastOrderId(game, unit, orderId)) {
			innerCheckCanTargetNoTarget(game, unit, orderId, receiver);
		} else {
			receiver.orderIdNotAccepted();
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
	}

	@Override
	public void checkCanAutoTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (orderId == getBaseOrderId()) {
			if (innerCheckCanTargetSpell(game, unit, orderId, receiver)) {
				String extraFailReason = innerCheckExtraAutoNoTargetConditions(game, unit, orderId, receiver);
				if (extraFailReason != null) {
					if (!extraFailReason.equals("unknown")) {
						receiver.targetCheckFailed(extraFailReason);
					} else {
						receiver.orderIdNotAccepted();
					}
				} else {
					receiver.targetOk(null);
				}
			}
		} else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanTarget(CSimulation game, CUnit unit, int orderId, AbilityPointTarget target,
			AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
			if (innerCheckTargetInRange(unit, target)) {
				localStore.put(
						ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION, ABConstants.NO_CAST_ID),
						target);
				String extraFailReason = innerCheckExtraTargetConditions(game, unit, orderId);
				localStore.remove(
						ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION, ABConstants.NO_CAST_ID));
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
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, ABConstants.NO_CAST_ID),
				target.visit(AbilityTargetVisitor.UNIT));
		this.localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, ABConstants.NO_CAST_ID),
				target.visit(AbilityTargetVisitor.ITEM));
		this.localStore.put(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, ABConstants.NO_CAST_ID),
				target.visit(AbilityTargetVisitor.DESTRUCTABLE));
		if (innerCheckCanTargetSpell(game, unit, orderId, target, receiver)) {
			if (innerCheckTargetTargetable(game, unit, target, receiver)) {
				if (innerCheckTargetInRange(unit, target)) {
					String extraFailReason = innerCheckExtraTargetConditions(game, unit, orderId);
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
			}
		}
		this.localStore
				.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, ABConstants.NO_CAST_ID));
		this.localStore
				.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, ABConstants.NO_CAST_ID));
		this.localStore.remove(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, ABConstants.NO_CAST_ID));
	}

	@Override
	protected void innerCheckCanTargetNoTarget(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (innerCheckCanTargetSpell(game, unit, orderId, receiver)) {
			receiver.targetOk(null);
		}
	}

	protected boolean innerCheckCastOrderId(final CSimulation game, final CUnit unit, final int orderId) {
		return ((!this.active || !this.toggleable) && orderId == getBaseOrderId())
				|| ((this.active || this.separateOnAndOff) && orderId == getOffOrderId());
	}

	protected boolean innerCheckTargetTargetable(CSimulation game, CUnit unit, CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		return target.canBeTargetedBy(game, unit, targetsAllowed, receiver);
	}

	protected boolean innerCheckTargetInRange(CUnit unit, AbilityTarget target) {
		return !unit.isMovementDisabled() || unit.canReach(target, this.getCastRange());
	}

	protected String innerCheckExtraTargetConditions(CSimulation game, CUnit unit, int orderId) {
		if (config.getExtraTargetConditions() != null) {
			boolean result = true;
			for (ABBooleanCallback condition : config.getExtraTargetConditions()) {
				result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
			}
			String failReason = (String) localStore.remove(ABLocalStoreKeys.CANTUSEREASON);
			if (result) {
				return null;
			} else {
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

	protected String innerCheckExtraAutoTargetConditions(CSimulation game, CUnit unit, int orderId) {
		if (config.getExtraTargetConditions() != null || config.getExtraAutoTargetConditions() != null) {
			boolean result = true;
			if (config.getExtraTargetConditions() != null) {
				for (ABBooleanCallback condition : config.getExtraTargetConditions()) {
					result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
				}
			}
			if (config.getExtraAutoTargetConditions() != null) {
				for (ABBooleanCallback condition : config.getExtraAutoTargetConditions()) {
					result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
				}
			}
			String failReason = (String) localStore.remove(ABLocalStoreKeys.CANTUSEREASON);
			if (result) {
				return null;
			} else {
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

	protected String innerCheckExtraAutoNoTargetConditions(CSimulation game, CUnit unit, int orderId,
			AbilityTargetCheckReceiver<Void> receiver) {
		if (config.getExtraAutoNoTargetConditions() != null) {
			boolean result = true;
			if (config.getExtraAutoNoTargetConditions() != null) {
				for (ABBooleanCallback condition : config.getExtraAutoNoTargetConditions()) {
					result = result && condition.callback(unit, localStore, ABConstants.NO_CAST_ID);
				}
			}
			String failReason = (String) localStore.remove(ABLocalStoreKeys.CANTUSEREASON);
			if (result) {
				return null;
			} else {
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
		if (this.toggleable && this.active && this.allowCastlessDeactivate) {
			return 0;
		}
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getFoodCost() != null) {
			return this.config.getDisplayFields().getFoodCost().callback(this.localStore.originUnit, localStore,
					ABConstants.NO_CAST_ID);
		}
		return 0;
	}

	@Override
	public int getUIGoldCost() {
		if (this.toggleable && this.active && this.allowCastlessDeactivate) {
			return 0;
		}
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getGoldCost() != null) {
			return this.config.getDisplayFields().getGoldCost().callback(this.localStore.originUnit, localStore,
					ABConstants.NO_CAST_ID);
		}
		return 0;
	}

	@Override
	public int getUILumberCost() {
		if (this.toggleable && this.active && this.allowCastlessDeactivate) {
			return 0;
		}
		if (this.config.getDisplayFields() != null && this.config.getDisplayFields().getLumberCost() != null) {
			return this.config.getDisplayFields().getLumberCost().callback(this.localStore.originUnit, localStore,
					ABConstants.NO_CAST_ID);
		}
		return 0;
	}

	@Override
	public void onRemove(CSimulation game, CUnit unit) {
		if (this.toggleable && this.active) {
			deactivate(game, unit);
		}
		if (config.getOnRemoveAbility() != null) {
			for (ABAction action : config.getOnRemoveAbility()) {
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
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
				action.runAction(unit, localStore, ABConstants.NO_CAST_ID);
			}
		}
	}

	@Override
	public void runOnOrderIssuedActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnOrderIssued() != null) {
			for (ABAction action : config.getOnOrderIssued()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void runBeginCastingActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnBeginCasting() != null) {
			for (ABAction action : config.getOnBeginCasting()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void runEndCastingActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnEndCasting() != null) {
			for (ABAction action : config.getOnEndCasting()) {
				action.runAction(caster, localStore, castId);
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
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void runEndChannelActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnEndChannel() != null) {
			for (ABAction action : config.getOnEndChannel()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void runCancelPreCastActions(final CSimulation game, final CUnit caster, int orderId) {
		if (config.getOnCancelPreCast() != null) {
			for (ABAction action : config.getOnCancelPreCast()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void activate(final CSimulation game, final CUnit caster) {
		Boolean failed = this.localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId),
				Boolean.class);
		if (failed != null && failed) {
			System.err.println("Failed to cast!");
			return;
		}
//		System.err.println("Activating!");
		this.active = true;
		if (this.manaDrain != null) {
			this.timer.start(game);
			caster.addNonStackingStatBuff(game, manaDrain);
		}
		if (config.getOnActivate() != null) {
			for (ABAction action : config.getOnActivate()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public void deactivate(final CSimulation game, final CUnit caster) {
		Boolean failed = this.localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.FAILEDTOCAST, castId),
				Boolean.class);
		if (failed != null && failed) {
			System.err.println("Failed to cast!");
			return;
		}
//		System.err.println("Deactivating!");
		this.active = false;
		if (this.manaDrain != null) {
			timer.pause(game);
			caster.removeNonStackingStatBuff(game, manaDrain);
		}
		if (config.getOnDeactivate() != null) {
			for (ABAction action : config.getOnDeactivate()) {
				action.runAction(caster, localStore, castId);
			}
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId, boolean autoOrder,
			final AbilityTarget target) {
		this.localStore.put(ABLocalStoreKeys.ISAUTOCASTTARGETING, autoOrder);
//		System.err.println("Checking queue top level: " + active + " orderID : " + orderId + " offID: " + this.getOffOrderId());
		if (this.allowCastlessDeactivate && this.toggleable && this.active && orderId == this.getOffOrderId()) {
			this.deactivate(game, caster);
			this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
			return false;
		}
		this.localStore.remove(ABLocalStoreKeys.ISAUTOCASTTARGETING);
		return super.checkBeforeQueue(game, caster, orderId, autoOrder, target);
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

	@Override
	public void checkRequirementsMet(CSimulation game, CUnit unit, AbilityActivationReceiver receiver) {
		List<CUnitTypeRequirement> reqs = this.levelData.get(this.getLevel() - 1).getRequirements();
		CPlayer player = game.getPlayer(unit.getPlayerIndex());
		if (reqs != null) {
			for (final CUnitTypeRequirement requirement : reqs) {
//				System.err.println("Checking reqs for " + this.getAlias() + ": looking for "
//						+ requirement.getRequirement() + " at amount " + requirement.getRequiredLevel() + " and found "
//						+ player.getTechtreeUnlocked(requirement.getRequirement()));
				if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
					receiver.missingRequirement(requirement.getRequirement(), requirement.getRequiredLevel());
				}
			}
		}
	}

	@Override
	public boolean isRequirementsMet(CSimulation game, CUnit unit) {
		List<CUnitTypeRequirement> reqs = this.levelData.get(this.getLevel() - 1).getRequirements();
		CPlayer player = game.getPlayer(unit.getPlayerIndex());
		boolean requirementsMet = player.isTechtreeAllowedByMax(this.getAlias());
		if (reqs != null) {
			for (final CUnitTypeRequirement requirement : reqs) {
//				System.err.println("Checking reqs for " + this.getAlias() + ": looking for "
//						+ requirement.getRequirement() + " at amount " + requirement.getRequiredLevel() + " and found "
//						+ player.getTechtreeUnlocked(requirement.getRequirement()));
				if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
					requirementsMet = false;
				}
			}
		}
//		System.err.println("Returning "+requirementsMet+" for " + this.getAlias());
		return requirementsMet;
	}

	public boolean isDispel() {
		return dispel;
	}

	@Override
	public boolean isPhysical() {
		return physical;
	}

	@Override
	public boolean isMagic() {
		return magic;
	}

	@Override
	public boolean isUniversal() {
		return universal;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.SPELL;
	}

	@Override
	public void cleanupInputs() {
		this.cleanupInputs(castId);
	}

	@Override
	public void cleanupInputs(int theCastId) {
		this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, theCastId));
		this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, theCastId));
		this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDITEM, theCastId));
		this.localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION, theCastId));
		this.localStore.remove(ABLocalStoreKeys.PREVIOUSBEHAVIOR);
	}

	@Override
	public int getIconVisibleMenuId() {
		return this.visibleMenuId;
	}

	@Override
	public void setIconVisibleMenuId(int menu) {
		this.visibleMenuId = menu;
	}

	@Override
	public boolean isMenuAbility() {
		return this.isMenu;
	}
}
