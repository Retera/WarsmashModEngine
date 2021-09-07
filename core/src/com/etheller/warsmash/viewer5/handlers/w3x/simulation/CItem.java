package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CItem extends CWidget {
	private final War3ID typeId;
	private final CItemType itemType;
	private boolean hidden;
	private boolean invulnerable;

	public CItem(final int handleId, final float x, final float y, final float life, final War3ID typeId,
			final CItemType itemTypeInstance) {
		super(handleId, x, y, life);
		this.typeId = typeId;
		this.itemType = itemTypeInstance;
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
	public void damage(final CSimulation simulation, final CUnit source, final CAttackType attackType,
			final String weaponType, final float damage) {
		if (this.invulnerable) {
			return;
		}
		final boolean wasDead = isDead();
		this.life -= damage;
		simulation.itemDamageEvent(this, weaponType, this.itemType.getArmorType());
		if (isDead() && !wasDead) {
			fireDeathEvents(simulation);
		}
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed) {
		return targetsAllowed.contains(CTargetType.ITEM);
	}

	public void setX(final float x, final CWorldCollision collision) {
		super.setX(x);
	}

	public void setY(final float y, final CWorldCollision collision) {
		super.setY(y);
	}

	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
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
		final CWorldCollision collision = game.getWorldCollision();
		final PathingGrid pathingGrid = game.getPathingGrid();
		;
		float outputX = newX, outputY = newY;
		int checkX = 0;
		int checkY = 0;
		float collisionSize;
		tempRect.setSize(16, 16);
		collisionSize = 16;
		for (int i = 0; i < 300; i++) {
			final float centerX = newX + (checkX * 64);
			final float centerY = newY + (checkY * 64);
			tempRect.setCenter(centerX, centerY);
			if (pathingGrid.isPathable(centerX, centerY, MovementType.FOOT, collisionSize)) {
				outputX = centerX;
				outputY = centerY;
				break;
			}
			final double angle = ((((int) Math.floor(Math.sqrt((4 * i) + 1))) % 4) * Math.PI) / 2;
			checkX -= (int) Math.cos(angle);
			checkY -= (int) Math.sin(angle);
		}
		setX(outputX);
		setY(outputY);
	}

	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulernable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

}
