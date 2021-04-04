package com.etheller.warsmash.datasources;

import java.nio.file.Paths;

public class FolderDataSourceDescriptor implements DataSourceDescriptor {
	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = -476724730967709309L;
	private final String folderPath;

	public FolderDataSourceDescriptor(final String folderPath) {
		this.folderPath = folderPath;
	}

	@Override
	public DataSource createDataSource() {
		return new FolderDataSource(Paths.get(this.folderPath));
	}

	@Override
	public String getDisplayName() {
		return "Folder: " + this.folderPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.folderPath == null) ? 0 : this.folderPath.hashCode());
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
		final FolderDataSourceDescriptor other = (FolderDataSourceDescriptor) obj;
		if (this.folderPath == null) {
			if (other.folderPath != null) {
				return false;
			}
		}
		else if (!this.folderPath.equals(other.folderPath)) {
			return false;
		}
		return true;
	}

}
