package com.etheller.interpreter.ast;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;

public class JassRunner {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <JassFiles> [<AdditionaFile>...]");
			return;
		}
		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		for (final String arg : args) {
			try {
				final JassLexer lexer = new JassLexer(CharStreams.fromFileName(arg));
				final JassParser parser = new JassParser(new CommonTokenStream(lexer));
				parser.addErrorListener(new BaseErrorListener() {
					@Override
					public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
							final int line, final int charPositionInLine, final String msg,
							final RecognitionException e) {
						if (!REPORT_SYNTAX_ERRORS) {
							return;
						}

						String sourceName = recognizer.getInputStream().getSourceName();
						if (!sourceName.isEmpty()) {
							sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
						}

						System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
					}
				});
				jassProgramVisitor.visit(parser.program());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
