package com.hiveworkshop.rms.parsers.mdlx;

import java.util.Iterator;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxGeosetAnimation extends MdlxAnimatedObject {
	public float alpha = 1;
	public int flags = 0;
	public float[] color = { 1, 1, 1 };
	public int geosetId = -1;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long size = reader.readUInt32();

		this.alpha = reader.readFloat32();
		this.flags = reader.readInt32();
		reader.readFloat32Array(this.color);
		this.geosetId = reader.readInt32();

		readTimelines(reader, size - 28);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeFloat32(this.alpha);
		writer.writeInt32(this.flags);
		writer.writeFloat32Array(this.color);
		writer.writeInt32(this.geosetId);

		writeTimelines(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
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
				readTimeline(stream, AnimationMap.KGAO);
				break;
			case MdlUtils.TOKEN_STATIC_COLOR: {
				this.flags |= 0x2;
				stream.readColor(this.color);
			}
				break;
			case MdlUtils.TOKEN_COLOR: {
				this.flags |= 0x2;
				readTimeline(stream, AnimationMap.KGAC);
			}
				break;
			case MdlUtils.TOKEN_GEOSETID:
				this.geosetId = stream.readInt();
				break;
			default:
				throw new RuntimeException("Unknown token in GeosetAnimation: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_GEOSETANIM);

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DROP_SHADOW);
		}

		if (!writeTimeline(stream, AnimationMap.KGAO)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ALPHA, this.alpha);
		}

		if ((this.flags & 0x2) != 0) {
			if (!writeTimeline(stream, AnimationMap.KGAC)
					&& ((this.color[0] != 0) || (this.color[1] != 0) || (this.color[2] != 0))) {
				stream.writeColor(MdlUtils.TOKEN_STATIC_COLOR, this.color);
			}
		}

		if (this.geosetId != -1) { // TODO Retera added -1 check here, why wasn't it there before in JS???
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETID, this.geosetId);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 28 + super.getByteLength(version);
	}

	public float getAlpha() {
		return this.alpha;
	}

	public int getFlags() {
		return this.flags;
	}

	public float[] getColor() {
		return this.color;
	}

	public int getGeosetId() {
		return this.geosetId;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	public void setFlags(final int flags) {
		this.flags = flags;
	}

	public void setColor(final float[] color) {
		this.color = color;
	}

	public void setGeosetId(final int geosetId) {
		this.geosetId = geosetId;
	}
}
