package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Extent {
	protected float boundsRadius = 0;
	protected final float[] min = new float[3];
	protected final float[] max = new float[3];

	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		this.boundsRadius = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.min);
		ParseUtils.readFloatArray(stream, this.max);
	}

	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeFloat(this.boundsRadius);
		ParseUtils.writeFloatArray(stream, this.min);
		ParseUtils.writeFloatArray(stream, this.max);
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

	public void setBoundsRadius(final float boundsRadius) {
		this.boundsRadius = boundsRadius;
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
