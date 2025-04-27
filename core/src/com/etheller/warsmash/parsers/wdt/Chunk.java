package com.etheller.warsmash.parsers.wdt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SMVert;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SOVert;
import com.etheller.warsmash.parsers.wdt.MapChunkLiquidLayer.SWVert;
import com.etheller.warsmash.util.War3ID;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlock;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBlockDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxChunk;
import com.hiveworkshop.rms.util.BinaryReader;

public class Chunk {

	private static final War3ID MCSE = War3ID.fromString("MCSE");
	private static final War3ID MCSH = War3ID.fromString("MCSH");
	private static final War3ID MCAL = War3ID.fromString("MCAL");
	private static final War3ID MCNR = War3ID.fromString("MCNR");
	private static final War3ID MCVT = War3ID.fromString("MCVT");
	private static final War3ID MCLQ = War3ID.fromString("MCLQ");
	private static final War3ID MCLY = War3ID.fromString("MCLY");
	private static final War3ID MCRF = War3ID.fromString("MCRF");
	private static final int i_MCSE = ('M' << 24) | ('C' << 16) | ('S' << 8) | ('E');
	private static final int i_MCSH = ('M' << 24) | ('C' << 16) | ('S' << 8) | ('H');
	private static final int i_MCAL = ('M' << 24) | ('C' << 16) | ('A' << 8) | ('L');
	private static final int i_MCNR = ('M' << 24) | ('C' << 16) | ('N' << 8) | ('R');
	private static final int i_MCVT = ('M' << 24) | ('C' << 16) | ('V' << 8) | ('T');
	private static final int i_MCLQ = ('M' << 24) | ('C' << 16) | ('L' << 8) | ('Q');
	private static final int i_MCLY = ('M' << 24) | ('C' << 16) | ('L' << 8) | ('Y');
	private static final int i_MCRF = ('M' << 24) | ('C' << 16) | ('R' << 8) | ('F');

	private long flags;
	private long indexX;
	private long indexY;
	private float radius;
	private int nLayers;
	private int nDoodadRefs;
	private long offsHeight;
	private long offsNormal;
	private long offsLayer;
	private long offsRefs;
	private long offsAlpha;
	private int sizeAlpha;
	private long offsShadow;
	private int sizeShadow;
	private long areaId;
	private int nMapObjRefs;
	private int holes;
	private int unknownTwelve;
	private int[] predTex = new int[8];
	private byte[] noEffectDoodad = new byte[8];
	private long offsSoundEmitters;
	private int nSoundEmitters;
	private long offsLiquid;
	private Long sizeLiquid;
	private float[][] heightMap;
	private Vector3b[][] normals;
	private long[] alphaMaps;
	private long[] doodadReferences;
	private long[] mapObjReferences;
	private long[] shadows;
	
	private List<MapChunkLayer> mapChunkLayers = new ArrayList<>();
	
	private List<MapChunkLiquidLayer> mapChunkLiquidLayers = new ArrayList<>();
	private List<MapChunkSoundEmitter> soundEmitters = new ArrayList<>();

	public void readMdx(BinaryReader reader, int size) {
		boolean isAlpha = true;
		long endPos = reader.position() + size;

		flags = reader.readUInt32();
		indexX = reader.readUInt32();
		indexY = reader.readUInt32();
		if (isAlpha) // alpha
			radius = reader.readFloat32();
		nLayers = reader.readInt32();
		nDoodadRefs = reader.readInt32();
		offsHeight = reader.readUInt32();
		offsNormal = reader.readUInt32();
		offsLayer = reader.readUInt32();
		offsRefs = reader.readUInt32();
		offsAlpha = reader.readUInt32();
		sizeAlpha = reader.readInt32();
		offsShadow = reader.readUInt32();
		sizeShadow = reader.readInt32();
		areaId = reader.readUInt32();
		nMapObjRefs = reader.readInt32();
		holes = reader.readUInt16();
		unknownTwelve = reader.readUInt16();
		reader.readUInt16Array(predTex);
		reader.readInt8Array(noEffectDoodad);
		offsSoundEmitters = reader.readUInt32();
		nSoundEmitters = reader.readInt32();
		offsLiquid = reader.readUInt32();
		sizeLiquid = reader.readUInt32();

		if (isAlpha) {
			sizeLiquid = null; // alpha
		}

		long relativeStart = reader.position();
		long relativeEnd = endPos - relativeStart;

		readChunkContents(reader, relativeStart, relativeEnd);
		
		reader.position((int)endPos);
	}

