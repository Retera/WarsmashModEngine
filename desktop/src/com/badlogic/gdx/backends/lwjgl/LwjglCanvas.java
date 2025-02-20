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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.etheller.warsmash.audio.OpenALAudio;

/**
 * An OpenGL surface on an AWT Canvas, allowing OpenGL to be embedded in a Swing
 * application. This uses {@link Display#setParent(Canvas)}, which is preferred
 * over {@link AWTGLCanvas} but is limited to a single LwjglCanvas in an
 * application. All OpenGL calls are done on the EDT. Note that you may need to
 * call {@link #stop()} or a Swing application may deadlock on System.exit due
 * to how LWJGL and/or Swing deal with shutdown hooks.
 *
 * @author Nathan Sweet
 */
public class LwjglCanvas implements Application {
	static boolean isWindows = System.getProperty("os.name").contains("Windows");

	LwjglGraphics graphics;
	OpenALAudio audio;
	LwjglFiles files;
	LwjglInput input;
	LwjglNet net;
	ApplicationListener listener;
	Canvas canvas;
	final Array<Runnable> runnables = new Array();
	final Array<Runnable> executedRunnables = new Array();
	final Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>();
	boolean running = true;
	int logLevel = LOG_INFO;
	ApplicationLogger applicationLogger;
	Cursor cursor;

