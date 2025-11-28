package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.LuaEnvironment;
import com.etheller.warsmash.parsers.fdf.UIFrameLuaWrapper;
import com.etheller.warsmash.parsers.fdf.UIFrameScripts;
import com.etheller.warsmash.parsers.fdf.lua.FourArgFunction;

public class XmlCheckBoxFrame extends CheckBoxFrame {
	private final List<UIFrame> childFrames = new ArrayList<>();

	public XmlCheckBoxFrame(final String name, final UIFrame parent) {
		super(name, parent);
		setOnClick(new Runnable() {
			@Override
			public void run() {
				final UIFrameScripts scripts = getScripts();
				if (scripts != null) {
					scripts.onClick();
				}
			}
		});
	}

	public void add(final UIFrame childFrame) {
		if (childFrame == null) {
			return;
		}
		this.childFrames.add(childFrame);
	}

	public void add(final int index, final UIFrame childFrame) {
		if (childFrame == null) {
			return;
		}
		this.childFrames.add(index, childFrame);
	}

	public void remove(final UIFrame childFrame) {
		if (childFrame == null) {
			return;
		}
		this.childFrames.remove(childFrame);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		for (final UIFrame childFrame : this.childFrames) {
			childFrame.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		for (final UIFrame childFrame : this.childFrames) {
			childFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		final UIFrame superResult = super.touchDown(screenX, screenY, button);
		if (superResult != null) {
			return superResult;
		}
		if (isVisible()) {
			// we render from front to back, then back is showing as top. So we iterate back
			// to front, so that we mouse interact with something corresponding to its
			// order.
			final ListIterator<UIFrame> reverseIterator = this.childFrames.listIterator(this.childFrames.size());
			while (reverseIterator.hasPrevious()) {
				final UIFrame childFrame = reverseIterator.previous();
				final UIFrame clickedChild = childFrame.touchDown(screenX, screenY, button);
				if (clickedChild != null) {
					return clickedChild;
				}
			}
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		final UIFrame superResult = super.touchUp(screenX, screenY, button);
		if (superResult != null) {
			return superResult;
		}
		if (isVisible()) {
			// we render from front to back, then back is showing as top. So we iterate back
			// to front, so that we mouse interact with something corresponding to its
			// order.
			final ListIterator<UIFrame> reverseIterator = this.childFrames.listIterator(this.childFrames.size());
			while (reverseIterator.hasPrevious()) {
				final UIFrame childFrame = reverseIterator.previous();
				final UIFrame clickedChild = childFrame.touchUp(screenX, screenY, button);
				if (clickedChild != null) {
					return clickedChild;
				}
			}
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		final UIFrame superResult = super.getFrameChildUnderMouse(screenX, screenY);
		if (superResult != null) {
			return superResult;
		}
		if (isVisible()) {
			// we render from front to back, then back is showing as top. So we iterate back
			// to front, so that we mouse interact with something corresponding to its
			// order.
			final ListIterator<UIFrame> reverseIterator = this.childFrames.listIterator(this.childFrames.size());
			while (reverseIterator.hasPrevious()) {
				final UIFrame childFrame = reverseIterator.previous();
				final UIFrame clickedChild = childFrame.getFrameChildUnderMouse(screenX, screenY);
				if (clickedChild != null) {
					return clickedChild;
				}
			}
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}

	@Override
	protected void checkLoad() {
		super.checkLoad();
		for (final UIFrame child : this.childFrames) {
			if (child instanceof AbstractRenderableFrame) {
				((AbstractRenderableFrame) child).checkLoad();
			}
		}
	}

	@Override
	public void setupTable(final LuaTable table, final LuaEnvironment luaEnvironment,
			final UIFrameLuaWrapper luaWrapper) {
		super.setupTable(table, luaEnvironment, luaWrapper);
		table.set("SetBackdropBorderColor", new TwoArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg) {
				return LuaValue.NIL;
			}
		});
		table.set("SetBackdropColor", new FourArgFunction() {
			@Override
			public LuaValue call(final LuaValue thistable, final LuaValue arg, final LuaValue arg2,
					final LuaValue arg3) {
				final UIFrame controlBackdrop = getControlBackdrop();
				if (controlBackdrop instanceof BackdropFrame) {
					final BackdropFrame backdropFrame = (BackdropFrame) controlBackdrop;
//					backdropFrame.setColor(arg.tofloat(), arg2.tofloat(), arg3.tofloat(),
//							TextureFrame.this.color == null ? 1.0f : TextureFrame.this.color.a);
				}
				return LuaValue.NIL;
			}
		});
	}
}
