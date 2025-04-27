package com.etheller.warsmash.parsers.wdt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlockDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.parsers.mdlx.MdlxUnknownChunk;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

/**
 * A map.
 */
public class WdtMap {
	private static final int MVER = ('M' << 24) | ('V' << 16) | ('E' << 8) | ('R');// War3ID.fromString("MDLX").getValue();
	private static final int MPHD = ('M' << 24) | ('P' << 16) | ('H' << 8) | ('D');// War3ID.fromString("MDLX").getValue();
	private static final int MAIN = ('M' << 24) | ('A' << 16) | ('I' << 8) | ('N');// War3ID.fromString("MDLX").getValue();
	private static final int MODF = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('F');// War3ID.fromString("MDLX").getValue();
	private static final int MDNM = ('M' << 24) | ('D' << 16) | ('N' << 8) | ('M');// War3ID.fromString("MDLX").getValue();
	private static final int MONM = ('M' << 24) | ('O' << 16) | ('N' << 8) | ('M');// War3ID.fromString("MDLX").getValue();
	private static final int MHDR = ('M' << 24) | ('H' << 16) | ('D' << 8) | ('R');// War3ID.fromString("MDLX").getValue();
	private static final int MCIN = ('M' << 24) | ('C' << 16) | ('I' << 8) | ('N');// War3ID.fromString("MDLX").getValue();
	private static final int MTEX = ('M' << 24) | ('T' << 16) | ('E' << 8) | ('X');// War3ID.fromString("MDLX").getValue();
	private static final int MDDF = ('M' << 24) | ('D' << 16) | ('D' << 8) | ('F');// War3ID.fromString("MDLX").getValue();
	private static final int MCNK = ('M' << 24) | ('C' << 16) | ('N' << 8) | ('K');// War3ID.fromString("MDLX").getValue();

	public int version = 0;
	public List<MdlxUnknownChunk> unknownChunks = new ArrayList<>();
	public int nDoodadNames;
	public int offsDoodadNames;
	public int nMapObjNames;
	public int offsMapObjNames;
	public int[] headerUnused = new int[28];
	public List<SMAreaInfo> smAreaInfos = new ArrayList<>();
	public List<String> worldModelFileNames = new ArrayList<>();
	public List<String> doodadModelFileNames = new ArrayList<>();
	public List<TileHeader> tileHeaders = new ArrayList<>();

	public WdtMap() {

	}

	public WdtMap(final ByteBuffer buffer) {
		load(buffer);
	}

