package com.etheller.warsmash.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Files;
import com.hiveworkshop.rms.parsers.mdlx.MdlxAttachment;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBone;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer.FilterMode;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight.Type;
import com.hiveworkshop.rms.parsers.mdlx.MdlxMaterial;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxParticleEmitter;
import com.hiveworkshop.rms.parsers.mdlx.MdlxSequence;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;

public class Test {

	public static void main(final String[] args) {
		final Pattern pattern = Pattern.compile("^\\[(.+?)\\]");
		final Matcher matcher = pattern.matcher("[boat] // ocean");
		if (matcher.matches()) {
			final String name = matcher.group(1).trim().toLowerCase();
			System.out.println(name);
		}
		else {
			System.out.println("no match");
		}

//		Quadtree<String> myQT = new Quadtree<>(new Rectangle(-, y, width, height))

		final MdlxModel model = new MdlxModel();
		upExtent(model.extent);
		MdlxSequence sequence = new MdlxSequence();
		sequence.name = "Stand";
		sequence.interval = new long[] { 333, 332 };
		upExtent(sequence.extent);
		model.sequences.add(sequence);
		sequence = new MdlxSequence();
		sequence.name = "Stand";
		sequence.interval = new long[] { 333, 332 };
		upExtent(sequence.extent);
		model.sequences.add(sequence);
		sequence.name = "Stand";
		sequence.interval = new long[] { 334, 333 };
		upExtent(sequence.extent);
		model.sequences.add(sequence);
		sequence.name = "Stand";
		sequence.interval = new long[] { 331, 330 };
		upExtent(sequence.extent);
		model.sequences.add(sequence);

		MdlxGeoset mdlxGeoset = new MdlxGeoset();
		upExtent(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.vertices = new float[] { -128f, -128f, 0f, 128f, -128f, 0f, 128f, 128f, 0f, -128f, 128f, 0f };
		mdlxGeoset.vertexGroups = new short[] { 0, 0, 0, 0 };
		mdlxGeoset.normals = new float[] { 0, 0, 1, 0, 128f, 1, 0, 0, Float.NaN, 0, 0, 1 };
		mdlxGeoset.faceTypeGroups = new long[] { 1, 0 };
		mdlxGeoset.faces = new int[] { 0, 1, 2, 3 };
		mdlxGeoset.faceGroups = new long[] { 2, 2 };
		mdlxGeoset.materialId = 0;
		mdlxGeoset.matrixGroups = new long[] { 3 };
		mdlxGeoset.matrixIndices = new long[] { 0, 1, 2 };
		mdlxGeoset.uvSets = new float[][] { { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f } };
		model.geosets.add(mdlxGeoset);

		final int n = 3600;
		mdlxGeoset = new MdlxGeoset();
		upExtent(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.sequenceExtents.add(mdlxGeoset.extent);
		mdlxGeoset.vertices = new float[(n + 1) * 3];
		for (int i = 1; i <= n; i++) {
			final float distance = 4000;
			final float down = (float) ((((((i / 360) % 10) + 1) / 10f) * Math.PI) / 2);
			final float around = (float) (((i % 360) / 360f) * Math.PI * 2);
			final float xyRad = (float) (Math.sin(down) * distance);
			final float z = (float) (Math.cos(down) * distance);
			mdlxGeoset.vertices[i * 3] = (float) (Math.cos(around) * xyRad);
			mdlxGeoset.vertices[(i * 3) + 1] = (float) (Math.sin(around) * xyRad);
			mdlxGeoset.vertices[(i * 3) + 2] = z;
		}
		mdlxGeoset.vertexGroups = new short[(n + 1)];
		mdlxGeoset.normals = new float[(n + 1) * 3];
		mdlxGeoset.faceTypeGroups = new long[n];
		Arrays.fill(mdlxGeoset.faceTypeGroups, 1);
		mdlxGeoset.faces = new int[n * 2];
		for (int i = 0; i < n; i++) {
			mdlxGeoset.faces[(i * 2) + 1] = i + 1;
		}
		mdlxGeoset.faceGroups = new long[n];
		Arrays.fill(mdlxGeoset.faceGroups, 2);
		mdlxGeoset.materialId = 0;
		mdlxGeoset.matrixGroups = new long[] { 3 };
		mdlxGeoset.matrixIndices = new long[] { 0, 1, 2 };
		mdlxGeoset.uvSets = new float[][] { { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f } };
		model.geosets.add(mdlxGeoset);

		final MdlxTexture texture = new MdlxTexture();
		texture.setPath("Textures\\white.blp");
		model.textures.add(texture);

		final MdlxMaterial material = new MdlxMaterial();
		final MdlxLayer layer = new MdlxLayer();
		layer.alpha = 1.0f;
		layer.textureId = 0;
		layer.filterMode = FilterMode.NONE;
		material.layers.add(layer);
		model.materials.add(material);

		final MdlxBone stupid1 = makeStupidBone("Bone_A");
		stupid1.setObjectId(0);
		stupid1.setParentId(-1);
		model.bones.add(stupid1);
		final MdlxBone stupid2 = makeStupidBone("Bone_B");
		stupid2.setObjectId(1);
		stupid1.setParentId(-1);
		model.bones.add(stupid2);
		final MdlxBone stupid3 = makeStupidBone("Bone_C");
		stupid2.setObjectId(2);
		stupid1.setParentId(-1);
		model.bones.add(stupid3);

		final MdlxLight light = new MdlxLight();
		light.ambientColor[0] = -5;
		light.ambientColor[1] = 0;
		light.ambientColor[2] = Float.MAX_VALUE;
		light.ambientIntensity = Float.MAX_VALUE;
		light.intensity = Float.MAX_VALUE;
		light.attenuation[0] = Float.MAX_VALUE;
		light.attenuation[1] = Float.MAX_VALUE;
		light.color[0] = -5;
		light.color[1] = -5;
		light.color[2] = Float.MAX_VALUE;
		light.type = Type.OMNIDIRECTIONAL;
		light.name = "#!@$!@#$";
		light.objectId = 3;
		light.parentId = -1;
		model.lights.add(light);

		final MdlxAttachment attachment = new MdlxAttachment();
		attachment.setObjectId(4);
		attachment.setParentId(-1);
		attachment.setName("!@#$");
//		attachment.setPath("war3mapImport\\stupidFan.mdl");
		model.attachments.add(attachment);

		final MdlxParticleEmitter mdlxParticleEmitter = new MdlxParticleEmitter();
		mdlxParticleEmitter.emissionRate = 99999;
		mdlxParticleEmitter.flags |= 0x8000;
		mdlxParticleEmitter.lifeSpan = 99999;
		mdlxParticleEmitter.name = "ATCH";
		mdlxParticleEmitter.objectId = 5;
		mdlxParticleEmitter.parentId = -1;
		mdlxParticleEmitter.path = "Doodads\\Cinematic\\ArthasIllidanFight\\ArthasIllidanFight.mdl";
		model.particleEmitters.add(mdlxParticleEmitter);

		model.pivotPoints.add(new float[] { 0, 0, 0 });
		model.pivotPoints.add(new float[] { 0, 0, 0 });
		model.pivotPoints.add(new float[] { 0, 0, 0 });
		model.pivotPoints.add(new float[] { 0, 0, 0 });
		model.pivotPoints.add(new float[] { 0, 0, 0 });
		model.pivotPoints.add(new float[] { 0, 0, 0 });

		final ByteBuffer mdxBuffer = model.saveMdx();
		try {
			Files.write(mdxBuffer.array(), new File("C:\\Temp\\doomball.mdx"));
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void upExtent(final MdlxExtent extent) {
		extent.boundsRadius = Float.MAX_VALUE;
		extent.max[0] = Float.MAX_VALUE;
		extent.max[1] = Float.MAX_VALUE;
		extent.max[2] = Float.MAX_VALUE;
		extent.min[0] = -Float.MAX_VALUE;
		extent.min[1] = -Float.MAX_VALUE;
		extent.min[2] = -Float.MAX_VALUE;
	}

	private static MdlxBone makeStupidBone(final String name) {
		final MdlxBone stupid1 = new MdlxBone();
		stupid1.setName(name);
		stupid1.setGeosetId(-1);
		stupid1.setGeosetAnimationId(-1);
		return stupid1;
	}

}
