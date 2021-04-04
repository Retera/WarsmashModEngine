package com.hiveworkshop.rms.parsers.mdlx.timeline;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public final class MdlxUInt32Timeline extends MdlxTimeline<long[]> {
	@Override
	protected int size() {
		return 1;
	}

	@Override
	protected long[] readMdxValue(final BinaryReader reader) {
		return new long[]{reader.readUInt32()};
	}

	@Override
	protected long[] readMdlValue(final MdlTokenInputStream stream) {
		return new long[]{stream.readUInt32()};
	}

	@Override
	protected void writeMdxValue(final BinaryWriter writer, final long[] uint32) {
		writer.writeUInt32(uint32[0]);
	}

	@Override
	protected void writeMdlValue(final MdlTokenOutputStream stream, final String prefix, final long[] uint32) {
		stream.writeKeyframe(prefix, uint32[0]);
	}

}
