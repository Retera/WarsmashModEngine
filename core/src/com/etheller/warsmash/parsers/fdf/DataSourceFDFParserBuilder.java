package com.etheller.warsmash.parsers.fdf;

import java.io.IOException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.fdfparser.FDFLexer;
import com.etheller.warsmash.fdfparser.FDFParser;
import com.etheller.warsmash.fdfparser.FDFParserBuilder;

public class DataSourceFDFParserBuilder implements FDFParserBuilder {
	private final DataSource dataSource;

	public DataSourceFDFParserBuilder(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public FDFParser build(final String path) {
		FDFLexer lexer;
		try {
			lexer = new FDFLexer(CharStreams.fromStream(this.dataSource.getResourceAsStream(path)));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		final FDFParser fdfParser = new FDFParser(new CommonTokenStream(lexer));
		final BaseErrorListener errorListener = new BaseErrorListener() {
			@Override
			public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
					final int charPositionInLine, final String msg, final RecognitionException e) {
				String sourceName = path;
				if (!sourceName.isEmpty()) {
					sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
				}

				System.err.println(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
			}
		};
		fdfParser.addErrorListener(errorListener);
		return fdfParser;
	}
}