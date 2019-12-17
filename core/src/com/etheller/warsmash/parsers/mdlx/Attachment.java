package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Attachment extends GenericObject {
	private String path = "";
	private int attachmentId;

	public Attachment() {
		super(0x800);
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

		this.path = ParseUtils.readString(stream, PATH_BYTES_HEAP);
		this.attachmentId = stream.readInt();

		this.readTimelines(stream, size - this.getByteLength());
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());

		super.writeMdx(stream);

		final byte[] bytes = this.path.getBytes(ParseUtils.UTF8);
		stream.write(bytes);
		for (int i = 0; i < (260 - bytes.length); i++) {
			stream.write((byte) 0);
		}
		stream.writeInt(this.attachmentId); // Used to be Int32 in JS

		this.writeNonGenericAnimationChunks(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
		for (final String token : super.readMdlGeneric(stream)) {
			if (MdlUtils.TOKEN_ATTACHMENT_ID.equals(token)) {
				this.attachmentId = stream.readInt();
			}
			else if (MdlUtils.TOKEN_PATH.equals(token)) {
				this.path = stream.read();
			}
			else if (MdlUtils.TOKEN_VISIBILITY.equals(token)) {
				this.readTimeline(stream, AnimationMap.KATV);
			}
			else {
				throw new IOException("Unknown token in Attachment " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_ATTACHMENT, this.name);
		this.writeGenericHeader(stream);

		// flowtsohg asks in his JS:
		// Is this needed? MDX supplies it, but MdlxConv does not use it.
		// Retera:
		// I tried to preserve it when it was shown, but omit it when it was omitted
		// for MDL in Matrix Eater. Matrix Eater's MDL -> MDX is generating them
		// and discarding what was read from the MDL. The Matrix Eater is notably
		// buggy from a cursory read through, and would always omit AttachmentID 0
		// in MDL output.
		stream.writeAttrib(MdlUtils.TOKEN_ATTACHMENT_ID, this.attachmentId);

		if ((this.path != null) && (this.path.length() > 0)) {
			stream.writeStringAttrib(MdlUtils.TOKEN_PATH, this.path);
		}

		this.writeTimeline(stream, AnimationMap.KATV);

		this.writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 268 + super.getByteLength();
	}

	public String getPath() {
		return this.path;
	}

	public int getAttachmentId() {
		return this.attachmentId;
	}
}
