package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityBag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.thirdperson.CBehaviorPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityPlayerPawn extends AbstractGenericNoIconAbility implements CAbilitySpell {
	public static final War3ID CODE = War3ID.fromString("Apwn");

	private static final int RENDER_MOVE_SPEED = (int) (18 * (1 / WarsmashConstants.SIMULATION_STEP_TIME));
	/** WoW's default backpack holds 16 slots. */
	public static final int BACKPACK_SLOT_COUNT = 16;
	private float z = 20000;
	private CBehaviorPlayerPawn behaviorPlayerPawn;
	private static final Vector3 tempVec = new Vector3();

	/**
	 * The always-present, built-in backpack bag (WoW container id 0). Unlike the
	 * other bags, it is intrinsic to the pawn rather than provided by a carried
	 * Warcraft III item, so the pawn owns it directly.
	 */
	private CAbilityBag backpack;

	public CAbilityPlayerPawn(final int handleId, final War3ID alias) {
		super(handleId, alias, alias);
	}

	public CAbilityBag getBackpack() {
		return this.backpack;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget target,
			final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}
	
	@Override
	public boolean checkBeforeQueue(CSimulation game, CUnit caster, int playerIndex, int orderId, boolean autoOrder,
			AbilityTarget target) {
		if (orderId == OrderIds.pawnCheesyRightMouseTurn) {
			AbilityPointTarget point = target.visit(AbilityTargetVisitor.POINT);
			if (point != null) {
				caster.setFacing(point.y);
				return false;
			}
		}
		return super.checkBeforeQueue(game, caster, playerIndex, orderId, autoOrder, target);
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final boolean autoOrder, final AbilityTargetCheckReceiver<Void> receiver) {
		if (orderId == OrderIds.pawnDownPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnUpPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnLeftPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnRightPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnJumpPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnJumpReleased) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnSitPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnLootPressed) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnLootReleased) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnDownReleased) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnUpReleased) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnLeftReleased) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.pawnRightReleased) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorPlayerPawn = new CBehaviorPlayerPawn(unit, this);
		unit.setDefaultBehavior(this.behaviorPlayerPawn);
		game.setupPlayerPawn(unit, this, this.behaviorPlayerPawn);
		if (this.backpack == null) {
			this.backpack = new CAbilityBag(game.getHandleIdAllocator().createId(), CODE, CODE, BACKPACK_SLOT_COUNT);
			this.backpack.setIconShowing(false);
			// Add the backpack as a real (but command-card-invisible) ability so it joins
			// the unit's ability iteration: when the hero's inventory is full, a pickup
			// order falls through to the backpack and is stored there automatically.
			unit.add(game, this.backpack);
		}
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder) {
		if (orderId == OrderIds.pawnDownPressed) {
			this.behaviorPlayerPawn.getCameraPanControls().down = true;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnUpPressed) {
			this.behaviorPlayerPawn.getCameraPanControls().up = true;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnLeftPressed) {
			this.behaviorPlayerPawn.getCameraPanControls().left = true;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnRightPressed) {
			this.behaviorPlayerPawn.getCameraPanControls().right = true;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnDownReleased) {
			this.behaviorPlayerPawn.getCameraPanControls().down = false;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnUpReleased) {
			this.behaviorPlayerPawn.getCameraPanControls().up = false;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnLeftReleased) {
			this.behaviorPlayerPawn.getCameraPanControls().left = false;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnRightReleased) {
			this.behaviorPlayerPawn.getCameraPanControls().right = false;
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnJumpPressed) {
			this.behaviorPlayerPawn.jump();
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnJumpReleased) {
			this.behaviorPlayerPawn.jumpReleased();
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnSitPressed) {
			this.behaviorPlayerPawn.sit();
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnLootPressed) {
			this.behaviorPlayerPawn.loot();
			return this.behaviorPlayerPawn;
		}
		else if (orderId == OrderIds.pawnLootReleased) {
			this.behaviorPlayerPawn.lootReleased();
			return this.behaviorPlayerPawn;
		}
		else {
			return this.behaviorPlayerPawn;
		}
	}

	public float getZ() {
		return this.z;
	}

	public void setZ(final float z) {
		this.z = z;
	}

	public CBehaviorPlayerPawn getBehaviorPlayerPawn() {
		return this.behaviorPlayerPawn;
	}

	@Override
	public void populate(final GameObject worldEditorAbility, final int level) {
	}

	public float getRenderMoveSpeed() {
		return Math.max(getBehaviorPlayerPawn().getVelocity().len(),
				getBehaviorPlayerPawn().getPreviousVelocity().len()) / WarsmashConstants.SIMULATION_STEP_TIME;
	}
}
