/**
 * Define a grammar called FDF
 */
grammar FDF;

@header {
	package com.etheller.warsmash.fdfparser;
}

program :
	(statement)*
	;
	
statement:
	STRING_LIST OPEN_CURLY (ID STRING_LITERAL COMMA)*? CLOSE_CURLY # StringListStatement
	|
	INCLUDE_FILE STRING_LITERAL COMMA # IncludeStatement
	|
	frame # FrameStatement
	; 
	
frame:
	frame_type_qualifier STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # CompDefinition
	|
	frame_type_qualifier STRING_LITERAL INHERITS STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # CompSubTypeDefinition
	|
	frame_type_qualifier STRING_LITERAL INHERITS WITHCHILDREN STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # CompSubTypeDefinitionWithChildren
	|
	FRAME STRING_LITERAL STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # FrameDefinition
	|
	FRAME STRING_LITERAL STRING_LITERAL INHERITS STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # FrameSubTypeDefinition
	|
	FRAME STRING_LITERAL STRING_LITERAL INHERITS WITHCHILDREN STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # FrameSubTypeDefinitionWithChildren
	;
	
frame_element: 
	frame # FrameFrameElement
	|
	HEIGHT FLOAT COMMA # HeightElement
	|
	WIDTH FLOAT COMMA # WidthElement
	|
	CONTROL_STYLE STRING_LITERAL COMMA # ControlStyleElement
	|
	CONTROL_BACKDROP STRING_LITERAL COMMA # ControlBackdropElement
	|
	CONTROL_PUSHED_BACKDROP STRING_LITERAL COMMA # ControlPushedBackdropElement
	|
	CONTROL_DISABLED_BACKDROP STRING_LITERAL COMMA # ControlDisabledBackdropElement
	|
	CONTROL_FOCUS_HIGHLIGHT STRING_LITERAL COMMA # ControlFocusHighlightElement
	|
	CONTROL_MOUSE_OVER_HIGHLIGHT STRING_LITERAL COMMA # ControlMouseOverHighlightElement
	|
	HIGHLIGHT_TYPE STRING_LITERAL COMMA # HighlightTypeElement
	|
	HIGHLIGHT_ALPHA_FILE STRING_LITERAL COMMA # HighlightAlphaFileElement
	|
	HIGHLIGHT_ALPHA_MODE STRING_LITERAL COMMA # HighlightAlphaModeElement
	|
	BUTTON_PUSHED_TEXT_OFFSET FLOAT FLOAT COMMA # ButtonPushedTextOffsetElement
	|
	DIALOG_BACKDROP STRING_LITERAL COMMA # DialogBackdropElement
	|
	BACKDROP_TILE_BACKGROUND COMMA # BackdropTileBackgroundElement
	|
	DECORATE_FILE_NAMES COMMA # DecorateFileNamesElement
	|
	SET_ALL_POINTS COMMA # SetAllPointsElement
	|
	USE_ACTIVE_CONTEXT COMMA # UseActiveContextElement
	|
	BACKDROP_HALF_SIDES COMMA # BackdropHalfSidesElement
	|
	BACKDROP_BACKGROUND STRING_LITERAL COMMA # BackdropBackgroundElement
	|
	BACKDROP_CORNER_FLAGS STRING_LITERAL COMMA # BackdropCornerFlagsElement
	|
	BACKDROP_CORNER_SIZE FLOAT COMMA # BackdropCornerSizeElement
	|
	BACKDROP_BACKGROUND_SIZE FLOAT COMMA # BackdropBackgroundSizeElement
	|
	BACKDROP_BACKGROUND_INSETS FLOAT FLOAT FLOAT FLOAT COMMA # BackdropBackgroundInsetsElement
	|
	BACKDROP_EDGE_FILE STRING_LITERAL COMMA # BackdropEdgeFileElement
	|
	BACKDROP_BACKGROUND STRING_LITERAL COMMA # BackdropBackgroundElement
	|
	BACKDROP_BLEND_ALL COMMA # BackdropBlendAllElement
	|
	BUTTON_TEXT STRING_LITERAL COMMA #ButtonTextElement
	|
	TEXT STRING_LITERAL COMMA # TextElement
	|
	SETPOINT frame_point COMMA STRING_LITERAL COMMA frame_point COMMA FLOAT COMMA FLOAT COMMA # SetPointElement
	|
	BACKDROP_CORNER_FILE STRING_LITERAL COMMA # BackdropCornerFileElement
	|
	BACKDROP_LEFT_FILE STRING_LITERAL COMMA # BackdropLeftFileElement
	|
	BACKDROP_RIGHT_FILE STRING_LITERAL COMMA # BackdropRightFileElement
	|
	BACKDROP_TOP_FILE STRING_LITERAL COMMA # BackdropTopFileElement
	|
	BACKDROP_BOTTOM_FILE STRING_LITERAL COMMA # BackdropBottomFileElement
	|
	FRAME_FONT STRING_LITERAL COMMA FLOAT COMMA STRING_LITERAL COMMA # DecorateFileNamesElement
	|
	FONT_FLAGS STRING_LITERAL COMMA # FontFlagsElement
	|
	FONT_COLOR color COMMA # FontColorElement
	|
	FONT_HIGHLIGHT_COLOR color COMMA # FontHighlightColorElement
	|
	FONT_DISABLED_COLOR color COMMA # FontDisabledColorElement
	|
	FONT_SHADOW_COLOR color COMMA # FontShadowColorElement
	|
	FONT_SHADOW_OFFSET FLOAT FLOAT COMMA # FontShadowOffsetElement
	|
	FONT_JUSTIFICATION_H text_justify COMMA # FontJustificationHElement
	|
	FONT_JUSTIFICATION_V text_justify COMMA # FontJustificationVElement
	|
	FONT_JUSTIFICATION_OFFSET FLOAT FLOAT COMMA # FontJustificationOffsetElement
	|
	SLIDER_LAYOUT_HORIZONTAL COMMA # SliderLayoutHorizontalElement
	|
	SLIDER_LAYOUT_VERTICAL COMMA # SliderLayoutVerticalElement
	|
	SCROLL_BAR_INC_BUTTON_FRAME STRING_LITERAL COMMA # ScrollBarIncButtonFrameElement
	|
	SCROLL_BAR_DEC_BUTTON_FRAME STRING_LITERAL COMMA # ScrollBarDecButtonFrameElement
	|
	SLIDER_THUMB_BUTTON_FRAME STRING_LITERAL COMMA # SliderThumbButtonFrameElement
	|
	LIST_BOX_BORDER FLOAT COMMA # ListBoxBorderElement
	|
	LIST_BOX_SCROLL_BAR STRING_LITERAL COMMA # ListBoxScrollBarElement
	|
	EDIT_BORDER_SIZE FLOAT COMMA # EditBorderSizeElement
	|
	EDIT_CURSOR_COLOR color COMMA # EditCursorColorElement
	|
	MENU_TEXT_HIGHLIGHT_COLOR color COMMA # MenuTextHighlightColorElement
	|
	MENU_ITEM_HEIGHT FLOAT COMMA # MenuItemHighlightElement
	|
	MENU_BORDER FLOAT COMMA # MenuBorderElement
	|
	POPUP_BUTTON_INSET FLOAT COMMA # PopupButtonInsetElement
	|
	POPUP_TITLE_FRAME STRING_LITERAL COMMA # PopupTitleFrameElement
	|
	POPUP_ARROW_FRAME STRING_LITERAL COMMA # PopupArrowFrameElement
	|
	POPUP_MENU_FRAME STRING_LITERAL COMMA # PopupMenuFrameElement
	|
	CHECK_BOX_CHECK_HIGHLIGHT STRING_LITERAL COMMA # CheckBoxCheckHighlightElement
	|
	CHECK_BOX_DISABLED_CHECK_HIGHLIGHT STRING_LITERAL COMMA # CheckBoxDisabledCheckHighlightElement
	|
	TEXT_AREA_LINE_HEIGHT FLOAT COMMA # TextAreaLineHeightElement
	|
	TEXT_AREA_LINE_GAP FLOAT COMMA # TextAreaLineGapElement
	|
	TEXT_AREA_INSET FLOAT COMMA # TextAreaInsetElement
	|
	TEXT_AREA_SCROLL_BAR STRING_LITERAL COMMA # TextAreaScrollBarElement
	|
	CHAT_DISPLAY_LINE_HEIGHT FLOAT COMMA # ChatDisplayLineHeightElement
	|
	CHAT_DISPLAY_BORDER_SIZE FLOAT COMMA # ChatDisplayBorderSize
	|
	CHAT_DISPLAY_SCROLL_BAR STRING_LITERAL COMMA # ChatDisplayScrollBarElement
	|
	CHAT_DISPLAY_EDIT_BOX STRING_LITERAL COMMA # ChatDisplayEditBoxElement
	|
	TEXT STRING_LITERAL COMMA # TextElement
	|
	FILE STRING_LITERAL COMMA # FileElement
	|
	FONT STRING_LITERAL COMMA FLOAT COMMA # FontElement
	;
	
