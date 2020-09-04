package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AbstractUIFrame extends AbstractRenderableFrame implements UIFrame {
	private final List<UIFrame> childFrames = new ArrayList<>();

	public void add(final UIFrame childFrame) {
		if (childFrame == null) {
			return;
		}
		this.childFrames.add(childFrame);
	}

	public AbstractUIFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		for (final UIFrame childFrame : this.childFrames) {
			childFrame.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	protected void innerPositionBounds(final Viewport viewport) {
		for (final UIFrame childFrame : this.childFrames) {
			childFrame.positionBounds(viewport);
		}
	}
}
