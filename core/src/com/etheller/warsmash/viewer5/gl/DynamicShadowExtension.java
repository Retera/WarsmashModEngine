package com.etheller.warsmash.viewer5.gl;

public interface DynamicShadowExtension {
	void glFramebufferTexture(int target, int attachment, int texture, int level);

	void glDrawBuffer(int mode);
}
