package com.hiveworkshop.rms.parsers.mdlx.timeline;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public final class MdlxFloatTimeline extends MdlxTimeline<float[]> {
	@Override
	protected int size() {
		return 1;
	}

	@Override
	protected float[] readMdxValue(final BinaryReader reader) {
		return new float[] { reader.readFloat32() };
	}

	@Override
	protected float[] readMdlValue(final MdlTokenInputStream stream) {
		return new float[] { stream.readFloat() };
	}

	@Override
	protected void writeMdxValue(final BinaryWriter writer, final float[] value) {
		writer.writeFloat32(value[0]);
	}

	@Override
	protected void writeMdlValue(final MdlTokenOutputStream stream, final String prefix, final float[] value) {
		stream.writeKeyframe(prefix, value[0]);
	}
}
