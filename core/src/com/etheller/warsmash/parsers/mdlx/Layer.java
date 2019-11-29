package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.Iterator;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Layer extends AnimatedObject {
	// 0: none
	// 1: transparent
	// 2: blend
	// 3: additive
	// 4: add alpha
	// 5: modulate
	// 6: modulate 2x
	public static enum FilterMode {
		NONE("None"),
		TRANSPARENT("Transparent"),
		BLEND("Blend"),
		ADDITIVE("Additive"),
		ADDALPHA("AddAlpha"),
		MODULATE("Modulate"),
		MODULATE2X("Modulate2x");

		String mdlText;

		FilterMode(final String str) {
			this.mdlText = str;
		}

		public String getMdlText() {
			return this.mdlText;
		}

		public static FilterMode fromId(final int id) {
			return values()[id];
		}

		public static int nameToId(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.getMdlText().equals(name)) {
					return mode.ordinal();
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return getMdlText();
		}
	}

	private FilterMode filterMode;
	private int flags = 0;
	private int textureId = -1;
	private int textureAnimationId = -1;
	private long coordId = 0;
	private float alpha = 1;

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		this.filterMode = FilterMode.fromId((int) ParseUtils.readUInt32(stream));
		this.flags = stream.readInt(); // UInt32 in JS
		this.textureId = stream.readInt();
		this.textureAnimationId = stream.readInt();
		this.coordId = ParseUtils.readUInt32(stream);
		this.alpha = stream.readFloat();

		readTimelines(stream, size - 28);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());
		ParseUtils.writeUInt32(stream, this.filterMode.ordinal());
		ParseUtils.writeUInt32(stream, this.flags);
		stream.writeInt(this.textureId);
		stream.writeInt(this.textureAnimationId);
		ParseUtils.writeUInt32(stream, this.coordId);
		stream.writeFloat(this.alpha);

		writeTimelines(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		final Iterator<String> iterator = readAnimatedBlock(stream);
		while (iterator.hasNext()) {
			final String token = iterator.next();
			switch (token) {
			case MdlUtils.TOKEN_FILTER_MODE:
				this.filterMode = FilterMode.fromId(FilterMode.nameToId(stream.read()));
				break;
			case MdlUtils.TOKEN_UNSHADED:
				this.flags |= 0x1;
				break;
			case MdlUtils.TOKEN_SPHERE_ENV_MAP:
				this.flags |= 0x2;
				break;
			case MdlUtils.TOKEN_TWO_SIDED:
				this.flags |= 0x10;
				break;
			case MdlUtils.TOKEN_UNFOGGED:
				this.flags |= 0x20;
				break;
			case MdlUtils.TOKEN_NO_DEPTH_TEST:
				this.flags |= 0x40;
				break;
			case MdlUtils.TOKEN_NO_DEPTH_SET:
				this.flags |= 0x100;
				break;
			case MdlUtils.TOKEN_STATIC_TEXTURE_ID:
				this.textureId = stream.readInt();
				break;
			case MdlUtils.TOKEN_TEXTURE_ID:
				readTimeline(stream, AnimationMap.KMTF);
				break;
			case MdlUtils.TOKEN_TVERTEX_ANIM_ID:
				this.textureAnimationId = stream.readInt();
				break;
			case MdlUtils.TOKEN_COORD_ID:
				this.coordId = stream.readInt();
				break;
			case MdlUtils.TOKEN_STATIC_ALPHA:
				this.alpha = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ALPHA:
				readTimeline(stream, AnimationMap.KMTA);
				break;
			default:
				throw new IllegalStateException("Unknown token in Layer: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(MdlUtils.TOKEN_LAYER);

		stream.writeAttrib(MdlUtils.TOKEN_FILTER_MODE, this.filterMode.getMdlText());

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNSHADED);
		}

		if ((this.flags & 0x2) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SPHERE_ENV_MAP);
		}

		if ((this.flags & 0x10) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_TWO_SIDED);
		}

		if ((this.flags & 0x20) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNFOGGED);
		}

		if ((this.flags & 0x40) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_NO_DEPTH_TEST);
		}

		if ((this.flags & 0x100) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_NO_DEPTH_SET);
		}

		if (!writeTimeline(stream, AnimationMap.KMTF)) {
			stream.writeAttrib(MdlUtils.TOKEN_STATIC_TEXTURE_ID, this.textureId);
		}

		if (this.textureAnimationId != -1) {
			stream.writeAttrib(MdlUtils.TOKEN_TVERTEX_ANIM_ID, this.textureAnimationId);
		}

		if (this.coordId != 0) {
			stream.writeAttribUInt32(MdlUtils.TOKEN_COORD_ID, this.coordId);
		}

		if (!writeTimeline(stream, AnimationMap.KMTA) && (this.alpha != 1)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ALPHA, this.alpha);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 28 + super.getByteLength();
	}

	public FilterMode getFilterMode() {
		return filterMode;
	}

	public int getFlags() {
		return flags;
	}

	public int getTextureId() {
		return textureId;
	}

	public int getTextureAnimationId() {
		return textureAnimationId;
	}

	public long getCoordId() {
		return coordId;
	}

	public float getAlpha() {
		return alpha;
	}

}
