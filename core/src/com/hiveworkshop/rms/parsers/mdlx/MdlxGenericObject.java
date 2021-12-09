package com.hiveworkshop.rms.parsers.mdlx;

import java.util.Iterator;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

/**
 * A generic object.
 * <p>
 * The parent class for all objects that exist in the world, and may contain
 * spatial animations. This includes bones, particle emitters, and many other
 * things.
 * <p>
 * Based on the works of Chananya Freiman.
 */
public abstract class MdlxGenericObject extends MdlxAnimatedObject {
	public String name = "";
	public int objectId = -1;
	public int parentId = -1;
	public int flags = 0;

	public MdlxGenericObject(final int flags) {
		this.flags = flags;
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long size = reader.readUInt32();

		this.name = reader.read(80);
		this.objectId = reader.readInt32();
		this.parentId = reader.readInt32();
		this.flags = reader.readInt32();

		this.readTimelines(reader, size - 96);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(this.getGenericByteLength(version));
		writer.writeWithNulls(this.name, 80);
		writer.writeInt32(this.objectId);
		writer.writeInt32(this.parentId);
		writer.writeInt32(this.flags);

		for (final MdlxTimeline<?> timeline : this.timelines) {
			if (this.isGeneric(timeline)) {
				timeline.writeMdx(writer);
			}
		}
	}

	public void writeNonGenericAnimationChunks(final BinaryWriter writer) {
		for (final MdlxTimeline<?> timeline : this.timelines) {
			if (!this.isGeneric(timeline)) {
				timeline.writeMdx(writer);
			}
		}
	}

	protected final Iterable<String> readMdlGeneric(final MdlTokenInputStream stream) {
		this.name = stream.read();
		return () -> new WrappedMdlTokenIterator(this.readAnimatedBlock(stream), MdlxGenericObject.this, stream);
	}

	public void writeGenericHeader(final MdlTokenOutputStream stream) {
		stream.writeAttrib(MdlUtils.TOKEN_OBJECTID, this.objectId);

		if (this.parentId != -1) {
			stream.writeAttrib("Parent", this.parentId);
		}

		if ((this.flags & 0x40) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_BILLBOARDED_LOCK_Z);
		}

		if ((this.flags & 0x20) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_BILLBOARDED_LOCK_Y);
		}

		if ((this.flags & 0x10) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_BILLBOARDED_LOCK_X);
		}

		if ((this.flags & 0x8) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_BILLBOARDED);
		}

		if ((this.flags & 0x80) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_CAMERA_ANCHORED);
		}

		if ((this.flags & 0x4) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_ROTATION + " }");
		}

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_TRANSLATION + " }");
		}

		if ((this.flags & 0x2) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_SCALING + " }");
		}
	}

	public void writeGenericTimelines(final MdlTokenOutputStream stream) {
		this.writeTimeline(stream, AnimationMap.KGTR);
		this.writeTimeline(stream, AnimationMap.KGRT);
		this.writeTimeline(stream, AnimationMap.KGSC);
	}

	public long getGenericByteLength(final int version) {
		long size = 96;

		for (final MdlxTimeline<?> timeline : this.timelines) {
			if (this.isGeneric(timeline)) {
				size += timeline.getByteLength();
			}
		}

		return size;
	}

	public boolean isGeneric(final MdlxTimeline<?> timeline) {
		final AnimationMap type = AnimationMap.ID_TO_TAG.get(timeline.name);

		return (type == AnimationMap.KGTR) || (type == AnimationMap.KGRT) || (type == AnimationMap.KGSC);
	}

	@Override
	public long getByteLength(final int version) {
		return 96 + super.getByteLength(version);
	}

	public String getName() {
		return this.name;
	}

	public int getObjectId() {
		return this.objectId;
	}

	public int getParentId() {
		return this.parentId;
	}

	public int getFlags() {
		return this.flags;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setObjectId(final int objectId) {
		this.objectId = objectId;
	}

	public void setParentId(final int parentId) {
		this.parentId = parentId;
	}

	public void setFlags(final int flags) {
		this.flags = flags;
	}

	private static final class WrappedMdlTokenIterator implements Iterator<String> {
		private final Iterator<String> delegate;
		private final MdlxGenericObject updatingObject;
		private final MdlTokenInputStream stream;
		private String next;
		private boolean hasLoaded = false;

		public WrappedMdlTokenIterator(final Iterator<String> delegate, final MdlxGenericObject updatingObject,
				final MdlTokenInputStream stream) {
			this.delegate = delegate;
			this.updatingObject = updatingObject;
			this.stream = stream;
		}

		@Override
		public boolean hasNext() {
			if (this.delegate.hasNext()) {
				this.next = this.read();
				this.hasLoaded = true;
				return this.next != null;
			}
			return false;
		}

		@Override
		public String next() {
			if (!this.hasLoaded) {
				this.next = this.read();
			}
			this.hasLoaded = false;
			return this.next;
		}

		private String read() {
			String token;
			InteriorParsing: do {
				token = this.delegate.next();
				if (token == null) {
					break;
				}
				switch (token) {
				case MdlUtils.TOKEN_OBJECTID:
					this.updatingObject.objectId = Integer.parseInt(this.delegate.next());
					token = null;
					break;
				case MdlUtils.TOKEN_PARENT:
					this.updatingObject.parentId = Integer.parseInt(this.delegate.next());
					token = null;
					break;
				case MdlUtils.TOKEN_BILLBOARDED_LOCK_Z:
					this.updatingObject.flags |= 0x40;
					token = null;
					break;
				case MdlUtils.TOKEN_BILLBOARDED_LOCK_Y:
					this.updatingObject.flags |= 0x20;
					token = null;
					break;
				case MdlUtils.TOKEN_BILLBOARDED_LOCK_X:
					this.updatingObject.flags |= 0x10;
					token = null;
					break;
				case MdlUtils.TOKEN_BILLBOARDED:
					this.updatingObject.flags |= 0x8;
					token = null;
					break;
				case MdlUtils.TOKEN_CAMERA_ANCHORED:
					this.updatingObject.flags |= 0x80;
					token = null;
					break;
				case MdlUtils.TOKEN_DONT_INHERIT:
					for (final String subToken : this.stream.readBlock()) {
						switch (subToken) {
						case MdlUtils.TOKEN_ROTATION:
							this.updatingObject.flags |= 0x4;
							break;
						case MdlUtils.TOKEN_TRANSLATION:
							this.updatingObject.flags |= 0x1;
							break;
						case MdlUtils.TOKEN_SCALING:
							this.updatingObject.flags |= 0x2;
							break;
						}
					}
					token = null;
					break;
				case MdlUtils.TOKEN_TRANSLATION:
					this.updatingObject.readTimeline(this.stream, AnimationMap.KGTR);
					token = null;
					break;
				case MdlUtils.TOKEN_ROTATION:
					this.updatingObject.readTimeline(this.stream, AnimationMap.KGRT);
					token = null;
					break;
				case MdlUtils.TOKEN_SCALING:
					this.updatingObject.readTimeline(this.stream, AnimationMap.KGSC);
					token = null;
					break;
				default:
					break InteriorParsing;
				}
			}
			while (this.delegate.hasNext());
			return token;
		}

	}
}
