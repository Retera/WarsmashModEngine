package com.etheller.interpreter.ast.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class JassLog {
	public static PrintStream jassLogWriter;

	public static PrintStream getWriter() {
		if (jassLogWriter == null) {
			new File("Logs").mkdir();
			try {
				jassLogWriter = new PrintStream(
						new FileOutputStream(new File("Logs/" + System.currentTimeMillis() + ".jass.log")));
			}
			catch (final FileNotFoundException e) {
				e.printStackTrace();
				jassLogWriter = System.err;
			}
		}
		return jassLogWriter;
	}

	public static void report(final Throwable t) {
		final PrintStream writer = getWriter();
		t.printStackTrace(writer);
		writer.flush();
	}
}
