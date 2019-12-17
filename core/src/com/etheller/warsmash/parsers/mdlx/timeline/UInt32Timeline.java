package com.etheller.warsmash.parsers.mdlx.timeline;

import java.io.IOException;

import com.etheller.warsmash.parsers.mdlx.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.MdlTokenOutputStream;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public final class UInt32Timeline extends Timeline<long[]> {

	@Override
	protected int size() {
		return 1;
	}

	@Override
	protected long[] readMdxValue(final LittleEndianDataInputStream stream) throws IOException {
		return new long[] { ParseUtils.readUInt32(stream) };
	}

	@Override
	protected long[] readMdlValue(final MdlTokenInputStream stream) {
		return new long[] { stream.readUInt32() };
	}

	@Override
	protected void writeMdxValue(final LittleEndianDataOutputStream stream, final long[] uint32) throws IOException {
		ParseUtils.writeUInt32(stream, uint32[0]);
	}

	@Override
	protected void writeMdlValue(final MdlTokenOutputStream stream, final String prefix, final long[] uint32) {
		stream.writeKeyframe(prefix, uint32[0]);
	}

}
