package com.etheller.warsmash.viewer5.handlers.w3x.rendersim;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.CollidableDoodadComponent;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.BuildingShadow;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class RenderDestructable extends RenderDoodad implements RenderWidget {
	private static final War3ID TEX_FILE = War3ID.fromString("btxf");
	private static final War3ID TEX_ID = War3ID.fromString("btxi");
	private static final War3ID SEL_CIRCLE_SIZE = War3ID.fromString("bgsc");

	private float life;
	public Rectangle walkableBounds;
	private final CDestructable simulationDestructable;
	private SplatMover selectionCircle;
	private SplatMover selectionPreviewHighlight;
	private final UnitAnimationListenerImpl unitAnimationListenerImpl;
	private boolean dead;
	private BuildingShadow destructableShadow;
	private final boolean selectable;
	private boolean blighted = false;
	private final int replaceableTextureId;
	private String replaceableTextureFile;

	private final List<CollidableDoodadComponent> collidableComponents = new ArrayList<>();

	public RenderDestructable(final War3MapViewer map, final MdxModel model, final MutableGameObject row,
			final float[] location3D, final float[] scale3D, final float facingRadians, final float selectionScale,
			final float maxPitch, final float maxRoll, final float life, final BuildingShadow destructableShadow,
			final CDestructable simulationDestructable, final int doodadVariation) {
		super(map, model, row, location3D, scale3D, facingRadians, maxPitch, maxRoll, selectionScale, doodadVariation);
		this.life = simulationDestructable.getLife();
		this.destructableShadow = destructableShadow;
		this.simulationDestructable = simulationDestructable;
		this.replaceableTextureFile = row.getFieldAsString(TEX_FILE, 0);
		this.replaceableTextureId = row.getFieldAsInteger(TEX_ID, 0);
		if ((this.replaceableTextureFile != null) && (this.replaceableTextureFile.length() > 1)) {
			final int dotIndex = this.replaceableTextureFile.lastIndexOf('.');
			if (dotIndex != -1) {
				this.replaceableTextureFile = this.replaceableTextureFile.substring(0, dotIndex);
			}
			if (simulationDestructable.isBlighted()) {
				this.blighted = true;
				this.replaceableTextureFile += "Blight";
			}
			this.instance.setReplaceableTexture(this.replaceableTextureId, this.replaceableTextureFile + ".blp");
			this.instance.setReplaceableTextureHD(this.replaceableTextureId, this.replaceableTextureFile);
		}
		this.selectionScale *= row.getFieldAsFloat(SEL_CIRCLE_SIZE, 0);
		this.unitAnimationListenerImpl = new UnitAnimationListenerImpl((MdxComplexInstance) this.instance, 0, 0);
		simulationDestructable.setUnitAnimationListener(this.unitAnimationListenerImpl);
		this.unitAnimationListenerImpl.playAnimation(true, getAnimation(), SequenceUtils.EMPTY, 1.0f, true);
		this.selectable = row.readSLKTagBoolean("selectable");
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

		final boolean dead = this.simulationDestructable.isDead();
		if (dead && !this.dead) {
			this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.DEATH, SequenceUtils.EMPTY, 1.0f, true);
			if (this.destructableShadow != null) {
				this.destructableShadow.remove();
				this.destructableShadow = null;
			}
			if (this.selectionCircle != null) {
				this.selectionCircle.destroy(Gdx.gl30, war3MapViewer.terrain.centerOffset);
				this.selectionCircle = null;
			}
			if (this.selectionPreviewHighlight != null) {
				this.selectionPreviewHighlight.destroy(Gdx.gl30, war3MapViewer.terrain.centerOffset);
				this.selectionPreviewHighlight = null;
			}
		}
		else if (!dead) {
			if (this.dead) {
				this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.BIRTH, SequenceUtils.EMPTY, 1.0f, true);
				this.unitAnimationListenerImpl.queueAnimation(PrimaryTag.STAND, SequenceUtils.EMPTY, true);
				// TODO add back shadow here

			}
			else {
				if (Math.abs(this.life - this.simulationDestructable.getLife()) > 0.003f) {
					if (this.life > this.simulationDestructable.getLife()) {
						this.unitAnimationListenerImpl.playAnimation(true, PrimaryTag.STAND, SequenceUtils.HIT, 1.0f,
								true);
					}
					this.life = this.simulationDestructable.getLife();
				}
			}
		}
		this.dead = dead;
		final boolean blighted = this.simulationDestructable.isBlighted();
		if (blighted && !this.blighted) {
			this.blighted = blighted;
			if (this.replaceableTextureFile != null) {
				this.replaceableTextureFile += "Blight";
			}
			this.instance.setReplaceableTexture(this.replaceableTextureId, this.replaceableTextureFile + ".blp");
		}
		this.unitAnimationListenerImpl.update();
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
	public float getZ() {
		return this.instance.localLocation.z;
	}

	@Override
	public void unassignSelectionCircle() {
		this.selectionCircle = null;
	}

	@Override
	public void assignSelectionCircle(final SplatMover selectionCircle) {
		this.selectionCircle = selectionCircle;
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
		return this.selectable;
	}

	@Override
	public SplatMover getSelectionPreviewHighlight() {
		return this.selectionPreviewHighlight;
	}

	@Override
	public SplatMover getSelectionCircle() {
		return this.selectionCircle;
	}

	public CDestructable getSimulationDestructable() {
		return this.simulationDestructable;
	}

	public UnitAnimationListenerImpl getUnitAnimationListenerImpl() {
		return this.unitAnimationListenerImpl;
	}

	public void setAnimation(final String sequence) {
		final EnumSet<PrimaryTag> primaryTags = EnumSet.noneOf(PrimaryTag.class);
		PrimaryTag bestPrimaryTag = null;
		final EnumSet<SecondaryTag> secondaryTags = EnumSet.noneOf(SecondaryTag.class);
		TokenLoop:
		for (final String token : sequence.split("\\s+")) {
			final String upperCaseToken = token.toUpperCase();
			for (final PrimaryTag primaryTag : PrimaryTag.values()) {
				if (upperCaseToken.equals(primaryTag.name())) {
					primaryTags.add(primaryTag);
					bestPrimaryTag = primaryTag;
					continue TokenLoop;
				}
			}
			for (final SecondaryTag secondaryTag : SecondaryTag.values()) {
				if (upperCaseToken.equals(secondaryTag.name())) {
					secondaryTags.add(secondaryTag);
					continue TokenLoop;
				}
			}
			break;
		}
		this.dead = this.simulationDestructable.isDead();
		this.life = this.simulationDestructable.getLife();
		this.unitAnimationListenerImpl.playAnimation(true, bestPrimaryTag, secondaryTags, 1.0f, true);
	}

	public void notifyLifeRestored() {
		this.dead = this.simulationDestructable.isDead();
		this.life = this.simulationDestructable.getLife();
		this.unitAnimationListenerImpl.playAnimation(true, getAnimation(), SequenceUtils.EMPTY, 1.0f, true);
	}

	@Override
	public boolean isShowSelectionCircleAboveWater() {
		return false;
	}

	private static final War3ID DOODAD_COLOR_RED = War3ID.fromString("bvcr");
	private static final War3ID DOODAD_COLOR_GREEN = War3ID.fromString("bvcg");
	private static final War3ID DOODAD_COLOR_BLUE = War3ID.fromString("bvcb");

	@Override
	public void applyColor(final MutableGameObject row, final int doodadVariation, final ModelInstance instance) {
		final int vertR = row.getFieldAsInteger(DOODAD_COLOR_RED, doodadVariation);
		final int vertG = row.getFieldAsInteger(DOODAD_COLOR_GREEN, doodadVariation);
		final int vertB = row.getFieldAsInteger(DOODAD_COLOR_BLUE, doodadVariation);
		((MdxComplexInstance) instance).setVertexColor(new float[] { vertR / 255f, vertG / 255f, vertB / 255f });
	}

	public void add(final CollidableDoodadComponent collidableComponent) {
		this.collidableComponents.add(collidableComponent);
	}

	public List<CollidableDoodadComponent> getCollidableComponents() {
		return this.collidableComponents;
	}
}
