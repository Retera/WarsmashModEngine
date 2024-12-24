/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.lwjgl;

import java.awt.Canvas;
import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.etheller.warsmash.audio.OpenALAudio;

/**
 * An OpenGL surface fullscreen or in a lightweight window. This was modified by
 * Retera in accordance with the permission to do so from the Apache 2.0 license
 * listed at the top of the file. The ONLY reason for this modified file
 * override currently is to use a replacement for the OpenALAudio class that
 * will now support the 3D sound!
 */
public class LwjglApplication implements Application {
	protected final LwjglGraphics graphics;
	protected OpenALAudio audio;
	protected final LwjglFiles files;
	protected final LwjglInput input;
	protected final LwjglNet net;
	protected final ApplicationListener listener;
	protected Thread mainLoopThread;
	protected boolean running = true;
	protected final Array<Runnable> runnables = new Array<Runnable>();
	protected final Array<Runnable> executedRunnables = new Array<Runnable>();
	protected final SnapshotArray<LifecycleListener> lifecycleListeners = new SnapshotArray<LifecycleListener>(
			LifecycleListener.class);
	protected int logLevel = LOG_INFO;
	protected ApplicationLogger applicationLogger;
	protected String preferencesdir;
	protected Files.FileType preferencesFileType;

	public LwjglApplication(final ApplicationListener listener, final String title, final int width, final int height) {
		this(listener, createConfig(title, width, height));
	}

	public LwjglApplication(final ApplicationListener listener) {
		this(listener, null, 640, 480);
	}

	public LwjglApplication(final ApplicationListener listener, final LwjglApplicationConfiguration config) {
		this(listener, config, new LwjglGraphics(config));
	}

	public LwjglApplication(final ApplicationListener listener, final Canvas canvas) {
		this(listener, new LwjglApplicationConfiguration(), new LwjglGraphics(canvas));
	}

	public LwjglApplication(final ApplicationListener listener, final LwjglApplicationConfiguration config,
			final Canvas canvas) {
		this(listener, config, new LwjglGraphics(canvas, config));
	}

	public LwjglApplication(final ApplicationListener listener, final LwjglApplicationConfiguration config,
			final LwjglGraphics graphics) {
		LwjglNativesLoader.load();
		setApplicationLogger(new LwjglApplicationLogger());

		if (config.title == null) {
			config.title = listener.getClass().getSimpleName();
		}
		this.graphics = graphics;
		if (!LwjglApplicationConfiguration.disableAudio) {
			try {
				this.audio = new OpenALAudio(config.audioDeviceSimultaneousSources, config.audioDeviceBufferCount,
						config.audioDeviceBufferSize);
			}
			catch (final Throwable t) {
				log("LwjglApplication", "Couldn't initialize audio, disabling audio", t);
				LwjglApplicationConfiguration.disableAudio = true;
			}
		}
		this.files = new LwjglFiles();
		this.input = new DefaultLwjglInput();
		this.net = new LwjglNet(config);
		this.listener = listener;
		this.preferencesdir = config.preferencesDirectory;
		this.preferencesFileType = config.preferencesFileType;

		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.audio = this.audio;
		Gdx.files = this.files;
		Gdx.input = this.input;
		Gdx.net = this.net;
		initialize();
	}