	public void load(final ByteBuffer buffer) {
		// WDT files start with "MVER" probably.
		if ((buffer.get(0) == 'R') && (buffer.get(1) == 'E') && (buffer.get(2) == 'V') && (buffer.get(3) == 'M')) {
			loadWdt(buffer);
		} else {
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

	public void loadWdt(final ByteBuffer buffer) {
		final BinaryReader reader = new BinaryReader(buffer);

		while (reader.remaining() > 0) {
			final int tag = reader.readInt32();
			final int size = reader.readInt32();
			switch (tag) {
			case MVER:
				loadVersionChunk(reader);
				break;
			case MPHD:
				loadHeaderChunk(reader);
				break;
			case MAIN:
//				loadMainChunk(reader); 
				loadNDynamicObjects(this.smAreaInfos, SMAreaInfo::new, reader, size / 16);
				break;
			case MONM:
				readStrings(reader, size, worldModelFileNames);
				break;
			case MDNM:
				readStrings(reader, size, doodadModelFileNames);
				break;
			case MHDR:
				loadTileHeader(reader, size);
				break;
			case MCIN:
				loadNDynamicObjects(getLastTileHeader().chunkInfos, ChunkInfo::new, reader,
						16 * 16);
				break;
			case MTEX:
				readStrings(reader, size, getLastTileHeader().textureFileNames);
				break;
			case MDDF:
				loadDynamicObjects(getLastTileHeader().doodads, DoodadDefinition::new, reader, size);
				break;
			case MODF:
				loadDynamicObjects(getLastTileHeader().mapObjectDefinitions, MapObjectDefinition::new, reader, size);
				break;
			case MCNK:
				Chunk chunk = new Chunk();
				chunk.readMdx(reader, size);
				getLastTileHeader().chunks.add(chunk);
				break;
//			case MODL:
//				loadModelChunk(reader);
//				break;
//			case SEQS: {
//				final int elementCount = this.version == 1300 ? (int) reader.readUInt32() : (size / 132);
//				loadStaticObjects(this.sequences, MdlxBlockDescriptor.SEQUENCE, reader, elementCount);
//				break;
//			}
//			case GLBS:
//				loadGlobalSequenceChunk(reader, size);
//				break;
//			case MTLS: {
//				if (this.version == 1300) {
//					final long numMaterials = reader.readUInt32();
//					reader.readUInt32(); // MTLS extra data
//					loadNDynamicObjects(this.materials, MdlxBlockDescriptor.MATERIAL, reader, numMaterials);
//				}
//				else {
//					loadDynamicObjects(this.materials, MdlxBlockDescriptor.MATERIAL, reader, size);
//				}
//				break;
//			}
//			case TEXS:
//				loadStaticObjects(this.textures, MdlxBlockDescriptor.TEXTURE, reader, size / 268);
//				break;
//			case TXAN:
//				loadDynamicObjects(this.textureAnimations, MdlxBlockDescriptor.TEXTURE_ANIMATION, reader, size);
//				break;
//			case GEOS:
//				if (this.version == 1300) {
//					final long numGeos = reader.readUInt32();
//					loadNDynamicObjects(this.geosets, MdlxBlockDescriptor.GEOSET, reader, numGeos);
//				}
//				else {
//					loadDynamicObjects(this.geosets, MdlxBlockDescriptor.GEOSET, reader, size);
//				}
//				break;
//			case GEOA:
//				if (this.version == 1300) {
//					final long numGeoAs = reader.readUInt32();
//					loadNDynamicObjects(this.geosetAnimations, MdlxBlockDescriptor.GEOSET_ANIMATION, reader, numGeoAs);
//				}
//				else {
//					loadDynamicObjects(this.geosetAnimations, MdlxBlockDescriptor.GEOSET_ANIMATION, reader, size);
//				}
//				break;
//			case BONE:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.bones, MdlxBlockDescriptor.BONE, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.bones, MdlxBlockDescriptor.BONE, reader, size);
//				}
//				break;
//			case LITE:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.lights, MdlxBlockDescriptor.LIGHT, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.lights, MdlxBlockDescriptor.LIGHT, reader, size);
//				}
//				break;
//			case HELP:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.helpers, MdlxBlockDescriptor.HELPER, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.helpers, MdlxBlockDescriptor.HELPER, reader, size);
//				}
//				break;
//			case ATCH:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					final long unused = reader.readUInt32();
//					loadNDynamicObjects(this.attachments, MdlxBlockDescriptor.ATTACHMENT, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.attachments, MdlxBlockDescriptor.ATTACHMENT, reader, size);
//				}
//				break;
//			case PIVT:
//				loadPivotPointChunk(reader, size);
//				break;
//			case PREM:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.particleEmitters, MdlxBlockDescriptor.PARTICLE_EMITTER, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.particleEmitters, MdlxBlockDescriptor.PARTICLE_EMITTER, reader, size);
//				}
//				break;
//			case PRE2:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.particleEmitters2, MdlxBlockDescriptor.PARTICLE_EMITTER2, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.particleEmitters2, MdlxBlockDescriptor.PARTICLE_EMITTER2, reader, size);
//				}
//				break;
//			case CORN:
//				loadDynamicObjects(this.particleEmittersPopcorn, MdlxBlockDescriptor.PARTICLE_EMITTER_POPCORN, reader,
//						size);
//				break;
//			case RIBB:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.ribbonEmitters, MdlxBlockDescriptor.RIBBON_EMITTER, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.ribbonEmitters, MdlxBlockDescriptor.RIBBON_EMITTER, reader, size);
//				}
//				break;
//			case CAMS:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.cameras, MdlxBlockDescriptor.CAMERA, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.cameras, MdlxBlockDescriptor.CAMERA, reader, size);
//				}
//				break;
//			case EVTS:
//				if (this.version == 1300) {
//					final long count = reader.readUInt32();
//					loadNDynamicObjects(this.eventObjects, MdlxBlockDescriptor.EVENT_OBJECT, reader, count);
//				}
//				else {
//					loadDynamicObjects(this.eventObjects, MdlxBlockDescriptor.EVENT_OBJECT, reader, size);
//				}
//				break;
//			case CLID:
//				if (this.version == 1300) {
//					// long count = reader.readUInt32();
//					// System.out.println("CLID count is: " + count);
//					// loadNDynamicObjects(collisionShapes, MdlxBlockDescriptor.COLLISION_SHAPE,
//					// reader, count);
//					this.unknownChunks.add(new MdlxUnknownChunk(reader, size, new War3ID(tag)));
//				}
//				else {
//					loadDynamicObjects(this.collisionShapes, MdlxBlockDescriptor.COLLISION_SHAPE, reader, size);
//				}
//				break;
//			case FAFX:
//				loadStaticObjects(this.faceEffects, MdlxBlockDescriptor.FACE_EFFECT, reader, size / 340);
//				break;
//			case BPOS:
//				loadBindPoseChunk(reader, size);
//				break;
			default:
				this.unknownChunks.add(new MdlxUnknownChunk(reader, size, new War3ID(tag)));
				break;
			}
			System.out.println("read chunk: " + new War3ID(tag));
		}
	}

	private TileHeader getLastTileHeader() {
		return this.tileHeaders.get(tileHeaders.size() - 1);
	}

	public static final class TileHeader {
		private long offsInfo; // MCIN
		private long offsTex; // MTEX
		private long sizeTex;
		private long offsDoo; // MDDF
		private long sizeDoo;
		private long offsMob; // MODF
		private long sizeMob;
		private byte[] pad = new byte[36];

		public List<ChunkInfo> chunkInfos = new ArrayList<>();
		public List<String> textureFileNames = new ArrayList<>();
		public List<DoodadDefinition> doodads = new ArrayList<>();
		public List<MapObjectDefinition> mapObjectDefinitions = new ArrayList<>();
		
		List<Chunk> chunks = new ArrayList<>();

		public void read(BinaryReader reader, int size) {
			offsInfo = reader.readUInt32();
			offsTex = reader.readUInt32();
			sizeTex = reader.readUInt32();
			offsDoo = reader.readUInt32();
			sizeDoo = reader.readUInt32();
			offsMob = reader.readUInt32();
			sizeMob = reader.readUInt32();
			reader.readInt8Array(pad);
		}

	}

	private void loadTileHeader(BinaryReader reader, int size) {
		int start = reader.position();
		TileHeader tileHeader = new TileHeader();
		tileHeader.read(reader, size);
		tileHeaders.add(tileHeader);
		if (start + size != reader.position()) {
			throw new IllegalStateException(start + size + " != " + reader.position());
		}
	}

	private void readStrings(final BinaryReader reader, final int size, List<String> output) {
		int sizeRemaining = size;
		while (sizeRemaining > 0) {
			StringBuilder sb = new StringBuilder();
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

	private void loadHeaderChunk(final BinaryReader reader) {
		this.nDoodadNames = reader.readInt32();
		this.offsDoodadNames = reader.readInt32();
		this.nMapObjNames = reader.readInt32();
		this.offsMapObjNames = reader.readInt32();
		reader.readInt32Array(headerUnused);
	}

	private void loadMainChunk(final BinaryReader reader) {

	}

	private void loadModelChunk(final BinaryReader reader) {
//		this.name = reader.read(80);
//		this.animationFile = reader.read(260);
//		this.extent.readMdx(reader);
//		this.blendTime = reader.readInt32();
//		if (this.version == 1300) {
//			this.flags = reader.readInt8();
//		}
	}

//
//	private <E extends MdlxBlock> void loadStaticObjects(final List<E> out, final MdlxBlockDescriptor<E> constructor,
//			final BinaryReader reader, final long count) {
//		for (int i = 0; i < count; i++) {
//			final E object = constructor.create();
//
//			object.readMdx(reader, this.version);
//
//			out.add(object);
//		}
//	}
//
//	private void loadGlobalSequenceChunk(final BinaryReader reader, final long size) {
//		for (long i = 0, l = size / 4; i < l; i++) {
//			this.globalSequences.add(reader.readUInt32());
//		}
//	}
//
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

	private void loadVersionBlock(final MdlTokenInputStream stream) {
		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_FORMAT_VERSION:
				this.version = stream.readInt();
				break;
			default:
				throw new IllegalStateException("Unknown token in Version: " + token);
			}
		}
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

}
