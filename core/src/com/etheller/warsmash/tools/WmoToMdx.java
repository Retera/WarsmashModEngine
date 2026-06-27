package com.etheller.warsmash.tools;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.wmo.ModelObjectGroup;
import com.etheller.warsmash.parsers.wmo.WmoDoodadDefinition;
import com.etheller.warsmash.parsers.wmo.WmoLight;
import com.etheller.warsmash.parsers.wmo.WorldModelObject;
import com.hiveworkshop.rms.parsers.mdlx.MdlxAttachment;
import com.hiveworkshop.rms.parsers.mdlx.MdlxBone;
import com.hiveworkshop.rms.parsers.mdlx.MdlxExtent;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLayer;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight;
import com.hiveworkshop.rms.parsers.mdlx.MdlxMaterial;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.MdlxSequence;
import com.hiveworkshop.rms.parsers.mdlx.MdlxTexture;

/**
 * Exports a (v14 alpha) WMO to a single MDX800 for hand-editing in ReteraModelStudio, then patching the
 * edited vertices back with {@link MdxToWmo}. ONE geoset per WMO group, in group order, vertices in raw
 * MOVT order (NOT centered) so all groups assemble into one tunnel and patch-back is a 1:1 index write.
 *
 * Groups larger than {@link #MAX_GEOSET_VERTS} are split into consecutive geosets at triangle boundaries
 * (MDX face indices are uint16). The split is DETERMINISTIC (group order, then 65535-vert chunks) so
 * MdxToWmo reproduces the exact same geoset->(group, vertexOffset) mapping without extra metadata.
 *
 * The WMO mesh is un-indexed (3 consecutive MOVT verts per triangle), so faces are the identity list.
 * Materials/textures are placeholders (geometry editing doesn't need them).
 *
 * args: in.wmo out.mdx
 */
public class WmoToMdx {
	/** Max verts per geoset (uint16 face indices); multiple of 3 so triangles are never split. */
	public static final int MAX_GEOSET_VERTS = 65535; // 65535 / 3 == 21845 exactly

	public static void main(final String[] args) throws Exception {
		final String inWmo = args[0];
		final String outMdx = args[1];
		// Optional 3rd arg "nonodes": skip exporting MODD doodads / MOLT lights as nodes. Useful for huge
		// WMOs (e.g. Ironforge has 3370 doodads) where the node count would overwhelm the editor and you're
		// only reshaping geometry. Patch-back then leaves all doodad/light positions untouched.
		final boolean exportNodes = !((args.length > 2) && args[2].equalsIgnoreCase("nonodes"));

		final byte[] wmoBytes = Files.readAllBytes(Paths.get(inWmo));
		final WorldModelObject wmo = new WorldModelObject(ByteBuffer.wrap(wmoBytes));
		final List<ModelObjectGroup> groups = wmo.getGroups();

		final MdlxModel model = new MdlxModel();
		model.version = 800;
		model.name = "wmo_export";

		final MdlxSequence seq = new MdlxSequence();
		seq.name = "Stand";
		seq.interval[0] = 0;
		seq.interval[1] = 1000;
		model.sequences.add(seq);

		final MdlxTexture tex = new MdlxTexture();
		tex.path = "";
		tex.replaceableId = 0;
		model.textures.add(tex);
		final MdlxMaterial mat = new MdlxMaterial();
		final MdlxLayer layer = new MdlxLayer();
		layer.alpha = 1.0f;
		layer.textureId = 0;
		layer.flags |= MdlxLayer.Flags.TWO_SIDED; // tunnels are viewed from inside
		mat.layers.add(layer);
		model.materials.add(mat);

		final MdlxBone bone = new MdlxBone();
		bone.name = "Root";
		bone.objectId = 0;
		bone.parentId = -1;
		bone.geosetId = -1;
		bone.geosetAnimationId = -1;
		model.bones.add(bone);
		model.pivotPoints.add(new float[] { 0, 0, 0 });

		final float[] gmin = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
		final float[] gmax = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };

		int geosetCount = 0;
		final List<int[]> mapping = new ArrayList<>(); // {groupIndex, vertOffset, vertCount} per geoset (for logging)
		for (int gi = 0; gi < groups.size(); gi++) {
			final ModelObjectGroup g = groups.get(gi);
			final float[] verts = g.getVertices();
			if ((verts == null) || (verts.length == 0)) {
				continue;
			}
			final int total = verts.length / 3;
			final float[] normals = (g.getNormals() != null) && (g.getNormals().length == verts.length)
					? g.getNormals()
					: new float[verts.length];
			final float[] uv = (g.getTextureVertices() != null) && (g.getTextureVertices().length == total * 2)
					? g.getTextureVertices()
					: new float[total * 2];

			for (int off = 0; off < total; off += MAX_GEOSET_VERTS) {
				final int n = Math.min(MAX_GEOSET_VERTS, total - off);
				final MdlxGeoset geo = new MdlxGeoset();
				geo.vertices = new float[n * 3];
				geo.normals = new float[n * 3];
				geo.uvSets = new float[][] { new float[n * 2] };
				System.arraycopy(verts, off * 3, geo.vertices, 0, n * 3);
				System.arraycopy(normals, off * 3, geo.normals, 0, n * 3);
				System.arraycopy(uv, off * 2, geo.uvSets[0], 0, n * 2);
				geo.faces = new int[n];
				for (int i = 0; i < n; i++) {
					geo.faces[i] = i; // identity: un-indexed triangles
				}
				geo.faceTypeGroups = new long[] { 4 }; // GL_TRIANGLES
				geo.faceGroups = new long[] { n };
				geo.vertexGroups = new short[n];
				geo.matrixGroups = new long[] { 1 };
				geo.matrixIndices = new long[] { 0 };
				geo.materialId = 0;
				geo.extent = extentOf(geo.vertices);
				final MdlxExtent se = new MdlxExtent();
				se.min = geo.extent.min.clone();
				se.max = geo.extent.max.clone();
				se.boundsRadius = geo.extent.boundsRadius;
				geo.sequenceExtents.add(se);
				model.geosets.add(geo);
				accumulate(geo.vertices, gmin, gmax);
				mapping.add(new int[] { gi, off, n });
				geosetCount++;
			}
		}

