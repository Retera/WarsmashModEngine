package com.etheller.warsmash.parsers.wmo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlockDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.MdlxUnknownChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.util.BinaryReader;

/**
 * A world model object.
 */
public class WorldModelObject {
	private static final int MVER = ('M' << 24) | ('V' << 16) | ('E' << 8) | ('R');// War3ID.fromString("MDLX").getValue();
	private static final int MOMO = ('M' << 24) | ('O' << 16) | ('M' << 8) | ('O');// War3ID.fromString("MDLX").getValue();
	private static final int MOGP = ('M' << 24) | ('O' << 16) | ('G' << 8) | ('P');// War3ID.fromString("MDLX").getValue();
	public static final String EXTENSION = ".wmo";

	public int version = 0;
	private ModelObjectHeaders headers;
	private final List<ModelObjectGroup> groups = new ArrayList<>();
	public List<MdlxUnknownChunk> unknownChunks = new ArrayList<>();

	public WorldModelObject() {

	}

	public WorldModelObject(final ByteBuffer buffer) {
		load(buffer);
	}

	public void load(final ByteBuffer buffer) {
		// WDT files start with "MVER" probably.
		if ((buffer.get(0) == 'R') && (buffer.get(1) == 'E') && (buffer.get(2) == 'V') && (buffer.get(3) == 'M')) {
			loadWmo(buffer);
		}
		else {
			loadMdl(buffer);
		}
	}

	public static String convertInt(final int tag) {
		return "" + (char) ((tag >> 24) & 0xFF) + (char) ((tag >> 16) & 0xFF) + (char) ((tag >> 8) & 0xFF)
				+ (char) ((tag >> 0) & 0xFF);
	}

	public static String convertInt2(final int tag) {
		return "" + (char) ((tag >> 0) & 0xFF) + (char) ((tag >> 8) & 0xFF) + (char) ((tag >> 16) & 0xFF)
				+ (char) ((tag >> 24) & 0xFF);
	}

	public void loadWmo(final ByteBuffer buffer) {
		final BinaryReader reader = new BinaryReader(buffer);

		final Map<War3ID, Integer> chunkToCount = new HashMap<>();
		while (reader.remaining() > 0) {
			final int tag = reader.readInt32();
			final int size = reader.readInt32();
			final War3ID readTag = new War3ID(tag);
			System.out.println("WMO parsing: " + readTag + "," + size);
			switch (tag) {
			case MVER:
				loadVersionChunk(reader);
				break;
			case MOMO:
				final ModelObjectHeaders headers = new ModelObjectHeaders();
				headers.readMdx(reader, size, this.version);
				this.headers = headers;
				break;
			case MOGP:
				final ModelObjectGroup group = new ModelObjectGroup();
				reader.position(reader.position() - 8);
				group.readMdx(reader, size + 8, this.version);
				this.groups.add(group);
				break;
			default:
				this.unknownChunks.add(new MdlxUnknownChunk(reader, size, readTag));
				break;
			}
			Integer count = chunkToCount.get(readTag);
			if (count == null) {
				count = 0;
			}
			count++;
			chunkToCount.put(readTag, count);
		}
	}

	private void readStrings(final BinaryReader reader, final int size, final List<String> output) {
		int sizeRemaining = size;
		while (sizeRemaining > 0) {
			final StringBuilder sb = new StringBuilder();
			byte x;
			while ((x = reader.readInt8()) != 0) {
				sb.append((char) x);
				sizeRemaining--;
			}
			sizeRemaining--;
			output.add(sb.toString());
		}
	}

	private void loadVersionChunk(final BinaryReader reader) {
		this.version = reader.readInt32();
	}

	private <E extends MdlxBlock & MdlxChunk> void loadDynamicObjects(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final BinaryReader reader, final long size) {
		long totalSize = 0;
		while (totalSize < size) {
			final E object = constructor.create();

			object.readMdx(reader, this.version);

			totalSize += object.getByteLength(this.version);

			out.add(object);
		}
	}

	private <E extends MdlxBlock & MdlxChunk> void loadNDynamicObjects(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final BinaryReader reader, final long count) {

		while (out.size() < count) {
			final E object = constructor.create();

			object.readMdx(reader, this.version);

			out.add(object);
		}
	}

	public void loadMdl(final ByteBuffer buffer) {
		throw new IllegalStateException("did not see MVER");
	}

	private <E extends MdlxBlock> void loadObject(final List<E> out, final MdlxBlockDescriptor<E> descriptor,
			final MdlTokenInputStream stream) {
		final E object = descriptor.create();

		object.readMdl(stream, this.version);

		out.add(object);
	}

	private <E extends MdlxChunk> long getObjectsByteLength(final List<E> objects) {
		long size = 0;
		for (final E object : objects) {
			size += object.getByteLength(this.version);
		}
		return size;
	}

	private <E extends MdlxChunk> long getDynamicObjectsChunkByteLength(final List<E> objects) {
		if (!objects.isEmpty()) {
			return 8 + getObjectsByteLength(objects);
		}

		return 0;
	}

	private <E> long getStaticObjectsChunkByteLength(final List<E> objects, final long size) {
		if (!objects.isEmpty()) {
			return 8 + (objects.size() * size);
		}

		return 0;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public ModelObjectHeaders getHeaders() {
		return this.headers;
	}

	public List<ModelObjectGroup> getGroups() {
		return this.groups;
	}

	public List<MdlxUnknownChunk> getUnknownChunks() {
		return this.unknownChunks;
	}

}
