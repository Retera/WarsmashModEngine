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
public class UInt32KeyFrame implements KeyFrame {
	private long time;
	private long value;
	private long inTan;
	private long outTan;

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static StringBuffer STRING_BUFFER_HEAP = new StringBuffer();

	@Override
	public void readMdx(final LittleEndianDataInputStream stream, final InterpolationType interpolationType)
			throws IOException {
		this.time = ParseUtils.readUInt32(stream);
		this.value = ParseUtils.readUInt32(stream);
		if (interpolationType.tangential()) {
			this.inTan = ParseUtils.readUInt32(stream);
			this.outTan = ParseUtils.readUInt32(stream);
		}
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream, final InterpolationType interpolationType)
			throws IOException {
		stream.writeInt((int) this.time);
		stream.writeInt((int) this.value);
		if (interpolationType.tangential()) {
			stream.writeInt((int) this.inTan);
			stream.writeInt((int) this.outTan);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final InterpolationType interpolationType)
			throws IOException {
		this.time = stream.readUInt32();
		this.value = stream.readUInt32();
		if (interpolationType.tangential()) {
			stream.read(); // "InTan"
			this.inTan = stream.readUInt32();
			stream.read(); // "OutTan"
			this.outTan = stream.readUInt32();
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
		final long valueSize = Integer.BYTES; // unsigned
		long size = 4 + valueSize;
		if (interpolationType.tangential()) {
			size += valueSize * 2;
		}
		return size;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public boolean matchingValue(final KeyFrame other) {
		if (other instanceof UInt32KeyFrame) {
			final UInt32KeyFrame otherFrame = (UInt32KeyFrame) other;
			return value == otherFrame.value;
		}
		return false;
	}

	@Override
	public KeyFrame clone(final long time) {
		final UInt32KeyFrame newKeyFrame = new UInt32KeyFrame();
		newKeyFrame.value = value;
		newKeyFrame.inTan = inTan;
		newKeyFrame.outTan = outTan;
		return newKeyFrame;
	}
}
