package com.etheller.warsmash.desktop.launcher.emulator.editor.fields;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.FieldPopupUtils;
import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundleDefaults;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public class BooleanEmulatorField extends AbstractEmulatorField {

	public BooleanEmulatorField(final String displayName, final String sortName, final String rawDataName,
			final String hintText) {
		super(displayName, sortName, rawDataName, hintText);
	}

	@Override
	public Object getValue(final Element gameUnit) {
		return gameUnit.getFieldAsBoolean(getRawDataName(), 0);
	}

	@Override
	public boolean popupEditor(final Element gameUnit, final Component parent, final StringBundle worldEditStrings,
			final boolean editRawData, final boolean disableLimits) {
		final String defaultDialogTitle = worldEditStrings
				.getString(HardcodedStringBundleDefaults.SMSHSTRING_EE_DLG_EDITVALUE);
		final String fieldKey = getRawDataName();
		final JPanel checkboxPanel = new JPanel();
		checkboxPanel.add(new JLabel(getDisplayName(gameUnit)));
		final JCheckBox checkBox = new JCheckBox("", gameUnit.getFieldAsBoolean(fieldKey, 0));
		checkboxPanel.add(checkBox);
		final String hintText = getHintText();
		if ((hintText != null) && !hintText.isEmpty()) {
			final JLabel hintLabel = new JLabel(hintText);
			hintLabel.setEnabled(false);
			checkboxPanel.add(hintLabel);
		}
		final int result = FieldPopupUtils.showPopup(parent, checkboxPanel,
				String.format(defaultDialogTitle,
						worldEditStrings.getString(HardcodedStringBundleDefaults.SMSHSTRING_COD_TYPE_BOOL)),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, checkBox);
		if (result == JOptionPane.OK_OPTION) {
			gameUnit.setField(fieldKey, checkBox.isSelected() ? "1" : "0");
			return true;
		}
		return false;
	}

}
