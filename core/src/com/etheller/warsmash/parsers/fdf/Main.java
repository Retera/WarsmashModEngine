package com.etheller.warsmash.parsers.fdf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.etheller.warsmash.fdfparser.FDFParser;
import com.etheller.warsmash.fdfparser.FrameDefinitionVisitor;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FrameTemplateEnvironment;

public class Main {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static void main(final String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <Filename>");
			return;
		}
		try {

			final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
					"E:\\Backups\\Warcraft\\Data\\127");
			final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
					"E:\\Backups\\Warsmash\\Data");
			final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
			final DataSource dataSource = new CompoundDataSourceDescriptor(
					Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder, currentFolder)).createDataSource();

			final FrameTemplateEnvironment templates = new FrameTemplateEnvironment();
			final DataSourceFDFParserBuilder dataSourceFDFParserBuilder = new DataSourceFDFParserBuilder(dataSource);
			final FrameDefinitionVisitor fdfVisitor = new FrameDefinitionVisitor(templates, dataSourceFDFParserBuilder);
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(dataSource.getResourceAsStream("UI\\FrameDef\\FrameDef.toc")))) {
				String line;
				while ((line = reader.readLine()) != null) {
					final FDFParser firstFileParser = dataSourceFDFParserBuilder.build(line);
					fdfVisitor.visit(firstFileParser.program());
				}
			}

			final FrameDefinition bnetChat = templates.getFrame("ConsoleUI");
			System.out.println("Value of ConsoleUI: " + bnetChat);
		}
		catch (final Exception exc) {
			exc.printStackTrace();
			System.err.println(exc.getMessage());
		}
	}

}
