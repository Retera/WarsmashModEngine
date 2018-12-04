package com.etheller.warsmash.parsers.mdlx;

import java.util.Iterator;

public interface MdlTokenInputStream {
	String read();

	long readUInt32();

	int readInt();

	float readFloat();

	void readKeyframe(float[] values);

	String peek();

	Iterator<String> readBlock();
}
