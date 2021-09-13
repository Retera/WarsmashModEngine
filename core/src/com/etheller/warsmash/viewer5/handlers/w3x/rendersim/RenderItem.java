package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import com.badlogic.gdx.math.Quaternion;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSoundset;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class RenderItem implements RenderWidget {
	private static final War3ID ITEM_MODEL_SCALE = War3ID.fromString("isca");
	private static final War3ID ITEM_RED = War3ID.fromString("iclr");
	private static final War3ID ITEM_GREEN = War3ID.fromString("iclg");
	private static final War3ID ITEM_BLUE = War3ID.fromString("iclb");
	private final CItem simulationItem;
	public final MdxComplexInstance instance;
	public final MutableGameObject row;
	public final float[] location = new float[3];
	public float radius;
	public UnitSoundset soundset;
	public final MdxModel portraitModel;
	public SplatMover shadow;
	public SplatMover selectionCircle;
	public SplatMover selectionPreviewHighlight;
	private boolean hidden;
	private boolean dead;

	public RenderItem(final War3MapViewer map, final MdxModel model, final MutableGameObject row, final float x,
			final float y, final float z, final float angle, final UnitSoundset soundset, final MdxModel portraitModel,
			final CItem simulationItem) {
		this.portraitModel = portraitModel;
		this.simulationItem = simulationItem;
		final MdxComplexInstance instance = (MdxComplexInstance) model.addInstance();

		this.location[0] = x;
		this.location[1] = y;
		this.location[2] = z;
		instance.move(this.location);
//		instance.localRotation.setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle);
		instance.rotate(new Quaternion().setFromAxisRad(RenderMathUtils.VEC3_UNIT_Z, angle));
		instance.setScene(map.worldScene);

		if (row != null) {
			War3ID red;
			War3ID green;
			War3ID blue;
			War3ID scale;
			scale = ITEM_MODEL_SCALE;
			red = ITEM_RED;
			green = ITEM_GREEN;
			blue = ITEM_BLUE;
			instance.setVertexColor(new float[] { (row.getFieldAsInteger(red, 0)) / 255f,
					(row.getFieldAsInteger(green, 0)) / 255f, (row.getFieldAsInteger(blue, 0)) / 255f });
			instance.uniformScale(row.getFieldAsFloat(scale, 0));

			this.radius = 1 * 36;
		}

		this.instance = instance;
		this.row = row;
		this.soundset = soundset;
	}

	@Override
	public MdxComplexInstance getInstance() {
		return this.instance;
	}

	@Override
	public CWidget getSimulationWidget() {
		return this.simulationItem;
	}

	@Override
	public void updateAnimations(final War3MapViewer map) {
		final boolean hidden = this.simulationItem.isHidden();
		if (hidden != this.hidden) {
			this.hidden = hidden;
			if (hidden) {
				this.instance.hide();
				if (this.shadow != null) {
					this.shadow.hide();
				}
			}
			else {
				this.instance.show();
				if (this.shadow != null) {
					this.shadow.show(map.terrain.centerOffset);
				}
			}
		}
		final boolean dead = this.simulationItem.isDead();
		final MdxComplexInstance mdxComplexInstance = this.instance;
		if (dead) {
			if (!this.dead) {
				this.dead = dead;
				SequenceUtils.randomDeathSequence(mdxComplexInstance);
			}
		}
		else if (mdxComplexInstance.sequenceEnded || (mdxComplexInstance.sequence == -1)) {
			SequenceUtils.randomStandSequence(mdxComplexInstance);
		}

		final float prevX = this.location[0];
		final float prevY = this.location[1];
		final float simulationX = this.simulationItem.getX();
		final float simulationY = this.simulationItem.getY();
		final float dx = simulationX - prevX;
		final float dy = simulationY - prevY;
		this.location[0] = simulationX;
		this.location[1] = simulationY;
		final float groundHeight;
		// land units will have their feet pass under the surface of the water, so items
		// here are in the same place
		final float groundHeightTerrainAndWater = map.terrain.getGroundHeight(this.location[0], this.location[1]);
		MdxComplexInstance currentWalkableUnder;
		currentWalkableUnder = map.getHighestWalkableUnder(this.location[0], this.location[1]);
		War3MapViewer.gdxRayHeap.set(this.location[0], this.location[1], 4096, 0, 0, -8192);
		if ((currentWalkableUnder != null)
				&& currentWalkableUnder.intersectRayWithCollision(War3MapViewer.gdxRayHeap,
						War3MapViewer.intersectionHeap, true, true)
				&& (War3MapViewer.intersectionHeap.z > groundHeightTerrainAndWater)) {
			groundHeight = War3MapViewer.intersectionHeap.z;
		}
		else {
			groundHeight = groundHeightTerrainAndWater;
			currentWalkableUnder = null;
		}
		this.location[2] = this.simulationItem.getFlyHeight() + groundHeight;

		this.instance.moveTo(this.location);
		if (this.shadow != null) {
			this.shadow.move(dx, dy, map.terrain.centerOffset);
			this.shadow.setHeightAbsolute(currentWalkableUnder != null, groundHeight + map.imageWalkableZOffset);
		}
	}

	@Override
	public boolean isIntersectedOnMeshAlways() {
		return false;
	}

	@Override
	public float getSelectionScale() {
		return 64.0f;
	}

	@Override
	public float getX() {
		return this.location[0];
	}

	@Override
	public float getY() {
		return this.location[1];
	}

	@Override
	public float getZ() {
		return this.location[2];
	}

	@Override
	public void unassignSelectionCircle() {
		this.selectionCircle = null;
	}

	@Override
	public void assignSelectionCircle(final SplatMover t) {
		this.selectionCircle = t;
	}

	@Override
	public void unassignSelectionPreviewHighlight() {
		this.selectionPreviewHighlight = null;
	}

	@Override
	public void assignSelectionPreviewHighlight(final SplatMover t) {
		this.selectionPreviewHighlight = t;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public SplatMover getSelectionPreviewHighlight() {
		return this.selectionPreviewHighlight;
	}

	@Override
	public SplatMover getSelectionCircle() {
		return this.selectionCircle;
	}

	public CItem getSimulationItem() {
		return this.simulationItem;
	}
}
