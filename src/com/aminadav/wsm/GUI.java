package com.aminadav.wsm;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.aminadav.database.DataBase;
import com.aminadav.database.Settings;
import com.aminadav.database.Table;
import com.aminadav.database.XorEncryptedDataBase;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	private static String TITLE = Strings.getString("GUI.program_title"); //$NON-NLS-1$

	Dimension pSize = new Dimension(700, 600);

	JTabbedPane tabs = new JTabbedPane();
	JPanel centerPanel = new JPanel(), workersPanel = new JPanel();
	String[] workersHeaders = { Strings.getString("GUI.name_header"), Strings.getString("GUI.salary_header") }; //$NON-NLS-1$ //$NON-NLS-2$
	JLabel l1 = new JLabel(Strings.getString("GUI.l1")); //$NON-NLS-1$
	JComboBox<Month> cbMonths = new JComboBox<>(Month.values());
	JButton addWorker = new JButton(Strings.getString("GUI.add_worker_button")), //$NON-NLS-1$
			addHours = new JButton(Strings.getString("GUI.add_hours_button")); //$NON-NLS-1$
	DefaultListModel<Worker> wlModel = new DefaultListModel<>();
	JList<Worker> workerList = new JList<>(wlModel);
	JScrollPane spWorkers = new JScrollPane(workerList), spSummary = new JScrollPane();

	JMenuBar menu = new JMenuBar();
	JMenu fileM = new JMenu(Strings.getString("GUI.file_menu")); //$NON-NLS-1$
	JMenuItem newJMI = new JMenuItem(Strings.getString("GUI.new_item")), //$NON-NLS-1$
			saveJMI = new JMenuItem(Strings.getString("GUI.save_item")), //$NON-NLS-1$
			loadJMI = new JMenuItem(Strings.getString("GUI.load_item")), //$NON-NLS-1$
			printJMI = new JMenuItem(Strings.getString("GUI.print_item")); //$NON-NLS-1$
	JToggleButton tb = new JToggleButton(Strings.getString("GUI.lang_tb")); //$NON-NLS-1$

	DataBase workersDB;

	public GUI() {
		super(TITLE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		//Strings.setLocale(Locale.getDefault());
		//defineLocale(this);

		JFileChooser jfc = new JFileChooser(
				System.getProperty("user.home") + System.getProperty("file.separator") + "desktop"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		jfc.setAcceptAllFileFilterUsed(false);

		GridBagConstraints con = new GridBagConstraints();

		cbMonths.setSelectedIndex(LocalDate.now().getMonth().getValue() - 1);
		workerList.setSelectionMode(JList.VERTICAL);

		workerList.addListSelectionListener(p -> {
			if (p.getValueIsAdjusting())
				return;
			Worker current = workerList.getSelectedValue();
			if (current == null)
				return;
			if (tabs.getTabCount() > 1)
				tabs.removeTabAt(1);
			tabs.addTab(Strings.getString("GUI.worker_prefix") + current.name, new JScrollPane(current.table)); //$NON-NLS-1$
			tabs.setSelectedIndex(1);
		});

		cbMonths.addItemListener(p -> {
			Object[][] data = new Object[wlModel.size()][workersHeaders.length];
			for (int i = 0; i < wlModel.size(); i++) {
				Worker current = wlModel.get(i);
				if (current.table != null) {
					data[i][0] = current.name;
					for (int j = 0; j < current.table.getRowCount(); j++) {
						if (current.table.getValueAt(j, 0) == (Month) p.getItem()) {
							data[i][1] = current.table.getValueAt(j, 2);
							break;
						}
					}
				}
			}
			centerPanel.remove(spSummary);
			JTable workersTable = new JTable(data, workersHeaders);
			workersTable.setFillsViewportHeight(true);
			spSummary = new JScrollPane(workersTable);
			con.fill = GridBagConstraints.BOTH;
			con.anchor = GridBagConstraints.PAGE_END;
			con.gridy = GridBagConstraints.RELATIVE;
			con.gridwidth = 2;
			con.gridx = 0;
			con.weightx = 1;
			con.weighty = 0.9;
			centerPanel.add(spSummary, con);
			// pack();
			update(getGraphics());
		});

		addWorker.addActionListener(p -> {
			Object[] data = Forms.addWorker(this);
			for (int i = 0; i < wlModel.size(); i++) {
				if (wlModel.get(i).name.equals(data[0])) {
					JOptionPane.showMessageDialog(this, Strings.getString("GUI.worker_error"), //$NON-NLS-1$
							Strings.getString("GUI.error"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			wlModel.addElement(new Worker((String) data[0], (double) data[1]));
		});

		addHours.addActionListener(p -> {
			Worker[] workers = new Worker[wlModel.size()];
			for (int i = 0; i < wlModel.size(); i++) {
				workers[i] = wlModel.get(i);
			}
			Object[] data = Forms.addHours(this, workers);
			((Worker) data[0]).addHours((Month) data[2], (Double) data[1]);
		});

		newJMI.addActionListener(p -> {
			Object[] options = { Strings.getString("GUI.normal_db_setup"), Strings.getString("GUI.enc_db_setup") }; //$NON-NLS-1$ //$NON-NLS-2$
			int n = JOptionPane.showOptionDialog(this, Strings.getString("GUI.question_db_setup"), //$NON-NLS-1$
					Strings.getString("GUI.new_db"), //$NON-NLS-1$
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			String fileName;
			if (n == JOptionPane.YES_OPTION) {
				fileName = JOptionPane.showInputDialog(Strings.getString("GUI.enter_db_name"), //$NON-NLS-1$
						System.getProperty("user.name")); //$NON-NLS-1$
				workersDB = new DataBase(fileName);
			} else {
				String[] str = Forms.getUserNPass(this);
				fileName = str[0];
				workersDB = new XorEncryptedDataBase(str[0], str[1], new Settings(".edb")); //$NON-NLS-1$
			}
			setTitle(TITLE + " - " + fileName + workersDB.getSettings().END); //$NON-NLS-1$
		});

		saveJMI.addActionListener(p -> {
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			try {
				for (int i = 0; i < wlModel.size(); i++) {
					Worker current = wlModel.get(i);
					String tableName = current.name + "@" + current.salaryPerHour; //$NON-NLS-1$
					String[] headers = { Strings.getString("GUI.month_header"), //$NON-NLS-1$
							Strings.getString("GUI.work_hours_header") }; //$NON-NLS-1$
					workersDB.createTable(tableName, headers);
					JTable cTable = current.table;
					for (int j = 0; j < cTable.getRowCount(); j++) {
						workersDB.insert(tableName, cTable.getValueAt(j, 0), cTable.getValueAt(j, 1));
					}
				}
				if (jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
					return;
				workersDB.save(jfc.getSelectedFile(), true);
			} catch (FileAlreadyExistsException e) {
				JOptionPane.showMessageDialog(this, e.getFile());
				return;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, Strings.getString("GUI.save_error")); //$NON-NLS-1$
				return;
			}
			JOptionPane.showMessageDialog(this, Strings.getString("GUI.save_success")); //$NON-NLS-1$
		});

		loadJMI.addActionListener(p -> {
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			jfc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory() || file.getName().endsWith(".adb") || file.getName().endsWith(".edb"); //$NON-NLS-1$ //$NON-NLS-2$
				}

				@Override
				public String getDescription() {
					return Strings.getString("GUI.db_type_name"); //$NON-NLS-1$
				}
			});
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String name = jfc.getSelectedFile().getAbsolutePath();
				if (name.endsWith(".edb")) //$NON-NLS-1$
					workersDB = new XorEncryptedDataBase(name.substring(0, name.length() - 4),
							JOptionPane.showInputDialog(Strings.getString("GUI.pass")), new Settings(".edb")); //$NON-NLS-1$ //$NON-NLS-2$
				else
					workersDB = new DataBase(name.substring(0, name.length() - 4));
				try {
					if (!wlModel.isEmpty())
						wlModel.removeAllElements();
					if (tabs.getTabCount() > 1)
						tabs.removeTabAt(1);
					workersDB.load();
					for (int i = 0; i < workersDB.tables.size(); i++) {
						Table cTable = workersDB.tables.get(i);
						String workerName = cTable.NAME.split("@")[0]; //$NON-NLS-1$
						double salaryPerHour = Double.parseDouble(cTable.NAME.split("@")[1]); //$NON-NLS-1$
						Worker current = new Worker(workerName, salaryPerHour);
						for (int j = 0; j < cTable.get(0).size(); j++) {
							current.addHours(Month.valueOf((String) cTable.get(0).get(j)),
									Double.parseDouble((String) cTable.get(1).get(j)));
						}
						wlModel.addElement(current);
						setTitle(TITLE + " - " + jfc.getSelectedFile().getName() + workersDB.getSettings().END); //$NON-NLS-1$
					}
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(this, Strings.getString("GUI.load_error") + e.getMessage()); //$NON-NLS-1$
				}
			}
		});

		printJMI.addActionListener(p -> {
			try {
				JTable toPrint;
				String additionalData;
				if (tabs.getSelectedIndex() == 1) {
					toPrint = ((JTable) ((JViewport) ((JScrollPane) tabs.getSelectedComponent()).getComponent(0))
							.getView());
					additionalData = Strings.getString("GUI.salary_per_hour") //$NON-NLS-1$
							+ workerList.getSelectedValue().salaryPerHour;
				} else {
					toPrint = ((JTable) ((JViewport) ((JScrollPane) centerPanel.getComponent(2)).getComponent(0))
							.getView());
					additionalData = Strings.getString("GUI.month") + Month.of(cbMonths.getSelectedIndex()).name(); //$NON-NLS-1$
				}
				LocalDate now = LocalDate.now();
				String date = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear(); //$NON-NLS-1$ //$NON-NLS-2$
				String header = tabs.getTitleAt(tabs.getSelectedIndex()) + ", \t" + additionalData + " - \t" + date; //$NON-NLS-1$ //$NON-NLS-2$
				MessageFormat headerFormat = new MessageFormat(header),
						footerFormat = new MessageFormat(Strings.getString("GUI.footer")); //$NON-NLS-1$
				if (toPrint == null) {
					System.err.println(Strings.getString("GUI.print_error")); //$NON-NLS-1$
					return;
				}
				boolean complete = toPrint.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat, true, null,
						true, null);
				if (complete)
					;
				else
					;
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		});

		tb.addActionListener(p -> {
			Locale nl;
			if (tb.isSelected()) {
				if (Locale.getDefault().equals(new Locale("en", "US"))) {
					nl = new Locale("iw", "IL");
				} else {
					nl =new Locale("en", "US");
				}
			} else {
				if (Locale.getDefault().equals(new Locale("en", "US"))) {
					nl = new Locale("en", "US");
				} else {
					nl = new Locale("iw", "IL");
				}
			}
			applyComponentOrientation(ComponentOrientation.getOrientation(nl));
			Strings.setLocale(nl);
			updateGUI(nl);
			update(getGraphics());
		});

		JPanel bp = new JPanel();
		bp.setLayout(new FlowLayout());
		bp.add(addWorker);
		bp.add(addHours);

		centerPanel.setLayout(new GridBagLayout());

		con.fill = GridBagConstraints.HORIZONTAL;
		con.anchor = GridBagConstraints.FIRST_LINE_START;
		con.gridy = 0;
		con.gridx = 0;
		con.weightx = 0.5;
		con.weighty = 0.1;
		centerPanel.add(l1, con);
		con.fill = GridBagConstraints.HORIZONTAL;
		con.anchor = GridBagConstraints.FIRST_LINE_END;
		con.gridy = 0;
		con.gridx = 1;
		con.weightx = 0.5;
		con.weighty = 0.1;
		centerPanel.add(cbMonths, con);
		con.fill = GridBagConstraints.BOTH;
		con.anchor = GridBagConstraints.PAGE_END;
		con.gridy = GridBagConstraints.RELATIVE;
		con.gridwidth = 2;
		con.gridx = 0;
		con.weightx = 1;
		con.weighty = 0.9;
		centerPanel.add(spSummary, con);

		// adding the items
		workersPanel.setLayout(new BorderLayout());
		workersPanel.add(centerPanel, BorderLayout.CENTER);
		workersPanel.add(spWorkers, BorderLayout.LINE_END);
		workersPanel.add(bp, BorderLayout.PAGE_END);

		tabs.addTab(Strings.getString("GUI.workers_tab"), new JScrollPane(workersPanel)); //$NON-NLS-1$
		add(tabs);

		newJMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		saveJMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		loadJMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		printJMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		fileM.add(newJMI);
		fileM.add(saveJMI);
		fileM.add(loadJMI);
		fileM.add(printJMI);

		menu.add(fileM);
		menu.add(tb);

		// settings
		setTitle(TITLE);
		setJMenuBar(menu);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(pSize);

		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

		pack();
		update(getGraphics());
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateGUI(Locale nl) {
		TITLE = Strings.getString("GUI.program_title"); //$NON-NLS-1$
		workersHeaders = new String[]{ Strings.getString("GUI.name_header"), Strings.getString("GUI.salary_header") }; //$NON-NLS-1$ //$NON-NLS-2$
		l1.setText(Strings.getString("GUI.l1")); //$NON-NLS-1$
		addWorker.setText(Strings.getString("GUI.add_worker_button")); //$NON-NLS-1$
				addHours.setText(Strings.getString("GUI.add_hours_button")); //$NON-NLS-1$
		fileM.setText(Strings.getString("GUI.file_menu")); //$NON-NLS-1$
		newJMI.setText(Strings.getString("GUI.new_item")); //$NON-NLS-1$
				saveJMI.setText(Strings.getString("GUI.save_item")); //$NON-NLS-1$
				loadJMI.setText(Strings.getString("GUI.load_item")); //$NON-NLS-1$
				printJMI.setText(Strings.getString("GUI.print_item")); //$NON-NLS-1$
		tb.setText(Strings.getString("GUI.lang_tb")); //$NON-NLS-1$
		tabs.setTitleAt(0, Strings.getString("GUI.workers_tab"));
		setTitle(TITLE);
		update(getGraphics());
	}

	public static void main(String[] args) {
		new GUI();
		SplashScreen splash = SplashScreen.getSplashScreen();
		if(splash != null) splash.close();
	}
}