package com.etheller.warsmash.tools;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.parsers.wmo.ModelObjectGroup;
import com.etheller.warsmash.parsers.wmo.WorldModelObject;
import com.hiveworkshop.rms.parsers.mdlx.AnimationMap;
import com.hiveworkshop.rms.parsers.mdlx.MdlxAttachment;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGenericObject;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.timeline.MdlxTimeline;

/**
 * Patches the vertex positions (and normals) edited in a {@link WmoToMdx}-exported, ReteraModelStudio-edited
 * MDX800 back into the original WMO, IN PLACE (overwrites each group's MOVT/MONR float data; same counts),
 * then writes a new WMO. Move-only edits in RMS (no add/delete/weld) are required; verified safe for RMS
 * via a no-edit round-trip (vertex order/count preserved).
 *
 * The geoset->(group, vertOffset) mapping is reconstructed deterministically (same iteration as WmoToMdx),
 * and every MOVT chunk size is validated against the parsed vertex count -> ABORTS rather than corrupt.
 *
 * args: original.wmo edited.mdx output.wmo
 */
public class MdxToWmo {
	static int tag(final String s) {
		return (s.charAt(0) << 24) | (s.charAt(1) << 16) | (s.charAt(2) << 8) | s.charAt(3);
	}

	public static void main(final String[] args) throws Exception {
		final String inWmo = args[0];
		final String inMdx = args[1];
		final String outWmo = args[2];

		final byte[] wmo = Files.readAllBytes(Paths.get(inWmo));
		final ByteBuffer bb = ByteBuffer.wrap(wmo).order(ByteOrder.LITTLE_ENDIAN);
		final WorldModelObject parsed = new WorldModelObject(ByteBuffer.wrap(wmo.clone()));
		final List<ModelObjectGroup> groups = parsed.getGroups();
		final MdlxModel mdx = new MdlxModel(ByteBuffer.wrap(Files.readAllBytes(Paths.get(inMdx))));

		// 1) Reconstruct the geoset -> (groupIndex, vertOffset, vertCount) mapping exactly as WmoToMdx emits.
		final List<int[]> map = new ArrayList<>();
		for (int gi = 0; gi < groups.size(); gi++) {
			final float[] v = groups.get(gi).getVertices();
			if ((v == null) || (v.length == 0)) {
				continue;
			}
			final int total = v.length / 3;
			for (int off = 0; off < total; off += WmoToMdx.MAX_GEOSET_VERTS) {
				map.add(new int[] { gi, off, Math.min(WmoToMdx.MAX_GEOSET_VERTS, total - off) });
			}
		}
		if (map.size() != mdx.geosets.size()) {
			throw new IllegalStateException("ABORT: geoset count " + mdx.geosets.size() + " != expected " + map.size()
					+ " (RMS changed model structure)");
		}

		// 2) Find each group's MOVT and MONR data byte offsets via a chunk walk. The WMO is: MVER, MOMO
		// {headers}, then one top-level MOGP chunk per group (in group order). The top-level MOGP tag+size IS
		// the group-header chunk header (the parser rewinds and re-reads it), so the 120-byte group header
		// fields start right at the chunk's dataStart, and the real subchunks (MOPY, MOVT, MONR, ...) follow.
		final int MOGP = tag("MOGP"), MOVT = tag("MOVT"), MONR = tag("MONR");
		final int MOMO = tag("MOMO"), MODD = tag("MODD"), MOLT = tag("MOLT");
		final int HEADER_FIELD_BYTES = 120; // see ModelObjectGroup.loadHeaderChunk (v14): 52 + 8*8 batches + 4
		// MODD doodad records (40 bytes, position at +4) and MOLT light records (v14: 32 bytes, position at +8)
		// live as flat subchunks inside the MOMO headers container. Found by walking MOMO.
		int moddOff = -1, moddLen = 0, moltOff = -1, moltLen = 0;
		final int[] movtOff = new int[groups.size()];
		final int[] monrOff = new int[groups.size()];
		final int[] movtLen = new int[groups.size()];
		final int[] monrLen = new int[groups.size()];
		java.util.Arrays.fill(movtOff, -1);
		java.util.Arrays.fill(monrOff, -1);

		int groupIdx = 0;
		int pos = 0;
		while ((pos + 8) <= wmo.length) {
			final int chunkTag = bb.getInt(pos);
			final int size = bb.getInt(pos + 4);
			final int dataStart = pos + 8;
			if (chunkTag == MOGP) {
				final int groupEnd = Math.min(dataStart + size, wmo.length);
				int sp = dataStart + HEADER_FIELD_BYTES; // skip the 120-byte group header; subchunks follow
				while ((sp + 8) <= groupEnd) {
					final int st = bb.getInt(sp);
					final int ss = bb.getInt(sp + 4);
					final int sd = sp + 8;
					if ((sd + ss) > groupEnd) {
						break; // malformed / overrun guard
					}
					if ((st == MOVT) && (groupIdx < groups.size())) {
						movtOff[groupIdx] = sd;
						movtLen[groupIdx] = ss;
					}
					else if ((st == MONR) && (groupIdx < groups.size())) {
						monrOff[groupIdx] = sd;
						monrLen[groupIdx] = ss;
					}
					sp = sd + ss;
				}
				groupIdx++;
				pos = dataStart + size;
			}
			else if (chunkTag == MOMO) {
				// Flat honest [tag][size] subchunks; find MODD + MOLT.
				final int momoEnd = Math.min(dataStart + size, wmo.length);
				int sp = dataStart;
				while ((sp + 8) <= momoEnd) {
					final int st = bb.getInt(sp);
					final int ss = bb.getInt(sp + 4);
					final int sd = sp + 8;
					if ((sd + ss) > momoEnd) {
						break;
					}
					if (st == MODD) {
						moddOff = sd;
						moddLen = ss;
					}
					else if (st == MOLT) {
						moltOff = sd;
						moltLen = ss;
					}
					sp = sd + ss;
				}
				pos = dataStart + size;
			}
			else {
				pos = dataStart + size;
			}
		}

		// 3) Validate offsets/sizes against the parsed vertex counts -> abort on any mismatch.
		for (int gi = 0; gi < groups.size(); gi++) {
			final float[] v = groups.get(gi).getVertices();
			if ((v == null) || (v.length == 0)) {
				continue;
			}
			if ((movtOff[gi] < 0) || (movtLen[gi] != (v.length * 4))) {
				throw new IllegalStateException("ABORT group " + gi + ": MOVT not found / size " + movtLen[gi]
						+ " != " + (v.length * 4) + " (chunk walk desync)");
			}
		}

		// 4) Overwrite MOVT in place from the edited geosets (normals are handled in 4b).
		int patchedVerts = 0;
		for (int m = 0; m < map.size(); m++) {
			final int[] e = map.get(m);
			final int gi = e[0], off = e[1], n = e[2];
			final MdlxGeoset geo = mdx.geosets.get(m);
			if ((geo.vertices.length / 3) != n) {
				throw new IllegalStateException("ABORT geoset " + m + ": vert count " + (geo.vertices.length / 3)
						+ " != " + n + " (RMS welded/added/removed verts)");
			}
			final int vBase = movtOff[gi] + (off * 12);
			for (int k = 0; k < (n * 3); k++) {
				bb.putFloat(vBase + (k * 4), geo.vertices[k]);
			}
			patchedVerts += n;
		}

		// 4b) RECOMPUTE NORMALS only for vertices that MOVED (vs the pristine WMO), keeping the original
		// authored normals everywhere else. (We can't reproduce the original normals geometrically - they
		// use smoothing-group/source info we don't have - so we only touch the deformed region.) Geometric
		// normal = angle-weighted average of adjacent face normals, exact-position-welded WITHIN the group.
		int patchedNormals = 0;
		for (int gi = 0; gi < groups.size(); gi++) {
			final float[] pristine = groups.get(gi).getVertices();
			if ((pristine == null) || (pristine.length == 0) || (monrOff[gi] < 0)) {
				continue;
			}
			final int count = pristine.length / 3;
			final float[] edited = new float[count * 3];
			for (int k = 0; k < (count * 3); k++) {
				edited[k] = bb.getFloat(movtOff[gi] + (k * 4)); // edited positions (already patched)
			}
			// which exact-positions contain a moved vertex
			final java.util.Map<Long, float[]> accum = new java.util.HashMap<>();
			final java.util.Set<Long> touched = new java.util.HashSet<>();
			final long[] key = new long[count];
			boolean anyMoved = false;
			for (int i = 0; i < count; i++) {
				key[i] = posKey(edited, i);
				boolean moved = false;
				for (int c = 0; c < 3; c++) {
					if (Math.abs(edited[(i * 3) + c] - pristine[(i * 3) + c]) > 1e-4f) {
						moved = true;
					}
				}
				if (moved) {
					touched.add(key[i]);
					anyMoved = true;
				}
			}
			if (!anyMoved) {
				continue;
			}
			final int nTri = count / 3;
			for (int t = 0; t < nTri; t++) {
				final float[] fn = faceNormalUnnormalized(edited, t); // magnitude carries 2*area
				final float fl = (float) Math.sqrt((fn[0] * fn[0]) + (fn[1] * fn[1]) + (fn[2] * fn[2]));
				for (int j = 0; j < 3; j++) {
					final float w = cornerAngle(edited, t, j); // angle-weighted
					final float[] a = accum.computeIfAbsent(key[(t * 3) + j], k -> new float[3]);
					if (fl > 1e-12f) {
						a[0] += (fn[0] / fl) * w;
						a[1] += (fn[1] / fl) * w;
						a[2] += (fn[2] / fl) * w;
					}
				}
			}
			for (int i = 0; i < count; i++) {
				if (!touched.contains(key[i])) {
					continue; // keep original authored normal
				}
				final float[] a = accum.get(key[i]);
				float x = a[0], y = a[1], z = a[2];
				final float l = (float) Math.sqrt((x * x) + (y * y) + (z * z));
				if (l > 1e-12f) {
					x /= l;
					y /= l;
					z /= l;
				}
				final int nb = monrOff[gi] + (i * 12);
				bb.putFloat(nb, x);
				bb.putFloat(nb + 4, y);
				bb.putFloat(nb + 8, z);
				patchedNormals++;
			}
		}

		// 5) Reposition MODD doodads / MOLT lights from the named MDX nodes (DoodadIdx_N / LightIdx_N).
		final int nDoodads = (parsed.getHeaders().getDoodadDefinitions() == null) ? 0
				: parsed.getHeaders().getDoodadDefinitions().size();
		final int nLights = (parsed.getHeaders().getLights() == null) ? 0 : parsed.getHeaders().getLights().size();
		if ((moddOff >= 0) && (moddLen != (nDoodads * 40))) {
			throw new IllegalStateException("ABORT: MODD size " + moddLen + " != " + (nDoodads * 40));
		}
		if ((moltOff >= 0) && (moltLen != (nLights * 32))) {
			throw new IllegalStateException("ABORT: MOLT size " + moltLen + " != " + (nLights * 32));
		}
		int patchedDoodads = 0, patchedLights = 0;
		for (final MdlxAttachment att : mdx.attachments) {
			final Integer idx = parseIdx(att.getName(), "DoodadIdx_");
			if ((idx == null) || (moddOff < 0) || (idx < 0) || (idx >= nDoodads)) {
				continue;
			}
			final float[] p = nodeLocalPos(att, mdx.pivotPoints);
			final int base = moddOff + (idx * 40) + 4; // MODD position offset
			bb.putFloat(base, p[0]);
			bb.putFloat(base + 4, p[1]);
			bb.putFloat(base + 8, p[2]);
			patchedDoodads++;
		}
		for (final MdlxLight lt : mdx.lights) {
			final Integer idx = parseIdx(lt.getName(), "LightIdx_");
			if ((idx == null) || (moltOff < 0) || (idx < 0) || (idx >= nLights)) {
				continue;
			}
			final float[] p = nodeLocalPos(lt, mdx.pivotPoints);
			final int base = moltOff + (idx * 32) + 8; // MOLT position offset (v14)
			bb.putFloat(base, p[0]);
			bb.putFloat(base + 4, p[1]);
			bb.putFloat(base + 8, p[2]);
			patchedLights++;
		}

		try (FileOutputStream fos = new FileOutputStream(outWmo)) {
			fos.write(wmo);
		}
		System.out.println("wrote " + outWmo + ": patched " + patchedVerts + " vertices, " + patchedNormals
				+ " normals across " + map.size() + " geosets / " + groups.size() + " groups; " + patchedDoodads
				+ " doodads, " + patchedLights + " lights repositioned");
	}

