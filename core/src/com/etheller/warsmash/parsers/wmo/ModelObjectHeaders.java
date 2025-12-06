package com.etheller.warsmash.parsers.wmo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.ByteArray;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlockDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.util.BinaryReader;

public class ModelObjectHeaders {
	private static final int MOHD = ('M' << 24) | ('O' << 16) | ('H' << 8) | ('D');// War3ID.fromString("MDLX").getValue();
	private static final int MOTX = ('M' << 24) | ('O' << 16) | ('T' << 8) | ('X');// War3ID.fromString("MDLX").getValue();
	private static final int MOSB = ('M' << 24) | ('O' << 16) | ('S' << 8) | ('B');// War3ID.fromString("MDLX").getValue();
	private static final int MOGN = ('M' << 24) | ('O' << 16) | ('G' << 8) | ('N');// War3ID.fromString("MDLX").getValue();
	private static final int MODN = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('N');// War3ID.fromString("MDLX").getValue();
	private static final int MOMT = ('M' << 24) | ('O' << 16) | ('M' << 8) | ('T');// War3ID.fromString("MDLX").getValue();
	private static final int MOGI = ('M' << 24) | ('O' << 16) | ('G' << 8) | ('I');// War3ID.fromString("MDLX").getValue();
	private static final int MOPV = ('M' << 24) | ('O' << 16) | ('P' << 8) | ('V');// War3ID.fromString("MDLX").getValue();
	private static final int MOPT = ('M' << 24) | ('O' << 16) | ('P' << 8) | ('T');// War3ID.fromString("MDLX").getValue();
	private static final int MOPR = ('M' << 24) | ('O' << 16) | ('P' << 8) | ('R');// War3ID.fromString("MDLX").getValue();
	private static final int MOLT = ('M' << 24) | ('O' << 16) | ('L' << 8) | ('T');// War3ID.fromString("MDLX").getValue();
	private static final int MODS = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('S');// War3ID.fromString("MDLX").getValue();
	private static final int MODD = ('M' << 24) | ('O' << 16) | ('D' << 8) | ('D');// War3ID.fromString("MDLX").getValue();
	private static final int MFOG = ('M' << 24) | ('F' << 16) | ('O' << 8) | ('G');// War3ID.fromString("MDLX").getValue();
	private static final int MOVV = ('M' << 24) | ('O' << 16) | ('V' << 8) | ('V');// War3ID.fromString("MDLX").getValue();
	private static final int MOVB = ('M' << 24) | ('O' << 16) | ('V' << 8) | ('B');// War3ID.fromString("MDLX").getValue();
	private static final int MCVP = ('M' << 24) | ('C' << 16) | ('V' << 8) | ('P');// War3ID.fromString("MDLX").getValue();

	private long nTextures;
	private long nGroups;
	private long nPortals;
	private long nLights;
	public long nDoodadNames;
	private long nDoodadDefs;
	private long nDoodadSets;
	private final short[] ambColor = new short[4]; // bgra
	private long wmoId;

	private List<String> textureFileNames;
	private List<String> skyboxFileNames;
	private List<String> groupNames;
	private List<String> doodadFileNames;
	private final List<WmoMaterial> materials = new ArrayList<>();
	private final List<WmoGroupInfo> groupInfos = new ArrayList<>();
	private float[] portalVertices;
	private final List<WmoPortal> portals = new ArrayList<>();
	private final List<WmoPortalReference> portalReferences = new ArrayList<>();
	private final List<WmoLight> lights = new ArrayList<>();
	private final List<WmoDoodadSet> doodadSets = new ArrayList<>();
	private final List<WmoDoodadDefinition> doodadDefinitions = new ArrayList<>();
	private final List<WmoFog> fogs = new ArrayList<>();
	private float[] visibleVertices;
	private final List<WmoVisibleBlocks> visibleBlocks = new ArrayList<>();
	private final List<WmoPlane> convexVolumePlanes = new ArrayList<>();

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
			System.out.println("MOMO parsing: " + readTag + "," + size);

