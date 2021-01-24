package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class RenderDestructable extends RenderDoodad implements RenderWidget {
	private static final War3ID TEX_FILE = War3ID.fromString("btxf");
	private static final War3ID TEX_ID = War3ID.fromString("btxi");
	private static final War3ID SEL_CIRCLE_SIZE = War3ID.fromString("bgsc");

	private final float life;
	public Rectangle walkableBounds;
	private final CDestructable simulationDestructable;
	private SplatMover selectionCircle;

	public RenderDestructable(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final com.etheller.warsmash.parsers.w3x.doo.Doodad doodad, final WorldEditorDataType type,
			final float maxPitch, final float maxRoll, final float life, final BuildingShadow destructableShadow,
			final CDestructable simulationDestructable) {
		super(map, model, row, doodad, type, maxPitch, maxRoll);
		this.life = life;
		this.simulationDestructable = simulationDestructable;
		String replaceableTextureFile = row.getFieldAsString(TEX_FILE, 0);
		final int replaceableTextureId = row.getFieldAsInteger(TEX_ID, 0);
		if ((replaceableTextureFile != null) && (replaceableTextureFile.length() > 1)) {
			final int dotIndex = replaceableTextureFile.lastIndexOf('.');
			if (dotIndex != -1) {
				replaceableTextureFile = replaceableTextureFile.substring(0, dotIndex);
			}
			replaceableTextureFile += ".blp";
			this.instance.setReplaceableTexture(replaceableTextureId, replaceableTextureFile);
		}
		this.selectionScale *= row.getFieldAsFloat(SEL_CIRCLE_SIZE, 0);
	}

	@Override
	public PrimaryTag getAnimation() {
		if (this.life <= 0) {
			return PrimaryTag.DEATH;
		}
		return super.getAnimation();
	}

	@Override
	public MdxComplexInstance getInstance() {
		return (MdxComplexInstance) this.instance;
	}

	@Override
	public CWidget getSimulationWidget() {
		return this.simulationDestructable;
	}

	@Override
	public void updateAnimations(final War3MapViewer war3MapViewer) {
		// TODO maybe move getAnimation behaviors to here and make this thing not a
		// doodad
	}

	@Override
	public boolean isIntersectedOnMeshAlways() {
		return false;
	}

	@Override
	public float getSelectionScale() {
		return this.selectionScale;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public void unassignSelectionCircle() {
		this.selectionCircle = null;
	}

	@Override
	public void assignSelectionCircle(final SplatMover selectionCircle) {
		this.selectionCircle = selectionCircle;

	}
}
