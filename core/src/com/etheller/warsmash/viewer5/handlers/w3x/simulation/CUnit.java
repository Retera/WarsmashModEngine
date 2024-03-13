package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntIntMap;
import com.etheller.warsmash.parsers.jass.scope.CommonTriggerExecutionScope;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener.CUnitStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityDisableType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.GetAbilityByRawcodeVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.CAutocastAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityCargoHold;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbilityGenericSingleIconPassiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.hero.CAbilityHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityNeutralBuilding;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.listeners.CUnitAbilityEffectReactionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityOverlayedMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABGenericTimedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.buff.ABTimedTickingPausedBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetUnitVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttackMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorBoardTransport;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorFollow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorHoldPosition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorPatrol;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStun;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build.AbilityDisableWhileUpgradingVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CRegenType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CAttackProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.CProjectile;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners.CUnitAbilityProjReactionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.projectile.listeners.CUnitAttackProjReactionListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CUnitAttackVisionFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CUnitDeathVisionFogModifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegion;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionEnumFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.region.CRegionManager;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.state.CUnitState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.JassGameEventsWar3;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CEffectType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CUnitTypeJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.CWidgetEvent;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingFx;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CUnit extends CWidget {
	private static RegionCheckerImpl regionCheckerImpl = new RegionCheckerImpl();

	private War3ID typeId;
	private float facing; // degrees
	private float mana;
	private int baseMaximumLife;
	private int maximumLife;
	private float lifeRegen;
	private float lifeRegenStrengthBonus;
	private float lifeRegenBonus;
	private float manaRegen;
	private float manaRegenIntelligenceBonus;
	private float manaRegenBonus;
	private int baseMaximumMana;
	private int maximumMana;
	private int speed;
	private int agilityDefensePermanentBonus;
	private float agilityDefenseTemporaryBonus;
	private int permanentDefenseBonus;
	private float temporaryDefenseBonus;
	private float totalTemporaryDefenseBonus;

	private int speedBonus;

	private CUnitDefaultThornsListener flatThornsListener = null;
	private CUnitDefaultThornsListener percentThornsListener = null;
	private CUnitDefaultLifestealListener lifestealListener = null;

	private final List<StateModBuff> stateModBuffs = new ArrayList<>();
	private final Map<NonStackingStatBuffType, Map<String, List<NonStackingStatBuff>>> nonStackingBuffs = new HashMap<>();
	private final Map<String, List<NonStackingFx>> nonStackingFx = new HashMap<>();
	private final Map<String, List<CBuff>> nonStackingDisplayBuffs = new HashMap<>();

	private int currentDefenseDisplay;
	private float currentDefense;
	private float baseLifeRegenPerTick;
	private float currentLifeRegenPerTick;
	private float currentManaRegenPerTick;
	private CDefenseType defenseType;

	private int cooldownEndTime = 0;
	private float flyHeight;
	private int playerIndex;

	private final List<CAbility> abilities = new ArrayList<>();
	private final List<CAbility> disabledAbilities = new ArrayList<>();

	private CBehavior currentBehavior;
	private final Queue<COrder> orderQueue = new LinkedList<>();
	private CUnitType unitType;

	private Rectangle collisionRectangle;
	private RemovablePathingMapInstance pathingInstance;

	private final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);

	private int deathTurnTick;
	private boolean raisable;
	private boolean decays;
	private boolean corpse;
	private boolean boneCorpse;
	private boolean falseDeath;

	private transient CUnitAnimationListener unitAnimationListener;

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CUnitStateNotifier stateNotifier = new CUnitStateNotifier();
	private transient List<StateListenerUpdate> stateListenersUpdates = new ArrayList<>();
	private float acquisitionRange;
	private transient static AutoAttackTargetFinderEnum autoAttackTargetFinderEnum = new AutoAttackTargetFinderEnum();
	private transient static AutocastTargetFinderEnum autocastTargetFinderEnum = new AutocastTargetFinderEnum();
	private transient CAutocastAbility autocastAbility = null;

	private transient CBehaviorMove moveBehavior;
	private transient CBehaviorAttack attackBehavior;
	private transient CBehaviorAttackMove attackMoveBehavior;
	private transient CBehaviorFollow followBehavior;
	private transient CBehaviorPatrol patrolBehavior;
	private transient CBehaviorStop stopBehavior;
	private transient CBehaviorHoldPosition holdPositionBehavior;
	private transient CBehaviorBoardTransport boardTransportBehavior;
	private boolean constructing = false;
	private boolean constructingPaused = false;
	private boolean structure;
	private War3ID upgradeIdType = null;
	private float constructionProgress;
	private boolean hidden = false;
	private boolean paused = false;
	private boolean acceptingOrders = true;
	private boolean invulnerable = false;
	private boolean magicImmune = false;
	private boolean resistant = false;
	private boolean autoAttack = true;
	private boolean moveDisabled = false;
	private CBehavior defaultBehavior;
	private CBehavior interruptedDefaultBehavior;
	private CBehavior interruptedBehavior;
	private COrder lastStartedOrder = null;
	private CUnit workerInside;
	private final War3ID[] buildQueue = new War3ID[WarsmashConstants.BUILD_QUEUE_SIZE];
	private final QueueItemType[] buildQueueTypes = new QueueItemType[WarsmashConstants.BUILD_QUEUE_SIZE];
	private boolean queuedUnitFoodPaid = false;
	private AbilityTarget rallyPoint;

	private int foodMade;
	private int foodUsed;

	private int triggerEditorCustomValue;

	private List<CUnitAttack> unitSpecificAttacks;
	private List<CUnitAttack> unitSpecificCurrentAttacks;
	private boolean disableAttacks;
	private CUnitAttackVisionFogModifier attackFogMod;

	private final Map<CUnitAttackPreDamageListenerPriority, List<CUnitAttackPreDamageListener>> preDamageListeners = new HashMap<>();
	private final List<CUnitAttackPostDamageListener> postDamageListeners = new ArrayList<>();
	private final List<CUnitAttackDamageTakenModificationListener> damageTakenModificationListeners = new ArrayList<>();
	private final List<CUnitAttackFinalDamageTakenModificationListener> finalDamageTakenModificationListeners = new ArrayList<>();
	private final List<CUnitAttackDamageTakenListener> damageTakenListeners = new ArrayList<>();
	private final Map<CUnitDeathReplacementEffectPriority, List<CUnitDeathReplacementEffect>> deathReplacementEffects = new HashMap<>();
	private final List<CUnitAttackEvasionListener> evasionListeners = new ArrayList<>();
	private final List<CUnitAttackProjReactionListener> attackProjReactionListeners = new ArrayList<>();
	private final List<CUnitAbilityProjReactionListener> abilityProjReactionListeners = new ArrayList<>();
	private final List<CUnitAbilityEffectReactionListener> abilityEffectReactionListeners = new ArrayList<>();

	private transient Set<CRegion> containingRegions = new LinkedHashSet<>();
	private transient Set<CRegion> priorContainingRegions = new LinkedHashSet<>();

	private boolean constructionConsumesWorker;
	private boolean explodesOnDeath;
	private War3ID explodesOnDeathBuffId;
	private final IntIntMap rawcodeToCooldownExpireTime = new IntIntMap();
	private final IntIntMap rawcodeToCooldownStartTime = new IntIntMap();

	public CUnit(final int handleId, final int playerIndex, final float x, final float y, final float life,
			final War3ID typeId, final float facing, final float mana, final int maximumLife, final float lifeRegen,
			final int maximumMana, final int speed, final CUnitType unitType) {
		super(handleId, x, y, life);
		this.playerIndex = playerIndex;
		this.typeId = typeId;
		this.facing = facing;
		this.mana = mana;
		this.baseMaximumLife = maximumLife;
		this.maximumLife = maximumLife;
		this.lifeRegen = lifeRegen;
		this.manaRegen = unitType.getManaRegen();
		this.baseMaximumMana = maximumMana;
		this.maximumMana = maximumMana;
		this.speed = speed;
		this.flyHeight = unitType.getDefaultFlyingHeight();
		this.unitType = unitType;
		this.defenseType = unitType.getDefenseType();
		this.classifications.addAll(unitType.getClassifications());
		this.acquisitionRange = unitType.getDefaultAcquisitionRange();
		this.structure = unitType.isBuilding();
		this.stopBehavior = new CBehaviorStop(this);
		this.defaultBehavior = this.stopBehavior;
		this.raisable = unitType.isRaise();
		this.decays = unitType.isDecay();
		initializeNonStackingBuffs();
		initializeListenerLists();
		this.addPreDamageListener(CUnitAttackPreDamageListenerPriority.ACCURACY,
				new CUnitDefaultAccuracyCheckListener());
		this.attackFogMod = new CUnitAttackVisionFogModifier(this, playerIndex);
		computeAllDerivedFields();
	}

	public void performDefaultBehavior(final CSimulation game) {
		if (this.currentBehavior != null) {
			this.currentBehavior.end(game, true);
		}
		this.currentBehavior = this.defaultBehavior;
		this.currentBehavior.begin(game);
	}

	public void regeneratePathingInstance(final CSimulation game, final BufferedImage buildingPathingPixelMap) {
		float unitX = getX();
		float unitY = getY();
		unitX = (float) Math.floor(unitX / 64f) * 64f;
		unitY = (float) Math.floor(unitY / 64f) * 64f;
		if (((buildingPathingPixelMap.getWidth() / 2) % 2) == 1) {
			unitX += 32f;
		}
		if (((buildingPathingPixelMap.getHeight() / 2) % 2) == 1) {
			unitY += 32f;
		}
		this.pathingInstance = game.getPathingGrid().blitRemovablePathingOverlayTexture(unitX, unitY,
				270 /* no rotation, face forward */, buildingPathingPixelMap);
		setX(unitX);
		setY(unitY);
	}

	public float getLifeRegenBonus() {
		return this.lifeRegenBonus;
	}

	public void setLifeRegenStrengthBonus(final float lifeRegenStrengthBonus) {
		this.lifeRegenStrengthBonus = lifeRegenStrengthBonus;
		computeDerivedFields(NonStackingStatBuffType.HPGEN);
	}

	public void addNonStackingStatBuff(final NonStackingStatBuff buff) {
		if (buff.getBuffType() == NonStackingStatBuffType.ALLATK) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATK);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATK, buffKeyMap);
			}
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATK, buffKeyMap);
			}
			theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);
		} else if (buff.getBuffType() == NonStackingStatBuffType.ALLATKPCT) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATKPCT);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATKPCT, buffKeyMap);
			}
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT);
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATKPCT, buffKeyMap);
			}
			theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);
		} else {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs.get(buff.getBuffType());
			if (buffKeyMap == null) {
				buffKeyMap = new HashMap<>();
				this.nonStackingBuffs.put(buff.getBuffType(), buffKeyMap);
			}
			List<NonStackingStatBuff> theList = buffKeyMap.get(buff.getStackingKey());
			if (theList == null) {
				theList = new ArrayList<>();
				buffKeyMap.put(buff.getStackingKey(), theList);
			}
			theList.add(buff);
		}
		computeDerivedFields(buff.getBuffType());
	}

	public void removeNonStackingStatBuff(final NonStackingStatBuff buff) {
		if (buff.getBuffType() == NonStackingStatBuffType.ALLATK) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATK);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			} catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + this.getTypeId().asStringValue());
			}

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			} catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + this.getTypeId().asStringValue());
			}
		} else if (buff.getBuffType() == NonStackingStatBuffType.ALLATKPCT) {
			Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs
					.get(NonStackingStatBuffType.MELEEATKPCT);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			} catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + this.getTypeId().asStringValue());
			}

			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT);
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			} catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + this.getTypeId().asStringValue());
			}
		} else {
			final Map<String, List<NonStackingStatBuff>> buffKeyMap = this.nonStackingBuffs.get(buff.getBuffType());
			try {
				buffKeyMap.get(buff.getStackingKey()).remove(buff);
			} catch (final Exception e) {
				System.err.println(e.getLocalizedMessage());
				System.err.println(e.getStackTrace().toString());
				System.err.println("From: " + this.getTypeId().asStringValue());
			}
		}
		computeDerivedFields(buff.getBuffType());
	}

	public void addStateModBuff(StateModBuff listener) {
		if (!stateModBuffs.contains(listener)) {
			stateModBuffs.add(0, listener);
		}
	}

	public void removeStateModBuff(final StateModBuff listener) {
		stateModBuffs.remove(listener);
	}

	public void removeAllStateModBuffs(final StateModBuffType type) {
		for (int i = stateModBuffs.size() - 1; i >= 0; i--) {
			if (stateModBuffs.get(i).getBuffType() == type) {
				stateModBuffs.remove(i);
			}
		}
	}

	public void computeUnitState(final CSimulation game, final StateModBuffType type) {
		switch (type) {
		case DISABLE_ATTACK:
		case DISABLE_MELEE_ATTACK:
		case DISABLE_RANGED_ATTACK:
		case DISABLE_SPECIAL_ATTACK:
		case DISABLE_SPELLS:
		case ETHEREAL:
			boolean isDisableAttack = false;
			boolean isDisableMeleeAttack = false;
			boolean isDisableRangedAttack = false;
			boolean isDisableSpecialAttack = false;
			boolean isDisableSpells = false;
			boolean isEthereal = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.DISABLE_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableAttack = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.DISABLE_MELEE_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableMeleeAttack = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.DISABLE_RANGED_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableRangedAttack = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.DISABLE_SPECIAL_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableSpecialAttack = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.DISABLE_SPELLS) {
					if (buff.getValue() != 0) {
						isDisableSpells = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.ETHEREAL) {
					if (buff.getValue() != 0) {
						isEthereal = true;
					}
				}
			}
//			CAbility attack = this.getFirstAbilityOfType(CAbilityAttack.class);
//			if (attack != null) {
//				attack.setDisabled(isDisableAttack, CAbilityDisableType.ATTACKDISABLED);
//			}
			for (CAbility ability : this.abilities) {
				if (((isDisableAttack || isEthereal) && ability.getAbilityCategory() == CAbilityCategory.ATTACK)
						|| (isDisableSpells && ability.getAbilityCategory() == CAbilityCategory.SPELL
								&& !ability.isPhysical())
						|| (isEthereal && ability.isPhysical()
								&& (ability.getAbilityCategory() == CAbilityCategory.SPELL
										|| ability.getAbilityCategory() == CAbilityCategory.CORE))) {
					ability.setDisabled(true, CAbilityDisableType.ATTACKDISABLED);
				} else {
					ability.setDisabled(false, CAbilityDisableType.ATTACKDISABLED);
				}
			}
			List<CUnitAttack> newAttackList = new ArrayList<CUnitAttack>();
			for (int i = 0; i < this.unitSpecificAttacks.size(); i++) {
				CUnitAttack attack = this.unitSpecificAttacks.get(i);
				if ((this.getUnitType().getAttacksEnabled() & (i + 1)) != 0 && !isDisableAttack
						&& (!isDisableMeleeAttack || !attack.getWeaponType().equals(CWeaponType.NORMAL))
						&& (!isDisableRangedAttack || attack.getWeaponType().equals(CWeaponType.NORMAL))
						&& (!isDisableSpecialAttack || !((attack.getTargetsAllowed().size() == 1)
								&& attack.getTargetsAllowed().equals(EnumSet.of(CTargetType.TREE))))) {
					newAttackList.add(attack);
				}

			}
			this.setUnitSpecificCurrentAttacks(newAttackList);
			this.notifyAttacksChanged();
			this.checkDisabledAbilities(game, isDisableAttack);

			if (isEthereal) {
				if (!this.damageTakenModificationListeners.contains(CUnitDefaultEtherealDamageModListener.INSTANCE)) {
					this.addDamageTakenModificationListener(CUnitDefaultEtherealDamageModListener.INSTANCE);
				}
				game.changeUnitVertexColor(this, RenderUnit.ETHEREAL);
			} else {
				if (this.damageTakenModificationListeners.contains(CUnitDefaultEtherealDamageModListener.INSTANCE)) {
					this.removeDamageTakenModificationListener(CUnitDefaultEtherealDamageModListener.INSTANCE);
					game.changeUnitVertexColor(this, RenderUnit.DEFAULT);
				}
			}
			break;
		case DISABLE_AUTO_ATTACK:
			boolean isDisableAutoAttack = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.DISABLE_AUTO_ATTACK) {
					if (buff.getValue() != 0) {
						isDisableAutoAttack = true;
					}
				}
			}
			this.autoAttack = !isDisableAutoAttack;
			break;
		case MAGIC_IMMUNE:
			boolean isMagicImmune = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.MAGIC_IMMUNE) {
					if (buff.getValue() != 0) {
						isMagicImmune = true;
					}
				}
			}
			this.setMagicImmune(isMagicImmune);
			if (isMagicImmune) {
				if (!this.finalDamageTakenModificationListeners
						.contains(CUnitDefaultMagicImmuneDamageModListener.INSTANCE)) {
					this.addFinalDamageTakenModificationListener(CUnitDefaultMagicImmuneDamageModListener.INSTANCE);
				}
			} else {
				if (this.finalDamageTakenModificationListeners
						.contains(CUnitDefaultMagicImmuneDamageModListener.INSTANCE)) {
					this.removeFinalDamageTakenModificationListener(CUnitDefaultMagicImmuneDamageModListener.INSTANCE);
				}
			}
			break;
		case RESISTANT:
			boolean isResistant = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.RESISTANT) {
					if (buff.getValue() != 0) {
						isResistant = true;
					}
				}
			}
			this.resistant = isResistant;
			break;
		case SLEEPING:
		case STUN:
			boolean isSleeping = false;
			boolean isStun = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SLEEPING) {
					if (buff.getValue() != 0) {
						isSleeping = true;
					}
				}
				if (buff.getBuffType() == StateModBuffType.STUN) {
					if (buff.getValue() != 0) {
						isStun = true;
					}
				}
			}
			if (isSleeping || isStun) {
				if ((this.currentBehavior == null)
						|| (this.currentBehavior.getHighlightOrderId() != OrderIds.stunned)) {
					if (this.currentBehavior != null) {
						this.interruptedBehavior = this.currentBehavior;
						this.interruptedDefaultBehavior = this.defaultBehavior;
					}
					this.currentBehavior = new CBehaviorStun(this);
					this.currentBehavior.begin(game);
					setDefaultBehavior(this.currentBehavior);
					this.stateNotifier.ordersChanged();
				}
			} else {
				if (this.currentBehavior != null && this.currentBehavior.getHighlightOrderId() == OrderIds.stunned) {
					setDefaultBehavior(this.interruptedDefaultBehavior);
					this.currentBehavior = this.pollNextOrderBehavior(game);
					this.interruptedBehavior = null;
					this.interruptedDefaultBehavior = null;
					this.stateNotifier.ordersChanged();
				}
			}

			if (isSleeping) {
				if (!this.damageTakenListeners.contains(CUnitDefaultSleepListener.INSTANCE)) {
					this.addDamageTakenListener(CUnitDefaultSleepListener.INSTANCE);
				}
			} else {
				if (this.damageTakenListeners.contains(CUnitDefaultSleepListener.INSTANCE)) {
					this.removeDamageTakenListener(CUnitDefaultSleepListener.INSTANCE);
				}
			}
			break;
		case SNARED:
			boolean isSnared = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SNARED) {
					if (buff.getValue() != 0) {
						isSnared = true;
					}
				}
			}
			if (isSnared) {
				this.setFlyHeight(0);
				this.moveDisabled = true;
			} else {
				if (this.moveDisabled) {
					this.setFlyHeight(this.unitType.getDefaultFlyingHeight());
					this.moveDisabled = false;
				}
			}
			break;
		case INVULNERABLE:
			boolean isInvuln = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.INVULNERABLE) {
					if (buff.getValue() != 0) {
						isInvuln = true;
					}
				}
			}
			this.setInvulnerable(isInvuln);
			this.stateNotifier.abilitiesChanged();
			break;
		default:
			break;
		}
	}

	public void computeDerivedFields(final NonStackingStatBuffType type) {
		Map<String, List<NonStackingStatBuff>> buffKeyMap;
		switch (type) {
		case DEF:
		case DEFPCT:
			this.currentDefenseDisplay = this.unitType.getDefense() + this.agilityDefensePermanentBonus
					+ this.permanentDefenseBonus;

			float totalNSDefBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.DEF);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSDefBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.DEFPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSDefBuff += buffForKey * this.currentDefenseDisplay;
			}

			this.totalTemporaryDefenseBonus = this.temporaryDefenseBonus + this.agilityDefenseTemporaryBonus
					+ totalNSDefBuff;
			this.currentDefense = this.currentDefenseDisplay + this.totalTemporaryDefenseBonus;
			break;
		case HPGEN:
		case HPGENPCT:
		case MAXHPGENPCT:
			float totalNSHPGenBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPGEN);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey * (this.lifeRegen + this.lifeRegenBonus + this.lifeRegenStrengthBonus);
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSHPGenBuff += buffForKey * (this.maximumLife);
			}
			this.currentLifeRegenPerTick = (this.lifeRegen + this.lifeRegenBonus + this.lifeRegenStrengthBonus
					+ totalNSHPGenBuff) * WarsmashConstants.SIMULATION_STEP_TIME;
			break;
		case MPGEN:
		case MPGENPCT:
		case MAXMPGENPCT:
			float totalNSMPGenBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MPGEN);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey
						* (this.manaRegen + this.manaRegenBonus + this.manaRegenIntelligenceBonus);
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMPGENPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMPGenBuff += buffForKey * this.maximumMana;
			}
			this.currentManaRegenPerTick = (this.manaRegen + this.manaRegenBonus + this.manaRegenIntelligenceBonus
					+ totalNSMPGenBuff) * WarsmashConstants.SIMULATION_STEP_TIME;
			break;
		case MVSPDPCT:
		case MVSPD:
			float totalNSMvSpdBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MVSPD);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMvSpdBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MVSPDPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMvSpdBuff += buffForKey * this.speed;
			}
			this.speedBonus = Math.round(totalNSMvSpdBuff);
			break;
		case MELEEATK:
		case MELEEATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() == CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATKPCT));
					attack.computeDerivedFields();
				}
			}
			this.notifyAttacksChanged();
			break;
		case RNGDATK:
		case RNGDATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() != CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT));
					attack.computeDerivedFields();
				}
			}
			this.notifyAttacksChanged();
			break;
		case ALLATK:
		case ALLATKPCT:
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() == CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.MELEEATKPCT));
					attack.computeDerivedFields();
				}
			}
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				if ((attack.getWeaponType() != null) && (attack.getWeaponType() != CWeaponType.NORMAL)) {
					attack.setNonStackingFlatBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATK));
					attack.setNonStackingPctBuffs(this.nonStackingBuffs.get(NonStackingStatBuffType.RNGDATKPCT));
					attack.computeDerivedFields();
				}
			}
			this.notifyAttacksChanged();
			break;
		case ATKSPD:
			float totalNSAtkSpdBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.ATKSPD);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSAtkSpdBuff += buffForKey;
			}
			for (final CUnitAttack attack : getUnitSpecificAttacks()) {
				attack.setAttackSpeedModifier(totalNSAtkSpdBuff);
			}
			this.notifyAttacksChanged();
			break;
		case HPSTEAL:
			float totalNSVampBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.HPSTEAL);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSVampBuff += buffForKey;
			}
			if (this.lifestealListener != null) {
				this.lifestealListener.setAmount(totalNSVampBuff);
			} else {
				this.lifestealListener = new CUnitDefaultLifestealListener(totalNSVampBuff);
				this.addPostDamageListener(this.lifestealListener);
			}
			break;
		case THORNS:
			float totalNSThornsBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.THORNS);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSThornsBuff += buffForKey;
			}
			if (this.flatThornsListener != null) {
				this.flatThornsListener.setAmount(totalNSThornsBuff);
			} else {
				this.flatThornsListener = new CUnitDefaultThornsListener(false, totalNSThornsBuff);
				this.addDamageTakenListener(this.flatThornsListener);
			}
			break;
		case THORNSPCT:
			float totalNSThornsPctBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.THORNSPCT);
			for (final String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (final NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSThornsPctBuff += buffForKey;
			}
			if (this.percentThornsListener != null) {
				this.percentThornsListener.setAmount(totalNSThornsPctBuff);
			} else {
				this.percentThornsListener = new CUnitDefaultThornsListener(true, totalNSThornsPctBuff);
				this.addDamageTakenListener(this.percentThornsListener);
			}
			break;
		case MAXHPPCT:
		case MAXHP:
			float totalNSMaxHPBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHP);
			for (String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxHPBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXHPPCT);
			for (String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxHPBuff += buffForKey * this.baseMaximumLife;
			}
			int newMaxLife = this.baseMaximumLife + Math.round(totalNSMaxHPBuff);
			if (newMaxLife > this.maximumLife) {
				this.life += newMaxLife - this.maximumLife;
				this.maximumLife = newMaxLife;
			} else {
				this.maximumLife = newMaxLife;
				this.life = Math.min(this.life, this.maximumLife);
			}
			break;
		case MAXMPPCT:
		case MAXMP:
			float totalNSMaxMPBuff = 0;
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMP);
			for (String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxMPBuff += buffForKey;
			}
			buffKeyMap = this.nonStackingBuffs.get(NonStackingStatBuffType.MAXMPPCT);
			for (String key : buffKeyMap.keySet()) {
				Float buffForKey = null;
				for (NonStackingStatBuff buff : buffKeyMap.get(key)) {
					if (buffForKey == null) {
						buffForKey = buff.getValue();
					} else {
						if (key.equals(NonStackingStatBuff.ALLOW_STACKING_KEY)) {
							buffForKey += buff.getValue();
						} else {
							buffForKey = Math.max(buffForKey, buff.getValue());
						}
					}
				}
				if (buffForKey == null) {
					continue;
				}
				totalNSMaxMPBuff += buffForKey * this.baseMaximumMana;
			}
			int newMaxMana = this.baseMaximumMana + Math.round(totalNSMaxMPBuff);
			if (newMaxMana > this.maximumMana) {
				this.mana += newMaxMana - this.maximumMana;
				this.maximumMana = newMaxMana;
			} else {
				this.maximumMana = newMaxMana;
				this.mana = Math.min(this.mana, this.maximumMana);
			}
			break;
		default:
			break;
		}
	}

	public NonStackingFx addNonStackingFx(final CSimulation game, final String stackingKey, final War3ID id,
			final CEffectType target) {
		List<NonStackingFx> existingArts = nonStackingFx.get(stackingKey);
		final NonStackingFx newFx = new NonStackingFx(stackingKey, id);
		if (existingArts == null) {
			existingArts = new ArrayList<>();
			nonStackingFx.put(stackingKey, existingArts);
		}
		if (existingArts.isEmpty()) {
			final SimulationRenderComponent fx = game.createPersistentSpellEffectOnUnit(this, id, target);
			newFx.setArt(fx);
		} else {
			newFx.setArt(existingArts.iterator().next().getArt());
		}
		existingArts.add(newFx);
		return newFx;
	}

	public void removeNonStackingFx(final CSimulation game, final NonStackingFx fx) {
		final List<NonStackingFx> existingArts = nonStackingFx.get(fx.getStackingKey());
		if (existingArts != null) {
			existingArts.remove(fx);
			if (existingArts.isEmpty()) {
				fx.getArt().remove();
			}
		}
	}

	public void addNonStackingDisplayBuff(final CSimulation game, final String stackingKey, final CBuff buff) {
		List<CBuff> existingBuffs = nonStackingDisplayBuffs.get(stackingKey);
		if (existingBuffs == null) {
			existingBuffs = new ArrayList<>();
			nonStackingDisplayBuffs.put(stackingKey, existingBuffs);
		}
		if (existingBuffs.isEmpty()) {
			this.add(game, buff);
		} else {
			final CBuff currentBuff = this.getFirstAbilityOfType(buff.getClass());
			if (currentBuff == null) {
				existingBuffs.clear();
				this.add(game, buff);
			} else {
				if (buff.getLevel() >= currentBuff.getLevel()) {
					this.remove(game, currentBuff);
					this.add(game, buff);
				}

			}
		}
		existingBuffs.add(buff);
	}

	public void removeNonStackingDisplayBuff(final CSimulation game, final String stackingKey, final CBuff buff) {
		final List<CBuff> existingBuffs = nonStackingDisplayBuffs.get(stackingKey);
		if (existingBuffs != null) {
			existingBuffs.remove(buff);
			CBuff currentBuff = this.getFirstAbilityOfType(buff.getClass());
			if (currentBuff == buff) {
				this.remove(game, currentBuff);
				if (!existingBuffs.isEmpty()) {
					currentBuff = null;
					for (final CBuff iterBuff : existingBuffs) {
						if ((currentBuff == null) || (currentBuff.getLevel() < iterBuff.getLevel())) {
							currentBuff = iterBuff;
						}
					}
					this.add(game, currentBuff);
				}
			}
		}
	}

	private void initializeNonStackingBuffs() {
		this.nonStackingBuffs.put(NonStackingStatBuffType.ATKSPD, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.DEF, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.DEFPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPGEN, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.HPSTEAL, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATK, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MELEEATKPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MPGEN, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMPGENPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MVSPDPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MVSPD, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATK, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.RNGDATKPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.THORNS, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.THORNSPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHP, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXHPPCT, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMP, new HashMap<>(1));
		this.nonStackingBuffs.put(NonStackingStatBuffType.MAXMPPCT, new HashMap<>(1));
	}

	private void initializeListenerLists() {
		for (final CUnitAttackPreDamageListenerPriority priority : CUnitAttackPreDamageListenerPriority.values()) {
			preDamageListeners.put(priority, new ArrayList<>());
		}
		for (final CUnitDeathReplacementEffectPriority priority : CUnitDeathReplacementEffectPriority.values()) {
			deathReplacementEffects.put(priority, new ArrayList<>());
		}
	}

	private void computeAllDerivedFields() {
		this.baseLifeRegenPerTick = this.lifeRegen * WarsmashConstants.SIMULATION_STEP_TIME;
		computeDerivedFields(NonStackingStatBuffType.DEF);
		computeDerivedFields(NonStackingStatBuffType.HPGEN);
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
		if (this.getUnitSpecificAttacks() != null) {
			computeDerivedFields(NonStackingStatBuffType.ALLATK);
			computeDerivedFields(NonStackingStatBuffType.ATKSPD);
		}
		computeDerivedFields(NonStackingStatBuffType.HPSTEAL);
		computeDerivedFields(NonStackingStatBuffType.MVSPD);
		computeDerivedFields(NonStackingStatBuffType.THORNS);
		computeDerivedFields(NonStackingStatBuffType.THORNSPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXHP);
		computeDerivedFields(NonStackingStatBuffType.MAXMP);
	}

	private void computeAllUnitStates(CSimulation game) {
		this.computeUnitState(game, StateModBuffType.INVULNERABLE);
		this.computeUnitState(game, StateModBuffType.ETHEREAL);
		this.computeUnitState(game, StateModBuffType.DISABLE_AUTO_ATTACK);
		this.computeUnitState(game, StateModBuffType.MAGIC_IMMUNE);
		this.computeUnitState(game, StateModBuffType.RESISTANT);
		this.computeUnitState(game, StateModBuffType.SNARED);
	}

	public void setManaRegenIntelligenceBonus(final float manaRegenIntelligenceBonus) {
		this.manaRegenIntelligenceBonus = manaRegenIntelligenceBonus;
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	public void setLifeRegenBonus(final float lifeRegenBonus) {
		this.lifeRegenBonus = lifeRegenBonus;
		computeDerivedFields(NonStackingStatBuffType.HPGEN);
	}

	public void setManaRegenBonus(final float manaRegenBonus) {
		this.manaRegenBonus = manaRegenBonus;
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	public void setManaRegen(final float manaRegen) {
		this.manaRegen = manaRegen;
		computeDerivedFields(NonStackingStatBuffType.MPGEN);
	}

	public float getManaRegenBonus() {
		return this.manaRegenBonus;
	}

	public float getManaRegen() {
		return this.manaRegen;
	}

	public void setAgilityDefensePermanentBonus(final int agilityDefensePermanentBonus) {
		this.agilityDefensePermanentBonus = agilityDefensePermanentBonus;
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	public void setAgilityDefenseTemporaryBonus(final float agilityDefenseTemporaryBonus) {
		this.agilityDefenseTemporaryBonus = agilityDefenseTemporaryBonus;
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	public void setPermanentDefenseBonus(final int permanentDefenseBonus) {
		this.permanentDefenseBonus = permanentDefenseBonus;
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	public int getPermanentDefenseBonus() {
		return this.permanentDefenseBonus;
	}

	public void setTemporaryDefenseBonus(final float temporaryDefenseBonus) {
		this.temporaryDefenseBonus = temporaryDefenseBonus;
		computeDerivedFields(NonStackingStatBuffType.DEF);
	}

	public float getTemporaryDefenseBonus() {
		return this.temporaryDefenseBonus;
	}

	public float getTotalTemporaryDefenseBonus() {
		return this.totalTemporaryDefenseBonus;
	}

	public int getCurrentDefenseDisplay() {
		return this.currentDefenseDisplay;
	}

	public void setUnitAnimationListener(final CUnitAnimationListener unitAnimationListener) {
		this.unitAnimationListener = unitAnimationListener;
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
	}

	public CUnitAnimationListener getUnitAnimationListener() {
		return this.unitAnimationListener;
	}

	public void add(final CSimulation simulation, final CAbility ability) {
		if (!ability.isRequirementsMet(simulation, this)) {
			ability.setDisabled(true, CAbilityDisableType.REQUIREMENTS);
		}
		if (ability.isDisabled()) {
			this.disabledAbilities.add(ability);
			this.abilities.add(ability);
			simulation.onAbilityAddedToUnit(this, ability);
			ability.onAddDisabled(simulation, this);
			this.stateNotifier.abilitiesChanged();
		} else {
			this.abilities.add(ability);
			simulation.onAbilityAddedToUnit(this, ability);
			ability.onAddDisabled(simulation, this);
			ability.onAdd(simulation, this);
			this.stateNotifier.abilitiesChanged();
		}
	}

	public void add(final CSimulation simulation, final CBuff ability) {
		if (this.abilities.contains(ability) && (ability instanceof ABGenericTimedBuff)) {
			((ABGenericTimedBuff) ability).updateExpiration(simulation, this);
		} else {
			this.abilities.add(ability);
			simulation.onAbilityAddedToUnit(this, ability);
			ability.onAdd(simulation, this);
			this.stateNotifier.abilitiesChanged();
		}
	}

	public void remove(final CSimulation simulation, final CAbility ability) {
		if (this.disabledAbilities.contains(ability)) {
			this.abilities.remove(ability);
			this.disabledAbilities.remove(ability);
			simulation.onAbilityRemovedFromUnit(this, ability);
			ability.onRemoveDisabled(simulation, this);
			this.stateNotifier.abilitiesChanged();
		} else {
			this.abilities.remove(ability);
			simulation.onAbilityRemovedFromUnit(this, ability);
			ability.onRemove(simulation, this);
			ability.onRemoveDisabled(simulation, this);
			this.stateNotifier.abilitiesChanged();
		}
	}

	public void remove(final CSimulation simulation, final CBuff ability) {
		this.abilities.remove(ability);
		simulation.onAbilityRemovedFromUnit(this, ability);
		ability.onRemove(simulation, this);
		this.stateNotifier.abilitiesChanged();
	}

	public void checkDisabledAbilities(final CSimulation simulation, final boolean disable) {
		if (disable) {
			for (CAbility ability : this.abilities) {
				if (!ability.isRequirementsMet(simulation, this)) {
					ability.setDisabled(true, CAbilityDisableType.REQUIREMENTS);
				}
				if (ability.isDisabled() && !this.disabledAbilities.contains(ability)) {
//					System.err.println("Disabling ability: " + ability.getAlias().asStringValue());
					this.disabledAbilities.add(ability);
					ability.onRemove(simulation, this);
				}
			}
		} else {
			for (CAbility ability : new ArrayList<>(this.disabledAbilities)) {
				if (ability.isRequirementsMet(simulation, this)) {
					ability.setDisabled(false, CAbilityDisableType.REQUIREMENTS);
				}
				if (!ability.isDisabled()) {
					ability.onAdd(simulation, this);
					this.disabledAbilities.remove(ability);
				}
			}
		}
	}

	public War3ID getTypeId() {
		return this.typeId;
	}

	/**
	 * @return facing in DEGREES
	 */
	public float getFacing() {
		return this.facing;
	}

	public float getMana() {
		return this.mana;
	}

	public int getMaximumMana() {
		return this.maximumMana;
	}

	public int getMaximumLife() {
		return this.maximumLife;
	}

	public void setTypeId(final CSimulation game, final War3ID typeId) {
		setTypeId(game, typeId, true);
	}

	public void setTypeId(final CSimulation game, final War3ID typeId, boolean updateArt) {
		game.getWorldCollision().removeUnit(this);
		CPlayer player = game.getPlayer(this.playerIndex);
		player.removeTechtreeUnlocked(game, this.typeId);
		this.typeId = typeId;
		player.addTechtreeUnlocked(game, this.typeId);
		final float lifeRatio = this.maximumLife == 0 ? 1 : this.life / this.maximumLife;
		final float manaRatio = this.maximumMana == 0 ? Float.NaN : this.mana / this.maximumMana;
		final CUnitType previousUnitType = getUnitType();
		this.unitType = game.getUnitData().getUnitType(typeId);
		if (!Float.isNaN(manaRatio)) {
			this.maximumMana = this.unitType.getManaMaximum();
		}
		this.maximumLife = this.unitType.getMaxLife();
		this.life = lifeRatio * this.maximumLife;
		this.lifeRegen = this.unitType.getLifeRegen();
		this.manaRegen = this.unitType.getManaRegen();
		if (updateArt) {
			this.flyHeight = this.unitType.getDefaultFlyingHeight();
		}
		this.speed = this.unitType.getSpeed();
		this.classifications.clear();
		this.classifications.addAll(this.unitType.getClassifications());
		this.defenseType = this.unitType.getDefenseType();
		this.acquisitionRange = this.unitType.getDefaultAcquisitionRange();
		this.structure = this.unitType.isBuilding();
		this.raisable = this.unitType.isRaise();
		this.decays = this.unitType.isDecay();
		final List<War3ID> sharedAbilities = new ArrayList<War3ID>(previousUnitType.getAbilityList());
		sharedAbilities.addAll(previousUnitType.getHeroAbilityList());
		final List<War3ID> newIds = new ArrayList<War3ID>(this.unitType.getAbilityList());
		newIds.addAll(this.unitType.getHeroAbilityList());
		sharedAbilities.retainAll(newIds); // TODO Seems wasteful, but need to avoid messing up heros on transform
		final List<CAbility> persistedAbilities = new ArrayList<>();
		final List<CAbility> removedAbilities = new ArrayList<>();
		for (final CAbility ability : this.abilities) {
			if (!ability.isPermanent() && !sharedAbilities.contains(ability.getAlias())
					&& !(ability.getAbilityCategory() == CAbilityCategory.BUFF)) {
				ability.onRemove(game, this);
				game.onAbilityRemovedFromUnit(this, ability);
				removedAbilities.add(ability);
			} else {
				persistedAbilities.add(ability);
			}
		}
		for (final CAbility removed : removedAbilities) {
			this.abilities.remove(removed); // TODO remove inefficient O(N) search
		}
		game.unitUpdatedType(this, typeId);
		game.getUnitData().addMissingDefaultAbilitiesToUnit(game, game.getHandleIdAllocator(), this.unitType, false, -1,
				this.speed, this);
		if (Float.isNaN(manaRatio)) {
			this.mana = this.unitType.getManaInitial();
		} else {
			this.mana = manaRatio * this.maximumMana;
		}
		game.getWorldCollision().addUnit(this);
		for (final CAbility ability : persistedAbilities) {
			ability.onSetUnitType(game, this);
			game.onAbilityAddedToUnit(this, ability);
		}
		computeAllDerivedFields();
		this.computeAllUnitStates(game);
	}

	public void setFacing(final float facing) {
		// java modulo output can be negative, but not if we
		// force positive and modulo again
		this.facing = ((facing % 360) + 360) % 360;
	}

	public void setMana(final float mana) {
		this.mana = mana;
		this.stateNotifier.manaChanged();
	}

	public void setMaximumLife(final int maximumLife) {
		this.baseMaximumLife = maximumLife;
		computeDerivedFields(NonStackingStatBuffType.MAXHPPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXHPGENPCT);
	}

	public void addMaxLifeRelative(final CSimulation game, final int hitPointBonus) {
		final int oldMaximumLife = getMaximumLife();
		final float oldLife = getLife();
		final int newMaximumLife = oldMaximumLife + hitPointBonus;
		final float newLife = (oldLife * (newMaximumLife)) / oldMaximumLife;
		setMaximumLife(newMaximumLife);
		setLife(game, newLife);
	}

	public void setMaximumMana(final int maximumMana) {
		this.baseMaximumMana = maximumMana;
		computeDerivedFields(NonStackingStatBuffType.MAXMPPCT);
		computeDerivedFields(NonStackingStatBuffType.MAXMPGENPCT);
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
		computeDerivedFields(NonStackingStatBuffType.MVSPD);
	}

	public int getSpeed() {
		return this.speed + this.speedBonus;
	}

	/**
	 * Updates one tick of simulation logic and return true if it's time to remove
	 * this unit from the game.
	 */
	public boolean update(final CSimulation game) {
		for (final StateListenerUpdate update : this.stateListenersUpdates) {
			switch (update.getUpdateType()) {
			case ADD:
				this.stateNotifier.subscribe(update.listener);
				break;
			case REMOVE:
				this.stateNotifier.unsubscribe(update.listener);
				break;
			}
		}
		this.stateListenersUpdates.clear();
		if (isDead()) {
			if (!falseDeath) {
				final int gameTurnTick = game.getGameTurnTick();
				if (!this.corpse) {
					if (this.collisionRectangle != null) {
						// Moved this here because doing it on "kill" was able to happen in some cases
						// while also iterating over the units that are in the collision system, and
						// then it hit the "writing while iterating" problem.
						game.getWorldCollision().removeUnit(this);
					}
					if (gameTurnTick > (this.deathTurnTick
							+ (int) (this.unitType.getDeathTime() / WarsmashConstants.SIMULATION_STEP_TIME))) {
						this.corpse = true;
						if (!this.isRaisable()) {
							this.boneCorpse = true;
							// start final phase immediately for "cant raise" case
						}
						if (!this.unitType.isHero()) {
							if (!this.isDecays()) {
								// if we dont raise AND dont decay, then now that death anim is over
								// we just delete the unit
								return true;
							}
						} else {
							game.heroDeathEvent(this);
						}
						this.deathTurnTick = gameTurnTick;
					}
				} else if (!this.boneCorpse) {
					if (game.getGameTurnTick() > (this.deathTurnTick + (int) (game.getGameplayConstants().getDecayTime()
							/ WarsmashConstants.SIMULATION_STEP_TIME))) {
						this.boneCorpse = true;
						this.deathTurnTick = gameTurnTick;

						if (this.isRaisable()) {
							game.getWorldCollision().addUnit(this);
						}
					}
				} else if (game.getGameTurnTick() > (this.deathTurnTick
						+ (int) (getEndingDecayTime(game) / WarsmashConstants.SIMULATION_STEP_TIME))) {
					if (this.unitType.isHero()) {
						if (!getHeroData().isAwaitingRevive()) {
							setHidden(true);
							getHeroData().setAwaitingRevive(true);
							game.heroDissipateEvent(this);
						}
						return false;
					}
					return true;
				}
			}
		} else {
			if (!this.paused) {
				if ((this.rallyPoint != this) && (this.rallyPoint instanceof CUnit)
						&& ((CUnit) this.rallyPoint).isDead()) {
					setRallyPoint(this);
				}
				if (this.constructing) {
					if (!this.constructingPaused) {
						this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
					}
					final int buildTime;
					final boolean upgrading = isUpgrading();
					if (!upgrading) {
						buildTime = this.unitType.getBuildTime();
						if (!this.constructingPaused) {
							final float healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / buildTime)
									* (this.maximumLife * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
							setLife(game, Math.min(this.life + healthGain, this.maximumLife));
						}
					} else {
						buildTime = game.getUnitData().getUnitType(this.upgradeIdType).getBuildTime();
					}
					if (this.constructionProgress >= buildTime) {
						this.constructing = false;
						this.constructingPaused = false;
						this.constructionProgress = 0;
						if (this.constructionConsumesWorker) {
							if (this.workerInside != null) {
								game.removeUnit(this.workerInside);
								this.workerInside = null;
							}
						} else {
							popoutWorker(game);
						}
						final Iterator<CAbility> abilityIterator = this.abilities.iterator();
						while (abilityIterator.hasNext()) {
							final CAbility ability = abilityIterator.next();
							if (ability instanceof CAbilityBuildInProgress) {
								abilityIterator.remove();
							} else {
								ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
								ability.setIconShowing(true);
							}
						}
						this.checkDisabledAbilities(game, false);
						final CPlayer player = game.getPlayer(this.playerIndex);
						if (upgrading) {
							if (this.unitType.getFoodMade() != 0) {
								player.setFoodCap(player.getFoodCap() - this.unitType.getFoodMade());
							}
							setTypeId(game, this.upgradeIdType);
							this.upgradeIdType = null;
						}
						if (this.unitType.getFoodMade() != 0) {
							player.setFoodCap(player.getFoodCap() + this.unitType.getFoodMade());
						}
						player.removeTechtreeInProgress(this.unitType.getTypeId());
						player.addTechtreeUnlocked(game, this.unitType.getTypeId());
						if (!upgrading) {
							game.unitConstructFinishEvent(this);
							fireConstructFinishEvents(game);
						} else {
							game.unitUpgradeFinishEvent(this);
						}
						if (upgrading || true) {
							// TODO shouldnt need to play stand here, probably
							getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
									true);
						}
						this.stateNotifier.ordersChanged();
					}
				} else {
					final War3ID queuedRawcode = this.buildQueue[0];
					if (queuedRawcode != null) {
						// queue step forward
						if (this.queuedUnitFoodPaid) {
							this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
						} else {
							if (this.buildQueueTypes[0] == QueueItemType.UNIT) {
								final CPlayer player = game.getPlayer(this.playerIndex);
								final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
								if (trainedUnitType.getFoodUsed() != 0) {
									final int newFoodUsed = player.getFoodUsed() + trainedUnitType.getFoodUsed();
									if (newFoodUsed <= player.getFoodCap()) {
										player.setFoodUsed(newFoodUsed);
										this.queuedUnitFoodPaid = true;
									}
								} else {
									this.queuedUnitFoodPaid = true;
								}
							} else if (this.buildQueueTypes[0] == QueueItemType.SACRIFICE) {
								this.queuedUnitFoodPaid = true;
							} else if (this.buildQueueTypes[0] == QueueItemType.HERO_REVIVE) {
								final CPlayer player = game.getPlayer(this.playerIndex);
								final CUnitType trainedUnitType = game.getUnit(queuedRawcode.getValue()).getUnitType();
								final int newFoodUsed = player.getFoodUsed() + trainedUnitType.getFoodUsed();
								if (newFoodUsed <= player.getFoodCap()) {
									player.setFoodUsed(newFoodUsed);
									this.queuedUnitFoodPaid = true;
								}
							} else {
								this.queuedUnitFoodPaid = true;
								System.err.println(
										"Unpaid food for non unit queue item ???? Attempting to correct this by setting paid=true");
							}
						}
						if (this.buildQueueTypes[0] == QueueItemType.UNIT) {
							final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
								this.constructionProgress = 0;
								final CUnit trainedUnit = game.createUnit(queuedRawcode, this.playerIndex, getX(),
										getY(), game.getGameplayConstants().getBuildingAngle());
								// dont add food cost to player 2x
								trainedUnit.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(trainedUnit, trainedUnitType.getFoodMade());
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechtreeUnlocked(game, queuedRawcode);
								fireTrainFinishEvents(game, trainedUnit);
								// nudge the trained unit out around us
								trainedUnit.nudgeAround(game, this);
								game.unitTrainedEvent(this, trainedUnit);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											trainedUnit, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						} else if (this.buildQueueTypes[0] == QueueItemType.SACRIFICE) {
							final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
								this.constructionProgress = 0;
								final CUnit trainedUnit = game.createUnit(queuedRawcode, this.playerIndex, getX(),
										getY(), game.getGameplayConstants().getBuildingAngle());

								game.removeUnit(this.getWorkerInside());

								// dont add food cost to player 2x
								trainedUnit.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(trainedUnit, trainedUnitType.getFoodMade());
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechtreeUnlocked(game, queuedRawcode);
								fireTrainFinishEvents(game, trainedUnit);
								// nudge the trained unit out around us
								trainedUnit.nudgeAround(game, this);
								game.unitTrainedEvent(this, trainedUnit);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											trainedUnit, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						} else if (this.buildQueueTypes[0] == QueueItemType.HERO_REVIVE) {
							final CUnit revivingHero = game.getUnit(queuedRawcode.getValue());
							final CUnitType trainedUnitType = revivingHero.getUnitType();
							final CGameplayConstants gameplayConstants = game.getGameplayConstants();
							if (this.constructionProgress >= gameplayConstants.getHeroReviveTime(
									trainedUnitType.getBuildTime(), revivingHero.getHeroData().getHeroLevel())) {
								this.constructionProgress = 0;
								revivingHero.getHeroData().setReviving(false);
								revivingHero.getHeroData().setAwaitingRevive(false);
								revivingHero.corpse = false;
								revivingHero.boneCorpse = false;
								revivingHero.deathTurnTick = 0;
								revivingHero.setX(getX());
								revivingHero.setY(getY());
								game.getWorldCollision().addUnit(revivingHero);
								revivingHero.setPoint(getX(), getY(), game.getWorldCollision(),
										game.getRegionManager());
								revivingHero.setHidden(false);
								revivingHero.setLife(game,
										revivingHero.getMaximumLife() * gameplayConstants.getHeroReviveLifeFactor());
								revivingHero.setMana(
										(revivingHero.getMaximumMana() * gameplayConstants.getHeroReviveManaFactor())
												+ (gameplayConstants.getHeroReviveManaStart()
														* trainedUnitType.getManaInitial()));
								// dont add food cost to player 2x
								revivingHero.setFoodUsed(trainedUnitType.getFoodUsed());
								final CPlayer player = game.getPlayer(this.playerIndex);
								player.setUnitFoodMade(revivingHero, trainedUnitType.getFoodMade());
								// NOTE: Dont "add techtree unlocked" here, because hero doesn't lose that
								// status upon death
								// nudge the trained unit out around us
								revivingHero.nudgeAround(game, this);
								game.unitRepositioned(revivingHero); // dont blend animation
								game.heroReviveEvent(this, revivingHero);
								if (this.rallyPoint != null) {
									final int rallyOrderId = OrderIds.smart;
									this.rallyPoint.visit(UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game,
											revivingHero, rallyOrderId));
								}
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								this.stateNotifier.queueChanged();
							}
						} else if (this.buildQueueTypes[0] == QueueItemType.RESEARCH) {
							final CUpgradeType trainedUnitType = game.getUpgradeData().getType(queuedRawcode);
							// TODO the "getBuildTime" math below probably would be better served to have
							// been cached, for performance, since we are in the update method. But maybe it
							// doens't matter.
							final CPlayer player = game.getPlayer(this.playerIndex);
							final int techtreeUnlocked = player.getTechtreeUnlocked(queuedRawcode);
							if (this.constructionProgress >= trainedUnitType.getBuildTime(techtreeUnlocked)) {
								this.constructionProgress = 0;
								for (int i = 0; i < (this.buildQueue.length - 1); i++) {
									setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
								}
								setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
								player.removeTechtreeInProgress(queuedRawcode);
								player.addTechResearched(game, queuedRawcode, 1);
								fireResearchFinishEvents(game, queuedRawcode);
								game.researchFinishEvent(this, queuedRawcode,
										player.getTechtreeUnlocked(queuedRawcode));
								this.stateNotifier.queueChanged();
							}
						}
					}
					if (this.life < this.maximumLife || this.currentLifeRegenPerTick < 0) {
						final CRegenType lifeRegenType = getUnitType().getLifeRegenType();
						boolean active = false;
						switch (lifeRegenType) {
						case ALWAYS:
							active = true;
							break;
						case DAY:
							active = game.isDay();
							break;
						case NIGHT:
							active = game.isNight();
							break;
						case BLIGHT:
							active = PathingFlags.isPathingFlag(game.getPathingGrid().getPathing(getX(), getY()),
									PathingFlags.BLIGHTED);
							break;
						default:
							active = false;
						}
						if (active) {
							float lifePlusRegen = this.life + this.currentLifeRegenPerTick;
							if (lifePlusRegen > this.maximumLife) {
								lifePlusRegen = this.maximumLife;
							}
							this.life = lifePlusRegen;
							this.stateNotifier.lifeChanged();
						} else {
							float lifePlusRegen = (this.life + this.currentLifeRegenPerTick)
									- this.baseLifeRegenPerTick;
							if (lifePlusRegen > this.maximumLife) {
								lifePlusRegen = this.maximumLife;
							}
							this.life = lifePlusRegen;
							this.stateNotifier.lifeChanged();
						}
					}
					if (this.mana < this.maximumMana || this.currentManaRegenPerTick < 0) {
						float manaPlusRegen = this.mana + this.currentManaRegenPerTick;
						if (manaPlusRegen > this.maximumMana) {
							manaPlusRegen = this.maximumMana;
						}
						this.mana = Math.max(manaPlusRegen, 0);
						this.stateNotifier.manaChanged();
					}
					if (this.currentBehavior != null) {
						CUnit target = this.currentBehavior.visit(BehaviorTargetUnitVisitor.INSTANCE);
						if (target != null && !game.getPlayer(playerIndex).hasAlliance(target.getPlayerIndex(),
								CAllianceType.SHARED_VISION)) {
							if (attackFogMod.getPlayerIndex() != target.getPlayerIndex()) {
								game.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(game,
										this.attackFogMod);
								this.attackFogMod.setPlayerIndex(target.getPlayerIndex());
								game.getPlayer(target.getPlayerIndex()).addFogModifer(game, this.attackFogMod);
							}
						} else {
							if (this.attackFogMod.getPlayerIndex() != this.playerIndex) {
								game.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(game,
										this.attackFogMod);
								this.attackFogMod.setPlayerIndex(playerIndex);
							}
						}

						final CBehavior lastBehavior = this.currentBehavior;
						final int lastBehaviorHighlightOrderId = lastBehavior.getHighlightOrderId();
						this.currentBehavior = this.currentBehavior.update(game);
						if (lastBehavior != this.currentBehavior) {
							lastBehavior.end(game, false);
							if (this.currentBehavior != null) {
								this.currentBehavior.begin(game);
							}
						}
						if ((this.currentBehavior != null)
								&& (this.currentBehavior.getHighlightOrderId() != lastBehaviorHighlightOrderId)) {
							this.stateNotifier.ordersChanged();
						}
					} else {
						// check to auto acquire targets
						autoAcquireTargets(game, this.moveDisabled);
					}
//					for (CAbility ability : new ArrayList<>(this.abilities)) {
//						ability.onTick(game, this);
//					}
					for (int i = this.abilities.size() - 1; i >= 0; i--) {
						// okay if it removes self from this during onTick() because of reverse
						// iteration order
						this.abilities.get(i).onTick(game, this);
					}
				}
			} else if (!this.constructing) {
				// Paused units only allow passives to function. Buffs don't tick (except a few)
				// Base and bonus life/mana regen function, but regen from Str/Int doesn't
				if (this.life < this.maximumLife || (this.currentLifeRegenPerTick - this.lifeRegenStrengthBonus) < 0) {
					final CRegenType lifeRegenType = getUnitType().getLifeRegenType();
					boolean active = false;
					switch (lifeRegenType) {
					case ALWAYS:
						active = true;
						break;
					case DAY:
						active = game.isDay();
						break;
					case NIGHT:
						active = game.isNight();
						break;
					case BLIGHT:
						active = PathingFlags.isPathingFlag(game.getPathingGrid().getPathing(getX(), getY()),
								PathingFlags.BLIGHTED);
						break;
					default:
						active = false;
					}
					if (active) {
						float lifePlusRegen = (this.life + this.currentLifeRegenPerTick)
								- (this.lifeRegenStrengthBonus * WarsmashConstants.SIMULATION_STEP_TIME);
						if (lifePlusRegen > this.maximumLife) {
							lifePlusRegen = this.maximumLife;
						}
						this.life = lifePlusRegen;
						this.stateNotifier.lifeChanged();
					}
				}
				if (this.mana < this.maximumMana
						|| (this.currentManaRegenPerTick - this.manaRegenIntelligenceBonus) < 0) {
					float manaPlusRegen = this.mana + this.currentManaRegenPerTick
							- this.manaRegenIntelligenceBonus * WarsmashConstants.SIMULATION_STEP_TIME;
					if (manaPlusRegen > this.maximumMana) {
						manaPlusRegen = this.maximumMana;
					}
					this.mana = Math.max(manaPlusRegen, 0);
					this.stateNotifier.manaChanged();
				}

				for (int i = this.abilities.size() - 1; i >= 0; i--) {
					// okay if it removes self from this during onTick() because of reverse
					// iteration order
					if (this.abilities.get(i) instanceof AbilityGenericSingleIconPassiveAbility
							|| this.abilities.get(i) instanceof ABTimedTickingPausedBuff) {
						this.abilities.get(i).onTick(game, this);
					}
				}
			}
		}
		return false;

	}

	private void popoutWorker(final CSimulation game) {
		if (this.workerInside != null) {
			this.workerInside.setInvulnerable(false);
			this.workerInside.setHidden(false);
			this.workerInside.setPaused(false);
			this.workerInside.nudgeAround(game, this);
			if (this.constructionConsumesWorker) {
				game.getPlayer(this.workerInside.getPlayerIndex()).setUnitFoodUsed(this.workerInside,
						this.workerInside.getUnitType().getFoodUsed());
			}
			this.workerInside = null;
		}
	}

	public boolean autoAcquireTargets(final CSimulation game, final boolean disableMove) {
		boolean autocast = autoAcquireAutocastTargets(game, disableMove);
		if (!autocast) {
			if (autoAttack) {
				return autoAcquireAttackTargets(game, disableMove);
			}
		}
		return autocast;
	}

	public boolean autoAcquireAutocastTargets(final CSimulation game, final boolean disableMove) {
		if (autocastAbility != null && !autocastAbility.isDisabled()) {
			if (autocastAbility.getAutocastType() == AutocastType.NOTARGET) {
				final BooleanAbilityTargetCheckReceiver<Void> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<Void>getInstance().reset();
				autocastAbility.checkCanAutoTargetNoTarget(game, this, autocastAbility.getBaseOrderId(),
						booleanTargetReceiver);
				if (booleanTargetReceiver.isTargetable()) {
					return this.order(game, autocastAbility.getBaseOrderId(), null);
				}
			} else if (autocastAbility.getAutocastType() != AutocastType.NONE) {
				if (this.collisionRectangle != null) {
					tempRect.set(this.collisionRectangle);
				} else {
					tempRect.set(getX(), getY(), 0, 0);
				}
				final float halfSize = this.acquisitionRange;
				tempRect.x -= halfSize;
				tempRect.y -= halfSize;
				tempRect.width += halfSize * 2;
				tempRect.height += halfSize * 2;
				game.getWorldCollision().enumUnitsInRect(tempRect,
						autocastTargetFinderEnum.reset(game, this, autocastAbility, disableMove));
				if (autocastTargetFinderEnum.currentUnitTarget != null) {
					this.order(game, autocastAbility.getBaseOrderId(), autocastTargetFinderEnum.currentUnitTarget);
					return true;
				}
			}
		}
		return false;
	}

	public boolean autoAcquireAttackTargets(final CSimulation game, final boolean disableMove) {
		if (!getCurrentAttacks().isEmpty() && !this.unitType.getClassifications().contains(CUnitClassification.PEON)) {
			if (this.collisionRectangle != null) {
				tempRect.set(this.collisionRectangle);
			} else {
				tempRect.set(getX(), getY(), 0, 0);
			}
			final float halfSize = this.acquisitionRange;
			tempRect.x -= halfSize;
			tempRect.y -= halfSize;
			tempRect.width += halfSize * 2;
			tempRect.height += halfSize * 2;
			game.getWorldCollision().enumUnitsInRect(tempRect,
					autoAttackTargetFinderEnum.reset(game, this, disableMove));
			return autoAttackTargetFinderEnum.foundAnyTarget;
		}
		return false;
	}

	public float getEndingDecayTime(final CSimulation game) {
		if (isBuilding()) {
			return game.getGameplayConstants().getStructureDecayTime();
		}
		if (this.unitType.isHero()) {
			return game.getGameplayConstants().getDissipateTime();
		}
		return game.getGameplayConstants().getBoneDecayTime();
	}

	public void order(final CSimulation game, final COrder order, final boolean queue) {
		if (isDead()) {
			return;
		}

		if (order != null) {
			final CAbility ability = game.getAbility(order.getAbilityHandleId());

			if (ability != null) {
				if (!getAbilities().contains(ability)) {
					// not allowed to use ability of other unit...
					return;
				}
				// Allow the ability to response to the order without actually placing itself in
				// the queue, nor modifying (interrupting) the queue.
				if (!ability.checkBeforeQueue(game, this, order.getOrderId(), order.getTarget(game))) {
					// TODO is this a possible bug vector that the network request doesn't
					// checkCanUse like the UI before checkBeforeQueue is called??
					order.fireEvents(game, this);
					this.stateNotifier.ordersChanged();
					return;
				}
			}
		}

		if ((this.lastStartedOrder != null) && this.lastStartedOrder.equals(order)
				&& (this.lastStartedOrder.getOrderId() == OrderIds.smart)) {
			// I skip your spammed move orders, TODO this will probably break some repeat
			// attack order or something later
			return;
		}
		if (!queue
				&& (!this.acceptingOrders || (this.currentBehavior != null && !this.currentBehavior.interruptable()))) {
			for (final COrder queuedOrder : this.orderQueue) {
				if (queuedOrder != null) {
					final int abilityHandleId = queuedOrder.getAbilityHandleId();
					final CAbility ability = game.getAbility(abilityHandleId);
					ability.onCancelFromQueue(game, this, queuedOrder.getOrderId());
				}
			}
			this.orderQueue.clear();
			this.orderQueue.add(order);
			this.stateNotifier.ordersChanged();
			this.stateNotifier.waypointsChanged();
		} else if (queue && (this.currentBehavior != this.stopBehavior)
				&& (this.currentBehavior != this.holdPositionBehavior)) {
			if (order.getOrderId() == OrderIds.patrol) {
				if (this.defaultBehavior == this.patrolBehavior) {
					this.patrolBehavior.addPatrolPoint(order.getTarget(game));
				} else {
					this.orderQueue.add(order);
				}
			} else {
				this.orderQueue.add(order);
			}
			this.stateNotifier.waypointsChanged();
		} else {
			setDefaultBehavior(this.stopBehavior);
			if (this.currentBehavior != null) {
				this.currentBehavior.end(game, true);
			}
			this.currentBehavior = beginOrder(game, order);
			if (this.currentBehavior != null) {
				this.currentBehavior.begin(game);
			}
			for (final COrder queuedOrder : this.orderQueue) {
				if (queuedOrder != null) {
					final int abilityHandleId = queuedOrder.getAbilityHandleId();
					final CAbility ability = game.getAbility(abilityHandleId);
					ability.onCancelFromQueue(game, this, queuedOrder.getOrderId());
				}
			}
			this.orderQueue.clear();
			this.stateNotifier.ordersChanged();
			this.stateNotifier.waypointsChanged();
		}
	}

	public boolean order(final CSimulation simulation, final int orderId, final AbilityTarget target) {
		if (orderId == OrderIds.stop) {
			order(simulation, new COrderNoTarget(0, orderId, false), false);
			return true;
		}
		for (final CAbility ability : this.abilities) {
			final BooleanAbilityActivationReceiver activationReceiver = BooleanAbilityActivationReceiver.INSTANCE;
			ability.checkCanUse(simulation, this, orderId, activationReceiver);
			if (activationReceiver.isOk()) {
				if (target == null) {
					final BooleanAbilityTargetCheckReceiver<Void> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
							.<Void>getInstance().reset();
					ability.checkCanTargetNoTarget(simulation, this, orderId, booleanTargetReceiver);
					if (booleanTargetReceiver.isTargetable()) {
						order(simulation, new COrderNoTarget(ability.getHandleId(), orderId, false), false);
						return true;
					}
				} else {
					final boolean targetable = target.visit(new AbilityTargetVisitor<Boolean>() {
						@Override
						public Boolean accept(final AbilityPointTarget target) {
							final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
									.<AbilityPointTarget>getInstance().reset();
							ability.checkCanTarget(simulation, CUnit.this, orderId, target, booleanTargetReceiver);
							final boolean pointTargetable = booleanTargetReceiver.isTargetable();
							if (pointTargetable) {
								order(simulation, new COrderTargetPoint(ability.getHandleId(), orderId, target, false),
										false);
							}
							return pointTargetable;
						}

						public Boolean acceptWidget(final CWidget target) {
							final BooleanAbilityTargetCheckReceiver<CWidget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
									.<CWidget>getInstance().reset();
							ability.checkCanTarget(simulation, CUnit.this, orderId, target, booleanTargetReceiver);
							final boolean widgetTargetable = booleanTargetReceiver.isTargetable();
							if (widgetTargetable) {
								order(simulation, new COrderTargetWidget(ability.getHandleId(), orderId,
										target.getHandleId(), false), false);
							}
							return widgetTargetable;
						}

						@Override
						public Boolean accept(final CUnit target) {
							return acceptWidget(target);
						}

						@Override
						public Boolean accept(final CDestructable target) {
							return acceptWidget(target);
						}

						@Override
						public Boolean accept(final CItem target) {
							return acceptWidget(target);
						}
					});
					if (targetable) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private CBehavior beginOrder(final CSimulation game, final COrder order) {
		this.lastStartedOrder = order;
		CBehavior nextBehavior;
		if (order != null) {
			nextBehavior = order.begin(game, this);
		} else {
			nextBehavior = this.defaultBehavior;
		}
		return nextBehavior;
	}

	public CBehavior getCurrentBehavior() {
		return this.currentBehavior;
	}

	public List<CAbility> getAbilities() {
		return this.abilities;
	}

	public <T> T getAbility(final CAbilityVisitor<T> visitor) {
		for (final CAbility ability : this.abilities) {
			final T visited = ability.visit(visitor);
			if (visited != null) {
				return visited;
			}
		}
		return null;
	}

	public <T extends CAbility> T getFirstAbilityOfType(final Class<T> cAbilityClass) {
		for (final CAbility ability : this.abilities) {
			if (cAbilityClass.isAssignableFrom(ability.getClass())) {
				return (T) ability;
			}
		}
		return null;
	}

	public void setCooldownEndTime(final int cooldownEndTime) {
		this.cooldownEndTime = cooldownEndTime;
	}

	public int getCooldownEndTime() {
		return this.cooldownEndTime;
	}

	@Override
	public float getFlyHeight() {
		return this.flyHeight;
	}

	public void setFlyHeight(final float flyHeight) {
		this.flyHeight = flyHeight;
	}

	public int getPlayerIndex() {
		return this.playerIndex;
	}

	public void setPlayerIndex(final CSimulation simulation, final int playerIndex, final boolean changeColor) {
		this.playerIndex = playerIndex;
		if (changeColor) {
			simulation.changeUnitColor(this, playerIndex);
		}
	}

	public CUnitType getUnitType() {
		return this.unitType;
	}

	public void setCollisionRectangle(final Rectangle collisionRectangle) {
		this.collisionRectangle = collisionRectangle;
	}

	public Rectangle getCollisionRectangle() {
		return this.collisionRectangle;
	}

	public void setX(final float newX, final CWorldCollision collision, final CRegionManager regionManager) {
		final float prevX = getX();
		if (!isBuilding()) {
			setX(newX);
			collision.translate(this, newX - prevX, 0);
		}
		checkRegionEvents(regionManager);
	}

	public void setY(final float newY, final CWorldCollision collision, final CRegionManager regionManager) {
		final float prevY = getY();
		if (!isBuilding()) {
			setY(newY);
			collision.translate(this, 0, newY - prevY);
		}
		checkRegionEvents(regionManager);
	}

	public void setPointAndCheckUnstuck(final float newX, final float newY, final CSimulation game) {
		final CWorldCollision collision = game.getWorldCollision();
		final PathingGrid pathingGrid = game.getPathingGrid();
		;
		float outputX = newX, outputY = newY;
		int checkX = 0;
		int checkY = 0;
		float collisionSize;
		if (isBuilding() && (this.unitType.getBuildingPathingPixelMap() != null)) {
			tempRect.setSize(this.unitType.getBuildingPathingPixelMap().getWidth() * 32,
					this.unitType.getBuildingPathingPixelMap().getHeight() * 32);
			collisionSize = tempRect.getWidth() / 2;
		} else if (this.collisionRectangle != null) {
			tempRect.set(this.collisionRectangle);
			collisionSize = this.unitType.getCollisionSize();
		} else {
			tempRect.setSize(16, 16);
			collisionSize = this.unitType.getCollisionSize();
		}
		for (int i = 0; i < 300; i++) {
			final float centerX = newX + (checkX * 64);
			final float centerY = newY + (checkY * 64);
			tempRect.setCenter(centerX, centerY);
			if (!collision.intersectsAnythingOtherThan(tempRect, this, getMovementType())
					&& pathingGrid.isPathable(centerX, centerY, getMovementType(), collisionSize)) {
				outputX = centerX;
				outputY = centerY;
				break;
			}
			final double angle = (((int) Math.floor(Math.sqrt((4 * i) + 1)) % 4) * Math.PI) / 2;
			checkX -= (int) Math.cos(angle);
			checkY -= (int) Math.sin(angle);
		}
		setPoint(outputX, outputY, collision, game.getRegionManager());
		game.unitRepositioned(this);
	}

	public void setPoint(final float newX, final float newY, final CWorldCollision collision,
			final CRegionManager regionManager) {
		final float prevX = getX();
		final float prevY = getY();
		setX(newX);
		setY(newY);
		if (!isBuilding()) {
			collision.translate(this, newX - prevX, newY - prevY);
		}
		checkRegionEvents(regionManager);
	}

	private void checkRegionEvents(final CRegionManager regionManager) {
		final Set<CRegion> temp = this.containingRegions;
		this.containingRegions = this.priorContainingRegions;
		this.priorContainingRegions = temp;
		this.containingRegions.clear();
		regionManager.checkRegions(
				this.collisionRectangle == null ? tempRect.set(getX(), getY(), 0, 0) : this.collisionRectangle,
				regionCheckerImpl.reset(this, regionManager));
		for (final CRegion region : this.priorContainingRegions) {
			if (!this.containingRegions.contains(region)) {
				regionManager.onUnitLeaveRegion(this, region);
			}
		}
	}

	public EnumSet<CUnitClassification> getClassifications() {
		return this.classifications;
	}

	public void addClassification(final CUnitClassification unitClassification) {
		this.classifications.add(unitClassification);
	}

	public float getDefense() {
		return this.currentDefense;
	}

	@Override
	public float getImpactZ() {
		return this.unitType.getImpactZ();
	}

	public double angleTo(final AbilityTarget target) {
		final double dx = target.getX() - getX();
		final double dy = target.getY() - getY();
		return StrictMath.atan2(dy, dx);
	}

	public double distance(final AbilityTarget target) {
		double dx = StrictMath.abs(target.getX() - getX());
		double dy = StrictMath.abs(target.getY() - getY());
		final float thisCollisionSize = this.unitType.getCollisionSize();
		float targetCollisionSize;
		if (target instanceof CUnit) {
			final CUnitType targetUnitType = ((CUnit) target).getUnitType();
			targetCollisionSize = targetUnitType.getCollisionSize();
		} else {
			targetCollisionSize = 0; // TODO destructable collision size here
		}
		if (dx < 0) {
			dx = 0;
		}
		if (dy < 0) {
			dy = 0;
		}

		double groundDistance = StrictMath.sqrt((dx * dx) + (dy * dy)) - thisCollisionSize - targetCollisionSize;
		if (groundDistance < 0) {
			groundDistance = 0;
		}
		return groundDistance;
	}

	@Override
	public double distance(final float x, final float y) {
		double dx = Math.abs(x - getX());
		double dy = Math.abs(y - getY());
		final float thisCollisionSize = this.unitType.getCollisionSize();
		if (dx < 0) {
			dx = 0;
		}
		if (dy < 0) {
			dy = 0;
		}

		double groundDistance = StrictMath.sqrt((dx * dx) + (dy * dy)) - thisCollisionSize;
		if (groundDistance < 0) {
			groundDistance = 0;
		}
		return groundDistance;
	}

	public boolean checkForMiss(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType, final float damage,
			final float bonusDamage) {
		boolean miss = false;
		if (isAttack) {
			for (final CUnitAttackEvasionListener listener : new ArrayList<>(evasionListeners)) {
				miss = miss || listener.onAttack(simulation, source, this, isAttack, isRanged, damageType);
			}
		}
		return miss;
	}

	public boolean checkForAttackProjReaction(final CSimulation simulation, final CUnit source, final CAttackProjectile projectile) {
		boolean allow = true;
		for (final CUnitAttackProjReactionListener listener : new ArrayList<>(attackProjReactionListeners)) {
			if (allow) {
				allow = allow && listener.onHit(simulation, source, this, projectile);
			}
		}
		return allow;
	}

	public boolean checkForAbilityProjReaction(final CSimulation simulation, final CUnit source, final CProjectile projectile) {
		boolean allow = true;
		for (final CUnitAbilityProjReactionListener listener : new ArrayList<>(abilityProjReactionListeners)) {
			if (allow) {
				allow = allow && listener.onHit(simulation, source, this, projectile);
			}
		}
		return allow;
	}

	public boolean checkForAbilityEffectReaction(final CSimulation simulation, final CUnit source, final CAbility ability) {
		boolean allow = true;
		for (final CUnitAbilityEffectReactionListener listener : new ArrayList<>(abilityEffectReactionListeners)) {
			if (allow) {
				allow = allow && listener.onHit(simulation, source, this, ability);
			}
		}
		return allow;
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage) {
		return this.damage(simulation, source, isAttack, isRanged, attackType, damageType, weaponSoundType, damage, 0);
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage, final float bonusDamage) {
		final boolean wasDead = isDead();
		if (wasDead) {
			return 0;
		}
		float trueDamage = 0;
		if (!this.invulnerable) {

			final CUnitAttackDamageTakenModificationListenerDamageModResult result = new CUnitAttackDamageTakenModificationListenerDamageModResult(
					damage, bonusDamage);
			for (final CUnitAttackDamageTakenModificationListener listener : damageTakenModificationListeners) {
				listener.onDamage(simulation, source, this, isAttack, isRanged, attackType, damageType, result);
			}

			final float damageRatioFromArmorClass = simulation.getGameplayConstants().getDamageRatioAgainst(attackType,
					getDefenseType());
			final float damageRatioFromDefense;
			final float defense = this.currentDefense;
			if (damageType != CDamageType.NORMAL) {
				damageRatioFromDefense = 1.0f;
			} else if (defense >= 0) {
				damageRatioFromDefense = 1f - ((defense * simulation.getGameplayConstants().getDefenseArmor())
						/ (1 + (simulation.getGameplayConstants().getDefenseArmor() * defense)));
			} else {
				damageRatioFromDefense = 2f
						- (float) StrictMath.pow(1f - simulation.getGameplayConstants().getDefenseArmor(), -defense);
			}
			trueDamage = damageRatioFromArmorClass * damageRatioFromDefense * (result.computeFinalDamage());

			for (final CUnitAttackFinalDamageTakenModificationListener listener : new ArrayList<>(
					finalDamageTakenModificationListeners)) {
				trueDamage = listener.onDamage(simulation, source, this, isAttack, isRanged, attackType, damageType,
						trueDamage);
			}

			final boolean wasAboveMax = this.life > this.maximumLife;
			this.life -= trueDamage;
			if (((result.computeFinalDamage()) < 0) && !wasAboveMax && (this.life > this.maximumLife)) {
				// NOTE wasAboveMax is for that weird life drain power to drain above max... to
				// be honest that's a crazy mechanic anyway so I didn't test whether it works
				// yet
				this.life = this.maximumLife;
			}
			this.stateNotifier.lifeChanged();
		}
		for (final CUnitAttackDamageTakenListener listener : new ArrayList<>(damageTakenListeners)) {
			listener.onDamage(simulation, source, this, isAttack, isRanged, damageType, damage, bonusDamage,
					trueDamage);
		}
		simulation.unitDamageEvent(this, weaponSoundType, this.unitType.getArmorType());
		if (!this.invulnerable && isDead()) {
			if (!wasDead) {
				kill(simulation, source);
			}
		} else {
			if ((this.currentBehavior == null)
					|| (this.currentBehavior == this.defaultBehavior && this.currentBehavior.interruptable())) {
				boolean foundMatchingReturnFireAttack = false;
				if (!simulation.getPlayer(getPlayerIndex()).hasAlliance(source.getPlayerIndex(), CAllianceType.PASSIVE)
						&& !this.unitType.getClassifications().contains(CUnitClassification.PEON)) {
					for (final CUnitAttack attack : getCurrentAttacks()) {
						if (source.canBeTargetedBy(simulation, this, attack.getTargetsAllowed())) {
							this.currentBehavior = getAttackBehavior().reset(OrderIds.attack, attack, source, false,
									CBehaviorAttackListener.DO_NOTHING);
							this.currentBehavior.begin(simulation);
							foundMatchingReturnFireAttack = true;
							break;
						}
					}
				}
				if (!foundMatchingReturnFireAttack && this.unitType.isCanFlee() && !isMovementDisabled()
						&& (this.moveBehavior != null) && (this.playerIndex != source.getPlayerIndex())) {
					final double angleTo = source.angleTo(this);
					final int distanceToFlee = getSpeed();
					this.currentBehavior = this.moveBehavior.reset(OrderIds.move,
							new AbilityPointTarget((float) (getX() + (distanceToFlee * StrictMath.cos(angleTo))),
									(float) (getY() + (distanceToFlee * StrictMath.sin(angleTo)))));
					this.currentBehavior.begin(simulation);
				}
			}
		}
		return trueDamage;
	}

	private void kill(final CSimulation simulation, final CUnit source) {
		if (this.currentBehavior != null) {
			this.currentBehavior.end(simulation, true);
		}
		this.currentBehavior = null;

		final CUnitDeathReplacementResult result = new CUnitDeathReplacementResult();
		CUnitDeathReplacementStacking allowContinue = new CUnitDeathReplacementStacking();
		for (final CUnitDeathReplacementEffectPriority priority : CUnitDeathReplacementEffectPriority.values()) {
			if (allowContinue.isAllowStacking()) {
				for (final CUnitDeathReplacementEffect effect : new ArrayList<>(deathReplacementEffects.get(priority))) {
					if (allowContinue.isAllowSamePriorityStacking()) {
						allowContinue = effect.onDeath(simulation, this, source, result);
					}
				}
			}
		}
		if (result.isReviving()) {
			return;
		}

		this.orderQueue.clear();
		killPathingInstance();
		popoutWorker(simulation);
		for (int i = this.abilities.size() - 1; i >= 0; i--) {
			// okay if it removes self from this during onDeath() because of reverse
			// iteration order
			this.abilities.get(i).onDeath(simulation, this);
		}
		simulation.getPlayer(playerIndex).addFogModifer(simulation, new CUnitDeathVisionFogModifier(this));
		if (source != null) {
			simulation.getPlayer(source.getPlayerIndex()).addFogModifer(simulation,
					new CUnitDeathVisionFogModifier(this));
		}
		if (this.attackFogMod.getPlayerIndex() != this.playerIndex) {
			simulation.getPlayer(this.attackFogMod.getPlayerIndex()).removeFogModifer(simulation, this.attackFogMod);
			this.attackFogMod.setPlayerIndex(playerIndex);
		}

		if (result.isReincarnating()) {
			return;
		}

		if (this.constructing) {
			simulation.createDeathExplodeEffect(this, explodesOnDeathBuffId);
		} else {
			this.deathTurnTick = simulation.getGameTurnTick();
		}

		final CPlayer player = simulation.getPlayer(this.playerIndex);
		if (this.foodMade != 0) {
			player.setUnitFoodMade(this, 0);
		}
		if (this.foodUsed != 0) {
			player.setUnitFoodUsed(this, 0);
		}
		if (getHeroData() == null) {
			if (this.constructing) {
				player.removeTechtreeInProgress(this.unitType.getTypeId());
			} else {
				player.removeTechtreeUnlocked(simulation, this.unitType.getTypeId());
			}
		}
		// else its a hero and techtree "remains unlocked" which is currently meaning
		// the "limit of 1" remains limited

		// Award hero experience
		if (source != null) {
			final CPlayer sourcePlayer = simulation.getPlayer(source.getPlayerIndex());
			if (!sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE)) {
				if (player.getPlayerState(simulation, CPlayerState.GIVES_BOUNTY) > 0) {
					int goldBountyAwarded = this.unitType.getGoldBountyAwardedBase();
					final int goldBountyAwardedDice = this.unitType.getGoldBountyAwardedDice();
					final int goldBountyAwardedSides = this.unitType.getGoldBountyAwardedSides();
					for (int i = 0; i < goldBountyAwardedDice; i++) {
						goldBountyAwarded += simulation.getSeededRandom().nextInt(goldBountyAwardedSides) + 1;
					}
					if (goldBountyAwarded > 0) {
						sourcePlayer.addGold(goldBountyAwarded);
						simulation.unitGainResourceEvent(this, sourcePlayer.getId(), ResourceType.GOLD,
								goldBountyAwarded);
					}
					int lumberBountyAwarded = this.unitType.getLumberBountyAwardedBase();
					final int lumberBountyAwardedDice = this.unitType.getLumberBountyAwardedDice();
					final int lumberBountyAwardedSides = this.unitType.getLumberBountyAwardedSides();
					for (int i = 0; i < lumberBountyAwardedDice; i++) {
						lumberBountyAwarded += simulation.getSeededRandom().nextInt(lumberBountyAwardedSides) + 1;
					}
					if (lumberBountyAwarded > 0) {
						sourcePlayer.addLumber(lumberBountyAwarded);
						simulation.unitGainResourceEvent(this, sourcePlayer.getId(), ResourceType.LUMBER,
								lumberBountyAwarded);
					}
				}
				final CGameplayConstants gameplayConstants = simulation.getGameplayConstants();
				if (gameplayConstants.isBuildingKillsGiveExp() || !source.isBuilding()) {
					final CUnit killedUnit = this;
					final CAbilityHero killedUnitHeroData = getHeroData();
					final boolean killedUnitIsAHero = killedUnitHeroData != null;
					int availableAwardXp;
					if (killedUnitIsAHero) {
						availableAwardXp = gameplayConstants.getGrantHeroXP(killedUnitHeroData.getHeroLevel());
					} else {
						availableAwardXp = gameplayConstants.getGrantNormalXP(this.unitType.getLevel());
					}
					final List<CUnit> xpReceivingHeroes = new ArrayList<>();
					final int heroExpRange = gameplayConstants.getHeroExpRange();
					simulation.getWorldCollision().enumUnitsInRect(new Rectangle(getX() - heroExpRange,
							getY() - heroExpRange, heroExpRange * 2, heroExpRange * 2), new CUnitEnumFunction() {
								@Override
								public boolean call(final CUnit unit) {
									if ((unit.distance(killedUnit) <= heroExpRange)
											&& sourcePlayer.hasAlliance(unit.getPlayerIndex(), CAllianceType.SHARED_XP)
											&& unit.isHero() && !unit.isDead()) {
										xpReceivingHeroes.add(unit);
									}
									return false;
								}
							});
					if (xpReceivingHeroes.isEmpty()) {
						if (gameplayConstants.isGlobalExperience()) {
							for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
								if (sourcePlayer.hasAlliance(i, CAllianceType.SHARED_XP)) {
									xpReceivingHeroes.addAll(simulation.getPlayerHeroes(i));
								}
							}
						}
					}
					for (final CUnit receivingHero : xpReceivingHeroes) {
						final CAbilityHero heroData = receivingHero.getHeroData();
						heroData.addXp(simulation, receivingHero,
								(int) (availableAwardXp * (1f / xpReceivingHeroes.size())
										* gameplayConstants.getHeroFactorXp(heroData.getHeroLevel())));
					}
				}
			}
		}
		fireDeathEvents(simulation);
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_DEATH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitDeathScope(JassGameEventsWar3.EVENT_UNIT_DEATH,
						event.getTrigger(), this, source));
			}
		}
		simulation.getPlayer(this.playerIndex).fireUnitDeathEvents(this, source);
		if (isExplodesOnDeath()) {
			setHidden(true);
			simulation.createDeathExplodeEffect(this, explodesOnDeathBuffId);
			simulation.removeUnit(this);
		}
	}

	public void killPathingInstance() {
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
			this.pathingInstance = null;
		}
	}

	public void kill(final CSimulation simulation) {
		if (!isDead()) {
			setLife(simulation, 0f);
		}
	}

	public boolean canReach(final AbilityTarget target, final float range) {
		final double distance = distance(target);
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			final CUnitType targetUnitType = targetUnit.getUnitType();
			if (targetUnit.isBuilding() && (targetUnitType.getBuildingPathingPixelMap() != null)) {
				final BufferedImage buildingPathingPixelMap = targetUnitType.getBuildingPathingPixelMap();
				final float targetX = target.getX();
				final float targetY = target.getY();
				if (canReachToPathing(range, targetUnit.getFacing(), buildingPathingPixelMap, targetX, targetY)) {
					return true;
				}
			}
		} else if (target instanceof CDestructable) {
			final CDestructable targetDest = (CDestructable) target;
			final CDestructableType targetDestType = targetDest.getDestType();
			final BufferedImage pathingPixelMap = targetDest.isDead() ? targetDestType.getPathingDeathPixelMap()
					: targetDestType.getPathingPixelMap();
			final float targetX = target.getX();
			final float targetY = target.getY();
			if ((pathingPixelMap != null) && canReachToPathing(range, 270, pathingPixelMap, targetX, targetY)) {
				return true;
			}
		}
		return distance <= range;
	}

	public boolean canReach(final float x, final float y, final float range) {
		return distance(x, y) <= range; // TODO use dist squared for performance
	}

	public boolean canReachToPathing(final float range, final float rotationForPathing,
			final BufferedImage buildingPathingPixelMap, final float targetX, final float targetY) {
		if (buildingPathingPixelMap == null) {
			return canReach(targetX, targetY, range);
		}
		final int rotation = ((int) rotationForPathing + 450) % 360;
		final float relativeOffsetX = getX() - targetX;
		final float relativeOffsetY = getY() - targetY;
		final int gridWidth = (rotation % 180) != 0 ? buildingPathingPixelMap.getHeight()
				: buildingPathingPixelMap.getWidth();
		final int gridHeight = (rotation % 180) != 0 ? buildingPathingPixelMap.getWidth()
				: buildingPathingPixelMap.getHeight();
		final int relativeGridX = (int) Math.floor(relativeOffsetX / 32f) + (gridWidth / 2);
		final int relativeGridY = (int) Math.floor(relativeOffsetY / 32f) + (gridHeight / 2);
		final int rangeInCells = (int) Math.floor(range / 32f) + 1;
		final int rangeInCellsSquare = rangeInCells * rangeInCells;
		int minCheckX = relativeGridX - rangeInCells;
		int minCheckY = relativeGridY - rangeInCells;
		int maxCheckX = relativeGridX + rangeInCells;
		int maxCheckY = relativeGridY + rangeInCells;
		if ((minCheckX < gridWidth) && (maxCheckX >= 0)) {
			if ((minCheckY < gridHeight) && (maxCheckY >= 0)) {
				if (minCheckX < 0) {
					minCheckX = 0;
				}
				if (minCheckY < 0) {
					minCheckY = 0;
				}
				if (maxCheckX > (gridWidth - 1)) {
					maxCheckX = gridWidth - 1;
				}
				if (maxCheckY > (gridHeight - 1)) {
					maxCheckY = gridHeight - 1;
				}
				for (int checkX = minCheckX; checkX <= maxCheckX; checkX++) {
					for (int checkY = minCheckY; checkY <= maxCheckY; checkY++) {
						final int dx = relativeGridX - checkX;
						final int dy = relativeGridY - checkY;
						if (((dx * dx) + (dy * dy)) <= rangeInCellsSquare) {
							if (((getRGBFromPixelData(buildingPathingPixelMap, checkX, checkY, rotation)
									& 0xFF0000) >>> 16) > 127) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private int getRGBFromPixelData(final BufferedImage buildingPathingPixelMap, final int checkX, final int checkY,
			final int rotation) {

		// Below: y is downwards (:()
		int x;
		int y;
		switch (rotation) {
		case 90:
			x = checkY;
			y = buildingPathingPixelMap.getWidth() - 1 - checkX;
			break;
		case 180:
			x = buildingPathingPixelMap.getWidth() - 1 - checkX;
			y = buildingPathingPixelMap.getHeight() - 1 - checkY;
			break;
		case 270:
			x = buildingPathingPixelMap.getHeight() - 1 - checkY;
			y = checkX;
			break;
		default:
		case 0:
			x = checkX;
			y = checkY;
		}
		return buildingPathingPixelMap.getRGB(x, buildingPathingPixelMap.getHeight() - 1 - y);
	}

	public void addStateListener(final CUnitStateListener listener) {
		this.stateListenersUpdates.add(new StateListenerUpdate(listener, StateListenerUpdateType.ADD));
	}

	public void removeStateListener(final CUnitStateListener listener) {
		this.stateListenersUpdates.add(new StateListenerUpdate(listener, StateListenerUpdateType.REMOVE));
	}

	public boolean isCorpse() {
		return this.corpse;
	}

	public boolean isBoneCorpse() {
		return this.boneCorpse;
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if ((this == source) && targetsAllowed.contains(CTargetType.NOTSELF)
				&& !targetsAllowed.contains(CTargetType.SELF)) {
			receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_SELF);
			return false;
		}
		if (targetsAllowed.contains(CTargetType.PLAYERUNITS) && (source.getPlayerIndex() != getPlayerIndex())) {
			receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ONE_OF_YOUR_OWN_UNITS);
			return false;
		}
		if (targetsAllowed.contains(CTargetType.NON_MAGIC_IMMUNE) && isMagicImmune()) {
			receiver.targetCheckFailed(CommandStringErrorKeys.THAT_UNIT_IS_IMMUNE_TO_MAGIC);
			return false;
		}
		if (targetsAllowed.containsAll(this.unitType.getTargetedAs()) || (!targetsAllowed.contains(CTargetType.GROUND)
				&& !targetsAllowed.contains(CTargetType.STRUCTURE) && !targetsAllowed.contains(CTargetType.AIR))) {
			final int sourcePlayerIndex = source.getPlayerIndex();
			final CPlayer sourcePlayer = simulation.getPlayer(sourcePlayerIndex);
			if (!targetsAllowed.contains(CTargetType.ENEMIES)
					|| !sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE)
					|| targetsAllowed.contains(CTargetType.FRIEND)) {
				if (!targetsAllowed.contains(CTargetType.FRIEND)
						|| sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE)
						|| targetsAllowed.contains(CTargetType.ENEMIES)) {
					if (!targetsAllowed.contains(CTargetType.MECHANICAL)
							|| this.unitType.getClassifications().contains(CUnitClassification.MECHANICAL)) {
						if (!targetsAllowed.contains(CTargetType.ORGANIC)
								|| !this.unitType.getClassifications().contains(CUnitClassification.MECHANICAL)) {
							if (!targetsAllowed.contains(CTargetType.ANCIENT)
									|| this.unitType.getClassifications().contains(CUnitClassification.ANCIENT)) {
								if (!targetsAllowed.contains(CTargetType.NONANCIENT)
										|| !this.unitType.getClassifications().contains(CUnitClassification.ANCIENT)) {
									final boolean invulnerable = this.isInvulnerable();
									if ((!invulnerable && (targetsAllowed.contains(CTargetType.VULNERABLE)
											|| !targetsAllowed.contains(CTargetType.INVULNERABLE)))
											|| (invulnerable && targetsAllowed.contains(CTargetType.INVULNERABLE))) {
										if (!targetsAllowed.contains(CTargetType.HERO) || (getHeroData() != null)) {
											if (!targetsAllowed.contains(CTargetType.NONHERO)
													|| (getHeroData() == null)) {
												if (isDead()) {
													if (this.isRaisable() && this.isDecays() && isBoneCorpse()) {
														if (targetsAllowed.contains(CTargetType.DEAD)) {
															return true;
														} else {
															receiver.targetCheckFailed(
																	CommandStringErrorKeys.TARGET_MUST_BE_LIVING);
														}
													} else {
														receiver.targetCheckFailed(
																CommandStringErrorKeys.MUST_TARGET_A_UNIT_WITH_THIS_ACTION);
													}
												} else {
													if (!targetsAllowed.contains(CTargetType.DEAD)
															|| targetsAllowed.contains(CTargetType.ALIVE)) {
														return true;
													} else {
														receiver.targetCheckFailed(
																CommandStringErrorKeys.MUST_TARGET_A_CORPSE);
													}
												}
											} else {
												receiver.targetCheckFailed(
														CommandStringErrorKeys.UNABLE_TO_TARGET_HEROES);
											}
										} else {
											receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_HERO);
										}
									} else {
										if (invulnerable) {
											receiver.targetCheckFailed(
													CommandStringErrorKeys.THAT_TARGET_IS_INVULNERABLE);
										} else {
											receiver.targetCheckFailed(
													CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
										}
									}
								} else {
									receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_ANCIENTS);
								}
							} else {
								receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_ANCIENT);
							}
						} else {
							receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_ORGANIC_UNITS);
						}
					} else {
						receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_ORGANIC_UNITS);
					}
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_FRIENDLY_UNIT);
				}
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_ENEMY_UNIT);
			}
		} else {
			if (this.unitType.getTargetedAs().contains(CTargetType.GROUND)
					&& !targetsAllowed.contains(CTargetType.GROUND)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_GROUND_UNITS);
			} else if (this.unitType.getTargetedAs().contains(CTargetType.STRUCTURE)
					&& !targetsAllowed.contains(CTargetType.STRUCTURE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_BUILDINGS);
			} else if (this.unitType.getTargetedAs().contains(CTargetType.AIR)
					&& !targetsAllowed.contains(CTargetType.AIR)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_AIR_UNITS);
			} else if (this.unitType.getTargetedAs().contains(CTargetType.WARD)
					&& !targetsAllowed.contains(CTargetType.WARD)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_WARDS);
			} else if (targetsAllowed.contains(CTargetType.GROUND)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_GROUND_UNIT);
			} else if (targetsAllowed.contains(CTargetType.STRUCTURE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_BUILDING);
			} else if (targetsAllowed.contains(CTargetType.AIR)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_AN_AIR_UNIT);
			} else if (targetsAllowed.contains(CTargetType.WARD)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.MUST_TARGET_A_WARD);
			} else {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
			}
		}
		return false;
	}

	public boolean isMovementDisabled() {
		return (this.moveBehavior == null) || this.moveDisabled;
		// TODO this used to directly return the state of whether our unit was a
		// building. Will it be a problem that I changed it?
		// I was trying to fix attack move on stationary units which was crashing
	}

	public boolean isMovementOnWaterAllowed() {
		return !isMovementDisabled() && getMovementType().isPathable((short) ~PathingFlags.UNSWIMABLE);
	}

	public MovementType getMovementType() {
		if (isMovementDisabled()) {
			return MovementType.DISABLED;
		}
		return getUnitType().getMovementType(); // later maybe it has unit instance override for windwalk, so this
												// wrapper exists to later mod
	}

	public float getAcquisitionRange() {
		return this.acquisitionRange;
	}

	public void setAcquisitionRange(final float acquisitionRange) {
		this.acquisitionRange = acquisitionRange;
	}

	public void setCurrentHp(final CSimulation game, final float hpValue) {
		setLife(game, Math.min(hpValue, getMaximumLife()));
	}

	public void heal(final CSimulation game, final float lifeToRegain) {
		setLife(game, Math.min(getLife() + lifeToRegain, getMaximumLife()));
	}

	public void restoreMana(final CSimulation game, final float manaToRegain) {
		setMana(Math.min(getMana() + manaToRegain, getMaximumMana()));
	}

	public void resurrect(final CSimulation simulation) {
		simulation.getWorldCollision().removeUnit(this);
		this.corpse = false;
		this.boneCorpse = false;
		this.deathTurnTick = 0;
		this.explodesOnDeath = false;
		this.explodesOnDeathBuffId = null;
		setLife(simulation, getMaximumLife());
		simulation.getWorldCollision().addUnit(this);
		simulation.unitUpdatedType(this, typeId); // clear out some state
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 0.0f, true);
	}

	private static final class AutocastTargetFinderEnum implements CUnitEnumFunction {
		private final static BooleanAbilityTargetCheckReceiver<CWidget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
				.<CWidget>getInstance();
		private CSimulation game;
		private CUnit source;
		private CAutocastAbility ability;
		private boolean disableMove;
		private AutocastType type;

		private CUnit currentUnitTarget;
		private double comparisonValue;

		private AutocastTargetFinderEnum reset(final CSimulation game, final CUnit source,
				final CAutocastAbility ability, final boolean disableMove) {
			this.game = game;
			this.source = source;
			this.ability = ability;
			this.disableMove = disableMove;
			this.type = ability.getAutocastType();

			currentUnitTarget = null;
			comparisonValue = Double.NaN;
			return this;
		}

		@Override
		public boolean call(final CUnit unit) {
			if (type != AutocastType.NONE) {
				switch (type) {
				case ATTACKINGALLY:
				case ATTACKINGENEMY:
					if (unit.getCurrentBehavior() != null && unit.getCurrentBehavior() instanceof CRangedBehavior) {
						CRangedBehavior rbeh = (CRangedBehavior) unit.getCurrentBehavior();
						if (rbeh.getHighlightOrderId() == OrderIds.attack) {
							AbilityTarget target = rbeh.getTarget();
							CUnit targetUnit = target.visit(AbilityTargetVisitor.UNIT);
							if (targetUnit != null) {
								if ((type == AutocastType.ATTACKINGALLY
										&& this.game.getPlayer(this.source.getPlayerIndex())
												.hasAlliance(targetUnit.getPlayerIndex(), CAllianceType.PASSIVE))
										|| (type == AutocastType.ATTACKINGENEMY
												&& !this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(
														targetUnit.getPlayerIndex(), CAllianceType.PASSIVE))) {
									targetCheckReceiver.reset();
									ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(),
											unit, targetCheckReceiver);
									if (targetCheckReceiver.isTargetable()) {
										if (currentUnitTarget == null) {
											currentUnitTarget = unit;
											comparisonValue = this.source.distance(unit);
										} else {
											double dist = this.source.distance(unit);
											if (dist < this.comparisonValue) {
												currentUnitTarget = unit;
												comparisonValue = dist;
											}
										}
									}
								}
							}
						}
					}
					break;
				case ATTACKTARGETING:
					if (!this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(),
							CAllianceType.PASSIVE) && !unit.isDead() && !unit.isInvulnerable()
							&& !unit.isUnitType(CUnitTypeJass.SLEEPING)) {
						for (final CUnitAttack attack : this.source.getCurrentAttacks()) {
							if (this.source.canReach(unit, this.source.acquisitionRange)
									&& unit.canBeTargetedBy(this.game, this.source, attack.getTargetsAllowed())
									&& (this.source.distance(unit) >= this.source.getUnitType()
											.getMinimumAttackRange())) {
								targetCheckReceiver.reset();
								ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
										targetCheckReceiver);
								if (targetCheckReceiver.isTargetable()) {
									if (currentUnitTarget == null) {
										currentUnitTarget = unit;
										comparisonValue = this.source.distance(unit);
									} else {
										double dist = this.source.distance(unit);
										if (dist < this.comparisonValue) {
											currentUnitTarget = unit;
											comparisonValue = dist;
										}
									}
								}
							}
						}
					}

					break;
				case HIGESTHP:
					targetCheckReceiver.reset();
					ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						if (currentUnitTarget == null) {
							currentUnitTarget = unit;
							comparisonValue = unit.getLife() / unit.getMaximumLife();
						} else {
							double ratio = unit.getLife() / unit.getMaximumLife();
							if (ratio > this.comparisonValue) {
								currentUnitTarget = unit;
								comparisonValue = ratio;
							}
						}
					}
					break;
				case LOWESTHP:
					targetCheckReceiver.reset();
					ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						if (unit.getLife() < unit.getMaximumLife()) {
							if (currentUnitTarget == null) {
								currentUnitTarget = unit;
								comparisonValue = unit.getLife() / unit.getMaximumLife();
							} else {
								double ratio = unit.getLife() / unit.getMaximumLife();
								if (ratio < this.comparisonValue) {
									currentUnitTarget = unit;
									comparisonValue = ratio;
								}
							}
						}
					}
					break;
				case NEARESTVALID:
					targetCheckReceiver.reset();
					ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						if (currentUnitTarget == null) {
							currentUnitTarget = unit;
							comparisonValue = this.source.distance(unit);
						} else {
							double dist = this.source.distance(unit);
							if (dist < this.comparisonValue) {
								currentUnitTarget = unit;
								comparisonValue = dist;
							}
						}
					}
					break;
				case NEARESTENEMY:
					targetCheckReceiver.reset();
					ability.checkCanAutoTarget(this.game, this.source, this.ability.getBaseOrderId(), unit,
							targetCheckReceiver);
					if (targetCheckReceiver.isTargetable() && !this.game.getPlayer(this.source.getPlayerIndex())
							.hasAlliance(unit.getPlayerIndex(), CAllianceType.PASSIVE)) {
						if (currentUnitTarget == null) {
							currentUnitTarget = unit;
							comparisonValue = this.source.distance(unit);
						} else {
							double dist = this.source.distance(unit);
							if (dist < this.comparisonValue) {
								currentUnitTarget = unit;
								comparisonValue = dist;
							}
						}
					}
					break;
				case NONE:
				case NOTARGET:
				default:
					break;

				}
			}

			return false;
		}
	}

	private static final class AutoAttackTargetFinderEnum implements CUnitEnumFunction {
		private CSimulation game;
		private CUnit source;
		private boolean disableMove;
		private boolean foundAnyTarget;

		private AutoAttackTargetFinderEnum reset(final CSimulation game, final CUnit source,
				final boolean disableMove) {
			this.game = game;
			this.source = source;
			this.disableMove = disableMove;
			this.foundAnyTarget = false;
			return this;
		}

		@Override
		public boolean call(final CUnit unit) {
			if (this.source.getAttackBehavior() != null
					&& !this.source.getFirstAbilityOfType(CAbilityAttack.class).isDisabled()) {
				// TODO this "attack behavior null" check was added for some weird Root edge
				// case with NE, maybe
				// refactor it later
				if (!this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(),
						CAllianceType.PASSIVE) && !unit.isDead() && !unit.isInvulnerable()
						&& !unit.isUnitType(CUnitTypeJass.SLEEPING)) {
					for (final CUnitAttack attack : this.source.getCurrentAttacks()) {
						if (this.source.canReach(unit, this.source.acquisitionRange)
								&& unit.canBeTargetedBy(this.game, this.source, attack.getTargetsAllowed())
								&& (this.source.distance(unit) >= this.source.getUnitType().getMinimumAttackRange())) {
							if (!(unit.isUnitType(CUnitTypeJass.ETHEREAL) && attack.getAttackType() != CAttackType.MAGIC
									&& attack.getAttackType() != CAttackType.SPELLS)
									&& !(this.game.getGameplayConstants().isMagicImmuneResistsDamage()
											&& unit.isUnitType(CUnitTypeJass.MAGIC_IMMUNE)
											&& attack.getAttackType() == CAttackType.MAGIC)) {
								if (this.source.currentBehavior != null) {
									this.source.currentBehavior.end(this.game, false);
								}
								this.source.currentBehavior = this.source.getAttackBehavior().reset(OrderIds.attack,
										attack, unit, this.disableMove, CBehaviorAttackListener.DO_NOTHING);
								this.source.currentBehavior.begin(this.game);
								this.foundAnyTarget = true;
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

	public CBehaviorMove getMoveBehavior() {
		return this.moveBehavior;
	}

	public void setMoveBehavior(final CBehaviorMove moveBehavior) {
		this.moveBehavior = moveBehavior;
	}

	public CBehaviorAttack getAttackBehavior() {
		return this.attackBehavior;
	}

	public void setAttackBehavior(final CBehaviorAttack attackBehavior) {
		this.attackBehavior = attackBehavior;
	}

	public void setAttackMoveBehavior(final CBehaviorAttackMove attackMoveBehavior) {
		this.attackMoveBehavior = attackMoveBehavior;
	}

	public CBehaviorAttackMove getAttackMoveBehavior() {
		return this.attackMoveBehavior;
	}

	public CBehaviorStop getStopBehavior() {
		return this.stopBehavior;
	}

	public void setFollowBehavior(final CBehaviorFollow followBehavior) {
		this.followBehavior = followBehavior;
	}

	public void setPatrolBehavior(final CBehaviorPatrol patrolBehavior) {
		this.patrolBehavior = patrolBehavior;
	}

	public void setHoldPositionBehavior(final CBehaviorHoldPosition holdPositionBehavior) {
		this.holdPositionBehavior = holdPositionBehavior;
	}

	public void setBoardTransportBehavior(final CBehaviorBoardTransport boardTransportBehavior) {
		this.boardTransportBehavior = boardTransportBehavior;
	}

	public CBehaviorFollow getFollowBehavior() {
		return this.followBehavior;
	}

	public CBehaviorPatrol getPatrolBehavior() {
		return this.patrolBehavior;
	}

	public CBehaviorHoldPosition getHoldPositionBehavior() {
		return this.holdPositionBehavior;
	}

	public CBehaviorBoardTransport getBoardTransportBehavior() {
		return boardTransportBehavior;
	}

	public CBehavior pollNextOrderBehavior(final CSimulation game) {
		if (this.defaultBehavior != this.stopBehavior) {
			// kind of a stupid hack, meant to align in feel with some behaviors that were
			// observed on War3
			return this.defaultBehavior;
		}
		if (this.interruptedBehavior != null) {
			return this.interruptedBehavior;
		}
		final COrder order = this.orderQueue.poll();
		final CBehavior nextOrderBehavior = beginOrder(game, order);
		this.stateNotifier.waypointsChanged();
		return nextOrderBehavior;
	}

	public boolean isMoving() {
		return getCurrentBehavior() instanceof CBehaviorMove;
	}

	public void setConstructing(final boolean constructing) {
		this.constructing = constructing;
		if (constructing) {
			this.unitAnimationListener.playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 0.0f, true);
		}
	}

	public void setConstructingPaused(final boolean constructingPaused) {
		this.constructingPaused = constructingPaused;
	}

	public void setConstructionProgress(final float constructionProgress) {
		this.constructionProgress = constructionProgress;
	}

	public boolean isConstructing() {
		return this.constructing && (this.upgradeIdType == null);
	}

	public boolean isConstructingPaused() {
		return this.constructingPaused;
	}

	public boolean isUpgrading() {
		return this.constructing && (this.upgradeIdType != null);
	}

	public War3ID getUpgradeIdType() {
		return this.upgradeIdType;
	}

	public boolean isConstructingOrUpgrading() {
		return this.constructing;
	}

	public float getConstructionProgress() {
		return this.constructionProgress;
	}

	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
		this.stateNotifier.hideStateChanged();
	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void setAcceptingOrders(final boolean acceptingOrders) {
		this.acceptingOrders = acceptingOrders;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setInvulnerable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setWorkerInside(final CUnit unit) {
		this.workerInside = unit;
	}

	public CUnit getWorkerInside() {
		return this.workerInside;
	}

	private void nudgeAround(final CSimulation simulation, final CUnit structure) {
		setPointAndCheckUnstuck(structure.getX(), structure.getY(), simulation);
	}

	@Override
	public void setLife(final CSimulation simulation, final float life) {
		final boolean wasDead = isDead();
		super.setLife(simulation, life);
		if (isDead() && !wasDead) {
			kill(simulation, null);
		}
		this.stateNotifier.lifeChanged();
	}

	private boolean queue(final CSimulation game, final War3ID rawcode, final QueueItemType queueItemType) {
		for (int i = 0; i < this.buildQueue.length; i++) {
			if (this.buildQueue[i] == null) {
				setBuildQueueItem(game, i, rawcode, queueItemType);
				if ((queueItemType == QueueItemType.UNIT) || (queueItemType == QueueItemType.RESEARCH)
						|| (queueItemType == QueueItemType.SACRIFICE)) {
					final CPlayer player = game.getPlayer(this.playerIndex);
					player.addTechtreeInProgress(rawcode);
				}
				return true;
			}
		}
		return false;
	}

	public War3ID[] getBuildQueue() {
		return this.buildQueue;
	}

	public QueueItemType[] getBuildQueueTypes() {
		return this.buildQueueTypes;
	}

	public boolean isBuildQueueActive() {
		return this.buildQueueTypes[0] != null;
	}

	public float getBuildQueueTimeRemaining(final CSimulation simulation) {
		if (!isBuildQueueActive()) {
			return 0;
		}
		switch (this.buildQueueTypes[0]) {
		case RESEARCH: {
			final War3ID rawcode = this.buildQueue[0];
			final CUpgradeType trainedUnitType = simulation.getUpgradeData().getType(rawcode);
			return trainedUnitType.getBuildTime(simulation.getPlayer(this.playerIndex).getTechtreeUnlocked(rawcode));
		}
		case SACRIFICE:
		case UNIT: {
			final CUnitType trainedUnitType = simulation.getUnitData().getUnitType(this.buildQueue[0]);
			return trainedUnitType.getBuildTime();
		}
		case HERO_REVIVE: {
			final CUnit hero = simulation.getUnit(this.buildQueue[0].getValue());
			final CUnitType trainedUnitType = hero.getUnitType();
			return simulation.getGameplayConstants().getHeroReviveTime(trainedUnitType.getBuildTime(),
					hero.getHeroData().getHeroLevel());
		}
		default:
			return 0;
		}
	}

	public void cancelBuildQueueItem(final CSimulation game, final int cancelIndex) {
		if ((cancelIndex >= 0) && (cancelIndex < this.buildQueueTypes.length)) {
			final QueueItemType cancelledType = this.buildQueueTypes[cancelIndex];
			if (cancelledType != null) {
				// TODO refund here!
				if (cancelIndex == 0) {
					this.constructionProgress = 0.0f;
					switch (cancelledType) {
					case SACRIFICE:
					case RESEARCH: {
						break;
					}
					case UNIT: {
						final CPlayer player = game.getPlayer(this.playerIndex);
						final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
						player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
						break;
					}
					case HERO_REVIVE: {
						final CPlayer player = game.getPlayer(this.playerIndex);
						final CUnitType unitType = game.getUnit(this.buildQueue[cancelIndex].getValue()).getUnitType();
						player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
						break;
					}
					}
				}
				switch (cancelledType) {
				case RESEARCH: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUpgradeType upgradeType = game.getUpgradeData().getType(this.buildQueue[cancelIndex]);
					player.refundFor(upgradeType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);
					break;
				}
				case UNIT: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
					player.refundFor(unitType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);
					break;
				}
				case SACRIFICE: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
					player.refundFor(unitType);
					player.removeTechtreeInProgress(this.buildQueue[cancelIndex]);

					this.getWorkerInside().setHidden(false);
					break;
				}
				case HERO_REVIVE: {
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnit hero = game.getUnit(this.buildQueue[cancelIndex].getValue());
					final CUnitType unitType = hero.getUnitType();
					final CAbilityHero heroData = hero.getHeroData();
					heroData.setReviving(false);
					final CGameplayConstants gameplayConstants = game.getGameplayConstants();
					player.refund(
							gameplayConstants.getHeroReviveGoldCost(unitType.getGoldCost(), heroData.getHeroLevel()),
							gameplayConstants.getHeroReviveLumberCost(unitType.getLumberCost(),
									heroData.getHeroLevel()));
					break;
				}
				}
				for (int i = cancelIndex; i < (this.buildQueueTypes.length - 1); i++) {
					setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
				}
				setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
				this.stateNotifier.queueChanged();
			}
		}
	}

	public void setBuildQueueItem(final CSimulation game, final int index, final War3ID rawcode,
			final QueueItemType queueItemType) {
		this.buildQueue[index] = rawcode;
		this.buildQueueTypes[index] = queueItemType;
		final CPlayer player = game.getPlayer(this.playerIndex);
		if (index == 0) {
			this.queuedUnitFoodPaid = true;
			if (rawcode != null) {
				if (queueItemType == QueueItemType.UNIT) {
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[index]);
					if (unitType.getFoodUsed() != 0) {
						final int newFoodUsed = player.getFoodUsed() + unitType.getFoodUsed();
						if (newFoodUsed <= player.getFoodCap()) {
							player.setFoodUsed(newFoodUsed);
						} else {
							this.queuedUnitFoodPaid = false;
							game.getCommandErrorListener().showInterfaceError(this.playerIndex,
									CommandStringErrorKeys.NOT_ENOUGH_FOOD);
							player.removeTechtreeInProgress(rawcode);
						}
					}
				} else if (queueItemType == QueueItemType.HERO_REVIVE) {
					final CUnitType unitType = game.getUnit(this.buildQueue[index].getValue()).getUnitType();
					if (unitType.getFoodUsed() != 0) {
						final int newFoodUsed = player.getFoodUsed() + unitType.getFoodUsed();
						if (newFoodUsed <= player.getFoodCap()) {
							player.setFoodUsed(newFoodUsed);
						} else {
							this.queuedUnitFoodPaid = false;
							game.getCommandErrorListener().showInterfaceError(this.playerIndex,
									CommandStringErrorKeys.NOT_ENOUGH_FOOD);
						}
					}
				}
			}
		}
	}

	public void queueTrainingUnit(final CSimulation game, final War3ID rawcode) {
		if (queue(game, rawcode, QueueItemType.UNIT)) {
			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUnitType unitType = game.getUnitData().getUnitType(rawcode);
			final boolean isHeroType = unitType.isHero();
			if (isHeroType && (player.getHeroTokens() > 0)) {
				player.setHeroTokens(player.getHeroTokens() - 1);
			} else {
				player.chargeFor(unitType);
			}
		}
	}

	public void queueSacrificingUnit(final CSimulation game, final War3ID rawcode, final CUnit sacrifice) {
		if (queue(game, rawcode, QueueItemType.SACRIFICE)) {
			sacrifice.setHidden(true);
			this.setWorkerInside(sacrifice);

			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUnitType unitType = game.getUnitData().getUnitType(rawcode);
			final boolean isHeroType = unitType.isHero();
			if (isHeroType && (player.getHeroTokens() > 0)) {
				player.setHeroTokens(player.getHeroTokens() - 1);
			} else {
				player.chargeFor(unitType);
			}
		}
	}

	public void queueRevivingHero(final CSimulation game, final CUnit hero) {
		if (queue(game, new War3ID(hero.getHandleId()), QueueItemType.HERO_REVIVE)) {
			hero.getHeroData().setReviving(true);
			final CPlayer player = game.getPlayer(this.playerIndex);
			final int heroReviveGoldCost = game.getGameplayConstants()
					.getHeroReviveGoldCost(hero.getUnitType().getGoldCost(), hero.getHeroData().getHeroLevel());
			final int heroReviveLumberCost = game.getGameplayConstants()
					.getHeroReviveLumberCost(hero.getUnitType().getGoldCost(), hero.getHeroData().getHeroLevel());
			player.charge(heroReviveGoldCost, heroReviveLumberCost);
		}
	}

	public void queueResearch(final CSimulation game, final War3ID rawcode) {
		if (queue(game, rawcode, QueueItemType.RESEARCH)) {
			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUpgradeType upgradeType = game.getUpgradeData().getType(rawcode);
			player.chargeFor(upgradeType);
		}
	}

	public static enum QueueItemType {
		UNIT, RESEARCH, HERO_REVIVE, SACRIFICE;
	}

	public void setRallyPoint(final AbilityTarget target) {
		this.rallyPoint = target;
		this.stateNotifier.rallyPointChanged();
	}

	public void internalPublishHeroStatsChanged() {
		this.stateNotifier.heroStatsChanged();
	}

	public AbilityTarget getRallyPoint() {
		return this.rallyPoint;
	}

	private static interface RallyProvider {
		float getX();

		float getY();
	}

	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public <T> T visit(final CWidgetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	private static final class UseAbilityOnTargetByIdVisitor implements AbilityTargetVisitor<Void> {
		private static final UseAbilityOnTargetByIdVisitor INSTANCE = new UseAbilityOnTargetByIdVisitor();
		private CSimulation game;
		private CUnit trainedUnit;
		private int rallyOrderId;

		private UseAbilityOnTargetByIdVisitor reset(final CSimulation game, final CUnit trainedUnit,
				final int rallyOrderId) {
			this.game = game;
			this.trainedUnit = trainedUnit;
			this.rallyOrderId = rallyOrderId;
			return this;
		}

		@Override
		public Void accept(final AbilityPointTarget target) {
			CAbility abilityToUse = null;
			for (final CAbility ability : this.trainedUnit.getAbilities()) {
				ability.checkCanUse(this.game, this.trainedUnit, this.rallyOrderId,
						BooleanAbilityActivationReceiver.INSTANCE);
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<AbilityPointTarget>getInstance().reset();
					ability.checkCanTarget(this.game, this.trainedUnit, this.rallyOrderId, target, targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						abilityToUse = ability;
					}
				}
			}
			if (abilityToUse != null) {
				this.trainedUnit.order(this.game,
						new COrderTargetPoint(abilityToUse.getHandleId(), this.rallyOrderId, target, false), false);
			}
			return null;
		}

		@Override
		public Void accept(final CUnit targetUnit) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, targetUnit);
		}

		private Void acceptWidget(final CSimulation game, final CUnit trainedUnit, final int rallyOrderId,
				final CWidget target) {
			CAbility abilityToUse = null;
			for (final CAbility ability : trainedUnit.getAbilities()) {
				ability.checkCanUse(game, trainedUnit, rallyOrderId, BooleanAbilityActivationReceiver.INSTANCE);
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					final BooleanAbilityTargetCheckReceiver<CWidget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<CWidget>getInstance().reset();
					ability.checkCanTarget(game, trainedUnit, rallyOrderId, target, targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						abilityToUse = ability;
					}
				}
			}
			if (abilityToUse != null) {
				trainedUnit.order(game,
						new COrderTargetWidget(abilityToUse.getHandleId(), rallyOrderId, target.getHandleId(), false),
						false);
			}
			return null;
		}

		@Override
		public Void accept(final CDestructable target) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, target);
		}

		@Override
		public Void accept(final CItem target) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, target);
		}
	}

	public int getFoodMade() {
		return this.foodMade;
	}

	public int getFoodUsed() {
		return this.foodUsed;
	}

	public int setFoodMade(final int foodMade) {
		final int delta = foodMade - this.foodMade;
		this.foodMade = foodMade;
		return delta;
	}

	public int setFoodUsed(final int foodUsed) {
		final int delta = foodUsed - this.foodUsed;
		this.foodUsed = foodUsed;
		return delta;
	}

	public void setDefaultBehavior(final CBehavior defaultBehavior) {
		this.defaultBehavior = defaultBehavior;
	}

	public CAbilityGoldMinable getGoldMineData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMinable) {
				return (CAbilityGoldMinable) ability;
			}
		}
		return null;
	}

	public CAbilityOverlayedMine getOverlayedGoldMineData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityOverlayedMine) {
				return (CAbilityOverlayedMine) ability;
			}
		}
		return null;
	}

	public int getGold() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMinable) {
				return ((CAbilityGoldMinable) ability).getGold();
			}
			if (ability instanceof CAbilityOverlayedMine) {
				return ((CAbilityOverlayedMine) ability).getGold();
			}
		}
		return 0;
	}

	public void setGold(final int goldAmount) {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMinable) {
				((CAbilityGoldMinable) ability).setGold(goldAmount);
			}
			if (ability instanceof CAbilityOverlayedMine) {
				((CAbilityOverlayedMine) ability).setGold(goldAmount);
			}
		}
	}

	public Queue<COrder> getOrderQueue() {
		return this.orderQueue;
	}

	public COrder getCurrentOrder() {
		return this.lastStartedOrder;
	}

	public CAbilityHero getHeroData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityHero) {
				return (CAbilityHero) ability;
			}
		}
		return null;
	}

	public CAbilityRoot getRootData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityRoot) {
				return (CAbilityRoot) ability;
			}
		}
		return null;
	}

	public CAbilityInventory getInventoryData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityInventory) {
				return (CAbilityInventory) ability;
			}
		}
		return null;
	}

	public CAbilityNeutralBuilding getNeutralBuildingData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityNeutralBuilding) {
				return (CAbilityNeutralBuilding) ability;
			}
		}
		return null;
	}

	public CAbilityCargoHold getCargoData() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityCargoHold) {
				return (CAbilityCargoHold) ability;
			}
		}
		return null;
	}

	public void setUnitSpecificAttacks(final List<CUnitAttack> unitSpecificAttacks) {
		this.unitSpecificAttacks = unitSpecificAttacks;
	}

	public void setUnitSpecificCurrentAttacks(final List<CUnitAttack> unitSpecificCurrentAttacks) {
		this.unitSpecificCurrentAttacks = unitSpecificCurrentAttacks;
		computeDerivedFields(NonStackingStatBuffType.ATKSPD);
	}

	public List<CUnitAttack> getUnitSpecificAttacks() {
		return this.unitSpecificAttacks;
	}

	public List<CUnitAttack> getCurrentAttacks() {
		if (this.disableAttacks) {
			return Collections.emptyList();
		}
		if (this.unitSpecificCurrentAttacks != null) {
			return this.unitSpecificCurrentAttacks;
		}
		return Collections.emptyList();
	}

	public void setDisableAttacks(final boolean disableAttacks) {
		this.disableAttacks = disableAttacks;
		this.stateNotifier.attacksChanged();
	}

	public boolean isDisableAttacks() {
		return this.disableAttacks;
	}

	public void onPickUpItem(final CSimulation game, final CItem item, final boolean playUserUISounds) {
		this.stateNotifier.inventoryChanged();
		if (playUserUISounds) {
			game.unitPickUpItemEvent(this, item);
		}
		firePickUpItemEvents(game, item);
	}

	public void onDropItem(final CSimulation game, final CItem droppedItem, final boolean playUserUISounds) {
		this.stateNotifier.inventoryChanged();
		if (playUserUISounds) {
			game.unitDropItemEvent(this, droppedItem);
		}
	}

	public boolean isInRegion(final CRegion region) {
		return this.containingRegions.contains(region);
	}

	@Override
	public float getMaxLife() {
		return this.maximumLife;
	}

	private static final class RegionCheckerImpl implements CRegionEnumFunction {
		private CUnit unit;
		private CRegionManager regionManager;

		public RegionCheckerImpl reset(final CUnit unit, final CRegionManager regionManager) {
			this.unit = unit;
			this.regionManager = regionManager;
			return this;
		}

		@Override
		public boolean call(final CRegion region) {
			if (this.unit.containingRegions.add(region)) {
				if (!this.unit.priorContainingRegions.contains(region)) {
					this.regionManager.onUnitEnterRegion(this.unit, region);
				}
			}
			return false;
		}

	}

	public boolean isBuilding() {
		return this.structure;
	}

	public void setStructure(final boolean flag) {
		this.structure = flag;
	}

	public void onRemove(final CSimulation simulation) {
		final CPlayer player = simulation.getPlayer(this.playerIndex);
		if (WarsmashConstants.FIRE_DEATH_EVENTS_ON_REMOVEUNIT) {
			// Firing userspace triggers here causes items to appear around the player bases
			// in melee games.
			// (See "Remove creeps and critters from used start locations" implementation)
			setLife(simulation, 0);
		} else {
			if (this.constructing) {
				player.removeTechtreeInProgress(this.unitType.getTypeId());
			} else {
				player.removeTechtreeUnlocked(simulation, this.unitType.getTypeId());
			}
			setHidden(true);
			// setting hidden to let things that refer to this before it gets garbage
			// collected see it as basically worthless
		}
		simulation.getWorldCollision().removeUnit(this);
	}

	private static enum StateListenerUpdateType {
		ADD, REMOVE;
	}

	private static final class StateListenerUpdate {
		private final CUnitStateListener listener;
		private final StateListenerUpdateType updateType;

		public StateListenerUpdate(final CUnitStateListener listener, final StateListenerUpdateType updateType) {
			this.listener = listener;
			this.updateType = updateType;
		}

		public CUnitStateListener getListener() {
			return this.listener;
		}

		public StateListenerUpdateType getUpdateType() {
			return this.updateType;
		}
	}

	public void cancelUpgrade(final CSimulation game) {
		final CPlayer player = game.getPlayer(this.playerIndex);
		player.setUnitFoodUsed(this, this.unitType.getFoodUsed());
		int goldCost, lumberCost;
		final CUnitType newUpgradeUnitType = game.getUnitData().getUnitType(this.upgradeIdType);
		if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
			goldCost = newUpgradeUnitType.getGoldCost() - this.unitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost() - this.unitType.getLumberCost();
		} else {
			goldCost = newUpgradeUnitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost();
		}
		player.refund(goldCost, lumberCost);

		final Iterator<CAbility> abilityIterator = this.abilities.iterator();
		while (abilityIterator.hasNext()) {
			final CAbility ability = abilityIterator.next();
			if (ability instanceof CAbilityBuildInProgress) {
				abilityIterator.remove();
			} else {
				ability.setDisabled(false, CAbilityDisableType.CONSTRUCTION);
				ability.setIconShowing(true);
			}
		}
		this.checkDisabledAbilities(game, false);

		game.unitCancelUpgradingEvent(this, this.upgradeIdType);
		this.upgradeIdType = null;
		this.constructing = false;
		this.constructionProgress = 0;
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 0.0f, true);
	}

	public void beginUpgrade(final CSimulation game, final War3ID rawcode) {
		this.upgradeIdType = rawcode;
		this.constructing = true;
		this.constructionProgress = 0;

		final CPlayer player = game.getPlayer(this.playerIndex);
		final CUnitType newUpgradeUnitType = game.getUnitData().getUnitType(rawcode);
		player.setUnitFoodUsed(this, newUpgradeUnitType.getFoodUsed());
		int goldCost, lumberCost;
		if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
			goldCost = newUpgradeUnitType.getGoldCost() - this.unitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost() - this.unitType.getLumberCost();
		} else {
			goldCost = newUpgradeUnitType.getGoldCost();
			lumberCost = newUpgradeUnitType.getLumberCost();
		}
		player.charge(goldCost, lumberCost);
		add(game, new CAbilityBuildInProgress(game.getHandleIdAllocator().createId()));
		for (final CAbility ability : getAbilities()) {
			ability.visit(AbilityDisableWhileUpgradingVisitor.INSTANCE);
		}
		this.checkDisabledAbilities(game, true);
		player.addTechtreeInProgress(rawcode);

		game.unitUpgradingEvent(this, rawcode);
		this.unitAnimationListener.playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 0.0f, true);
	}

	public void setUnitState(final CSimulation game, final CUnitState whichUnitState, final float value) {
		switch (whichUnitState) {
		case LIFE:
			setLife(game, value);
			break;
		case MANA:
			setMana(value);
			break;
		case MAX_LIFE:
			setMaximumLife((int) value);
			break;
		case MAX_MANA:
			setMaximumMana((int) value);
			break;
		}
	}

	public float getUnitState(final CSimulation game, final CUnitState whichUnitState) {
		switch (whichUnitState) {
		case LIFE:
			return getLife();
		case MANA:
			return getMana();
		case MAX_LIFE:
			return getMaximumLife();
		case MAX_MANA:
			return getMaximumMana();
		}
		return 0;
	}

	public boolean isUnitType(final CUnitTypeJass whichUnitType) {
		switch (whichUnitType) {
		case HERO:
			return isHero();
		case DEAD:
			return isDead();
		case STRUCTURE:
			return isBuilding();

		case FLYING:
			return getUnitType().getTargetedAs().contains(CTargetType.AIR);
		case GROUND:
			return getUnitType().getTargetedAs().contains(CTargetType.GROUND);

		case ATTACKS_FLYING:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getTargetsAllowed().contains(CTargetType.AIR)) {
					return true;
				}
			}
			return false;
		case ATTACKS_GROUND:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getTargetsAllowed().contains(CTargetType.GROUND)) {
					return true;
				}
			}
			return false;

		case MELEE_ATTACKER:
			boolean hasAttacks = false;
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getWeaponType() != CWeaponType.NORMAL) {
					return false;
				}
				hasAttacks = true;
			}
			return hasAttacks;

		case RANGED_ATTACKER:
			for (final CUnitAttack attack : getCurrentAttacks()) {
				if (attack.getWeaponType() != CWeaponType.NORMAL) {
					return true;
				}
			}
			return false;

		case GIANT:
			return getUnitType().getClassifications().contains(CUnitClassification.GIANT);
		case SUMMONED:
			return getUnitType().getClassifications().contains(CUnitClassification.SUMMONED);
		case STUNNED:
			return getCurrentBehavior().getHighlightOrderId() == OrderIds.stunned;
		case PLAGUED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is plagued: plague is not yet implemented");
		case SNARED:
			boolean isSnared = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SNARED) {
					if (buff.getValue() != 0) {
						isSnared = true;
					}
				}
			}
			return isSnared;
		case UNDEAD:
			return getUnitType().getClassifications().contains(CUnitClassification.UNDEAD);
		case MECHANICAL:
			return getUnitType().getClassifications().contains(CUnitClassification.MECHANICAL);
		case PEON:
			return getUnitType().getClassifications().contains(CUnitClassification.PEON);
		case SAPPER:
			return getUnitType().getClassifications().contains(CUnitClassification.SAPPER);
		case TOWNHALL:
			return getUnitType().getClassifications().contains(CUnitClassification.TOWNHALL);
		case ANCIENT:
			return this.unitType.getClassifications().contains(CUnitClassification.ANCIENT);

		case TAUREN:
			return getUnitType().getClassifications().contains(CUnitClassification.TAUREN);
		case POISONED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is poisoned: poison is not yet implemented");
		case POLYMORPHED:
			throw new UnsupportedOperationException(
					"cannot ask engine if unit is POLYMORPHED: POLYMORPHED is not yet implemented");
		case SLEEPING:
			boolean isSleeping = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.SLEEPING) {
					if (buff.getValue() != 0) {
						isSleeping = true;
					}
				}
			}
			return isSleeping;
		case RESISTANT:
			return this.resistant;
		case ETHEREAL:
			boolean isEthereal = false;
			for (final StateModBuff buff : stateModBuffs) {
				if (buff.getBuffType() == StateModBuffType.ETHEREAL) {
					if (buff.getValue() != 0) {
						isEthereal = true;
					}
				}
			}
			return isEthereal;
		case MAGIC_IMMUNE:
			return isMagicImmune();
		}
		return false;
	}

	public int getTriggerEditorCustomValue() {
		return this.triggerEditorCustomValue;
	}

	public void setTriggerEditorCustomValue(final int triggerEditorCustomValue) {
		this.triggerEditorCustomValue = triggerEditorCustomValue;
	}

	public static String maybeMeaningfulName(final CUnit unit) {
		if (unit == null) {
			return "null";
		}
		return unit.getUnitType().getName();
	}

	public void fireCooldownsChangedEvent() {
		this.stateNotifier.ordersChanged();
	}

	public int getAbilityLevel(final War3ID abilityId) {
		final CLevelingAbility ability = getAbility(GetAbilityByRawcodeVisitor.getInstance().reset(abilityId));
		if (ability == null) {
			return 0;
		} else {
			return ability.getLevel();
		}
	}

	public boolean chargeMana(final int manaCost) {
		if (this.mana >= manaCost) {
			setMana(this.mana - manaCost);
			return true;
		}
		return false;
	}

	public void fireSpellEvents(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability,
			final AbilityTarget target) {
		if (target == null) {
			this.fireSpellEventsNoTarget(game, eventId, ability);
		} else {
			target.visit(new AbilityTargetVisitor<Object>() {

				@Override
				public Object accept(AbilityPointTarget target) {
					fireSpellEventsPointTarget(game, eventId, ability, target);
					return null;
				}

				@Override
				public Object accept(CUnit target) {
					fireSpellEventsUnitTarget(game, eventId, ability, target);
					return null;
				}

				@Override
				public Object accept(CDestructable target) {
					fireSpellEventsDestructableTarget(game, eventId, ability, target);
					return null;
				}

				@Override
				public Object accept(CItem target) {
					fireSpellEventsItemTarget(game, eventId, ability, target);
					return null;
				}

			});
		}
	}

	public void fireSpellEventsNoTarget(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability) {
		final List<CWidgetEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitSpellNoTargetScope(eventId, event.getTrigger(),
						ability, this, ability.getAlias()));
			}
		}
		game.getPlayer(this.playerIndex).fireSpellEventsNoTarget(unitSpellEventToPlayerEvent(eventId), ability, this);
		game.fireSpellEventsNoTarget(eventId, ability, this);
	}

	public void fireSpellEventsPointTarget(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability,
			final AbilityPointTarget target) {
		final List<CWidgetEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitSpellPointScope(eventId, event.getTrigger(), ability,
						this, target, ability.getAlias()));
			}
		}
		game.getPlayer(this.playerIndex).fireSpellEventsPointTarget(unitSpellEventToPlayerEvent(eventId), ability, this,
				target);
		game.fireSpellEventsPointTarget(eventId, ability, this, target);
	}

	public void fireSpellEventsUnitTarget(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability,
			final CUnit target) {
		final List<CWidgetEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitSpellTargetUnitScope(eventId, event.getTrigger(),
						ability, this, target, ability.getAlias()));
			}
		}
		game.getPlayer(this.playerIndex).fireSpellEventsUnitTarget(unitSpellEventToPlayerEvent(eventId), ability, this,
				target);
		game.fireSpellEventsUnitTarget(eventId, ability, this, target);
	}

	public void fireSpellEventsItemTarget(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability,
			final CItem target) {
		final List<CWidgetEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitSpellTargetItemScope(eventId, event.getTrigger(),
						ability, this, target, ability.getAlias()));
			}
		}
		game.getPlayer(this.playerIndex).fireSpellEventsItemTarget(unitSpellEventToPlayerEvent(eventId), ability, this,
				target);
		game.fireSpellEventsItemTarget(eventId, ability, this, target);
	}

	public void fireSpellEventsDestructableTarget(final CSimulation game, JassGameEventsWar3 eventId, CAbility ability,
			final CDestructable target) {
		final List<CWidgetEvent> eventList = getEventList(eventId);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitSpellTargetDestructableScope(eventId,
						event.getTrigger(), ability, this, target, ability.getAlias()));
			}
		}
		game.getPlayer(this.playerIndex).fireSpellEventsDestructableTarget(unitSpellEventToPlayerEvent(eventId),
				ability, this, target);
		game.fireSpellEventsDestructableTarget(eventId, ability, this, target);
	}

	private JassGameEventsWar3 unitSpellEventToPlayerEvent(JassGameEventsWar3 eventId) {
		switch (eventId) {
		case EVENT_UNIT_SPELL_CAST:
			return JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_CAST;
		case EVENT_UNIT_SPELL_CHANNEL:
			return JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_CHANNEL;
		case EVENT_UNIT_SPELL_EFFECT:
			return JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_EFFECT;
		case EVENT_UNIT_SPELL_FINISH:
			return JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_FINISH;
		case EVENT_UNIT_SPELL_ENDCAST:
			return JassGameEventsWar3.EVENT_PLAYER_UNIT_SPELL_ENDCAST;
		default:
			return null;
		}
	}

	public void firePickUpItemEvents(final CSimulation game, final CItem item) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_PICKUP_ITEM);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitPickupItemScope(
						JassGameEventsWar3.EVENT_UNIT_PICKUP_ITEM, event.getTrigger(), this, item));
			}
		}
		game.getPlayer(this.playerIndex).firePickUpItemEvents(this, item, game);
	}

	public void fireOrderEvents(final CSimulation game, final COrderNoTarget order) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_ORDER);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitOrderScope(JassGameEventsWar3.EVENT_UNIT_ISSUED_ORDER,
						event.getTrigger(), this, order.getOrderId()));
			}
		}
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	public void fireOrderEvents(final CSimulation game, final COrderTargetPoint order) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_POINT_ORDER);
		if (eventList != null) {
			final AbilityPointTarget target = order.getTarget(game);
			for (final CWidgetEvent event : eventList) {
				event.fire(this,
						CommonTriggerExecutionScope.unitOrderPointScope(
								JassGameEventsWar3.EVENT_UNIT_ISSUED_POINT_ORDER, event.getTrigger(), this,
								order.getOrderId(), target.x, target.y));
			}
		}
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	public void fireOrderEvents(final CSimulation game, final COrderTargetWidget order) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_ISSUED_TARGET_ORDER);
		if (eventList != null) {
			final CWidget target = order.getTarget(game);
			for (final CWidgetEvent event : eventList) {
				event.fire(this,
						CommonTriggerExecutionScope.unitOrderTargetScope(
								JassGameEventsWar3.EVENT_UNIT_ISSUED_TARGET_ORDER, event.getTrigger(), this,
								order.getOrderId(), target));
			}
		}
		game.getPlayer(this.playerIndex).fireOrderEvents(this, game, order);
	}

	public void fireConstructFinishEvents(final CSimulation game) {
		final CUnit constructingUnit = this.workerInside; // TODO incorrect for human/undead/ancient, etc, needs work
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_CONSTRUCT_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitConstructFinishScope(
						JassGameEventsWar3.EVENT_UNIT_CONSTRUCT_FINISH, event.getTrigger(), this, constructingUnit));
			}
		}
		game.getPlayer(this.playerIndex).fireConstructFinishEvents(this, game, constructingUnit);
	}

	public void fireTrainFinishEvents(final CSimulation game, final CUnit trainedUnit) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_TRAIN_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitTrainFinishScope(
						JassGameEventsWar3.EVENT_UNIT_TRAIN_FINISH, event.getTrigger(), this, trainedUnit));
			}
		}
		game.getPlayer(this.playerIndex).fireTrainFinishEvents(this, game, trainedUnit);
	}

	public void fireResearchFinishEvents(final CSimulation game, final War3ID researched) {
		final List<CWidgetEvent> eventList = getEventList(JassGameEventsWar3.EVENT_UNIT_RESEARCH_FINISH);
		if (eventList != null) {
			for (final CWidgetEvent event : eventList) {
				event.fire(this, CommonTriggerExecutionScope.unitResearchFinishScope(
						JassGameEventsWar3.EVENT_UNIT_RESEARCH_FINISH, event.getTrigger(), this, researched));
			}
		}
		game.getPlayer(this.playerIndex).fireResearchFinishEvents(this, game, researched);
	}

	public boolean isHero() {
		return getHeroData() != null; // in future maybe do this with better performance
	}

	public boolean isUnitAlly(final CPlayer whichPlayer) {
		return whichPlayer.hasAlliance(getPlayerIndex(), CAllianceType.PASSIVE);
	}

	public ResourceType backToWork(final CSimulation game, final ResourceType defaultResourceType) {
		// if possible, check if this is a worker and send it to work
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityHarvest) {
				final CAbilityHarvest abilityHarvest = (CAbilityHarvest) ability;
				final int carriedResourceAmount = abilityHarvest.getCarriedResourceAmount();
				final ResourceType carriedResourceType = abilityHarvest.getCarriedResourceType();
				if (carriedResourceAmount != 0) {
					switch (carriedResourceType) {
					case GOLD:
						if (carriedResourceAmount >= abilityHarvest.getGoldCapacity()) {
							abilityHarvest.getBehaviorReturnResources().reset(game);
							this.order(game, OrderIds.returnresources,
									abilityHarvest.getBehaviorReturnResources().findNearestDropoffPoint(game));
						} else {
							this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
						}
						return ResourceType.GOLD;
					case LUMBER:
						if (carriedResourceAmount >= abilityHarvest.getLumberCapacity()) {
							abilityHarvest.getBehaviorReturnResources().reset(game);
							this.order(game, OrderIds.returnresources,
									abilityHarvest.getBehaviorReturnResources().findNearestDropoffPoint(game));
						} else {
							this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
									abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
						}
						return ResourceType.LUMBER;
					default:
						throw new IllegalStateException(
								"Worker was carrying a resource of unsupported type: " + carriedResourceType);
					}
				} else if (carriedResourceType != null) {
					if (carriedResourceType == ResourceType.GOLD) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
						return ResourceType.GOLD;
					} else if (carriedResourceType == ResourceType.LUMBER) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
								abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
						return ResourceType.LUMBER;
					}
				} else if (defaultResourceType != null) {
					if (((defaultResourceType == ResourceType.GOLD) || (abilityHarvest.getLumberCapacity() == 0))
							&& (abilityHarvest.getGoldCapacity() > 0)) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestMine(this, game));
						return ResourceType.GOLD;
					} else if (abilityHarvest.getLumberCapacity() > 0) {
						this.order(game, OrderIds.harvest, CBehaviorReturnResources.findNearestTree(this,
								abilityHarvest, game, abilityHarvest.getLastHarvestTarget()));
						return ResourceType.LUMBER;
					}
				}
			}
		}

		return null;
	}

	public void notifyAttacksChanged() {
		this.stateNotifier.attacksChanged();
	}

	public void notifyOrdersChanged() {
		this.stateNotifier.ordersChanged();
	}

	public void setConstructionConsumesWorker(final boolean constructionConsumesWorker) {
		this.constructionConsumesWorker = constructionConsumesWorker;
	}

	public boolean isConstructionConsumesWorker() {
		return this.constructionConsumesWorker;
	}

	public CDefenseType getDefenseType() {
		return this.defenseType;
	}

	public void setDefenseType(final CDefenseType defenseType) {
		this.defenseType = defenseType;
	}

	public void updateFogOfWar(final CSimulation game) {
		if (!isDead() && !this.hidden) {
			final float sightRadius = game.isDay() ? this.unitType.getSightRadiusDay()
					: this.unitType.getSightRadiusNight();
			if (sightRadius > 0) {
				final float radSq = sightRadius * sightRadius / (CPlayerFogOfWar.GRID_STEP * CPlayerFogOfWar.GRID_STEP);
				final CPlayerFogOfWar fogOfWar = game.getPlayer(this.playerIndex).getFogOfWar();
				final boolean flying = this.getUnitType().getMovementType() == MovementType.FLY;
				final float myX = getX();
				final float myY = getY();
				final int myZ = flying ? Integer.MAX_VALUE : game.getTerrainHeight(myX, myY);
				final PathingGrid pathingGrid = game.getPathingGrid();
//				final byte vision = CFogState.VISIBLE.getId();
//				final byte visible = CFogState.VISIBLE.getId();
				fogOfWar.setVisible(pathingGrid.getFogOfWarIndexX(myX), pathingGrid.getFogOfWarIndexY(myY), CFogState.VISIBLE);

				int myXi = pathingGrid.getFogOfWarIndexX(myX);
				int myYi = pathingGrid.getFogOfWarIndexY(myY);
				int maxXi = pathingGrid.getFogOfWarIndexX(myX + sightRadius);
				int maxYi = pathingGrid.getFogOfWarIndexY(myY + sightRadius);
				for (int a = 1; a <= Math.max(maxYi - myYi, maxXi - myXi); a++) {
					int distance = a * a;

					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX, myY - (a - 1) * CPlayerFogOfWar.GRID_STEP))
							&& fogOfWar.isVisible(myXi, myYi - a + 1)
							&& (flying || game.isTerrainWater(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
									|| myZ > game.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)
									|| (!game.isTerrainRomp(myX, myY - a * CPlayerFogOfWar.GRID_STEP) && myZ == game
											.getTerrainHeight(myX, myY - a * CPlayerFogOfWar.GRID_STEP)))) {
						fogOfWar.setVisible(myXi, myYi - a, CFogState.VISIBLE);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX, myY + (a - 1) * CPlayerFogOfWar.GRID_STEP))
							&& fogOfWar.isVisible(myXi, myYi + a - 1)
							&& (flying || game.isTerrainWater(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
									|| myZ > game.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)
									|| (!game.isTerrainRomp(myX, myY + a * CPlayerFogOfWar.GRID_STEP) && myZ == game
											.getTerrainHeight(myX, myY + a * CPlayerFogOfWar.GRID_STEP)))) {
						fogOfWar.setVisible(myXi, myYi + a, CFogState.VISIBLE);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX - (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
							&& fogOfWar.isVisible(myXi - a + 1, myYi)
							&& (flying || game.isTerrainWater(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
									|| myZ > game.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)
									|| (!game.isTerrainRomp(myX - a * CPlayerFogOfWar.GRID_STEP, myY) && myZ == game
											.getTerrainHeight(myX - a * CPlayerFogOfWar.GRID_STEP, myY)))) {
						fogOfWar.setVisible(myXi - a, myYi, CFogState.VISIBLE);
					}
					if (distance <= radSq
							&& (flying || !pathingGrid.isBlockVision(myX + (a - 1) * CPlayerFogOfWar.GRID_STEP, myY))
							&& fogOfWar.isVisible(myXi + a - 1, myYi)
							&& (flying || game.isTerrainWater(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
									|| myZ > game.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)
									|| (!game.isTerrainRomp(myX + a * CPlayerFogOfWar.GRID_STEP, myY) && myZ == game
											.getTerrainHeight(myX + a * CPlayerFogOfWar.GRID_STEP, myY)))) {
						fogOfWar.setVisible(myXi + a, myYi, CFogState.VISIBLE);
					}
				}

				for (int y = 1; y <= maxYi - myYi; y++) {
					for (int x = 1; x <= maxXi - myXi; x++) {
						float distance = x * x + y * y;
						if (distance <= radSq) {
							int xf = x * CPlayerFogOfWar.GRID_STEP;
							int yf = y * CPlayerFogOfWar.GRID_STEP;

							if ((flying || game.isTerrainWater(myX - xf, myY - yf)
									|| myZ > game.getTerrainHeight(myX - xf, myY - yf)
									|| (!game.isTerrainRomp(myX - xf, myY - yf)
											&& myZ == game.getTerrainHeight(myX - xf, myY - yf)))
									&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
											myY - yf + CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.isVisible(myXi - x + 1, myYi - y + 1)
									&& (x == y
											|| (x > y && fogOfWar.isVisible(myXi - x + 1, myYi - y)
													&& (flying || !pathingGrid.isBlockVision(
															myX - xf + CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| (x < y && fogOfWar.isVisible(myXi - x, myYi - y + 1)
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setVisible(myXi - x, myYi - y, CFogState.VISIBLE);
							}
							if ((flying || game.isTerrainWater(myX - xf, myY + yf)
									|| myZ > game.getTerrainHeight(myX - xf, myY + yf)
									|| (!game.isTerrainRomp(myX - xf, myY + yf)
											&& myZ == game.getTerrainHeight(myX - xf, myY + yf)))
									&& (flying || !pathingGrid.isBlockVision(myX - xf + CPlayerFogOfWar.GRID_STEP,
											myY + yf - CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.isVisible(myXi - x + 1, myYi + y - 1)
									&& (x == y
											|| (x > y && fogOfWar.isVisible(myXi - x + 1, myYi + y)
													&& (flying || !pathingGrid.isBlockVision(
															myX - xf + CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| (x < y && fogOfWar.isVisible(myXi - x, myYi + y - 1)
													&& (flying || !pathingGrid.isBlockVision(myX - xf,
															myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setVisible(myXi - x, myYi + y, CFogState.VISIBLE);
							}
							if ((flying || game.isTerrainWater(myX + xf, myY - yf)
									|| myZ > game.getTerrainHeight(myX + xf, myY - yf)
									|| (!game.isTerrainRomp(myX + xf, myY - yf)
											&& myZ == game.getTerrainHeight(myX + xf, myY - yf)))
									&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
											myY - yf + CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.isVisible(myXi + x - 1, myYi - y + 1)
									&& (x == y
											|| (x > y && fogOfWar.isVisible(myXi + x - 1, myYi - y)
													&& (flying || !pathingGrid.isBlockVision(
															myX + xf - CPlayerFogOfWar.GRID_STEP, myY - yf)))
											|| (x < y && fogOfWar.isVisible(myXi + x, myYi - y + 1)
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															myY - yf + CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setVisible(myXi + x, myYi - y, CFogState.VISIBLE);
							}
							if ((flying || game.isTerrainWater(myX + xf, myY + yf)
									|| myZ > game.getTerrainHeight(myX + xf, myY + yf)
									|| (!game.isTerrainRomp(myX + xf, myY + yf)
											&& myZ == game.getTerrainHeight(myX + xf, myY + yf)))
									&& (flying || !pathingGrid.isBlockVision(myX + xf - CPlayerFogOfWar.GRID_STEP,
											myY + yf - CPlayerFogOfWar.GRID_STEP))
									&& fogOfWar.isVisible(myXi + x - 1, myYi + y - 1)
									&& (x == y
											|| (x > y && fogOfWar.isVisible(myXi + x - 1, myYi + y)
													&& (flying || !pathingGrid.isBlockVision(
															myX + xf - CPlayerFogOfWar.GRID_STEP, myY + yf)))
											|| (x < y && fogOfWar.isVisible(myXi + x, myYi + y - 1)
													&& (flying || !pathingGrid.isBlockVision(myX + xf,
															myY + yf - CPlayerFogOfWar.GRID_STEP))))) {
								fogOfWar.setVisible(myXi + x, myYi + y, CFogState.VISIBLE);
							}
						}
					}
				}
			}
		}
	}

	public void setExplodesOnDeath(final boolean explodesOnDeath) {
		this.explodesOnDeath = explodesOnDeath;
	}

	public void setExplodesOnDeathBuffId(final War3ID explodesOnDeathBuffId) {
		this.explodesOnDeathBuffId = explodesOnDeathBuffId;
	}

	public boolean isExplodesOnDeath() {
		return this.explodesOnDeath;
	}

	public List<CUnitAttackPreDamageListener> getPreDamageListenersForPriority(
			final CUnitAttackPreDamageListenerPriority priority) {
		return preDamageListeners.get(priority);
	}

	public void addPreDamageListener(final CUnitAttackPreDamageListenerPriority priority,
			final CUnitAttackPreDamageListener listener) {
		List<CUnitAttackPreDamageListener> list = preDamageListeners.get(priority);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(0, listener);
	}

	public void removePreDamageListener(final CUnitAttackPreDamageListenerPriority priority,
			final CUnitAttackPreDamageListener listener) {
		final List<CUnitAttackPreDamageListener> list = preDamageListeners.get(priority);
		if (list != null) {
			list.remove(listener);
		}
	}

	public List<CUnitAttackPostDamageListener> getPostDamageListeners() {
		return this.postDamageListeners;
	}

	public void addPostDamageListener(final CUnitAttackPostDamageListener listener) {
		postDamageListeners.add(0, listener);
	}

	public void removePostDamageListener(final CUnitAttackPostDamageListener listener) {
		postDamageListeners.remove(listener);
	}

	public void addDamageTakenModificationListener(final CUnitAttackDamageTakenModificationListener listener) {
		damageTakenModificationListeners.add(0, listener);
	}

	public void removeDamageTakenModificationListener(final CUnitAttackDamageTakenModificationListener listener) {
		damageTakenModificationListeners.remove(listener);
	}

	public void addFinalDamageTakenModificationListener(
			final CUnitAttackFinalDamageTakenModificationListener listener) {
		finalDamageTakenModificationListeners.add(0, listener);
	}

	public void removeFinalDamageTakenModificationListener(
			final CUnitAttackFinalDamageTakenModificationListener listener) {
		finalDamageTakenModificationListeners.remove(listener);
	}

	public void addDamageTakenListener(final CUnitAttackDamageTakenListener listener) {
		damageTakenListeners.add(0, listener);
	}

	public void removeDamageTakenListener(final CUnitAttackDamageTakenListener listener) {
		damageTakenListeners.remove(listener);
	}

	public List<CUnitDeathReplacementEffect> getDeathReplacementEffectsForPriority(
			final CUnitDeathReplacementEffectPriority priority) {
		return deathReplacementEffects.get(priority);
	}

	public void addDeathReplacementEffect(final CUnitDeathReplacementEffectPriority priority,
			final CUnitDeathReplacementEffect listener) {
		List<CUnitDeathReplacementEffect> list = deathReplacementEffects.get(priority);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(0, listener);
	}

	public void removeDeathReplacementEffect(final CUnitDeathReplacementEffectPriority priority,
			final CUnitDeathReplacementEffect listener) {
		final List<CUnitDeathReplacementEffect> list = deathReplacementEffects.get(priority);
		if (list != null) {
			list.remove(listener);
		}
	}

	public void addEvasionListener(final CUnitAttackEvasionListener listener) {
		evasionListeners.add(0, listener);
	}

	public void removeEvasionListener(final CUnitAttackEvasionListener listener) {
		evasionListeners.remove(listener);
	}

	public void addAttackProjReactionListener(final CUnitAttackProjReactionListener listener) {
		this.attackProjReactionListeners.add(0, listener);
	}

	public void removeAttackProjReactionListener(final CUnitAttackProjReactionListener listener) {
		this.attackProjReactionListeners.remove(listener);
	}

	public void addAbilityProjReactionListener(final CUnitAbilityProjReactionListener listener) {
		this.abilityProjReactionListeners.add(0, listener);
	}

	public void removeAbilityProjReactionListener(final CUnitAbilityProjReactionListener listener) {
		this.abilityProjReactionListeners.remove(listener);
	}

	public void addAbilityEffectReactionListener(final CUnitAbilityEffectReactionListener listener) {
		this.abilityEffectReactionListeners.add(0, listener);
	}

	public void removeAbilityEffectReactionListener(final CUnitAbilityEffectReactionListener listener) {
		this.abilityEffectReactionListeners.remove(listener);
	}

	public void beginCooldown(final CSimulation game, final War3ID abilityId, final float cooldownDuration) {
		final int gameTurnTick = game.getGameTurnTick();
		rawcodeToCooldownExpireTime.put(abilityId.getValue(),
				gameTurnTick + (int) StrictMath.ceil(cooldownDuration / WarsmashConstants.SIMULATION_STEP_TIME));
		rawcodeToCooldownStartTime.put(abilityId.getValue(), gameTurnTick);
		fireCooldownsChangedEvent();
	}

	public int getCooldownRemainingTicks(final CSimulation game, final War3ID abilityId) {
		final int expireTime = rawcodeToCooldownExpireTime.get(abilityId.getValue(), -1);
		final int gameTurnTick = game.getGameTurnTick();
		if ((expireTime == -1) || (expireTime <= gameTurnTick)) {
			return 0;
		}
		return expireTime - gameTurnTick;
	}

	public int getCooldownLengthDisplayTicks(final CSimulation game, final War3ID abilityId) {
		final int startTime = rawcodeToCooldownStartTime.get(abilityId.getValue(), -1);
		final int expireTime = rawcodeToCooldownExpireTime.get(abilityId.getValue(), -1);
		if ((startTime == -1) || (expireTime == -1)) {
			return 0;
		}
		return expireTime - startTime;
	}

	public boolean isRaisable() {
		return raisable;
	}

	public boolean isDecays() {
		return decays;
	}

	public void setRaisable(final boolean raisable) {
		this.raisable = raisable;
	}

	public void setDecays(final boolean decays) {
		this.decays = decays;
	}

	public void setMagicImmune(final boolean magicImmune) {
		this.magicImmune = magicImmune;
	}

	public boolean isMagicImmune() {
		return magicImmune;
	}

	public boolean isFalseDeath() {
		return falseDeath;
	}

	public void setFalseDeath(final boolean falseDeath) {
		this.falseDeath = falseDeath;
	}

	public void setAutocastAbility(CAutocastAbility autocastAbility) {
		if (this.autocastAbility != null) {
			this.autocastAbility.setAutoCastOff();
		}
		this.autocastAbility = autocastAbility;
	}

	public boolean isVisible(final CSimulation simulation, final int toPlayerIndex) {
		final CPlayer toPlayer = simulation.getPlayer(toPlayerIndex);
		if ((toPlayerIndex == this.playerIndex || toPlayer.hasAlliance(this.playerIndex, CAllianceType.SHARED_VISION))
				&& (simulation.isDay() ? this.unitType.getSightRadiusDay() : this.unitType.getSightRadiusNight()) > 0) {
			return true;
		}
		return toPlayer.getFogOfWar().isVisible(simulation.getPathingGrid(), this.getX(), this.getY());
//		System.err.println(fogState + " =? " + CFogState.VISIBLE.getId());
//		if ((fogState & CFogState.VISIBLE.getId()) != 0) {
//			return true;
//		}
//		return false;
	}
}
