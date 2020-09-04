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
	|
	NOT expression # NotExpression
	;

functionExpression:
	ID '(' argsList ')'
	|
	ID '(' ')'
	;
	
argsList:
	expression # SingleArgument
	|
	expression ',' argsList # ListArgument
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
	IF ifStatementPartial # IfStatement
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
	FUNCTION ID TAKES paramList RETURNS type newlines statements ENDFUNCTION newlines
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

STRING_LITERAL : ('"'.*?'"');

INTEGER : [0]|([1-9][0-9]*) ;

NULL : 'null' ;
TRUE : 'true' ;
FALSE : 'false' ;

NOT : 'not';

ID : ([a-zA-Z_][a-zA-Z_0-9]*) ;             // match identifiers

WS : [ \t]+ -> skip ; // skip spaces, tabs

NEWLINE : '//'.*?'\r\n' | '//'.*?'\n' | '//'.*?'\r' | '\r' '\n' | '\n' | '\r';
