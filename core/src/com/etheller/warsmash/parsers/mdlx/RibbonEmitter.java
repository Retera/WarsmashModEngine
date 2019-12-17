package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class RibbonEmitter extends GenericObject {
	private float heightAbove = 0;
	private float heightBelow = 0;
	private float alpha = 0;
	private final float[] color = new float[3];
	private float lifeSpan = 0;
	private long textureSlot = 0;
	private long emissionRate = 0;
	private long rows = 0;
	private long columns = 0;
	private int materialId = 0;
	private float gravity = 0;

	public RibbonEmitter() {
		super(0x4000);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		super.readMdx(stream);

		this.heightAbove = stream.readFloat();
		this.heightBelow = stream.readFloat();
		this.alpha = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.color);
		this.lifeSpan = stream.readFloat();
		this.textureSlot = ParseUtils.readUInt32(stream);
		this.emissionRate = ParseUtils.readUInt32(stream);
		this.rows = ParseUtils.readUInt32(stream);
		this.columns = ParseUtils.readUInt32(stream);
		this.materialId = stream.readInt();
		this.gravity = stream.readFloat();

		readTimelines(stream, size - getByteLength());
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());

		super.writeMdx(stream);

		stream.writeFloat(this.heightAbove);
		stream.writeFloat(this.heightBelow);
		stream.writeFloat(this.alpha);
		ParseUtils.writeFloatArray(stream, this.color);
		stream.writeFloat(this.lifeSpan);
		ParseUtils.writeUInt32(stream, this.textureSlot);
		ParseUtils.writeUInt32(stream, this.emissionRate);
		ParseUtils.writeUInt32(stream, this.rows);
		ParseUtils.writeUInt32(stream, this.columns);
		stream.writeInt(this.materialId);
		stream.writeFloat(this.gravity);

		writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_STATIC_HEIGHT_ABOVE:
				this.heightAbove = stream.readFloat();
				break;
			case MdlUtils.TOKEN_HEIGHT_ABOVE:
				readTimeline(stream, AnimationMap.KRHA);
				break;
			case MdlUtils.TOKEN_STATIC_HEIGHT_BELOW:
				this.heightBelow = stream.readFloat();
				break;
			case MdlUtils.TOKEN_HEIGHT_BELOW:
				readTimeline(stream, AnimationMap.KRHB);
				break;
			case MdlUtils.TOKEN_STATIC_ALPHA:
				this.alpha = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ALPHA:
				readTimeline(stream, AnimationMap.KRAL);
				break;
			case MdlUtils.TOKEN_STATIC_COLOR:
				stream.readColor(this.color);
				break;
			case MdlUtils.TOKEN_COLOR:
				readTimeline(stream, AnimationMap.KRCO);
				break;
			case MdlUtils.TOKEN_STATIC_TEXTURE_SLOT:
				this.textureSlot = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_TEXTURE_SLOT:
				readTimeline(stream, AnimationMap.KRTX);
				break;
			case MdlUtils.TOKEN_VISIBILITY:
				readTimeline(stream, AnimationMap.KRVS);
				break;
			case MdlUtils.TOKEN_EMISSION_RATE:
				this.emissionRate = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_LIFE_SPAN:
				this.lifeSpan = stream.readFloat();
				break;
			case MdlUtils.TOKEN_GRAVITY:
				this.gravity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ROWS:
				this.rows = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_COLUMNS:
				this.columns = stream.readUInt32();
				break;
			case MdlUtils.TOKEN_MATERIAL_ID:
				this.materialId = stream.readInt();
				break;
			default:
				throw new IllegalStateException("Unknown token in RibbonEmitter " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_RIBBON_EMITTER, this.name);
		writeGenericHeader(stream);

		if (!writeTimeline(stream, AnimationMap.KRHA)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_HEIGHT_ABOVE, this.heightAbove);
		}

		if (!writeTimeline(stream, AnimationMap.KRHB)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_HEIGHT_BELOW, this.heightBelow);
		}

		if (!writeTimeline(stream, AnimationMap.KRAL)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ALPHA, this.alpha);
		}

		if (!writeTimeline(stream, AnimationMap.KRCO)) {
			stream.writeColor(MdlUtils.TOKEN_STATIC_COLOR, this.color);
		}

		if (!writeTimeline(stream, AnimationMap.KRTX)) {
			stream.writeAttribUInt32(MdlUtils.TOKEN_STATIC_TEXTURE_SLOT, this.textureSlot);
		}

		writeTimeline(stream, AnimationMap.KRVS);

		stream.writeAttribUInt32(MdlUtils.TOKEN_EMISSION_RATE, this.emissionRate);
		stream.writeFloatAttrib(MdlUtils.TOKEN_LIFE_SPAN, this.lifeSpan);

		if (this.gravity != 0) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_GRAVITY, this.gravity);
		}

		stream.writeAttribUInt32(MdlUtils.TOKEN_ROWS, this.rows);
		stream.writeAttribUInt32(MdlUtils.TOKEN_COLUMNS, this.columns);
		stream.writeAttrib(MdlUtils.TOKEN_MATERIAL_ID, this.materialId);

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 56 + super.getByteLength();
	}

	public float getHeightAbove() {
		return this.heightAbove;
	}

	public float getHeightBelow() {
		return this.heightBelow;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float[] getColor() {
		return this.color;
	}

	public float getLifeSpan() {
		return this.lifeSpan;
	}

	public long getTextureSlot() {
		return this.textureSlot;
	}

	public long getEmissionRate() {
		return this.emissionRate;
	}

	public long getRows() {
		return this.rows;
	}

	public long getColumns() {
		return this.columns;
	}

	public int getMaterialId() {
		return this.materialId;
	}

	public float getGravity() {
		return this.gravity;
	}

}
