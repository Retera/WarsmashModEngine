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
	private InstructionWriter instructionWriter;
	
	public void scanAndParse(String currentParsingFilePath, GlobalScope globalScope, JassNativeManager jassNativeManager) throws IOException {
		this.currentParsingFilePath = currentParsingFilePath;
		this.globalScope = globalScope;
		this.jassNativeManager = jassNativeManager;
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
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.expression.ArithmeticSigns;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;
import com.etheller.interpreter.ast.util.JassSettings;
}

%lex-param { Reader r }
%language "Java"
%define api.prefix {SmashJass}
%define api.package {net.warsmash.parsers.jass}
%define api.parser.public

%token EQUALS PLUSEQUALS MINUSEQUALS PLUSPLUS MINUSMINUS GLOBALS ENDGLOBALS NATIVE FUNCTION TAKES RETURNS ENDFUNCTION NOTHING CALL SET RETURN ARRAY TYPE EXTENDS IF THEN ELSE ENDIF ELSEIF CONSTANT LOCAL LOOP ENDLOOP EXITWHEN DEBUG NULL TRUE FALSE NOT OR AND NEWLINE TIMES DIVIDE PLUS MINUS LESS GREATER LESS_EQUALS GREATER_EQUALS DOUBLE_EQUALS NOT_EQUALS OPEN_BRACKET CLOSE_BRACKET OPEN_PAREN CLOSE_PAREN COMMA
%token <String> ID STRING_LITERAL
%token <int> INTEGER HEX_CONSTANT DOLLAR_HEX_CONSTANT RAWCODE
%token <double> REAL

%type <JassType> type
%type <LinkedList<JassParameter>> paramList
%type <JassParameter> param
%type <int> argsList

%%

program :
	newlines
	|
	newlines_opt
	blocks
	newlines_opt
	;

typeDeclarationBlock :
	TYPE ID EXTENDS ID
	;  

type :
	ID
	{
		$$ = globalScope.parseType($1);
	}
	|
	ID ARRAY
	{
		$$ = globalScope.parseArrayType($1);
	}
	|
	NOTHING
	{
		$$ = JassType.NOTHING;
	}
	;

constant_opt:
	CONSTANT
	|
	;

global : 
	constant_opt type ID newlines
	|
	constant_opt type ID assignTail newlines
	;
local : 
	LOCAL type ID
	{
		instructionWriter.declareLocal($3);
		instructionWriter.pushDefaultValue($2);
	}
	|
	LOCAL type ID
	{
		instructionWriter.declareLocal($3);
	}
	assignTail
	;
	
assignTail:
	EQUALS expression;
	
multDivExpression:
	multDivExpression TIMES baseExpression 
	{
		instructionWriter.arithmetic(ArithmeticSigns.MULTIPLY);
	}
	|
	multDivExpression DIVIDE baseExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.DIVIDE);
	}
	|
	baseExpression
	;
	
simpleArithmeticExpression:
	simpleArithmeticExpression PLUS multDivExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.ADD);
	}
	|
	simpleArithmeticExpression MINUS multDivExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.SUBTRACT);
	}
	|
	multDivExpression // BaseAdditionExpression
	;
	
boolComparisonExpression:
	boolComparisonExpression LESS simpleArithmeticExpression // BooleanLessExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.LESS);
	}
	|
	boolComparisonExpression GREATER simpleArithmeticExpression // BooleanGreaterExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.GREATER);
	}
	|
	boolComparisonExpression LESS_EQUALS simpleArithmeticExpression // BooleanLessOrEqualsExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.LESS_OR_EQUALS);
	}
	|
	boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression // BooleanGreaterOrEqualsExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.GREATER_OR_EQUALS);
	}
	|
	simpleArithmeticExpression // BaseBoolComparisonExpression
	;
	
boolEqualityExpression:
	boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression // EqualsExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.EQUALS);
	}
	|
	boolEqualityExpression NOT_EQUALS boolComparisonExpression // NotEqualsExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.NOT_EQUALS);
	}
	|
	boolComparisonExpression // BaseBoolExpression
	;
	
boolAndsExpression:
	boolAndsExpression AND boolEqualityExpression // BooleanAndExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.AND);
	}
	|
	boolEqualityExpression // BaseBoolAndsExpression
	;
	
boolExpression:
	boolExpression OR boolAndsExpression // BooleanOrExpression
	{
		instructionWriter.arithmetic(ArithmeticSigns.OR);
	}
	|
	boolAndsExpression // BaseBoolOrsExpression
	;
	
