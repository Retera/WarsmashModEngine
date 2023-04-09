package com.etheller.warsmash.desktop.editor.w3m.util;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * A class that tracks the focused component. This is necessary to delegate the
 * menu cut/copy/paste commands to the right component. An instance of this
 * class is listening and when the user fires one of these commands, it calls
 * the appropriate action on the currently focused component.
 *
 * Copied from
 * https://docs.oracle.com/javase/tutorial/uiswing/dnd/listpaste.html
 */
public class TransferActionListener implements ActionListener, PropertyChangeListener {
	private JComponent focusOwner = null;

	public TransferActionListener() {
		final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addPropertyChangeListener("permanentFocusOwner", this);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		final Object o = e.getNewValue();
		if (o instanceof JComponent) {
			focusOwner = (JComponent) o;
		}
		else {
			focusOwner = null;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (focusOwner == null) {
			return;
		}
		final String action = e.getActionCommand();
		final Action a = focusOwner.getActionMap().get(action);
		if (a != null) {
			a.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
		}
	}
}