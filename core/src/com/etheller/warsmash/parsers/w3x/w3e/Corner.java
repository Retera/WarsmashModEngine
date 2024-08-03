package com.etheller.warsmash.parsers.w3x.w3e;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

/**
 * A tile corner.
 */
public class Corner {
	private float groundHeight;
	private float waterHeight;
	private int mapEdge;
	private int ramp;
	private int blight;
	private int water;
	private int boundary;
	private int groundTexture;
	private int cliffVariation;
	private int groundVariation;
	private int cliffTexture;
	private int layerHeight;

	public Corner() {
		// TODO Auto-generated constructor stub
	}

	public Corner(final Corner other) {
		this.groundHeight = other.groundHeight;
		this.waterHeight = other.waterHeight;
		this.mapEdge = other.mapEdge;
		this.ramp = other.ramp;
		this.blight = other.blight;
		this.water = other.water;
		this.boundary = other.boundary;
		this.groundTexture = other.groundTexture;
		this.cliffVariation = other.cliffVariation;
		this.groundVariation = other.groundVariation;
		this.cliffTexture = other.cliffTexture;
		this.layerHeight = other.layerHeight;
	}

	public void load(final LittleEndianDataInputStream stream) throws IOException {
		this.groundHeight = (stream.readShort() - 8192) / (float) 512;

		final short waterAndEdge = stream.readShort();
		this.waterHeight = ((waterAndEdge & 0x3FFF) - 8192) / (float) 512;
		this.mapEdge = waterAndEdge & 0x4000;

		final short textureAndFlags = ParseUtils.readUInt8(stream);

		this.ramp = textureAndFlags & 0b00010000;
		this.blight = textureAndFlags & 0b00100000;
		this.water = textureAndFlags & 0b01000000;
		this.boundary = textureAndFlags & 0b10000000;

		this.groundTexture = textureAndFlags & 0b00001111;

		final short variation = ParseUtils.readUInt8(stream);

		this.cliffVariation = (variation & 0b11100000) >>> 5;
		this.groundVariation = variation & 0b00011111;

		final short cliffTextureAndLayer = ParseUtils.readUInt8(stream);

		this.cliffTexture = (cliffTextureAndLayer & 0b11110000) >>> 4;
		this.layerHeight = cliffTextureAndLayer & 0b00001111;

	}

	public void save(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeShort((short) ((this.groundHeight * 512f) + 8192f));
		final int mapEdgeWrite = (this.mapEdge != 0) ? 0x4000 : 0;
		stream.writeShort((short) ((int) ((this.waterHeight * 512f) + 8192f) | (mapEdgeWrite)));
		final int rampWrite = (this.ramp != 0) ? 0b00010000 : 0;
		final int blightWrite = (this.blight != 0) ? 0b00100000 : 0;
		final int waterWrite = (this.water != 0) ? 0b01000000 : 0;
		final int boundaryWrite = (this.boundary != 0) ? 0b10000000 : 0;
		ParseUtils.writeUInt8(stream,
				(short) ((rampWrite) | (blightWrite) | (waterWrite) | (boundaryWrite) | this.groundTexture));
		ParseUtils.writeUInt8(stream, (short) ((this.cliffVariation << 5) | this.groundVariation));
		ParseUtils.writeUInt8(stream, (short) ((this.cliffTexture << 4) + this.layerHeight));
	}

	public float getGroundHeight() {
		return this.groundHeight;
	}

	public float getWaterHeight() {
		return this.waterHeight;
	}

	public int getMapEdge() {
		return this.mapEdge;
	}

	public int getRamp() {
		return this.ramp;
	}

	public boolean isRamp() {
		return this.ramp != 0;
	}

	public void setRamp(final int ramp) {
		this.ramp = ramp;
	}

	public int getBlight() {
		return this.blight;
	}

	public boolean setBlight(final boolean flag) {
		final int newBlightValue = flag ? 0b00100000 : 0;
		if (this.blight != newBlightValue) {
			this.blight = newBlightValue;
			return true;
		}
		return false;
	}

	public int getWater() {
		return this.water;
	}

	public int getBoundary() {
		return this.boundary;
	}

	public int getGroundTexture() {
		return this.groundTexture;
	}

	public int getCliffVariation() {
		return this.cliffVariation;
	}

	public int getGroundVariation() {
		return this.groundVariation;
	}

	public int getCliffTexture() {
		return this.cliffTexture;
	}

	public void setCliffTexture(final int cliffTexture) {
		this.cliffTexture = cliffTexture;
	}

	public int getLayerHeight() {
		return this.layerHeight;
	}

	public float computeFinalGroundHeight() {
		return (this.groundHeight + this.layerHeight) - 2.0f;
	}

	public float computeFinalWaterHeight(final float waterOffset) {
		return this.waterHeight + waterOffset;
	}

	public void setWaterHeight(final float waterHeight) {
		this.waterHeight = waterHeight;
	}
}
