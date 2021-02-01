package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public interface MdlxBlock {
	void readMdx(final BinaryReader reader, final int version);

	void writeMdx(final BinaryWriter writer, final int version);

	void readMdl(final MdlTokenInputStream stream, final int version);

	void writeMdl(final MdlTokenOutputStream stream, final int version);
}
