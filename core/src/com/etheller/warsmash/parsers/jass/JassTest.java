package com.etheller.warsmash.parsers.jass;

import java.io.IOException;
import java.util.Arrays;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.etheller.interpreter.JassLexer;
import com.etheller.interpreter.JassParser;
import com.etheller.interpreter.ast.visitors.JassProgramVisitor;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;

public class JassTest {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static void main(final String[] args) {
		final JassProgramVisitor jassProgramVisitor = new JassProgramVisitor();
		try {
			final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
					"E:\\Backups\\Warcraft\\Data\\127");
			final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
					"E:\\Backups\\Warsmash\\Data");
			final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
			final DataSource dataSource = new CompoundDataSourceDescriptor(
					Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder, currentFolder)).createDataSource();
			JassLexer lexer;
			try {
				lexer = new JassLexer(CharStreams.fromStream(dataSource.getResourceAsStream("Scripts\\common.j")));
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
			final JassParser parser = new JassParser(new CommonTokenStream(lexer));
			parser.addErrorListener(new BaseErrorListener() {
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
			});
			jassProgramVisitor.visit(parser.program());
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