			switch (readTag.getValue()) {
			case MOHD:
				loadHeaderChunk(reader, version);
				break;
			case MOTX:
				this.textureFileNames = loadStringsChunk(reader, size);
				break;
			case MOSB:
				this.skyboxFileNames = loadStringsChunk(reader, size);
				break;
			case MOGN:
				this.groupNames = loadStringsChunk(reader, size);
				break;
			case MODN:
				this.doodadFileNames = loadStringsChunk(reader, size);
				break;
			case MOMT:
				loadDynamicObjects(this.materials, WmoMaterial::new, reader, size, version);
				break;
			case MOGI:
				loadDynamicObjects(this.groupInfos, WmoGroupInfo::new, reader, size, version);
				break;
			case MOPV:
				this.portalVertices = reader.readFloat32Array(size / 4);
				break;
			case MOPT:
				// NOTE: fixed size struct WmoPortal, doesn't actually need dynamic objects call
				loadDynamicObjects(this.portals, WmoPortal::new, reader, size, version);
				break;
			case MOPR:
				loadDynamicObjects(this.portalReferences, WmoPortalReference::new, reader, size, version);
				break;
			case MOLT:
				loadDynamicObjects(this.lights, WmoLight::new, reader, size, version);
				break;
			case MODS:
				loadDynamicObjects(this.doodadSets, WmoDoodadSet::new, reader, size, version);
				break;
			case MODD:
				loadDynamicObjects(this.doodadDefinitions, WmoDoodadDefinition::new, reader, size, version);
				break;
			case MFOG:
				loadDynamicObjects(this.fogs, WmoFog::new, reader, size, version);
				break;
			case MOVV:
				this.visibleVertices = reader.readFloat32Array(size / 4);
				break;
			case MOVB:
				loadDynamicObjects(this.visibleBlocks, WmoVisibleBlocks::new, reader, size, version);
				break;
			case MCVP:
				loadDynamicObjects(this.convexVolumePlanes, WmoPlane::new, reader, size, version);
				break;
			default:
				System.out.println("MOMO unknown tag, continuing");
				reader.position(reader.position() + size);
				break;
			}
		}
	}

	private void loadHeaderChunk(final BinaryReader reader, final int version) {
		this.nTextures = reader.readUInt32();
		this.nGroups = reader.readUInt32();
		this.nPortals = reader.readUInt32();
		this.nLights = reader.readUInt32();
		this.nDoodadNames = reader.readUInt32();
		this.nDoodadDefs = reader.readUInt32();
		this.nDoodadSets = reader.readUInt32();
		reader.readUInt8Array(this.ambColor);
		this.wmoId = reader.readUInt32();

		if (version == 14) {
			// 0.5.3 skip some stuff here, dont know what it is

			reader.position(reader.position() + 0x1C);
		}
		else {
			throw new IllegalStateException("WMO MOMO version: " + version);
		}
	}

	private List<String> loadStringsChunk(final BinaryReader reader, final int size) {
		final List<String> strings = new ArrayList<>();
		final ByteArray byteArray = new ByteArray();
		for (int k = 0; k < size; k++) {
			final byte value = reader.readInt8();
			if (value == 0) {
				strings.add(new String(byteArray.toArray()));
				byteArray.clear();
			}
			else {
				byteArray.add(value);
			}
		}
		if (!byteArray.isEmpty()) {
			strings.add(new String(byteArray.toArray()));
		}
		return strings;
	}

	private <E extends MdlxBlock> void loadNDynamicObjects(final List<E> out, final MdlxBlockDescriptor<E> constructor,
			final BinaryReader reader, final long count) {

		while (out.size() < count) {
			final E object = constructor.create();

			object.readMdx(reader, -1);

			out.add(object);
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

	public long getnTextures() {
		return this.nTextures;
	}

	public long getnGroups() {
		return this.nGroups;
	}

	public long getnPortals() {
		return this.nPortals;
	}

	public long getnLights() {
		return this.nLights;
	}

	public long getnDoodadNames() {
		return this.nDoodadNames;
	}

	public long getnDoodadDefs() {
		return this.nDoodadDefs;
	}

	public long getnDoodadSets() {
		return this.nDoodadSets;
	}

	public short[] getAmbColor() {
		return this.ambColor;
	}

	public long getWmoId() {
		return this.wmoId;
	}

	public List<String> getTextureFileNames() {
		return this.textureFileNames;
	}

	public List<String> getSkyboxFileNames() {
		return this.skyboxFileNames;
	}

	public List<String> getGroupNames() {
		return this.groupNames;
	}

	public List<String> getDoodadFileNames() {
		return this.doodadFileNames;
	}

	public List<WmoMaterial> getMaterials() {
		return this.materials;
	}

	public List<WmoGroupInfo> getGroupInfos() {
		return this.groupInfos;
	}

	public float[] getPortalVertices() {
		return this.portalVertices;
	}

	public List<WmoPortal> getPortals() {
		return this.portals;
	}

	public List<WmoPortalReference> getPortalReferences() {
		return this.portalReferences;
	}

	public List<WmoLight> getLights() {
		return this.lights;
	}

	public List<WmoDoodadSet> getDoodadSets() {
		return this.doodadSets;
	}

	public List<WmoDoodadDefinition> getDoodadDefinitions() {
		return this.doodadDefinitions;
	}

	public List<WmoFog> getFogs() {
		return this.fogs;
	}

	public float[] getVisibleVertices() {
		return this.visibleVertices;
	}

	public List<WmoVisibleBlocks> getVisibleBlocks() {
		return this.visibleBlocks;
	}

	public List<WmoPlane> getConvexVolumePlanes() {
		return this.convexVolumePlanes;
	}

}
