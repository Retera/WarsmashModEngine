package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxTexture implements MdlxBlock {
	public enum WrapMode {
		REPEAT_BOTH(false, false), WRAP_WIDTH(true, false), WRAP_HEIGHT(false, true), WRAP_BOTH(true, true);

		private final boolean wrapWidth;
		private final boolean wrapHeight;

		public static WrapMode fromId(final int id) {
			return values()[id];
		}

		private WrapMode(final boolean wrapWidth, final boolean wrapHeight) {
			this.wrapWidth = wrapWidth;
			this.wrapHeight = wrapHeight;
		}

		public boolean isWrapWidth() {
			return this.wrapWidth;
		}

		public boolean isWrapHeight() {
			return this.wrapHeight;
		}
	}

	public int replaceableId = 0;
	public String path = "";
	public WrapMode wrapMode = WrapMode.REPEAT_BOTH;
	public boolean reforged = false;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.replaceableId = reader.readInt32();
		this.path = reader.read(260);
		this.wrapMode = WrapMode.fromId(reader.readInt32());
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeInt32(this.replaceableId);
		writer.writeWithNulls(this.path, 260);
		writer.writeInt32(this.wrapMode.ordinal());
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_IMAGE:
				this.path = stream.read();
				break;
			case MdlUtils.TOKEN_REPLACEABLE_ID:
				this.replaceableId = stream.readInt();
				break;
			case MdlUtils.TOKEN_WRAP_WIDTH:
				this.wrapMode = WrapMode.fromId(this.wrapMode.ordinal() + 0x1);
				break;
			case MdlUtils.TOKEN_WRAP_HEIGHT:
				this.wrapMode = WrapMode.fromId(this.wrapMode.ordinal() + 0x2);
				break;
			default:
				throw new RuntimeException("Unknown token in Texture: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_BITMAP);
		stream.writeStringAttrib(MdlUtils.TOKEN_IMAGE, this.path);

		if (this.replaceableId != 0) {
			stream.writeAttrib(MdlUtils.TOKEN_REPLACEABLE_ID, this.replaceableId);
		}

		if ((this.wrapMode == WrapMode.WRAP_WIDTH) || (this.wrapMode == WrapMode.WRAP_BOTH)) {
			stream.writeFlag(MdlUtils.TOKEN_WRAP_WIDTH);
		}

		if ((this.wrapMode == WrapMode.WRAP_HEIGHT) || (this.wrapMode == WrapMode.WRAP_BOTH)) {
			stream.writeFlag(MdlUtils.TOKEN_WRAP_HEIGHT);
		}

		stream.endBlock();
	}

	public int getReplaceableId() {
		return this.replaceableId;
	}

	public String getPath() {
		return this.path;
	}

	public WrapMode getWrapMode() {
		return this.wrapMode;
	}

	public void setReplaceableId(final int replaceableId) {
		this.replaceableId = replaceableId;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setWrapMode(final WrapMode wrapMode) {
		this.wrapMode = wrapMode;
	}
}