baseExpression:
	ID // ReferenceExpression
	{
		instructionWriter.referenceExpression($1);
	}
	|
	STRING_LITERAL //StringLiteralExpression
	{
		instructionWriter.stringLiteral($1);
	}
	|
	INTEGER //IntegerLiteralExpression
	{
		instructionWriter.integerLiteral($1);
	}
	|
	HEX_CONSTANT //HexIntegerLiteralExpression
	{
		instructionWriter.integerLiteral($1);
	}
	|
	DOLLAR_HEX_CONSTANT //DollarHexIntegerLiteralExpression
	{
		instructionWriter.integerLiteral($1);
	}
	|
	RAWCODE //RawcodeLiteralExpression
	{
		instructionWriter.integerLiteral($1);
	}
	|
	REAL //RealLiteralExpression
	{
		instructionWriter.realLiteral($1);
	}
	|
	FUNCTION ID //FunctionReferenceExpression
	{
		instructionWriter.functionReference($2);
	}
	|
	NULL // NullExpression
	{
		instructionWriter.literal(null);
	}
	|
	TRUE // TrueExpression
	{
		instructionWriter.booleanLiteral(true);
	}
	|
	FALSE // FalseExpression
	{
		instructionWriter.booleanLiteral(false);
	}
	|
	ID
	{
		instructionWriter.referenceExpression($1);
	}
	OPEN_BRACKET expression CLOSE_BRACKET // ArrayReferenceExpression
	{
		instructionWriter.arrayReferenceInstruction();
	}
	|
	functionExpression // FunctionCallExpression
	// Handled by functionExpression, no need to extra code in a call expr
	|
	OPEN_PAREN expression CLOSE_PAREN // ParentheticalExpression
	// handled by expression
	|
	NOT baseExpression // NotExpression
	{
		instructionWriter.notInstruction();
	}
	|
	MINUS baseExpression // NegateExpression
	{
		instructionWriter.negateInstruction();
	}
	;
	
expression:
	boolExpression;

functionExpression:
	ID OPEN_PAREN argsList CLOSE_PAREN
	{
		instructionWriter.call($1, $3);
	}
	;
	
argsList:
	expression // SingleArgument
	{
		$$ = 1;
	}
	|
	expression COMMA argsList // ListArgument
	{
		$$ = $3 + 1;
	}
	|
	// EmptyArgument
	{
		$$ = 0;
	}
	;

////booleanExpression:
//	simpleArithmeticExpression // PassBooleanThroughExpression
//	|

setPart:
	ID EQUALS expression //SetStatement
	{
		instructionWriter.set($1);
	}
	|
	ID OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression // ArrayedAssignmentStatement
	{
		instructionWriter.arrayedAssignmentStatement($1);
	}
	;
	
callPart:
	functionExpression //CallStatement
	{
		instructionWriter.popInstruction(); // ignore return value
	}
	;
	
statement:
	CALL callPart // CallCallPart
	|
	callPart // EasyCallPart
	|
	SET setPart // SetSetPart
	|
	setPart // EasySetPart
	|
	RETURN expression // ReturnStatement
	{
		instructionWriter.returnInstruction();
	}
	|
	RETURN // ReturnNothingStatement
	{
		instructionWriter.returnNothingInstruction();
	}
	|
	EXITWHEN expression // ExitWhenStatement
	{
		instructionWriter.exitWhenStatement();
	}
	|
	local // LocalStatement
	|
	LOOP {
		instructionWriter.loop();
	}
	statements_opt ENDLOOP // LoopStatement
	{
		instructionWriter.endloop();
	}
	|
	IF ifStatementPartial // IfStatement
	|
	DEBUG statement // DebugStatement
	;
	
conditionalThen:
	expression THEN
	{
		instructionWriter.beginIf();
	}
	;
	 
ifStatementPartial:
	conditionalThen statements_opt ENDIF // SimpleIfStatement
	{
		instructionWriter.endIf();
	}
	|
	conditionalThen statements_opt ELSE
	{
		instructionWriter.beginElse();
	}
	statements_opt ENDIF // IfElseStatement
	{
		instructionWriter.endElse();
	}
	|
	conditionalThen statements_opt ELSEIF
	{
		instructionWriter.beginElse();
	}
	ifStatementPartial // IfElseIfStatement
	{
		instructionWriter.endElse();
	}
	;

param:
	type ID
	{
		$$ = new JassParameter($1, $2);
	}
	;
	
paramList:
	param // SingleParameter
	{
		LinkedList<JassParameter> list = new LinkedList<JassParameter>();
		list.addFirst($1);
		$$ = list;
	}
	|
	param COMMA paramList // ListParameter
	{
		LinkedList<JassParameter> list = $3;
		list.addFirst($1);
		$$ = list;
	}
	|
	NOTHING // NothingParameter
	{
		$$ = new LinkedList<JassParameter>();
	}
	;
	
globals:
	global
	|
	globals global
	;

globalsBlock :
	GLOBALS newlines globals ENDGLOBALS;
	
nativeBlock:
	constant_opt NATIVE ID TAKES paramList RETURNS type
	{
		final String text = $3;
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Registering native: " + text);
		}
		jassNativeManager.registerNativeCode(getLine(), currentParsingFilePath, text, $5, $7, globalScope);
	}
	;
	
functionBlock:
	constant_opt FUNCTION ID TAKES paramList
	{
		instructionWriter = globalScope.beginDefiningFunction(getLine(), currentParsingFilePath, $3, $5);
	}
	RETURNS type statements_opt ENDFUNCTION
	{
		instructionWriter.endFunction();
	}
	;
	
block:
	globalsBlock
	|
	nativeBlock
	|
	functionBlock
	|
	typeDeclarationBlock
	;
	
blocks:
	block
	|
	blocks newlines block
	;
	
statements:
	{
		if (JassSettings.DEBUG) {
			instructionWriter.setLineNo(getLine());
		}
	}
	statement
	|
	statements newlines
	{
		if (JassSettings.DEBUG) {
			instructionWriter.setLineNo(getLine());
		}
	}
	statement
	;
	
statements_opt:
	newlines statements newlines
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