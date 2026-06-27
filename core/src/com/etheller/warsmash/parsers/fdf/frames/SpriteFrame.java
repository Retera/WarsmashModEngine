package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.parsers.fdf.UIFrameScripts;
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
	private boolean lastSequenceEnded;

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

	public void setFrameWithinSequence(final int animationFrame) {
		if (this.instance != null) {
			this.instance.setFrameWithinSequence(animationFrame);
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

	public void setModelScale(final float scale) {
		if (this.instance != null) {
			this.instance.setUniformScale(scale);
		}
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("SetScale", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue scaleValue) {
				setModelScale(scaleValue.tofloat());
				return LuaValue.NIL;
			}
		});
		table.set("SetSequence", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue sequenceIndex) {
				final int index = sequenceIndex.checkint();
				// A script-driven model (the cooldown swipe) is re-armed every poll via
				// CooldownFrame_SetTimer's SetSequence(0). Snapping it back to frame 0 here
				// would show the reset for one frame before OnUpdateModel re-poses it, which
				// reads as a flicker. OnUpdateModel sets the exact pose each frame anyway, so
				// keep the current pose when the sequence is unchanged.
				if (isModelScriptDriven() && (SpriteFrame.this.instance != null)
						&& (SpriteFrame.this.instance.sequence == index)) {
					return LuaValue.NIL;
				}
				setSequence(index);
				return LuaValue.NIL;
			}
		});
		table.set("SetSequenceTime", new ThreeArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue sequenceIndex, final LuaValue time) {
				final int index = sequenceIndex.checkint();
				// Only (re)start the sequence when it actually changes; re-starting the same
				// sequence each frame would needlessly snap to frame 0 before the ratio is
				// re-applied below.
				if ((SpriteFrame.this.instance == null) || (SpriteFrame.this.instance.sequence != index)) {
					setSequence(index);
				}
				// WoW passes the time as (fractionCompleted * 1000) -- see Cooldown.lua. Map
				// it proportionally onto the sequence so the swipe is correct regardless of
				// the model's actual sequence length, mirroring the working command card
				// cooldown which positions by ratio.
				final float ratio = Math.max(0f, Math.min(1f, time.tofloat() / 1000.0f));
				setFrameByRatio(ratio);
				return LuaValue.NIL;
			}
		});
		table.set("AdvanceTime", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				// Hand control of the playhead back to the scene so the finish ("stopping")
				// animation plays out on its own; OnAnimFinished then hides the frame.
				if (SpriteFrame.this.instance != null) {
					SpriteFrame.this.instance.setAnimationSpeed(1.0f);
				}
				return LuaValue.NIL;
			}
		});
	}

	private boolean isModelScriptDriven() {
		final UIFrameScripts scripts = getScripts();
		return (scripts != null) && (scripts.OnUpdateModel != null);
	}

	@Override
	protected void prepareModelScriptUpdate() {
		if (this.instance != null) {
			// While the OnUpdateModel script drives the model (setting the swipe pose every
			// frame), stop the scene from auto-advancing it and from looping, so the
			// Lua-set pose is authoritative. AdvanceTime() re-enables stepping when the
			// script wants the finish animation to play.
			this.instance.setAnimationSpeed(0f);
			this.instance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
		}
	}

	@Override
	protected boolean pollSequenceJustEnded() {
		if (this.instance == null) {
			return false;
		}
		final boolean ended = this.instance.sequenceEnded;
		final boolean justEnded = ended && !this.lastSequenceEnded;
		this.lastSequenceEnded = ended;
		return justEnded;
	}

}
