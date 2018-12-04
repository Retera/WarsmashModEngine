package com.etheller.warsmash.parsers.mdlx;

public interface MdlTokenOutputStream {
	void writeKeyframe(String prefix, long uInt32Value);

	void writeKeyframe(String prefix, float floatValue);

	void writeKeyframe(String prefix, float[] floatArrayValues);

	void indent();

	void unindent();

	void startBlock(String name, int blockSize);

	void writeFlag(String token);

	void writeAttrib(String string, int globalSequenceId);

	void endBlock();
}
