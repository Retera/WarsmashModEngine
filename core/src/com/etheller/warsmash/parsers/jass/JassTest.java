package com.etheller.warsmash.parsers.jass;

import java.io.InputStreamReader;
import java.util.Arrays;

import com.etheller.interpreter.ast.util.JassProgram;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;

import net.warsmash.parsers.jass.SmashJassParser;

public class JassTest {
	public static final boolean REPORT_SYNTAX_ERRORS = true;

	public static void main(final String[] args) {
		final JassProgram jassProgramVisitor = new JassProgram();
		try {
			final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor("C:\\Warsmash\\127-fix");
			final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
					"E:\\Backups\\Warsmash\\Data");
			final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
			final DataSource dataSource = new CompoundDataSourceDescriptor(
					Arrays.<DataSourceDescriptor>asList(war3mpq/* , testingFolder */, currentFolder))
					.createDataSource();
			try (InputStreamReader reader = new InputStreamReader(
					dataSource.getResourceAsStream("Scripts\\common.j"))) {
				final SmashJassParser smashJassParser = new SmashJassParser(reader);
				smashJassParser.scanAndParse("Scripts\\common.j", jassProgramVisitor);
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
