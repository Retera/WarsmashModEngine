package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Material implements MdlxBlock, Chunk {
	private static final War3ID LAYS = War3ID.fromString("LAYS");
	private int priorityPlane = 0;
	private int flags;
	private final List<Layer> layers = new ArrayList<>();

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		ParseUtils.readUInt32(stream); // Don't care about the size

		this.priorityPlane = stream.readInt();// ParseUtils.readUInt32(stream);
		this.flags = stream.readInt();// ParseUtils.readUInt32(stream);

		stream.readInt(); // skip LAYS

		final long layerCount = ParseUtils.readUInt32(stream);
		for (int i = 0; i < layerCount; i++) {
			final Layer layer = new Layer();
			layer.readMdx(stream);
			this.layers.add(layer);
		}
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());
		stream.writeInt(this.priorityPlane); // was UInt32 in JS, but I *really* thought I used -1 in a past model
		stream.writeInt(this.flags); // UInt32 in JS
		ParseUtils.writeWar3ID(stream, LAYS);
		ParseUtils.writeUInt32(stream, this.layers.size());

		for (final Layer layer : this.layers) {
			layer.writeMdx(stream);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_CONSTANT_COLOR:
				this.flags |= 0x1;
				break;
			case MdlUtils.TOKEN_SORT_PRIMS_NEAR_Z:
				this.flags |= 0x8;
				break;
			case MdlUtils.TOKEN_SORT_PRIMS_FAR_Z:
				this.flags |= 0x10;
				break;
			case MdlUtils.TOKEN_FULL_RESOLUTION:
				this.flags |= 0x20;
				break;
			case MdlUtils.TOKEN_PRIORITY_PLANE:
				this.priorityPlane = stream.readInt();
				break;
			case MdlUtils.TOKEN_LAYER: {
				final Layer layer = new Layer();
				layer.readMdl(stream);
				this.layers.add(layer);
				break;
			}
			default:
				throw new IllegalStateException("Unknown token in Material: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(MdlUtils.TOKEN_MATERIAL);

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_CONSTANT_COLOR);
		}

		if ((this.flags & 0x8) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SORT_PRIMS_NEAR_Z);
		}

		if ((this.flags & 0x10) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SORT_PRIMS_FAR_Z);
		}

		if ((this.flags & 0x20) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_FULL_RESOLUTION);
		}

		if (this.priorityPlane != 0) {
			stream.writeAttrib(MdlUtils.TOKEN_PRIORITY_PLANE, this.priorityPlane);
		}

		for (final Layer layer : this.layers) {
			layer.writeMdl(stream);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		long size = 20;

		for (final Layer layer : this.layers) {
			size += layer.getByteLength();
		}

		return size;
	}
}
