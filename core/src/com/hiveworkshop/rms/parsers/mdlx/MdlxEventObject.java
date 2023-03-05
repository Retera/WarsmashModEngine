package com.hiveworkshop.rms.parsers.mdlx;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxEventObject extends MdlxGenericObject {
	private static final War3ID KEVT = War3ID.fromString("KEVT");

	public int globalSequenceId = -1;
	public long[] keyFrames = { 1 };

	public MdlxEventObject() {
		super(0x400);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		long size;
		long position;
		if (version == 1300) {
			position = reader.position();
			size = reader.readUInt32();
			System.out.println("MdlxEventObject size: " + size);
		}
		else {
			position = 0;
			size = 0;
		}
		super.readMdx(reader, version);

		if ((version != 1300) || ((reader.position() - position) < size)) {
			System.out.println((reader.position() - position) + " < " + size);
			final int shouldBeKEVT = reader.readInt32(); // KEVT skipped
			System.out.println("MdlxEventObject should be KEVT: " + MdlxModel.convertInt2(shouldBeKEVT));

			final long count = reader.readUInt32();
			System.out.println("MdlxEventObject count: " + count);

			this.globalSequenceId = reader.readInt32();
			System.out.println("MdlxEventObject globalSequenceId: " + this.globalSequenceId);

			this.keyFrames = new long[(int) count];

			for (int i = 0; i < count; i++) {
				this.keyFrames[i] = reader.readInt32();
				System.out.println("MdlxEventObject keyFrame[" + i + "]: " + this.keyFrames[i]);
			}
		}
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		super.writeMdx(writer, version);

		writer.writeTag(KEVT.getValue());
		writer.writeUInt32(this.keyFrames.length);
		writer.writeInt32(this.globalSequenceId);

		for (final long keyFrame : this.keyFrames) {
			writer.writeUInt32(keyFrame);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			if (MdlUtils.TOKEN_EVENT_TRACK.equals(token)) {
				this.keyFrames = new long[stream.readInt()];
				stream.readIntArray(this.keyFrames);
			}
			else {
				throw new RuntimeException("Unknown token in EventObject " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_EVENT_OBJECT, this.name);
		writeGenericHeader(stream);
		stream.startBlock(MdlUtils.TOKEN_EVENT_TRACK, this.keyFrames.length);

		for (final long keyFrame : this.keyFrames) {
			stream.writeFlagUInt32(keyFrame);
		}

		stream.endBlock();

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 12 + (this.keyFrames.length * 4) + super.getByteLength(version);
	}

	public static War3ID getKevt() {
		return KEVT;
	}

	public int getGlobalSequenceId() {
		return this.globalSequenceId;
	}

	public long[] getKeyFrames() {
		return this.keyFrames;
	}
}
