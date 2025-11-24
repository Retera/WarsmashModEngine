package com.etheller.warsmash.desktop.launcher.emulator.editor.fields;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.FieldPopupUtils;
import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.StringObjectField;
import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundleDefaults;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public class StringEmulatorField extends AbstractEmulatorField {

	public StringEmulatorField(final String displayName, final String sortName, final String rawDataName,
			final String hintText) {
		super(displayName, sortName, rawDataName, hintText);
	}

	@Override
	public Object getValue(final Element gameUnit) {
		return gameUnit.getFieldAsString(getRawDataName(), 0);
	}

	@Override
	public boolean popupEditor(final Element gameUnit, final Component parent, final StringBundle worldEditStrings,
			final boolean editRawData, final boolean disableLimits) {
		final String defaultDialogTitle = worldEditStrings
				.getString(HardcodedStringBundleDefaults.SMSHSTRING_EE_DLG_EDITVALUE);
		final JPanel popupPanel = new JPanel();
		popupPanel.add(new JLabel(getDisplayName(gameUnit)));
		final String fieldKey = getRawDataName();
		final JTextField textField = new JTextField(gameUnit.getFieldAsString(fieldKey, 0),
				StringObjectField.STRING_FIELD_COLUMNS);
		popupPanel.add(textField);
		final String hintText = getHintText();
		if ((hintText != null) && !hintText.isEmpty()) {
			final JLabel hintLabel = new JLabel(hintText);
			hintLabel.setEnabled(false);
			popupPanel.add(hintLabel);
		}
		final int result = FieldPopupUtils.showPopup(parent, popupPanel,
				String.format(defaultDialogTitle,
						worldEditStrings.getString(HardcodedStringBundleDefaults.SMSHSTRING_COD_TYPE_STRING)),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, textField);
		if (result == JOptionPane.OK_OPTION) {
			gameUnit.setField(fieldKey, textField.getText(), 0);
			return true;
		}
		return false;
	}

}
