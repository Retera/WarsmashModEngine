package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxParticleEmitterPopcorn extends MdlxGenericObject {
	public float lifeSpan = 0;
	public float emissionRate = 0;
	public float speed = 0;
	public float[] color = new float[] { 1, 1, 1 };
	public float alpha = 0;
	public int replaceableId = 0;
	public String path = "";
	public String animationVisiblityGuide = "";

	public MdlxParticleEmitterPopcorn() {
		super(0);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		super.readMdx(reader, version);

		this.lifeSpan = reader.readFloat32();
		this.emissionRate = reader.readFloat32();
		this.speed = reader.readFloat32();
		reader.readFloat32Array(this.color);
		this.alpha = reader.readFloat32();
		this.replaceableId = reader.readInt32();
		this.path = reader.read(260);
		this.animationVisiblityGuide = reader.read(260);

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeFloat32(this.lifeSpan);
		writer.writeFloat32(this.emissionRate);
		writer.writeFloat32(this.speed);
		writer.writeFloat32Array(this.color);
		writer.writeFloat32(this.alpha);
		writer.writeInt32(this.replaceableId);
		writer.writeWithNulls(this.path, 260);
		writer.writeWithNulls(this.animationVisiblityGuide, 260);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case "SortPrimsFarZ":
				this.flags |= 0x10000;
				break;
			case "Unshaded":
				this.flags |= 0x8000;
				break;
			case "Unfogged":
				this.flags |= 0x40000;
				break;
			case "static LifeSpan":
				this.lifeSpan = stream.readFloat();
				break;
			case "LifeSpan":
				readTimeline(stream, AnimationMap.KPPL);
				break;
			case "static EmissionRate":
				this.emissionRate = stream.readFloat();
				break;
			case "EmissionRate":
				readTimeline(stream, AnimationMap.KPPE);
				break;
			case "static Speed":
				this.speed = stream.readFloat();
				break;
			case "Speed":
				readTimeline(stream, AnimationMap.KPPS);
				break;
			case "static Color":
				stream.readColor(this.color);
				break;
			case "Color":
				readTimeline(stream, AnimationMap.KPPC);
				break;
			case "static Alpha":
				this.alpha = stream.readFloat();
				break;
			case "Alpha":
				readTimeline(stream, AnimationMap.KPPA);
				break;
			case "Visibility":
				readTimeline(stream, AnimationMap.KPPV);
				break;
			case "ReplaceableId":
				this.replaceableId = stream.readInt();
				break;
			case "Path":
				this.path = stream.read();
				break;
			case "AnimVisibilityGuide":
				this.animationVisiblityGuide = stream.read();
				break;
			default:
				throw new RuntimeException("Unknown token in MdlxParticleEmitterPopcorn " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_PARTICLE_EMITTER2, this.name);
		writeGenericHeader(stream);

		if ((this.flags & 0x10000) != 0) {
			stream.writeFlag("SortPrimsFarZ");
		}

		if ((this.flags & 0x8000) != 0) {
			stream.writeFlag("Unshaded");
		}

		if ((this.flags & 0x40000) != 0) {
			stream.writeFlag("Unfogged");
		}

		if (!writeTimeline(stream, AnimationMap.KPPL)) {
			stream.writeFloatAttrib("static LifeSpan", this.lifeSpan);
		}

		if (!writeTimeline(stream, AnimationMap.KPPE)) {
			stream.writeFloatAttrib("static EmissionRate", this.emissionRate);
		}

		if (!writeTimeline(stream, AnimationMap.KPPS)) {
			stream.writeFloatAttrib("static Speed", this.speed);
		}

		if (!writeTimeline(stream, AnimationMap.KPPC)) {
			stream.writeFloatArrayAttrib("static Color", this.color);
		}

		if (!writeTimeline(stream, AnimationMap.KPPA)) {
			stream.writeFloatAttrib("static Alpha", this.alpha);
		}

		writeTimeline(stream, AnimationMap.KPPV);

		if (this.replaceableId != 0) {
			stream.writeAttrib("ReplaceableId", this.replaceableId);
		}

		if (this.path.length() != 0) {
			stream.writeStringAttrib("Path", this.path);
		}

		if (this.animationVisiblityGuide.length() != 0) {
			stream.writeStringAttrib("AnimVisibilityGuide", this.animationVisiblityGuide);
		}

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 556 + super.getByteLength(version);
	}
}
