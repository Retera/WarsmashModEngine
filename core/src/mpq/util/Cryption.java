package mpq.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Cryption {
	private static final int[] CRYPT_TABLE = new int[0x500];
	static {
		int seed = 0x00100001;

		for (int index1 = 0; index1 < 0x100; index1++) {
			for (int index2 = index1, i = 0; i < 5; i++, index2 += 0x100) {
				seed = ((seed * 125) + 3) % 0x2AAAAB;
				final int temp1 = (seed & 0xFFFF) << 0x10;

				seed = ((seed * 125) + 3) % 0x2AAAAB;
				final int temp2 = (seed & 0xFFFF);

				CRYPT_TABLE[index2] = (temp1 | temp2);
			}
		}
	}

	// different types of hashes to make with HashString
	public static final int MPQ_HASH_TABLE_OFFSET = 0;
	public static final int MPQ_HASH_NAME_A = 1;
	public static final int MPQ_HASH_NAME_B = 2;
	public static final int MPQ_HASH_FILE_KEY = 3;

	// cached hashes
	public static final int KEY_HASH_TABLE = HashString("(hash table)", MPQ_HASH_FILE_KEY);
	public static final int KEY_BLOCK_TABLE = HashString("(block table)", MPQ_HASH_FILE_KEY);

	public static void cryptData(ByteBuffer in, ByteBuffer out, int length, int key, final boolean de) {
		// prepare platform independent views
		in = in.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
		out = out.duplicate().order(ByteOrder.LITTLE_ENDIAN);
		// cryption
		int seed = 0xEEEEEEEE;
		length /= 4;
		while (length-- > 0) {
			seed += CRYPT_TABLE[0x400 + (key & 0xFF)];
			// basic algorithm
			final int read = in.getInt();
			final int ch = read ^ (key + seed);
			out.putInt(ch);
			// generation for next iteration
			seed += (de ? ch : read) + (seed << 5) + 3;
			key = ((~key << 21) + 0x11111111) | (key >>> 11);
		}
		out.rewind();
	}

	public static void encryptData(final ByteBuffer in, final ByteBuffer out, final int length, final int key) {
		cryptData(in, out, length, key, false);
	}

	public static void decryptData(final ByteBuffer in, final ByteBuffer out, final int length, final int key) {
		cryptData(in, out, length, key, true);
	}

	public static void cryptData(ByteBuffer in, ByteBuffer out, int key, final boolean de) {
		// prepare platform independent views
		in = in.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
		out = out.duplicate().order(ByteOrder.LITTLE_ENDIAN);

		// round to last dword to prevent buffer underflow
		in.limit(in.limit() & ~0x03);

		// cryption
		int seed = 0xEEEEEEEE;
		while (in.hasRemaining()) {
			seed += CRYPT_TABLE[0x400 + (key & 0xFF)];
			// basic algorithm
			final int read = in.getInt();
			final int ch = read ^ (key + seed);
			out.putInt(ch);
			// generation for next iteration
			seed += (de ? ch : read) + (seed << 5) + 3;
			key = ((~key << 21) + 0x11111111) | (key >>> 11);
		}
	}

	public static void encryptData(final ByteBuffer in, final ByteBuffer out, final int key) {
		cryptData(in, out, key, false);
	}

	public static void decryptData(final ByteBuffer in, final ByteBuffer out, final int key) {
		cryptData(in, out, key, true);
	}

	public static byte[] stringToHashable(final String in) {
		return in.toUpperCase(Locale.US).getBytes(StandardCharsets.UTF_8); // UTF_8 defined for platform independence
	}

	public static int HashString(final String in, final int HashType) {
		return HashString(stringToHashable(in), HashType);
	}

	// Based on code from StormLib.
	public static int HashString(final byte[] in, final int HashType) {
		int seed1 = 0x7FED7FED;
		int seed2 = 0xEEEEEEEE;
		for (final byte ch : in) {
			seed1 = CRYPT_TABLE[(HashType * 0x100) + ch] ^ (seed1 + seed2);
			seed2 = ch + seed1 + seed2 + (seed2 << 5) + 3;
		}
		return seed1;
	}

	public static int adjustFileDecryptKey(final int in, final int pos, final int size) {
		return (in + pos) ^ size;
	}
}
