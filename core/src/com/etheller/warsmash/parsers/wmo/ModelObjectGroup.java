package com.etheller.warsmash.parsers.wmo;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlockDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.util.BinaryReader;

public class ModelObjectGroup {
	private static final int MVER = ('M' << 24) | ('V' << 16) | ('E' << 8) | ('R');
	private static final int MOGP = ('M' << 24) | ('O' << 16) | ('G' << 8) | ('P');
	private static final int MOPY = ('M' << 24) | ('O' << 16) | ('P' << 8) | ('Y');
	private static final int MOVI = ('M' << 24) | ('O' << 16) | ('V' << 8) | ('I');
	private static final int MOIN = ('M' << 24) | ('O' << 16) | ('I' << 8) | ('N');
	private static final int MOVT = ('M' << 24) | ('O' << 16) | ('V' << 8) | ('T');
	private static final int MONR = ('M' << 24) | ('O' << 16) | ('N' << 8) | ('R');
	private static final int MOTV = ('M' << 24) | ('O' << 16) | ('T' << 8) | ('V');
	private static final int MOLV = ('M' << 24) | ('O' << 16) | ('L' << 8) | ('V');
	private static final int MOBA = ('M' << 24) | ('O' << 16) | ('B' << 8) | ('A');
	private static final int MOLR = ('M' << 24) | ('O' << 16) | ('L' << 8) | ('R');
	private static final int MODR = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('R');
	private static final int MOBN = ('M' << 24) | ('O' << 16) | ('B' << 8) | ('N');
	private static final int MOBR = ('M' << 24) | ('O' << 16) | ('B' << 8) | ('R');
	private static final int MOCV = ('M' << 24) | ('O' << 16) | ('C' << 8) | ('V');
	private static final int MLIQ = ('M' << 24) | ('L' << 16) | ('I' << 8) | ('Q');
	private static final int MORI = ('M' << 24) | ('O' << 16) | ('R' << 8) | ('I');
	private static final int MOLM = ('M' << 24) | ('O' << 16) | ('L' << 8) | ('M');
	private static final int MOLD = ('M' << 24) | ('O' << 16) | ('L' << 8) | ('D');
	private static final int MPBV = ('M' << 24) | ('P' << 16) | ('B' << 8) | ('V');
	private static final int MPBP = ('M' << 24) | ('P' << 16) | ('B' << 8) | ('P');
	private static final int MPBI = ('M' << 24) | ('P' << 16) | ('B' << 8) | ('I');
	private static final int MPBG = ('M' << 24) | ('P' << 16) | ('B' << 8) | ('G');
	private static final long ALPHA_VERSION = 14;

	private long version;
	private long groupName;
	private long dbgName;
	private int flags;
	public float[] boundingBoxMin = new float[3];
	public float[] boundingBoxMax = new float[3];
	private int portalStart;
	private long portalCount;
	private byte[] fogIds;
	private long groupLiquid;
	private final List<GroupGxBatch> intBatch = new ArrayList<>();
	private final List<GroupGxBatch> extBatch = new ArrayList<>();
	private int uniqueId;

	private final List<GroupPolygon> polygons = new ArrayList<>();
	private int[] vertexIndices;
	private float[] vertices;
	private float[] normals;
	private float[] textureVertices;
	private float[] lightMapVertices;
	private final List<GroupBatch> batches = new ArrayList<>();
	private int[] lightReferences;
	private int[] doodadReferences;
	private final List<GroupBSPNode> bspNodes = new ArrayList<>();
	private int[] bspFaceIndices;
	private int[] vertexColors;
	private final List<GroupLiquid> liquids = new ArrayList<>();
	private int[] triangleStripIndices;
	private final List<GroupRect> lightMaps = new ArrayList<>();
	private final List<GroupLightMapTexels> lightMapTexels = new ArrayList<>();
	private final List<GroupMPB> mpbv = new ArrayList<>();
	private final List<GroupMPB> mpbp = new ArrayList<>();
	private int[] mbpIndices;
	private float[] mpbg;

	public void readMdx(final BinaryReader reader, final int size, final int version) {
		final long relativeStart = reader.position();
		final long relativeEnd = relativeStart + size;

		readChunkContents(reader, relativeStart, relativeEnd, version);

		reader.position((int) relativeEnd);
	}

	private void readChunkContents(final BinaryReader reader, final long relativeStart, final long relativeEnd,
			final int version) {
		final boolean isAlpha = true;

		while (reader.position() < relativeEnd) {
			final int tag = reader.readInt32();
			final int size = reader.readInt32();
			final War3ID readTag = new War3ID(tag);
			System.out.println("MOGP parsing: " + readTag + "," + size);

			switch (readTag.getValue()) {
			case MVER:
				this.version = reader.readUInt32();
				break;
			case MOGP:
				if (this.version == 0) {
					this.version = ALPHA_VERSION;
				}
				loadHeaderChunk(reader, version);
				break;
			case MOPY:
				// NOTE fixed size
				loadDynamicObjects(this.polygons, GroupPolygon::new, reader, size, version);
				break;
			case MOVI:
			case MOIN:
				this.vertexIndices = reader.readUInt16Array(size / 2);
				break;
			case MOVT:
				this.vertices = reader.readFloat32Array(size / 4);
				break;
			case MONR:
				this.normals = reader.readFloat32Array(size / 4);
				break;
			case MOTV:
				this.textureVertices = reader.readFloat32Array(size / 4);
				break;
			case MOLV:
				this.lightMapVertices = reader.readFloat32Array(size / 4);
				break;
			case MOBA:
				loadDynamicObjects(this.batches, GroupBatch::new, reader, size, version);
				break;
			case MOLR:
				this.lightReferences = reader.readUInt16Array(size / 2);
				break;
			case MODR:
				this.doodadReferences = reader.readUInt16Array(size / 2);
				break;
			case MOBN:
				loadDynamicObjects(this.bspNodes, GroupBSPNode::new, reader, size, version);
				break;
			case MOBR:
				this.bspFaceIndices = reader.readUInt16Array(size / 2);
				break;
			case MOCV:
				this.vertexColors = reader.readInt32Array(size / 4);
				break;
			case MLIQ:
				loadDynamicObjects(this.liquids, GroupLiquid::new, reader, size, version);
				break;
			case MORI:
				this.triangleStripIndices = reader.readUInt16Array(size / 2);
				break;
			case MOLM:
				loadDynamicObjects(this.lightMaps, GroupRect::new, reader, size, version);
				break;
			case MOLD:
				loadDynamicObjects(this.lightMapTexels, GroupLightMapTexels::new, reader, size, version);
				break;
			case MPBV:
				loadDynamicObjects(this.mpbv, GroupMPB::new, reader, size, version);
				break;
			case MPBP:
				loadDynamicObjects(this.mpbp, GroupMPB::new, reader, size, version);
				break;
			case MPBI:
				this.mbpIndices = reader.readUInt16Array(size / 2);
				break;
			case MPBG:
				this.mpbg = reader.readFloat32Array(size / 4);
				break;
			default:
				System.out.println("MOGP unknown tag, continuing");
				reader.position(reader.position() + size);
				break;
			}
		}
	}

