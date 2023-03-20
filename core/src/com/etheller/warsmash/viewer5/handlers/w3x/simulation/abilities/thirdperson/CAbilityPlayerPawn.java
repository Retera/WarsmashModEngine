package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.thirdperson;

import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.skills.CAbilitySpell;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.thirdperson.CBehaviorPlayerPawn;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityPlayerPawn extends AbstractGenericNoIconAbility implements CAbilitySpell {
	private static final int RENDER_MOVE_SPEED = (int) (18 * (1 / WarsmashConstants.SIMULATION_STEP_TIME));
	private float z;
	private CBehaviorPlayerPawn behaviorPlayerPawn;
	private static final Vector3 tempVec = new Vector3();

	public CAbilityPlayerPawn(final int handleId, final War3ID alias) {
		super(handleId, alias);
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
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
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
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
	public void populate(final MutableGameObject worldEditorAbility, final int level) {
	}

	public float getRenderMoveSpeed() {
		final Vector3 vec = tempVec.set(getBehaviorPlayerPawn().getVelocity());
		vec.z = 0;
		return vec.len() / WarsmashConstants.SIMULATION_STEP_TIME;
	}
}
