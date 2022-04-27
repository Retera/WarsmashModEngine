package com.etheller.warsmash.datasources;

import java.util.Collections;
import java.util.List;

public class CascDataSourceDescriptor implements DataSourceDescriptor {
	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = 832549098549298820L;
	private final String gameInstallPath;
	private final List<String> prefixes;

	public CascDataSourceDescriptor(final String gameInstallPath, final List<String> prefixes) {
		this.gameInstallPath = gameInstallPath;
		this.prefixes = prefixes;
	}

	@Override
	public DataSource createDataSource() {
		return new CascDataSource(this.gameInstallPath, this.prefixes.toArray(new String[this.prefixes.size()]));
	}

	@Override
	public String getDisplayName() {
		return "CASC: " + this.gameInstallPath;
	}

	public void addPrefix(final String prefix) {
		this.prefixes.add(prefix);
	}

	public void deletePrefix(final int index) {
		this.prefixes.remove(index);
	}

	public void movePrefixUp(final int index) {
		if (index > 0) {
			Collections.swap(this.prefixes, index, index - 1);
		}
	}

	public void movePrefixDown(final int index) {
		if (index < (this.prefixes.size() - 1)) {
			Collections.swap(this.prefixes, index, index + 1);
		}
	}

	public String getGameInstallPath() {
		return this.gameInstallPath;
	}

	public List<String> getPrefixes() {
		return this.prefixes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.gameInstallPath == null) ? 0 : this.gameInstallPath.hashCode());
		result = (prime * result) + ((this.prefixes == null) ? 0 : this.prefixes.hashCode());
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
		final CascDataSourceDescriptor other = (CascDataSourceDescriptor) obj;
		if (this.gameInstallPath == null) {
			if (other.gameInstallPath != null) {
				return false;
			}
		}
		else if (!this.gameInstallPath.equals(other.gameInstallPath)) {
			return false;
		}
		if (this.prefixes == null) {
			if (other.prefixes != null) {
				return false;
			}
		}
		else if (!this.prefixes.equals(other.prefixes)) {
			return false;
		}
		return true;
	}
}
