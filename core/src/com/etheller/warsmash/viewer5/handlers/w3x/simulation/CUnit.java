package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener.CUnitStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorFollow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorPatrol;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorStop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;

public class CUnit extends CWidget {
	private static final Rectangle tempRect = new Rectangle();
	private War3ID typeId;
	private float facing; // degrees
	private float mana;
	private int maximumLife;
	private int maximumMana;
	private int speed;
	private final int defense;
	private int cooldownEndTime = 0;
	private float flyHeight;
	private int playerIndex;

	private final List<CAbility> abilities = new ArrayList<>();

	private CBehavior currentBehavior;
	private COrder currentOrder;
	private final Queue<COrder> orderQueue = new LinkedList<>();
	private final CUnitType unitType;

	private Rectangle collisionRectangle;
	private RemovablePathingMapInstance pathingInstance;
	private BuildingShadow buildingShadowInstance;

	private final EnumSet<CUnitClassification> classifications = EnumSet.noneOf(CUnitClassification.class);

	private int deathTurnTick;
	private boolean corpse;
	private boolean boneCorpse;

	private transient CUnitAnimationListener unitAnimationListener;

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CUnitStateNotifier stateNotifier = new CUnitStateNotifier();
	private final float acquisitionRange;
	private transient static AutoAttackTargetFinderEnum autoAttackTargetFinderEnum = new AutoAttackTargetFinderEnum();

	private transient CBehaviorMove moveBehavior;
	private transient CBehaviorAttack attackBehavior;
	private transient CBehaviorFollow followBehavior;
	private transient CBehaviorPatrol patrolBehavior;
	private transient CBehaviorStop stopBehavior;
	private boolean constructing = false;
	private float constructionProgress;
	private boolean hidden = false;
	private boolean paused = false;
	private boolean acceptingOrders = true;
	private boolean invulnerable = false;
	private CUnit workerInside;
	private final War3ID[] buildQueue = new War3ID[WarsmashConstants.BUILD_QUEUE_SIZE];
	private final QueueItemType[] buildQueueTypes = new QueueItemType[WarsmashConstants.BUILD_QUEUE_SIZE];
	private AbilityTarget rallyPoint;

	private int foodMade;
	private int foodUsed;

	public CUnit(final int handleId, final int playerIndex, final float x, final float y, final float life,
			final War3ID typeId, final float facing, final float mana, final int maximumLife, final int maximumMana,
			final int speed, final int defense, final CUnitType unitType,
			final RemovablePathingMapInstance pathingInstance, final BuildingShadow buildingShadowInstance) {
		super(handleId, x, y, life);
		this.playerIndex = playerIndex;
		this.typeId = typeId;
		this.facing = facing;
		this.mana = mana;
		this.maximumLife = maximumLife;
		this.maximumMana = maximumMana;
		this.speed = speed;
		this.defense = defense;
		this.pathingInstance = pathingInstance;
		this.buildingShadowInstance = buildingShadowInstance;
		this.flyHeight = unitType.getDefaultFlyingHeight();
		this.unitType = unitType;
		this.classifications.addAll(unitType.getClassifications());
		this.acquisitionRange = unitType.getDefaultAcquisitionRange();
		this.stopBehavior = new CBehaviorStop(this);
		this.currentBehavior = this.stopBehavior;
	}

	public void setUnitAnimationListener(final CUnitAnimationListener unitAnimationListener) {
		this.unitAnimationListener = unitAnimationListener;
		this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
	}

	public CUnitAnimationListener getUnitAnimationListener() {
		return this.unitAnimationListener;
	}

