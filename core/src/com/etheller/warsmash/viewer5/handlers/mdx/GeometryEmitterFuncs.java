package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.ClientBuffer;
import com.etheller.warsmash.viewer5.gl.DataTexture;
import com.etheller.warsmash.viewer5.handlers.EmitterObject;
import com.etheller.warsmash.viewer5.handlers.w3x.W3xSceneLightManager;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.IVec3;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter2.HeadOrTail;

//The total storage that emitted objects can use.
//This is enough to support all of the MDX geometry emitters.
//The memory layout is the same as this C struct:
//
//struct {
//  float p0[3]
//  float p1[3]
//  float p2[3]
//  float p3[3]
//  float health
//  byte color[4]
//  byte tail
//  byte leftRightTop[3]
//}
//
public class GeometryEmitterFuncs {
	public static final int BYTES_PER_OBJECT = 60;
	public static final int FLOATS_PER_OBJECT = BYTES_PER_OBJECT >> 2;

	// Offsets into the emitted object structure
	public static final int BYTE_OFFSET_P0 = 0;
	public static final int BYTE_OFFSET_P1 = 12;
	public static final int BYTE_OFFSET_P2 = 24;
	public static final int BYTE_OFFSET_P3 = 36;
	public static final int BYTE_OFFSET_HEALTH = 48;
	public static final int BYTE_OFFSET_COLOR = 52;
	public static final int BYTE_OFFSET_TAIL = 56;
	public static final int BYTE_OFFSET_LEFT_RIGHT_TOP = 57;

	// Offset aliases
	public static final int FLOAT_OFFSET_P0 = BYTE_OFFSET_P0 >> 2;
	public static final int FLOAT_OFFSET_P1 = BYTE_OFFSET_P1 >> 2;
	public static final int FLOAT_OFFSET_P2 = BYTE_OFFSET_P2 >> 2;
	public static final int FLOAT_OFFSET_P3 = BYTE_OFFSET_P3 >> 2;
	public static final int FLOAT_OFFSET_HEALTH = BYTE_OFFSET_HEALTH >> 2;
	public static final int BYTE_OFFSET_TEAM_COLOR = BYTE_OFFSET_LEFT_RIGHT_TOP;

	// Head or tail.
	public static final int HEAD = 0;
	public static final int TAIL = 1;

	// Emitter types
	public static final int EMITTER_PARTICLE2 = 0;
	public static final int EMITTER_RIBBON = 1;
	public static final int EMITTER_SPLAT = 2;
	public static final int EMITTER_UBERSPLAT = 3;
	public static final int EMITTER_SPN = 4; // added by Retera because reasons

	private static final Vector3 locationHeap = new Vector3();
	private static final Vector3 startHeap = new Vector3();
	private static final Vector3 endHeap = new Vector3();
	private static final float[] vectorTemp = new float[3];
	private static final Vector3[] vector3Heap = { new Vector3(), new Vector3(), new Vector3(), new Vector3(),
			new Vector3(), new Vector3() };