text_justify:
	JUSTIFYTOP | JUSTIFYMIDDLE | JUSTIFYBOTTOM | JUSTIFYLEFT | JUSTIFYCENTER | JUSTIFYRIGHT;

frame_point:
	FRAMEPOINT_TOPLEFT
	| FRAMEPOINT_TOP
	| FRAMEPOINT_TOPRIGHT
	| FRAMEPOINT_LEFT
	| FRAMEPOINT_CENTER
	| FRAMEPOINT_RIGHT
	| FRAMEPOINT_BOTTOMLEFT
	| FRAMEPOINT_BOTTOM
	| FRAMEPOINT_BOTTOMRIGHT;
	
color:
	FLOAT FLOAT FLOAT
	|
	FLOAT FLOAT FLOAT FLOAT
	;
	
frame_type_qualifier:
	STRING
	|
	TEXTURE
	|
	LAYER
	;

OPEN_CURLY : '{';

CLOSE_CURLY : '}';

STRING_LIST : 'StringList' ;

INCLUDE_FILE : 'IncludeFile' ;

FRAME : 'Frame' ;

STRING : 'String' ;

TEXTURE : 'Texture' ;

LAYER : 'Layer' ;

INHERITS : 'INHERITS' ;

WITHCHILDREN : 'WITHCHILDREN' ;

