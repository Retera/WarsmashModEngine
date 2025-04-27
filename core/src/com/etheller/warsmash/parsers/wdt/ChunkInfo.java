package com.etheller.warsmash.parsers.wdt;

import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class ChunkInfo implements MdlxBlock, MdlxChunk {
	private long offset;
	private long size;
	private long flags;
	private long asyncId;

	@Override
	public void readMdx(BinaryReader reader, int version) {
		offset = reader.readUInt32();
		size = reader.readUInt32();
		flags = reader.readUInt32();
		asyncId = reader.readUInt32();
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

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public void setFlags(long flags) {
		this.flags = flags;
	}
	
	public void setAsyncId(long asyncId) {
		this.asyncId = asyncId;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public long getSize() {
		return size;
	}
	
	public long getFlags() {
		return flags;
	}
	
	public long getAsyncId() {
		return asyncId;
	}

	@Override
	public long getByteLength(int version) {
		return 4 * 4;
	}

	@Override
	public String toString() {
		return "ChunkInfo [offset=" + offset + ", size=" + size + ", flags=" + flags + ", asyncId=" + asyncId + "]";
	}

}