	private void readChunkContents(BinaryReader reader, long relativeStart, long relativeEnd) {
		boolean hasLiquids = (flags & Flags.HasLiquid) != 0;
		boolean isAlpha = true;

		for (Offset offsetPair : getOffsets(relativeEnd, isAlpha)) {
			long offset = offsetPair.offset;
			War3ID token = offsetPair.token;
			reader.position((int) (relativeStart + offset));

			// use chunk headers when possible
			if (!isAlpha && ( token.equals(MCRF) || token.equals(MCLY))) {
				token = new War3ID(reader.readInt32());
				int size = reader.readInt32();

				// NOTE: what is this? code is copied from "barncastle" on github,
				// and he says: "welcome to the world of blizzard"

				if (size <= 0 && !(offsetPair.token.equals(MCLQ) && hasLiquids)) {
					continue;
				}
			}

			switch (token.getValue()) {
			case i_MCVT:
				heightMap = readVTNRFloat(reader, isAlpha);
				break;
			case i_MCNR:
				normals = readVTNRVector3b(reader, isAlpha);
				break;
			case i_MCAL:
				alphaMaps = reader.readUInt32Array(sizeAlpha / 4);
				break;
			case i_MCLY:
				loadNDynamicObjects(mapChunkLayers, MapChunkLayer::new, reader, nLayers);
				break;
			case i_MCRF:
				if(nDoodadRefs > 0) {
					doodadReferences = reader.readUInt32Array(nDoodadRefs);
				}
				if(nMapObjRefs > 0) {
					mapObjReferences = reader.readUInt32Array(nMapObjRefs);
				}
				break;
			case i_MCSH:
				shadows = reader.readUInt32Array(sizeShadow / 4);
				break;
			case i_MCLQ:
				if((flags & Flags.IsRiver) != 0) {
					mapChunkLiquidLayers.add(new MapChunkLiquidLayer<SWVert>(SWVert::new));
				}
				if((flags & Flags.IsOcean) != 0) {
					mapChunkLiquidLayers.add(new MapChunkLiquidLayer<SOVert>(SOVert::new));
				}
				if((flags & Flags.IsMagma) != 0) {
					mapChunkLiquidLayers.add(new MapChunkLiquidLayer<SMVert>(SMVert::new));
				}
				if((flags & Flags.IsSlime) != 0) {
					mapChunkLiquidLayers.add(new MapChunkLiquidLayer<SWVert>(SWVert::new));
				}
				break;
			case i_MCSE:
				loadNDynamicObjects(soundEmitters, MapChunkSoundEmitter::new, reader, nSoundEmitters);
				break;
			default:
				throw new IllegalStateException(token.toString());
			}
			System.out.println("Map Chunk read tag: " + token);
		}
	}

	private float[][] readVTNRFloat(BinaryReader reader, boolean isAlpha) {
		if (isAlpha) {
			float[][] data = new float[][] { new float[9], new float[9], new float[9], new float[9], new float[9],
					new float[9], new float[9], new float[9], new float[9], new float[8], new float[8], new float[8],
					new float[8], new float[8], new float[8], new float[8], new float[8], };
			for (float[] row : data) {
				reader.readFloat32Array(row);
			}
			return data;
		} else {
			throw new IllegalStateException();
		}
	}

