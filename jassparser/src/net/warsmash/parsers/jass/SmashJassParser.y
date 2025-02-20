%code lexer {
	SmashJassLexer jflexLexer;
	String currentFile;
	
	public YYLexer(Reader r) {
		jflexLexer = new SmashJassLexer(r);
	}
	
	@Override
	public int yylex() throws IOException {
		return jflexLexer.yylex();
	}
	
	@Override
	public Object getLVal() {
		return jflexLexer.getLVal();
	}
	
	@Override
	public void yyerror(final String error) {
		throw new IllegalStateException("SmashJassLexer: " + error + " at " + this.currentFile + ":" + getLine() + ":" + getColumn());
	}
	
	public int getLine() {
		return jflexLexer.getLine() + 1;
	}
	
	public int getColumn() {
		return jflexLexer.getColumn();
	}
	
	public void setFile(String currentFile) {
		this.currentFile = currentFile;
	}
}
%code {

	private String currentParsingFilePath;
	private GlobalScope globalScope;
	private JassNativeManager jassNativeManager;
	private JassProgram jassProgram;
	private JassStructLikeDefinitionBlock currentStruct;
	
	public void scanAndParse(String currentParsingFilePath, JassProgram jassProgram) throws IOException {
		this.currentParsingFilePath = currentParsingFilePath;
		this.globalScope = jassProgram.globalScope;
		this.jassNativeManager = jassProgram.jassNativeManager;
		this.jassProgram = jassProgram;
		((YYLexer)yylexer).setFile(currentParsingFilePath);
		this.parse();
	}
	
	public int getLine() {
		return ((YYLexer)yylexer).getLine();
	}
	
}
%code imports {
import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.execution.instruction.*;
import com.etheller.interpreter.ast.value.*;
import com.etheller.interpreter.ast.value.visitor.*;
import com.etheller.interpreter.ast.function.*;
import com.etheller.interpreter.ast.definition.*;
import com.etheller.interpreter.ast.expression.ArithmeticSigns;
import com.etheller.interpreter.ast.expression.*;
import com.etheller.interpreter.ast.statement.*;
import com.etheller.interpreter.ast.debug.*;
import com.etheller.interpreter.ast.qualifier.*;
import com.etheller.interpreter.ast.struct.*;
import com.etheller.interpreter.ast.type.*;
import com.etheller.interpreter.ast.util.JassProgram;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.EnumSet;
import com.etheller.interpreter.ast.util.JassSettings;
}

%lex-param { Reader r }
%language "Java"
%define api.prefix {SmashJass}
%define api.package {net.warsmash.parsers.jass}
%define api.parser.public

%token EQUALS PLUSEQUALS MINUSEQUALS PLUSPLUS MINUSMINUS GLOBALS ENDGLOBALS NATIVE FUNCTION TAKES RETURNS ENDFUNCTION NOTHING CALL SET RETURN ARRAY TYPE EXTENDS IF THEN ELSE ENDIF ELSEIF CONSTANT LOCAL LOOP ENDLOOP EXITWHEN DEBUG NULL TRUE FALSE NOT OR AND NEWLINE TIMES DIVIDE PLUS MINUS LESS GREATER LESS_EQUALS GREATER_EQUALS DOUBLE_EQUALS NOT_EQUALS OPEN_BRACKET CLOSE_BRACKET OPEN_PAREN CLOSE_PAREN COMMA STRUCT ENDSTRUCT METHOD ENDMETHOD DOT STATIC  LIBRARY LIBRARY_ONCE ENDLIBRARY SCOPE ENDSCOPE INTERFACE ENDINTERFACE REQUIRES OPTIONAL PRIVATE PUBLIC READONLY OPERATOR IMPLEMENT MODULE ENDMODULE INITIALIZER DEFAULTS
%token <String> ID STRING_LITERAL
%token <int> INTEGER HEX_CONSTANT DOLLAR_HEX_CONSTANT RAWCODE
%token <double> REAL

