package com.etheller.warsmash.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.AbstractRenderableFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;

public abstract class AbstractListItemDisplay {
    protected ListItemEnum dataType;
    protected SimpleFrame parentFrame;

    public AbstractListItemDisplay(ListItemEnum dataType, String name, ListBoxFrame rootList, GameUI gameUI, Viewport viewport) {
        this.dataType = dataType;

        // parentFrame = (SimpleFrame) gameUI.createSimpleFrame(null, rootList, 0);
        parentFrame = (SimpleFrame) gameUI.createFrameByType("FRAME", name, rootList, "", 0);
        parentFrame.setWidth(rootList.getRenderBounds().width - 2 * rootList.getListBoxBorder());
        parentFrame.setHeight(rootList.getFrameFont().getLineHeight());
    }

    public void setValuesFromProperty(AbstractListItemProperty itemProperty) {
        
    }

    public Boolean compareType(AbstractListItemProperty itemProperty) {
        return this.dataType == itemProperty.dataType;
    }

    public void remove(GameUI gameUI) {
        gameUI.remove(parentFrame);
    }

    public AbstractRenderableFrame getParentFrame() {
        return parentFrame;
    }

    // ===== Frame-related important functions ===== //

    public void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
        parentFrame.render(batch, baseFont, glyphLayout);
    }

    public void positionBounds(final GameUI gameUI, final Viewport viewport) {
        parentFrame.positionBounds(gameUI, viewport);
    }

    // ======== GLOBAL ========= //

    public static AbstractListItemDisplay createFromType(ListItemEnum dataType, String name, ListBoxFrame rootList, GameUI gameUI, Viewport viewport) {
        switch(dataType) {
            case ITEM_STRING:
                return new ListItemStringDisplay(rootList, name, gameUI, viewport);
            case ITEM_MAP:
                return new ListItemMapDisplay(dataType, name, rootList, gameUI, viewport);
            default:
                return null;
        }
    }
}
