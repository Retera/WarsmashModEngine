package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.gl.WebGL;
import com.etheller.warsmash.viewer5.handlers.w3x.DynamicShadowManager;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;

public class BatchGroup extends GenericGroup {
	private static float[] tempFloat3Array = new float[3];

	private final MdxModel model;
	public final SkinningType skinningType;
	public final boolean hd;

	public BatchGroup(final MdxModel model, final SkinningType skinningType, final boolean hd) {
		this.model = model;
		this.skinningType = skinningType;
		this.hd = hd;
	}

	@Override
	public void render(final MdxComplexInstance instance, final Matrix4 mvp) {
		final Scene scene = instance.scene;
		final Camera camera = scene.camera;
		final MdxModel model = this.model;
		final List<Texture> textures = model.getTextures();
		final MdxHandler handler = model.handler;
		final List<Batch> batches = model.batches;
		final List<Integer> replaceables = model.replaceables;
		final ModelViewer viewer = model.viewer;
		final GL20 gl = viewer.gl;
		final WebGL webGL = viewer.webGL;
		final SkinningType skinningType = this.skinningType;
		final boolean hd = this.hd;
		final ShaderProgram shader;
		final W3xSceneLightManager lightManager = (W3xSceneLightManager) scene.getLightManager();

		if (hd) {
			shader = handler.shaders.hd;
		}
		else if (skinningType == SkinningType.ExtendedVertexGroups) {
			if (DynamicShadowManager.IS_SHADOW_MAPPING) {
				shader = handler.shaders.extendedShadowMap;
			}
			else {
				shader = handler.shaders.extended;
			}
		}
		else {
			if (DynamicShadowManager.IS_SHADOW_MAPPING) {
				shader = handler.shaders.complexShadowMap;
			}
			else {
				shader = handler.shaders.complex;
			}
		}

		webGL.useShaderProgram(shader);

		shader.setUniformMatrix(hd ? "u_VP" : "u_mvp", mvp);

		final DataTexture boneTexture = instance.boneTexture;
		final DataTexture unitLightsTexture = lightManager.getUnitLightsTexture();

		unitLightsTexture.bind(14);
		shader.setUniformi("u_lightTexture", 14);
		shader.setUniformf("u_lightCount", lightManager.getUnitLightCount());
		shader.setUniformf("u_lightTextureHeight", unitLightsTexture.getHeight());

		// Instances of models with no bones don't have a bone texture.
		if (boneTexture != null) {
			boneTexture.bind(15);

			shader.setUniformf("u_hasBones", 1);
			shader.setUniformi("u_boneMap", 15);
			shader.setUniformf("u_vectorSize", 1f / boneTexture.getWidth());
			shader.setUniformf("u_rowSize", 1);
		}
		else {
			shader.setUniformf("u_hasBones", 0);
		}

		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, model.arrayBuffer);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, model.elementBuffer);

		if (hd) {
			shader.setUniformi("u_diffuseMap", 0);
			shader.setUniformi("u_normalsMap", 1);
			shader.setUniformi("u_ormMap", 2);
			shader.setUniformi("u_emissiveMap", 3);
			shader.setUniformi("u_teamColorMap", 4);
			shader.setUniformi("u_environmentMap", 5);

			gl.glEnable(GL20.GL_BLEND);
			gl.glEnable(GL20.GL_DEPTH_TEST);
			gl.glDepthMask(true);

			shader.setUniformMatrix("u_MV", camera.viewMatrix);

			tempFloat3Array[0] = camera.location.x;
			tempFloat3Array[1] = camera.location.y;
			tempFloat3Array[2] = camera.location.z;
			shader.setUniform3fv("u_eyePos", tempFloat3Array, 0, 3);

			for (final int index : this.objects) {
				final Batch batch = batches.get(index);
				final Geoset geoset = batch.geoset;
				final Material material = batch.material;
				final Layer diffuseLayer = material.layers.get(0);
				final Layer normalsLayer = material.layers.get(1);
				final Layer ormLayer = material.layers.get(2);
				final Layer emissiveLayer = material.layers.get(3);
				final Layer teamColorLayer = material.layers.get(4);
				final Layer environmentMapLayer = material.layers.get(5);
				final float[] geosetColor = instance.geosetColors[geoset.index];
				final float layerAlpha = instance.layerAlphas[diffuseLayer.index];

				if ((geosetColor[3] > 0.01) && (layerAlpha > 0)) {
					shader.setUniformf("u_layerAlpha", layerAlpha * geosetColor[3]);
					shader.setUniformf("u_filterMode", diffuseLayer.filterMode);

					final int diffuseId = Math.max(0, instance.layerTextures[diffuseLayer.index]);
					final int normalsId = Math.max(0, instance.layerTextures[normalsLayer.index]);
					final int ormId = Math.max(0, instance.layerTextures[ormLayer.index]);
					final int emissiveId = Math.max(0, instance.layerTextures[emissiveLayer.index]);
					final int teamColorId = Math.max(0, instance.layerTextures[teamColorLayer.index]);
					final int environmentMapId = Math.max(0, instance.layerTextures[environmentMapLayer.index]);

					final Texture diffuseTexture;
					final Texture normalsTexture;
					final Texture ormTexture;
					final Texture emissiveTexture = textures.get(emissiveId);
					final Texture teamColorTexture;
					final Texture environmentMapTexture = textures.get(environmentMapId);

					{
						final Integer replaceable = replaceables.get(diffuseId);
						if ((replaceable > 0) && (replaceable < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
								&& (instance.replaceableTextures_diffuse[replaceable] != null)) {
							diffuseTexture = instance.replaceableTextures_diffuse[replaceable];
						}
						else {
							diffuseTexture = textures.get(diffuseId);
						}
					}

					{
						final Integer replaceable = replaceables.get(normalsId);
						if ((replaceable > 0) && (replaceable < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
								&& (instance.replaceableTextures_normal[replaceable] != null)) {
							normalsTexture = instance.replaceableTextures_normal[replaceable];
						}
						else {
							normalsTexture = textures.get(normalsId);
						}
					}

					{
						final Integer replaceable = replaceables.get(ormId);
						if ((replaceable > 0) && (replaceable < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
								&& (instance.replaceableTextures_orm[replaceable] != null)) {
							ormTexture = instance.replaceableTextures_orm[replaceable];
						}
						else {
							ormTexture = textures.get(ormId);
						}
					}

					final Integer replaceable = replaceables.get(teamColorId);
					if ((replaceable > 0) && (replaceable < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
							&& (instance.replaceableTextures[replaceable] != null)) {
						teamColorTexture = instance.replaceableTextures[replaceable];
					}
					else {
						teamColorTexture = textures.get(teamColorId);
					}

					webGL.bindTexture(diffuseTexture, 0);
					webGL.bindTexture(normalsTexture, 1);
					webGL.bindTexture(ormTexture, 2);
					webGL.bindTexture(emissiveTexture, 3);
					webGL.bindTexture(teamColorTexture, 4);
					webGL.bindTexture(environmentMapTexture, 5);

					diffuseLayer.bind(shader);

					geoset.bindHd(shader, batch.skinningType, diffuseLayer.coordId);
					geoset.render();
				}
			}
		}
		else {
			shader.setUniformi("u_texture", 0);

			shader.setUniform4fv("u_vertexColor", instance.vertexColor, 0, instance.vertexColor.length);

			for (final int index : this.objects) {
				final Batch batch = batches.get(index);
				final Geoset geoset = batch.geoset;
				final Layer layer = batch.layer;
				final int geosetIndex = geoset.index;
				final int layerIndex = layer.index;
				final float[] geosetColor = instance.geosetColors[geosetIndex];
				final float layerAlpha = instance.layerAlphas[layerIndex];

				if ((geosetColor[3] > 0.01) && (layerAlpha > 0.01)) {
					// BELOW: I updated it to "Math.max(0," because MDL and MDX parser for PRSCMOD
					// menu screen behaved differently,
					// the MDL case was getting "no data" for default value when unanimated, and "no
					// data" resolved to -1,
					// whereas MDX binary contained an "unused" 0 value.
					final int layerTexture = Math.max(0, instance.layerTextures[layerIndex]);
					final float[] uvAnim = instance.uvAnims[layerIndex];

					shader.setUniform4fv("u_geosetColor", geosetColor, 0, geosetColor.length);

					shader.setUniformf("u_layerAlpha", layerAlpha);
					shader.setUniformi("u_unshaded", layer.unshaded != 0 ? 1 : 0);
					shader.setUniformi("u_unfogged", layer.unfogged != 0 ? 1 : 0);
					shader.setUniformf("u_fogColor", scene.fogSettings.color);
					shader.setUniformf("u_fogParams", scene.fogSettings.style.ordinal(), scene.fogSettings.start,
							scene.fogSettings.end, scene.fogSettings.density);

					shader.setUniform2fv("u_uvTrans", uvAnim, 0, 2);
					shader.setUniform2fv("u_uvRot", uvAnim, 2, 2);
					shader.setUniform1fv("u_uvScale", uvAnim, 4, 1);

					if (instance.additiveOverrideMeshMode) {
						layer.bindBlended(shader);
						gl.glBlendFunc(FilterMode.ADDITIVE_ALPHA[0], FilterMode.ADDITIVE_ALPHA[1]);
					}
					else if (instance.vertexColor[3] < 1.0f) {
						layer.bindBlended(shader);
					}
					else {
						layer.bind(shader);
					}

					final Integer replaceable = replaceables.get(layerTexture); // TODO is this OK?
					Texture texture;

					if ((replaceable > 0) && (replaceable < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
							&& (instance.replaceableTextures[replaceable] != null)) {
						texture = instance.replaceableTextures[replaceable];
					}
					else {
						texture = textures.get(layerTexture);

						Texture textureLookup = instance.textureMapper.get(texture);
						if (textureLookup == null) {
							textureLookup = texture;
						}
						texture = textureLookup;
					}

					viewer.webGL.bindTexture(texture, 0);

					if (skinningType == SkinningType.ExtendedVertexGroups) {
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
}
