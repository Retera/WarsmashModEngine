package com.etheller.warsmash.parsers.fdf.datamodel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Pretty sure this is probably not how it works in-game but this silly
 * everything class might help get a prototype running fast until I have a
 * better understanding of how I want these classes designed.
 */
public class FrameDefintionOld {
	private String frameType;
	private String name;
	// everything
	private float width;
	private float height;
	private boolean decorateFileNames;
	private boolean setAllPoints;
	private final List<FrameDefintionOld> innerFrames = new ArrayList<>();
	// "BACKDROP"
	private boolean backdropTileBackground;
	private boolean backdropHalfSides;
	private boolean backdropBlendAll;
	private String backdropBackground;
	private EnumSet<BackdropCornerFlags> backdropCornerFlags;
	private float backdropCornerSize;
	private float backdropBackgroundSize;
	private Insets backdropBackgroundInsets;
	private String backdropCornerFile;
	private String backdropLeftFile;
	private String backdropRightFile;
	private String backdropTopFile;
	private String backdropBottomFile;
	private String backdropEdgeFile;
	// "HIGHLIGHT"
	private HighlightType highlightType;
	private String highlightAlphaFile;
	private HighlightAlphaMode highlightAlphaMode;
	// "TEXTBUTTON" or "GLUEBUTTON"
	private EnumSet<ControlStyle> controlStyle;
	private Offset buttonPushedTextOffset;
	private String controlBackdrop;
	private String controlPushedBackdrop;
	private String controlDisabledBackdrop;
	private String controlFocusHighlight;
	private String controlMouseOverHighlight;
	// "TEXT"
	private FontDefinition frameFont;
	private TextJustify fontJustificationH;
	private TextJustify fontJustificationV;
	private EnumSet<FontFlags> fontFlags;
	private ColorDefinition fontColor;
	private ColorDefinition fontHighlightColor;
	private ColorDefinition fontDisabledColor;
	private ColorDefinition fontShadowColor;
	private Offset fontShadowOffset;
	// "SLIDER"
	private boolean sliderLayoutHorizontal;
	// "SCROLLBAR"
	private boolean sliderLayoutVertical;
	private String scrollBarIncButtonFrame;
	private String scrollBarDecButtonFrame;
	private String scrollBarThumbButtonFrame;
	// "LISTBOX"
	private float listBoxBorder;
	// "EDITBOX"
}
