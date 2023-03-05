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
	public float[] color = new float[]{1, 1, 1};
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

		lifeSpan = reader.readFloat32();
		emissionRate = reader.readFloat32();
		speed = reader.readFloat32();
		reader.readFloat32Array(color);
		alpha = reader.readFloat32();
		replaceableId = reader.readInt32();
		path = reader.read(260);
		animationVisiblityGuide = reader.read(260);

		readTimelines(reader, size - (reader.position() - position), version);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeFloat32(lifeSpan);
		writer.writeFloat32(emissionRate);
		writer.writeFloat32(speed);
		writer.writeFloat32Array(color);
		writer.writeFloat32(alpha);
		writer.writeInt32(replaceableId);
		writer.writeWithNulls(path, 260);
		writer.writeWithNulls(animationVisiblityGuide, 260);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
				case "SortPrimsFarZ":
					flags |= 0x10000;
					break;
				case "Unshaded":
					flags |= 0x8000;
					break;
				case "Unfogged":
					flags |= 0x40000;
					break;
				case "static LifeSpan":
					lifeSpan = stream.readFloat();
					break;
				case "LifeSpan":
					readTimeline(stream, AnimationMap.KPPL);
					break;
				case "static EmissionRate":
					emissionRate = stream.readFloat();
					break;
				case "EmissionRate":
					readTimeline(stream, AnimationMap.KPPE);
					break;
				case "static Speed":
					speed = stream.readFloat();
					break;
				case "Speed":
					readTimeline(stream, AnimationMap.KPPS);
					break;
				case "static Color":
					stream.readColor(color);
					break;
				case "Color":
					readTimeline(stream, AnimationMap.KPPC);
					break;
				case "static Alpha":
					alpha = stream.readFloat();
					break;
				case "Alpha":
					readTimeline(stream, AnimationMap.KPPA);
					break;
				case "Visibility":
					readTimeline(stream, AnimationMap.KPPV);
					break;
				case "ReplaceableId":
					replaceableId = stream.readInt();
					break;
				case "Path":
					path = stream.read();
					break;
				case "AnimVisibilityGuide":
					animationVisiblityGuide = stream.read();
					break;
				default:
					throw new RuntimeException("Unknown token in MdlxParticleEmitterPopcorn " + name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_PARTICLE_EMITTER2, name);
		writeGenericHeader(stream);

		if ((flags & 0x10000) != 0) {
			stream.writeFlag("SortPrimsFarZ");
		}

		if ((flags & 0x8000) != 0) {
			stream.writeFlag("Unshaded");
		}

		if ((flags & 0x40000) != 0) {
			stream.writeFlag("Unfogged");
		}

		if (!writeTimeline(stream, AnimationMap.KPPL)) {
			stream.writeFloatAttrib("static LifeSpan", lifeSpan);
		}

		if (!writeTimeline(stream, AnimationMap.KPPE)) {
			stream.writeFloatAttrib("static EmissionRate", emissionRate);
		}

		if (!writeTimeline(stream, AnimationMap.KPPS)) {
			stream.writeFloatAttrib("static Speed", speed);
		}

		if (!writeTimeline(stream, AnimationMap.KPPC)) {
			stream.writeFloatArrayAttrib("static Color", color);
		}

		if (!writeTimeline(stream, AnimationMap.KPPA)) {
			stream.writeFloatAttrib("static Alpha", alpha);
		}

		writeTimeline(stream, AnimationMap.KPPV);

		if (replaceableId != 0) {
			stream.writeAttrib("ReplaceableId", replaceableId);
		}

		if (path.length() != 0) {
			stream.writeStringAttrib("Path", path);
		}

		if (animationVisiblityGuide.length() != 0) {
			stream.writeStringAttrib("AnimVisibilityGuide", animationVisiblityGuide);
		}

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 556 + super.getByteLength(version);
	}
}
