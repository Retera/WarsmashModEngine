package com.etheller.warsmash.fdfparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestFDFParserBuilder implements FDFParserBuilder {
	private final BaseErrorListener errorListener;

	public TestFDFParserBuilder(final BaseErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	@Override
	public FDFParser build(final String path) {
		if (Files.exists(Path.of(path))) {
			FDFLexer lexer;
			try {
				lexer = new FDFLexer(CharStreams.fromFileName(path));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			return new FDFParser(new CommonTokenStream(lexer));
		} else {
			System.err.println("File " + path + " does not exist.");
			return null;
		}
	}
}
