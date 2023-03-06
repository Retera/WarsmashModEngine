package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class RenderDoodad {
	private static final int SAMPLE_RADIUS = 4;
	private static final float[] VERTEX_COLOR_BLACK = { 0f, 0f, 0f, 1f };
	private static final float[] VERTEX_COLOR_UNEXPLORED = VERTEX_COLOR_BLACK; // later optionally gray
	public final ModelInstance instance;
	private final MutableGameObject row;
	private final float maxPitch;
	private final float maxRoll;
	protected float x;
	protected float y;
	protected float selectionScale;

	private static final War3ID DOODAD_COLOR_RED = War3ID.fromString("dvr1");
	private static final War3ID DOODAD_COLOR_GREEN = War3ID.fromString("dvg1");
	private static final War3ID DOODAD_COLOR_BLUE = War3ID.fromString("dvb1");

	private CFogState fogState;
	private final float[] vertexColorBase;
	private final float[] vertexColorFogged;

	public RenderDoodad(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final float[] location3D, final float[] scale3D, final float facingRadians, final float maxPitch,
			final float maxRoll, final float selectionScale, final int doodadVariation) {
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
			else {
				applyColor(row, doodadVariation + 1, instance);
			}
		}
		vertexColorBase = new float[] { ((MdxComplexInstance) instance).vertexColor[0],
				((MdxComplexInstance) instance).vertexColor[1], ((MdxComplexInstance) instance).vertexColor[2] };
		vertexColorFogged = new float[] { vertexColorBase[0] / 2, vertexColorBase[1] / 2, vertexColorBase[2] / 2 };
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

		this.fogState = CFogState.MASKED;
		((MdxComplexInstance) instance).setVertexColor(VERTEX_COLOR_BLACK);

	}

	public void applyColor(final MutableGameObject row, final int doodadVariation, final ModelInstance instance) {
		final int vertR = row.getFieldAsInteger(DOODAD_COLOR_RED, doodadVariation);
		final int vertG = row.getFieldAsInteger(DOODAD_COLOR_GREEN, doodadVariation);
		final int vertB = row.getFieldAsInteger(DOODAD_COLOR_BLUE, doodadVariation);
		((MdxComplexInstance) instance).setVertexColor(new float[] { vertR / 255f, vertG / 255f, vertB / 255f });
	}

	public PrimaryTag getAnimation() {
		return PrimaryTag.STAND;
	}

	public void updateFog(final War3MapViewer war3MapViewer) {
		final CPlayerFogOfWar fogOfWar = war3MapViewer.getFogOfWar();
		final PathingGrid pathingGrid = war3MapViewer.simulation.getPathingGrid();
		final int fogOfWarIndexX = pathingGrid.getFogOfWarIndexX(x);
		final int fogOfWarIndexY = pathingGrid.getFogOfWarIndexY(y);
		final byte state = fogOfWar.getState(fogOfWarIndexX, fogOfWarIndexY);
		CFogState newFogState = CFogState.MASKED;
		if (state < 0) {
			newFogState = CFogState.MASKED;
		}
		else if (state == 0) {
			newFogState = CFogState.VISIBLE;
		}
		else {
			newFogState = CFogState.FOGGED;
		}
		if (newFogState != fogState) {
			this.fogState = newFogState;
			switch (newFogState) {
			case MASKED:
				((MdxComplexInstance) instance).setVertexColor(VERTEX_COLOR_BLACK);
				break;
			case FOGGED:
				((MdxComplexInstance) instance).setVertexColor(vertexColorFogged);
				break;
			case VISIBLE:
				((MdxComplexInstance) instance).setVertexColor(vertexColorBase);
				break;
			}
		}
	}
}
