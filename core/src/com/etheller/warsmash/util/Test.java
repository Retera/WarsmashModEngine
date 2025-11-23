package com.etheller.warsmash.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;

public class Test {

	public static void main(final String[] args) {
		final String model = "TargetArtLumber";
		WarsmashConstants.PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = true;
		{
			final File mdxFile = new File("/home/etheller/Downloads/" + model + ".mdx");
			final File mdlFile = new File("/home/etheller/Downloads/" + model + ".mdl");
			try (FileInputStream stream = new FileInputStream(mdxFile)) {
				final MdlxModel mdlx = MdxUtils.loadMdlx(stream);
				final ByteBuffer mdl = mdlx.saveMdl();
				try (FileChannel outChannel = FileChannel.open(mdlFile.toPath(), StandardOpenOption.CREATE,
						StandardOpenOption.WRITE)) {
					outChannel.write(mdl);
				}
			}
			catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
		WarsmashConstants.PARSE_REIGN_OF_CHAOS_BETA_MODELS_INSTEAD = false;
		{
			final File mdxFile = new File("/home/etheller/Downloads/" + model + ".mdl");
			final File mdlFile = new File("/home/etheller/Downloads/" + model + "BETA.mdx");
			try (FileInputStream stream = new FileInputStream(mdxFile)) {
				final MdlxModel mdlx = MdxUtils.loadMdlx(stream);
				final ByteBuffer mdl = mdlx.saveMdx();
				try (FileChannel outChannel = FileChannel.open(mdlFile.toPath(), StandardOpenOption.CREATE,
						StandardOpenOption.WRITE)) {
					outChannel.write(mdl);
				}
			}
			catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
