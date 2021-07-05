package com.hiveworkshop.rms.parsers.mdlx.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.compress.utils.IOUtils;

import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

public class MdxUtils {
	public static MdlxModel loadMdlx(final InputStream inputStream) throws IOException {
		return new MdlxModel(ByteBuffer.wrap(IOUtils.toByteArray(inputStream)));
	}

	public static void saveMdx(final MdlxModel model, final OutputStream outputStream) throws IOException {
		outputStream.write(model.saveMdx().array());
	}

	public static void saveMdl(final MdlxModel model, final OutputStream outputStream) throws IOException {
		outputStream.write(model.saveMdl().array());
	}

	public static void saveMdl(final MdlxModel model, final File file) throws IOException {
		saveMdl(model, new FileOutputStream(file));
	}

}
