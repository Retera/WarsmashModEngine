package com.etheller.warsmash.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.*;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.*;
//import com.badlogic.gdx.backends.lwjgl3.gl.*;
import com.etheller.warsmash.WarsmashGdxFDFTestRenderScreen;
import com.etheller.warsmash.WarsmashGdxMenuScreen;
import com.etheller.warsmash.WarsmashGdxMultiScreenGame;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.AudioContext;
import com.etheller.warsmash.viewer5.AudioContext.Listener;
import com.etheller.warsmash.viewer5.AudioDestination;
import com.etheller.warsmash.viewer5.gl.ANGLEInstancedArrays;
import com.etheller.warsmash.viewer5.gl.AudioExtension;
import com.etheller.warsmash.viewer5.gl.DynamicShadowExtension;
import com.etheller.warsmash.viewer5.gl.Extensions;
import com.etheller.warsmash.viewer5.gl.WireframeExtension;


import java.util.function.Consumer;

public class DesktopLauncher {

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Warsmash3Phone");
		configuration.useVsync(true);
		configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 3);
		//// Limits FPS to the refresh rate of the currently active monitor.
		configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
		//// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
		//// useful for testing performance, but can also be very stressful to some hardware.
		//// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
//		configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		configuration.setWindowedMode(640, 480);
		configuration.setWindowIcon("resources/Icon128.png", "resources/Icon64.png", "resources/Icon32.png", "resources/Icon16.png");
		return configuration;
	}

	public static void main(final String[] arg) {
		System.out.println("Warsmash engine is starting...");
		final Lwjgl3ApplicationConfiguration config = getDefaultConfiguration();
		String fileToLoad = null;
		String iniPath = null;
		boolean noLogs = false;
		for (int argIndex = 0; argIndex < arg.length; argIndex++) {
			if ("-window".equals(arg[argIndex])) {
				//config.fullscreen = false;
				config.setWindowedMode(1920, 1080);
			}
			else if ("-nolog".equals(arg[argIndex])) {
				noLogs = true;
			}
			else if ((arg.length > (argIndex + 1)) && "-loadfile".equals(arg[argIndex])) {
				argIndex++;
				fileToLoad = arg[argIndex];
			}
			else if ((arg.length > (argIndex + 1)) && "-ini".equals(arg[argIndex])) {
				argIndex++;
				iniPath = arg[argIndex];
			}
		}
		if (!noLogs) {
			new File("Logs").mkdir();
			try {
				System.setOut(new PrintStream(
						new FileOutputStream(new File("Logs/" + System.currentTimeMillis() + ".out.log"))));
			}
			catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				System.setErr(new PrintStream(
						new FileOutputStream(new File("Logs/" + System.currentTimeMillis() + ".err.log"))));
			}
			catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		loadExtensions();
		final DataTable warsmashIni = loadWarsmashIni(iniPath);
		final Element emulatorConstants = warsmashIni.get("Emulator");
		WarsmashConstants.loadConstants(emulatorConstants, warsmashIni);

		if (fileToLoad != null) {
			System.out.println("About to run loading file: " + fileToLoad);
		}
		final String finalFileToLoad = fileToLoad;
		final WarsmashGdxMultiScreenGame warsmashGdxMultiScreenGame = new WarsmashGdxMultiScreenGame((Consumer<WarsmashGdxMultiScreenGame>)(game) -> {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if ((finalFileToLoad != null) && finalFileToLoad.toLowerCase().endsWith(".toc")) {
					game.setScreen(new WarsmashGdxFDFTestRenderScreen(warsmashIni,
							game, finalFileToLoad));
				}
				else {
					final WarsmashGdxMenuScreen menuScreen = new WarsmashGdxMenuScreen(warsmashIni,
							game);
					game.setScreen(menuScreen);
					if (finalFileToLoad != null) {
						menuScreen.startMap(finalFileToLoad);
					}
				}
			}
		});});
		new Lwjgl3Application(warsmashGdxMultiScreenGame, config);
	}

	public static DataTable loadWarsmashIni(final String iniPath) {
		final DataTable warsmashIni = new DataTable(StringBundle.EMPTY);
		try (FileInputStream warsmashIniInputStream = new FileInputStream(iniPath != null ? iniPath : "warsmash.ini")) {
			warsmashIni.readTXT(warsmashIniInputStream, true);
		}
		catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return warsmashIni;
	}

	public static DataTable loadWarsmashIni() {
		return loadWarsmashIni(null);
	}

	public static void loadExtensions() {
		//LwjglNativesLoader.load();
		Extensions.angleInstancedArrays = new ANGLEInstancedArrays() {
			@Override
			public void glVertexAttribDivisorANGLE(final int index, final int divisor) {
				GL33.glVertexAttribDivisor(index, divisor);
			}

			@Override
			public void glDrawElementsInstancedANGLE(final int mode, final int count, final int type,
					final int indicesOffset, final int instanceCount) {
				GL31.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
			}

			@Override
			public void glDrawArraysInstancedANGLE(final int mode, final int first, final int count,
					final int instanceCount) {
				GL31.glDrawArraysInstanced(mode, first, count, instanceCount);
			}
		};
		Extensions.dynamicShadowExtension = new DynamicShadowExtension() {
			@Override
			public void glFramebufferTexture(final int target, final int attachment, final int texture,
					final int level) {
				GL32.glFramebufferTexture(target, attachment, texture, level);
			}

			@Override
			public void glDrawBuffer(final int mode) {
				GL11.glDrawBuffer(mode);
			}
		};
		Extensions.wireframeExtension = new WireframeExtension() {
			@Override
			public void glPolygonMode(final int face, final int mode) {
				GL11.glPolygonMode(face, mode);
			}
		};
		Extensions.audio = new AudioExtension() {

			@Override
			public float getDuration(final Sound sound) {
				if (sound == null) {
					return 1;
				}
				return 2.0f;
			}

			@Override
			public long play(final Sound buffer, final float volume, final float pitch, final float x, final float y,
							 final float z, final boolean is3dSound, final float maxDistance, final float refDistance,
							 final boolean looping) {
				return buffer.play(volume, pitch, 0.0f);
			}

			@Override
			public AudioContext createContext(final boolean world) {
				AudioContext.Listener listener;
					listener = AudioContext.Listener.DO_NOTHING;

				return new AudioContext(listener, new AudioDestination() {
				});
			}
		};
		Extensions.GL_LINE = GL11.GL_LINE;
		Extensions.GL_FILL = GL11.GL_FILL;
	}
}
