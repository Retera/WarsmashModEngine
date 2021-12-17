package com.etheller.warsmash.parsers.fdf.frames;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.MenuItem;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;

public class MenuFrame extends AbstractUIFrame {

	private UIFrame controlBackdrop;
	private MenuClickListener onClick;
	private final List<MenuItem> menuItems;
	private float itemHeight;
	private float border;
	private BitmapFont frameFont;
	private Color fontHighlightColor;
	private Color fontDisabledColor;

	public MenuFrame(final String name, final UIFrame parent) {
		super(name, parent);
		this.menuItems = new ArrayList<>();
	}

	public void setControlBackdrop(final UIFrame controlBackdrop) {
		this.controlBackdrop = controlBackdrop;
	}

	public UIFrame getControlBackdrop() {
		return this.controlBackdrop;
	}

	@Override
	public void positionBounds(final GameUI gameUI, final Viewport viewport) {
		if (this.menuItems.size() != 0) {
			final float menuHeight = (this.border * 2) + (this.itemHeight * this.menuItems.size());
			setHeight(menuHeight);
		}
		// couldn't put this on innerPositionBounds because it needed to happen before
		// positioning of self (Maybe later do this a better way?)
		super.positionBounds(gameUI, viewport);
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		this.controlBackdrop.positionBounds(gameUI, viewport);
		super.innerPositionBounds(gameUI, viewport);
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		this.controlBackdrop.render(batch, baseFont, glyphLayout);
		super.internalRender(batch, baseFont, glyphLayout);
	}

	public void doClick(final int button, final int menuItemIndex) {
		if (this.onClick != null) {
			this.onClick.onClick(button, menuItemIndex);
		}
	}

	public void setOnClick(final MenuClickListener onClick) {
		this.onClick = onClick;
	}

	public interface MenuClickListener {
		void onClick(int button, int menuItemIndex);
	}

	public void add(final MenuItem menuItem) {
		this.menuItems.add(menuItem);
	}

	public int getMenuItemCount() {
		return this.menuItems.size();
	}

	public MenuItem getMenuItem(final int index) {
		return this.menuItems.get(index);
	}

	public void setItemHeight(final float itemHeight) {
		this.itemHeight = itemHeight;
	}

	public void setBorder(final float border) {
		this.border = border;
	}

	public void setItems(final Viewport viewport2, final List<MenuItem> items) {
		UIFrame lastChildFrame = null;
		int index = 0;
		for (final MenuItem menuItem : items) {
			final GlueTextButtonFrame childFrame = new GlueTextButtonFrame(null, this);
			final StringFrame stringFrame = new StringFrame(null, childFrame, Color.WHITE, TextJustify.LEFT,
					TextJustify.TOP, this.frameFont, menuItem.getText(), this.fontHighlightColor,
					this.fontDisabledColor);
			stringFrame.setSetAllPoints(true);
			childFrame.setButtonText(stringFrame);
			childFrame.setHeight(this.itemHeight);
			childFrame.setHighlightOnMouseOver(true);
			if (lastChildFrame == null) {
				childFrame.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, this.border, -this.border));
				childFrame.addSetPoint(
						new SetPoint(FramePoint.TOPRIGHT, this, FramePoint.TOPRIGHT, -this.border, -this.border));
			}
			else {
				childFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, lastChildFrame, FramePoint.BOTTOMLEFT, 0, 0));
				childFrame.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, lastChildFrame, FramePoint.BOTTOMRIGHT, 0, 0));
			}
			add(childFrame);
			add(menuItem);
			lastChildFrame = childFrame;
			final int childIndex = index;
			childFrame.setOnClick(new Runnable() {
				@Override
				public void run() {
					doClick(Input.Buttons.LEFT, childIndex);
				}
			});
			index++;
		}
	}

	public void setFrameFont(final BitmapFont frameFont) {
		this.frameFont = frameFont;
	}

	public void setFontHighlightColor(final Color fontHighlightColor) {
		this.fontHighlightColor = fontHighlightColor;
	}

	public void setFontDisabledColor(final Color fontDisabledColor) {
		this.fontDisabledColor = fontDisabledColor;
	}
}
