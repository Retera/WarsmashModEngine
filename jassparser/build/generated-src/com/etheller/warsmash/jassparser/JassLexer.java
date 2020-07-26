// Generated from Jass.g4 by ANTLR 4.7

	package com.etheller.interpreter;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JassLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "EQUALS", "GLOBALS", "ENDGLOBALS", 
		"NATIVE", "FUNCTION", "TAKES", "RETURNS", "ENDFUNCTION", "NOTHING", "CALL", 
		"SET", "RETURN", "ARRAY", "TYPE", "EXTENDS", "IF", "THEN", "ELSE", "ENDIF", 
		"ELSEIF", "CONSTANT", "STRING_LITERAL", "INTEGER", "NULL", "TRUE", "FALSE", 
		"NOT", "ID", "WS", "NEWLINE"
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


	public JassLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Jass.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2%\u0139\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26"+
		"\3\26\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\34\3\34\7\34\u00e3\n\34\f\34\16\34\u00e6\13"+
		"\34\3\34\3\34\3\35\3\35\3\35\7\35\u00ed\n\35\f\35\16\35\u00f0\13\35\5"+
		"\35\u00f2\n\35\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3"+
		" \3 \3 \3 \3 \3!\3!\3!\3!\3\"\3\"\7\"\u010a\n\"\f\"\16\"\u010d\13\"\3"+
		"#\6#\u0110\n#\r#\16#\u0111\3#\3#\3$\3$\3$\3$\7$\u011a\n$\f$\16$\u011d"+
		"\13$\3$\3$\3$\3$\3$\3$\7$\u0125\n$\f$\16$\u0128\13$\3$\3$\3$\3$\3$\7$"+
		"\u012f\n$\f$\16$\u0132\13$\3$\3$\3$\3$\5$\u0138\n$\6\u00e4\u011b\u0126"+
		"\u0130\2%\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%\3\2\t\3\2\62\62\3\2\63;\3\2\62;\5\2C\\aac|\6"+
		"\2\62;C\\aac|\4\2\13\13\"\"\4\2\f\f\17\17\2\u0144\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\3I\3\2\2\2\5K\3\2\2"+
		"\2\7M\3\2\2\2\tO\3\2\2\2\13Q\3\2\2\2\rS\3\2\2\2\17U\3\2\2\2\21]\3\2\2"+
		"\2\23h\3\2\2\2\25o\3\2\2\2\27x\3\2\2\2\31~\3\2\2\2\33\u0086\3\2\2\2\35"+
		"\u0092\3\2\2\2\37\u009a\3\2\2\2!\u009f\3\2\2\2#\u00a3\3\2\2\2%\u00aa\3"+
		"\2\2\2\'\u00b0\3\2\2\2)\u00b5\3\2\2\2+\u00bd\3\2\2\2-\u00c0\3\2\2\2/\u00c5"+
		"\3\2\2\2\61\u00ca\3\2\2\2\63\u00d0\3\2\2\2\65\u00d7\3\2\2\2\67\u00e0\3"+
		"\2\2\29\u00f1\3\2\2\2;\u00f3\3\2\2\2=\u00f8\3\2\2\2?\u00fd\3\2\2\2A\u0103"+
		"\3\2\2\2C\u0107\3\2\2\2E\u010f\3\2\2\2G\u0137\3\2\2\2IJ\7]\2\2J\4\3\2"+
		"\2\2KL\7_\2\2L\6\3\2\2\2MN\7*\2\2N\b\3\2\2\2OP\7+\2\2P\n\3\2\2\2QR\7."+
		"\2\2R\f\3\2\2\2ST\7?\2\2T\16\3\2\2\2UV\7i\2\2VW\7n\2\2WX\7q\2\2XY\7d\2"+
		"\2YZ\7c\2\2Z[\7n\2\2[\\\7u\2\2\\\20\3\2\2\2]^\7g\2\2^_\7p\2\2_`\7f\2\2"+
		"`a\7i\2\2ab\7n\2\2bc\7q\2\2cd\7d\2\2de\7c\2\2ef\7n\2\2fg\7u\2\2g\22\3"+
		"\2\2\2hi\7p\2\2ij\7c\2\2jk\7v\2\2kl\7k\2\2lm\7x\2\2mn\7g\2\2n\24\3\2\2"+
		"\2op\7h\2\2pq\7w\2\2qr\7p\2\2rs\7e\2\2st\7v\2\2tu\7k\2\2uv\7q\2\2vw\7"+
		"p\2\2w\26\3\2\2\2xy\7v\2\2yz\7c\2\2z{\7m\2\2{|\7g\2\2|}\7u\2\2}\30\3\2"+
		"\2\2~\177\7t\2\2\177\u0080\7g\2\2\u0080\u0081\7v\2\2\u0081\u0082\7w\2"+
		"\2\u0082\u0083\7t\2\2\u0083\u0084\7p\2\2\u0084\u0085\7u\2\2\u0085\32\3"+
		"\2\2\2\u0086\u0087\7g\2\2\u0087\u0088\7p\2\2\u0088\u0089\7f\2\2\u0089"+
		"\u008a\7h\2\2\u008a\u008b\7w\2\2\u008b\u008c\7p\2\2\u008c\u008d\7e\2\2"+
		"\u008d\u008e\7v\2\2\u008e\u008f\7k\2\2\u008f\u0090\7q\2\2\u0090\u0091"+
		"\7p\2\2\u0091\34\3\2\2\2\u0092\u0093\7p\2\2\u0093\u0094\7q\2\2\u0094\u0095"+
		"\7v\2\2\u0095\u0096\7j\2\2\u0096\u0097\7k\2\2\u0097\u0098\7p\2\2\u0098"+
		"\u0099\7i\2\2\u0099\36\3\2\2\2\u009a\u009b\7e\2\2\u009b\u009c\7c\2\2\u009c"+
		"\u009d\7n\2\2\u009d\u009e\7n\2\2\u009e \3\2\2\2\u009f\u00a0\7u\2\2\u00a0"+
		"\u00a1\7g\2\2\u00a1\u00a2\7v\2\2\u00a2\"\3\2\2\2\u00a3\u00a4\7t\2\2\u00a4"+
		"\u00a5\7g\2\2\u00a5\u00a6\7v\2\2\u00a6\u00a7\7w\2\2\u00a7\u00a8\7t\2\2"+
		"\u00a8\u00a9\7p\2\2\u00a9$\3\2\2\2\u00aa\u00ab\7c\2\2\u00ab\u00ac\7t\2"+
		"\2\u00ac\u00ad\7t\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7{\2\2\u00af&\3\2"+
		"\2\2\u00b0\u00b1\7v\2\2\u00b1\u00b2\7{\2\2\u00b2\u00b3\7r\2\2\u00b3\u00b4"+
		"\7g\2\2\u00b4(\3\2\2\2\u00b5\u00b6\7g\2\2\u00b6\u00b7\7z\2\2\u00b7\u00b8"+
		"\7v\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7p\2\2\u00ba\u00bb\7f\2\2\u00bb"+
		"\u00bc\7u\2\2\u00bc*\3\2\2\2\u00bd\u00be\7k\2\2\u00be\u00bf\7h\2\2\u00bf"+
		",\3\2\2\2\u00c0\u00c1\7v\2\2\u00c1\u00c2\7j\2\2\u00c2\u00c3\7g\2\2\u00c3"+
		"\u00c4\7p\2\2\u00c4.\3\2\2\2\u00c5\u00c6\7g\2\2\u00c6\u00c7\7n\2\2\u00c7"+
		"\u00c8\7u\2\2\u00c8\u00c9\7g\2\2\u00c9\60\3\2\2\2\u00ca\u00cb\7g\2\2\u00cb"+
		"\u00cc\7p\2\2\u00cc\u00cd\7f\2\2\u00cd\u00ce\7k\2\2\u00ce\u00cf\7h\2\2"+
		"\u00cf\62\3\2\2\2\u00d0\u00d1\7g\2\2\u00d1\u00d2\7n\2\2\u00d2\u00d3\7"+
		"u\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7k\2\2\u00d5\u00d6\7h\2\2\u00d6\64"+
		"\3\2\2\2\u00d7\u00d8\7e\2\2\u00d8\u00d9\7q\2\2\u00d9\u00da\7p\2\2\u00da"+
		"\u00db\7u\2\2\u00db\u00dc\7v\2\2\u00dc\u00dd\7c\2\2\u00dd\u00de\7p\2\2"+
		"\u00de\u00df\7v\2\2\u00df\66\3\2\2\2\u00e0\u00e4\7$\2\2\u00e1\u00e3\13"+
		"\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00e8\7$"+
		"\2\2\u00e88\3\2\2\2\u00e9\u00f2\t\2\2\2\u00ea\u00ee\t\3\2\2\u00eb\u00ed"+
		"\t\4\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee"+
		"\u00ef\3\2\2\2\u00ef\u00f2\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00e9\3\2"+
		"\2\2\u00f1\u00ea\3\2\2\2\u00f2:\3\2\2\2\u00f3\u00f4\7p\2\2\u00f4\u00f5"+
		"\7w\2\2\u00f5\u00f6\7n\2\2\u00f6\u00f7\7n\2\2\u00f7<\3\2\2\2\u00f8\u00f9"+
		"\7v\2\2\u00f9\u00fa\7t\2\2\u00fa\u00fb\7w\2\2\u00fb\u00fc\7g\2\2\u00fc"+
		">\3\2\2\2\u00fd\u00fe\7h\2\2\u00fe\u00ff\7c\2\2\u00ff\u0100\7n\2\2\u0100"+
		"\u0101\7u\2\2\u0101\u0102\7g\2\2\u0102@\3\2\2\2\u0103\u0104\7p\2\2\u0104"+
		"\u0105\7q\2\2\u0105\u0106\7v\2\2\u0106B\3\2\2\2\u0107\u010b\t\5\2\2\u0108"+
		"\u010a\t\6\2\2\u0109\u0108\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2"+
		"\2\2\u010b\u010c\3\2\2\2\u010cD\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u0110"+
		"\t\7\2\2\u010f\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2\2\2\u0111"+
		"\u0112\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\b#\2\2\u0114F\3\2\2\2\u0115"+
		"\u0116\7\61\2\2\u0116\u0117\7\61\2\2\u0117\u011b\3\2\2\2\u0118\u011a\13"+
		"\2\2\2\u0119\u0118\3\2\2\2\u011a\u011d\3\2\2\2\u011b\u011c\3\2\2\2\u011b"+
		"\u0119\3\2\2\2\u011c\u011e\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u011f\7\17"+
		"\2\2\u011f\u0138\7\f\2\2\u0120\u0121\7\61\2\2\u0121\u0122\7\61\2\2\u0122"+
		"\u0126\3\2\2\2\u0123\u0125\13\2\2\2\u0124\u0123\3\2\2\2\u0125\u0128\3"+
		"\2\2\2\u0126\u0127\3\2\2\2\u0126\u0124\3\2\2\2\u0127\u0129\3\2\2\2\u0128"+
		"\u0126\3\2\2\2\u0129\u0138\7\f\2\2\u012a\u012b\7\61\2\2\u012b\u012c\7"+
		"\61\2\2\u012c\u0130\3\2\2\2\u012d\u012f\13\2\2\2\u012e\u012d\3\2\2\2\u012f"+
		"\u0132\3\2\2\2\u0130\u0131\3\2\2\2\u0130\u012e\3\2\2\2\u0131\u0133\3\2"+
		"\2\2\u0132\u0130\3\2\2\2\u0133\u0138\7\17\2\2\u0134\u0135\7\17\2\2\u0135"+
		"\u0138\7\f\2\2\u0136\u0138\t\b\2\2\u0137\u0115\3\2\2\2\u0137\u0120\3\2"+
		"\2\2\u0137\u012a\3\2\2\2\u0137\u0134\3\2\2\2\u0137\u0136\3\2\2\2\u0138"+
		"H\3\2\2\2\f\2\u00e4\u00ee\u00f1\u010b\u0111\u011b\u0126\u0130\u0137\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}