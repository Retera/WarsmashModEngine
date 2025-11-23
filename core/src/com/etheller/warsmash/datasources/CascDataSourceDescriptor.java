package com.etheller.warsmash.datasources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.etheller.warsmash.datasources.CascDataSource.Product;

public class CascDataSourceDescriptor implements DataSourceDescriptor {
	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = 832549098549298820L;
	private final String gameInstallPath;
	private final List<String> prefixes;
	private final String product;

	public CascDataSourceDescriptor(final String gameInstallPath, final List<String> prefixes) {
		this(gameInstallPath, prefixes, CascDataSource.Product.WARCRAFT_III.getKey());
	}

	public CascDataSourceDescriptor(final String gameInstallPath, final List<String> prefixes, final String product) {
		this.gameInstallPath = gameInstallPath;
		this.prefixes = prefixes;
		this.product = product;
	}

	@Override
	public DataSource createDataSource() {
		String product = this.product;
		if (product == null) {
			product = Product.WARCRAFT_III.getKey();
		}
		return new CascDataSource(this.gameInstallPath, this.prefixes.toArray(new String[this.prefixes.size()]),
				product);
	}

	@Override
	public String getDisplayName() {
		return "CASC: " + this.gameInstallPath + " (" + this.product + ")";
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
		result = (prime * result) + ((this.product == null) ? 0 : this.product.hashCode());
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
		if (this.product == null) {
			if (other.product != null) {
				return false;
			}
		}
		else if (!this.product.equals(other.product)) {
			return false;
		}
		return true;
	}

	@Override
	public DataSourceDescriptor duplicate() {
		return new CascDataSourceDescriptor(this.gameInstallPath, new ArrayList<>(this.prefixes), this.product);
	}

	public String getProduct() {
		return this.product;
	}
}