	private static LwjglApplicationConfiguration createConfig(final String title, final int width, final int height) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = title;
		config.width = width;
		config.height = height;
		config.vSyncEnabled = true;
		return config;
	}

	private void initialize() {
		this.mainLoopThread = new Thread("LWJGL Application") {
			@Override
			public void run() {
				LwjglApplication.this.graphics.setVSync(LwjglApplication.this.graphics.config.vSyncEnabled);
				try {
					LwjglApplication.this.mainLoop();
				}
				catch (final Throwable t) {
					if (LwjglApplication.this.audio != null) {
						LwjglApplication.this.audio.dispose();
					}
					Gdx.input.setCursorCatched(false);
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t;
					}
					else {
						throw new GdxRuntimeException(t);
					}
				}
			}
		};
		this.mainLoopThread.start();
	}

	void mainLoop() {
		final SnapshotArray<LifecycleListener> lifecycleListeners = this.lifecycleListeners;

		try {
			this.graphics.setupDisplay();
		}
		catch (final LWJGLException e) {
			throw new GdxRuntimeException(e);
		}

		this.listener.create();
		this.graphics.resize = true;

		int lastWidth = this.graphics.getWidth();
		int lastHeight = this.graphics.getHeight();

		this.graphics.lastTime = System.nanoTime();
		boolean wasActive = true;
		while (this.running) {
			Display.processMessages();
			if (Display.isCloseRequested()) {
				exit();
			}

			final boolean isActive = Display.isActive();
			if (wasActive && !isActive) { // if it's just recently minimized from active state
				wasActive = false;
				synchronized (lifecycleListeners) {
					final LifecycleListener[] listeners = lifecycleListeners.begin();
					for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
						listeners[i].pause();
					}
					lifecycleListeners.end();
				}
				this.listener.pause();
			}
			if (!wasActive && isActive) { // if it's just recently focused from minimized state
				wasActive = true;
				synchronized (lifecycleListeners) {
					final LifecycleListener[] listeners = lifecycleListeners.begin();
					for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
						listeners[i].resume();
					}
					lifecycleListeners.end();
				}
				this.listener.resume();
			}

			boolean shouldRender = false;

			if (this.graphics.canvas != null) {
				final int width = this.graphics.canvas.getWidth();
				final int height = this.graphics.canvas.getHeight();
				if ((lastWidth != width) || (lastHeight != height)) {
					lastWidth = width;
					lastHeight = height;
					Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
					this.listener.resize(lastWidth, lastHeight);
					shouldRender = true;
				}
			}
			else {
				this.graphics.config.x = Display.getX();
				this.graphics.config.y = Display.getY();
				if (this.graphics.resize || Display.wasResized()
						|| ((int) (Display.getWidth() * Display.getPixelScaleFactor()) != this.graphics.config.width)
						|| ((int) (Display.getHeight()
								* Display.getPixelScaleFactor()) != this.graphics.config.height)) {
					this.graphics.resize = false;
					this.graphics.config.width = (int) (Display.getWidth() * Display.getPixelScaleFactor());
					this.graphics.config.height = (int) (Display.getHeight() * Display.getPixelScaleFactor());
					Gdx.gl.glViewport(0, 0, this.graphics.config.width, this.graphics.config.height);
					if (this.listener != null) {
						this.listener.resize(this.graphics.config.width, this.graphics.config.height);
					}
					this.graphics.requestRendering();
				}
			}

			if (executeRunnables()) {
				shouldRender = true;
			}

			// If one of the runnables set running to false, for example after an exit().
			if (!this.running) {
				break;
			}

			this.input.update();
			shouldRender |= this.graphics.shouldRender();
			this.input.processEvents();
			if (this.audio != null) {
				this.audio.update();
			}

			if (!isActive && (this.graphics.config.backgroundFPS == -1)) {
				shouldRender = false;
			}
			int frameRate = isActive ? this.graphics.config.foregroundFPS : this.graphics.config.backgroundFPS;
			if (shouldRender) {
				this.graphics.updateTime();
				this.graphics.frameId++;
				this.listener.render();
				Display.update(false);
			}
			else {
				// Sleeps to avoid wasting CPU in an empty loop.
				if (frameRate == -1) {
					frameRate = 10;
				}
				if (frameRate == 0) {
					frameRate = this.graphics.config.backgroundFPS;
				}
				if (frameRate == 0) {
					frameRate = 30;
				}
			}
			if (frameRate > 0) {
				Display.sync(frameRate);
			}
		}

		synchronized (lifecycleListeners) {
			final LifecycleListener[] listeners = lifecycleListeners.begin();
			for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
				listeners[i].pause();
				listeners[i].dispose();
			}
			lifecycleListeners.end();
		}
		this.listener.pause();
		this.listener.dispose();
		Display.destroy();
		if (this.audio != null) {
			this.audio.dispose();
		}
		if (this.graphics.config.forceExit) {
			System.exit(-1);
		}
	}

	public boolean executeRunnables() {
		synchronized (this.runnables) {
			for (int i = this.runnables.size - 1; i >= 0; i--) {
				this.executedRunnables.add(this.runnables.get(i));
			}
			this.runnables.clear();
		}
		if (this.executedRunnables.size == 0) {
			return false;
		}
		do {
			this.executedRunnables.pop().run();
		}
		while (this.executedRunnables.size > 0);
		return true;
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return this.listener;
	}

	@Override
	public Audio getAudio() {
		return this.audio;
	}

	@Override
	public Files getFiles() {
		return this.files;
	}

	@Override
	public LwjglGraphics getGraphics() {
		return this.graphics;
	}

	@Override
	public Input getInput() {
		return this.input;
	}

	@Override
	public Net getNet() {
		return this.net;
	}

	@Override
	public ApplicationType getType() {
		return ApplicationType.Desktop;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	public void stop() {
		this.running = false;
		try {
			this.mainLoopThread.join();
		}
		catch (final Exception ex) {
		}
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	ObjectMap<String, Preferences> preferences = new ObjectMap<String, Preferences>();

	@Override
	public Preferences getPreferences(final String name) {
		if (this.preferences.containsKey(name)) {
			return this.preferences.get(name);
		}
		else {
			final Preferences prefs = new LwjglPreferences(
					new LwjglFileHandle(new File(this.preferencesdir, name), this.preferencesFileType));
			this.preferences.put(name, prefs);
			return prefs;
		}
	}

	@Override
	public Clipboard getClipboard() {
		return new LwjglClipboard();
	}

	@Override
	public void postRunnable(final Runnable runnable) {
		synchronized (this.runnables) {
			this.runnables.add(runnable);
			Gdx.graphics.requestRendering();
		}
	}

	@Override
	public void debug(final String tag, final String message) {
		if (this.logLevel >= LOG_DEBUG) {
			getApplicationLogger().debug(tag, message);
		}
	}

	@Override
	public void debug(final String tag, final String message, final Throwable exception) {
		if (this.logLevel >= LOG_DEBUG) {
			getApplicationLogger().debug(tag, message, exception);
		}
	}

	@Override
	public void log(final String tag, final String message) {
		if (this.logLevel >= LOG_INFO) {
			getApplicationLogger().log(tag, message);
		}
	}

	@Override
	public void log(final String tag, final String message, final Throwable exception) {
		if (this.logLevel >= LOG_INFO) {
			getApplicationLogger().log(tag, message, exception);
		}
	}

	@Override
	public void error(final String tag, final String message) {
		if (this.logLevel >= LOG_ERROR) {
			getApplicationLogger().error(tag, message);
		}
	}

	@Override
	public void error(final String tag, final String message, final Throwable exception) {
		if (this.logLevel >= LOG_ERROR) {
			getApplicationLogger().error(tag, message, exception);
		}
	}

	@Override
	public void setLogLevel(final int logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public int getLogLevel() {
		return this.logLevel;
	}

	@Override
	public void setApplicationLogger(final ApplicationLogger applicationLogger) {
		this.applicationLogger = applicationLogger;
	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		return this.applicationLogger;
	}

	@Override
	public void exit() {
		postRunnable(new Runnable() {
			@Override
			public void run() {
				LwjglApplication.this.running = false;
			}
		});
	}

	@Override
	public void addLifecycleListener(final LifecycleListener listener) {
		synchronized (this.lifecycleListeners) {
			this.lifecycleListeners.add(listener);
		}
	}

	@Override
	public void removeLifecycleListener(final LifecycleListener listener) {
		synchronized (this.lifecycleListeners) {
			this.lifecycleListeners.removeValue(listener, true);
		}
	}
}
