package com.etheller.warsmash.desktop.editor.w3m.ui.editors.object;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class EditorFieldTransferHandler extends TransferHandler {
	private final DataFlavor dataFlavor;

	public EditorFieldTransferHandler(final WorldEditorDataType worldEditorDataType) {
		this.dataFlavor = new DataFlavor(byte[].class, "JWc3FieldData:" + worldEditorDataType.name());
	}

	/**
	 * Perform the actual data import.
	 */
	@Override
	public boolean importData(final TransferHandler.TransferSupport info) {
		byte[] data = null;
		War3ObjectDataChangeset pastedObjects = null;

		// If we can't handle the import, bail now.
		if (!canImport(info)) {
			return false;
		}

		final UnitEditorTreeInterface editorPanel = (UnitEditorTreeInterface) info.getComponent();
		// Fetch the data -- bail if this fails
		try {
			data = (byte[]) info.getTransferable().getTransferData(this.dataFlavor);
			pastedObjects = new War3ObjectDataChangeset(editorPanel.getWar3ObjectDataChangesetKindChar());
			try (LittleEndianDataInputStream inputStream = new LittleEndianDataInputStream(
					new ByteArrayInputStream(data))) {

				pastedObjects.load(inputStream, null, false);
			}
		}
		catch (final UnsupportedFlavorException ufe) {
			System.out.println("importData: unsupported data flavor");
			ufe.printStackTrace();
			return false;
		}
		catch (final IOException ioe) {
			System.out.println("importData: I/O exception");
			ioe.printStackTrace();
			return false;
		}

		if (info.isDrop()) { // This is a drop
			final UnitEditorPanelInterface.DropLocation dl = (UnitEditorPanelInterface.DropLocation) info
					.getDropLocation();
			final Point dropPoint = dl.getDropPoint();
			// discard drop point, unit location is based on tree sorter folders
			editorPanel.acceptPastedObjectData(pastedObjects);
			return true;
		}
		else { // This is a paste
			editorPanel.acceptPastedObjectData(pastedObjects);
			return true;
		}
	}

	/**
	 * Bundle up the data for export.
	 */
	@Override
	protected Transferable createTransferable(final JComponent c) {
		final UnitEditorTreeInterface unitEditorPanel = (UnitEditorTreeInterface) c;
		final War3ObjectDataChangeset selectedUnitsAsChangeset = unitEditorPanel.copySelectedObjects();
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (LittleEndianDataOutputStream blizzardStream = new LittleEndianDataOutputStream(outputStream)) {
			selectedUnitsAsChangeset.save(blizzardStream, false);
		}
		catch (final FileNotFoundException e) {
			System.out.println("failed to copy");
			e.printStackTrace();
		}
		catch (final IOException e) {
			System.out.println("failed to copy");
			e.printStackTrace();
		}
		final byte[] byteArray = outputStream.toByteArray();
		return new Transferable() {
			DataFlavor[] flavors = { EditorFieldTransferHandler.this.dataFlavor };

			@Override
			public boolean isDataFlavorSupported(final DataFlavor flavor) {
				for (final DataFlavor flavorAllowed : this.flavors) {
					if (flavorAllowed.equals(flavor)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return this.flavors;
			}

			@Override
			public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return byteArray;
			}
		};
	}

	/**
	 * The list handles both copy and move actions.
	 */
	@Override
	public int getSourceActions(final JComponent c) {
		return COPY_OR_MOVE;
	}

	/**
	 * When the export is complete, remove the old list entry if the action was a
	 * move.
	 */
	@Override
	protected void exportDone(final JComponent c, final Transferable data, final int action) {
		if (action != MOVE) {
			return;
		}
		// final JList list = (JList) c;
		// final DefaultListModel model = (DefaultListModel) list.getModel();
		// final int index = list.getSelectedIndex();
		// model.remove(index);
	}

	/**
	 * We only support importing strings.
	 */
	@Override
	public boolean canImport(final TransferHandler.TransferSupport support) {
		// we only import Strings
		if (!support.isDataFlavorSupported(this.dataFlavor)) {
			return false;
		}
		return true;
	}
}