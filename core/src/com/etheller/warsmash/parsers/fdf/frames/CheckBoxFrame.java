package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;

public class CheckBoxFrame extends GlueButtonFrame {
	private boolean checked = false;
	private UIFrame checkBoxCheckHighlight;
	private UIFrame checkBoxDisabledCheckHighlight;
	private UIFrame activeChildHighlight;

	public CheckBoxFrame(final String name, final UIFrame parent) {
		super(name, parent);
	}

	public void setCheckBoxCheckHighlight(final UIFrame checkBoxCheckHighlight) {
		this.checkBoxCheckHighlight = checkBoxCheckHighlight;
	}

	public void setCheckBoxDisabledCheckHighlight(final UIFrame checkBoxDisabledCheckHighlight) {
		this.checkBoxDisabledCheckHighlight = checkBoxDisabledCheckHighlight;
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		super.innerPositionBounds(gameUI, viewport);
		if (this.checkBoxCheckHighlight != null) {
			this.checkBoxCheckHighlight.positionBounds(gameUI, viewport);
		}
		if (this.checkBoxDisabledCheckHighlight != null) {
			this.checkBoxDisabledCheckHighlight.positionBounds(gameUI, viewport);
		}
		updateCheckHighlight();
	}

	@Override
	protected void internalRender(final SpriteBatch batch, final BitmapFont baseFont, final GlyphLayout glyphLayout) {
		super.internalRender(batch, baseFont, glyphLayout);
		if (this.activeChildHighlight != null) {
			this.activeChildHighlight.render(batch, baseFont, glyphLayout);
		}
	}

	@Override
	public void mouseUp(final GameUI gameUI, final Viewport uiViewport) {
		super.mouseUp(gameUI, uiViewport);
		updateCheckHighlight();
	}

	private void updateCheckHighlight() {
		if (this.checked) {
			if (isEnabled()) {
				this.activeChildHighlight = this.checkBoxCheckHighlight;
			}
			else {
				this.activeChildHighlight = this.checkBoxDisabledCheckHighlight;
			}
		}
		else {
			this.activeChildHighlight = null;
		}
	}

	public void setChecked(final boolean checked) {
		this.checked = checked;
		updateCheckHighlight();
	}

	public boolean isChecked() {
		return this.checked;
	}

	@Override
	public void onClick(final int button) {
		this.checked = !this.checked;
		super.onClick(button);
	}

}
