package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class RenderDoodad {
	private static final int SAMPLE_RADIUS = 4;
	private static final float[] VERTEX_COLOR_BLACK = { 0f, 0f, 0f, 1f };
	public final ModelInstance instance;
	private final MutableGameObject row;
	private final float maxPitch;
	private final float maxRoll;
	protected float x;
	protected float y;
	protected float selectionScale;

	public RenderDoodad(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final float[] location3D, final float[] scale3D, final float facingRadians, final float maxPitch,
			final float maxRoll, final float selectionScale) {
		this.maxPitch = maxPitch;
		this.maxRoll = maxRoll;
		final boolean isSimple = row.readSLKTagBoolean("lightweight");
		ModelInstance instance;

		if (isSimple && false) {
			instance = model.addInstance(1);
		}
		else {
			instance = model.addInstance();
			((MdxComplexInstance) instance).setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
		}

		instance.move(location3D);
		// TODO: the following pitch/roll system is a heuristic, and we probably want to
		// revisit it later.
		// Specifically, I was pretty convinced that whichever is applied first
		// (pitch/roll) should be used to do a projection onto the already-tilted plane
		// to find the angle used for the other of the two
		// (instead of measuring down from an imaginary flat ground plane, as we do
		// currently).
		float pitch, roll;
		this.x = location3D[0];
		this.y = location3D[1];
		{
			if (!map.terrain.inPlayableArea(this.x, this.y)) {
				((MdxComplexInstance) instance).setVertexColor(VERTEX_COLOR_BLACK);
			}
		}
		final float pitchSampleForwardX = this.x + (SAMPLE_RADIUS * (float) Math.cos(facingRadians));
		final float pitchSampleForwardY = this.y + (SAMPLE_RADIUS * (float) Math.sin(facingRadians));
		final float pitchSampleBackwardX = this.x - (SAMPLE_RADIUS * (float) Math.cos(facingRadians));
		final float pitchSampleBackwardY = this.y - (SAMPLE_RADIUS * (float) Math.sin(facingRadians));
		final float pitchSampleGroundHeight1 = map.terrain.getGroundHeight(pitchSampleBackwardX, pitchSampleBackwardY);
		final float pitchSampleGorundHeight2 = map.terrain.getGroundHeight(pitchSampleForwardX, pitchSampleForwardY);
		pitch = Math.max(-maxPitch, Math.min(maxPitch,
				(float) Math.atan2(pitchSampleGorundHeight2 - pitchSampleGroundHeight1, SAMPLE_RADIUS * 2)));
		final double leftOfFacingAngle = facingRadians + (Math.PI / 2);
		final float rollSampleForwardX = this.x + (SAMPLE_RADIUS * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleForwardY = this.y + (SAMPLE_RADIUS * (float) Math.sin(leftOfFacingAngle));
		final float rollSampleBackwardX = this.x - (SAMPLE_RADIUS * (float) Math.cos(leftOfFacingAngle));
		final float rollSampleBackwardY = this.y - (SAMPLE_RADIUS * (float) Math.sin(leftOfFacingAngle));
		final float rollSampleGroundHeight1 = map.terrain.getGroundHeight(rollSampleBackwardX, rollSampleBackwardY);
		final float rollSampleGroundHeight2 = map.terrain.getGroundHeight(rollSampleForwardX, rollSampleForwardY);
		roll = Math.max(-maxRoll, Math.min(maxRoll,
				(float) Math.atan2(rollSampleGroundHeight2 - rollSampleGroundHeight1, SAMPLE_RADIUS * 2)));
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, facingRadians));
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Y, -pitch));
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_X, roll));
//		instance.rotate(new Quaternion().setEulerAnglesRad(facingRadians, 0, 0));
		instance.scale(scale3D);
		this.selectionScale = selectionScale;
		instance.setScene(map.worldScene);

		this.instance = instance;
		this.row = row;
	}

	public PrimaryTag getAnimation() {
		return PrimaryTag.STAND;
	}
}
