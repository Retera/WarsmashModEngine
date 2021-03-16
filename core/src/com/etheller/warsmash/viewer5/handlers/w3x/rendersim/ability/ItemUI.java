package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

public class ItemUI {
	private final IconUI iconUI;
	private final String name;
	private final String description;
	private final String itemIconPathForDragging;

	public ItemUI(final IconUI iconUI, final String name, final String description,
			final String itemIconPathForDragging) {
		this.iconUI = iconUI;
		this.name = name;
		this.description = description;
		this.itemIconPathForDragging = itemIconPathForDragging;
	}

	public IconUI getIconUI() {
		return this.iconUI;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getItemIconPathForDragging() {
		return this.itemIconPathForDragging;
	}
}