	public void add(final CSimulation simulation, final CAbility ability) {
		this.abilities.add(ability);
		simulation.onAbilityAddedToUnit(this, ability);
		ability.onAdd(simulation, this);
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

	public int getMaximumLife() {
		return this.maximumLife;
	}

	public int getMaximumMana() {
		return this.maximumMana;
	}

	public void setTypeId(final War3ID typeId) {
		this.typeId = typeId;
	}

	public void setFacing(final float facing) {
		// java modulo output can be negative, but not if we
		// force positive and modulo again
		this.facing = ((facing % 360) + 360) % 360;
	}

	public void setMana(final float mana) {
		this.mana = mana;
	}

	public void setMaximumLife(final int maximumLife) {
		this.maximumLife = maximumLife;
	}

	public void setMaximumMana(final int maximumMana) {
		this.maximumMana = maximumMana;
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return this.speed;
	}

	/**
	 * Updates one tick of simulation logic and return true if it's time to remove
	 * this unit from the game.
	 */
	public boolean update(final CSimulation game) {
		if (isDead()) {
			if (this.collisionRectangle != null) {
				// Moved this here because doing it on "kill" was able to happen in some cases
				// while also iterating over the units that are in the collision system, and
				// then it hit the "writing while iterating" problem.
				game.getWorldCollision().removeUnit(this);
			}
			final int gameTurnTick = game.getGameTurnTick();
			if (!this.corpse) {
				if (gameTurnTick > (this.deathTurnTick
						+ (int) (this.unitType.getDeathTime() / WarsmashConstants.SIMULATION_STEP_TIME))) {
					this.corpse = true;
					if (!this.unitType.isRaise()) {
						this.boneCorpse = true;
						// start final phase immediately for "cant raise" case
					}
					if (!this.unitType.isDecay()) {
						// if we dont raise AND dont decay, then now that death anim is over
						// we just delete the unit
						return true;
					}
					this.deathTurnTick = gameTurnTick;
				}
			}
			else if (!this.boneCorpse) {
				if (game.getGameTurnTick() > (this.deathTurnTick + (int) (game.getGameplayConstants().getDecayTime()
						/ WarsmashConstants.SIMULATION_STEP_TIME))) {
					this.boneCorpse = true;
					this.deathTurnTick = gameTurnTick;
				}
			}
			else if (game.getGameTurnTick() > (this.deathTurnTick
					+ (int) (getEndingDecayTime(game) / WarsmashConstants.SIMULATION_STEP_TIME))) {
				return true;
			}
		}
		else if (!this.paused) {
			if (this.constructing) {
				this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
				final int buildTime = this.unitType.getBuildTime();
				final float healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / buildTime)
						* (this.maximumLife * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
				setLife(game, Math.min(this.life + healthGain, this.maximumLife));
				if (this.constructionProgress >= buildTime) {
					this.constructing = false;
					this.constructionProgress = 0;
					popoutWorker(game);
					final Iterator<CAbility> abilityIterator = this.abilities.iterator();
					while (abilityIterator.hasNext()) {
						final CAbility ability = abilityIterator.next();
						if (ability instanceof CAbilityBuildInProgress) {
							abilityIterator.remove();
						}
						else {
							ability.setDisabled(false);
							ability.setIconShowing(true);
						}
					}
					if (this.unitType.getFoodMade() != 0) {
						final CPlayer player = game.getPlayer(this.playerIndex);
						player.setFoodCap(player.getFoodCap() + this.unitType.getFoodMade());
					}
					game.unitConstructFinishEvent(this);
					this.stateNotifier.ordersChanged(getCurrentAbilityHandleId(), getCurrentOrderId());
				}
			}
			else {
				final War3ID queuedRawcode = this.buildQueue[0];
				if (queuedRawcode != null) {
					// queue step forward
					this.constructionProgress += WarsmashConstants.SIMULATION_STEP_TIME;
					if (this.buildQueueTypes[0] == QueueItemType.UNIT) {
						final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
						if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
							this.constructionProgress = 0;
							final CUnit trainedUnit = game.createUnit(queuedRawcode, this.playerIndex, getX(), getY(),
									game.getGameplayConstants().getBuildingAngle());
							// dont add food cost to player 2x
							trainedUnit.setFoodUsed(trainedUnitType.getFoodUsed());
							game.getPlayer(this.playerIndex).setUnitFoodMade(trainedUnit,
									trainedUnitType.getFoodMade());
							// nudge the trained unit out around us
							trainedUnit.nudgeAround(game, this);
							game.unitTrainedEvent(this, trainedUnit);
							if (this.rallyPoint != null) {
								final int rallyOrderId = OrderIds.smart;
								this.rallyPoint.visit(
										UseAbilityOnTargetByIdVisitor.INSTANCE.reset(game, trainedUnit, rallyOrderId));
							}
							for (int i = 0; i < (this.buildQueue.length - 1); i++) {
								setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
							}
							setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
							this.stateNotifier.queueChanged();
						}
					}
					else if (this.buildQueueTypes[0] == QueueItemType.RESEARCH) {
						final CUnitType trainedUnitType = game.getUnitData().getUnitType(queuedRawcode);
						if (this.constructionProgress >= trainedUnitType.getBuildTime()) {
							this.constructionProgress = 0;
							for (int i = 0; i < (this.buildQueue.length - 1); i++) {
								setBuildQueueItem(game, i, this.buildQueue[i + 1], this.buildQueueTypes[i + 1]);
							}
							setBuildQueueItem(game, this.buildQueue.length - 1, null, null);
							this.stateNotifier.queueChanged();
						}
					}
				}
				if (this.currentBehavior != null) {
					final CBehavior lastBehavior = this.currentBehavior;
					this.currentBehavior = this.currentBehavior.update(game);
					if (this.currentBehavior.getHighlightOrderId() != lastBehavior.getHighlightOrderId()) {
						this.stateNotifier.ordersChanged(getCurrentAbilityHandleId(), getCurrentOrderId());
					}
				}
				else {
					// check to auto acquire targets
					autoAcquireAttackTargets(game);
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
			this.workerInside = null;
		}
	}

	public void autoAcquireAttackTargets(final CSimulation game) {
		if (!this.unitType.getAttacks().isEmpty()) {
			if (this.collisionRectangle != null) {
				tempRect.set(this.collisionRectangle);
			}
			else {
				tempRect.set(this.getX(), this.getY(), 0, 0);
			}
			final float halfSize = this.acquisitionRange;
			tempRect.x -= halfSize;
			tempRect.y -= halfSize;
			tempRect.width += halfSize * 2;
			tempRect.height += halfSize * 2;
			game.getWorldCollision().enumUnitsInRect(tempRect, autoAttackTargetFinderEnum.reset(game, this));
		}
	}

	public float getEndingDecayTime(final CSimulation game) {
		if (this.unitType.isBuilding()) {
			return game.getGameplayConstants().getStructureDecayTime();
		}
		return game.getGameplayConstants().getBoneDecayTime();
	}

	public void order(final CSimulation game, final COrder order, final boolean queue) {
		if (isDead()) {
			return;
		}

		final CAbility ability = game.getAbility(order.getAbilityHandleId());
		if (ability != null) {
			// Allow the ability to response to the order without actually placing itself in
			// the queue, nor modifying (interrupting) the queue.
			if (!ability.checkBeforeQueue(game, this, order.getOrderId())) {
				this.stateNotifier.ordersChanged(getCurrentAbilityHandleId(), getCurrentOrderId());
				return;
			}
		}

		if ((queue || !this.acceptingOrders) && (this.currentOrder != null)) {
			this.orderQueue.add(order);
		}
		else {
			this.currentBehavior = beginOrder(game, order);
			this.orderQueue.clear();
			final boolean omitNotify = (this.currentOrder == null) && (order == null);
			if (!omitNotify) {
				this.stateNotifier.ordersChanged(getCurrentAbilityHandleId(), getCurrentOrderId());
			}
		}
	}

	private CBehavior beginOrder(final CSimulation game, final COrder order) {
		this.currentOrder = order;
		CBehavior nextBehavior;
		if (order != null) {
			nextBehavior = order.begin(game, this);
		}
		else {
			nextBehavior = this.stopBehavior;
		}
		return nextBehavior;
	}

	public CBehavior getCurrentBehavior() {
		return this.currentBehavior;
	}

	public int getCurrentAbilityHandleId() {
		return this.currentOrder == null ? 0 : this.currentOrder.getAbilityHandleId();
	}

	public int getCurrentOrderId() {
		return this.currentOrder == null ? OrderIds.stop : this.currentOrder.getOrderId();
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

	public void setPlayerIndex(final int playerIndex) {
		this.playerIndex = playerIndex;
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

	public void setX(final float newX, final CWorldCollision collision) {
		final float prevX = getX();
		if (!this.unitType.isBuilding()) {
			setX(newX);
			collision.translate(this, newX - prevX, 0);
		}
	}

	public void setY(final float newY, final CWorldCollision collision) {
		final float prevY = getY();
		if (!this.unitType.isBuilding()) {
			setY(newY);
			collision.translate(this, 0, newY - prevY);
		}
	}

	public void setPointAndCheckUnstuck(final float newX, final float newY, final CSimulation game) {
		final CWorldCollision collision = game.getWorldCollision();
		final PathingGrid pathingGrid = game.getPathingGrid();
		;
		float outputX = newX, outputY = newY;
		int checkX = 0;
		int checkY = 0;
		float collisionSize;
		if (this.unitType.getBuildingPathingPixelMap() != null) {
			tempRect.setSize(this.unitType.getBuildingPathingPixelMap().getWidth() * 32,
					this.unitType.getBuildingPathingPixelMap().getHeight() * 32);
			collisionSize = tempRect.getWidth() / 2;
		}
		else if (this.collisionRectangle != null) {
			tempRect.set(this.collisionRectangle);
			collisionSize = this.unitType.getCollisionSize();
		}
		else {
			tempRect.setSize(16, 16);
			collisionSize = this.unitType.getCollisionSize();
		}
		final boolean repos = false;
		for (int i = 0; i < 300; i++) {
			final float centerX = newX + (checkX * 64);
			final float centerY = newY + (checkY * 64);
			tempRect.setCenter(centerX, centerY);
			if (!collision.intersectsAnythingOtherThan(tempRect, this, this.unitType.getMovementType())
					&& pathingGrid.isPathable(centerX, centerY, this.unitType.getMovementType(), collisionSize)) {
				outputX = centerX;
				outputY = centerY;
				break;
			}
			final double angle = ((((int) Math.floor(Math.sqrt((4 * i) + 1))) % 4) * Math.PI) / 2;
			checkX -= (int) Math.cos(angle);
			checkY -= (int) Math.sin(angle);
		}
		setPoint(outputX, outputY, collision);
		game.unitRepositioned(this);
	}

	public void setPoint(final float newX, final float newY, final CWorldCollision collision) {
		final float prevX = getX();
		final float prevY = getY();
		setX(newX);
		setY(newY);
		if (!this.unitType.isBuilding()) {
			collision.translate(this, newX - prevX, newY - prevY);
		}
	}

	public EnumSet<CUnitClassification> getClassifications() {
		return this.classifications;
	}

	public int getDefense() {
		return this.defense;
	}

	@Override
	public float getImpactZ() {
		return this.unitType.getImpactZ();
	}

	public double distance(final AbilityTarget target) {
		double dx = Math.abs(target.getX() - getX());
		double dy = Math.abs(target.getY() - getY());
		final float thisCollisionSize = this.unitType.getCollisionSize();
		float targetCollisionSize;
		if (target instanceof CUnit) {
			final CUnitType targetUnitType = ((CUnit) target).getUnitType();
			targetCollisionSize = targetUnitType.getCollisionSize();
		}
		else {
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

	@Override
	public void damage(final CSimulation simulation, final CUnit source, final CAttackType attackType,
			final String weaponType, final float damage) {
		final boolean wasDead = isDead();
		if (!this.invulnerable) {
			final float damageRatioFromArmorClass = simulation.getGameplayConstants().getDamageRatioAgainst(attackType,
					this.unitType.getDefenseType());
			final float damageRatioFromDefense;
			if (this.defense >= 0) {
				damageRatioFromDefense = 1f - (float) (((this.defense) * 0.06) / (1 + (0.06 * this.defense)));
			}
			else {
				damageRatioFromDefense = 2f - (float) StrictMath.pow(0.94, -this.defense);
			}
			final float trueDamage = damageRatioFromArmorClass * damageRatioFromDefense * damage;
			this.life -= trueDamage;
			this.stateNotifier.lifeChanged();
		}
		simulation.unitDamageEvent(this, weaponType, this.unitType.getArmorType());
		if (!this.invulnerable && isDead()) {
			if (!wasDead) {
				kill(simulation);
			}
		}
		else {
			if (this.currentBehavior == null) {
				if (!simulation.getPlayer(getPlayerIndex()).hasAlliance(source.getPlayerIndex(),
						CAllianceType.PASSIVE)) {
					for (final CUnitAttack attack : this.unitType.getAttacks()) {
						if (source.canBeTargetedBy(simulation, this, attack.getTargetsAllowed())) {
							this.currentBehavior = getAttackBehavior().reset(OrderIds.attack, attack, source);
							break;
						}
					}
				}
			}
		}
	}

	private void kill(final CSimulation simulation) {
		this.currentBehavior = null;
		this.orderQueue.clear();
		if (this.constructing) {
			simulation.createBuildingDeathEffect(this);
		}
		else {
			this.deathTurnTick = simulation.getGameTurnTick();
		}
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
			this.pathingInstance = null;
		}
		if (this.buildingShadowInstance != null) {
			this.buildingShadowInstance.remove();
			this.buildingShadowInstance = null;
		}
		popoutWorker(simulation);
		final CPlayer player = simulation.getPlayer(this.playerIndex);
		if (this.foodMade != 0) {
			player.setUnitFoodMade(this, 0);
		}
		if (this.foodUsed != 0) {
			player.setUnitFoodUsed(this, 0);
		}
	}

	public boolean canReach(final AbilityTarget target, final float range) {
		final double distance = distance(target);
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			final CUnitType targetUnitType = targetUnit.getUnitType();
			if (targetUnitType.isBuilding() && (targetUnitType.getBuildingPathingPixelMap() != null)) {
				final BufferedImage buildingPathingPixelMap = targetUnitType.getBuildingPathingPixelMap();
				final float targetX = target.getX();
				final float targetY = target.getY();
				if (canReachToPathing(range, targetUnit.getFacing(), buildingPathingPixelMap, targetX, targetY)) {
					return true;
				}
			}
		}
		return distance <= range;
	}

	public boolean canReach(final float x, final float y, final float range) {
		return distance(x, y) <= range; // TODO use dist squared for performance
	}

	public boolean canReachToPathing(final float range, final float rotationForPathing,
			final BufferedImage buildingPathingPixelMap, final float targetX, final float targetY) {
		final int rotation = ((int) rotationForPathing + 450) % 360;
		final float relativeOffsetX = getX() - targetX;
		final float relativeOffsetY = getY() - targetY;
		final int gridWidth = ((rotation % 180) != 0) ? buildingPathingPixelMap.getHeight()
				: buildingPathingPixelMap.getWidth();
		final int gridHeight = ((rotation % 180) != 0) ? buildingPathingPixelMap.getWidth()
				: buildingPathingPixelMap.getHeight();
		final int relativeGridX = (int) Math.floor(relativeOffsetX / 32f) + (gridWidth / 2);
		final int relativeGridY = (int) Math.floor(relativeOffsetY / 32f) + (gridHeight / 2);
		final int rangeInCells = (int) Math.floor(range / 32f);
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
		this.stateNotifier.subscribe(listener);
	}

	public void removeStateListener(final CUnitStateListener listener) {
		this.stateNotifier.unsubscribe(listener);
	}

	public boolean isCorpse() {
		return this.corpse;
	}

	public boolean isBoneCorpse() {
		return this.boneCorpse;
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed) {
		if (targetsAllowed.containsAll(this.unitType.getTargetedAs())) {
			final int sourcePlayerIndex = source.getPlayerIndex();
			final CPlayer sourcePlayer = simulation.getPlayer(sourcePlayerIndex);
			if (!targetsAllowed.contains(CTargetType.ENEMIES)
					|| !sourcePlayer.hasAlliance(this.playerIndex, CAllianceType.PASSIVE)) {
				if (isDead()) {
					if (this.unitType.isRaise() && this.unitType.isDecay() && isBoneCorpse()) {
						return targetsAllowed.contains(CTargetType.DEAD);
					}
				}
				else {
					return !targetsAllowed.contains(CTargetType.DEAD) || targetsAllowed.contains(CTargetType.ALIVE);
				}
			}
		}
		else {
			System.err.println("No targeting because " + targetsAllowed + " does not contain all of "
					+ this.unitType.getTargetedAs());
		}
		return false;
	}

	public boolean isMovementDisabled() {
		return this.unitType.isBuilding();
	}

	public float getAcquisitionRange() {
		return this.acquisitionRange;
	}

	private static final class AutoAttackTargetFinderEnum implements CUnitEnumFunction {
		private CSimulation game;
		private CUnit source;

		private AutoAttackTargetFinderEnum reset(final CSimulation game, final CUnit source) {
			this.game = game;
			this.source = source;
			return this;
		}

		@Override
		public boolean call(final CUnit unit) {
			if (!this.game.getPlayer(this.source.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(),
					CAllianceType.PASSIVE)) {
				for (final CUnitAttack attack : this.source.unitType.getAttacks()) {
					if (this.source.canReach(unit, this.source.acquisitionRange)
							&& unit.canBeTargetedBy(this.game, this.source, attack.getTargetsAllowed())
							&& (this.source.distance(unit) >= this.source.getUnitType().getMinimumAttackRange())) {
						this.source.currentBehavior = this.source.getAttackBehavior().reset(OrderIds.attack, attack,
								unit);
						return true;
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

	public CBehaviorStop getStopBehavior() {
		return this.stopBehavior;
	}

	public void setFollowBehavior(final CBehaviorFollow followBehavior) {
		this.followBehavior = followBehavior;
	}

	public void setPatrolBehavior(final CBehaviorPatrol patrolBehavior) {
		this.patrolBehavior = patrolBehavior;
	}

	public CBehaviorFollow getFollowBehavior() {
		return this.followBehavior;
	}

	public CBehaviorPatrol getPatrolBehavior() {
		return this.patrolBehavior;
	}

	public CBehavior pollNextOrderBehavior(final CSimulation game) {
		final COrder order = this.orderQueue.poll();
		return beginOrder(game, order);
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

	public void setConstructionProgress(final float constructionProgress) {
		this.constructionProgress = constructionProgress;
	}

	public boolean isConstructing() {
		return this.constructing;
	}

	public float getConstructionProgress() {
		return this.constructionProgress;
	}

	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
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
			kill(simulation);
		}
		this.stateNotifier.lifeChanged();
	}

	private boolean queue(final CSimulation game, final War3ID rawcode, final QueueItemType queueItemType) {
		for (int i = 0; i < this.buildQueue.length; i++) {
			if (this.buildQueue[i] == null) {
				setBuildQueueItem(game, i, rawcode, queueItemType);
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

	public float getBuildQueueTimeRemaining(final CSimulation simulation) {
		if (this.buildQueueTypes[0] == null) {
			return 0;
		}
		switch (this.buildQueueTypes[0]) {
		case RESEARCH:
			return 999; // TODO
		case UNIT:
			final CUnitType trainedUnitType = simulation.getUnitData().getUnitType(this.buildQueue[0]);
			return trainedUnitType.getBuildTime();
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
					case RESEARCH:
						break;
					case UNIT:
						final CPlayer player = game.getPlayer(this.playerIndex);
						final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
						player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
						break;
					}
				}
				switch (cancelledType) {
				case RESEARCH:
					break;
				case UNIT:
					final CPlayer player = game.getPlayer(this.playerIndex);
					final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[cancelIndex]);
					player.refundFor(unitType);
					break;
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
		if ((index == 0) && (rawcode != null) && (queueItemType == QueueItemType.UNIT)) {
			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUnitType unitType = game.getUnitData().getUnitType(this.buildQueue[index]);
			if (unitType.getFoodUsed() != 0) {
				player.setFoodUsed(player.getFoodUsed() + unitType.getFoodUsed());
			}
		}
	}

	public void queueTrainingUnit(final CSimulation game, final War3ID rawcode) {
		if (queue(game, rawcode, QueueItemType.UNIT)) {
			final CPlayer player = game.getPlayer(this.playerIndex);
			final CUnitType unitType = game.getUnitData().getUnitType(rawcode);
			player.chargeFor(unitType);
		}
	}

	public void queueResearch(final CSimulation game, final War3ID rawcode) {
		queue(game, rawcode, QueueItemType.RESEARCH);
	}

	public static enum QueueItemType {
		UNIT,
		RESEARCH;
	}

	public void setRallyPoint(final AbilityTarget target) {
		this.rallyPoint = target;
		this.stateNotifier.rallyPointChanged();
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
			for (final CAbility ability : this.trainedUnit.getAbilities()) {
				ability.checkCanUse(this.game, this.trainedUnit, this.rallyOrderId,
						BooleanAbilityActivationReceiver.INSTANCE);
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<AbilityPointTarget>getInstance().reset();
					ability.checkCanTarget(this.game, this.trainedUnit, this.rallyOrderId, target, targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						this.trainedUnit.order(this.game,
								new COrderTargetPoint(ability.getHandleId(), this.rallyOrderId, target), false);
						return null;
					}
				}
			}
			return null;
		}

		@Override
		public Void accept(final CUnit targetUnit) {
			return acceptWidget(this.game, this.trainedUnit, this.rallyOrderId, targetUnit);
		}

		private Void acceptWidget(final CSimulation game, final CUnit trainedUnit, final int rallyOrderId,
				final CWidget target) {
			for (final CAbility ability : trainedUnit.getAbilities()) {
				ability.checkCanUse(game, trainedUnit, rallyOrderId, BooleanAbilityActivationReceiver.INSTANCE);
				if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
					final BooleanAbilityTargetCheckReceiver<CWidget> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
							.<CWidget>getInstance().reset();
					ability.checkCanTarget(game, trainedUnit, rallyOrderId, target, targetCheckReceiver);
					if (targetCheckReceiver.isTargetable()) {
						trainedUnit.order(game,
								new COrderTargetWidget(ability.getHandleId(), rallyOrderId, target.getHandleId()),
								false);
						return null;
					}
				}
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

	public int getGold() {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMine) {
				return ((CAbilityGoldMine) ability).getGold();
			}
		}
		return 0;
	}

	public void setGold(final int goldAmount) {
		for (final CAbility ability : this.abilities) {
			if (ability instanceof CAbilityGoldMine) {
				((CAbilityGoldMine) ability).setGold(goldAmount);
			}
		}
	}
}
