/* A Bison parser, made by GNU Bison 3.7.5.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2021 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

package net.warsmash.parsers.jass;



import java.text.MessageFormat;
/* "%code imports" blocks.  */
/* "SmashJassParser.y":56  */

import com.etheller.interpreter.ast.scope.GlobalScope;
import com.etheller.interpreter.ast.execution.instruction.*;
import com.etheller.interpreter.ast.value.*;
import com.etheller.interpreter.ast.value.visitor.*;
import com.etheller.interpreter.ast.function.JassParameter;
import com.etheller.interpreter.ast.function.JassNativeManager;
import com.etheller.interpreter.ast.expression.ArithmeticSigns;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;
import com.etheller.interpreter.ast.util.JassSettings;

/* "SmashJassParser.java":59  */

/**
 * A Bison parser, automatically generated from <tt>SmashJassParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class SmashJassParser
{
  /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.7.5";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";






  public enum SymbolKind
  {
    S_YYEOF(0),                    /* "end of file"  */
    S_YYerror(1),                  /* error  */
    S_YYUNDEF(2),                  /* "invalid token"  */
    S_EQUALS(3),                   /* EQUALS  */
    S_PLUSEQUALS(4),               /* PLUSEQUALS  */
    S_MINUSEQUALS(5),              /* MINUSEQUALS  */
    S_PLUSPLUS(6),                 /* PLUSPLUS  */
    S_MINUSMINUS(7),               /* MINUSMINUS  */
    S_GLOBALS(8),                  /* GLOBALS  */
    S_ENDGLOBALS(9),               /* ENDGLOBALS  */
    S_NATIVE(10),                  /* NATIVE  */
    S_FUNCTION(11),                /* FUNCTION  */
    S_TAKES(12),                   /* TAKES  */
    S_RETURNS(13),                 /* RETURNS  */
    S_ENDFUNCTION(14),             /* ENDFUNCTION  */
    S_NOTHING(15),                 /* NOTHING  */
    S_CALL(16),                    /* CALL  */
    S_SET(17),                     /* SET  */
    S_RETURN(18),                  /* RETURN  */
    S_ARRAY(19),                   /* ARRAY  */
    S_TYPE(20),                    /* TYPE  */
    S_EXTENDS(21),                 /* EXTENDS  */
    S_IF(22),                      /* IF  */
    S_THEN(23),                    /* THEN  */
    S_ELSE(24),                    /* ELSE  */
    S_ENDIF(25),                   /* ENDIF  */
    S_ELSEIF(26),                  /* ELSEIF  */
    S_CONSTANT(27),                /* CONSTANT  */
    S_LOCAL(28),                   /* LOCAL  */
    S_LOOP(29),                    /* LOOP  */
    S_ENDLOOP(30),                 /* ENDLOOP  */
    S_EXITWHEN(31),                /* EXITWHEN  */
    S_DEBUG(32),                   /* DEBUG  */
    S_NULL(33),                    /* NULL  */
    S_TRUE(34),                    /* TRUE  */
    S_FALSE(35),                   /* FALSE  */
    S_NOT(36),                     /* NOT  */
    S_OR(37),                      /* OR  */
    S_AND(38),                     /* AND  */
    S_NEWLINE(39),                 /* NEWLINE  */
    S_TIMES(40),                   /* TIMES  */
    S_DIVIDE(41),                  /* DIVIDE  */
    S_PLUS(42),                    /* PLUS  */
    S_MINUS(43),                   /* MINUS  */
    S_LESS(44),                    /* LESS  */
    S_GREATER(45),                 /* GREATER  */
    S_LESS_EQUALS(46),             /* LESS_EQUALS  */
    S_GREATER_EQUALS(47),          /* GREATER_EQUALS  */
    S_DOUBLE_EQUALS(48),           /* DOUBLE_EQUALS  */
    S_NOT_EQUALS(49),              /* NOT_EQUALS  */
    S_OPEN_BRACKET(50),            /* OPEN_BRACKET  */
    S_CLOSE_BRACKET(51),           /* CLOSE_BRACKET  */
    S_OPEN_PAREN(52),              /* OPEN_PAREN  */
    S_CLOSE_PAREN(53),             /* CLOSE_PAREN  */
    S_COMMA(54),                   /* COMMA  */
    S_ID(55),                      /* ID  */
    S_STRING_LITERAL(56),          /* STRING_LITERAL  */
    S_INTEGER(57),                 /* INTEGER  */
    S_HEX_CONSTANT(58),            /* HEX_CONSTANT  */
    S_DOLLAR_HEX_CONSTANT(59),     /* DOLLAR_HEX_CONSTANT  */
    S_RAWCODE(60),                 /* RAWCODE  */
    S_REAL(61),                    /* REAL  */
    S_YYACCEPT(62),                /* $accept  */
    S_program(63),                 /* program  */
    S_typeDeclarationBlock(64),    /* typeDeclarationBlock  */
    S_type(65),                    /* type  */
    S_constant_opt(66),            /* constant_opt  */
    S_global(67),                  /* global  */
    S_68_1(68),                    /* $@1  */
    S_local(69),                   /* local  */
    S_70_2(70),                    /* $@2  */
    S_assignTail(71),              /* assignTail  */
    S_multDivExpression(72),       /* multDivExpression  */
    S_simpleArithmeticExpression(73), /* simpleArithmeticExpression  */
    S_boolComparisonExpression(74), /* boolComparisonExpression  */
    S_boolEqualityExpression(75),  /* boolEqualityExpression  */
    S_boolAndsExpression(76),      /* boolAndsExpression  */
    S_boolExpression(77),          /* boolExpression  */
    S_baseExpression(78),          /* baseExpression  */
    S_79_3(79),                    /* $@3  */
    S_expression(80),              /* expression  */
    S_functionExpression(81),      /* functionExpression  */
    S_argsList(82),                /* argsList  */
    S_setPart(83),                 /* setPart  */
    S_callPart(84),                /* callPart  */
    S_statement(85),               /* statement  */
    S_86_4(86),                    /* $@4  */
    S_conditionalThen(87),         /* conditionalThen  */
    S_ifStatementPartial(88),      /* ifStatementPartial  */
    S_89_5(89),                    /* $@5  */
    S_90_6(90),                    /* $@6  */
    S_param(91),                   /* param  */
    S_paramList(92),               /* paramList  */
    S_globals(93),                 /* globals  */
    S_globals_opt(94),             /* globals_opt  */
    S_globalsBlock(95),            /* globalsBlock  */
    S_96_7(96),                    /* $@7  */
    S_nativeBlock(97),             /* nativeBlock  */
    S_functionBlock(98),           /* functionBlock  */
    S_99_8(99),                    /* $@8  */
    S_block(100),                  /* block  */
    S_blocks(101),                 /* blocks  */
    S_statements(102),             /* statements  */
    S_103_9(103),                  /* $@9  */
    S_104_10(104),                 /* $@10  */
    S_statements_opt(105),         /* statements_opt  */
    S_newlines(106),               /* newlines  */
    S_newlines_opt(107);           /* newlines_opt  */


    private final int yycode_;

    SymbolKind (int n) {
      this.yycode_ = n;
    }

    private static final SymbolKind[] values_ = {
      SymbolKind.S_YYEOF,
      SymbolKind.S_YYerror,
      SymbolKind.S_YYUNDEF,
      SymbolKind.S_EQUALS,
      SymbolKind.S_PLUSEQUALS,
      SymbolKind.S_MINUSEQUALS,
      SymbolKind.S_PLUSPLUS,
      SymbolKind.S_MINUSMINUS,
      SymbolKind.S_GLOBALS,
      SymbolKind.S_ENDGLOBALS,
      SymbolKind.S_NATIVE,
      SymbolKind.S_FUNCTION,
      SymbolKind.S_TAKES,
      SymbolKind.S_RETURNS,
      SymbolKind.S_ENDFUNCTION,
      SymbolKind.S_NOTHING,
      SymbolKind.S_CALL,
      SymbolKind.S_SET,
      SymbolKind.S_RETURN,
      SymbolKind.S_ARRAY,
      SymbolKind.S_TYPE,
      SymbolKind.S_EXTENDS,
      SymbolKind.S_IF,
      SymbolKind.S_THEN,
      SymbolKind.S_ELSE,
      SymbolKind.S_ENDIF,
      SymbolKind.S_ELSEIF,
      SymbolKind.S_CONSTANT,
      SymbolKind.S_LOCAL,
      SymbolKind.S_LOOP,
      SymbolKind.S_ENDLOOP,
      SymbolKind.S_EXITWHEN,
      SymbolKind.S_DEBUG,
      SymbolKind.S_NULL,
      SymbolKind.S_TRUE,
      SymbolKind.S_FALSE,
      SymbolKind.S_NOT,
      SymbolKind.S_OR,
      SymbolKind.S_AND,
      SymbolKind.S_NEWLINE,
      SymbolKind.S_TIMES,
      SymbolKind.S_DIVIDE,
      SymbolKind.S_PLUS,
      SymbolKind.S_MINUS,
      SymbolKind.S_LESS,
      SymbolKind.S_GREATER,
      SymbolKind.S_LESS_EQUALS,
      SymbolKind.S_GREATER_EQUALS,
      SymbolKind.S_DOUBLE_EQUALS,
      SymbolKind.S_NOT_EQUALS,
      SymbolKind.S_OPEN_BRACKET,
      SymbolKind.S_CLOSE_BRACKET,
      SymbolKind.S_OPEN_PAREN,
      SymbolKind.S_CLOSE_PAREN,
      SymbolKind.S_COMMA,
      SymbolKind.S_ID,
      SymbolKind.S_STRING_LITERAL,
      SymbolKind.S_INTEGER,
      SymbolKind.S_HEX_CONSTANT,
      SymbolKind.S_DOLLAR_HEX_CONSTANT,
      SymbolKind.S_RAWCODE,
      SymbolKind.S_REAL,
      SymbolKind.S_YYACCEPT,
      SymbolKind.S_program,
      SymbolKind.S_typeDeclarationBlock,
      SymbolKind.S_type,
      SymbolKind.S_constant_opt,
      SymbolKind.S_global,
      SymbolKind.S_68_1,
      SymbolKind.S_local,
      SymbolKind.S_70_2,
      SymbolKind.S_assignTail,
      SymbolKind.S_multDivExpression,
      SymbolKind.S_simpleArithmeticExpression,
      SymbolKind.S_boolComparisonExpression,
      SymbolKind.S_boolEqualityExpression,
      SymbolKind.S_boolAndsExpression,
      SymbolKind.S_boolExpression,
      SymbolKind.S_baseExpression,
      SymbolKind.S_79_3,
      SymbolKind.S_expression,
      SymbolKind.S_functionExpression,
      SymbolKind.S_argsList,
      SymbolKind.S_setPart,
      SymbolKind.S_callPart,
      SymbolKind.S_statement,
      SymbolKind.S_86_4,
      SymbolKind.S_conditionalThen,
      SymbolKind.S_ifStatementPartial,
      SymbolKind.S_89_5,
      SymbolKind.S_90_6,
      SymbolKind.S_param,
      SymbolKind.S_paramList,
      SymbolKind.S_globals,
      SymbolKind.S_globals_opt,
      SymbolKind.S_globalsBlock,
      SymbolKind.S_96_7,
      SymbolKind.S_nativeBlock,
      SymbolKind.S_functionBlock,
      SymbolKind.S_99_8,
      SymbolKind.S_block,
      SymbolKind.S_blocks,
      SymbolKind.S_statements,
      SymbolKind.S_103_9,
      SymbolKind.S_104_10,
      SymbolKind.S_statements_opt,
      SymbolKind.S_newlines,
      SymbolKind.S_newlines_opt
    };

    static final SymbolKind get(int code) {
      return values_[code];
    }

    public final int getCode() {
      return this.yycode_;
    }

    /* Return YYSTR after stripping away unnecessary quotes and
       backslashes, so that it's suitable for yyerror.  The heuristic is
       that double-quoting is unnecessary unless the string contains an
       apostrophe, a comma, or backslash (other than backslash-backslash).
       YYSTR is taken from yytname.  */
    private static String yytnamerr_(String yystr)
    {
      if (yystr.charAt (0) == '"')
        {
          StringBuffer yyr = new StringBuffer();
          strip_quotes: for (int i = 1; i < yystr.length(); i++)
            switch (yystr.charAt(i))
              {
              case '\'':
              case ',':
                break strip_quotes;

              case '\\':
                if (yystr.charAt(++i) != '\\')
                  break strip_quotes;
                /* Fall through.  */
              default:
                yyr.append(yystr.charAt(i));
                break;

              case '"':
                return yyr.toString();
              }
        }
      return yystr;
    }

    /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
       First, the terminals, then, starting at \a YYNTOKENS_, nonterminals.  */
    private static final String[] yytname_ = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "\"end of file\"", "error", "\"invalid token\"", "EQUALS", "PLUSEQUALS",
  "MINUSEQUALS", "PLUSPLUS", "MINUSMINUS", "GLOBALS", "ENDGLOBALS",
  "NATIVE", "FUNCTION", "TAKES", "RETURNS", "ENDFUNCTION", "NOTHING",
  "CALL", "SET", "RETURN", "ARRAY", "TYPE", "EXTENDS", "IF", "THEN",
  "ELSE", "ENDIF", "ELSEIF", "CONSTANT", "LOCAL", "LOOP", "ENDLOOP",
  "EXITWHEN", "DEBUG", "NULL", "TRUE", "FALSE", "NOT", "OR", "AND",
  "NEWLINE", "TIMES", "DIVIDE", "PLUS", "MINUS", "LESS", "GREATER",
  "LESS_EQUALS", "GREATER_EQUALS", "DOUBLE_EQUALS", "NOT_EQUALS",
  "OPEN_BRACKET", "CLOSE_BRACKET", "OPEN_PAREN", "CLOSE_PAREN", "COMMA",
  "ID", "STRING_LITERAL", "INTEGER", "HEX_CONSTANT", "DOLLAR_HEX_CONSTANT",
  "RAWCODE", "REAL", "$accept", "program", "typeDeclarationBlock", "type",
  "constant_opt", "global", "$@1", "local", "$@2", "assignTail",
  "multDivExpression", "simpleArithmeticExpression",
  "boolComparisonExpression", "boolEqualityExpression",
  "boolAndsExpression", "boolExpression", "baseExpression", "$@3",
  "expression", "functionExpression", "argsList", "setPart", "callPart",
  "statement", "$@4", "conditionalThen", "ifStatementPartial", "$@5",
  "$@6", "param", "paramList", "globals", "globals_opt", "globalsBlock",
  "$@7", "nativeBlock", "functionBlock", "$@8", "block", "blocks",
  "statements", "$@9", "$@10", "statements_opt", "newlines",
  "newlines_opt", null
    };
  }

    /* The user-facing name of this symbol.  */
    public final String getName() {
      return yytnamerr_(yytname_[yycode_]);
    }

  };


  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>SmashJassParser</tt>.
   */
  public interface Lexer {
    /* Token kinds.  */
    /** Token "end of file", to be returned by the scanner.  */
    static final int YYEOF = 0;
    /** Token error, to be returned by the scanner.  */
    static final int YYerror = 256;
    /** Token "invalid token", to be returned by the scanner.  */
    static final int YYUNDEF = 257;
    /** Token EQUALS, to be returned by the scanner.  */
    static final int EQUALS = 258;
    /** Token PLUSEQUALS, to be returned by the scanner.  */
    static final int PLUSEQUALS = 259;
    /** Token MINUSEQUALS, to be returned by the scanner.  */
    static final int MINUSEQUALS = 260;
    /** Token PLUSPLUS, to be returned by the scanner.  */
    static final int PLUSPLUS = 261;
    /** Token MINUSMINUS, to be returned by the scanner.  */
    static final int MINUSMINUS = 262;
    /** Token GLOBALS, to be returned by the scanner.  */
    static final int GLOBALS = 263;
    /** Token ENDGLOBALS, to be returned by the scanner.  */
    static final int ENDGLOBALS = 264;
    /** Token NATIVE, to be returned by the scanner.  */
    static final int NATIVE = 265;
    /** Token FUNCTION, to be returned by the scanner.  */
    static final int FUNCTION = 266;
    /** Token TAKES, to be returned by the scanner.  */
    static final int TAKES = 267;
    /** Token RETURNS, to be returned by the scanner.  */
    static final int RETURNS = 268;
    /** Token ENDFUNCTION, to be returned by the scanner.  */
    static final int ENDFUNCTION = 269;
    /** Token NOTHING, to be returned by the scanner.  */
    static final int NOTHING = 270;
    /** Token CALL, to be returned by the scanner.  */
    static final int CALL = 271;
    /** Token SET, to be returned by the scanner.  */
    static final int SET = 272;
    /** Token RETURN, to be returned by the scanner.  */
    static final int RETURN = 273;
    /** Token ARRAY, to be returned by the scanner.  */
    static final int ARRAY = 274;
    /** Token TYPE, to be returned by the scanner.  */
    static final int TYPE = 275;
    /** Token EXTENDS, to be returned by the scanner.  */
    static final int EXTENDS = 276;
    /** Token IF, to be returned by the scanner.  */
    static final int IF = 277;
    /** Token THEN, to be returned by the scanner.  */
    static final int THEN = 278;
    /** Token ELSE, to be returned by the scanner.  */
    static final int ELSE = 279;
    /** Token ENDIF, to be returned by the scanner.  */
    static final int ENDIF = 280;
    /** Token ELSEIF, to be returned by the scanner.  */
    static final int ELSEIF = 281;
    /** Token CONSTANT, to be returned by the scanner.  */
    static final int CONSTANT = 282;
    /** Token LOCAL, to be returned by the scanner.  */
    static final int LOCAL = 283;
    /** Token LOOP, to be returned by the scanner.  */
    static final int LOOP = 284;
    /** Token ENDLOOP, to be returned by the scanner.  */
    static final int ENDLOOP = 285;
    /** Token EXITWHEN, to be returned by the scanner.  */
    static final int EXITWHEN = 286;
    /** Token DEBUG, to be returned by the scanner.  */
    static final int DEBUG = 287;
    /** Token NULL, to be returned by the scanner.  */
    static final int NULL = 288;
    /** Token TRUE, to be returned by the scanner.  */
    static final int TRUE = 289;
    /** Token FALSE, to be returned by the scanner.  */
    static final int FALSE = 290;
    /** Token NOT, to be returned by the scanner.  */
    static final int NOT = 291;
    /** Token OR, to be returned by the scanner.  */
    static final int OR = 292;
    /** Token AND, to be returned by the scanner.  */
    static final int AND = 293;
    /** Token NEWLINE, to be returned by the scanner.  */
    static final int NEWLINE = 294;
    /** Token TIMES, to be returned by the scanner.  */
    static final int TIMES = 295;
    /** Token DIVIDE, to be returned by the scanner.  */
    static final int DIVIDE = 296;
    /** Token PLUS, to be returned by the scanner.  */
    static final int PLUS = 297;
    /** Token MINUS, to be returned by the scanner.  */
    static final int MINUS = 298;
    /** Token LESS, to be returned by the scanner.  */
    static final int LESS = 299;
    /** Token GREATER, to be returned by the scanner.  */
    static final int GREATER = 300;
    /** Token LESS_EQUALS, to be returned by the scanner.  */
    static final int LESS_EQUALS = 301;
    /** Token GREATER_EQUALS, to be returned by the scanner.  */
    static final int GREATER_EQUALS = 302;
    /** Token DOUBLE_EQUALS, to be returned by the scanner.  */
    static final int DOUBLE_EQUALS = 303;
    /** Token NOT_EQUALS, to be returned by the scanner.  */
    static final int NOT_EQUALS = 304;
    /** Token OPEN_BRACKET, to be returned by the scanner.  */
    static final int OPEN_BRACKET = 305;
    /** Token CLOSE_BRACKET, to be returned by the scanner.  */
    static final int CLOSE_BRACKET = 306;
    /** Token OPEN_PAREN, to be returned by the scanner.  */
    static final int OPEN_PAREN = 307;
    /** Token CLOSE_PAREN, to be returned by the scanner.  */
    static final int CLOSE_PAREN = 308;
    /** Token COMMA, to be returned by the scanner.  */
    static final int COMMA = 309;
    /** Token ID, to be returned by the scanner.  */
    static final int ID = 310;
    /** Token STRING_LITERAL, to be returned by the scanner.  */
    static final int STRING_LITERAL = 311;
    /** Token INTEGER, to be returned by the scanner.  */
    static final int INTEGER = 312;
    /** Token HEX_CONSTANT, to be returned by the scanner.  */
    static final int HEX_CONSTANT = 313;
    /** Token DOLLAR_HEX_CONSTANT, to be returned by the scanner.  */
    static final int DOLLAR_HEX_CONSTANT = 314;
    /** Token RAWCODE, to be returned by the scanner.  */
    static final int RAWCODE = 315;
    /** Token REAL, to be returned by the scanner.  */
    static final int REAL = 316;

    /** Deprecated, use YYEOF instead.  */
    public static final int EOF = YYEOF;


    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex() throws java.io.IOException;

    /**
     * Emit an errorin a user-defined way.
     *
     *
     * @param msg The string for the error message.
     */
     void yyerror(String msg);


  }


  private class YYLexer implements Lexer {
/* "%code lexer" blocks.  */
/* "SmashJassParser.y":1  */

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

/* "SmashJassParser.java":586  */

  }


  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;




  /**
   * Instantiates the Bison-generated parser.
   */
  public SmashJassParser (Reader r)
  {

    this.yylexer = new YYLexer (r);

  }


  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  protected SmashJassParser (Lexer yylexer)
  {

    this.yylexer = yylexer;

  }



  private int yynerrs = 0;

  /**
   * The number of syntax errors so far.
   */
  public final int getNumberOfErrors () { return yynerrs; }

  /**
   * Print an error message via the lexer.
   *
   * @param msg The error message.
   */
  public final void yyerror(String msg) {
      yylexer.yyerror(msg);
  }



  private final class YYStack {
    private int[] stateStack = new int[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push (int state, Object value) {
      height++;
      if (size == height)
        {
          int[] newStateStack = new int[size * 2];
          System.arraycopy (stateStack, 0, newStateStack, 0, height);
          stateStack = newStateStack;

          Object[] newValueStack = new Object[size * 2];
          System.arraycopy (valueStack, 0, newValueStack, 0, height);
          valueStack = newValueStack;

          size *= 2;
        }

      stateStack[height] = state;
      valueStack[height] = value;
    }

    public final void pop () {
      pop (1);
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill (valueStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt (int i) {
      return stateStack[height - i];
    }

    public final Object valueAt (int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print (java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++)
        {
          out.print (' ');
          out.print (stateStack[i]);
        }
      out.println ();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState (int yystate, int yysym)
  {
    int yyr = yypgoto_[yysym - YYNTOKENS_] + yystate;
    if (0 <= yyr && yyr <= YYLAST_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - YYNTOKENS_];
  }

  private int yyaction(int yyn, YYStack yystack, int yylen)
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt(yylen - 1) : yystack.valueAt(0);

    switch (yyn)
      {
          case 5: /* type: ID  */
  if (yyn == 5)
    /* "SmashJassParser.y":103  */
        {
		yyval = globalScope.parseType(((String)(yystack.valueAt (0))));
	};
  break;


  case 6: /* type: ID ARRAY  */
  if (yyn == 6)
    /* "SmashJassParser.y":108  */
        {
		yyval = globalScope.parseArrayType(((String)(yystack.valueAt (1))));
	};
  break;


  case 7: /* type: NOTHING  */
  if (yyn == 7)
    /* "SmashJassParser.y":113  */
        {
		yyval = JassType.NOTHING;
	};
  break;


  case 10: /* global: constant_opt type ID  */
  if (yyn == 10)
    /* "SmashJassParser.y":125  */
        {
		final JassType type = ((JassType)(yystack.valueAt (1)));
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		if (arrayPrimType != null) {
			globalScope.createGlobalArray(((String)(yystack.valueAt (0))), type);
		}
		else {
			globalScope.createGlobal(((String)(yystack.valueAt (0))), type);
		}
	};
  break;


  case 11: /* $@1: %empty  */
  if (yyn == 11)
    /* "SmashJassParser.y":137  */
        {
		final JassType type = ((JassType)(yystack.valueAt (1)));
		final JassType arrayPrimType = type.visit(ArrayPrimitiveTypeVisitor.getInstance());
		if (arrayPrimType != null) {
			globalScope.createGlobalArray(((String)(yystack.valueAt (0))), type);
		}
		else {
			globalScope.createGlobal(((String)(yystack.valueAt (0))), type);
		}
	};
  break;


  case 12: /* global: constant_opt type ID $@1 assignTail  */
  if (yyn == 12)
    /* "SmashJassParser.y":148  */
        {
		instructionWriter.setGlobal(((String)(yystack.valueAt (2))));
	};
  break;


  case 13: /* local: LOCAL type ID  */
  if (yyn == 13)
    /* "SmashJassParser.y":154  */
        {
		instructionWriter.declareLocal(((String)(yystack.valueAt (0))));
		instructionWriter.pushDefaultValue(((JassType)(yystack.valueAt (1))));
	};
  break;


  case 14: /* $@2: %empty  */
  if (yyn == 14)
    /* "SmashJassParser.y":160  */
        {
		instructionWriter.declareLocal(((String)(yystack.valueAt (0))));
	};
  break;


  case 17: /* multDivExpression: multDivExpression TIMES baseExpression  */
  if (yyn == 17)
    /* "SmashJassParser.y":171  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.MULTIPLY);
	};
  break;


  case 18: /* multDivExpression: multDivExpression DIVIDE baseExpression  */
  if (yyn == 18)
    /* "SmashJassParser.y":176  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.DIVIDE);
	};
  break;


  case 20: /* simpleArithmeticExpression: simpleArithmeticExpression PLUS multDivExpression  */
  if (yyn == 20)
    /* "SmashJassParser.y":185  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.ADD);
	};
  break;


  case 21: /* simpleArithmeticExpression: simpleArithmeticExpression MINUS multDivExpression  */
  if (yyn == 21)
    /* "SmashJassParser.y":190  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.SUBTRACT);
	};
  break;


  case 23: /* boolComparisonExpression: boolComparisonExpression LESS simpleArithmeticExpression  */
  if (yyn == 23)
    /* "SmashJassParser.y":199  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.LESS);
	};
  break;


  case 24: /* boolComparisonExpression: boolComparisonExpression GREATER simpleArithmeticExpression  */
  if (yyn == 24)
    /* "SmashJassParser.y":204  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.GREATER);
	};
  break;


  case 25: /* boolComparisonExpression: boolComparisonExpression LESS_EQUALS simpleArithmeticExpression  */
  if (yyn == 25)
    /* "SmashJassParser.y":209  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.LESS_OR_EQUALS);
	};
  break;


  case 26: /* boolComparisonExpression: boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression  */
  if (yyn == 26)
    /* "SmashJassParser.y":214  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.GREATER_OR_EQUALS);
	};
  break;


  case 28: /* boolEqualityExpression: boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression  */
  if (yyn == 28)
    /* "SmashJassParser.y":223  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.EQUALS);
	};
  break;


  case 29: /* boolEqualityExpression: boolEqualityExpression NOT_EQUALS boolComparisonExpression  */
  if (yyn == 29)
    /* "SmashJassParser.y":228  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.NOT_EQUALS);
	};
  break;


  case 31: /* boolAndsExpression: boolAndsExpression AND boolEqualityExpression  */
  if (yyn == 31)
    /* "SmashJassParser.y":237  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.AND);
	};
  break;


  case 33: /* boolExpression: boolExpression OR boolAndsExpression  */
  if (yyn == 33)
    /* "SmashJassParser.y":246  */
        {
		instructionWriter.arithmetic(ArithmeticSigns.OR);
	};
  break;


  case 35: /* baseExpression: ID  */
  if (yyn == 35)
    /* "SmashJassParser.y":255  */
        {
		instructionWriter.referenceExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 36: /* baseExpression: STRING_LITERAL  */
  if (yyn == 36)
    /* "SmashJassParser.y":260  */
        {
		instructionWriter.stringLiteral(((String)(yystack.valueAt (0))));
	};
  break;


  case 37: /* baseExpression: INTEGER  */
  if (yyn == 37)
    /* "SmashJassParser.y":265  */
        {
		instructionWriter.integerLiteral(((int)(yystack.valueAt (0))));
	};
  break;


  case 38: /* baseExpression: HEX_CONSTANT  */
  if (yyn == 38)
    /* "SmashJassParser.y":270  */
        {
		instructionWriter.integerLiteral(((int)(yystack.valueAt (0))));
	};
  break;


  case 39: /* baseExpression: DOLLAR_HEX_CONSTANT  */
  if (yyn == 39)
    /* "SmashJassParser.y":275  */
        {
		instructionWriter.integerLiteral(((int)(yystack.valueAt (0))));
	};
  break;


  case 40: /* baseExpression: RAWCODE  */
  if (yyn == 40)
    /* "SmashJassParser.y":280  */
        {
		instructionWriter.integerLiteral(((int)(yystack.valueAt (0))));
	};
  break;


  case 41: /* baseExpression: REAL  */
  if (yyn == 41)
    /* "SmashJassParser.y":285  */
        {
		instructionWriter.realLiteral(((double)(yystack.valueAt (0))));
	};
  break;


  case 42: /* baseExpression: FUNCTION ID  */
  if (yyn == 42)
    /* "SmashJassParser.y":290  */
        {
		instructionWriter.functionReference(((String)(yystack.valueAt (0))));
	};
  break;


  case 43: /* baseExpression: NULL  */
  if (yyn == 43)
    /* "SmashJassParser.y":295  */
        {
		instructionWriter.literal(null);
	};
  break;


  case 44: /* baseExpression: TRUE  */
  if (yyn == 44)
    /* "SmashJassParser.y":300  */
        {
		instructionWriter.booleanLiteral(true);
	};
  break;


  case 45: /* baseExpression: FALSE  */
  if (yyn == 45)
    /* "SmashJassParser.y":305  */
        {
		instructionWriter.booleanLiteral(false);
	};
  break;


  case 46: /* $@3: %empty  */
  if (yyn == 46)
    /* "SmashJassParser.y":310  */
        {
		instructionWriter.referenceExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 47: /* baseExpression: ID $@3 OPEN_BRACKET expression CLOSE_BRACKET  */
  if (yyn == 47)
    /* "SmashJassParser.y":314  */
        {
		instructionWriter.arrayReferenceInstruction();
	};
  break;


  case 50: /* baseExpression: NOT baseExpression  */
  if (yyn == 50)
    /* "SmashJassParser.y":325  */
        {
		instructionWriter.notInstruction();
	};
  break;


  case 51: /* baseExpression: MINUS baseExpression  */
  if (yyn == 51)
    /* "SmashJassParser.y":330  */
        {
		instructionWriter.negateInstruction();
	};
  break;


  case 53: /* functionExpression: ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 53)
    /* "SmashJassParser.y":340  */
        {
		instructionWriter.call(((String)(yystack.valueAt (3))), ((int)(yystack.valueAt (1))));
	};
  break;


  case 54: /* argsList: expression  */
  if (yyn == 54)
    /* "SmashJassParser.y":347  */
        {
		yyval = 1;
	};
  break;


  case 55: /* argsList: expression COMMA argsList  */
  if (yyn == 55)
    /* "SmashJassParser.y":352  */
        {
		yyval = ((int)(yystack.valueAt (0))) + 1;
	};
  break;


  case 56: /* argsList: %empty  */
  if (yyn == 56)
    /* "SmashJassParser.y":357  */
        {
		yyval = 0;
	};
  break;


  case 57: /* setPart: ID EQUALS expression  */
  if (yyn == 57)
    /* "SmashJassParser.y":368  */
        {
		instructionWriter.set(((String)(yystack.valueAt (2))));
	};
  break;


  case 58: /* setPart: ID OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression  */
  if (yyn == 58)
    /* "SmashJassParser.y":373  */
        {
		instructionWriter.arrayedAssignmentStatement(((String)(yystack.valueAt (5))));
	};
  break;


  case 59: /* callPart: functionExpression  */
  if (yyn == 59)
    /* "SmashJassParser.y":380  */
        {
		instructionWriter.popInstruction(); // ignore return value
	};
  break;


  case 64: /* statement: RETURN expression  */
  if (yyn == 64)
    /* "SmashJassParser.y":395  */
        {
		instructionWriter.returnInstruction();
	};
  break;


  case 65: /* statement: RETURN  */
  if (yyn == 65)
    /* "SmashJassParser.y":400  */
        {
		instructionWriter.returnNothingInstruction();
	};
  break;


  case 66: /* statement: EXITWHEN expression  */
  if (yyn == 66)
    /* "SmashJassParser.y":405  */
        {
		instructionWriter.exitWhenStatement();
	};
  break;


  case 68: /* $@4: %empty  */
  if (yyn == 68)
    /* "SmashJassParser.y":411  */
             {
		instructionWriter.loop();
	};
  break;


  case 69: /* statement: LOOP $@4 statements_opt ENDLOOP  */
  if (yyn == 69)
    /* "SmashJassParser.y":415  */
        {
		instructionWriter.endloop();
	};
  break;


  case 72: /* conditionalThen: expression THEN  */
  if (yyn == 72)
    /* "SmashJassParser.y":426  */
        {
		instructionWriter.beginIf();
	};
  break;


  case 73: /* ifStatementPartial: conditionalThen statements_opt ENDIF  */
  if (yyn == 73)
    /* "SmashJassParser.y":433  */
        {
		instructionWriter.endIf();
	};
  break;


  case 74: /* $@5: %empty  */
  if (yyn == 74)
    /* "SmashJassParser.y":438  */
        {
		instructionWriter.beginElse();
	};
  break;


  case 75: /* ifStatementPartial: conditionalThen statements_opt ELSE $@5 statements_opt ENDIF  */
  if (yyn == 75)
    /* "SmashJassParser.y":442  */
        {
		instructionWriter.endElse();
	};
  break;


  case 76: /* $@6: %empty  */
  if (yyn == 76)
    /* "SmashJassParser.y":447  */
        {
		instructionWriter.beginElse();
	};
  break;


  case 77: /* ifStatementPartial: conditionalThen statements_opt ELSEIF $@6 ifStatementPartial  */
  if (yyn == 77)
    /* "SmashJassParser.y":451  */
        {
		instructionWriter.endElse();
	};
  break;


  case 78: /* param: type ID  */
  if (yyn == 78)
    /* "SmashJassParser.y":458  */
        {
		yyval = new JassParameter(((JassType)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 79: /* paramList: param  */
  if (yyn == 79)
    /* "SmashJassParser.y":465  */
        {
		LinkedList<JassParameter> list = new LinkedList<JassParameter>();
		list.addFirst(((JassParameter)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 80: /* paramList: param COMMA paramList  */
  if (yyn == 80)
    /* "SmashJassParser.y":472  */
        {
		LinkedList<JassParameter> list = ((LinkedList<JassParameter>)(yystack.valueAt (0)));
		list.addFirst(((JassParameter)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 81: /* paramList: NOTHING  */
  if (yyn == 81)
    /* "SmashJassParser.y":479  */
        {
		yyval = new LinkedList<JassParameter>();
	};
  break;


  case 86: /* $@7: %empty  */
  if (yyn == 86)
    /* "SmashJassParser.y":498  */
        {
		instructionWriter = globalScope.beginDefiningGlobals(getLine(), currentParsingFilePath);
	};
  break;


  case 87: /* globalsBlock: GLOBALS $@7 globals_opt ENDGLOBALS  */
  if (yyn == 87)
    /* "SmashJassParser.y":502  */
        {
		globalScope.endDefiningGlobals();
		instructionWriter.endGlobals();
	};
  break;


  case 88: /* nativeBlock: constant_opt NATIVE ID TAKES paramList RETURNS type  */
  if (yyn == 88)
    /* "SmashJassParser.y":509  */
        {
		final String text = ((String)(yystack.valueAt (4)));
		if (JassSettings.LOG_FUNCTION_DEFINITIONS) {
			System.out.println("Registering native: " + text);
		}
		jassNativeManager.registerNativeCode(getLine(), currentParsingFilePath, text, ((LinkedList<JassParameter>)(yystack.valueAt (2))), ((JassType)(yystack.valueAt (0))), globalScope);
	};
  break;


  case 89: /* $@8: %empty  */
  if (yyn == 89)
    /* "SmashJassParser.y":520  */
        {
		instructionWriter = globalScope.beginDefiningFunction(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (2))), ((LinkedList<JassParameter>)(yystack.valueAt (0))));
	};
  break;


  case 90: /* functionBlock: constant_opt FUNCTION ID TAKES paramList $@8 RETURNS type statements_opt ENDFUNCTION  */
  if (yyn == 90)
    /* "SmashJassParser.y":524  */
        {
		instructionWriter.endFunction();
	};
  break;


  case 97: /* $@9: %empty  */
  if (yyn == 97)
    /* "SmashJassParser.y":546  */
        {
		if (JassSettings.DEBUG) {
			instructionWriter.setLineNo(getLine());
		}
	};
  break;


  case 99: /* $@10: %empty  */
  if (yyn == 99)
    /* "SmashJassParser.y":554  */
        {
		if (JassSettings.DEBUG) {
			instructionWriter.setLineNo(getLine());
		}
	};
  break;



/* "SmashJassParser.java":1375  */

        default: break;
      }

    yystack.pop(yylen);
    yylen = 0;
    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState(yystack.stateAt(0), yyr1_[yyn]);
    yystack.push(yystate, yyval);
    return YYNEWSTATE;
  }




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse() throws java.io.IOException

  {


    /* Lookahead token kind.  */
    int yychar = YYEMPTY_;
    /* Lookahead symbol kind.  */
    SymbolKind yytoken = null;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;



    /* Semantic value of the lookahead.  */
    Object yylval = null;

    yyerrstatus_ = 0;
    yynerrs = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == YYFINAL_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == YYEMPTY_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal();

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        if (yytoken == SymbolKind.S_YYerror)
          {
            // The scanner already issued an error message, process directly
            // to error recovery.  But do not keep the error token as
            // lookahead, it is too special and may lead us to an endless
            // loop in error recovery. */
            yychar = Lexer.YYUNDEF;
            yytoken = SymbolKind.S_YYUNDEF;
            label = YYERRLAB1;
          }
        else
          {
            /* If the proper action on seeing token YYTOKEN is to reduce or to
               detect an error, take that action.  */
            yyn += yytoken.getCode();
            if (yyn < 0 || YYLAST_ < yyn || yycheck_[yyn] != yytoken.getCode())
              label = YYDEFAULT;

            /* <= 0 means reduce or error.  */
            else if ((yyn = yytable_[yyn]) <= 0)
              {
                if (yyTableValueIsError (yyn))
                  label = YYERRLAB;
                else
                  {
                    yyn = -yyn;
                    label = YYREDUCE;
                  }
              }

            else
              {
                /* Shift the lookahead token.  */
                /* Discard the token being shifted.  */
                yychar = YYEMPTY_;

                /* Count tokens shifted since error; after three, turn off error
                   status.  */
                if (yyerrstatus_ > 0)
                  --yyerrstatus_;

                yystate = yyn;
                yystack.push (yystate, yylval);
                label = YYNEWSTATE;
              }
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction(yyn, yystack, yylen);
        yystate = yystack.stateAt (0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs;
            if (yychar == YYEMPTY_)
              yytoken = null;
            yyreportSyntaxError (new Context (yystack, yytoken));
          }

        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.YYEOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.YYEOF)
                  return false;
              }
            else
              yychar = YYEMPTY_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt (0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        // Pop stack until we find a state that shifts the error token.
        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += SymbolKind.S_YYerror.getCode();
                if (0 <= yyn && yyn <= YYLAST_
                    && yycheck_[yyn] == SymbolKind.S_YYerror.getCode())
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;


            yystack.pop ();
            yystate = yystack.stateAt (0);
          }

        if (label == YYABORT)
          /* Leave the switch.  */
          break;



        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  /**
   * Information needed to get the list of expected tokens and to forge
   * a syntax error diagnostic.
   */
  public static final class Context
  {
    Context (YYStack stack, SymbolKind token)
    {
      yystack = stack;
      yytoken = token;
    }

    private YYStack yystack;


    /**
     * The symbol kind of the lookahead token.
     */
    public final SymbolKind getToken ()
    {
      return yytoken;
    }

    private SymbolKind yytoken;
    static final int NTOKENS = SmashJassParser.YYNTOKENS_;

    /**
     * Put in YYARG at most YYARGN of the expected tokens given the
     * current YYCTX, and return the number of tokens stored in YYARG.  If
     * YYARG is null, return the number of expected tokens (guaranteed to
     * be less than YYNTOKENS).
     */
    int getExpectedTokens (SymbolKind yyarg[], int yyargn)
    {
      return getExpectedTokens (yyarg, 0, yyargn);
    }

    int getExpectedTokens (SymbolKind yyarg[], int yyoffset, int yyargn)
    {
      int yycount = yyoffset;
      int yyn = yypact_[this.yystack.stateAt (0)];
      if (!yyPactValueIsDefault (yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative
             indexes in YYCHECK.  In other words, skip the first
             -YYN actions for this state because they are default
             actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST_ - yyn + 1;
          int yyxend = yychecklim < NTOKENS ? yychecklim : NTOKENS;
          for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck_[yyx + yyn] == yyx && yyx != SymbolKind.S_YYerror.getCode()
                && !yyTableValueIsError(yytable_[yyx + yyn]))
              {
                if (yyarg == null)
                  yycount += 1;
                else if (yycount == yyargn)
                  return 0; // FIXME: this is incorrect.
                else
                  yyarg[yycount++] = SymbolKind.get(yyx);
              }
        }
      if (yyarg != null && yycount == yyoffset && yyoffset < yyargn)
        yyarg[yycount] = null;
      return yycount - yyoffset;
    }
  }



  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      yyerror("syntax error");
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  private static final short yypact_ninf_ = -139;
  private static final short yytable_ninf_ = -106;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
     -12,   -12,    12,    30,    56,  -139,  -139,  -139,   -21,  -139,
    -139,    40,  -139,  -139,  -139,  -139,   -12,   -12,    61,    23,
      43,    21,  -139,    75,    13,    48,   101,   103,  -139,  -139,
     -11,  -139,   -12,  -139,    -8,    -8,  -139,    86,    69,    68,
      74,    77,    79,   116,  -139,  -139,   127,  -139,  -139,    -8,
     -11,   118,   131,  -139,  -139,   -11,     2,  -139,   -12,    80,
    -139,  -139,  -139,     2,     2,     2,    15,  -139,  -139,  -139,
    -139,  -139,  -139,   -15,    28,    47,    32,    98,   100,  -139,
    -139,  -139,   124,    76,  -139,  -139,  -139,    87,     2,    89,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,  -139,   -12,    57,  -139,    88,    90,     2,  -139,
    -139,   -15,   -15,    28,    28,    28,    28,    47,    47,    32,
      98,    93,    91,    92,     2,     2,   -11,  -139,     2,    57,
       3,  -139,  -139,  -139,  -139,  -139,     2,  -139,    94,    57,
      97,  -139,    16,  -139,  -139,   121,   -12,  -139,    95,   -12,
    -139,  -139,     2,     2,  -139,  -139,  -139,  -139,    96,   138,
     122,  -139,   102,  -139,  -139,  -139,   131,  -139,   145,   -12,
       2,  -139,     2,   126,  -139,  -139,  -139
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte[] yydefact_ = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
     106,   103,     0,   105,     9,   104,     1,    86,     0,     8,
      94,     0,    91,    92,    93,    95,   106,     0,     0,     0,
       0,     9,     3,     0,     9,     0,     0,     0,    96,    87,
       0,    82,     0,     4,     0,     0,     7,     5,     0,     9,
       7,     0,    79,     0,    89,     6,    10,    83,    78,     0,
       0,     0,     0,    80,    88,     0,     0,    12,     0,     0,
      43,    44,    45,     0,     0,     0,    35,    36,    37,    38,
      39,    40,    41,    22,    27,    30,    32,    34,    52,    19,
      16,    48,     0,    97,    42,    50,    51,     0,    56,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,    90,     0,     0,    49,    54,     0,     0,    17,
      18,    20,    21,    23,    24,    25,    26,    28,    29,    31,
      33,    99,     0,     0,    65,     0,     0,    68,     0,     0,
       0,    67,    59,    63,    61,    98,    56,    53,     0,     0,
       0,    60,     0,    62,    64,     0,     0,    70,     0,     0,
      66,    71,     0,     0,    55,    47,   100,    72,     0,    13,
       0,    57,     0,    74,    73,    76,     0,    69,     0,     0,
       0,    15,     0,     0,    77,    58,    75
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -139,  -139,  -139,   -27,   -19,   115,  -139,  -139,  -139,   -10,
      33,    14,    29,    55,    58,  -139,   -48,  -139,   -56,   -90,
      22,    34,    38,   -83,  -139,  -139,    -9,  -139,  -139,  -139,
     -25,  -139,  -139,  -139,  -139,  -139,  -139,  -139,   141,  -139,
    -139,  -139,  -139,  -138,     1,   147
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     2,    10,    41,    11,    31,    52,   131,   166,    57,
      73,    74,    75,    76,    77,    78,    79,    89,   106,    81,
     107,   133,   134,   135,   149,   146,   147,   169,   170,    42,
      43,    32,    23,    12,    17,    13,    14,    51,    15,    16,
     103,   104,   139,    82,    83,     4
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final short[] yytable_ = yytable_init();
  private static final short[] yytable_init()
  {
    return new short[]
    {
      80,     3,     5,    38,    36,    30,   152,    40,   158,    87,
      44,   160,     6,    59,   132,    85,    86,    21,    24,   152,
      30,  -105,   -85,    54,    53,    90,    91,     1,    58,     7,
      -2,   173,   132,    39,    18,    60,    61,    62,    63,   132,
       9,     8,   109,   110,    37,    64,   151,    37,     9,   132,
      19,    20,   138,   153,    65,    88,   156,    66,    67,    68,
      69,    70,    71,    72,     7,   -46,   153,    88,   144,   145,
      92,    93,   150,   122,   123,   124,     8,   -84,    26,   125,
      98,    99,    25,     9,    29,   126,   127,   -81,   128,   129,
    -102,    94,    95,    96,    97,     9,   161,   162,    27,   148,
    -102,  -102,  -102,    33,   121,    45,  -102,  -101,   113,   114,
     115,   116,   130,    34,   145,    35,   175,  -101,  -101,  -101,
     163,   164,   165,  -101,    46,   111,   112,   117,   118,    50,
     -11,    55,    48,    49,    56,    84,   100,   101,   102,   108,
     105,   -14,   136,   137,   157,   155,   140,   142,   172,    88,
     159,   176,   167,   168,    47,   119,   171,   143,   154,   120,
     141,   174,    28,    22
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
      56,     0,     1,    30,    15,    24,     3,    15,   146,    65,
      35,   149,     0,    11,   104,    63,    64,    16,    17,     3,
      39,     0,     9,    50,    49,    40,    41,    39,    55,     8,
       0,   169,   122,    32,    55,    33,    34,    35,    36,   129,
      27,    20,    90,    91,    55,    43,   129,    55,    27,   139,
      10,    11,   108,    50,    52,    52,   139,    55,    56,    57,
      58,    59,    60,    61,     8,    50,    50,    52,   124,   125,
      42,    43,   128,    16,    17,    18,    20,     9,    55,    22,
      48,    49,    21,    27,     9,    28,    29,    13,    31,    32,
      14,    44,    45,    46,    47,    27,   152,   153,    55,   126,
      24,    25,    26,    55,   103,    19,    30,    14,    94,    95,
      96,    97,    55,    12,   170,    12,   172,    24,    25,    26,
      24,    25,    26,    30,    55,    92,    93,    98,    99,    13,
       3,    13,    55,    54,     3,    55,    38,    37,    14,    50,
      53,     3,    54,    53,    23,    51,    55,    55,     3,    52,
      55,    25,    30,    51,    39,   100,   166,   123,   136,   101,
     122,   170,    21,    16
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,    39,    63,   106,   107,   106,     0,     8,    20,    27,
      64,    66,    95,    97,    98,   100,   101,    96,    55,    10,
      11,   106,   107,    94,   106,    21,    55,    55,   100,     9,
      66,    67,    93,    55,    12,    12,    15,    55,    65,   106,
      15,    65,    91,    92,    92,    19,    55,    67,    55,    54,
      13,    99,    68,    92,    65,    13,     3,    71,    65,    11,
      33,    34,    35,    36,    43,    52,    55,    56,    57,    58,
      59,    60,    61,    72,    73,    74,    75,    76,    77,    78,
      80,    81,   105,   106,    55,    78,    78,    80,    52,    79,
      40,    41,    42,    43,    44,    45,    46,    47,    48,    49,
      38,    37,    14,   102,   103,    53,    80,    82,    50,    78,
      78,    72,    72,    73,    73,    73,    73,    74,    74,    75,
      76,   106,    16,    17,    18,    22,    28,    29,    31,    32,
      55,    69,    81,    83,    84,    85,    54,    53,    80,   104,
      55,    84,    55,    83,    80,    80,    87,    88,    65,    86,
      80,    85,     3,    50,    82,    51,    85,    23,   105,    55,
     105,    80,    80,    24,    25,    26,    70,    30,    51,    89,
      90,    71,     3,   105,    88,    80,    25
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    62,    63,    63,    64,    65,    65,    65,    66,    66,
      67,    68,    67,    69,    70,    69,    71,    72,    72,    72,
      73,    73,    73,    74,    74,    74,    74,    74,    75,    75,
      75,    76,    76,    77,    77,    78,    78,    78,    78,    78,
      78,    78,    78,    78,    78,    78,    79,    78,    78,    78,
      78,    78,    80,    81,    82,    82,    82,    83,    83,    84,
      85,    85,    85,    85,    85,    85,    85,    85,    86,    85,
      85,    85,    87,    88,    89,    88,    90,    88,    91,    92,
      92,    92,    93,    93,    94,    94,    96,    95,    97,    99,
      98,   100,   100,   100,   100,   101,   101,   103,   102,   104,
     102,   105,   105,   106,   106,   107,   107
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     3,     4,     1,     2,     1,     1,     0,
       3,     0,     5,     3,     0,     5,     2,     3,     3,     1,
       3,     3,     1,     3,     3,     3,     3,     1,     3,     3,
       1,     3,     1,     3,     1,     1,     1,     1,     1,     1,
       1,     1,     2,     1,     1,     1,     0,     5,     1,     3,
       2,     2,     1,     4,     1,     3,     0,     3,     6,     1,
       2,     1,     2,     1,     2,     1,     2,     1,     0,     4,
       2,     2,     2,     3,     0,     6,     0,     5,     2,     1,
       3,     1,     1,     3,     3,     1,     0,     4,     7,     0,
      10,     1,     1,     1,     1,     1,     3,     0,     2,     0,
       4,     3,     1,     1,     2,     1,     0
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 316;
    if (t <= 0)
      return SymbolKind.S_YYEOF;
    else if (t <= code_max)
      return SymbolKind.get(yytranslate_table_[t]);
    else
      return SymbolKind.S_YYUNDEF;
  }
  private static final byte[] yytranslate_table_ = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56,    57,    58,    59,    60,    61
    };
  }


  private static final int YYLAST_ = 163;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 6;
  private static final int YYNTOKENS_ = 62;

/* Unqualified %code blocks.  */
/* "SmashJassParser.y":36  */


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
	

/* "SmashJassParser.java":2022  */

}
/* "SmashJassParser.y":579  */
