package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.RemovablePathingMapInstance;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderWidget.UnitAnimationListenerImpl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CDestructable extends CWidget {

	private final CDestructableType destType;
	private final RemovablePathingMapInstance pathingInstance;
	private final RemovablePathingMapInstance pathingInstanceDeath;
	private UnitAnimationListenerImpl unitAnimationListenerImpl;
	private boolean invulnerable;
	private boolean blighted;
	private Rectangle registeredEnumRectangle;

	private List<CDestructableBuff> buffs;

	public CDestructable(final int handleId, final float x, final float y, final float life,
			final CDestructableType destTypeInstance, final RemovablePathingMapInstance pathingInstance,
			final RemovablePathingMapInstance pathingInstanceDeath) {
		super(handleId, x, y, life);
		this.destType = destTypeInstance;
		this.pathingInstance = pathingInstance;
		this.pathingInstanceDeath = pathingInstanceDeath;
		if ((this.destType.getOcclusionHeight() > 0) && (pathingInstance != null)) {
			this.pathingInstance.setBlocksVision(true);
		}
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
	public float damage(final CSimulation simulation, final CUnit source, final boolean isAttack,
			final boolean isRanged, final CAttackType attackType, final CDamageType damageType,
			final String weaponSoundType, final float damage) {
		if (isInvulnerable()) {
			return 0;
		}
		final boolean wasDead = isDead();
		this.life -= damage;
		simulation.destructableDamageEvent(this, weaponSoundType, this.destType.getArmorType());
		if (!wasDead && isDead()) {
			kill(simulation);
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

	private void kill(final CSimulation simulation) {
		if (this.pathingInstance != null) {
			this.pathingInstance.setBlocksVision(false);
			this.pathingInstance.remove();
		}
		if (this.pathingInstanceDeath != null) {
			this.pathingInstanceDeath.add();
		}
		if (this.buffs != null) {
			for (int i = this.buffs.size() - 1; i >= 0; i--) {
				// okay if it removes self from this during onDeath() because of reverse
				// iteration order
				this.buffs.get(i).onDeath(simulation, this);
			}
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
		else if (!isDead() && wasDead) {
			if (this.pathingInstanceDeath != null) {
				this.pathingInstanceDeath.remove();
			}
			if (this.pathingInstance != null) {
				this.pathingInstance.add();
				if ((this.destType.getOcclusionHeight() > 0) && (this.pathingInstance != null)) {
					this.pathingInstance.setBlocksVision(true);
				}
			}
		}
	}

	@Override
	public boolean canBeTargetedBy(final CSimulation simulation, final CUnit source,
			final EnumSet<CTargetType> targetsAllowed, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (targetsAllowed.containsAll(this.destType.getTargetedAs())) {
			if (isDead()) {
				if (targetsAllowed.contains(CTargetType.DEAD)) {
					return true;
				}
				receiver.targetCheckFailed(CommandStringErrorKeys.TARGET_MUST_BE_LIVING);
			}
			else {
				if (!targetsAllowed.contains(CTargetType.DEAD) || targetsAllowed.contains(CTargetType.ALIVE)) {
					return true;
				}
				receiver.targetCheckFailed(CommandStringErrorKeys.SOMETHING_IS_BLOCKING_THAT_TREE_STUMP);
			}
		}
		else {
			if (this.destType.getTargetedAs().contains(CTargetType.TREE)
					&& !targetsAllowed.contains(CTargetType.TREE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_TREES);
			}
			else if (this.destType.getTargetedAs().contains(CTargetType.DEBRIS)
					&& !targetsAllowed.contains(CTargetType.DEBRIS)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_DEBRIS);
			}
			else if (this.destType.getTargetedAs().contains(CTargetType.WALL)
					&& !targetsAllowed.contains(CTargetType.WALL)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_WALLS);
			}
			else if (this.destType.getTargetedAs().contains(CTargetType.BRIDGE)
					&& !targetsAllowed.contains(CTargetType.BRIDGE)) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_BRIDGES);
			}
			else {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_TARGET_THIS_UNIT);
			}
		}
		return false;
	}

	@Override
	public <T> T visit(final AbilityTargetVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public <T> T visit(final CWidgetVisitor<T> visitor) {
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

	@Override
	public double distance(final float x, final float y) {
		return StrictMath.sqrt(distanceSquaredNoCollision(x, y));
	}

	public void add(final CSimulation simulation, final CDestructableBuff buff) {
		if (this.buffs == null) {
			this.buffs = new ArrayList<>();
		}
		this.buffs.add(buff);
		buff.onAdd(simulation, this);
	}

	public void remove(final CSimulation simulation, final CDestructableBuff buff) {
		this.buffs.remove(buff);
		buff.onRemove(simulation, this);
	}

	public void onRemove(CSimulation cSimulation) {
		if (this.pathingInstance != null) {
			this.pathingInstance.remove();
		}
		if (this.pathingInstanceDeath != null) {
			this.pathingInstanceDeath.remove();
		}
	}
}
