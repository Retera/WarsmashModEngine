package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Bone extends GenericObject {
	private int geosetId = -1;
	private int geosetAnimationId = -1;

	public Bone() {
		super(0x100);
	}

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		super.readMdx(stream);

		this.geosetId = stream.readInt();
		this.geosetAnimationId = stream.readInt();
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		super.writeMdx(stream);
		stream.writeInt(this.geosetId);
		stream.writeInt(this.geosetAnimationId);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) {
		for (String token : super.readMdlGeneric(stream)) {
			if (MdlUtils.TOKEN_GEOSETID.equals(token)) {
				token = stream.read();

				if (MdlUtils.TOKEN_MULTIPLE.equals(token)) {
					this.geosetId = -1;
				}
				else {
					this.geosetId = Integer.parseInt(token);
				}
			}
			else if (MdlUtils.TOKEN_GEOSETANIMID.equals(token)) {
				token = stream.read();

				if (MdlUtils.TOKEN_NONE.equals(token)) {
					this.geosetAnimationId = -1;
				}
				else {
					this.geosetAnimationId = Integer.parseInt(token);
				}
			}
			else {
				throw new RuntimeException("Unknown token in Bone " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
		stream.startObjectBlock(MdlUtils.TOKEN_BONE, this.name);
		this.writeGenericHeader(stream);

		if (this.geosetId == -1) {
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETID, MdlUtils.TOKEN_MULTIPLE);
		}
		else {
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETID, this.geosetId);
		}
		if (this.geosetAnimationId == -1) {
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETANIMID, MdlUtils.TOKEN_NONE);
		}
		else {
			stream.writeAttrib(MdlUtils.TOKEN_GEOSETANIMID, this.geosetAnimationId);
		}

		this.writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength() {
		return 8 + super.getByteLength();
	}

	public int getGeosetAnimationId() {
		return this.geosetAnimationId;
	}

	public int getGeosetId() {
		return this.geosetId;
	}
}
