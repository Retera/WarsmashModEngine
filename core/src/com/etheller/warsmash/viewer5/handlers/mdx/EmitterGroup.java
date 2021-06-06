package com.etheller.warsmash.viewer5.handlers.mdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;

public class EmitterGroup extends GenericGroup {
	private final MdxModel model;

	public EmitterGroup(final MdxModel model) {
		this.model = model;
	}

	@Override
	public void render(final MdxComplexInstance instance, final Matrix4 mvp) {
		if (DynamicShadowManager.IS_SHADOW_MAPPING) {
			return;
		}

		final Scene scene = instance.scene;
		final SkeletalNode[] nodes = instance.nodes;
		final Model<?> model = instance.model;
		final ModelViewer viewer = model.viewer;
		final GL20 gl = viewer.gl;
		final ANGLEInstancedArrays instancedArrays = viewer.webGL.instancedArrays;
		final ShaderProgram shader = ((MdxModel) model).handler.shaders.particles;

		gl.glDepthMask(false);
		gl.glEnable(GL20.GL_BLEND);
		gl.glDisable(GL20.GL_CULL_FACE);
		gl.glEnable(GL20.GL_DEPTH_TEST);

		viewer.webGL.useShaderProgram(shader);

		shader.setUniformMatrix("u_mvp", mvp);
		shader.setUniformi("u_texture", 0);

		final int a_position = shader.getAttributeLocation("a_position");
		instancedArrays.glVertexAttribDivisorANGLE(a_position, 0);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, viewer.rectBuffer);
		gl.glVertexAttribPointer(a_position, 1, GL20.GL_UNSIGNED_BYTE, false, 0, 0);

		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p0"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p1"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p2"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p3"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_health"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_color"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_tail"), 1);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_leftRightTop"), 1);

		for (final int index : this.objects) {
			GeometryEmitterFuncs.renderEmitter((MdxEmitter<?, ?, ?>) nodes[index].object, shader);
		}

		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_leftRightTop"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_tail"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_color"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_health"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p3"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p2"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p1"), 0);
		instancedArrays.glVertexAttribDivisorANGLE(shader.getAttributeLocation("a_p0"), 0);

	}
}
