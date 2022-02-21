package com.etheller.warsmash.parsers.fdf.frames;

import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class ClickConsumingTextureFrame extends TextureFrame {

	public ClickConsumingTextureFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final Vector4Definition texCoord) {
		super(name, parent, decorateFileNames, texCoord);
	}

	@Override
	public UIFrame touchUp(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchUp(screenX, screenY, button);
	}

	@Override
	public UIFrame touchDown(final float screenX, final float screenY, final int button) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.touchDown(screenX, screenY, button);
	}

	@Override
	public UIFrame getFrameChildUnderMouse(final float screenX, final float screenY) {
		if (isVisible() && this.renderBounds.contains(screenX, screenY)) {
			return this;
		}
		return super.getFrameChildUnderMouse(screenX, screenY);
	}
}
