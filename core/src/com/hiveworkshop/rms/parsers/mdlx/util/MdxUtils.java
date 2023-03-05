package com.hiveworkshop.rms.parsers.mdlx.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

public class MdxUtils {
	public static MdlxModel loadMdlx(final InputStream in) throws IOException {
		return new MdlxModel(ByteBuffer.wrap(in.readAllBytes()));
	}

	public static void saveMdx(final MdlxModel model, final OutputStream out) throws IOException {
		out.write(model.saveMdx().array());
	}

	public static void saveMdl(final MdlxModel model, final OutputStream out) throws IOException {
		out.write(model.saveMdl().array());
	}

	public static void saveMdl(final MdlxModel model, final File out) throws IOException {
		saveMdl(model, new FileOutputStream(out));
	}

}
