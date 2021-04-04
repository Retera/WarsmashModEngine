package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxLight extends MdlxGenericObject {
	public enum Type {
		OMNIDIRECTIONAL("Omnidirectional"),
		DIRECTIONAL("Directional"),
		AMBIENT("Ambient");

		String token;

		Type(final String token) {
			this.token = token;
		}

		public static Type fromId(final int id) {
			return values()[id];
		}

		@Override
		public String toString() {
			return this.token;
		}
	}

	public Type type = Type.OMNIDIRECTIONAL;
	public float[] attenuation = new float[2];
	public float[] color = new float[3];
	public float intensity = 0;
	public float[] ambientColor = new float[3];
	public float ambientIntensity = 0;

	public MdlxLight() {
		super(0x200);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		super.readMdx(reader, version);

		this.type = Type.fromId(reader.readInt32());
		reader.readFloat32Array(this.attenuation);
		reader.readFloat32Array(this.color);
		this.intensity = reader.readFloat32();
		reader.readFloat32Array(this.ambientColor);
		this.ambientIntensity = reader.readFloat32();

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeUInt32(this.type.ordinal());
		writer.writeFloat32Array(this.attenuation);
		writer.writeFloat32Array(this.color);
		writer.writeFloat32(this.intensity);
		writer.writeFloat32Array(this.ambientColor);
		writer.writeFloat32(this.ambientIntensity);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_OMNIDIRECTIONAL:
				this.type = Type.OMNIDIRECTIONAL;
				break;
			case MdlUtils.TOKEN_DIRECTIONAL:
				this.type = Type.DIRECTIONAL;
				break;
			case MdlUtils.TOKEN_AMBIENT:
				this.type = Type.AMBIENT;
				break;
			case MdlUtils.TOKEN_STATIC_ATTENUATION_START:
				this.attenuation[0] = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ATTENUATION_START:
				readTimeline(stream, AnimationMap.KLAS);
				break;
			case MdlUtils.TOKEN_STATIC_ATTENUATION_END:
				this.attenuation[1] = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ATTENUATION_END:
				readTimeline(stream, AnimationMap.KLAE);
				break;
			case MdlUtils.TOKEN_STATIC_INTENSITY:
				this.intensity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_INTENSITY:
				readTimeline(stream, AnimationMap.KLAI);
				break;
			case MdlUtils.TOKEN_STATIC_COLOR:
				stream.readColor(this.color);
				break;
			case MdlUtils.TOKEN_COLOR:
				readTimeline(stream, AnimationMap.KLAC);
				break;
			case MdlUtils.TOKEN_STATIC_AMB_INTENSITY:
				this.ambientIntensity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_AMB_INTENSITY:
				readTimeline(stream, AnimationMap.KLBI);
				break;
			case MdlUtils.TOKEN_STATIC_AMB_COLOR:
				stream.readColor(this.ambientColor);
				break;
			case MdlUtils.TOKEN_AMB_COLOR:
				readTimeline(stream, AnimationMap.KLBC);
				break;
			case MdlUtils.TOKEN_VISIBILITY:
				readTimeline(stream, AnimationMap.KLAV);
				break;
			default:
				throw new RuntimeException("Unknown token in Light: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_LIGHT, this.name);
		writeGenericHeader(stream);

		stream.writeFlag(this.type.toString());

		if (!writeTimeline(stream, AnimationMap.KLAS)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ATTENUATION_START, this.attenuation[0]);
		}

		if (!writeTimeline(stream, AnimationMap.KLAE)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ATTENUATION_END, this.attenuation[1]);
		}

		if (!writeTimeline(stream, AnimationMap.KLAI)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_INTENSITY, this.intensity);
		}

		if (!writeTimeline(stream, AnimationMap.KLAC)) {
			stream.writeColor(MdlUtils.TOKEN_STATIC_COLOR, this.color);
		}

		if (!writeTimeline(stream, AnimationMap.KLBI)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_AMB_INTENSITY, this.ambientIntensity);
		}

		if (!writeTimeline(stream, AnimationMap.KLBC)) {
			stream.writeColor(MdlUtils.TOKEN_STATIC_AMB_COLOR, this.ambientColor);
		}

		writeTimeline(stream, AnimationMap.KLAV);

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 48 + super.getByteLength(version);
	}

	public Type getType() {
		return this.type;
	}

	public float[] getAttenuation() {
		return this.attenuation;
	}

	public float[] getColor() {
		return this.color;
	}

	public float getIntensity() {
		return this.intensity;
	}

	public float[] getAmbientColor() {
		return this.ambientColor;
	}

	public float getAmbientIntensity() {
		return this.ambientIntensity;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public void setAttenuation(final float[] attenuation) {
		this.attenuation = attenuation;
	}

	public void setColor(final float[] color) {
		this.color = color;
	}

	public void setIntensity(final float intensity) {
		this.intensity = intensity;
	}

	public void setAmbientColor(final float[] ambientColor) {
		this.ambientColor = ambientColor;
	}

	public void setAmbientIntensity(final float ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}
}
