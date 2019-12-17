package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Camera extends AnimatedObject {
	protected String name;
	private final float[] position;
	private float fieldOfView;
	private float farClippingPlane;
	private float nearClippingPlane;
	private final float[] targetPosition;

	public Camera() {
		this.position = new float[3];
		this.targetPosition = new float[3];
	}

	/**
	 * Restricts us to only be able to parse models on one thread at a time, in
	 * return for high performance.
	 */
	private static final byte[] NAME_BYTES_HEAP = new byte[80];

	@Override
	public void readMdx(final LittleEndianDataInputStream stream) throws IOException {
		final long size = ParseUtils.readUInt32(stream);

		this.name = ParseUtils.readString(stream, NAME_BYTES_HEAP);
		ParseUtils.readFloatArray(stream, this.position);
		this.fieldOfView = stream.readFloat();
		this.farClippingPlane = stream.readFloat();
		this.nearClippingPlane = stream.readFloat();
		ParseUtils.readFloatArray(stream, this.targetPosition);

		readTimelines(stream, size - 120);
	}

	@Override
	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeUInt32(stream, getByteLength());
		final byte[] bytes = this.name.getBytes(ParseUtils.UTF8);
		stream.write(bytes);
		for (int i = 0; i < (80 - bytes.length); i++) {
			stream.write((byte) 0);
		}
		ParseUtils.writeFloatArray(stream, this.position);
		stream.writeFloat(this.fieldOfView);
		stream.writeFloat(this.farClippingPlane);
		stream.writeFloat(this.nearClippingPlane);
		ParseUtils.writeFloatArray(stream, this.targetPosition);

		writeTimelines(stream);
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream) throws IOException {
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
				throw new IllegalStateException("Unknown token in Camera " + this.name + ": " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream) throws IOException {
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
	public long getByteLength() {
		return 120 + super.getByteLength();
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
}