	public static void bindParticleEmitter2Buffer(final ParticleEmitter2 emitter, final ClientBuffer buffer) {
		final MdxComplexInstance instance = emitter.instance;
		final List<Particle2> objects = emitter.objects;
		final ByteBuffer byteView = buffer.byteView;
		final FloatBuffer floatView = buffer.floatView;
		final ParticleEmitter2Object emitterObject = emitter.emitterObject;
		final int modelSpace = emitterObject.modelSpace;
		final float tailLength = emitterObject.tailLength;
		int offset = 0;

		for (int objectIndex = 0; objectIndex < emitter.alive; objectIndex++) {
			final Particle2 object = objects.get(objectIndex);
			final HeadOrTail tailness = HeadOrTail.fromId(object.tail);
			final boolean tail = tailness.isIncludesTail();
			final boolean head = tailness.isIncludesHead();

			if (head) {
				final int byteOffset = offset * BYTES_PER_OBJECT;
				final int floatOffset = offset * FLOATS_PER_OBJECT;
				final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
				Vector3 location = object.location;
				final Vector3 scale = object.scale;

				// If this is a model space emitter, the location is in local space, so convert
				// it to world space.
				if (modelSpace != 0) {
					location = locationHeap.set(location).prj(emitter.node.worldMatrix);
				}

				floatView.put(p0Offset + 0, location.x);
				floatView.put(p0Offset + 1, location.y);
				floatView.put(p0Offset + 2, location.z);
				if (emitterObject.xYQuad != 0) {
					final Vector3 velocity = object.velocity;
					floatView.put(p0Offset + 3, velocity.x);
					floatView.put(p0Offset + 4, velocity.y);
//					floatView.put(p0Offset + 5, velocity.z);
				}
				else {
					floatView.put(p0Offset + 3, 0);
					floatView.put(p0Offset + 4, 0);
				}

				floatView.put(p0Offset + 6, scale.x);
				floatView.put(p0Offset + 7, scale.y);
				floatView.put(p0Offset + 8, scale.z);

				floatView.put(floatOffset + FLOAT_OFFSET_HEALTH, object.health);

				byteView.put(byteOffset + BYTE_OFFSET_TAIL, (byte) 0);
				byteView.put(byteOffset + BYTE_OFFSET_TEAM_COLOR, (byte) 0);

				offset += 1;
			}
			if (tail) {
				final int byteOffset = offset * BYTES_PER_OBJECT;
				final int floatOffset = offset * FLOATS_PER_OBJECT;
				final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
				final Vector3 location = object.location;
				final Vector3 scale = object.scale;

				final Vector3 velocity = object.velocity;
				final Vector3 start = startHeap;
				Vector3 end = location;

				start.x = end.x - (tailLength * velocity.x);
				start.y = end.y - (tailLength * velocity.y);
				start.z = end.z - (tailLength * velocity.z);

				// If this is a model space emitter, the start and end are in local space, so
				// convert them to world space.
				if (modelSpace != 0) {
					start.prj(emitter.node.worldMatrix);
					end = endHeap.set(end).prj(emitter.node.worldMatrix);
				}

				floatView.put(p0Offset + 0, start.x);
				floatView.put(p0Offset + 1, start.y);
				floatView.put(p0Offset + 2, start.z);
				floatView.put(p0Offset + 3, end.x);
				floatView.put(p0Offset + 4, end.y);
				floatView.put(p0Offset + 5, end.z);

				floatView.put(p0Offset + 6, scale.x);
				floatView.put(p0Offset + 7, scale.y);
				floatView.put(p0Offset + 8, scale.z);

				floatView.put(floatOffset + FLOAT_OFFSET_HEALTH, object.health);

				byteView.put(byteOffset + BYTE_OFFSET_TAIL, (byte) 1);
				byteView.put(byteOffset + BYTE_OFFSET_TEAM_COLOR, (byte) 0);

				offset += 1;
			}

		}

	}

	public static void bindParticleEmitter2Shader(final ParticleEmitter2 emitter, final ShaderProgram shader) {
		final MdxComplexInstance instance = emitter.instance;
		final Scene scene = instance.scene;
		final Camera camera = scene.camera;
		final ParticleEmitter2Object emitterObject = emitter.emitterObject;
		final MdxModel model = emitterObject.model;
		final ModelViewer viewer = model.viewer;
		final GL20 gl = viewer.gl;
		final float[][] colors = emitterObject.colors;
		final float[][] intervals = emitterObject.intervals;
		final long replaceableId = emitterObject.replaceableId;
		Vector3[] vectors;
		Texture texture;

		gl.glBlendFunc(emitterObject.blendSrc, emitterObject.blendDst);

		if ((replaceableId > 0) && (replaceableId < WarsmashConstants.REPLACEABLE_TEXTURE_LIMIT)
				&& (instance.replaceableTextures[(int) replaceableId] != null)) {
			texture = instance.replaceableTextures[(int) replaceableId];
		}
		else {
			texture = emitterObject.internalTexture;
		}

		viewer.webGL.bindTexture(texture, 0);

		// Choose between a default rectangle and a billboarded one
		if (emitterObject.xYQuad != 0) {
			vectors = camera.vectors;
		}
		else {
			vectors = camera.billboardedVectors;
		}

		shader.setUniformf("u_emitter", EMITTER_PARTICLE2);

		shader.setUniformf("u_filterMode", emitterObject.filterModeForShader);
		shader.setUniformf("u_lifeSpan", emitterObject.lifeSpan);
		shader.setUniformf("u_timeMiddle", emitterObject.timeMiddle);
		shader.setUniformf("u_columns", emitterObject.columns);
		shader.setUniformf("u_rows", emitterObject.rows);
		shader.setUniformf("u_teamColored", emitterObject.teamColored);
		shader.setUniformi("u_unshaded", emitterObject.emitterUsesMdlOrUnshaded != 0 ? 1 : 0);
		shader.setUniformi("u_unfogged", emitterObject.unfogged != 0 ? 1 : 0);
		shader.setUniformf("u_fogColor", scene.fogSettings.color);
		shader.setUniformf("u_fogParams", scene.fogSettings.style.ordinal(), scene.fogSettings.start,
				scene.fogSettings.end, scene.fogSettings.density);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) scene.getLightManager();
		final DataTexture unitLightsTexture = lightManager.getUnitLightsTexture();

