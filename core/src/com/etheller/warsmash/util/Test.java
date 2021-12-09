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
		final File mdxFile = new File("C:\\Users\\micro\\Downloads\\DrunkSnake\\NagaTridentyr.mdx");
		final File mdlFile = new File("C:\\Users\\micro\\Downloads\\DrunkSnake\\NagaTridentyr.mdl");
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

}
