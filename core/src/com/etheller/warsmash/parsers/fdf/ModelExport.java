package com.etheller.warsmash.parsers.fdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;

public class ModelExport {

	public static void main(final String[] args) {

		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor("E:\\Backups\\Warcraft\\Data\\127");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor("E:\\Backups\\Warsmash\\Data");
		final FolderDataSourceDescriptor currentFolder = new FolderDataSourceDescriptor(".");
		final DataSource dataSource = new CompoundDataSourceDescriptor(
				Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder, currentFolder)).createDataSource();

		try (InputStream modelStream = dataSource
				.getResourceAsStream("UI\\Glues\\MainMenu\\MainMenu3D\\MainMenu3D.mdx")) {
			final MdlxModel model = new MdlxModel(dataSource.read("UI\\Glues\\MainMenu\\MainMenu3D\\MainMenu3D.mdx"));
			try (FileOutputStream fos = new FileOutputStream(new File("C:\\Temp\\MainMenu3D.mdl"))) {
				MdxUtils.saveMdl(model, fos);
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
