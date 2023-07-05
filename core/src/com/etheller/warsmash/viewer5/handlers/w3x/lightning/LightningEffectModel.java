package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class LightningEffectModel extends Model<LightningEffectModelHandler> {
	private final War3ID typeId;
	private final String textureFilePath;
	private final float avgSegLen;
	private final float width;
	private final float[] color;
	private final float noiseScale;
	private final float texCoordScale;
	private final float duration;
	private final int version;
	public int elementBuffer;
	protected Texture texture;

	public LightningEffectModel(LightningEffectModelHandler handler, ModelViewer viewer, String extension,
								PathSolver pathSolver, String fetchUrl, War3ID typeId, String textureFilePath,
								float avgSegLen, float width, float[] color, float noiseScale, float texCoordScale,
								float duration, int version) {
		super(handler, viewer, extension, pathSolver, fetchUrl);
		this.typeId = typeId;
		this.textureFilePath = textureFilePath;
		this.avgSegLen = avgSegLen;
		this.width = width;
		this.color = color;
		this.noiseScale = noiseScale;
		this.texCoordScale = texCoordScale;
		this.duration = duration;
		this.version = version;
	}

	public War3ID getTypeId() {
		return typeId;
	}

	public String getTextureFilePath() {
		return textureFilePath;
	}

	public Texture getTexture() {
		return texture;
	}

	public float getAvgSegLen() {
		return avgSegLen;
	}

	public float getWidth() {
		return width;
	}

	public float[] getColor() {
		return color;
	}

	public float getNoiseScale() {
		return noiseScale;
	}

	public float getTexCoordScale() {
		return texCoordScale;
	}

	public float getDuration() {
		return duration;
	}

	public int getVersion() {
		return version;
	}

	@Override
	protected ModelInstance createInstance(int type) {
		return new LightningEffectNode(this);
	}

	@Override
	protected void lateLoad() {

	}

	@Override
	protected void load(InputStream src, Object options) {
		String path = textureFilePath;
		if ("".equals(path)) {
			path = "Textures\\white.blp";
		}

		try {
			this.texture = (Texture) viewer.load(path, pathSolver, solverParams);
			this.texture.setWrapS(true);

			this.bounds.fromExtents(new float[]{-width, -width, -width}, new float[]{width, width, width}, width);

			GL20 gl = Gdx.gl;
			elementBuffer = gl.glGenBuffer();
			// TODO probably dont need to bind here if we're not going to do some preliminary glBufferData call,
			// but maybe we should do such a call (isn't this inefficient?)
			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		}
		catch (Exception exc) {
			exc.printStackTrace();
			this.texture = (Texture) viewer.load("Textures\\white.blp", pathSolver, solverParams);
		}

	}

	@Override
	protected void error(Exception e) {
		e.printStackTrace();
	}
}
