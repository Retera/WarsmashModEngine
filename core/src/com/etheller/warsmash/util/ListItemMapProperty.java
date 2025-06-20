package com.etheller.warsmash.util;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.objectdata.Warcraft3MapObjectData;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3iFlags;
import com.etheller.warsmash.units.custom.WTS;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;

public class ListItemMapProperty extends AbstractListItemProperty {

	public String mapName;
	public int playerCount;
	public MapType mapType;

	public ListItemMapProperty(final ListItemEnum dataType, final String rawValue, final GameUI gameUI,
			final DataSource data) {
		super(dataType, rawValue);

		try {
			final War3Map map = War3MapViewer.beginLoadingMap(data, rawValue);
			final War3MapW3i mapInfo = map.readMapInformation();
			final WTS wtsFile = Warcraft3MapObjectData.loadWTS(map);
			gameUI.setMapStrings(wtsFile);

			final String mapNameString = gameUI.getTrigStr(mapInfo.getName());
			this.mapName = mapNameString == null ? mapInfo.getName() : mapNameString;
			this.playerCount = mapInfo.getPlayers().size();
			this.mapType = (mapInfo.hasFlag(War3MapW3iFlags.MELEE_MAP)) ? MapType.MELEE_MAP : MapType.CUSTOM_MAP;
		}
		catch (final Exception e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
			this.mapName = rawValue;
			this.playerCount = 48;
			this.mapType = MapType.CUSTOM_MAP;
		}
	}

	@Override
	public int compare(final AbstractListItemProperty itemProperty) {
		if (getItemType() != itemProperty.getItemType()) {
			throw new RuntimeException("Cannot compare with mix items");
		}
		final ListItemMapProperty mapProperty = (ListItemMapProperty) itemProperty;
		if (this.playerCount == mapProperty.playerCount) {
			return this.mapName.compareTo(mapProperty.mapName);
		}
		else {
			return Integer.compare(this.playerCount, mapProperty.playerCount);
		}
	}
}