%type <JassTypeToken> type extends_opt
%type <LinkedList<JassParameterDefinition>> paramList
%type <LinkedList<JassLibraryRequirementDefinition>> requirementList requirementList_opt
%type <JassLibraryRequirementDefinition> requirement
%type <JassParameterDefinition> param
%type <LinkedList<JassExpression>> argsList
%type <JassExpression> multDivExpression assignTail simpleArithmeticExpression boolComparisonExpression boolEqualityExpression boolAndsExpression boolExpression baseExpression negatableExpression expression functionExpression methodExpression defaultsTail
%type <JassStatement> statement setPart callPart local ifStatementPartial global
%type <JassStructMemberTypeDefinition> member
%type <LinkedList<JassStatement>> statements_opt statements globals globals_opt
%type <JassMethodDefinitionBlock> methodBlock interfaceMethodBlock
%type <JassImplementModuleDefinition> implementModuleStatement
%type <JassFunctionDefinitionBlock> functionBlock
%type <JassDefinitionBlock> nativeBlock typeDeclarationBlock structDeclarationBlock interfaceDeclarationBlock moduleDeclarationBlock globalsBlock libraryBlock scopeBlock block nonLibraryBlock
%type <LinkedList<JassDefinitionBlock>> blocks blocks_opt nonLibraryBlocks nonLibraryBlocks_opt
%type <JassQualifier> qualifier
%type <EnumSet<JassQualifier>> qualifiers qualifiers_opt

%%

program :
	blocks_opt
	{
		jassProgram.addAll($1);
	}
	;

typeDeclarationBlock :
	TYPE ID EXTENDS ID
	{
		$$ = new JassTypeDefinitionBlock($2, $4);
	}
	;  

type :
	ID
	{
		$$ = new PrimitiveJassTypeToken($1);
	}
	|
	ID ARRAY
	{
		$$ = new ArrayJassTypeToken($1);
	}
	|
	NOTHING
	{
		$$ = NothingJassTypeToken.INSTANCE;
	}
	;
	
qualifier:
	PUBLIC
	{
		$$ = JassQualifier.PUBLIC;
	}
	|
	PRIVATE
	{
		$$ = JassQualifier.PRIVATE;
	}
	|
	STATIC
	{
		$$ = JassQualifier.STATIC;
	}
	|
	CONSTANT
	{
		$$ = JassQualifier.CONSTANT;
	}
	|
	READONLY
	{
		$$ = JassQualifier.READONLY;
	}
	;

qualifiers:
	qualifier
	{
		$$ = EnumSet.of($1);
	}
	|
	qualifier qualifiers
	{
		EnumSet<JassQualifier> set = $2;
		set.add($1);
		$$ = set;
	}
	;
	
qualifiers_opt:
	qualifiers
	{
		$$ = $1;
	}
	|
	{
		$$ = EnumSet.noneOf(JassQualifier.class);
	}
	;

global : 
	qualifiers_opt type ID
	{
		$$ = new JassGlobalStatement($1, $3, $2);
	}
	|
	qualifiers_opt type ID assignTail
	{
		$$ = new JassGlobalDefinitionStatement($1, $3, $2, $4);
	}
	;
local : 
	LOCAL type ID
	{
		$$ = new JassLocalStatement($3, $2);
	}
	|
	LOCAL type ID assignTail
	{
		$$ = new JassLocalDefinitionStatement($3, $2, $4);
	}
	|
	type ID
	{
		$$ = new JassLocalStatement($2, $1);
	}
	|
	type ID assignTail
	{
		$$ = new JassLocalDefinitionStatement($2, $1, $3);
	}
	;

member:
	qualifiers_opt type ID
	{
		$$ = new JassStructMemberTypeDefinition($1, $2, $3, null);
	}
	|
	qualifiers_opt type ID assignTail
	{
		$$ = new JassStructMemberTypeDefinition($1, $2, $3, $4);
	}
	;
	
assignTail:
	EQUALS expression
	{
		$$ = $2;
	};
	
multDivExpression:
	multDivExpression TIMES negatableExpression 
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.MULTIPLY);
	}
	|
	multDivExpression DIVIDE negatableExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.DIVIDE);
	}
	|
	negatableExpression
	;
	
simpleArithmeticExpression:
	simpleArithmeticExpression PLUS multDivExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.ADD);
	}
	|
	simpleArithmeticExpression MINUS multDivExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.SUBTRACT);
	}
	|
	multDivExpression // BaseAdditionExpression
	;
	
