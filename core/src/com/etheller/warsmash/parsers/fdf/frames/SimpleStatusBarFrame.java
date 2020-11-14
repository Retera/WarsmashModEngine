package com.etheller.warsmash.parsers.fdf.frames;

import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;

public class SimpleStatusBarFrame extends AbstractUIFrame {
	private final boolean decorateFileNames;
	private final TextureFrame barFrame;
	private final TextureFrame borderFrame;

	public SimpleStatusBarFrame(final String name, final UIFrame parent, final boolean decorateFileNames) {
		super(name, parent);
		this.decorateFileNames = decorateFileNames;
		this.barFrame = new TextureFrame(name + "Bar", this, decorateFileNames, new Vector4Definition(0, 1, 0, 1));
		this.borderFrame = new TextureFrame(name + "Border", this, decorateFileNames,
				new Vector4Definition(0, 1, 0, 1));
		this.borderFrame.setSetAllPoints(true);
		this.barFrame.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this, FramePoint.TOPLEFT, 0, 0));
		this.barFrame.addSetPoint(new SetPoint(FramePoint.BOTTOMLEFT, this, FramePoint.BOTTOMLEFT, 0, 0));
		add(this.barFrame);
		add(this.borderFrame);
	}

	public boolean isDecorateFileNames() {
		return this.decorateFileNames;
	}

	public void setValue(final float value) {
		this.barFrame.setWidth(this.renderBounds.width * value);
	}

	public TextureFrame getBarFrame() {
		return this.barFrame;
	}

	public TextureFrame getBorderFrame() {
		return this.borderFrame;
	}
}
