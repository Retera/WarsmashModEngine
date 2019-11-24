package com.etheller.warsmash.viewer5.deprecated;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class ShaderUnitDeprecated {

	public boolean ok;
	private final int webglResource;
	private final String src;
	private final int shaderType;

	public ShaderUnitDeprecated(final GL20 gl, final String src, final int type) {
		final int id = gl.glCreateShader(type);
		this.ok = false;
		this.webglResource = id;
		this.src = src;
		this.shaderType = type;

		gl.glShaderSource(id, src);
		gl.glCompileShader(id);

		final IntBuffer success = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder()).asIntBuffer();
		gl.glGetShaderiv(id, GL20.GL_COMPILE_STATUS, success);
		throw new UnsupportedOperationException("Not yet implemented, probably using library instead");
	}

}
