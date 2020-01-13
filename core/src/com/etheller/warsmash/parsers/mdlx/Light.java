package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Light extends GenericObject {

	private int type = -1;
	private final float[] attenuation = new float[2];
	private final float[] color = new float[3];
	private float intensity = 0;
	private final float[] ambientColor = new float[3];
	private float ambientIntensity = 0;

	public Light() {
		super(0x200);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		super.readMdx(stream);

		this.type = stream.readInt(); // UInt32 in JS
		ParseUtils.readFloatArray(stream, this.attenuation);
		ParseUtils.readFloatArray(stream, this.color);
		this.intensity = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.ambientColor);
		this.ambientIntensity = stream.readFloat();

		readTimelines(stream, size - this.getByteLength());
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());

		super.writeMdx(stream);

		ParseUtils.writeUInt32(stream, this.type);
		ParseUtils.writeFloatArray(stream, this.attenuation);
		ParseUtils.writeFloatArray(stream, this.color);
		stream.writeFloat(this.intensity);
		ParseUtils.writeFloatArray(stream, this.ambientColor);
		stream.writeFloat(this.ambientIntensity);

		writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_OMNIDIRECTIONAL:
				this.type = 0;
				break;
			case MdlUtils.TOKEN_DIRECTIONAL:
				this.type = 1;
				break;
			case MdlUtils.TOKEN_AMBIENT:
				this.type = 2;
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
				throw new IllegalStateException("Unknown token in Light: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_LIGHT, this.name);
		writeGenericHeader(stream);

		switch (this.type) {
		case 0:
			stream.writeFlag(MdlUtils.TOKEN_OMNIDIRECTIONAL);
			break;
		case 1:
			stream.writeFlag(MdlUtils.TOKEN_DIRECTIONAL);
			break;
		case 2:
			stream.writeFlag(MdlUtils.TOKEN_AMBIENT);
			break;
		default:
			throw new IllegalStateException("Unable to save Light of type: " + this.type);
		}

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
	public long getByteLength() {
		return 48 + super.getByteLength();
	}

	public int getType() {
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

}