	/** Parses the trailing integer of a node name like "DoodadIdx_109" (returns null if it doesn't match). */
	private static Integer parseIdx(final String name, final String prefix) {
		if ((name == null) || !name.startsWith(prefix)) {
			return null;
		}
		final StringBuilder digits = new StringBuilder();
		for (int i = prefix.length(); i < name.length(); i++) {
			final char c = name.charAt(i);
			if (Character.isDigit(c)) {
				digits.append(c);
			}
			else if (digits.length() > 0) {
				break;
			}
		}
		if (digits.length() == 0) {
			return null;
		}
		return Integer.parseInt(digits.toString());
	}

	private static long posKey(final float[] v, final int i) {
		final long x = Float.floatToIntBits(v[i * 3]) & 0xFFFFFFFFL;
		final long y = Float.floatToIntBits(v[(i * 3) + 1]) & 0xFFFFFFFFL;
		final long z = Float.floatToIntBits(v[(i * 3) + 2]) & 0xFFFFFFFFL;
		return (x * 73856093L) ^ (y * 19349663L) ^ (z * 83492791L);
	}

	private static float[] faceNormalUnnormalized(final float[] v, final int t) {
		final int a = t * 3, b = a + 1, c = a + 2;
		final float ax = v[a * 3], ay = v[(a * 3) + 1], az = v[(a * 3) + 2];
		final float e1x = v[b * 3] - ax, e1y = v[(b * 3) + 1] - ay, e1z = v[(b * 3) + 2] - az;
		final float e2x = v[c * 3] - ax, e2y = v[(c * 3) + 1] - ay, e2z = v[(c * 3) + 2] - az;
		return new float[] { (e1y * e2z) - (e1z * e2y), (e1z * e2x) - (e1x * e2z), (e1x * e2y) - (e1y * e2x) };
	}

