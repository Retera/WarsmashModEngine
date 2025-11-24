package com.etheller.warsmash.desktop.launcher.emulator.editor.fields;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.fields.FieldPopupUtils;
import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundleDefaults;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public class IntegerEmulatorField extends AbstractEmulatorField {
	private int minValue;
	private int maxValue;

	public IntegerEmulatorField(final String displayName, final String sortName, final String rawDataName,
			final String hintText, final int minValue, final int maxValue) {
		super(displayName, sortName, rawDataName, hintText);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public Object getValue(final Element gameUnit) {
		return gameUnit.getFieldAsInteger(getRawDataName(), 0);
	}

	@Override
	public boolean popupEditor(final Element gameUnit, final Component parent, final StringBundle worldEditStrings,
			final boolean editRawData, final boolean disableLimits) {
		final String defaultDialogTitle = worldEditStrings
				.getString(HardcodedStringBundleDefaults.SMSHSTRING_EE_DLG_EDITVALUE);
		final JPanel popupPanel = new JPanel();
		popupPanel.add(new JLabel(getDisplayName(gameUnit)));
		final String fieldKey = getRawDataName();
		if (disableLimits) {
			this.minValue = -1000000000;
			this.maxValue = 1000000000;
		}
		int currentValue = gameUnit.getFieldAsInteger(fieldKey, 0);
		if (this.minValue > currentValue) {
			currentValue = this.minValue;
		}
		if (this.maxValue < currentValue) {
			currentValue = this.maxValue;
		}
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue, this.minValue, this.maxValue, 1));
		spinner.setMinimumSize(new Dimension(50, 1));
		spinner.setPreferredSize(new Dimension(75, 20));
		popupPanel.add(spinner);
		final String hintText = getHintText();
		if ((hintText != null) && !hintText.isEmpty()) {
			final JLabel hintLabel = new JLabel(hintText);
			hintLabel.setEnabled(false);
			popupPanel.add(hintLabel);
		}
		final int result = FieldPopupUtils.showPopup(parent, popupPanel,
				String.format(defaultDialogTitle,
						worldEditStrings.getString(HardcodedStringBundleDefaults.SMSHSTRING_COD_TYPE_INT)),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, spinner);
		if (result == JOptionPane.OK_OPTION) {
			gameUnit.setField(fieldKey, Integer.toString(((Number) spinner.getValue()).intValue()));
			return true;
		}
		return false;
	}

}
