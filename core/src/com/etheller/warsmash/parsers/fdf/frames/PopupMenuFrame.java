package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.frames.MenuFrame.MenuClickListener;

public class PopupMenuFrame extends GlueTextButtonFrame {
	private UIFrame popupArrowFrame;
	private UIFrame popupMenuFrame;
	private MenuClickListener menuClickListener;

	public PopupMenuFrame(final String name, final UIFrame parent) {
		super(name, parent);
		setOnClick(new Runnable() {
			@Override
			public void run() {
				if (PopupMenuFrame.this.popupMenuFrame != null) {
					PopupMenuFrame.this.popupMenuFrame.setVisible(!PopupMenuFrame.this.popupMenuFrame.isVisible());
				}
			}
		});
	}

	public UIFrame getPopupTitleFrame() {
		return getButtonText();
	}

	public void setPopupArrowFrame(final UIFrame popupArrowFrame) {
		this.popupArrowFrame = popupArrowFrame;
	}

	public void setPopupMenuFrame(final UIFrame popupMenuFrame) {
		this.popupMenuFrame = popupMenuFrame;
		popupMenuFrame.setVisible(false);
	}

	public UIFrame getPopupMenuFrame() {
		return this.popupMenuFrame;
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

	// TODO We dont do rendering or other handling of the popup menu frame because
	// it is spawned as a child of GameUI. Calling position bounds on it
	// is a hack because I do not yet have a system of anchor dependencies,
	// i.e. otherwise it will get "positionBounds" called on it by its parent
	// (the GameUI) without first calling on the stuff it depends on (i.e.
	// "this" PopupMenuFrame instance, not its sub frame that is named
	// this.popupMenuFrame of type MenuFrame)

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		if (this.popupArrowFrame != null) {
			this.popupArrowFrame.render(batch, baseFont, glyphLayout);
		}
	}

	public void onClickItem(final int button, final int menuItemIndex) {
		onClick(button);
		if (this.menuClickListener != null) {
			this.menuClickListener.onClick(button, menuItemIndex);
		}
	}

	public void setMenuClickListener(final MenuClickListener menuClickListener) {
		this.menuClickListener = menuClickListener;
	}

}