		// Export each WMO doodad (MODD) as a named MDX Attachment node "DoodadIdx_N" at the doodad's
		// position, and each WMO light (MOLT) as a named MDX Light "LightIdx_N" at the light's position, so
		// they can be moved in RMS and repositioned by MdxToWmo (which parses the name -> MODD/MOLT index).
		// objectId == pivotPoint index, so set objectId = pivotPoints.size() right before adding the pivot.
		final List<WmoDoodadDefinition> doodads = wmo.getHeaders().getDoodadDefinitions();
		int doodadCount = 0;
		if (exportNodes && (doodads != null)) {
			for (int n = 0; n < doodads.size(); n++) {
				final float[] p = doodads.get(n).getPosition();
				final MdlxAttachment att = new MdlxAttachment();
				att.name = "DoodadIdx_" + n;
				att.objectId = model.pivotPoints.size();
				att.parentId = 0;
				att.path = "";
				att.attachmentId = n;
				model.attachments.add(att);
				model.pivotPoints.add(new float[] { p[0], p[1], p[2] });
				doodadCount++;
			}
		}
		final List<WmoLight> lights = wmo.getHeaders().getLights();
		int lightCount = 0;
		if (exportNodes && (lights != null)) {
			for (int n = 0; n < lights.size(); n++) {
				final WmoLight wl = lights.get(n);
				final float[] p = wl.getPosition();
				final MdlxLight lt = new MdlxLight();
				lt.name = "LightIdx_" + n;
				lt.objectId = model.pivotPoints.size();
				lt.parentId = 0;
				switch (wl.getType()) {
				case DIRECTIONAL:
					lt.type = MdlxLight.Type.DIRECTIONAL;
					break;
				case AMBIENT:
					lt.type = MdlxLight.Type.AMBIENT;
					break;
				default:
					lt.type = MdlxLight.Type.OMNIDIRECTIONAL;
					break;
				}
				final short[] c = wl.getColor(); // bgra
				lt.color = new float[] { c[2] / 255f, c[1] / 255f, c[0] / 255f };
				lt.intensity = wl.getIntensity();
				lt.attenuation = new float[] { wl.getAttenStart(), wl.getAttenEnd() };
				model.lights.add(lt);
				model.pivotPoints.add(new float[] { p[0], p[1], p[2] });
				lightCount++;
			}
		}

		model.extent = extentFromMinMax(gmin, gmax);
		seq.extent = extentFromMinMax(gmin, gmax);

		final ByteBuffer out = model.saveMdx();
		try (FileOutputStream fos = new FileOutputStream(outMdx)) {
			fos.write(out.array(), 0, out.limit());
		}
		System.out.println("wrote " + outMdx + ": groups=" + groups.size() + " geosets=" + geosetCount
				+ " doodadAttachments=" + doodadCount + " lights=" + lightCount + " bytes=" + out.limit());

		// Round-trip self-check: reload the MDX we just wrote and confirm vertices survived saveMdx/load
		// exactly (proves the EXPORT side; RMS preservation is a separate test once you load+save it).
		out.rewind();
		final MdlxModel reload = new MdlxModel(out);
		int mismatches = 0;
		if (reload.geosets.size() != geosetCount) {
			System.out.println("SELFCHECK FAIL: geoset count " + reload.geosets.size() + " != " + geosetCount);
		}
		for (int i = 0; i < Math.min(reload.geosets.size(), model.geosets.size()); i++) {
			final float[] a = model.geosets.get(i).vertices;
			final float[] b = reload.geosets.get(i).vertices;
			if (a.length != b.length) {
				mismatches++;
				continue;
			}
			for (int k = 0; k < a.length; k++) {
				if (a[k] != b[k]) {
					mismatches++;
					break;
				}
			}
		}
		System.out.println("SELFCHECK: vertex round-trip mismatches across geosets = " + mismatches
				+ (mismatches == 0 ? "  (export is faithful)" : "  (PROBLEM)"));
	}

	private static MdlxExtent extentOf(final float[] verts) {
		final float[] min = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
		final float[] max = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
		accumulate(verts, min, max);
		return extentFromMinMax(min, max);
	}

	private static void accumulate(final float[] verts, final float[] min, final float[] max) {
		for (int i = 0; i + 2 < verts.length; i += 3) {
			for (int k = 0; k < 3; k++) {
				final float v = verts[i + k];
				if (v < min[k]) {
					min[k] = v;
				}
				if (v > max[k]) {
					max[k] = v;
				}
			}
		}
	}

	private static MdlxExtent extentFromMinMax(final float[] min, final float[] max) {
		final MdlxExtent e = new MdlxExtent();
		e.min = min.clone();
		e.max = max.clone();
		final float dx = max[0] - min[0], dy = max[1] - min[1], dz = max[2] - min[2];
		e.boundsRadius = (float) (0.5 * Math.sqrt((dx * dx) + (dy * dy) + (dz * dz)));
		return e;
	}
}
