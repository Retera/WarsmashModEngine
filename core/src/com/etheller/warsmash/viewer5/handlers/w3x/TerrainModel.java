package com.etheller.warsmash.viewer5.handlers.w3x;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.WarsmashGdxGame;
import com.etheller.warsmash.parsers.mdlx.Geoset;
import com.etheller.warsmash.parsers.mdlx.MdlxModel;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.WebGL;

public class TerrainModel {
	private static final IntBuffer GL_TEMP_BUFFER = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder())
			.asIntBuffer();
	private final War3MapViewer viewer;
	private final int vertexBuffer;
	private final int faceBuffer;
	private final int normalsOffset;
	private final int uvsOffset;
	private final int elements;
	private final int locationAndTextureBuffer;
	private final int texturesOffset;
	private final int instances;
	private final int vao;

	public TerrainModel(final War3MapViewer viewer, final InputStream modelInput, final List<float[]> locations,
			final List<Integer> textures, final ShaderProgram shader) {
		final GL20 gl = viewer.gl;
		final WebGL webgl = viewer.webGL;
		final ANGLEInstancedArrays instancedArrays = webgl.instancedArrays;
		final MdlxModel parser;
		try {
			parser = new MdlxModel(modelInput);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		final Geoset geoset = parser.getGeosets().get(0);
		final float[] vertices = geoset.getVertices();
		final float[] normals = geoset.getNormals();
		final float[] uvs = geoset.getUvSets()[0];
		final int[] faces = geoset.getFaces();
		final int normalsOffset = vertices.length * 4;
		final int uvsOffset = normalsOffset + (normals.length * 4);
		int vao;

		GL_TEMP_BUFFER.clear();
		Gdx.gl30.glGenVertexArrays(1, GL_TEMP_BUFFER);
		vao = GL_TEMP_BUFFER.get(0);
		Gdx.gl30.glBindVertexArray(vao);

		final int vertexBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, uvsOffset + (uvs.length * 4), null, GL20.GL_STATIC_DRAW);
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, vertices.length * 4, RenderMathUtils.wrap(vertices));
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, normalsOffset, normals.length * 4, RenderMathUtils.wrap(normals));
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, uvsOffset, uvs.length * 4, RenderMathUtils.wrap(uvs));

		shader.setVertexAttribute("a_position", 3, GL20.GL_FLOAT, false, 0, 0);
		shader.enableVertexAttribute("a_position");

		shader.setVertexAttribute("a_normal", 3, GL20.GL_FLOAT, false, 0, normalsOffset);
		shader.enableVertexAttribute("a_normal");

		shader.setVertexAttribute("a_uv", 3, GL20.GL_FLOAT, false, 0, uvsOffset);
		shader.enableVertexAttribute("a_uv");

		final int texturesOffset = locations.size() * 3 * 4;
		final int locationAndTextureBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, locationAndTextureBuffer);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, texturesOffset + textures.size(), null, GL20.GL_STATIC_DRAW);
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, locations.size() * 3 * 4, wrapVectors(locations));
		gl.glBufferSubData(GL20.GL_ARRAY_BUFFER, texturesOffset, textures.size(), wrapTexIndices(textures));

		shader.setVertexAttribute("a_instancePosition", 3, GL20.GL_FLOAT, false, 0, 0);
		shader.enableVertexAttribute("a_instancePosition");
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_instancePosition"), 1);

		shader.setVertexAttribute("a_instanceTexture", 1, GL20.GL_UNSIGNED_BYTE, false, 0, texturesOffset);
		shader.enableVertexAttribute("a_instanceTexture");
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_instanceTexture"), 1);

		final int faceBuffer = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, faceBuffer);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, faces.length * 2, RenderMathUtils.wrapFaces(faces),
				GL20.GL_STATIC_DRAW);

		WarsmashGdxGame.bindDefaultVertexArray();

		this.viewer = viewer;
		this.vertexBuffer = vertexBuffer;
		this.faceBuffer = faceBuffer;
		this.normalsOffset = normalsOffset;
		this.uvsOffset = uvsOffset;
		this.elements = faces.length;
		this.locationAndTextureBuffer = locationAndTextureBuffer;
		this.texturesOffset = texturesOffset;
		this.instances = locations.size() / 3;
		this.vao = vao;
	}

	private Buffer wrapTexIndices(final List<Integer> textures) {
		final ByteBuffer wrapper = ByteBuffer.allocateDirect(textures.size()).order(ByteOrder.nativeOrder());
		for (final Integer texture : textures) {
			wrapper.put(texture.byteValue());
		}
		wrapper.clear();
		return wrapper;
	}

	private Buffer wrapVectors(final List<float[]> locations) {
		final FloatBuffer wrapper = ByteBuffer.allocateDirect(locations.size() * 12).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		for (final float[] vector : locations) {
			wrapper.put(vector[0]);
			wrapper.put(vector[1]);
			wrapper.put(vector[2]);
		}
		wrapper.clear();
		return wrapper;
	}

	public void render(final ShaderProgram shader) {
		final War3MapViewer viewer = this.viewer;
		final GL20 gl = viewer.gl;
		final WebGL webGL = viewer.webGL;
		final ANGLEInstancedArrays instancedArrays = webGL.instancedArrays;

		Gdx.gl30.glBindVertexArray(this.vao);

		instancedArrays.glDrawElementsInstancedANGLE(GL20.GL_TRIANGLES, this.elements, GL20.GL_UNSIGNED_SHORT, 0,
				this.instances);

		WarsmashGdxGame.bindDefaultVertexArray();

	}
}
