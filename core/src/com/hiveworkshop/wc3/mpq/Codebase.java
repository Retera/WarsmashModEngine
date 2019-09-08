package com.hiveworkshop.wc3.mpq;

import java.io.File;
import java.io.InputStream;

public interface Codebase {
	InputStream getResourceAsStream(String filepath);

	File getFile(String filepath);

	boolean has(String filepath);
}