		unitLightsTexture.bind(14);
		shader.setUniformi("u_lightTexture", 14);
		shader.setUniformf("u_lightCount", lightManager.getUnitLightCount());
		shader.setUniformf("u_lightTextureHeight", unitLightsTexture.getHeight());

		shader.setUniform3fv("u_intervals[0]", intervals[0], 0, 3);
		shader.setUniform3fv("u_intervals[1]", intervals[1], 0, 3);
		shader.setUniform3fv("u_intervals[2]", intervals[2], 0, 3);
		shader.setUniform3fv("u_intervals[3]", intervals[3], 0, 3);

		shader.setUniform4fv("u_colors[0]", colors[0], 0, 4);
		shader.setUniform4fv("u_colors[1]", colors[1], 0, 4);
		shader.setUniform4fv("u_colors[2]", colors[2], 0, 4);

		shader.setUniform3fv("u_scaling", emitterObject.scaling, 0, 3);

		if (emitterObject.headOrTail.isIncludesHead()) {
			shader.setUniform3fv("u_vertices[0]", asFloatArray(vectors[0]), 0, 3);
			shader.setUniform3fv("u_vertices[1]", asFloatArray(vectors[1]), 0, 3);
			shader.setUniform3fv("u_vertices[2]", asFloatArray(vectors[2]), 0, 3);
			shader.setUniform3fv("u_vertices[3]", asFloatArray(vectors[3]), 0, 3);
		}

