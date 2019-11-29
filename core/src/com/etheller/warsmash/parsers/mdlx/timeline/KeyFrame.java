package com.etheller.warsmash.parsers.mdlx.timeline;

import java.io.IOException;

import com.etheller.warsmash.parsers.mdlx.InterpolationType;
import com.etheller.warsmash.parsers.mdlx.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.MdlTokenOutputStream;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public interface KeyFrame {
	void readMdx(LittleEndianDataInputStream stream, InterpolationType interpolationType) throws IOException;

	void writeMdx(LittleEndianDataOutputStream stream, InterpolationType interpolationType) throws IOException;

	void readMdl(MdlTokenInputStream stream, InterpolationType interpolationType) throws IOException;

	void writeMdl(MdlTokenOutputStream stream, InterpolationType interpolationType) throws IOException;

	long getByteLength(InterpolationType interpolationType);

	long getTime();

	boolean matchingValue(KeyFrame other);

	KeyFrame clone(long time);
}
