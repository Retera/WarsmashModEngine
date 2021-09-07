package com.etheller.warsmash.parsers.fdf.frames;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class SimpleStatusBarFrame extends AbstractUIFrame {
	private final boolean decorateFileNames;
	private final TextureFrame barFrame;
	private final TextureFrame borderFrame;
	private final float barInset;
	private float lastValue = Float.NaN;

	public SimpleStatusBarFrame(final String name, final UIFrame parent, final boolean decorateFileNames,
			final boolean borderBelow, final float barInset) {
		super(name, parent);
		this.decorateFileNames = decorateFileNames;
		this.barInset = barInset;
		this.barFrame = new TextureFrame(name + "Bar", this, decorateFileNames, new Vector4Definition(0, 1, 0, 1));
		this.borderFrame = new TextureFrame(name + "Border", this, decorateFileNames,
				new Vector4Definition(0, 1, 0, 1));
		this.borderFrame.setSetAllPoints(true);
		this.barFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, barInset, -barInset));
		this.barFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMLEFT, this, FramePoint.BOTTOMLEFT, barInset, barInset));
		this.barFrame.setSetAllPoints(true, barInset);
		if (borderBelow) {
			add(this.borderFrame);
			add(this.barFrame);
		}
		else {
			add(this.barFrame);
			add(this.borderFrame);
		}
	}

	@Override
	protected void innerPositionBounds(final GameUI gameUI, final Viewport viewport) {
		if (!Float.isNaN(this.lastValue)) {
			this.barFrame.setWidth(((this.renderBounds.width - (this.barInset * 2)) * this.lastValue));
		}
		super.innerPositionBounds(gameUI, viewport);
	}

	public boolean isDecorateFileNames() {
		return this.decorateFileNames;
	}

	public void setValue(final float value) {
		this.barFrame.setTexCoord(0, value, 0, 1);
		this.barFrame.setWidth(((this.renderBounds.width - (this.barInset * 2)) * value));
		this.lastValue = value;
	}

	public TextureFrame getBarFrame() {
		return this.barFrame;
	}

	public TextureFrame getBorderFrame() {
		return this.borderFrame;
	}
}