boolComparisonExpression:
	boolComparisonExpression LESS simpleArithmeticExpression // BooleanLessExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.LESS);
	}
	|
	boolComparisonExpression GREATER simpleArithmeticExpression // BooleanGreaterExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.GREATER);
	}
	|
	boolComparisonExpression LESS_EQUALS simpleArithmeticExpression // BooleanLessOrEqualsExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.LESS_OR_EQUALS);
	}
	|
	boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression // BooleanGreaterOrEqualsExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.GREATER_OR_EQUALS);
	}
	|
	simpleArithmeticExpression // BaseBoolComparisonExpression
	;
	
boolEqualityExpression:
	boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression // EqualsExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.EQUALS);
	}
	|
	boolEqualityExpression NOT_EQUALS boolComparisonExpression // NotEqualsExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.NOT_EQUALS);
	}
	|
	boolComparisonExpression // BaseBoolExpression
	;
	
boolAndsExpression:
	boolAndsExpression AND boolEqualityExpression // BooleanAndExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.AND);
	}
	|
	boolEqualityExpression // BaseBoolAndsExpression
	;
	
boolExpression:
	boolExpression OR boolAndsExpression // BooleanOrExpression
	{
		$$ = new ArithmeticJassExpression($1, $3, ArithmeticSigns.OR);
	}
	|
	boolAndsExpression // BaseBoolOrsExpression
	;
	
baseExpression:
	ID // ReferenceExpression
	{
		$$ = new ReferenceJassExpression($1);
	}
	|
	STRING_LITERAL //StringLiteralExpression
	{
		$$ = new LiteralJassExpression(StringJassValue.of($1));
	}
	|
	INTEGER //IntegerLiteralExpression
	{
		$$ = new LiteralJassExpression(IntegerJassValue.of($1));
	}
	|
	HEX_CONSTANT //HexIntegerLiteralExpression
	{
		$$ = new LiteralJassExpression(IntegerJassValue.of($1));
	}
	|
	DOLLAR_HEX_CONSTANT //DollarHexIntegerLiteralExpression
	{
		$$ = new LiteralJassExpression(IntegerJassValue.of($1));
	}
	|
	RAWCODE //RawcodeLiteralExpression
	{
		$$ = new LiteralJassExpression(IntegerJassValue.of($1));
	}
	|
	REAL //RealLiteralExpression
	{
		$$ = new LiteralJassExpression(RealJassValue.of($1));
	}
	|
	FUNCTION ID //FunctionReferenceExpression
	{
		$$ = new FunctionReferenceJassExpression($2);
	}
	|
	baseExpression DOT ID //MemberReferenceExpression
	{
		$$ = new MemberJassExpression($1, $3);
	}
	|
	NULL // NullExpression
	{
		$$ = new LiteralJassExpression(null);
	}
	|
	TRUE // TrueExpression
	{
		$$ = new LiteralJassExpression(BooleanJassValue.TRUE);
	}
	|
	FALSE // FalseExpression
	{
		$$ = new LiteralJassExpression(BooleanJassValue.FALSE);
	}
	|
	ID OPEN_BRACKET expression CLOSE_BRACKET // ArrayReferenceExpression
	{
		$$ = new ArrayRefJassExpression($1, $3);
	}
	|
	functionExpression // FunctionCallExpression
	{
		$$ = $1;
		// Handled by functionExpression, no need to extra code in a call expr
	}
	|
	methodExpression // MethodCallExpression
	{
		$$ = $1;
	}
	|
	OPEN_PAREN expression CLOSE_PAREN // ParentheticalExpression
	{
		$$ = $2;
		// handled by expression
	}
	;
	
negatableExpression:
	baseExpression
	{
		$$ = $1;
	}
	|
	NOT negatableExpression // NotExpression
	{
		$$ = new NotJassExpression($2);
	}
	|
	MINUS negatableExpression // NegateExpression
	{
		$$ = new NegateJassExpression($2);
	}
	|
	METHOD baseExpression DOT ID //MethodReferenceExpression
	{
		$$ = new MethodReferenceJassExpression($2, $4);
	}
	;
	
expression:
	boolExpression
	{
		$$ = $1;
	};

functionExpression:
	ID OPEN_PAREN argsList CLOSE_PAREN
	{
		$$ = new FunctionCallJassExpression($1, $3);
	}
	;

