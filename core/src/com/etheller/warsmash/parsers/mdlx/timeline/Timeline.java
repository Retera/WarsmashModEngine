package com.etheller.warsmash.parsers.mdlx.timeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.mdlx.AnimationMap;
import com.etheller.warsmash.parsers.mdlx.Chunk;
import com.etheller.warsmash.parsers.mdlx.InterpolationType;
import com.etheller.warsmash.parsers.mdlx.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.MdlTokenOutputStream;
import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public abstract class Timeline implements Chunk {
	private War3ID name;
	private InterpolationType interpolationType;
	private int globalSequenceId = -1;
	private final List<KeyFrame> keyFrames;

	public War3ID getName() {
		return this.name;
	}

	public Timeline() {
		this.keyFrames = new ArrayList<>();
	}

	public void readMdx(final LittleEndianDataInputStream stream, final War3ID name) throws IOException {
		this.name = name;

		final long keyFrameCount = ParseUtils.readUInt32(stream);

		this.interpolationType = InterpolationType.VALUES[stream.readInt()];
		this.globalSequenceId = stream.readInt();

		for (int i = 0; i < keyFrameCount; i++) {
			final KeyFrame keyFrame = newKeyFrame();

			keyFrame.readMdx(stream, this.interpolationType);

			this.keyFrames.add(keyFrame);
		}
	}

	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeInt(Integer.reverseBytes(this.name.getValue()));
		stream.writeInt(this.keyFrames.size());
		stream.writeInt(this.interpolationType.ordinal());
		stream.writeInt(this.globalSequenceId);

		for (final KeyFrame keyFrame : this.keyFrames) {
			keyFrame.writeMdx(stream, this.interpolationType);
		}
	}

	public void readMdl(final MdlTokenInputStream stream, final War3ID name) throws IOException {
		this.name = name;

		final int keyFrameCount = stream.readInt();

		stream.read(); // {

		final String token = stream.read();
		final InterpolationType interpolationType;
		switch (token) {
		case MdlUtils.TOKEN_DONT_INTERP:
			interpolationType = InterpolationType.DONT_INTERP;
			break;
		case MdlUtils.TOKEN_LINEAR:
			interpolationType = InterpolationType.LINEAR;
			break;
		case MdlUtils.TOKEN_HERMITE:
			interpolationType = InterpolationType.HERMITE;
			break;
		case MdlUtils.TOKEN_BEZIER:
			interpolationType = InterpolationType.BEZIER;
			break;
		default:
			interpolationType = InterpolationType.DONT_INTERP;
			break;
		}

		this.interpolationType = interpolationType;

		if (stream.peek().equals(MdlUtils.TOKEN_GLOBAL_SEQ_ID)) {
			stream.read();
			this.globalSequenceId = stream.readInt();
		}
		else {
			this.globalSequenceId = -1;
		}

		for (int i = 0; i < keyFrameCount; i++) {
			final KeyFrame keyFrame = newKeyFrame();

			keyFrame.readMdl(stream, interpolationType);

			this.keyFrames.add(keyFrame);
		}

		stream.read(); // }
	}

	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startBlock(AnimationMap.ID_TO_TAG.get(this.name).getMdlToken(), this.keyFrames.size());

		String token;
		switch (this.interpolationType) {
		case DONT_INTERP:
			token = MdlUtils.TOKEN_DONT_INTERP;
			break;
		case LINEAR:
			token = MdlUtils.TOKEN_LINEAR;
			break;
		case HERMITE:
			token = MdlUtils.TOKEN_HERMITE;
			break;
		case BEZIER:
			token = MdlUtils.TOKEN_BEZIER;
			break;
		default:
			token = MdlUtils.TOKEN_DONT_INTERP;
			break;
		}

		stream.writeFlag(token);

		if (this.globalSequenceId != -1) {
			stream.writeAttrib(MdlUtils.TOKEN_GLOBAL_SEQ_ID, this.globalSequenceId);
		}

		for (final KeyFrame keyFrame : this.keyFrames) {
			keyFrame.writeMdl(stream, this.interpolationType);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 16 + (this.keyFrames.size() * (4 + (4 * (this.size() * (this.interpolationType.tangential() ? 3 : 1)))));
	}

	protected abstract KeyFrame newKeyFrame();

	protected abstract int size();

	public int getGlobalSequenceId() {
		return globalSequenceId;
	}

	public List<KeyFrame> getKeyFrames() {
		return keyFrames;
	}

	public InterpolationType getInterpolationType() {
		return interpolationType;
	}
}
