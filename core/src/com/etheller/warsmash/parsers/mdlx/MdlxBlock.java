package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public interface MdlxBlock {
	void readMdx(final LittleEndianDataInputStream stream) throws IOException;

	void writeMdx(final LittleEndianDataOutputStream stream) throws IOException;

	void readMdl(final MdlTokenInputStream stream) throws IOException;

	void writeMdl(final MdlTokenOutputStream stream) throws IOException;
}
