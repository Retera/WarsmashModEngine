package com.etheller.warsmash.viewer5;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.etheller.warsmash.viewer5.handlers.ResourceHandler;

/**
 * Similar to GdxTextureResource, but now I'm probably replacing use of that one
 * with this one. I'm trying to fight the system here and avoid porting
 * Ghostwolf's BLP parser to java, and just use the Java BLP parser that I
 * already had, but the libraries are not playing nicely with each other, so
 * this class is written to be a lower level solution (OpenGL calls instead of
 * LibGDX api) that will work.
 *
 * My theory is that because doing it THIS way works on Retera Model Studio,
 * therefore it should work here as well.
 */
public abstract class RawOpenGLTextureResource extends Texture {
	private static final int BYTES_PER_PIXEL = 4;
	private final int target;
	protected int handle;
	private int width;
	private int height;
	private int wrapS = GL20.GL_CLAMP_TO_EDGE;
	private int wrapT = GL20.GL_CLAMP_TO_EDGE;
	private final int magFilter = GL20.GL_LINEAR;
	private final int minFilter = GL20.GL_LINEAR;

	public RawOpenGLTextureResource(final ModelViewer viewer, final String extension, final PathSolver pathSolver,
			final String fetchUrl, final ResourceHandler handler) {
		super(viewer, extension, pathSolver, fetchUrl, handler);
		final GL20 gl = this.viewer.gl;
		this.handle = gl.glGenTexture();
		this.target = GL20.GL_TEXTURE_2D;
		gl.glBindTexture(this.target, this.handle);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, this.minFilter);
		gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, this.magFilter);
	}

	@Override
	protected void error(final Exception e) {
		e.printStackTrace();
	}

	@Override
	public void bind(final int unit) {
		this.viewer.webGL.bindTexture(this, unit);
	}

	@Override
	public void internalBind() {
		this.viewer.gl.glBindTexture(this.target, this.handle);
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getGlTarget() {
		return this.target;
	}

	@Override
	public int getGlHandle() {
		return this.handle;
	}

	@Override
	public void setWrapS(final boolean wrapS) {
		this.wrapS = wrapS ? GL20.GL_REPEAT : GL20.GL_CLAMP_TO_EDGE;
		final GL20 gl = this.viewer.gl;

		gl.glBindTexture(this.target, this.handle);
		gl.glTexParameteri(this.target, GL20.GL_TEXTURE_WRAP_S, this.wrapS);
	}

	@Override
	public void setWrapT(final boolean wrapT) {
		this.wrapT = wrapT ? GL20.GL_REPEAT : GL20.GL_CLAMP_TO_EDGE;
		final GL20 gl = this.viewer.gl;

		gl.glBindTexture(this.target, this.handle);
		gl.glTexParameteri(this.target, GL20.GL_TEXTURE_WRAP_T, this.wrapT);

	}

	public void update(final BufferedImage image) {
		final GL20 gl = this.viewer.gl;

		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();
		final int[] pixels = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

		final ByteBuffer buffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * BYTES_PER_PIXEL)
				.order(ByteOrder.nativeOrder());
		// 4
		// for
		// RGBA,
		// 3
		// for
		// RGB

		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				final int pixel = pixels[(y * imageWidth) + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
				// Only for RGBA
			}
		}

		buffer.flip();

		gl.glBindTexture(GL20.GL_TEXTURE_2D, this.handle);

//		if ((this.width == imageWidth) && (this.height == imageHeight)) {
//			gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, imageWidth, imageHeight, GL20.GL_RGBA,
//					GL20.GL_UNSIGNED_BYTE, buffer);
//		}
//		else {
		gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_SRGB8_ALPHA8, imageWidth, imageHeight, 0, GL20.GL_RGBA,
				GL20.GL_UNSIGNED_BYTE, buffer);

		this.width = imageWidth;
		this.height = imageHeight;
//		}
	}

}
