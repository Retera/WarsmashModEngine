package com.etheller.warsmash.viewer5.handlers.w3x.environment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.util.RenderMathUtils;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

public class CliffMesh {
	public int vertexBuffer;
	public int uvBuffer;
	public int normalBuffer;
	public int indexBuffer;
	public int instanceBuffer;
	public int indices;

	private FloatBuffer renderJobs = ByteBuffer.allocateDirect(16 * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
	private final GL30 gl;

	public CliffMesh(final String path, final DataSource dataSource, final GL30 gl) throws IOException {
		this.gl = gl;
		if (path.endsWith(".mdx") || path.endsWith(".MDX")) {
			final MdlxModel model = new MdlxModel(dataSource.read(path));
			final MdlxGeoset geoset = model.getGeosets().get(0);

			this.vertexBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, geoset.getVertices().length * 4,
					RenderMathUtils.wrap(geoset.getVertices()), GL20.GL_STATIC_DRAW);

			this.uvBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.uvBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, geoset.getUvSets()[0].length * 4,
					RenderMathUtils.wrap(geoset.getUvSets()[0]), GL20.GL_STATIC_DRAW);

			this.normalBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.normalBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, geoset.getNormals().length * 4,
					RenderMathUtils.wrap(geoset.getNormals()), GL20.GL_STATIC_DRAW);

			this.instanceBuffer = gl.glGenBuffer();

			this.indices = geoset.getFaces().length;
			this.indexBuffer = gl.glGenBuffer();
			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.indexBuffer);
			gl.glBufferData(GL20.GL_ARRAY_BUFFER, geoset.getFaces().length * 2,
					RenderMathUtils.wrapFaces(geoset.getFaces()), GL20.GL_STATIC_DRAW);
		}
	}

	public void renderQueue(final float[] position) {
		if (this.renderJobs.remaining() < 4) {
			final int newCapacity = this.renderJobs.capacity() * 2;
			final FloatBuffer newRenderJobs = ByteBuffer.allocateDirect(newCapacity * 4).order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			newRenderJobs.clear();
			this.renderJobs.flip();
			newRenderJobs.put(this.renderJobs);
			this.renderJobs = newRenderJobs;
		}
		this.renderJobs.put(position);
	}

	public void render(final ShaderProgram cliffShader) {
		if (this.renderJobs.position() == 0) {
			return;
		}
		this.renderJobs.flip();

		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		this.gl.glBufferData(GL20.GL_ARRAY_BUFFER, this.renderJobs.remaining() * 4, this.renderJobs,
				GL20.GL_DYNAMIC_DRAW);

		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vertexBuffer);
		this.gl.glVertexAttribPointer(cliffShader.getAttributeLocation("vPosition"), 3, GL20.GL_FLOAT, false, 0, 0);

		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.uvBuffer);
		this.gl.glVertexAttribPointer(cliffShader.getAttributeLocation("vUV"), 2, GL20.GL_FLOAT, false, 0, 0);

		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.normalBuffer);
		this.gl.glVertexAttribPointer(cliffShader.getAttributeLocation("vNormal"), 3, GL20.GL_FLOAT, false, 0, 0);

		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.instanceBuffer);
		final int offsetAttributeLocation = cliffShader.getAttributeLocation("vOffset");
		this.gl.glVertexAttribPointer(offsetAttributeLocation, 4, GL20.GL_FLOAT, false, 0, 0);
		this.gl.glVertexAttribDivisor(offsetAttributeLocation, 1);

		this.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer);
		this.gl.glDrawElementsInstanced(GL20.GL_TRIANGLES, this.indices, GL30.GL_UNSIGNED_SHORT, 0,
				this.renderJobs.remaining() / 4);

		this.gl.glVertexAttribDivisor(offsetAttributeLocation, 0); // ToDo use vao

		this.renderJobs.clear();
	}
}
