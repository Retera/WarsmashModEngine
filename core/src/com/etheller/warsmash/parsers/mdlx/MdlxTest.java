package com.etheller.warsmash.parsers.mdlx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MdlxTest {

	public static void main(final String[] args) {
		try (FileInputStream stream = new FileInputStream(
				new File("C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\ArcaneEpic13.mdx"))) {
			final MdlxModel model = new MdlxModel(stream);
			try (FileOutputStream mdlStream = new FileOutputStream(new File(
					"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\Test\\MyOutAutomated.mdl"))) {

				model.saveMdl(mdlStream);
			}
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("Created MDL, now reparsing to MDX");

		try (FileInputStream stream = new FileInputStream(
				new File("C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\Test\\MyOutAutomated.mdl"))) {
			final MdlxModel model = new MdlxModel(null);
			model.loadMdl(stream);
			try (FileOutputStream mdlStream = new FileOutputStream(new File(
					"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\Test\\MyOutAutomatedMDX.mdx"))) {

				model.saveMdx(mdlStream);
			}
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}

		try (FileInputStream stream = new FileInputStream(
				new File("C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\Test\\MyOutAutomatedMDX.mdx"))) {
			final MdlxModel model = new MdlxModel(stream);
			try (FileOutputStream mdlStream = new FileOutputStream(new File(
					"C:\\Users\\micro\\OneDrive\\Documents\\Warcraft III\\Models\\Test\\MyOutAutomatedMDXBack2MDL.mdl"))) {

				model.saveMdl(mdlStream);
			}
		}
		catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
