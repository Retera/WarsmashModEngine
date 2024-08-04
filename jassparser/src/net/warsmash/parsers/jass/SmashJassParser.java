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
/* "SmashJassParser.y":58  */

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
import com.etheller.interpreter.ast.struct.*;
import com.etheller.interpreter.ast.type.*;
import com.etheller.interpreter.ast.util.JassProgram;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import com.etheller.interpreter.ast.util.JassSettings;

/* "SmashJassParser.java":66  */

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
    S_STRUCT(55),                  /* STRUCT  */
    S_ENDSTRUCT(56),               /* ENDSTRUCT  */
    S_METHOD(57),                  /* METHOD  */
    S_ENDMETHOD(58),               /* ENDMETHOD  */
    S_DOT(59),                     /* DOT  */
    S_STATIC(60),                  /* STATIC  */
    S_ID(61),                      /* ID  */
    S_STRING_LITERAL(62),          /* STRING_LITERAL  */
    S_INTEGER(63),                 /* INTEGER  */
    S_HEX_CONSTANT(64),            /* HEX_CONSTANT  */
    S_DOLLAR_HEX_CONSTANT(65),     /* DOLLAR_HEX_CONSTANT  */
    S_RAWCODE(66),                 /* RAWCODE  */
    S_REAL(67),                    /* REAL  */
    S_YYACCEPT(68),                /* $accept  */
    S_program(69),                 /* program  */
    S_typeDeclarationBlock(70),    /* typeDeclarationBlock  */
    S_type(71),                    /* type  */
    S_constant_opt(72),            /* constant_opt  */
    S_global(73),                  /* global  */
    S_local(74),                   /* local  */
    S_member(75),                  /* member  */
    S_assignTail(76),              /* assignTail  */
    S_multDivExpression(77),       /* multDivExpression  */
    S_simpleArithmeticExpression(78), /* simpleArithmeticExpression  */
    S_boolComparisonExpression(79), /* boolComparisonExpression  */
    S_boolEqualityExpression(80),  /* boolEqualityExpression  */
    S_boolAndsExpression(81),      /* boolAndsExpression  */
    S_boolExpression(82),          /* boolExpression  */
    S_baseExpression(83),          /* baseExpression  */
    S_negatableExpression(84),     /* negatableExpression  */
    S_expression(85),              /* expression  */
    S_functionExpression(86),      /* functionExpression  */
    S_methodExpression(87),        /* methodExpression  */
    S_argsList(88),                /* argsList  */
    S_setPart(89),                 /* setPart  */
    S_callPart(90),                /* callPart  */
    S_statement(91),               /* statement  */
    S_ifStatementPartial(92),      /* ifStatementPartial  */
    S_param(93),                   /* param  */
    S_paramList(94),               /* paramList  */
    S_globals(95),                 /* globals  */
    S_globals_opt(96),             /* globals_opt  */
    S_globalsBlock(97),            /* globalsBlock  */
    S_nativeBlock(98),             /* nativeBlock  */
    S_functionBlock(99),           /* functionBlock  */
    S_methodBlock(100),            /* methodBlock  */
    S_extends_opt(101),            /* extends_opt  */
    S_structDeclarationBlock(102), /* structDeclarationBlock  */
    S_103_1(103),                  /* $@1  */
    S_block(104),                  /* block  */
    S_blocks(105),                 /* blocks  */
    S_statements(106),             /* statements  */
    S_statements_opt(107),         /* statements_opt  */
    S_structStatement(108),        /* structStatement  */
    S_structStatements(109),       /* structStatements  */
    S_structStatements_opt(110),   /* structStatements_opt  */
    S_newlines(111),               /* newlines  */
    S_newlines_opt(112);           /* newlines_opt  */


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
      SymbolKind.S_STRUCT,
      SymbolKind.S_ENDSTRUCT,
      SymbolKind.S_METHOD,
      SymbolKind.S_ENDMETHOD,
      SymbolKind.S_DOT,
      SymbolKind.S_STATIC,
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
      SymbolKind.S_local,
      SymbolKind.S_member,
      SymbolKind.S_assignTail,
      SymbolKind.S_multDivExpression,
      SymbolKind.S_simpleArithmeticExpression,
      SymbolKind.S_boolComparisonExpression,
      SymbolKind.S_boolEqualityExpression,
      SymbolKind.S_boolAndsExpression,
      SymbolKind.S_boolExpression,
      SymbolKind.S_baseExpression,
      SymbolKind.S_negatableExpression,
      SymbolKind.S_expression,
      SymbolKind.S_functionExpression,
      SymbolKind.S_methodExpression,
      SymbolKind.S_argsList,
      SymbolKind.S_setPart,
      SymbolKind.S_callPart,
      SymbolKind.S_statement,
      SymbolKind.S_ifStatementPartial,
      SymbolKind.S_param,
      SymbolKind.S_paramList,
      SymbolKind.S_globals,
      SymbolKind.S_globals_opt,
      SymbolKind.S_globalsBlock,
      SymbolKind.S_nativeBlock,
      SymbolKind.S_functionBlock,
      SymbolKind.S_methodBlock,
      SymbolKind.S_extends_opt,
      SymbolKind.S_structDeclarationBlock,
      SymbolKind.S_103_1,
      SymbolKind.S_block,
      SymbolKind.S_blocks,
      SymbolKind.S_statements,
      SymbolKind.S_statements_opt,
      SymbolKind.S_structStatement,
      SymbolKind.S_structStatements,
      SymbolKind.S_structStatements_opt,
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
  "STRUCT", "ENDSTRUCT", "METHOD", "ENDMETHOD", "DOT", "STATIC", "ID",
  "STRING_LITERAL", "INTEGER", "HEX_CONSTANT", "DOLLAR_HEX_CONSTANT",
  "RAWCODE", "REAL", "$accept", "program", "typeDeclarationBlock", "type",
  "constant_opt", "global", "local", "member", "assignTail",
  "multDivExpression", "simpleArithmeticExpression",
  "boolComparisonExpression", "boolEqualityExpression",
  "boolAndsExpression", "boolExpression", "baseExpression",
  "negatableExpression", "expression", "functionExpression",
  "methodExpression", "argsList", "setPart", "callPart", "statement",
  "ifStatementPartial", "param", "paramList", "globals", "globals_opt",
  "globalsBlock", "nativeBlock", "functionBlock", "methodBlock",
  "extends_opt", "structDeclarationBlock", "$@1", "block", "blocks",
  "statements", "statements_opt", "structStatement", "structStatements",
  "structStatements_opt", "newlines", "newlines_opt", null
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
    /** Token STRUCT, to be returned by the scanner.  */
    static final int STRUCT = 310;
    /** Token ENDSTRUCT, to be returned by the scanner.  */
    static final int ENDSTRUCT = 311;
    /** Token METHOD, to be returned by the scanner.  */
    static final int METHOD = 312;
    /** Token ENDMETHOD, to be returned by the scanner.  */
    static final int ENDMETHOD = 313;
    /** Token DOT, to be returned by the scanner.  */
    static final int DOT = 314;
    /** Token STATIC, to be returned by the scanner.  */
    static final int STATIC = 315;
    /** Token ID, to be returned by the scanner.  */
    static final int ID = 316;
    /** Token STRING_LITERAL, to be returned by the scanner.  */
    static final int STRING_LITERAL = 317;
    /** Token INTEGER, to be returned by the scanner.  */
    static final int INTEGER = 318;
    /** Token HEX_CONSTANT, to be returned by the scanner.  */
    static final int HEX_CONSTANT = 319;
    /** Token DOLLAR_HEX_CONSTANT, to be returned by the scanner.  */
    static final int DOLLAR_HEX_CONSTANT = 320;
    /** Token RAWCODE, to be returned by the scanner.  */
    static final int RAWCODE = 321;
    /** Token REAL, to be returned by the scanner.  */
    static final int REAL = 322;

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

