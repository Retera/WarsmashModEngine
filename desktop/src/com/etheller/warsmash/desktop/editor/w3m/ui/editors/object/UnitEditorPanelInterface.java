package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object;

import java.awt.Point;

import javax.swing.TransferHandler;

public interface UnitEditorPanelInterface {

	public static class DropLocation extends TransferHandler.DropLocation {
		protected DropLocation(final Point dropPoint) {
			super(dropPoint);
		}

	}
}
