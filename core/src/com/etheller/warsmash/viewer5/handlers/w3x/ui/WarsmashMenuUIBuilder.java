package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.networking.uberserver.GamingNetworkConnectionImpl;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxViewer;

public interface WarsmashMenuUIBuilder {

	WarsmashMenuUI buildMenuUI(DataSource dataSource, Viewport uiViewport, Scene uiScene, MdxViewer viewer,
			WarsmashGdxMultiScreenGame game, WarsmashGdxMenuScreen warsmashGdxMenuScreen, DataTable warsmashIni,
			RootFrameListener rootFrameListener, GamingNetworkConnectionImpl gamingNetworkConnectionImpl,
			String mapDownloadDir);

	WarsmashMenuUIBuilder DEFAULT = new WarsmashMenuUIBuilder() {
		@Override
		public WarsmashMenuUI buildMenuUI(DataSource dataSource, Viewport uiViewport, Scene uiScene, MdxViewer viewer,
				WarsmashGdxMultiScreenGame game, WarsmashGdxMenuScreen warsmashGdxMenuScreen, DataTable warsmashIni,
				RootFrameListener rootFrameListener, GamingNetworkConnectionImpl gamingNetworkConnectionImpl,
				String mapDownloadDir) {
			return new MenuUI(dataSource, uiViewport, uiScene, viewer, game, warsmashGdxMenuScreen, warsmashIni,
					rootFrameListener, gamingNetworkConnectionImpl, mapDownloadDir);
		}
	};

	WarsmashMenuUIBuilder SIMPLE_GAME = new WarsmashMenuUIBuilder() {
		@Override
		public WarsmashMenuUI buildMenuUI(DataSource dataSource, Viewport uiViewport, Scene uiScene, MdxViewer viewer,
				WarsmashGdxMultiScreenGame game, WarsmashGdxMenuScreen warsmashGdxMenuScreen, DataTable warsmashIni,
				RootFrameListener rootFrameListener, GamingNetworkConnectionImpl gamingNetworkConnectionImpl,
				String mapDownloadDir) {
			return new MenuUISimpleGame(dataSource, uiViewport, uiScene, viewer, game, warsmashGdxMenuScreen,
					warsmashIni, rootFrameListener, gamingNetworkConnectionImpl, mapDownloadDir);
		}
	};
}
