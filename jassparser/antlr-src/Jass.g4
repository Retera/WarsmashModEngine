/**
 * Define a grammar called Hello
 */
grammar Jass;

@header {
	package com.etheller.interpreter;
}


program :
	newlines
	|
	newlines_opt
	typeDefinitionBlock
	(block)* 
	(functionBlock)*
	;

typeDefinition :
	TYPE ID EXTENDS ID newlines
	;  

type :
	ID # BasicType
	|
	ID ARRAY # ArrayType
	|
	NOTHING # NothingType
	;

global : 
	CONSTANT? type ID newlines # BasicGlobal
	|
	CONSTANT? type ID assignTail newlines # DefinitionGlobal
	;
local : 
	LOCAL type ID newlines # BasicLocal
	|
	LOCAL type ID assignTail newlines # DefinitionLocal
	;
	
assignTail:
	EQUALS expression;
	
multDivExpression:
	multDivExpression '*' baseExpression # MultiplicationExpression
	|
	multDivExpression '/' baseExpression # DivisionExpression
	|
	baseExpression # BaseMultiplicationExpression
	;
	
simpleArithmeticExpression:
	simpleArithmeticExpression '+' multDivExpression # AdditionExpression
	|
	simpleArithmeticExpression '-' multDivExpression # SubtrationExpression
	|
	multDivExpression # BaseAdditionExpression
	;
	
boolComparisonExpression:
	boolComparisonExpression '<' simpleArithmeticExpression # BooleanLessExpression
	|
	boolComparisonExpression '>' simpleArithmeticExpression # BooleanGreaterExpression
	|
	boolComparisonExpression '<=' simpleArithmeticExpression # BooleanLessOrEqualsExpression
	|
	boolComparisonExpression '>=' simpleArithmeticExpression # BooleanGreaterOrEqualsExpression
	|
	simpleArithmeticExpression # BaseBoolComparisonExpression
	;
	
boolEqualityExpression:
	boolEqualityExpression '==' boolComparisonExpression # EqualsExpression
	|
	boolEqualityExpression '!=' boolComparisonExpression # NotEqualsExpression
	|
	boolComparisonExpression # BaseBoolExpression
	;
	
boolAndsExpression:
	boolAndsExpression AND boolEqualityExpression # BooleanAndExpression
	|
	boolEqualityExpression # BaseBoolAndsExpression
	;
	
boolExpression:
	boolExpression OR boolAndsExpression # BooleanOrExpression
	|
	boolAndsExpression # BaseBoolOrsExpression
	;
	
baseExpression:
	ID # ReferenceExpression
	|
	STRING_LITERAL #StringLiteralExpression
	|
	INTEGER #IntegerLiteralExpression
	|
	HEX_CONSTANT #HexIntegerLiteralExpression
	|
	DOLLAR_HEX_CONSTANT #DollarHexIntegerLiteralExpression
	|
	RAWCODE #RawcodeLiteralExpression
	|
	REAL #RealLiteralExpression
	|
	FUNCTION ID #FunctionReferenceExpression
	|
	NULL # NullExpression
	|
	TRUE # TrueExpression
	|
	FALSE # FalseExpression
	|
	ID '[' expression ']' # ArrayReferenceExpression
	|
	functionExpression # FunctionCallExpression
	|
	'(' expression ')' # ParentheticalExpression
	|
	NOT baseExpression # NotExpression
	|
	'-' baseExpression # NegateExpression
	;
	
expression:
	boolExpression;

functionExpression:
	ID '(' argsList ')'
	|
	ID '(' ')'
	;
	
argsList:
	expression # SingleArgument
	|
	expression ',' argsList # ListArgument
	|
	# EmptyArgument
	;

//#booleanExpression:
//	simpleArithmeticExpression # PassBooleanThroughExpression
//	|
	
statement:
	CALL functionExpression newlines #CallStatement
	|
	SET ID EQUALS expression newlines #SetStatement
	|
	SET ID '[' expression ']' EQUALS expression newlines # ArrayedAssignmentStatement
	|
	RETURN expression newlines # ReturnStatement
	|
	RETURN newlines # ReturnNothingStatement
	|
	EXITWHEN expression newlines # ExitWhenStatement
	|
	local # LocalStatement
	|
	LOOP newlines statements ENDLOOP newlines # LoopStatement
	|
	IF ifStatementPartial # IfStatement
	|
	DEBUG statement # DebugStatement
	;
	 
ifStatementPartial:
	expression THEN newlines statements ENDIF newlines # SimpleIfStatement
	|
	expression THEN newlines statements ELSE newlines statements ENDIF newlines # IfElseStatement
	|
	expression THEN newlines statements ELSEIF ifStatementPartial # IfElseIfStatement
	;

param:
	type ID;
	
paramList:
	param # SingleParameter
	|
	param ',' paramList # ListParameter
	|
	NOTHING # NothingParameter
	;

globalsBlock :
	GLOBALS newlines (global)* ENDGLOBALS newlines ;
	
typeDefinitionBlock :
	(typeDefinition)*
	;
	
nativeBlock:
	CONSTANT? NATIVE ID TAKES paramList RETURNS type newlines
	;
	
block:
	globalsBlock
	|
	nativeBlock
	;
	
functionBlock:
	CONSTANT? FUNCTION ID TAKES paramList RETURNS type newlines statements ENDFUNCTION newlines
	;
	
statements:
	(statement)*
	;
	
newlines:
	pnewlines
	|
	EOF;
	
newlines_opt:
	pnewlines
	|
	EOF
	|
	;
	
pnewlines:
	NEWLINE
	|
	NEWLINE newlines
	;

EQUALS : '=';


GLOBALS : 'globals' ; // globals
ENDGLOBALS : 'endglobals' ; // end globals block

NATIVE : 'native' ;

FUNCTION : 'function' ; // function
TAKES : 'takes' ; // takes
RETURNS : 'returns' ;
ENDFUNCTION : 'endfunction' ; // endfunction
NOTHING : 'nothing' ;

CALL : 'call' ;
SET : 'set' ;
RETURN : 'return' ;

ARRAY : 'array' ;

TYPE : 'type';

EXTENDS : 'extends';

IF : 'if';
THEN : 'then';
ELSE : 'else';
ENDIF : 'endif';
ELSEIF : 'elseif';
CONSTANT : 'constant';
LOCAL : 'local';
LOOP : 'loop';
ENDLOOP : 'endloop';
EXITWHEN : 'exitwhen';
DEBUG : 'debug';

fragment ESCAPED_QUOTE : '\\"';
STRING_LITERAL :   '"' ( ESCAPED_QUOTE | . )*? '"';

 
INTEGER : [0]|([1-9][0-9]*) ;
 
HEX_CONSTANT : '0x'(([0-9]|[a-f]|[A-F])*) ;
DOLLAR_HEX_CONSTANT : '$'(([0-9]|[A-F])*) ;

RAWCODE : ('\''.*?'\'');

REAL : (([0]|([1-9][0-9]*))'.'[0-9]*)|('.'([0-9]*)) ;

NULL : 'null' ;
TRUE : 'true' ;
FALSE : 'false' ;

NOT : 'not';
OR : 'or';
AND : 'and';

ID : ([a-zA-Z_][a-zA-Z_0-9]*) ;             // match identifiers

WS : [ \t]+ -> skip ; // skip spaces, tabs

NEWLINE : '//'.*?'\r\n' | '//'.*?'\n' | '//'.*?'\r' | '\r' '\n' | '\n' | '\r';
