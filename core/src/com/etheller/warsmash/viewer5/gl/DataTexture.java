package com.etheller.warsmash.viewer5.gl;

import java.nio.Buffer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

public class DataTexture {
	public GL20 gl;
	public int texture;
	public int format;
	public int internalFormat;
	public int width = 0;
	public int height = 0;

	public DataTexture(final GL20 gl, final int channels, final int width, final int height) {
		this.gl = gl;
		this.texture = gl.glGenTexture();
		this.format = (channels == 3 ? GL20.GL_RGB : GL20.GL_RGBA);
		this.internalFormat = (channels == 3 ? GL20.GL_RGB : GL30.GL_RGBA32F);

		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.texture);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

		this.reserve(width, height);
	}

	public void reserve(final int width, final int height) {
		if ((this.width < width) || (this.height < height)) {
			final GL20 gl = this.gl;

			this.width = Math.max(this.width, width);
			this.height = Math.max(this.height, height);

			gl.glBindTexture(GL20.GL_TEXTURE_2D, this.texture);
			gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, this.internalFormat, this.width, this.height, 0, this.format,
					GL20.GL_FLOAT, null);
		}

	}

	public void bindAndUpdate(final Buffer buffer) {
		bindAndUpdate(buffer, this.width, this.height);
	}

	public void bindAndUpdate(final Buffer buffer, final int width, final int height) {
		final GL20 gl = this.gl;

		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.texture);
		gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, width, height, this.format, GL20.GL_FLOAT, buffer);
	}

	public void bind(final int unit) {
		final GL20 gl = this.gl;

		gl.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.texture);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
