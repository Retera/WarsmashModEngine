package com.etheller.warsmash.viewer5.gl;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.deprecated.ShaderUnitDeprecated;

/**
 * This needs a rename. Just a ripoff of ghostwolf's wrapper utility class, it's
 * a utility, not a webgl
 */
public class WebGL {
	public GL20 gl;
	public Map<Integer, ShaderUnitDeprecated> shaderUnits;
	public Map<Integer, ShaderProgram> shaderPrograms;
	public ShaderProgram currentShaderProgram;
	public String floatPrecision;
	public final com.badlogic.gdx.graphics.Texture emptyTexture;
	public ANGLEInstancedArrays instancedArrays;

	public WebGL(final GL20 gl) {
		gl.glDepthFunc(GL20.GL_LEQUAL);
		gl.glEnable(GL20.GL_DEPTH_TEST);

		// TODO here ghostwolf throws exceptions for unsupported versions of opengl

		this.gl = gl;

		this.shaderUnits = new HashMap<>();

		this.shaderPrograms = new HashMap<>();

		this.currentShaderProgram = null;
		this.floatPrecision = "precision mediump float;\n";

		final Pixmap imageData = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				imageData.drawPixel(i, j, 0x000000FF);
			}
		}
		this.emptyTexture = new com.badlogic.gdx.graphics.Texture(imageData);
		this.instancedArrays = Extensions.angleInstancedArrays;
	}

	public ShaderUnitDeprecated createShaderUnit(final String src, final int type) {
		final int hash = stringHash(src); // TODO: why on earth are we doing this, what about hash collisions?
		if (!this.shaderUnits.containsKey(hash)) {
			this.shaderUnits.put(hash, new ShaderUnitDeprecated(this.gl, src, type));
		}
		return this.shaderUnits.get(hash);
	}

	public ShaderProgram createShaderProgram(String vertexSrc, String fragmentSrc) {
		vertexSrc = vertexSrc.replace("mediump", "");
		fragmentSrc = fragmentSrc.replace("mediump", "");
		final Map<Integer, ShaderProgram> shaderPrograms = this.shaderPrograms;

		final int hash = stringHash(vertexSrc + fragmentSrc);
		ShaderProgram.pedantic = false;
		if (!shaderPrograms.containsKey(hash)) {
			shaderPrograms.put(hash, new ShaderProgram(vertexSrc, fragmentSrc));
		}

		final ShaderProgram shaderProgram = shaderPrograms.get(hash);

		if (shaderProgram.isCompiled()) {
			return shaderProgram;
		}
		else {
			System.err.println(shaderProgram.getLog());
			if (true) {
				throw new IllegalStateException("Bad shader");
			}
		}
		return null;
	}

	public void enableVertexAttribs(final int start, final int end) {
		final GL20 gl = this.gl;

		for (int i = start; i < end; i++) {
			gl.glEnableVertexAttribArray(i);
		}
	}

	public void disableVertexAttribs(final int start, final int end) {
		final GL20 gl = this.gl;

		for (int i = start; i < end; i++) {
			gl.glDisableVertexAttribArray(i);
		}
	}

	public void useShaderProgram(final ShaderProgram shaderProgram) {
		final ShaderProgram currentShaderProgram = this.currentShaderProgram;

		if ((shaderProgram != null) && shaderProgram.isCompiled() && (shaderProgram != currentShaderProgram)) {
			int oldAttribs = 0;
			final int newAttribs = shaderProgram.getAttributes().length;

			if (currentShaderProgram != null) {
				oldAttribs = currentShaderProgram.getAttributes().length;
			}

			shaderProgram.begin();

			if (newAttribs > oldAttribs) {
				this.enableVertexAttribs(oldAttribs, newAttribs);
			}
			else if (newAttribs < oldAttribs) {
				this.disableVertexAttribs(newAttribs, oldAttribs);
			}

			this.currentShaderProgram = shaderProgram;
		}
		else if (shaderProgram == null) {
			int oldAttribs = 0;
			final int newAttribs = 0;

			if (currentShaderProgram != null) {
				oldAttribs = currentShaderProgram.getAttributes().length;
				currentShaderProgram.end();
			}

			if (newAttribs > oldAttribs) {
				this.enableVertexAttribs(oldAttribs, newAttribs);
			}
			else if (newAttribs < oldAttribs) {
				this.disableVertexAttribs(newAttribs, oldAttribs);
			}

			this.currentShaderProgram = shaderProgram;
		}
	}

	public void bindTexture(final Texture texture, final int unit) {
		final GL20 gl = this.gl;

		gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);

		if (texture != null /* && texture.ok */) {
			texture.internalBind();
		}
		else {
			this.emptyTexture.bind();
		}
	}

	public void setTextureMode(final int wrapS, final int wrapT, final int magFilter, final int minFilter) {
		final GL20 gl = this.gl;

		// TODO make sure we dont assign this parameter doubly if we're already using
		// libgdx texture, which does do some wrapS and wrapT stuff already
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, wrapS);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, wrapT);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, magFilter);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, minFilter);
	}

	private int stringHash(final String src) {
		return src.hashCode();
	}
}