/* "SmashJassParser.java":617  */

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
          case 3: /* program: newlines_opt blocks newlines_opt  */
  if (yyn == 3)
    /* "SmashJassParser.y":112  */
        {
		jassProgram.definitionBlocks.addAll(((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))));
	};
  break;


  case 4: /* typeDeclarationBlock: TYPE ID EXTENDS ID  */
  if (yyn == 4)
    /* "SmashJassParser.y":119  */
        {
		yyval = new JassTypeDefinitionBlock(((String)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 5: /* type: ID  */
  if (yyn == 5)
    /* "SmashJassParser.y":126  */
        {
		yyval = new PrimitiveJassTypeToken(((String)(yystack.valueAt (0))));
	};
  break;


  case 6: /* type: ID ARRAY  */
  if (yyn == 6)
    /* "SmashJassParser.y":131  */
        {
		yyval = new ArrayJassTypeToken(((String)(yystack.valueAt (1))));
	};
  break;


  case 7: /* type: NOTHING  */
  if (yyn == 7)
    /* "SmashJassParser.y":136  */
        {
		yyval = NothingJassTypeToken.INSTANCE;
	};
  break;


  case 10: /* global: constant_opt type ID  */
  if (yyn == 10)
    /* "SmashJassParser.y":148  */
        {
		yyval = new JassGlobalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 11: /* global: constant_opt type ID assignTail  */
  if (yyn == 11)
    /* "SmashJassParser.y":153  */
        {
		yyval = new JassGlobalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 12: /* local: LOCAL type ID  */
  if (yyn == 12)
    /* "SmashJassParser.y":159  */
        {
		yyval = new JassLocalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 13: /* local: LOCAL type ID assignTail  */
  if (yyn == 13)
    /* "SmashJassParser.y":164  */
        {
		yyval = new JassLocalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 14: /* local: type ID  */
  if (yyn == 14)
    /* "SmashJassParser.y":169  */
        {
		yyval = new JassLocalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 15: /* local: type ID assignTail  */
  if (yyn == 15)
    /* "SmashJassParser.y":174  */
        {
		yyval = new JassLocalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 16: /* member: type ID  */
  if (yyn == 16)
    /* "SmashJassParser.y":181  */
        {
		yyval = new JassStructMemberTypeDefinition(((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))), null);
	};
  break;


  case 17: /* member: type ID assignTail  */
  if (yyn == 17)
    /* "SmashJassParser.y":186  */
        {
		yyval = new JassStructMemberTypeDefinition(((JassTypeToken)(yystack.valueAt (2))), ((String)(yystack.valueAt (1))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 18: /* assignTail: EQUALS expression  */
  if (yyn == 18)
    /* "SmashJassParser.y":193  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 19: /* multDivExpression: multDivExpression TIMES negatableExpression  */
  if (yyn == 19)
    /* "SmashJassParser.y":199  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.MULTIPLY);
	};
  break;


  case 20: /* multDivExpression: multDivExpression DIVIDE negatableExpression  */
  if (yyn == 20)
    /* "SmashJassParser.y":204  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.DIVIDE);
	};
  break;


  case 22: /* simpleArithmeticExpression: simpleArithmeticExpression PLUS multDivExpression  */
  if (yyn == 22)
    /* "SmashJassParser.y":213  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.ADD);
	};
  break;


  case 23: /* simpleArithmeticExpression: simpleArithmeticExpression MINUS multDivExpression  */
  if (yyn == 23)
    /* "SmashJassParser.y":218  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.SUBTRACT);
	};
  break;


  case 25: /* boolComparisonExpression: boolComparisonExpression LESS simpleArithmeticExpression  */
  if (yyn == 25)
    /* "SmashJassParser.y":227  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS);
	};
  break;


  case 26: /* boolComparisonExpression: boolComparisonExpression GREATER simpleArithmeticExpression  */
  if (yyn == 26)
    /* "SmashJassParser.y":232  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER);
	};
  break;


  case 27: /* boolComparisonExpression: boolComparisonExpression LESS_EQUALS simpleArithmeticExpression  */
  if (yyn == 27)
    /* "SmashJassParser.y":237  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS_OR_EQUALS);
	};
  break;


  case 28: /* boolComparisonExpression: boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression  */
  if (yyn == 28)
    /* "SmashJassParser.y":242  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER_OR_EQUALS);
	};
  break;


  case 30: /* boolEqualityExpression: boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression  */
  if (yyn == 30)
    /* "SmashJassParser.y":251  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.EQUALS);
	};
  break;


  case 31: /* boolEqualityExpression: boolEqualityExpression NOT_EQUALS boolComparisonExpression  */
  if (yyn == 31)
    /* "SmashJassParser.y":256  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.NOT_EQUALS);
	};
  break;


  case 33: /* boolAndsExpression: boolAndsExpression AND boolEqualityExpression  */
  if (yyn == 33)
    /* "SmashJassParser.y":265  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.AND);
	};
  break;


  case 35: /* boolExpression: boolExpression OR boolAndsExpression  */
  if (yyn == 35)
    /* "SmashJassParser.y":274  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.OR);
	};
  break;


  case 37: /* baseExpression: ID  */
  if (yyn == 37)
    /* "SmashJassParser.y":283  */
        {
		yyval = new ReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 38: /* baseExpression: STRING_LITERAL  */
  if (yyn == 38)
    /* "SmashJassParser.y":288  */
        {
		yyval = new LiteralJassExpression(StringJassValue.of(((String)(yystack.valueAt (0)))));
	};
  break;


  case 39: /* baseExpression: INTEGER  */
  if (yyn == 39)
    /* "SmashJassParser.y":293  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 40: /* baseExpression: HEX_CONSTANT  */
  if (yyn == 40)
    /* "SmashJassParser.y":298  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 41: /* baseExpression: DOLLAR_HEX_CONSTANT  */
  if (yyn == 41)
    /* "SmashJassParser.y":303  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 42: /* baseExpression: RAWCODE  */
  if (yyn == 42)
    /* "SmashJassParser.y":308  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 43: /* baseExpression: REAL  */
  if (yyn == 43)
    /* "SmashJassParser.y":313  */
        {
		yyval = new LiteralJassExpression(RealJassValue.of(((double)(yystack.valueAt (0)))));
	};
  break;


  case 44: /* baseExpression: FUNCTION ID  */
  if (yyn == 44)
    /* "SmashJassParser.y":318  */
        {
		yyval = new FunctionReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 45: /* baseExpression: baseExpression DOT ID  */
  if (yyn == 45)
    /* "SmashJassParser.y":323  */
        {
		yyval = new MemberJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 46: /* baseExpression: NULL  */
  if (yyn == 46)
    /* "SmashJassParser.y":328  */
        {
		yyval = new LiteralJassExpression(null);
	};
  break;


  case 47: /* baseExpression: TRUE  */
  if (yyn == 47)
    /* "SmashJassParser.y":333  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.TRUE);
	};
  break;


  case 48: /* baseExpression: FALSE  */
  if (yyn == 48)
    /* "SmashJassParser.y":338  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.FALSE);
	};
  break;


  case 49: /* baseExpression: ID OPEN_BRACKET expression CLOSE_BRACKET  */
  if (yyn == 49)
    /* "SmashJassParser.y":343  */
        {
		yyval = new ArrayRefJassExpression(((String)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (1))));
	};
  break;


  case 50: /* baseExpression: functionExpression  */
  if (yyn == 50)
    /* "SmashJassParser.y":348  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
		// Handled by functionExpression, no need to extra code in a call expr
	};
  break;


  case 51: /* baseExpression: methodExpression  */
  if (yyn == 51)
    /* "SmashJassParser.y":354  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 52: /* baseExpression: OPEN_PAREN expression CLOSE_PAREN  */
  if (yyn == 52)
    /* "SmashJassParser.y":359  */
        {
		yyval = ((JassExpression)(yystack.valueAt (1)));
		// handled by expression
	};
  break;


  case 53: /* negatableExpression: baseExpression  */
  if (yyn == 53)
    /* "SmashJassParser.y":367  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 54: /* negatableExpression: NOT baseExpression  */
  if (yyn == 54)
    /* "SmashJassParser.y":372  */
        {
		yyval = new NotJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 55: /* negatableExpression: MINUS baseExpression  */
  if (yyn == 55)
    /* "SmashJassParser.y":377  */
        {
		yyval = new NegateJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 56: /* negatableExpression: METHOD baseExpression DOT ID  */
  if (yyn == 56)
    /* "SmashJassParser.y":382  */
        {
		yyval = new MethodReferenceJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 57: /* expression: boolExpression  */
  if (yyn == 57)
    /* "SmashJassParser.y":389  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 58: /* functionExpression: ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 58)
    /* "SmashJassParser.y":395  */
        {
		yyval = new FunctionCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 59: /* methodExpression: baseExpression DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 59)
    /* "SmashJassParser.y":402  */
        {
		yyval = new MethodCallJassExpression(((JassExpression)(yystack.valueAt (5))), ((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 60: /* methodExpression: DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 60)
    /* "SmashJassParser.y":407  */
        {
		yyval = new ParentlessMethodCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 61: /* argsList: expression  */
  if (yyn == 61)
    /* "SmashJassParser.y":414  */
        {
		LinkedList<JassExpression> list = new LinkedList<JassExpression>();
		list.addFirst(((JassExpression)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 62: /* argsList: expression COMMA argsList  */
  if (yyn == 62)
    /* "SmashJassParser.y":421  */
        {
		LinkedList<JassExpression> list = ((LinkedList<JassExpression>)(yystack.valueAt (0)));
		list.addFirst(((JassExpression)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 63: /* argsList: %empty  */
  if (yyn == 63)
    /* "SmashJassParser.y":428  */
        {
		yyval = new LinkedList<JassExpression>();
	};
  break;


  case 64: /* setPart: ID EQUALS expression  */
  if (yyn == 64)
    /* "SmashJassParser.y":439  */
        {
		yyval = new JassSetStatement(((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 65: /* setPart: ID OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression  */
  if (yyn == 65)
    /* "SmashJassParser.y":444  */
        {
		yyval = new JassArrayedAssignmentStatement(((String)(yystack.valueAt (5))), ((JassExpression)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 66: /* setPart: baseExpression DOT ID EQUALS expression  */
  if (yyn == 66)
    /* "SmashJassParser.y":449  */
        {
		yyval = new JassSetMemberStatement(((JassExpression)(yystack.valueAt (4))), ((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 67: /* callPart: functionExpression  */
  if (yyn == 67)
    /* "SmashJassParser.y":456  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 68: /* callPart: methodExpression  */
  if (yyn == 68)
    /* "SmashJassParser.y":461  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 69: /* statement: CALL callPart  */
  if (yyn == 69)
    /* "SmashJassParser.y":468  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 70: /* statement: callPart  */
  if (yyn == 70)
    /* "SmashJassParser.y":473  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 71: /* statement: SET setPart  */
  if (yyn == 71)
    /* "SmashJassParser.y":478  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 72: /* statement: setPart  */
  if (yyn == 72)
    /* "SmashJassParser.y":483  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 73: /* statement: RETURN expression  */
  if (yyn == 73)
    /* "SmashJassParser.y":488  */
        {
		yyval = new JassReturnStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 74: /* statement: RETURN  */
  if (yyn == 74)
    /* "SmashJassParser.y":493  */
        {
		yyval = new JassReturnNothingStatement();
	};
  break;


  case 75: /* statement: EXITWHEN expression  */
  if (yyn == 75)
    /* "SmashJassParser.y":498  */
        {
		yyval = new JassExitWhenStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 76: /* statement: local  */
  if (yyn == 76)
    /* "SmashJassParser.y":503  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 77: /* statement: LOOP statements_opt ENDLOOP  */
  if (yyn == 77)
    /* "SmashJassParser.y":508  */
        {
		yyval = new JassLoopStatement(((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 78: /* statement: IF ifStatementPartial  */
  if (yyn == 78)
    /* "SmashJassParser.y":513  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 79: /* statement: DEBUG statement  */
  if (yyn == 79)
    /* "SmashJassParser.y":518  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 80: /* ifStatementPartial: expression THEN statements_opt ENDIF  */
  if (yyn == 80)
    /* "SmashJassParser.y":526  */
        {
		yyval = new JassIfStatement(((JassExpression)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 81: /* ifStatementPartial: expression THEN statements_opt ELSE statements_opt ENDIF  */
  if (yyn == 81)
    /* "SmashJassParser.y":531  */
        {
		yyval = new JassIfElseStatement(((JassExpression)(yystack.valueAt (5))), ((LinkedList<JassStatement>)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 82: /* ifStatementPartial: expression THEN statements_opt ELSEIF ifStatementPartial  */
  if (yyn == 82)
    /* "SmashJassParser.y":536  */
        {
		yyval = new JassIfElseIfStatement(((JassExpression)(yystack.valueAt (4))), ((LinkedList<JassStatement>)(yystack.valueAt (2))), ((JassStatement)(yystack.valueAt (0))));
	};
  break;


  case 83: /* param: type ID  */
  if (yyn == 83)
    /* "SmashJassParser.y":543  */
        {
		yyval = new JassParameterDefinition(((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 84: /* paramList: param  */
  if (yyn == 84)
    /* "SmashJassParser.y":550  */
        {
		LinkedList<JassParameterDefinition> list = new LinkedList<JassParameterDefinition>();
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 85: /* paramList: param COMMA paramList  */
  if (yyn == 85)
    /* "SmashJassParser.y":557  */
        {
		LinkedList<JassParameterDefinition> list = ((LinkedList<JassParameterDefinition>)(yystack.valueAt (0)));
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 86: /* paramList: NOTHING  */
  if (yyn == 86)
    /* "SmashJassParser.y":564  */
        {
		yyval = new LinkedList<JassParameterDefinition>();
	};
  break;


  case 87: /* globals: global  */
  if (yyn == 87)
    /* "SmashJassParser.y":571  */
        {
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		list.addFirst(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 88: /* globals: globals newlines global  */
  if (yyn == 88)
    /* "SmashJassParser.y":578  */
        {
		LinkedList<JassStatement> list = ((LinkedList<JassStatement>)(yystack.valueAt (2)));
		list.addLast(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 89: /* globals_opt: newlines globals newlines  */
  if (yyn == 89)
    /* "SmashJassParser.y":587  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 90: /* globals_opt: newlines  */
  if (yyn == 90)
    /* "SmashJassParser.y":592  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 91: /* globalsBlock: GLOBALS globals_opt ENDGLOBALS  */
  if (yyn == 91)
    /* "SmashJassParser.y":599  */
        {
		yyval = new JassGlobalsDefinitionBlock(getLine(), currentParsingFilePath, ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 92: /* nativeBlock: constant_opt NATIVE ID TAKES paramList RETURNS type  */
  if (yyn == 92)
    /* "SmashJassParser.y":605  */
        {
		final String text = ((String)(yystack.valueAt (4)));
		yyval = new JassNativeDefinitionBlock(getLine(), currentParsingFilePath, text, ((LinkedList<JassParameterDefinition>)(yystack.valueAt (2))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 93: /* functionBlock: constant_opt FUNCTION ID TAKES paramList RETURNS type statements_opt ENDFUNCTION  */
  if (yyn == 93)
    /* "SmashJassParser.y":613  */
        {
		yyval = new JassFunctionDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))));
	};
  break;


  case 94: /* methodBlock: constant_opt METHOD ID TAKES paramList RETURNS type statements_opt ENDMETHOD  */
  if (yyn == 94)
    /* "SmashJassParser.y":620  */
        {
		yyval = new JassMethodDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))), false);
	};
  break;


  case 95: /* methodBlock: constant_opt STATIC METHOD ID TAKES paramList RETURNS type statements_opt ENDMETHOD  */
  if (yyn == 95)
    /* "SmashJassParser.y":625  */
        {
		yyval = new JassMethodDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))), true);
	};
  break;


  case 96: /* extends_opt: EXTENDS type  */
  if (yyn == 96)
    /* "SmashJassParser.y":632  */
        {
		yyval = ((JassTypeToken)(yystack.valueAt (0)));
	};
  break;


  case 97: /* extends_opt: %empty  */
  if (yyn == 97)
    /* "SmashJassParser.y":636  */
        {
		yyval = NothingJassTypeToken.INSTANCE;
	};
  break;


  case 98: /* $@1: %empty  */
  if (yyn == 98)
    /* "SmashJassParser.y":643  */
        {
		currentStruct = new JassStructDefinitionBlock(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 99: /* structDeclarationBlock: STRUCT ID extends_opt $@1 structStatements_opt ENDSTRUCT  */
  if (yyn == 99)
    /* "SmashJassParser.y":647  */
        {
		yyval = currentStruct;
	};
  break;


  case 100: /* block: globalsBlock  */
  if (yyn == 100)
    /* "SmashJassParser.y":654  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 101: /* block: nativeBlock  */
  if (yyn == 101)
    /* "SmashJassParser.y":659  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 102: /* block: functionBlock  */
  if (yyn == 102)
    /* "SmashJassParser.y":664  */
        {
		yyval = ((JassFunctionDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 103: /* block: typeDeclarationBlock  */
  if (yyn == 103)
    /* "SmashJassParser.y":669  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 104: /* block: structDeclarationBlock  */
  if (yyn == 104)
    /* "SmashJassParser.y":674  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 105: /* blocks: block  */
  if (yyn == 105)
    /* "SmashJassParser.y":681  */
        {
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 106: /* blocks: blocks newlines block  */
  if (yyn == 106)
    /* "SmashJassParser.y":688  */
        {
		LinkedList<JassDefinitionBlock> list = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (2)));
		list.addLast(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 107: /* statements: statement  */
  if (yyn == 107)
    /* "SmashJassParser.y":697  */
        {
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		JassStatement statement;
		if (JassSettings.DEBUG) {
			statement = new DebuggingJassStatement(getLine(), ((JassStatement)(yystack.valueAt (0))));
		} else {
			statement = ((JassStatement)(yystack.valueAt (0)));
		}
		list.addFirst(statement);
		yyval = list;
	};
  break;


  case 108: /* statements: statements newlines statement  */
  if (yyn == 108)
    /* "SmashJassParser.y":710  */
        {
		LinkedList<JassStatement> list = ((LinkedList<JassStatement>)(yystack.valueAt (2)));
		JassStatement statement;
		if (JassSettings.DEBUG) {
			statement = new DebuggingJassStatement(getLine(), ((JassStatement)(yystack.valueAt (0))));
		} else {
			statement = ((JassStatement)(yystack.valueAt (0)));
		}
		list.addLast(statement);
		yyval = list;
	};
  break;


  case 109: /* statements_opt: newlines statements newlines  */
  if (yyn == 109)
    /* "SmashJassParser.y":725  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 110: /* statements_opt: newlines  */
  if (yyn == 110)
    /* "SmashJassParser.y":730  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 111: /* structStatement: member  */
  if (yyn == 111)
    /* "SmashJassParser.y":737  */
        {
		currentStruct.add(((JassStructMemberTypeDefinition)(yystack.valueAt (0))));
	};
  break;


  case 112: /* structStatement: methodBlock  */
  if (yyn == 112)
    /* "SmashJassParser.y":742  */
        {
		currentStruct.add(((JassMethodDefinitionBlock)(yystack.valueAt (0))));
	};
  break;



/* "SmashJassParser.java":1755  */

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

  private static final short yypact_ninf_ = -144;
  private static final short yytable_ninf_ = -120;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
     -22,   -22,    27,    32,    40,  -144,  -144,   -22,   -10,  -144,
      14,  -144,    67,  -144,  -144,  -144,  -144,  -144,   -22,    47,
      16,    60,    62,    24,    38,    18,  -144,  -144,    13,  -144,
     -22,    53,    13,  -144,    56,   100,  -144,  -144,    96,    66,
      25,  -144,  -144,   -22,    26,    26,  -144,   129,  -144,    77,
      73,   123,    76,    89,   148,   153,    83,  -144,  -144,   111,
     -11,  -144,  -144,  -144,   -22,  -144,    26,    13,    13,   112,
    -144,  -144,  -144,   176,   176,    83,   176,   113,     9,  -144,
    -144,  -144,  -144,  -144,  -144,    51,    55,   108,    57,   141,
     143,   124,  -144,  -144,  -144,  -144,   129,   125,   127,    95,
    -144,  -144,   -22,  -144,   124,   124,   132,   131,   144,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      83,    83,    83,   136,  -144,   186,   138,  -144,   187,   160,
    -144,   139,    83,   151,   149,   152,  -144,  -144,    51,    51,
      55,    55,    55,    55,   108,   108,    57,   141,   154,    26,
     192,  -144,   176,   196,    83,    83,    13,   -22,    83,   160,
      34,   147,  -144,   155,   174,   177,  -144,  -144,  -144,   -22,
      -5,   162,  -144,    83,  -144,    83,   204,    26,   124,  -144,
      12,  -144,  -144,   195,  -144,   159,   202,  -144,  -144,    83,
      83,   129,   172,   160,  -144,  -144,   181,    13,   223,   -22,
     129,  -144,  -144,   193,  -144,    17,  -144,  -144,   -22,    13,
      99,  -144,   242,    83,   188,   -22,   -22,  -144,    83,    83,
    -144,  -144,   189,   224,  -144,  -144,  -144,  -144
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
     120,   117,     0,   119,     9,   118,     1,     0,     0,     8,
       0,   103,     0,   100,   101,   102,   104,   105,   120,     0,
       9,     0,    97,     0,     0,     9,     3,    91,     0,    87,
       0,     0,     0,    98,     0,     0,   106,     7,     5,     0,
       9,     4,    96,     0,     0,     0,     6,    10,    88,     0,
       9,     7,     0,    84,     0,     0,     0,    11,    99,     0,
       0,   111,   112,   113,     0,    83,     0,     0,     0,     0,
      46,    47,    48,     0,     0,     0,     0,     0,    37,    38,
      39,    40,    41,    42,    43,    24,    29,    32,    34,    36,
      57,    53,    21,    18,    50,    51,    16,     0,     0,     9,
      85,    92,     0,    44,    54,    55,     0,     0,     0,     0,
      63,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,    17,     0,     0,   114,     0,   110,
      52,     0,    63,     0,    61,     0,    19,    20,    22,    23,
      25,    26,    27,    28,    30,    31,    33,    35,    45,     0,
       0,    93,     0,     0,    74,     0,     0,     0,     0,     0,
       5,     0,    76,     0,    50,    51,    72,    70,   107,     0,
      56,     0,    49,    63,    58,    63,     0,     0,     0,    69,
      37,    71,    73,     0,    78,     0,     0,    75,    79,     0,
       0,    14,     0,   109,    60,    62,     0,     0,     0,     0,
      12,    77,    64,     0,    15,    45,   108,    59,     0,     0,
       0,    13,    49,     0,     0,     0,     0,    80,     0,     0,
      66,    94,     0,     0,    82,    65,    95,    81
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -144,  -144,  -144,   -28,   -17,   210,  -144,  -144,   -89,     7,
      42,    44,   130,   134,  -144,   -63,    -3,   -51,  -123,  -117,
    -118,   101,   114,  -143,    35,  -144,   -36,  -144,  -144,  -144,
    -144,  -144,  -144,  -144,  -144,  -144,   227,  -144,  -144,  -136,
     165,  -144,  -144,     1,   247
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     2,    11,    52,    12,    29,   162,    61,    57,    85,
      86,    87,    88,    89,    90,    91,    92,   134,    94,    95,
     135,   166,   167,   168,   184,    53,    54,    30,    19,    13,
      14,    15,    62,    33,    16,    43,    17,    18,   169,   128,
      63,    64,    49,   129,     4
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
      39,     3,     5,    28,    42,    93,   164,   124,    20,    55,
     104,   105,   165,   107,   171,   189,   188,     1,  -119,    25,
     213,   186,    59,    28,   106,   -90,     7,     6,    37,   164,
     100,    40,    -2,    60,   -89,   165,   164,   189,     8,   101,
     102,    51,   165,     9,    50,     9,    97,   175,     7,    98,
     206,    21,     9,    46,   -45,   195,    27,   196,   133,   109,
       8,   110,   190,   210,   110,    99,   163,     9,    44,   175,
     164,    59,   214,    10,    38,    22,   165,    23,    24,   222,
     223,    31,    60,    32,   190,    34,   110,    38,    37,   178,
     163,   111,   112,   -37,    69,    10,   163,   113,   114,    35,
       9,   161,   204,   182,   183,   119,   120,   187,   136,   137,
      37,   211,    45,   176,    41,    46,    70,    71,    72,    73,
     138,   139,     9,   216,   217,   218,    74,    47,   185,  -116,
     163,   161,    56,    58,    38,    75,   -86,    65,   202,   203,
      76,   198,    77,    66,    78,    79,    80,    81,    82,    83,
      84,  -115,   115,   116,   117,   118,    38,   140,   141,   142,
     143,    67,   220,   144,   145,   161,    68,   183,   225,   208,
     193,    69,    96,   103,   108,    37,   152,   153,   154,   121,
     122,   215,   155,   123,   126,   130,   125,    69,   156,   157,
     131,   158,   159,    70,    71,    72,   132,   148,   149,   150,
     170,   151,   172,   173,   177,   174,   175,    69,   191,    70,
      71,    72,    75,   -67,   192,   194,   -68,   197,   199,    77,
     200,   160,    79,    80,    81,    82,    83,    84,    75,    70,
      71,    72,   201,   205,   207,    77,   209,    78,    79,    80,
      81,    82,    83,    84,   212,   219,   221,   226,    75,   227,
      48,   146,    36,   224,   181,    77,   147,   180,    79,    80,
      81,    82,    83,    84,   127,    26,   179
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
      28,     0,     1,    20,    32,    56,   129,    96,     7,    45,
      73,    74,   129,    76,   132,     3,   159,    39,     0,    18,
       3,   157,    50,    40,    75,     9,     8,     0,    15,   152,
      66,    30,     0,    50,     9,   152,   159,     3,    20,    67,
      68,    15,   159,    27,    43,    27,    57,    52,     8,    60,
     193,    61,    27,    19,    59,   173,     9,   175,   109,    50,
      20,    52,    50,   199,    52,    64,   129,    27,    12,    52,
     193,    99,   208,    55,    61,    61,   193,    10,    11,   215,
     216,    21,    99,    21,    50,    61,    52,    61,    15,   152,
     153,    40,    41,    59,    11,    55,   159,    42,    43,    61,
      27,   129,   191,   154,   155,    48,    49,   158,   111,   112,
      15,   200,    12,   149,    61,    19,    33,    34,    35,    36,
     113,   114,    27,    24,    25,    26,    43,    61,   156,    56,
     193,   159,     3,    56,    61,    52,    13,    61,   189,   190,
      57,   177,    59,    54,    61,    62,    63,    64,    65,    66,
      67,    56,    44,    45,    46,    47,    61,   115,   116,   117,
     118,    13,   213,   119,   120,   193,    13,   218,   219,   197,
     169,    11,    61,    61,    61,    15,    16,    17,    18,    38,
      37,   209,    22,    59,    57,    53,    61,    11,    28,    29,
      59,    31,    32,    33,    34,    35,    52,    61,    12,    61,
      61,    14,    51,    54,    12,    53,    52,    11,    61,    33,
      34,    35,    52,    39,    59,    53,    39,    13,    23,    59,
      61,    61,    62,    63,    64,    65,    66,    67,    52,    33,
      34,    35,    30,    61,    53,    59,    13,    61,    62,    63,
      64,    65,    66,    67,    51,     3,    58,    58,    52,    25,
      40,   121,    25,   218,   153,    59,   122,    61,    62,    63,
      64,    65,    66,    67,    99,    18,   152
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,    39,    69,   111,   112,   111,     0,     8,    20,    27,
      55,    70,    72,    97,    98,    99,   102,   104,   105,    96,
     111,    61,    61,    10,    11,   111,   112,     9,    72,    73,
      95,    21,    21,   101,    61,    61,   104,    15,    61,    71,
     111,    61,    71,   103,    12,    12,    19,    61,    73,   110,
     111,    15,    71,    93,    94,    94,     3,    76,    56,    71,
      72,    75,   100,   108,   109,    61,    54,    13,    13,    11,
      33,    34,    35,    36,    43,    52,    57,    59,    61,    62,
      63,    64,    65,    66,    67,    77,    78,    79,    80,    81,
      82,    83,    84,    85,    86,    87,    61,    57,    60,   111,
      94,    71,    71,    61,    83,    83,    85,    83,    61,    50,
      52,    40,    41,    42,    43,    44,    45,    46,    47,    48,
      49,    38,    37,    59,    76,    61,    57,   108,   107,   111,
      53,    59,    52,    85,    85,    88,    84,    84,    77,    77,
      78,    78,    78,    78,    79,    79,    80,    81,    61,    12,
      61,    14,    16,    17,    18,    22,    28,    29,    31,    32,
      61,    71,    74,    83,    86,    87,    89,    90,    91,   106,
      61,    88,    51,    54,    53,    52,    94,    12,    83,    90,
      61,    89,    85,    85,    92,    71,   107,    85,    91,     3,
      50,    61,    59,   111,    53,    88,    88,    13,    94,    23,
      61,    30,    85,    85,    76,    61,    91,    53,    71,    13,
     107,    76,    51,     3,   107,    71,    24,    25,    26,     3,
      85,    58,   107,   107,    92,    85,    58,    25
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    68,    69,    69,    70,    71,    71,    71,    72,    72,
      73,    73,    74,    74,    74,    74,    75,    75,    76,    77,
      77,    77,    78,    78,    78,    79,    79,    79,    79,    79,
      80,    80,    80,    81,    81,    82,    82,    83,    83,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      83,    83,    83,    84,    84,    84,    84,    85,    86,    87,
      87,    88,    88,    88,    89,    89,    89,    90,    90,    91,
      91,    91,    91,    91,    91,    91,    91,    91,    91,    91,
      92,    92,    92,    93,    94,    94,    94,    95,    95,    96,
      96,    97,    98,    99,   100,   100,   101,   101,   103,   102,
     104,   104,   104,   104,   104,   105,   105,   106,   106,   107,
     107,   108,   108,   109,   109,   110,   110,   111,   111,   112,
     112
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     3,     4,     1,     2,     1,     1,     0,
       3,     4,     3,     4,     2,     3,     2,     3,     2,     3,
       3,     1,     3,     3,     1,     3,     3,     3,     3,     1,
       3,     3,     1,     3,     1,     3,     1,     1,     1,     1,
       1,     1,     1,     1,     2,     3,     1,     1,     1,     4,
       1,     1,     3,     1,     2,     2,     4,     1,     4,     6,
       5,     1,     3,     0,     3,     6,     5,     1,     1,     2,
       1,     2,     1,     2,     1,     2,     1,     3,     2,     2,
       4,     6,     5,     2,     1,     3,     1,     1,     3,     3,
       1,     3,     7,     9,     9,    10,     2,     0,     0,     6,
       1,     1,     1,     1,     1,     1,     3,     1,     3,     3,
       1,     1,     1,     1,     3,     3,     1,     1,     2,     1,
       0
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 322;
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
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67
    };
  }


  private static final int YYLAST_ = 266;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 6;
  private static final int YYNTOKENS_ = 68;

/* Unqualified %code blocks.  */
/* "SmashJassParser.y":36  */


	private String currentParsingFilePath;
	private GlobalScope globalScope;
	private JassNativeManager jassNativeManager;
	private JassProgram jassProgram;
	private JassStructDefinitionBlock currentStruct;
	
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
	

/* "SmashJassParser.java":2444  */

}
/* "SmashJassParser.y":770  */
