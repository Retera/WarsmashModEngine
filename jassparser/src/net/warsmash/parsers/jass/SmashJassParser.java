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


  case 20: /* local: LOCAL type STRUCT  */
  if (yyn == 20)
    /* "SmashJassParser.y":219  */
        {
		yyval = new JassLocalStatement("struct", ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 21: /* local: LOCAL type STRUCT assignTail  */
  if (yyn == 21)
    /* "SmashJassParser.y":224  */
        {
		yyval = new JassLocalDefinitionStatement("struct", ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 22: /* local: type ID  */
  if (yyn == 22)
    /* "SmashJassParser.y":229  */
        {
		yyval = new JassLocalStatement(((String)(yystack.valueAt (0))), ((JassTypeToken)(yystack.valueAt (1))));
	};
  break;


  case 23: /* local: type ID assignTail  */
  if (yyn == 23)
    /* "SmashJassParser.y":234  */
        {
		yyval = new JassLocalDefinitionStatement(((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 24: /* member: qualifiers_opt type ID  */
  if (yyn == 24)
    /* "SmashJassParser.y":241  */
        {
		yyval = new JassStructMemberTypeDefinition(((EnumSet<JassQualifier>)(yystack.valueAt (2))), ((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))), null);
	};
  break;


  case 25: /* member: qualifiers_opt type ID assignTail  */
  if (yyn == 25)
    /* "SmashJassParser.y":246  */
        {
		yyval = new JassStructMemberTypeDefinition(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((JassTypeToken)(yystack.valueAt (2))), ((String)(yystack.valueAt (1))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 26: /* assignTail: EQUALS expression  */
  if (yyn == 26)
    /* "SmashJassParser.y":253  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 27: /* multDivExpression: multDivExpression TIMES negatableExpression  */
  if (yyn == 27)
    /* "SmashJassParser.y":259  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.MULTIPLY);
	};
  break;


  case 28: /* multDivExpression: multDivExpression DIVIDE negatableExpression  */
  if (yyn == 28)
    /* "SmashJassParser.y":264  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.DIVIDE);
	};
  break;


  case 30: /* simpleArithmeticExpression: simpleArithmeticExpression PLUS multDivExpression  */
  if (yyn == 30)
    /* "SmashJassParser.y":273  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.ADD);
	};
  break;


  case 31: /* simpleArithmeticExpression: simpleArithmeticExpression MINUS multDivExpression  */
  if (yyn == 31)
    /* "SmashJassParser.y":278  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.SUBTRACT);
	};
  break;


  case 33: /* boolComparisonExpression: boolComparisonExpression LESS simpleArithmeticExpression  */
  if (yyn == 33)
    /* "SmashJassParser.y":287  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS);
	};
  break;


  case 34: /* boolComparisonExpression: boolComparisonExpression GREATER simpleArithmeticExpression  */
  if (yyn == 34)
    /* "SmashJassParser.y":292  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER);
	};
  break;


  case 35: /* boolComparisonExpression: boolComparisonExpression LESS_EQUALS simpleArithmeticExpression  */
  if (yyn == 35)
    /* "SmashJassParser.y":297  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.LESS_OR_EQUALS);
	};
  break;


  case 36: /* boolComparisonExpression: boolComparisonExpression GREATER_EQUALS simpleArithmeticExpression  */
  if (yyn == 36)
    /* "SmashJassParser.y":302  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.GREATER_OR_EQUALS);
	};
  break;


  case 38: /* boolEqualityExpression: boolEqualityExpression DOUBLE_EQUALS boolComparisonExpression  */
  if (yyn == 38)
    /* "SmashJassParser.y":311  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.EQUALS);
	};
  break;


  case 39: /* boolEqualityExpression: boolEqualityExpression NOT_EQUALS boolComparisonExpression  */
  if (yyn == 39)
    /* "SmashJassParser.y":316  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.NOT_EQUALS);
	};
  break;


  case 41: /* boolAndsExpression: boolAndsExpression AND boolEqualityExpression  */
  if (yyn == 41)
    /* "SmashJassParser.y":325  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.AND);
	};
  break;


  case 43: /* boolExpression: boolExpression OR boolAndsExpression  */
  if (yyn == 43)
    /* "SmashJassParser.y":334  */
        {
		yyval = new ArithmeticJassExpression(((JassExpression)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))), ArithmeticSigns.OR);
	};
  break;


  case 45: /* baseExpression: ID  */
  if (yyn == 45)
    /* "SmashJassParser.y":343  */
        {
		yyval = new ReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 46: /* baseExpression: STRUCT  */
  if (yyn == 46)
    /* "SmashJassParser.y":348  */
        {
		yyval = new ReferenceJassExpression("struct");
	};
  break;


  case 47: /* baseExpression: STRING_LITERAL  */
  if (yyn == 47)
    /* "SmashJassParser.y":353  */
        {
		yyval = new LiteralJassExpression(StringJassValue.of(((String)(yystack.valueAt (0)))));
	};
  break;


  case 48: /* baseExpression: INTEGER  */
  if (yyn == 48)
    /* "SmashJassParser.y":358  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 49: /* baseExpression: HEX_CONSTANT  */
  if (yyn == 49)
    /* "SmashJassParser.y":363  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 50: /* baseExpression: DOLLAR_HEX_CONSTANT  */
  if (yyn == 50)
    /* "SmashJassParser.y":368  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 51: /* baseExpression: RAWCODE  */
  if (yyn == 51)
    /* "SmashJassParser.y":373  */
        {
		yyval = new LiteralJassExpression(IntegerJassValue.of(((int)(yystack.valueAt (0)))));
	};
  break;


  case 52: /* baseExpression: REAL  */
  if (yyn == 52)
    /* "SmashJassParser.y":378  */
        {
		yyval = new LiteralJassExpression(RealJassValue.of(((double)(yystack.valueAt (0)))));
	};
  break;


  case 53: /* baseExpression: FUNCTION ID  */
  if (yyn == 53)
    /* "SmashJassParser.y":383  */
        {
		yyval = new FunctionReferenceJassExpression(((String)(yystack.valueAt (0))));
	};
  break;


  case 54: /* baseExpression: baseExpression DOT ID  */
  if (yyn == 54)
    /* "SmashJassParser.y":388  */
        {
		yyval = new MemberJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 55: /* baseExpression: NULL  */
  if (yyn == 55)
    /* "SmashJassParser.y":393  */
        {
		yyval = new LiteralJassExpression(null);
	};
  break;


  case 56: /* baseExpression: TRUE  */
  if (yyn == 56)
    /* "SmashJassParser.y":398  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.TRUE);
	};
  break;


  case 57: /* baseExpression: FALSE  */
  if (yyn == 57)
    /* "SmashJassParser.y":403  */
        {
		yyval = new LiteralJassExpression(BooleanJassValue.FALSE);
	};
  break;


  case 58: /* baseExpression: baseExpression OPEN_BRACKET expression CLOSE_BRACKET  */
  if (yyn == 58)
    /* "SmashJassParser.y":408  */
        {
		yyval = new ArrayRefJassExpression(((JassExpression)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (1))));
	};
  break;


  case 59: /* baseExpression: functionExpression  */
  if (yyn == 59)
    /* "SmashJassParser.y":413  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
		// Handled by functionExpression, no need to extra code in a call expr
	};
  break;


  case 60: /* baseExpression: methodExpression  */
  if (yyn == 60)
    /* "SmashJassParser.y":419  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 61: /* baseExpression: OPEN_PAREN expression CLOSE_PAREN  */
  if (yyn == 61)
    /* "SmashJassParser.y":424  */
        {
		yyval = ((JassExpression)(yystack.valueAt (1)));
		// handled by expression
	};
  break;


  case 62: /* negatableExpression: baseExpression  */
  if (yyn == 62)
    /* "SmashJassParser.y":432  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 63: /* negatableExpression: NOT negatableExpression  */
  if (yyn == 63)
    /* "SmashJassParser.y":437  */
        {
		yyval = new NotJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 64: /* negatableExpression: MINUS negatableExpression  */
  if (yyn == 64)
    /* "SmashJassParser.y":442  */
        {
		yyval = new NegateJassExpression(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 65: /* negatableExpression: METHOD baseExpression DOT ID  */
  if (yyn == 65)
    /* "SmashJassParser.y":447  */
        {
		yyval = new MethodReferenceJassExpression(((JassExpression)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 66: /* expression: boolExpression  */
  if (yyn == 66)
    /* "SmashJassParser.y":454  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 67: /* functionExpression: ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 67)
    /* "SmashJassParser.y":460  */
        {
		yyval = new FunctionCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 68: /* methodExpression: baseExpression DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 68)
    /* "SmashJassParser.y":467  */
        {
		yyval = new MethodCallJassExpression(((JassExpression)(yystack.valueAt (5))), ((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 69: /* methodExpression: DOT ID OPEN_PAREN argsList CLOSE_PAREN  */
  if (yyn == 69)
    /* "SmashJassParser.y":472  */
        {
		yyval = new ParentlessMethodCallJassExpression(((String)(yystack.valueAt (3))), ((LinkedList<JassExpression>)(yystack.valueAt (1))));
	};
  break;


  case 70: /* argsList: expression  */
  if (yyn == 70)
    /* "SmashJassParser.y":479  */
        {
		LinkedList<JassExpression> list = new LinkedList<JassExpression>();
		list.addFirst(((JassExpression)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 71: /* argsList: expression COMMA argsList  */
  if (yyn == 71)
    /* "SmashJassParser.y":486  */
        {
		LinkedList<JassExpression> list = ((LinkedList<JassExpression>)(yystack.valueAt (0)));
		list.addFirst(((JassExpression)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 72: /* argsList: %empty  */
  if (yyn == 72)
    /* "SmashJassParser.y":493  */
        {
		yyval = new LinkedList<JassExpression>();
	};
  break;


  case 73: /* setPart: ID EQUALS expression  */
  if (yyn == 73)
    /* "SmashJassParser.y":504  */
        {
		yyval = new JassSetStatement(((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 74: /* setPart: STRUCT EQUALS expression  */
  if (yyn == 74)
    /* "SmashJassParser.y":509  */
        {
		yyval = new JassSetStatement("struct", ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 75: /* setPart: baseExpression OPEN_BRACKET expression CLOSE_BRACKET EQUALS expression  */
  if (yyn == 75)
    /* "SmashJassParser.y":514  */
        {
		yyval = new JassArrayedAssignmentStatement(((JassExpression)(yystack.valueAt (5))), ((JassExpression)(yystack.valueAt (3))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 76: /* setPart: baseExpression DOT ID EQUALS expression  */
  if (yyn == 76)
    /* "SmashJassParser.y":519  */
        {
		yyval = new JassSetMemberStatement(((JassExpression)(yystack.valueAt (4))), ((String)(yystack.valueAt (2))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 77: /* callPart: functionExpression  */
  if (yyn == 77)
    /* "SmashJassParser.y":526  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 78: /* callPart: methodExpression  */
  if (yyn == 78)
    /* "SmashJassParser.y":531  */
        {
		yyval = new JassCallExpressionStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 79: /* statement: CALL callPart  */
  if (yyn == 79)
    /* "SmashJassParser.y":538  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 80: /* statement: callPart  */
  if (yyn == 80)
    /* "SmashJassParser.y":543  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 81: /* statement: SET setPart  */
  if (yyn == 81)
    /* "SmashJassParser.y":548  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 82: /* statement: setPart  */
  if (yyn == 82)
    /* "SmashJassParser.y":553  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 83: /* statement: ID PLUSPLUS  */
  if (yyn == 83)
    /* "SmashJassParser.y":558  */
        {
		yyval = new JassSetStatement(((String)(yystack.valueAt (1))), new ArithmeticJassExpression(new ReferenceJassExpression(((String)(yystack.valueAt (1)))), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.ADD));
	};
  break;


  case 84: /* statement: baseExpression DOT ID PLUSPLUS  */
  if (yyn == 84)
    /* "SmashJassParser.y":563  */
        {
		yyval = new JassSetMemberStatement(((JassExpression)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), new ArithmeticJassExpression(new MemberJassExpression(((JassExpression)(yystack.valueAt (3))), ((String)(yystack.valueAt (1)))), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.ADD));
	};
  break;


  case 85: /* statement: ID MINUSMINUS  */
  if (yyn == 85)
    /* "SmashJassParser.y":568  */
        {
		yyval = new JassSetStatement(((String)(yystack.valueAt (1))), new ArithmeticJassExpression(new ReferenceJassExpression(((String)(yystack.valueAt (1)))), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.SUBTRACT));
	};
  break;


  case 86: /* statement: baseExpression DOT ID MINUSMINUS  */
  if (yyn == 86)
    /* "SmashJassParser.y":573  */
        {
		yyval = new JassSetMemberStatement(((JassExpression)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), new ArithmeticJassExpression(new MemberJassExpression(((JassExpression)(yystack.valueAt (3))), ((String)(yystack.valueAt (1)))), new LiteralJassExpression(IntegerJassValue.of(1)), ArithmeticSigns.SUBTRACT));
	};
  break;


  case 87: /* statement: RETURN expression  */
  if (yyn == 87)
    /* "SmashJassParser.y":578  */
        {
		yyval = new JassReturnStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 88: /* statement: RETURN  */
  if (yyn == 88)
    /* "SmashJassParser.y":583  */
        {
		yyval = new JassReturnNothingStatement();
	};
  break;


  case 89: /* statement: EXITWHEN expression  */
  if (yyn == 89)
    /* "SmashJassParser.y":588  */
        {
		yyval = new JassExitWhenStatement(((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 90: /* statement: local  */
  if (yyn == 90)
    /* "SmashJassParser.y":593  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 91: /* statement: LOOP statements_opt ENDLOOP  */
  if (yyn == 91)
    /* "SmashJassParser.y":598  */
        {
		yyval = new JassLoopStatement(((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 92: /* statement: IF ifStatementPartial  */
  if (yyn == 92)
    /* "SmashJassParser.y":603  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 93: /* statement: DEBUG statement  */
  if (yyn == 93)
    /* "SmashJassParser.y":608  */
        {
		yyval = ((JassStatement)(yystack.valueAt (0)));
	};
  break;


  case 94: /* ifStatementPartial: expression THEN statements_opt ENDIF  */
  if (yyn == 94)
    /* "SmashJassParser.y":616  */
        {
		yyval = new JassIfStatement(((JassExpression)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 95: /* ifStatementPartial: expression THEN statements_opt ELSE statements_opt ENDIF  */
  if (yyn == 95)
    /* "SmashJassParser.y":621  */
        {
		yyval = new JassIfElseStatement(((JassExpression)(yystack.valueAt (5))), ((LinkedList<JassStatement>)(yystack.valueAt (3))), ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 96: /* ifStatementPartial: expression THEN statements_opt ELSEIF ifStatementPartial  */
  if (yyn == 96)
    /* "SmashJassParser.y":626  */
        {
		yyval = new JassIfElseIfStatement(((JassExpression)(yystack.valueAt (4))), ((LinkedList<JassStatement>)(yystack.valueAt (2))), ((JassStatement)(yystack.valueAt (0))));
	};
  break;


  case 97: /* param: type ID  */
  if (yyn == 97)
    /* "SmashJassParser.y":633  */
        {
		yyval = new JassParameterDefinition(((JassTypeToken)(yystack.valueAt (1))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 98: /* paramList: param  */
  if (yyn == 98)
    /* "SmashJassParser.y":640  */
        {
		LinkedList<JassParameterDefinition> list = new LinkedList<JassParameterDefinition>();
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 99: /* paramList: param COMMA paramList  */
  if (yyn == 99)
    /* "SmashJassParser.y":647  */
        {
		LinkedList<JassParameterDefinition> list = ((LinkedList<JassParameterDefinition>)(yystack.valueAt (0)));
		list.addFirst(((JassParameterDefinition)(yystack.valueAt (2))));
		yyval = list;
	};
  break;


  case 100: /* paramList: NOTHING  */
  if (yyn == 100)
    /* "SmashJassParser.y":654  */
        {
		yyval = new LinkedList<JassParameterDefinition>();
	};
  break;


  case 101: /* requirement: ID  */
  if (yyn == 101)
    /* "SmashJassParser.y":661  */
        {
		yyval = new JassLibraryRequirementDefinition(((String)(yystack.valueAt (0))), false);
	};
  break;


  case 102: /* requirement: OPTIONAL ID  */
  if (yyn == 102)
    /* "SmashJassParser.y":666  */
        {
		yyval = new JassLibraryRequirementDefinition(((String)(yystack.valueAt (0))), true);
	};
  break;


  case 103: /* requirementList: requirement  */
  if (yyn == 103)
    /* "SmashJassParser.y":673  */
        {
		LinkedList<JassLibraryRequirementDefinition> list = new LinkedList<>();
		list.addFirst(((JassLibraryRequirementDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 104: /* requirementList: requirementList COMMA requirement  */
  if (yyn == 104)
    /* "SmashJassParser.y":680  */
        {
		LinkedList<JassLibraryRequirementDefinition> list = ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2)));
		list.addLast(((JassLibraryRequirementDefinition)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 105: /* requirementList_opt: REQUIRES requirementList  */
  if (yyn == 105)
    /* "SmashJassParser.y":689  */
        {
		yyval = ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (0)));
	};
  break;


  case 106: /* requirementList_opt: %empty  */
  if (yyn == 106)
    /* "SmashJassParser.y":693  */
        {
		yyval = new LinkedList<JassLibraryRequirementDefinition>(); // maybe use Collections.emptyList later
	};
  break;


  case 107: /* globals: global  */
  if (yyn == 107)
    /* "SmashJassParser.y":700  */
        {
		LinkedList<JassStatement> list = new LinkedList<JassStatement>();
		list.addFirst(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 108: /* globals: globals newlines global  */
  if (yyn == 108)
    /* "SmashJassParser.y":707  */
        {
		LinkedList<JassStatement> list = ((LinkedList<JassStatement>)(yystack.valueAt (2)));
		list.addLast(((JassStatement)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 109: /* globals_opt: newlines globals newlines  */
  if (yyn == 109)
    /* "SmashJassParser.y":716  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 110: /* globals_opt: newlines  */
  if (yyn == 110)
    /* "SmashJassParser.y":721  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 111: /* globalsBlock: GLOBALS globals_opt ENDGLOBALS  */
  if (yyn == 111)
    /* "SmashJassParser.y":728  */
        {
		yyval = new JassGlobalsDefinitionBlock(getLine(), currentParsingFilePath, ((LinkedList<JassStatement>)(yystack.valueAt (1))));
	};
  break;


  case 112: /* nativeBlock: qualifiers_opt NATIVE ID TAKES paramList RETURNS type  */
  if (yyn == 112)
    /* "SmashJassParser.y":734  */
        {
		final String text = ((String)(yystack.valueAt (4)));
		yyval = new JassNativeDefinitionBlock(getLine(), currentParsingFilePath, text, ((LinkedList<JassParameterDefinition>)(yystack.valueAt (2))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 113: /* functionBlock: qualifiers_opt FUNCTION ID TAKES paramList RETURNS type statements_opt ENDFUNCTION  */
  if (yyn == 113)
    /* "SmashJassParser.y":742  */
        {
		yyval = new JassFunctionDefinitionBlock(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (8))), ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))));
	};
  break;


  case 114: /* methodBlock: qualifiers_opt METHOD ID TAKES paramList RETURNS type statements_opt ENDMETHOD  */
  if (yyn == 114)
    /* "SmashJassParser.y":749  */
        {
		yyval = new JassMethodDefinitionBlock(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (8))), ((String)(yystack.valueAt (6))), ((LinkedList<JassStatement>)(yystack.valueAt (1))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (4))), ((JassTypeToken)(yystack.valueAt (2))));
	};
  break;


  case 115: /* implementModuleStatement: IMPLEMENT ID  */
  if (yyn == 115)
    /* "SmashJassParser.y":756  */
        {
		yyval = new JassImplementModuleDefinition(((String)(yystack.valueAt (0))), false);
	};
  break;


  case 116: /* implementModuleStatement: IMPLEMENT OPTIONAL ID  */
  if (yyn == 116)
    /* "SmashJassParser.y":761  */
        {
		yyval = new JassImplementModuleDefinition(((String)(yystack.valueAt (0))), true);
	};
  break;


  case 117: /* defaultsTail: DEFAULTS expression  */
  if (yyn == 117)
    /* "SmashJassParser.y":768  */
        {
		yyval = ((JassExpression)(yystack.valueAt (0)));
	};
  break;


  case 118: /* defaultsTail: DEFAULTS NOTHING  */
  if (yyn == 118)
    /* "SmashJassParser.y":773  */
        {
		yyval = new LiteralJassExpression(null);
	};
  break;


  case 119: /* defaultsTail: %empty  */
  if (yyn == 119)
    /* "SmashJassParser.y":777  */
        {
		yyval = null;
	};
  break;


  case 120: /* interfaceMethodBlock: qualifiers_opt METHOD ID TAKES paramList RETURNS type defaultsTail  */
  if (yyn == 120)
    /* "SmashJassParser.y":784  */
        {
		yyval = JassMethodDefinitionBlock.createInterfaceMethod(getLine(), currentParsingFilePath, ((EnumSet<JassQualifier>)(yystack.valueAt (7))), ((String)(yystack.valueAt (5))), ((LinkedList<JassParameterDefinition>)(yystack.valueAt (3))), ((JassTypeToken)(yystack.valueAt (1))), ((JassExpression)(yystack.valueAt (0))));
	};
  break;


  case 121: /* libraryBlock: LIBRARY ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 121)
    /* "SmashJassParser.y":791  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (3))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null, true);
	};
  break;


  case 122: /* libraryBlock: LIBRARY ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 122)
    /* "SmashJassParser.y":796  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (5))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (3))), true);
	};
  break;


  case 123: /* libraryBlock: LIBRARY_ONCE ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 123)
    /* "SmashJassParser.y":801  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (3))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null, true);
	};
  break;


  case 124: /* libraryBlock: LIBRARY_ONCE ID INITIALIZER ID requirementList_opt nonLibraryBlocks_opt ENDLIBRARY  */
  if (yyn == 124)
    /* "SmashJassParser.y":806  */
        {
		yyval = new JassLibraryDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (5))), ((LinkedList<JassLibraryRequirementDefinition>)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (3))), true);
	};
  break;


  case 125: /* scopeBlock: SCOPE ID nonLibraryBlocks_opt ENDSCOPE  */
  if (yyn == 125)
    /* "SmashJassParser.y":813  */
        {
		yyval = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (2))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), null);
	};
  break;


  case 126: /* scopeBlock: SCOPE ID INITIALIZER ID nonLibraryBlocks_opt ENDSCOPE  */
  if (yyn == 126)
    /* "SmashJassParser.y":818  */
        {
		yyval = new JassScopeDefinitionBlock(getLine(), currentParsingFilePath, ((String)(yystack.valueAt (4))), ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1))), ((String)(yystack.valueAt (2))));
	};
  break;


  case 127: /* extends_opt: EXTENDS type  */
  if (yyn == 127)
    /* "SmashJassParser.y":825  */
        {
		yyval = ((JassTypeToken)(yystack.valueAt (0)));
	};
  break;


  case 128: /* extends_opt: %empty  */
  if (yyn == 128)
    /* "SmashJassParser.y":829  */
        {
		yyval = NothingJassTypeToken.INSTANCE;
	};
  break;


  case 129: /* $@1: %empty  */
  if (yyn == 129)
    /* "SmashJassParser.y":836  */
        {
		currentStruct = new JassStructDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 130: /* structDeclarationBlock: qualifiers_opt STRUCT ID extends_opt $@1 structStatements_opt ENDSTRUCT  */
  if (yyn == 130)
    /* "SmashJassParser.y":840  */
        {
		yyval = currentStruct;
	};
  break;


  case 131: /* $@2: %empty  */
  if (yyn == 131)
    /* "SmashJassParser.y":847  */
        {
		currentStruct = new JassStructDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (3))), ((String)(yystack.valueAt (1))), ((JassTypeToken)(yystack.valueAt (0))));
	};
  break;


  case 132: /* interfaceDeclarationBlock: qualifiers_opt INTERFACE ID extends_opt $@2 interfaceStatements_opt ENDINTERFACE  */
  if (yyn == 132)
    /* "SmashJassParser.y":851  */
        {
		yyval = currentStruct;
	};
  break;


  case 133: /* $@3: %empty  */
  if (yyn == 133)
    /* "SmashJassParser.y":858  */
        {
		currentStruct = new JassModuleDefinitionBlock(((EnumSet<JassQualifier>)(yystack.valueAt (2))), ((String)(yystack.valueAt (0))));
	};
  break;


  case 134: /* moduleDeclarationBlock: qualifiers_opt MODULE ID $@3 structStatements_opt ENDMODULE  */
  if (yyn == 134)
    /* "SmashJassParser.y":862  */
        {
		yyval = currentStruct;
	};
  break;


  case 135: /* nonLibraryBlock: globalsBlock  */
  if (yyn == 135)
    /* "SmashJassParser.y":869  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 136: /* nonLibraryBlock: nativeBlock  */
  if (yyn == 136)
    /* "SmashJassParser.y":874  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 137: /* nonLibraryBlock: functionBlock  */
  if (yyn == 137)
    /* "SmashJassParser.y":879  */
        {
		yyval = ((JassFunctionDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 138: /* nonLibraryBlock: typeDeclarationBlock  */
  if (yyn == 138)
    /* "SmashJassParser.y":884  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 139: /* nonLibraryBlock: structDeclarationBlock  */
  if (yyn == 139)
    /* "SmashJassParser.y":889  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 140: /* nonLibraryBlock: interfaceDeclarationBlock  */
  if (yyn == 140)
    /* "SmashJassParser.y":894  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 141: /* nonLibraryBlock: moduleDeclarationBlock  */
  if (yyn == 141)
    /* "SmashJassParser.y":899  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 142: /* nonLibraryBlock: scopeBlock  */
  if (yyn == 142)
    /* "SmashJassParser.y":904  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 143: /* block: nonLibraryBlock  */
  if (yyn == 143)
    /* "SmashJassParser.y":911  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 144: /* block: libraryBlock  */
  if (yyn == 144)
    /* "SmashJassParser.y":916  */
        {
		yyval = ((JassDefinitionBlock)(yystack.valueAt (0)));
	};
  break;


  case 145: /* blocks: block  */
  if (yyn == 145)
    /* "SmashJassParser.y":923  */
        {
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 146: /* blocks: blocks newlines block  */
  if (yyn == 146)
    /* "SmashJassParser.y":930  */
        {
		LinkedList<JassDefinitionBlock> list = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (2)));
		list.addLast(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 147: /* nonLibraryBlocks: nonLibraryBlock  */
  if (yyn == 147)
    /* "SmashJassParser.y":939  */
        {
		LinkedList<JassDefinitionBlock> list = new LinkedList<>();
		list.addFirst(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 148: /* nonLibraryBlocks: nonLibraryBlocks newlines nonLibraryBlock  */
  if (yyn == 148)
    /* "SmashJassParser.y":946  */
        {
		LinkedList<JassDefinitionBlock> list = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (2)));
		list.addLast(((JassDefinitionBlock)(yystack.valueAt (0))));
		yyval = list;
	};
  break;


  case 149: /* statements: statement  */
  if (yyn == 149)
    /* "SmashJassParser.y":955  */
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


  case 150: /* statements: statements newlines statement  */
  if (yyn == 150)
    /* "SmashJassParser.y":968  */
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


  case 151: /* statements_opt: newlines statements newlines  */
  if (yyn == 151)
    /* "SmashJassParser.y":983  */
        {
		yyval = ((LinkedList<JassStatement>)(yystack.valueAt (1)));
	};
  break;


  case 152: /* statements_opt: newlines  */
  if (yyn == 152)
    /* "SmashJassParser.y":988  */
        {
		yyval = new LinkedList<JassStatement>();
	};
  break;


  case 153: /* blocks_opt: newlines_opt blocks newlines_opt  */
  if (yyn == 153)
    /* "SmashJassParser.y":995  */
        {
		yyval = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1)));
	};
  break;


  case 154: /* blocks_opt: newlines_opt  */
  if (yyn == 154)
    /* "SmashJassParser.y":1000  */
        {
		yyval = new LinkedList<JassDefinitionBlock>();
	};
  break;


  case 155: /* nonLibraryBlocks_opt: newlines nonLibraryBlocks newlines  */
  if (yyn == 155)
    /* "SmashJassParser.y":1007  */
        {
		yyval = ((LinkedList<JassDefinitionBlock>)(yystack.valueAt (1)));
	};
  break;


  case 156: /* nonLibraryBlocks_opt: newlines  */
  if (yyn == 156)
    /* "SmashJassParser.y":1012  */
        {
		yyval = new LinkedList<JassDefinitionBlock>();
	};
  break;


  case 157: /* structStatement: member  */
  if (yyn == 157)
    /* "SmashJassParser.y":1019  */
        {
		currentStruct.add(((JassStructMemberTypeDefinition)(yystack.valueAt (0))));
	};
  break;


  case 158: /* structStatement: methodBlock  */
  if (yyn == 158)
    /* "SmashJassParser.y":1024  */
        {
		currentStruct.add(((JassMethodDefinitionBlock)(yystack.valueAt (0))));
	};
  break;


  case 159: /* structStatement: implementModuleStatement  */
  if (yyn == 159)
    /* "SmashJassParser.y":1029  */
        {
		currentStruct.add(((JassImplementModuleDefinition)(yystack.valueAt (0))));
	};
  break;


  case 160: /* interfaceStatement: member  */
  if (yyn == 160)
    /* "SmashJassParser.y":1036  */
        {
		currentStruct.add(((JassStructMemberTypeDefinition)(yystack.valueAt (0))));
	};
  break;


  case 161: /* interfaceStatement: interfaceMethodBlock  */
  if (yyn == 161)
    /* "SmashJassParser.y":1041  */
        {
		currentStruct.add(((JassMethodDefinitionBlock)(yystack.valueAt (0))));
	};
  break;



/* "SmashJassParser.java":2359  */

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

  private static final short yypact_ninf_ = -228;
  private static final short yytable_ninf_ = -173;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
     -16,   -16,    31,  -228,  -228,    48,  -228,  -228,   -16,    -8,
    -228,  -228,    18,    25,    57,  -228,  -228,  -228,  -228,   332,
    -228,    50,  -228,  -228,  -228,  -228,  -228,  -228,  -228,  -228,
    -228,  -228,   -16,   113,    43,   138,   -32,    60,     2,  -228,
      67,    82,    87,   109,   111,   165,  -228,  -228,     4,  -228,
     -16,   112,   -51,   114,   -16,   116,   -16,   117,   133,   335,
     195,   196,   179,   179,  -228,  -228,  -228,   192,   134,   108,
    -228,   135,  -228,  -228,   161,   156,   167,   156,   169,   -16,
    -228,  -228,   -16,    19,    19,     4,  -228,  -228,   -16,  -228,
     230,  -228,  -228,   -51,   -16,  -228,   -16,  -228,   173,   374,
     221,   160,   187,   229,   232,  -228,   -16,   -16,   172,    84,
     253,  -228,  -228,   186,   197,  -228,  -228,  -228,    19,     4,
       4,   198,   199,   191,  -228,   -42,     6,  -228,  -228,  -228,
    -228,   -16,   176,  -228,  -228,  -228,   253,   253,   253,  -228,
     295,   180,   213,  -228,  -228,  -228,  -228,  -228,  -228,   -11,
       1,   246,    -2,   236,   243,    22,  -228,  -228,  -228,  -228,
    -228,  -228,  -228,  -228,   -16,  -228,  -228,    10,  -228,  -228,
    -228,   -16,   200,  -228,   203,   204,   354,  -228,  -228,  -228,
     231,    83,   248,   253,   253,   253,   253,   253,   253,   253,
     253,   253,   253,   253,   253,   253,   253,   216,   284,   188,
     223,   381,  -228,   297,   230,  -228,  -228,   228,   253,   257,
     265,  -228,  -228,   -11,   -11,     1,     1,     1,     1,   246,
     246,    -2,   236,   268,   279,  -228,   295,   306,   253,   253,
       4,   -16,   253,   188,   317,     7,   263,  -228,    88,   305,
     307,  -228,  -228,  -228,   -16,   333,  -228,    19,  -228,     5,
     296,   253,  -228,  -228,   253,    22,  -228,    13,    89,  -228,
    -228,   325,  -228,   -44,   321,  -228,  -228,   253,   253,  -228,
    -228,   230,   253,   273,   188,    19,   340,  -228,  -228,   303,
     278,   -16,   230,   230,  -228,  -228,  -228,  -228,   309,   123,
    -228,   350,     4,  -228,    17,   157,  -228,  -228,   361,   253,
    -228,  -228,     4,   -16,   -16,  -228,   253,   253,  -228,   288,
     310,   342,  -228,  -228,   242,  -228,  -228,  -228,  -228,  -228
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
     173,   170,     0,     2,   172,    15,   171,     1,     0,     0,
      10,     9,     0,     0,     0,     8,     7,    11,   138,    12,
      14,     0,   135,   136,   137,   144,   142,   139,   140,   141,
     143,   145,   173,     0,    15,     0,   106,   106,     0,    13,
       0,     0,     0,     0,     0,    15,   153,   111,     0,   107,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    15,
       0,     0,   128,   128,   133,   146,     6,     4,     0,    15,
       3,     0,   101,   103,   105,   106,     0,   106,     0,     0,
     125,   147,     0,     0,     0,     0,   129,   131,     0,     5,
      16,   108,   102,     0,     0,   121,     0,   123,     0,    15,
       6,     0,    98,     0,     0,   127,     0,     0,     0,    15,
       0,    17,   104,     0,     0,   126,   148,    97,     0,     0,
       0,     0,     0,    15,   134,     0,     0,   157,   158,   159,
     162,     0,     0,    55,    56,    57,     0,     0,     0,    46,
       0,     0,    45,    47,    48,    49,    50,    51,    52,    32,
      37,    40,    42,    44,    66,    62,    29,    26,    59,    60,
     122,   124,    99,   112,     0,   130,   132,     0,   160,   161,
     164,     0,     0,   115,     0,     0,    15,    53,    63,    64,
       0,     0,     0,    72,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   152,
       0,    15,   116,     0,    24,   163,    61,     0,    72,    70,
       0,    27,    28,    30,    31,    33,    34,    35,    36,    38,
      39,    41,    43,     0,    54,   113,     0,     0,    88,     0,
       0,     0,     0,     0,    46,    45,     0,    90,     0,    59,
      60,    82,    80,   149,     0,     0,   165,     0,    25,    65,
       0,    72,    67,    58,    72,     0,    79,    45,     0,    81,
      87,     0,    92,     0,     0,    89,    93,     0,     0,    83,
      85,    22,     0,     0,   151,     0,     0,    69,    71,     0,
       0,     0,    20,    18,    91,    74,    73,    23,     0,    54,
     150,     0,     0,    68,    54,     0,    21,    19,    58,     0,
      84,    86,     0,     0,     0,    94,     0,     0,    76,   119,
       0,     0,    96,    75,     0,   120,   114,    95,   118,   117
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -228,  -228,  -228,   -46,  -228,   351,   -31,   300,  -228,  -114,
    -187,   -10,   125,   -41,   177,   178,  -228,  -133,   -35,  -105,
    -184,  -175,   -65,   145,   158,  -221,    77,  -228,   -78,   304,
    -228,   175,  -228,  -228,  -228,  -228,  -228,  -228,  -228,  -228,
    -228,  -228,  -228,   330,  -228,  -228,  -228,  -228,  -228,  -228,
     -37,   364,  -228,  -228,  -228,  -227,  -228,    78,   220,   210,
    -228,  -228,   311,  -228,     0,   380
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     2,    18,   101,    19,    20,    21,    49,   237,   127,
     111,   149,   150,   151,   152,   153,   154,   155,   156,   209,
     158,   159,   210,   241,   242,   243,   262,   102,   103,    73,
      74,    54,    50,    33,    22,    23,    24,   128,   129,   315,
     169,    25,    26,    86,    27,   106,    28,   107,    29,    88,
      30,    31,    32,    82,   244,   198,     3,    58,   130,   170,
     131,   171,   108,   122,    59,     5
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
       4,     6,    68,    48,   264,   157,   104,   181,    34,   168,
     268,   282,   266,   269,   270,   239,   268,   248,    71,    66,
     299,    66,    81,     1,   240,    66,    89,   172,    72,   184,
     185,     7,    45,   180,   100,   283,    52,   173,    48,   105,
     162,     1,   239,   186,   187,    53,   192,   193,  -154,   239,
      69,   240,  -110,   290,   295,   -54,     8,   254,   240,   183,
      40,    41,   116,   174,   -54,   183,   238,   200,     9,   254,
      10,    35,   196,   163,   164,    10,   310,   311,   126,    57,
     175,   197,    99,    67,   287,    67,    -4,   168,   109,    67,
     239,   223,   167,   255,   258,   296,   297,    36,    67,   240,
     238,   178,   179,    11,    37,    42,   109,   123,    11,    12,
      13,    10,    14,    15,    16,    17,    43,  -109,    15,    16,
      17,   175,    47,   260,   261,    44,   299,   265,    52,   300,
     301,   176,    76,   196,    78,    10,    38,    55,   272,   272,
    -167,   238,   207,   250,    11,   126,    60,   273,   280,   211,
     212,   219,   220,   236,    15,    16,    17,    98,   125,    51,
    -167,    61,   285,   286,   199,  -172,    62,   288,    11,   276,
     167,   201,   113,     8,   114,   254,   213,   214,    15,    16,
      17,   304,   305,   306,   263,     9,   278,   236,    63,   279,
      64,    70,    10,    75,   308,    77,    79,   291,    80,   132,
      85,   261,   313,    66,   226,   227,   228,    83,    84,   319,
     229,    89,    56,    90,    92,    93,   230,   231,    10,   232,
     233,   133,   134,   135,    52,    11,    12,    13,   236,    14,
      95,   199,    97,   110,  -100,    15,    16,    17,   115,   117,
     138,   118,   119,   234,   274,   120,   303,   141,   124,   160,
      94,    11,    96,   132,   165,   177,   309,   318,  -169,   182,
     161,    15,    16,    17,   132,   183,   166,   235,   143,   144,
     145,   146,   147,   148,   194,   133,   134,   135,   136,   202,
     195,   199,   203,   204,   206,   137,   133,   134,   135,   136,
     188,   189,   190,   191,   138,   224,   137,   139,   225,   140,
     208,   141,   245,   199,   199,   138,   132,   249,   139,   247,
     140,   251,   141,   215,   216,   217,   218,   132,   252,   253,
     267,   142,   143,   144,   145,   146,   147,   148,   133,   134,
     135,   254,   142,   143,   144,   145,   146,   147,   148,   133,
     134,   135,   271,     8,   -77,   275,   -78,   138,   281,   277,
     139,   284,   289,   292,   141,     9,   293,   294,   138,    10,
     298,   234,    10,   302,   307,   141,   314,   317,   316,    91,
      39,   221,   259,   222,   142,   143,   144,   145,   146,   147,
     148,    10,     8,   312,   256,   257,   143,   144,   145,   146,
     147,   148,    11,    87,     9,    11,   205,   112,  -156,    14,
    -156,    10,    15,    16,    17,    15,    16,    17,    10,    65,
    -166,   246,    46,     0,    11,     0,     0,   121,     0,     0,
       0,     0,     0,     0,    15,    16,    17,     0,   125,     0,
    -166,     0,     0,     0,    11,     0,     0,  -155,    14,  -155,
       0,    11,     0,     0,    15,    16,    17,     0,  -168,     0,
       0,    15,    16,    17
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
       0,     1,    48,    34,   231,   110,    84,   140,     8,   123,
       3,    55,   233,     6,     7,   199,     3,   204,    69,    15,
       3,    15,    59,    39,   199,    15,    19,    69,    79,    40,
      41,     0,    32,   138,    15,    79,    68,    79,    69,    85,
     118,    39,   226,    42,    43,    77,    48,    49,     0,   233,
      50,   226,     9,   274,   281,    50,     8,    52,   233,    52,
      10,    11,    99,    57,    59,    52,   199,    57,    20,    52,
      27,    79,    50,   119,   120,    27,   303,   304,   109,    77,
     126,    59,    82,    79,   271,    79,    79,   201,    88,    79,
     274,   196,   123,   226,   227,   282,   283,    79,    79,   274,
     233,   136,   137,    60,    79,    55,   106,   107,    60,    61,
      62,    27,    64,    70,    71,    72,    66,     9,    70,    71,
      72,   167,     9,   228,   229,    75,     3,   232,    68,     6,
       7,   131,    54,    50,    56,    27,    79,    77,    50,    50,
      56,   274,    59,   208,    60,   176,    79,    59,    59,   184,
     185,   192,   193,   199,    70,    71,    72,    79,    74,    21,
      76,    79,   267,   268,   164,     0,    79,   272,    60,   247,
     201,   171,    94,     8,    96,    52,   186,   187,    70,    71,
      72,    24,    25,    26,   230,    20,   251,   233,    79,   254,
      79,    79,    27,    79,   299,    79,    79,   275,    65,    11,
      21,   306,   307,    15,    16,    17,    18,    12,    12,   314,
      22,    19,    37,    79,    79,    54,    28,    29,    27,    31,
      32,    33,    34,    35,    68,    60,    61,    62,   274,    64,
      63,   231,    63,     3,    13,    70,    71,    72,    65,    79,
      52,    54,    13,    55,   244,    13,   292,    59,    76,    63,
      75,    60,    77,    11,    56,    79,   302,    15,    67,    79,
      63,    70,    71,    72,    11,    52,    67,    79,    80,    81,
      82,    83,    84,    85,    38,    33,    34,    35,    36,    79,
      37,   281,    79,    79,    53,    43,    33,    34,    35,    36,
      44,    45,    46,    47,    52,    79,    43,    55,    14,    57,
      52,    59,    79,   303,   304,    52,    11,    79,    55,    12,
      57,    54,    59,   188,   189,   190,   191,    11,    53,    51,
       3,    79,    80,    81,    82,    83,    84,    85,    33,    34,
      35,    52,    79,    80,    81,    82,    83,    84,    85,    33,
      34,    35,    79,     8,    39,    12,    39,    52,    23,    53,
      55,    30,    79,    13,    59,    20,    53,    79,    52,    27,
      51,    55,    27,    13,     3,    59,    78,    25,    58,    69,
      19,   194,   227,   195,    79,    80,    81,    82,    83,    84,
      85,    27,     8,   306,   226,    79,    80,    81,    82,    83,
      84,    85,    60,    63,    20,    60,   176,    93,    63,    64,
      65,    27,    70,    71,    72,    70,    71,    72,    27,    45,
      56,   201,    32,    -1,    60,    -1,    -1,   106,    -1,    -1,
      -1,    -1,    -1,    -1,    70,    71,    72,    -1,    74,    -1,
      76,    -1,    -1,    -1,    60,    -1,    -1,    63,    64,    65,
      -1,    60,    -1,    -1,    70,    71,    72,    -1,    67,    -1,
      -1,    70,    71,    72
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
     144,   146,    11,    33,    34,    35,    36,    43,    52,    55,
      57,    59,    79,    80,    81,    82,    83,    84,    85,    97,
      98,    99,   100,   101,   102,   103,   104,   105,   106,   107,
      63,    63,   114,    89,    89,    56,    67,    92,    95,   126,
     145,   147,    69,    79,    57,    89,   150,    79,   104,   104,
     105,   103,    79,    52,    40,    41,    42,    43,    44,    45,
      46,    47,    48,    49,    38,    37,    50,    59,   141,   150,
      57,   150,    79,    79,    79,   144,    53,    59,    52,   105,
     108,   104,   104,    97,    97,    98,    98,    98,    98,    99,
      99,   100,   101,   105,    79,    14,    16,    17,    18,    22,
      28,    29,    31,    32,    55,    79,    89,    94,   103,   106,
     107,   109,   110,   111,   140,    79,   145,    12,    96,    79,
     108,    54,    53,    51,    52,   103,   110,    79,   103,   109,
     105,   105,   112,    89,   141,   105,   111,     3,     3,     6,
       7,    79,    50,    59,   150,    12,   114,    53,   108,   108,
      59,    23,    55,    79,    30,   105,   105,    96,   105,    79,
     111,   114,    13,    53,    79,   141,    96,    96,    51,     3,
       6,     7,    13,    89,    24,    25,    26,     3,   105,    89,
     141,   141,   112,   105,    78,   125,    58,    25,    15,   105
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
      94,    94,    94,    94,    95,    95,    96,    97,    97,    97,
      98,    98,    98,    99,    99,    99,    99,    99,   100,   100,
     100,   101,   101,   102,   102,   103,   103,   103,   103,   103,
     103,   103,   103,   103,   103,   103,   103,   103,   103,   103,
     103,   103,   104,   104,   104,   104,   105,   106,   107,   107,
     108,   108,   108,   109,   109,   109,   109,   110,   110,   111,
     111,   111,   111,   111,   111,   111,   111,   111,   111,   111,
     111,   111,   111,   111,   112,   112,   112,   113,   114,   114,
     114,   115,   115,   116,   116,   117,   117,   118,   118,   119,
     119,   120,   121,   122,   123,   124,   124,   125,   125,   125,
     126,   127,   127,   127,   127,   128,   128,   129,   129,   131,
     130,   133,   132,   135,   134,   136,   136,   136,   136,   136,
     136,   136,   136,   137,   137,   138,   138,   139,   139,   140,
     140,   141,   141,   142,   142,   143,   143,   144,   144,   144,
     145,   145,   146,   146,   147,   147,   148,   148,   149,   149,
     150,   150,   151,   151
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
       3,     4,     2,     3,     3,     4,     2,     3,     3,     1,
       3,     3,     1,     3,     3,     3,     3,     1,     3,     3,
       1,     3,     1,     3,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     2,     3,     1,     1,     1,     4,     1,
       1,     3,     1,     2,     2,     4,     1,     4,     6,     5,
       1,     3,     0,     3,     3,     6,     5,     1,     1,     2,
       1,     2,     1,     2,     4,     2,     4,     2,     1,     2,
       1,     3,     2,     2,     4,     6,     5,     2,     1,     3,
       1,     1,     2,     1,     3,     2,     0,     1,     3,     3,
       1,     3,     7,     9,     9,     2,     3,     2,     2,     0,
       8,     5,     7,     5,     7,     4,     6,     2,     0,     0,
       7,     0,     7,     0,     6,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     3,     1,     3,     1,
       3,     3,     1,     3,     1,     3,     1,     1,     1,     1,
       1,     1,     1,     3,     1,     3,     3,     1,     3,     1,
       1,     2,     1,     0
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


  private static final int YYLAST_ = 453;
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
	

/* "SmashJassParser.java":3129  */

}
/* "SmashJassParser.y":1082  */