methodExpression:
	baseExpression DOT ID OPEN_PAREN argsList CLOSE_PAREN
	{
		$$ = new MethodCallJassExpression($1, $3, $5);
	}
	|
	DOT ID OPEN_PAREN argsList CLOSE_PAREN
	{
		$$ = new ParentlessMethodCallJassExpression($2, $4);
	}
	;
	
argsList:
	expression // SingleArgument
	{
		LinkedList<JassExpression> list = new LinkedList<JassExpression>();
		list.addFirst($1);
		$$ = list;
	}
	|
	expression COMMA argsList // ListArgument
	{
		LinkedList<JassExpression> list = $3;
		list.addFirst($1);
		$$ = list;
	}
	|
	// EmptyArgument
	{
		$$ = new LinkedList<JassExpression>();
	}
	;

////booleanExpression:
//	simpleArithmeticExpression // PassBooleanThroughExpression
//	|

setPart:
	ID EQUALS expression //SetStatement
	{
		$$ = new JassSetStatement($1, $3);
	}
	|
	ID OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression // ArrayedAssignmentStatement
	{
		$$ = new JassArrayedAssignmentStatement($1, $3, $6);
	}
	|
	baseExpression DOT ID EQUALS expression //SetStatement
	{
		$$ = new JassSetMemberStatement($1, $3, $5);
	}
	;
	
callPart:
	functionExpression //CallStatement
	{
		$$ = new JassCallExpressionStatement($1);
	}
	|
	methodExpression //CallStatement
	{
		$$ = new JassCallExpressionStatement($1);
	}
	;
	
statement:
	CALL callPart // CallCallPart
	{
		$$ = $2;
	}
	|
	callPart // EasyCallPart
	{
		$$ = $1;
	}
	|
	SET setPart // SetSetPart
	{
		$$ = $2;
	}
	|
	setPart // EasySetPart
	{
		$$ = $1;
	}
	|
	ID PLUSPLUS
	{
		$$ = new JassSetStatement($1, new ArithmeticJassExpression(new ReferenceJassExpression($1), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.ADD));
	}
	|
	baseExpression DOT ID PLUSPLUS
	{
		$$ = new JassSetMemberStatement($1, $3, new ArithmeticJassExpression(new MemberJassExpression($1, $3), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.ADD));
	}
	|
	ID MINUSMINUS
	{
		$$ = new JassSetStatement($1, new ArithmeticJassExpression(new ReferenceJassExpression($1), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.SUBTRACT));
	}
	|
	baseExpression DOT ID MINUSMINUS
	{
		$$ = new JassSetMemberStatement($1, $3, new ArithmeticJassExpression(new MemberJassExpression($1, $3), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.SUBTRACT));
	}
	|
	RETURN expression // ReturnStatement
	{
		$$ = new JassReturnStatement($2);
	}
	|
	RETURN // ReturnNothingStatement
	{
		$$ = new JassReturnNothingStatement();
	}
	|
	EXITWHEN expression // ExitWhenStatement
	{
		$$ = new JassExitWhenStatement($2);
	}
	|
	local // LocalStatement
	{
		$$ = $1;
	}
	|
	LOOP statements_opt ENDLOOP // LoopStatement
	{
		$$ = new JassLoopStatement($2);
	}
	|
	IF ifStatementPartial // IfStatement
	{
		$$ = $2;
	}
	|
	DEBUG statement // DebugStatement
	{
		$$ = $2;
	}
	;
	
	 
ifStatementPartial:
	expression THEN statements_opt ENDIF // SimpleIfStatement
	{
		$$ = new JassIfStatement($1, $3);
	}
	|
	expression THEN statements_opt ELSE statements_opt ENDIF // IfElseStatement
	{
		$$ = new JassIfElseStatement($1, $3, $5);
	}
	|
	expression THEN statements_opt ELSEIF ifStatementPartial // IfElseIfStatement
	{
		$$ = new JassIfElseIfStatement($1, $3, $5);
	}
	;

param:
	type ID
	{
		$$ = new JassParameterDefinition($1, $2);
	}
	;
	
