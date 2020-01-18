package com.etheller.warsmash.viewer5.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.GL20;

public class ClientBuffer {
	private final GL20 gl;
	private final int buffer;
	private int size;
	private ByteBuffer arrayBuffer;
	public ByteBuffer byteView;
	public FloatBuffer floatView;

	public ClientBuffer(final GL20 gl) {
		this(gl, 4);
	}

	public ClientBuffer(final GL20 gl, final int size) {
		this.gl = gl;
		this.buffer = gl.glGenBuffer();
		this.arrayBuffer = null;

		this.reserve(size);
	}

	public void reserve(final int size) {
		if (this.size < size) {

			// Ensure the size is on a 4 byte boundary.
			this.size = (int) Math.ceil(size / 4.) * 4;

			this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.buffer);

			this.arrayBuffer = ByteBuffer.allocateDirect(this.size).order(ByteOrder.nativeOrder());
			this.gl.glBufferData(GL20.GL_ARRAY_BUFFER, this.size, this.arrayBuffer, GL20.GL_DYNAMIC_DRAW);
			this.byteView = this.arrayBuffer;
			this.floatView = this.arrayBuffer.asFloatBuffer();

		}
	}

	public void bindAndUpdate() {
		bindAndUpdate(this.size);
	}

	public void bindAndUpdate(final int size) {
		final GL20 gl = this.gl;

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.buffer);
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, size, this.byteView);
	}
}
