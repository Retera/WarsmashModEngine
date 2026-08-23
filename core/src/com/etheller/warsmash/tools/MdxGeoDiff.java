package com.etheller.warsmash.tools;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

/** Compares two MDX files geoset-by-geoset (count, per-geoset vertex count, vertex-position deltas) to
 * verify that an external editor (ReteraModelStudio) preserved vertex ORDER and COUNT on save. */
public class MdxGeoDiff {
	public static void main(final String[] args) throws Exception {
		final MdlxModel a = new MdlxModel(ByteBuffer.wrap(Files.readAllBytes(Paths.get(args[0]))));
		final MdlxModel b = new MdlxModel(ByteBuffer.wrap(Files.readAllBytes(Paths.get(args[1]))));
		System.out.println("A geosets=" + a.geosets.size() + "  B geosets=" + b.geosets.size());
		if (a.geosets.size() != b.geosets.size()) {
			System.out.println("MISMATCH: geoset count differs -> editor changed structure (split/merged geosets)");
		}
		final int n = Math.min(a.geosets.size(), b.geosets.size());
		int countMismatch = 0;
		float globalMaxDelta = 0;
		int movedGeosets = 0;
		for (int i = 0; i < n; i++) {
			final MdlxGeoset ga = a.geosets.get(i);
			final MdlxGeoset gb = b.geosets.get(i);
			final int va = ga.vertices.length / 3, vb = gb.vertices.length / 3;
			if (va != vb) {
				countMismatch++;
				System.out.printf("  geoset%d VERT COUNT %d -> %d  (editor welded/added/removed verts!)%n", i, va, vb);
				continue;
			}
			float maxD = 0;
			for (int k = 0; k < ga.vertices.length; k++) {
				final float d = Math.abs(ga.vertices[k] - gb.vertices[k]);
				if (d > maxD) {
					maxD = d;
				}
			}
			if (maxD > globalMaxDelta) {
				globalMaxDelta = maxD;
			}
			if (maxD > 0.001f) {
				movedGeosets++;
			}
		}
		System.out.println("vertex-count mismatches: " + countMismatch);
		System.out.printf("max vertex position delta across all geosets: %.5f%n", globalMaxDelta);
		System.out.println("geosets with any moved vertex (>0.001): " + movedGeosets);
		if (countMismatch == 0) {
			System.out.println("RESULT: counts preserved -> index-based patch-back is SAFE. "
					+ (globalMaxDelta < 0.01f ? "(no-edit resave: positions identical, order preserved)"
							: "(positions changed = edits present)"));
		}
		else {
			System.out.println("RESULT: counts changed -> RMS welds/optimizes; need an index-tag scheme, not raw index.");
		}
	}
}
