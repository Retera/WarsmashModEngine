package com.etheller.warsmash.viewer5.handlers;

import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.TextureMapper;

public interface BatchDescriptor {
	Batch create(Scene scene, Model model, TextureMapper textureMapper);
}
