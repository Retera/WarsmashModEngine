package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;

public class ListBoxFrame extends ControlFrame {
	private final List<String> listItems = new ArrayList<>();
	private final List<SingleStringFrame> stringFrames = new ArrayList<>();
	private BitmapFont frameFont;
	private float listBoxBorder;

	public ListBoxFrame(final String name, final UIFrame parent, final Viewport viewport) {
		super(name, parent);
		this.listBoxBorder = GameUI.convertX(viewport, 0.01f);
	}

	public void setListBoxBorder(final float listBoxBorder) {
		this.listBoxBorder = listBoxBorder;
	}

	public float getListBoxBorder() {
		return this.listBoxBorder;
	}

	public void setFrameFont(final BitmapFont frameFont) {
		this.frameFont = frameFont;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		updateUI(gameUI, viewport);
	}

	private void positionChildren(final GameUI gameUI, final Viewport viewport) {
		for (final SingleStringFrame frame : this.stringFrames) {
			frame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		for (final SingleStringFrame frame : this.stringFrames) {
			frame.render(batch, baseFont, glyphLayout);
		}
	}

	public void addItem(final String item, final GameUI gameUI, final Viewport viewport) {
		this.listItems.add(item);
//		updateUI(gameUI, viewport);
	}

	public void setItems(final List<String> items, final GameUI gameUI, final Viewport viewport) {
		this.listItems.clear();
		this.listItems.addAll(items);
//		updateUI(gameUI, viewport);
	}

	public void removeItem(final String item, final GameUI gameUI, final Viewport viewport) {
		this.listItems.remove(item);
//		updateUI(gameUI, viewport);
	}

	public void removeItem(final int index, final GameUI gameUI, final Viewport viewport) {
		this.listItems.remove(index);
//		updateUI(gameUI, viewport);
	}

	private void updateUI(final GameUI gameUI, final Viewport viewport) {
		this.stringFrames.clear();
		SingleStringFrame prev = null;
		int i = 0;
		for (final String string : this.listItems) {
			final SingleStringFrame stringFrame = new SingleStringFrame("LISTY" + i++, this, Color.WHITE,
					TextJustify.LEFT, TextJustify.MIDDLE, this.frameFont);
			stringFrame.setText(string);
			stringFrame.setWidth(this.renderBounds.width);
			stringFrame.setHeight(this.frameFont.getLineHeight());
			if (prev != null) {
				stringFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, prev, FramePoint.BOTTOMLEFT, 0, 0));
			}
			else {
				stringFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, this.listBoxBorder,
						-this.listBoxBorder));
			}
			this.stringFrames.add(stringFrame);
			prev = stringFrame;
		}
		positionChildren(gameUI, viewport);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {

			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}
}
