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
	'nothing' # NothingType
	;

global : 
	type ID newlines # BasicGlobal
	|
	type ID assignTail newlines # DefinitionGlobal
	;
	
assignTail:
	EQUALS expression;
	
expression:
	ID # ReferenceExpression
	|
	STRING_LITERAL #StringLiteralExpression
	|
	INTEGER #IntegerLiteralExpression
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
	;

functionExpression:
	ID '(' argsList ')'
	;
	
argsList:
	expression # SingleArgument
	|
	expression ',' argsList # ListArgument
	|
	#EmptyArgument
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
	;

param:
	type ID;
	
paramList:
	param # SingleParameter
	|
	param ',' paramList # ListParameter
	|
	'nothing' # NothingParameter
	;

globalsBlock :
	GLOBALS newlines (global)* ENDGLOBALS newlines ;
	
typeDefinitionBlock :
	(typeDefinition)*
	;
	
nativeBlock:
	NATIVE ID TAKES paramList RETURNS type newlines
	;
	
block:
	globalsBlock
	|
	nativeBlock
	;
	
functionBlock:
	FUNCTION ID TAKES paramList RETURNS type newlines (statement)* ENDFUNCTION newlines
	;
	
newlines:
	NEWLINES
	|
	EOF;
	
newlines_opt:
	NEWLINES
	|
	EOF
	|
	;

EQUALS : '=';


GLOBALS : 'globals' ; // globals
ENDGLOBALS : 'endglobals' ; // end globals block

NATIVE : 'native' ;

FUNCTION : 'function' ; // function
TAKES : 'takes' ; // takes
RETURNS : 'returns' ;
ENDFUNCTION : 'endfunction' ; // endfunction

CALL : 'call' ;
SET : 'set' ;
RETURN : 'return' ;

ARRAY : 'array' ;

TYPE : 'type';

EXTENDS : 'extends';

STRING_LITERAL : ('"'.*?'"');

INTEGER : [0]|([1-9][0-9]*) ;

NULL : 'null' ;
TRUE : 'true' ;
FALSE : 'false' ;

ID : ([a-zA-Z_][a-zA-Z_0-9]*) ;             // match identifiers

WS : [ \t]+ -> skip ; // skip spaces, tabs

NEWLINES : NEWLINE+;
fragment NEWLINE   : '\r' '\n' | '\n' | '\r' | ('//'.*?'\n');