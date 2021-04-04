package com.etheller.warsmash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

public class WarsmashTestGame3 extends ApplicationAdapter {
	private int VBO;
	private int VAO;

	@Override
	public void create() {
		Gdx.gl30.glBindVertexArray(0);

		this.vertexByteBuffer = ByteBuffer.allocateDirect(4 * 9);
		this.vertexByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final FloatBuffer vertexBuffer = this.vertexByteBuffer.asFloatBuffer();

		vertexBuffer.put(0, -0.5f);
		vertexBuffer.put(1, -0.5f);
		vertexBuffer.put(2, 0);
		vertexBuffer.put(3, 0.5f);
		vertexBuffer.put(4, -0.5f);
		vertexBuffer.put(5, 0);
		vertexBuffer.put(6, 0f);
		vertexBuffer.put(7, 0.5f);
		vertexBuffer.put(8, 0);
		vertexBuffer.clear();

		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl30.glEnable(GL20.GL_DEPTH_TEST);
//		Gdx.gl30.glEnable(GL20.GL_CULL_FACE);

		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();

		Gdx.gl30.glGenVertexArrays(1, temp);
		this.VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(this.VAO);

		temp.clear();
		Gdx.gl30.glGenBuffers(1, temp);
		this.VBO = temp.get(0);

		Gdx.gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.VBO);
		Gdx.gl30.glBufferData(GL30.GL_ARRAY_BUFFER, 9 * 4, this.vertexByteBuffer, GL30.GL_STATIC_DRAW);

		Gdx.gl30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 3 * 4, 0);
		Gdx.gl30.glEnableVertexAttribArray(0);

		final int vertexShader = Gdx.gl30.glCreateShader(GL30.GL_VERTEX_SHADER);
		Gdx.gl30.glShaderSource(vertexShader, vsSimple);
		Gdx.gl30.glCompileShader(vertexShader);

		temp.clear();
		Gdx.gl30.glGetShaderiv(vertexShader, GL30.GL_COMPILE_STATUS, temp);
		int success = temp.get(0);
		if (success == 0) {
			final String infoLog = Gdx.gl30.glGetShaderInfoLog(vertexShader);
			System.err.println(infoLog);
			throw new IllegalStateException("bad vertex shader");
		}

		final int fragmentShader = Gdx.gl30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
		Gdx.gl30.glShaderSource(fragmentShader, fsSimple);
		Gdx.gl30.glCompileShader(fragmentShader);

		temp.clear();
		Gdx.gl30.glGetShaderiv(fragmentShader, GL30.GL_COMPILE_STATUS, temp);
		success = temp.get(0);
		if (success == 0) {
			final String infoLog = Gdx.gl30.glGetShaderInfoLog(fragmentShader);
			System.err.println(infoLog);
			throw new IllegalStateException("bad fragment shader");
		}

		this.shaderProgram = Gdx.gl30.glCreateProgram();

		Gdx.gl30.glAttachShader(this.shaderProgram, vertexShader);
		Gdx.gl30.glAttachShader(this.shaderProgram, fragmentShader);
		Gdx.gl30.glLinkProgram(this.shaderProgram);

		temp.clear();
		Gdx.gl30.glGetProgramiv(this.shaderProgram, GL30.GL_LINK_STATUS, temp);
		success = temp.get(0);
		if (success == 0) {
			final String infoLog = Gdx.gl30.glGetProgramInfoLog(this.shaderProgram);
			System.err.println(infoLog);
			throw new IllegalStateException("bad program");
		}

		Gdx.gl30.glDeleteShader(vertexShader);
		Gdx.gl30.glDeleteShader(fragmentShader);
	}

	@Override
	public void render() {

		Gdx.gl30.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl30.glBindVertexArray(this.VAO);
		Gdx.gl30.glUseProgram(this.shaderProgram);
		Gdx.gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.VBO);
		Gdx.gl30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 3 * 4, 0);
		Gdx.gl30.glEnableVertexAttribArray(0);

		Gdx.gl30.glDrawArrays(GL30.GL_TRIANGLES, 0, 3);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resize(final int width, final int height) {
		final int side = Math.min(width, height);
		Gdx.gl30.glViewport((width - side) / 2, (height - side) / 2, side, side);

	}

	public static final String vsSimple = "\r\n" + //
			"#version 450 core\r\n" + //
			"    layout(location = 0) in vec3 aPos;\r\n" + //
			"    void main() {\r\n" + //
			"      gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\r\n" + //
			"    }\r\n";

	public static final String fsSimple = "\r\n" + //
			"#version 450 core\r\n" + //
			"    out vec4 FragColor;\r\n" + //
			"    void main() {\r\n" + //
			"      FragColor = vec4(0.2f, 1.0f, 0.2f, 1.0f);\r\n" + //
			"    }\r\n";
	private int shaderProgram;

	private ByteBuffer vertexByteBuffer;

}
