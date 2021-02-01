package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;

public class MdlxCamera extends MdlxAnimatedObject {
	public String name = "";
	public float[] position = new float[3];
	public float fieldOfView = 0;
	public float farClippingPlane = 0;
	public float nearClippingPlane = 0;
	public float[] targetPosition = new float[3];

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		final long size = reader.readUInt32();

		this.name = reader.read(80);
		reader.readFloat32Array(this.position);
		this.fieldOfView = reader.readFloat32();
		this.farClippingPlane = reader.readFloat32();
		this.nearClippingPlane = reader.readFloat32();
		reader.readFloat32Array(this.targetPosition);

		readTimelines(reader, size - 120);
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeWithNulls(this.name, 80);
		writer.writeFloat32Array(this.position);
		writer.writeFloat32(this.fieldOfView);
		writer.writeFloat32(this.farClippingPlane);
		writer.writeFloat32(this.nearClippingPlane);
		writer.writeFloat32Array(this.targetPosition);

		writeTimelines(writer);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		this.name = stream.read();

		for (final String token : stream.readBlock()) {
			switch (token) {
			case MdlUtils.TOKEN_POSITION:
				stream.readFloatArray(this.position);
				break;
			case MdlUtils.TOKEN_TRANSLATION:
				readTimeline(stream, AnimationMap.KCTR);
				break;
			case MdlUtils.TOKEN_ROTATION:
				readTimeline(stream, AnimationMap.KCRL);
				break;
			case MdlUtils.TOKEN_FIELDOFVIEW:
				this.fieldOfView = stream.readFloat();
				break;
			case MdlUtils.TOKEN_FARCLIP:
				this.farClippingPlane = stream.readFloat();
				break;
			case MdlUtils.TOKEN_NEARCLIP:
				this.nearClippingPlane = stream.readFloat();
				break;
			case MdlUtils.TOKEN_TARGET:
				for (final String subToken : stream.readBlock()) {
					switch (subToken) {
					case MdlUtils.TOKEN_POSITION:
						stream.readFloatArray(this.targetPosition);
						break;
					case MdlUtils.TOKEN_TRANSLATION:
						readTimeline(stream, AnimationMap.KTTR);
						break;
					default:
						throw new IllegalStateException(
								"Unknown token in Camera " + this.name + "'s Target: " + subToken);
					}
				}
				break;
			default:
				throw new RuntimeException("Unknown token in Camera " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startObjectBlock(MdlUtils.TOKEN_CAMERA, this.name);

		stream.writeFloatArrayAttrib(MdlUtils.TOKEN_POSITION, this.position);
		writeTimeline(stream, AnimationMap.KCTR);
		writeTimeline(stream, AnimationMap.KCRL);
		stream.writeFloatAttrib(MdlUtils.TOKEN_FIELDOFVIEW, this.fieldOfView);
		stream.writeFloatAttrib(MdlUtils.TOKEN_FARCLIP, this.farClippingPlane);
		stream.writeFloatAttrib(MdlUtils.TOKEN_NEARCLIP, this.nearClippingPlane);

		stream.startBlock(MdlUtils.TOKEN_TARGET);
		stream.writeFloatArrayAttrib(MdlUtils.TOKEN_POSITION, this.targetPosition);
		writeTimeline(stream, AnimationMap.KTTR);
		stream.endBlock();

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		return 120 + super.getByteLength(version);
	}

	public String getName() {
		return this.name;
	}

	public float[] getPosition() {
		return this.position;
	}

	public float getFieldOfView() {
		return this.fieldOfView;
	}

	public float getFarClippingPlane() {
		return this.farClippingPlane;
	}

	public float getNearClippingPlane() {
		return this.nearClippingPlane;
	}

	public float[] getTargetPosition() {
		return this.targetPosition;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPosition(final float[] position) {
		this.position = position;
	}

	public void setFieldOfView(final float fieldOfView) {
		this.fieldOfView = fieldOfView;
	}

	public void setFarClippingPlane(final float farClippingPlane) {
		this.farClippingPlane = farClippingPlane;
	}

	public void setNearClippingPlane(final float nearClippingPlane) {
		this.nearClippingPlane = nearClippingPlane;
	}

	public void setTargetPosition(final float[] targetPosition) {
		this.targetPosition = targetPosition;
	}
}
