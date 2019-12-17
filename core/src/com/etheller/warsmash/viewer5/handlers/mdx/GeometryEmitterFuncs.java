package com.etheller.warsmash.viewer5.handlers.mdx;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.etheller.warsmash.viewer5.Camera;
import com.etheller.warsmash.viewer5.ModelViewer;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.Texture;
import com.etheller.warsmash.viewer5.TextureMapper;

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

	private static final Vector3 locationHeap = new Vector3();
	private static final Vector3 startHeap = new Vector3();
	private static final Vector3 endHeap = new Vector3();
	private static final float[] vectorTemp = new float[3];

	public static void bindParticleEmitter2Buffer(final ParticleEmitter2 emitter, final ByteBuffer buffer) {
		final MdxComplexInstance instance = emitter.instance;
		final List<Particle2> objects = emitter.objects;
		final ByteBuffer byteView = buffer;
		final FloatBuffer floatView = buffer.asFloatBuffer();
		final ParticleEmitter2Object emitterObject = emitter.emitterObject;
		final int modelSpace = emitterObject.modelSpace;
		final float tailLength = emitterObject.tailLength;
		final int teamColor = instance.teamColor;
		int offset = 0;

		for (final Particle2 object : objects) {
			final int byteOffset = offset * BYTES_PER_OBJECT;
			final int floatOffset = offset * FLOATS_PER_OBJECT;
			final int p0Offset = floatOffset + FLOAT_OFFSET_P0;
			Vector3 location = object.location;
			final Vector3 scale = object.scale;
			final int tail = object.tail;

			if (tail == HEAD) {
				// If this is a model space emitter, the location is in local space, so convert
				// it to world space.
				if (modelSpace != 0) {
					location = locationHeap.set(location).prj(emitter.node.worldMatrix);
				}

				floatView.put(p0Offset + 0, location.x);
				floatView.put(p0Offset + 1, location.y);
				floatView.put(p0Offset + 2, location.z);
			}
			else {
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
			}

			floatView.put(p0Offset + 6, scale.x);
			floatView.put(p0Offset + 7, scale.y);
			floatView.put(p0Offset + 8, scale.z);

			floatView.put(floatOffset + FLOAT_OFFSET_HEALTH, object.health);
			byteView.put(byteOffset + BYTE_OFFSET_TAIL, (byte) tail);
			byteView.put(byteOffset + BYTE_OFFSET_TEAM_COLOR, (byte) teamColor);

			offset += 1;
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

		if (replaceableId == 1) {
			final List<Texture> teamColors = model.reforged ? MdxHandler.reforgedTeamColors : MdxHandler.teamColors;

			texture = teamColors.get(instance.teamColor);
		}
		else if (replaceableId == 2) {
			final List<Texture> teamGlows = model.reforged ? MdxHandler.reforgedTeamGlows : MdxHandler.teamGlows;

			texture = teamGlows.get(instance.teamColor);
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

		shader.setUniformf("u_lifeSpan", emitterObject.lifeSpan);
		shader.setUniformf("u_timeMiddle", emitterObject.timeMiddle);
		shader.setUniformf("u_columns", emitterObject.columns);
		shader.setUniformf("u_rows", emitterObject.rows);
		shader.setUniformf("u_teamColored", emitterObject.teamColored);

		shader.setUniform3fv("u_intervals[0]", intervals[0], 0, 3);
		shader.setUniform3fv("u_intervals[1]", intervals[1], 0, 3);
		shader.setUniform3fv("u_intervals[2]", intervals[2], 0, 3);
		shader.setUniform3fv("u_intervals[3]", intervals[3], 0, 3);

		shader.setUniform4fv("u_colors[0]", colors[0], 0, 3);
		shader.setUniform4fv("u_colors[1]", colors[1], 0, 3);
		shader.setUniform4fv("u_colors[2]", colors[2], 0, 3);

		shader.setUniform3fv("u_scaling", emitterObject.scaling, 0, 3);

		if (emitterObject.head) {
			shader.setUniform3fv("u_vertices[0]", asFloatArray(vectors[0]), 0, 3);
			shader.setUniform3fv("u_vertices[1]", asFloatArray(vectors[1]), 0, 3);
			shader.setUniform3fv("u_vertices[2]", asFloatArray(vectors[2]), 0, 3);
			shader.setUniform3fv("u_vertices[3]", asFloatArray(vectors[3]), 0, 3);
		}

		if (emitterObject.tail) {
			shader.setUniform3fv("u_cameraZ", asFloatArray(camera.billboardedVectors[6]), 0, 3);
		}
	}

	public static void bindRibbonEmitterBuffer(final RibbonEmitter emitter, final ByteBuffer buffer) {
		Ribbon object = emitter.first;
		final ByteBuffer byteView = buffer;
		final FloatBuffer floatView = buffer.asFloatBuffer();
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

	private static final float[] asFloatArray(final Vector3 vec) {
		vectorTemp[0] = vec.x;
		vectorTemp[1] = vec.y;
		vectorTemp[2] = vec.z;
		return vectorTemp;
	}

}
