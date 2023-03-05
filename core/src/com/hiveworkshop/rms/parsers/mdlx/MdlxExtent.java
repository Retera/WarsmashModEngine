package com.hiveworkshop.rms.parsers.mdlx;

import java.util.Arrays;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxExtent {
	public float boundsRadius = 0;
	public float[] min = new float[3];
	public float[] max = new float[3];

	public void readMdx(final BinaryReader reader) {
		this.boundsRadius = reader.readFloat32();
		reader.readFloat32Array(this.min);
		reader.readFloat32Array(this.max);
	}

	public void writeMdx(final BinaryWriter writer) {
		writer.writeFloat32(this.boundsRadius);
		writer.writeFloat32Array(this.min);
		writer.writeFloat32Array(this.max);
	}

	public void writeMdl(final MdlTokenOutputStream stream) {
		if ((this.min[0] != 0) || (this.min[1] != 0) || (this.min[2] != 0)) {
			stream.writeFloatArrayAttrib(MdlUtils.TOKEN_MINIMUM_EXTENT, this.min);
		}

		if ((this.max[0] != 0) || (this.max[1] != 0) || (this.max[2] != 0)) {
			stream.writeFloatArrayAttrib(MdlUtils.TOKEN_MAXIMUM_EXTENT, this.max);
		}

		if (this.boundsRadius != 0) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_BOUNDSRADIUS, this.boundsRadius);
		}
	}

	@Override
	public String toString() {
		return "MdlxExtent{" + "boundsRadius=" + this.boundsRadius + ", min=" + Arrays.toString(this.min) + ", max="
				+ Arrays.toString(this.max) + '}';
	}

	public float getBoundsRadius() {
		return this.boundsRadius;
	}

	public float[] getMin() {
		return this.min;
	}

	public float[] getMax() {
		return this.max;
	}
}
