package com.etheller.warsmash.viewer5;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.viewer5.gl.WebGL;

public class ModelViewer {
	private final DataSource dataSource;
	public final CanvasProvider canvas;
	public List<Resource> resources;
	public Map<String, Resource> fetchCache;
	public int frameTime;
	public GL20 gl;
	public WebGL webGL;
	public List<Scene> scenes;
	private int visibleCells;
	private int visibleInstances;
	private int updatedParticles;
	public int frame;
	private final int rectBuffer;
	private final boolean enableAudio;
	private final Map<Model, List<TextureMapper>> textureMappers;

	public ModelViewer(final DataSource dataSource, final CanvasProvider canvas) {
		this.dataSource = dataSource;
		this.canvas = canvas;
		this.resources = new ArrayList<>();
		this.fetchCache = new HashMap<>();
		this.frameTime = 1000 / 60;
		this.gl = Gdx.gl;
		this.webGL = new WebGL(this.gl);
		this.scenes = new ArrayList<>();
		this.visibleCells = 0;
		this.visibleInstances = 0;
		this.updatedParticles = 0;

		this.frame = 0;

		this.rectBuffer = this.gl.glGenBuffer();
		this.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.rectBuffer);
		final ByteBuffer temp = ByteBuffer.allocate(6);
		temp.put((byte) 0);
		temp.put((byte) 1);
		temp.put((byte) 2);
		temp.put((byte) 0);
		temp.put((byte) 2);
		temp.put((byte) 3);
		temp.clear();
		this.gl.glBufferData(GL20.GL_ARRAY_BUFFER, temp.capacity(), temp, GL20.GL_STATIC_DRAW);
		this.enableAudio = false;
		this.textureMappers = new HashMap<Model, List<TextureMapper>>();
	}

	public Scene addScene() {
		final Scene scene = new Scene(this);

		this.scenes.add(scene);

		return scene;
	}

	public boolean removeScene(final Scene scene) {
		return this.scenes.remove(scene);
	}

	public void clear() {
		this.scenes.clear();
	}

	public boolean has(final String key) {
		return this.fetchCache.containsKey(key);
	}

	public Resource get(final String key) {
		return this.fetchCache.get(key);
	}

	public void updateAndRender() {
		update();
	}

//	public Resource loadGeneric(String path, String dataType, )
	public void update() {
		final float dt = this.frameTime * 0.001f;

		this.frame += 1;

		this.visibleCells = 0;
		this.visibleInstances = 0;
		this.updatedParticles = 0;

		for (final Scene scene : this.scenes) {
			scene.update(dt);

			this.visibleCells += scene.visibleCells;
			this.visibleInstances += scene.visibleInstances;
			this.updatedParticles += scene.updatedParticles;
		}
	}

	public void startFrame() {
		this.gl.glDepthMask(true);
		this.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public void render() {
		this.renderOpaque();
		this.renderTranslucent();
	}

	private void renderOpaque() {
		for (final Scene scene : this.scenes) {
			scene.renderOpaque();
		}
	}

	private void renderTranslucent() {
		for (final Scene scene : this.scenes) {
			scene.renderTranslucent();
		}
	}

	public TextureMapper baseTextureMapper(final ModelInstance instance) {
		final Model model = instance.model;
		List<TextureMapper> mappers = this.textureMappers.get(model);
		if (mappers == null) {
			mappers = new ArrayList<>();
			this.textureMappers.put(model, mappers);
		}
		if (mappers.isEmpty()) {
			mappers.add(new TextureMapper(model));
		}
		return mappers.get(0);
	}

	public TextureMapper changeTextureMapper(final ModelInstance instance, final Object key, final Texture texture) {
		final Map<Object, Texture> map = new HashMap<>(instance.textureMapper.textures);

		if (texture instanceof Texture) { // not null?
			map.put(key, texture);
		}
		else {
			map.remove(key);
		}

		final Model model = instance.model;
		final List<TextureMapper> mappers = this.textureMappers.get(model);

		for (final TextureMapper mapper : mappers) {
			if (mapper.textures.equals(map)) {
				return mapper;
			}
		}

		final TextureMapper mapper = new TextureMapper(model, map);

		mappers.add(mapper);

		return mapper;
	}
}
