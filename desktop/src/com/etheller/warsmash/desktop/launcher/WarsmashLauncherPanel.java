package com.etheller.warsmash.desktop.launcher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.etheller.warsmash.WarsmashGdxMapScreen;
import com.etheller.warsmash.datasources.DataSourceDescriptor;
import com.etheller.warsmash.desktop.editor.util.ExceptionPopup;
import com.etheller.warsmash.desktop.launcher.datasources.DataSourceChooserPanel;
import com.etheller.warsmash.desktop.launcher.emulator.EmulatorEditorPanel;
import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundle;
import com.etheller.warsmash.desktop.launcher.emulator.HardcodedStringBundleDefaults;
import com.etheller.warsmash.desktop.launcher.emulator.editor.DefaultEmulatorEditorFieldBuilder;
import com.etheller.warsmash.units.DataTable;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.util.StringBundle;

public class WarsmashLauncherPanel extends JPanel {
	private final DataSourceChooserPanel dataSourceChooserPanel;
	private final JFileChooser jFileChooser = new JFileChooser();
	private DefaultListModel<File> filesListModel;

	public WarsmashLauncherPanel() {
		final BorderLayout layout = new BorderLayout();
		setLayout(layout);

		final JTabbedPane tabbedPane = new JTabbedPane();

		this.dataSourceChooserPanel = new DataSourceChooserPanel(new ArrayList<>());

		final JPanel build = new JPanel();
		tabbedPane.addTab("Build", build);

		tabbedPane.addTab("Data Sources", this.dataSourceChooserPanel);

		final JPanel gamingNetworkPanel = new JPanel();
		tabbedPane.addTab("Gaming Network", gamingNetworkPanel);

		final HardcodedStringBundle emulatorEditorStringBundle = HardcodedStringBundleDefaults.loadEnglish();
		final EmulatorEditorPanel emulatorPanel = new EmulatorEditorPanel(
				new Element("0000", new DataTable(emulatorEditorStringBundle)),
				new DefaultEmulatorEditorFieldBuilder(emulatorEditorStringBundle), emulatorEditorStringBundle);
		tabbedPane.addTab("Emulator", new JScrollPane(emulatorPanel));

		add(tabbedPane, BorderLayout.CENTER);

		this.filesListModel = new DefaultListModel<File>();
		final JList<File> fileList = new JList<>(this.filesListModel);
		fileList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(final JList<?> list, Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				if (value instanceof File) {
					value = ((File) value).getName();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					final File selectedValue = fileList.getSelectedValue();
					final DataTable warsmashIni = new DataTable(StringBundle.EMPTY);
					try (FileInputStream warsmashIniInputStream = new FileInputStream(selectedValue)) {
						warsmashIni.readTXT(warsmashIniInputStream, true);
					}
					catch (final Exception e3) {
						e3.printStackTrace();
						ExceptionPopup.display(e3);
					}
					final List<DataSourceDescriptor> dataSourcesList = new ArrayList<>();
					final List<String> allCascPrefixes = new ArrayList<>();
					WarsmashGdxMapScreen.parseDataSourceDescriptors(warsmashIni, dataSourcesList, allCascPrefixes);
					WarsmashLauncherPanel.this.dataSourceChooserPanel.setDataSourceDescriptors(dataSourcesList);
				}
			}
		});

		add(new JScrollPane(fileList), BorderLayout.WEST);

		this.jFileChooser.setFileFilter(new FileNameExtensionFilter("Warsmash INI", "ini"));
		this.jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	public JMenuBar createJMenuBar() {
		final JMenuBar menuBar = new JMenuBar();

		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int fileResult = WarsmashLauncherPanel.this.jFileChooser
						.showOpenDialog(WarsmashLauncherPanel.this);
				if (fileResult == JFileChooser.APPROVE_OPTION) {
					final File selectedFile = WarsmashLauncherPanel.this.jFileChooser.getSelectedFile();
					if ((selectedFile != null) && selectedFile.isDirectory()) {
						WarsmashLauncherPanel.this.filesListModel.removeAllElements();
						final File[] listFiles = selectedFile.listFiles();
						for (final File file : listFiles) {
							if (file.getName().toLowerCase().endsWith(".ini")) {
								WarsmashLauncherPanel.this.filesListModel.addElement(file);
							}
						}
					}
					else {
						JOptionPane.showMessageDialog(WarsmashLauncherPanel.this, "No directory selected", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		fileMenu.add(openItem);

		final JMenuItem saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);

		menuBar.add(fileMenu);

		return menuBar;
	}
}
