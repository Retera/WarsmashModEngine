package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxTextureAnimation extends MdlxAnimatedObject {
	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long size = reader.readUInt32();

		readTimelines(reader, size - 4);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		writeTimelines(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_TRANSLATION:
				readTimeline(stream, AnimationMap.KTAT);
				break;
			case MdlUtils.TOKEN_ROTATION:
				readTimeline(stream, AnimationMap.KTAR);
				break;
			case MdlUtils.TOKEN_SCALING:
				readTimeline(stream, AnimationMap.KTAS);
				break;
			default:
				throw new RuntimeException("Unknown token in TextureAnimation: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_TVERTEX_ANIM);
		writeTimeline(stream, AnimationMap.KTAT);
		writeTimeline(stream, AnimationMap.KTAR);
		writeTimeline(stream, AnimationMap.KTAS);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 4 + super.getByteLength(version);
	}
}
