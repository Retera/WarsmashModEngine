package com.etheller.warsmash.datasources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.utils.ObjectIntMap;

public class CompoundDataSource implements DataSource {
	private final List<DataSource> mpqList = new ArrayList<>();
	private final ObjectIntMap<String> indexCache = new ObjectIntMap<>();

	public CompoundDataSource(final List<DataSource> dataSources) {
		if (dataSources != null) {
			for (final DataSource dataSource : dataSources) {
				this.mpqList.add(dataSource);
			}
		}
	}

	Map<String, File> cache = new HashMap<>();

	@Override
	public File getFile(final String filepath) {
		if (this.cache.containsKey(filepath)) {
			return this.cache.get(filepath);
		}
		try {
			final int index = this.indexCache.get(filepath, -1);
			if (index == -2) {
				return null;
			}
			if (index != -1) {
				final DataSource mpq = this.mpqList.get(index);
				final File tempProduct = mpq.getFile(filepath);
				if (tempProduct != null) {
					this.cache.put(filepath, tempProduct);
					return tempProduct;
				}
			}
			for (int i = this.mpqList.size() - 1; i >= 0; i--) {
				final DataSource mpq = this.mpqList.get(i);
				final File tempProduct = mpq.getFile(filepath);
				if (tempProduct != null) {
					this.cache.put(filepath, tempProduct);
					this.indexCache.put(filepath, i);
					return tempProduct;
				}
			}
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public File getDirectory(final String filepath) throws IOException {
		try {
			final int index = this.indexCache.get(filepath, -1);
			if (index == -2) {
				return null;
			}
			if (index != -1) {
				return this.mpqList.get(index).getDirectory(filepath);
			}
			for (int i = this.mpqList.size() - 1; i >= 0; i--) {
				final DataSource mpq = this.mpqList.get(i);
				final File tempProduct = mpq.getDirectory(filepath);
				if (tempProduct != null) {
					this.indexCache.put(filepath, i);
					return tempProduct;
				}
			}
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ByteBuffer read(final String path) throws IOException {
		try {
			final int index = this.indexCache.get(path, -1);
			if (index == -2) {
				return null;
			}
			if (index != -1) {
				return this.mpqList.get(index).read(path);
			}
			for (int i = this.mpqList.size() - 1; i >= 0; i--) {
				final DataSource mpq = this.mpqList.get(i);
				final ByteBuffer buffer = mpq.read(path);
				if (buffer != null) {
					this.indexCache.put(path, i);
					return buffer;
				}
			}
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InputStream getResourceAsStream(final String filepath) {
		try {
			final int index = this.indexCache.get(filepath, -1);
			if (index == -2) {
				return null;
			}
			if (index != -1) {
				return this.mpqList.get(index).getResourceAsStream(filepath);
			}
			for (int i = this.mpqList.size() - 1; i >= 0; i--) {
				final DataSource mpq = this.mpqList.get(i);
				final InputStream resourceAsStream = mpq.getResourceAsStream(filepath);
				if (resourceAsStream != null) {
					this.indexCache.put(filepath, i);
					return resourceAsStream;
				}
			}
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean has(final String filepath) {
		if (this.cache.containsKey(filepath)) {
			return true;
		}
		final int index = this.indexCache.get(filepath, -1);
		if (index == -2) {
			return false;
		}
		if (index != -1) {
			return this.mpqList.get(index).has(filepath);
		}
		for (int i = this.mpqList.size() - 1; i >= 0; i--) {
			final DataSource mpq = this.mpqList.get(i);
			if (mpq.has(filepath)) {
				this.indexCache.put(filepath, i);
				return true;
			}
		}
		this.indexCache.put(filepath, -2);
		return false;
	}

	public void refresh(final List<DataSourceDescriptor> dataSourceDescriptors) {
		for (final DataSource dataSource : this.mpqList) {
			try {
				dataSource.close();
			}
			catch (final NullPointerException e) {
				e.printStackTrace();
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
		}
		this.cache.clear();
		this.indexCache.clear();
		this.mpqList.clear();
		if (dataSourceDescriptors != null) {
			for (final DataSourceDescriptor descriptor : dataSourceDescriptors) {
				this.mpqList.add(descriptor.createDataSource());
			}
		}
	}

	public interface LoadedMPQ {
		void unload();

		boolean hasListfile();

		boolean has(String path);
	}

	public Set<String> getMergedListfile() {
		final Set<String> listfile = new HashSet<>();
		for (final DataSource mpqGuy : this.mpqList) {
			final Collection<String> dataSourceListfile = mpqGuy.getListfile();
			if (dataSourceListfile != null) {
				for (final String element : dataSourceListfile) {
					listfile.add(element);
				}
			}
		}
		return listfile;
	}

	@Override
	public Collection<String> getListfile() {
		return getMergedListfile();
	}

	@Override
	public void close() throws IOException {
		for (final DataSource mpqGuy : this.mpqList) {
			mpqGuy.close();
		}
	}
}
