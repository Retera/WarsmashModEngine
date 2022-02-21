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

public class TextAreaFrame extends ControlFrame implements ScrollBarFrame.ScrollBarChangeListener {
	// TODO where are these colors in the UI definition files?
	private final List<String> listItems = new ArrayList<>();
	private final List<StringFrame> stringFrames = new ArrayList<>();
	private BitmapFont frameFont;
	private float lineHeight;
	private float lineGap;
	private float inset;
	private int maxLines;

	private GameUI gameUI;
	private Viewport viewport;
	private ScrollBarFrame scrollBarFrame;

	public TextAreaFrame(final String name, final UIFrame parent, final Viewport viewport) {
		super(name, parent);
	}

	public void setLineGap(final float lineGap) {
		this.lineGap = lineGap;
	}

	public void setLineHeight(final float lineHeight) {
		this.lineHeight = lineHeight;
	}

	public void setInset(final float inset) {
		this.inset = inset;
	}

	public void setMaxLines(final int maxLines) {
		this.maxLines = maxLines;
	}

	public void setScrollBarFrame(final ScrollBarFrame scrollBarFrame) {
		this.scrollBarFrame = scrollBarFrame;
		// TODO might be a better place to add these set points, but we definitely need
		// them
		scrollBarFrame
				.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this, FramePoint.TOPRIGHT, -this.inset, -this.inset));
		scrollBarFrame.addSetPoint(
				new SetPoint(FramePoint.BOTTOMRIGHT, this, FramePoint.BOTTOMRIGHT, -this.inset, this.inset));
		scrollBarFrame.setChangeListener(this);
	}

	public ScrollBarFrame getScrollBarFrame() {
		return this.scrollBarFrame;
	}

	public void setFrameFont(final BitmapFont frameFont) {
		this.frameFont = frameFont;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.gameUI = gameUI;
		this.viewport = viewport;
		super.innerPositionBounds(gameUI, viewport);
		updateUI(gameUI, viewport);
	}

	private void positionChildren(final GameUI gameUI, final Viewport viewport) {
		for (final StringFrame frame : this.stringFrames) {
			frame.positionBounds(gameUI, viewport);
		}
		if (this.scrollBarFrame != null) {
			this.scrollBarFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		for (final StringFrame frame : this.stringFrames) {
			frame.render(batch, baseFont, glyphLayout);
		}
		if (this.scrollBarFrame != null) {
			this.scrollBarFrame.render(batch, baseFont, glyphLayout);
		}
	}

	public void addItem(final String item, final GameUI gameUI, final Viewport viewport) {
		this.listItems.add(item);
		while (this.listItems.size() > this.maxLines) {
			// TODO this is not performant
			this.listItems.remove(0);
		}
		updateUI(gameUI, viewport);
	}

	public void setItems(final List<String> items, final GameUI gameUI, final Viewport viewport) {
		this.listItems.clear();
		this.listItems.addAll(items);
		updateUI(gameUI, viewport);
	}

	public void removeItem(final String item, final GameUI gameUI, final Viewport viewport) {
		this.listItems.remove(item);
		updateUI(gameUI, viewport);
	}

	public void removeItem(final int index, final GameUI gameUI, final Viewport viewport) {
		this.listItems.remove(index);
		updateUI(gameUI, viewport);
	}

	private void updateUI(final GameUI gameUI, final Viewport viewport) {
		StringFrame prev = null;
		final int numStringFrames = (int) (Math
				.floor((this.renderBounds.height - (this.inset * 2)) / (this.lineHeight + this.lineGap)));
		final int scrollOffset = computeScrollOffset(numStringFrames);
		if (numStringFrames != this.stringFrames.size()) {
			this.stringFrames.clear();
			for (int stringFrameIndex = 0; stringFrameIndex < numStringFrames; stringFrameIndex++) {
				final int index = stringFrameIndex + scrollOffset;
				final StringFrame stringFrame = new StringFrame("LISTY" + index, this, Color.WHITE, TextJustify.LEFT,
						TextJustify.MIDDLE, this.frameFont, null, null, null);
				if (index < this.listItems.size()) {
					gameUI.setText(stringFrame, this.listItems.get(index));
				}
				else {
					gameUI.setText(stringFrame, "");
				}
				stringFrame.setWidth(this.renderBounds.width - (this.inset * 2));
				stringFrame.setHeight(this.lineHeight);
				if (prev != null) {
					stringFrame.addSetPoint(
							new SetPoint(FramePoint.TOPLEFT, prev, FramePoint.BOTTOMLEFT, 0, -this.lineGap));
				}
				else {
					stringFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, this.inset,
							-this.inset - this.lineGap));
				}
				this.stringFrames.add(stringFrame);
				prev = stringFrame;
			}
		}
		else {
			for (int stringFrameIndex = 0; stringFrameIndex < numStringFrames; stringFrameIndex++) {
				final int index = stringFrameIndex + scrollOffset;
				final StringFrame stringFrame = this.stringFrames.get(stringFrameIndex);
				if (index < this.listItems.size()) {
					gameUI.setText(stringFrame, this.listItems.get(index));
				}
				else {
					gameUI.setText(stringFrame, "");
				}
			}
		}
		this.scrollBarFrame.setVisible(this.listItems.size() > numStringFrames);
		positionChildren(gameUI, viewport);
	}

	private int computeScrollOffset(final int numStringFrames) {
		int scrollOffset;
		if ((this.scrollBarFrame != null) && (this.listItems.size() > numStringFrames)) {
			scrollOffset = (int) Math
					.ceil(((100 - this.scrollBarFrame.getValue()) / 100f) * (this.listItems.size() - numStringFrames));
		}
		else {
			scrollOffset = 0;
		}
		return scrollOffset;
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.touchDown(screenX, screenY, button);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.touchDown(screenX, screenY, button);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			if (this.scrollBarFrame != null) {
				final UIFrame sliderFrameChildUnderMouse = this.scrollBarFrame.getFrameChildUnderMouse(screenX,
						screenY);
				if (sliderFrameChildUnderMouse != null) {
					return sliderFrameChildUnderMouse;
				}
			}
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}

	@Override
	public void onChange(final GameUI gameUI, final Viewport uiViewport, final int newValue) {
		updateUI(gameUI, uiViewport);
	}
}
