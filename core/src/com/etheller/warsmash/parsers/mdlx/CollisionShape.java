package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class CollisionShape extends GenericObject {
	public static enum Type {
		BOX,
		PLANE,
		SPHERE,
		CYLINDER;

		private static final Type[] VALUES = values();

		private final boolean boundsRadius;

		private Type() {
			this.boundsRadius = false;
		}

		private Type(final boolean boundsRadius) {
			this.boundsRadius = boundsRadius;
		}

		public boolean isBoundsRadius() {
			return this.boundsRadius;
		}

		public static Type from(final int index) {
			return VALUES[index];
		}
	}

	private CollisionShape.Type type;
	private final float[][] vertices = { new float[3], new float[3] };
	private float boundsRadius;

	public CollisionShape() {
		super(0x2000);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		super.readMdx(stream);

		final long typeIndex = ParseUtils.readUInt32(stream);
		this.type = CollisionShape.Type.from((int) typeIndex);
		ParseUtils.readFloatArray(stream, this.vertices[0]);

		if (this.type != Type.SPHERE) {
			ParseUtils.readFloatArray(stream, this.vertices[1]);
		}
		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			this.boundsRadius = stream.readFloat();
		}
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		super.writeMdx(stream);

		ParseUtils.writeUInt32(stream, this.type == null ? -1 : this.type.ordinal());
		ParseUtils.writeFloatArray(stream, this.vertices[0]);
		if (this.type != CollisionShape.Type.SPHERE) {
			ParseUtils.writeFloatArray(stream, this.vertices[1]);
		}
		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			stream.writeFloat(this.boundsRadius);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) {
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
			case MdlUtils.TOKEN_VERTICES:
				final int count = stream.readInt();
				stream.read(); // {

				stream.readFloatArray(this.vertices[0]);
				if (count == 2) {
					stream.readFloatArray(this.vertices[1]);
				}

				stream.read(); // }
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
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_COLLISION_SHAPE, this.name);
		writeGenericHeader(stream);
		String type;
		int vertices = 2;
		switch (this.type) {
		case BOX:
			type = MdlUtils.TOKEN_BOX;
			break;
		case PLANE:
			type = MdlUtils.TOKEN_PLANE;
			break;
		case SPHERE:
			type = MdlUtils.TOKEN_SPHERE;
			vertices = 1;
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
	public long getByteLength() {
		long size = 16 + super.getByteLength();

		if (this.type != Type.SPHERE) {
			size += 12;
		}

		if ((this.type == Type.SPHERE) || (this.type == Type.CYLINDER)) {
			size += 4;
		}

		return size;
	}

	public float[][] getVertices() {
		return this.vertices;
	}

	public CollisionShape.Type getType() {
		return this.type;
	}

	public float getBoundsRadius() {
		return this.boundsRadius;
	}

}
