package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;

public class Helper extends GenericObject {

	public Helper() {
		super(0x0); // NOTE: ghostwolf JS didn't pass the 0x1 flag????
		// ANOTHER NOTE: setting the 0x1 flag causes other fan programs to spam error
		// messages
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) {
		for (final String token : readMdlGeneric(stream)) {
			throw new IllegalStateException("Unknown token in Helper: " + token);
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_HELPER, this.name);
		writeGenericHeader(stream);
		writeGenericTimelines(stream);
		stream.endBlock();
	}
}
