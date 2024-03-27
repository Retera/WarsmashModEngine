package com.etheller.warsmash.viewer5.handlers.w3x.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.viewer5.*;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.ClientBuffer;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.mdx.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class LightningEffectBatch extends RenderBatch {
	private static final int LIGHTNING_EFFECT_STRIDE_BYTES = 40;
	private static final int LIGHTNING_EFFECT_INSTANCE_BYTES = LIGHTNING_EFFECT_STRIDE_BYTES * 6;
	private static final int LIGHTNING_EFFECT_INDEX_STRIDE_BYTES = 3 * 2;
	private static final int LIGHTNING_EFFECT_INDEX_INSTANCE_BYTES = LIGHTNING_EFFECT_INDEX_STRIDE_BYTES * 4;
	private static final Matrix4 transposeHeap = new Matrix4();
	protected static final Vector3 lineHeap = new Vector3();
	private static final Vector3 crossHeap = new Vector3();
	private static final Vector3 vertexHeap = new Vector3();
	private ShortBuffer indexBuffer;
	public LightningEffectBatch(Scene scene, Model<?> model, TextureMapper textureMapper) {
		super(scene, model, textureMapper);
		indexBuffer = ByteBuffer.allocateDirect(2 * 6).order(ByteOrder.nativeOrder()).asShortBuffer();
	}

	private void bindAndUpdateBuffer(ClientBuffer buffer) {
		final int count = this.count;
		final List<ModelInstance> instances = this.instances;

		// Ensure there is enough memory for all of the instances data.
		buffer.reserve(count * LIGHTNING_EFFECT_INSTANCE_BYTES);
		if(indexBuffer.capacity() < count * LIGHTNING_EFFECT_INDEX_INSTANCE_BYTES) {
			indexBuffer = ByteBuffer.allocateDirect(count * LIGHTNING_EFFECT_INDEX_INSTANCE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
		}
		indexBuffer.clear();

		final FloatBuffer floatView = buffer.floatView;

		final LightningEffectModel model = (LightningEffectModel) this.model;

		// "Copy" the instances into the buffer
		for (int i = 0; i < count; i++) {
			final LightningEffectNode instance = (LightningEffectNode)instances.get(i);
			LightningEffectNode source = instance.getSource();
			LightningEffectNode target = source.friend;
			final Matrix4 worldMatrix = instance.worldMatrix;
			int offset = i * LIGHTNING_EFFECT_INSTANCE_BYTES / 4;

			lineHeap.set(target.worldLocation);
			lineHeap.sub(source.worldLocation);
			float length = lineHeap.len();
			crossHeap.set(lineHeap).crs(this.scene.camera.billboardedVectors[6]);
			crossHeap.nor();
			float maxWidth = Math.min(length/2, model.getWidth());
			lineHeap.nor();

			float texCoordScaleOffset = source.textureAnimationPosition;
			float texCoordUOffset = maxWidth / length;

			floatView.put(offset++, source.worldLocation.x);
			floatView.put(offset++, source.worldLocation.y);
			floatView.put(offset++, source.worldLocation.z);
			floatView.put(offset++, -texCoordScaleOffset); // u
			floatView.put(offset++, 0.5f); // v
			floatView.put(offset++, 0);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			vertexHeap.set(source.worldLocation);
			vertexHeap.mulAdd(lineHeap, maxWidth);
			vertexHeap.mulAdd(crossHeap, maxWidth);

			floatView.put(offset++, vertexHeap.x);
			floatView.put(offset++, vertexHeap.y);
			floatView.put(offset++, vertexHeap.z);
			floatView.put(offset++, texCoordUOffset - texCoordScaleOffset); // u
			floatView.put(offset++, 0); // v
			floatView.put(offset++, maxWidth);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			vertexHeap.set(source.worldLocation);
			vertexHeap.mulAdd(lineHeap, maxWidth);
			vertexHeap.mulAdd(crossHeap, -maxWidth);

			floatView.put(offset++, vertexHeap.x);
			floatView.put(offset++, vertexHeap.y);
			floatView.put(offset++, vertexHeap.z);
			floatView.put(offset++, texCoordUOffset - texCoordScaleOffset); // u
			floatView.put(offset++, 1); // v
			floatView.put(offset++, -maxWidth);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			vertexHeap.set(target.worldLocation);
			vertexHeap.mulAdd(lineHeap, -maxWidth);
			vertexHeap.mulAdd(crossHeap, maxWidth);

			floatView.put(offset++, vertexHeap.x);
			floatView.put(offset++, vertexHeap.y);
			floatView.put(offset++, vertexHeap.z);
			floatView.put(offset++, 1 - texCoordUOffset - texCoordScaleOffset); // u
			floatView.put(offset++, 0); // v
			floatView.put(offset++, maxWidth);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			vertexHeap.set(target.worldLocation);
			vertexHeap.mulAdd(lineHeap, -maxWidth);
			vertexHeap.mulAdd(crossHeap, -maxWidth);

			floatView.put(offset++, vertexHeap.x);
			floatView.put(offset++, vertexHeap.y);
			floatView.put(offset++, vertexHeap.z);
			floatView.put(offset++, 1 - texCoordUOffset - texCoordScaleOffset); // u
			floatView.put(offset++, 1); // v
			floatView.put(offset++, -maxWidth);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			floatView.put(offset++, target.worldLocation.x);
			floatView.put(offset++, target.worldLocation.y);
			floatView.put(offset++, target.worldLocation.z);
			floatView.put(offset++, 1 - texCoordScaleOffset); // u
			floatView.put(offset++, 0.5f); // v
			floatView.put(offset++, 0);
			floatView.put(offset++, source.color[0]);
			floatView.put(offset++, source.color[1]);
			floatView.put(offset++, source.color[2]);
			floatView.put(offset++, source.color[3]);

			int indexBufferGroupIdx = i * 6;
			indexBuffer.put((short) (indexBufferGroupIdx + 0));
			indexBuffer.put((short) (indexBufferGroupIdx + 2));
			indexBuffer.put((short) (indexBufferGroupIdx + 1));
			indexBuffer.put((short) (indexBufferGroupIdx + 2));
			indexBuffer.put((short) (indexBufferGroupIdx + 4));
			indexBuffer.put((short) (indexBufferGroupIdx + 1));
			indexBuffer.put((short) (indexBufferGroupIdx + 1));
			indexBuffer.put((short) (indexBufferGroupIdx + 4));
			indexBuffer.put((short) (indexBufferGroupIdx + 3));
			indexBuffer.put((short) (indexBufferGroupIdx + 4));
			indexBuffer.put((short) (indexBufferGroupIdx + 5));
			indexBuffer.put((short) (indexBufferGroupIdx + 3));
		}

		GL20 gl = Gdx.gl;
		indexBuffer.flip();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.remaining(), indexBuffer, GL20.GL_DYNAMIC_DRAW);
		// Update the buffer.
		buffer.bindAndUpdate(count * LIGHTNING_EFFECT_INSTANCE_BYTES);
	}

	@Override
	public void add(ModelInstance instance) {
		final LightningEffectNode lightningEffectNode = (LightningEffectNode)instance;
		LightningEffectNode source = lightningEffectNode.getSource();
		if(!source.showing && !source.friend.showing) {
			super.add(source);
			lightningEffectNode.showing = true;
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < count; i++) {
			// TODO maybe this is an efficiency waste? Can we skip O(N) calculation here?
			final LightningEffectNode instance = (LightningEffectNode) instances.get(i);
			instance.showing = false;
			instance.friend.showing = false;
		}
		super.clear();
	}

	@Override
	public void renderOpaque() {
	}

	@Override
	public void renderTranslucent() {
		final int count = this.count;

		if (count != 0) {
			final LightningEffectModel model = (LightningEffectModel) this.model;
			Texture texture = model.texture;
			final ModelViewer viewer = model.viewer;
			final GL20 gl = viewer.gl;
			final WebGL webGL = viewer.webGL;
			final ANGLEInstancedArrays instancedArrays = webGL.instancedArrays;
			final ShaderProgram shader = LightningEffectModelHandler.Shaders.simple;
			final int position = shader.getAttributeLocation("a_position");
			final int uv = shader.getAttributeLocation("a_uv");
			final int outwardHeight = shader.getAttributeLocation("a_outwardHeight");
			final int color = shader.getAttributeLocation("a_color");
			final ClientBuffer buffer = viewer.buffer;
			final TextureMapper textureMapper = this.textureMapper;

			webGL.useShaderProgram(shader);

			this.bindAndUpdateBuffer(buffer);

			shader.setVertexAttribute(position, 3, GL20.GL_FLOAT, false, LIGHTNING_EFFECT_STRIDE_BYTES, 0);
			shader.setVertexAttribute(uv, 2, GL20.GL_FLOAT, false, LIGHTNING_EFFECT_STRIDE_BYTES, 12);
			shader.setVertexAttribute(outwardHeight, 1, GL20.GL_FLOAT, false, LIGHTNING_EFFECT_STRIDE_BYTES, 20);
			shader.setVertexAttribute(color, 4, GL20.GL_FLOAT, false, LIGHTNING_EFFECT_STRIDE_BYTES, 24);

			transposeHeap.set(this.scene.camera.viewProjectionMatrix);
			transposeHeap.tra();
			shader.setUniformMatrix4fv("u_VP", this.scene.camera.viewProjectionMatrix.val, 0,
					this.scene.camera.viewProjectionMatrix.val.length);

			gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);

			shader.setUniformi("u_texture", 0);
			viewer.webGL.bindTexture(texture, 0);

//			shader.setUniformf("u_avgSegLen", model.getAvgSegLen());
//			shader.setUniform4fv("u_color", model.getColor(), 0, 4);
//			shader.setUniformf("u_noiseScale", model.getNoiseScale());
//			shader.setUniformf("u_texCoordScale", model.getTexCoordScale());
//			shader.setUniformf("u_duration", model.getDuration());

			gl.glEnable(GL20.GL_BLEND);
			gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

//			gl.glEnable(GL20.GL_CULL_FACE);
			gl.glDisable(GL20.GL_CULL_FACE);

			gl.glDisable(GL20.GL_DEPTH_TEST);

			gl.glDepthMask(false);

			gl.glDrawElements(GL20.GL_TRIANGLES, count * 12, GL20.GL_UNSIGNED_SHORT, 0);
		}
	}
}
