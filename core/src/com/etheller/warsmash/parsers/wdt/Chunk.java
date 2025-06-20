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
	private short[] alphaMaps;
	private long[] doodadReferences;
	private long[] mapObjReferences;
	private long[] shadows;

	private List<MapChunkLayer> mapChunkLayers = new ArrayList<>();

	private List<MapChunkLiquidLayer> mapChunkLiquidLayers = new ArrayList<>();
	private List<MapChunkSoundEmitter> soundEmitters = new ArrayList<>();

	public void readMdx(final BinaryReader reader, final int size) {
		final boolean isAlpha = true;
		final long endPos = reader.position() + size;

		this.flags = reader.readUInt32();
		this.indexX = reader.readUInt32();
		this.indexY = reader.readUInt32();
		if (isAlpha) { // alpha
			this.radius = reader.readFloat32();
		}
		this.nLayers = reader.readInt32();
		this.nDoodadRefs = reader.readInt32();
		this.offsHeight = reader.readUInt32();
		this.offsNormal = reader.readUInt32();
		this.offsLayer = reader.readUInt32();
		this.offsRefs = reader.readUInt32();
		this.offsAlpha = reader.readUInt32();
		this.sizeAlpha = reader.readInt32();
		this.offsShadow = reader.readUInt32();
		this.sizeShadow = reader.readInt32();
		this.areaId = reader.readUInt32();
		this.nMapObjRefs = reader.readInt32();
		this.holes = reader.readUInt16();
		this.unknownTwelve = reader.readUInt16();
		reader.readUInt16Array(this.predTex);
		reader.readInt8Array(this.noEffectDoodad);
		this.offsSoundEmitters = reader.readUInt32();
		this.nSoundEmitters = reader.readInt32();
		this.offsLiquid = reader.readUInt32();
		this.sizeLiquid = reader.readUInt32();
		reader.position(reader.position() + 20); // some padding

		if (isAlpha) {
			this.sizeLiquid = null; // alpha
		}

		final long relativeStart = reader.position();
		final long relativeEnd = endPos - relativeStart;

		readChunkContents(reader, relativeStart, relativeEnd);

		reader.position((int) endPos);
	}

	private void readChunkContents(final BinaryReader reader, final long relativeStart, final long relativeEnd) {
		final boolean hasLiquids = (this.flags & Flags.HasLiquid) != 0;
		final boolean isAlpha = true;

		for (final Offset offsetPair : getOffsets(relativeEnd, isAlpha)) {
			final long offset = offsetPair.offset;
			War3ID token = offsetPair.token;
			reader.position((int) (relativeStart + offset));

			// use chunk headers when possible
			if ((!isAlpha && token.equals(MCRF)) || token.equals(MCLY)) {
				token = new War3ID(reader.readInt32());
				final int size = reader.readInt32();

				// NOTE: what is this? code is copied from "barncastle" on github,
				// and he says: "welcome to the world of blizzard"

				if ((size <= 0) && !(offsetPair.token.equals(MCLQ) && hasLiquids)) {
					continue;
				}
			}

			switch (token.getValue()) {
			case i_MCVT:
				this.heightMap = readVTNRFloat(reader, isAlpha);
				break;
			case i_MCNR:
				this.normals = readVTNRVector3b(reader, isAlpha);
				break;
			case i_MCAL:
				this.alphaMaps = reader.readUInt8Array(this.sizeAlpha);
				break;
			case i_MCLY:
				loadNDynamicObjects(this.mapChunkLayers, MapChunkLayer::new, reader, this.nLayers);
				break;
			case i_MCRF:
				if (this.nDoodadRefs > 0) {
					this.doodadReferences = reader.readUInt32Array(this.nDoodadRefs);
				}
				if (this.nMapObjRefs > 0) {
					this.mapObjReferences = reader.readUInt32Array(this.nMapObjRefs);
				}
				break;
			case i_MCSH:
				this.shadows = reader.readUInt32Array(this.sizeShadow / 4);
				break;
			case i_MCLQ:
				if ((this.flags & Flags.IsRiver) != 0) {
					final MapChunkLiquidLayer<SWVert> layer = new MapChunkLiquidLayer<SWVert>(SWVert::new);
					layer.readMdx(reader, 0);
					this.mapChunkLiquidLayers.add(layer);
				}
				if ((this.flags & Flags.IsOcean) != 0) {
					final MapChunkLiquidLayer<SOVert> layer = new MapChunkLiquidLayer<SOVert>(SOVert::new);
					layer.readMdx(reader, 0);
					this.mapChunkLiquidLayers.add(layer);
				}
				if ((this.flags & Flags.IsMagma) != 0) {
					final MapChunkLiquidLayer<SMVert> layer = new MapChunkLiquidLayer<SMVert>(SMVert::new);
					layer.readMdx(reader, 0);
					this.mapChunkLiquidLayers.add(layer);
				}
				if ((this.flags & Flags.IsSlime) != 0) {
					final MapChunkLiquidLayer<SWVert> layer = new MapChunkLiquidLayer<SWVert>(SWVert::new);
					layer.readMdx(reader, 0);
					this.mapChunkLiquidLayers.add(layer);
				}
				break;
			case i_MCSE:
				loadNDynamicObjects(this.soundEmitters, MapChunkSoundEmitter::new, reader, this.nSoundEmitters);
				break;
			default:
				throw new IllegalStateException(token.toString());
			}
		}
	}

	private float[][] readVTNRFloat(final BinaryReader reader, final boolean isAlpha) {
		if (isAlpha) {
			final float[][] data = new float[][] { new float[9], new float[9], new float[9], new float[9], new float[9],
					new float[9], new float[9], new float[9], new float[9], new float[8], new float[8], new float[8],
					new float[8], new float[8], new float[8], new float[8], new float[8], };
			for (final float[] row : data) {
				reader.readFloat32Array(row);
			}
			return data;
		}
		else {
			throw new IllegalStateException();
		}
	}

	private Vector3b[][] readVTNRVector3b(final BinaryReader reader, final boolean isAlpha) {
		if (isAlpha) {
			final Vector3b[][] data = { new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9],
					new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9], new Vector3b[9],
					new Vector3b[8], new Vector3b[8], new Vector3b[8], new Vector3b[8], new Vector3b[8],
					new Vector3b[8], new Vector3b[8], new Vector3b[8], };
			for (final Vector3b[] row : data) {
				for (int i = 0; i < row.length; i++) {
					row[i] = new Vector3b();
					reader.readInt8Array(row[i].components);
				}
			}
			return data;
		}
		else {
			throw new IllegalStateException();
		}
	}

	public static final class Vector3b {
		public byte[] components = new byte[3];
	}

	private List<Offset> getOffsets(final long relativeEnd, final boolean isAlpha) {
		final int offset = !isAlpha ? 8 : 0;

		final List<Offset> offsets = new ArrayList<>();
		offsets.add(new Offset(this.offsHeight - offset, MCVT));
		offsets.add(new Offset(this.offsNormal - offset, MCNR));
		offsets.add(new Offset(this.offsLayer - offset, MCLY));
		offsets.add(new Offset(this.offsRefs - offset, MCRF));

		if (this.sizeAlpha > 0) {
			offsets.add(new Offset(this.offsAlpha - offset, MCAL));
		}

		if (this.sizeShadow > 0) {
			offsets.add(new Offset(this.offsShadow - offset, MCSH));
		}

		if (this.nSoundEmitters > 0) {
			offsets.add(new Offset(this.offsSoundEmitters - offset, MCSE));
		}

		if (((this.flags & Flags.HasLiquid) != 0) || ((this.sizeLiquid != null) && (this.sizeLiquid > 8))) {
			offsets.add(new Offset(this.offsLiquid - offset, MCLQ));
		}

		offsets.sort(new Comparator<Offset>() {
			@Override
			public int compare(final Offset o1, final Offset o2) {
				return Long.compare(o1.offset, o2.offset);
			}
		});

		return offsets;
	}

	private <E extends MdlxBlock> void loadNDynamicObjects(final List<E> out, final MdlxBlockDescriptor<E> constructor,
			final BinaryReader reader, final long count) {

		while (out.size() < count) {
			final E object = constructor.create();

			object.readMdx(reader, -1);

			out.add(object);
		}
	}

	public long getFlags() {
		return this.flags;
	}

	public void setFlags(final long flags) {
		this.flags = flags;
	}

	public long getIndexX() {
		return this.indexX;
	}

	public void setIndexX(final long indexX) {
		this.indexX = indexX;
	}

	public long getIndexY() {
		return this.indexY;
	}

	public void setIndexY(final long indexY) {
		this.indexY = indexY;
	}

	public float getRadius() {
		return this.radius;
	}

	public void setRadius(final float radius) {
		this.radius = radius;
	}

	public int getnLayers() {
		return this.nLayers;
	}

	public void setnLayers(final int nLayers) {
		this.nLayers = nLayers;
	}

	public int getnDoodadRefs() {
		return this.nDoodadRefs;
	}

	public void setnDoodadRefs(final int nDoodadRefs) {
		this.nDoodadRefs = nDoodadRefs;
	}

	public long getOffsHeight() {
		return this.offsHeight;
	}

	public void setOffsHeight(final long offsHeight) {
		this.offsHeight = offsHeight;
	}

	public long getOffsNormal() {
		return this.offsNormal;
	}

	public void setOffsNormal(final long offsNormal) {
		this.offsNormal = offsNormal;
	}

	public long getOffsLayer() {
		return this.offsLayer;
	}

	public void setOffsLayer(final long offsLayer) {
		this.offsLayer = offsLayer;
	}

	public long getOffsRefs() {
		return this.offsRefs;
	}

	public void setOffsRefs(final long offsRefs) {
		this.offsRefs = offsRefs;
	}

	public long getOffsAlpha() {
		return this.offsAlpha;
	}

	public void setOffsAlpha(final long offsAlpha) {
		this.offsAlpha = offsAlpha;
	}

	public int getSizeAlpha() {
		return this.sizeAlpha;
	}

	public void setSizeAlpha(final int sizeAlpha) {
		this.sizeAlpha = sizeAlpha;
	}

	public long getOffsShadow() {
		return this.offsShadow;
	}

	public void setOffsShadow(final long offsShadow) {
		this.offsShadow = offsShadow;
	}

	public int getSizeShadow() {
		return this.sizeShadow;
	}

	public void setSizeShadow(final int sizeShadow) {
		this.sizeShadow = sizeShadow;
	}

	public long getAreaId() {
		return this.areaId;
	}

	public void setAreaId(final long areaId) {
		this.areaId = areaId;
	}

	public int getnMapObjRefs() {
		return this.nMapObjRefs;
	}

	public void setnMapObjRefs(final int nMapObjRefs) {
		this.nMapObjRefs = nMapObjRefs;
	}

	public int getHoles() {
		return this.holes;
	}

	public void setHoles(final int holes) {
		this.holes = holes;
	}

	public int getUnknownTwelve() {
		return this.unknownTwelve;
	}

	public void setUnknownTwelve(final int unknownTwelve) {
		this.unknownTwelve = unknownTwelve;
	}

	public int[] getPredTex() {
		return this.predTex;
	}

	public void setPredTex(final int[] predTex) {
		this.predTex = predTex;
	}

	public byte[] getNoEffectDoodad() {
		return this.noEffectDoodad;
	}

	public void setNoEffectDoodad(final byte[] noEffectDoodad) {
		this.noEffectDoodad = noEffectDoodad;
	}

	public long getOffsSoundEmitters() {
		return this.offsSoundEmitters;
	}

	public void setOffsSoundEmitters(final long offsSoundEmitters) {
		this.offsSoundEmitters = offsSoundEmitters;
	}

	public int getnSoundEmitters() {
		return this.nSoundEmitters;
	}

	public void setnSoundEmitters(final int nSoundEmitters) {
		this.nSoundEmitters = nSoundEmitters;
	}

	public long getOffsLiquid() {
		return this.offsLiquid;
	}

	public void setOffsLiquid(final long offsLiquid) {
		this.offsLiquid = offsLiquid;
	}

	public Long getSizeLiquid() {
		return this.sizeLiquid;
	}

	public void setSizeLiquid(final Long sizeLiquid) {
		this.sizeLiquid = sizeLiquid;
	}

	public float[][] getHeightMap() {
		return this.heightMap;
	}

	public void setHeightMap(final float[][] heightMap) {
		this.heightMap = heightMap;
	}

	public Vector3b[][] getNormals() {
		return this.normals;
	}

	public void setNormals(final Vector3b[][] normals) {
		this.normals = normals;
	}

	public short[] getAlphaMaps() {
		return this.alphaMaps;
	}

	public void setAlphaMaps(final short[] alphaMaps) {
		this.alphaMaps = alphaMaps;
	}

	public long[] getDoodadReferences() {
		return this.doodadReferences;
	}

	public void setDoodadReferences(final long[] doodadReferences) {
		this.doodadReferences = doodadReferences;
	}

	public long[] getMapObjReferences() {
		return this.mapObjReferences;
	}

	public void setMapObjReferences(final long[] mapObjReferences) {
		this.mapObjReferences = mapObjReferences;
	}

	public long[] getShadows() {
		return this.shadows;
	}

	public void setShadows(final long[] shadows) {
		this.shadows = shadows;
	}

	public List<MapChunkLayer> getMapChunkLayers() {
		return this.mapChunkLayers;
	}

	public void setMapChunkLayers(final List<MapChunkLayer> mapChunkLayers) {
		this.mapChunkLayers = mapChunkLayers;
	}

	public List<MapChunkLiquidLayer> getMapChunkLiquidLayers() {
		return this.mapChunkLiquidLayers;
	}

	public void setMapChunkLiquidLayers(final List<MapChunkLiquidLayer> mapChunkLiquidLayers) {
		this.mapChunkLiquidLayers = mapChunkLiquidLayers;
	}

	public List<MapChunkSoundEmitter> getSoundEmitters() {
		return this.soundEmitters;
	}

	public void setSoundEmitters(final List<MapChunkSoundEmitter> soundEmitters) {
		this.soundEmitters = soundEmitters;
	}

	private static final class Offset {
		long offset;
		War3ID token;

		public Offset(final long offset, final War3ID token) {
			this.offset = offset;
			this.token = token;
		}

		public void setOffset(final long offset) {
			this.offset = offset;
		}

		public void setToken(final War3ID token) {
			this.token = token;
		}

		public long getOffset() {
			return this.offset;
		}

		public War3ID getToken() {
			return this.token;
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
