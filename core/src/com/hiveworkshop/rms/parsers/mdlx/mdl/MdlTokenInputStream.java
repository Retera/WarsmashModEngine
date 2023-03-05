package com.hiveworkshop.rms.parsers.mdlx.mdl;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class MdlTokenInputStream {
	private final ByteBuffer buffer;
	private int index;

	public MdlTokenInputStream(final ByteBuffer buffer) {
		this.buffer = buffer;
		index = 0;
	}

	public String read() {
		boolean inComment = false;
		boolean inString = false;
		final StringBuilder token = new StringBuilder();
		final int length = buffer.remaining();

		while (index < length) {
			// Note: cast from 'byte' to 'char' will cause Java incompatibility with Chinese
			// and Russian/Cyrillic and others
			final char c = (char) buffer.get(buffer.position() + index++);

			if (inComment) {
				if (c == '\n') {
					inComment = false;
				}
			}
			else if (inString) {
				if (c == '"') {
					return token.toString();
				}
				else {
					token.append(c);
				}
			}
			else if ((c == ' ') || (c == ',') || (c == '\t') || (c == '\n') || (c == ':') || (c == '\r')) {
				if (token.length() > 0) {
					return token.toString();
				}
			}
			else if ((c == '{') || (c == '}')) {
				if (token.length() > 0) {
					index--;
					return token.toString();
				}
				else {
					return Character.toString(c);
				}
			}
			else if ((c == '/') && (buffer.get(buffer.position() + index) == '/')) {
				if (token.length() > 0) {
					index--;
					return token.toString();
				}
				else {
					inComment = true;
				}
			}
			else if (c == '"') {
				if (token.length() > 0) {
					index--;
					return token.toString();
				}
				else {
					inString = true;
				}
			}
			else {
				token.append(c);
			}
		}
		return null;
	}

	public String peek() {
		final int index = this.index;
		final String value = read();

		this.index = index;
		return value;
	}

	public long readUInt32() {
		return Long.parseLong(read());
	}

	public int readInt() {
		return Integer.parseInt(read());
	}

	public float readFloat() {
		return Float.parseFloat(read());
	}

	public void readIntArray(final long[] values) {
		read(); // {

		for (int i = 0, l = values.length; i < l; i++) {
			values[i] = readInt();
		}

		read(); // }
	}

	public float[] readFloatArray(final float[] values) {
		read(); // {

		for (int i = 0, l = values.length; i < l; i++) {
			values[i] = readFloat();
		}

		read(); // }
		return values;
	}

	/**
	 * Read an MDL keyframe value. If the value is a scalar, it is just the number.
	 * If the value is a vector, it is enclosed with curly braces.
	 *
	 * @param {Float32Array|Uint32Array} value
	 */
	public void readKeyframe(final float[] values) {
		if (values.length == 1) {
			values[0] = readFloat();
		}
		else {
			readFloatArray(values);
		}
	}

	public float[] readVectorArray(final float[] array, final int vectorLength) {
		read(); // {

		for (int i = 0, l = array.length / vectorLength; i < l; i++) {
			read(); // {

			for (int j = 0; j < vectorLength; j++) {
				array[(i * vectorLength) + j] = readFloat();
			}

			read(); // }
		}

		read(); // }
		return array;
	}

	public Iterable<String> readBlock() {
		read(); // {
		return () -> new Iterator<>() {
			String current;
			private boolean hasLoaded = false;

			@Override
			public String next() {
				if (!hasLoaded) {
					hasNext();
				}
				hasLoaded = false;
				return current;
			}

			@Override
			public boolean hasNext() {
				current = read();
				hasLoaded = true;
				return (current != null) && !current.equals("}");
			}
		};
	}

	public int[] readUInt16Array(final int[] values) {
		read(); // {

		for (int i = 0, l = values.length; i < l; i++) {
			values[i] = readInt();
		}

		read(); // }

		return values;
	}

	public short[] readUInt8Array(final short[] values) {
		read(); // {

		for (int i = 0, l = values.length; i < l; i++) {
			values[i] = Short.parseShort(read());
		}

		read(); // }

		return values;
	}

	public void readColor(final float[] color) {
		read(); // {

		color[2] = readFloat();
		color[1] = readFloat();
		color[0] = readFloat();

		read(); // }
	}
}