DECORATE_FILE_NAMES : 'DecorateFileNames';

SET_ALL_POINTS : 'SetAllPoints';

USE_ACTIVE_CONTEXT : 'UseActiveContext';

HEIGHT : 'Height';

WIDTH : 'Width';

HIGHLIGHT_TYPE : 'HighlightType';
HIGHLIGHT_ALPHA_FILE : 'HighlightAlphaFile';
HIGHLIGHT_ALPHA_MODE : 'HighlightAlphaMode';

CONTROL_STYLE : 'ControlStyle';
CONTROL_BACKDROP : 'ControlBackdrop';
CONTROL_PUSHED_BACKDROP : 'ControlPushedBackdrop';
CONTROL_DISABLED_BACKDROP : 'ControlDisabledBackdrop';
CONTROL_FOCUS_HIGHLIGHT : 'ControlFocusHighlight';
CONTROL_MOUSE_OVER_HIGHLIGHT : 'ControlMouseOverHighlight';

DIALOG_BACKDROP : 'DialogBackdrop';

BACKDROP_TILE_BACKGROUND : 'BackdropTileBackground';
BACKDROP_HALF_SIDES : 'BackdropHalfSides';
BACKDROP_BACKGROUND : 'BackdropBackground';
BACKDROP_BLEND_ALL : 'BackdropBlendAll';
BACKDROP_CORNER_FLAGS : 'BackdropCornerFlags';
BACKDROP_CORNER_SIZE : 'BackdropCornerSize';
BACKDROP_BACKGROUND_SIZE : 'BackdropBackgroundSize';
BACKDROP_BACKGROUND_INSETS : 'BackdropBackgroundInsets';
BACKDROP_EDGE_FILE : 'BackdropEdgeFile';
BACKDROP_CORNER_FILE : 'BackdropCornerFile';
BACKDROP_LEFT_FILE : 'BackdropLeftFile';
BACKDROP_RIGHT_FILE : 'BackdropRightFile';
BACKDROP_TOP_FILE : 'BackdropTopFile';
BACKDROP_BOTTOM_FILE : 'BackdropBottomFile';

