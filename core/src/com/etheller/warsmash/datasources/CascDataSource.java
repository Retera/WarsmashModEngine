package com.etheller.warsmash.datasources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hiveworkshop.blizzard.casc.io.WarcraftIIICASC;
import com.hiveworkshop.blizzard.casc.io.WarcraftIIICASC.FileSystem;
import com.hiveworkshop.json.JSONArray;
import com.hiveworkshop.json.JSONObject;
import com.hiveworkshop.json.JSONTokener;

public class CascDataSource implements DataSource {
	private final String[] prefixes;
	private WarcraftIIICASC warcraftIIICASC;
	private FileSystem rootFileSystem;
	private List<String> listFile;
	private Map<String, String> fileAliases;

	public CascDataSource(final String warcraft3InstallPath, final String[] prefixes) {
		this.prefixes = prefixes;
		for (int i = 0; i < (prefixes.length / 2); i++) {
			final String temp = prefixes[i];
			prefixes[i] = prefixes[prefixes.length - i - 1];
			prefixes[prefixes.length - i - 1] = temp;
		}

		try {
			this.warcraftIIICASC = new WarcraftIIICASC(Paths.get(warcraft3InstallPath), true);
			this.rootFileSystem = this.warcraftIIICASC.getRootFileSystem();
			this.listFile = this.rootFileSystem.enumerateFiles();
			this.fileAliases = new HashMap<>();
			if (this.has("filealiases.json")) {
				try (InputStream stream = this.getResourceAsStream("filealiases.json")) {
					stream.mark(4);
					if ('\ufeff' != stream.read()) {
						stream.reset(); // not the BOM marker
					}
					final JSONArray jsonObject = new JSONArray(new JSONTokener(stream));
					for (int i = 0; i < jsonObject.length(); i++) {
						final JSONObject alias = jsonObject.getJSONObject(i);
						final String src = alias.getString("src");
						final String dest = alias.getString("dest");
						this.fileAliases.put(src.toLowerCase(Locale.US).replace('/', '\\'),
								dest.toLowerCase(Locale.US).replace('/', '\\'));
						if ((src.toLowerCase(Locale.US).contains(".blp")
								|| dest.toLowerCase(Locale.US).contains(".blp"))
								&& (!alias.has("assetType") || "Texture".equals(alias.getString("assetType")))) {
							// This case: I saw a texture that resolves in game but was failing in our code
							// here, because of this entry:
							// {"src":"Units/Human/WarWagon/SiegeEngine.blp",
							// "dest":"Textures/Steamtank.blp", "assetType": "Texture"},
							// Our repo here checks BLP then DDS at a high-up application level thing, and
							// the problem is that this entry is written using .BLP but we must be able to
							// resolve .DDS when we go to look it up. The actual model is .BLP so maybe
							// that's how the game does it, but my alias mapping is happening after the
							// .BLP->.DDS dynamic fix, and not before.
							this.fileAliases.put(src.toLowerCase(Locale.US).replace('/', '\\').replace(".blp", ".dds"),
									dest.toLowerCase(Locale.US).replace('/', '\\').replace(".blp", ".dds"));
						}
					}
				}
			}
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getResourceAsStream(String filepath) {
		filepath = filepath.toLowerCase(Locale.US).replace('/', '\\').replace(':', '\\');
		final String resolvedAlias = this.fileAliases.get(filepath);
		if (resolvedAlias != null) {
			filepath = resolvedAlias;
		}
		for (final String prefix : this.prefixes) {
			final String tempFilepath = prefix + "\\" + filepath;
			final InputStream stream = internalGetResourceAsStream(tempFilepath);
			if (stream != null) {
				return stream;
			}
		}
		return internalGetResourceAsStream(filepath);
	}

	private InputStream internalGetResourceAsStream(final String tempFilepath) {
		try {
			if (this.rootFileSystem.isFile(tempFilepath) && this.rootFileSystem.isFileAvailable(tempFilepath)) {
				final ByteBuffer buffer = this.rootFileSystem.readFileData(tempFilepath);
				if (buffer.hasArray()) {
					return new ByteArrayInputStream(buffer.array());
				}
				final byte[] data = new byte[buffer.remaining()];
				buffer.clear();
				buffer.get(data);
				return new ByteArrayInputStream(data);
			}
		}
		catch (final IOException e) {
			throw new RuntimeException("CASC parser error for: " + tempFilepath, e);
		}
		return null;
	}

	@Override
	public ByteBuffer read(String path) {
		path = path.toLowerCase(Locale.US).replace('/', '\\').replace(':', '\\');
		final String resolvedAlias = this.fileAliases.get(path);
		if (resolvedAlias != null) {
			path = resolvedAlias;
		}
		for (final String prefix : this.prefixes) {
			final String tempFilepath = prefix + "\\" + path;
			final ByteBuffer stream = internalRead(tempFilepath);
			if (stream != null) {
				return stream;
			}
		}
		return internalRead(path);
	}

	private ByteBuffer internalRead(final String tempFilepath) {
		try {
			if (this.rootFileSystem.isFile(tempFilepath) && this.rootFileSystem.isFileAvailable(tempFilepath)) {
				final ByteBuffer buffer = this.rootFileSystem.readFileData(tempFilepath);
				return buffer;
			}
		}
		catch (final IOException e) {
			throw new RuntimeException("CASC parser error for: " + tempFilepath, e);
		}
		return null;
	}

	@Override
	public File getFile(String filepath) {
		filepath = filepath.toLowerCase(Locale.US).replace('/', '\\').replace(':', '\\');
		final String resolvedAlias = this.fileAliases.get(filepath);
		if (resolvedAlias != null) {
			filepath = resolvedAlias;
		}
		for (final String prefix : this.prefixes) {
			final String tempFilepath = prefix + "\\" + filepath;
			final File file = internalGetFile(tempFilepath);
			if (file != null) {
				return file;
			}
		}
		return internalGetFile(filepath);
	}

	@Override
	public File getDirectory(String filepath) throws IOException {
		return null;
	}

	private File internalGetFile(final String tempFilepath) {
		try {
			if (this.rootFileSystem.isFile(tempFilepath) && this.rootFileSystem.isFileAvailable(tempFilepath)) {
				final ByteBuffer buffer = this.rootFileSystem.readFileData(tempFilepath);
				String tmpdir = System.getProperty("java.io.tmpdir");
				if (!tmpdir.endsWith(File.separator)) {
					tmpdir += File.separator;
				}
				final String tempDir = tmpdir + "MatrixEaterExtract/";
				final File tempProduct = new File(tempDir + tempFilepath.replace('\\', File.separatorChar));
				tempProduct.delete();
				tempProduct.getParentFile().mkdirs();
				try (final FileChannel fileChannel = FileChannel.open(tempProduct.toPath(), StandardOpenOption.CREATE,
						StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING)) {
					fileChannel.write(buffer);
				}
				tempProduct.deleteOnExit();
				return tempProduct;
			}
		}
		catch (final IOException e) {
			throw new RuntimeException("CASC parser error for: " + tempFilepath, e);
		}
		return null;
	}

	@Override
	public boolean has(String filepath) {
		filepath = filepath.toLowerCase(Locale.US).replace('/', '\\').replace(':', '\\');
		final String resolvedAlias = this.fileAliases.get(filepath);
		if (resolvedAlias != null) {
			filepath = resolvedAlias;
		}
		for (final String prefix : this.prefixes) {
			final String tempFilepath = prefix + "\\" + filepath;
			try {
				if (this.rootFileSystem.isFile(tempFilepath)) {
					return true;
				}
			}
			catch (final IOException e) {
				throw new RuntimeException("CASC parser error for: " + tempFilepath, e);
			}
		}
		try {
			return this.rootFileSystem.isFile(filepath);
		}
		catch (final IOException e) {
			throw new RuntimeException("CASC parser error for: " + filepath, e);
		}
	}

	@Override
	public Collection<String> getListfile() {
		return this.listFile;
	}

	@Override
	public void close() throws IOException {
		this.warcraftIIICASC.close();
	}

}
