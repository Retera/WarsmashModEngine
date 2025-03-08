package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CFogState;

public class RenderDoodad {
	private static final int SAMPLE_RADIUS = 4;
	private static final float[] VERTEX_COLOR_BLACK = { 0f, 0f, 0f, 1f };
	private static final float[] VERTEX_COLOR_HEAP = { 0f, 0f, 0f, 1f };
	private static final float[] VERTEX_COLOR_UNEXPLORED = VERTEX_COLOR_BLACK; // later optionally gray
	public final ModelInstance instance;
	private final GameObject row;
	private final float maxPitch;
	private final float maxRoll;
	protected float x;
	protected float y;
	protected float selectionScale;

	private static final String DOODAD_COLOR_RED = "vertR"; // replaced from 'dvr1'
	private static final String DOODAD_COLOR_GREEN = "vertG"; // replaced from 'dvg1'
	private static final String DOODAD_COLOR_BLUE = "vertB"; // replaced from 'dvb1'

	private CFogState fogState;
	private final float[] vertexColorBase;
	private final float[] vertexColorFogged;

	public RenderDoodad(final War3MapViewer map, final MdxModel model, final GameObject row, final float[] location3D,
			final float[] scale3D, final float facingRadians, final float maxPitch, final float maxRoll,
			final float selectionScale, final int doodadVariation) {
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
		this.vertexColorBase = new float[] { ((MdxComplexInstance) instance).vertexColor[0],
				((MdxComplexInstance) instance).vertexColor[1], ((MdxComplexInstance) instance).vertexColor[2] };
		this.vertexColorFogged = new float[] { this.vertexColorBase[0] / 2, this.vertexColorBase[1] / 2,
				this.vertexColorBase[2] / 2 };
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

	public void applyColor(final GameObject row, final int doodadVariation, final ModelInstance instance) {
		String variationString = Integer.toString(doodadVariation);
		if (variationString.length() < 2) {
			variationString = "0" + variationString;
		}
		final int vertR = row.getFieldValue(DOODAD_COLOR_RED + variationString);
		final int vertG = row.getFieldValue(DOODAD_COLOR_GREEN + variationString);
		final int vertB = row.getFieldValue(DOODAD_COLOR_BLUE + variationString);
		((MdxComplexInstance) instance).setVertexColor(new float[] { vertR / 255f, vertG / 255f, vertB / 255f });
	}

	public PrimaryTag getAnimation() {
		return PrimaryTag.STAND;
	}

	private byte lastFogStateColor = 0;

	public void updateFog(final War3MapViewer war3MapViewer) {
		final CPlayerFogOfWar fogOfWar = war3MapViewer.getFogOfWar();
		final PathingGrid pathingGrid = war3MapViewer.simulation.getPathingGrid();
		final int fogOfWarIndexX = pathingGrid.getFogOfWarIndexX(this.x);
		final int fogOfWarIndexY = pathingGrid.getFogOfWarIndexY(this.y);
		final CFogState newFogState = fogOfWar.getFogState(war3MapViewer.simulation, fogOfWarIndexX, fogOfWarIndexY);
		if (newFogState != this.fogState) {
			this.fogState = newFogState;
		}
		if (newFogState.getMask() != this.lastFogStateColor) {
			this.lastFogStateColor = War3MapViewer.fadeLineOfSightColor(this.lastFogStateColor, newFogState.getMask());
			for (int i = 0; i < this.vertexColorBase.length; i++) {
				VERTEX_COLOR_HEAP[i] = (this.vertexColorBase[i] * (255 - (this.lastFogStateColor & 0xFF))) / 255f;
			}
			((MdxComplexInstance) this.instance).setVertexColor(VERTEX_COLOR_HEAP);
		}
	}

	public float[] getVertexColor() {
		return ((MdxComplexInstance) this.instance).vertexColor;
	}
}