FRAME_FONT : 'FrameFont';
FONT_FLAGS : 'FontFlags';
FONT_COLOR : 'FontColor';
FONT_HIGHLIGHT_COLOR : 'FontHighlightColor';
FONT_DISABLED_COLOR : 'FontDisabledColor';
FONT_SHADOW_COLOR : 'FontShadowColor';
FONT_SHADOW_OFFSET : 'FontShadowOffset';
FONT_JUSTIFICATION_H : 'FontJustificationH';
FONT_JUSTIFICATION_V : 'FontJustificationV';
FONT_JUSTIFICATION_OFFSET : 'FontJustificationOffset';

SLIDER_LAYOUT_HORIZONTAL : 'SliderLayoutHorizontal';
SLIDER_LAYOUT_VERTICAL : 'SliderLayoutVertical';
SLIDER_THUMB_BUTTON_FRAME : 'SliderThumbButtonFrame';
SCROLL_BAR_DEC_BUTTON_FRAME : 'ScrollBarDecButtonFrame';
SCROLL_BAR_INC_BUTTON_FRAME : 'ScrollBarIncButtonFrame';

LIST_BOX_BORDER : 'ListBoxBorder';
LIST_BOX_SCROLL_BAR : 'ListBoxScrollBar';

EDIT_BORDER_SIZE : 'EditBorderSize';
EDIT_CURSOR_COLOR : 'EditCursorColor';

MENU_TEXT_HIGHLIGHT_COLOR : 'MenuTextHighlightColor';
MENU_ITEM_HEIGHT : 'MenuItemHeight';
MENU_BORDER : 'MenuBorder';

POPUP_BUTTON_INSET : 'PopupButtonInset';
POPUP_TITLE_FRAME : 'PopupTitleFrame';
POPUP_ARROW_FRAME : 'PopupArrowFrame';
POPUP_MENU_FRAME : 'PopupMenuFrame';
CHECK_BOX_CHECK_HIGHLIGHT : 'CheckBoxCheckHighlight';
CHECK_BOX_DISABLED_CHECK_HIGHLIGHT : 'CheckBoxDisabledCheckHighlight';

TEXT_AREA_LINE_HEIGHT : 'TextAreaLineHeight';
TEXT_AREA_LINE_GAP : 'TextAreaLineGap';
TEXT_AREA_INSET : 'TextAreaInset';
TEXT_AREA_SCROLL_BAR : 'TextAreaScrollBar';

CHAT_DISPLAY_LINE_HEIGHT : 'ChatDisplayLineHeight';
CHAT_DISPLAY_BORDER_SIZE : 'ChatDisplayBorderSize';
CHAT_DISPLAY_SCROLL_BAR : 'ChatDisplayScrollBar';
CHAT_DISPLAY_EDIT_BOX : 'ChatDisplayEditBox';

BUTTON_TEXT : 'ButtonText';
BUTTON_PUSHED_TEXT_OFFSET : 'ButtonPushedTextOffset';

TEXT : 'Text';

FILE : 'File';

FONT : 'Font';

SETPOINT : 'SetPoint';

JUSTIFYTOP : 'JUSTIFYTOP';
JUSTIFYMIDDLE : 'JUSTIFYMIDDLE';
JUSTIFYBOTTOM : 'JUSTIFYBOTTOM';
JUSTIFYLEFT : 'JUSTIFYLEFT';
JUSTIFYCENTER : 'JUSTIFYCENTER';
JUSTIFYRIGHT : 'JUSTIFYRIGHT';

FRAMEPOINT_TOPLEFT : 'TOPLEFT';
FRAMEPOINT_TOP : 'TOP';
FRAMEPOINT_TOPRIGHT : 'TOPRIGHT';
FRAMEPOINT_LEFT : 'LEFT';
FRAMEPOINT_CENTER : 'CENTER';
FRAMEPOINT_RIGHT : 'RIGHT';
FRAMEPOINT_BOTTOMLEFT : 'BOTTOMLEFT';
FRAMEPOINT_BOTTOM : 'BOTTOM';
FRAMEPOINT_BOTTOMRIGHT : 'BOTTOMRIGHT';

ID : ([a-zA-Z_][a-zA-Z_0-9]*) ;

COMMA : ',';

STRING_LITERAL : ('"'.*?'"');

WS : [ \t\r\n]+ -> skip ;

FLOAT : '-'?([0]|([1-9][0-9]*))('.'([0-9]*)?)?'f'? ;

MULTI_LINE_COMMENT : '/*'.*?'*/' -> skip ;
COMMENT : '//'.*?'\n' -> skip ;
