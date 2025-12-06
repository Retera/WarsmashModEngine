package com.etheller.warsmash.viewer5.gl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.badlogic.gdx.Gdx;

public class DataTexturePool {
	private final Map<Key, List<DataTexture>> keyToDataTextures = new HashMap<>();
	// TODO maybe not static
	public static DataTexturePool INSTANCE = new DataTexturePool();

	public DataTexturePool() {
	}

	public DataTexture get(final int channels, final int width, final int height) {
		final Key key = new Key(channels, width, height);
		final List<DataTexture> dataTextures = getDataTextures(key);
		if (dataTextures.isEmpty()) {
			return new DataTexture(Gdx.gl, channels, width, height);
		}
		else {
			return dataTextures.remove(dataTextures.size() - 1);
		}
	}

	public void release(final DataTexture dataTexture) {
		final Key key = new Key(dataTexture.getChannels(), dataTexture.getWidth(), dataTexture.getHeight());
		final List<DataTexture> dataTextures = getDataTextures(key);
		dataTextures.add(dataTexture);
	}

	private List<DataTexture> getDataTextures(final Key key) {
		List<DataTexture> dataTextures = this.keyToDataTextures.get(key);
		if (dataTextures == null) {
			dataTextures = new ArrayList<>();
			this.keyToDataTextures.put(key, dataTextures);
		}
		return dataTextures;
	}

	private static final class Key {
		private final int channels;
		private final int width;
		private final int height;

		public Key(final int channels, final int width, final int height) {
			this.channels = channels;
			this.width = width;
			this.height = height;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.channels, this.height, this.width);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Key other = (Key) obj;
			return (this.channels == other.channels) && (this.height == other.height) && (this.width == other.width);
		}
	}
}
