package com.etheller.warsmash.parsers.wdt;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MapChunkLayer implements MdlxBlock, MdlxChunk {
	private long textureId;
	private long props;
	private long offsAlpha;
	private int effectId;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.textureId = reader.readUInt32();
		this.props = reader.readUInt32();
		this.offsAlpha = reader.readUInt32();
		this.effectId = reader.readInt32();
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		throw new RuntimeException();
	}

	@Override
	public long getByteLength(final int version) {
		return 4 * 4;
	}

	@Override
	public String toString() {
		return "MapChunkLayer [textureId=" + this.textureId + ", props=" + this.props + ", offsAlpha=" + this.offsAlpha
				+ ", effectId=" + this.effectId + "]";
	}

	public long getTextureId() {
		return this.textureId;
	}

	public long getProps() {
		return this.props;
	}

	public long getOffsAlpha() {
		return this.offsAlpha;
	}

	public int getEffectId() {
		return this.effectId;
	}

}
