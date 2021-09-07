package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget.UnitAnimationListenerImpl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;

public class CDestructable extends CWidget {

	private final CDestructableType destType;
	private final RemovablePathingMapInstance pathingInstance;
	private final RemovablePathingMapInstance pathingInstanceDeath;
	private UnitAnimationListenerImpl unitAnimationListenerImpl;
	private boolean invulnerable;
	private boolean blighted;
	private Rectangle registeredEnumRectangle;

	public CDestructable(final int handleId, final float x, final float y, final float life,
			final CDestructableType destTypeInstance, final RemovablePathingMapInstance pathingInstance,
			final RemovablePathingMapInstance pathingInstanceDeath) {
		super(handleId, x, y, life);
		this.destType = destTypeInstance;
		this.pathingInstance = pathingInstance;
		this.pathingInstanceDeath = pathingInstanceDeath;
	}

	@Override
	public float getFlyHeight() {
		return 0;
	}

	@Override
	public float getImpactZ() {
		return 0; // TODO maybe from DestructableType
	}

	public Rectangle getOrCreateRegisteredEnumRectangle() {
		if (this.registeredEnumRectangle == null) {
			BufferedImage pathingPixelMap = this.destType.getPathingPixelMap();
			BufferedImage pathingDeathPixelMap = this.destType.getPathingDeathPixelMap();
			if (pathingPixelMap == null) {
				pathingPixelMap = PathingGrid.BLANK_PATHING;
			}
			if (pathingDeathPixelMap == null) {
				pathingDeathPixelMap = PathingGrid.BLANK_PATHING;
			}
			final float width = Math.max(pathingPixelMap.getWidth() * 16, pathingDeathPixelMap.getWidth() * 16);
			final float height = Math.max(pathingPixelMap.getHeight() * 16, pathingDeathPixelMap.getHeight() * 16);
			this.registeredEnumRectangle = new Rectangle(getX() - (width / 2), getY() - (height / 2), width, height);
		}
		return this.registeredEnumRectangle;
	}

	@Override
	public void damage(final CSimulation simulation, final CUnit source, final CAttackType attackType,
			final String weaponType, final float damage) {
		if (isInvulnerable()) {
			return;
		}
		final boolean wasDead = isDead();
		this.life -= damage;
		simulation.destructableDamageEvent(this, weaponType, this.destType.getArmorType());
		if (!wasDead && isDead()) {
			kill(simulation);
		}
	}

	private void kill(final CSimulation simulation) {
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
		}
		if (this.pathingInstanceDeath != null) {
			this.pathingInstanceDeath.add();
		}
		fireDeathEvents(simulation);
	}

	@Override
	public void setLife(final CSimulation simulation, final float life) {
		final boolean wasDead = isDead();
		super.setLife(simulation, life);
		if (isDead() && !wasDead) {
			kill(simulation);
		}
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed) {
		if (targetsAllowed.containsAll(this.destType.getTargetedAs())) {
			if (isDead()) {
				return targetsAllowed.contains(CTargetType.DEAD);
			}
			else {
				return !targetsAllowed.contains(CTargetType.DEAD) || targetsAllowed.contains(CTargetType.ALIVE);
			}
		}
		else {
			System.err.println("Not targeting because " + targetsAllowed + " does not contain all of "
					+ this.destType.getTargetedAs());
		}
		return false;
	}

	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	public CDestructableType getDestType() {
		return this.destType;
	}

	public void setUnitAnimationListener(final UnitAnimationListenerImpl unitAnimationListenerImpl) {
		this.unitAnimationListenerImpl = unitAnimationListenerImpl;
	}

	@Override
	public float getMaxLife() {
		return this.destType.getMaxLife();
	}

	public void setInvulnerable(final boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	@Override
	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setBlighted(final boolean blighted) {
		this.blighted = blighted;
	}

	public boolean isBlighted() {
		return this.blighted;
	}

	public boolean checkIsOnBlight(final CSimulation game) {
		return !game.getPathingGrid().checkPathingTexture(getX(), getY(), 0, this.destType.getPathingPixelMap(),
				EnumSet.of(CBuildingPathingType.BLIGHTED), EnumSet.noneOf(CBuildingPathingType.class),
				game.getWorldCollision(), null);
	}

	public double distance(final float x, final float y) {
		return StrictMath.sqrt(distanceSquaredNoCollision(x, y));
	}
}