	private Vector3b[][] readVTNRVector3b(BinaryReader reader, boolean isAlpha) {
		if (isAlpha) {
			Vector3b[][] data = { new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9],
					new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[8],
					new Vector3b[8], new Vector3b[8], new Vector3b[8], new Vector3b[8], new Vector3b[8],
					new Vector3b[8], new Vector3b[8], };
			for (Vector3b[] row : data) {
				for (int i = 0; i < row.length; i++) {
					row[i] = new Vector3b();
					reader.readInt8Array(row[i].components);
				}
			}
			return data;
		} else {
			throw new IllegalStateException();
		}
	}

	private static final class Vector3b {
		byte[] components = new byte[3];
	}

	private List<Offset> getOffsets(long relativeEnd, boolean isAlpha) {
		int offset = !isAlpha ? 8 : 0;

		List<Offset> offsets = new ArrayList<>();
		offsets.add(new Offset(offsHeight - offset, MCVT));
		offsets.add(new Offset(offsNormal - offset, MCNR));
		offsets.add(new Offset(offsLayer - offset, MCLY));
		offsets.add(new Offset(offsRefs - offset, MCRF));

		if (sizeAlpha > 0) {
			offsets.add(new Offset(offsAlpha - offset, MCAL));
		}

		if (sizeShadow > 0) {
			offsets.add(new Offset(offsShadow - offset, MCSH));
		}

		if (nSoundEmitters > 0) {
			offsets.add(new Offset(offsSoundEmitters - offset, MCSE));
		}

		if ((flags & Flags.HasLiquid) != 0 || (sizeLiquid != null && sizeLiquid > 8)) {
			offsets.add(new Offset(offsLiquid - offset, MCLQ));
		}

		offsets.sort(new Comparator<Offset>() {
			@Override
			public int compare(Offset o1, Offset o2) {
				return Long.compare(o1.offset, o2.offset);
			}
		});

		return offsets;
	}
	private <E extends MdlxBlock> void loadNDynamicObjects(final List<E> out,
			final MdlxBlockDescriptor<E> constructor, final BinaryReader reader, final long count) {

		while (out.size() < count) {
			final E object = constructor.create();

			object.readMdx(reader, -1);

			out.add(object);
		}
	}
	
	


	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public long getIndexX() {
		return indexX;
	}

	public void setIndexX(long indexX) {
		this.indexX = indexX;
	}

	public long getIndexY() {
		return indexY;
	}

	public void setIndexY(long indexY) {
		this.indexY = indexY;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getnLayers() {
		return nLayers;
	}

	public void setnLayers(int nLayers) {
		this.nLayers = nLayers;
	}

	public int getnDoodadRefs() {
		return nDoodadRefs;
	}

	public void setnDoodadRefs(int nDoodadRefs) {
		this.nDoodadRefs = nDoodadRefs;
	}

	public long getOffsHeight() {
		return offsHeight;
	}

	public void setOffsHeight(long offsHeight) {
		this.offsHeight = offsHeight;
	}

	public long getOffsNormal() {
		return offsNormal;
	}

	public void setOffsNormal(long offsNormal) {
		this.offsNormal = offsNormal;
	}

	public long getOffsLayer() {
		return offsLayer;
	}

	public void setOffsLayer(long offsLayer) {
		this.offsLayer = offsLayer;
	}

	public long getOffsRefs() {
		return offsRefs;
	}

	public void setOffsRefs(long offsRefs) {
		this.offsRefs = offsRefs;
	}

	public long getOffsAlpha() {
		return offsAlpha;
	}

	public void setOffsAlpha(long offsAlpha) {
		this.offsAlpha = offsAlpha;
	}

	public int getSizeAlpha() {
		return sizeAlpha;
	}

	public void setSizeAlpha(int sizeAlpha) {
		this.sizeAlpha = sizeAlpha;
	}

	public long getOffsShadow() {
		return offsShadow;
	}

	public void setOffsShadow(long offsShadow) {
		this.offsShadow = offsShadow;
	}

	public int getSizeShadow() {
		return sizeShadow;
	}

	public void setSizeShadow(int sizeShadow) {
		this.sizeShadow = sizeShadow;
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public int getnMapObjRefs() {
		return nMapObjRefs;
	}

	public void setnMapObjRefs(int nMapObjRefs) {
		this.nMapObjRefs = nMapObjRefs;
	}

	public int getHoles() {
		return holes;
	}

	public void setHoles(int holes) {
		this.holes = holes;
	}

	public int getUnknownTwelve() {
		return unknownTwelve;
	}

	public void setUnknownTwelve(int unknownTwelve) {
		this.unknownTwelve = unknownTwelve;
	}

	public int[] getPredTex() {
		return predTex;
	}

	public void setPredTex(int[] predTex) {
		this.predTex = predTex;
	}

	public byte[] getNoEffectDoodad() {
		return noEffectDoodad;
	}

	public void setNoEffectDoodad(byte[] noEffectDoodad) {
		this.noEffectDoodad = noEffectDoodad;
	}

	public long getOffsSoundEmitters() {
		return offsSoundEmitters;
	}

	public void setOffsSoundEmitters(long offsSoundEmitters) {
		this.offsSoundEmitters = offsSoundEmitters;
	}

	public int getnSoundEmitters() {
		return nSoundEmitters;
	}

	public void setnSoundEmitters(int nSoundEmitters) {
		this.nSoundEmitters = nSoundEmitters;
	}

	public long getOffsLiquid() {
		return offsLiquid;
	}

	public void setOffsLiquid(long offsLiquid) {
		this.offsLiquid = offsLiquid;
	}

	public Long getSizeLiquid() {
		return sizeLiquid;
	}

	public void setSizeLiquid(Long sizeLiquid) {
		this.sizeLiquid = sizeLiquid;
	}

	public float[][] getHeightMap() {
		return heightMap;
	}

	public void setHeightMap(float[][] heightMap) {
		this.heightMap = heightMap;
	}

	public Vector3b[][] getNormals() {
		return normals;
	}

	public void setNormals(Vector3b[][] normals) {
		this.normals = normals;
	}

	public long[] getAlphaMaps() {
		return alphaMaps;
	}

	public void setAlphaMaps(long[] alphaMaps) {
		this.alphaMaps = alphaMaps;
	}

	public long[] getDoodadReferences() {
		return doodadReferences;
	}

	public void setDoodadReferences(long[] doodadReferences) {
		this.doodadReferences = doodadReferences;
	}

	public long[] getMapObjReferences() {
		return mapObjReferences;
	}

	public void setMapObjReferences(long[] mapObjReferences) {
		this.mapObjReferences = mapObjReferences;
	}

	public long[] getShadows() {
		return shadows;
	}

	public void setShadows(long[] shadows) {
		this.shadows = shadows;
	}

	public List<MapChunkLayer> getMapChunkLayers() {
		return mapChunkLayers;
	}

	public void setMapChunkLayers(List<MapChunkLayer> mapChunkLayers) {
		this.mapChunkLayers = mapChunkLayers;
	}

	public List<MapChunkLiquidLayer> getMapChunkLiquidLayers() {
		return mapChunkLiquidLayers;
	}

	public void setMapChunkLiquidLayers(List<MapChunkLiquidLayer> mapChunkLiquidLayers) {
		this.mapChunkLiquidLayers = mapChunkLiquidLayers;
	}

	public List<MapChunkSoundEmitter> getSoundEmitters() {
		return soundEmitters;
	}

	public void setSoundEmitters(List<MapChunkSoundEmitter> soundEmitters) {
		this.soundEmitters = soundEmitters;
	}

	private static final class Offset {
		long offset;
		War3ID token;

		public Offset(long offset, War3ID token) {
			this.offset = offset;
			this.token = token;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		public void setToken(War3ID token) {
			this.token = token;
		}

		public long getOffset() {
			return offset;
		}

		public War3ID getToken() {
			return token;
		}
	}

	public static final class Flags {
		public static final int IsRiver = 4;
		public static final int IsOcean = 8;
		public static final int IsMagma = 16;
		public static final int IsSlime = 32;
		public static final int HasLiquid = IsRiver | IsOcean | IsMagma | IsSlime;
	}
}
