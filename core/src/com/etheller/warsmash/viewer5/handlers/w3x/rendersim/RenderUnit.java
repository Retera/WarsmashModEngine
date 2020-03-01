package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.parsers.mdlx.Sequence;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.IndexedSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.StandSequence;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityHoldPosition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityPatrol;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityStop;

public class RenderUnit {
	private static final double GLOBAL_TURN_RATE = Math.toDegrees(7f);
	private static final Quaternion tempQuat = new Quaternion();
	private static final War3ID RED = War3ID.fromString("uclr");
	private static final War3ID GREEN = War3ID.fromString("uclg");
	private static final War3ID BLUE = War3ID.fromString("uclb");
	private static final War3ID MODEL_SCALE = War3ID.fromString("usca");
	private static final War3ID MOVE_HEIGHT = War3ID.fromString("umvh");
	private static final float[] heapZ = new float[3];
	public final MdxComplexInstance instance;
	public final MutableGameObject row;
	public final float[] location = new float[3];
	public float radius;
	public UnitSoundset soundset;
	public final MdxModel portraitModel;
	public int playerIndex;
	private final CUnit simulationUnit;
	private COrder lastOrder;
	private String lastOrderAnimation;
	public SplatMover shadow;
	public SplatMover selectionCircle;
	private final List<CommandCardIcon> commandCardIcons = new ArrayList<>();

	private float x;
	private float y;
	private float facing;

