package com.etheller.warsmash.datasources;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface SourcedData {

	public ByteBuffer read();

	public InputStream getResourceAsStream();
}
