package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.EditableOnscreenObjectField;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.WorldEditStrings;

public class ObjectDataTableModel implements TableModel {
	private final MutableGameObject gameObject;
	private final ObjectData metaData;
	private final WorldEditStrings worldEditStrings;
	private final List<EditableOnscreenObjectField> fields;
	private final Set<TableModelListener> tableModelListeners;
	private boolean displayAsRawData;
	private final Runnable runOnIsCustomUnitStateChange;

	public ObjectDataTableModel(final MutableGameObject gameObject, final ObjectData metaData,
			final WorldEditStrings worldEditStrings, final DataTable unitEditorData,
			final EditorFieldBuilder editorFieldBuilder, final boolean displayAsRawData,
			final Runnable runOnIsCustomUnitStateChange) {
		this.gameObject = gameObject;
		this.metaData = metaData;
		this.worldEditStrings = worldEditStrings;
		this.displayAsRawData = displayAsRawData;
		this.runOnIsCustomUnitStateChange = runOnIsCustomUnitStateChange;
		this.tableModelListeners = new LinkedHashSet<>();
		if (gameObject != null) {
			this.fields = editorFieldBuilder.buildFields(metaData, worldEditStrings, unitEditorData, gameObject);
			Collections.sort(this.fields, new Comparator<EditableOnscreenObjectField>() {
				@Override
				public int compare(final EditableOnscreenObjectField o1, final EditableOnscreenObjectField o2) {
					final int o1Level = o1.getLevel();
					final int o2Level = o2.getLevel();
					if (o1.isShowingLevelDisplay() && !o2.isShowingLevelDisplay()) {
						return 1;
					}
					if (!o1.isShowingLevelDisplay() && o2.isShowingLevelDisplay()) {
						return -1;
					}
					final int sortNameComparison = o1.getSortName(gameObject).compareTo(o2.getSortName(gameObject));
					if (sortNameComparison != 0) {
						return sortNameComparison;
					}
					return Integer.compare(o1Level, o2Level);
				}
			});
		}
		else {
			this.fields = new ArrayList<>();
		}
	}

	public void setDisplayAsRawData(final boolean displayAsRawData) {
		this.displayAsRawData = displayAsRawData;
		for (final TableModelListener listener : this.tableModelListeners) {
			listener.tableChanged(new TableModelEvent(this, 0, Integer.MAX_VALUE, 0));
		}
	}

	@Override
	public int getRowCount() {
		return this.fields.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	public boolean hasEditedValue(final int rowIndex) {
		if (this.gameObject == null) {
			return false;
		}
		return this.fields.get(rowIndex).hasEditedValue(this.gameObject);
	}

	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return this.worldEditStrings.getString("WESTRING_UE_FIELDNAME");
		case 1:
			return this.worldEditStrings.getString("WESTRING_UE_FIELDVALUE");
		}
		return this.worldEditStrings.getString("WESTRING_UNKNOWN");
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		}
		return Object.class;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (this.gameObject == null) {
			return 0;
		}
		if (columnIndex == 0) {
			if (this.displayAsRawData) {
				return this.fields.get(rowIndex).getRawDataName();
			}
			else {
				return this.fields.get(rowIndex).getDisplayName(this.gameObject);
			}
		}
		return this.fields.get(rowIndex).getValue(this.gameObject);
	}

	public String getFieldRawDataName(final int rowIndex) {
		return this.fields.get(rowIndex).getRawDataName();
	}

	public int getFieldLevel(final int rowIndex) {
		return this.fields.get(rowIndex).getLevel();
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {

	}

	public void doPopupAt(final Component parent, final int rowIndex, final boolean isHoldingShift) {
		if (this.gameObject == null) {
			return;
		}
		final boolean hadBeenEdited = this.gameObject.hasEditorData();
		final EditableOnscreenObjectField field = this.fields.get(rowIndex);
		if (field.popupEditor(this.gameObject, parent, this.worldEditStrings, this.displayAsRawData, isHoldingShift)) {
			for (final TableModelListener listener : this.tableModelListeners) {
				listener.tableChanged(new TableModelEvent(this, rowIndex, rowIndex, 1));
			}
		}
		if (this.gameObject.hasEditorData() != hadBeenEdited) {
			this.runOnIsCustomUnitStateChange.run();
		}
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		this.tableModelListeners.add(l);
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		this.tableModelListeners.remove(l);
	}

}
