package com.etheller.warsmash.parsers.fdf;

import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;

public class GameSkin {
	private final Element skin;
	private final DataTable skinsTable;

	public GameSkin(final Element skin, final DataTable skinsTable) {
		this.skin = skin;
		this.skinsTable = skinsTable;
	}

	public Element getSkin() {
		return this.skin;
	}

	public DataTable getSkinsTable() {
		return this.skinsTable;
	}
}
