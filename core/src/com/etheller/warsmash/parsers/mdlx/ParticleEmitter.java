package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.Iterator;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class ParticleEmitter extends GenericObject {
	private float emissionRate = 0;
	private float gravity = 0;
	private float longitude = 0;
	private float latitude = 0;
	private String path = "";
	private float lifeSpan = 0;
	private float speed = 0;

	public ParticleEmitter() {
		super(0x1000);
	}

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static final byte[] PATH_BYTES_HEAP = new byte[260];

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		super.readMdx(stream);

		this.emissionRate = stream.readFloat();
		this.gravity = stream.readFloat();
		this.longitude = stream.readFloat();
		this.latitude = stream.readFloat();
		this.path = ParseUtils.readString(stream, PATH_BYTES_HEAP);
		this.lifeSpan = stream.readFloat();
		this.speed = stream.readFloat();

		readTimelines(stream, size - this.getByteLength());
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());

		super.writeMdx(stream);

		stream.writeFloat(this.emissionRate);
		stream.writeFloat(this.gravity);
		stream.writeFloat(this.longitude);
		stream.writeFloat(this.latitude);
		final byte[] bytes = this.path.getBytes(ParseUtils.UTF8);
		stream.write(bytes);
		for (int i = 0; i < (PATH_BYTES_HEAP.length - bytes.length); i++) {
			stream.write((byte) 0);
		}
		stream.writeFloat(this.lifeSpan);
		stream.writeFloat(this.speed);

		writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
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
			case MdlUtils.TOKEN_PARTICLE:
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
						throw new IllegalStateException(
								"Unknown token in ParticleEmitter " + this.name + "'s Particle: " + subToken);
					}
				}
				break;
			default:
				throw new IllegalStateException("Unknown token in ParticleEmitter " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_PARTICLE_EMITTER, this.name);
		writeGenericHeader(stream);

		if ((this.flags & 0x8000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_EMITTER_USES_MDL);
		}

		if ((this.flags & 0x10000) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_EMITTER_USES_TGA);
		}

		if (!this.writeTimeline(stream, AnimationMap.KPEE)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_EMISSION_RATE, this.emissionRate);
		}

		if (!this.writeTimeline(stream, AnimationMap.KPEG)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_GRAVITY, this.gravity);
		}

		if (!this.writeTimeline(stream, AnimationMap.KPLN)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LONGITUDE, this.longitude);
		}

		if (!this.writeTimeline(stream, AnimationMap.KPLT)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LATITUDE, this.latitude);
		}

		this.writeTimeline(stream, AnimationMap.KPEV);

		stream.startBlock(MdlUtils.TOKEN_PARTICLE);

		if (!this.writeTimeline(stream, AnimationMap.KPEL)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_LIFE_SPAN, this.lifeSpan);
		}

		if (!this.writeTimeline(stream, AnimationMap.KPES)) {
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
	public long getByteLength() {
		return 288 + super.getByteLength();
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

}
