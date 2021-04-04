package com.etheller.warsmash.viewer5;

import java.util.HashMap;
import java.util.Map;

public class TextureMapper {
	public final Model model;
	public final Map<Object, Texture> textures;

	public TextureMapper(final Model model) {
		this.model = model;
		this.textures = new HashMap<>();
	}

	public TextureMapper(final Model model, final Map<Object, Texture> textures) {
		this.model = model;
		this.textures = new HashMap<>(textures);
	}

	public Texture get(final Object key) {
		return this.textures.get(key);
	}
}
