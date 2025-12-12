package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public class IntegerObjectField extends AbstractObjectField {

	public IntegerObjectField(final String displayName, final String sortName, final String rawDataName,
			final boolean showingLevelDisplay, final War3ID metaKey, final int level,
			final WorldEditorDataType dataType, final GameObject metaDataField) {
		super(displayName, sortName, rawDataName, showingLevelDisplay, metaKey, level, dataType, metaDataField);
	}

	@Override
	protected Object getValue(final MutableGameObject gameUnit, final War3ID metaKey, final int level) {
		return gameUnit.getFieldAsInteger(metaKey, level);
	}

	@Override
	protected boolean popupEditor(final MutableGameObject gameUnit, final Component parent,
			final WorldEditStrings worldEditStrings, final boolean editRawData, final boolean disableLimits,
			final War3ID metaKey, final int level, final String defaultDialogTitle, final GameObject metaDataField) {
		final JPanel popupPanel = new JPanel();
		popupPanel.add(new JLabel(getDisplayName(gameUnit)));
		int minValue = metaDataField.getFieldValue("minVal");
		int maxValue = metaDataField.getFieldValue("maxVal");
		if (disableLimits) {
			minValue = -1000000000;
			maxValue = 1000000000;
		}
		int currentValue = gameUnit.getFieldAsInteger(metaKey, level);
		if (minValue > currentValue) {
			currentValue = minValue;
		}
		if (maxValue < currentValue) {
			currentValue = maxValue;
		}
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue, minValue, maxValue, 1));
		spinner.setMinimumSize(new Dimension(50, 1));
		spinner.setPreferredSize(new Dimension(75, 20));
		popupPanel.add(spinner);
		final int result = FieldPopupUtils.showPopup(parent, popupPanel,
				String.format(defaultDialogTitle, worldEditStrings.getString("WESTRING_COD_TYPE_INT")),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, spinner);
		if (result == JOptionPane.OK_OPTION) {
			gameUnit.setField(metaKey, level, ((Number) spinner.getValue()).intValue());
			return true;
		}
		return false;
	}

}
