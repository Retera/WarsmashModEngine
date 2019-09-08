package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class TextureAnimation extends AnimatedObject {

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);
		this.readTimelines(stream, size - 4);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.getByteLength());
		this.writeTimelines(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_TRANSLATION:
				this.readTimeline(stream, AnimationMap.KTAT);
				break;
			case MdlUtils.TOKEN_ROTATION:
				this.readTimeline(stream, AnimationMap.KTAR);
				break;
			case MdlUtils.TOKEN_SCALING:
				this.readTimeline(stream, AnimationMap.KTAS);
				break;
			default:
				throw new IllegalStateException("Unknown token in TextureAnimation: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(MdlUtils.TOKEN_TVERTEX_ANIM_SPACE);
		this.writeTimeline(stream, AnimationMap.KTAT);
		this.writeTimeline(stream, AnimationMap.KTAR);
		this.writeTimeline(stream, AnimationMap.KTAS);
		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 4 + super.getByteLength();
	}

}
