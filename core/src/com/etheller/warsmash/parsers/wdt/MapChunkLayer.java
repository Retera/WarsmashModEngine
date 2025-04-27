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
	public void readMdx(BinaryReader reader, int version) {
		textureId = reader.readUInt32();
		props = reader.readUInt32();
		offsAlpha = reader.readUInt32();
		effectId = reader.readInt32();
	}

	@Override
	public void writeMdx(BinaryWriter writer, int version) {
		throw new RuntimeException();
	}

	@Override
	public void readMdl(MdlTokenInputStream stream, int version) {
		throw new RuntimeException();
	}

	@Override
	public void writeMdl(MdlTokenOutputStream stream, int version) {
		throw new RuntimeException();
	}


	@Override
	public long getByteLength(int version) {
		return 4 * 4;
	}

	@Override
	public String toString() {
		return "MapChunkLayer [textureId=" + textureId + ", props=" + props + ", offsAlpha=" + offsAlpha + ", effectId="
				+ effectId + "]";
	}
	

}
