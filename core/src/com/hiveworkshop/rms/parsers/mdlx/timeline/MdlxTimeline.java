package com.hiveworkshop.rms.parsers.mdlx.timeline;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.InterpolationType;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public abstract class MdlxTimeline<TYPE> {
	public War3ID name;
	public InterpolationType interpolationType;
	public int globalSequenceId = -1;

	public long[] frames;
	public TYPE[] values;
	public TYPE[] inTans;
	public TYPE[] outTans;

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static final StringBuffer STRING_BUFFER_HEAP = new StringBuffer();

	public MdlxTimeline() {

	}

	public void readMdx(final BinaryReader reader, final War3ID name) {
		this.name = name;

		final long keyFrameCount = reader.readUInt32();

		this.interpolationType = InterpolationType.getType(reader.readInt32());
		this.globalSequenceId = reader.readInt32();

		this.frames = new long[(int) keyFrameCount];
		this.values = (TYPE[]) new Object[(int) keyFrameCount];
		if (this.interpolationType.tangential()) {
			this.inTans = (TYPE[]) new Object[(int) keyFrameCount];
			this.outTans = (TYPE[]) new Object[(int) keyFrameCount];
		}

		for (int i = 0; i < keyFrameCount; i++) {
			this.frames[i] = reader.readInt32();
			this.values[i] = (readMdxValue(reader));

			if (this.interpolationType.tangential()) {
				this.inTans[i] = (readMdxValue(reader));
				this.outTans[i] = (readMdxValue(reader));
			}
		}
	}

	public void writeMdx(final BinaryWriter writer) {
		writer.writeTag(this.name.getValue());

		final int keyframeCount = this.frames.length;

		writer.writeInt32(keyframeCount);
		writer.writeInt32(this.interpolationType.ordinal());
		writer.writeInt32(this.globalSequenceId);

		for (int i = 0; i < keyframeCount; i++) {
			writer.writeInt32((int) this.frames[i]);
			writeMdxValue(writer, this.values[i]);

			if (this.interpolationType.tangential()) {
				writeMdxValue(writer, this.inTans[i]);
				writeMdxValue(writer, this.outTans[i]);
			}
		}
	}

	public void readMdl(final MdlTokenInputStream stream, final War3ID name) {
		this.name = name;

		final int keyFrameCount = stream.readInt();

		stream.read(); // {

		final String token = stream.read();
		final InterpolationType interpolationType;
		switch (token) {
		case MdlUtils.TOKEN_DONT_INTERP:
			interpolationType = InterpolationType.DONT_INTERP;
			break;
		case MdlUtils.TOKEN_LINEAR:
			interpolationType = InterpolationType.LINEAR;
			break;
		case MdlUtils.TOKEN_HERMITE:
			interpolationType = InterpolationType.HERMITE;
			break;
		case MdlUtils.TOKEN_BEZIER:
			interpolationType = InterpolationType.BEZIER;
			break;
		default:
			interpolationType = InterpolationType.DONT_INTERP;
			break;
		}
		;

		this.interpolationType = interpolationType;

		if (stream.peek().equals(MdlUtils.TOKEN_GLOBAL_SEQ_ID)) {
			stream.read();
			this.globalSequenceId = stream.readInt();
		}
		else {
			this.globalSequenceId = -1;
		}

		this.frames = new long[keyFrameCount];
		this.values = (TYPE[]) new Object[keyFrameCount];
		if (this.interpolationType.tangential()) {
			this.inTans = (TYPE[]) new Object[keyFrameCount];
			this.outTans = (TYPE[]) new Object[keyFrameCount];
		}
		for (int i = 0; i < keyFrameCount; i++) {
			this.frames[i] = (stream.readInt());
			this.values[i] = (readMdlValue(stream));
			if (interpolationType.tangential()) {
				stream.read(); // InTan
				this.inTans[i] = (readMdlValue(stream));
				stream.read(); // OutTan
				this.outTans[i] = (readMdlValue(stream));
			}
		}

		stream.read(); // }
	}

	public void writeMdl(final MdlTokenOutputStream stream) {
		final int tracksCount = this.frames.length;
		stream.startBlock(AnimationMap.ID_TO_TAG.get(this.name).getMdlToken(), tracksCount);

		stream.writeFlag(this.interpolationType.toString());

		if (this.globalSequenceId != -1) {
			stream.writeAttrib(MdlUtils.TOKEN_GLOBAL_SEQ_ID, this.globalSequenceId);
		}

		for (int i = 0; i < tracksCount; i++) {
			STRING_BUFFER_HEAP.setLength(0);
			STRING_BUFFER_HEAP.append(this.frames[i]);
			STRING_BUFFER_HEAP.append(':');
			writeMdlValue(stream, STRING_BUFFER_HEAP.toString(), this.values[i]);
			if (this.interpolationType.tangential()) {
				stream.indent();
				writeMdlValue(stream, "InTan", this.inTans[i]);
				writeMdlValue(stream, "OutTan", this.outTans[i]);
				stream.unindent();
			}
		}

		stream.endBlock();
	}

	public long getByteLength() {
		final int tracksCount = this.frames.length;
		int size = 16;

		if (tracksCount > 0) {
			final int bytesPerValue = size() * 4;
			int valuesPerTrack = 1;
			if (this.interpolationType.tangential()) {
				valuesPerTrack = 3;
			}

			size += (4 + (valuesPerTrack * bytesPerValue)) * tracksCount;
		}
		return size;
	}

	protected abstract int size();

	protected abstract TYPE readMdxValue(BinaryReader reader);

	protected abstract TYPE readMdlValue(MdlTokenInputStream stream);

	protected abstract void writeMdxValue(BinaryWriter writer, TYPE value);

	protected abstract void writeMdlValue(MdlTokenOutputStream stream, String prefix, TYPE value);

	public War3ID getName() {
		return this.name;
	}

	public InterpolationType getInterpolationType() {
		return this.interpolationType;
	}

	public int getGlobalSequenceId() {
		return this.globalSequenceId;
	}

	public long[] getFrames() {
		return this.frames;
	}

	public TYPE[] getValues() {
		return this.values;
	}

	public TYPE[] getInTans() {
		return this.inTans;
	}

	public TYPE[] getOutTans() {
		return this.outTans;
	}

}
