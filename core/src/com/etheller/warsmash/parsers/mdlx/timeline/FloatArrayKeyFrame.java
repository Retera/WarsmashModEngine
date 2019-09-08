package com.etheller.warsmash.parsers.mdlx.timeline;

import java.io.IOException;

import com.etheller.warsmash.parsers.mdlx.InterpolationType;
import com.etheller.warsmash.parsers.mdlx.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.MdlTokenOutputStream;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A UInt32 animation keyframe in a time track.
 *
 * Based on the works of Chananya Freiman. I changed the name of Track to
 * KeyFrame. A possible future optimization would be to make 2 subclasses, one
 * with inTan and one without, so that non-Hermite/non-Bezier animation data
 * would use less memory.
 *
 */
public class FloatArrayKeyFrame implements KeyFrame {
	private long time;
	private final float[] value;
	private final float[] inTan;
	private final float[] outTan;

	public FloatArrayKeyFrame(final int arraySize) {
		this.value = new float[arraySize];
		this.inTan = new float[arraySize];
		this.outTan = new float[arraySize];
	}

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static StringBuffer STRING_BUFFER_HEAP = new StringBuffer();

	@Override
	public void readMdx(final LittleEndianDataInputStream stream, final InterpolationType interpolationType)
			throws IOException {
		this.time = ParseUtils.readUInt32(stream);
		ParseUtils.readFloatArray(stream, this.value);
		if (interpolationType.tangential()) {
			ParseUtils.readFloatArray(stream, this.inTan);
			ParseUtils.readFloatArray(stream, this.outTan);
		}
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream, final InterpolationType interpolationType)
			throws IOException {
		stream.writeInt((int) this.time);
		ParseUtils.writeFloatArray(stream, this.value);
		if (interpolationType.tangential()) {
			ParseUtils.writeFloatArray(stream, this.inTan);
			ParseUtils.writeFloatArray(stream, this.outTan);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final InterpolationType interpolationType)
			throws IOException {
		this.time = stream.readUInt32();
		stream.readKeyframe(this.value);
		if (interpolationType.tangential()) {
			stream.read(); // "InTan"
			stream.readKeyframe(this.inTan);
			stream.read(); // "OutTan"
			stream.readKeyframe(this.outTan);
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final InterpolationType interpolationType)
			throws IOException {
		STRING_BUFFER_HEAP.setLength(0);
		STRING_BUFFER_HEAP.append(this.time);
		STRING_BUFFER_HEAP.append(':');
		stream.writeKeyframe(STRING_BUFFER_HEAP.toString(), this.value);
		if (interpolationType.tangential()) {
			stream.indent();
			stream.writeKeyframe("InTan", this.inTan);
			stream.writeKeyframe("OutTan", this.outTan);
			stream.unindent();
		}
	}

	@Override
	public long getByteLength(final InterpolationType interpolationType) {
		final long valueSize = Float.BYTES * this.value.length; // unsigned
		long size = 4 + valueSize;
		if (interpolationType.tangential()) {
			size += valueSize * 2;
		}
		return size;
	}

}
