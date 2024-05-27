package com.etheller.warsmash.desktop.editor.abilitybuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.AbilityBuilderGsonBuilder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderDupe;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParserUtil;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderType;
import com.google.gson.Gson;

public class AbilityBuilderUIPanel extends JPanel {
	private final JFileChooser jsonFileChooser = new JFileChooser(new File("abilityBehaviors"));
	private final Gson gson = AbilityBuilderGsonBuilder.create();
	private DefaultListModel<AbilityBuilderConfiguration> idsListModel;
	private JList<AbilityBuilderConfiguration> idsList;
	private AbilityBuilderConfigTree abilityBuilderConfigTree;

	public AbilityBuilderUIPanel() {
		FileNameExtensionFilter jsonFilenameFilter = new FileNameExtensionFilter("JSON ability definitions file",
				"json");
		jsonFileChooser.addChoosableFileFilter(jsonFilenameFilter);
		jsonFileChooser.setFileFilter(jsonFilenameFilter);

		idsListModel = new DefaultListModel<AbilityBuilderConfiguration>();
		idsList = new JList<AbilityBuilderConfiguration>(idsListModel);
		idsList.setPreferredSize(new Dimension(200, 1));
		idsList.setCellRenderer(new AbilityBuilderDupeCellRenderer());

		abilityBuilderConfigTree = AbilityBuilderConfigTree.create();

		idsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		idsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					AbilityBuilderConfiguration selectedValue = idsList.getSelectedValue();
					if (selectedValue != null) {
						abilityBuilderConfigTree.setConfig(selectedValue);
					}
				}
			}
		});

		setLayout(new BorderLayout());
		add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(idsList),
				new JScrollPane(abilityBuilderConfigTree)), BorderLayout.CENTER);
	}

	public JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileOpenClicked();
			}
		});
		fileMenu.add(openItem);
		return menuBar;
	}

	public void fileOpenClicked() {
		int userResult = jsonFileChooser.showOpenDialog(this);
		if (userResult == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jsonFileChooser.getSelectedFile();
			if (selectedFile != null) {
				try {
					idsListModel.clear();
					abilityBuilderConfigTree.clearConfig();
					AbilityBuilderParserUtil.loadAbilityBuilderFile(gson, selectedFile, behavior -> {
						if (behavior.getType() == AbilityBuilderType.TEMPLATE) {
							for (AbilityBuilderDupe dupe : behavior.getIds()) {
								System.out.println("template: " + dupe.getId());
								idsListModel.addElement(new AbilityBuilderConfiguration(behavior, dupe));
							}
						} else {
							for (AbilityBuilderDupe dupe : behavior.getIds()) {
								System.out.println("non-template: " + dupe.getId());
								idsListModel.addElement(new AbilityBuilderConfiguration(behavior, dupe));
							}
						}
					});
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
}
