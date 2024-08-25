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

/* "SmashJassParser.java":68  */

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
    S_LIBRARY(61),                 /* LIBRARY  */
    S_LIBRARY_ONCE(62),            /* LIBRARY_ONCE  */
    S_ENDLIBRARY(63),              /* ENDLIBRARY  */
    S_SCOPE(64),                   /* SCOPE  */
    S_ENDSCOPE(65),                /* ENDSCOPE  */
    S_INTERFACE(66),               /* INTERFACE  */
    S_ENDINTERFACE(67),            /* ENDINTERFACE  */
    S_REQUIRES(68),                /* REQUIRES  */
    S_OPTIONAL(69),                /* OPTIONAL  */
    S_PRIVATE(70),                 /* PRIVATE  */
    S_PUBLIC(71),                  /* PUBLIC  */
    S_READONLY(72),                /* READONLY  */
    S_OPERATOR(73),                /* OPERATOR  */
    S_IMPLEMENT(74),               /* IMPLEMENT  */
    S_MODULE(75),                  /* MODULE  */
    S_ENDMODULE(76),               /* ENDMODULE  */
    S_INITIALIZER(77),             /* INITIALIZER  */
    S_DEFAULTS(78),                /* DEFAULTS  */
    S_ID(79),                      /* ID  */
    S_STRING_LITERAL(80),          /* STRING_LITERAL  */
    S_INTEGER(81),                 /* INTEGER  */
    S_HEX_CONSTANT(82),            /* HEX_CONSTANT  */
    S_DOLLAR_HEX_CONSTANT(83),     /* DOLLAR_HEX_CONSTANT  */
    S_RAWCODE(84),                 /* RAWCODE  */
    S_REAL(85),                    /* REAL  */
    S_YYACCEPT(86),                /* $accept  */
    S_program(87),                 /* program  */
    S_typeDeclarationBlock(88),    /* typeDeclarationBlock  */
    S_type(89),                    /* type  */
    S_qualifier(90),               /* qualifier  */
    S_qualifiers(91),              /* qualifiers  */
    S_qualifiers_opt(92),          /* qualifiers_opt  */
    S_global(93),                  /* global  */
    S_local(94),                   /* local  */
    S_member(95),                  /* member  */
    S_assignTail(96),              /* assignTail  */
    S_multDivExpression(97),       /* multDivExpression  */
    S_simpleArithmeticExpression(98), /* simpleArithmeticExpression  */
    S_boolComparisonExpression(99), /* boolComparisonExpression  */
    S_boolEqualityExpression(100), /* boolEqualityExpression  */
    S_boolAndsExpression(101),     /* boolAndsExpression  */
    S_boolExpression(102),         /* boolExpression  */
    S_baseExpression(103),         /* baseExpression  */
    S_negatableExpression(104),    /* negatableExpression  */
    S_expression(105),             /* expression  */
    S_functionExpression(106),     /* functionExpression  */
    S_methodExpression(107),       /* methodExpression  */
    S_argsList(108),               /* argsList  */
    S_setPart(109),                /* setPart  */
    S_callPart(110),               /* callPart  */
    S_statement(111),              /* statement  */
    S_ifStatementPartial(112),     /* ifStatementPartial  */
    S_param(113),                  /* param  */
    S_paramList(114),              /* paramList  */
    S_requirement(115),            /* requirement  */
    S_requirementList(116),        /* requirementList  */
    S_requirementList_opt(117),    /* requirementList_opt  */
    S_globals(118),                /* globals  */
    S_globals_opt(119),            /* globals_opt  */
    S_globalsBlock(120),           /* globalsBlock  */
    S_nativeBlock(121),            /* nativeBlock  */
    S_functionBlock(122),          /* functionBlock  */
    S_methodBlock(123),            /* methodBlock  */
    S_implementModuleStatement(124), /* implementModuleStatement  */
    S_defaultsTail(125),           /* defaultsTail  */
    S_interfaceMethodBlock(126),   /* interfaceMethodBlock  */
    S_libraryBlock(127),           /* libraryBlock  */
    S_scopeBlock(128),             /* scopeBlock  */
    S_extends_opt(129),            /* extends_opt  */
    S_structDeclarationBlock(130), /* structDeclarationBlock  */
    S_131_1(131),                  /* $@1  */
    S_interfaceDeclarationBlock(132), /* interfaceDeclarationBlock  */
    S_133_2(133),                  /* $@2  */
    S_moduleDeclarationBlock(134), /* moduleDeclarationBlock  */
    S_135_3(135),                  /* $@3  */
    S_nonLibraryBlock(136),        /* nonLibraryBlock  */
    S_block(137),                  /* block  */
    S_blocks(138),                 /* blocks  */
    S_nonLibraryBlocks(139),       /* nonLibraryBlocks  */
    S_statements(140),             /* statements  */
    S_statements_opt(141),         /* statements_opt  */
    S_blocks_opt(142),             /* blocks_opt  */
    S_nonLibraryBlocks_opt(143),   /* nonLibraryBlocks_opt  */
    S_structStatement(144),        /* structStatement  */
    S_interfaceStatement(145),     /* interfaceStatement  */
    S_structStatements(146),       /* structStatements  */
    S_interfaceStatements(147),    /* interfaceStatements  */
    S_structStatements_opt(148),   /* structStatements_opt  */
    S_interfaceStatements_opt(149), /* interfaceStatements_opt  */
    S_newlines(150),               /* newlines  */
    S_newlines_opt(151);           /* newlines_opt  */


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
      SymbolKind.S_LIBRARY,
      SymbolKind.S_LIBRARY_ONCE,
      SymbolKind.S_ENDLIBRARY,
      SymbolKind.S_SCOPE,
      SymbolKind.S_ENDSCOPE,
      SymbolKind.S_INTERFACE,
      SymbolKind.S_ENDINTERFACE,
      SymbolKind.S_REQUIRES,
      SymbolKind.S_OPTIONAL,
      SymbolKind.S_PRIVATE,
      SymbolKind.S_PUBLIC,
      SymbolKind.S_READONLY,
      SymbolKind.S_OPERATOR,
      SymbolKind.S_IMPLEMENT,
      SymbolKind.S_MODULE,
      SymbolKind.S_ENDMODULE,
      SymbolKind.S_INITIALIZER,
      SymbolKind.S_DEFAULTS,
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
      SymbolKind.S_qualifier,
      SymbolKind.S_qualifiers,
      SymbolKind.S_qualifiers_opt,
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
      SymbolKind.S_requirement,
      SymbolKind.S_requirementList,
      SymbolKind.S_requirementList_opt,
      SymbolKind.S_globals,
      SymbolKind.S_globals_opt,
      SymbolKind.S_globalsBlock,
      SymbolKind.S_nativeBlock,
      SymbolKind.S_functionBlock,
      SymbolKind.S_methodBlock,
      SymbolKind.S_implementModuleStatement,
      SymbolKind.S_defaultsTail,
      SymbolKind.S_interfaceMethodBlock,
      SymbolKind.S_libraryBlock,
      SymbolKind.S_scopeBlock,
      SymbolKind.S_extends_opt,
      SymbolKind.S_structDeclarationBlock,
      SymbolKind.S_131_1,
      SymbolKind.S_interfaceDeclarationBlock,
      SymbolKind.S_133_2,
      SymbolKind.S_moduleDeclarationBlock,
      SymbolKind.S_135_3,
      SymbolKind.S_nonLibraryBlock,
      SymbolKind.S_block,
      SymbolKind.S_blocks,
      SymbolKind.S_nonLibraryBlocks,
      SymbolKind.S_statements,
      SymbolKind.S_statements_opt,
      SymbolKind.S_blocks_opt,
      SymbolKind.S_nonLibraryBlocks_opt,
      SymbolKind.S_structStatement,
      SymbolKind.S_interfaceStatement,
      SymbolKind.S_structStatements,
      SymbolKind.S_interfaceStatements,
      SymbolKind.S_structStatements_opt,
      SymbolKind.S_interfaceStatements_opt,
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
  "STRUCT", "ENDSTRUCT", "METHOD", "ENDMETHOD", "DOT", "STATIC", "LIBRARY",
  "LIBRARY_ONCE", "ENDLIBRARY", "SCOPE", "ENDSCOPE", "INTERFACE",
  "ENDINTERFACE", "REQUIRES", "OPTIONAL", "PRIVATE", "PUBLIC", "READONLY",
  "OPERATOR", "IMPLEMENT", "MODULE", "ENDMODULE", "INITIALIZER",
  "DEFAULTS", "ID", "STRING_LITERAL", "INTEGER", "HEX_CONSTANT",
  "DOLLAR_HEX_CONSTANT", "RAWCODE", "REAL", "$accept", "program",
  "typeDeclarationBlock", "type", "qualifier", "qualifiers",
  "qualifiers_opt", "global", "local", "member", "assignTail",
  "multDivExpression", "simpleArithmeticExpression",
  "boolComparisonExpression", "boolEqualityExpression",
  "boolAndsExpression", "boolExpression", "baseExpression",
  "negatableExpression", "expression", "functionExpression",
  "methodExpression", "argsList", "setPart", "callPart", "statement",
  "ifStatementPartial", "param", "paramList", "requirement",
  "requirementList", "requirementList_opt", "globals", "globals_opt",
  "globalsBlock", "nativeBlock", "functionBlock", "methodBlock",
  "implementModuleStatement", "defaultsTail", "interfaceMethodBlock",
  "libraryBlock", "scopeBlock", "extends_opt", "structDeclarationBlock",
  "$@1", "interfaceDeclarationBlock", "$@2", "moduleDeclarationBlock",
  "$@3", "nonLibraryBlock", "block", "blocks", "nonLibraryBlocks",
  "statements", "statements_opt", "blocks_opt", "nonLibraryBlocks_opt",
  "structStatement", "interfaceStatement", "structStatements",
  "interfaceStatements", "structStatements_opt", "interfaceStatements_opt",
  "newlines", "newlines_opt", null
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
    /** Token LIBRARY, to be returned by the scanner.  */
    static final int LIBRARY = 316;
    /** Token LIBRARY_ONCE, to be returned by the scanner.  */
    static final int LIBRARY_ONCE = 317;
    /** Token ENDLIBRARY, to be returned by the scanner.  */
    static final int ENDLIBRARY = 318;
    /** Token SCOPE, to be returned by the scanner.  */
    static final int SCOPE = 319;
    /** Token ENDSCOPE, to be returned by the scanner.  */
    static final int ENDSCOPE = 320;
    /** Token INTERFACE, to be returned by the scanner.  */
    static final int INTERFACE = 321;
    /** Token ENDINTERFACE, to be returned by the scanner.  */
    static final int ENDINTERFACE = 322;
    /** Token REQUIRES, to be returned by the scanner.  */
    static final int REQUIRES = 323;
    /** Token OPTIONAL, to be returned by the scanner.  */
    static final int OPTIONAL = 324;
    /** Token PRIVATE, to be returned by the scanner.  */
    static final int PRIVATE = 325;
    /** Token PUBLIC, to be returned by the scanner.  */
    static final int PUBLIC = 326;
    /** Token READONLY, to be returned by the scanner.  */
    static final int READONLY = 327;
    /** Token OPERATOR, to be returned by the scanner.  */
    static final int OPERATOR = 328;
    /** Token IMPLEMENT, to be returned by the scanner.  */
    static final int IMPLEMENT = 329;
    /** Token MODULE, to be returned by the scanner.  */
    static final int MODULE = 330;
    /** Token ENDMODULE, to be returned by the scanner.  */
    static final int ENDMODULE = 331;
    /** Token INITIALIZER, to be returned by the scanner.  */
    static final int INITIALIZER = 332;
    /** Token DEFAULTS, to be returned by the scanner.  */
    static final int DEFAULTS = 333;
    /** Token ID, to be returned by the scanner.  */
    static final int ID = 334;
    /** Token STRING_LITERAL, to be returned by the scanner.  */
    static final int STRING_LITERAL = 335;
    /** Token INTEGER, to be returned by the scanner.  */
    static final int INTEGER = 336;
    /** Token HEX_CONSTANT, to be returned by the scanner.  */
    static final int HEX_CONSTANT = 337;
    /** Token DOLLAR_HEX_CONSTANT, to be returned by the scanner.  */
    static final int DOLLAR_HEX_CONSTANT = 338;
    /** Token RAWCODE, to be returned by the scanner.  */
    static final int RAWCODE = 339;
    /** Token REAL, to be returned by the scanner.  */
    static final int REAL = 340;

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

