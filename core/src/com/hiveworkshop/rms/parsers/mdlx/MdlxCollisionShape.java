package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxCollisionShape extends MdlxGenericObject {
	public enum Type {
		BOX,
		PLANE,
		SPHERE,
		CYLINDER;

		private static final Type[] VALUES = values();

		private final boolean boundsRadius;

		Type() {
			this.boundsRadius = false;
		}

		Type(final boolean boundsRadius) {
			this.boundsRadius = boundsRadius;
		}

		public boolean isBoundsRadius() {
			return this.boundsRadius;
		}

		public static Type from(final int index) {
			return VALUES[index];
		}
	}

	public MdlxCollisionShape.Type type = Type.SPHERE;
	public final float[][] vertices = { new float[3], new float[3] };
	public float boundsRadius = 0;

	public MdlxCollisionShape() {
		super(0x2000);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		super.readMdx(reader, version);

		this.type = MdlxCollisionShape.Type.from(reader.readInt32());
		reader.readFloat32Array(this.vertices[0]);

		if (this.type != Type.SPHERE) {
			reader.readFloat32Array(this.vertices[1]);
		}

		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			this.boundsRadius = reader.readFloat32();
		}
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		super.writeMdx(writer, version);

		writer.writeInt32(this.type.ordinal());
		writer.writeFloat32Array(this.vertices[0]);

		if (this.type != MdlxCollisionShape.Type.SPHERE) {
			writer.writeFloat32Array(this.vertices[1]);
		}

		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			writer.writeFloat32(this.boundsRadius);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : super.readMdlGeneric(stream)) {
			switch (token) {
			case MdlUtils.TOKEN_BOX:
				this.type = Type.BOX;
				break;
			case MdlUtils.TOKEN_PLANE:
				this.type = Type.PLANE;
				break;
			case MdlUtils.TOKEN_SPHERE:
				this.type = Type.SPHERE;
				break;
			case MdlUtils.TOKEN_CYLINDER:
				this.type = Type.CYLINDER;
				break;
			case MdlUtils.TOKEN_VERTICES: {
				final int count = stream.readInt();
				stream.read(); // {
				stream.readFloatArray(this.vertices[0]);
				if (count == 2) {
					stream.readFloatArray(this.vertices[1]);
				}
				stream.read(); // }
			}
				break;
			case MdlUtils.TOKEN_BOUNDSRADIUS:
				this.boundsRadius = stream.readFloat();
				break;
			default:
				throw new RuntimeException("Unknown token in CollisionShape " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_COLLISION_SHAPE, this.name);
		writeGenericHeader(stream);
		final String type;
		int vertices = 2;
		switch (this.type) {
		case BOX:
			type = MdlUtils.TOKEN_BOX;
			break;
		case PLANE:
			type = MdlUtils.TOKEN_PLANE;
			break;
		case SPHERE: {
			type = MdlUtils.TOKEN_SPHERE;
			vertices = 1;
		}
			break;
		case CYLINDER:
			type = MdlUtils.TOKEN_CYLINDER;
			break;
		default:
			throw new IllegalStateException("Invalid type in CollisionShape " + this.name + ": " + this.type);
		}

		stream.writeFlag(type);
		stream.startBlock(MdlUtils.TOKEN_VERTICES, vertices);
		stream.writeFloatArray(this.vertices[0]);
		if (vertices == 2) {
			stream.writeFloatArray(this.vertices[1]);
		}
		stream.endBlock();

		if (this.type.boundsRadius) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_BOUNDSRADIUS, this.boundsRadius);
		}

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		long size = 16 + super.getByteLength(version);

		if (this.type != Type.SPHERE) {
			size += 12;
		}

		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			size += 4;
		}

		return size;
	}

	public MdlxCollisionShape.Type getType() {
		return this.type;
	}

	public float[][] getVertices() {
		return this.vertices;
	}

	public float getBoundsRadius() {
		return this.boundsRadius;
	}

	public void setType(final MdlxCollisionShape.Type type) {
		this.type = type;
	}

	public void setBoundsRadius(final float boundsRadius) {
		this.boundsRadius = boundsRadius;
	}
}