	public RenderUnit(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.unitsdoo.Unit unit, final UnitSoundset soundset,
			final MdxModel portraitModel, final CUnit simulationUnit) {
		this.portraitModel = portraitModel;
		this.simulationUnit = simulationUnit;
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		final float[] location = unit.getLocation();
		System.arraycopy(location, 0, this.location, 0, 3);
		instance.move(location);
		this.facing = simulationUnit.getFacing();
		final float angle = (float) Math.toRadians(this.facing);
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		this.x = simulationUnit.getX();
		this.y = simulationUnit.getY();
		instance.rotate(tempQuat.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		instance.scale(unit.getScale());
		this.playerIndex = unit.getPlayer();
		instance.setTeamColor(this.playerIndex);
		instance.setScene(map.worldScene);

		if (row != null) {
			heapZ[2] = simulationUnit.getFlyHeight();
			this.location[2] += heapZ[2];

			instance.move(heapZ);
			War3ID red;
			War3ID green;
			War3ID blue;
			War3ID scale;
			scale = MODEL_SCALE;
			red = RED;
			green = GREEN;
			blue = BLUE;
			instance.setVertexColor(new float[] { (row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f });
			instance.uniformScale(row.getFieldAsFloat(scale, 0));

			this.radius = row.getFieldAsFloat(War3MapViewer.UNIT_SELECT_SCALE, 0) * 36;
		}

		this.instance = instance;
		this.row = row;
		this.soundset = soundset;

		for (final CAbility ability : simulationUnit.getAbilities()) {
			if (ability instanceof CAbilityMove) {
				this.commandCardIcons.add(new CommandCardIcon(0, 0,
						ImageUtils.getBLPTexture(map.dataSource, "ReplaceableTextures\\CommandButtons\\BTNMove.blp"),
						ability.getOrderId()));
			}
			else if (ability instanceof CAbilityAttack) {
				this.commandCardIcons.add(new CommandCardIcon(4, 0,
						ImageUtils.getBLPTexture(map.dataSource, "ReplaceableTextures\\CommandButtons\\BTNAttack.blp"),
						ability.getOrderId()));
			}
			else if (ability instanceof CAbilityHoldPosition) {
				this.commandCardIcons
						.add(new CommandCardIcon(2, 0,
								ImageUtils.getBLPTexture(map.dataSource,
										"ReplaceableTextures\\CommandButtons\\BTNHoldPosition.blp"),
								ability.getOrderId()));
			}
			else if (ability instanceof CAbilityPatrol) {
				this.commandCardIcons.add(new CommandCardIcon(3, 0,
						ImageUtils.getBLPTexture(map.dataSource, "ReplaceableTextures\\CommandButtons\\BTNPatrol.blp"),
						ability.getOrderId()));
			}
			else if (ability instanceof CAbilityStop) {
				this.commandCardIcons.add(new CommandCardIcon(1, 0,
						ImageUtils.getBLPTexture(map.dataSource, "ReplaceableTextures\\CommandButtons\\BTNStop.blp"),
						ability.getOrderId()));
			}
		}
	}

	public void updateAnimations(final War3MapViewer map) {
		final float deltaTime = Gdx.graphics.getDeltaTime();
		final float simulationX = this.simulationUnit.getX();
		final float simulationY = this.simulationUnit.getY();
		final float simDx = simulationX - this.x;
		final float simDy = simulationY - this.y;
		final float distanceToSimulation = (float) Math.sqrt((simDx * simDx) + (simDy * simDy));
		final int speed = this.simulationUnit.getSpeed();
		final float speedDelta = speed * deltaTime;
		if (distanceToSimulation > speedDelta) {
			this.x += (speedDelta * simDx) / distanceToSimulation;
			this.y += (speedDelta * simDy) / distanceToSimulation;
		}
		else {
			this.x = simulationX;
			this.y = simulationY;
		}
		final float x = this.x;
		final float dx = x - this.location[0];
		this.location[0] = x;
		final float y = this.y;
		final float dy = y - this.location[1];
		this.location[1] = y;
		this.location[2] = this.simulationUnit.getFlyHeight() + map.terrain.getGroundHeight(x, y);
		this.instance.moveTo(this.location);
		float simulationFacing = this.simulationUnit.getFacing();
		if (simulationFacing < 0) {
			simulationFacing += 360;
		}
		float renderFacing = this.facing;
		if (renderFacing < 0) {
			renderFacing += 360;
		}
		float facingDelta = simulationFacing - renderFacing;
		if (facingDelta < -180) {
			facingDelta = 360 + facingDelta;
		}
		if (facingDelta > 180) {
			facingDelta = -360 + facingDelta;
		}
		final float absoluteFacingDelta = Math.abs(facingDelta);
		float angleToAdd = (float) (Math.signum(facingDelta) * GLOBAL_TURN_RATE * deltaTime);
		if (absoluteFacingDelta < Math.abs(angleToAdd)) {
			angleToAdd = facingDelta;
		}
		this.facing = (((this.facing + angleToAdd) % 360) + 360) % 360;
		this.instance.setLocalRotation(tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z, this.facing));
		map.worldScene.grid.moved(this.instance);
		final MdxComplexInstance mdxComplexInstance = this.instance;
		final COrder currentOrder = this.simulationUnit.getCurrentOrder();
		if (this.simulationUnit.getLife() <= 0) {
			final MdxModel model = (MdxModel) mdxComplexInstance.model;
			final List<Sequence> sequences = model.getSequences();
			final IndexedSequence sequence = StandSequence.selectSequence("death", sequences);
			if ((sequence != null) && (mdxComplexInstance.sequence != sequence.index)) {
				mdxComplexInstance.setSequence(sequence.index);
			}
		}
		else if (mdxComplexInstance.sequenceEnded || (mdxComplexInstance.sequence == -1)
				|| (currentOrder != this.lastOrder)
				|| ((currentOrder != null) && (currentOrder.getAnimationName() != null)
						&& !currentOrder.getAnimationName().equals(this.lastOrderAnimation))) {
			if (this.simulationUnit.getCurrentOrder() != null) {
				final String animationName = this.simulationUnit.getCurrentOrder().getAnimationName();
				StandSequence.randomSequence(mdxComplexInstance, animationName);
				this.lastOrderAnimation = animationName;
			}
			else {
				StandSequence.randomStandSequence(mdxComplexInstance);
			}
		}
		this.lastOrder = currentOrder;
		if (this.shadow != null) {
			this.shadow.move(dx, dy);
		}
		if (this.selectionCircle != null) {
			this.selectionCircle.move(dx, dy);
		}
	}

	public CUnit getSimulationUnit() {
		return this.simulationUnit;
	}

	public List<CommandCardIcon> getCommandCardIcons() {
		return this.commandCardIcons;
	}
}
