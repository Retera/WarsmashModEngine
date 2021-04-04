package com.etheller.warsmash.units;

/**
 * A hashable wrapper object for a String that can be used as the key in a
 * hashtable, but which disregards case as a key -- except that it will remember
 * case if directly asked for its value. The game needs this to be able to show
 * the original case of a string to the user in the editor, while still doing
 * map lookups in a case insensitive way.
 *
 * @author Eric
 *
 */
public final class StringKey {
	private final String string;

	public StringKey(final String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.string.toLowerCase() == null) ? 0 : this.string.toLowerCase().hashCode());
		return result;
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
		final StringKey other = (StringKey) obj;
		if (this.string == null) {
			if (other.string != null) {
				return false;
			}
		}
		else if (!this.string.equalsIgnoreCase(other.string)) {
			return false;
		}
		return true;
	}
}