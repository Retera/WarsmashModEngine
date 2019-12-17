package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.Iterator;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class GeosetAnimation extends AnimatedObject {
	private float alpha = 1;
	private int flags = 0;
	private final float[] color = { 1, 1, 1 };
	private int geosetId = -1;

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		this.alpha = stream.readFloat();
		this.flags = stream.readInt();// ParseUtils.readUInt32(stream);
		ParseUtils.readFloatArray(stream, this.color);
		this.geosetId = stream.readInt();

		readTimelines(stream, size - 28);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());
		stream.writeFloat(this.alpha);
		stream.writeInt(this.flags);// ParseUtils.writeUInt32(stream, this.flags);
		ParseUtils.writeFloatArray(stream, this.color);
		stream.writeInt(this.geosetId);

		writeTimelines(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		final Iterator<String> blockIterator = readAnimatedBlock(stream);
		while (blockIterator.hasNext()) {
			final String token = blockIterator.next();
			switch (token) {
			case MdlUtils.TOKEN_DROP_SHADOW:
				this.flags |= 0x1;
				break;
			case MdlUtils.TOKEN_STATIC_ALPHA:
				this.alpha = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ALPHA:
				this.readTimeline(stream, AnimationMap.KGAO);
				break;
			case MdlUtils.TOKEN_STATIC_COLOR:
				this.flags |= 0x2;
				stream.readColor(this.color);
				break;
			case MdlUtils.TOKEN_COLOR:
				this.flags |= 0x2;
				readTimeline(stream, AnimationMap.KGAC);
				break;
			case MdlUtils.TOKEN_GEOSETID:
				this.geosetId = stream.readInt();
				break;
			default:
				throw new IllegalStateException("Unknown token in GeosetAnimation: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(MdlUtils.TOKEN_GEOSETANIM);

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DROP_SHADOW);
		}

		if (!this.writeTimeline(stream, AnimationMap.KGAO)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ALPHA, this.alpha);
		}

		if ((this.flags & 0x2) != 0) {
			if (!this.writeTimeline(stream, AnimationMap.KGAC)
					&& ((this.color[0] != 0) || (this.color[1] != 0) || (this.color[2] != 0))) {
				stream.writeColor(MdlUtils.TOKEN_STATIC_COLOR + " ", this.color); // TODO why the space?
			}
		}

		if (this.geosetId != -1) { // TODO Retera added -1 check here, why wasn't it there before in JS???
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETID, this.geosetId);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 28 + super.getByteLength();
	}

	public float[] getColor() {
		return this.color;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public int getGeosetId() {
		return this.geosetId;
	}
}
