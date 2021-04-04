package com.hiveworkshop.rms.parsers.mdlx;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxMaterial implements MdlxBlock, MdlxChunk {
	public static final War3ID LAYS = War3ID.fromString("LAYS");
	public int priorityPlane = 0;
	public int flags;
	/**
	 * @since 900
	 */
	public String shader = "";
	public final List<MdlxLayer> layers = new ArrayList<>();

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		reader.readUInt32(); // Don't care about the size

		this.priorityPlane = reader.readInt32();
		this.flags = reader.readInt32();

		if (version > 800) {
			this.shader = reader.read(80);
		}

		reader.readInt32(); // skip LAYS

		final long layerCount = reader.readUInt32();
		for (int i = 0; i < layerCount; i++) {
			final MdlxLayer layer = new MdlxLayer();
			layer.readMdx(reader, version);
			this.layers.add(layer);
		}
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeInt32(this.priorityPlane);
		writer.writeInt32(this.flags);

		if (version > 800) {
			writer.writeWithNulls(this.shader, 80);
		}

		writer.writeTag(LAYS.getValue());
		writer.writeUInt32(this.layers.size());

		for (final MdlxLayer layer : this.layers) {
			layer.writeMdx(writer, version);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
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
			case "Shader":
				this.shader = stream.read();
				break;
			case MdlUtils.TOKEN_LAYER: {
				final MdlxLayer layer = new MdlxLayer();
				layer.readMdl(stream, version);
				this.layers.add(layer);
			}
				break;
			default:
				throw new RuntimeException("Unknown token in Material: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
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

		if (version > 800) {
			stream.writeStringAttrib("Shader", this.shader);
		}

		for (final MdlxLayer layer : this.layers) {
			layer.writeMdl(stream, version);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		long size = 20;

		if (version > 800) {
			size += 80;
		}

		for (final MdlxLayer layer : this.layers) {
			size += layer.getByteLength(version);
		}

		return size;
	}

	public int getPriorityPlane() {
		return this.priorityPlane;
	}

	public int getFlags() {
		return this.flags;
	}

	public String getShader() {
		return this.shader;
	}

	public List<MdlxLayer> getLayers() {
		return this.layers;
	}

	public void setPriorityPlane(final int priorityPlane) {
		this.priorityPlane = priorityPlane;
	}

	public void setFlags(final int flags) {
		this.flags = flags;
	}

	public void setShader(final String shader) {
		this.shader = shader;
	}
}
