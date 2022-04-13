package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;

public class SpriteFrame extends AbstractUIFrame {

	protected final Scene scene;
	protected final Viewport uiViewport;
	private MdxComplexInstance instance;
	private float zDepth;

	public SpriteFrame(final String name, final UIFrame parent, final Scene scene, final Viewport uiViewport) {
		super(name, parent);
		this.scene = scene;
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
			this.instance.setLocation(this.renderBounds.x, this.renderBounds.y, this.zDepth);
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
		updateInstanceLocation(viewport);
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

	public void setSequence(final PrimaryTag animationName, final EnumSet<SecondaryTag> secondaryTags) {
		if (this.instance != null) {
			SequenceUtils.randomSequence(this.instance, animationName, secondaryTags, true);
		}
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
			this.instance.setLocation(GameUI.unconvertX(viewport, this.renderBounds.x),
					GameUI.unconvertY(viewport, this.renderBounds.y), this.zDepth);
			if (isVisible()) {
				this.instance.show();
			}
			else {
				this.instance.hide();
			}
		}
	}

	public boolean isSequenceEnded() {
		return this.instance.sequenceEnded;
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

}
