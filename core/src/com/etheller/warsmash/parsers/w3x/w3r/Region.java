package com.etheller.warsmash.parsers.w3x.w3r;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class Region {
	private float left;
	private float right;
	private float bottom;
	private float top;
	private String name;
	private int creationNumber;
	private War3ID weatherId;
	private String ambientId;
	private final short[] color = new short[4];

	public void load(final LittleEndianDataInputStream stream, final int version) throws IOException {
		this.left = stream.readFloat();
		this.bottom = stream.readFloat();
		this.right = stream.readFloat();
		this.top = stream.readFloat();
		this.name = ParseUtils.readUntilNull(stream);
		this.creationNumber = stream.readInt();
		this.weatherId = ParseUtils.readWar3ID(stream);
		this.ambientId = ParseUtils.readUntilNull(stream);
		ParseUtils.readUInt8Array(stream, this.color);
	}

	public void save(final LittleEndianDataOutputStream stream, final int version) throws IOException {
		stream.writeFloat(this.left);
		stream.writeFloat(this.bottom);
		stream.writeFloat(this.right);
		stream.writeFloat(this.top);
		ParseUtils.writeWithNullTerminator(stream, this.name);
		stream.writeInt(this.creationNumber);
		ParseUtils.writeWar3ID(stream, this.weatherId);
		ParseUtils.writeWithNullTerminator(stream, this.ambientId);
		ParseUtils.writeUInt8Array(stream, this.color);
	}

	public float getLeft() {
		return this.left;
	}

	public void setLeft(final float left) {
		this.left = left;
	}

	public float getBottom() {
		return this.bottom;
	}

	public void setBottom(final float bottom) {
		this.bottom = bottom;
	}

	public float getRight() {
		return this.right;
	}

	public void setRight(final float right) {
		this.right = right;
	}

	public float getTop() {
		return this.top;
	}

	public void setTop(final float top) {
		this.top = top;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getCreationNumber() {
		return this.creationNumber;
	}

	public void setCreationNumber(final int creationNumber) {
		this.creationNumber = creationNumber;
	}

	public War3ID getWeatherId() {
		return this.weatherId;
	}

	public void setWeatherId(final War3ID weatherId) {
		this.weatherId = weatherId;
	}

	public String getAmbientId() {
		return this.ambientId;
	}

	public void setAmbientId(final String ambientId) {
		this.ambientId = ambientId;
	}

	public short[] getColor() {
		return this.color;
	}
}
