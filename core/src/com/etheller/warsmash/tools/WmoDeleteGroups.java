package com.etheller.warsmash.tools;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.etheller.warsmash.parsers.wmo.WorldModelObject;

/**
 * Removes whole groups from a v14 (alpha) WMO by group index, keeping it consistent for the Warsmash
 * parser: drops each group's top-level MOGP chunk, drops its MOGI entry (40 bytes, v14), shrinks the MOGI
 * and MOMO chunk sizes, and decrements MOHD.nGroups. Group index here == the geoset index in a WmoToMdx
 * export (one geoset per group, in order). Other chunks (MOGN names, portals) are left as harmless stale
 * data. Re-parses the result and asserts nGroups / groups / groupInfos all dropped by the right count.
 *
 * args: in.wmo out.wmo  comma,separated,groupIndices
 */
public class WmoDeleteGroups {
	static int tag(final String s) {
		return (s.charAt(0) << 24) | (s.charAt(1) << 16) | (s.charAt(2) << 8) | s.charAt(3);
	}

	public static void main(final String[] args) throws Exception {
		final String in = args[0];
		final String out = args[1];
		final Set<Integer> del = new TreeSet<>();
		for (final String s : args[2].split(",")) {
			del.add(Integer.parseInt(s.trim()));
		}

		final byte[] wmo = Files.readAllBytes(Paths.get(in));
		final ByteBuffer bb = ByteBuffer.wrap(wmo).order(ByteOrder.LITTLE_ENDIAN);
		final int MVER = tag("MVER"), MOMO = tag("MOMO"), MOGP = tag("MOGP"), MOHD = tag("MOHD"), MOGI = tag("MOGI");
		final int GI = 40; // v14 WmoGroupInfo size

		// Pass 1: top-level offsets.
		int momoHeaderPos = -1, momoDataStart = -1, momoSize = -1;
		final List<int[]> topChunks = new ArrayList<>(); // {tag, start, dataStart, size}
		int pos = 0;
		while ((pos + 8) <= wmo.length) {
			final int t = bb.getInt(pos), sz = bb.getInt(pos + 4), ds = pos + 8;
			topChunks.add(new int[] { t, pos, ds, sz });
			if (t == MOMO) {
				momoHeaderPos = pos;
				momoDataStart = ds;
				momoSize = sz;
			}
			pos = ds + sz;
		}
		if (momoHeaderPos < 0) {
			throw new IllegalStateException("MOMO not found");
		}

		// Inside MOMO: MOHD nGroups + MOGI.
		int mohdDataStart = -1, mogiHeaderPos = -1, mogiDataStart = -1, mogiSize = -1;
		int sp = momoDataStart;
		final int momoEnd = momoDataStart + momoSize;
		while ((sp + 8) <= momoEnd) {
			final int t = bb.getInt(sp), sz = bb.getInt(sp + 4), ds = sp + 8;
			if (t == MOHD) {
				mohdDataStart = ds;
			}
			else if (t == MOGI) {
				mogiHeaderPos = sp;
				mogiDataStart = ds;
				mogiSize = sz;
			}
			sp = ds + sz;
		}
		if ((mohdDataStart < 0) || (mogiHeaderPos < 0)) {
			throw new IllegalStateException("MOHD/MOGI not found in MOMO");
		}
		final int nGroups = bb.getInt(mohdDataStart + 4); // MOHD: nTextures, nGroups, ...
		if (mogiSize != (nGroups * GI)) {
			throw new IllegalStateException("ABORT: MOGI size " + mogiSize + " != nGroups*40 " + (nGroups * GI));
		}
		for (final int d : del) {
			if ((d < 0) || (d >= nGroups)) {
				throw new IllegalStateException("ABORT: delete index " + d + " out of range 0.." + (nGroups - 1));
			}
		}
		final int delCount = del.size();

		// Patch MOHD.nGroups in place.
		bb.putInt(mohdDataStart + 4, nGroups - delCount);

		// Assemble output by walking top-level chunks.
		final ByteArrayOutputStream o = new ByteArrayOutputStream(wmo.length);
		int groupIdx = 0;
		for (final int[] c : topChunks) {
			final int t = c[0], start = c[1], ds = c[2], sz = c[3];
			if (t == MOMO) {
				// MOMO tag + new size (MOGI shrank by delCount*GI)
				o.write(wmo, start, 4);
				o.write(le32(momoSize - (delCount * GI)));
				// MOMO data up to MOGI tag (incl MOHD with patched nGroups)
				o.write(wmo, momoDataStart, mogiHeaderPos - momoDataStart);
				// MOGI tag + new size
				o.write(wmo, mogiHeaderPos, 4);
				o.write(le32(mogiSize - (delCount * GI)));
				// kept MOGI entries
				for (int i = 0; i < nGroups; i++) {
					if (!del.contains(i)) {
						o.write(wmo, mogiDataStart + (i * GI), GI);
					}
				}
				// MOMO data after MOGI
				final int afterMogi = mogiDataStart + (nGroups * GI);
				o.write(wmo, afterMogi, momoEnd - afterMogi);
			}
			else if (t == MOGP) {
				if (!del.contains(groupIdx)) {
					o.write(wmo, start, 8 + sz);
				}
				groupIdx++;
			}
			else {
				o.write(wmo, start, 8 + sz);
			}
		}
		final byte[] result = o.toByteArray();
		try (FileOutputStream fos = new FileOutputStream(out)) {
			fos.write(result);
		}
		System.out.println("deleted groups " + del + " of " + nGroups + " -> " + (nGroups - delCount)
				+ " groups; bytes " + wmo.length + " -> " + result.length);

		// Verify by re-parsing.
		final WorldModelObject check = new WorldModelObject(ByteBuffer.wrap(result));
		System.out.println("VERIFY: MOHD nGroups=" + check.getHeaders().getnGroups() + "  parsed groups="
				+ check.getGroups().size() + "  groupInfos=" + check.getHeaders().getGroupInfos().size()
				+ (((check.getHeaders().getnGroups() == (nGroups - delCount))
						&& (check.getGroups().size() == (nGroups - delCount))
						&& (check.getHeaders().getGroupInfos().size() == (nGroups - delCount))) ? "  OK" : "  MISMATCH!"));
	}

	static byte[] le32(final int v) {
		return new byte[] { (byte) v, (byte) (v >> 8), (byte) (v >> 16), (byte) (v >> 24) };
	}
}
