package com.etheller.warsmash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.Shaders;

public class WarsmashTestGameTextureBuffer extends ApplicationAdapter {
	private int arrayBuffer;
	private int elementBuffer;
	private int VAO;
	private ShaderProgram shaderProgram;
	private FloatBuffer vertexBuffer;
	private ShortBuffer faceBuffer;

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
		vertex3ByteBuffer.order(ByteOrder.nativeOrder());
		this.vertexTextureBuffer3 = vertex3ByteBuffer.asFloatBuffer();

		this.vertexTextureBuffer3.put(0, 0.5f);
		this.vertexTextureBuffer3.put(1, 0);
		this.vertexTextureBuffer3.put(2, 0);
		this.vertexTextureBuffer3.put(3, 0);
		this.vertexTextureBuffer3.put(4, 0);
		this.vertexTextureBuffer3.put(5, 1);
		this.vertexTextureBuffer3.put(6, 0);
		this.vertexTextureBuffer3.put(7, 0);
		this.vertexTextureBuffer3.put(8, 0);
		this.vertexTextureBuffer3.put(9, 0);
		this.vertexTextureBuffer3.put(10, 1);
		this.vertexTextureBuffer3.put(11, 0);
		this.vertexTextureBuffer3.put(12, 0.0f);
		this.vertexTextureBuffer3.put(13, 0.0f);
		this.vertexTextureBuffer3.put(14, 0);
		this.vertexTextureBuffer3.put(15, 1);

//		this.vertexTextureBuffer = Gdx.gl.glGenBuffer();
		this.vertexTexture = Gdx.gl.glGenTexture();

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE15);
//		Gdx.gl.glBindBuffer(GL20.GL_TEXTURE_2D, this.vertexTextureBuffer);
//		Gdx.gl.glBindBuffer(GL20.GL_TEXTURE_2D, 0);

		Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, this.vertexTexture);
		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
		Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL30.GL_RGBA, 4, 1, 0, GL30.GL_RGBA, GL20.GL_FLOAT,
				this.vertexTextureBuffer3);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		Gdx.gl30.glBindVertexArray(this.VAO);
		this.shaderProgram.begin();

		Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.arrayBuffer);
		this.shaderProgram.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, 0);
		this.shaderProgram.enableVertexAttribute("a_position");
		this.shaderProgram.setVertexAttribute("a_position2", 3, GL20.GL_FLOAT, false, 0, 4 * 9);
		this.shaderProgram.enableVertexAttribute("a_position2");
		this.shaderProgram.setVertexAttribute("a_boneNumber", 1, GL20.GL_UNSIGNED_BYTE, false, 1, 4 * 9 * 2);
		this.shaderProgram.enableVertexAttribute("a_boneNumber");

		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE15);
		Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, this.vertexTexture);
		Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, 4, 1, GL30.GL_RGBA, GL20.GL_FLOAT,
				this.vertexTextureBuffer3);
		this.shaderProgram.setUniformi("u_boneMap", 15);
		this.shaderProgram.setUniformf("u_vectorSize", 1f / 4);
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

	public static final String boneTexture = ""//
			+ "    uniform samplerBuffer u_boneMap;\r\n" + //
			// " uniform uint u_vectorSize;\r\n" + //
			"    uniform uint u_rowSize;\r\n" + //
			"    mat4 fetchMatrix(uint column, uint row) {\r\n" + //
			// " column *= u_vectorSize * 4.0;\r\n" + //
			// " row *= u_rowSize;\r\n" + //
			"      // Add in half texel to sample in the middle of the texel.\r\n" + //
			"      // Otherwise, since the sample is directly on the boundry, small floating point errors can cause the sample to get the wrong pixel.\r\n"
			+ //
			"      // This is mostly noticable with NPOT textures, which the bone maps are.\r\n" + //
			// " column += 0.5 * u_vectorSize;\r\n" + //
			// " row += 0.5 * u_rowSize;\r\n" + //
			"      return mat4(texelFetch(u_boneMap, row * u_rowSize + column * 4),\r\n" + //
			"                  texelFetch(u_boneMap, row * u_rowSize + column * 4 + 1),\r\n" + //
			"                  texelFetch(u_boneMap, row * u_rowSize + column * 4 + 2),\r\n" + //
			"                  texelFetch(u_boneMap, row * u_rowSize + column * 4 + 3);\r\n" + //
			"    }";

	public static final String vsSimple = "\r\n" + //
			"\r\n" + //
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec3 a_position2;\r\n" + //
			"    attribute float a_boneNumber;\r\n" + //
			"    varying float fragNumber;\r\n" + //
			Shaders.boneTexture + "\r\n" + //
			"    void main() {\r\n" + //
			"      mat4 bone = fetchMatrix(0.0, 0.0);\r\n" + //
			"      if( a_boneNumber <= 34.5 ) {\r\n" + //
			"        gl_Position = vec4(a_position2.x * bone[0][0], a_position2.y, a_position2.z, 1.0);\r\n" + //
			"      } else {\r\n" + //
			"        gl_Position = vec4(a_position2.x, a_position2.y, a_position2.z, 1.0);\r\n" + //
			"      }\r\n" + //
			"      fragNumber = a_boneNumber;\r\n" + //
			"    }\r\n";

	public static final String fsSimple = "\r\n" + //
			"    varying float fragNumber;\r\n" + //
			Shaders.boneTexture + "\r\n" + //
			"    void main() {\r\n" + //
			"      mat4 bone = fetchMatrix(0.0, 0.0);\r\n" + //
			"      if( fragNumber > 35.5 ) {\r\n" + //
			"        gl_FragColor = bone[0];//vec4(1.0, 0.0, 0.0, 1.0);\r\n" + //
			"      } else if( fragNumber > 34.5 ) {\r\n" + //
			"        gl_FragColor = bone[1];//vec4(0.0, 1.0, 1.0, 1.0);\r\n" + //
			"      } else if( fragNumber > 33.5 ) {\r\n" + //
			"        gl_FragColor = bone[2];//vec4(1.0, 0.0, 1.0, 1.0);\r\n" + //
			"      } else {\r\n" + //
			"        gl_FragColor = vec4(fragNumber*100.0, fragNumber, fragNumber, 1.0);\r\n" + //
			"      }\r\n" + //
			"    }\r\n";
	private int vertexTexture;
	private int vertexTextureBuffer;
	private FloatBuffer vertexTextureBuffer3;

}
