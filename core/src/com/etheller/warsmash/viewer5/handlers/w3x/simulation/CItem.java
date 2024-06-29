package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CItem extends CWidget {
	private final static int COLLISION_SIZE = 16;
	private final War3ID typeId;
	private final CItemType itemType;
	private boolean hidden;
	private boolean invulnerable;
	private int charges;
	private CAbilityInventory containedInventory;
	private CUnit containedUnit;
	private Rectangle registeredEnumRectangle;
	private boolean dropOnDeath;
	private boolean droppable;
	private boolean pawnable;
	private int userData;
	private War3ID dropId;

	public CItem(final int handleId, final float x, final float y, final float life, final War3ID typeId,
			final CItemType itemTypeInstance) {
		super(handleId, x, y, life);
		this.typeId = typeId;
		this.itemType = itemTypeInstance;
		this.charges = itemTypeInstance.getNumberOfCharges();
		this.dropOnDeath = itemTypeInstance.isDroppedWhenCarrierDies();
		this.droppable = itemTypeInstance.isCanBeDropped();
		this.pawnable = itemTypeInstance.isPawnable();
	}

	@Override
	public float getFlyHeight() {
		return 0;
	}

	@Override
	public float getImpactZ() {
		return 0; // TODO probably from ItemType
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage) {
		if (this.invulnerable) {
			return 0;
		}
		final boolean wasDead = isDead();
		this.life -= damage;
		simulation.itemDamageEvent(this, weaponSoundType, this.itemType.getArmorType());
		if (isDead() && !wasDead) {
			fireDeathEvents(simulation);
			forceDropIfHeld(simulation);
			simulation.getWorldCollision().removeItem(this);
		}
		return damage;
	}

	@Override
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage, final float bonusDamage) {
		return this.damage(simulation, source, isAttack, isRanged, attackType, damageType, weaponSoundType,
				damage + bonusDamage);
	}

	public void forceDropIfHeld(final CSimulation simulation) {
		if ((this.containedInventory != null) && (this.containedUnit != null)) {
			this.containedInventory.dropItem(simulation, this.containedUnit, this, this.containedUnit.getX(),
					this.containedUnit.getY(), false);
		}
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (targetsAllowed.contains(CTargetType.ITEM)) {
			return true;
		}
		receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_ITEMS);
		return false;
	}

	public void setX(final float x, final CWorldCollision collision) {
		final float oldX = getX();
		super.setX(x);
		collision.translate(this, x - oldX, 0);
	}

	public void setY(final float y, final CWorldCollision collision) {
		final float oldY = getY();
		super.setY(y);
		collision.translate(this, 0, y - oldY);
	}

	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public <T> T visit(final CWidgetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	public War3ID getTypeId() {
		return this.typeId;
	}

	public CItemType getItemType() {
		return this.itemType;
	}

	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	@Override
	public float getMaxLife() {
		return this.itemType.getMaxLife();
	}

	public void setPointAndCheckUnstuck(final float newX, final float newY, final CSimulation game) {
		final PathingGrid pathingGrid = game.getPathingGrid();
		;
		float outputX = newX, outputY = newY;
		int checkX = 0;
		int checkY = 0;
		tempRect.setSize(16, 16);
		for (int i = 0; i < 300; i++) {
			final float centerX = newX + (checkX * 64);
			final float centerY = newY + (checkY * 64);
			tempRect.setCenter(centerX, centerY);
			if (pathingGrid.isPathable(centerX, centerY, MovementType.FOOT, COLLISION_SIZE)) {
				outputX = centerX;
				outputY = centerY;
				break;
			}
			final double angle = ((((int) Math.floor(Math.sqrt((4 * i) + 1))) % 4) * Math.PI) / 2;
			checkX -= (int) Math.cos(angle);
			checkY -= (int) Math.sin(angle);
		}
		final float oldX = getX();
		final float oldY = getY();
		setX(outputX);
		setY(outputY);
		game.getWorldCollision().translate(this, outputX - oldX, outputY - oldY);
	}

	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulernable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public int getCharges() {
		return this.charges;
	}

	public void setCharges(final int charges) {
		this.charges = charges;
	}

	public void setContainedInventory(final CAbilityInventory containedInventory, final CUnit containedUnit) {
		this.containedInventory = containedInventory;
		this.containedUnit = containedUnit;
	}

	public CAbilityInventory getContainedInventory() {
		return this.containedInventory;
	}

	public CUnit getContainedUnit() {
		return this.containedUnit;
	}

	@Override
	public double distance(final float x, final float y) {
		return StrictMath.sqrt(distanceSquaredNoCollision(x, y));
	}

	public Rectangle getOrCreateRegisteredEnumRectangle() {
		if (this.registeredEnumRectangle == null) {
			this.registeredEnumRectangle = new Rectangle(getX() - COLLISION_SIZE, getY() - COLLISION_SIZE,
					COLLISION_SIZE * 2, COLLISION_SIZE * 2);
		}
		return this.registeredEnumRectangle;
	}

	public boolean isDropOnDeath() {
		return this.dropOnDeath;
	}

	public void setDropOnDeath(final boolean dropOnDeath) {
		this.dropOnDeath = dropOnDeath;
	}

	public boolean isDroppable() {
		return this.droppable;
	}

	public void setDroppable(final boolean droppable) {
		this.droppable = droppable;
	}

	public boolean isPawnable() {
		return this.pawnable;
	}

	public void setPawnable(final boolean pawnable) {
		this.pawnable = pawnable;
	}

	public int getUserData() {
		return this.userData;
	}

	public void setUserData(final int userData) {
		this.userData = userData;
	}

	public War3ID getDropId() {
		return this.dropId;
	}

	public void setDropId(final War3ID unitId) {
		this.dropId = unitId;
	}

}
