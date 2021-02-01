package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxParticleEmitter2 extends MdlxGenericObject {
	public enum FilterMode {
		BLEND("Blend"),
		ADDITIVE("Additive"),
		MODULATE("Modulate"),
		MODULATE2X("Modulate2x"),
		ALPHAKEY("AlphaKey");

		String token;

		FilterMode(final String token) {
			this.token = token;
		}

		public static FilterMode fromId(final int id) {
			return values()[id];
		}

		public static int nameToId(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.token.equals(name)) {
					return mode.ordinal();
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return this.token;
		}
	}

	public enum HeadOrTail {
		HEAD("Head", true, false),
		TAIL("Tail", false, true),
		BOTH("Both", true, true);

		String token;
		boolean includesHead;
		boolean includesTail;

		private HeadOrTail(final String token, final boolean includesHead, final boolean includesTail) {
			this.token = token;
			this.includesHead = includesHead;
			this.includesTail = includesTail;
		}

		public static HeadOrTail fromId(final int id) {
			return values()[id];
		}

		public static int nameToId(final String name) {
			for (final HeadOrTail mode : values()) {
				if (mode.token.equals(name)) {
					return mode.ordinal();
				}
			}

			return -1;
		}

		@Override
		public String toString() {
			return this.token;
		}

		public boolean isIncludesHead() {
			return this.includesHead;
		}

		public boolean isIncludesTail() {
			return this.includesTail;
		}
	}

	public float speed = 0;
	public float variation = 0;
	public float latitude = 0;
	public float gravity = 0;
	public float lifeSpan = 0;
	public float emissionRate = 0;
	public float length = 0;
	public float width = 0;
	public FilterMode filterMode = FilterMode.BLEND;
	public long rows = 0;
	public long columns = 0;
	public HeadOrTail headOrTail = HeadOrTail.HEAD;
	public float tailLength = 0;
	public float timeMiddle = 0;
	public final float[][] segmentColors = new float[3][3];
	public short[] segmentAlphas = new short[3]; // unsigned byte[]
	public float[] segmentScaling = new float[3];
	public long[][] headIntervals = new long[2][3];
	public long[][] tailIntervals = new long[2][3];
	public int textureId = -1;
	public long squirt = 0;
	public int priorityPlane = 0;
	public long replaceableId = 0;

	public MdlxParticleEmitter2() {
		super(0x1000);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		super.readMdx(reader, version);

		this.speed = reader.readFloat32();
		this.variation = reader.readFloat32();
		this.latitude = reader.readFloat32();
		this.gravity = reader.readFloat32();
		this.lifeSpan = reader.readFloat32();
		this.emissionRate = reader.readFloat32();
		this.length = reader.readFloat32();
		this.width = reader.readFloat32();
		this.filterMode = FilterMode.fromId(reader.readInt32());
		this.rows = reader.readUInt32();
		this.columns = reader.readUInt32();
		this.headOrTail = HeadOrTail.fromId(reader.readInt32());
		this.tailLength = reader.readFloat32();
		this.timeMiddle = reader.readFloat32();
		reader.readFloat32Array(this.segmentColors[0]);
		reader.readFloat32Array(this.segmentColors[1]);
		reader.readFloat32Array(this.segmentColors[2]);
		reader.readUInt8Array(this.segmentAlphas);
		reader.readFloat32Array(this.segmentScaling);
		reader.readUInt32Array(this.headIntervals[0]);
		reader.readUInt32Array(this.headIntervals[1]);
		reader.readUInt32Array(this.tailIntervals[0]);
		reader.readUInt32Array(this.tailIntervals[1]);
		this.textureId = reader.readInt32();
		this.squirt = reader.readUInt32();
		this.priorityPlane = reader.readInt32();
		this.replaceableId = reader.readUInt32();

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeFloat32(this.speed);
		writer.writeFloat32(this.variation);
		writer.writeFloat32(this.latitude);
		writer.writeFloat32(this.gravity);
		writer.writeFloat32(this.lifeSpan);
		writer.writeFloat32(this.emissionRate);
		writer.writeFloat32(this.length);
		writer.writeFloat32(this.width);
		writer.writeInt32(this.filterMode.ordinal());
		writer.writeUInt32(this.rows);
		writer.writeUInt32(this.columns);
		writer.writeInt32(this.headOrTail.ordinal());
		writer.writeFloat32(this.tailLength);
		writer.writeFloat32(this.timeMiddle);
		writer.writeFloat32Array(this.segmentColors[0]);
		writer.writeFloat32Array(this.segmentColors[1]);
		writer.writeFloat32Array(this.segmentColors[2]);
		writer.writeUInt8Array(this.segmentAlphas);
		writer.writeFloat32Array(this.segmentScaling);
		writer.writeUInt32Array(this.headIntervals[0]);
		writer.writeUInt32Array(this.headIntervals[1]);
		writer.writeUInt32Array(this.tailIntervals[0]);
		writer.writeUInt32Array(this.tailIntervals[1]);
		writer.writeInt32(this.textureId);
		writer.writeUInt32(this.squirt);
		writer.writeInt32(this.priorityPlane);
		writer.writeUInt32(this.replaceableId);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
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
				this.headOrTail = HeadOrTail.HEAD;
				break;
			case MdlUtils.TOKEN_TAIL:
				this.headOrTail = HeadOrTail.TAIL;
				break;
			case MdlUtils.TOKEN_BOTH:
				this.headOrTail = HeadOrTail.BOTH;
				break;
			case MdlUtils.TOKEN_TAIL_LENGTH:
				this.tailLength = stream.readFloat();
				break;
			case MdlUtils.TOKEN_TIME:
				this.timeMiddle = stream.readFloat();
				break;
			case MdlUtils.TOKEN_SEGMENT_COLOR: {
				stream.read(); // {
				for (int i = 0; i < 3; i++) {
					stream.read(); // Color
					stream.readColor(this.segmentColors[i]);
				}
				stream.read(); // }
			}
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
				throw new RuntimeException("Unknown token in ParticleEmitter2 " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
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

		if (!writeTimeline(stream, AnimationMap.KP2S)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_SPEED, this.speed);
		}

		if (!writeTimeline(stream, AnimationMap.KP2R)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_VARIATION, this.variation);
		}

		if (!writeTimeline(stream, AnimationMap.KP2L)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LATITUDE, this.latitude);
		}

		if (!writeTimeline(stream, AnimationMap.KP2G)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_GRAVITY, this.gravity);
		}

		writeTimeline(stream, AnimationMap.KP2V);

		if (this.squirt != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SQUIRT);
		}

		stream.writeFloatAttrib(MdlUtils.TOKEN_LIFE_SPAN, this.lifeSpan);

		if (!writeTimeline(stream, AnimationMap.KP2E)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_EMISSION_RATE, this.emissionRate);
		}

		if (!writeTimeline(stream, AnimationMap.KP2W)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_WIDTH, this.width);
		}

		if (!writeTimeline(stream, AnimationMap.KP2N)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LENGTH, this.length);
		}

		stream.writeFlag(this.filterMode.toString());
		stream.writeAttribUInt32(MdlUtils.TOKEN_ROWS, this.rows);
		stream.writeAttribUInt32(MdlUtils.TOKEN_COLUMNS, this.columns);
		stream.writeFlag(this.headOrTail.toString());
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
	public long getByteLength(final int version) {
		return 175 + super.getByteLength(version);
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

	public HeadOrTail getHeadOrTail() {
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

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	public void setVariation(final float variation) {
		this.variation = variation;
	}

	public void setLatitude(final float latitude) {
		this.latitude = latitude;
	}

	public void setGravity(final float gravity) {
		this.gravity = gravity;
	}

	public void setLifeSpan(final float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public void setEmissionRate(final float emissionRate) {
		this.emissionRate = emissionRate;
	}

	public void setLength(final float length) {
		this.length = length;
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public void setFilterMode(final FilterMode filterMode) {
		this.filterMode = filterMode;
	}

	public void setRows(final long rows) {
		this.rows = rows;
	}

	public void setColumns(final long columns) {
		this.columns = columns;
	}

	public void setHeadOrTail(final HeadOrTail headOrTail) {
		this.headOrTail = headOrTail;
	}

	public void setTailLength(final float tailLength) {
		this.tailLength = tailLength;
	}

	public void setTimeMiddle(final float timeMiddle) {
		this.timeMiddle = timeMiddle;
	}

	public void setSegmentAlphas(final short[] segmentAlphas) {
		this.segmentAlphas = segmentAlphas;
	}

	public void setSegmentScaling(final float[] segmentScaling) {
		this.segmentScaling = segmentScaling;
	}

	public void setHeadIntervals(final long[][] headIntervals) {
		this.headIntervals = headIntervals;
	}

	public void setTailIntervals(final long[][] tailIntervals) {
		this.tailIntervals = tailIntervals;
	}

	public void setTextureId(final int textureId) {
		this.textureId = textureId;
	}

	public void setSquirt(final long squirt) {
		this.squirt = squirt;
	}

	public void setPriorityPlane(final int priorityPlane) {
		this.priorityPlane = priorityPlane;
	}

	public void setReplaceableId(final long replaceableId) {
		this.replaceableId = replaceableId;
	}
}