paramList:
	param // SingleParameter
	{
		LinkedList<JassParameterDefinition> list = new LinkedList<JassParameterDefinition>();
		list.addFirst($1);
		$$ = list;
	}
	|
	param COMMA paramList // ListParameter
	{
		LinkedList<JassParameterDefinition> list = $3;
		list.addFirst($1);
		$$ = list;
	}
	|
	NOTHING // NothingParameter
	{
		$$ = new LinkedList<JassParameterDefinition>();
	}
	;
	
requirement:
	ID
	{
		$$ = new JassLibraryRequirementDefinition($1, false);
	}
	|
	OPTIONAL ID
	{
		$$ = new JassLibraryRequirementDefinition($2, true);
	}
	;
	
requirementList:
	requirement
	{
		LinkedList<JassLibraryRequirementDefinition> list = new LinkedList<>();
		list.addFirst($1);
		$$ = list;
	}
	|
	requirementList COMMA requirement
	{
		LinkedList<JassLibraryRequirementDefinition> list = $1;
		list.addLast($3);
		$$ = list;
	}
	;
	
requirementList_opt:
	REQUIRES requirementList
	{
		$$ = $2;
	}
	|
	{
		$$ = new LinkedList<JassLibraryRequirementDefinition>(); // maybe use Collections.emptyList later
	}
	;

globals:
	global
	{
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		list.addFirst($1);
		$$ = list;
	}
	|
	globals newlines global
	{
		LinkedList<JassStatement> list = $1;
		list.addLast($3);
		$$ = list;
	}
	;
	
globals_opt:
	newlines globals newlines
	{
		$$ = $2;
	}
	|
	newlines
	{
		$$ = new LinkedList<JassStatement>();
	}
	;

globalsBlock:
	GLOBALS globals_opt ENDGLOBALS
	{
		$$ = new JassGlobalsDefinitionBlock(getLine(), currentParsingFilePath, $2);
	};
	
nativeBlock:
	qualifiers_opt NATIVE ID TAKES paramList RETURNS type
	{
		final String text = $3;
		$$ = new JassNativeDefinitionBlock(getLine(), currentParsingFilePath, text, $5, $7);
	}
	;
	
functionBlock:
	qualifiers_opt FUNCTION ID TAKES paramList RETURNS type statements_opt ENDFUNCTION
	{
		$$ = new JassFunctionDefinitionBlock(getLine(), currentParsingFilePath, $1, $3, $8, $5, $7);
	}
	;
	
methodBlock:
	qualifiers_opt METHOD ID TAKES paramList RETURNS type statements_opt ENDMETHOD
	{
		$$ = new JassMethodDefinitionBlock(getLine(), currentParsingFilePath, $1, $3, $8, $5, $7);
	}
	;
	
implementModuleStatement:
	IMPLEMENT ID
	{
		$$ = new JassImplementModuleDefinition($2, false);
	}
	|
	IMPLEMENT OPTIONAL ID
	{
		$$ = new JassImplementModuleDefinition($3, true);
	}
	;
	
defaultsTail:
	DEFAULTS expression
	{
		$$ = $2;
	}
	|
	DEFAULTS NOTHING
	{
		$$ = new LiteralJassExpression(null);
	}
	|
	{
		$$ = null;
	}
	;
	
interfaceMethodBlock:
	qualifiers_opt METHOD ID TAKES paramList RETURNS type defaultsTail
	{
		$$ = JassMethodDefinitionBlock.createInterfaceMethod(getLine(), currentParsingFilePath, $1, $3, $5, $7, $8);
	}
	;
	
libraryBlock:
	LIBRARY ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY
	{
		$$ = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, $2, $3, $4, null, true);
	}
	|
	LIBRARY ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY
	{
		$$ = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, $2, $5, $6, $4, true);
	}
	|
	LIBRARY_ONCE ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY
	{
		$$ = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, $2, $3, $4, null, true);
	}
	|
	LIBRARY_ONCE ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY
	{
		$$ = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, $2, $5, $6, $4, true);
	}
	;
	
scopeBlock:
	SCOPE ID nonLibraryBlocks_opt ENDSCOPE
	{
		$$ = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, $2, $3, null);
	}
	|
	SCOPE ID INITIALIZER ID nonLibraryBlocks_opt ENDSCOPE
	{
		$$ = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, $2, $5, $4);
	}
	;
	
extends_opt:
	EXTENDS type
	{
		$$ = $2;
	}
	|
	{
		$$ = NothingJassTypeToken.INSTANCE;
	}
	;
	
