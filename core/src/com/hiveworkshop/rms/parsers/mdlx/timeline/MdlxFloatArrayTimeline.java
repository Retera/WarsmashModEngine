package com.hiveworkshop.rms.parsers.mdlx.timeline;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public final class MdlxFloatArrayTimeline extends MdlxTimeline<float[]> {
	private final int arraySize;

	public MdlxFloatArrayTimeline(final int arraySize) {
		this.arraySize = arraySize;
	}

	@Override
	protected int size() {
		return arraySize;
	}

	@Override
	protected float[] readMdxValue(final BinaryReader reader) {
		return reader.readFloat32Array(arraySize);
	}

	@Override
	protected float[] readMdlValue(final MdlTokenInputStream stream) {
		final float[] output = new float[arraySize];
		stream.readKeyframe(output);
		return output;
	}

	@Override
	protected void writeMdxValue(final BinaryWriter writer, final float[] value) {
		writer.writeFloat32Array(value);
	}

	@Override
	protected void writeMdlValue(final MdlTokenOutputStream stream, final String prefix, final float[] value) {
		stream.writeKeyframe(prefix, value);
	}

	public int getArraySize() {
		return arraySize;
	}
}
