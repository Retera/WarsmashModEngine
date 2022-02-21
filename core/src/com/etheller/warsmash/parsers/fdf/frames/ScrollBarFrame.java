package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableFrame;

public class ScrollBarFrame extends AbstractRenderableFrame implements ClickableFrame {
	private UIFrame controlBackdrop;
	private UIFrame incButtonFrame;
	private UIFrame decButtonFrame;
	private UIFrame thumbButtonFrame;
	private int scrollValuePercent = 50;
	private ScrollBarChangeListener changeListener = ScrollBarChangeListener.DO_NOTHING;

	public ScrollBarFrame(final String name, final UIFrame parent, final boolean vertical) {
		super(name, parent);
	}

	public void setControlBackdrop(final UIFrame controlBackdrop) {
		this.controlBackdrop = controlBackdrop;
	}

	public void setIncButtonFrame(final UIFrame incButtonFrame) {
		this.incButtonFrame = incButtonFrame;
		((GlueButtonFrame) incButtonFrame).setButtonListener(new GlueButtonFrame.ButtonListener() {
			@Override
			public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {

			}

			@Override
			public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
				setValue(gameUI, uiViewport, ScrollBarFrame.this.scrollValuePercent + 10);
			}

			@Override
			public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {

			}
		});
	}

	public void setDecButtonFrame(final UIFrame decButtonFrame) {
		this.decButtonFrame = decButtonFrame;
		((GlueButtonFrame) decButtonFrame).setButtonListener(new GlueButtonFrame.ButtonListener() {
			@Override
			public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {

			}

			@Override
			public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
				setValue(gameUI, uiViewport, ScrollBarFrame.this.scrollValuePercent - 10);
			}

			@Override
			public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {

			}
		});
	}

	public void setThumbButtonFrame(final UIFrame thumbButtonFrame) {
		if (this.thumbButtonFrame instanceof GlueButtonFrame) {
			((GlueButtonFrame) this.thumbButtonFrame).setButtonListener(GlueButtonFrame.ButtonListener.DO_NOTHING);
		}
		this.thumbButtonFrame = thumbButtonFrame;
		if (thumbButtonFrame instanceof GlueButtonFrame) {
			final GlueButtonFrame frame = (GlueButtonFrame) thumbButtonFrame;
			frame.setButtonListener(new GlueButtonFrame.ButtonListener() {
				@Override
				public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
				}

				@Override
				public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
				}

				@Override
				public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x,
						final float y) {
					ScrollBarFrame.this.mouseDragged(rootFrame, uiViewport, x, y);
				}
			});
		}
	}

	private float getMaxThumbButtonTravelDistance() {
		final float incButtonFrameHeight = this.incButtonFrame == null ? 0 : this.incButtonFrame.getAssignedHeight();
		final float decButtonFrameHeight = this.decButtonFrame == null ? 0 : this.decButtonFrame.getAssignedHeight();
		return this.renderBounds.height - this.thumbButtonFrame.getAssignedHeight() - incButtonFrameHeight
				- decButtonFrameHeight;
	}

	public void setValue(final GameUI gameUI, final Viewport uiViewport, final int percent) {
		this.scrollValuePercent = Math.min(100, Math.max(0, percent));
		updateThumbButtonPoint();
		this.changeListener.onChange(gameUI, uiViewport, this.scrollValuePercent);
		positionBounds(gameUI, uiViewport);
	}

	public int getValue() {
		return this.scrollValuePercent;
	}

	public void updateThumbButtonPoint() {
		final float newYValue = (this.scrollValuePercent / 100f) * getMaxThumbButtonTravelDistance();
		this.thumbButtonFrame
				.addSetPoint(new SetPoint(FramePoint.BOTTOM, this.decButtonFrame, FramePoint.TOP, 0, newYValue));
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		if (this.controlBackdrop != null) {
			this.controlBackdrop.positionBounds(gameUI, viewport);
		}
		if (this.incButtonFrame != null) {
			this.incButtonFrame.positionBounds(gameUI, viewport);
		}
		if (this.decButtonFrame != null) {
			this.decButtonFrame.positionBounds(gameUI, viewport);
		}
		updateThumbButtonPoint();
		if (this.thumbButtonFrame != null) {
			this.thumbButtonFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		if (this.controlBackdrop != null) {
			this.controlBackdrop.render(batch, baseFont, glyphLayout);
		}
		if (this.incButtonFrame != null) {
			this.incButtonFrame.render(batch, baseFont, glyphLayout);
		}
		if (this.decButtonFrame != null) {
			this.decButtonFrame.render(batch, baseFont, glyphLayout);
		}
		if (this.thumbButtonFrame != null) {
			this.thumbButtonFrame.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public void mouseDown(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseEnter(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void mouseExit(final GameUI gameUI, final Viewport uiViewport) {
	}

	@Override
	public void onClick(final int button) {
	}

	@Override
	public void mouseDragged(final GameUI rootFrame, final Viewport uiViewport, final float x, final float y) {
		final float maxThumbButtonTravelDistance = getMaxThumbButtonTravelDistance();
		final int newScrollValuePercent = Math.min(100,
				Math.max(0,
						(int) (((y - this.renderBounds.y - this.decButtonFrame.getAssignedHeight()
								- (this.thumbButtonFrame.getAssignedHeight() / 2)) / maxThumbButtonTravelDistance)
								* 100)));
		if (newScrollValuePercent != this.scrollValuePercent) {
			setValue(rootFrame, uiViewport, newScrollValuePercent);
			positionBounds(rootFrame, uiViewport);
		}

	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			UIFrame frameChildUnderMouse = this.thumbButtonFrame.touchUp(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.incButtonFrame.touchUp(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.decButtonFrame.touchUp(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.controlBackdrop.touchUp(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			return this;
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			UIFrame frameChildUnderMouse = this.thumbButtonFrame.touchDown(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.incButtonFrame.touchDown(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.decButtonFrame.touchDown(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.controlBackdrop.touchDown(screenX, screenY, button);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			UIFrame frameChildUnderMouse = this.thumbButtonFrame.getFrameChildUnderMouse(screenX, screenY);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.incButtonFrame.getFrameChildUnderMouse(screenX, screenY);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.decButtonFrame.getFrameChildUnderMouse(screenX, screenY);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			frameChildUnderMouse = this.controlBackdrop.getFrameChildUnderMouse(screenX, screenY);
			if (frameChildUnderMouse != null) {
				return frameChildUnderMouse;
			}
			return this;
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}

	public void setChangeListener(final ScrollBarChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	public interface ScrollBarChangeListener {
		void onChange(GameUI gameUI, Viewport uiViewport, int newValue);

		ScrollBarChangeListener DO_NOTHING = new ScrollBarChangeListener() {
			@Override
			public void onChange(final GameUI gameUI, final Viewport uiViewport, final int newValue) {

			}
		};
	}
}
