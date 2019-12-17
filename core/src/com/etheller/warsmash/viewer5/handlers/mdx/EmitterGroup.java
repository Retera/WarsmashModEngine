package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.Model;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.SkeletalNode;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;

public class EmitterGroup {
	private final MdxModel model;
	private final List<Integer> objects;

	public EmitterGroup(final MdxModel model) {
		this.model = model;
		this.objects = new ArrayList<>();
	}

	public void render(final MdxComplexInstance instance) {
		final Scene scene = instance.scene;
		final SkeletalNode[] nodes = instance.nodes;
		final Model<?> model = instance.model;
		final ModelViewer viewer = model.viewer;
		final GL20 gl = viewer.gl;
		final ShaderProgram shader = MdxHandler.Shaders.particles;

		gl.glDepthMask(false);
		gl.glEnable(GL20.GL_BLEND);
		gl.glDisable(GL20.GL_CULL_FACE);
		gl.glEnable(GL20.GL_DEPTH_TEST);

		shader.begin();

		shader.setUniformMatrix("u_mvp", scene.camera.worldProjectionMatrix);
		shader.setUniformf("u_texture", 0);

		final int a_position = shader.getAttributeLocation("a_position");
		Gdx.gl30.glVertexAttribDivisor(a_position, 0);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, viewer.rectBuffer);
		gl.glVertexAttribPointer(a_position, 1, GL20.GL_UNSIGNED_BYTE, false, 0, 0);

		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_p0"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_p1"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_p2"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_p3"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_health"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_color"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_tail"), 1);
		Gdx.gl30.glVertexAttribDivisor(shader.getAttributeLocation("a_leftRightTop"), 1);

		for (final int index : this.objects) {

		}

	}

	protected abstract void renderEmitter(EmitterObject emitter, ShaderProgram shader);
}
