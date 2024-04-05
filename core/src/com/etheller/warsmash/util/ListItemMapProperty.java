package com.etheller.warsmash.util;

import java.io.IOException;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class ListItemMapProperty extends AbstractListItemProperty {

    public final String mapName;
    public final int playerCount;
    public final MapType mapType;

    public ListItemMapProperty(ListItemEnum dataType, String rawValue, GameUI gameUI, DataSource data) {
        super(dataType, rawValue);

        try {
            final War3Map map = War3MapViewer.beginLoadingMap(data, rawValue);
            final War3MapW3i mapInfo = map.readMapInformation();
            final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
            gameUI.setMapStrings(wtsFile);

            String mapNameString = gameUI.getTrigStr(mapInfo.getName());
			mapName = mapNameString == null ? mapInfo.getName() : mapNameString;
            playerCount = mapInfo.getPlayers().size();
            mapType = (mapInfo.hasFlag(War3MapW3iFlags.MELEE_MAP)) ? MapType.MELEE_MAP : MapType.CUSTOM_MAP;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compare(AbstractListItemProperty itemProperty) {
        if (this.getItemType() != itemProperty.getItemType()) {
            throw new RuntimeException("Cannot compare with mix items");
        }
        ListItemMapProperty mapProperty = (ListItemMapProperty) itemProperty;
        if (this.playerCount == mapProperty.playerCount) {
            return this.mapName.compareTo(mapProperty.mapName);
        } else {
            return Integer.compare(this.playerCount, mapProperty.playerCount);
        }
    }
}
