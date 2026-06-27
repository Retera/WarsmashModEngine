package com.etheller.warsmash.tools;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;

/** Loads an MDX of any version and re-saves it as MDX800 (for editing an MDX1300 doodad as 800). args: in out */
public class MdxTo800 {
	public static void main(final String[] args) throws Exception {
		final MdlxModel m = new MdlxModel(ByteBuffer.wrap(Files.readAllBytes(Paths.get(args[0]))));
		System.out.println("loaded version=" + m.version + " geosets=" + m.geosets.size() + " materials="
				+ m.materials.size() + " textures=" + m.textures.size() + " bones=" + m.bones.size() + " seqs="
				+ m.sequences.size());
		m.version = 800;
		final ByteBuffer out = m.saveMdx();
		try (FileOutputStream fos = new FileOutputStream(args[1])) {
			fos.write(out.array(), 0, out.limit());
		}
		out.rewind();
		final MdlxModel re = new MdlxModel(out);
		System.out.println("saved 800 bytes=" + out.limit() + "; reload version=" + re.version + " geosets="
				+ re.geosets.size() + " materials=" + re.materials.size() + " textures=" + re.textures.size()
				+ (re.geosets.size() == m.geosets.size() ? "  OK" : "  GEOSET MISMATCH"));
	}
}
