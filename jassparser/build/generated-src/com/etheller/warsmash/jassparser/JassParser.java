// Generated from Jass.g4 by ANTLR 4.7

	package com.etheller.interpreter;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JassParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, EQUALS=6, GLOBALS=7, ENDGLOBALS=8, 
		NATIVE=9, FUNCTION=10, TAKES=11, RETURNS=12, ENDFUNCTION=13, NOTHING=14, 
		CALL=15, SET=16, RETURN=17, ARRAY=18, TYPE=19, EXTENDS=20, IF=21, THEN=22, 
		ELSE=23, ENDIF=24, ELSEIF=25, CONSTANT=26, STRING_LITERAL=27, INTEGER=28, 
		NULL=29, TRUE=30, FALSE=31, NOT=32, ID=33, WS=34, NEWLINE=35;
	public static final int
		RULE_program = 0, RULE_typeDefinition = 1, RULE_type = 2, RULE_global = 3, 
		RULE_assignTail = 4, RULE_expression = 5, RULE_functionExpression = 6, 
		RULE_argsList = 7, RULE_statement = 8, RULE_ifStatementPartial = 9, RULE_param = 10, 
		RULE_paramList = 11, RULE_globalsBlock = 12, RULE_typeDefinitionBlock = 13, 
		RULE_nativeBlock = 14, RULE_block = 15, RULE_functionBlock = 16, RULE_statements = 17, 
		RULE_newlines = 18, RULE_newlines_opt = 19, RULE_pnewlines = 20;
	public static final String[] ruleNames = {
		"program", "typeDefinition", "type", "global", "assignTail", "expression", 
		"functionExpression", "argsList", "statement", "ifStatementPartial", "param", 
		"paramList", "globalsBlock", "typeDefinitionBlock", "nativeBlock", "block", 
		"functionBlock", "statements", "newlines", "newlines_opt", "pnewlines"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "']'", "'('", "')'", "','", "'='", "'globals'", "'endglobals'", 
		"'native'", "'function'", "'takes'", "'returns'", "'endfunction'", "'nothing'", 
		"'call'", "'set'", "'return'", "'array'", "'type'", "'extends'", "'if'", 
		"'then'", "'else'", "'endif'", "'elseif'", "'constant'", null, null, "'null'", 
		"'true'", "'false'", "'not'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "EQUALS", "GLOBALS", "ENDGLOBALS", 
		"NATIVE", "FUNCTION", "TAKES", "RETURNS", "ENDFUNCTION", "NOTHING", "CALL", 
		"SET", "RETURN", "ARRAY", "TYPE", "EXTENDS", "IF", "THEN", "ELSE", "ENDIF", 
		"ELSEIF", "CONSTANT", "STRING_LITERAL", "INTEGER", "NULL", "TRUE", "FALSE", 
		"NOT", "ID", "WS", "NEWLINE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Jass.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JassParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public Newlines_optContext newlines_opt() {
			return getRuleContext(Newlines_optContext.class,0);
		}
		public TypeDefinitionBlockContext typeDefinitionBlock() {
			return getRuleContext(TypeDefinitionBlockContext.class,0);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public List<FunctionBlockContext> functionBlock() {
			return getRuleContexts(FunctionBlockContext.class);
		}
		public FunctionBlockContext functionBlock(int i) {
			return getRuleContext(FunctionBlockContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			setState(57);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(42);
				newlines();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(43);
				newlines_opt();
				setState(44);
				typeDefinitionBlock();
				setState(48);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GLOBALS) | (1L << NATIVE) | (1L << CONSTANT))) != 0)) {
					{
					{
					setState(45);
					block();
					}
					}
					setState(50);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FUNCTION) {
					{
					{
					setState(51);
					functionBlock();
					}
					}
					setState(56);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeDefinitionContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(JassParser.TYPE, 0); }
		public List<TerminalNode> ID() { return getTokens(JassParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(JassParser.ID, i);
		}
		public TerminalNode EXTENDS() { return getToken(JassParser.EXTENDS, 0); }
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public TypeDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDefinition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitTypeDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeDefinitionContext typeDefinition() throws RecognitionException {
		TypeDefinitionContext _localctx = new TypeDefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_typeDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(TYPE);
			setState(60);
			match(ID);
			setState(61);
			match(EXTENDS);
			setState(62);
			match(ID);
			setState(63);
			newlines();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArrayTypeContext extends TypeContext {
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public TerminalNode ARRAY() { return getToken(JassParser.ARRAY, 0); }
		public ArrayTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BasicTypeContext extends TypeContext {
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public BasicTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitBasicType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NothingTypeContext extends TypeContext {
		public TerminalNode NOTHING() { return getToken(JassParser.NOTHING, 0); }
		public NothingTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNothingType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			setState(69);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new BasicTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				match(ID);
				}
				break;
			case 2:
				_localctx = new ArrayTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				match(ID);
				setState(67);
				match(ARRAY);
				}
				break;
			case 3:
				_localctx = new NothingTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(68);
				match(NOTHING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GlobalContext extends ParserRuleContext {
		public GlobalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global; }
	 
		public GlobalContext() { }
		public void copyFrom(GlobalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DefinitionGlobalContext extends GlobalContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public AssignTailContext assignTail() {
			return getRuleContext(AssignTailContext.class,0);
		}
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(JassParser.CONSTANT, 0); }
		public DefinitionGlobalContext(GlobalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitDefinitionGlobal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BasicGlobalContext extends GlobalContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(JassParser.CONSTANT, 0); }
		public BasicGlobalContext(GlobalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitBasicGlobal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlobalContext global() throws RecognitionException {
		GlobalContext _localctx = new GlobalContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_global);
		int _la;
		try {
			setState(86);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new BasicGlobalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONSTANT) {
					{
					setState(71);
					match(CONSTANT);
					}
				}

				setState(74);
				type();
				setState(75);
				match(ID);
				setState(76);
				newlines();
				}
				break;
			case 2:
				_localctx = new DefinitionGlobalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CONSTANT) {
					{
					setState(78);
					match(CONSTANT);
					}
				}

				setState(81);
				type();
				setState(82);
				match(ID);
				setState(83);
				assignTail();
				setState(84);
				newlines();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignTailContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(JassParser.EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignTailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignTail; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitAssignTail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignTailContext assignTail() throws RecognitionException {
		AssignTailContext _localctx = new AssignTailContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_assignTail);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(EQUALS);
			setState(89);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TrueExpressionContext extends ExpressionContext {
		public TerminalNode TRUE() { return getToken(JassParser.TRUE, 0); }
		public TrueExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitTrueExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParentheticalExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParentheticalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitParentheticalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringLiteralExpressionContext extends ExpressionContext {
		public TerminalNode STRING_LITERAL() { return getToken(JassParser.STRING_LITERAL, 0); }
		public StringLiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitStringLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntegerLiteralExpressionContext extends ExpressionContext {
		public TerminalNode INTEGER() { return getToken(JassParser.INTEGER, 0); }
		public IntegerLiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitIntegerLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReferenceExpressionContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public ReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionReferenceExpressionContext extends ExpressionContext {
		public TerminalNode FUNCTION() { return getToken(JassParser.FUNCTION, 0); }
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public FunctionReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitFunctionReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotExpressionContext extends ExpressionContext {
		public TerminalNode NOT() { return getToken(JassParser.NOT, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNotExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArrayReferenceExpressionContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArrayReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitArrayReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallExpressionContext extends ExpressionContext {
		public FunctionExpressionContext functionExpression() {
			return getRuleContext(FunctionExpressionContext.class,0);
		}
		public FunctionCallExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitFunctionCallExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullExpressionContext extends ExpressionContext {
		public TerminalNode NULL() { return getToken(JassParser.NULL, 0); }
		public NullExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNullExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FalseExpressionContext extends ExpressionContext {
		public TerminalNode FALSE() { return getToken(JassParser.FALSE, 0); }
		public FalseExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitFalseExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expression);
		try {
			setState(111);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new ReferenceExpressionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(91);
				match(ID);
				}
				break;
			case 2:
				_localctx = new StringLiteralExpressionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(92);
				match(STRING_LITERAL);
				}
				break;
			case 3:
				_localctx = new IntegerLiteralExpressionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(93);
				match(INTEGER);
				}
				break;
			case 4:
				_localctx = new FunctionReferenceExpressionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(94);
				match(FUNCTION);
				setState(95);
				match(ID);
				}
				break;
			case 5:
				_localctx = new NullExpressionContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(96);
				match(NULL);
				}
				break;
			case 6:
				_localctx = new TrueExpressionContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(97);
				match(TRUE);
				}
				break;
			case 7:
				_localctx = new FalseExpressionContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(98);
				match(FALSE);
				}
				break;
			case 8:
				_localctx = new ArrayReferenceExpressionContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(99);
				match(ID);
				setState(100);
				match(T__0);
				setState(101);
				expression();
				setState(102);
				match(T__1);
				}
				break;
			case 9:
				_localctx = new FunctionCallExpressionContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(104);
				functionExpression();
				}
				break;
			case 10:
				_localctx = new ParentheticalExpressionContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(105);
				match(T__2);
				setState(106);
				expression();
				setState(107);
				match(T__3);
				}
				break;
			case 11:
				_localctx = new NotExpressionContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(109);
				match(NOT);
				setState(110);
				expression();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionExpressionContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public ArgsListContext argsList() {
			return getRuleContext(ArgsListContext.class,0);
		}
		public FunctionExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionExpression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitFunctionExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionExpressionContext functionExpression() throws RecognitionException {
		FunctionExpressionContext _localctx = new FunctionExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_functionExpression);
		try {
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(113);
				match(ID);
				setState(114);
				match(T__2);
				setState(115);
				argsList();
				setState(116);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(118);
				match(ID);
				setState(119);
				match(T__2);
				setState(120);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgsListContext extends ParserRuleContext {
		public ArgsListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argsList; }
	 
		public ArgsListContext() { }
		public void copyFrom(ArgsListContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleArgumentContext extends ArgsListContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SingleArgumentContext(ArgsListContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitSingleArgument(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListArgumentContext extends ArgsListContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArgsListContext argsList() {
			return getRuleContext(ArgsListContext.class,0);
		}
		public ListArgumentContext(ArgsListContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitListArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgsListContext argsList() throws RecognitionException {
		ArgsListContext _localctx = new ArgsListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_argsList);
		try {
			setState(128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new SingleArgumentContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(123);
				expression();
				}
				break;
			case 2:
				_localctx = new ListArgumentContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(124);
				expression();
				setState(125);
				match(T__4);
				setState(126);
				argsList();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ArrayedAssignmentStatementContext extends StatementContext {
		public TerminalNode SET() { return getToken(JassParser.SET, 0); }
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EQUALS() { return getToken(JassParser.EQUALS, 0); }
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public ArrayedAssignmentStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitArrayedAssignmentStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfStatementContext extends StatementContext {
		public TerminalNode IF() { return getToken(JassParser.IF, 0); }
		public IfStatementPartialContext ifStatementPartial() {
			return getRuleContext(IfStatementPartialContext.class,0);
		}
		public IfStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReturnStatementContext extends StatementContext {
		public TerminalNode RETURN() { return getToken(JassParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public ReturnStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CallStatementContext extends StatementContext {
		public TerminalNode CALL() { return getToken(JassParser.CALL, 0); }
		public FunctionExpressionContext functionExpression() {
			return getRuleContext(FunctionExpressionContext.class,0);
		}
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public CallStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitCallStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SetStatementContext extends StatementContext {
		public TerminalNode SET() { return getToken(JassParser.SET, 0); }
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public TerminalNode EQUALS() { return getToken(JassParser.EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public SetStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitSetStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_statement);
		try {
			setState(155);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				_localctx = new CallStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				match(CALL);
				setState(131);
				functionExpression();
				setState(132);
				newlines();
				}
				break;
			case 2:
				_localctx = new SetStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				match(SET);
				setState(135);
				match(ID);
				setState(136);
				match(EQUALS);
				setState(137);
				expression();
				setState(138);
				newlines();
				}
				break;
			case 3:
				_localctx = new ArrayedAssignmentStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
				match(SET);
				setState(141);
				match(ID);
				setState(142);
				match(T__0);
				setState(143);
				expression();
				setState(144);
				match(T__1);
				setState(145);
				match(EQUALS);
				setState(146);
				expression();
				setState(147);
				newlines();
				}
				break;
			case 4:
				_localctx = new ReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(149);
				match(RETURN);
				setState(150);
				expression();
				setState(151);
				newlines();
				}
				break;
			case 5:
				_localctx = new IfStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(153);
				match(IF);
				setState(154);
				ifStatementPartial();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStatementPartialContext extends ParserRuleContext {
		public IfStatementPartialContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatementPartial; }
	 
		public IfStatementPartialContext() { }
		public void copyFrom(IfStatementPartialContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class IfElseIfStatementContext extends IfStatementPartialContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode THEN() { return getToken(JassParser.THEN, 0); }
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public TerminalNode ELSEIF() { return getToken(JassParser.ELSEIF, 0); }
		public IfStatementPartialContext ifStatementPartial() {
			return getRuleContext(IfStatementPartialContext.class,0);
		}
		public IfElseIfStatementContext(IfStatementPartialContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitIfElseIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfElseStatementContext extends IfStatementPartialContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode THEN() { return getToken(JassParser.THEN, 0); }
		public List<NewlinesContext> newlines() {
			return getRuleContexts(NewlinesContext.class);
		}
		public NewlinesContext newlines(int i) {
			return getRuleContext(NewlinesContext.class,i);
		}
		public List<StatementsContext> statements() {
			return getRuleContexts(StatementsContext.class);
		}
		public StatementsContext statements(int i) {
			return getRuleContext(StatementsContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(JassParser.ELSE, 0); }
		public TerminalNode ENDIF() { return getToken(JassParser.ENDIF, 0); }
		public IfElseStatementContext(IfStatementPartialContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitIfElseStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SimpleIfStatementContext extends IfStatementPartialContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode THEN() { return getToken(JassParser.THEN, 0); }
		public List<NewlinesContext> newlines() {
			return getRuleContexts(NewlinesContext.class);
		}
		public NewlinesContext newlines(int i) {
			return getRuleContext(NewlinesContext.class,i);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public TerminalNode ENDIF() { return getToken(JassParser.ENDIF, 0); }
		public SimpleIfStatementContext(IfStatementPartialContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitSimpleIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStatementPartialContext ifStatementPartial() throws RecognitionException {
		IfStatementPartialContext _localctx = new IfStatementPartialContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_ifStatementPartial);
		try {
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new SimpleIfStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				expression();
				setState(158);
				match(THEN);
				setState(159);
				newlines();
				setState(160);
				statements();
				setState(161);
				match(ENDIF);
				setState(162);
				newlines();
				}
				break;
			case 2:
				_localctx = new IfElseStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				expression();
				setState(165);
				match(THEN);
				setState(166);
				newlines();
				setState(167);
				statements();
				setState(168);
				match(ELSE);
				setState(169);
				newlines();
				setState(170);
				statements();
				setState(171);
				match(ENDIF);
				setState(172);
				newlines();
				}
				break;
			case 3:
				_localctx = new IfElseIfStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(174);
				expression();
				setState(175);
				match(THEN);
				setState(176);
				newlines();
				setState(177);
				statements();
				setState(178);
				match(ELSEIF);
				setState(179);
				ifStatementPartial();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			type();
			setState(184);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamListContext extends ParserRuleContext {
		public ParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramList; }
	 
		public ParamListContext() { }
		public void copyFrom(ParamListContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NothingParameterContext extends ParamListContext {
		public TerminalNode NOTHING() { return getToken(JassParser.NOTHING, 0); }
		public NothingParameterContext(ParamListContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNothingParameter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleParameterContext extends ParamListContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public SingleParameterContext(ParamListContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitSingleParameter(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListParameterContext extends ParamListContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public ListParameterContext(ParamListContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitListParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamListContext paramList() throws RecognitionException {
		ParamListContext _localctx = new ParamListContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_paramList);
		try {
			setState(192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				_localctx = new SingleParameterContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				param();
				}
				break;
			case 2:
				_localctx = new ListParameterContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				param();
				setState(188);
				match(T__4);
				setState(189);
				paramList();
				}
				break;
			case 3:
				_localctx = new NothingParameterContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(191);
				match(NOTHING);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GlobalsBlockContext extends ParserRuleContext {
		public TerminalNode GLOBALS() { return getToken(JassParser.GLOBALS, 0); }
		public List<NewlinesContext> newlines() {
			return getRuleContexts(NewlinesContext.class);
		}
		public NewlinesContext newlines(int i) {
			return getRuleContext(NewlinesContext.class,i);
		}
		public TerminalNode ENDGLOBALS() { return getToken(JassParser.ENDGLOBALS, 0); }
		public List<GlobalContext> global() {
			return getRuleContexts(GlobalContext.class);
		}
		public GlobalContext global(int i) {
			return getRuleContext(GlobalContext.class,i);
		}
		public GlobalsBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_globalsBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitGlobalsBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlobalsBlockContext globalsBlock() throws RecognitionException {
		GlobalsBlockContext _localctx = new GlobalsBlockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_globalsBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(GLOBALS);
			setState(195);
			newlines();
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOTHING) | (1L << CONSTANT) | (1L << ID))) != 0)) {
				{
				{
				setState(196);
				global();
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			match(ENDGLOBALS);
			setState(203);
			newlines();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeDefinitionBlockContext extends ParserRuleContext {
		public List<TypeDefinitionContext> typeDefinition() {
			return getRuleContexts(TypeDefinitionContext.class);
		}
		public TypeDefinitionContext typeDefinition(int i) {
			return getRuleContext(TypeDefinitionContext.class,i);
		}
		public TypeDefinitionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDefinitionBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitTypeDefinitionBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeDefinitionBlockContext typeDefinitionBlock() throws RecognitionException {
		TypeDefinitionBlockContext _localctx = new TypeDefinitionBlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typeDefinitionBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(205);
				typeDefinition();
				}
				}
				setState(210);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NativeBlockContext extends ParserRuleContext {
		public TerminalNode NATIVE() { return getToken(JassParser.NATIVE, 0); }
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public TerminalNode TAKES() { return getToken(JassParser.TAKES, 0); }
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public TerminalNode RETURNS() { return getToken(JassParser.RETURNS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public TerminalNode CONSTANT() { return getToken(JassParser.CONSTANT, 0); }
		public NativeBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nativeBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNativeBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NativeBlockContext nativeBlock() throws RecognitionException {
		NativeBlockContext _localctx = new NativeBlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_nativeBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CONSTANT) {
				{
				setState(211);
				match(CONSTANT);
				}
			}

			setState(214);
			match(NATIVE);
			setState(215);
			match(ID);
			setState(216);
			match(TAKES);
			setState(217);
			paramList();
			setState(218);
			match(RETURNS);
			setState(219);
			type();
			setState(220);
			newlines();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public GlobalsBlockContext globalsBlock() {
			return getRuleContext(GlobalsBlockContext.class,0);
		}
		public NativeBlockContext nativeBlock() {
			return getRuleContext(NativeBlockContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_block);
		try {
			setState(224);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case GLOBALS:
				enterOuterAlt(_localctx, 1);
				{
				setState(222);
				globalsBlock();
				}
				break;
			case NATIVE:
			case CONSTANT:
				enterOuterAlt(_localctx, 2);
				{
				setState(223);
				nativeBlock();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionBlockContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(JassParser.FUNCTION, 0); }
		public TerminalNode ID() { return getToken(JassParser.ID, 0); }
		public TerminalNode TAKES() { return getToken(JassParser.TAKES, 0); }
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public TerminalNode RETURNS() { return getToken(JassParser.RETURNS, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<NewlinesContext> newlines() {
			return getRuleContexts(NewlinesContext.class);
		}
		public NewlinesContext newlines(int i) {
			return getRuleContext(NewlinesContext.class,i);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public TerminalNode ENDFUNCTION() { return getToken(JassParser.ENDFUNCTION, 0); }
		public FunctionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionBlock; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitFunctionBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionBlockContext functionBlock() throws RecognitionException {
		FunctionBlockContext _localctx = new FunctionBlockContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_functionBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(FUNCTION);
			setState(227);
			match(ID);
			setState(228);
			match(TAKES);
			setState(229);
			paramList();
			setState(230);
			match(RETURNS);
			setState(231);
			type();
			setState(232);
			newlines();
			setState(233);
			statements();
			setState(234);
			match(ENDFUNCTION);
			setState(235);
			newlines();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementsContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public StatementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statements; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitStatements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementsContext statements() throws RecognitionException {
		StatementsContext _localctx = new StatementsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_statements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CALL) | (1L << SET) | (1L << RETURN) | (1L << IF))) != 0)) {
				{
				{
				setState(237);
				statement();
				}
				}
				setState(242);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NewlinesContext extends ParserRuleContext {
		public PnewlinesContext pnewlines() {
			return getRuleContext(PnewlinesContext.class,0);
		}
		public TerminalNode EOF() { return getToken(JassParser.EOF, 0); }
		public NewlinesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_newlines; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNewlines(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NewlinesContext newlines() throws RecognitionException {
		NewlinesContext _localctx = new NewlinesContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_newlines);
		try {
			setState(245);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NEWLINE:
				enterOuterAlt(_localctx, 1);
				{
				setState(243);
				pnewlines();
				}
				break;
			case EOF:
				enterOuterAlt(_localctx, 2);
				{
				setState(244);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Newlines_optContext extends ParserRuleContext {
		public PnewlinesContext pnewlines() {
			return getRuleContext(PnewlinesContext.class,0);
		}
		public TerminalNode EOF() { return getToken(JassParser.EOF, 0); }
		public Newlines_optContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_newlines_opt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitNewlines_opt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Newlines_optContext newlines_opt() throws RecognitionException {
		Newlines_optContext _localctx = new Newlines_optContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_newlines_opt);
		try {
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(247);
				pnewlines();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PnewlinesContext extends ParserRuleContext {
		public TerminalNode NEWLINE() { return getToken(JassParser.NEWLINE, 0); }
		public NewlinesContext newlines() {
			return getRuleContext(NewlinesContext.class,0);
		}
		public PnewlinesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pnewlines; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JassVisitor ) return ((JassVisitor<? extends T>)visitor).visitPnewlines(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PnewlinesContext pnewlines() throws RecognitionException {
		PnewlinesContext _localctx = new PnewlinesContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_pnewlines);
		try {
			setState(255);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(252);
				match(NEWLINE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(253);
				match(NEWLINE);
				setState(254);
				newlines();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u0104\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\2\3\2\7\2\61\n\2\f"+
		"\2\16\2\64\13\2\3\2\7\2\67\n\2\f\2\16\2:\13\2\5\2<\n\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\5\4H\n\4\3\5\5\5K\n\5\3\5\3\5\3\5\3\5\3\5\5\5"+
		"R\n\5\3\5\3\5\3\5\3\5\3\5\5\5Y\n\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7r\n\7\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b|\n\b\3\t\3\t\3\t\3\t\3\t\5\t\u0083\n"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u009e\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\5\13\u00b8\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\5\r\u00c3\n\r\3\16\3\16\3\16\7\16\u00c8\n\16\f\16\16\16\u00cb"+
		"\13\16\3\16\3\16\3\16\3\17\7\17\u00d1\n\17\f\17\16\17\u00d4\13\17\3\20"+
		"\5\20\u00d7\n\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\5\21"+
		"\u00e3\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\7\23\u00f1\n\23\f\23\16\23\u00f4\13\23\3\24\3\24\5\24\u00f8\n\24\3\25"+
		"\3\25\3\25\5\25\u00fd\n\25\3\26\3\26\3\26\5\26\u0102\n\26\3\26\2\2\27"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*\2\2\2\u0113\2;\3\2\2\2"+
		"\4=\3\2\2\2\6G\3\2\2\2\bX\3\2\2\2\nZ\3\2\2\2\fq\3\2\2\2\16{\3\2\2\2\20"+
		"\u0082\3\2\2\2\22\u009d\3\2\2\2\24\u00b7\3\2\2\2\26\u00b9\3\2\2\2\30\u00c2"+
		"\3\2\2\2\32\u00c4\3\2\2\2\34\u00d2\3\2\2\2\36\u00d6\3\2\2\2 \u00e2\3\2"+
		"\2\2\"\u00e4\3\2\2\2$\u00f2\3\2\2\2&\u00f7\3\2\2\2(\u00fc\3\2\2\2*\u0101"+
		"\3\2\2\2,<\5&\24\2-.\5(\25\2.\62\5\34\17\2/\61\5 \21\2\60/\3\2\2\2\61"+
		"\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\638\3\2\2\2\64\62\3\2\2\2\65\67"+
		"\5\"\22\2\66\65\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2\2\29<\3\2\2\2:8"+
		"\3\2\2\2;,\3\2\2\2;-\3\2\2\2<\3\3\2\2\2=>\7\25\2\2>?\7#\2\2?@\7\26\2\2"+
		"@A\7#\2\2AB\5&\24\2B\5\3\2\2\2CH\7#\2\2DE\7#\2\2EH\7\24\2\2FH\7\20\2\2"+
		"GC\3\2\2\2GD\3\2\2\2GF\3\2\2\2H\7\3\2\2\2IK\7\34\2\2JI\3\2\2\2JK\3\2\2"+
		"\2KL\3\2\2\2LM\5\6\4\2MN\7#\2\2NO\5&\24\2OY\3\2\2\2PR\7\34\2\2QP\3\2\2"+
		"\2QR\3\2\2\2RS\3\2\2\2ST\5\6\4\2TU\7#\2\2UV\5\n\6\2VW\5&\24\2WY\3\2\2"+
		"\2XJ\3\2\2\2XQ\3\2\2\2Y\t\3\2\2\2Z[\7\b\2\2[\\\5\f\7\2\\\13\3\2\2\2]r"+
		"\7#\2\2^r\7\35\2\2_r\7\36\2\2`a\7\f\2\2ar\7#\2\2br\7\37\2\2cr\7 \2\2d"+
		"r\7!\2\2ef\7#\2\2fg\7\3\2\2gh\5\f\7\2hi\7\4\2\2ir\3\2\2\2jr\5\16\b\2k"+
		"l\7\5\2\2lm\5\f\7\2mn\7\6\2\2nr\3\2\2\2op\7\"\2\2pr\5\f\7\2q]\3\2\2\2"+
		"q^\3\2\2\2q_\3\2\2\2q`\3\2\2\2qb\3\2\2\2qc\3\2\2\2qd\3\2\2\2qe\3\2\2\2"+
		"qj\3\2\2\2qk\3\2\2\2qo\3\2\2\2r\r\3\2\2\2st\7#\2\2tu\7\5\2\2uv\5\20\t"+
		"\2vw\7\6\2\2w|\3\2\2\2xy\7#\2\2yz\7\5\2\2z|\7\6\2\2{s\3\2\2\2{x\3\2\2"+
		"\2|\17\3\2\2\2}\u0083\5\f\7\2~\177\5\f\7\2\177\u0080\7\7\2\2\u0080\u0081"+
		"\5\20\t\2\u0081\u0083\3\2\2\2\u0082}\3\2\2\2\u0082~\3\2\2\2\u0083\21\3"+
		"\2\2\2\u0084\u0085\7\21\2\2\u0085\u0086\5\16\b\2\u0086\u0087\5&\24\2\u0087"+
		"\u009e\3\2\2\2\u0088\u0089\7\22\2\2\u0089\u008a\7#\2\2\u008a\u008b\7\b"+
		"\2\2\u008b\u008c\5\f\7\2\u008c\u008d\5&\24\2\u008d\u009e\3\2\2\2\u008e"+
		"\u008f\7\22\2\2\u008f\u0090\7#\2\2\u0090\u0091\7\3\2\2\u0091\u0092\5\f"+
		"\7\2\u0092\u0093\7\4\2\2\u0093\u0094\7\b\2\2\u0094\u0095\5\f\7\2\u0095"+
		"\u0096\5&\24\2\u0096\u009e\3\2\2\2\u0097\u0098\7\23\2\2\u0098\u0099\5"+
		"\f\7\2\u0099\u009a\5&\24\2\u009a\u009e\3\2\2\2\u009b\u009c\7\27\2\2\u009c"+
		"\u009e\5\24\13\2\u009d\u0084\3\2\2\2\u009d\u0088\3\2\2\2\u009d\u008e\3"+
		"\2\2\2\u009d\u0097\3\2\2\2\u009d\u009b\3\2\2\2\u009e\23\3\2\2\2\u009f"+
		"\u00a0\5\f\7\2\u00a0\u00a1\7\30\2\2\u00a1\u00a2\5&\24\2\u00a2\u00a3\5"+
		"$\23\2\u00a3\u00a4\7\32\2\2\u00a4\u00a5\5&\24\2\u00a5\u00b8\3\2\2\2\u00a6"+
		"\u00a7\5\f\7\2\u00a7\u00a8\7\30\2\2\u00a8\u00a9\5&\24\2\u00a9\u00aa\5"+
		"$\23\2\u00aa\u00ab\7\31\2\2\u00ab\u00ac\5&\24\2\u00ac\u00ad\5$\23\2\u00ad"+
		"\u00ae\7\32\2\2\u00ae\u00af\5&\24\2\u00af\u00b8\3\2\2\2\u00b0\u00b1\5"+
		"\f\7\2\u00b1\u00b2\7\30\2\2\u00b2\u00b3\5&\24\2\u00b3\u00b4\5$\23\2\u00b4"+
		"\u00b5\7\33\2\2\u00b5\u00b6\5\24\13\2\u00b6\u00b8\3\2\2\2\u00b7\u009f"+
		"\3\2\2\2\u00b7\u00a6\3\2\2\2\u00b7\u00b0\3\2\2\2\u00b8\25\3\2\2\2\u00b9"+
		"\u00ba\5\6\4\2\u00ba\u00bb\7#\2\2\u00bb\27\3\2\2\2\u00bc\u00c3\5\26\f"+
		"\2\u00bd\u00be\5\26\f\2\u00be\u00bf\7\7\2\2\u00bf\u00c0\5\30\r\2\u00c0"+
		"\u00c3\3\2\2\2\u00c1\u00c3\7\20\2\2\u00c2\u00bc\3\2\2\2\u00c2\u00bd\3"+
		"\2\2\2\u00c2\u00c1\3\2\2\2\u00c3\31\3\2\2\2\u00c4\u00c5\7\t\2\2\u00c5"+
		"\u00c9\5&\24\2\u00c6\u00c8\5\b\5\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb\3\2"+
		"\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c9\3\2\2\2\u00cc\u00cd\7\n\2\2\u00cd\u00ce\5&\24\2\u00ce\33\3\2\2"+
		"\2\u00cf\u00d1\5\4\3\2\u00d0\u00cf\3\2\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d0"+
		"\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\35\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5"+
		"\u00d7\7\34\2\2\u00d6\u00d5\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d8\3"+
		"\2\2\2\u00d8\u00d9\7\13\2\2\u00d9\u00da\7#\2\2\u00da\u00db\7\r\2\2\u00db"+
		"\u00dc\5\30\r\2\u00dc\u00dd\7\16\2\2\u00dd\u00de\5\6\4\2\u00de\u00df\5"+
		"&\24\2\u00df\37\3\2\2\2\u00e0\u00e3\5\32\16\2\u00e1\u00e3\5\36\20\2\u00e2"+
		"\u00e0\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3!\3\2\2\2\u00e4\u00e5\7\f\2\2"+
		"\u00e5\u00e6\7#\2\2\u00e6\u00e7\7\r\2\2\u00e7\u00e8\5\30\r\2\u00e8\u00e9"+
		"\7\16\2\2\u00e9\u00ea\5\6\4\2\u00ea\u00eb\5&\24\2\u00eb\u00ec\5$\23\2"+
		"\u00ec\u00ed\7\17\2\2\u00ed\u00ee\5&\24\2\u00ee#\3\2\2\2\u00ef\u00f1\5"+
		"\22\n\2\u00f0\u00ef\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f2"+
		"\u00f3\3\2\2\2\u00f3%\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5\u00f8\5*\26\2"+
		"\u00f6\u00f8\7\2\2\3\u00f7\u00f5\3\2\2\2\u00f7\u00f6\3\2\2\2\u00f8\'\3"+
		"\2\2\2\u00f9\u00fd\5*\26\2\u00fa\u00fd\7\2\2\3\u00fb\u00fd\3\2\2\2\u00fc"+
		"\u00f9\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fb\3\2\2\2\u00fd)\3\2\2\2"+
		"\u00fe\u0102\7%\2\2\u00ff\u0100\7%\2\2\u0100\u0102\5&\24\2\u0101\u00fe"+
		"\3\2\2\2\u0101\u00ff\3\2\2\2\u0102+\3\2\2\2\27\628;GJQXq{\u0082\u009d"+
		"\u00b7\u00c2\u00c9\u00d2\u00d6\u00e2\u00f2\u00f7\u00fc\u0101";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}