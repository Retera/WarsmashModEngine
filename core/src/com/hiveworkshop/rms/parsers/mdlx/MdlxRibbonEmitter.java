package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxRibbonEmitter extends MdlxGenericObject {
	public float heightAbove = 0;
	public float heightBelow = 0;
	public float alpha = 0;
	public float[] color = new float[3];
	public float lifeSpan = 0;
	public long textureSlot = 0;
	public long emissionRate = 0;
	public long rows = 0;
	public long columns = 0;
	public int materialId = 0;
	public float gravity = 0;

	public MdlxRibbonEmitter() {
		super(0x4000);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		super.readMdx(reader, version);

		this.heightAbove = reader.readFloat32();
		this.heightBelow = reader.readFloat32();
		this.alpha = reader.readFloat32();
		reader.readFloat32Array(this.color);
		this.lifeSpan = reader.readFloat32();
		this.textureSlot = reader.readUInt32();
		this.emissionRate = reader.readUInt32();
		this.rows = reader.readUInt32();
		this.columns = reader.readUInt32();
		this.materialId = reader.readInt32();
		this.gravity = reader.readFloat32();

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeFloat32(this.heightAbove);
		writer.writeFloat32(this.heightBelow);
		writer.writeFloat32(this.alpha);
		writer.writeFloat32Array(this.color);
		writer.writeFloat32(this.lifeSpan);
		writer.writeUInt32(this.textureSlot);
		writer.writeUInt32(this.emissionRate);
		writer.writeUInt32(this.rows);
		writer.writeUInt32(this.columns);
		writer.writeInt32(this.materialId);
		writer.writeFloat32(this.gravity);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
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
				throw new RuntimeException("Unknown token in RibbonEmitter " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
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
	public long getByteLength(final int version) {
		return 56 + super.getByteLength(version);
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

	public void setHeightAbove(final float heightAbove) {
		this.heightAbove = heightAbove;
	}

	public void setHeightBelow(final float heightBelow) {
		this.heightBelow = heightBelow;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	public void setColor(final float[] color) {
		this.color = color;
	}

	public void setLifeSpan(final float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public void setTextureSlot(final long textureSlot) {
		this.textureSlot = textureSlot;
	}

	public void setEmissionRate(final long emissionRate) {
		this.emissionRate = emissionRate;
	}

	public void setRows(final long rows) {
		this.rows = rows;
	}

	public void setColumns(final long columns) {
		this.columns = columns;
	}

	public void setMaterialId(final int materialId) {
		this.materialId = materialId;
	}

	public void setGravity(final float gravity) {
		this.gravity = gravity;
	}
}