	private static float cornerAngle(final float[] v, final int t, final int corner) {
		final int a = (t * 3) + corner, b = (t * 3) + ((corner + 1) % 3), c = (t * 3) + ((corner + 2) % 3);
		final float ux = v[b * 3] - v[a * 3], uy = v[(b * 3) + 1] - v[(a * 3) + 1], uz = v[(b * 3) + 2] - v[(a * 3) + 2];
		final float wx = v[c * 3] - v[a * 3], wy = v[(c * 3) + 1] - v[(a * 3) + 1], wz = v[(c * 3) + 2] - v[(a * 3) + 2];
		final float ul = (float) Math.sqrt((ux * ux) + (uy * uy) + (uz * uz));
		final float wl = (float) Math.sqrt((wx * wx) + (wy * wy) + (wz * wz));
		if ((ul < 1e-9f) || (wl < 1e-9f)) {
			return 0;
		}
		float d = ((ux * wx) + (uy * wy) + (uz * wz)) / (ul * wl);
		d = Math.max(-1f, Math.min(1f, d));
		return (float) Math.acos(d);
	}

	/** A node's position in model (= WMO-local) space: its pivot point plus the first KGTR translation
	 * keyframe (covers RMS moving the pivot OR adding a translation track). */
	private static float[] nodeLocalPos(final MdlxGenericObject node, final java.util.List<float[]> pivots) {
		final float[] p = ((node.objectId >= 0) && (node.objectId < pivots.size()))
				? pivots.get(node.objectId).clone()
				: new float[] { 0, 0, 0 };
		for (final MdlxTimeline<?> t : node.getTimelines()) {
			if (AnimationMap.KGTR.getWar3id().equals(t.getName())) {
				final Object[] vals = t.getValues();
				if ((vals != null) && (vals.length > 0) && (vals[0] instanceof float[])) {
					final float[] v0 = (float[]) vals[0];
					if (v0.length >= 3) {
						p[0] += v0[0];
						p[1] += v0[1];
						p[2] += v0[2];
					}
				}
			}
		}
		return p;
	}
}
