package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.jass.Jass2;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;

public class MapListBoxFrame extends ListBoxFrame {

    private DataSource dataSource;

    private List<String> mapNamesList = new ArrayList<>();
    private List<Integer> mapPlayerCountsList = new ArrayList<>();
    private List<MapTypes> mapTypeList = new ArrayList<>();

    public MapListBoxFrame(String name, UIFrame parent, Viewport viewport, DataSource dataSource) {
        super(name, parent, viewport);
        this.dataSource = dataSource;
    }

    @Override
    public void addItem(String item, GameUI gameUI, Viewport viewport) {
        try {
            final War3Map map = War3MapViewer.beginLoadingMap(dataSource, item);
            final War3MapW3i mapInfo = map.readMapInformation();
            final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
            gameUI.setMapStrings(wtsFile);

            final War3MapConfig mapConfig = new War3MapConfig(WarsmashConstants.MAX_PLAYERS);
            Jass2.loadConfig(map, viewport, gameUI.getUiScene(), gameUI, mapConfig, WarsmashConstants.JASS_FILE_LIST).config();

            String mapName = gameUI.getTrigStr(mapConfig.getMapName());
            int playablePlayers = 0;
            for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
                if(mapConfig.getPlayer(i).getController() == CMapControl.USER) {
                    playablePlayers++;
                }
            }

            MapTypes curMap;
            if (mapInfo.hasFlag(War3MapW3iFlags.MELEE_MAP)) {
                curMap = MapTypes.MELEE_MAP;
            } else {
                curMap = MapTypes.CUSTOM_MAP;
            }

            mapNamesList.add(mapName);
            mapPlayerCountsList.add(playablePlayers);
            mapTypeList.add(curMap);
            listItems.add(item);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void positionBounds(GameUI gameUI, Viewport viewport) {
        super.positionBounds(gameUI, viewport);
    }

    @Override
    protected void internalRender(SpriteBatch batch, BitmapFont baseFont, GlyphLayout glyphLayout) {
        super.internalRender(batch, baseFont, glyphLayout);
    }

    public enum MapTypes {
        MELEE_MAP,
        CUSTOM_MAP
    }
}
