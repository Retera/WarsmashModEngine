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
import com.etheller.warsmash.viewer5.Shaders;
import com.etheller.warsmash.viewer5.gl.DataTexture;

public class WarsmashTestGameTextureBuffer2 extends ApplicationAdapter {
	private int arrayBuffer;
	private int elementBuffer;
	private int VAO;

	@Override
	public void create() {
//		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // colour to use when clearing

		final ByteBuffer tempByteBuffer = ByteBuffer.allocateDirect(4);
		tempByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final IntBuffer temp = tempByteBuffer.asIntBuffer();

		Gdx.gl30.glGenVertexArrays(1, temp);
		this.VAO = temp.get(0);

		Gdx.gl30.glBindVertexArray(this.VAO);

		System.out.println(vsSimple);
		this.shaderProgram = new ShaderProgram(vsSimple, fsSimple);
		if (!this.shaderProgram.isCompiled()) {
			throw new IllegalStateException(this.shaderProgram.getLog());
		}

		this.arrayBuffer = Gdx.gl.glGenBuffer();
		this.elementBuffer = Gdx.gl.glGenBuffer();
		System.out.println("arrayBuffer: " + this.arrayBuffer + ", elementBuffer: " + this.elementBuffer);

		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.arrayBuffer);

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

		Gdx.gl.glBufferData(GL20.GL_ARRAY_BUFFER, ((9 * 4) * 2) + 3, null, GL20.GL_STATIC_DRAW);
		Gdx.gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, 9 * 4, this.vertexBuffer);

		final ByteBuffer vertex2ByteBuffer = ByteBuffer.allocateDirect(4 * 9);
		vertex2ByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final FloatBuffer vertexBuffer2 = vertex2ByteBuffer.asFloatBuffer();

		vertexBuffer2.put(0, -1f);
		vertexBuffer2.put(1, -1f);
		vertexBuffer2.put(2, 0);
		vertexBuffer2.put(3, 1f);
		vertexBuffer2.put(4, -1f);
		vertexBuffer2.put(5, 0);
		vertexBuffer2.put(6, 0f);
		vertexBuffer2.put(7, 1f);
		vertexBuffer2.put(8, 0);

		Gdx.gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 9 * 4, 9 * 4, vertexBuffer2);

		final ByteBuffer skinByteBuffer = ByteBuffer.allocateDirect(3);
		skinByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		skinByteBuffer.put((byte) 34);
		skinByteBuffer.put((byte) 35);
		skinByteBuffer.put((byte) 36);
		skinByteBuffer.clear();
		Gdx.gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 9 * 4 * 2, 3, skinByteBuffer);

//		this.shaderProgram.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, 0);
//		this.shaderProgram.enableVertexAttribute("a_position");
//		this.shaderProgram.setVertexAttribute("a_position2", 3, GL20.GL_FLOAT, false, 0, 4 * 9);
//		this.shaderProgram.enableVertexAttribute("a_position2");
//		this.shaderProgram.setVertexAttribute("a_boneNumber", 1, GL20.GL_UNSIGNED_BYTE, false, 1, 4 * 9 * 2);
//		this.shaderProgram.enableVertexAttribute("a_boneNumber");
//		this.shaderProgram.setUniformi("u_boneMap", 15);

		final ByteBuffer faceByteBuffer = ByteBuffer.allocateDirect(6);
		faceByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.faceBuffer = faceByteBuffer.asShortBuffer();

		this.faceBuffer.put(0, (short) 0);
		this.faceBuffer.put(1, (short) 1);
		this.faceBuffer.put(2, (short) 2);

		Gdx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 3 * 2, null, GL20.GL_STATIC_DRAW);

		final int glGetError = Gdx.gl.glGetError();
		System.out.println(glGetError);

		final ByteBuffer vertex3ByteBuffer = ByteBuffer.allocateDirect(4 * 16);
		vertex3ByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final FloatBuffer vertexBuffer3 = vertex3ByteBuffer.asFloatBuffer();

		vertexBuffer3.put(0, 1);
		vertexBuffer3.put(1, 0);
		vertexBuffer3.put(2, 0);
		vertexBuffer3.put(3, 0);
		vertexBuffer3.put(4, 0);
		vertexBuffer3.put(5, 1);
		vertexBuffer3.put(6, 0);
		vertexBuffer3.put(7, 0);
		vertexBuffer3.put(8, 0);
		vertexBuffer3.put(9, 0);
		vertexBuffer3.put(10, 1);
		vertexBuffer3.put(11, 0);
		vertexBuffer3.put(12, 0.0f);
		vertexBuffer3.put(13, 0.0f);
		vertexBuffer3.put(14, 0);
		vertexBuffer3.put(15, 1);

		this.dataTexture = new DataTexture(Gdx.gl, 4, 4, 1);
		this.dataTexture.bindAndUpdate(vertexBuffer3);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		Gdx.gl30.glBindVertexArray(this.VAO);
		this.shaderProgram.begin();

		this.dataTexture.bind(15);

		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.arrayBuffer);
		this.shaderProgram.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, 0);
		this.shaderProgram.enableVertexAttribute("a_position");
		this.shaderProgram.setVertexAttribute("a_position2", 3, GL20.GL_FLOAT, false, 0, 4 * 9);
		this.shaderProgram.enableVertexAttribute("a_position2");
		this.shaderProgram.setVertexAttribute("a_boneNumber", 1, GL20.GL_UNSIGNED_BYTE, false, 1, 4 * 9 * 2);
		this.shaderProgram.enableVertexAttribute("a_boneNumber");

		this.shaderProgram.setUniformi("u_boneMap", 15);
		this.shaderProgram.setUniformf("u_vectorSize", 1f / this.dataTexture.getWidth());
		this.shaderProgram.setUniformf("u_rowSize", 1);

		Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.elementBuffer);
		Gdx.gl.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, 3 * 2, this.faceBuffer);
		Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, 3, GL20.GL_UNSIGNED_SHORT, 0);
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
			"\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec3 a_position2;\r\n" + //
			"    attribute float a_boneNumber;\r\n" + //
			"    varying float fragNumber;\r\n" + //
			Shaders.boneTexture + "\r\n" + //
			"    void main() {\r\n" + //
			"      mat4 bone = fetchMatrix(0.0, 0.0);\r\n" + //
			"      gl_Position = bone * vec4(a_position2.x, a_position2.y, a_position2.z, 1.0);\r\n" + //
			"      fragNumber = a_boneNumber;\r\n" + //
			"    }\r\n";

	public static final String fsSimple = "\r\n" + //
			"    varying float fragNumber;\r\n" + //
			"    void main() {\r\n" + //
			"      if( fragNumber > 35.5 ) {\r\n" + //
			"        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\r\n" + //
			"      } else if( fragNumber > 34.5 ) {\r\n" + //
			"        gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);\r\n" + //
			"      } else if( fragNumber > 33.5 ) {\r\n" + //
			"        gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);\r\n" + //
			"      } else {\r\n" + //
			"        gl_FragColor = vec4(fragNumber*100.0, fragNumber, fragNumber, 1.0);\r\n" + //
			"      }\r\n" + //
			"    }\r\n";
	private ShaderProgram shaderProgram;
	private FloatBuffer vertexBuffer;
	private ShortBuffer faceBuffer;
	private DataTexture dataTexture;

}
