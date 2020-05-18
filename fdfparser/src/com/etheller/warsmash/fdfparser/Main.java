package com.etheller.warsmash.fdfparser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.etheller.warsmash.parsers.fdf.templates.FrameTemplateEnvironment;

public class Main {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <Filename>");
			return;
		}
		try {
			final BaseErrorListener errorListener = new BaseErrorListener() {
				@Override
				public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
						final int charPositionInLine, final String msg, final RecognitionException e) {
					if (!REPORT_SYNTAX_ERRORS) {
						return;
					}

					String sourceName = recognizer.getInputStream().getSourceName();
					if (!sourceName.isEmpty()) {
						sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
					}

					System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
				}
			};
			final FrameTemplateEnvironment templates = new FrameTemplateEnvironment();
			final TestFDFParserBuilder testFDFParserBuilder = new TestFDFParserBuilder(errorListener);
			final FrameDefinitionVisitor fdfVisitor = new FrameDefinitionVisitor(templates, testFDFParserBuilder);
			final FDFParser firstFileParser = testFDFParserBuilder.build(args[0]);
			fdfVisitor.visit(firstFileParser.program());
			System.out.println("Value of MONTH_12: " + templates.getDecoratedString("MONTH_12"));
		}
		catch (final Exception exc) {
			System.err.println(exc.getMessage());
		}
	}

}
