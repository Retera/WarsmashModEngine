package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;

public class AbilityBuilderDupeCellRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (value instanceof AbilityBuilderConfiguration) {
			AbilityBuilderConfiguration config = (AbilityBuilderConfiguration) value;
			value = config.getId() + " \"" + config.getCastId() + "\"";
		}
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
}
