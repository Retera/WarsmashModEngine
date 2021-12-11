package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.w3x.War3Map;
import com.etheller.warsmash.parsers.w3x.w3i.War3MapW3i;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.StandardObjectData;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.config.War3MapConfig;

public class MapInfoPane {
	private final UIFrame mapInfoPaneFrame;
	private final StringFrame maxPlayersValue;
	private final StringFrame mapNameValue;
	private final UIFrame authIconFrame;
	private final TextureFrame minimapImageTextureFrame;
	private final StringFrame suggestedPlayersValue;
	private final StringFrame mapSizeValue;
	private final StringFrame mapDescValue;
	private final StringFrame mapTilesetValue;

	public MapInfoPane(final GameUI rootFrame, final Viewport uiViewport, final SimpleFrame container) {
		this.mapInfoPaneFrame = rootFrame.createFrame("MapInfoPane", container, 0, 0);
		this.mapInfoPaneFrame.setSetAllPoints(true);
		container.add(this.mapInfoPaneFrame);

		final UIFrame maxPlayersIcon = rootFrame.getFrameByName("MaxPlayersIcon", 0);
		this.maxPlayersValue = (StringFrame) rootFrame.getFrameByName("MaxPlayersValue", 0);
		this.suggestedPlayersValue = (StringFrame) rootFrame.getFrameByName("SuggestedPlayersValue", 0);
		this.mapSizeValue = (StringFrame) rootFrame.getFrameByName("MapSizeValue", 0);
		this.mapDescValue = (StringFrame) rootFrame.getFrameByName("MapDescValue", 0);
		this.mapNameValue = (StringFrame) rootFrame.getFrameByName("MapNameValue", 0);
		this.mapTilesetValue = (StringFrame) rootFrame.getFrameByName("MapTilesetValue", 0);
		this.authIconFrame = rootFrame.getFrameByName("AuthIcon", 0);
		final UIFrame minimapImageFrame = rootFrame.getFrameByName("MinimapImage", 0);
		this.minimapImageTextureFrame = rootFrame.createTextureFrame("MinimapImageTexture", minimapImageFrame, false,
				TextureFrame.DEFAULT_TEX_COORDS);
		rootFrame.remove(this.minimapImageTextureFrame);
		((SimpleFrame) this.mapInfoPaneFrame).add(this.minimapImageTextureFrame);
		this.minimapImageTextureFrame.setSetAllPoints(true);

		final UIFrame suggestedPlayersLabel = rootFrame.getFrameByName("SuggestedPlayersLabel", 0);
		final UIFrame mapSizeLabel = rootFrame.getFrameByName("MapSizeLabel", 0);
		final UIFrame mapTilesetLabel = rootFrame.getFrameByName("MapTilesetLabel", 0);
		final UIFrame mapDescLabel = rootFrame.getFrameByName("MapDescLabel", 0);

		// TODO there might be some kind of layout manager system that is supposed to do
		// the below anchoring automatically but I don't have that atm
		maxPlayersIcon.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.mapInfoPaneFrame, FramePoint.TOPLEFT, 0, 0));

		minimapImageFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, maxPlayersIcon, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(uiViewport, -0.01f)));
		suggestedPlayersLabel.addSetPoint(new SetPoint(FramePoint.TOPLEFT, minimapImageFrame, FramePoint.BOTTOMLEFT, 0,
				GameUI.convertY(uiViewport, -0.01f)));
		this.suggestedPlayersValue
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, suggestedPlayersLabel, FramePoint.TOPRIGHT, 0, 0));
		mapSizeLabel.addSetPoint(new SetPoint(FramePoint.TOPLEFT, suggestedPlayersLabel, FramePoint.BOTTOMLEFT, 0, 0));
		this.mapSizeValue.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapSizeLabel, FramePoint.TOPRIGHT, 0, 0));
		mapTilesetLabel.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapSizeLabel, FramePoint.BOTTOMLEFT, 0, 0));
		this.mapTilesetValue.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTilesetLabel, FramePoint.TOPRIGHT, 0, 0));
		mapDescLabel.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapTilesetLabel, FramePoint.BOTTOMLEFT, 0, 0));
		this.mapDescValue.clearFramePointAssignments();
		this.mapDescValue.addSetPoint(new SetPoint(FramePoint.TOPLEFT, mapDescLabel, FramePoint.BOTTOMLEFT, 0, 0));
		this.mapDescValue.setWidth(GameUI.convertX(uiViewport, 0.23f));

		this.authIconFrame.setVisible(false);
	}

	public void setMap(final GameUI rootFrame, final Viewport uiViewport, final War3Map map, final War3MapW3i mapInfo,
			final War3MapConfig war3MapConfig) throws IOException {
		rootFrame.setText(this.mapNameValue, rootFrame.getTrigStr(war3MapConfig.getMapName()));
		rootFrame.setText(this.maxPlayersValue, Integer.toString(mapInfo.getPlayers().size()));
		rootFrame.setText(this.suggestedPlayersValue, rootFrame.getTrigStr(mapInfo.getRecommendedPlayers()));
		rootFrame.setText(this.mapDescValue, rootFrame.getTrigStr(war3MapConfig.getMapDescription()));
		rootFrame.setText(this.mapSizeValue, rootFrame.getTrigStr(Integer.toString(mapInfo.getPlayableSize()[0])));

		final StandardObjectData standardObjectData = new StandardObjectData(map);
		final DataTable worldEditData = standardObjectData.getWorldEditData();
		final Element tilesets = worldEditData.get("TileSets");
		final String tileSetNameKey = tilesets.getField(Character.toString(mapInfo.getTileset()), 0);
		final String tileSetNameString = worldEditData.getLocalizedString(tileSetNameKey);
		rootFrame.setText(this.mapTilesetValue, rootFrame.getTrigStr(tileSetNameString));

		Texture minimapTexture;
		try {
			minimapTexture = ImageUtils.getAnyExtensionTexture(map, "war3mapPreview.blp");
		}
		catch (final Exception exc) {
			minimapTexture = ImageUtils.getAnyExtensionTexture(map, "war3mapMap.blp");
		}
		this.minimapImageTextureFrame.setTexture(minimapTexture);

		this.mapInfoPaneFrame.positionBounds(rootFrame, uiViewport);
	}

}
