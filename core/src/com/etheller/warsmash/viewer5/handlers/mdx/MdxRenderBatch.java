package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.FloatBuffer;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelInstance;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.RenderBatch;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.ClientBuffer;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;

public class MdxRenderBatch extends RenderBatch {
	private static final Matrix4 transposeHeap = new Matrix4();

	public MdxRenderBatch(final Scene scene, final Model<?> model, final TextureMapper textureMapper) {
		super(scene, model, textureMapper);
	}

	private void bindAndUpdateBuffer(final ClientBuffer buffer) {
		final int count = this.count;
		final List<ModelInstance> instances = this.instances;

		// Ensure there is enough memory for all of the instances data.
		buffer.reserve(count * 48);

		final FloatBuffer floatView = buffer.floatView;

		// "Copy" the instances into the buffer
		for (int i = 0; i < count; i++) {
			final ModelInstance instance = instances.get(i);
			final Matrix4 worldMatrix = instance.worldMatrix;
			final int offset = i * 12;

			floatView.put(offset + 0, worldMatrix.val[Matrix4.M00]);
			floatView.put(offset + 1, worldMatrix.val[Matrix4.M10]);
			floatView.put(offset + 2, worldMatrix.val[Matrix4.M20]);
			floatView.put(offset + 3, worldMatrix.val[Matrix4.M01]);
			floatView.put(offset + 4, worldMatrix.val[Matrix4.M11]);
			floatView.put(offset + 5, worldMatrix.val[Matrix4.M21]);
			floatView.put(offset + 6, worldMatrix.val[Matrix4.M02]);
			floatView.put(offset + 7, worldMatrix.val[Matrix4.M12]);
			floatView.put(offset + 8, worldMatrix.val[Matrix4.M22]);
			floatView.put(offset + 9, worldMatrix.val[Matrix4.M03]);
			floatView.put(offset + 10, worldMatrix.val[Matrix4.M13]);
			floatView.put(offset + 11, worldMatrix.val[Matrix4.M23]);
		}

		// Update the buffer.
		buffer.bindAndUpdate(count * 48);
	}

	@Override
	public void renderOpaque() {
		if (DynamicShadowManager.IS_SHADOW_MAPPING) {
			return;
		}
		final int count = this.count;

		if (count != 0) {
			final MdxModel model = (MdxModel) this.model;
			final List<Batch> batches = model.batches;
			final List<Texture> textures = model.textures;
			final ModelViewer viewer = model.viewer;
			final GL20 gl = viewer.gl;
			final WebGL webGL = viewer.webGL;
			final ANGLEInstancedArrays instancedArrays = webGL.instancedArrays;
			final ShaderProgram shader = model.handler.shaders.simple;
			final int m0 = shader.getAttributeLocation("a_m0");
			final int m1 = shader.getAttributeLocation("a_m1");
			final int m2 = shader.getAttributeLocation("a_m2");
			final int m3 = shader.getAttributeLocation("a_m3");
			final ClientBuffer buffer = viewer.buffer;
			final TextureMapper textureMapper = this.textureMapper;

			webGL.useShaderProgram(shader);

			this.bindAndUpdateBuffer(buffer);

			shader.setVertexAttribute(m0, 3, GL20.GL_FLOAT, false, 48, 0);
			shader.setVertexAttribute(m1, 3, GL20.GL_FLOAT, false, 48, 12);
			shader.setVertexAttribute(m2, 3, GL20.GL_FLOAT, false, 48, 24);
			shader.setVertexAttribute(m3, 3, GL20.GL_FLOAT, false, 48, 36);

			transposeHeap.set(this.scene.camera.viewProjectionMatrix);
			transposeHeap.tra();
			shader.setUniformMatrix4fv("u_VP", this.scene.camera.viewProjectionMatrix.val, 0,
					this.scene.camera.viewProjectionMatrix.val.length);

			gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, model.arrayBuffer);
			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);

			instancedArrays.glVertexAttribDivisorANGLE(m0, 1);
			instancedArrays.glVertexAttribDivisorANGLE(m1, 1);
			instancedArrays.glVertexAttribDivisorANGLE(m2, 1);
			instancedArrays.glVertexAttribDivisorANGLE(m3, 1);

			for (final GenericGroup group : model.simpleGroups) {
				for (final Integer object : group.objects) {
					final Batch batch = batches.get(object);
					final Geoset geoset = batch.geoset;
					final Layer layer = batch.layer;
					final Texture texture = textures.get(layer.textureId);

					shader.setUniformi("u_texture", 0);

					Texture mappedTexture = textureMapper.get(texture);
					if (mappedTexture == null) {
						mappedTexture = texture;
					}
					viewer.webGL.bindTexture(mappedTexture, 0);

					layer.bind(shader);

					geoset.bindSimple(shader);
					geoset.renderSimple(count);
				}
			}

			instancedArrays.glVertexAttribDivisorANGLE(m3, 0);
			instancedArrays.glVertexAttribDivisorANGLE(m2, 0);
			instancedArrays.glVertexAttribDivisorANGLE(m1, 0);
			instancedArrays.glVertexAttribDivisorANGLE(m0, 0);
		}
	}

	@Override
	public void renderTranslucent() {
	}
}
