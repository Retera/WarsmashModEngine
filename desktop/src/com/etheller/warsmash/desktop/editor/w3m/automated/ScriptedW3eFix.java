package com.etheller.warsmash.desktop.editor.w3m.automated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.etheller.warsmash.parsers.w3x.w3e.Corner;
import com.etheller.warsmash.parsers.w3x.w3e.War3MapW3e;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class ScriptedW3eFix {
	public static void main(String[] args) {
		Set<Double> numbers = new HashSet<>();
		try (FileInputStream stream = new FileInputStream(
				new File("/home/etheller/Games/Warcraft III Patch 1.22/Maps/Download/Modified/war3map.w3e"))) {
			try (LittleEndianDataInputStream wc3Stream = new LittleEndianDataInputStream(stream)) {
				War3MapW3e war3MapW3e = new War3MapW3e(wc3Stream);
				for (Corner[] corners : war3MapW3e.getCorners()) {
					for (Corner corner : corners) {
						if (corner.getWaterHeight() > 0 && corner.getWater() == 0) {
							numbers.add(Double.valueOf(corner.getWaterHeight()));
							corner.setWaterHeight(0);
						}
					}
				}
//				try (LittleEndianDataOutputStream outStream = new LittleEndianDataOutputStream(new FileOutputStream(
//						new File("/home/etheller/Games/Warcraft III Patch 1.22/Maps/Download/Modified/war3map.w3e")))) {
//					war3MapW3e.save(outStream);
//				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(numbers);
	}
}
