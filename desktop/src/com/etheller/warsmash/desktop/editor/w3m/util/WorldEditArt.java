package com.etheller.warsmash.desktop.editor.w3m.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.util.ImageUtils;

public class WorldEditArt {
	public static final String UNTITLED_DOODAD_ICON_PATH = "ReplaceableTextures\\WorldEditUI\\DoodadPlaceholder.blp";

	private final DataTable worldEditorData;
	private final DataSource gameDataSource;

	public WorldEditArt(final DataSource gameDataSource, final DataTable worldEditorData) {
		this.gameDataSource = gameDataSource;
		this.worldEditorData = worldEditorData;
	}

	public ImageIcon getIcon(final String iconName) {
		String iconTexturePath = worldEditorData.get("WorldEditArt").getField(iconName);
		if (!iconTexturePath.toString().endsWith(".blp")) {
			iconTexturePath += ".blp";
		}
		BufferedImage gameTex;
		try {
			gameTex = ImageUtils
					.getAnyExtensionImageFixRGB(gameDataSource, iconTexturePath, "AbstractWorldEditorPanel.getIcon")
					.getRGBCorrectImageData();
		}
		catch (final IOException e) {
			try {
				gameTex = ImageUtils.getAnyExtensionImageFixRGB(gameDataSource, UNTITLED_DOODAD_ICON_PATH,
						"AbstractWorldEditorPanel.getIcon").getRGBCorrectImageData();
			}
			catch (final IOException exc2) {
				throw new RuntimeException(exc2);
			}
		}
		return new ImageIcon(gameTex);
	}
}