/* "SmashJassParser.java":743  */

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
          case 2: /* program: blocks_opt  */
  if (yyn == 2)
    /* "SmashJassParser.y":115  */
        {
		jassProgram.addAll(((LinkedList<JassDefinitionBlock>)(yystack.valueAt (0))));
	};
  break;


  case 3: /* typeDeclarationBlock: TYPE ID EXTENDS ID  */
  if (yyn == 3)
    /* "SmashJassParser.y":122  */
        {
		yyval = new JassTypeDefinitionBlock(((String)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 4: /* type: ID  */
  if (yyn == 4)
    /* "SmashJassParser.y":129  */
        {
		yyval = new PrimitiveJassTypeToken(((String)(yystack.valueAt (0))));
	};
  break;


  case 5: /* type: ID ARRAY  */
  if (yyn == 5)
    /* "SmashJassParser.y":134  */
        {
		yyval = new ArrayJassTypeToken(((String)(yystack.valueAt (1))));
	};
  break;


  case 6: /* type: NOTHING  */
  if (yyn == 6)
    /* "SmashJassParser.y":139  */
        {
		yyval = NothingJassTypeToken.INSTANCE;
	};
  break;


  case 7: /* qualifier: PUBLIC  */
  if (yyn == 7)
    /* "SmashJassParser.y":146  */
        {
		yyval = JassQualifier.PUBLIC;
	};
  break;


  case 8: /* qualifier: PRIVATE  */
  if (yyn == 8)
    /* "SmashJassParser.y":151  */
        {
		yyval = JassQualifier.PRIVATE;
	};
  break;


  case 9: /* qualifier: STATIC  */
  if (yyn == 9)
    /* "SmashJassParser.y":156  */
        {
		yyval = JassQualifier.STATIC;
	};
  break;


  case 10: /* qualifier: CONSTANT  */
  if (yyn == 10)
    /* "SmashJassParser.y":161  */
        {
		yyval = JassQualifier.CONSTANT;
	};
  break;


  case 11: /* qualifier: READONLY  */
  if (yyn == 11)
    /* "SmashJassParser.y":166  */
        {
		yyval = JassQualifier.READONLY;
	};
  break;


  case 12: /* qualifiers: qualifier  */
  if (yyn == 12)
    /* "SmashJassParser.y":173  */
        {
		yyval = EnumSet.of(((JassQualifier)(yystack.valueAt (0))));
	};
  break;


  case 13: /* qualifiers: qualifier qualifiers  */
  if (yyn == 13)
    /* "SmashJassParser.y":178  */
        {
		EnumSet<JassQualifier> set = ((EnumSet<JassQualifier>)(yystack.valueAt (0)));
		set.add(((JassQualifier)(yystack.valueAt (1))));
		yyval = set;
	};
  break;


  case 14: /* qualifiers_opt: qualifiers  */
  if (yyn == 14)
    /* "SmashJassParser.y":187  */
        {
		yyval = ((EnumSet<JassQualifier>)(yystack.valueAt (0)));
	};
  break;


  case 15: /* qualifiers_opt: %empty  */
  if (yyn == 15)
    /* "SmashJassParser.y":191  */
        {
		yyval = EnumSet.noneOf(JassQualifier.class);
	};
  break;


  case 16: /* global: qualifiers_opt type ID  */
  if (yyn == 16)
    /* "SmashJassParser.y":198  */
        {
		yyval = new JassGlobalStatement(((EnumSet<JassQualifier>)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 17: /* global: qualifiers_opt type ID assignTail  */
  if (yyn == 17)
    /* "SmashJassParser.y":203  */
        {
		yyval = new JassGlobalDefinitionStatement(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 18: /* local: LOCAL type ID  */
  if (yyn == 18)
    /* "SmashJassParser.y":209  */
        {
		yyval = new JassLocalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 19: /* local: LOCAL type ID assignTail  */
  if (yyn == 19)
    /* "SmashJassParser.y":214  */
        {
		yyval = new JassLocalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 20: /* local: type ID  */
  if (yyn == 20)
    /* "SmashJassParser.y":219  */
        {
		yyval = new JassLocalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 21: /* local: type ID assignTail  */
  if (yyn == 21)
    /* "SmashJassParser.y":224  */
        {
		yyval = new JassLocalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 22: /* member: qualifiers_opt type ID  */
  if (yyn == 22)
    /* "SmashJassParser.y":231  */
        {
		yyval = new JassStructMemberTypeDefinition(((EnumSet<JassQualifier>)(yystack.valueAt (2))), ((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))), null);
	};
  break;


  case 23: /* member: qualifiers_opt type ID assignTail  */
  if (yyn == 23)
    /* "SmashJassParser.y":236  */
        {
		yyval = new JassStructMemberTypeDefinition(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((JassTypeToken)(yystack.valueAt (2))), ((String)(yystack.valueAt (1))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 24: /* assignTail: EQUALS expression  */
  if (yyn == 24)
    /* "SmashJassParser.y":243  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 25: /* multDivExpression: multDivExpression TIMES negatableExpression  */
  if (yyn == 25)
    /* "SmashJassParser.y":249  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.MULTIPLY);
	};
  break;


  case 26: /* multDivExpression: multDivExpression DIVIDE negatableExpression  */
  if (yyn == 26)
    /* "SmashJassParser.y":254  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.DIVIDE);
	};
  break;


  case 28: /* simpleArithmeticExpression: simpleArithmeticExpression PLUS multDivExpression  */
  if (yyn == 28)
    /* "SmashJassParser.y":263  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.ADD);
	};
  break;


  case 29: /* simpleArithmeticExpression: simpleArithmeticExpression MINUS multDivExpression  */
  if (yyn == 29)
    /* "SmashJassParser.y":268  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.SUBTRACT);
	};
  break;


  case 31: /* boolComparisonExpression: boolComparisonExpression LESS simpleArithmeticExpression  */
  if (yyn == 31)
    /* "SmashJassParser.y":277  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS);
	};
  break;


  case 32: /* boolComparisonExpression: boolComparisonExpression GREATER simpleArithmeticExpression  */
  if (yyn == 32)
    /* "SmashJassParser.y":282  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER);
	};
  break;


  case 33: /* boolComparisonExpression: boolComparisonExpression LESS_EQUALS simpleArithmeticExpression  */
  if (yyn == 33)
    /* "SmashJassParser.y":287  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS_OR_EQUALS);
	};
  break;


  case 34: /* boolComparisonExpression: boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression  */
  if (yyn == 34)
    /* "SmashJassParser.y":292  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER_OR_EQUALS);
	};
  break;


  case 36: /* boolEqualityExpression: boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression  */
  if (yyn == 36)
    /* "SmashJassParser.y":301  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.EQUALS);
	};
  break;


  case 37: /* boolEqualityExpression: boolEqualityExpression NOT_EQUALS boolComparisonExpression  */
  if (yyn == 37)
    /* "SmashJassParser.y":306  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.NOT_EQUALS);
	};
  break;


  case 39: /* boolAndsExpression: boolAndsExpression AND boolEqualityExpression  */
  if (yyn == 39)
    /* "SmashJassParser.y":315  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.AND);
	};
  break;


  case 41: /* boolExpression: boolExpression OR boolAndsExpression  */
  if (yyn == 41)
    /* "SmashJassParser.y":324  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.OR);
	};
  break;


  case 43: /* baseExpression: ID  */
  if (yyn == 43)
    /* "SmashJassParser.y":333  */
        {
		yyval = new ReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 44: /* baseExpression: STRING_LITERAL  */
  if (yyn == 44)
    /* "SmashJassParser.y":338  */
        {
		yyval = new LiteralJassExpression(StringJassValue.of(((String)(yystack.valueAt (0)))));
	};
  break;


  case 45: /* baseExpression: INTEGER  */
  if (yyn == 45)
    /* "SmashJassParser.y":343  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 46: /* baseExpression: HEX_CONSTANT  */
  if (yyn == 46)
    /* "SmashJassParser.y":348  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 47: /* baseExpression: DOLLAR_HEX_CONSTANT  */
  if (yyn == 47)
    /* "SmashJassParser.y":353  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 48: /* baseExpression: RAWCODE  */
  if (yyn == 48)
    /* "SmashJassParser.y":358  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 49: /* baseExpression: REAL  */
  if (yyn == 49)
    /* "SmashJassParser.y":363  */
        {
		yyval = new LiteralJassExpression(RealJassValue.of(((double)(yystack.valueAt (0)))));
	};
  break;


  case 50: /* baseExpression: FUNCTION ID  */
  if (yyn == 50)
    /* "SmashJassParser.y":368  */
        {
		yyval = new FunctionReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 51: /* baseExpression: baseExpression DOT ID  */
  if (yyn == 51)
    /* "SmashJassParser.y":373  */
        {
		yyval = new MemberJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 52: /* baseExpression: NULL  */
  if (yyn == 52)
    /* "SmashJassParser.y":378  */
        {
		yyval = new LiteralJassExpression(null);
	};
  break;


  case 53: /* baseExpression: TRUE  */
  if (yyn == 53)
    /* "SmashJassParser.y":383  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.TRUE);
	};
  break;


  case 54: /* baseExpression: FALSE  */
  if (yyn == 54)
    /* "SmashJassParser.y":388  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.FALSE);
	};
  break;


  case 55: /* baseExpression: ID OPEN_BRACKET expression CLOSE_BRACKET  */
  if (yyn == 55)
    /* "SmashJassParser.y":393  */
        {
		yyval = new ArrayRefJassExpression(((String)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (1))));
	};
  break;


  case 56: /* baseExpression: functionExpression  */
  if (yyn == 56)
    /* "SmashJassParser.y":398  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
		// Handled by functionExpression, no need to extra code in a call expr
	};
  break;


  case 57: /* baseExpression: methodExpression  */
  if (yyn == 57)
    /* "SmashJassParser.y":404  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 58: /* baseExpression: OPEN_PAREN expression CLOSE_PAREN  */
  if (yyn == 58)
    /* "SmashJassParser.y":409  */
        {
		yyval = ((JassExpression)(yystack.valueAt (1)));
		// handled by expression
	};
  break;


  case 59: /* negatableExpression: baseExpression  */
  if (yyn == 59)
    /* "SmashJassParser.y":417  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 60: /* negatableExpression: NOT baseExpression  */
  if (yyn == 60)
    /* "SmashJassParser.y":422  */
        {
		yyval = new NotJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 61: /* negatableExpression: MINUS baseExpression  */
  if (yyn == 61)
    /* "SmashJassParser.y":427  */
        {
		yyval = new NegateJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 62: /* negatableExpression: METHOD baseExpression DOT ID  */
  if (yyn == 62)
    /* "SmashJassParser.y":432  */
        {
		yyval = new MethodReferenceJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 63: /* expression: boolExpression  */
  if (yyn == 63)
    /* "SmashJassParser.y":439  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 64: /* functionExpression: ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 64)
    /* "SmashJassParser.y":445  */
        {
		yyval = new FunctionCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 65: /* methodExpression: baseExpression DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 65)
    /* "SmashJassParser.y":452  */
        {
		yyval = new MethodCallJassExpression(((JassExpression)(yystack.valueAt (5))), ((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 66: /* methodExpression: DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 66)
    /* "SmashJassParser.y":457  */
        {
		yyval = new ParentlessMethodCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 67: /* argsList: expression  */
  if (yyn == 67)
    /* "SmashJassParser.y":464  */
        {
		LinkedList<JassExpression> list = new LinkedList<JassExpression>();
		list.addFirst(((JassExpression)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 68: /* argsList: expression COMMA argsList  */
  if (yyn == 68)
    /* "SmashJassParser.y":471  */
        {
		LinkedList<JassExpression> list = ((LinkedList<JassExpression>)(yystack.valueAt (0)));
		list.addFirst(((JassExpression)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 69: /* argsList: %empty  */
  if (yyn == 69)
    /* "SmashJassParser.y":478  */
        {
		yyval = new LinkedList<JassExpression>();
	};
  break;


  case 70: /* setPart: ID EQUALS expression  */
  if (yyn == 70)
    /* "SmashJassParser.y":489  */
        {
		yyval = new JassSetStatement(((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 71: /* setPart: ID OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression  */
  if (yyn == 71)
    /* "SmashJassParser.y":494  */
        {
		yyval = new JassArrayedAssignmentStatement(((String)(yystack.valueAt (5))), ((JassExpression)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 72: /* setPart: baseExpression DOT ID EQUALS expression  */
  if (yyn == 72)
    /* "SmashJassParser.y":499  */
        {
		yyval = new JassSetMemberStatement(((JassExpression)(yystack.valueAt (4))), ((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 73: /* callPart: functionExpression  */
  if (yyn == 73)
    /* "SmashJassParser.y":506  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 74: /* callPart: methodExpression  */
  if (yyn == 74)
    /* "SmashJassParser.y":511  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 75: /* statement: CALL callPart  */
  if (yyn == 75)
    /* "SmashJassParser.y":518  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 76: /* statement: callPart  */
  if (yyn == 76)
    /* "SmashJassParser.y":523  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 77: /* statement: SET setPart  */
  if (yyn == 77)
    /* "SmashJassParser.y":528  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 78: /* statement: setPart  */
  if (yyn == 78)
    /* "SmashJassParser.y":533  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 79: /* statement: RETURN expression  */
  if (yyn == 79)
    /* "SmashJassParser.y":538  */
        {
		yyval = new JassReturnStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 80: /* statement: RETURN  */
  if (yyn == 80)
    /* "SmashJassParser.y":543  */
        {
		yyval = new JassReturnNothingStatement();
	};
  break;


  case 81: /* statement: EXITWHEN expression  */
  if (yyn == 81)
    /* "SmashJassParser.y":548  */
        {
		yyval = new JassExitWhenStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 82: /* statement: local  */
  if (yyn == 82)
    /* "SmashJassParser.y":553  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 83: /* statement: LOOP statements_opt ENDLOOP  */
  if (yyn == 83)
    /* "SmashJassParser.y":558  */
        {
		yyval = new JassLoopStatement(((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 84: /* statement: IF ifStatementPartial  */
  if (yyn == 84)
    /* "SmashJassParser.y":563  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 85: /* statement: DEBUG statement  */
  if (yyn == 85)
    /* "SmashJassParser.y":568  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 86: /* ifStatementPartial: expression THEN statements_opt ENDIF  */
  if (yyn == 86)
    /* "SmashJassParser.y":576  */
        {
		yyval = new JassIfStatement(((JassExpression)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 87: /* ifStatementPartial: expression THEN statements_opt ELSE statements_opt ENDIF  */
  if (yyn == 87)
    /* "SmashJassParser.y":581  */
        {
		yyval = new JassIfElseStatement(((JassExpression)(yystack.valueAt (5))), ((LinkedList<JassStatement>)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 88: /* ifStatementPartial: expression THEN statements_opt ELSEIF ifStatementPartial  */
  if (yyn == 88)
    /* "SmashJassParser.y":586  */
        {
		yyval = new JassIfElseIfStatement(((JassExpression)(yystack.valueAt (4))), ((LinkedList<JassStatement>)(yystack.valueAt (2))), ((JassStatement)(yystack.valueAt (0))));
	};
  break;


  case 89: /* param: type ID  */
  if (yyn == 89)
    /* "SmashJassParser.y":593  */
        {
		yyval = new JassParameterDefinition(((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 90: /* paramList: param  */
  if (yyn == 90)
    /* "SmashJassParser.y":600  */
        {
		LinkedList<JassParameterDefinition> list = new LinkedList<JassParameterDefinition>();
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 91: /* paramList: param COMMA paramList  */
  if (yyn == 91)
    /* "SmashJassParser.y":607  */
        {
		LinkedList<JassParameterDefinition> list = ((LinkedList<JassParameterDefinition>)(yystack.valueAt (0)));
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 92: /* paramList: NOTHING  */
  if (yyn == 92)
    /* "SmashJassParser.y":614  */
        {
		yyval = new LinkedList<JassParameterDefinition>();
	};
  break;


  case 93: /* requirement: ID  */
  if (yyn == 93)
    /* "SmashJassParser.y":621  */
        {
		yyval = new JassLibraryRequirementDefinition(((String)(yystack.valueAt (0))), false);
	};
  break;


  case 94: /* requirement: OPTIONAL ID  */
  if (yyn == 94)
    /* "SmashJassParser.y":626  */
        {
		yyval = new JassLibraryRequirementDefinition(((String)(yystack.valueAt (0))), true);
	};
  break;


  case 95: /* requirementList: requirement  */
  if (yyn == 95)
    /* "SmashJassParser.y":633  */
        {
		LinkedList<JassLibraryRequirementDefinition> list = new LinkedList<>();
		list.addFirst(((JassLibraryRequirementDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 96: /* requirementList: requirementList COMMA requirement  */
  if (yyn == 96)
    /* "SmashJassParser.y":640  */
        {
		LinkedList<JassLibraryRequirementDefinition> list = ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2)));
		list.addLast(((JassLibraryRequirementDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 97: /* requirementList_opt: REQUIRES requirementList  */
  if (yyn == 97)
    /* "SmashJassParser.y":649  */
        {
		yyval = ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (0)));
	};
  break;


  case 98: /* requirementList_opt: %empty  */
  if (yyn == 98)
    /* "SmashJassParser.y":653  */
        {
		yyval = new LinkedList<JassLibraryRequirementDefinition>(); // maybe use Collections.emptyList later
	};
  break;


  case 99: /* globals: global  */
  if (yyn == 99)
    /* "SmashJassParser.y":660  */
        {
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		list.addFirst(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 100: /* globals: globals newlines global  */
  if (yyn == 100)
    /* "SmashJassParser.y":667  */
        {
		LinkedList<JassStatement> list = ((LinkedList<JassStatement>)(yystack.valueAt (2)));
		list.addLast(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 101: /* globals_opt: newlines globals newlines  */
  if (yyn == 101)
    /* "SmashJassParser.y":676  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 102: /* globals_opt: newlines  */
  if (yyn == 102)
    /* "SmashJassParser.y":681  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 103: /* globalsBlock: GLOBALS globals_opt ENDGLOBALS  */
  if (yyn == 103)
    /* "SmashJassParser.y":688  */
        {
		yyval = new JassGlobalsDefinitionBlock(getLine(), currentParsingFilePath, ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 104: /* nativeBlock: qualifiers_opt NATIVE ID TAKES paramList RETURNS type  */
  if (yyn == 104)
    /* "SmashJassParser.y":694  */
        {
		final String text = ((String)(yystack.valueAt (4)));
		yyval = new JassNativeDefinitionBlock(getLine(), currentParsingFilePath, text, ((LinkedList<JassParameterDefinition>)(yystack.valueAt (2))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 105: /* functionBlock: qualifiers_opt FUNCTION ID TAKES paramList RETURNS type statements_opt ENDFUNCTION  */
  if (yyn == 105)
    /* "SmashJassParser.y":702  */
        {
		yyval = new JassFunctionDefinitionBlock(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (8))), ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))));
	};
  break;


  case 106: /* methodBlock: qualifiers_opt METHOD ID TAKES paramList RETURNS type statements_opt ENDMETHOD  */
  if (yyn == 106)
    /* "SmashJassParser.y":709  */
        {
		yyval = new JassMethodDefinitionBlock(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (8))), ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))));
	};
  break;


  case 107: /* implementModuleStatement: IMPLEMENT ID  */
  if (yyn == 107)
    /* "SmashJassParser.y":716  */
        {
		yyval = new JassImplementModuleDefinition(((String)(yystack.valueAt (0))), false);
	};
  break;


  case 108: /* implementModuleStatement: IMPLEMENT OPTIONAL ID  */
  if (yyn == 108)
    /* "SmashJassParser.y":721  */
        {
		yyval = new JassImplementModuleDefinition(((String)(yystack.valueAt (0))), true);
	};
  break;


  case 109: /* defaultsTail: DEFAULTS expression  */
  if (yyn == 109)
    /* "SmashJassParser.y":728  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 110: /* defaultsTail: DEFAULTS NOTHING  */
  if (yyn == 110)
    /* "SmashJassParser.y":733  */
        {
		yyval = new LiteralJassExpression(null);
	};
  break;


  case 111: /* defaultsTail: %empty  */
  if (yyn == 111)
    /* "SmashJassParser.y":737  */
        {
		yyval = null;
	};
  break;


  case 112: /* interfaceMethodBlock: qualifiers_opt METHOD ID TAKES paramList RETURNS type defaultsTail  */
  if (yyn == 112)
    /* "SmashJassParser.y":744  */
        {
		yyval = JassMethodDefinitionBlock.createInterfaceMethod(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (7))), ((String)(yystack.valueAt (5))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (3))), ((JassTypeToken)(yystack.valueAt (1))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 113: /* libraryBlock: LIBRARY ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 113)
    /* "SmashJassParser.y":751  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (3))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null, true);
	};
  break;


  case 114: /* libraryBlock: LIBRARY ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 114)
    /* "SmashJassParser.y":756  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (5))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (3))), true);
	};
  break;


  case 115: /* libraryBlock: LIBRARY_ONCE ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 115)
    /* "SmashJassParser.y":761  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (3))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null, true);
	};
  break;


  case 116: /* libraryBlock: LIBRARY_ONCE ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 116)
    /* "SmashJassParser.y":766  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (5))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (3))), true);
	};
  break;


  case 117: /* scopeBlock: SCOPE ID nonLibraryBlocks_opt ENDSCOPE  */
  if (yyn == 117)
    /* "SmashJassParser.y":773  */
        {
		yyval = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null);
	};
  break;


  case 118: /* scopeBlock: SCOPE ID INITIALIZER ID nonLibraryBlocks_opt ENDSCOPE  */
  if (yyn == 118)
    /* "SmashJassParser.y":778  */
        {
		yyval = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (4))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (2))));
	};
  break;


  case 119: /* extends_opt: EXTENDS type  */
  if (yyn == 119)
    /* "SmashJassParser.y":785  */
        {
		yyval = ((JassTypeToken)(yystack.valueAt (0)));
	};
  break;


  case 120: /* extends_opt: %empty  */
  if (yyn == 120)
    /* "SmashJassParser.y":789  */
        {
		yyval = NothingJassTypeToken.INSTANCE;
	};
  break;


  case 121: /* $@1: %empty  */
  if (yyn == 121)
    /* "SmashJassParser.y":796  */
        {
		currentStruct = new JassStructDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 122: /* structDeclarationBlock: qualifiers_opt STRUCT ID extends_opt $@1 structStatements_opt ENDSTRUCT  */
  if (yyn == 122)
    /* "SmashJassParser.y":800  */
        {
		yyval = currentStruct;
	};
  break;


  case 123: /* $@2: %empty  */
  if (yyn == 123)
    /* "SmashJassParser.y":807  */
        {
		currentStruct = new JassStructDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 124: /* interfaceDeclarationBlock: qualifiers_opt INTERFACE ID extends_opt $@2 interfaceStatements_opt ENDINTERFACE  */
  if (yyn == 124)
    /* "SmashJassParser.y":811  */
        {
		yyval = currentStruct;
	};
  break;


  case 125: /* $@3: %empty  */
  if (yyn == 125)
    /* "SmashJassParser.y":818  */
        {
		currentStruct = new JassModuleDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 126: /* moduleDeclarationBlock: qualifiers_opt MODULE ID $@3 structStatements_opt ENDMODULE  */
  if (yyn == 126)
    /* "SmashJassParser.y":822  */
        {
		yyval = currentStruct;
	};
  break;


  case 127: /* nonLibraryBlock: globalsBlock  */
  if (yyn == 127)
    /* "SmashJassParser.y":829  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 128: /* nonLibraryBlock: nativeBlock  */
  if (yyn == 128)
    /* "SmashJassParser.y":834  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 129: /* nonLibraryBlock: functionBlock  */
  if (yyn == 129)
    /* "SmashJassParser.y":839  */
        {
		yyval = ((JassFunctionDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 130: /* nonLibraryBlock: typeDeclarationBlock  */
  if (yyn == 130)
    /* "SmashJassParser.y":844  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 131: /* nonLibraryBlock: structDeclarationBlock  */
  if (yyn == 131)
    /* "SmashJassParser.y":849  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 132: /* nonLibraryBlock: interfaceDeclarationBlock  */
  if (yyn == 132)
    /* "SmashJassParser.y":854  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 133: /* nonLibraryBlock: moduleDeclarationBlock  */
  if (yyn == 133)
    /* "SmashJassParser.y":859  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 134: /* nonLibraryBlock: scopeBlock  */
  if (yyn == 134)
    /* "SmashJassParser.y":864  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 135: /* block: nonLibraryBlock  */
  if (yyn == 135)
    /* "SmashJassParser.y":871  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 136: /* block: libraryBlock  */
  if (yyn == 136)
    /* "SmashJassParser.y":876  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 137: /* blocks: block  */
  if (yyn == 137)
    /* "SmashJassParser.y":883  */
        {
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 138: /* blocks: blocks newlines block  */
  if (yyn == 138)
    /* "SmashJassParser.y":890  */
        {
		LinkedList<JassDefinitionBlock> list = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (2)));
		list.addLast(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 139: /* nonLibraryBlocks: nonLibraryBlock  */
  if (yyn == 139)
    /* "SmashJassParser.y":899  */
        {
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 140: /* nonLibraryBlocks: nonLibraryBlocks newlines nonLibraryBlock  */
  if (yyn == 140)
    /* "SmashJassParser.y":906  */
        {
		LinkedList<JassDefinitionBlock> list = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (2)));
		list.addLast(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 141: /* statements: statement  */
  if (yyn == 141)
    /* "SmashJassParser.y":915  */
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


  case 142: /* statements: statements newlines statement  */
  if (yyn == 142)
    /* "SmashJassParser.y":928  */
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


  case 143: /* statements_opt: newlines statements newlines  */
  if (yyn == 143)
    /* "SmashJassParser.y":943  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 144: /* statements_opt: newlines  */
  if (yyn == 144)
    /* "SmashJassParser.y":948  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 145: /* blocks_opt: newlines_opt blocks newlines_opt  */
  if (yyn == 145)
    /* "SmashJassParser.y":955  */
        {
		yyval = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1)));
	};
  break;


  case 146: /* blocks_opt: newlines_opt  */
  if (yyn == 146)
    /* "SmashJassParser.y":960  */
        {
		yyval = new LinkedList<JassDefinitionBlock>();
	};
  break;


  case 147: /* nonLibraryBlocks_opt: newlines nonLibraryBlocks newlines  */
  if (yyn == 147)
    /* "SmashJassParser.y":967  */
        {
		yyval = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1)));
	};
  break;


  case 148: /* nonLibraryBlocks_opt: newlines  */
  if (yyn == 148)
    /* "SmashJassParser.y":972  */
        {
		yyval = new LinkedList<JassDefinitionBlock>();
	};
  break;


  case 149: /* structStatement: member  */
  if (yyn == 149)
    /* "SmashJassParser.y":979  */
        {
		currentStruct.add(((JassStructMemberTypeDefinition)(yystack.valueAt (0))));
	};
  break;


  case 150: /* structStatement: methodBlock  */
  if (yyn == 150)
    /* "SmashJassParser.y":984  */
        {
		currentStruct.add(((JassMethodDefinitionBlock)(yystack.valueAt (0))));
	};
  break;


  case 151: /* structStatement: implementModuleStatement  */
  if (yyn == 151)
    /* "SmashJassParser.y":989  */
        {
		currentStruct.add(((JassImplementModuleDefinition)(yystack.valueAt (0))));
	};
  break;


  case 152: /* interfaceStatement: member  */
  if (yyn == 152)
    /* "SmashJassParser.y":996  */
        {
		currentStruct.add(((JassStructMemberTypeDefinition)(yystack.valueAt (0))));
	};
  break;


  case 153: /* interfaceStatement: interfaceMethodBlock  */
  if (yyn == 153)
    /* "SmashJassParser.y":1001  */
        {
		currentStruct.add(((JassMethodDefinitionBlock)(yystack.valueAt (0))));
	};
  break;



/* "SmashJassParser.java":2287  */

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

  private static final short yypact_ninf_ = -218;
  private static final short yytable_ninf_ = -165;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
     -12,   -12,    40,  -218,  -218,    41,  -218,  -218,   -12,    -4,
    -218,  -218,     4,    17,    29,  -218,  -218,  -218,  -218,   260,
    -218,     1,  -218,  -218,  -218,  -218,  -218,  -218,  -218,  -218,
    -218,  -218,   -12,    70,   108,    97,   -44,   -30,   -23,  -218,
      49,    51,    55,    61,    67,    77,  -218,  -218,    16,  -218,
     -12,    75,   -49,    92,   -12,   106,   -12,   124,   111,   102,
     194,   209,   202,   202,  -218,  -218,  -218,   205,   147,   142,
    -218,   152,  -218,  -218,   178,   165,   171,   165,   175,   -12,
    -218,  -218,   -12,    20,    20,    16,  -218,  -218,   -12,  -218,
     233,  -218,  -218,   -49,   -12,  -218,   -12,  -218,   174,   275,
     228,   187,   200,   255,   257,  -218,   -12,   -12,   195,   315,
     242,  -218,  -218,   210,   216,  -218,  -218,  -218,    20,    16,
      16,   224,   215,   309,  -218,   -43,     7,  -218,  -218,  -218,
    -218,   -12,   213,  -218,  -218,  -218,   166,   166,   242,   166,
     217,   -27,  -218,  -218,  -218,  -218,  -218,  -218,     2,    90,
      98,   133,   246,   252,   238,  -218,  -218,  -218,  -218,  -218,
    -218,  -218,  -218,   -12,  -218,  -218,    13,  -218,  -218,  -218,
     -12,   219,  -218,   221,   225,   322,  -218,   238,   238,   250,
     247,   253,   242,   242,   242,   242,   242,   242,   242,   242,
     242,   242,   242,   242,   242,   242,   239,   293,   176,   240,
     330,  -218,   316,   233,  -218,  -218,   254,   242,   278,   283,
     290,  -218,  -218,     2,     2,    90,    90,    90,    90,    98,
      98,   133,   246,   292,  -218,   166,   282,   242,   242,    16,
     -12,   242,   176,    10,   269,  -218,   291,   312,   313,  -218,
    -218,  -218,   -12,   341,  -218,    20,  -218,    -7,   301,  -218,
     242,  -218,   242,   238,  -218,    69,  -218,  -218,   332,  -218,
     277,   328,  -218,  -218,   242,   242,   233,   280,   176,    20,
     347,  -218,  -218,   317,   -12,   233,  -218,  -218,   321,  -218,
      11,  -218,   355,    16,  -218,   191,  -218,   370,   242,    16,
     -12,   -12,  -218,   242,   242,  -218,   296,   319,   358,  -218,
    -218,   229,  -218,  -218,  -218,  -218,  -218
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final short[] yydefact_ = yydefact_init();
  private static final short[] yydefact_init()
  {
    return new short[]
    {
     165,   162,     0,     2,   164,    15,   163,     1,     0,     0,
      10,     9,     0,     0,     0,     8,     7,    11,   130,    12,
      14,     0,   127,   128,   129,   136,   134,   131,   132,   133,
     135,   137,   165,     0,    15,     0,    98,    98,     0,    13,
       0,     0,     0,     0,     0,    15,   145,   103,     0,    99,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    15,
       0,     0,   120,   120,   125,   138,     6,     4,     0,    15,
       3,     0,    93,    95,    97,    98,     0,    98,     0,     0,
     117,   139,     0,     0,     0,     0,   121,   123,     0,     5,
      16,   100,    94,     0,     0,   113,     0,   115,     0,    15,
       6,     0,    90,     0,     0,   119,     0,     0,     0,    15,
       0,    17,    96,     0,     0,   118,   140,    89,     0,     0,
       0,     0,     0,    15,   126,     0,     0,   149,   150,   151,
     154,     0,     0,    52,    53,    54,     0,     0,     0,     0,
       0,    43,    44,    45,    46,    47,    48,    49,    30,    35,
      38,    40,    42,    63,    59,    27,    24,    56,    57,   114,
     116,    91,   104,     0,   122,   124,     0,   152,   153,   156,
       0,     0,   107,     0,     0,    15,    50,    60,    61,     0,
       0,     0,     0,    69,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   144,     0,
      15,   108,     0,    22,   155,    58,     0,    69,     0,    67,
       0,    25,    26,    28,    29,    31,    32,    33,    34,    36,
      37,    39,    41,    51,   105,     0,     0,    80,     0,     0,
       0,     0,     0,     4,     0,    82,     0,    56,    57,    78,
      76,   141,     0,     0,   157,     0,    23,    62,     0,    55,
      69,    64,    69,     0,    75,    43,    77,    79,     0,    84,
       0,     0,    81,    85,     0,     0,    20,     0,   143,     0,
       0,    66,    68,     0,     0,    18,    83,    70,     0,    21,
      51,   142,     0,     0,    65,     0,    19,    55,     0,     0,
       0,     0,    86,     0,     0,    72,   111,     0,     0,    88,
      71,     0,   112,   106,    87,   110,   109
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -218,  -218,  -218,   -46,  -218,   365,   -25,   326,  -218,  -113,
    -185,  -128,   -32,     3,   211,   193,  -218,  -132,    35,  -104,
    -179,  -177,  -136,   177,   179,  -217,   113,  -218,   -81,   306,
    -218,    78,  -218,  -218,  -218,  -218,  -218,  -218,  -218,  -218,
    -218,  -218,  -218,   344,  -218,  -218,  -218,  -218,  -218,  -218,
     -42,   363,  -218,  -218,  -218,  -165,  -218,   173,   234,   212,
    -218,  -218,   304,  -218,     0,   379
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     2,    18,   101,    19,    20,    21,    49,   235,   127,
     111,   148,   149,   150,   151,   152,   153,   154,   155,   209,
     157,   158,   210,   239,   240,   241,   259,   102,   103,    73,
      74,    54,    50,    33,    22,    23,    24,   128,   129,   302,
     168,    25,    26,    86,    27,   106,    28,   107,    29,    88,
      30,    31,    32,    82,   242,   197,     3,    58,   130,   169,
     131,   170,   108,   122,    59,     5
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
       4,     6,    68,   104,   177,   178,   156,   180,    34,    48,
     167,    40,    41,   264,   288,   263,     1,    81,   246,   237,
      71,   238,    66,   182,    52,   183,   171,     1,    66,    89,
      72,    66,    45,    53,   179,   100,   172,   161,    52,   105,
       7,  -146,   184,   185,    48,   252,   237,    55,   238,     8,
      69,   281,   -51,   237,    57,   238,    42,   116,   213,   214,
     265,     9,   183,   252,   173,   261,   236,    43,    10,   -43,
     199,   248,   264,   162,   163,    35,    44,  -164,   208,    47,
     174,   279,    99,    36,   126,     8,    67,   167,   109,   237,
     286,   238,    67,   253,   236,    67,    37,     9,   166,    67,
     236,    11,    12,    13,    10,    14,   109,   123,    38,   285,
       8,    15,    16,    17,   272,    56,   273,  -102,    51,   265,
     174,   183,     9,   257,   258,   297,   298,   262,    60,    10,
      61,   175,   186,   187,    62,    10,   236,    11,    12,    13,
      63,    14,   188,   189,   190,   191,    64,    15,    16,    17,
     126,  -101,   234,    94,    70,    96,   215,   216,   217,   218,
     277,   278,    11,   198,   270,  -148,    14,  -148,    11,    10,
     200,    75,    15,    16,    17,   166,    80,   132,    15,    16,
      17,   192,   193,   260,   295,    77,   234,   132,   282,   258,
     300,    66,   225,   226,   227,   219,   220,   306,   228,   133,
     134,   135,    11,    79,   229,   230,    83,   231,   232,   133,
     134,   135,    15,    16,    17,   291,   292,   293,   138,   211,
     212,    84,   234,    85,    89,   140,    90,    76,   138,    78,
     198,    92,    93,    52,    95,   140,   110,   290,    97,   115,
     132,   -92,   268,   296,   305,   141,   142,   143,   144,   145,
     146,   147,    98,   132,   118,   233,   142,   143,   144,   145,
     146,   147,   133,   134,   135,   136,   117,   113,   119,   114,
     120,   124,   137,   159,   198,   133,   134,   135,   136,   160,
     164,   138,   165,     8,   194,   137,   139,    10,   140,   195,
     198,   198,   176,   132,   138,     9,   181,   196,   201,   139,
     202,   140,    10,   205,   203,   207,   206,   224,   141,   142,
     143,   144,   145,   146,   147,   133,   134,   135,   223,   243,
      11,   141,   142,   143,   144,   145,   146,   147,   245,   249,
      15,    16,    17,   247,   138,    11,    10,   250,  -147,    14,
    -147,   140,    10,   251,   252,    15,    16,    17,   266,    10,
     267,   -73,   -74,   269,   271,   274,   275,    10,   276,   280,
     283,   255,   142,   143,   144,   145,   146,   147,   289,    11,
     284,  -159,   287,   294,   301,    11,  -161,   303,  -158,    15,
      16,    17,    11,   304,    39,    15,    16,    17,   222,   125,
      11,  -159,    15,    16,    17,    91,   125,  -160,  -158,   112,
      15,    16,    17,   256,   254,   221,   299,    87,    65,   204,
     121,    46,   244
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
       0,     1,    48,    84,   136,   137,   110,   139,     8,    34,
     123,    10,    11,     3,     3,   232,    39,    59,   203,   198,
      69,   198,    15,    50,    68,    52,    69,    39,    15,    19,
      79,    15,    32,    77,   138,    15,    79,   118,    68,    85,
       0,     0,    40,    41,    69,    52,   225,    77,   225,     8,
      50,   268,    59,   232,    77,   232,    55,    99,   186,   187,
      50,    20,    52,    52,    57,   230,   198,    66,    27,    59,
      57,   207,     3,   119,   120,    79,    75,     0,   182,     9,
     126,   266,    82,    79,   109,     8,    79,   200,    88,   268,
     275,   268,    79,   225,   226,    79,    79,    20,   123,    79,
     232,    60,    61,    62,    27,    64,   106,   107,    79,   274,
       8,    70,    71,    72,   250,    37,   252,     9,    21,    50,
     166,    52,    20,   227,   228,   290,   291,   231,    79,    27,
      79,   131,    42,    43,    79,    27,   268,    60,    61,    62,
      79,    64,    44,    45,    46,    47,    79,    70,    71,    72,
     175,     9,   198,    75,    79,    77,   188,   189,   190,   191,
     264,   265,    60,   163,   245,    63,    64,    65,    60,    27,
     170,    79,    70,    71,    72,   200,    65,    11,    70,    71,
      72,    48,    49,   229,   288,    79,   232,    11,   269,   293,
     294,    15,    16,    17,    18,   192,   193,   301,    22,    33,
      34,    35,    60,    79,    28,    29,    12,    31,    32,    33,
      34,    35,    70,    71,    72,    24,    25,    26,    52,   184,
     185,    12,   268,    21,    19,    59,    79,    54,    52,    56,
     230,    79,    54,    68,    63,    59,     3,   283,    63,    65,
      11,    13,   242,   289,    15,    79,    80,    81,    82,    83,
      84,    85,    79,    11,    54,    79,    80,    81,    82,    83,
      84,    85,    33,    34,    35,    36,    79,    94,    13,    96,
      13,    76,    43,    63,   274,    33,    34,    35,    36,    63,
      56,    52,    67,     8,    38,    43,    57,    27,    59,    37,
     290,   291,    79,    11,    52,    20,    79,    59,    79,    57,
      79,    59,    27,    53,    79,    52,    59,    14,    79,    80,
      81,    82,    83,    84,    85,    33,    34,    35,    79,    79,
      60,    79,    80,    81,    82,    83,    84,    85,    12,    51,
      70,    71,    72,    79,    52,    60,    27,    54,    63,    64,
      65,    59,    27,    53,    52,    70,    71,    72,    79,    27,
      59,    39,    39,    12,    53,    23,    79,    27,    30,    79,
      13,    79,    80,    81,    82,    83,    84,    85,    13,    60,
      53,    56,    51,     3,    78,    60,    67,    58,    56,    70,
      71,    72,    60,    25,    19,    70,    71,    72,   195,    74,
      60,    76,    70,    71,    72,    69,    74,    67,    76,    93,
      70,    71,    72,   226,   225,   194,   293,    63,    45,   175,
     106,    32,   200
    };
  }

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
  private static final short[] yystos_ = yystos_init();
  private static final short[] yystos_init()
  {
    return new short[]
    {
       0,    39,    87,   142,   150,   151,   150,     0,     8,    20,
      27,    60,    61,    62,    64,    70,    71,    72,    88,    90,
      91,    92,   120,   121,   122,   127,   128,   130,   132,   134,
     136,   137,   138,   119,   150,    79,    79,    79,    79,    91,
      10,    11,    55,    66,    75,   150,   151,     9,    92,    93,
     118,    21,    68,    77,   117,    77,   117,    77,   143,   150,
      79,    79,    79,    79,    79,   137,    15,    79,    89,   150,
      79,    69,    79,   115,   116,    79,   143,    79,   143,    79,
      65,   136,   139,    12,    12,    21,   129,   129,   135,    19,
      79,    93,    79,    54,   117,    63,   117,    63,   143,   150,
      15,    89,   113,   114,   114,    89,   131,   133,   148,   150,
       3,    96,   115,   143,   143,    65,   136,    79,    54,    13,
      13,   148,   149,   150,    76,    74,    92,    95,   123,   124,
     144,   146,    11,    33,    34,    35,    36,    43,    52,    57,
      59,    79,    80,    81,    82,    83,    84,    85,    97,    98,
      99,   100,   101,   102,   103,   104,   105,   106,   107,    63,
      63,   114,    89,    89,    56,    67,    92,    95,   126,   145,
     147,    69,    79,    57,    89,   150,    79,   103,   103,   105,
     103,    79,    50,    52,    40,    41,    42,    43,    44,    45,
      46,    47,    48,    49,    38,    37,    59,   141,   150,    57,
     150,    79,    79,    79,   144,    53,    59,    52,   105,   105,
     108,   104,   104,    97,    97,    98,    98,    98,    98,    99,
      99,   100,   101,    79,    14,    16,    17,    18,    22,    28,
      29,    31,    32,    79,    89,    94,   103,   106,   107,   109,
     110,   111,   140,    79,   145,    12,    96,    79,   108,    51,
      54,    53,    52,   103,   110,    79,   109,   105,   105,   112,
      89,   141,   105,   111,     3,    50,    79,    59,   150,    12,
     114,    53,   108,   108,    23,    79,    30,   105,   105,    96,
      79,   111,   114,    13,    53,   141,    96,    51,     3,    13,
      89,    24,    25,    26,     3,   105,    89,   141,   141,   112,
     105,    78,   125,    58,    25,    15,   105
    };
  }

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final short[] yyr1_ = yyr1_init();
  private static final short[] yyr1_init()
  {
    return new short[]
    {
       0,    86,    87,    88,    89,    89,    89,    90,    90,    90,
      90,    90,    91,    91,    92,    92,    93,    93,    94,    94,
      94,    94,    95,    95,    96,    97,    97,    97,    98,    98,
      98,    99,    99,    99,    99,    99,   100,   100,   100,   101,
     101,   102,   102,   103,   103,   103,   103,   103,   103,   103,
     103,   103,   103,   103,   103,   103,   103,   103,   103,   104,
     104,   104,   104,   105,   106,   107,   107,   108,   108,   108,
     109,   109,   109,   110,   110,   111,   111,   111,   111,   111,
     111,   111,   111,   111,   111,   111,   112,   112,   112,   113,
     114,   114,   114,   115,   115,   116,   116,   117,   117,   118,
     118,   119,   119,   120,   121,   122,   123,   124,   124,   125,
     125,   125,   126,   127,   127,   127,   127,   128,   128,   129,
     129,   131,   130,   133,   132,   135,   134,   136,   136,   136,
     136,   136,   136,   136,   136,   137,   137,   138,   138,   139,
     139,   140,   140,   141,   141,   142,   142,   143,   143,   144,
     144,   144,   145,   145,   146,   146,   147,   147,   148,   148,
     149,   149,   150,   150,   151,   151
    };
  }

/* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     4,     1,     2,     1,     1,     1,     1,
       1,     1,     1,     2,     1,     0,     3,     4,     3,     4,
       2,     3,     3,     4,     2,     3,     3,     1,     3,     3,
       1,     3,     3,     3,     3,     1,     3,     3,     1,     3,
       1,     3,     1,     1,     1,     1,     1,     1,     1,     1,
       2,     3,     1,     1,     1,     4,     1,     1,     3,     1,
       2,     2,     4,     1,     4,     6,     5,     1,     3,     0,
       3,     6,     5,     1,     1,     2,     1,     2,     1,     2,
       1,     2,     1,     3,     2,     2,     4,     6,     5,     2,
       1,     3,     1,     1,     2,     1,     3,     2,     0,     1,
       3,     3,     1,     3,     7,     9,     9,     2,     3,     2,
       2,     0,     8,     5,     7,     5,     7,     4,     6,     2,
       0,     0,     7,     0,     7,     0,     6,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     3,     1,
       3,     1,     3,     3,     1,     3,     1,     3,     1,     1,
       1,     1,     1,     1,     1,     3,     1,     3,     3,     1,
       3,     1,     1,     2,     1,     0
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 340;
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
      65,    66,    67,    68,    69,    70,    71,    72,    73,    74,
      75,    76,    77,    78,    79,    80,    81,    82,    83,    84,
      85
    };
  }


  private static final int YYLAST_ = 412;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 7;
  private static final int YYNTOKENS_ = 86;

/* Unqualified %code blocks.  */
/* "SmashJassParser.y":36  */


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
	

/* "SmashJassParser.java":3044  */

}
/* "SmashJassParser.y":1042  */

