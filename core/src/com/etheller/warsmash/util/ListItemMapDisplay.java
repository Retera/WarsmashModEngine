package com.etheller.warsmash.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.frames.BackdropFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SingleStringFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;

public class ListItemMapDisplay extends AbstractListItemDisplay {

    private final BackdropFrame mapBackdrop;
    private final SingleStringFrame mapName;
    private final SingleStringFrame mapPlayerCount;

    private final GameUI gameUI;

    public ListItemMapDisplay(ListItemEnum dataType, String name, ListBoxFrame rootList, GameUI gameUI, Viewport viewport) {
        super(dataType, name, rootList, gameUI, viewport);
        final float mapIconSize = (float)Math.floor(GameUI.convertY(viewport, 0.018f));
        final BitmapFont refFont = ((StringFrame)gameUI.getFrameByName("MaxPlayersValue", 0)).getFrameFont();
        this.gameUI = gameUI;

        mapBackdrop = (BackdropFrame) gameUI.createFrameByType("BACKDROP", name + "_BACKDROP", parentFrame, "", 0);
        mapName = new SingleStringFrame(name + "_NAME", parentFrame, Color.WHITE, TextJustify.LEFT, TextJustify.MIDDLE, rootList.getFrameFont());
        mapPlayerCount = new SingleStringFrame(name + "_COUNT", parentFrame, Color.YELLOW, TextJustify.CENTER, TextJustify.MIDDLE, refFont);

        mapBackdrop.setHeight(mapIconSize);
        mapBackdrop.setWidth(mapIconSize);
        parentFrame.setHeight(mapIconSize);

        mapBackdrop.addSetPoint(new SetPoint(FramePoint.LEFT, parentFrame, FramePoint.LEFT, 0, 0));
        mapPlayerCount.addSetPoint(new SetPoint(FramePoint.CENTER, mapBackdrop, FramePoint.CENTER, 0, 0));
        mapName.addSetPoint(new SetPoint(FramePoint.LEFT, mapBackdrop, FramePoint.RIGHT, 0, 0));

        parentFrame.add(mapBackdrop);
        parentFrame.add(mapPlayerCount);
        parentFrame.add(mapName);
    }

    @Override
    public void remove(GameUI gameUI) {
        super.remove(gameUI);
        gameUI.remove(mapName);
        gameUI.remove(mapPlayerCount);
        gameUI.remove(mapBackdrop);
    }
    
    @Override
    public void setValuesFromProperty(AbstractListItemProperty itemProperty) {
        if(!compareType(itemProperty)) return;

        ListItemMapProperty mapProperty = (ListItemMapProperty) itemProperty;

        mapName.setText(mapProperty.mapName);
        mapPlayerCount.setText(Integer.toString(mapProperty.playerCount));
        if (mapProperty.mapType == MapType.MELEE_MAP) {
            mapBackdrop.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-melee.blp"));
        } else {
            mapBackdrop.setBackground(gameUI.loadTexture("ui\\widgets\\glues\\icon-file-ums.blp"));
        }
    }
}
