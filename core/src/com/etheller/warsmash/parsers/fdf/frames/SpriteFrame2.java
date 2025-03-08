package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.WarsmashGdxGame;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SimpleScene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.Sequence;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.PortraitCameraManager;

public class SpriteFrame2 extends AbstractUIFrame {
	private static final Rectangle tempRect = new Rectangle();
	private static final Vector2 projectionTemp1 = new Vector2();
	private static final Vector2 projectionTemp2 = new Vector2();

	protected final Scene scene;
	protected final Viewport uiViewport;
	private MdxComplexInstance instance;
	private float zDepth;
	private final PortraitCameraManager portraitCameraManager;
	private final ModelViewer viewer;

	public SpriteFrame2(final String name, final UIFrame parent, final Viewport uiViewport, ModelViewer viewer) {
		super(name, parent);
		this.viewer = viewer;
		this.scene = new SimpleScene(viewer, viewer.createLightManager(true));
//		this.scene.alpha = true;
		this.portraitCameraManager = new PortraitCameraManager();
		this.portraitCameraManager.setupCamera(this.scene);
		this.uiViewport = uiViewport;
	}

	public void setModel(final MdxModel model) {
		if (this.instance != null) {
			this.scene.removeInstance(this.instance);
		}
		if (model != null) {
			this.instance = (MdxComplexInstance) model.addInstance();
			this.instance.setSequenceLoopMode(SequenceLoopMode.MODEL_LOOP);
			this.instance.setScene(this.scene);
			this.instance.setBlendTime(model.blendTime);
			this.portraitCameraManager.setModelInstance(this.instance, model);
		}
		else {
			this.instance = null;
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		updateInstanceLocation(this.uiViewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		this.portraitCameraManager.updateCamera();
		if (this.portraitCameraManager.modelCamera == null) {
			viewportUpdated();
		}
		this.scene.update(Gdx.graphics.getDeltaTime());

		if ((this.scene.camera.rect.width != 0) && (this.scene.camera.rect.height != 0)) {
			batch.end();

			Gdx.gl30.glEnable(GL30.GL_SCISSOR_TEST);
			Gdx.gl30.glBindVertexArray(WarsmashGdxGame.VAO);

			this.scene.startFrame();
			this.scene.renderOpaque();
			this.scene.renderTranslucent();

			Gdx.gl30.glDisable(GL30.GL_SCISSOR_TEST);

			Gdx.gl30.glDisable(GL30.GL_CULL_FACE);

			this.viewer.webGL.useShaderProgram(null);

			Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);
			batch.begin();
			this.uiViewport.apply();
		}

	}

	@Override
	public void setFramePointX(final FramePoint framePoint, final float x) {
		super.setFramePointX(framePoint, x);
		updateInstanceLocation(this.uiViewport);
	}

	@Override
	public void setFramePointY(final FramePoint framePoint, final float y) {
		super.setFramePointY(framePoint, y);
		updateInstanceLocation(this.uiViewport);
	}

	public void setZDepth(final float depth) {
		this.zDepth = depth;
		updateInstanceLocation(this.uiViewport);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);

		projectionTemp1.x = getFramePointX(FramePoint.LEFT);
		projectionTemp1.y = getFramePointY(FramePoint.BOTTOM);
		projectionTemp2.x = getFramePointX(FramePoint.RIGHT);
		projectionTemp2.y = getFramePointY(FramePoint.TOP);
		this.uiViewport.project(projectionTemp1);
		this.uiViewport.project(projectionTemp2);

		tempRect.x = projectionTemp1.x + this.uiViewport.getScreenX();
		tempRect.y = projectionTemp1.y + this.uiViewport.getScreenY();
		tempRect.width = projectionTemp2.x - projectionTemp1.x;
		tempRect.height = projectionTemp2.y - projectionTemp1.y;

		this.scene.camera.viewport(tempRect);

		updateInstanceLocation(viewport);
	}

	private float getMinWorldWidth() {
		if (this.uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) this.uiViewport).getMinWorldWidth();
		}
		return this.uiViewport.getWorldWidth();
	}

	private float getMinWorldHeight() {
		if (this.uiViewport instanceof ExtendViewport) {
			return ((ExtendViewport) this.uiViewport).getMinWorldHeight();
		}
		return this.uiViewport.getWorldHeight();
	}

	private void viewportUpdated() {
		final float worldWidth = this.uiViewport.getWorldWidth();
		final float worldHeight = this.uiViewport.getWorldHeight();
		final float xScale = worldWidth / getMinWorldWidth();
		final float yScale = worldHeight / getMinWorldHeight();
		final float uiSceneWidth = 0.8f * xScale;
		final float uiSceneHeight = 0.6f * yScale;
		final float uiSceneX = ((0.8f - uiSceneWidth) / 2);
		final float uiSceneY = ((0.6f - uiSceneHeight) / 2);
		this.scene.camera.ortho(0, this.renderBounds.width, 0, this.renderBounds.height, -1024f, 1024);
	}

	public void setSequence(final int index) {
		if (this.instance != null) {
			this.instance.setSequence(index);
		}
	}

	public void setSequence(final String animationName) {
		if (this.instance != null) {
			SequenceUtils.randomSequence(this.instance, animationName.toLowerCase());
		}
	}

	public void setSequence(final PrimaryTag animationName) {
		if (this.instance != null) {
			SequenceUtils.randomSequence(this.instance, animationName);
		}
	}

	public Sequence setSequence(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryTags) {
		if (this.instance != null) {
			return SequenceUtils.randomSequence(this.instance, animationName, secondaryTags, true);
		}
		return null;
	}

	public void setAnimationSpeed(final float speedRatio) {
		if (this.instance != null) {
			this.instance.setAnimationSpeed(speedRatio);
		}
	}

	public void setFrame(final int animationFrame) {
		if (this.instance != null) {
			this.instance.setFrame(animationFrame);
		}
	}

	public void setFrameByRatio(final float ratioOfAnimationCompleted) {
		if (this.instance != null) {
			this.instance.setFrameByRatio(ratioOfAnimationCompleted);
		}
	}

	private void updateInstanceLocation(final Viewport viewport) {
		if (this.instance != null) {
//			this.instance.setLocation(GameUI.unconvertX(viewport, this.renderBounds.x),
//					GameUI.unconvertY(viewport, this.renderBounds.y), this.zDepth);
			if (isVisible()) {
				this.instance.show();
			}
			else {
				this.instance.hide();
			}
		}
	}

	public boolean isSequenceEnded() {
		if (this.instance == null) {
			return true;
		}
		return this.instance.sequenceEnded || (this.instance.sequence == -1);
	}

	public void setReplaceableId(final int replaceableId, final String blpPath) {
		if (this.instance != null) {
			this.instance.setReplaceableTexture(replaceableId, blpPath);
		}

	}

	public void setVertexColor(final Color color) {
		if (this.instance != null) {
			this.instance.setVertexColor(color);
		}
	}

	public void setModelScale(float scale) {
		if (this.instance != null) {
			this.instance.setUniformScale(scale);
		}
	}

	public void setTeamColor(int teamColorIndex) {
		if (this.instance != null) {
			this.instance.setTeamColor(teamColorIndex);
		}
	}

	public void setSequenceLoopMode(SequenceLoopMode alwaysLoop) {
		if (this.instance != null) {
			this.instance.setSequenceLoopMode(alwaysLoop);
		}
	}

	public void setSequenceEnded(boolean b) {
		if (this.instance != null) {
			this.instance.sequenceEnded = b;
		}
	}

}
