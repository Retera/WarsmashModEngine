package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class ParticleEmitter2 extends GenericObject {
	// 0: blend
	// 1: additive
	// 2: modulate
	// 3: modulate 2x
	// 4: alphakey
	public static enum FilterMode {
		BLEND("Blend"),
		ADDITIVE("Additive"),
		MODULATE("Modulate"),
		MODULATE2X("Modulate2x"),
		ALPHAKEY("AlphaKey");

		String mdlText;

		FilterMode(final String str) {
			this.mdlText = str;
		}

		public String getMdlText() {
			return this.mdlText;
		}

		public static FilterMode fromId(final int id) {
			return values()[id];
		}

		public static int nameToId(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.getMdlText().equals(name)) {
					return mode.ordinal();
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return getMdlText();
		}
	}

	private float speed = 0;
	private float variation = 0;
	private float latitude = 0;
	private float gravity = 0;
	private float lifeSpan = 0;
	private float emissionRate = 0;
	private float length;
	private float width;
	private FilterMode filterMode = FilterMode.BLEND;
	private long rows = 0;
	private long columns = 0;
	private long headOrTail = 0;
	private float tailLength = 0;
	private float timeMiddle = 0;
	private final float[][] segmentColors = new float[3][3];
	private final short[] segmentAlphas = new short[3]; // unsigned byte[]
	private final float[] segmentScaling = new float[3];
	private final long[][] headIntervals = new long[2][3];
	private final long[][] tailIntervals = new long[2][3];
	private int textureId = -1;
	private long squirt = 0;
	private int priorityPlane = 0;
	private long replaceableId = 0;

	public ParticleEmitter2() {
		super(0x1000);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		super.readMdx(stream);

		this.speed = stream.readFloat();
		this.variation = stream.readFloat();
		this.latitude = stream.readFloat();
		this.gravity = stream.readFloat();
		this.lifeSpan = stream.readFloat();
		this.emissionRate = stream.readFloat();
		this.length = stream.readFloat();
		this.width = stream.readFloat();
		this.filterMode = FilterMode.fromId((int) (ParseUtils.readUInt32(stream)));
		this.rows = ParseUtils.readUInt32(stream);
		this.columns = ParseUtils.readUInt32(stream);
		this.headOrTail = ParseUtils.readUInt32(stream);
		this.tailLength = stream.readFloat();
		this.timeMiddle = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.segmentColors[0]);
		ParseUtils.readFloatArray(stream, this.segmentColors[1]);
		ParseUtils.readFloatArray(stream, this.segmentColors[2]);
		ParseUtils.readUInt8Array(stream, this.segmentAlphas);
		ParseUtils.readFloatArray(stream, this.segmentScaling);
		ParseUtils.readUInt32Array(stream, this.headIntervals[0]);
		ParseUtils.readUInt32Array(stream, this.headIntervals[1]);
		ParseUtils.readUInt32Array(stream, this.tailIntervals[0]);
		ParseUtils.readUInt32Array(stream, this.tailIntervals[1]);
		this.textureId = stream.readInt();
		this.squirt = ParseUtils.readUInt32(stream);
		this.priorityPlane = stream.readInt();
		this.replaceableId = ParseUtils.readUInt32(stream);

		readTimelines(stream, size - this.getByteLength());
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());

		super.writeMdx(stream);

		stream.writeFloat(this.speed);
		stream.writeFloat(this.variation);
		stream.writeFloat(this.latitude);
		stream.writeFloat(this.gravity);
		stream.writeFloat(this.lifeSpan);
		stream.writeFloat(this.emissionRate);
		stream.writeFloat(this.length);
		stream.writeFloat(this.width);
		ParseUtils.writeUInt32(stream, this.filterMode.ordinal());
		ParseUtils.writeUInt32(stream, this.rows);
		ParseUtils.writeUInt32(stream, this.columns);
		ParseUtils.writeUInt32(stream, this.headOrTail);
		stream.writeFloat(this.tailLength);
		stream.writeFloat(this.timeMiddle);
		ParseUtils.writeFloatArray(stream, this.segmentColors[0]);
		ParseUtils.writeFloatArray(stream, this.segmentColors[1]);
		ParseUtils.writeFloatArray(stream, this.segmentColors[2]);
		ParseUtils.writeUInt8Array(stream, this.segmentAlphas);
		ParseUtils.writeFloatArray(stream, this.segmentScaling);
		ParseUtils.writeUInt32Array(stream, this.headIntervals[0]);
		ParseUtils.writeUInt32Array(stream, this.headIntervals[1]);
		ParseUtils.writeUInt32Array(stream, this.tailIntervals[0]);
		ParseUtils.writeUInt32Array(stream, this.tailIntervals[1]);
		stream.writeInt(this.textureId);
		ParseUtils.writeUInt32(stream, this.squirt);
		stream.writeInt(this.priorityPlane);
		ParseUtils.writeUInt32(stream, this.replaceableId);

		writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_SORT_PRIMS_FAR_Z:
				this.flags |= 0x10000;
				break;
			case MdlUtils.TOKEN_UNSHADED:
				this.flags |= 0x8000;
				break;
			case MdlUtils.TOKEN_LINE_EMITTER:
				this.flags |= 0x20000;
				break;
			case MdlUtils.TOKEN_UNFOGGED:
				this.flags |= 0x40000;
				break;
			case MdlUtils.TOKEN_MODEL_SPACE:
				this.flags |= 0x80000;
				break;
			case MdlUtils.TOKEN_XY_QUAD:
				this.flags |= 0x100000;
				break;
			case MdlUtils.TOKEN_STATIC_SPEED:
				this.speed = stream.readFloat();
				break;
			case MdlUtils.TOKEN_SPEED:
				readTimeline(stream, AnimationMap.KP2S);
				break;
			case MdlUtils.TOKEN_STATIC_VARIATION:
				this.variation = stream.readFloat();
				break;
			case MdlUtils.TOKEN_VARIATION:
				readTimeline(stream, AnimationMap.KP2R);
				break;
			case MdlUtils.TOKEN_STATIC_LATITUDE:
				this.latitude = stream.readFloat();
				break;
			case MdlUtils.TOKEN_LATITUDE:
				readTimeline(stream, AnimationMap.KP2L);
				break;
			case MdlUtils.TOKEN_STATIC_GRAVITY:
				this.gravity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_GRAVITY:
				readTimeline(stream, AnimationMap.KP2G);
				break;
			case MdlUtils.TOKEN_VISIBILITY:
				readTimeline(stream, AnimationMap.KP2V);
				break;
			case MdlUtils.TOKEN_SQUIRT:
				this.squirt = 1;
				break;
			case MdlUtils.TOKEN_LIFE_SPAN:
				this.lifeSpan = stream.readFloat();
				break;
			case MdlUtils.TOKEN_STATIC_EMISSION_RATE:
				this.emissionRate = stream.readFloat();
				break;
			case MdlUtils.TOKEN_EMISSION_RATE:
				readTimeline(stream, AnimationMap.KP2E);
				break;
			case MdlUtils.TOKEN_STATIC_WIDTH:
				this.width = stream.readFloat();
				break;
			case MdlUtils.TOKEN_WIDTH:
				readTimeline(stream, AnimationMap.KP2W);
				break;
			case MdlUtils.TOKEN_STATIC_LENGTH:
				this.length = stream.readFloat();
				break;
			case MdlUtils.TOKEN_LENGTH:
				readTimeline(stream, AnimationMap.KP2N);
				break;
			case MdlUtils.TOKEN_BLEND:
				this.filterMode = FilterMode.BLEND;
				break;
			case MdlUtils.TOKEN_ADDITIVE:
				this.filterMode = FilterMode.ADDITIVE;
				break;
			case MdlUtils.TOKEN_MODULATE:
				this.filterMode = FilterMode.MODULATE;
				break;
			case MdlUtils.TOKEN_MODULATE2X:
				this.filterMode = FilterMode.MODULATE2X;
				break;
			case MdlUtils.TOKEN_ALPHAKEY:
				this.filterMode = FilterMode.ALPHAKEY;
				break;
			case MdlUtils.TOKEN_ROWS:
				this.rows = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_COLUMNS:
				this.columns = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_HEAD:
				this.headOrTail = 0;
				break;
			case MdlUtils.TOKEN_TAIL:
				this.headOrTail = 1;
				break;
			case MdlUtils.TOKEN_BOTH:
				this.headOrTail = 2;
				break;
			case MdlUtils.TOKEN_TAIL_LENGTH:
				this.tailLength = stream.readFloat();
				break;
			case MdlUtils.TOKEN_TIME:
				this.timeMiddle = stream.readFloat();
				break;
			case MdlUtils.TOKEN_SEGMENT_COLOR:
				stream.read(); // {

				for (int i = 0; i < 3; i++) {
					stream.read(); // Color
					stream.readColor(this.segmentColors[i]);
				}

				stream.read(); // }
				break;
			case MdlUtils.TOKEN_ALPHA:
				stream.readUInt8Array(this.segmentAlphas);
				break;
			case MdlUtils.TOKEN_PARTICLE_SCALING:
				stream.readFloatArray(this.segmentScaling);
				break;
			case MdlUtils.TOKEN_LIFE_SPAN_UV_ANIM:
				stream.readIntArray(this.headIntervals[0]);
				break;
			case MdlUtils.TOKEN_DECAY_UV_ANIM:
				stream.readIntArray(this.headIntervals[1]);
				break;
			case MdlUtils.TOKEN_TAIL_UV_ANIM:
				stream.readIntArray(this.tailIntervals[0]);
				break;
			case MdlUtils.TOKEN_TAIL_DECAY_UV_ANIM:
				stream.readIntArray(this.tailIntervals[1]);
				break;
			case MdlUtils.TOKEN_TEXTURE_ID:
				this.textureId = stream.readInt();
				break;
			case MdlUtils.TOKEN_REPLACEABLE_ID:
				this.replaceableId = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_PRIORITY_PLANE:
				this.priorityPlane = stream.readInt();
				break;

			default:
				throw new IllegalStateException("Unknown token in ParticleEmitter2 " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_PARTICLE_EMITTER2, this.name);
		writeGenericHeader(stream);

		if ((this.flags & 0x10000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SORT_PRIMS_FAR_Z);
		}

		if ((this.flags & 0x8000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNSHADED);
		}

		if ((this.flags & 0x20000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_LINE_EMITTER);
		}

		if ((this.flags & 0x40000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNFOGGED);
		}

		if ((this.flags & 0x80000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_MODEL_SPACE);
		}

		if ((this.flags & 0x100000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_XY_QUAD);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2S)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_SPEED, this.speed);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2R)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_VARIATION, this.variation);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2L)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LATITUDE, this.latitude);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2G)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_GRAVITY, this.gravity);
		}

		writeTimeline(stream, AnimationMap.KP2V);

		if (this.squirt != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SQUIRT);
		}

		stream.writeFloatAttrib(MdlUtils.TOKEN_LIFE_SPAN, this.lifeSpan);

		if (!this.writeTimeline(stream, AnimationMap.KP2E)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_EMISSION_RATE, this.emissionRate);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2W)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_WIDTH, this.width);
		}

		if (!this.writeTimeline(stream, AnimationMap.KP2N)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LENGTH, this.length);
		}

		stream.writeFlag(this.filterMode.getMdlText());

		stream.writeAttribUInt32(MdlUtils.TOKEN_ROWS, this.rows);
		stream.writeAttribUInt32(MdlUtils.TOKEN_COLUMNS, this.columns);

		switch ((int) this.headOrTail) {
		case 0:
			stream.writeFlag(MdlUtils.TOKEN_HEAD);
			break;
		case 1:
			stream.writeFlag(MdlUtils.TOKEN_TAIL);
			break;
		case 2:
			stream.writeFlag(MdlUtils.TOKEN_BOTH);
			break;
		default:
			throw new IllegalStateException("Bad headOrTail value when saving MDL: " + this.headOrTail);
		}

		stream.writeFloatAttrib(MdlUtils.TOKEN_TAIL_LENGTH, this.tailLength);
		stream.writeFloatAttrib(MdlUtils.TOKEN_TIME, this.timeMiddle);

		stream.startBlock(MdlUtils.TOKEN_SEGMENT_COLOR);
		stream.writeColor(MdlUtils.TOKEN_COLOR, this.segmentColors[0]);
		stream.writeColor(MdlUtils.TOKEN_COLOR, this.segmentColors[1]);
		stream.writeColor(MdlUtils.TOKEN_COLOR, this.segmentColors[2]);
		stream.endBlockComma();

		stream.writeArrayAttrib(MdlUtils.TOKEN_ALPHA, this.segmentAlphas);
		stream.writeFloatArrayAttrib(MdlUtils.TOKEN_PARTICLE_SCALING, this.segmentScaling);
		stream.writeArrayAttrib(MdlUtils.TOKEN_LIFE_SPAN_UV_ANIM, this.headIntervals[0]);
		stream.writeArrayAttrib(MdlUtils.TOKEN_DECAY_UV_ANIM, this.headIntervals[1]);
		stream.writeArrayAttrib(MdlUtils.TOKEN_TAIL_UV_ANIM, this.tailIntervals[0]);
		stream.writeArrayAttrib(MdlUtils.TOKEN_TAIL_DECAY_UV_ANIM, this.tailIntervals[1]);
		stream.writeAttrib(MdlUtils.TOKEN_TEXTURE_ID, this.textureId);

		if (this.replaceableId != 0) {
			stream.writeAttribUInt32(MdlUtils.TOKEN_REPLACEABLE_ID, this.replaceableId);
		}

		if (this.priorityPlane != 0) {
			stream.writeAttrib(MdlUtils.TOKEN_PRIORITY_PLANE, this.priorityPlane);
		}

		writeGenericTimelines(stream);

		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 175 + super.getByteLength();
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getVariation() {
		return this.variation;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public float getGravity() {
		return this.gravity;
	}

	public float getLifeSpan() {
		return this.lifeSpan;
	}

	public float getEmissionRate() {
		return this.emissionRate;
	}

	public float getLength() {
		return this.length;
	}

	public float getWidth() {
		return this.width;
	}

	public FilterMode getFilterMode() {
		return this.filterMode;
	}

	public long getRows() {
		return this.rows;
	}

	public long getColumns() {
		return this.columns;
	}

	public long getHeadOrTail() {
		return this.headOrTail;
	}

	public float getTailLength() {
		return this.tailLength;
	}

	public float getTimeMiddle() {
		return this.timeMiddle;
	}

	public float[][] getSegmentColors() {
		return this.segmentColors;
	}

	public short[] getSegmentAlphas() {
		return this.segmentAlphas;
	}

	public float[] getSegmentScaling() {
		return this.segmentScaling;
	}

	public long[][] getHeadIntervals() {
		return this.headIntervals;
	}

	public long[][] getTailIntervals() {
		return this.tailIntervals;
	}

	public int getTextureId() {
		return this.textureId;
	}

	public long getSquirt() {
		return this.squirt;
	}

	public int getPriorityPlane() {
		return this.priorityPlane;
	}

	public long getReplaceableId() {
		return this.replaceableId;
	}

}
