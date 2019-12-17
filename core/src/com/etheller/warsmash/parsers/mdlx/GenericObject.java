package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A generic object.
 *
 * The parent class for all objects that exist in the world, and may contain
 * spatial animations. This includes bones, particle emitters, and many other
 * things.
 *
 * Based on the works of Chananya Freiman.
 */
public abstract class GenericObject extends AnimatedObject implements Chunk {
	protected String name;
	private int objectId;
	private int parentId;
	protected int flags;

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static final byte[] NAME_BYTES_HEAP = new byte[80];

	public GenericObject(final int flags) {
		this.name = "";
		this.objectId = -1;
		this.parentId = -1;
		this.flags = flags;
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);
		this.name = ParseUtils.readString(stream, NAME_BYTES_HEAP);
		this.objectId = stream.readInt();
		this.parentId = stream.readInt();
		this.flags = stream.readInt(); // Used to be Int32 in JS
		readTimelines(stream, size - 96);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getGenericByteLength());
		final byte[] bytes = this.name.getBytes(ParseUtils.UTF8);
		stream.write(bytes);
		for (int i = 0; i < (80 - bytes.length); i++) {
			stream.write((byte) 0);
		}
		stream.writeInt(this.objectId);
		stream.writeInt(this.parentId);
		stream.writeInt(this.flags); // UInt32 in ghostwolf JS, shouldn't matter for Java

		for (final Timeline<?> timeline : eachTimeline(true)) {
			timeline.writeMdx(stream);
		}
	}

	public void writeNonGenericAnimationChunks(final LittleEndianDataOutputStream stream) throws IOException {
		for (final Timeline<?> timeline : eachTimeline(false)) {
			timeline.writeMdx(stream);
		}
	}

	protected final Iterable<String> readMdlGeneric(final MdlTokenInputStream stream) {
		this.name = stream.read();
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return new WrappedMdlTokenIterator(GenericObject.this.readAnimatedBlock(stream), GenericObject.this,
						stream);
			}
		};
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

		if ((this.flags & 0x2) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_ROTATION + " }");
		}

		if ((this.flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_TRANSLATION + " }");
		}

		if ((this.flags & 0x4) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_DONT_INHERIT + " { " + MdlUtils.TOKEN_SCALING + " }");
		}
	}

	public void writeGenericTimelines(final MdlTokenOutputStream stream) throws IOException {
		this.writeTimeline(stream, AnimationMap.KGTR);
		this.writeTimeline(stream, AnimationMap.KGRT);
		this.writeTimeline(stream, AnimationMap.KGSC);
	}

	public Iterable<Timeline<?>> eachTimeline(final boolean generic) {
		return new TimelineMaskingIterable(generic);
	}

	public long getGenericByteLength() {
		long size = 96;
		for (final Chunk animation : eachTimeline(true)) {
			size += animation.getByteLength();
		}
		return size;
	}

	@Override
	public long getByteLength() {
		return 96 + super.getByteLength();
	}

	private final class TimelineMaskingIterable implements Iterable<Timeline<?>> {
		private final boolean generic;

		private TimelineMaskingIterable(final boolean generic) {
			this.generic = generic;
		}

		@Override
		public Iterator<Timeline<?>> iterator() {
			return new TimelineMaskingIterator(this.generic, GenericObject.this.timelines);
		}
	}

	private static final class TimelineMaskingIterator implements Iterator<Timeline<?>> {
		private final boolean wantGeneric;
		private final Iterator<Timeline<?>> delegate;
		private boolean hasNext;
		private Timeline<?> next;

		public TimelineMaskingIterator(final boolean wantGeneric, final List<Timeline<?>> timelines) {
			this.wantGeneric = wantGeneric;
			this.delegate = timelines.iterator();
			scanUntilNext();
		}

		private boolean isGeneric(final Timeline<?> timeline) {
			final War3ID name = timeline.getName();
			final boolean generic = AnimationMap.KGTR.getWar3id().equals(name)
					|| AnimationMap.KGRT.getWar3id().equals(name) || AnimationMap.KGSC.getWar3id().equals(name);
			return generic;
		}

		private void scanUntilNext() {
			boolean hasNext = false;
			if (hasNext = this.delegate.hasNext()) {
				do {
					this.next = this.delegate.next();
				}
				while ((isGeneric(this.next) != this.wantGeneric) && (hasNext = this.delegate.hasNext()));
			}
			if (!hasNext) {
				this.next = null;
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@Override
		public Timeline<?> next() {
			final Timeline<?> last = this.next;
			scanUntilNext();
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove is not supported");
		}

	}

	private static final class WrappedMdlTokenIterator implements Iterator<String> {
		private final Iterator<String> delegate;
		private final GenericObject updatingObject;
		private final MdlTokenInputStream stream;
		private String next;
		private boolean hasLoaded = false;

		public WrappedMdlTokenIterator(final Iterator<String> delegate, final GenericObject updatingObject,
				final MdlTokenInputStream stream) {
			this.delegate = delegate;
			this.updatingObject = updatingObject;
			this.stream = stream;
		}

		@Override
		public boolean hasNext() {
			if (this.delegate.hasNext()) {
				this.next = read();
				this.hasLoaded = true;
				return this.next != null;
			}
			return false;
		}

		@Override
		public String next() {
			if (!this.hasLoaded) {
				this.next = read();
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
							this.updatingObject.flags |= 0x2;
							break;
						case MdlUtils.TOKEN_TRANSLATION:
							this.updatingObject.flags |= 0x1;
							break;
						case MdlUtils.TOKEN_SCALING:
							this.updatingObject.flags |= 0x0;
							break;
						}
					}
					token = null;
					break;
				case MdlUtils.TOKEN_TRANSLATION:
					try {
						this.updatingObject.readTimeline(this.stream, AnimationMap.KGTR);
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
					token = null;
					break;
				case MdlUtils.TOKEN_ROTATION:
					try {
						this.updatingObject.readTimeline(this.stream, AnimationMap.KGRT);
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
					token = null;
					break;
				case MdlUtils.TOKEN_SCALING:
					try {
						this.updatingObject.readTimeline(this.stream, AnimationMap.KGSC);
					}
					catch (final IOException e) {
						throw new RuntimeException(e);
					}
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
}
