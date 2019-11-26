package com.etheller.warsmash.viewer5.handlers;

import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelInstance;

public interface ModelInstanceDescriptor {
	ModelInstance create(Model model);
}
