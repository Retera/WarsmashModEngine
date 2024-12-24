package com.etheller.warsmash.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SingleStringFrame;

public class ListItemStringDisplay extends AbstractListItemDisplay {

    private SingleStringFrame stringFrame;

    public ListItemStringDisplay(ListBoxFrame rootList, String name, GameUI gameUI, Viewport viewport) {
        super(ListItemEnum.ITEM_STRING, name, rootList, gameUI, viewport);
        
        stringFrame = new SingleStringFrame(null, parentFrame, Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, rootList.getFrameFont());
        stringFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, super.parentFrame, FramePoint.LEFT, 0, 0));
        parentFrame.add(stringFrame);
    }

    @Override
    public void remove(GameUI gameUI) {
        super.remove(gameUI);
        gameUI.remove(stringFrame);
    }

    @Override
    public void setValuesFromProperty(AbstractListItemProperty itemProperty) {
        if (!this.compareType(itemProperty)) return;

        String val = ((ListItemStringProperty) itemProperty).getRawValue();
        stringFrame.setText(val); 
    }
    
}
