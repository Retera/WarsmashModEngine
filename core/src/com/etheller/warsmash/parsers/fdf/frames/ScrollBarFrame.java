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

    public ScrollBarFrame(final String name, final UIFrame parent, boolean vertical) {
        super(name, parent);
    }

    public void setControlBackdrop(final UIFrame controlBackdrop) {
        this.controlBackdrop = controlBackdrop;
    }

    public void setIncButtonFrame(UIFrame incButtonFrame) {
        this.incButtonFrame = incButtonFrame;
        ((GlueButtonFrame)incButtonFrame).setButtonListener(new GlueButtonFrame.ButtonListener() {
            @Override
            public void mouseDown(GameUI gameUI, Viewport uiViewport) {

            }

            @Override
            public void mouseUp(GameUI gameUI, Viewport uiViewport) {
                setValue(gameUI, uiViewport, scrollValuePercent+10);
            }

            @Override
            public void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y) {

            }
        });
    }

    public void setDecButtonFrame(UIFrame decButtonFrame) {
        this.decButtonFrame = decButtonFrame;
        ((GlueButtonFrame)decButtonFrame).setButtonListener(new GlueButtonFrame.ButtonListener() {
            @Override
            public void mouseDown(GameUI gameUI, Viewport uiViewport) {

            }

            @Override
            public void mouseUp(GameUI gameUI, Viewport uiViewport) {
                setValue(gameUI, uiViewport, scrollValuePercent-10);
            }

            @Override
            public void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y) {

            }
        });
    }

    public void setThumbButtonFrame(UIFrame thumbButtonFrame) {
        if (this.thumbButtonFrame instanceof GlueButtonFrame) {
            ((GlueButtonFrame) this.thumbButtonFrame).setButtonListener(GlueButtonFrame.ButtonListener.DO_NOTHING);
        }
        this.thumbButtonFrame = thumbButtonFrame;
        if (thumbButtonFrame instanceof GlueButtonFrame) {
            GlueButtonFrame frame = (GlueButtonFrame) thumbButtonFrame;
            frame.setButtonListener(new GlueButtonFrame.ButtonListener() {
                @Override
                public void mouseDown(GameUI gameUI, Viewport uiViewport) {
                }

                @Override
                public void mouseUp(GameUI gameUI, Viewport uiViewport) {
                }

                @Override
                public void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y) {
                    ScrollBarFrame.this.mouseDragged(rootFrame, uiViewport, x, y);
                }
            });
        }
    }

    private float getMaxThumbButtonTravelDistance() {
        return renderBounds.height - thumbButtonFrame.getAssignedHeight() - incButtonFrame.getAssignedHeight() - decButtonFrame.getAssignedHeight();
    }

    public void setValue(GameUI gameUI, Viewport uiViewport, int percent) {
        this.scrollValuePercent = Math.min(100,Math.max(0,percent));
        updateThumbButtonPoint();
        changeListener.onChange(gameUI, uiViewport, this.scrollValuePercent);
        positionBounds(gameUI, uiViewport);
    }

    public int getValue() {
        return scrollValuePercent;
    }

    public void updateThumbButtonPoint() {
        float newYValue = scrollValuePercent / 100f * getMaxThumbButtonTravelDistance();
        thumbButtonFrame.addSetPoint(new SetPoint(FramePoint.BOTTOM, decButtonFrame, FramePoint.TOP, 0, newYValue));
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
            controlBackdrop.render(batch, baseFont, glyphLayout);
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
    public void mouseDragged(GameUI rootFrame, Viewport uiViewport, float x, float y) {
        float maxThumbButtonTravelDistance = getMaxThumbButtonTravelDistance();
        int newScrollValuePercent = Math.min(100,Math.max(0,(int)((y - renderBounds.y - decButtonFrame.getAssignedHeight() - thumbButtonFrame.getAssignedHeight()/2) / maxThumbButtonTravelDistance * 100)));
        if(newScrollValuePercent != scrollValuePercent) {
            setValue(rootFrame, uiViewport, newScrollValuePercent);
            positionBounds(rootFrame, uiViewport);
        }

    }

    @Override
    public UIFrame touchUp(final float screenX, final float screenY, final int button) {
        if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
            UIFrame frameChildUnderMouse = thumbButtonFrame.touchUp(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = incButtonFrame.touchUp(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = decButtonFrame.touchUp(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = controlBackdrop.touchUp(screenX, screenY, button);
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
            UIFrame frameChildUnderMouse = thumbButtonFrame.touchDown(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = incButtonFrame.touchDown(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = decButtonFrame.touchDown(screenX, screenY, button);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = controlBackdrop.touchDown(screenX, screenY, button);
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
            UIFrame frameChildUnderMouse = thumbButtonFrame.getFrameChildUnderMouse(screenX, screenY);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = incButtonFrame.getFrameChildUnderMouse(screenX, screenY);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = decButtonFrame.getFrameChildUnderMouse(screenX, screenY);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            frameChildUnderMouse = controlBackdrop.getFrameChildUnderMouse(screenX, screenY);
            if (frameChildUnderMouse != null) {
                return frameChildUnderMouse;
            }
            return this;
        }
        return super.getFrameChildUnderMouse(screenX, screenY);
    }

    public void setChangeListener(ScrollBarChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface ScrollBarChangeListener {
        void onChange(GameUI gameUI, Viewport uiViewport, int newValue);

        ScrollBarChangeListener DO_NOTHING = new ScrollBarChangeListener() {
            @Override
            public void onChange(GameUI gameUI, Viewport uiViewport, int newValue) {

            }
        };
    }
}
