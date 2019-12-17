package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;

public class BatchGroup {

	private final MdxModel model;
	private final boolean isExtended;
	private final List<Integer> objects;

	public BatchGroup(final MdxModel model, final boolean isExtended) {
		this.model = model;
		this.isExtended = isExtended;
		this.objects = new ArrayList<>(); // TODO IntArrayList
	}

	public void render(final MdxComplexInstance instance) {
		final Scene scene = instance.scene;
		final MdxModel model = this.model;
		final List<Texture> textures = model.getTextures();
		final MdxHandler handler = model.handler;
		final List<Texture> teamColors = MdxHandler.teamColors;
		final List<Texture> teamGlows = MdxHandler.teamGlows;
		final List<Batch> batches = model.batches;
		final List<Integer> replaceables = model.replaceables;
		final ModelViewer viewer = model.viewer;
		final GL20 gl = viewer.gl;
		final boolean isExtended = this.isExtended;
		final ShaderProgram shader;

		if (isExtended) {
			shader = MdxHandler.Shaders.extended;
		}
		else {
			shader = MdxHandler.Shaders.complex;
		}

		shader.begin();

		shader.setUniformMatrix("u_mvp", scene.camera.worldProjectionMatrix);

		final Texture boneTexture = instance.boneTexture;

		// Instances of models with no bones don't have a bone texture.
		if (boneTexture != null) {
			boneTexture.bind(15);

			shader.setUniformf("u_hasBones", 1);
			shader.setUniformf("u_boneMap", 15);
			shader.setUniformf("u_vectorSize", 1f / boneTexture.getWidth());
			shader.setUniformf("u_rowSize", 1);
		}
		else {
			shader.setUniformf("u_hasBones", 0);
		}

		shader.setUniformi("u_texture", 0);

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, model.arrayBuffer);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);

		shader.setUniform4fv("u_vertexColor", instance.vertexColor, 0, instance.vertexColor.length);

		for (final int index : this.objects) {
			final Batch batch = batches.get(index);
			final Geoset geoset = batch.geoset;
			final Layer layer = batch.layer;
			final int geosetIndex = geoset.index;
			final int layerIndex = layer.index;
			final float[] geosetColor = instance.geosetColors[geosetIndex];
			final float layerAlpha = instance.layerAlphas[layerIndex];

			if ((geosetColor[3] > 0) && (layerAlpha > 0)) {
				final int layerTexture = instance.layerTextures[layerIndex];
				final float[] uvAnim = instance.uvAnims[layerIndex];

				shader.setUniform4fv("u_geosetColor", geosetColor, 0, geosetColor.length);

				shader.setUniformf("u_layerAlpha", layerAlpha);

				shader.setUniform2fv("u_uvTrans", uvAnim, 0, 2);
				shader.setUniform2fv("u_uvRot", uvAnim, 2, 2);
				shader.setUniform1fv("u_uvRot", uvAnim, 4, 1);

				layer.bind(shader);

				final Integer replaceable = replaceables.get(layerTexture); // TODO is this OK?
				Texture texture;

				if (replaceable == 1) {
					texture = teamColors.get(instance.teamColor);
				}
				else if (replaceable == 2) {
					texture = teamGlows.get(instance.teamColor);
				}
				else {
					texture = textures.get(layerTexture);
				}

				Texture textureLookup = instance.textureMapper.get(texture);
				if (textureLookup == null) {
					textureLookup = texture;
				}
				viewer.webGL.bindTexture(textureLookup, 0);

				if (isExtended) {
					geoset.bindExtended(shader, layer.coordId);
				}
				else {
					geoset.bind(shader, layer.coordId);
				}

				geoset.render();
			}
		}
	}
}
