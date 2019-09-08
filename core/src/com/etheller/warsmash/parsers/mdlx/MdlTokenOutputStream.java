package com.etheller.warsmash.parsers.mdlx;

public interface MdlTokenOutputStream {
	void writeKeyframe(String prefix, long uInt32Value);

	void writeKeyframe(String prefix, float floatValue);

	void writeKeyframe(String prefix, float[] floatArrayValues);

	void indent();

	void unindent();

	void startObjectBlock(String name, String objectName);

	void startBlock(String name, int blockSize);

	void startBlock(String name);

	void writeFlag(String token);

	void writeFlagUInt32(long flag);

	void writeAttrib(String string, int globalSequenceId);

	void writeAttribUInt32(String attribName, long uInt);

	void writeAttrib(String string, String value);

	void writeFloatAttrib(String attribName, float value);

	// if this matches writeAttrib(String,String),
	// then remove it
	void writeStringAttrib(String attribName, String value);

	void writeFloatArrayAttrib(String attribName, float[] floatArray);

	void writeLongSubArrayAttrib(String attribName, long[] array, int startIndexInclusive, int endIndexExclusive);

	void writeFloatArray(float[] floatArray);

	void writeVectorArray(String token, float[] vectors, int vectorLength);

	void endBlock();

	void endBlockComma();

	void writeLine(String string);

	void startBlock(String tokenFaces, int sizeNumberProbably, int length);

	void writeColor(String tokenStaticColor, float[] color);

	void writeArrayAttrib(String tokenAlpha, short[] uint8Array);

	void writeArrayAttrib(String tokenAlpha, int[] uint16Array);

	void writeArrayAttrib(String tokenAlpha, long[] uint32Array);
}
