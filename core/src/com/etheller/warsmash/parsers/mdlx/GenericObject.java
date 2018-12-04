package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.util.ParseUtils;
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
	private static final Charset UTF8 = Charset.forName("utf-8");
	private String name;
	private int objectId;
	private int parentId;
	private int flags;

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

	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.parseUInt32(stream);
		stream.read(NAME_BYTES_HEAP);
		this.name = new String(NAME_BYTES_HEAP, UTF8);
		this.objectId = stream.readInt();
		this.parentId = stream.readInt();
		this.flags = stream.readInt();
		readTimelines(stream, size - 96);
	}

	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt((int) getGenericByteLength());
		final byte[] bytes = this.name.getBytes(UTF8);
		for (int i = 0; i < (80 - bytes.length); i++) {
			stream.write((byte) 0);
		}
		stream.writeInt(this.objectId);
		stream.writeInt(this.parentId);
		stream.writeInt(this.flags); // UInt32 in ghostwolf JS, shouldn't matter for Java

		for (final Timeline timeline : eachTimeline(true)) {
			timeline.writeMdx(stream);
		}
	}

	public void writeNonGenericAnimationChunks(final LittleEndianDataOutputStream stream) throws IOException {
		for (final Timeline timeline : eachTimeline(false)) {
			timeline.writeMdx(stream);
		}
	}

	protected abstract Iterable<Timeline> eachTimeline(boolean generic);

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

	private static final class WrappedMdlTokenIterator implements Iterator<String> {
		private final Iterator<String> delegate;

		public WrappedMdlTokenIterator(final Iterator<String> delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean hasNext() {
			return this.delegate.hasNext();
		}

		@Override
		public String next() {
			final String token = this.delegate.next();

			return null;
		}

	}
}
