package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener.CUnitStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CBehaviorAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

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

	private CBehavior currentOrder;
	private final Queue<CBehavior> orderQueue = new LinkedList<>();
	private final CUnitType unitType;

	private Rectangle collisionRectangle;

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

	public CUnit(final int handleId, final int playerIndex, final float x, final float y, final float life,
			final War3ID typeId, final float facing, final float mana, final int maximumLife, final int maximumMana,
			final int speed, final int defense, final CUnitType unitType) {
		super(handleId, x, y, life);
		this.playerIndex = playerIndex;
		this.typeId = typeId;
		this.facing = facing;
		this.mana = mana;
		this.maximumLife = maximumLife;
		this.maximumMana = maximumMana;
		this.speed = speed;
		this.defense = defense;
		this.flyHeight = unitType.getDefaultFlyingHeight();
		this.unitType = unitType;
		this.classifications.addAll(unitType.getClassifications());
		this.acquisitionRange = unitType.getDefaultAcquisitionRange();
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
		else if (this.currentOrder != null) {
			if (this.currentOrder.update(game)) {
				// remove current order, because it's completed, polling next
				// item from order queue
				this.currentOrder = this.orderQueue.poll();
				this.stateNotifier.ordersChanged();
			}
			if (this.currentOrder == null) {
				// maybe order "stop" here
				this.unitAnimationListener.playAnimation(true, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
			}
		}
		else {
			// check to auto acquire targets
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
		return false;
	}

	public float getEndingDecayTime(final CSimulation game) {
		if (this.unitType.isBuilding()) {
			return game.getGameplayConstants().getStructureDecayTime();
		}
		return game.getGameplayConstants().getBoneDecayTime();
	}

	public void order(final CBehavior order, final boolean queue) {
		if (isDead()) {
			return;
		}
		if (queue && (this.currentOrder != null)) {
			this.orderQueue.add(order);
		}
		else {
			this.currentOrder = order;
			this.orderQueue.clear();
		}
		this.stateNotifier.ordersChanged();
	}

	public CBehavior getCurrentOrder() {
		return this.currentOrder;
	}

	public List<CAbility> getAbilities() {
		return this.abilities;
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

	public double distance(final CWidget target) {
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
		simulation.unitDamageEvent(this, weaponType, this.unitType.getArmorType());
		this.stateNotifier.lifeChanged();
		if (isDead()) {
			if (!wasDead && !this.unitType.isBuilding()) {
				kill(simulation);
			}
		}
		else {
			if (this.currentOrder == null) {
				if (!simulation.getPlayer(getPlayerIndex()).hasAlliance(source.getPlayerIndex(),
						CAllianceType.PASSIVE)) {
					for (final CUnitAttack attack : this.unitType.getAttacks()) {
						if (source.canBeTargetedBy(simulation, this, attack.getTargetsAllowed())) {
							this.order(new CBehaviorAttack(this, attack, OrderIds.attack, source), false);
							break;
						}
					}
				}
			}
		}
	}

	private void kill(final CSimulation simulation) {
		this.currentOrder = null;
		this.orderQueue.clear();
		this.deathTurnTick = simulation.getGameTurnTick();
	}

	public boolean canReach(final CWidget target, final float range) {
		final double distance = distance(target);
		if (target instanceof CUnit) {
			final CUnit targetUnit = (CUnit) target;
			final CUnitType targetUnitType = targetUnit.getUnitType();
			if (targetUnitType.isBuilding() && (targetUnitType.getBuildingPathingPixelMap() != null)) {
				final float relativeOffsetX = getX() - target.getX();
				final float relativeOffsetY = getY() - target.getY();
				final int rotation = ((int) targetUnit.getFacing() + 450) % 360;
				final BufferedImage buildingPathingPixelMap = targetUnitType.getBuildingPathingPixelMap();
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
			}
		}
		return distance <= range;
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
						this.source.order(new CBehaviorAttack(this.source, attack, OrderIds.attack, unit), false);
						return true;
					}
				}
			}
			return false;
		}
	}
}