	public LwjglCanvas(final ApplicationListener listener) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		initialize(listener, config);
	}

	public LwjglCanvas(final ApplicationListener listener, final LwjglApplicationConfiguration config) {
		initialize(listener, config);
	}

	private void initialize(final ApplicationListener listener, final LwjglApplicationConfiguration config) {
		LwjglNativesLoader.load();
		setApplicationLogger(new LwjglApplicationLogger());
		this.canvas = new Canvas() {
			private final Dimension minSize = new Dimension(1, 1);

			@Override
			public final void addNotify() {
				super.addNotify();
				if (SharedLibraryLoader.isMac) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							create();
						}
					});
				}
				else {
					create();
				}
			}

			@Override
			public final void removeNotify() {
				stop();
				super.removeNotify();
			}

			@Override
			public Dimension getMinimumSize() {
				return this.minSize;
			}
		};
		this.canvas.setSize(1, 1);
		this.canvas.setIgnoreRepaint(true);

		this.graphics = new LwjglGraphics(this.canvas, config) {
			@Override
			public void setTitle(final String title) {
				super.setTitle(title);
				LwjglCanvas.this.setTitle(title);
			}

			public boolean setWindowedMode(final int width, final int height, final boolean fullscreen) {
				if (!super.setWindowedMode(width, height)) {
					return false;
				}
				if (!fullscreen) {
					LwjglCanvas.this.setDisplayMode(width, height);
				}
				return true;
			}

			@Override
			public boolean setFullscreenMode(final DisplayMode displayMode) {
				if (!super.setFullscreenMode(displayMode)) {
					return false;
				}
				LwjglCanvas.this.setDisplayMode(displayMode.width, displayMode.height);
				return true;
			}
		};
		this.graphics.setVSync(config.vSyncEnabled);
		if (!LwjglApplicationConfiguration.disableAudio) {
			this.audio = new OpenALAudio();
		}
		this.files = new LwjglFiles();
		this.input = new DefaultLwjglInput();
		this.net = new LwjglNet(config);
		this.listener = listener;

		Gdx.app = this;
		Gdx.graphics = this.graphics;
		Gdx.audio = this.audio;
		Gdx.files = this.files;
		Gdx.input = this.input;
		Gdx.net = this.net;
	}

	protected void setDisplayMode(final int width, final int height) {
	}

	protected void setTitle(final String title) {
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return this.listener;
	}

	public Canvas getCanvas() {
		return this.canvas;
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
	public Graphics getGraphics() {
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

	void create() {
		try {
			this.graphics.setupDisplay();

			this.listener.create();
			this.listener.resize(Math.max(1, this.graphics.getWidth()), Math.max(1, this.graphics.getHeight()));

			start();
		}
		catch (final Exception ex) {
			stopped();
			exception(ex);
			return;
		}

		EventQueue.invokeLater(new Runnable() {
			int lastWidth = Math.max(1, LwjglCanvas.this.graphics.getWidth());
			int lastHeight = Math.max(1, LwjglCanvas.this.graphics.getHeight());

			@Override
			public void run() {
				if (!LwjglCanvas.this.running || Display.isCloseRequested()) {
					LwjglCanvas.this.running = false;
					stopped();
					return;
				}
				try {
					Display.processMessages();
					if ((LwjglCanvas.this.cursor != null) || !isWindows) {
						LwjglCanvas.this.canvas.setCursor(LwjglCanvas.this.cursor);
					}

					boolean shouldRender = false;

					final int width = Math.max(1, LwjglCanvas.this.graphics.getWidth());
					final int height = Math.max(1, LwjglCanvas.this.graphics.getHeight());
					if ((this.lastWidth != width) || (this.lastHeight != height)) {
						this.lastWidth = width;
						this.lastHeight = height;
						Gdx.gl.glViewport(0, 0, this.lastWidth, this.lastHeight);
						resize(width, height);
						LwjglCanvas.this.listener.resize(width, height);
						shouldRender = true;
					}

					if (executeRunnables()) {
						shouldRender = true;
					}

					// If one of the runnables set running to false, for example after an exit().
					if (!LwjglCanvas.this.running) {
						return;
					}

					LwjglCanvas.this.input.update();
					shouldRender |= LwjglCanvas.this.graphics.shouldRender();
					LwjglCanvas.this.input.processEvents();
					if (LwjglCanvas.this.audio != null) {
						LwjglCanvas.this.audio.update();
					}

					if (shouldRender) {
						LwjglCanvas.this.graphics.updateTime();
						LwjglCanvas.this.graphics.frameId++;
						LwjglCanvas.this.listener.render();
						Display.update(false);
					}

					Display.sync(getFrameRate());
				}
				catch (final Throwable ex) {
					exception(ex);
				}
				EventQueue.invokeLater(this);
			}
		});
	}

	public boolean executeRunnables() {
		synchronized (this.runnables) {
			for (int i = this.runnables.size - 1; i >= 0; i--) {
				this.executedRunnables.addAll(this.runnables.get(i));
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

	protected int getFrameRate() {
		int frameRate = Display.isActive() ? this.graphics.config.foregroundFPS : this.graphics.config.backgroundFPS;
		if (frameRate == -1) {
			frameRate = 10;
		}
		if (frameRate == 0) {
			frameRate = this.graphics.config.backgroundFPS;
		}
		if (frameRate == 0) {
			frameRate = 30;
		}
		return frameRate;
	}

	protected void exception(final Throwable ex) {
		ex.printStackTrace();
		stop();
	}

	/**
	 * Called after {@link ApplicationListener} create and resize, but before the
	 * game loop iteration.
	 */
	protected void start() {
	}

	/** Called when the canvas size changes. */
	protected void resize(final int width, final int height) {
	}

	/** Called when the game loop has stopped. */
	protected void stopped() {
	}

	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!LwjglCanvas.this.running) {
					return;
				}
				LwjglCanvas.this.running = false;
				final Array<LifecycleListener> listeners = LwjglCanvas.this.lifecycleListeners;
				synchronized (listeners) {
					for (final LifecycleListener listener : listeners) {
						listener.pause();
						listener.dispose();
					}
				}
				LwjglCanvas.this.listener.pause();
				LwjglCanvas.this.listener.dispose();
				try {
					Display.destroy();
					if (LwjglCanvas.this.audio != null) {
						LwjglCanvas.this.audio.dispose();
					}
				}
				catch (final Throwable ignored) {
				}
			}
		});
	}

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	Map<String, Preferences> preferences = new HashMap<String, Preferences>();

	@Override
	public Preferences getPreferences(final String name) {
		if (this.preferences.containsKey(name)) {
			return this.preferences.get(name);
		}
		else {
			final Preferences prefs = new LwjglPreferences(name, ".prefs/");
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
				LwjglCanvas.this.listener.pause();
				LwjglCanvas.this.listener.dispose();
				if (LwjglCanvas.this.audio != null) {
					LwjglCanvas.this.audio.dispose();
				}
				System.exit(-1);
			}
		});
	}

	/** @param cursor May be null. */
	public void setCursor(final Cursor cursor) {
		this.cursor = cursor;
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
