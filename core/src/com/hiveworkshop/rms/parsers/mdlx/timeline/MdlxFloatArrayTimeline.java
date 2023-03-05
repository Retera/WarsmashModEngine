package com.hiveworkshop.rms.parsers.mdlx.timeline;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public final class MdlxFloatArrayTimeline extends MdlxTimeline<float[]> {
	private final int arraySize;

	public MdlxFloatArrayTimeline(final int arraySize) {
		this.arraySize = arraySize;
	}

	@Override
	protected int size(int version) {
		if (version == 1300 && arraySize == 4) {
			return 2;
		}
		return arraySize;
	}

	@Override
	protected float[] readMdxValue(final BinaryReader reader, int version) {
		if(version == 1300 && arraySize == 4) {
//			float x = (reader.readUInt16() - Short.MAX_VALUE) / (float)(Short.MAX_VALUE);
//			float y = (reader.readUInt16() - Short.MAX_VALUE) / (float)(Short.MAX_VALUE);
//			float z = (reader.readUInt16() - Short.MAX_VALUE) / (float)(Short.MAX_VALUE);
//			float w = (reader.readUInt16() - Short.MAX_VALUE) / (float)(Short.MAX_VALUE);
			// quaternion decompress
			long compressedQuaternion = reader.readInt64();
			double x = ( compressedQuaternion >> 42 ) * 0.00000047683716;
			double y = (( compressedQuaternion << 22 ) >> 43 ) * 0.00000095367432;
			double z = ( ((compressedQuaternion & 0x1FFFFF) << 43) >> 43 ) * 0.00000095367432;
			double w;

			double len = x * x + y * y + z * z;
			if (( 1.0 - len ) >= 0.00000095367432 ) {
				w = Math.sqrt(1.0 - len);
			}
			else {
				w = 0.0f;
			}

			return new float[] {(float)x, (float)y, (float)z, (float)w};
		} else {
			return reader.readFloat32Array(arraySize);
		}
	}

	@Override
	protected float[] readMdlValue(final MdlTokenInputStream stream) {
		final float[] output = new float[arraySize];
		stream.readKeyframe(output);
		return output;
	}

	@Override
	protected void writeMdxValue(final BinaryWriter writer, final float[] value) {
		writer.writeFloat32Array(value);
	}

	@Override
	protected void writeMdlValue(final MdlTokenOutputStream stream, final String prefix, final float[] value) {
		stream.writeKeyframe(prefix, value);
	}

	public int getArraySize() {
		return arraySize;
	}
}
