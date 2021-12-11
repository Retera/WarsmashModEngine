package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;

public class PopupMenuFrame extends GlueTextButtonFrame {
	private UIFrame popupArrowFrame;
	private UIFrame popupMenuFrame;

	public PopupMenuFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public UIFrame getPopupTitleFrame() {
		return getButtonText();
	}

	public void setPopupArrowFrame(final UIFrame popupArrowFrame) {
		this.popupArrowFrame = popupArrowFrame;
	}

	public void setPopupMenuFrame(final UIFrame popupMenuFrame) {
		this.popupMenuFrame = popupMenuFrame;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		if (this.popupArrowFrame != null) {
			this.popupArrowFrame.positionBounds(gameUI, viewport);
		}
		if (this.popupMenuFrame != null) {
			this.popupMenuFrame.positionBounds(gameUI, viewport);
		}
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		if (this.popupArrowFrame != null) {
			this.popupArrowFrame.render(batch, baseFont, glyphLayout);
		}
	}

}
