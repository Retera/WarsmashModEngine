package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.timeline.Timeline;
import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * Based on the works of Chananya Freiman.
 *
 */
public abstract class AnimatedObject implements Chunk, MdlxBlock {
	protected final List<Timeline> timelines;

	public AnimatedObject() {
		this.timelines = new ArrayList<>();
	}

	public void readTimelines(final LittleEndianDataInputStream stream, long size) throws IOException {
		while (size > 0) {
			final War3ID name = new War3ID(Integer.reverseBytes(stream.readInt()));
			final Timeline timeline = AnimationMap.ID_TO_TAG.get(name).getImplementation().createTimeline();

			timeline.readMdx(stream, name);

			size -= timeline.getByteLength();

			this.timelines.add(timeline);
		}
	}

	public void writeTimelines(final LittleEndianDataOutputStream stream) throws IOException {
		for (final Timeline timeline : this.timelines) {
			timeline.writeMdx(stream);
		}
	}

	public Iterator<String> readAnimatedBlock(final MdlTokenInputStream stream) {
		return new TransformedAnimatedBlockIterator(stream.readBlock().iterator());
	}

	public void readTimeline(final MdlTokenInputStream stream, final AnimationMap name) throws IOException {
		final Timeline timeline = name.getImplementation().createTimeline();

		timeline.readMdl(stream, name.getWar3id());

		this.timelines.add(timeline);
	}

	public boolean writeTimeline(final MdlTokenOutputStream stream, final AnimationMap name) throws IOException {
		for (final Timeline timeline : this.timelines) {
			if (timeline.getName().equals(name.getWar3id())) {
				timeline.writeMdl(stream);
				return true;
			}
		}
		return false;
	}

	@Override
	public long getByteLength() {
		long size = 0;
		for (final Timeline timeline : this.timelines) {
			size += timeline.getByteLength();
		}
		return size;
	}

	/**
	 * TODO: This code uses StringBuilder implicitly during string concat. This
	 * should be upgraded for performance.
	 *
	 * @author micro
	 *
	 */
	private static final class TransformedAnimatedBlockIterator implements Iterator<String> {
		private final Iterator<String> delegate;

		public TransformedAnimatedBlockIterator(final Iterator<String> delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean hasNext() {
			return this.delegate.hasNext();
		}

		@Override
		public String next() {
			final String token = this.delegate.next();
			if (token.equals(MdlUtils.TOKEN_STATIC) && hasNext()) {
				return MdlUtils.TOKEN_STATIC + " " + this.delegate.next();
			}
			return token;
		}

	}
}
