package com.etheller.warsmash.desktop.launcher.emulator;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import com.etheller.warsmash.desktop.editor.w3m.ui.editors.object.UnitEditorSettings;
import com.etheller.warsmash.desktop.launcher.emulator.editor.EmulatorEditorFieldBuilder;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public class EmulatorEditorPanel extends JPanel {
	private static final Object SHIFT_KEY_LOCK = new Object();
	Element currentUnit = null;
	UnitEditorSettings settings = new UnitEditorSettings();

	JTable table;
	private boolean holdingShift = false;
	private ObjectDataTableModel dataModel;
	private final Set<String> lastSelectedFields = new HashSet<>();
	private EmulatorEditorFieldBuilder editorFieldBuilder;
	private final StringBundle stringBundle;

	public EmulatorEditorPanel(final Element emulatorConstants, final EmulatorEditorFieldBuilder editorFieldBuilder,
			final StringBundle stringBundle) {
		this.stringBundle = stringBundle;
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.editorFieldBuilder = editorFieldBuilder;
		// temp.setBackground(Color.blue);
		this.table = new JTable();

		((DefaultTableCellRenderer) this.table.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);
		this.table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(final MouseEvent e) {

			}

			@Override
			public void mousePressed(final MouseEvent e) {

			}

			@Override
			public void mouseExited(final MouseEvent e) {

			}

			@Override
			public void mouseEntered(final MouseEvent e) {

			}

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final int rowIndex = EmulatorEditorPanel.this.table.getSelectedRow();
					if (EmulatorEditorPanel.this.dataModel != null) {
						EmulatorEditorPanel.this.dataModel.doPopupAt(EmulatorEditorPanel.this, rowIndex,
								EmulatorEditorPanel.this.holdingShift);
					}
				}
			}
		});
		final KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		this.table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "EnterKeyPopupAction");
		this.table.getActionMap().put("EnterKeyPopupAction", new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final int rowIndex = EmulatorEditorPanel.this.table.getSelectedRow();
				if (EmulatorEditorPanel.this.dataModel != null) {
					EmulatorEditorPanel.this.dataModel.doPopupAt(EmulatorEditorPanel.this, rowIndex, false);
				}
			}
		});
		final KeyStroke shiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
		this.table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(shiftEnter, "ShiftEnterKeyPopupAction");
		this.table.getActionMap().put("ShiftEnterKeyPopupAction", new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final int rowIndex = EmulatorEditorPanel.this.table.getSelectedRow();
				if (EmulatorEditorPanel.this.dataModel != null) {
					EmulatorEditorPanel.this.dataModel.doPopupAt(EmulatorEditorPanel.this, rowIndex, true);
				}
			}
		});
		final DefaultTableCellRenderer editHighlightingRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value,
					final boolean isSelected, final boolean hasFocus, final int row, final int column) {
				final boolean rowHasFocus = isSelected && table.hasFocus();
				setBackground(null);
				final Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
				if (isSelected) {
					if (rowHasFocus) {
						setForeground(EmulatorEditorPanel.this.settings.getSelectedValueColor());
					}
					else {
						setForeground(null);
						setBackground(EmulatorEditorPanel.this.settings.getSelectedUnfocusedValueColor());
					}
				}
				else if ((EmulatorEditorPanel.this.dataModel != null)
						&& EmulatorEditorPanel.this.dataModel.hasEditedValue(row)) {
					setForeground(EmulatorEditorPanel.this.settings.getEditedValueColor());
				}
				else {
					setForeground(null);
				}
				return this;
			}
		};
		this.table.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent e) {
				EmulatorEditorPanel.this.table.repaint();
			}

			@Override
			public void focusGained(final FocusEvent e) {
				EmulatorEditorPanel.this.table.repaint();
			}
		});
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setDefaultRenderer(Object.class, editHighlightingRenderer);
		this.table.setDefaultRenderer(String.class, editHighlightingRenderer);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(final KeyEvent ke) {
				synchronized (SHIFT_KEY_LOCK) {
					switch (ke.getID()) {
					case KeyEvent.KEY_PRESSED:
						if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
							EmulatorEditorPanel.this.holdingShift = true;
						}
						break;

					case KeyEvent.KEY_RELEASED:
						if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
							EmulatorEditorPanel.this.holdingShift = false;
						}
						break;
					}
					return false;
				}
			}
		});
		this.table.setShowGrid(false);

