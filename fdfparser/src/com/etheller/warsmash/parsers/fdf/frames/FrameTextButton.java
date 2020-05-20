package com.etheller.warsmash.parsers.fdf.frames;

import java.util.EnumSet;

import com.etheller.warsmash.parsers.fdf.datamodel.ControlStyle;
import com.etheller.warsmash.parsers.fdf.datamodel.Offset;
import com.etheller.warsmash.parsers.fdf.frames.base.Frame;

public class FrameTextButton extends Frame {
	private EnumSet<ControlStyle> controlStyle;
	private Offset buttonPushedTextOffset;
	private String controlBackdrop;

	private String controlPushedBackdrop;
	private String controlDisabledBackdrop;
	private String controlFocusHighlight;
	private String controlMouseOverHighlight;

}
