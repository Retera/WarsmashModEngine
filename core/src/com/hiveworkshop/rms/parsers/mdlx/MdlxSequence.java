package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxSequence implements MdlxBlock {
	public String name = "";
	public long[] interval = new long[2];
	public float moveSpeed = 0;
	public int flags = 0;
	public float rarity = 0;
	public long syncPoint = 0;
	public MdlxExtent extent = new MdlxExtent();

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		this.name = reader.read(80);
		reader.readUInt32Array(this.interval);
		this.moveSpeed = reader.readFloat32();
		this.flags = reader.readInt32();
		this.rarity = reader.readFloat32();
		this.syncPoint = reader.readUInt32();
		this.extent.readMdx(reader);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeWithNulls(this.name, 80);
		writer.writeUInt32Array(this.interval);
		writer.writeFloat32(this.moveSpeed);
		writer.writeUInt32(this.flags);
		writer.writeFloat32(this.rarity);
		writer.writeUInt32(this.syncPoint);
		this.extent.writeMdx(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		this.name = stream.read();

		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_INTERVAL:
				stream.readIntArray(this.interval);
				break;
			case MdlUtils.TOKEN_NONLOOPING:
				this.flags = 1;
				break;
			case MdlUtils.TOKEN_MOVESPEED:
				this.moveSpeed = stream.readFloat();
				break;
			case MdlUtils.TOKEN_RARITY:
				this.rarity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_MINIMUM_EXTENT:
				stream.readFloatArray(this.extent.min);
				break;
			case MdlUtils.TOKEN_MAXIMUM_EXTENT:
				stream.readFloatArray(this.extent.max);
				break;
			case MdlUtils.TOKEN_BOUNDSRADIUS:
				this.extent.boundsRadius = stream.readFloat();
				break;
			default:
				throw new IllegalStateException("Unknown token in Sequence \"" + this.name + "\": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_ANIM, this.name);
		stream.writeArrayAttrib(MdlUtils.TOKEN_INTERVAL, this.interval);

		if (this.flags == 1) {
			stream.writeFlag(MdlUtils.TOKEN_NONLOOPING);
		}

		if (this.moveSpeed != 0) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_MOVESPEED, this.moveSpeed);
		}

		if (this.rarity != 0) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_RARITY, this.rarity);
		}

		this.extent.writeMdl(stream);
		stream.endBlock();
	}

	public String getName() {
		return this.name;
	}

	public long[] getInterval() {
		return this.interval;
	}

	public float getMoveSpeed() {
		return this.moveSpeed;
	}

	public int getFlags() {
		return this.flags;
	}

	public float getRarity() {
		return this.rarity;
	}

	public long getSyncPoint() {
		return this.syncPoint;
	}

	public MdlxExtent getExtent() {
		return this.extent;
	}
}
