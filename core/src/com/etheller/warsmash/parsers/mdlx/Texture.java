package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Texture implements MdlxBlock {
	private int replaceableId = 0;
	private String path = "";
	private int flags = 0;

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static final byte[] PATH_BYTES_HEAP = new byte[260];

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		this.replaceableId = (int) ParseUtils.readUInt32(stream);
		this.path = ParseUtils.readString(stream, PATH_BYTES_HEAP);
		this.flags = (int) ParseUtils.readUInt32(stream);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, this.replaceableId);
		final byte[] bytes = this.path.getBytes(ParseUtils.UTF8);
		stream.write(bytes);
		for (int i = 0; i < (PATH_BYTES_HEAP.length - bytes.length); i++) {
			stream.write((byte) 0);
		}
		ParseUtils.writeUInt32(stream, this.flags);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_IMAGE:
				this.path = stream.read();
				break;
			case MdlUtils.TOKEN_REPLACEABLE_ID:
				this.replaceableId = stream.readInt();
				break;
			case MdlUtils.TOKEN_WRAP_WIDTH:
				this.flags |= 0x1;
				break;
			case MdlUtils.TOKEN_WRAP_HEIGHT:
				this.flags |= 0x2;
				break;
			default:
				throw new IllegalStateException("Unknown token in Texture: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(MdlUtils.TOKEN_BITMAP);
		stream.writeStringAttrib(MdlUtils.TOKEN_IMAGE, this.path);

		if (this.replaceableId != 0) {
			stream.writeAttrib(MdlUtils.TOKEN_REPLACEABLE_ID, this.replaceableId);
		}

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_WRAP_WIDTH);
		}

		if ((this.flags & 0x2) != 0) {
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

	public int getFlags() {
		return this.flags;
	}

}
