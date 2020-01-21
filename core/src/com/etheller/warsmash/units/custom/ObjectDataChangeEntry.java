package com.etheller.warsmash.units.custom;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;

public final class ObjectDataChangeEntry {
	private War3ID oldId;
	private War3ID newId;
	private final ChangeMap changes;

	public ObjectDataChangeEntry(final War3ID oldId, final War3ID newId) {
		this.oldId = oldId;
		this.newId = newId;
		this.changes = new ChangeMap();
	}

	@Override
	public ObjectDataChangeEntry clone() {
		final ObjectDataChangeEntry objectDataChangeEntry = new ObjectDataChangeEntry(this.oldId, this.newId);
		for (final Map.Entry<War3ID, List<Change>> entry : this.changes) {
			objectDataChangeEntry.getChanges().add(entry.getKey(), entry.getValue());
		}
		return objectDataChangeEntry;
	}

	public ChangeMap getChanges() {
		return this.changes;
	}

	public War3ID getOldId() {
		return this.oldId;
	}

	public void setOldId(final War3ID oldId) {
		this.oldId = oldId;
	}

	public War3ID getNewId() {
		return this.newId;
	}

	public void setNewId(final War3ID newId) {
		this.newId = newId;
	}
}
