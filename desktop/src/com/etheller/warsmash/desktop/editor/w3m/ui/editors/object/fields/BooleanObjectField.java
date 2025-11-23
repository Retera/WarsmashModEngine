package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public class BooleanObjectField extends AbstractObjectField {
	public BooleanObjectField(final String displayName, final String sortName, final String rawDataName,
			final boolean showingLevelDisplay, final War3ID metaKey, final int level,
			final WorldEditorDataType dataType, final GameObject metaDataField) {
		super(displayName, sortName, rawDataName, showingLevelDisplay, metaKey, level, dataType, metaDataField);
	}

	@Override
	protected Object getValue(final MutableGameObject gameUnit, final War3ID metaKey, final int level) {
		return gameUnit.getFieldAsBoolean(metaKey, level);
	}

	@Override
	protected boolean popupEditor(final MutableGameObject gameUnit, final Component parent,
			final WorldEditStrings worldEditStrings, final boolean editRawData, final boolean disableLimits,
			final War3ID metaKey, final int level, final String defaultDialogTitle, final GameObject metaDataField) {
		final JPanel checkboxPanel = new JPanel();
		checkboxPanel.add(new JLabel(getDisplayName(gameUnit)));
		final JCheckBox checkBox = new JCheckBox("", gameUnit.getFieldAsBoolean(metaKey, level));
		checkboxPanel.add(checkBox);
		final int result = FieldPopupUtils.showPopup(parent, checkboxPanel,
				String.format(defaultDialogTitle, worldEditStrings.getString("WESTRING_COD_TYPE_BOOL")),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, checkBox);
		if (result == JOptionPane.OK_OPTION) {
			gameUnit.setField(metaKey, level, checkBox.isSelected());
			return true;
		}
		return false;
	}

}