structDeclarationBlock:
	qualifiers_opt STRUCT ID extends_opt
	{
		currentStruct = new JassStructDefinitionBlock($1, $3, $4);
	}
	structStatements_opt ENDSTRUCT
	{
		$$ = currentStruct;
	}
	;
	
interfaceDeclarationBlock:
	qualifiers_opt INTERFACE ID extends_opt
	{
		currentStruct = new JassStructDefinitionBlock($1, $3, $4);
	}
	interfaceStatements_opt ENDINTERFACE
	{
		$$ = currentStruct;
	}
	;
	
moduleDeclarationBlock:
	qualifiers_opt MODULE ID
	{
		currentStruct = new JassModuleDefinitionBlock($1, $3);
	}
	structStatements_opt ENDMODULE
	{
		$$ = currentStruct;
	}
	;
	
nonLibraryBlock:
	globalsBlock
	{
		$$ = $1;
	}
	|
	nativeBlock
	{
		$$ = $1;
	}
	|
	functionBlock
	{
		$$ = $1;
	}
	|
	typeDeclarationBlock
	{
		$$ = $1;
	}
	|
	structDeclarationBlock
	{
		$$ = $1;
	}
	|
	interfaceDeclarationBlock
	{
		$$ = $1;
	}
	|
	moduleDeclarationBlock
	{
		$$ = $1;
	}
	|
	scopeBlock
	{
		$$ = $1;
	}
	;
	
block:
	nonLibraryBlock
	{
		$$ = $1;
	}
	|
	libraryBlock
	{
		$$ = $1;
	}
	;
	
blocks:
	block
	{
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst($1);
		$$ = list;
	}
	|
	blocks newlines block
	{
		LinkedList<JassDefinitionBlock> list = $1;
		list.addLast($3);
		$$ = list;
	}
	;
	
nonLibraryBlocks:
	nonLibraryBlock
	{
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst($1);
		$$ = list;
	}
	|
	nonLibraryBlocks newlines nonLibraryBlock
	{
		LinkedList<JassDefinitionBlock> list = $1;
		list.addLast($3);
		$$ = list;
	}
	;
	
statements:
	statement
	{
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		JassStatement statement;
		if (JassSettings.DEBUG) {
			statement = new DebuggingJassStatement(getLine(), $1);
		} else {
			statement = $1;
		}
		list.addFirst(statement);
		$$ = list;
	}
	|
	statements newlines statement
	{
		LinkedList<JassStatement> list = $1;
		JassStatement statement;
		if (JassSettings.DEBUG) {
			statement = new DebuggingJassStatement(getLine(), $3);
		} else {
			statement = $3;
		}
		list.addLast(statement);
		$$ = list;
	}
	;
	
statements_opt:
	newlines statements newlines
	{
		$$ = $2;
	}
	|
	newlines
	{
		$$ = new LinkedList<JassStatement>();
	}
	;
	
blocks_opt:
	newlines_opt blocks newlines_opt
	{
		$$ = $2;
	}
	|
	newlines_opt
	{
		$$ = new LinkedList<JassDefinitionBlock>();
	}
	;
	
nonLibraryBlocks_opt:
	newlines nonLibraryBlocks newlines
	{
		$$ = $2;
	}
	|
	newlines
	{
		$$ = new LinkedList<JassDefinitionBlock>();
	}
	;
	
structStatement:
	member
	{
		currentStruct.add($1);
	}
	|
	methodBlock
	{
		currentStruct.add($1);
	}
	|
	implementModuleStatement
	{
		currentStruct.add($1);
	}
	;
	
interfaceStatement:
	member
	{
		currentStruct.add($1);
	}
	|
	interfaceMethodBlock
	{
		currentStruct.add($1);
	}
	;
	
structStatements:
	structStatement
	|
	structStatements newlines structStatement
	;
	
interfaceStatements:
	interfaceStatement
	|
	interfaceStatements newlines interfaceStatement
	;
	
structStatements_opt:
	newlines structStatements newlines
	|
	newlines
	;
	
interfaceStatements_opt:
	newlines interfaceStatements newlines
	|
	newlines
	;
	
newlines:
	NEWLINE
	|
	NEWLINE newlines
	;
	
newlines_opt:
	newlines
	|
	;


%%
