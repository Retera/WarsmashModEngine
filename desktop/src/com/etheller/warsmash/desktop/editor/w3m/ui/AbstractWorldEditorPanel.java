package com.etheller.warsmash.desktop.editor.w3m.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import com.etheller.warsmash.desktop.editor.w3m.util.IconUtils;
import com.etheller.warsmash.desktop.editor.w3m.util.TransferActionListener;
import com.etheller.warsmash.desktop.editor.w3m.util.WorldEditArt;
import com.etheller.warsmash.units.DataTable;

public abstract class AbstractWorldEditorPanel extends JPanel {

	private JButton copyButton;
	private JButton pasteButton;

	public AbstractWorldEditorPanel() {
	}

	protected JToolBar createToolbar(final WorldEditArt worldEditArt, final DataTable worldEditorData) {
		final JToolBar toolBar = new JToolBar();
		makeButton(worldEditArt, worldEditorData, toolBar, "newMap", "ToolBarIcon_New", "WESTRING_TOOLBAR_NEW");
		final JButton openButton = makeButton(worldEditArt, worldEditorData, toolBar, "openMap", "ToolBarIcon_Open",
				"WESTRING_TOOLBAR_OPEN");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		});
		final JButton saveButton = makeButton(worldEditArt, worldEditorData, toolBar, "saveMap", "ToolBarIcon_Save",
				"WESTRING_TOOLBAR_SAVE");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
			}
		});
		toolBar.add(Box.createHorizontalStrut(8));
		final TransferActionListener transferActionListener = new TransferActionListener();
		copyButton = makeButton(worldEditArt, worldEditorData, toolBar, "copy", "ToolBarIcon_Copy",
				"WESTRING_MENU_OE_UNIT_COPY");
		copyButton.addActionListener(transferActionListener);
		copyButton.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
		pasteButton = makeButton(worldEditArt, worldEditorData, toolBar, "paste", "ToolBarIcon_Paste",
				"WESTRING_MENU_OE_UNIT_PASTE");
		pasteButton.addActionListener(transferActionListener);
		pasteButton.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
		toolBar.add(Box.createHorizontalStrut(8));
		createWindowSpecificToolbarButtons(worldEditArt, worldEditorData, toolBar);
		toolBar.add(Box.createHorizontalStrut(8));
		makeButton(worldEditArt, worldEditorData, toolBar, "terrainEditor", "ToolBarIcon_Module_Terrain",
				"WESTRING_MENU_MODULE_TERRAIN");
		makeButton(worldEditArt, worldEditorData, toolBar, "scriptEditor", "ToolBarIcon_Module_Script",
				"WESTRING_MENU_MODULE_SCRIPTS");
		makeButton(worldEditArt, worldEditorData, toolBar, "soundEditor", "ToolBarIcon_Module_Sound",
				"WESTRING_MENU_MODULE_SOUND");
		// final JButton objectEditorButton = makeButton(worldEditorData, toolBar,
		// "objectEditor",
		// "ToolBarIcon_Module_ObjectEditor", "WESTRING_MENU_OBJECTEDITOR");
		makePressedButton(worldEditArt, worldEditorData, toolBar);
		makeButton(worldEditArt, worldEditorData, toolBar, "objectEditor", "ToolBarIcon_Module_ObjectEditor",
				"WESTRING_MENU_OBJECTEDITOR");
		makeButton(worldEditArt, worldEditorData, toolBar, "campaignEditor", "ToolBarIcon_Module_Campaign",
				"WESTRING_MENU_MODULE_CAMPAIGN");
		makeButton(worldEditArt, worldEditorData, toolBar, "aiEditor", "ToolBarIcon_Module_AIEditor",
				"WESTRING_MENU_MODULE_AI");
		makeButton(worldEditArt, worldEditorData, toolBar, "objectEditor", "ToolBarIcon_Module_ObjectManager",
				"WESTRING_MENU_OBJECTMANAGER");
		final String legacyImportManagerIcon = worldEditorData.get("WorldEditArt")
				.getField("ToolBarIcon_Module_ImportManager");
		String importManagerIconPath = "ToolBarIcon_Module_ImportManager";
		String importManagerMenuName = "WESTRING_MENU_IMPORTMANAGER";
		if ((legacyImportManagerIcon == null) || "".equals(legacyImportManagerIcon)) {
			importManagerIconPath = "ToolBarIcon_Module_AssetManager";
			importManagerMenuName = "WESTRING_MENU_ASSETMANAGER";
		}
		makeButton(worldEditArt, worldEditorData, toolBar, "importEditor", importManagerIconPath,
				importManagerMenuName);
		toolBar.add(Box.createHorizontalStrut(8));
		makeButton(worldEditorData, toolBar, "testMap",
				new ImageIcon(IconUtils.worldEditStyleIcon(worldEditArt.getIcon("ToolBarIcon_TestMap").getImage())),
				"WESTRING_TOOLBAR_TESTMAP").addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
					}
				});
		;
		return toolBar;
	}

	private void makePressedButton(final WorldEditArt worldEditArt, final DataTable worldEditorData,
			final JToolBar toolBar) {
		final JToggleButton objectEditorButton = new JToggleButton(
				worldEditArt.getIcon("ToolBarIcon_Module_ObjectEditor"));
		objectEditorButton
				.setToolTipText(worldEditorData.getLocalizedString("WESTRING_MENU_OBJECTEDITOR").replace("&", ""));
		objectEditorButton.setPreferredSize(new Dimension(24, 24));
		objectEditorButton.setMargin(new Insets(1, 1, 1, 1));
		objectEditorButton.setSelected(true);
		objectEditorButton.setEnabled(false);
		objectEditorButton.setDisabledIcon(objectEditorButton.getIcon());
		toolBar.add(objectEditorButton);
	}

	protected abstract void createWindowSpecificToolbarButtons(WorldEditArt worldEditArt, DataTable worldEditorData,
			JToolBar toolBar);

	public static final class ToolbarButtonAction extends AbstractAction {
		private ToolbarButtonAction(final String name, final Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {

		}
	}

	public static JButton makeButton(final WorldEditArt worldEditArt, final DataTable worldEditorData,
			final JToolBar toolBar, final String actionName, final String iconKey, final String tooltipKey) {
		return makeButton(worldEditorData, toolBar, actionName,
				new ImageIcon(IconUtils.worldEditStyleIcon(worldEditArt.getIcon(iconKey).getImage())), tooltipKey);
	}

	public static JButton makeButton(final DataTable worldEditorData, final JToolBar toolBar, final String actionName,
			final ImageIcon icon, final String tooltipKey) {
		final JButton button = toolBar.add(new ToolbarButtonAction(actionName, icon));
		button.setToolTipText(worldEditorData.getLocalizedString(tooltipKey).replace("&", ""));
		button.setPreferredSize(new Dimension(24, 24));
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setFocusable(false);
		return button;
	}
}
