/**
 * Define a grammar called FDF
 */
grammar FDF;

@header {
	package com.etheller.warsmash.fdfparser;
}

program :
    UnicodeBOM?
	(statement)*
	;

UnicodeBOM :
    '\uFEFF' {skip();}
    ;


	
statement:
	STRING_LIST OPEN_CURLY (ID STRING_LITERAL COMMA)*? CLOSE_CURLY # StringListStatement
	|
	INCLUDE_FILE STRING_LITERAL COMMA # IncludeStatement
	|
	frame # FrameStatement
	; 
	
frame:
	frame_type_qualifier OPEN_CURLY frame_element* CLOSE_CURLY # AnonymousCompDefinition
	|
	frame_type_qualifier INHERITS STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # AnonymousCompSubTypeDefinition
	|
	frame_type_qualifier INHERITS WITHCHILDREN STRING_LITERAL OPEN_CURLY frame_element* CLOSE_CURLY # AnonymousCompSubTypeDefinitionWithChildren
	|
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
	ID FLOAT COMMA # FloatElement
	|
	ID STRING_LITERAL COMMA # StringElement
	|
	ID STRING_LITERAL STRING_LITERAL COMMA # StringPairElement
	|
	ID FLOAT FLOAT COMMA # Vector2Element
	|
	ID COMMA # FlagElement
	|
	ID FLOAT FLOAT FLOAT FLOAT COMMA # Vector4Element
	|
	ID FLOAT COMMA FLOAT COMMA FLOAT COMMA FLOAT COMMA # Vector4CommaElement
	|
	SETPOINT frame_point COMMA STRING_LITERAL COMMA frame_point COMMA FLOAT COMMA FLOAT COMMA # SetPointElement
	|
	ANCHOR frame_point COMMA FLOAT COMMA FLOAT COMMA # AnchorElement
	|
	ID STRING_LITERAL COMMA FLOAT COMMA STRING_LITERAL COMMA # FontElement
	|
	ID FLOAT FLOAT FLOAT COMMA # Vector3Element
	|
	ID text_justify COMMA # TextJustifyElement
	|
	ID STRING_LITERAL COMMA FLOAT COMMA # SimpleFontElement
	|
	MENUITEM STRING_LITERAL COMMA FLOAT COMMA # MenuItemElement
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

SETPOINT : 'SetPoint';
ANCHOR : 'Anchor';
MENUITEM : 'MenuItem' ;

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
