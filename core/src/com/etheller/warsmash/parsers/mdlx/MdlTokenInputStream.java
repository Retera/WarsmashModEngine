package com.etheller.warsmash.parsers.mdlx;

public interface MdlTokenInputStream {
	String read();

	long readUInt32();

	int readInt();

	float readFloat();

	void readIntArray(long[] values);

	float[] readFloatArray(float[] values); // is this same as read keyframe???

	void readKeyframe(float[] values);

	float[] readVectorArray(float[] array, int vectorLength);

	int[] readUInt16Array(int[] values);

	short[] readUInt8Array(short[] values);

	String peek();

	// needs crazy generator function behavior that I can call this multiple times
	// and it allocates a new iterator that is changing the same underlying
	// stream position, and needs nesting of blocks within blocks
	// (see crazy transcribed generator in GenericObject, only makes good sense
	// in JS)
	Iterable<String> readBlock();

	void readColor(float[] color);

}
