package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxBone extends MdlxGenericObject {
	public int geosetId = -1;
	public int geosetAnimationId = -1;

	public MdlxBone() {
		super(0x100);
	}

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		super.readMdx(reader, version);

		this.geosetId = reader.readInt32();
		this.geosetAnimationId = reader.readInt32();
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		super.writeMdx(writer, version);

		writer.writeInt32(this.geosetId);
		writer.writeInt32(this.geosetAnimationId);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
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
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_BONE, this.name);
		writeGenericHeader(stream);

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

		writeGenericTimelines(stream);
		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 8 + super.getByteLength(version);
	}

	public int getGeosetId() {
		return this.geosetId;
	}

	public int getGeosetAnimationId() {
		return this.geosetAnimationId;
	}

	public void setGeosetId(final int geosetId) {
		this.geosetId = geosetId;
	}

	public void setGeosetAnimationId(final int geosetAnimationId) {
		this.geosetAnimationId = geosetAnimationId;
	}
}
