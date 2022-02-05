package com.etheller.warsmash.networking.uberserver.users;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Hash passwords for storage, and test passwords against password tokens.
 *
 * Instances of this class can be used concurrently by multiple threads.
 *
 * @author erickson
 * @see <a href="http://stackoverflow.com/a/2861125/3474">StackOverflow</a>
 */
public final class PasswordAuthentication {

	/**
	 * Each token produced by this class uses this identifier as a prefix.
	 */
	public static final String ID = "$31$";

	/**
	 * The minimum recommended cost, used by default
	 */
	public static final int DEFAULT_COST = 16;

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final int SIZE = 128;

	private static final Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

	private final SecureRandom random;

	private final int cost;

	public PasswordAuthentication() {
		this(DEFAULT_COST);
	}

	/**
	 * Create a password manager with a specified cost
	 *
	 * @param cost the exponential computational cost of hashing a password, 0 to 30
	 */
	public PasswordAuthentication(final int cost) {
		iterations(cost); /* Validate cost */
		this.cost = cost;
		this.random = new SecureRandom();
	}

	private static int iterations(final int cost) {
		if ((cost & ~0x1F) != 0) {
			throw new IllegalArgumentException("cost: " + cost);
		}
		return 1 << cost;
	}

	/**
	 * Hash a password for storage.
	 *
	 * @return a secure authentication token to be stored for later authentication
	 */
	public String hash(final char[] password) {
		final byte[] salt = new byte[SIZE / 8];
		this.random.nextBytes(salt);
		final byte[] dk = pbkdf2(password, salt, 1 << this.cost);
		final byte[] hash = new byte[salt.length + dk.length];
		System.arraycopy(salt, 0, hash, 0, salt.length);
		System.arraycopy(dk, 0, hash, salt.length, dk.length);
		// final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
		return ID + this.cost + '$' + Base64.encodeBase64URLSafeString(hash);
	}

	/**
	 * Authenticate with a password and a stored password token.
	 *
	 * @return true if the password and token match
	 */
	public static boolean authenticate(final char[] password, final String token) {
		final Matcher m = layout.matcher(token);
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid token format");
		}
		final int iterations = iterations(Integer.parseInt(m.group(1)));
		final byte[] hash = Base64.decodeBase64(m.group(2));// Base64.getUrlDecoder().decode(m.group(2));
		final byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
		final byte[] check = pbkdf2(password, salt, iterations);
		int zero = 0;
		for (int idx = 0; idx < check.length; ++idx) {
			zero |= hash[salt.length + idx] ^ check[idx];
		}
		return zero == 0;
	}

	private static byte[] pbkdf2(final char[] password, final byte[] salt, final int iterations) {
		final KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
		try {
			final SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
			return f.generateSecret(spec).getEncoded();
		} catch (final NoSuchAlgorithmException ex) {
			throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
		} catch (final InvalidKeySpecException ex) {
			throw new IllegalStateException("Invalid SecretKeyFactory", ex);
		}
	}

	/**
	 * Hash a password in an immutable {@code String}.
	 *
	 * <p>
	 * Passwords should be stored in a {@code char[]} so that it can be filled with
	 * zeros after use instead of lingering on the heap and elsewhere.
	 *
	 * @deprecated Use {@link #hash(char[])} instead
	 */
	@Deprecated
	public String hash(final String password) {
		return hash(password.toCharArray());
	}

	/**
	 * Authenticate with a password in an immutable {@code String} and a stored
	 * password token.
	 *
	 * @deprecated Use {@link #authenticate(char[],String)} instead.
	 * @see #hash(String)
	 */
	@Deprecated
	public boolean authenticate(final String password, final String token) {
		return authenticate(password.toCharArray(), token);
	}

}