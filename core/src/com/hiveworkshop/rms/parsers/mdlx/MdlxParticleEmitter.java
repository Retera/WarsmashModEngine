package com.hiveworkshop.rms.parsers.mdlx;

import java.util.Iterator;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxParticleEmitter extends MdlxGenericObject {
	public float emissionRate = 0;
	public float gravity = 0;
	public float longitude = 0;
	public float latitude = 0;
	public String path = "";
	public float lifeSpan = 0;
	public float speed = 0;

	public MdlxParticleEmitter() {
		super(0x1000);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		super.readMdx(reader, version);

		this.emissionRate = reader.readFloat32();
		this.gravity = reader.readFloat32();
		this.longitude = reader.readFloat32();
		this.latitude = reader.readFloat32();
		this.path = reader.read(260);
		this.lifeSpan = reader.readFloat32();
		this.speed = reader.readFloat32();

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));

		super.writeMdx(writer, version);

		writer.writeFloat32(this.emissionRate);
		writer.writeFloat32(this.gravity);
		writer.writeFloat32(this.longitude);
		writer.writeFloat32(this.latitude);
		writer.writeWithNulls(this.path, 260);
		writer.writeFloat32(this.lifeSpan);
		writer.writeFloat32(this.speed);

		writeNonGenericAnimationChunks(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_EMITTER_USES_MDL:
				this.flags |= 0x8000;
				break;
			case MdlUtils.TOKEN_EMITTER_USES_TGA:
				this.flags |= 0x10000;
				break;
			case MdlUtils.TOKEN_STATIC_EMISSION_RATE:
				this.emissionRate = stream.readFloat();
				break;
			case MdlUtils.TOKEN_EMISSION_RATE:
				readTimeline(stream, AnimationMap.KPEE);
				break;
			case MdlUtils.TOKEN_STATIC_GRAVITY:
				this.gravity = stream.readFloat();
				break;
			case MdlUtils.TOKEN_GRAVITY:
				readTimeline(stream, AnimationMap.KPEG);
				break;
			case MdlUtils.TOKEN_STATIC_LONGITUDE:
				this.longitude = stream.readFloat();
				break;
			case MdlUtils.TOKEN_LONGITUDE:
				readTimeline(stream, AnimationMap.KPLN);
				break;
			case MdlUtils.TOKEN_STATIC_LATITUDE:
				this.latitude = stream.readFloat();
				break;
			case MdlUtils.TOKEN_LATITUDE:
				readTimeline(stream, AnimationMap.KPLT);
				break;
			case MdlUtils.TOKEN_VISIBILITY:
				readTimeline(stream, AnimationMap.KPEV);
				break;
			case MdlUtils.TOKEN_PARTICLE: {
				final Iterator<String> iterator = readAnimatedBlock(stream);
				while (iterator.hasNext()) {
					final String subToken = iterator.next();
					switch (subToken) {
					case MdlUtils.TOKEN_STATIC_LIFE_SPAN:
						this.lifeSpan = stream.readFloat();
						break;
					case MdlUtils.TOKEN_LIFE_SPAN:
						readTimeline(stream, AnimationMap.KPEL);
						break;
					case MdlUtils.TOKEN_STATIC_INIT_VELOCITY:
						this.speed = stream.readFloat();
						break;
					case MdlUtils.TOKEN_INIT_VELOCITY:
						readTimeline(stream, AnimationMap.KPES);
						break;
					case MdlUtils.TOKEN_PATH:
						this.path = stream.read();
						break;
					default:
						throw new RuntimeException(
								"Unknown token in ParticleEmitter " + this.name + "'s Particle: " + subToken);
					}
				}
			}
				break;
			default:
				throw new RuntimeException("Unknown token in ParticleEmitter " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_PARTICLE_EMITTER, this.name);
		writeGenericHeader(stream);

		if ((this.flags & 0x8000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_EMITTER_USES_MDL);
		}

		if ((this.flags & 0x10000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_EMITTER_USES_TGA);
		}

		if (!writeTimeline(stream, AnimationMap.KPEE)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_EMISSION_RATE, this.emissionRate);
		}

		if (!writeTimeline(stream, AnimationMap.KPEG)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_GRAVITY, this.gravity);
		}

		if (!writeTimeline(stream, AnimationMap.KPLN)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LONGITUDE, this.longitude);
		}

		if (!writeTimeline(stream, AnimationMap.KPLT)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LATITUDE, this.latitude);
		}

		writeTimeline(stream, AnimationMap.KPEV);

		stream.startBlock(MdlUtils.TOKEN_PARTICLE);

		if (!writeTimeline(stream, AnimationMap.KPEL)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LIFE_SPAN, this.lifeSpan);
		}

		if (!writeTimeline(stream, AnimationMap.KPES)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_INIT_VELOCITY, this.speed);
		}

		if (((this.flags & 0x8000) != 0) || ((this.flags & 0x10000) != 0)) {
			stream.writeAttrib(MdlUtils.TOKEN_PATH, this.path);
		}

		stream.endBlock();

		writeGenericTimelines(stream);

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 288 + super.getByteLength(version);
	}

	public float getEmissionRate() {
		return this.emissionRate;
	}

	public float getGravity() {
		return this.gravity;
	}

	public float getLongitude() {
		return this.longitude;
	}

	public float getLatitude() {
		return this.latitude;
	}

	public String getPath() {
		return this.path;
	}

	public float getLifeSpan() {
		return this.lifeSpan;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setEmissionRate(final float emissionRate) {
		this.emissionRate = emissionRate;
	}

	public void setGravity(final float gravity) {
		this.gravity = gravity;
	}

	public void setLongitude(final float longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(final float latitude) {
		this.latitude = latitude;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setLifeSpan(final float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}
}
