package com.etheller.warsmash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.datasources.CompoundDataSourceDescriptor;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.datasources.FolderDataSourceDescriptor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

public class WarsmashTestGameAttributes2 extends ApplicationAdapter {
	private int arrayBuffer;
	private int elementBuffer;
	private int VAO;
	private DataSource codebase;

	@Override
	public void create() {
		final FolderDataSourceDescriptor war3mpq = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\War3.mpq\\war3.mpq");
		final FolderDataSourceDescriptor testingFolder = new FolderDataSourceDescriptor(
				"D:\\NEEDS_ORGANIZING\\MPQBuild\\Test");
		this.codebase = new CompoundDataSourceDescriptor(Arrays.<DataSourceDescriptor>asList(war3mpq, testingFolder))
				.createDataSource();

		final MdlxModel model;
		try {
			model = new MdlxModel(this.codebase.read("Buildings\\Other\\TempArtB\\TempArtB.mdx"));
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}

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

		Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.elementBuffer);

		final MdlxGeoset geoset0 = model.getGeosets().get(0);
		final float[] vertices = geoset0.getVertices();
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

		final ByteBuffer faceByteBuffer = ByteBuffer.allocateDirect(6);
		faceByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		this.faceBuffer = faceByteBuffer.asShortBuffer();

		this.faceBuffer.put(0, (short) 0);
		this.faceBuffer.put(1, (short) 1);
		this.faceBuffer.put(2, (short) 2);

		Gdx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 3 * 2, null, GL20.GL_STATIC_DRAW);

		final int glGetError = Gdx.gl.glGetError();
		System.out.println(glGetError);
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
			"    attribute vec3 a_position;\r\n" + //
			"    attribute vec3 a_position2;\r\n" + //
			"    attribute float a_boneNumber;\r\n" + //
			"    varying float fragNumber;\r\n" + //
			"    void main() {\r\n" + //
			"      gl_Position = vec4(a_position2.x, a_position2.y, a_position2.z, 1.0);\r\n" + //
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

	private static ShortBuffer wrapFaces(final int[] faces) {
		final ShortBuffer wrapper = ByteBuffer.allocateDirect(faces.length * 2).order(ByteOrder.nativeOrder())
				.asShortBuffer();
		for (final int face : faces) {
			wrapper.put((short) face);
		}
		wrapper.clear();
		return wrapper;
	}

	private static ByteBuffer wrap(final byte[] skin) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(skin.length).order(ByteOrder.nativeOrder());
		wrapper.put(skin);
		wrapper.clear();
		return wrapper;
	}

	private static FloatBuffer wrap(final float[] positions) {
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(positions.length * 4).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		wrapper.put(positions);
		wrapper.clear();
		return wrapper;
	}
}
