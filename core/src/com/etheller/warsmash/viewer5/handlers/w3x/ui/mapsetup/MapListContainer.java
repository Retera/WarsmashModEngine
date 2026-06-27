package com.etheller.warsmash.viewer5.handlers.w3x.ui.mapsetup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame;
import com.etheller.warsmash.parsers.fdf.frames.ListBoxFrame.ListBoxSelelectionListener;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.util.ListItemEnum;

public class MapListContainer {
	private static final boolean WDT_ONLY = true;
	private final SimpleFrame mapListContainer;
	private final ListBoxFrame mapListBox;

	public MapListContainer(final GameUI rootFrame, final Viewport uiViewport, final String containerKey,
			final DataSource dataSource, final BitmapFont font) {
		this.mapListContainer = (SimpleFrame) rootFrame.getFrameByName(containerKey, 0);
		this.mapListBox = (ListBoxFrame) rootFrame.createFrameByType("LISTBOX", "MapListBox", this.mapListContainer,
				"WITHCHILDREN", 0);
		this.mapListBox.setSetAllPoints(true);
		this.mapListBox.setFrameFont(font);
		final Collection<String> listfile = dataSource.getListfile();
		final List<String> displayItemPaths = new ArrayList<>();
		for (final String file : listfile) {
			if (file.contains("/") || file.contains("\\")) {
				continue;
			}
			final String fileLower = file.toLowerCase();
			// Maps ship wrapped in an extra MPQ layer ("<name>.wdt.MPQ"); list them under
			// their logical ".wdt" name so the rest of the pipeline keys on .wdt and the
			// loader can re-append the ".MPQ" suffix to find the archive on disk.
			if (fileLower.endsWith(".wdt.mpq")) {
				displayItemPaths.add(file.substring(0, file.length() - ".MPQ".length()));
			}
			else if (((fileLower.endsWith(".w3x") || fileLower.endsWith(".w3m")) && !WDT_ONLY)
					|| fileLower.endsWith(".wdt")) {
				displayItemPaths.add(file);
			}
		}
		for (final String displayItemPath : displayItemPaths) {
			this.mapListBox.addItem(displayItemPath, ListItemEnum.ITEM_MAP, rootFrame, uiViewport);
		}
		this.mapListBox.sortItems();
		this.mapListContainer.add(this.mapListBox);
	}

	public void addSelectionListener(final ListBoxSelelectionListener listener) {
		this.mapListBox.setSelectionListener(listener);
	}

	public String getSelectedItem() {
		return this.mapListBox.getSelectedItem();
	}
}