		shader.setUniform3fv("u_cameraZ", asFloatArray(camera.billboardedVectors[6]), 0, 3);
	}

	public static void bindRibbonEmitterBuffer(final RibbonEmitter emitter, final ClientBuffer buffer) {
		Ribbon object = emitter.first;
		final ByteBuffer byteView = buffer.byteView;
		final FloatBuffer floatView = buffer.floatView;
		final RibbonEmitterObject emitterObject = emitter.emitterObject;
		final long columns = emitterObject.columns;
		final int alive = emitter.alive;
		final float chainLengthFactor = 1 / (float) (alive - 1);
		int offset = 0;

		while (object.next != null) {
			final float[] next = object.next.vertices;
			final int byteOffset = offset * BYTES_PER_OBJECT;
			final int floatOffset = offset * FLOATS_PER_OBJECT;
			final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
			final int colorOffset = byteOffset + BYTE_OFFSET_COLOR;
			final int leftRightTopOffset = byteOffset + BYTE_OFFSET_LEFT_RIGHT_TOP;
			final float left = ((object.slot % columns) + (1 - (offset * chainLengthFactor) - chainLengthFactor))
					/ columns;
			final float top = object.slot / (float) columns;
			final float right = left + chainLengthFactor;
			final float[] vertices = object.vertices;
			final byte[] color = object.color;

			floatView.put(p0Offset + 0, vertices[0]);
			floatView.put(p0Offset + 1, vertices[1]);
			floatView.put(p0Offset + 2, vertices[2]);
			floatView.put(p0Offset + 3, vertices[3]);
			floatView.put(p0Offset + 4, vertices[4]);
			floatView.put(p0Offset + 5, vertices[5]);
			floatView.put(p0Offset + 6, next[3]);
			floatView.put(p0Offset + 7, next[4]);
			floatView.put(p0Offset + 8, next[5]);
			floatView.put(p0Offset + 9, next[0]);
			floatView.put(p0Offset + 10, next[1]);
			floatView.put(p0Offset + 11, next[2]);

			byteView.put(colorOffset + 0, color[0]);
			byteView.put(colorOffset + 1, color[1]);
			byteView.put(colorOffset + 2, color[2]);
			byteView.put(colorOffset + 3, color[3]);

			byteView.put(leftRightTopOffset + 0, (byte) (left * 255));
			byteView.put(leftRightTopOffset + 1, (byte) (right * 255));
			byteView.put(leftRightTopOffset + 2, (byte) (top * 255));

			object = object.next;
			offset += 1;

		}
	}

	public static void bindRibbonEmitterShader(final RibbonEmitter emitter, final ShaderProgram shader) {
		final TextureMapper textureMapper = emitter.instance.textureMapper;
		final RibbonEmitterObject emitterObject = emitter.emitterObject;
		final Layer layer = emitterObject.layer;
		final MdxModel model = emitterObject.model;
		final GL20 gl = model.viewer.gl;
		final Texture texture = model.getTextures().get(layer.textureId);

		layer.bind(shader);

		Texture mappedTexture = textureMapper.get(texture);
		if (mappedTexture == null) {
			mappedTexture = texture;
		}
		model.viewer.webGL.bindTexture(mappedTexture, 0);

		shader.setUniformf("u_emitter", EMITTER_RIBBON);

		shader.setUniformf("u_columns", emitterObject.columns);
		shader.setUniformf("u_rows", emitterObject.rows);
	}

	public static void bindEventObjectSplEmitterBuffer(
			final EventObjectEmitter<EventObjectEmitterObject, EventObjectSpl> emitter, final ClientBuffer buffer) {
		final List<EventObjectSpl> objects = emitter.objects;
		final ByteBuffer byteView = buffer.byteView;
		final FloatBuffer floatView = buffer.floatView;
		int offset = 0;

		for (final EventObjectSpl object : objects) {
			final int floatOffset = offset * FLOATS_PER_OBJECT;
			final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
			final float[] vertices = object.vertices;
			final IVec3 normal = object.normal;

			if (floatView.limit() < (p0Offset + 11)) {
				System.err.println(
						"FloatView has a limit of " + floatView.limit() + " which is not large enough. Aborting");
				continue;
			}
			floatView.put(p0Offset + 0, vertices[0]);
			floatView.put(p0Offset + 1, vertices[1]);
			floatView.put(p0Offset + 2, vertices[2]);
			floatView.put(p0Offset + 3, vertices[3]);
			floatView.put(p0Offset + 4, vertices[4]);
			floatView.put(p0Offset + 5, vertices[5]);
			floatView.put(p0Offset + 6, vertices[6]);
			floatView.put(p0Offset + 7, vertices[7]);
			floatView.put(p0Offset + 8, vertices[8]);
			floatView.put(p0Offset + 9, vertices[9]);
			floatView.put(p0Offset + 10, vertices[10]);
			floatView.put(p0Offset + 11, vertices[11]);

			floatView.put(floatOffset + FLOAT_OFFSET_HEALTH, object.health);

			final int byteOffset = offset * BYTES_PER_OBJECT;
			final int leftRightTopOffset = byteOffset + BYTE_OFFSET_LEFT_RIGHT_TOP;
			byteView.put(leftRightTopOffset + 0, (byte) (normal.x));
			byteView.put(leftRightTopOffset + 1, (byte) (normal.y));
			byteView.put(leftRightTopOffset + 2, (byte) (normal.z));

			offset += 1;
		}
	}

	public static void bindEventObjectSplEmitterShader(final EventObjectSplEmitter emitter,
			final ShaderProgram shader) {
		final MdxComplexInstance instance = emitter.instance;
		final Scene scene = instance.scene;
		final TextureMapper textureMapper = instance.textureMapper;
		final EventObjectEmitterObject emitterObject = emitter.emitterObject;
		final float[] intervalTimes = emitterObject.intervalTimes;
		final float[][] intervals = emitterObject.intervals;
		final float[][] colors = emitterObject.colors;
		final MdxModel model = emitterObject.model;
		final GL20 gl = model.viewer.gl;
		final Texture texture = emitterObject.internalTexture;

		gl.glBlendFunc(emitterObject.blendSrc, emitterObject.blendDst);

		Texture finalTexture = textureMapper.get(texture);
		if (finalTexture == null) {
			finalTexture = texture;
		}
		model.viewer.webGL.bindTexture(finalTexture, 0);

		shader.setUniformf("u_lifeSpan", emitterObject.lifeSpan);
		shader.setUniformf("u_columns", emitterObject.columns);
		shader.setUniformf("u_rows", emitterObject.rows);
		shader.setUniformi("u_unshaded", emitterObject.emitterUsesMdlOrUnshaded != 0 ? 1 : 0);

		final W3xSceneLightManager lightManager = (W3xSceneLightManager) scene.getLightManager();
		final DataTexture unitLightsTexture = lightManager.getUnitLightsTexture();

		unitLightsTexture.bind(14);
		shader.setUniformi("u_lightTexture", 14);
		shader.setUniformf("u_lightCount", lightManager.getUnitLightCount());
		shader.setUniformf("u_lightTextureHeight", unitLightsTexture.getHeight());

		// 3 because the uniform is shared with UBR, which has 3 values.
		vectorTemp[0] = intervalTimes[0];
		vectorTemp[1] = intervalTimes[1];
		vectorTemp[2] = 0;
		shader.setUniform3fv("u_intervalTimes", vectorTemp, 0, 3);

		shader.setUniform3fv("u_intervals[0]", intervals[0], 0, 3);
		shader.setUniform3fv("u_intervals[1]", intervals[1], 0, 3);

		shader.setUniform4fv("u_colors[0]", colors[0], 0, 4);
		shader.setUniform4fv("u_colors[1]", colors[1], 0, 4);
		shader.setUniform4fv("u_colors[2]", colors[2], 0, 4);
	}

	public static void bindEventObjectUbrEmitterBuffer(
			final EventObjectEmitter<EventObjectEmitterObject, EventObjectUbr> emitter, final ClientBuffer buffer) {
		final List<EventObjectUbr> objects = emitter.objects;
		final FloatBuffer floatView = buffer.floatView;
		int offset = 0;

		for (final EventObjectUbr object : objects) {
			final int floatOffset = offset * FLOATS_PER_OBJECT;
			final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
			final float[] vertices = object.vertices;

			floatView.put(p0Offset + 0, vertices[0]);
			floatView.put(p0Offset + 1, vertices[1]);
			floatView.put(p0Offset + 2, vertices[2]);
			floatView.put(p0Offset + 3, vertices[3]);
			floatView.put(p0Offset + 4, vertices[4]);
			floatView.put(p0Offset + 5, vertices[5]);
			floatView.put(p0Offset + 6, vertices[6]);
			floatView.put(p0Offset + 7, vertices[7]);
			floatView.put(p0Offset + 8, vertices[8]);
			floatView.put(p0Offset + 9, vertices[9]);
			floatView.put(p0Offset + 10, vertices[10]);
			floatView.put(p0Offset + 11, vertices[11]);

			floatView.put(floatOffset + FLOAT_OFFSET_HEALTH, object.health);

			offset += 1;
		}
	}

	public static void bindEventObjectUbrEmitterShader(final EventObjectUbrEmitter emitter,
			final ShaderProgram shader) {
		final TextureMapper textureMapper = emitter.instance.textureMapper;
		final EventObjectEmitterObject emitterObject = emitter.emitterObject;
		final float[] intervalTimes = emitterObject.intervalTimes;
		final float[][] colors = emitterObject.colors;
		final MdxModel model = emitterObject.model;
		final GL20 gl = model.viewer.gl;
		final Texture texture = emitterObject.internalTexture;

		gl.glBlendFunc(emitterObject.blendSrc, emitterObject.blendDst);

		Texture finalTexture = textureMapper.get(texture);
		if (finalTexture == null) {
			finalTexture = texture;
		}
		model.viewer.webGL.bindTexture(finalTexture, 0);

		shader.setUniformf("u_lifeSpan", emitterObject.lifeSpan);
		shader.setUniformf("u_columns", emitterObject.columns);
		shader.setUniformf("u_rows", emitterObject.rows);

		shader.setUniform3fv("u_intervalTimes", intervalTimes, 0, 3);

		shader.setUniform4fv("u_colors[0]", colors[0], 0, 4);
		shader.setUniform4fv("u_colors[1]", colors[1], 0, 4);
		shader.setUniform4fv("u_colors[2]", colors[2], 0, 4);
	}

	public static void renderEmitter(final MdxEmitter<?, ?, ?> emitter, final ShaderProgram shader) {
		int alive = emitter.alive;
		final EmitterObject emitterObject = emitter.emitterObject;
		final int emitterType = emitterObject.getGeometryEmitterType();

		if (emitterType == EMITTER_PARTICLE2) {
			final ParticleEmitter2Object emitterObject2 = ((ParticleEmitter2) emitter).emitterObject;
			if (emitterObject2.headOrTail.isIncludesHead() && emitterObject2.headOrTail.isIncludesTail()) {
				alive *= 2;
			}
		}
		else if (emitterType == EMITTER_RIBBON) {
			alive -= 1;
		}
		else if (emitterType == EMITTER_SPN) {
			return;
		}

		if (alive > 0) {
			final ModelViewer viewer = emitter.instance.model.viewer;
			final ANGLEInstancedArrays instancedArrays = viewer.webGL.instancedArrays;
			final ClientBuffer buffer = viewer.buffer;
			final GL20 gl = viewer.gl;
			final int size = alive * BYTES_PER_OBJECT;

			buffer.reserve(size);

			switch (emitterType) {
			case EMITTER_PARTICLE2:
				bindParticleEmitter2Buffer((ParticleEmitter2) emitter, buffer);
				bindParticleEmitter2Shader((ParticleEmitter2) emitter, shader);
				break;
			case EMITTER_RIBBON:
				bindRibbonEmitterBuffer((RibbonEmitter) emitter, buffer);
				bindRibbonEmitterShader((RibbonEmitter) emitter, shader);
				break;
			case EMITTER_SPLAT:
				bindEventObjectSplEmitterBuffer((EventObjectSplEmitter) emitter, buffer);
				bindEventObjectSplEmitterShader((EventObjectSplEmitter) emitter, shader);
				break;
			default:
				bindEventObjectUbrEmitterBuffer((EventObjectUbrEmitter) emitter, buffer);
				bindEventObjectUbrEmitterShader((EventObjectUbrEmitter) emitter, shader);
				break;
			}

			buffer.bindAndUpdate(size);

			shader.setUniformf("u_emitter", emitterType);

			shader.setVertexAttribute("a_p0", 3, GL20.GL_FLOAT, false, BYTES_PER_OBJECT, BYTE_OFFSET_P0);
			shader.setVertexAttribute("a_p1", 3, GL20.GL_FLOAT, false, BYTES_PER_OBJECT, BYTE_OFFSET_P1);
			shader.setVertexAttribute("a_p2", 3, GL20.GL_FLOAT, false, BYTES_PER_OBJECT, BYTE_OFFSET_P2);
			shader.setVertexAttribute("a_p3", 3, GL20.GL_FLOAT, false, BYTES_PER_OBJECT, BYTE_OFFSET_P3);
			shader.setVertexAttribute("a_health", 1, GL20.GL_FLOAT, false, BYTES_PER_OBJECT, BYTE_OFFSET_HEALTH);
			shader.setVertexAttribute("a_color", 4, GL20.GL_UNSIGNED_BYTE, true, BYTES_PER_OBJECT, BYTE_OFFSET_COLOR);
			shader.setVertexAttribute("a_tail", 1, GL20.GL_UNSIGNED_BYTE, false, BYTES_PER_OBJECT, BYTE_OFFSET_TAIL);
			shader.setVertexAttribute("a_leftRightTop", 3, GL20.GL_UNSIGNED_BYTE, false, BYTES_PER_OBJECT,
					BYTE_OFFSET_LEFT_RIGHT_TOP);

			instancedArrays.glDrawArraysInstancedANGLE(GL20.GL_TRIANGLES, 0, 6, alive);
		}
	}

	private static final float[] asFloatArray(final Vector3 vec) {
		vectorTemp[0] = vec.x;
		vectorTemp[1] = vec.y;
		vectorTemp[2] = vec.z;
		return vectorTemp;
	}

}