	private void loadHeaderChunk(final BinaryReader reader, final int version) {
		this.groupName = reader.readUInt32();
		this.dbgName = reader.readUInt32();
		this.flags = reader.readInt32();
		reader.readFloat32Array(this.boundingBoxMin);
		reader.readFloat32Array(this.boundingBoxMax);
		this.portalStart = (this.version == 14) ? reader.readInt32() : reader.readInt16();
		this.portalCount = (this.version == 14) ? reader.readUInt32() : reader.readUInt16();

		if (version != 14) {
			throw new IllegalStateException("unsupported version: " + version);
		}
		this.fogIds = reader.readInt8Array(4);
		this.groupLiquid = reader.readUInt32();

		if (version == 14) {
			loadNDynamicObjects(this.intBatch, GroupGxBatch::new, reader, 4, (int) this.version);
			loadNDynamicObjects(this.extBatch, GroupGxBatch::new, reader, 4, (int) this.version);
		}
		this.uniqueId = reader.readInt32();

		if (version != 14) {
			throw new IllegalStateException("unsupported version: " + version);
		}
		else {
			reader.position(reader.position() + 8); // skip bytes
		}

	}

	private <E extends MdlxBlock & MdlxChunk> void loadDynamicObjects(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final BinaryReader reader, final long size, final int version) {
		long totalSize = 0;
		while (totalSize < size) {
			final E object = constructor.create();

			object.readMdx(reader, version);

			totalSize += object.getByteLength(version);

			out.add(object);
		}
	}

	private <E extends MdlxBlock> void loadNDynamicObjects(final List<E> out, final MdlxBlockDescriptor<E> constructor,
			final BinaryReader reader, final long count, final int version) {

		while (out.size() < count) {
			final E object = constructor.create();

			object.readMdx(reader, version);

			out.add(object);
		}
	}

	public long getVersion() {
		return this.version;
	}

	public long getGroupName() {
		return this.groupName;
	}

	public long getDbgName() {
		return this.dbgName;
	}

	public int getFlags() {
		return this.flags;
	}

	public float[] getBoundingBoxMin() {
		return this.boundingBoxMin;
	}

	public float[] getBoundingBoxMax() {
		return this.boundingBoxMax;
	}

	public int getPortalStart() {
		return this.portalStart;
	}

	public long getPortalCount() {
		return this.portalCount;
	}

	public byte[] getFogIds() {
		return this.fogIds;
	}

	public long getGroupLiquid() {
		return this.groupLiquid;
	}

	public List<GroupGxBatch> getIntBatch() {
		return this.intBatch;
	}

	public List<GroupGxBatch> getExtBatch() {
		return this.extBatch;
	}

	public int getUniqueId() {
		return this.uniqueId;
	}

	public List<GroupPolygon> getPolygons() {
		return this.polygons;
	}

	public int[] getVertexIndices() {
		return this.vertexIndices;
	}

	public float[] getVertices() {
		return this.vertices;
	}

	public float[] getNormals() {
		return this.normals;
	}

	public float[] getTextureVertices() {
		return this.textureVertices;
	}

	public float[] getLightMapVertices() {
		return this.lightMapVertices;
	}

	public List<GroupBatch> getBatches() {
		return this.batches;
	}

	public int[] getLightReferences() {
		return this.lightReferences;
	}

	public int[] getDoodadReferences() {
		return this.doodadReferences;
	}

	public List<GroupBSPNode> getBspNodes() {
		return this.bspNodes;
	}

	public int[] getBspFaceIndices() {
		return this.bspFaceIndices;
	}

	public int[] getVertexColors() {
		return this.vertexColors;
	}

	public List<GroupLiquid> getLiquids() {
		return this.liquids;
	}

	public int[] getTriangleStripIndices() {
		return this.triangleStripIndices;
	}

	public List<GroupRect> getLightMaps() {
		return this.lightMaps;
	}

	public List<GroupLightMapTexels> getLightMapTexels() {
		return this.lightMapTexels;
	}

	public List<GroupMPB> getMpbv() {
		return this.mpbv;
	}

	public List<GroupMPB> getMpbp() {
		return this.mpbp;
	}

	public int[] getMbpIndices() {
		return this.mbpIndices;
	}

	public float[] getMpbg() {
		return this.mpbg;
	}
}
