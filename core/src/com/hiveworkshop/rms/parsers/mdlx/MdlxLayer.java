package com.hiveworkshop.rms.parsers.mdlx;

import java.util.Iterator;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxLayer extends MdlxAnimatedObject {
	public enum FilterMode {
		NONE("None"),
		TRANSPARENT("Transparent"),
		BLEND("Blend"),
		ADDITIVE("Additive"),
		ADDALPHA("AddAlpha"),
		MODULATE("Modulate"),
		MODULATE2X("Modulate2x");

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

		public static FilterMode nameToFilter(final String name) {
			for (final FilterMode mode : values()) {
				if (mode.token.equals(name)) {
					return mode;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return this.token;
		}
	}

	public FilterMode filterMode = FilterMode.NONE;
	public int flags = 0;
	public int textureId = -1;
	public int textureAnimationId = -1;
	public long coordId = 0;
	public float alpha = 1;
	/**
	 * @since 900
	 */
	public float emissiveGain = 1;
	/**
	 * @since 1000
	 */
	public float[] fresnelColor = new float[] { 1, 1, 1 };
	/**
	 * @since 1000
	 */
	public float fresnelOpacity = 0;
	/**
	 * @since 1000
	 */
	public float fresnelTeamColor = 0;

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final int position = reader.position();
		final long size = reader.readUInt32();

		this.filterMode = FilterMode.fromId(reader.readInt32());
		this.flags = reader.readInt32(); // UInt32 in JS
		this.textureId = reader.readInt32();
		this.textureAnimationId = reader.readInt32();
		this.coordId = reader.readInt32();
		this.alpha = reader.readFloat32();

		if (version > 800) {
			this.emissiveGain = reader.readFloat32();

			if (version > 900) {
				reader.readFloat32Array(this.fresnelColor);
				this.fresnelOpacity = reader.readFloat32();
				this.fresnelTeamColor = reader.readFloat32();
			}
		}

		readTimelines(reader, size - (reader.position() - position));
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeUInt32(this.filterMode.ordinal());
		writer.writeUInt32(this.flags);
		writer.writeInt32(this.textureId);
		writer.writeInt32(this.textureAnimationId);
		writer.writeUInt32(this.coordId);
		writer.writeFloat32(this.alpha);

		if (version > 800) {
			writer.writeFloat32(this.emissiveGain);

			if (version > 900) {
				writer.writeFloat32Array(this.fresnelColor);
				writer.writeFloat32(this.fresnelOpacity);
				writer.writeFloat32(this.fresnelTeamColor);
			}
		}

		writeTimelines(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		final Iterator<String> iterator = readAnimatedBlock(stream);
		while (iterator.hasNext()) {
			final String token = iterator.next();
			switch (token) {
			case MdlUtils.TOKEN_FILTER_MODE:
				this.filterMode = FilterMode.fromId(FilterMode.nameToId(stream.read()));
				break;
			case MdlUtils.TOKEN_UNSHADED:
				this.flags |= 0x1;
				break;
			case MdlUtils.TOKEN_SPHERE_ENV_MAP:
				this.flags |= 0x2;
				break;
			case MdlUtils.TOKEN_TWO_SIDED:
				this.flags |= 0x10;
				break;
			case MdlUtils.TOKEN_UNFOGGED:
				this.flags |= 0x20;
				break;
			case MdlUtils.TOKEN_NO_DEPTH_TEST:
				this.flags |= 0x40;
				break;
			case MdlUtils.TOKEN_NO_DEPTH_SET:
				this.flags |= 0x80;
				break;
			case "Unlit":
				this.flags |= 0x100;
			case MdlUtils.TOKEN_STATIC_TEXTURE_ID:
				this.textureId = stream.readInt();
				break;
			case MdlUtils.TOKEN_TEXTURE_ID:
				readTimeline(stream, AnimationMap.KMTF);
				break;
			case MdlUtils.TOKEN_TVERTEX_ANIM_ID:
				this.textureAnimationId = stream.readInt();
				break;
			case MdlUtils.TOKEN_COORD_ID:
				this.coordId = stream.readInt();
				break;
			case MdlUtils.TOKEN_STATIC_ALPHA:
				this.alpha = stream.readFloat();
				break;
			case MdlUtils.TOKEN_ALPHA:
				readTimeline(stream, AnimationMap.KMTA);
				break;
			case "static EmissiveGain":
				this.emissiveGain = stream.readFloat();
				break;
			case "EmissiveGain":
				readTimeline(stream, AnimationMap.KMTE);
				break;
			case "static FresnelColor":
				stream.readColor(this.fresnelColor);
				break;
			case "FresnelColor":
				readTimeline(stream, AnimationMap.KFC3);
				break;
			case "static FresnelOpacity":
				this.fresnelOpacity = stream.readFloat();
				break;
			case "FresnelOpacity":
				readTimeline(stream, AnimationMap.KFCA);
				break;
			case "static FresnelTeamColor":
				this.fresnelTeamColor = stream.readFloat();
				break;
			case "FresnelTeamColor":
				readTimeline(stream, AnimationMap.KFTC);
				break;
			default:
				throw new RuntimeException("Unknown token in Layer: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_LAYER);

		stream.writeAttrib(MdlUtils.TOKEN_FILTER_MODE, this.filterMode.toString());

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNSHADED);
		}

		if ((this.flags & 0x2) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SPHERE_ENV_MAP);
		}

		if ((this.flags & 0x10) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_TWO_SIDED);
		}

		if ((this.flags & 0x20) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_UNFOGGED);
		}

		if ((this.flags & 0x40) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_NO_DEPTH_TEST);
		}

		if ((this.flags & 0x100) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_NO_DEPTH_SET);
		}

		if ((version > 800) && ((this.flags & 0x100) != 0)) {
			stream.writeFlag("Unlit");
		}

		if (!writeTimeline(stream, AnimationMap.KMTF)) {
			stream.writeAttrib(MdlUtils.TOKEN_STATIC_TEXTURE_ID, this.textureId);
		}

		if (this.textureAnimationId != -1) {
			stream.writeAttrib(MdlUtils.TOKEN_TVERTEX_ANIM_ID, this.textureAnimationId);
		}

		if (this.coordId != 0) {
			stream.writeAttribUInt32(MdlUtils.TOKEN_COORD_ID, this.coordId);
		}

		if (!writeTimeline(stream, AnimationMap.KMTA) && (this.alpha != 1)) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_STATIC_ALPHA, this.alpha);
		}

		if (version > 800) {
			if (!writeTimeline(stream, AnimationMap.KMTE) && (this.emissiveGain != 1)) {
				stream.writeFloatAttrib("static EmissiveGain", this.emissiveGain);
			}

			if (!writeTimeline(stream, AnimationMap.KFC3)
					&& ((this.fresnelColor[0] != 1) || (this.fresnelColor[1] != 1) || (this.fresnelColor[2] != 1))) {
				stream.writeFloatArrayAttrib("static FresnelColor", this.fresnelColor);
			}

			if (!writeTimeline(stream, AnimationMap.KFCA) && (this.fresnelOpacity != 0)) {
				stream.writeFloatAttrib("static FresnelOpacity", this.fresnelOpacity);
			}

			if (!writeTimeline(stream, AnimationMap.KFTC) && (this.fresnelTeamColor != 0)) {
				stream.writeFloatAttrib("static FresnelTeamColor", this.fresnelTeamColor);
			}
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		long byteLength = 28 + super.getByteLength(version);

		if (version > 800) {
			byteLength += 4;

			if (version > 900) {
				byteLength += 20;
			}
		}

		return byteLength;
	}

	public FilterMode getFilterMode() {
		return this.filterMode;
	}

	public int getFlags() {
		return this.flags;
	}

	public int getTextureId() {
		return this.textureId;
	}

	public int getTextureAnimationId() {
		return this.textureAnimationId;
	}

	public long getCoordId() {
		return this.coordId;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public float getEmissiveGain() {
		return this.emissiveGain;
	}

	public float[] getFresnelColor() {
		return this.fresnelColor;
	}

	public float getFresnelOpacity() {
		return this.fresnelOpacity;
	}

	public float getFresnelTeamColor() {
		return this.fresnelTeamColor;
	}

	public void setFilterMode(final FilterMode filterMode) {
		this.filterMode = filterMode;
	}

	public void setFlags(final int flags) {
		this.flags = flags;
	}

	public void setTextureId(final int textureId) {
		this.textureId = textureId;
	}

	public void setTextureAnimationId(final int textureAnimationId) {
		this.textureAnimationId = textureAnimationId;
	}

	public void setCoordId(final long coordId) {
		this.coordId = coordId;
	}

	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	public void setEmissiveGain(final float emissiveGain) {
		this.emissiveGain = emissiveGain;
	}

	public void setFresnelColor(final float[] fresnelColor) {
		this.fresnelColor = fresnelColor;
	}

	public void setFresnelOpacity(final float fresnelOpacity) {
		this.fresnelOpacity = fresnelOpacity;
	}

	public void setFresnelTeamColor(final float fresnelTeamColor) {
		this.fresnelTeamColor = fresnelTeamColor;
	}
}