//		setupCopyPaste(new ObjectTabTreeBrowserTransferHandler(dataType));

	}

	public void fillTable() {
		this.dataModel = new ObjectDataTableModel(this.currentUnit, this.stringBundle, this.editorFieldBuilder,
				this.settings.isDisplayAsRawData());
		this.dataModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(final TableModelEvent e) {
				if (EmulatorEditorPanel.this.currentUnit != null) {
					EmulatorEditorPanel.this.lastSelectedFields.clear();
					if (EmulatorEditorPanel.this.dataModel != null) {
						for (final int rowIndex : EmulatorEditorPanel.this.table.getSelectedRows()) {
							EmulatorEditorPanel.this.lastSelectedFields
									.add(EmulatorEditorPanel.this.dataModel.getFieldRawDataName(rowIndex));
						}
					}
				}
			}
		});
		this.table.setModel(this.dataModel);
		this.dataModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(final TableModelEvent e) {
				for (int rowIndex = 0; rowIndex < EmulatorEditorPanel.this.table.getRowCount(); rowIndex++) {
					if (EmulatorEditorPanel.this.lastSelectedFields
							.contains(EmulatorEditorPanel.this.dataModel.getFieldRawDataName(rowIndex))) {
						EmulatorEditorPanel.this.table.addRowSelectionInterval(rowIndex, rowIndex);
					}
				}
			}
		});
		this.table.setAutoCreateColumnsFromModel(false);
	}

	public void toggleDisplayAsRawData() {
		this.settings.setDisplayAsRawData(!this.settings.isDisplayAsRawData());
		if (this.dataModel != null) {
			this.dataModel.setDisplayAsRawData(this.settings.isDisplayAsRawData());
		}
		// fillTable();
		repaint();
	}

	public static class DropLocation extends TransferHandler.DropLocation {
		protected DropLocation(final Point dropPoint) {
			super(dropPoint);
		}

	}

	private void findInTable(String text, final boolean displayAsRawData, final boolean caseSensitive) {
		if (!caseSensitive) {
			text = text.toLowerCase();
		}
		final int startIndex = this.table.getSelectedRow() + 1;
		for (int i = startIndex; i < this.dataModel.getRowCount(); i++) {
			for (int j = 0; j < this.dataModel.getColumnCount(); j++) {
				final Object tableData = this.dataModel.getValueAt(i, j);
				String tableString = tableData.toString();
				if (!caseSensitive) {
					tableString = tableString.toLowerCase();
					if (tableString.contains(text)) {
						final int rowToSelect = this.table.convertRowIndexToView(i);
						this.table.setRowSelectionInterval(rowToSelect, rowToSelect);
						this.table.scrollRectToVisible(this.table.getCellRect(rowToSelect, j, true));
						return;
					}
				}
			}
		}
		if (startIndex > 0) {
			for (int i = 0; i < startIndex; i++) {
				for (int j = 0; j < this.dataModel.getColumnCount(); j++) {
					final Object tableData = this.dataModel.getValueAt(i, j);
					String tableString = tableData.toString();
					if (!caseSensitive) {
						tableString = tableString.toLowerCase();
						if (tableString.contains(text)) {
							final int rowToSelect = this.table.convertRowIndexToView(i);
							this.table.setRowSelectionInterval(rowToSelect, rowToSelect);
							this.table.scrollRectToVisible(this.table.getCellRect(rowToSelect, j, true));
							return;
						}
					}
				}
			}
		}
	}
}
