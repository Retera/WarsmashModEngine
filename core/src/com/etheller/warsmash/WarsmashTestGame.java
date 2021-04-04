package com.etheller.warsmash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class WarsmashTestGame extends ApplicationAdapter {
	private int arrayBuffer;
	private int elementBuffer;
	private int VAO;

	@Override
	public void create() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // colour to use when clearing

		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();

		Gdx.gl30.glGenVertexArrays(1, temp);
		this.VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(this.VAO);

		this.shaderProgram = new ShaderProgram(vsSimple, fsSimple);
		if (!this.shaderProgram.isCompiled()) {
			throw new IllegalStateException(this.shaderProgram.getLog());
		}

		this.arrayBuffer = Gdx.gl.glGenBuffer();
		this.elementBuffer = Gdx.gl.glGenBuffer();
		System.out.println("arrayBuffer: " + this.arrayBuffer + ", elementBuffer: " + this.elementBuffer);

		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.arrayBuffer);

		this.shaderProgram.enableVertexAttribute("a_position");
		this.shaderProgram.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, 0);

		Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.elementBuffer);

		final ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(4 * 9);
		vertexByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.vertexBuffer = vertexByteBuffer.asFloatBuffer();

		this.vertexBuffer.put(0, -1f);
		this.vertexBuffer.put(1, -1f);
		this.vertexBuffer.put(2, 0);
		this.vertexBuffer.put(3, 1f);
		this.vertexBuffer.put(4, -1f);
		this.vertexBuffer.put(5, 0);
		this.vertexBuffer.put(6, 0f);
		this.vertexBuffer.put(7, 1f);
		this.vertexBuffer.put(8, 0);

		Gdx.gl.glBufferData(GL20.GL_ARRAY_BUFFER, 9 * 4, null, GL20.GL_STATIC_DRAW);

		final ByteBuffer faceByteBuffer = ByteBuffer.allocateDirect(6);
		faceByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.faceBuffer = faceByteBuffer.asShortBuffer();

		this.faceBuffer.put(0, (short) 0);
		this.faceBuffer.put(1, (short) 1);
		this.faceBuffer.put(2, (short) 2);

		Gdx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 3 * 2, null, GL20.GL_STATIC_DRAW);

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		Gdx.gl30.glBindVertexArray(this.VAO);
		this.shaderProgram.begin();
		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.arrayBuffer);
		Gdx.gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, 9 * 4, this.vertexBuffer);
		Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.elementBuffer);
		Gdx.gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, 3 * 2, this.faceBuffer);
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, 9, GL20.GL_UNSIGNED_SHORT, 0);
//		Gdx.gl.glDrawArrays(GL20.GL_TRIANGLES, 0, 3);
		this.shaderProgram.end();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resize(final int width, final int height) {
	}

	public static final String vsSimple = "\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    void main() {\r\n" + //
			"      gl_Position = vec4(a_position, 1.0);\r\n" + //
			"    }\r\n";

	public static final String fsSimple = "\r\n" + //
			"    void main() {\r\n" + //
			"      gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);\r\n" + //
			"    }\r\n";
	private ShaderProgram shaderProgram;
	private FloatBuffer vertexBuffer;
	private ShortBuffer faceBuffer;

}
